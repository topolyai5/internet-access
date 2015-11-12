package com.topolyai.internet.access;

public interface ErrorHandler {
    RuntimeException handle(ResponseStatus responseStatus);
}
