package com.example.carrental.payload;

import lombok.Data;

@Data
public class FilterCarsPayload {

    private double latitude;
    private double longitude;
    private double radius;
    private String brand;
    private String name;
    private String color;
    private Double minPrice;
    private Double maxPrice;
}
