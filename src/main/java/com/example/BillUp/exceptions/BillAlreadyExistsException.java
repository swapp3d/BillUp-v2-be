package com.example.BillUp.exceptions;

public class BillAlreadyExistsException extends RuntimeException {
    public BillAlreadyExistsException(String message) {
        super(message);
    }
}
