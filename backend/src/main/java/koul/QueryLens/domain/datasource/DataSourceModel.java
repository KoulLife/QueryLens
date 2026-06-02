package koul.QueryLens.domain.datasource;

import jakarta.persistence.*;
import koul.QueryLens.domain.datasource.connection.DataSourceConnection;
import koul.QueryLens.domain.datasource.connection.DataSourceConnectionConverter;
import koul.QueryLens.domain.datasource.schema.DataSourceSchema;
import koul.QueryLens.domain.datasource.schema.DataSourceSchemaConverter;
import koul.QueryLens.support.BaseEntity;
import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "data_sources")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataSourceModel extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DbType dbType;

    @Convert(converter = DataSourceConnectionConverter.class)
    @Column(nullable = false, columnDefinition = "text")
    private DataSourceConnection connection;

    @Convert(converter = DataSourceSchemaConverter.class)
    @Column(columnDefinition = "text")
    private DataSourceSchema schema;

    public static DataSourceModel create(DbType dbType, DataSourceConnection connection) {
        if (dbType == null) {
            throw new CoreException(ErrorType.INVALID_INPUT, "DB 타입이 존재하지 않습니다.");
        }
        if (connection == null) {
            throw new CoreException(ErrorType.INVALID_INPUT, "연결 정보가 존재하지 않습니다.");
        }
        if (!connection.supports(dbType)) {
            throw new CoreException(ErrorType.INVALID_INPUT, "연결 정보가 DB 타입과 일치하지 않습니다.");
        }
        DataSourceModel model = new DataSourceModel();
        model.dbType = dbType;
        model.connection = connection;
        return model;
    }

    public void updateSchema(DataSourceSchema schema) {
        this.schema = schema;
    }
}
