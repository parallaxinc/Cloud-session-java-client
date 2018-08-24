/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.exceptions;

/**
 * Exception indicating a failure communicating with the REST server
 * 
 * @author Michel
 */
public class ServerException extends Exception {

    /**
     * General exception without a message or a stack trace
     */
    public ServerException() {
        super();
    }

    /**
     * Exception including a provided message
     * 
     * @param message
     */
    public ServerException(String message) {
        super(message);
    }

    /**
     * Exception posting supplied message and the underlying cause.
     * <p>
     * The exception that triggered this exception.
     * 
     * @param message
     * @param cause
     */
    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception with a causal exception.
     * 
     * @param cause
     */
    public ServerException(Throwable cause) {
        super(cause);
    }

    /**
     * Complete exception handler
     * 
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    protected ServerException(String message, Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
