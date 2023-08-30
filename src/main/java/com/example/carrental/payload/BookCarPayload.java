package com.example.carrental.payload;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
public class BookCarPayload {
    private String pickupDate;
    private String dropOffDate;
    private String additionalServices;
}

