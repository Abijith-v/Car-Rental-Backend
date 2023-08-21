package com.example.carrental.controller;

import com.example.carrental.model.Users;
import com.example.carrental.payload.LoginRequestPayload;
import com.example.carrental.repository.UserRepository;
import com.example.carrental.response.GetUserByIdResponse;
import com.example.carrental.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users user, HttpSession httpSession) {
        return userService.registerUser(user, httpSession);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestPayload requestPayload) {
        return userService.loginUser(requestPayload.getEmail(), requestPayload.getPassword());
    }

    @GetMapping("/email")
    public ResponseEntity<Map<String, String>> checkToken(@RequestHeader("Authorization") String token) {
        String email = userService.getEmailFromToken(token);
        if (email != null) {
            return new ResponseEntity<>(Map.of("username", email), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("username", ""), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer")) {
            return userService.logoutUserWithToken(token);
        } else {
            return new ResponseEntity<>("Invalid token", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/get/{email}")
    public ResponseEntity<GetUserByIdResponse> getUserByEmail(@PathVariable String email, @RequestHeader("Authorization") String token) {
        if (userService.isAdminToken(token)) {
            Optional<Users> user = userRepository.findByEmail(email);
            GetUserByIdResponse userForResponse = new GetUserByIdResponse(user.orElse(null));
            return new ResponseEntity<>(userForResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }
}
