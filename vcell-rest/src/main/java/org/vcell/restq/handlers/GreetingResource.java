package org.vcell.restq.handlers;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/api/hello")
public class GreetingResource {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance hello(String name);
    }

    @Inject
    SecurityIdentity identity;

    @GET
    @Operation(hidden=true)
    @Produces(MediaType.TEXT_PLAIN)
    public TemplateInstance hello_web() {
        String userName = "anonymous";
        if (!identity.isAnonymous()) {
            userName = identity.getPrincipal().getName();
        }
        return Templates.hello(userName);
    }

    @GET
    @Operation(hidden=true)
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        String userName = "anonymous";
        if (!identity.isAnonymous()) {
            userName = identity.getPrincipal().getName();
        }
        return "Hello " + userName + "!";
    }
}
