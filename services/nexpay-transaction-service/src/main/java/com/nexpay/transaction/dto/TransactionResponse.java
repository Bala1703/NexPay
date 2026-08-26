package com.nexpay.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionResponse {

    private Long transactionId;
    private Long senderId;
    private Long receiverId;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}