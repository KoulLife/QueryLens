package koul.QueryLens.domain.agent;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record LlmModel(
        @Enumerated(EnumType.STRING) LlmProvider provider,
        String modelName
) {
}
