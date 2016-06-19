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
public class WrongAuthenticationSourceException extends Exception {

    private static final String DEFAULT_MESSAGE = "Wrong authentication srouce";
    private String authenticationSource;

    public WrongAuthenticationSourceException() {
        super(DEFAULT_MESSAGE);
    }

    public WrongAuthenticationSourceException(String authenticationSource) {
        super(DEFAULT_MESSAGE);
        this.authenticationSource = authenticationSource;
    }

    public WrongAuthenticationSourceException(String authenticationSource, String message) {
        super(message);
        this.authenticationSource = authenticationSource;
    }

    public String getAuthenticationSource() {
        return authenticationSource;
    }

}
