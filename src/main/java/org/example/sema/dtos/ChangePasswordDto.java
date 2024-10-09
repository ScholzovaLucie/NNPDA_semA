package org.example.sema.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangePasswordDto {
    private String token;
    private String newPassword;
}
