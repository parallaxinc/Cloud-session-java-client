/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.objects;

import java.io.Serializable;
import java.util.Calendar;

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
    private Boolean blocked;
    private Boolean confirmed;
    private String coachEmail;
    private int birthMonth;
    private int birthYear;
    private int coachEmailSource;
    
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
    public User(
            Long id, String email, String locale, String authenticationSource,
            int birthMonth, int birthYear, String parentEmail,
            int parentEmailSource) {
        
        this.id = id;
        this.email = email;
        this.locale = locale;
        this.authenticationSource = authenticationSource;
        this.blocked = false;
        this.confirmed = false;
        this.birthMonth = 0;
        this.birthYear = 0;
        this.coachEmail = "";
        this.coachEmailSource = 0;
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
    public Boolean getBlockled() {
        return blocked;
    }

    /**
     * 
     * @param state 
     */
    public void setBlockled(Boolean state) {
        this.blocked = state;
    }
    
    /**
     * 
     * @return 
     */
    public Boolean getConfirmed() {
        return confirmed;
    }
    
    /**
     * 
     * @param state 
     */
    public void setConfirmed(Boolean state) {
        this.confirmed = state;
    }
    
    /**
     * 
     * @return 
     */
    public int getBirthMonth() {
        return this.birthMonth;
    }
    
    /**
     * 
     * @param month 
     */
    public void setBirthMonth( int month) {
        this.birthMonth = month;
    }
    
    /**
     * 
     * @return 
     */
    public int getBirthYear() {
        return this.birthYear;
    }
    
    /**
     * 
     * @param year 
     */
    public void setBirthYear( int year) {
        this.birthYear = year;
    }
    
    /**
     * 
     * @return 
     */
    public String getCoachEmail() {
        return this.coachEmail;
    }
    
    /**
     * 
     * @param email 
     */
    public void setCoachEmail( String email) {
        this.coachEmail = email;
    }
    
    /**
     * 
     * @return 
     */
    public int getCoachEmailSource() {
        return coachEmailSource;
    }
    
    /**
     * 
     * @param source 
     */
    public void setCoachEmailSource(int source) {
        coachEmailSource = source;
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
    
    public boolean isCoppaEligible() {
        return this.isCoppaEligible(this.birthMonth, this.birthYear);
    }
    
    // Return true if the user is less than 13 years old
    public boolean isCoppaEligible(int month, int year) {
        // 156 months is equivelent to 13 years
        int cap = 156;
        
        // Calculate the user's age as a number of months since 0 AD
        int user_age = (year * 12) + month;
        
        // Calculate the current number of months since 0 AD
        int current_month = Calendar.getInstance().get(Calendar.MONTH);
        int current_year = Calendar.getInstance().get(Calendar.YEAR);
        int current_cap = (current_year * 12) + current_month;
        
        // If the difference is at or under the cap, COPPA rules apply
        return (current_cap - user_age) <= cap;
    }

}
