/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.objects;

import java.io.Serializable;

/**
 *
 * @author Michel
 */
public class User implements Serializable {

    private Long id;
    private String email;
    private String locale;
    private String screenname;
    private String authenticationSource;
    
    private int error;
    private String errorMessage;

    /**
     * The User class holds information related to a Cloud-Session user account
     */
    public User() {
    }

    /**
     * User constructor
     * 
     * @param id Identifier used to locate user account
     * @param email User email address
     * @param locale User language
     * @param authenticationSource Authentication provider
     */
    public User(Long id, String email, String locale, String authenticationSource) {
        this.id = id;
        this.email = email;
        this.locale = locale;
        this.authenticationSource = authenticationSource;
        this.error = 0;
        this.errorMessage = "";
    }

    /**
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     */
    public String getLocale() {
        return locale;
    }

    /**
     *
     * @param locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     *
     * @return
     */
    public String getScreenname() {
        return screenname;
    }

    /**
     *
     * @param screenname
     */
    public void setScreenname(String screenname) {
        this.screenname = screenname;
    }

    /**
     *
     * @return
     */
    public String getAuthenticationSource() {
        return authenticationSource;
    }

    /**
     *
     * @param authenticationSource
     */
    public void setAuthenticationSource(String authenticationSource) {
        this.authenticationSource = authenticationSource;
    }

    /**
     * 
     * @return 
     */
    public int getError() {
        return error;
    }
   
    /**
     * 
     * @param error_number 
     */
     public void setError( int error_number) {
         this.error = error;
     }
   
    /**
     * 
     * @return 
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }
   
    /**
     * 
     * @param errorMessage 
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
