package koul.QueryLens.api.conversation;

import jakarta.validation.Valid;
import koul.QueryLens.api.ApiResponse;
import koul.QueryLens.application.conversation.TextToSqlFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final TextToSqlFacade textToSqlFacade;

    @PostMapping
    public ApiResponse<ConversationResponse> ask(
            @PathVariable Long userId,
            @RequestBody @Valid AskRequest request
    ) {
        return ApiResponse.success(
                ConversationResponse.from(
                        textToSqlFacade.ask(userId, request.agentId(), request.dataSourceId(), request.question(), request.tools())
                )
        );
    }
}
