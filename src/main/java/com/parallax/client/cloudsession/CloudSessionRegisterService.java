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
import com.parallax.client.cloudsession.exceptions.NonUniqueEmailException;
import com.parallax.client.cloudsession.exceptions.PasswordComplexityException;
import com.parallax.client.cloudsession.exceptions.PasswordVerifyException;
import com.parallax.client.cloudsession.exceptions.ScreennameUsedException;
import com.parallax.client.cloudsession.exceptions.ServerException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local user account registration services
 *
 * @author Michel
 */
public class CloudSessionRegisterService {

    /**
     *  System logger 
     */
    private final Logger LOG = LoggerFactory.getLogger(CloudSessionRegisterService.class);

    
    /**
     * Base URL use to obtain authentication service.
     */
    private final String BASE_URL;
    
    
    /**
     * Host name
     */
    private final String SERVER;
    
    
    // REST endpoint URI constants
    private final String URI_REGISTER_USER = "/user/register";


    
    /**
     * Set the REST host server and base REST URI
     * 
     * @param server The cloud session host name
     * @param baseUrl The cloud session URL as defined in the 
     */
    public CloudSessionRegisterService(String server, String baseUrl) {
        this.SERVER = server;
        this.BASE_URL = baseUrl;
    }
    

    /**
     * Create a new user account in the CloudSession database
     * 
     * @param email
     * @param password
     * @param passwordConfirm
     * @param locale
     * @param screenname
     * @param birthMonth
     * @param birthYear
     * @param coachEmail
     * @param coachEmailSource
     * 
     * @return New user Cloud Session user ID or zero if account creation has failed
     * 
     * @throws NonUniqueEmailException
     * @throws PasswordVerifyException
     * @throws PasswordComplexityException
     * @throws ScreennameUsedException
     * @throws ServerException 
     */
    public Long registerUser(
            String email, 
            String password, 
            String passwordConfirm, 
            String locale, 
            String screenname,
            int birthMonth,
            int birthYear,
            String coachEmail,
            int coachEmailSource) throws
                NonUniqueEmailException, 
                PasswordVerifyException, 
                PasswordComplexityException, 
                ScreennameUsedException, 
                ServerException {
            
        try {
            Map<String, String> data = new HashMap<>();
            
            data.put("email", email);
            data.put("password", password);
            data.put("password-confirm", passwordConfirm);
            data.put("locale", locale);
            data.put("screenname", screenname);
            data.put("bdmonth", Integer.toString(birthMonth));
            data.put("bdyear", Integer.toString(birthYear));
            data.put("parent-email", coachEmail);
            data.put("parent-email-source", Integer.toString(coachEmailSource));
            
            // Post the new user request to the cloud session server REST API
            HttpRequest request = HttpRequest
                    .post(getUrl(URI_REGISTER_USER))
                    .header("server", SERVER)
                    .form(data);
            
            // Response from the Cloud Session server
            String response = request.body();
            if (response == null) {
                throw new ServerException("No response from server.");
            }

            LOG.debug("registerUser returns:{}",response );
            
            JsonElement jelement = new JsonParser().parse(response);
            JsonObject responseObject = jelement.getAsJsonObject();

            if (responseObject.get("success").getAsBoolean()) {
                return responseObject.get("user").getAsLong();
            } else {
                // The call failed. Respond appropriately
                switch (responseObject.get("code").getAsInt()) {
                    case 450:
                        throw new NonUniqueEmailException(
                                responseObject.get("data").getAsString());
                    case 460:
                        throw new PasswordVerifyException();
                    case 490:
                        throw new PasswordComplexityException();
                    case 500:
                        throw new ScreennameUsedException(
                                responseObject.get("data").getAsString());
                }
                LOG.warn("Created new user account but user ID is zero.");
                return 0L;
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntax error", jse);
            throw new ServerException(jse);
        }
    }
        
    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }
}
