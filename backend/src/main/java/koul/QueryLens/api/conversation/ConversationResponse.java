package koul.QueryLens.api.conversation;

import koul.QueryLens.application.conversation.ConversationResult;
import koul.QueryLens.domain.conversation.ConversationAttempt;

import java.time.LocalDateTime;
import java.util.List;

public record ConversationResponse(
        Long id,
        Long userId,
        Long agentId,
        String question,
        List<ConversationAttempt> attempts,
        String finalSql,
        String result,
        LocalDateTime createdAt
) {
    public static ConversationResponse from(ConversationResult result) {
        return new ConversationResponse(
                result.id(),
                result.userId(),
                result.agentId(),
                result.question(),
                result.attempts(),
                result.finalSql(),
                result.result(),
                result.createdAt()
        );
    }
}
