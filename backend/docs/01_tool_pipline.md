# Tool Pipeline 설계

## 핵심 개념

사용자가 Tool을 자유롭게 조합하고, Agent에 파이프라인(실행 순서)을 정의한다.
요청이 들어오면 파이프라인 순서대로 Tool이 실행되며, Tool 간 데이터는 **공유 컨텍스트(ToolContext)** 로 전달된다.
SQL 생성 및 재시도는 단일 Tool 내부에서 처리하고, Tool 순서는 사람이 결정한다.

---

## 설계 선택지와 결정

### 1. LLM이 Tool 순서를 결정할 것인가, 사람이 결정할 것인가?

**선택지 A — ReAct (LLM이 Tool 선택)**

LangChain, LlamaIndex 등에서 채택한 방식.
LLM이 어떤 Tool을 쓸지 스스로 판단하고 Thought → Action → Observation 루프를 돌린다.

```
LLM이 판단:
  "스키마가 필요하다" → SEARCH_SCHEMA 호출
  "SQL을 써야 한다"  → GENERATE_SQL 호출
  ...
```

장점: LLM이 상황에 맞게 유연하게 대응  
단점: LLM 판단 결과를 예측하기 어렵고, 파이프라인 디버깅이 어렵다. 프롬프트 품질에 전체 흐름이 종속된다.

**선택지 B — Human-defined Pipeline (사람이 순서 결정)**

Agent 등록 시 실행 순서를 명시적으로 정의한다.
Tool은 정해진 순서대로만 실행되며, LLM은 SQL 생성에만 관여한다.

```
Agent.tools = [SEARCH_SCHEMA, GENERATE_AND_EXECUTE_SQL]
              ↑ 이 순서대로만 실행
```

장점: 실행 흐름이 예측 가능하고, 디버깅이 용이하다.  
단점: 상황별 유연한 분기가 어렵다.

**→ 결정: Human-defined Pipeline 채택**

현재 QueryLens는 Text-to-SQL 목적에 집중하며, 파이프라인이 단순하고 예측 가능해야 한다.
LLM 자율 판단은 SQL 생성 실패 시 재시도(ReAct)에만 제한적으로 적용한다.

---

### 2. GENERATE_SQL + EXECUTE_SQL을 분리할 것인가, 합칠 것인가?

**선택지 A — 분리**

```
[GENERATE_SQL] → [EXECUTE_SQL]
```

각 Tool이 단일 책임을 가지며, 파이프라인에서 독립적으로 조합 가능하다.

단점: SQL 실패 시 재시도 로직이 파이프라인 수준에서 처리되어야 한다.
파이프라인이 `[GENERATE_SQL, EXECUTE_SQL, GENERATE_SQL, EXECUTE_SQL, ...]` 형태가 되거나
별도 재시도 조율자가 필요해진다. 재시도 상태(이전 SQL, 오류 메시지)를 ToolContext에 계속 노출해야 한다.

**선택지 B — 합침 (`GENERATE_AND_EXECUTE_SQL`)**

```
[GENERATE_AND_EXECUTE_SQL]  ← 내부에서 생성 + 실행 + 재시도를 처리
```

ReAct 루프(생성 → 실행 → 실패 시 오류 피드백 → 재생성)가 Tool 내부에 캡슐화된다.
외부 파이프라인은 이 복잡성을 볼 필요가 없다.

**→ 결정: 합침 채택**

재시도 로직은 SQL 생성·실행 Tool 내부의 관심사다.
파이프라인은 단방향 흐름을 유지하고, Tool 내부 구현이 복잡성을 흡수한다.

```
내부 ReAct 루프 (최대 3회):
  질문 + DDL → LLM → SQL 생성
                        │
                    SQL 실행 시도
                        │
             ┌──────────┴──────────┐
           성공                   실패
             │                     │
         결과 저장        오류 메시지 → LLM 재시도
                          (이전 SQL + 오류 포함)
```

---

### 3. LLM 연동 방식 — Ollama 전용 vs 멀티 프로바이더

**선택지 A — spring-ai-starter-model-ollama**

Ollama 전용 starter. WebFlux 의존성이 포함되어 있어 현재 프로젝트(Spring MVC)와 충돌한다.

**선택지 B — spring-ai-starter-model-openai (OpenAI 호환 엔드포인트)**

Ollama는 `/v1` 경로로 OpenAI 호환 API를 제공한다.
`spring-ai-starter-model-openai`를 사용해 `base-url`만 Ollama 서버로 변경하면 동작한다.
WebFlux 의존성 없이 사용 가능하다.

