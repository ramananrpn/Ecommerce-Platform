package com.tutorial.ecommerce.apiservice.validator;

import com.tutorial.ecommerce.apiservice.dto.UserRequest;
import com.tutorial.ecommerce.apiservice.dto.UserUpdateRequest;
import com.tutorial.ecommerce.exception.BadRequestException;
import com.tutorial.ecommerce.model.Role;
import com.tutorial.ecommerce.model.User;
import com.tutorial.ecommerce.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserValidator {
    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateCreateUserRequest(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        // validate role
        validateUserRole(userRequest.getRole());
    }

    public void validateUpdateUserRequest(Long id, UserUpdateRequest userRequest) {
        if (StringUtils.hasLength(userRequest.getEmail())) {
            throw new BadRequestException("Email cannot be updated");
        }

        if (StringUtils.hasLength(userRequest.getRole())) {
            validateUserRole(userRequest.getRole());
        }
    }

    private static void validateUserRole(String role) {
        try {
            Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role, allowed values : ADMIN, AGENT, USER");
        }
    }


}
