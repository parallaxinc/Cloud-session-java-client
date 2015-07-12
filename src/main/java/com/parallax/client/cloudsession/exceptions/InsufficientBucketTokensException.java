/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.exceptions;

import java.util.Date;

/**
 *
 * @author Michel
 */
public class InsufficientBucketTokensException extends Exception {

    private String type;
    private int available;
    private int required;
    private Date nextTime;
    private static final String DEFAULT_MESSAGE = "Insufficient bucket tokens";

    public InsufficientBucketTokensException() {
        super(DEFAULT_MESSAGE);
    }

    public InsufficientBucketTokensException(String type, int available, int required, Date nextTime) {
        super(DEFAULT_MESSAGE);
        this.type = type;
        this.available = available;
        this.required = required;
        this.nextTime = nextTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getRequired() {
        return required;
    }

    public void setRequired(int required) {
        this.required = required;
    }

    public Date getNextTime() {
        return nextTime;
    }

    public void setNextTime(Date nextTime) {
        this.nextTime = nextTime;
    }

}
