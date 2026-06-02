package koul.QueryLens.domain.conversation.tool;

import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;

import java.util.HashMap;
import java.util.Map;

public class ToolContext {

    private final Long userId;
    private final Long agentId;
    private final Long dataSourceId;
    private final String question;
    private final Map<String, Object> store = new HashMap<>();

    public ToolContext(Long userId, Long agentId, Long dataSourceId, String question) {
        this.userId = userId;
        this.agentId = agentId;
        this.dataSourceId = dataSourceId;
        this.question = question;
    }

    public Long getUserId() { return userId; }
    public Long getAgentId() { return agentId; }
    public Long getDataSourceId() { return dataSourceId; }
    public String getQuestion() { return question; }

    public void put(String key, Object value) {
        store.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = store.get(key);
        if (value == null) {
            return null;
        }
        if (!type.isInstance(value)) {
            throw new CoreException(ErrorType.INVALID_INPUT,
                    String.format("ToolContext key '%s'의 타입이 올바르지 않습니다. 기대: %s, 실제: %s",
                            key, type.getSimpleName(), value.getClass().getSimpleName()));
        }
        return type.cast(value);
    }

    public boolean has(String key) {
        return store.containsKey(key) && store.get(key) != null;
    }
}
