package koul.QueryLens.domain.conversation.tool;

import koul.QueryLens.domain.agent.AgentToolType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ToolPipelineExecutor {

    private final Map<AgentToolType, Tool> toolRegistry;

    public ToolPipelineExecutor(List<Tool> tools) {
        this.toolRegistry = tools.stream()
                .collect(Collectors.toMap(Tool::type, Function.identity()));
    }

    public ToolContext execute(List<AgentToolType> pipeline, ToolContext context) {
        for (AgentToolType toolType : pipeline) {
            Tool tool = toolRegistry.get(toolType);
            if (tool == null) {
                throw new IllegalStateException("등록되지 않은 Tool입니다: " + toolType);
            }
            tool.execute(context);
        }
        return context;
    }
}
