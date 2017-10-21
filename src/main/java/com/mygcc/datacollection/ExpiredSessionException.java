package com.mygcc.datacollection;

/**
 * Expired Session Exception.
 *
 * Thrown when a user's session is expired and valid data can not be retrieved.
 */
public class ExpiredSessionException extends Exception {
    /**
     * Default constructor.
     */
    public ExpiredSessionException() {

    }

    /**
     * Constructor with message.
     * @param message message about login issue
     */
    public ExpiredSessionException(final String message) {
        super(message);
    }
}
