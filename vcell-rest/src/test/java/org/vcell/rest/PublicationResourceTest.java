package org.vcell.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

@QuarkusTest
public class PublicationResourceTest {

    @Test
    public void testList() {
        given()
                .when().get("/publications")
                .then()
                .statusCode(200)
                .body("$.size()", is(2),
                        "name", containsInAnyOrder("publication 1", "publication 2"),
                        "description", containsInAnyOrder("first publication", "second publication"));
    }

    @Test
    public void testAdd() {
        given()
                .body("{\"name\": \"publication 3\", \"description\": \"third publication\"}")
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .post("/publications")
                .then()
                .statusCode(200)
                .body("$.size()", is(3),
                        "name", containsInAnyOrder("publication 1", "publication 2", "publication 3"),
                        "description", containsInAnyOrder("first publication", "second publication", "third publication"));

        given()
                .body("{\"name\": \"publication 3\", \"description\": \"third publication\"}")
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .delete("/publications")
                .then()
                .statusCode(200)
                .body("$.size()", is(2),
                        "name", containsInAnyOrder("publication 1", "publication 2"),
                        "description", containsInAnyOrder("first publication", "second publication"));
    }
}