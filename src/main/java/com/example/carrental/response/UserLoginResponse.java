package com.example.carrental.response;

import lombok.Data;

@Data
public class UserLoginResponse {
    String message;
    String token;

    public UserLoginResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }
}
