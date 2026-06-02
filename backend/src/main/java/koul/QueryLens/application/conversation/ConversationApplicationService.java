package koul.QueryLens.application.conversation;

import koul.QueryLens.domain.conversation.ConversationAttempt;
import koul.QueryLens.domain.conversation.ConversationModel;
import koul.QueryLens.domain.conversation.ConversationRepository;
import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationApplicationService {

    private final ConversationRepository conversationRepository;

    public ConversationResult create(Long userId, Long agentId, String question) {
        ConversationModel conversation = conversationRepository.save(
                ConversationModel.create(userId, agentId, question)
        );
        return ConversationResult.from(conversation);
    }

    public ConversationResult complete(Long conversationId, ConversationAttempt attempt, String finalSql, String result) {
        ConversationModel conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 대화입니다."));
        conversation.addAttempt(attempt);
        conversation.complete(finalSql, result);
        return ConversationResult.from(conversation);
    }

    @Transactional(readOnly = true)
    public List<ConversationResult> findAllByUserId(Long userId) {
        return conversationRepository.findAllByUserId(userId).stream()
                .map(ConversationResult::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConversationResult findById(Long id) {
        ConversationModel conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 대화입니다."));
        return ConversationResult.from(conversation);
    }
}
