/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.parallax.client.cloudsession.exceptions.EmailAlreadyConfirmedException;
import com.parallax.client.cloudsession.exceptions.InsufficientBucketTokensException;
import com.parallax.client.cloudsession.exceptions.PasswordComplexityException;
import com.parallax.client.cloudsession.exceptions.PasswordVerifyException;
import com.parallax.client.cloudsession.exceptions.ServerException;
import com.parallax.client.cloudsession.exceptions.UnknownUserException;
import com.parallax.client.cloudsession.exceptions.UnknownUserIdException;
import com.parallax.client.cloudsession.exceptions.WrongAuthenticationSourceException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface to the Cloud Session server
 * 
 * Supported REST endpoints:
 *      /confirm
 *      /confirm/string_email
 *      /reset/string_email
 *      /password/int_userID
 *
 * @author Michel
 */
public class CloudSessionLocalUserService {

    /**
     * Application logging facility
     */
    private final Logger LOG = LoggerFactory.getLogger(CloudSessionLocalUserService.class);
    
    /**
     * The local user services REST base URL
     */
    private final String BASE_URL;
    
    /**
     *  The local user services host address
     */
    private final String SERVER;

    /**
     * Class constructor
     * 
     * @param server Local user services host server address
     * @param baseUrl Local user services REST API base URL
     */
    public CloudSessionLocalUserService(String server, String baseUrl) {
        this.SERVER = server;
        this.BASE_URL = baseUrl;
    }

