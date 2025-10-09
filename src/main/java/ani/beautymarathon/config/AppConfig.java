package ani.beautymarathon.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class AppConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addExamples("BadRequestExample", new Example()
                                .value(Map.of(
                                        "status", "BAD_REQUEST",
                                        "message", "Invalid input data"
                                ))
                        )
                );
    }


}
