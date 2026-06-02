package koul.QueryLens.infrastructure.tool;

import koul.QueryLens.domain.agent.AgentToolType;
import koul.QueryLens.domain.conversation.tool.Tool;
import koul.QueryLens.domain.conversation.tool.ToolContext;
import koul.QueryLens.domain.conversation.tool.ToolContextKeys;
import koul.QueryLens.domain.datasource.DataSourceModel;
import koul.QueryLens.domain.datasource.DataSourceRepository;
import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchSchemaTool implements Tool {

    private final DataSourceRepository dataSourceRepository;

    @Override
    public AgentToolType type() {
        return AgentToolType.SEARCH_SCHEMA;
    }

    @Override
    public void execute(ToolContext context) {
        DataSourceModel dataSource = dataSourceRepository.findById(context.getDataSourceId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 데이터소스입니다."));

        String ddl = dataSource.getSchema() != null ? dataSource.getSchema().toDdl() : "";
        context.put(ToolContextKeys.DDL, ddl);
    }
}
