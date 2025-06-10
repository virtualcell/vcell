package org.vcell.restq.handlers;

import cbit.util.xml.XmlUtil;
import cbit.vcell.geometry.GeometryInfo;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jdom2.JDOMException;
import org.vcell.restq.errors.exceptions.*;
import org.vcell.restq.services.GeometryService;
import org.vcell.restq.services.UserRestService;
import org.vcell.util.*;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/api/v1/geometry")
@RequestScoped
public class GeometryResource {

    @Inject
    SecurityIdentity securityIdentity;
    @Inject
    UserRestService userRestService;
    @Inject
    GeometryService geometryService;

    @DELETE
    @Path("/{id}")
    @Operation(operationId = "deleteGeometry", description = "Remove specific Geometry.")
    public void deleteGeometry(@PathParam("id") String id) throws DataAccessWebException, NotAuthenticatedWebException, NotFoundWebException, PermissionWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try{
            geometryService.deleteGeometryVCML(user, new KeyValue(id));
        } catch (ObjectNotFoundException e) {
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
    @Operation(operationId = "getGeometryVCML", description = "Returns Geometry in VCML format.")
    public String getGeometry(@PathParam("id") String id) throws DataAccessWebException, NotFoundWebException, PermissionWebException {
        User user = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        try{
            return geometryService.getGeometryVCML(user, new KeyValue(id)).toString();
        } catch (ObjectNotFoundException e) {
            throw new NotFoundWebException(e.getMessage(), e);
        } catch (PermissionException e) {
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/summary/{id}")
    @Operation(operationId = "getGeometrySummary", description = "All of the text based information about a Geometry (dimensions, extent, origin, etc...), but not the actual Geometry itself.")
    public GeometrySummary getGeometrySummary(@PathParam("id") String id) throws DataAccessWebException, NotFoundWebException, PermissionWebException {
        User user = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        try {
            GeometryInfo info = geometryService.getGeometryInfo(user, new KeyValue(id));
            return new GeometrySummary(info.getDimension(), info.getOrigin(), info.getExtent(), info.getImageRef(), info.getVersion(), info.getSoftwareVersion());
        } catch (ObjectNotFoundException e) {
            throw new NotFoundWebException(e.getMessage(), e);
        } catch (PermissionException e) {
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @GET
    @Path("/summaries")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "getGeometrySummaries", description = "Return Geometry summaries.")
    public List<GeometrySummary> getGeometrySummaries(
            @Parameter(required = false, description = "Include Geometry summaries that are public and shared with the requester. Default is true.")
            @QueryParam("includePublicAndShared") Optional<Boolean> includePublicAndShared) throws DataAccessWebException, ObjectNotFoundException {
        User user = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        try{
            GeometryInfo[] infos = geometryService.getGeometryInfos(user, includePublicAndShared.orElse(true));
            ArrayList<GeometrySummary> summaries = new ArrayList<GeometrySummary>();
            for (GeometryInfo info : infos) {
                summaries.add(new GeometrySummary(info.getDimension(), info.getOrigin(), info.getExtent(), info.getImageRef(), info.getVersion(), info.getSoftwareVersion()));
            }
            return summaries;
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @RolesAllowed("user")
    @Operation(operationId = "saveGeometry")
    public String save(@RequestBody(name = "mathModelVCML", required = true) String mathGeometryVCML,
                       @Parameter(name = "newName", required = false, description = "Name to save new Geometry under. Leave blank if re-saving existing Geometry.")
                       @QueryParam("newName") Optional<String> newName) throws DataAccessWebException, NotAuthenticatedWebException, UnprocessableContentWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try{
            XmlUtil.vetXMLForMaliciousEntities(mathGeometryVCML);
            return geometryService.saveGeometry(user, new BigString(mathGeometryVCML), newName.orElse(null)).toString();
        } catch (IOException | JDOMException e) {
            throw new UnprocessableContentWebException(e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }


    public record GeometrySummary(
            int dimension,
            Origin origin,
            Extent extent,
            KeyValue imageRef,
            Version version,
            VCellSoftwareVersion softwareVersion
    ){ }

}
