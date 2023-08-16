package com.example.carrental.service;

import com.example.carrental.model.Car;
import com.example.carrental.model.Users;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.response.GetCarsByRadiusResponse;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CarService {

    public List<GetCarsByRadiusResponse> getCarsByRadiusResponse(List<Object[]> cars) {

        List<GetCarsByRadiusResponse> responseList = new ArrayList<>();
        try {
            for (Object[] car : cars) {
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
        } catch (Exception e) {
            System.out.println(e);
        }

        return responseList;
    }
}
