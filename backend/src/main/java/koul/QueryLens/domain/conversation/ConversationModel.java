package koul.QueryLens.domain.conversation;

import jakarta.persistence.*;
import koul.QueryLens.support.BaseEntity;
import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConversationModel extends BaseEntity {

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long agentId;

    @Column(nullable = false, columnDefinition = "text")
    private String question;

    @ElementCollection
    @CollectionTable(name = "conversation_attempts", joinColumns = @JoinColumn(name = "conversation_id"))
    @OrderColumn(name = "attempt_order")
    private List<ConversationAttempt> attempts = new ArrayList<>();

    @Column(columnDefinition = "text")
    private String finalSql;

    @Column(columnDefinition = "text")
    private String result;

    public static ConversationModel create(Long userId, Long agentId, String question) {
        if (userId == null) {
            throw new CoreException(ErrorType.INVALID_INPUT, "사용자 ID가 존재하지 않습니다.");
        }
        if (agentId == null) {
            throw new CoreException(ErrorType.INVALID_INPUT, "에이전트 ID가 존재하지 않습니다.");
        }
        if (question == null || question.isBlank()) {
            throw new CoreException(ErrorType.INVALID_INPUT, "질문이 존재하지 않습니다.");
        }
        ConversationModel conversation = new ConversationModel();
        conversation.userId = userId;
        conversation.agentId = agentId;
        conversation.question = question;
        return conversation;
    }

    public void addAttempt(ConversationAttempt attempt) {
        this.attempts.add(attempt);
    }

    public void complete(String finalSql, String result) {
        this.finalSql = finalSql;
        this.result = result;
    }
}
