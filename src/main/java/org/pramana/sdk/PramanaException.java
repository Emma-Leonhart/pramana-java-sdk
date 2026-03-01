package org.pramana.sdk;

/**
 * Exception thrown when a Pramana OGM constraint is violated,
 * such as attempting to assign an ID to an object that already has one.
 */
public class PramanaException extends RuntimeException {

    public PramanaException() {
        super();
    }

    public PramanaException(String message) {
        super(message);
    }

    public PramanaException(String message, Throwable cause) {
        super(message, cause);
    }
}
