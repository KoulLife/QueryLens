package koul.QueryLens.infrastructure.tool;

import koul.QueryLens.domain.agent.AgentModel;
import koul.QueryLens.domain.agent.AgentRepository;
import koul.QueryLens.domain.agent.AgentToolType;
import koul.QueryLens.domain.conversation.sql.SqlExecutor;
import koul.QueryLens.domain.conversation.sql.SqlGenerationRequest;
import koul.QueryLens.domain.conversation.sql.SqlGenerator;
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
public class GenerateAndExecuteSqlTool implements Tool {

    private static final int MAX_RETRY = 10;

    private final SqlGenerator sqlGenerator;
    private final SqlExecutor sqlExecutor;
    private final AgentRepository agentRepository;
    private final DataSourceRepository dataSourceRepository;

    @Override
    public AgentToolType type() {
        return AgentToolType.GENERATE_AND_EXECUTE_SQL;
    }

    @Override
    public void execute(ToolContext context) {
        AgentModel agent = agentRepository.findById(context.getAgentId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 에이전트입니다."));

        DataSourceModel dataSource = dataSourceRepository.findById(context.getDataSourceId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 데이터소스입니다."));

        String ddl = context.has(ToolContextKeys.DDL) ? context.get(ToolContextKeys.DDL, String.class) : "";

        String previousSql = null;
        String previousError = null;

        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            SqlGenerationRequest request = new SqlGenerationRequest(
                    context.getQuestion(),
                    agent.getSystemPrompt(),
                    ddl,
                    agent.getLlmModel().provider(),
                    agent.getLlmModel().modelName(),
                    previousSql,
                    previousError
            );

            String sql = sqlGenerator.generate(request);
            context.put(ToolContextKeys.GENERATED_SQL, sql);

            try {
                String result = sqlExecutor.execute(sql, dataSource.getConnection());
                context.put(ToolContextKeys.RESULT, result);
                return;
            } catch (CoreException e) {
                previousSql = sql;
                previousError = e.getCustomMessage();

                if (attempt == MAX_RETRY) {
                    context.put(ToolContextKeys.ERROR_MESSAGE, previousError);
                    throw new CoreException(ErrorType.SQL_EXECUTION_FAILED,
                            String.format("SQL 실행이 %d회 시도 후 실패했습니다. 마지막 오류: %s", MAX_RETRY, previousError));
                }
            }
        }
    }
}
