package com.tutorial.ecommerce.userservice;

import com.tutorial.ecommerce.dto.ErrorResponse;
import com.tutorial.ecommerce.model.User;
import com.tutorial.ecommerce.security.JwtTokenProvider;
import com.tutorial.ecommerce.userservice.dto.JwtResponse;
import com.tutorial.ecommerce.userservice.dto.LoginRequest;
import com.tutorial.ecommerce.userservice.dto.SuccessResponse;
import com.tutorial.ecommerce.userservice.dto.UserRequest;
import com.tutorial.ecommerce.userservice.dto.UserResponse;
import com.tutorial.ecommerce.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        User user = userService.createUser(userRequest);
        UserResponse response = new UserResponse(user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.authenticateUser(loginRequest);
            String token = jwtTokenProvider.generateToken(user.getUsername(), String.valueOf(user.getRole()));
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse("Invalid credentials"), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserResponse response = new UserResponse(user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        User user = userService.updateUser(id, userRequest);
        UserResponse response = new UserResponse(user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new SuccessResponse("User deleted successfully."));
    }
}
