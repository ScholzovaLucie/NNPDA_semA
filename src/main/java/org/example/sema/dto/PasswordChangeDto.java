package org.example.sema.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PasswordChangeDto {

    @Schema(description = "User's username", example = "test_username")
    @NotBlank(message = "Username is required.")
    private String username;

    @Schema(description = "Old password for verification", example = "old_password123")
    @NotBlank(message = "Old password is required.")
    private String oldPassword;

    @Schema(description = "New password to be set", example = "new_password456")
    @NotBlank(message = "New password is required.")
    private String newPassword;
}
