package com.example.carrental.payload;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentConfirmationKafkaPayload {

    private Long id;
    private Long carId;
    private Long userId;
}
