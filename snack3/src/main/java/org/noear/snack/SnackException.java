package org.noear.snack;

/**
 * @author noear 2021/9/11 created
 */
public class SnackException extends RuntimeException {
    public SnackException(String message) {
        super(message);
    }

    public SnackException(Throwable cause) {
        super(cause);
    }

    public SnackException(String message, Throwable cause) {
        super(message, cause);
    }
}
