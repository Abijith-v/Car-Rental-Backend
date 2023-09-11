package com.example.carrental.service;

import com.example.carrental.model.Role;
import com.example.carrental.model.Users;
import com.example.carrental.payload.UpdateUserPayload;
import com.example.carrental.repository.UserRepository;
import com.example.carrental.response.UserLoginResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    public final static String GET_TOKEN_API_ENDPOINT = "http://localhost:8080/auth/token";

    public final static String LOGOUT_USER_API_ENDPOINT = "http://localhost:8080/auth/revoke";

    public final static String VALIDATE_TOKEN_API_ENDPOINT = "http://localhost:8080/auth/validate";

    public final static String GET_USERNAME_FROM_TOKEN_API_ENDPOINT = "http://localhost:8080/auth/get/username";

    public final static String DEFAULT_ROLE = "ROLE_NORMAL";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    public ResponseEntity<String> registerUser(Users user, HttpSession httpSession) {
        try {
            if (!userRepository.existsByEmail(user.getEmail())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
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

    public ResponseEntity<?> loginUser(String username, String password) {
        try {
            // Call get token API
            if (userRepository.existsByEmail(username)) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                String requestBody = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
                // Create the request entity with headers and body
                HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
                // Make the POST request
                ResponseEntity<String> response = restTemplate.exchange(
                        GET_TOKEN_API_ENDPOINT,
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );
                if (response.getStatusCode().is2xxSuccessful()) {
                    return response;
                } else {
                    throw new Exception("Login returned " + response.getStatusCode());
                }
            } else {
                return new ResponseEntity<>(
                    new UserLoginResponse("User not found with username " + username, null),
                    HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                new UserLoginResponse("Invalid password - " + e, null),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public String getEmailFromToken(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    GET_USERNAME_FROM_TOKEN_API_ENDPOINT,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                System.out.println("500 code while fetching username from " + GET_USERNAME_FROM_TOKEN_API_ENDPOINT);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "";
    }

    public ResponseEntity<?> logoutUserWithToken(String token) {
        try {
            // Call logout user API
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
            return restTemplate.exchange(
                    LOGOUT_USER_API_ENDPOINT,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );
        } catch (Exception e) {

            System.out.println(e.getMessage());
            return new ResponseEntity<>(
                    Map.of("message", "Failed to logout user, service returned 500"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public boolean validateToken(String token) {
        try {
            String username = this.getEmailFromToken(token);
            // Call validate token API
            if (userRepository.existsByEmail(username)) {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", token);
                headers.setContentType(MediaType.APPLICATION_JSON);

                String requestBody = "{\"username\": \"" + username + "\"}";
                HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        VALIDATE_TOKEN_API_ENDPOINT,
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );
                if (response.getStatusCode().is2xxSuccessful()) {
                    JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                    return jsonResponse.get("tokenValid").asBoolean();
                } else {
                    System.out.println("500 code");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public boolean isAdminToken(String token) {
        try {
            // @TODO - Cache fetching username later
            String username = this.getEmailFromToken(token.substring(7));
            String role = DEFAULT_ROLE;
            Optional<Users> optionalUser = userRepository.findByEmail(username);
            Users user = optionalUser.orElse(null);
            if (user != null) {
                Set<Role> roles = user.getRoles();
                role = roles.iterator().hasNext() ? roles.iterator().next().getRoleName() : "ROLE_NORMAL";
            }

            System.out.println(role);
            return role.trim().equals("ROLE_ADMIN");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public boolean updateUserDetails(Users user, UpdateUserPayload payload) {

        try {
            if (user != null) {

                if (payload.getAge() != null) {
                    user.setAge(payload.getAge());
                }
                if (payload.getCity() != null) {
                    user.setCity(payload.getCity());
                }
                if (payload.getName() != null) {
                    user.setName(payload.getName());
                }
                if (payload.getState() != null) {
                    user.setState(payload.getState());
                }
                if (payload.getLatitude() != 0.0) {
                    user.setLatitude(payload.getLatitude());
                }
                if (payload.getLongitude() != 0.0) {
                    user.setLongitude(payload.getLongitude());
                }
                userRepository.save(user);
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}
