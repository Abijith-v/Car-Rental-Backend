package com.example.carrental.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String brand;
    private String modelName;
    private String color;
    private Boolean status = true;
    private Double price = -1.0;
    private Boolean booked = false;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "car_owner",
            joinColumns = @JoinColumn(name = "carId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ownerId", referencedColumnName = "userId")
    )
    private Users owner = new Users();
    private Integer stock = 1;
}
