package com.example.carrental.controller;

import com.example.carrental.model.Car;
import com.example.carrental.payload.FilterCarsPayload;
import com.example.carrental.payload.GetCarsByRadiusPayload;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.repository.UserRepository;
import com.example.carrental.response.GetCarsByRadiusResponse;
import com.example.carrental.service.CarService;
import com.example.carrental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarService carService;

    @Autowired
    private UserService userService;

    @PostMapping("/all")
    public ResponseEntity<?> getAllCarsWithinRadius(@RequestHeader("Authorization") String token, @RequestBody FilterCarsPayload requestPayload) {
        try {
            if (userService.validateToken(token)) {
                if (requestPayload != null
                        && requestPayload.getLatitude() != 0
                        && requestPayload.getLongitude() != 0
                        && requestPayload.getRadius() != 0) {

                    List<Object[]> cars = carRepository.findCarsWithinDistance(
                            requestPayload.getLatitude(),
                            requestPayload.getLongitude(),
                            requestPayload.getRadius()
                    );
                    return new ResponseEntity<>(carService.getCarsByRadiusResponse(cars, requestPayload), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Invalid request payload - latitude, longitude and radius are mandatory", HttpStatus.NOT_ACCEPTABLE);
                }
            } else {
                return new ResponseEntity<>("Invalid token", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/brands")
    public ResponseEntity<?> getAllBrands(@RequestHeader("Authorization") String token) {
        try {
            if (userService.validateToken(token)) {
                return new ResponseEntity<>(carService.getCarBrands(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid token", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
