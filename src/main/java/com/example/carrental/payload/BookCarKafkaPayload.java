package com.example.carrental.payload;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class BookCarKafkaPayload {
    private String userEmail;
    private Long carId;
    private Date pickupDate;
    private Date dropOffDate;
    private String additionalServices;
}
