package com.example.carrental.service;

import com.example.carrental.helper.CommonHelper;
import com.example.carrental.model.Car;
import com.example.carrental.model.CarBooking;
import com.example.carrental.model.ConfirmedBooking;
import com.example.carrental.model.Users;
import com.example.carrental.payload.PaymentConfirmationKafkaPayload;
import com.example.carrental.payload.PaymentConfirmationPayload;
import com.example.carrental.repository.BookingRepository;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.repository.ConfirmedBookingRepository;
import com.example.carrental.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLDataException;

import static com.example.carrental.service.UserService.VALIDATE_TOKEN_API_ENDPOINT;

@Service
public class PaymentService {

    private static final String PAYMENT_CONFIRMED_SSE_STATUS_ENDPOINT = "http://localhost:8082/qconsumer/sse/payment/confirmation";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ConfirmedBookingRepository confirmedBookingRepository;

    @Autowired
    private CommonHelper commonHelper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Transactional
    public boolean completePayment(PaymentConfirmationPayload payload, String token) {
        // @TODO Check payment mode and continue, right now only doing COD only
        // Insert into confirmed_booking table and remove from CarBooking
        // Reduce stock of car by 1 in car table
        ConfirmedBooking confirmedBooking = new ConfirmedBooking();
        Car car = carRepository.findById(payload.getCarId()).orElse(null);
        Users user = userRepository.findByEmail(userService.getEmailFromToken(token)).orElse(null);
        if (car != null && user != null) {

            confirmedBooking.setCar(car)
                    .setUser(user)
                    .setAmount(payload.getAmount())
                    .setModeOfPayment(payload.getPaymentMode())
                    .setDateOfBooking(commonHelper.getDateInISO8601(payload.getDateOfPayment()))
                    .setAdditionalServices(payload.getAdditionalServices())
                    .setPickupDate(commonHelper.getDateInISO8601(payload.getPickupDate()))
                    .setDropOffDate(commonHelper.getDateInISO8601(payload.getDropOffDate()));

            ConfirmedBooking newBooking = confirmedBookingRepository.save(confirmedBooking);
            // Delete from bookings
            bookingRepository.deleteByCarAndUser(car, user);
            // Reduce stock
            car.setStock(car.getStock() - 1);
            carRepository.save(car);
            sendMessageToClientsViaSSE(newBooking, token);
            return true;
        } else {
            System.out.println("unknown car or user during payment");
            return false;
        }
    }

    private void sendMessageToClientsViaSSE(ConfirmedBooking newBooking, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            PaymentConfirmationKafkaPayload carBookingConfirmation = new PaymentConfirmationKafkaPayload();
            carBookingConfirmation.setCarId(newBooking.getCar().getId())
                    .setUserId(newBooking.getUser().getUserId())
                    .setId(newBooking.getId());

            HttpEntity<PaymentConfirmationKafkaPayload> requestEntity = new HttpEntity<>(carBookingConfirmation, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    PAYMENT_CONFIRMED_SSE_STATUS_ENDPOINT,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                System.out.println("500 code while calling " + PAYMENT_CONFIRMED_SSE_STATUS_ENDPOINT);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void completeUpiPayment() {
        // @TODO
    }

    public void completeDebitCardPayment() {
        // @TODO
    }

    public void completeCreditCardPayment() {
        // @TODO
    }
}
