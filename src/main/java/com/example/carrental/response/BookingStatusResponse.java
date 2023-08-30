package com.example.carrental.response;

import lombok.Data;

@Data
public class BookingStatusResponse {
    private String username;
    private Long carId;
    private String status;

    public BookingStatusResponse(
        String username,
        Long carId,
        String status
    ) {
        this.username = username;
        this.carId = carId;
        this.status = status;
    }
}
