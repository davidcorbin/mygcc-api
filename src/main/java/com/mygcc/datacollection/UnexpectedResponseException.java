package com.mygcc.datacollection;

/**
 * Unexpected Response Exception.
 *
 * Thrown when unexpected data is received from myGCC.
 */
public class UnexpectedResponseException extends Exception {
    /**
     * Default constructor.
     */
    public UnexpectedResponseException() {

    }

    /**
     * Constructor with message.
     * @param message message about login issue
     */
    public UnexpectedResponseException(final String message) {
        super(message);
    }
}
