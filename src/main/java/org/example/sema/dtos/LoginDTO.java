package org.example.sema.dtos;

import lombok.Getter;

@Getter
public class LoginDTO {
    private String token;

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
