package com.example.carrental.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Entity
@Data
@Accessors(chain = true)
public class ConfirmedBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    private Double amount;
    private String modeOfPayment;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfBooking;

    @Temporal(TemporalType.TIMESTAMP)
    private Date pickupDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dropOffDate;

    private String additionalServices;
    private String status;
}
