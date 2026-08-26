package com.nexpay.connection.service;

import com.nexpay.connection.dto.ConnectionRequest;
import com.nexpay.connection.dto.ConnectionResponse;
import com.nexpay.connection.entity.Connection;
import com.nexpay.connection.repository.ConnectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionService {

    private final ConnectionRepository connectionRepository;

    public ConnectionService(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    public ConnectionResponse createConnection(ConnectionRequest request) {

        Connection connection = new Connection();

        connection.setUserId(request.getUserId());
        connection.setConnectedUserId(request.getConnectedUserId());

        Connection savedConnection = connectionRepository.save(connection);

        return new ConnectionResponse(
                savedConnection.getConnectionId(),
                savedConnection.getUserId(),
                savedConnection.getConnectedUserId()
        );
    }

    public List<Connection> getConnectionsByUserId(Long userId) {
        return connectionRepository.findByUserId(userId);
    }

    public void deleteConnection(Long connectionId) {
        connectionRepository.deleteById(connectionId);
    }
}