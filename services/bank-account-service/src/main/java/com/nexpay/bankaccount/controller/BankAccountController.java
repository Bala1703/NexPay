package com.nexpay.bankaccount.controller;

import com.nexpay.bankaccount.DTO.BankAccountResponse;
import com.nexpay.bankaccount.DTO.CreateBankAccountRequest;
import com.nexpay.bankaccount.DTO.UpdateBankAccountRequest;
import com.nexpay.bankaccount.services.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(
            @Valid @RequestBody CreateBankAccountRequest request) {

        BankAccountResponse response =
                bankAccountService.createAccount(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<BankAccountResponse> getAccountById(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(
                bankAccountService.getAccountById(accountId)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<BankAccountResponse>>
    getAccountsByUserId(
            @PathVariable Long userId,
            Pageable pageable) {

        return ResponseEntity.ok(
                bankAccountService
                        .getAccountsByUserId(userId, pageable)
        );
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<BankAccountResponse> updateAccount(
            @PathVariable Long accountId,
            @Valid @RequestBody UpdateBankAccountRequest request) {

        return ResponseEntity.ok(
                bankAccountService.updateAccount(
                        accountId,
                        request
                )
        );
    }

    @PostMapping("/{accountId}/debit")
    public ResponseEntity<BankAccountResponse> debit(
            @PathVariable Long accountId,
            @RequestParam BigDecimal amount) {

        return ResponseEntity.ok(
                bankAccountService.debit(
                        accountId,
                        amount
                )
        );
    }

    @PostMapping("/{accountId}/credit")
    public ResponseEntity<BankAccountResponse> credit(
            @PathVariable Long accountId,
            @RequestParam BigDecimal amount) {

        return ResponseEntity.ok(
                bankAccountService.credit(
                        accountId,
                        amount
                )
        );
    }
}