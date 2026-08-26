package com.nexpay.bankaccount.services;

import com.nexpay.bankaccount.DTO.BankAccountResponse;
import com.nexpay.bankaccount.DTO.CreateBankAccountRequest;
import com.nexpay.bankaccount.DTO.UpdateBankAccountRequest;

import java.util.List;

public interface BankAccountService {

    BankAccountResponse createAccount(CreateBankAccountRequest request);

    BankAccountResponse getAccountById(Long accountId);

    List<BankAccountResponse> getAccountsByUserId(Long userId);

    BankAccountResponse updateAccount(Long accountId, UpdateBankAccountRequest request);
}