package koul.QueryLens.domain.conversation.sql;

import koul.QueryLens.domain.datasource.connection.DataSourceConnection;

public interface SqlExecutor {

    String execute(String sql, DataSourceConnection connection);
}
