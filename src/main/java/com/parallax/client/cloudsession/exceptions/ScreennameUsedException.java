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
public class ScreennameUsedException extends Exception {

    private String screenname;
    private static final String DEFAULT_MESSAGE = "Screenname already in use";

    public ScreennameUsedException() {
        super(DEFAULT_MESSAGE);
    }

    public ScreennameUsedException(String screenname) {
        super(DEFAULT_MESSAGE);
        this.screenname = screenname;
    }

    public ScreennameUsedException(String screenname, String message) {
        super(message);
        this.screenname = screenname;
    }

    public String getScreenname() {
        return screenname;
    }

    public void setScreenname(String screenname) {
        this.screenname = screenname;
    }

}
