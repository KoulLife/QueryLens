package koul.QueryLens.application.agent;

import koul.QueryLens.domain.agent.AgentModel;
import koul.QueryLens.domain.agent.AgentToolType;
import koul.QueryLens.domain.agent.LlmProvider;

import java.time.LocalDateTime;
import java.util.List;

public record AgentResult(
        Long id,
        LlmProvider provider,
        String modelName,
        String systemPrompt,
        List<AgentToolType> tools,
        LocalDateTime createdAt
) {
    public static AgentResult from(AgentModel agent) {
        return new AgentResult(
                agent.getId(),
                agent.getLlmModel().provider(),
                agent.getLlmModel().modelName(),
                agent.getSystemPrompt(),
                agent.getTools(),
                agent.getCreatedAt()
        );
    }
}
