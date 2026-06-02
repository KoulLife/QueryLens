package koul.QueryLens.domain.datasource.connection;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import koul.QueryLens.domain.datasource.DbType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PostgreSqlConnection.class, name = "POSTGRESQL"),
        @JsonSubTypes.Type(value = ClickHouseConnection.class, name = "CLICKHOUSE")
})
public interface DataSourceConnection {

    boolean supports(DbType dbType);
}
