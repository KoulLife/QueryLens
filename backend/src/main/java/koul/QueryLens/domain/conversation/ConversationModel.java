package koul.QueryLens.domain.conversation;

import jakarta.persistence.*;
import koul.QueryLens.support.BaseEntity;
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
}
