package com.mygcc.api;

/**
 * User class.
 */
public class User {
    /**
     * Username string.
     */
    private String username;

    /**
     * Password string.
     */
    private String password;

    /**
     * User constructor.
     * @param un username
     * @param pw password
     */
    public User(final String un, final String pw) {
        this.username = un;
        this.password = pw;
    }

    /**
     * Default constructor.
     * Required to deserialize the entity from stream.
     */
    public User() {
    }

    /**
     * Get username.
     * @return username string
     */
    public final String getUsername() {
        return this.username;
    }

    /**
     * Set username.
     * @param un username
     */
    public final void setUsername(final String un) {
        this.username = un;
    }

    /**
     * Get password.
     * @return password string
     */
    public final String getPassword() {
        return this.password;
    }

    /**
     * Set password.
     * @param pw password string
     */
    public final void setPassword(final String pw) {
        this.password = pw;
    }

    /**
     * Check if any member variables are null.
     * @return whether any values are null
     */
    public final boolean checkRequiredParams() {
        return !(username == null || password == null);
    }
}
