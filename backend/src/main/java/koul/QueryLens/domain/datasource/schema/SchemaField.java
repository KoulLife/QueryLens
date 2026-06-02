package koul.QueryLens.domain.datasource.schema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SchemaField {

    private String name;
    private String dataType;
    private Boolean nullable;
    private String description;
}
