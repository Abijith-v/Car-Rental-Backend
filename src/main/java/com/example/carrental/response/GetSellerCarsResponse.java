package com.example.carrental.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetSellerCarsResponse {

    private Long carId;
    private String brand;
    private String color;
    private String modelName;
    private Double price;
    private Boolean status;
}
