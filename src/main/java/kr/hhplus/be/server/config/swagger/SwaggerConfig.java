package kr.hhplus.be.server.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI swaggerApi() {

        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Local server");

        return new OpenAPI().servers(List.of(server));
    }
}

