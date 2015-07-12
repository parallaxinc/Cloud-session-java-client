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
public class UnknownBucketTypeException extends Exception {

    private String type;
    private static final String DEFAULT_MESSAGE = "Unkown bucket type";

    public UnknownBucketTypeException() {
        super(DEFAULT_MESSAGE);
    }

    public UnknownBucketTypeException(String type) {
        super(DEFAULT_MESSAGE);
        this.type = type;
    }

    public UnknownBucketTypeException(String type, String message) {
        super(message);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
