package com.example.carrental.repository;

import com.example.carrental.model.Car;
import com.example.carrental.response.GetCarsByRadiusResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    // Join car_owner table with car table and then join that table with users table and call function
    @Query(value =
            "SELECT c.id, c.brand, c.color, c.model_name, c.price, " +
            "u.user_id, u.email, u.name FROM car_owner co " +
            "JOIN car c ON co.car_id = c.id " +
            "JOIN users u ON co.owner_id = u.user_id " +
            "WHERE c.status = true AND c.stock > 0 AND " +
            "calculate_haversine_distance(u.latitude, u.longitude, :latValue, :lonValue) <= :radius",
            nativeQuery = true)
    List<Object[]> findCarsWithinDistance(
        @Param("latValue") Double latValue,
        @Param("lonValue") Double lonValue,
        @Param("radius") Double radius
    );

    @Query(value = "SELECT DISTINCT brand FROM car", nativeQuery = true)
    List<String> findDistinctBrands();
}
