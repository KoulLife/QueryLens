package koul.QueryLens.infrastructure.datasource;

import koul.QueryLens.domain.datasource.DataSourceSchemaReader;
import koul.QueryLens.domain.datasource.connection.ClickHouseConnection;
import koul.QueryLens.domain.datasource.connection.ClickHouseProtocol;
import koul.QueryLens.domain.datasource.connection.DataSourceConnection;
import koul.QueryLens.domain.datasource.connection.PostgreSqlConnection;
import koul.QueryLens.domain.datasource.schema.DataSourceSchema;
import koul.QueryLens.domain.datasource.schema.SchemaField;
import koul.QueryLens.domain.datasource.schema.SchemaObject;
import koul.QueryLens.domain.datasource.schema.SchemaObjectType;
import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcDataSourceSchemaReader implements DataSourceSchemaReader {

    @Override
    public DataSourceSchema read(DataSourceConnection connection) {
        String url;
        String username;
        String password;

        if (connection instanceof PostgreSqlConnection pg) {
            url = String.format("jdbc:postgresql://%s:%d/%s", pg.getHost(), pg.getPort(), pg.getDatabaseName());
            username = pg.getUsername();
            password = pg.getEncryptedPassword();
        } else if (connection instanceof ClickHouseConnection ch) {
            String protocol = ch.getProtocol() == ClickHouseProtocol.NATIVE ? "tcp" : "http";
            url = String.format("jdbc:clickhouse://%s://%s:%d/%s", protocol, ch.getHost(), ch.getPort(), ch.getDatabaseName());
            username = ch.getUsername();
            password = ch.getEncryptedPassword();
        } else {
            throw new CoreException(ErrorType.INVALID_INPUT, "지원하지 않는 데이터소스 타입입니다.");
        }

        try (var conn = DriverManager.getConnection(url, username, password)) {
            DatabaseMetaData metaData = conn.getMetaData();
            List<SchemaObject> objects = new ArrayList<>();

            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE", "VIEW"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    String tableType = tables.getString("TABLE_TYPE");
                    String schemaName = tables.getString("TABLE_SCHEM");

                    SchemaObjectType objectType = "VIEW".equals(tableType) ? SchemaObjectType.VIEW : SchemaObjectType.TABLE;
                    String qualifiedName = schemaName != null ? schemaName + "." + tableName : tableName;

                    List<SchemaField> fields = readFields(metaData, schemaName, tableName);
                    objects.add(new SchemaObject(tableName, qualifiedName, objectType, null, fields));
                }
            }

            return new DataSourceSchema(objects, LocalDateTime.now());

        } catch (SQLException e) {
            throw new CoreException(ErrorType.DATA_SOURCE_CONNECTION_FAILED, "스키마 읽기에 실패했습니다. " + e.getMessage());
        }
    }

    private List<SchemaField> readFields(DatabaseMetaData metaData, String schemaName, String tableName) throws SQLException {
        List<SchemaField> fields = new ArrayList<>();
        try (ResultSet columns = metaData.getColumns(null, schemaName, tableName, "%")) {
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String typeName = columns.getString("TYPE_NAME");
                boolean nullable = DatabaseMetaData.columnNullable == columns.getInt("NULLABLE");
                fields.add(new SchemaField(columnName, typeName, nullable, null));
            }
        }
        return fields;
    }
}
