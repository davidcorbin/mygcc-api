package com.mygcc.datacollection;

/**
 * Invalid Credentials Exception.
 *
 * Thrown when user enters an invalid username or password.
 */
public class InvalidCredentialsException extends Exception {
    /**
     * Constructor with message.
     * @param message message about login issue
     */
    public InvalidCredentialsException(final String message) {
        super(message);
    }
}
