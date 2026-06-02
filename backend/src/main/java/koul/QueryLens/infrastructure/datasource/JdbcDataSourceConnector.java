package koul.QueryLens.infrastructure.datasource;

import koul.QueryLens.domain.datasource.DataSourceConnector;
import koul.QueryLens.domain.datasource.connection.ClickHouseConnection;
import koul.QueryLens.domain.datasource.connection.ClickHouseProtocol;
import koul.QueryLens.domain.datasource.connection.DataSourceConnection;
import koul.QueryLens.domain.datasource.connection.PostgreSqlConnection;
import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;
import org.springframework.stereotype.Component;

import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class JdbcDataSourceConnector implements DataSourceConnector {

    @Override
    public void verify(DataSourceConnection connection) {
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
            if (!conn.isValid(3)) {
                throw new CoreException(ErrorType.DATA_SOURCE_CONNECTION_FAILED, "데이터소스 연결에 실패했습니다.");
            }
        } catch (SQLException e) {
            throw new CoreException(ErrorType.DATA_SOURCE_CONNECTION_FAILED, "데이터소스 연결에 실패했습니다. " + e.getMessage());
        }
    }
}
