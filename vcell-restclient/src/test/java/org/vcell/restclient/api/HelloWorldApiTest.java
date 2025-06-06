/*
 * VCell API
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

import org.vcell.restclient.ApiException;
import org.vcell.restclient.model.HelloWorldMessage;
import org.vcell.restclient.model.VCellHTTPError;
import org.junit.Test;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * API tests for HelloWorldApi
 */
@Ignore
public class HelloWorldApiTest {

    private final HelloWorldApi api = new HelloWorldApi();

    
    /**
     * Get hello world message.
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getHelloWorldTest() throws ApiException {
        HelloWorldMessage response = 
        api.getHelloWorld();
        
        // TODO: test validations
    }
    
}
