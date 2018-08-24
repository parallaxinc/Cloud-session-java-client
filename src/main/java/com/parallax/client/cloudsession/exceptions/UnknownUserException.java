/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.exceptions;

/**
 * The system is unable to find a matching user email record
 * 
 * @author Michel
 */
public class UnknownUserException extends Exception {

    private String email;
    private static final String DEFAULT_MESSAGE = "Unknown user";

    /**
     * Exception using the default "Unknown user" message
     * 
     */
    public UnknownUserException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     *
     * @param email
     */
    public UnknownUserException(String email) {
        super(DEFAULT_MESSAGE);
        this.email = email;
    }

    /**
     *
     * @param email
     * @param message
     */
    public UnknownUserException(String email, String message) {
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
