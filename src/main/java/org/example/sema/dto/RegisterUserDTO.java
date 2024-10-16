package org.example.sema.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterUserDTO {

    @Schema(description = "Username for registration", example = "new_user123")
    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "Password for registration", example = "password123")
    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must have at least 8 characters")
    private String password;

    @Schema(description = "Email address of the user", example = "user@example.com")
    @NotBlank(message = "Email is required.")
    @Email(message = "Email should be valid", regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private String email;
}
