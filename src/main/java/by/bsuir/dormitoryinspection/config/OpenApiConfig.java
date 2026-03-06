package by.bsuir.dormitoryinspection.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

  @Bean
  public OpenAPI openApi() {
    return new OpenAPI()
            .info(new Info()
                    .title("Dormitory Inspection API")
                    .description("API для системы обходов общежития")
                    .version("1.0.0"));
  }
}
