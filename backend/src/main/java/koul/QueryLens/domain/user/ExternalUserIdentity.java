package koul.QueryLens.domain.user;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record ExternalUserIdentity(
        String externalUserId,
        @Enumerated(EnumType.STRING) UserSource source
) {
}
