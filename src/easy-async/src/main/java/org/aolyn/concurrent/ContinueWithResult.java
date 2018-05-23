package org.aolyn.concurrent;

public class ContinueWithResult {

    private Exception exception;
    private boolean isSuccess;

    ContinueWithResult(boolean isSuccess, Exception exception) {
        this.isSuccess = isSuccess;
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
