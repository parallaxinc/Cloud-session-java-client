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
 * <p>
 * Supported REST endpoints:
 * 
 *      /local/confirm
 *      Activate user account after verifying the user account email and confirmation token.
 * 
 *      /local/confirm/{string_email}
 *      Send account confirmation request email to user.
 * 
 *      /local/reset/{string_email}
 *      Send a password reset email to the user.  
 * 
 *      /local/password/{int_userID}
 *      Reset user account password with details provided in the payload.
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
    
    
    // REST endpoint URI constants
    private final String URI_PASSWORD_RESET = "/local/reset/";
    private final String URI_CONFIRM_ACCOUNT = "/local/confirm";
    private final String URI_PASSWORD_SET = "/local/password/";

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
     * @param token is the GUID issued from the password reset request
     * @param email is the user account email address affected by the password reset
     * @param password is the first copy of the password
     * @param passwordConfirm is the second copy of the password.
     * 
     * @return boolean true on success, otherwise false
     * 
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
            String passwordConfirm) throws 
                    UnknownUserException, 
                    PasswordVerifyException, 
                    PasswordComplexityException, 
                    WrongAuthenticationSourceException, 
                    ServerException {

        try {
            // Create a key-value pair structure to send to the endpoint
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            data.put("password", password);
            data.put("password-confirm", passwordConfirm);
            
            // POST the request
            HttpRequest request = HttpRequest.post(
                    getUrl(URI_PASSWORD_RESET + email)).form(data);

            if (request.ok()) {
                // Get response from Cloud Session server
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
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
            
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntace service error", jse);
            throw new ServerException(jse);
        }
        
        return false;
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
                    getUrl(URI_PASSWORD_RESET + email)).header("server", SERVER);

            if (request.ok()) {
                // Get response from Cloud Session server
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
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
            
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntace service error", jse);
            throw new ServerException(jse);
            
        } catch (NullPointerException npe) {
            LOG.error("Encountered a Null Pointer exception.");
            throw new ServerException(npe);
        }
        
        return false;
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

        String response = null;
        String cloudSessionUri = null;
        
        try {
            Map<String, String> data = new HashMap<>();
            data.put("email", email);
            data.put("token", token);
            
            cloudSessionUri = getUrl(URI_CONFIRM_ACCOUNT);
            
            LOG.info("Requesting from Cloud Session server: '{}'", cloudSessionUri);

            HttpRequest request = HttpRequest.post(cloudSessionUri).form(data);

            if (request.ok()) {
                // Get response from Cloud Session server
                response = request.body();
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
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
            
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntax error", jse.getMessage());
            throw new ServerException(jse);
        }
        
        return false;
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
                    getUrl(URI_CONFIRM_ACCOUNT + "/" + email)).header("server", SERVER);

            if (request.ok()) {
                // Get response from Cloud Session server
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
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        }
        
        return false;
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
                    getUrl(URI_PASSWORD_SET + idUser)).form(data);

            if (request.ok()) {
                // Get response from Cloud Session server
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
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        }
        
        return false;
    }

    // Helper function to build a complete URL
    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }
}
