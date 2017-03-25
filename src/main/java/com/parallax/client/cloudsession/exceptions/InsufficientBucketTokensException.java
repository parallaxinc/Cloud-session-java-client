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
 * Insufficient tokens available to complete the activity
 * <p>
 * This exception should be raised when a throttled activity has an
 * insufficient number of tokens available.
 * <p>
 * Tokens are used to provide a limit to the number of times an activity,
 * such as compiling a project into C or Spin code, can be performed within
 * a specific time frame. The user is assigned a specific number of tokens
 * when the account is authenticated. A new token is added to the user's
 * bucket at specific time intervals, up the maximum number of tokens that
 * can be assigned for an activity. A token is removed from the bucket each
 * time an activity is performed.
 * <p>
 * @author Michel
 */
public class InsufficientBucketTokensException extends Exception {

    private static SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private Date nextTime;
    private static final String DEFAULT_MESSAGE = "Insufficient bucket tokens";

    /**
     *  Exception with default "Insufficient bucket tokens" message
     */
    public InsufficientBucketTokensException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     *
     * @param message
     * @param nextTimeString
     */
    public InsufficientBucketTokensException(String message, String nextTimeString) {
        super(message);
        try {
            this.nextTime = DATE_TIME_FORMATTER.parse(nextTimeString);
        } catch (ParseException pe) {
        }
    }

    /**
     *
     * @return
     */
    public Date getNextTime() {
        return nextTime;
    }

    /**
     *
     * @param nextTime
     */
    public void setNextTime(Date nextTime) {
        this.nextTime = nextTime;
    }

}
