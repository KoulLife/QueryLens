package koul.QueryLens.domain.conversation.sql;

import koul.QueryLens.domain.agent.LlmProvider;

public record SqlGenerationRequest(
        String question,
        String systemPrompt,
        String ddl,
        LlmProvider provider,
        String modelName,
        String previousSql,      // 재시도 시 이전 SQL
        String previousError     // 재시도 시 이전 오류 메시지
) {
}
