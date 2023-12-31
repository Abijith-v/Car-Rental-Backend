package com.example.carrental.response;

import com.example.carrental.model.Car;
import com.example.carrental.model.Users;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetCarsByRadiusResponse {

    private Long carId;
    private String brand;
    private String color;
    private String modelName;
    private double price;
    private Long userId;
    private String ownerEmail;
    private String ownerName;
}
