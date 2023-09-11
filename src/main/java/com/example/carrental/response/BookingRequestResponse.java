package com.example.carrental.response;

import com.example.carrental.model.Car;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BookingRequestResponse {

    private Long bookingId;
    private Car car;
    private String pickupDate;
    private String dropOffDate;
    private String modeOfPayment;
    private String customerName;
    private String customerEmail;
    private String additionalRequests;
}
