package com.example.carrental.payload;

import lombok.Data;

@Data
public class PaymentConfirmationPayload {

    private Long carId;
    private String username;
    private String paymentMode;
    private Double amount;
    private String dateOfPayment;
}
