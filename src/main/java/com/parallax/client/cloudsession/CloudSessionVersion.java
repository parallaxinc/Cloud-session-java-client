/*
 * Copyright (c) 2019 Parallax Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.parallax.client.cloudsession;

/**
 * Maintain application version number here
 * 
 * @author Jim Ewald
 * 
 */
public class CloudSessionVersion {
    
    static final String Version = "1.2.2";
    
}

/*
 * Revision history
 *
 * 1.3.1    Cleaned up method to get a user profile that seemed to report a
 *          nonsense result if the Cloud Session server was unable to
 *          connect to the backend database, and sent an unexpected message.
 *
 * 1.3.0    Released to align with BlocklyProp server
 *
 * 1.2.1    All calls to REST services now check the HTTP response code prior
 *          to evaluating any data returned in the body of the response.
 *
 *          Upgraded the Gson package to version 2.8.5.
*/