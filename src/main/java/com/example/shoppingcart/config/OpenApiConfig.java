package com.example.shoppingcart.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI(
      @Value("${spring.application.name}") String appName,
      @Value("${server.port}") Integer port) {

    return new OpenAPI()
        .info(new Info()
            .title(appName.toUpperCase() + " API")
            .version("v1")
            .description("Documentación OpenAPI de " + appName))
        .servers(List.of(
            new Server().url("http://localhost:" + port).description("Local")
        ));
  }

  
  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("public")
        .packagesToScan("com.example.arkashoppingcart") 
        .build();
  }
}
