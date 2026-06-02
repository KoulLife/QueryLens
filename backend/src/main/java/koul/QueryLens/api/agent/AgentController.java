package koul.QueryLens.api.agent;

import jakarta.validation.Valid;
import koul.QueryLens.api.ApiResponse;
import koul.QueryLens.application.agent.AgentApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentApplicationService agentApplicationService;

    @PostMapping
    public ApiResponse<AgentResponse> create(@RequestBody @Valid CreateAgentRequest request) {
        return ApiResponse.success(
                AgentResponse.from(agentApplicationService.create(
                        request.provider(),
                        request.modelName(),
                        request.systemPrompt(),
                        request.tools()
                ))
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<AgentResponse> findById(@PathVariable Long id) {
        return ApiResponse.success(
                AgentResponse.from(agentApplicationService.findById(id))
        );
    }
}
