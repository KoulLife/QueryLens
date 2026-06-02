package koul.QueryLens.application.user;

import koul.QueryLens.domain.user.UserModel;
import koul.QueryLens.domain.user.UserRole;
import koul.QueryLens.domain.user.UserSource;

import java.time.LocalDateTime;

public record UserResult(
        Long id,
        String externalUserId,
        UserSource source,
        UserRole role,
        LocalDateTime createdAt
) {
    public static UserResult from(UserModel user) {
        return new UserResult(
                user.getId(),
                user.getExternalIdentity().externalUserId(),
                user.getExternalIdentity().source(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
