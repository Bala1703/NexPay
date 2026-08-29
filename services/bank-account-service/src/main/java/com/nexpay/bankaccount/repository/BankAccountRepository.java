package com.nexpay.bankaccount.repository;

import com.nexpay.bankaccount.entity.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    Page<BankAccount> findByUserId(Long userId, Pageable pageable);

    Optional<BankAccount> findByIban(String iban);
}