package com.nexpay.transaction.service;

import com.nexpay.transaction.dto.TransactionRequest;
import com.nexpay.transaction.dto.TransactionResponse;
import com.nexpay.transaction.model.Transaction;
import com.nexpay.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponse createTransaction(TransactionRequest request) {

        Transaction transaction = new Transaction();

        transaction.setSenderId(request.getSenderId());
        transaction.setReceiverId(request.getReceiverId());
        transaction.setAmount(request.getAmount());
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return new TransactionResponse(
                savedTransaction.getTransactionId(),
                savedTransaction.getSenderId(),
                savedTransaction.getReceiverId(),
                savedTransaction.getAmount(),
                savedTransaction.getCreatedAt()
        );
    }

    public List<Transaction> getTransactionsBySenderId(Long senderId) {
        return transactionRepository.findBySenderId(senderId);
    }

    public List<Transaction> getTransactionsByReceiverId(Long receiverId) {
        return transactionRepository.findByReceiverId(receiverId);
    }

    public void deleteTransaction(Long transactionId) {
        transactionRepository.deleteById(transactionId);
    }
}