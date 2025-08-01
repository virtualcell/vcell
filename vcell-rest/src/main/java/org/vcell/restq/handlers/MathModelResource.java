package org.vcell.restq.handlers;


import cbit.util.xml.XmlUtil;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jdom2.JDOMException;
import org.vcell.restq.errors.exceptions.*;
import org.vcell.restq.services.MathModelService;
import org.vcell.restq.services.UserRestService;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.*;

import java.io.IOException;
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
    @Operation(operationId = "deleteMathModel", description = "Remove specific Math Model.")
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
    @Operation(operationId = "getVCML", description = "Returns MathModel in VCML format.")
    public String getMathModel(@PathParam("id") String id) throws DataAccessWebException, NotFoundWebException, PermissionWebException {
        User user = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        try{
            BigString result = mathModelService.getMathModelVCML(user, new KeyValue(id));
            XmlUtil.vetXMLForMaliciousEntities(result.toString());
            return result.toString();
        } catch (ObjectNotFoundException e){
            throw new NotFoundWebException(e.getMessage(), e);
        } catch (PermissionException e){
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (IOException | JDOMException e) {
            throw new RuntimeWebException("The MathModel you are trying to retrieve seems to be malformed, please contact VCell support with the Math Model ID: " + id, e);
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
            @Parameter(required = false, description = "Include MathModel summaries that are public and shared with the requester. Default is true.")
            @QueryParam("includePublicAndShared") Optional<Boolean> includePublicAndShared) throws DataAccessWebException, ObjectNotFoundException {
        User user = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        try{
            MathModelInfo[] infos = mathModelService.getMathModelInfos(user, includePublicAndShared.orElse(true));
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
                       @QueryParam("simsRequiringUpdates") List<String> simNames) throws DataAccessWebException, NotAuthenticatedWebException, UnprocessableContentWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try{
            XmlUtil.vetXMLForMaliciousEntities(mathModelVCML);
            BigString result = mathModelService.saveModel(user, new BigString(mathModelVCML), newName.orElse(null), simNames.toArray(new String[0]));
            XmlUtil.vetXMLForMaliciousEntities(result.toString()); // partial saves might include already saved XML
            return result.toString();
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (IOException | JDOMException e) {
            throw new UnprocessableContentWebException(e.getMessage(), e);
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
