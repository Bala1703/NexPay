package com.nexpay.bankaccount.services;

import com.nexpay.bankaccount.DTO.BankAccountResponse;
import com.nexpay.bankaccount.DTO.CreateBankAccountRequest;
import com.nexpay.bankaccount.DTO.UpdateBankAccountRequest;
import com.nexpay.bankaccount.entity.BankAccount;
import com.nexpay.bankaccount.entity.BankAccountStatus;
import com.nexpay.bankaccount.exceptions.BankAccountNotFoundException;
import com.nexpay.bankaccount.exceptions.DuplicateIbanException;
import com.nexpay.bankaccount.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    @Override
    public BankAccountResponse createAccount(
            CreateBankAccountRequest request) {

        if (bankAccountRepository
                .findByIban(request.getIban())
                .isPresent()) {

            throw new DuplicateIbanException(
                    "Bank account with this IBAN already exists"
            );
        }

        BankAccount bankAccount = new BankAccount();

        bankAccount.setUserId(request.getUserId());
        bankAccount.setBankName(request.getBankName());
        bankAccount.setIban(request.getIban());
        bankAccount.setBalance(request.getBalance());

        BankAccount savedAccount =
                bankAccountRepository.save(bankAccount);

        return mapToResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccountResponse getAccountById(
            Long accountId) {

        BankAccount bankAccount =
                bankAccountRepository.findById(accountId)
                        .orElseThrow(() ->
                                new BankAccountNotFoundException(
                                        "Bank account not found with id: "
                                                + accountId
                                )
                        );

        return mapToResponse(bankAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BankAccountResponse> getAccountsByUserId(
            Long userId,
            Pageable pageable) {

        return bankAccountRepository
                .findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public BankAccountResponse updateAccount(
            Long accountId,
            UpdateBankAccountRequest request) {

        BankAccount bankAccount =
                bankAccountRepository.findById(accountId)
                        .orElseThrow(() ->
                                new BankAccountNotFoundException(
                                        "Bank account not found with id: "
                                                + accountId
                                )
                        );

        bankAccount.setBankName(request.getBankName());

        BankAccount updatedAccount =
                bankAccountRepository.save(bankAccount);

        return mapToResponse(updatedAccount);
    }

    @Override
    public BankAccountResponse debit(
            Long accountId,
            BigDecimal amount) {

        validateAmount(amount);

        BankAccount bankAccount =
                bankAccountRepository.findById(accountId)
                        .orElseThrow(() ->
                                new BankAccountNotFoundException(
                                        "Bank account not found with id: "
                                                + accountId
                                )
                        );

        validateActiveAccount(bankAccount);

        if (bankAccount.getBalance().compareTo(amount) < 0) {

            throw new IllegalArgumentException(
                    "Insufficient balance"
            );
        }

        bankAccount.setBalance(
                bankAccount.getBalance().subtract(amount)
        );

        BankAccount updatedAccount =
                bankAccountRepository.save(bankAccount);

        return mapToResponse(updatedAccount);
    }

    @Override
    public BankAccountResponse credit(
            Long accountId,
            BigDecimal amount) {

        validateAmount(amount);

        BankAccount bankAccount =
                bankAccountRepository.findById(accountId)
                        .orElseThrow(() ->
                                new BankAccountNotFoundException(
                                        "Bank account not found with id: "
                                                + accountId
                                )
                        );

        validateActiveAccount(bankAccount);

        bankAccount.setBalance(
                bankAccount.getBalance().add(amount)
        );

        BankAccount updatedAccount =
                bankAccountRepository.save(bankAccount);

        return mapToResponse(updatedAccount);
    }

    private void validateAmount(BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Amount must be greater than zero"
            );
        }
    }

    private void validateActiveAccount(
            BankAccount bankAccount) {

        if (bankAccount.getStatus() != BankAccountStatus.ACTIVE) {

            throw new IllegalStateException(
                    "Bank account is not active"
            );
        }
    }

    private BankAccountResponse mapToResponse(
            BankAccount bankAccount) {

        BankAccountResponse response =
                new BankAccountResponse();

        response.setAccountId(
                bankAccount.getAccountId()
        );

        response.setUserId(
                bankAccount.getUserId()
        );

        response.setBankName(
                bankAccount.getBankName()
        );

        response.setIban(
                bankAccount.getIban()
        );

        response.setBalance(
                bankAccount.getBalance()
        );

        response.setStatus(
                bankAccount.getStatus().name()
        );

        response.setCreatedAt(
                bankAccount.getCreatedAt()
        );

        response.setUpdatedAt(
                bankAccount.getUpdatedAt()
        );

        return response;
    }
}