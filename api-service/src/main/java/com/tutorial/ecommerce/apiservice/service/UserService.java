package com.tutorial.ecommerce.apiservice.service;

import com.tutorial.ecommerce.apiservice.dto.UserUpdateRequest;
import com.tutorial.ecommerce.apiservice.validator.UserValidator;
import com.tutorial.ecommerce.exception.BadRequestException;
import com.tutorial.ecommerce.model.Role;
import com.tutorial.ecommerce.model.User;
import com.tutorial.ecommerce.repository.UserRepository;
import com.tutorial.ecommerce.apiservice.dto.LoginRequest;
import com.tutorial.ecommerce.apiservice.dto.UserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public UserService(UserRepository userRepository, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    @Transactional
    public User createUser(UserRequest userRequest) {
        userValidator.validateCreateUserRequest(userRequest);

        User user = User.builder().username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .role(Role.valueOf(userRequest.getRole()))
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public User authenticateUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public User updateUser(Long id, UserUpdateRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userValidator.validateUpdateUserRequest(id, userRequest);
        if (StringUtils.hasLength(userRequest.getUsername())) {
            user.setUsername(userRequest.getUsername());
        }
        if (StringUtils.hasLength(userRequest.getPassword())) {
            user.setPassword(userRequest.getPassword());
        }
        if (StringUtils.hasLength(userRequest.getRole())) {
            user.setRole(Role.valueOf(userRequest.getRole()));
        }
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        // Implement user deletion logic
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Implement loadUserByUsername for authentication
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail()) // overwriting email instead of userName, as we have email as token subject
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
        return userDetails;
    }
}
