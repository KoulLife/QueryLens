package koul.QueryLens.domain.agent;

import jakarta.persistence.*;
import koul.QueryLens.support.BaseEntity;
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
}
