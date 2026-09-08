package com.nexpay.connection.service;

import com.nexpay.common.service.PaginationService;
import com.nexpay.connection.dto.ConnectionRequest;
import com.nexpay.connection.dto.ConnectionResponse;
import com.nexpay.connection.entity.Connection;
import com.nexpay.connection.repository.ConnectionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final PaginationService paginationService;
    private final RestClient restClient;

    public ConnectionService(
            ConnectionRepository connectionRepository,
            PaginationService paginationService,
            RestClient restClient) {

        this.connectionRepository = connectionRepository;
        this.paginationService = paginationService;
        this.restClient = restClient;
    }

    public ConnectionResponse createConnection(
            ConnectionRequest request) {

        // 1. Check whether the user exists
        checkUserExists(request.getUserId());

        // 2. Check whether the connected user exists
        checkUserExists(request.getConnectedUserId());

        // 3. Create connection
        Connection connection = new Connection();

        connection.setUserId(request.getUserId());

        connection.setConnectedUserId(
                request.getConnectedUserId()
        );

        // 4. Save connection
        Connection savedConnection =
                connectionRepository.save(connection);

        // 5. Return response
        return new ConnectionResponse(
                savedConnection.getConnectionId(),
                savedConnection.getUserId(),
                savedConnection.getConnectedUserId()
        );
    }

    private void checkUserExists(Long userId) {

        try {

            restClient.get()
                    .uri(
                            "http://localhost:8082/api/users/"
                                    + userId
                    )
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception exception) {

            throw new RuntimeException(
                    "User with ID "
                            + userId
                            + " does not exist"
            );
        }
    }

    public Page<Connection> getConnectionsByUserId(
            Long userId,
            Pageable pageable) {

        List<Connection> connections =
                connectionRepository.findByUserId(userId);

        return paginationService.paginate(
                connections,
                pageable
        );
    }

    public boolean isConnected(
            Long userId,
            Long connectedUserId) {

        return connectionRepository
                .existsByUserIdAndConnectedUserId(
                        userId,
                        connectedUserId
                );
    }

    public void deleteConnection(Long connectionId) {

        connectionRepository.deleteById(connectionId);
    }
}