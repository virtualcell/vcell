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
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.NoCache;
import org.vcell.restq.auth.CustomSecurityIdentityAugmentor;
import org.vcell.restq.db.UserRestDB;
import org.vcell.util.DataAccessException;
import org.vcell.util.UseridIDExistsException;

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
    public UsersResource(UserRestDB userRestDB) {
        this.userRestDB = userRestDB;
    }

    @GET
    @Path("/me")
//    @RolesAllowed("user")
//    @SecurityRequirement(name = "openId", scopes = {"roles"})
    @Operation(operationId = "getMe", summary = "Get current user")
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public Identity me() {
        if (securityIdentity.isAnonymous()){
            return new Identity("anonymous", null, null, null);
        }
        return Identity.fromSecurityIdentity(securityIdentity);
    }

    @POST
    @Path("/mapUser")
    @RolesAllowed("user")
    @Operation(operationId = "mapUser", summary = "map vcell user")
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean mapUser(UserLoginInfoForMapping mapUser) throws DataAccessException {
        return userRestDB.mapUserIdentity(securityIdentity, mapUser);
    }

    @POST
    @Path("/newUser")
    @RolesAllowed("user")
    @Operation(operationId = "mapNewUser", summary = "create vcell user")
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Successful, returning the identity"),
            @APIResponse(responseCode = "409", description = "VCell Identity not mapped, userid already exists")
    })
    public void mapNewUser(UserRegistrationInfo userRegistrationInfo) {
        JsonWebToken jwt = CustomSecurityIdentityAugmentor.getJsonWebToken(securityIdentity);
        if (jwt == null) {
            throw new WebApplicationException("securityIdentity is missing jwt", Response.Status.INTERNAL_SERVER_ERROR);
        }
        String subject = jwt.getSubject();
        String issuer = jwt.getIssuer();
        String email = jwt.getClaim("email");
        String name = jwt.getClaim("name");
        if (subject == null || issuer == null) {
            throw new WebApplicationException("securityIdentity is missing subject or issuer", Response.Status.INTERNAL_SERVER_ERROR);
        }
        try {
            userRestDB.createUserIdentity(subject, issuer, email, name, userRegistrationInfo);
        } catch (UseridIDExistsException e) {
            throw new WebApplicationException("userid already used", Response.Status.CONFLICT);
        } catch (DataAccessException e) {
            throw new WebApplicationException("database error while creating user identity: "+e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @PUT
    @Path("/unmapUser/{userName}")
    @RolesAllowed("user")
    @Operation(operationId = "unmapUser", summary = "remove vcell identity mapping")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean unmapUser(String userName) throws DataAccessException {
        return userRestDB.unmapUserIdentity(securityIdentity, userName);
    }

    @GET
    @Path("/mappedUser")
    @Operation(operationId = "getMappedUser", summary = "Get mapped VCell identity")
    @RolesAllowed("user")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Successful, returning the identity"),
            @APIResponse(responseCode = "404", description = "Identity not found")
    })
    @Produces(MediaType.APPLICATION_JSON)
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
    @Produces(MediaType.APPLICATION_JSON)
    // Not using user PASSWD because they should already be authenticated with OIDC
    public AccesTokenRepresentationRecord generateBearerToken() throws DataAccessException {
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

    @POST
    @Path("/forgotLegacyPassword")
    @Operation(operationId = "forgotLegacyPassword", summary = "The end user has forgotten the legacy password they used for VCell, so they will be emailed it.")
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Legacy password sent in email"),
            @APIResponse(responseCode = "401", description = "Need to login to Auth0"),
            @APIResponse(responseCode = "500", description = "Internal Error")
    })
    public void forgotLegacyPassword(@QueryParam("userID") String userID) throws DataAccessException {
        if(securityIdentity.isAnonymous()){
            throw new WebApplicationException("securityIdentity is missing jwt", Response.Status.UNAUTHORIZED);
        }
        try {
            userRestDB.sendOldLegacyPassword(userID);
        } catch (SQLException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
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

    public record UserRegistrationInfo(
            String userID,
            String title,
            String organization,
            String country,
            Boolean emailNotification
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

    public record Identity(
            String principal_name,
            String[] roles,
            String[] attributes,
            String[] credentials
    ) {
        static Identity fromSecurityIdentity(SecurityIdentity securityIdentity) {
            return new Identity(
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
