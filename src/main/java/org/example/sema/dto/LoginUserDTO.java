package org.example.sema.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginUserDTO {

    @Schema(description = "Username for login", example = "user_login")
    @NotBlank(message = "Username is required.")
    private String username;

    @Schema(description = "Password for login", example = "password123")
    @NotBlank(message = "Password is required.")
    private String password;
}
