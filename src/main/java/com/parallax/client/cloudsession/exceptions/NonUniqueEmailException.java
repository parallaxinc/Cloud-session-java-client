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
public class NonUniqueEmailException extends Exception {

    private String email;
    private static final String DEFAULT_MESSAGE = "Email already in use";

    public NonUniqueEmailException() {
        super(DEFAULT_MESSAGE);
    }

    public NonUniqueEmailException(String email) {
        super(DEFAULT_MESSAGE);
        this.email = email;
    }

    public NonUniqueEmailException(String email, String message) {
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
