package com.homefit.backend.global.util;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HomeFit API Document")
                        .description("HomeFit API 명세서")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Kamil Lee")
                                .email("parousia0918@gmail.com")
                        )
                        .termsOfService("https://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"))
                )
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                // API 가 배포된 서버들을 정의함
                .servers(List.of(
                        new Server().url("https://localhost:8080").description("Local Server"), // 로컬 서버
                        new Server().url("https://localhost:8081").description("Local Server"), // 로컬 서버
                        new Server().url("https://125.132.216.190:12500").description("Dev-01 Server"), // 개발 서버(맥)
                        new Server().url("https://125.132.216.190:12502").description("Dev-02 Server"), // 개발 서버(MSI)
                        new Server().url("https://homefit.my").description("Production Server") // 프로덕션 서버
                ));
    }
}
