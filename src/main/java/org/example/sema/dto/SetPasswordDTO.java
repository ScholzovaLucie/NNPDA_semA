package org.example.sema.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SetPasswordDTO {
    @Schema(description = "Token for reset password", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50IiwiaWF0IjoxNzI5MTQ3Mzg4LCJleHAiOjE3MjkxNTA5ODh9.PT5RGtgfPq-vEbibUHJymGEQvgJc89JNmX3DhuehE6E")
    @NotBlank(message = "Username is required.")
    private String token;

    @Schema(description = "New password", example = "password123")
    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must have at least 8 characters")
    private String password;

}
