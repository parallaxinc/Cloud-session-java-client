/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.exceptions;

/**
 * Email confirmation has already been completed
 * 
 * @author Michel
 */
public class EmailAlreadyConfirmedException extends Exception {

    private static final String DEFAULT_MESSAGE = "Email already confirmed";

    /**
     * Exception with default "Email is already confirmed" message
     */
    public EmailAlreadyConfirmedException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Exception using a supplied message
     * 
     * @param message
     */
    public EmailAlreadyConfirmedException(String message) {
        super(message);
    }

}
