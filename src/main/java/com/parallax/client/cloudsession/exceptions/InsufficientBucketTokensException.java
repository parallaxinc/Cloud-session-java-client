/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.exceptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Michel
 */
public class InsufficientBucketTokensException extends Exception {

    private static SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private Date nextTime;
    private static final String DEFAULT_MESSAGE = "Insufficient bucket tokens";

    public InsufficientBucketTokensException() {
        super(DEFAULT_MESSAGE);
    }

    public InsufficientBucketTokensException(String message, String nextTimeString) {
        super(message);
        try {
            this.nextTime = DATE_TIME_FORMATTER.parse(nextTimeString);
        } catch (ParseException pe) {
        }
    }

    public Date getNextTime() {
        return nextTime;
    }

    public void setNextTime(Date nextTime) {
        this.nextTime = nextTime;
    }

}
