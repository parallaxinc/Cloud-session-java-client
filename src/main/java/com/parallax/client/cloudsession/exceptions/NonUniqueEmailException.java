/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.exceptions;

/**
 * A duplicate email address has been detected during user registration
 * <p>
 * The email address submitted during a new account registration already
 * exists in the local user database.
 * <p>
 * @author Michel
 */
public class NonUniqueEmailException extends Exception {

    private String email;
    private static final String DEFAULT_MESSAGE = "Email already in use";

    /**
     * Exception using the default "Email already in use" message
     */
    public NonUniqueEmailException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     *
     * @param email
     */
    public NonUniqueEmailException(String email) {
        super(DEFAULT_MESSAGE);
        this.email = email;
    }

    /**
     *
     * @param email
     * @param message
     */
    public NonUniqueEmailException(String email, String message) {
        super(message);
        this.email = email;
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

}
