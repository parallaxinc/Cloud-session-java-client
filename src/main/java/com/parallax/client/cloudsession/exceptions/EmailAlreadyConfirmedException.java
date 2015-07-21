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
public class EmailAlreadyConfirmedException extends Exception {

    private static final String DEFAULT_MESSAGE = "Email already confirmed";

    public EmailAlreadyConfirmedException() {
        super(DEFAULT_MESSAGE);
    }

    public EmailAlreadyConfirmedException(String message) {
        super(message);
    }

}
