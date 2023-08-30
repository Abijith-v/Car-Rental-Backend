package com.example.carrental.service;

import com.example.carrental.model.CarBooking;
import com.example.carrental.model.Users;
import com.example.carrental.payload.BookCarKafkaPayload;
import com.example.carrental.payload.BookCarPayload;
import com.example.carrental.repository.BookingRepository;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.repository.UserRepository;
import com.example.carrental.response.BookingStatusResponse;
import com.example.carrental.response.CarBookingResponse;
import com.example.carrental.response.CommonResponse;
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

    private static final String[] BOOKING_STATUS = new String[] {
        "REQUESTED"
    };

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

    SimpleDateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Transactional
    public CarBookingResponse bookCar(BookCarPayload bookCarPayload, Long id, String token) throws Exception {
        try {
            if (carRepository.existsById(id)) {
                Date pickupDate = iso8601DateFormat.parse(bookCarPayload.getPickupDate());
                Date dropOffDate = iso8601DateFormat.parse(bookCarPayload.getDropOffDate());

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
                throw new Exception("User or Car does not exist or is not available");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // Spring will roll back the transaction with @Transactional
            throw e;
        }
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
                    booking.getStatus()
                ));
            }
            return new ResponseEntity<>(bookingStatusResponses, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CommonResponse("Invalid user"), HttpStatus.FORBIDDEN);
        }
    }
}
