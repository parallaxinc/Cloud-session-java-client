/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.exceptions;

/**
 * Submitted password does not meet complexity requirements
 * 
 * @author Michel
 */
public class PasswordComplexityException extends Exception {

    private static final String DEFAULT_MESSAGE = "Password is not complex enough";

    /**
     * Exception using the default message
     */
    public PasswordComplexityException() {
        super(DEFAULT_MESSAGE);
    }

}
