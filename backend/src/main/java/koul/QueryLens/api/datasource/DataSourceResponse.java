package koul.QueryLens.api.datasource;

import koul.QueryLens.application.datasource.DataSourceResult;
import koul.QueryLens.domain.datasource.DbType;
import koul.QueryLens.domain.datasource.connection.DataSourceConnection;

import java.time.LocalDateTime;

public record DataSourceResponse(
        Long id,
        DbType dbType,
        DataSourceConnection connection,
        LocalDateTime schemaSyncedAt,
        LocalDateTime createdAt
) {
    public static DataSourceResponse from(DataSourceResult result) {
        return new DataSourceResponse(
                result.id(),
                result.dbType(),
                result.connection(),
                result.schemaSyncedAt(),
                result.createdAt()
        );
    }
}