**→ 결정: OpenAI 호환 엔드포인트 방식 채택**

추가로 Google Gemini(`spring-ai-starter-model-google-genai`)를 별도 빈으로 등록해,
Agent의 `LlmProvider` 설정에 따라 런타임에 모델을 선택한다.

```java
// Agent.llmModel.provider 기준으로 ChatModel 분기
OLLAMA  → OpenAiChatModel  (base-url: Ollama 서버)
GEMINI  → GoogleGenAiChatModel
```

---

## 전체 흐름

```
사용자 질문
    │
    ▼
ToolContext 생성
{ userId, agentId, dataSourceId, question }
    │
    ▼
Agent 파이프라인 실행 (등록된 tools 순서)
    │
    ├─ [SEARCH_SCHEMA]
    │       └─ DataSource에서 DDL 조회
    │       └─ context["ddl"] 에 저장
    │
    └─ [GENERATE_AND_EXECUTE_SQL]
            └─ context["ddl"] + question → LLM → SQL
            └─ SQL 실행 → 실패 시 최대 3회 재시도
            └─ context["generatedSql"], context["result"] 에 저장
    │
    ▼
ConversationModel 저장 (질문, SQL, 결과, 시도 기록)
    │
    ▼
응답 반환
```

---

## 파이프라인 정의 방식

### Agent 등록 시 기본 파이프라인 지정

```json
POST /api/agents
{
  "provider": "OLLAMA",
  "modelName": "qwen.qwen3-coder-30b-a3b-v1:0",
  "systemPrompt": "당신은 SRE 엔지니어입니다.",
  "tools": ["SEARCH_SCHEMA", "GENERATE_AND_EXECUTE_SQL"]
}
```

`tools` 배열의 순서가 곧 실행 파이프라인이다.

### 요청 시 파이프라인 오버라이드 (선택)

```json
POST /api/users/1/conversations
{
  "agentId": 1,
  "dataSourceId": 1,
  "question": "월별 매출 합계 알려줘",
  "tools": ["SEARCH_SCHEMA", "GENERATE_AND_EXECUTE_SQL", "SEND_NOTIFICATION"]
}
```

요청에 `tools`를 명시하면 Agent 기본 파이프라인을 그 요청에 한해 덮어쓴다.
명시하지 않으면 Agent에 등록된 파이프라인이 그대로 실행된다.

---

## 현재 구현된 Tool 목록

| Tool | 설명 | 상태 |
|------|------|------|
| `SEARCH_SCHEMA` | DataSource DDL 조회 후 컨텍스트에 저장 | 구현 완료 |
| `GENERATE_AND_EXECUTE_SQL` | SQL 생성 + 실행 + 실패 시 재시도 (최대 3회) | 구현 완료 |
| `SEARCH_TOOL_MEMORY` | 과거 성공 SQL 예제 벡터 검색 (RAG) | 미구현 |
| `GENERATE_CHART` | 결과를 차트로 변환 | 미구현 |
| `SEND_NOTIFICATION` | Slack/Email 알림 발송 | Stub |

---

## 확장 시나리오

**알림 발송 파이프라인**
```
SEARCH_SCHEMA → GENERATE_AND_EXECUTE_SQL → SEND_NOTIFICATION
```

**과거 SQL 참고 파이프라인 (RAG)**
```
SEARCH_SCHEMA → SEARCH_TOOL_MEMORY → GENERATE_AND_EXECUTE_SQL
```

**차트 생성 파이프라인**
```
SEARCH_SCHEMA → GENERATE_AND_EXECUTE_SQL → GENERATE_CHART
```

새로운 Tool은 `Tool` 인터페이스를 구현하고 `AgentToolType` enum에 항목을 추가하는 것만으로 파이프라인에 조합 가능하다.

---

## 아키텍처 레이어 매핑

```
domain/conversation/tool/
├── Tool.java                    ← Tool 인터페이스
├── ToolContext.java              ← 공유 컨텍스트 (Map 기반)
├── ToolContextKeys.java          ← 컨텍스트 키 상수
└── ToolPipelineExecutor.java     ← 파이프라인 실행기

infrastructure/tool/
├── SearchSchemaTool.java
├── GenerateAndExecuteSqlTool.java
└── SendNotificationTool.java

application/conversation/
└── TextToSqlFacade.java          ← 파이프라인 조립 및 실행
```
