package com.nexpay.payment.dto;

import com.nexpay.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentResponse {

    private Long paymentId;
    private Long senderId;
    private Long receiverId;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
}