package koul.QueryLens.domain.agent;

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
@Table(name = "agents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentModel extends BaseEntity {

    @Embedded
    private LlmModel llmModel;

    @Column(columnDefinition = "text")
    private String systemPrompt;

    @ElementCollection
    @CollectionTable(name = "agent_tools", joinColumns = @JoinColumn(name = "agent_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tool_type", nullable = false)
    private List<AgentToolType> tools = new ArrayList<>();

    public static AgentModel create(LlmModel llmModel, String systemPrompt, List<AgentToolType> tools) {
        if (llmModel == null) {
            throw new CoreException(ErrorType.INVALID_INPUT, "LLM 모델 정보가 존재하지 않습니다.");
        }
        if (llmModel.provider() == null) {
            throw new CoreException(ErrorType.INVALID_INPUT, "LLM 프로바이더가 존재하지 않습니다.");
        }
        if (llmModel.modelName() == null || llmModel.modelName().isBlank()) {
            throw new CoreException(ErrorType.INVALID_INPUT, "LLM 모델명이 존재하지 않습니다.");
        }
        AgentModel agent = new AgentModel();
        agent.llmModel = llmModel;
        agent.systemPrompt = systemPrompt;
        agent.tools = tools != null ? new ArrayList<>(tools) : new ArrayList<>();
        return agent;
    }
}