    /**
     * Reset an account password
     * 
     * @param token
     * @param email
     * @param password
     * @param passwordConfirm
     * @return boolean true on success, otherwise false
     * @throws UnknownUserException
     * @throws PasswordVerifyException
     * @throws PasswordComplexityException
     * @throws WrongAuthenticationSourceException
     * @throws ServerException
     */
    public boolean doPasswordReset(
            String token, 
            String email, 
            String password, 
            String passwordConfirm)
                throws UnknownUserException, 
                       PasswordVerifyException, 
                       PasswordComplexityException, 
                       WrongAuthenticationSourceException, 
                       ServerException {

        try {
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            data.put("password", password);
            data.put("password-confirm", passwordConfirm);
            
            HttpRequest request = HttpRequest.post(
                    getUrl("/local/reset/" + email)).form(data);

            String response = request.body();
            JsonElement jelement = new JsonParser().parse(response);
            JsonObject responseObject = jelement.getAsJsonObject();

            if (responseObject.get("success").getAsBoolean()) {
                return true;
            } 
            else {
                switch (responseObject.get("code").getAsInt()) {
                    case 400:
                        throw new UnknownUserException(
                                responseObject.get("data").getAsString());
                    case 460:
                        throw new PasswordVerifyException();
                    case 490:
                        throw new PasswordComplexityException();
                    case 480:
                        throw new WrongAuthenticationSourceException(
                                responseObject.get("data").getAsString());
                }
                return false;
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
            
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntace service error", jse);
            throw new ServerException(jse);
        }
    }

    /**
     *
     * @param email
     * @return boolean true on success, otherwise false
     * @throws UnknownUserException
     * @throws InsufficientBucketTokensException
     * @throws WrongAuthenticationSourceException
     * @throws ServerException
     */
    public boolean requestPasswordReset(String email) 
            throws UnknownUserException, 
                   InsufficientBucketTokensException, 
                   WrongAuthenticationSourceException, 
                   ServerException {
        
        try {
            HttpRequest request = HttpRequest.get(
                    getUrl("/local/reset/" + email)).header("server", SERVER);

            String response = request.body();
            JsonElement jelement = new JsonParser().parse(response);
            JsonObject responseObject = jelement.getAsJsonObject();

            if (responseObject.get("success").getAsBoolean()) {
                return true;
            } 
            else {
                switch (responseObject.get("code").getAsInt()) {
                    case 400:
                        throw new UnknownUserException(
                                responseObject.get("data").getAsString());
                    case 470:
                        throw new InsufficientBucketTokensException(
                                responseObject.get("message").getAsString(),
                                responseObject.get("data").getAsString());
                    case 480:
                        throw new WrongAuthenticationSourceException(
                                responseObject.get("data").getAsString());
                }
                return false;
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
            
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntace service error", jse);
            throw new ServerException(jse);
        }
    }

    /**
     *
     * @param email
     * @param token
     * @return boolean true on success, otherwise false
     * @throws UnknownUserException
     * @throws WrongAuthenticationSourceException
     * @throws ServerException
     */
    public boolean doConfirm(String email, String token) 
            throws UnknownUserException, 
                   WrongAuthenticationSourceException, 
                   ServerException {
        
        try {
            Map<String, String> data = new HashMap<>();
            data.put("email", email);
            data.put("token", token);
            
            HttpRequest request = HttpRequest.post(
                    getUrl("/local/confirm")).form(data);

            String response = request.body();
            JsonElement jelement = new JsonParser().parse(response);
            JsonObject responseObject = jelement.getAsJsonObject();
            
            if (responseObject.get("success").getAsBoolean()) {
                return true;
            } 
            else {
                switch (responseObject.get("code").getAsInt()) {
                    case 400:
                        throw new UnknownUserException(
                                responseObject.get("data").getAsString());
                    case 480:
                        throw new WrongAuthenticationSourceException(
                                responseObject.get("data").getAsString());
                    case 510:
                        // The submitted token has expired or was not found 
                        return false;
                }
                return false;
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
            
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntace service error", jse);
            throw new ServerException(jse);
        }
    }

    /**
     *
     * @param email
     * @return boolean true on success, otherwise false
     * @throws UnknownUserException
     * @throws InsufficientBucketTokensException
     * @throws EmailAlreadyConfirmedException
     * @throws WrongAuthenticationSourceException
     * @throws ServerException
     */
    public boolean requestNewConfirmEmail(String email) 
            throws UnknownUserException, 
                   InsufficientBucketTokensException, 
                   EmailAlreadyConfirmedException, 
                   WrongAuthenticationSourceException, 
                   ServerException {
        
        try {
            HttpRequest request = HttpRequest.get(
                    getUrl("/local/confirm/" + email)).header("server", SERVER);

            String response = request.body();
            JsonElement jelement = new JsonParser().parse(response);
            JsonObject responseObject = jelement.getAsJsonObject();

            if (responseObject.get("success").getAsBoolean()) {
                return true;
            } 
            else {
                switch (responseObject.get("code").getAsInt()) {
                    case 400:
                        throw new UnknownUserException(
                                responseObject.get("data").getAsString());
                    case 470:
                        throw new InsufficientBucketTokensException(
                                responseObject.get("message").getAsString(), 
                                responseObject.get("data").getAsString());
                    case 480:
                        throw new WrongAuthenticationSourceException(
                                responseObject.get("data").getAsString());
                    case 520:
                        throw new EmailAlreadyConfirmedException(
                                responseObject.get("message").getAsString());
                    case 540:
                        throw new ServerException (
                                responseObject.get("message").getAsString());
                }
                return false;
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        }
    }

    /**
     *
     * @param idUser
     * @param oldPassword
     * @param password
     * @param confirmPassword
     * @return boolean true on success, otherwise false
     * @throws UnknownUserIdException
     * @throws PasswordVerifyException
     * @throws PasswordComplexityException
     * @throws WrongAuthenticationSourceException
     * @throws ServerException
     */
    public boolean changePassword(
            Long idUser, 
            String oldPassword, 
            String password, 
            String confirmPassword) 
                throws UnknownUserIdException, 
                       PasswordVerifyException, 
                       PasswordComplexityException, 
                       WrongAuthenticationSourceException, 
                       ServerException {
        
        try {
            Map<String, String> data = new HashMap<>();
            data.put("old-password", oldPassword);
            data.put("password", password);
            data.put("password-confirm", confirmPassword);
            
            HttpRequest request = HttpRequest.post(
                    getUrl("/local/password/" + idUser)).form(data);

            String response = request.body();
            JsonElement jelement = new JsonParser().parse(response);
            JsonObject responseObject = jelement.getAsJsonObject();

            if (responseObject.get("success").getAsBoolean()) {
                return true;
            } 
            else {
                switch (responseObject.get("code").getAsInt()) {
                    case 400:
                        throw new UnknownUserIdException(
                                responseObject.get("data").getAsString());
                    case 460:
                        throw new PasswordVerifyException();
                    case 480:
                        throw new WrongAuthenticationSourceException(
                                responseObject.get("data").getAsString());
                    case 490:
                        throw new PasswordComplexityException();
                    case 510:
                        // The submitted token has expired or was not found 
                        return false;
                }
                return false;
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        }
    }

    // Helper function to build a complete URL
    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }
}
