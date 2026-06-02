package koul.QueryLens.api.datasource;

import jakarta.validation.constraints.NotNull;
import koul.QueryLens.domain.datasource.DbType;
import koul.QueryLens.domain.datasource.connection.DataSourceConnection;

public record CreateDataSourceRequest(
        @NotNull DbType dbType,
        @NotNull DataSourceConnection connection
) {
}
