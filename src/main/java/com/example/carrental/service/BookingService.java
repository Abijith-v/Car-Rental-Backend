package com.example.carrental.service;

import com.example.carrental.helper.CommonHelper;
import com.example.carrental.model.CarBooking;
import com.example.carrental.model.ConfirmedBooking;
import com.example.carrental.model.Users;
import com.example.carrental.payload.BookCarKafkaPayload;
import com.example.carrental.payload.BookCarPayload;
import com.example.carrental.payload.PaymentConfirmationPayload;
import com.example.carrental.repository.BookingRepository;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.repository.ConfirmedBookingRepository;
import com.example.carrental.repository.UserRepository;
import com.example.carrental.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BookingService {

    public final static String BOOKING_STATUS_REQUESTED = "REQUESTED";

    public final static String BOOKING_STATUS_PAYMENT_PENDING = "PAYMENT_PENDING";

    public final static String BOOKING_STATUS_ACCEPTED = "ACCEPTED";

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private CommonHelper commonHelper;

    @Autowired
    private ConfirmedBookingRepository confirmedBookingRepository;

    @Transactional
    public CarBookingResponse bookCar(BookCarPayload bookCarPayload, Long id, String token) throws Exception {
        try {
            if (carRepository.existsById(id)
                && isCarAvailableToBook(id)) {
                Date pickupDate = commonHelper.getDateInISO8601(bookCarPayload.getPickupDate());
                Date dropOffDate = commonHelper.getDateInISO8601(bookCarPayload.getDropOffDate());

                BookCarKafkaPayload bookingPayload = new BookCarKafkaPayload();
                bookingPayload.setUserEmail(userService.getEmailFromToken(token))
                        .setCarId(id)
                        .setPickupDate(pickupDate)
                        .setDropOffDate(dropOffDate)
                        .setAdditionalServices(bookCarPayload.getAdditionalServices())
                        .setStatus("REQUESTED");

                ObjectMapper objectMapper = new ObjectMapper();
                String bookingJson = objectMapper.writeValueAsString(bookingPayload);

                ProducerRecord<String, String> record = new ProducerRecord<>("car-booking-topic", bookingJson);
                SendResult<String, String> result = kafkaTemplate.send(record).get();
                return new CarBookingResponse()
                        .setMessage("Booking request received")
                        .setMessageId(result.getRecordMetadata().offset()); // Message ID
            } else {
                throw new Exception("Car is already booked / Not available right now");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // Spring will roll back the transaction with @Transactional
            throw e;
        }
    }

    @Transactional
    private boolean isCarAvailableToBook(Long id) {
        CarBooking carBooking = bookingRepository.findByCarId(id).orElse(null);
        return carBooking == null;
    }

    public ResponseEntity<?> getBookingStatus(String username) {
        Users user = userRepository.findByEmail(username).orElse(null);
        if (user != null) {
            List<CarBooking> bookings = bookingRepository.findAllByUser(user);
            List<BookingStatusResponse> bookingStatusResponses = new ArrayList<>();
            for (CarBooking booking : bookings) {
                bookingStatusResponses.add(new BookingStatusResponse(
                    booking.getUser().getEmail(),
                    booking.getCar().getId(),
                    "TEST"
//                    booking.getStatus()
                ));
            }
            return new ResponseEntity<>(bookingStatusResponses, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CommonResponse("Invalid user"), HttpStatus.FORBIDDEN);
        }
    }

    public List<BookingRequestResponse> getBookingRequests(String token) throws Exception {
        String username = userService.getEmailFromToken(token);
        Users user = userRepository.findByEmail(username).orElse(null);
        if (user != null) {
            List<BookingRequestResponse> requestResponses = new ArrayList<>();
            List<ConfirmedBooking> confirmedBookings = confirmedBookingRepository.findAllByOwnerId(user.getUserId());
            for (ConfirmedBooking confirmedBooking : confirmedBookings) {
                BookingRequestResponse response = new BookingRequestResponse();
                requestResponses.add(
                        response.setBookingId(confirmedBooking.getId())
                        .setCar(confirmedBooking.getCar())
                        .setModeOfPayment(confirmedBooking.getModeOfPayment())
                        .setCustomerEmail(confirmedBooking.getUser().getEmail())
                        .setCustomerName(confirmedBooking.getUser().getName())
                        .setPickupDate(confirmedBooking.getPickupDate().toString())
                        .setDropOffDate(confirmedBooking.getDropOffDate().toString())
                        .setAdditionalRequests(confirmedBooking.getAdditionalServices())
                );
            }
            return requestResponses;
        } else {
            throw new Exception("Invalid user ID");
        }
    }
}
