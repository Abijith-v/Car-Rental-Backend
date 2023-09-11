package com.example.carrental.payload;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class PaymentConfirmationPayload {
    private Long carId;
    private String username;
    private String paymentMode;
    private Double amount;
    private String dateOfPayment;
    private String pickupDate;
    private String dropOffDate;
    private String additionalServices;
}
