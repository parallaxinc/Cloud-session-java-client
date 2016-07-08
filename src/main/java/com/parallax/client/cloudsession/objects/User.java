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

    public User() {
    }

    public User(Long id, String email, String locale, String authenticationSource) {
        this.id = id;
        this.email = email;
        this.locale = locale;
        this.authenticationSource = authenticationSource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getScreenname() {
        return screenname;
    }

    public void setScreenname(String screenname) {
        this.screenname = screenname;
    }

    public String getAuthenticationSource() {
        return authenticationSource;
    }

    public void setAuthenticationSource(String authenticationSource) {
        this.authenticationSource = authenticationSource;
    }

}
