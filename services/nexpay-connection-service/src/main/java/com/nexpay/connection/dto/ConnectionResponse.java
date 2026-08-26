package com.nexpay.connection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectionResponse {

    private Long connectionId;
    private Long userId;
    private Long connectedUserId;
}