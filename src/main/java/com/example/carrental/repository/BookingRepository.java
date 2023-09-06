package com.example.carrental.repository;

import com.example.carrental.model.Car;
import com.example.carrental.model.CarBooking;
import com.example.carrental.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<CarBooking, Long> {

    public List<CarBooking> findAllByUser(Users user);

    public Optional<CarBooking> findByCarId(Long carId);

    public void deleteByCarAndUser(Car car, Users user);
}
