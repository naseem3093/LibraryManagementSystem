package com.example.exceptions;

public class TransactionNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public TransactionNotFoundException(String message) {
        super(message);
    }
}
