/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.exceptions;

/**
 * Exception triggered by referencing an undefined token bucket type
 * 
 * @author Michel
 */
public class UnknownBucketTypeException extends Exception {

    private String type;
    private static final String DEFAULT_MESSAGE = "Unkown bucket type";

    /**
     * Exception using the default "Unknown bucket type" message
     */
    public UnknownBucketTypeException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Exception using the default message
     * <p>
     * The exception can receive a bucket type string. This string can be
     * accessed through the Type getter and setter methods.
     * 
     * @param type A bucket type
     */
    public UnknownBucketTypeException(String type) {
        super(DEFAULT_MESSAGE);
        this.type = type;
    }

    /**
     * Exception using provided message and bucket type
     * 
     * @param type A bucket type
     * @param message Exception message
     */
    public UnknownBucketTypeException(String type, String message) {
        super(message);
        this.type = type;
    }

    /**
     *
     * @return A bucket type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type a Bucket type
     */
    public void setType(String type) {
        this.type = type;
    }

}
