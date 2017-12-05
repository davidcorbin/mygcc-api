package com.mygcc.datacollection;

/**
 * Unexpected Response Exception.
 *
 * Thrown when unexpected data is received from myGCC.
 */
public class ClassDoesNotExistException extends Exception {
    /**
     * Default constructor.
     */
    public ClassDoesNotExistException() {

    }

    /**
     * Constructor with message.
     * @param message message about login issue
     */
    public ClassDoesNotExistException(final String message) {
        super(message);
    }
}
