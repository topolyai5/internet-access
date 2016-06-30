package io.internetaccess;

public class ExecuteException extends RuntimeException {
    public ExecuteException(String msg, Exception e) {
        super(msg, e);
    }

    public ExecuteException(String msg) {
        super(msg);
    }

    public ExecuteException(Exception e) {
        super(e);
    }
}
