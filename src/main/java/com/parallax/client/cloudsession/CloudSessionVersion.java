/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession;

/**
 * Maintain application version number here
 * 
 * @author Jim Ewald
 * 
 */
public class CloudSessionVersion {
    
    static final String Version = "1.2.1";
    
}

/*
 * Revision history
 *
 * 1.2.1    All calls to REST services now check the HTTP response code prior
 *          to evaluating any data returned in the body of the response.
 *
 *          Upgraded the Gson package to version 2.8.5.
*/