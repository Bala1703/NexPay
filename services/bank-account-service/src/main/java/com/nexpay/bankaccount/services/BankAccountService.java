package com.nexpay.bankaccount.services;

import com.nexpay.bankaccount.DTO.BankAccountResponse;
import com.nexpay.bankaccount.DTO.CreateBankAccountRequest;
import com.nexpay.bankaccount.DTO.UpdateBankAccountRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface BankAccountService {

    BankAccountResponse createAccount(CreateBankAccountRequest request);

    BankAccountResponse getAccountById(Long accountId);

    Page<BankAccountResponse> getAccountsByUserId(
            Long userId,
            Pageable pageable
    );

    BankAccountResponse updateAccount(
            Long accountId,
            UpdateBankAccountRequest request
    );

    BankAccountResponse debit(
            Long accountId,
            BigDecimal amount
    );

    BankAccountResponse credit(
            Long accountId,
            BigDecimal amount
    );
}