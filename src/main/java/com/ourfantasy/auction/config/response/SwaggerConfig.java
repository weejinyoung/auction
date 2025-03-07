package com.ourfantasy.auction.config.response;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Auction API 문서")
                .version("v.0.1.0")
                .description("API 명세서");

        return new OpenAPI()
                .info(info)
                .components(new Components());
    }
}