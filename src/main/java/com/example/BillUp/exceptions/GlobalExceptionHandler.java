package com.example.BillUp.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_SECTION_NAME = "error";

    private static final String ERROR_DESCRIPTION = "error description";

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
}
