package com.example.carrental.payload;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
public class AddNewCarPayload {

    private String brand;
    private String modelName;
    private String color;
    private double price = -1.0;
    private String ownerEmail;
    private Boolean status = true;
    private Integer stocks = 1;
}
