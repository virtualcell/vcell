package org.vcell.restq.handlers;

import cbit.vcell.modeldb.ApiAccessToken;
import cbit.vcell.modeldb.UserIdentity;
import com.google.gson.Gson;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.NoCache;
import org.vcell.api.common.AccessTokenRepresentation;
import org.vcell.restq.db.UserRestDB;
import org.vcell.util.DataAccessException;

import java.math.BigDecimal;
import java.sql.SQLException;

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
        try{
            return User.fromSecurityIdentity(securityIdentity);
        }
        catch (Exception e){
            return new User(e.getMessage(), null, null, null);
        }
    }

    @POST
    @Path("/mapUser")
    @RolesAllowed("user")
    @Operation(operationId = "setVCellIdentity", summary = "set or replace vcell identity mapping")
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean mapUser(MapUser mapUser) throws SQLException, DataAccessException {
        return userRestDB.mapUserIdentity(securityIdentity, mapUser);
    }

    @GET
    @Path("/getIdentity")
    @Operation(operationId = "getVCellIdentity", summary = "Get mapped VCell identity")
    @RolesAllowed("user")
    public UserIdentityJSONSafe getIdentity() throws SQLException, DataAccessException {
        UserIdentity userIdentity = userRestDB.getUserIdentity(securityIdentity);
        if(userIdentity == null){
            return new UserIdentityJSONSafe(null, null, null, null);
        }
        return UserIdentityJSONSafe.fromUserIdentity(userIdentity);
    }

    @POST
    @Path("/bearerToken")
//    @RolesAllowed("user")
    @Operation(operationId = "getLegacyApiToken", summary = "Get token for legacy API")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    // Not using user PASSWD because they should already be authenticated with OIDC
    public AccesTokenRepresentationRecord generateBearerToken(@FormParam("user_id") String userID, @FormParam("user_password") String passwd, @FormParam("client_id") String client_id) throws SQLException, DataAccessException {
        if(securityIdentity.isAnonymous()){
            return new AccesTokenRepresentationRecord(null, 0, 0, null, null);
        }
        org.vcell.util.document.User vcellUser = userRestDB.getUserFromIdentity(securityIdentity);
        if(vcellUser == null || !vcellUser.getID().toString().equals(userID)){
            return new AccesTokenRepresentationRecord(null, 0, 0, null, null);
        }
//        UserIdentity identityUser = new UserIdentity(null, new org.vcell.util.document.User("vcellNagios", new KeyValue("3")), null, null);
        ApiAccessToken apiAccessToken = userRestDB.generateApiAccessToken(userRestDB.getAPIClient().getKey(), vcellUser);
//        AccessTokenRepresentation tokenRep = new AccessTokenRepresentation(apiAccessToken.getToken());
//        Gson gson = new Gson();
//        return gson.toJson(tokenRep);
        return AccesTokenRepresentationRecord.getRecordFromAccessTokenRepresentation(new AccessTokenRepresentation(apiAccessToken.getToken()));
    }

    public record AccesTokenRepresentationRecord(
            String token,
            long creationDateSeconds,
            long expireDateSeconds,
            String userId,
            String userKey
    ){
        public static AccesTokenRepresentationRecord getRecordFromAccessTokenRepresentation(AccessTokenRepresentation atr){
            return new AccesTokenRepresentationRecord(atr.token, atr.creationDateSeconds, atr.expireDateSeconds, atr.userId, atr.userKey);
        }
    }

    public record MapUser(
            String userID,
            String password){
        public static MapUser getRecordFromString(String jsonString){
            Gson gson = new Gson();
            return gson.fromJson(jsonString, MapUser.class);
        }
    }

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
