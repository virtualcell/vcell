package org.vcell.restq;

import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.resource.PropertyLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.db.PublicationService;
import org.vcell.restq.models.BiomodelRef;
import org.vcell.restq.models.MathmodelRef;
import org.vcell.restq.models.Publication;
import org.vcell.util.DataAccessException;

import java.sql.SQLException;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

@QuarkusTest
public class PublicationResourceTest {
    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    public ObjectMapper objectMapper;
    @Inject
    PublicationService publicationService;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    private ApiClient aliceAPIClient;
    private ApiClient bobAPIClient;

    @BeforeAll
    public static void setupConfig(){
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void createClients(){
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        bobAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.bob);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
    }

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @Test
    public void testAddListRemove() throws JsonProcessingException, SQLException, DataAccessException, ApiException {
        String pubuser = "alice";
        String nonpubuser = "bob";
        boolean mapped = new UsersResourceApi(aliceAPIClient).mapUser(TestEndpointUtils.administratorUserLoginInfo);
        Assertions.assertTrue(mapped);
        mapped = new UsersResourceApi(bobAPIClient).mapUser(TestEndpointUtils.vcellNagiosUserLoginInfo);
        Assertions.assertTrue(mapped);

        // create a test publication using org.vcell.rest.model.Publication and add it to the list
        Publication publication1 = new Publication(
                null,
                "publication 1",
                new String[]{"author1", "author2"},
                1994,
                "citation",
                "pubmedId",
                "doi",
                0,
                "url",
                0,
                new BiomodelRef[0],
                new MathmodelRef[0],
                new Date());
        // use objectMapper to serialize publication1 to json
        String publication1_json = objectMapper.writeValueAsString(publication1);

        // list publications should be empty initially
        given()
                .when().get("/api/v1/publications")
                .then()
                .statusCode(200)
                .body("$.size()", is(0));

        // insert publication1 as no user
        given()
                .body(publication1_json)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .post("/api/v1/publications")
                .then()
                .statusCode(401);

        // insert publication1 as nonpubuser (doesn't have proper permission)
        given().auth().oauth2(keycloakClient.getAccessToken(nonpubuser))
                .body(publication1_json)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .post("/api/v1/publications")
                .then()
                .statusCode(403);

        // insert publication1 as user pubuser (has proper permission)
        given().auth().oauth2(keycloakClient.getAccessToken(pubuser))
                .body(publication1_json)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .post("/api/v1/publications")
                .then()
                .statusCode(200);

        Publication[] publications = publicationService.getPublications(DatabaseServerImpl.OrderBy.year_desc, Main.DUMMY_USER);
        Assertions.assertEquals(1, publications.length);
        Long pubKey = publications[0].pubKey();

        // list publications, should return list with publication1
        given()
                .when().get("/api/v1/publications")
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .body("[0].title", is("publication 1"))
                .body("[0].authors", containsInAnyOrder("author1", "author2"))
                .body("[0].year", is(1994))
                .body("[0].citation", is("citation"))
                .body("[0].pubmedid", is("pubmedId"))
                .body("[0].doi", is("doi"))
                .body("[0].endnoteid", is(0))
                .body("[0].url", is("url"))
                .body("[0].wittid", is(0));

        // get publication1 as no user
        given()
                .pathParam("id", pubKey)
                .when()
                .get("/api/v1/publications/{id}")
                .then()
                .statusCode(200)
                .body("title", is("publication 1"))
                .body("authors", containsInAnyOrder("author1", "author2"))
                .body("year", is(1994))
                .body("citation", is("citation"))
                .body("pubmedid", is("pubmedId"))
                .body("doi", is("doi"))
                .body("endnoteid", is(0))
                .body("url", is("url"))
                .body("wittid", is(0));

        // remove publication1 as basicuser (doesn't have permission)
        given().auth().oauth2(keycloakClient.getAccessToken(nonpubuser))
                .pathParam("id", pubKey)
                .when()
                .delete("/api/v1/publications/{id}")
                .then()
                .statusCode(403);

        // remove publication1 no user
        given()
                .pathParam("id", pubKey)
                .when()
                .delete("/api/v1/publications/{id}")
                .then()
                .statusCode(401);

        // remove publication1 as pubuser (does have permission)
        given().auth().oauth2(keycloakClient.getAccessToken(pubuser))
                .pathParam("id", pubKey)
                .when()
                .delete("/api/v1/publications/{id}")
                .then()
                .statusCode(204);

        // verify that list publications is empty
        given()
                .when().get("/api/v1/publications")
                .then()
                .statusCode(200)
                .body("$.size()", is(0));
    }
}