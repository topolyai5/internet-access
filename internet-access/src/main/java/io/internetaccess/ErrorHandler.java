package io.internetaccess;

public interface ErrorHandler {
    RuntimeException toException(ResponseStatus responseStatus);

    <T> T handle(ResponseStatus status, Class clzz);
}
