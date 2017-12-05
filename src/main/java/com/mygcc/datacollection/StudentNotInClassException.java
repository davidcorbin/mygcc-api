package com.mygcc.datacollection;

/**
 * Unexpected Response Exception.
 *
 * Thrown when unexpected data is received from myGCC.
 */
public class StudentNotInClassException extends Exception {
    /**
     * Default constructor.
     */
    public StudentNotInClassException() {

    }

    /**
     * Constructor with message.
     * @param message message about login issue
     */
    public StudentNotInClassException(final String message) {
        super(message);
    }
}
