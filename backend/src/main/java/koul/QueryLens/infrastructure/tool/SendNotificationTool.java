package koul.QueryLens.infrastructure.tool;

import koul.QueryLens.domain.agent.AgentToolType;
import koul.QueryLens.domain.conversation.tool.Tool;
import koul.QueryLens.domain.conversation.tool.ToolContext;
import koul.QueryLens.domain.conversation.tool.ToolContextKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendNotificationTool implements Tool {

    @Override
    public AgentToolType type() {
        return AgentToolType.SEND_NOTIFICATION;
    }

    @Override
    public void execute(ToolContext context) {
        String result = context.get(ToolContextKeys.RESULT, String.class);
        // TODO: 알림 채널 구현 (Slack, Email 등)
        log.info("[SendNotificationTool] 알림 발송 - userId: {}, result: {}", context.getUserId(), result);
    }
}
