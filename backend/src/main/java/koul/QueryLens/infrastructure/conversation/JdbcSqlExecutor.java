package koul.QueryLens.infrastructure.conversation;

import koul.QueryLens.domain.conversation.sql.SqlExecutor;
import koul.QueryLens.domain.datasource.connection.ClickHouseConnection;
import koul.QueryLens.domain.datasource.connection.ClickHouseProtocol;
import koul.QueryLens.domain.datasource.connection.DataSourceConnection;
import koul.QueryLens.domain.datasource.connection.PostgreSqlConnection;
import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class JdbcSqlExecutor implements SqlExecutor {

    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Override
    public String execute(String sql, DataSourceConnection connection) {
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

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            List<Map<String, Object>> rows = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                rows.add(row);
            }

            return objectMapper.writeValueAsString(rows);
        } catch (SQLException e) {
            throw new CoreException(ErrorType.SQL_EXECUTION_FAILED, "SQL 실행에 실패했습니다. " + e.getMessage());
        } catch (Exception e) {
            throw new CoreException(ErrorType.SQL_EXECUTION_FAILED, "SQL 실행 중 오류가 발생했습니다.");
        }
    }
}
