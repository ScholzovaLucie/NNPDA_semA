package org.example.sema.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterUserDto {

    private String username;

    private String password;

    private String email;

}
