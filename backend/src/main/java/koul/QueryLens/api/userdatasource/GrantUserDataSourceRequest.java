package koul.QueryLens.api.userdatasource;

import jakarta.validation.constraints.NotNull;

public record GrantUserDataSourceRequest(
        @NotNull Long dataSourceId
) {
}
