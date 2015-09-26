package com.topolyai.internet.access;

public class ExtractResponseException extends RuntimeException {
    public ExtractResponseException(String message, Exception e) {
        super(message, e);
    }
}
