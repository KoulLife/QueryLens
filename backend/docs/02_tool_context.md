# ToolContext 설계

## Background

Tool Pipeline 구조에서 각 Tool은 독립적으로 실행된다.
Tool 간 데이터를 어떻게 전달할 것인가가 핵심 설계 문제다.

예를 들어 `SEARCH_SCHEMA`가 수집한 DDL 정보를 `GENERATE_AND_EXECUTE_SQL`이 써야 하고,
SQL 실행 결과를 `SEND_NOTIFICATION`이 알림으로 보내야 한다.

이 데이터 흐름을 담는 그릇이 `ToolContext`다.

---

## 설계 선택지와 결정

### 1. 고정 필드 방식 vs Map 기반 동적 방식

**선택지 A — 고정 필드**

```java
class ToolContext {
    String schema;
    String generatedSql;
    String result;
    String errorMessage;
}
```

장점: 타입 안전, IDE 자동완성 지원  
단점: Tool이 추가될 때마다 ToolContext 클래스를 수정해야 한다.
Tool은 서로 독립적이어야 하는데, 특정 Tool의 데이터를 위해 공유 클래스가 변경되면 결합도가 높아진다.

**선택지 B — Map 기반 동적 저장소**

```java
class ToolContext {
    // 요청 시 고정값
    Long userId;
    Long agentId;
    Long dataSourceId;
    String question;

    // Tool이 자유롭게 읽고 쓰는 저장소
    Map<String, Object> store;
}
```

장점: 새 Tool이 추가되어도 ToolContext를 수정할 필요 없다. 각 Tool이 자신의 키만 정의하면 된다.  
단점: 컴파일 타임에 키 오타를 잡을 수 없다. 타입 캐스팅이 필요하다.

**→ 결정: Map 기반 동적 방식 채택**

키 오타 문제는 `ToolContextKeys` 상수 클래스로 해결한다.
Tool 간 독립성과 확장성이 더 중요하다.

---

### 2. 키 이름 — SCHEMA vs DDL

초기 설계에서는 스키마 정보를 담는 키를 `schema`로 명명했다.
그러나 실제로 LLM에 전달하는 것은 `CREATE TABLE ...` 형태의 DDL 문자열이다.

`schema`는 데이터베이스의 스키마(구조 전체)를 의미하는 넓은 용어이고,
LLM 프롬프트에 삽입되는 내용은 구체적으로 DDL이다.

**→ 결정: 키 이름을 `ddl`로 변경**

명확성을 위해 `ToolContextKeys.DDL`을 사용한다.

---

## ToolContext 구조

```java
// 요청 고정값 — 모든 Tool에서 읽기 가능
context.getUserId()
context.getAgentId()
context.getDataSourceId()
context.getQuestion()

// 동적 저장소 — Tool이 읽고 씀
context.put(key, value)
context.get(key, Class<T>)
context.has(key)
```

---

## ToolContextKeys — 키 상수 정의

```java
public class ToolContextKeys {
    public static final String DDL            = "ddl";           // SEARCH_SCHEMA 가 저장
    public static final String GENERATED_SQL  = "generatedSql";  // GENERATE_AND_EXECUTE_SQL 이 저장
    public static final String RESULT         = "result";        // GENERATE_AND_EXECUTE_SQL 이 저장
    public static final String ERROR_MESSAGE  = "errorMessage";  // 실패 시 저장
}
```

문자열 키를 직접 쓰지 않고 상수를 참조하여 오타와 중복을 방지한다.

---

## 실행 흐름 예시

```
[요청 진입]
ToolContext {
  userId: 1, agentId: 1, dataSourceId: 1,
  question: "월별 주문 수 알려줘",
  store: {}
}

────────────────────────────────

[SEARCH_SCHEMA 실행 후]
store: {
  "ddl": "CREATE TABLE orders (id bigint NOT NULL, ...);\nCREATE TABLE users (...);"
}

────────────────────────────────

[GENERATE_AND_EXECUTE_SQL 실행 후]
store: {
  "ddl": "CREATE TABLE orders ...",
  "generatedSql": "SELECT DATE_TRUNC('month', created_at), COUNT(*) FROM orders GROUP BY 1",
  "result": "[{\"date_trunc\":\"2026-01-01\",\"count\":\"1234\"}, ...]"
}

────────────────────────────────

[SEND_NOTIFICATION 실행 시]
context.get(ToolContextKeys.RESULT, String.class)  ← 결과를 읽어 알림 발송
```

---

## Tool 구현 예시

```java
// 쓰기
@Override
public void execute(ToolContext context) {
    String ddl = fetchDdl(context.getDataSourceId());
    context.put(ToolContextKeys.DDL, ddl);
}

// 읽기
@Override
public void execute(ToolContext context) {
    String ddl = context.has(ToolContextKeys.DDL)
            ? context.get(ToolContextKeys.DDL, String.class)
            : "";
    // ... ddl을 LLM 프롬프트에 삽입
}
```

---

## 설계 원칙

- **Tool은 ToolContext를 통해서만 데이터를 주고받는다.** Tool 간 직접 참조 금지.
- **각 Tool은 자신이 필요한 키를 읽고, 결과를 정해진 키에 쓴다.**
- **필수 데이터가 없으면 빈 문자열("")로 처리한다.** Tool은 방어적으로 동작한다.
- **키는 항상 ToolContextKeys 상수를 사용한다.** 문자열 리터럴 직접 사용 금지.
