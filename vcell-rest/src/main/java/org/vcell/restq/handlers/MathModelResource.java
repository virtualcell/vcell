package org.vcell.restq.handlers;


import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.vcell.restq.errors.exceptions.DataAccessWebException;
import org.vcell.restq.errors.exceptions.NotAuthenticatedWebException;
import org.vcell.restq.errors.exceptions.NotFoundWebException;
import org.vcell.restq.errors.exceptions.PermissionWebException;
import org.vcell.restq.services.MathModelService;
import org.vcell.restq.services.UserRestService;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/api/v1/mathModel")
public class MathModelResource {

    @Inject
    SecurityIdentity securityIdentity;
    @Inject
    UserRestService userRestService;
    @Inject
    MathModelService mathModelService;

    @DELETE
    @Path("/{id}")
    @Operation(operationId = "deleteMathModel")
    public void deleteMathModel(@PathParam("id") String id) throws DataAccessWebException, NotAuthenticatedWebException, NotFoundWebException, PermissionWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try{
            mathModelService.deleteMathModelVCML(user, new KeyValue(id));
        } catch (ObjectNotFoundException e){
            throw new NotFoundWebException(e.getMessage(), e);
        } catch (PermissionException e){
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/{id}")
    @Operation(operationId = "getVCML")
    public String getMathModel(@PathParam("id") String id) throws DataAccessWebException, NotFoundWebException, PermissionWebException {
        User user = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        try{
            BigString result = mathModelService.getMathModelVCML(user, new KeyValue(id));
            return result.toString();
        } catch (ObjectNotFoundException e){
            throw new NotFoundWebException(e.getMessage(), e);
        } catch (PermissionException e){
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/summary/{id}")
    @Operation(operationId = "getSummary", description = "All of the text based information about a MathModel (summary, version, publication status, etc...), but not the actual MathModel itself.")
    public MathModelSummary getMathModelSummary(@PathParam("id") String id) throws DataAccessWebException, NotFoundWebException, PermissionWebException {
        User user = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        try{
            MathModelInfo info = mathModelService.getMathModelInfo(user, new KeyValue(id));
            if (info == null) {
                throw new NotFoundWebException("Math Model with id " + id + " was not found");
            }
            return new MathModelSummary(info.getVersion(), info.getMathKey(), info.getMathModelChildSummary(),
                    info.getSoftwareVersion(), info.getPublicationInfos(), info.getAnnotatedFunctionsStr());
        } catch (ObjectNotFoundException e) {
            throw new NotFoundWebException(e.getMessage(), e);
        } catch (PermissionException e){
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @GET
    @Path("/summaries")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "getSummaries", description = "Return MathModel summaries.")
    public List<MathModelSummary> getMathModelSummaries(
            @Parameter(description = "Include MathModel summaries that are public and shared with the requester.")
            @QueryParam("includePublicAndShared") boolean includePublicAndShared) throws DataAccessWebException, ObjectNotFoundException {
        User user = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        try{
            MathModelInfo[] infos = mathModelService.getMathModelInfos(user, includePublicAndShared);
            ArrayList<MathModelSummary> summaries = new ArrayList<>();
            for (MathModelInfo info : infos) {
                MathModelChildSummary childSummary = info.getMathModelChildSummary();
                summaries.add(new MathModelSummary(info.getVersion(), info.getMathKey(), childSummary,
                        info.getSoftwareVersion(), info.getPublicationInfos(), info.getAnnotatedFunctionsStr()));
            }
            return summaries;
        } catch (ObjectNotFoundException e) {
            throw new ObjectNotFoundException(e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @RolesAllowed("user")
    @Operation(operationId = "saveMathModel")
    public String save(@RequestBody(name = "mathModelVCML", required = true) String mathModelVCML,
                       @Parameter(name = "newName", required = false, description = "Name to save new MathModel under. Leave blank if re-saving existing MathModel.")
                       @QueryParam("newName") Optional<String> newName,
                       @Parameter(name = "simNames", required = false, description = BioModelResource.simsRequiringUpdatesDescription)
                       @QueryParam("simsRequiringUpdates") List<String> simNames) throws DataAccessWebException, NotAuthenticatedWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try{
            BigString result = mathModelService.saveModel(user, new BigString(mathModelVCML), newName.orElse(null), simNames.toArray(new String[0]));
            return result.toString();
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }


    public record MathModelSummary(
        Version version,
        KeyValue keyValue,
        MathModelChildSummary modelInfo,
        VCellSoftwareVersion softwareVersion,
        PublicationInfo[] publicationInfos,
        String annotatedFunctions
    ){ }

}
