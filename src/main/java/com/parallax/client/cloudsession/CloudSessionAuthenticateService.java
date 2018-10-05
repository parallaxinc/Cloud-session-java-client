/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession;

import com.parallax.client.cloudsession.exceptions.EmailNotConfirmedException;
import com.parallax.client.cloudsession.exceptions.InsufficientBucketTokensException;
import com.parallax.client.cloudsession.exceptions.ServerException;
import com.parallax.client.cloudsession.exceptions.UnknownUserException;
import com.parallax.client.cloudsession.exceptions.UserBlockedException;
import com.parallax.client.cloudsession.exceptions.WrongAuthenticationSourceException;
import com.parallax.client.cloudsession.objects.User;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Authenticate user login
 *
 * Provides an interface to the Could-Session server for user authentication
 * services. The methods in this class use custom exceptions to indicate 
 * a situation where a program fault has been detected.
 * 
 * @author Michel
 * 
 */
public class CloudSessionAuthenticateService {

    /**
     * Handle for any logging activity
     */
    private final Logger LOG = LoggerFactory.getLogger(CloudSessionAuthenticateService.class);

    /**
     * Base URL use to obtain authentication service.
     */
    private final String BASE_URL;

    /**
     * Host name
     */
    private final String SERVER;

    /**
     * Class constructor
     *
     * @param server  The cloud session host name
     * @param baseUrl The cloud session URL as defined in the
     *                blocklyprop.properties file
     */
    public CloudSessionAuthenticateService(String server, String baseUrl) {
        this.SERVER = server;
        this.BASE_URL = baseUrl;
    }

    /**
     * Authenticate user from local authentication database
     *
     * @param login    Account user name
     * @param password Unencrypted account password
     * @return Returns a User object that represents the user profile for the authenticated account.
     * If the authentication was unsuccessful, one of a number of exceptions will be thrown.
     *
     * @throws UnknownUserException               This exception is thrown when the authentication provider is unable to identify the account.
     * @throws UserBlockedException               This exception is thrown when the user account is in a BLOCKED state.
     * @throws EmailNotConfirmedException         This exception is thrown when the user account has been created but the email address
     *                                            used in the account registration has not been verified.
     * @throws InsufficientBucketTokensException  This exception is thrown when the user has tried and failed a pre-determined number of times
     *                                            to either log in to an account or requested confirmation emails.
     * @throws WrongAuthenticationSourceException This exception occurs when the referenced user account is secured through a third-party
     *                                            OAUTH provider and the an attempt is being made to authenticate through the local user
     *                                            authentication mechanism.
     * @throws ServerException                    This exception is thrown when an error was detected either in contacting the Cloud Session
     *                                            service or that Cloud Session service reported a protocol error.
     */
    public User authenticateLocalUser(String login, String password)
            throws
            UnknownUserException,
            UserBlockedException,
            EmailNotConfirmedException,
            InsufficientBucketTokensException,
            WrongAuthenticationSourceException,
            ServerException {

        LOG.debug("Attempting to authenticate user: {}", login);

        try {
            Map<String, String> data = new HashMap<>();
            data.put("email", login);
            data.put("password", password);

            HttpRequest httpRequest;

            // Place a request to the cloud session service. Trap any response
            // that indicates an error
            try {
                httpRequest = HttpRequest
                        .post(getUrl("/authenticate/local"))
                        .header("server", SERVER)
                        .form(data);

                if (!httpRequest.ok()) {
                    // Cloud session server is unavailable or broken
                    LOG.error("Auth server reported error {}", httpRequest.code());
                    throw new ServerException("The authentication server reported error code '" + httpRequest.code() + "'.");
                }

            } catch (HttpRequest.HttpRequestException hex) {
                LOG.warn("REST service request failed. The reported error is: {}", hex.getMessage());
                throw new ServerException(hex);
            }

            // Convert response from login attempt
            String response = httpRequest.body();
            LOG.debug("Received a response: {}", response);

            JsonElement jelement = new JsonParser().parse(response);
            JsonObject responseObject = jelement.getAsJsonObject();

            if (responseObject.get("success").getAsBoolean()) {
                // Create and return a user object
                JsonObject userJson = responseObject.get("user").getAsJsonObject();
                User user = new User();

                user.setId(userJson.get("id").getAsLong());
                user.setEmail(userJson.get("email").getAsString());
                user.setLocale(userJson.get("locale").getAsString());
                user.setScreenname(userJson.get("screenname").getAsString());
                user.setAuthenticationSource(userJson.get("authentication-source").getAsString());

                // These dates might be zero for grandfathered accounts
                user.setBirthMonth(userJson.get("bdmonth").getAsInt());
                user.setBirthYear(userJson.get("bdyear").getAsInt());

                // This gets stored as a Null if the sponsor address is not supplied
                if (userJson.get("parent-email").isJsonNull()) {
                    user.setCoachEmail("");
                } else {
                    user.setCoachEmail(userJson.get("parent-email").getAsString());
                }

                // Set the field that identifies the address as a parent or an instructor
                user.setCoachEmailSource(userJson.get("parent-email-source").getAsInt());

                return user;
            } else {
                // Authentication failed. Obtain result code
                String message = responseObject.get("message").getAsString();

                switch (responseObject.get("code").getAsInt()) {
                    case 400:
                        throw new UnknownUserException(login, message);

                    case 410:
                        // Wrong password, but we should report it as an
                        // unknown username OR password to increase ambiguity
                        throw new UnknownUserException(login, message);

                    case 420:
                        throw new UserBlockedException(message);

                    case 430:
                        throw new EmailNotConfirmedException(message);

                    case 470:
                        throw new InsufficientBucketTokensException();

                    case 480:
                        String authenticationSource = responseObject.get("data").getAsString();
                        throw new WrongAuthenticationSourceException(authenticationSource);

                    default:
                        throw new ServerException("Error " + responseObject.get("code").getAsString());
                }
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);

        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntax error", jse);
            throw new ServerException(jse);
        }
    }

    /**
     * Prepend the base url to the action url
     *
     * @param actionUrl
     * The uti to append to the base url.
     *
     * @return
     * Returns a CharSequence suitable for use in the HttpRequest calls that require a url
     */
    private CharSequence getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }
}
