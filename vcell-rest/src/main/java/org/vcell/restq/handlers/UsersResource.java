package org.vcell.restq.handlers;

import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.jboss.resteasy.reactive.NoCache;

import java.util.stream.Collectors;

@Path("/api/users")
public class UsersResource {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("/me")
    @SecurityRequirement(name = "openId", scopes = {"roles"})
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public User me() {
        return User.fromSecurityIdentity(securityIdentity, (JsonWebToken) securityIdentity.getPrincipal());
    }

    public record SecurityIdentityAttributes(
            String configuration_metadata,
            String authenticationRequestContext,
            String routingContext,
            String identityExpireTime,
            String tenantId
    ) {}

    public record User(
            String securityIdentity_PrincipalName,
            String[] securityIdentity_Roles,
            String[] securityIdentity_Attributes,
            String[] securityIdentity_Credentials,
            SecurityIdentityAttributes securityIdentityAttributes,
            String idToken_subject,
            String idToken_issuer,
            String idToken_name,
            String idToken_claims,
            String idToken_groups,
            String idToken_audience,
            String idToken_idTokenRaw
    ) {
        static User fromSecurityIdentity(SecurityIdentity securityIdentity, JsonWebToken idToken) {
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
                            .toArray(String[]::new),
                    new SecurityIdentityAttributes(
                            securityIdentity.getAttribute("configuration-metadata").toString(),
                            securityIdentity.getAttribute("io.quarkus.security.identity.AuthenticationRequestContext").toString(),
                            securityIdentity.getAttribute("io.vertx.ext.web.RoutingContext").toString(),
                            securityIdentity.getAttribute("quarkus.identity.expire-time").toString(),
                            securityIdentity.getAttribute("tenant-id").toString()
                    ),
                    idToken.getSubject(),
                    idToken.getIssuer(),
                    idToken.getName(),
                    (idToken.getClaimNames()==null) ? null :
                            idToken.getClaimNames().stream()
                            .map(key -> key + "=" + idToken.getClaim(key))
                            .collect(Collectors.joining(", ")),
                    (idToken.getGroups()==null) ? null :
                            String.join(", ", idToken.getGroups()),
                    (idToken.getAudience()==null) ? null :
                            String.join(", ", idToken.getAudience()),
                    idToken.getRawToken());
        }
    }
}
