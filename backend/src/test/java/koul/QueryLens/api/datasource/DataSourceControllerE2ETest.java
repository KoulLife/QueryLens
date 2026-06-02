package koul.QueryLens.api.datasource;

import koul.QueryLens.domain.datasource.DataSourceConnector;
import koul.QueryLens.domain.datasource.DataSourceRepository;
import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DataSourceControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @MockitoBean
    private DataSourceConnector dataSourceConnector;

    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @BeforeEach
    void setUp() {
        dataSourceRepository.deleteAll();
    }

    @Test
    void create_success() throws Exception {
        willDoNothing().given(dataSourceConnector).verify(any());

        String request = """
                {
                  "dbType": "POSTGRESQL",
                  "connection": {
                    "type": "POSTGRESQL",
                    "host": "localhost",
                    "port": 5432,
                    "databaseName": "testdb",
                    "username": "user",
                    "encryptedPassword": "password"
                  }
                }
                """;

        mockMvc.perform(post("/api/data-sources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.dbType").value("POSTGRESQL"));
    }

    @Test
    void create_fails_when_connection_verify_fails() throws Exception {
        willThrow(new CoreException(ErrorType.DATA_SOURCE_CONNECTION_FAILED, "데이터소스 연결에 실패했습니다."))
                .given(dataSourceConnector).verify(any());

        String request = """
                {
                  "dbType": "POSTGRESQL",
                  "connection": {
                    "type": "POSTGRESQL",
                    "host": "invalid-host",
                    "port": 5432,
                    "databaseName": "testdb",
                    "username": "user",
                    "encryptedPassword": "wrong"
                  }
                }
                """;

        mockMvc.perform(post("/api/data-sources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.result").value("FAIL"))
                .andExpect(jsonPath("$.meta.message").value("데이터소스 연결에 실패했습니다."));
    }

    @Test
    void create_fails_when_dbType_is_missing() throws Exception {
        String request = """
                {
                  "connection": {
                    "type": "POSTGRESQL",
                    "host": "localhost",
                    "port": 5432,
                    "databaseName": "testdb",
                    "username": "user",
                    "encryptedPassword": "password"
                  }
                }
                """;

        mockMvc.perform(post("/api/data-sources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.result").value("FAIL"));
    }

    @Test
    void findById_fails_when_not_found() throws Exception {
        mockMvc.perform(get("/api/data-sources/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.meta.result").value("FAIL"))
                .andExpect(jsonPath("$.meta.message").value("존재하지 않는 데이터소스입니다."));
    }
}
