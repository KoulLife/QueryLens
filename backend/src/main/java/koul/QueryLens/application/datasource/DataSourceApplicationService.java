package koul.QueryLens.application.datasource;

import koul.QueryLens.domain.datasource.DataSourceConnector;
import koul.QueryLens.domain.datasource.DataSourceModel;
import koul.QueryLens.domain.datasource.DataSourceRepository;
import koul.QueryLens.domain.datasource.DataSourceSchemaReader;
import koul.QueryLens.domain.datasource.DbType;
import koul.QueryLens.domain.datasource.connection.DataSourceConnection;
import koul.QueryLens.domain.datasource.schema.DataSourceSchema;
import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DataSourceApplicationService {

    private final DataSourceRepository dataSourceRepository;
    private final DataSourceConnector dataSourceConnector;
    private final DataSourceSchemaReader dataSourceSchemaReader;

    public DataSourceResult create(DbType dbType, DataSourceConnection connection) {
        dataSourceConnector.verify(connection);
        DataSourceModel model = dataSourceRepository.save(DataSourceModel.create(dbType, connection));
        return DataSourceResult.from(model);
    }

    @Transactional(readOnly = true)
    public DataSourceResult findById(Long id) {
        DataSourceModel model = dataSourceRepository.findById(id)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 데이터소스입니다."));
        return DataSourceResult.from(model);
    }

    public DataSourceResult syncSchema(Long id) {
        DataSourceModel model = dataSourceRepository.findById(id)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 데이터소스입니다."));
        DataSourceSchema schema = dataSourceSchemaReader.read(model.getConnection());
        model.updateSchema(schema);
        return DataSourceResult.from(model);
    }
}
