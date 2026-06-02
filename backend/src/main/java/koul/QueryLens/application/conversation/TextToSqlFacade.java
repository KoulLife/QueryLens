package koul.QueryLens.application.conversation;

import koul.QueryLens.domain.agent.AgentModel;
import koul.QueryLens.domain.agent.AgentRepository;
import koul.QueryLens.domain.agent.AgentToolType;
import koul.QueryLens.domain.conversation.ConversationAttempt;
import koul.QueryLens.domain.conversation.ConversationModel;
import koul.QueryLens.domain.conversation.ConversationRepository;
import koul.QueryLens.domain.conversation.tool.ToolContext;
import koul.QueryLens.domain.conversation.tool.ToolContextKeys;
import koul.QueryLens.domain.conversation.tool.ToolPipelineExecutor;
import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TextToSqlFacade {

    private final ConversationRepository conversationRepository;
    private final AgentRepository agentRepository;
    private final ToolPipelineExecutor toolPipelineExecutor;

    public ConversationResult ask(Long userId, Long agentId, Long dataSourceId, String question, List<AgentToolType> toolsOverride) {
        AgentModel agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 에이전트입니다."));

        ConversationModel conversation = conversationRepository.save(
                ConversationModel.create(userId, agentId, question)
        );

        ToolContext context = new ToolContext(userId, agentId, dataSourceId, question);

        List<AgentToolType> pipeline = (toolsOverride != null && !toolsOverride.isEmpty())
                ? toolsOverride
                : agent.getTools();

        try {
            toolPipelineExecutor.execute(pipeline, context);

            String finalSql = context.get(ToolContextKeys.GENERATED_SQL, String.class);
            String result = context.get(ToolContextKeys.RESULT, String.class);

            conversation.addAttempt(ConversationAttempt.success(1, finalSql));
            conversation.complete(finalSql, result);

        } catch (CoreException e) {
            String failedSql = context.get(ToolContextKeys.GENERATED_SQL, String.class);
            conversation.addAttempt(ConversationAttempt.failure(1, failedSql, e.getCustomMessage()));
            throw e;
        }

        return ConversationResult.from(conversation);
    }
}
