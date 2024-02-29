package org.vcell.restq.apiclient;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.api.BioModelResourceApi;
import org.vcell.restclient.model.BioModel;

import java.io.IOException;

//@QuarkusTest
public class BioModelApiTest {

//    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    //TODO: Add endpoint that retrieves all BM affiliated with user, then use that for testing
    @Test
    public void testAddRemove() throws ApiException, IOException {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setHost("localhost");
        defaultClient.setPort(9000);
        BioModelResourceApi bioModelResourceApi = new BioModelResourceApi(defaultClient);

        String vcmlString = IOUtils.toString(getClass().getResourceAsStream("/TestVCML.vcml"));

        String bioModelID = bioModelResourceApi.uploadBioModel(vcmlString);
        BioModel bioModel = bioModelResourceApi.getBiomodelById(bioModelID);

        bioModelResourceApi.deleteBioModel(bioModelID);


    }
}
