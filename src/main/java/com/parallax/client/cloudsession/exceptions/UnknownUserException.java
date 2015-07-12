/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.exceptions;

/**
 *
 * @author Michel
 */
public class UnknownUserException extends Exception {

    private String email;
    private static final String DEFAULT_MESSAGE = "Unknown user";

    public UnknownUserException() {
        super(DEFAULT_MESSAGE);
    }

    public UnknownUserException(String email) {
        super(DEFAULT_MESSAGE);
        this.email = email;
    }

    public UnknownUserException(String email, String message) {
        super(message);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
