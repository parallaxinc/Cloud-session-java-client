/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.exceptions;

/**
 * The screen name submitted during registration already exists
 * 
 * @author Michel
 */
public class ScreennameUsedException extends Exception {

    private String screenname;
    private static final String DEFAULT_MESSAGE = "Screenname already in use";

    /**
     * Exception with a default message
     */
    public ScreennameUsedException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Exception using a provided screen name
     * 
     * @param screenname
     */
    public ScreennameUsedException(String screenname) {
        super(DEFAULT_MESSAGE);
        this.screenname = screenname;
    }

    /**
     * Exception using a provided screen name and error message
     * 
     * @param screenname The screen name that was detected in the database 
     * @param message An error message to be passed with the exception
     */
    public ScreennameUsedException(String screenname, String message) {
        super(message);
        this.screenname = screenname;
    }

    /**
     * Returns the screen name.
     * <p>
     * TODO: find out why there is a getter and setter for a screen name
     * within an Exception object. 
     * 
     * @return
     */
    public String getScreenname() {
        return screenname;
    }

    /**
     * Sets a screen name.
     * 
     * @param screenname
     */
    public void setScreenname(String screenname) {
        this.screenname = screenname;
    }

}
