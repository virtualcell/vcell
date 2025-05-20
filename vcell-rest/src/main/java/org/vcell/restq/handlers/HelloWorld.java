package org.vcell.restq.handlers;

import io.quarkus.logging.Log;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.vcell.restq.errors.exceptions.PermissionWebException;
import org.vcell.restq.models.HelloWorldMessage;
import org.vcell.util.PermissionException;

@Path("/api/v1/helloworld")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorld {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Operation(operationId = "getHelloWorld", summary = "Get hello world message.")
    @Produces(MediaType.APPLICATION_JSON)
    public HelloWorldMessage get_HelloWorld() throws PermissionWebException {
        try {
            return new HelloWorldMessage("Hello Security Roles: " + securityIdentity.getRoles());
        } catch (PermissionException ee) {
            throw new PermissionWebException("not authorized", ee);
        }
    }
}