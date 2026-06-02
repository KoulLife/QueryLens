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
}
