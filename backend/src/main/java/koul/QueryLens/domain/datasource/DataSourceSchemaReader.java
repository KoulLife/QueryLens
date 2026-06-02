package koul.QueryLens.domain.datasource;

import koul.QueryLens.domain.datasource.connection.DataSourceConnection;
import koul.QueryLens.domain.datasource.schema.DataSourceSchema;

public interface DataSourceSchemaReader {

    DataSourceSchema read(DataSourceConnection connection);
}
