package com.example.carrental.service;

import com.example.carrental.helper.CommonHelper;
import com.example.carrental.model.Car;
import com.example.carrental.model.CarBooking;
import com.example.carrental.model.ConfirmedBooking;
import com.example.carrental.model.Users;
import com.example.carrental.payload.PaymentConfirmationPayload;
import com.example.carrental.repository.BookingRepository;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.repository.ConfirmedBookingRepository;
import com.example.carrental.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;

@Service
public class PaymentService {

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

    @Transactional
    public boolean completePayment(PaymentConfirmationPayload payload) {
        // @TODO Check payment mode and continue, right now only doing COD only
        // Insert into confirmed_booking table and remove from CarBooking
        // Reduce stock of car by 1 in car table
        ConfirmedBooking confirmedBooking = new ConfirmedBooking();
        Car car = carRepository.findById(payload.getCarId()).orElse(null);
        Users user = userRepository.findByEmail(payload.getUsername()).orElse(null);
        if (car != null && user != null) {

            confirmedBooking.setCar(car)
                    .setUser(user)
                    .setAmount(payload.getAmount())
                    .setModeOfPayment(payload.getPaymentMode())
                    .setDateOfBooking(commonHelper.getDateInISO8601(payload.getDateOfPayment()));

            confirmedBookingRepository.save(confirmedBooking);
            // Delete from bookings
            bookingRepository.deleteByCarAndUser(car, user);
            // Reduce stock
            car.setStock(car.getStock() - 1);
            carRepository.save(car);
            return true;
        } else {
            System.out.println("unknown car or user during payment");
            return false;
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
