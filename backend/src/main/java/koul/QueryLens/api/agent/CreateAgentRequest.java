package koul.QueryLens.api.agent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import koul.QueryLens.domain.agent.AgentToolType;
import koul.QueryLens.domain.agent.LlmProvider;

import java.util.List;

public record CreateAgentRequest(
        @NotNull LlmProvider provider,
        @NotBlank String modelName,
        String systemPrompt,
        List<AgentToolType> tools
) {
}
