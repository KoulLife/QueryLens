package koul.QueryLens.domain.datasource.connection;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.databind.ObjectMapper;

@Converter
public class DataSourceConnectionConverter implements AttributeConverter<DataSourceConnection, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(DataSourceConnection connection) {
        if (connection == null) return null;
        try {
            return objectMapper.writeValueAsString(connection);
        } catch (Exception e) {
            throw new IllegalStateException("DataSourceConnection 직렬화 실패", e);
        }
    }

    @Override
    public DataSourceConnection convertToEntityAttribute(String json) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, DataSourceConnection.class);
        } catch (Exception e) {
            throw new IllegalStateException("DataSourceConnection 역직렬화 실패", e);
        }
    }
}
