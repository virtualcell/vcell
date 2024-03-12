package org.vcell.restq.auth;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.JsonArray;
import jakarta.json.JsonString;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class CustomSecurityIdentityAugmentor implements SecurityIdentityAugmentor {

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        if (identity.isAnonymous()) {
            return Uni.createFrom().item(identity);
        }
        JsonWebToken jwt = (JsonWebToken) identity.getPrincipal();
        // create a new builder and copy principal, attributes, credentials and roles from the original identity
        QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder(identity);
        Set<String> claimNames = jwt.getClaimNames();
        System.out.println("claimNames = " + claimNames);
        builder.addRoles(getRolesFromToken(jwt));
        builder.addRole("user");
        builder.addAttribute("rawtoken", jwt.getRawToken());
        builder.addAttribute("permissions", getPermissionsFromToken(jwt));
        return Uni.createFrom().item(builder.build());
    }

    private Set<String> getRolesFromToken(JsonWebToken jwt) {
        if (jwt.containsClaim("vcellapi.cam.uchc.edu/roles")) {
            return getRolesFromAuth0Token(jwt);
        } else {
            return getRolesFromKeycloakToken(jwt);
        }
    }

    private Set<String> getRolesFromAuth0Token(JsonWebToken jwt) {
        return ((JsonArray) jwt.getClaim("vcellapi.cam.uchc.edu/roles"))
                .getValuesAs(JsonString.class).stream().map(JsonString::getString).collect(Collectors.toSet());
    }

    private Set<String> getRolesFromKeycloakToken(JsonWebToken jwt) {
        return new HashSet<>();
//        return ((JsonArray) jwt.getClaim("roles"))
//                .getValuesAs(JsonString.class).stream().map(JsonString::getString).collect(Collectors.toSet());
    }
    private Set<String> getPermissionsFromToken(JsonWebToken jwt) {
        if (jwt.containsClaim("permissions")) {
            return getPermissionsFromAuth0Token(jwt);
        } else {
            return getPermissionsFromKeycloakToken(jwt);
        }
    }

    private Set<String> getPermissionsFromAuth0Token(JsonWebToken jwt) {
        return ((JsonArray) jwt.getClaim("permissions"))
                .getValuesAs(JsonString.class).stream().map(JsonString::getString).collect(Collectors.toSet());
    }

    private Set<String> getPermissionsFromKeycloakToken(JsonWebToken jwt) {
        return new HashSet<>();
//        return ((JsonArray) jwt.getClaim("permissions"))
//                .getValuesAs(JsonString.class).stream().map(JsonString::getString).collect(Collectors.toSet());
    }

}
