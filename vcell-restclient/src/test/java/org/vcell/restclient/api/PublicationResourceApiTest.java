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
import org.vcell.restclient.model.Publication;

import java.util.List;


/**
 * API tests for PublicationResourceApi
 */
@Disabled
@Tag("Fast")
public class PublicationResourceApiTest {

    private final PublicationResourceApi api = new PublicationResourceApi();

    
    @Test
    public void getPublicationByIdTest() throws ApiException {
        Long key = null;
        api.getPublicationById(key);
        
        // TODO: test validations
    }
    
    @Test
    public void getPublicationsTest() throws ApiException {
        List<Publication> response = api.getPublications();
        
        // TODO: test validations
    }

    @Test
    public void addPublicationTest() throws ApiException {
        Publication publication = null;
        api.createPublication(publication);

        // TODO: test validations
    }

    @Test
    public void deletePublicationTest() throws ApiException {
        Long publication_key = null;
        api.deletePublication(publication_key);

        // TODO: test validations
    }

}