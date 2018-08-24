/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.exceptions;

/**
 * Exception when the referenced user account has been administratively blocked
 * 
 * @author Michel
 */
public class UserBlockedException extends Exception {

    private static final String DEFAULT_MESSAGE = "User is blocked";

    /**
     * Exception using the default "User is Blocked" message
     * 
     */
    public UserBlockedException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     *
     * @param message
     */
    public UserBlockedException(String message) {
        super(message);
    }

}
