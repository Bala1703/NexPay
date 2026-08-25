package com.nexpay.bankaccount.exceptions;

public class DuplicateIbanException extends RuntimeException {

    public DuplicateIbanException(String message) {
        super(message);
    }
}