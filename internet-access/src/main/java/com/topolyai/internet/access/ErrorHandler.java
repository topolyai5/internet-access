package com.topolyai.internet.access;

public interface ErrorHandler {
    RuntimeException toException(ResponseStatus responseStatus);

    <T> T handle(ResponseStatus status, Class clzz);
}
