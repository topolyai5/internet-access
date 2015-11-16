package com.topolyai.internet.access;

import java.io.IOException;

public class ConnectionErrorException extends RuntimeException {
    public ConnectionErrorException(String message, IOException e) {
        super(message, e);
    }
}
