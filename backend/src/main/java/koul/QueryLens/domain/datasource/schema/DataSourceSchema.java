package koul.QueryLens.domain.datasource.schema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceSchema {

    private List<SchemaObject> objects;
    private LocalDateTime syncedAt;
}
