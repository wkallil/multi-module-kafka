package wkallil.microservice.orderService.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8084}")
    private String serverPort;

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:" + serverPort);
        localServer.setDescription("Local Development Server for Order Service");

        Server dockerServer = new Server();
        dockerServer.setUrl("http://localhost:8082");
        dockerServer.setDescription("Docker Server for Order Service");

        Info info = new Info()
                .title("Order Service API")
                .version("1.0.0")
                .description("REST API for Order Management in Microservices Architecture");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, dockerServer));
    }
}
