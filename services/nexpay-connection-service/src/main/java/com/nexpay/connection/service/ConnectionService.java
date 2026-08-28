package com.nexpay.connection.service;

import com.nexpay.common.service.PaginationService;
import com.nexpay.connection.dto.ConnectionRequest;
import com.nexpay.connection.dto.ConnectionResponse;
import com.nexpay.connection.entity.Connection;
import com.nexpay.connection.repository.ConnectionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final PaginationService paginationService;

    public ConnectionService(
            ConnectionRepository connectionRepository,
            PaginationService paginationService) {

        this.connectionRepository = connectionRepository;
        this.paginationService = paginationService;
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

    public Page<Connection> getConnectionsByUserId(
            Long userId,
            Pageable pageable) {

        List<Connection> connections =
                connectionRepository.findByUserId(userId);

        return paginationService.paginate(connections, pageable);
    }

    public void deleteConnection(Long connectionId) {
        connectionRepository.deleteById(connectionId);
    }
}