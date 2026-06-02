package koul.QueryLens.application.conversation;

import koul.QueryLens.domain.conversation.ConversationAttempt;
import koul.QueryLens.domain.conversation.ConversationModel;

import java.time.LocalDateTime;
import java.util.List;

public record ConversationResult(
        Long id,
        Long userId,
        Long agentId,
        String question,
        List<ConversationAttempt> attempts,
        String finalSql,
        String result,
        LocalDateTime createdAt
) {
    public static ConversationResult from(ConversationModel model) {
        return new ConversationResult(
                model.getId(),
                model.getUserId(),
                model.getAgentId(),
                model.getQuestion(),
                model.getAttempts(),
                model.getFinalSql(),
                model.getResult(),
                model.getCreatedAt()
        );
    }
}
