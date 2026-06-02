package koul.QueryLens.domain.conversation.tool;

import koul.QueryLens.domain.agent.AgentToolType;

public interface Tool {

    AgentToolType type();

    void execute(ToolContext context);
}
