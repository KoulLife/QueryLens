# QueryLens

자연어 질문을 SQL로 변환하여 데이터베이스를 조회할 수 있는 Text-to-SQL AI 플랫폼이다.
SQL을 모르는 사용자도 질문을 입력하면 AI 에이전트가 DB 스키마를 분석하고 SQL을 생성·실행하여 결과를 반환한다.
PostgreSQL, ClickHouse 등 다양한 데이터소스를 연결할 수 있으며, Gemini·Ollama 등 여러 LLM 공급자를 지원한다.

## Domain Overview

### User

사용자 인증 및 권한을 관리한다.

외부 시스템(Grafana, Internal Service)의 사용자 식별자를 `ExternalUserIdentity`로 추상화하여 수용하며,
`UserRole`을 통해 관리자(`ADMIN`)와 일반 사용자(`USER`)를 구분한다.

| 클래스                 | 설명                                             |
| ---------------------- | ------------------------------------------------ |
| `UserModel`            | 사용자 핵심 도메인 객체 (JPA Entity)             |
| `ExternalUserIdentity` | 외부 시스템 식별자 (externalUserId + source), `@Embeddable` |
| `UserRole`             | 사용자 권한 (`USER`, `ADMIN`)                    |
| `UserSource`           | 사용자 유입 경로 (`GRAFANA`, `INTERNAL_SERVICE`) |

---

### DataSource

쿼리 대상 데이터베이스의 연결 정보와 스키마를 관리한다.

PostgreSQL과 ClickHouse를 지원하며, 각 DB 타입에 맞는 `DataSourceConnection` 구현체로 연결을 처리한다.
연결 정보와 스키마는 JSON으로 직렬화하여 `text` 컬럼에 저장한다.
스키마 싱크(`/schema-sync`) 를 통해 실제 DB의 DDL을 읽어 저장하며, 이후 SQL 생성 시 LLM 프롬프트에 자동으로 포함된다.

| 클래스                   | 설명                                          |
| ------------------------ | --------------------------------------------- |
| `DataSourceModel`        | 데이터소스 핵심 도메인 객체 (JPA Entity)      |
| `DbType`                 | 지원 DB 종류 (`POSTGRESQL`, `CLICKHOUSE`)     |
| `DataSourceConnection`   | DB 연결 정보 인터페이스 (`@JsonTypeInfo` 폴리모픽 직렬화) |
| `PostgreSqlConnection`   | PostgreSQL 연결 정보                          |
| `ClickHouseConnection`   | ClickHouse 연결 정보 (HTTP / NATIVE 프로토콜) |
| `DataSourceConnector`    | DB 연결 검증 인터페이스                       |
| `DataSourceSchemaReader` | JDBC 메타데이터로 DDL을 읽는 인터페이스       |
| `DataSourceSchema`       | 동기화된 DB 스키마 (테이블·뷰·컬럼 목록)      |
| `SchemaObject`           | 테이블 또는 뷰 단위 스키마 객체               |
| `SchemaField`            | 컬럼 단위 스키마 정보 (타입, nullable 등)     |

---

### UserDataSource

사용자와 데이터소스 간의 접근 권한을 관리한다.

사용자마다 조회 가능한 데이터소스를 제한할 수 있으며, 동일한 (사용자, 데이터소스) 조합의 중복 등록을 방지한다.

| 클래스               | 설명                                               |
| -------------------- | -------------------------------------------------- |
| `UserDataSourceModel` | 사용자-데이터소스 연결 도메인 객체 (JPA Entity)   |

---

### Agent

Text-to-SQL을 수행하는 AI 에이전트의 설정을 관리한다.

사용할 LLM 공급자와 모델명, 시스템 프롬프트, 실행할 Tool 파이프라인을 `AgentModel` 하나로 정의한다.
`tools` 목록의 순서가 곧 실행 파이프라인이며, 요청 시 오버라이드도 가능하다.

| 클래스          | 설명                                                        |
| --------------- | ----------------------------------------------------------- |
| `AgentModel`    | 에이전트 설정 핵심 도메인 객체 (JPA Entity)                 |
| `LlmModel`      | LLM 공급자 및 모델명, `@Embeddable`                         |
| `LlmProvider`   | 지원 LLM 공급자 (`OPENAI`, `ANTHROPIC`, `GEMINI`, `OLLAMA`) |
| `AgentToolType` | 에이전트 파이프라인에 포함할 Tool 종류                      |

**AgentToolType**

| 값                          | 설명                                          | 상태    |
| --------------------------- | --------------------------------------------- | ------- |
| `SEARCH_SCHEMA`             | DataSource에서 DDL을 읽어 컨텍스트에 저장     | 구현 완료 |
| `GENERATE_AND_EXECUTE_SQL`  | SQL 생성 + 실행, 실패 시 최대 3회 재시도      | 구현 완료 |
| `SEARCH_TOOL_MEMORY`        | 과거 성공한 SQL 예제 벡터 검색 (RAG)          | 미구현  |
| `GENERATE_CHART`            | 실행 결과를 차트로 변환                       | 미구현  |
| `SEND_NOTIFICATION`         | Slack/Email 알림 발송                         | Stub    |

---

### Conversation

사용자의 자연어 질문을 SQL로 변환하는 과정과 그 이력을 관리한다.

요청이 들어오면 Agent에 정의된 Tool 파이프라인이 순서대로 실행되며,
각 시도의 생성 SQL·성공 여부·오류 메시지는 `ConversationAttempt`에 기록된다.
최종 확정된 SQL은 `finalSql`에, 실행 결과는 `result`에 보관한다.

Tool 간 데이터는 `ToolContext`를 통해 공유되며, 각 Tool은 `ToolContextKeys`에 정의된 키로 값을 읽고 쓴다.

| 클래스                      | 설명                                                       |
| --------------------------- | ---------------------------------------------------------- |
| `ConversationModel`         | SQL 변환 요청 핵심 도메인 객체 (JPA Entity)                |
| `ConversationAttempt`       | 개별 SQL 생성 시도 (생성된 SQL, 성공/실패, 오류 메시지), `@Embeddable` |
| `ConversationAttemptStatus` | SQL 생성 결과 (`SUCCESS`, `FAILED`)                        |
| `Tool`                      | Tool 인터페이스 (`type()`, `execute(ToolContext)`)          |
| `ToolContext`                | Tool 간 공유 데이터 저장소 (고정 요청값 + Map 기반 동적 저장소) |
| `ToolContextKeys`           | ToolContext 키 상수 (`ddl`, `generatedSql`, `result`, `errorMessage`) |
| `ToolPipelineExecutor`      | 파이프라인 순서대로 Tool을 실행하는 실행기                 |
| `SqlGenerator`              | SQL 생성 인터페이스 (LLM 호출)                             |
| `SqlExecutor`               | SQL 실행 인터페이스 (JDBC)                                 |
| `SqlGenerationRequest`      | SQL 생성 요청 (question, ddl, 이전 SQL·오류 포함)          |
