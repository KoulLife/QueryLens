package koul.QueryLens.support.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("QueryLens API")
                        .description("자연어 질문을 SQL로 변환하는 Text-to-SQL AI 플랫폼")
                        .version("v1"));
    }
}
