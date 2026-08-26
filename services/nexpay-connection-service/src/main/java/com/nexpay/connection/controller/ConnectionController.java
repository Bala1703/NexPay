package com.nexpay.connection.controller;

import com.nexpay.connection.dto.ConnectionRequest;
import com.nexpay.connection.dto.ConnectionResponse;
import com.nexpay.connection.entity.Connection;
import com.nexpay.connection.service.ConnectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping
    public ResponseEntity<ConnectionResponse> createConnection(
            @RequestBody ConnectionRequest request) {

        return ResponseEntity.ok(
                connectionService.createConnection(request)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Connection>> getConnections(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                connectionService.getConnectionsByUserId(userId)
        );
    }

    @DeleteMapping("/{connectionId}")
    public ResponseEntity<Void> deleteConnection(
            @PathVariable Long connectionId) {

        connectionService.deleteConnection(connectionId);

        return ResponseEntity.noContent().build();
    }
}