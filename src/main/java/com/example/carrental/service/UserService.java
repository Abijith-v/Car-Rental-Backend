package com.example.carrental.service;

import com.example.carrental.model.Users;
import com.example.carrental.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> registerUser(Users user, HttpSession httpSession) {
        try {

            if (!userRepository.existsByEmail(user.getEmail())) {
                userRepository.save(user);
                return new ResponseEntity<>("Created User", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Email already exists", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch(Exception e) {
            return new ResponseEntity<>(
                    "Failed to create user : " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
