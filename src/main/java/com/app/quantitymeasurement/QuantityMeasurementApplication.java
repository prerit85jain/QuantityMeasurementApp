package com.app.quantitymeasurement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * QuantityMeasurementApplication – UC18 entry point.
 *
 * @SecurityScheme tells Swagger UI to show an "Authorize" button
 * where the user can paste their JWT token (without "Bearer " prefix).
 * Swagger then sends: Authorization: Bearer <token> on every request.
 *
 * Run:    mvn spring-boot:run
 * Swagger: http://localhost:8080/swagger-ui.html
 *   → POST /auth/register to create a user
 *   → Click "Authorize" and paste the returned token
 *   → All protected endpoints are now callable
 */
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title       = "Quantity Measurement REST API",
        version     = "18.0",
        description = "UC18: Spring Security with JWT Authentication and OAuth2 Social Login. " +
                      "Register at POST /auth/register, then click Authorize and paste your token."
    )
)
@SecurityScheme(
    name   = "bearerAuth",              // matches @SecurityRequirement(name="bearerAuth")
    type   = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description  = "Paste your JWT token here (without 'Bearer ' prefix). " +
                   "Obtain it from POST /auth/register or POST /auth/login."
)
public class QuantityMeasurementApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuantityMeasurementApplication.class, args);
    }
}
