package koul.QueryLens.domain.conversation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<ConversationModel, Long> {

    List<ConversationModel> findAllByUserId(Long userId);
}
