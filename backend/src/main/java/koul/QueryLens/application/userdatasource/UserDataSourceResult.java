package koul.QueryLens.application.userdatasource;

import koul.QueryLens.domain.userdatasource.UserDataSourceModel;

import java.time.LocalDateTime;

public record UserDataSourceResult(
        Long id,
        Long userId,
        Long dataSourceId,
        LocalDateTime createdAt
) {
    public static UserDataSourceResult from(UserDataSourceModel model) {
        return new UserDataSourceResult(
                model.getId(),
                model.getUserId(),
                model.getDataSourceId(),
                model.getCreatedAt()
        );
    }
}
