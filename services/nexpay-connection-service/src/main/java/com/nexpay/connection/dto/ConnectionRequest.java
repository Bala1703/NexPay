package com.nexpay.connection.dto;

import lombok.Data;

@Data
public class ConnectionRequest {

    private Long userId;
    private Long connectedUserId;
}