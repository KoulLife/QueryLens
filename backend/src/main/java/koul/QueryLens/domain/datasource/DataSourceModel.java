package koul.QueryLens.domain.datasource;

import jakarta.persistence.*;
import koul.QueryLens.domain.datasource.connection.DataSourceConnection;
import koul.QueryLens.domain.datasource.connection.DataSourceConnectionConverter;
import koul.QueryLens.domain.datasource.schema.DataSourceSchema;
import koul.QueryLens.domain.datasource.schema.DataSourceSchemaConverter;
import koul.QueryLens.support.BaseEntity;
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
}
