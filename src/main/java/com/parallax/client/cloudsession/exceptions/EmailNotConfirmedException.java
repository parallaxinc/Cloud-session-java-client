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
public class EmailNotConfirmedException extends Exception {

    private static final String DEFAULT_MESSAGE = "Email not confirmed";

    public EmailNotConfirmedException() {
        super(DEFAULT_MESSAGE);
    }

    public EmailNotConfirmedException(String message) {
        super(message);
    }

}
