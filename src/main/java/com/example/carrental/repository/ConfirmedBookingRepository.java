package com.example.carrental.repository;

import com.example.carrental.model.ConfirmedBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfirmedBookingRepository extends JpaRepository<ConfirmedBooking, Long> {

    @Query(value = "SELECT cb.* FROM confirmed_booking cb JOIN car_owner co ON cb.car_id = co.car_id WHERE co.owner_id = :ownerId", nativeQuery = true)
    List<ConfirmedBooking> findAllByOwnerId(@Param("ownerId") Long ownerId);

    List<ConfirmedBooking> findAllByStatus(String status);
}
