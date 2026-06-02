package koul.QueryLens.domain.datasource.schema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SchemaObject {

    private String name;
    private String qualifiedName;
    private SchemaObjectType type;
    private String description;
    private List<SchemaField> fields;
}
