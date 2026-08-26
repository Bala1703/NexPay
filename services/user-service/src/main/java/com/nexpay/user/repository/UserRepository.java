package com.nexpay.user.repository;

import com.nexpay.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByFirstNameAndLastName(
            String firstName,
            String lastName
    );

    boolean existsByEmail(String email);
}