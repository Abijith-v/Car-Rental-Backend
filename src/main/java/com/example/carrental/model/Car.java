package com.example.carrental.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String brand;
    private String modelName;
    private String color;
    private double price;
    private boolean status;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "car_owner",
            joinColumns = @JoinColumn(name = "carId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ownerId", referencedColumnName = "userId")
    )
    private Users owner = new Users();
}
