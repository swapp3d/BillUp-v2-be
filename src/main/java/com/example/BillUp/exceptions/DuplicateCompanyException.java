package com.example.BillUp.exceptions;

public class DuplicateCompanyException extends RuntimeException {
    public DuplicateCompanyException(String message) {
        super(message);
    }
}
