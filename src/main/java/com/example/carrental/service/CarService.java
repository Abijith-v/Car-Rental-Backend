package com.example.carrental.service;

import com.example.carrental.model.Car;
import com.example.carrental.model.Users;
import com.example.carrental.payload.FilterCarsPayload;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.response.GetCarsByRadiusResponse;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

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
            System.out.println(e);
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
}
