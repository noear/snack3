package org.noear.snack.exception;

/**
 * @author noear 2025/3/16 created
 */
public class ReflectionException extends RuntimeException {
    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
