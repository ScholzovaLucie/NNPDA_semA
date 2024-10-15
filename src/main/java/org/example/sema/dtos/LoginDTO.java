package org.example.sema.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginDTO {
    @Schema(description = "Authentication token", example = "eyJhbGciOiJIUzI1NiIsIn...")
    private String token;

    @Schema(description = "Token expiration time in milliseconds", example = "3600000")
    private long expiresIn;

    public LoginDTO setToken(String token) {
        this.token = token;
        return this;
    }

    public LoginDTO setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }
}
