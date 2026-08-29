package com.nexpay.transaction.repository;

import com.nexpay.transaction.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findBySenderId(
            Long senderId,
            Pageable pageable
    );

    Page<Transaction> findByReceiverId(
            Long receiverId,
            Pageable pageable
    );
}