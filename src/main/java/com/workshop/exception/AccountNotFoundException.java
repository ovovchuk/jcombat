package com.workshop.exception;


public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
    }

    public AccountNotFoundException(String msg) {
        super(msg);
    }
}
