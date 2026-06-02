package koul.QueryLens.api.user;

import koul.QueryLens.application.user.UserResult;
import koul.QueryLens.domain.user.UserRole;
import koul.QueryLens.domain.user.UserSource;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String externalUserId,
        UserSource source,
        UserRole role,
        LocalDateTime createdAt
) {
    public static UserResponse from(UserResult result) {
        return new UserResponse(
                result.id(),
                result.externalUserId(),
                result.source(),
                result.role(),
                result.createdAt()
        );
    }
}
