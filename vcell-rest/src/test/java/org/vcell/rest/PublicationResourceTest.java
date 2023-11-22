package org.vcell.rest;

import cbit.vcell.modeldb.DatabaseServerImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vcell.rest.auth.AuthUtils;
import org.vcell.rest.db.PublicationService;
import org.vcell.rest.models.BiomodelRef;
import org.vcell.rest.models.MathmodelRef;
import org.vcell.rest.models.Publication;
import org.vcell.util.DataAccessException;

import java.sql.SQLException;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

@QuarkusTest
public class PublicationResourceTest {

    @Inject
    public ObjectMapper objectMapper;

    @Inject
    PublicationService publicationService;

    @Test
    public void testAddListRemove() throws JsonProcessingException, SQLException, DataAccessException {
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
                .when().get("/publications")
                .then()
                .statusCode(200)
                .body("$.size()", is(0));

        // insert publication1
        given()
                .body(publication1_json)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .post("/publications")
                .then()
                .statusCode(200);

        Publication[] publications = publicationService.getPublications(DatabaseServerImpl.OrderBy.year_desc, AuthUtils.PUBLICATION_USER);
        Assertions.assertEquals(1, publications.length);
        String pubKey_json = objectMapper.writeValueAsString(publications[0].pubKey());

        // list publications, should return list with publication1
        given()
                .when().get("/publications")
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

        // remove publication1
        given()
                .body(pubKey_json)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .delete("/publications")
                .then()
                .statusCode(204);

        // verify that list publications is empty
        given()
                .when().get("/publications")
                .then()
                .statusCode(200)
                .body("$.size()", is(0));
    }
}