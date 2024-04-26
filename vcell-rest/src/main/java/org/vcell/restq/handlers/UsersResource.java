package org.vcell.restq.handlers;

import cbit.vcell.modeldb.ApiAccessToken;
import cbit.vcell.modeldb.UserIdentity;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.NoCache;
import org.vcell.restq.db.UserRestDB;
import org.vcell.util.DataAccessException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@Path("/api/v1/users")
@RequestScoped
public class UsersResource {

    @Inject
    SecurityIdentity securityIdentity;

    private final UserRestDB userRestDB;

    @Inject
    public UsersResource(UserRestDB userRestDB) throws DataAccessException, SQLException {
        this.userRestDB = userRestDB;
    }

    @GET
    @Path("/me")
//    @RolesAllowed("user")
//    @SecurityRequirement(name = "openId", scopes = {"roles"})
    @Operation(operationId = "getMe", summary = "Get current user")
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public User me() {
        if (securityIdentity.isAnonymous()){
            return new User("anonymous", null, null, null);
        }
        return User.fromSecurityIdentity(securityIdentity);
    }

    @POST
    @Path("/mapUser")
    @RolesAllowed("user")
    @Operation(operationId = "setVCellIdentity", summary = "set vcell identity mapping")
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean mapUser(UserLoginInfoForMapping mapUser) throws DataAccessException {
        return userRestDB.mapUserIdentity(securityIdentity, mapUser);
    }

    @PUT
    @Path("/unmapUser/{userName}")
    @RolesAllowed("user")
    @Operation(operationId = "clearVCellIdentity", summary = "remove vcell identity mapping")
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean unmapUser(String userName) throws DataAccessException {
        return userRestDB.unmapUserIdentity(securityIdentity, userName);
    }

    @GET
    @Path("/getIdentity")
    @Operation(operationId = "getVCellIdentity", summary = "Get mapped VCell identity")
    @RolesAllowed("user")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Successful, returning the identity"),
            @APIResponse(responseCode = "404", description = "Identity not found")
    })
    public UserIdentityJSONSafe getIdentity() throws DataAccessException {
        List<UserIdentity> userIdentities = userRestDB.getUserIdentities(securityIdentity);
        if (userIdentities.isEmpty()){
            throw new WebApplicationException("Identity not found", Response.Status.NOT_FOUND);
        } else if (userIdentities.size() > 1){
            throw new WebApplicationException("Multiple identities found for user", Response.Status.INTERNAL_SERVER_ERROR);
        } else {
            return UserIdentityJSONSafe.fromUserIdentity(userIdentities.get(0));
        }
    }

    @POST
    @Path("/bearerToken")
//    @RolesAllowed("user")
    @Operation(operationId = "getLegacyApiToken", summary = "Get token for legacy API")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    // Not using user PASSWD because they should already be authenticated with OIDC
    public AccesTokenRepresentationRecord generateBearerToken() throws SQLException, DataAccessException {
        if(securityIdentity.isAnonymous()){
            return new AccesTokenRepresentationRecord(null, 0, 0, null, null);
        }
        org.vcell.util.document.User vcellUser = userRestDB.getUserFromIdentity(securityIdentity);
        if(vcellUser == null){
            return new AccesTokenRepresentationRecord(null, 0, 0, null, null);
        }
        ApiAccessToken apiAccessToken = userRestDB.generateApiAccessToken(userRestDB.getAPIClient().getKey(), vcellUser);
        return AccesTokenRepresentationRecord.getRecordFromAccessTokenRepresentation(apiAccessToken);
    }

    public record AccesTokenRepresentationRecord(
            String token,
            long creationDateSeconds,
            long expireDateSeconds,
            String userId,
            String userKey
    ){
        public static AccesTokenRepresentationRecord getRecordFromAccessTokenRepresentation(ApiAccessToken atr){
            return new AccesTokenRepresentationRecord(atr.getToken(), atr.getCreationDate().getTime(), atr.getExpiration().getTime(), atr.getUser().getName(), atr.getUser().getID().toString());
        }
    }

    public record UserLoginInfoForMapping(
            String userID,
            String password
    ){ }

    public record UserIdentityJSONSafe(
            String userName,
            BigDecimal id,
            String subject,
            String insertDate
    ){
        static UserIdentityJSONSafe fromUserIdentity(UserIdentity userIdentity){
            return new UserIdentityJSONSafe(userIdentity.user().getName(), userIdentity.id(), userIdentity.subject(), userIdentity.insertDate().toString());
        }
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
