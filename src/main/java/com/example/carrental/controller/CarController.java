package com.example.carrental.controller;

import com.example.carrental.model.Car;
import com.example.carrental.payload.GetCarsByRadiusPayload;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.repository.UserRepository;
import com.example.carrental.response.GetCarsByRadiusResponse;
import com.example.carrental.service.CarService;
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

    @PostMapping("/all")
    public ResponseEntity<?> getAllCarsWithinRadius(@RequestBody GetCarsByRadiusPayload requestPayload) {
        try {
            if (requestPayload != null
                    && requestPayload.getLatitude() != 0
                    && requestPayload.getLongitude() != 0
                    && requestPayload.getRadius() != 0) {

                List<Object[]> cars = carRepository.findCarsWithinDistance(
                        requestPayload.getLatitude(),
                        requestPayload.getLongitude(),
                        requestPayload.getRadius()
                );
                return new ResponseEntity<>(carService.getCarsByRadiusResponse(cars), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid request payload", HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
