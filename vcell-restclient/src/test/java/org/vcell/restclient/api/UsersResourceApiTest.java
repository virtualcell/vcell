/*
 * VCell API (development)
 * VCell API
 *
 * The version of the OpenAPI document: 1.0.1
 * Contact: vcell_support@uchc.com
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.vcell.restclient.api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.model.Identity;
import org.vcell.restclient.model.User;


/**
 * API tests for UsersResourceApi
 */
@Disabled
@Tag("Fast")
public class UsersResourceApiTest {

    private final UsersResourceApi api = new UsersResourceApi();

    
    /**
     * 
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getMeTest() throws ApiException {
        Identity response =
        api.getMe();
        
        // TODO: test validations
    }
    
}
