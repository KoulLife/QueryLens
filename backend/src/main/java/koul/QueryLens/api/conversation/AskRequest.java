package koul.QueryLens.api.conversation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import koul.QueryLens.domain.agent.AgentToolType;

import java.util.List;

public record AskRequest(
        @NotNull Long agentId,
        @NotNull Long dataSourceId,
        @NotBlank String question,
        List<AgentToolType> tools
) {
}
