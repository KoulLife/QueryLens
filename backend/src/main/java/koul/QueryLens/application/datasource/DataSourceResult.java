package koul.QueryLens.application.datasource;

import koul.QueryLens.domain.datasource.DataSourceModel;
import koul.QueryLens.domain.datasource.DbType;
import koul.QueryLens.domain.datasource.connection.DataSourceConnection;

import java.time.LocalDateTime;

public record DataSourceResult(
        Long id,
        DbType dbType,
        DataSourceConnection connection,
        LocalDateTime createdAt
) {
    public static DataSourceResult from(DataSourceModel model) {
        return new DataSourceResult(
                model.getId(),
                model.getDbType(),
                model.getConnection(),
                model.getCreatedAt()
        );
    }
}
