package com.nexpay.user.services;

import com.nexpay.user.dto.CreateUserRequest;
import com.nexpay.user.dto.UserResponse;
import com.nexpay.user.entity.User;
import com.nexpay.user.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        if (isInvalidEmail(request.email())) {
            throw new IllegalArgumentException(
                    "The email provided is invalid."
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Email " + request.email() + " is already used."
            );
        }

        User user = new User();

        user.setEmail(request.email());

        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setBalance(BigDecimal.ZERO);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    public List<UserResponse> getUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUserById(Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User with ID " + id + " not found."
                        )
                );

        return toResponse(user);
    }

    public UserResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User with email " + email + " not found."
                        )
                );

        return toResponse(user);
    }

    @Transactional
    public void deleteUser(Integer id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException(
                    "User with ID " + id + " not found."
            );
        }

        userRepository.deleteById(id);
    }

    /**
     * Returns the currently authenticated user.
     *
     * The email of the logged-in user is obtained from
     * Spring Security's SecurityContext.
     */
    public User getAuthenticatedUser() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Email " + username +
                                " does not match any user."
                        )
                );
    }

    /**
     * Validates the email address.
     */
    public static boolean isInvalidEmail(String emailAddress) {

        if (emailAddress == null || emailAddress.isBlank()) {
            return true;
        }

        return !Pattern.compile(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )
        .matcher(emailAddress)
        .matches();
    }

    /**
     * Converts User entity into UserResponse DTO.
     */
    private UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getBalance()
        );
    }
}

