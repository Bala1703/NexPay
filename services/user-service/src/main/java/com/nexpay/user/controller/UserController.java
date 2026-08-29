package com.nexpay.user.controller;

import com.nexpay.user.dto.CreateUserRequest;
import com.nexpay.user.dto.UserResponse;
import com.nexpay.user.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        return userService.createUser(request);
    }

   @GetMapping
public Page<UserResponse> getUsers(Pageable pageable) {
    return userService.getUsers(pageable);
}

    @GetMapping("/{id}")
    public UserResponse getUserById(
            @PathVariable Integer id
    ) {
        return userService.getUserById(id);
    }

    @GetMapping("/email/{email}")
    public UserResponse getUserByEmail(
            @PathVariable String email
    ) {
        return userService.getUserByEmail(email);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @PathVariable Integer id
    ) {
        userService.deleteUser(id);
    }
}