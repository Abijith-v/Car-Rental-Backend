package com.example.carrental.payload;

import lombok.Data;

@Data
public class GetCarsByRadiusPayload {

    private double latitude;
    private double longitude;
    private double radius;
}
