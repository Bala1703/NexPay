package com.nexpay.transaction.controller;

import com.nexpay.transaction.dto.TransactionRequest;
import com.nexpay.transaction.dto.TransactionResponse;
import com.nexpay.transaction.model.Transaction;
import com.nexpay.transaction.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody TransactionRequest request) {

        return ResponseEntity.ok(
                transactionService.createTransaction(request)
        );
    }

    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<Transaction>> getTransactionsBySender(
            @PathVariable Long senderId) {

        return ResponseEntity.ok(
                transactionService.getTransactionsBySenderId(senderId)
        );
    }

    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<Transaction>> getTransactionsByReceiver(
            @PathVariable Long receiverId) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByReceiverId(receiverId)
        );
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long transactionId) {

        transactionService.deleteTransaction(transactionId);

        return ResponseEntity.noContent().build();
    }
}