/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession.exceptions;

/**
 * Exception generated when the system is unable to find a matching user ID record
 * 
 * @author Michel
 */
public class UnknownUserIdException extends Exception {

    private Long idUser;
    private static final String DEFAULT_MESSAGE = "Unknown user";

    /**
     * Exception using the default "Unknown user" message.
     * 
     */
    public UnknownUserIdException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     *
     * @param message
     */
    public UnknownUserIdException(String message) {
        super(message);
    }

    /**
     *
     * @param idUser
     */
    public UnknownUserIdException(Long idUser) {
        super(DEFAULT_MESSAGE);
        this.idUser = idUser;
    }

    /**
     *
     * @param idUser
     * @param message
     */
    public UnknownUserIdException(Long idUser, String message) {
        super(message);
        this.idUser = idUser;
    }

    /**
     *
     * @return
     */
    public Long getIdUser() {
        return idUser;
    }

    /**
     *
     * @param idUser
     */
    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

}
