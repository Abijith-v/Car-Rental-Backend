package com.example.carrental.repository;

import com.example.carrental.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    public boolean existsByEmail(String email);

    public Optional<Users> findByEmail(String Email);
}
