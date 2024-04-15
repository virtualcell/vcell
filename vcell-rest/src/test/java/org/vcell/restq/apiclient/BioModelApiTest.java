package org.vcell.restq.apiclient;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.api.BioModelResourceApi;
import org.vcell.restclient.model.BioModel;

import java.io.IOException;

@QuarkusIntegrationTest
public class BioModelApiTest {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;
    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    //TODO: Add endpoint that retrieves all BM affiliated with user, then use that for testing
    @Test
    public void testAddRemove() throws ApiException, IOException {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setScheme("http");
        defaultClient.setHost("localhost");
        defaultClient.setPort(testPort);
        BioModelResourceApi bioModelResourceApi = new BioModelResourceApi(defaultClient);

        String vcmlString = IOUtils.toString(getClass().getResourceAsStream("/TestVCML.vcml"));

        String bioModelID = bioModelResourceApi.uploadBioModel(vcmlString);
        BioModel bioModel = bioModelResourceApi.getBiomodelById(bioModelID);

        bioModelResourceApi.deleteBioModel(bioModelID);


    }
}
