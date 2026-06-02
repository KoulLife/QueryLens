package koul.QueryLens.application.datasource;

import koul.QueryLens.domain.datasource.DataSourceConnector;
import koul.QueryLens.domain.datasource.DataSourceModel;
import koul.QueryLens.domain.datasource.DataSourceRepository;
import koul.QueryLens.domain.datasource.DbType;
import koul.QueryLens.domain.datasource.connection.PostgreSqlConnection;
import koul.QueryLens.support.error.CoreException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DataSourceApplicationServiceTest {

    @InjectMocks
    private DataSourceApplicationService dataSourceApplicationService;

    @Mock
    private DataSourceRepository dataSourceRepository;

    @Mock
    private DataSourceConnector dataSourceConnector;

    @Test
    void create_returns_DataSourceResult_on_success() {
        PostgreSqlConnection connection = new PostgreSqlConnection("localhost", 5432, "mydb", "user", "password");
        given(dataSourceRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        DataSourceResult result = dataSourceApplicationService.create(DbType.POSTGRESQL, connection);

        assertThat(result.dbType()).isEqualTo(DbType.POSTGRESQL);
        assertThat(result.connection()).isEqualTo(connection);
        verify(dataSourceConnector).verify(connection);
    }

    @Test
    void create_throws_when_connection_verify_fails() {
        PostgreSqlConnection connection = new PostgreSqlConnection("invalid-host", 5432, "mydb", "user", "password");
        willThrow(new CoreException(null, "데이터소스 연결에 실패했습니다."))
                .given(dataSourceConnector).verify(connection);

        assertThatThrownBy(() -> dataSourceApplicationService.create(DbType.POSTGRESQL, connection))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("데이터소스 연결에 실패했습니다.");
    }

    @Test
    void findById_returns_DataSourceResult() {
        DataSourceModel model = mock(DataSourceModel.class);
        given(model.getDbType()).willReturn(DbType.POSTGRESQL);
        given(dataSourceRepository.findById(1L)).willReturn(java.util.Optional.of(model));

        DataSourceResult result = dataSourceApplicationService.findById(1L);

        assertThat(result.dbType()).isEqualTo(DbType.POSTGRESQL);
    }

    @Test
    void findById_throws_when_not_found() {
        given(dataSourceRepository.findById(999L)).willReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> dataSourceApplicationService.findById(999L))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("존재하지 않는 데이터소스입니다.");
    }
}
