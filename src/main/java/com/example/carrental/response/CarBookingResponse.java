package com.example.carrental.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CarBookingResponse {

    private String message;
    private Long messageId;
}
