package com.example.carrental.response;

import com.example.carrental.model.Users;
import lombok.Data;

@Data
public class GetUserByIdResponse {

    private Long userId;
    private String email;
    private String name;
    private Integer age;
    private String city;
    private String state;
    private double latitude;
    private double longitude;

    public GetUserByIdResponse(Users user) {
        if (user != null) {
            userId = user.getUserId();
            email = user.getEmail();
            name = user.getName();
            age = user.getAge();
            city = user.getCity();
            state = user.getState();
            latitude = user.getLatitude();
            longitude = user.getLongitude();
        }
    }
}
