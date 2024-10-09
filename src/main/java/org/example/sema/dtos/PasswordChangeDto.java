package org.example.sema.dtos;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordChangeDto {
    private String username;
    private String oldPassword;
    private String newPassword;
}
