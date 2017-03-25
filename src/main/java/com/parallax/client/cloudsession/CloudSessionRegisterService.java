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
 * Register a local user account
 *
 * @author Michel
 */
public class CloudSessionRegisterService {

    private final Logger LOG = LoggerFactory.getLogger(CloudSessionRegisterService.class);

    /**
     * Base URL use to obtain authentication service.
     */
    private final String BASE_URL;
    
    /**
     * Host name
     */
    private final String SERVER;

    /**
     * Constructor
     * 
     * @param server
     * @param baseUrl 
     */
    public CloudSessionRegisterService(String server, String baseUrl) {
        this.SERVER = server;
        this.BASE_URL = baseUrl;
    }

    /**
     *
     * @param email
     * @param password
     * @param passwordConfirm
     * @param locale
     * @param screenname
     * @return
     * @throws NonUniqueEmailException
     * @throws PasswordVerifyException
     * @throws PasswordComplexityException
     * @throws ScreennameUsedException
     * @throws ServerException
     */
    public Long registerUser(String email, String password, String passwordConfirm, String locale, String screenname) throws NonUniqueEmailException, PasswordVerifyException, PasswordComplexityException, ScreennameUsedException, ServerException {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("email", email);
            data.put("password", password);
            data.put("password-confirm", passwordConfirm);
            data.put("locale", locale);
            data.put("screenname", screenname);
            HttpRequest request = HttpRequest.post(getUrl("/user/register")).header("server", SERVER).form(data);
//        int responseCode = request.code();
//        System.out.println("Response code: " + responseCode);
            String response = request.body();
//        System.out.println(response);
            JsonElement jelement = new JsonParser().parse(response);
            JsonObject responseObject = jelement.getAsJsonObject();
            if (responseObject.get("success").getAsBoolean()) {
                return responseObject.get("user").getAsLong();
            } else {
                switch (responseObject.get("code").getAsInt()) {
                    case 450:
                        throw new NonUniqueEmailException(responseObject.get("data").getAsString());
                    case 460:
                        throw new PasswordVerifyException();
                    case 490:
                        throw new PasswordComplexityException();
                    case 500:
                        throw new ScreennameUsedException(responseObject.get("data").getAsString());
                }
                return null;
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntace service error", jse);
            throw new ServerException(jse);
        }
    }

    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }

}
