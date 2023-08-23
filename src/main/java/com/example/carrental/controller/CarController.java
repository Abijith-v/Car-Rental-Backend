package com.example.carrental.controller;
import com.example.carrental.model.Car;
import com.example.carrental.payload.AddNewCarPayload;
import com.example.carrental.payload.FilterCarsPayload;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.response.CommonResponse;
import com.example.carrental.service.CarService;
import com.example.carrental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                    return new ResponseEntity<>("invalid request payload - latitude, longitude and radius are mandatory", HttpStatus.NOT_ACCEPTABLE);
                }
            } else {
                return new ResponseEntity<>("invalid token", HttpStatus.FORBIDDEN);
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
                return new ResponseEntity<>("invalid token", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewCar(@RequestHeader("Authorization") String token, @RequestBody AddNewCarPayload carPayload) {
        if (carPayload.getBrand() == null
            || carPayload.getOwnerEmail() == null
            || carPayload.getColor() == null
            || carPayload.getModelName() == null
            || carPayload.getPrice() == -1.0
            || !userService.validateToken(token)) {
            return new ResponseEntity<>("invalid request payload or access token", HttpStatus.NOT_ACCEPTABLE);
        }

        return carService.addNewCar(carPayload);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCar(@RequestHeader("Authorization") String token, @PathVariable Long id) {

        Optional<Car> optionalCar = carRepository.findById(id);
        Car car = optionalCar.orElse(null);
        if (car != null) {
            if (userService.isAdminToken(token) || userService.getEmailFromToken(token).equals(car.getOwner().getEmail())) {
                car.setOwner(null);
                carRepository.deleteById(id);
                return new ResponseEntity<>(new CommonResponse("success"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new CommonResponse("action is unauthorized for this user"), HttpStatus.NOT_ACCEPTABLE);
            }
        }

        return new ResponseEntity<>(new CommonResponse("car id is invalid"), HttpStatus.NOT_ACCEPTABLE);
    }
}
