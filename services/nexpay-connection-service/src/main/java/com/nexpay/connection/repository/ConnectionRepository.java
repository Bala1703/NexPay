package com.nexpay.connection.repository;

import com.nexpay.connection.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository
        extends JpaRepository<Connection, Long> {

    List<Connection> findByUserId(Long userId);

    Optional<Connection> findByUserIdAndConnectedUserId(
            Long userId,
            Long connectedUserId
    );

    boolean existsByUserIdAndConnectedUserId(
            Long userId,
            Long connectedUserId
    );
}