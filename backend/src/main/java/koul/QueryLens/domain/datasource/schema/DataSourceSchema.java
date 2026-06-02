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

    public String toDdl() {
        if (objects == null || objects.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (SchemaObject obj : objects) {
            String keyword = obj.getType() == SchemaObjectType.VIEW ? "CREATE VIEW" : "CREATE TABLE";
            sb.append(keyword).append(" ").append(obj.getQualifiedName()).append(" (\n");

            List<SchemaField> fields = obj.getFields();
            if (fields != null) {
                for (int i = 0; i < fields.size(); i++) {
                    SchemaField field = fields.get(i);
                    sb.append("  ").append(field.getName())
                            .append(" ").append(field.getDataType());
                    if (Boolean.FALSE.equals(field.getNullable())) {
                        sb.append(" NOT NULL");
                    }
                    if (i < fields.size() - 1) {
                        sb.append(",");
                    }
                    sb.append("\n");
                }
            }

            sb.append(");\n\n");
        }
        return sb.toString().trim();
    }
}
