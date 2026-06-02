package koul.QueryLens.domain.datasource;

import koul.QueryLens.domain.datasource.connection.DataSourceConnection;

public interface DataSourceConnector {

    void verify(DataSourceConnection connection);
}
