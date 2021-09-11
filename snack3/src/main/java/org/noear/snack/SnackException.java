package org.noear.snack;

/**
 * Snack 异常
 *
 * @author noear
 * @since 3.1
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
