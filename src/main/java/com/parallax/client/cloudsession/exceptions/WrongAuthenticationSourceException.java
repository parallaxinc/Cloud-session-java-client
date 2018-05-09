/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.exceptions;

/**
 * Exception signaling that the selected authentication source is incorrect
 * <p>
 * This can happen when the user has an account that was created using Google
 * Authentication and the user subsequently attempts to log in with local
 * user database authentication. 
 * 
 * @author Michel
 */
public class WrongAuthenticationSourceException extends Exception {

    private static final String DEFAULT_MESSAGE = "Wrong authentication srouce";
    private String authenticationSource;

    /**
     * Exception using default "Wrong authentication source" message
     * 
     */
    public WrongAuthenticationSourceException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     *
     * @param authenticationSource
     */
    public WrongAuthenticationSourceException(String authenticationSource) {
        super(DEFAULT_MESSAGE);
        this.authenticationSource = authenticationSource;
    }

    /**
     *
     * @param authenticationSource
     * @param message
     */
    public WrongAuthenticationSourceException(String authenticationSource, String message) {
        super(message);
        this.authenticationSource = authenticationSource;
    }

    /**
     *
     * @return
     */
    public String getAuthenticationSource() {
        return authenticationSource;
    }

}
