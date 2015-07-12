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
public class UnknownUserIdException extends Exception {

    private Long idUser;
    private static final String DEFAULT_MESSAGE = "Unknown user";

    public UnknownUserIdException() {
        super(DEFAULT_MESSAGE);
    }

    public UnknownUserIdException(String message) {
        super(message);
    }

    public UnknownUserIdException(Long idUser) {
        super(DEFAULT_MESSAGE);
        this.idUser = idUser;
    }

    public UnknownUserIdException(Long idUser, String message) {
        super(message);
        this.idUser = idUser;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

}
