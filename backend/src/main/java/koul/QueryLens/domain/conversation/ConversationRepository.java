package koul.QueryLens.domain.conversation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<ConversationModel, Long> {
}
