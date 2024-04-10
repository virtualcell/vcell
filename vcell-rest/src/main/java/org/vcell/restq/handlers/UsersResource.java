package org.vcell.restq.handlers;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.jboss.resteasy.reactive.NoCache;

@Path("/api/v1/users")
public class UsersResource {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("/me")
    @RolesAllowed("user")
//    @SecurityRequirement(name = "openId", scopes = {"roles"})
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public User me() {
        return User.fromSecurityIdentity(securityIdentity);
    }

    public record User(
            String principal_name,
            String[] roles,
            String[] attributes,
            String[] credentials
    ) {
        static User fromSecurityIdentity(SecurityIdentity securityIdentity) {
            return new User(
                    securityIdentity.getPrincipal().getName(),
                    securityIdentity.getRoles().toArray(String[]::new),
                    securityIdentity.getAttributes().keySet().stream()
                            .map(key -> key + "=" + securityIdentity.getAttribute(key))
                            .toList()
                            .toArray(String[]::new),
                    securityIdentity.getCredentials().stream()
                            .map(credential -> credential.toString())
                            .toList()
                            .toArray(String[]::new)
            );
        }
    }
}
