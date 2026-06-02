package koul.QueryLens.domain.conversation;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class ConversationAttempt {

    private Integer attemptNo;

    @Column(columnDefinition = "text")
    private String generatedSql;

    @Enumerated(EnumType.STRING)
    private ConversationAttemptStatus status;

    private String errorMessage;

    public static ConversationAttempt success(Integer attemptNo, String generatedSql) {
        ConversationAttempt attempt = new ConversationAttempt();
        attempt.attemptNo = attemptNo;
        attempt.generatedSql = generatedSql;
        attempt.status = ConversationAttemptStatus.SUCCESS;
        return attempt;
    }

    public static ConversationAttempt failure(Integer attemptNo, String generatedSql, String errorMessage) {
        ConversationAttempt attempt = new ConversationAttempt();
        attempt.attemptNo = attemptNo;
        attempt.generatedSql = generatedSql;
        attempt.status = ConversationAttemptStatus.FAILED;
        attempt.errorMessage = errorMessage;
        return attempt;
    }
}
