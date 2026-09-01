package com.nexpay.payment.controller;

import com.nexpay.payment.dto.PaymentRequest;
import com.nexpay.payment.dto.PaymentResponse;
import com.nexpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> makePayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response =
                paymentService.makePayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}