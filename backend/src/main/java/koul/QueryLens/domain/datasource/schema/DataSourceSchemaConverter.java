package koul.QueryLens.domain.datasource.schema;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Converter
public class DataSourceSchemaConverter implements AttributeConverter<DataSourceSchema, String> {

    private static final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Override
    public String convertToDatabaseColumn(DataSourceSchema schema) {
        if (schema == null) return null;
        try {
            return objectMapper.writeValueAsString(schema);
        } catch (Exception e) {
            throw new IllegalStateException("DataSourceSchema 직렬화 실패", e);
        }
    }

    @Override
    public DataSourceSchema convertToEntityAttribute(String json) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, DataSourceSchema.class);
        } catch (Exception e) {
            throw new IllegalStateException("DataSourceSchema 역직렬화 실패", e);
        }
    }
}
