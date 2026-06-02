package koul.QueryLens.domain.datasource.connection;

import koul.QueryLens.domain.datasource.DbType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClickHouseConnection implements DataSourceConnection {

    private String host;
    private Integer port;
    private String databaseName;
    private String username;
    private String encryptedPassword;
    private ClickHouseProtocol protocol;

    @Override
    public boolean supports(DbType dbType) {
        return dbType == DbType.CLICKHOUSE;
    }
}
