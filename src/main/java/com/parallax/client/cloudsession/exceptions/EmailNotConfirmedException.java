/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.exceptions;

/**
 * Confirmation process is incomplete
 * <p>
 * An account confirmation email has been sent to the subscribing
 * user but the use has not yet use the link included in the email message
 * to return to the site to verify their identity.
 * 
 * @author Michel
 */
public class EmailNotConfirmedException extends Exception {

    private static final String DEFAULT_MESSAGE = "Email not confirmed";

    /**
     * Exception using the default "Email not confirmed" message
     */
    public EmailNotConfirmedException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Exception using a supplied email message
     * 
     * @param message
     */
    public EmailNotConfirmedException(String message) {
        super(message);
    }

}
