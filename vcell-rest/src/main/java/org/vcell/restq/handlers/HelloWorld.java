package org.vcell.restq.handlers;

import io.quarkus.logging.Log;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.vcell.restq.models.HelloWorldMessage;
import org.vcell.util.PermissionException;

@Path("/api/helloworld")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorld {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Operation(operationId = "getHelloWorld", summary = "Get hello world message.")
    @Produces(MediaType.APPLICATION_JSON)
    public HelloWorldMessage get_HelloWorld() {
        try {
            return new HelloWorldMessage("Hello Security Roles: " + securityIdentity.getRoles());
        } catch (PermissionException ee) {
            Log.error(ee);
            throw new RuntimeException("not authorized", ee);
        }
    }
}