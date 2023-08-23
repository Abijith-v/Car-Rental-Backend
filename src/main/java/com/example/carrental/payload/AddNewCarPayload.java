package com.example.carrental.payload;

import lombok.Data;

@Data
public class AddNewCarPayload {

    private String brand;
    private String modelName;
    private String color;
    private double price = -1.0;
    private String ownerEmail;
}