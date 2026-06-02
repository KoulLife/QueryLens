package koul.QueryLens.domain.conversation.tool;

public final class ToolContextKeys {

    private ToolContextKeys() {}

    public static final String DDL            = "ddl";           // SEARCH_SCHEMA 가 채움 (CREATE TABLE ...)
    public static final String GENERATED_SQL  = "generatedSql";  // GENERATE_AND_EXECUTE_SQL 이 채움
    public static final String RESULT         = "result";         // GENERATE_AND_EXECUTE_SQL 이 채움
    public static final String ERROR_MESSAGE  = "errorMessage";   // 실패 시 채움
}
