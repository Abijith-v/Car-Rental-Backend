package com.example.carrental.payload;

import lombok.Data;

@Data
public class UpdateUserPayload {

    // Email used to identify user, not to update email itself
    private String email;
    private String name;
    private Integer age;
    private String city;
    private String state;
    private double latitude;
    private double longitude;
}
