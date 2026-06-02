package koul.QueryLens.infrastructure.conversation;

import koul.QueryLens.domain.agent.LlmProvider;
import koul.QueryLens.domain.conversation.sql.SqlGenerationRequest;
import koul.QueryLens.domain.conversation.sql.SqlGenerator;
import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SpringAiSqlGenerator implements SqlGenerator {

    private final OpenAiChatModel openAiChatModel;
    private final ChatModel googleGenAiChatModel;

    public SpringAiSqlGenerator(
            OpenAiChatModel openAiChatModel,
            @Qualifier("googleGenAiChatModel") ChatModel googleGenAiChatModel
    ) {
        this.openAiChatModel = openAiChatModel;
        this.googleGenAiChatModel = googleGenAiChatModel;
    }

    @Override
    public String generate(SqlGenerationRequest request) {
        ChatModel chatModel = resolveChatModel(request.provider());
        Prompt prompt = buildPrompt(request);
        return chatModel.call(prompt).getResult().getOutput().getText();
    }

    private ChatModel resolveChatModel(LlmProvider provider) {
        if (provider == null) {
            return openAiChatModel;
        }
        return switch (provider) {
            case OLLAMA -> openAiChatModel;
            case GEMINI -> googleGenAiChatModel;
            default -> throw new CoreException(ErrorType.INVALID_INPUT, "지원하지 않는 LLM 프로바이더입니다: " + provider);
        };
    }

    private Prompt buildPrompt(SqlGenerationRequest request) {
        String system = """
                당신은 SQL 전문가입니다. 아래 지침과 DDL을 참고하여 사용자의 질문에 맞는 SQL 쿼리만 반환하세요.
                설명이나 마크다운 없이 SQL 문자열만 반환하세요.

                [지침]
                {systemPrompt}

                [DDL]
                {ddl}
                """;

        String userMessage = buildUserMessage(request);

        var systemMessage = new SystemPromptTemplate(system).createMessage(
                java.util.Map.of(
                        "systemPrompt", request.systemPrompt() != null ? request.systemPrompt() : "",
                        "ddl", request.ddl() != null ? request.ddl() : ""
                )
        );

        return new Prompt(java.util.List.of(systemMessage,
                new org.springframework.ai.chat.messages.UserMessage(userMessage)));
    }

    private String buildUserMessage(SqlGenerationRequest request) {
        if (request.previousSql() != null && request.previousError() != null) {
            return String.format("""
                    질문: %s

                    이전 시도한 SQL:
                    %s

                    실패 원인:
                    %s

                    위 오류를 참고하여 올바른 SQL을 다시 작성해주세요.
                    """, request.question(), request.previousSql(), request.previousError());
        }
        return request.question();
    }
}
