package com.example.carrental.repository;

import com.example.carrental.model.ConfirmedBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmedBookingRepository extends JpaRepository<ConfirmedBooking, Long> {
}
