package org.example.sema.config.swagger;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:2024");

        Contact myContact = new Contact();
        myContact.setName("Lucie Scholzová");

        Info information = new Info()
                .title("NNPDA SEM A")
                .version("1.0")
                .description("This API exposes endpoints for NNPDA SEM A.")
                .contact(myContact);
        return new OpenAPI().info(information).servers(List.of(server));
    }
}
