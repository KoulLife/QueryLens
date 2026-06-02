package koul.QueryLens.application.agent;

import koul.QueryLens.domain.agent.AgentModel;
import koul.QueryLens.domain.agent.AgentRepository;
import koul.QueryLens.domain.agent.AgentToolType;
import koul.QueryLens.domain.agent.LlmModel;
import koul.QueryLens.domain.agent.LlmProvider;
import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AgentApplicationService {

    private final AgentRepository agentRepository;

    public AgentResult create(LlmProvider provider, String modelName, String systemPrompt, List<AgentToolType> tools) {
        LlmModel llmModel = new LlmModel(provider, modelName);
        AgentModel agent = agentRepository.save(AgentModel.create(llmModel, systemPrompt, tools));
        return AgentResult.from(agent);
    }

    @Transactional(readOnly = true)
    public AgentResult findById(Long id) {
        AgentModel agent = agentRepository.findById(id)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 에이전트입니다."));
        return AgentResult.from(agent);
    }
}
