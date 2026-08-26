package com.nexpay.transaction.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {

    private Long senderId;
    private Long receiverId;
    private BigDecimal amount;
}