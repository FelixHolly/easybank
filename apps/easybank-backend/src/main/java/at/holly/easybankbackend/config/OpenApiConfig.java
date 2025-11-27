package at.holly.easybankbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI Configuration
 * Configures Swagger UI and API documentation
 * Available at: /swagger-ui/index.html and /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

  @Value("${api.server.url}")
  private String serverUrl;

  @Value("${api.server.description}")
  private String serverDescription;

  @Bean
  public OpenAPI easyBankOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("EasyBank API")
            .description("RESTful API for EasyBank - A modern banking application with Keycloak authentication")
            .version("v1.0")
            .contact(new Contact()
                .name("EasyBank Support")
                .email("support@easybank.com"))
            .license(new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0")))
        .servers(List.of(
            new Server()
                .url(serverUrl)
                .description(serverDescription)))
        .components(new Components()
            .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT token from Keycloak")));
  }
}
