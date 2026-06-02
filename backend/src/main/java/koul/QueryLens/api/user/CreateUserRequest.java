package koul.QueryLens.api.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import koul.QueryLens.domain.user.UserSource;

public record CreateUserRequest(
        @NotBlank String externalUserId,
        @NotNull UserSource source
) {
}
