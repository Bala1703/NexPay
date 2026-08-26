package com.nexpay.user.dto;

import java.math.BigDecimal;

public record UserResponse(
        Integer id,
        String email,
        String firstName,
        String lastName,
        BigDecimal balance
) {
}