package koul.QueryLens.support.config;

import koul.QueryLens.domain.conversation.tool.Tool;
import koul.QueryLens.domain.conversation.tool.ToolPipelineExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ToolPipelineConfig {

    @Bean
    public ToolPipelineExecutor toolPipelineExecutor(List<Tool> tools) {
        return new ToolPipelineExecutor(tools);
    }
}
