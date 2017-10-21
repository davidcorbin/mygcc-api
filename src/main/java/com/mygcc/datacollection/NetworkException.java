package com.mygcc.datacollection;

/**
 * Network error.
 *
 * Thrown when there is an issue connection to my.gcc.edu.
 */
public class NetworkException extends Exception {
    /**
     * Default constructor.
     */
    public NetworkException() {

    }

    /**
     * Constructor with message.
     * @param message message about issue
     */
    public NetworkException(final String message) {
        super(message);
    }
}
