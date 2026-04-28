package ru.blps.googleplay.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI googlePlayOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Google Play Process API")
                .version("1.0.0")
                .description("REST API для бизнес-процесса выбора, покупки, установки приложения и управления платежными данными."));
    }
}
