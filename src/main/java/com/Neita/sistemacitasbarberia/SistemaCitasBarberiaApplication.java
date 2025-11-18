package com.Neita.sistemacitasbarberia;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Neita's Barber Shop API",
        version = "1.0.0",
        description = "Sistema completo de gestión de citas para barbería con arquitectura profesional",
        contact = @Contact(
            name = "David Neita",
            email = "david.neita@sena.edu.co"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8088", description = "Servidor de desarrollo")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "Autenticación JWT mediante Bearer Token",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class SistemaCitasBarberiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaCitasBarberiaApplication.class, args);
    }
}
