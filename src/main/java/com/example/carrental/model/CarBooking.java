package com.example.carrental.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Accessors(chain = true)
@Data
@Entity
@Table (
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "car_id"})
)
public class CarBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @Temporal(TemporalType.TIMESTAMP)
    private Date pickupDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dropOffDate;

    private String additionalServices;
}
