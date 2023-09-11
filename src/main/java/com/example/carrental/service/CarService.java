package com.example.carrental.service;

import com.example.carrental.model.Car;
import com.example.carrental.model.Users;
import com.example.carrental.payload.AddNewCarPayload;
import com.example.carrental.payload.FilterCarsPayload;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.repository.UserRepository;
import com.example.carrental.response.CommonResponse;
import com.example.carrental.response.GetCarsByRadiusResponse;
import com.example.carrental.response.GetSellerCarsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public List<GetCarsByRadiusResponse> getCarsByRadiusResponse(List<Object[]> cars, FilterCarsPayload filters) {

        List<GetCarsByRadiusResponse> responseList = new ArrayList<>();
        try {
            for (Object[] car : cars) {
                Pattern filterName = Pattern.compile(getPatternFromName(filters.getName()));
                Matcher modelNameInDb = filterName.matcher(car[3].toString().trim());
                if ((filters.getBrand() == null || filters.getBrand().equals(car[1]))
                    && (filters.getColor() == null || filters.getColor().equals(car[2]))
                    && (filters.getMinPrice() == null || (double)car[4] >= filters.getMinPrice())
                    && (filters.getMaxPrice() == null || (double)car[4] <= filters.getMaxPrice())
                    && modelNameInDb.find()
                ) {
                    GetCarsByRadiusResponse response = new GetCarsByRadiusResponse();
                    response.setCarId((Long) car[0]);
                    response.setBrand((String) car[1]);
                    response.setColor((String) car[2]);
                    response.setModelName((String) car[3]);
                    response.setPrice((double) car[4]);
                    response.setUserId((Long) car[5]);
                    response.setOwnerEmail((String) car[6]);
                    response.setOwnerName((String) car[7]);
                    responseList.add(response);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return responseList;
    }

    private String getPatternFromName(String name) {
        if (name == null) {
            return "";
        }
        StringBuilder pattern = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            pattern.append(".*").append(name.charAt(i));
        }
        return pattern.toString();
    }

    public List<String> getCarBrands() {
        return carRepository.findDistinctBrands();
    }

    public ResponseEntity<?> addNewCar(AddNewCarPayload carPayload) {

        Optional<Users> optionalUser = userRepository.findByEmail(carPayload.getOwnerEmail());
        Users user = optionalUser.orElse(null);

        if (user != null) {
            Car car = new Car();
            car.setModelName(carPayload.getModelName());
            car.setBrand(carPayload.getBrand());
            car.setOwner(user);
            car.setColor(carPayload.getColor());
            car.setPrice(carPayload.getPrice());
            carRepository.save(car);
            return new ResponseEntity<>(new CommonResponse("success"), HttpStatus.OK);
        }

        return new ResponseEntity<>(new CommonResponse("User not found with the given username"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public boolean updateCar(Long id, AddNewCarPayload payload) {
        Car car = carRepository.findById(id).orElse(null);
        if (car != null) {
            if (payload.getStatus() != null) {
                car.setStatus(payload.getStatus());
            }
            if (payload.getBrand() != null) {
                car.setBrand(payload.getBrand());
            }
            if (payload.getColor() != null) {
                car.setColor(payload.getColor());
            }
            if (payload.getModelName() != null) {
                car.setModelName(payload.getModelName());
            }
            if (payload.getPrice() != 0.0) {
                car.setPrice(payload.getPrice());
            }
            car.setStock(payload.getStocks());
            carRepository.save(car);
            return true;
        }

        return false;
    }

    public List<GetSellerCarsResponse> getSellerCarsByUsername(String token) throws Exception {
        try {
            String username = userService.getEmailFromToken(token);
            Users user = userRepository.findByEmail(username).orElse(null);
            if (user != null) {
                List<Car> cars = carRepository.findAllByOwner(user);
                List<GetSellerCarsResponse> carsResponse = new ArrayList<>();
                for (Car car : cars) {
                    GetSellerCarsResponse response = new GetSellerCarsResponse();
                    carsResponse.add(response.setCarId(car.getId())
                            .setPrice(car.getPrice())
                            .setBrand(car.getBrand())
                            .setColor(car.getColor())
                            .setModelName(car.getModelName())
                            .setStatus(car.getStatus()));
                }
                return carsResponse;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }

        return new ArrayList<>();
    }
}
