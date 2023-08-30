package com.example.carrental.repository;

import com.example.carrental.model.CarBooking;
import com.example.carrental.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<CarBooking, Long> {

    public List<CarBooking> findAllByUser(Users user);
}
