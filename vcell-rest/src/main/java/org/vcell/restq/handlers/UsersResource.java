package org.vcell.restq.handlers;

import cbit.vcell.modeldb.ApiAccessToken;
import cbit.vcell.modeldb.UserIdentity;
import cbit.vcell.resource.PropertyLoader;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.NoCache;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;
import org.vcell.auth.JWTUtils;
import org.vcell.restq.auth.CustomSecurityIdentityAugmentor;
import org.vcell.restq.db.UserRestDB;
import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

@Path("/api/v1/users")
@RequestScoped
public class UsersResource {
    private static final Logger LOG = Logger.getLogger(UsersResource.class.getName());

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    UriInfo uriInfo;

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
    @Produces(MediaType.APPLICATION_JSON)
    public boolean mapUser(UserLoginInfoForMapping mapUser) throws DataAccessException {
        return userRestDB.mapUserIdentity(securityIdentity, mapUser);
    }

    @POST
    @Path("/requestRecoveryEmail")
    @RolesAllowed("user")
    @Operation(operationId = "requestRecoveryEmail", summary = "request a recovery email to link a VCell account.")
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponses({
            @APIResponse(responseCode = "200", description = "magic link sent in email if appropriate"),
    })
    public void requestRecoveryEmail(@QueryParam("userID") String userID, @QueryParam("email") String email) {
        if(securityIdentity.isAnonymous()){
            throw new WebApplicationException("securityIdentity is missing jwt", Response.Status.UNAUTHORIZED);
        }
        try {
            JsonWebToken jwt = CustomSecurityIdentityAugmentor.getJsonWebToken(securityIdentity);
            if (jwt == null) {
                throw new DataAccessException("securityIdentity is missing jwt");
            }
            String requestorSubject = jwt.getSubject();
            String requestorIssuer = jwt.getIssuer();
            if (requestorSubject == null) {
                throw new DataAccessException("securityIdentity is missing subject");
            }
            if (requestorIssuer == null) {
                throw new DataAccessException("securityIdentity is missing issuer");
            }

            // verify that there is a user with this userid and email
            UserInfo userInfo = userRestDB.getUserInfo(userID);
            if (userInfo == null || !userInfo.email.equals(email)) {
                return;
            }
            var magicTokenClaims = new JWTUtils.MagicTokenClaims(
                    email, requestorSubject, requestorIssuer, new User(userInfo.userid, userInfo.id)
            );
            String magicJWT = JWTUtils.createMagicLinkJWT(magicTokenClaims, JWTUtils.MAGIC_LINK_DURATION_SECONDS);
            // create URL with same host as this server, but a query parameter 'magic' with text magicJWT
            URI uri = uriInfo.getAbsolutePath();
            String magicLink = uri.getScheme()+"://"+uri.getHost();
            if (uri.getPort()!=-1) {
                magicLink += ":" + uri.getPort();
            }
            magicLink += "/api/v1/users/processRecoveryEmail" +
                        "?magic="+magicJWT;
            String subject = "VCell Account Link Request";
            String content = "Dear VCell User,\n" +
                    "\n" +
                    "We received a request to link your VCell account for '"+userID+"' associated with this email address. " +
                    "If you made this request, please click on the link below to confirm your email and link your account:\n" +
                    "\n" +
                    "<a href=\""+magicLink+"\">"+magicLink+"<a>\n" +
                    "\n" +
                    "If you did not request to link your account, please ignore this email and no changes will be made to your account.\n" +
                    "\n" +
                    "Please note that this link will expire in 24 hours, and can only be used once.";

            //Send new password to user
            BeanUtils.sendSMTP(
                    PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPHostName),
                    Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPPort)),
                    PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPEmailAddress),
                    userInfo.email,
                    subject,
                    content
            );
        } catch (Exception e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/processMagicLink")
    @Operation(operationId = "processMagicLink", summary = "Process the magic link and map the user")
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponses({
            @APIResponse(responseCode = "200", description = "User mapped successfully"),
            @APIResponse(responseCode = "400", description = "Invalid or expired magic link"),
            @APIResponse(responseCode = "406", description = "User not mapped"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Response processMagicLink(@QueryParam("magic") String magicToken) {
        try {
            // Decode the magic token into a MagicTokenClaims object
            JWTUtils.MagicTokenClaims magicTokenClaims = JWTUtils.decodeMagicLinkJWT(magicToken);

            // Map the user
            boolean mapped = userRestDB.mapUserIdentity(magicTokenClaims);

            if (mapped) {
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_ACCEPTABLE).build();
            }
        } catch (JoseException | MalformedClaimException | InvalidJwtException e) {
            // The magic token is invalid or expired
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid or expired magic link").build();
        } catch (Exception e) {
            // An error occurred while mapping the user
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
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
    })
    @Produces(MediaType.APPLICATION_JSON)
    public UserIdentityJSONSafe getIdentity() throws DataAccessException {
        List<UserIdentity> userIdentities = userRestDB.getUserIdentities(securityIdentity);
        if (userIdentities.isEmpty()){
            return UserIdentityJSONSafe.noIdentity();
        } else if (userIdentities.size() > 1){
            throw new WebApplicationException("Multiple identities found for user", Response.Status.INTERNAL_SERVER_ERROR);
        } else {
            return UserIdentityJSONSafe.fromUserIdentity(userIdentities.get(0));
        }
    }

    @POST
    @Path("/bearerToken")
    @RolesAllowed("user")
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
    @Path("/guestBearerToken")
    @Operation(operationId = "getGuestLegacyApiToken", summary = "Method to get legacy tokens for guest users")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public AccesTokenRepresentationRecord generateGuestBearerToken() throws DataAccessException {
        if(securityIdentity.isAnonymous()){
            ApiAccessToken apiAccessToken = userRestDB.generateApiAccessToken(userRestDB.getAPIClient().getKey(), User.VCELL_GUEST);
            return AccesTokenRepresentationRecord.getRecordFromAccessTokenRepresentation(apiAccessToken);
        }
        return null;
    }

    @POST
    @Path("/forgotLegacyPassword")
    @RolesAllowed("user")
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
            boolean mapped,
            String userName,
            BigDecimal id,
            String subject,
            String insertDate
    ){
        public static UserIdentityJSONSafe noIdentity(){
            return new UserIdentityJSONSafe(false, null, null, null, null);
        }
        static UserIdentityJSONSafe fromUserIdentity(UserIdentity userIdentity){
            return new UserIdentityJSONSafe(true, userIdentity.user().getName(), userIdentity.id(), userIdentity.subject(), userIdentity.insertDate().toString());
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
