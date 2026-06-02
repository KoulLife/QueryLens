package koul.QueryLens.api.userdatasource;

import koul.QueryLens.application.userdatasource.UserDataSourceResult;

import java.time.LocalDateTime;

public record UserDataSourceResponse(
        Long id,
        Long userId,
        Long dataSourceId,
        LocalDateTime createdAt
) {
    public static UserDataSourceResponse from(UserDataSourceResult result) {
        return new UserDataSourceResponse(
                result.id(),
                result.userId(),
                result.dataSourceId(),
                result.createdAt()
        );
    }
}
