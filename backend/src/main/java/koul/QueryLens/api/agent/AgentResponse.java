package koul.QueryLens.api.agent;

import koul.QueryLens.application.agent.AgentResult;
import koul.QueryLens.domain.agent.AgentToolType;
import koul.QueryLens.domain.agent.LlmProvider;

import java.time.LocalDateTime;
import java.util.List;

public record AgentResponse(
        Long id,
        LlmProvider provider,
        String modelName,
        String systemPrompt,
        List<AgentToolType> tools,
        LocalDateTime createdAt
) {
    public static AgentResponse from(AgentResult result) {
        return new AgentResponse(
                result.id(),
                result.provider(),
                result.modelName(),
                result.systemPrompt(),
                result.tools(),
                result.createdAt()
        );
    }
}
