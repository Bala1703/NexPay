package com.nexpay.bankaccount.services;

import com.nexpay.bankaccount.DTO.BankAccountResponse;
import com.nexpay.bankaccount.DTO.CreateBankAccountRequest;
import com.nexpay.bankaccount.DTO.UpdateBankAccountRequest;
import com.nexpay.bankaccount.entity.BankAccount;
import com.nexpay.bankaccount.exceptions.BankAccountNotFoundException;
import com.nexpay.bankaccount.exceptions.DuplicateIbanException;
import com.nexpay.bankaccount.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    @Override
    public BankAccountResponse createAccount(CreateBankAccountRequest request) {

        if (bankAccountRepository.findByIban(request.getIban()).isPresent()) {
            throw new DuplicateIbanException(
                    "Bank account with this IBAN already exists"
            );
        }

        BankAccount bankAccount = new BankAccount();

        bankAccount.setUserId(request.getUserId());
        bankAccount.setBankName(request.getBankName());
        bankAccount.setIban(request.getIban());
        bankAccount.setBalance(request.getBalance());

        BankAccount savedAccount = bankAccountRepository.save(bankAccount);

        return mapToResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccountResponse getAccountById(Long accountId) {

        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() ->
                        new BankAccountNotFoundException(
                                "Bank account not found with id: " + accountId
                        )
                );

        return mapToResponse(bankAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountResponse> getAccountsByUserId(Long userId) {

        return bankAccountRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BankAccountResponse updateAccount(
            Long accountId,
            UpdateBankAccountRequest request) {

        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() ->
                        new BankAccountNotFoundException(
                                "Bank account not found with id: " + accountId
                        )
                );

        bankAccount.setBankName(request.getBankName());

        BankAccount updatedAccount =
                bankAccountRepository.save(bankAccount);

        return mapToResponse(updatedAccount);
    }

    private BankAccountResponse mapToResponse(
            BankAccount bankAccount) {

        BankAccountResponse response = new BankAccountResponse();

        response.setAccountId(bankAccount.getAccountId());
        response.setUserId(bankAccount.getUserId());
        response.setBankName(bankAccount.getBankName());
        response.setIban(bankAccount.getIban());
        response.setBalance(bankAccount.getBalance());
        response.setStatus(bankAccount.getStatus().name());
        response.setCreatedAt(bankAccount.getCreatedAt());
        response.setUpdatedAt(bankAccount.getUpdatedAt());

        return response;
    }
}