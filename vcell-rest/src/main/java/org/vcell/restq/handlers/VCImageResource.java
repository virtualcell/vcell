package org.vcell.restq.handlers;

import cbit.image.GIFImage;
import cbit.image.VCImageInfo;
import cbit.util.xml.XmlUtil;
import cbit.vcell.xml.XmlHelper;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jdom2.JDOMException;
import org.vcell.restq.errors.exceptions.*;
import org.vcell.restq.services.UserRestService;
import org.vcell.restq.services.VCImageService;
import org.vcell.util.*;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Path("/api/v1/image")
public class VCImageResource {

    @Inject
    SecurityIdentity securityIdentity;
    @Inject
    UserRestService userRestService;
    @Inject
    VCImageService vcImageService;

    @Path("/{id}")
    @DELETE
    @Operation(operationId = "deleteImageVCML", description = "Remove specific image VCML.")
    public void deleteVCImage(@PathParam("id") String id) throws DataAccessWebException, NotAuthenticatedWebException, NotFoundWebException, PermissionWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try{
            vcImageService.deleteVCImage(user, new KeyValue(id));
        } catch (ObjectNotFoundException e){
            throw new NotFoundWebException(e.getMessage(), e);
        } catch (PermissionException e){
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @Path("/{id}")
    @GET
    @Operation(operationId = "getImageVCML", description = "Get specific image VCML.")
    public String getVCImage(@PathParam("id") String id) throws DataAccessWebException, NotFoundWebException, PermissionWebException, UnprocessableContentWebException {
        User user = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        try{
            String result = vcImageService.getVCImageXML(user, new KeyValue(id));
            XmlUtil.vetXMLForMaliciousEntities(result);
            return result;
        } catch (ObjectNotFoundException e){
            throw new NotFoundWebException(e.getMessage(), e);
        } catch (PermissionException e){
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }  catch (IOException | JDOMException e) {
            throw new UnprocessableContentWebException(e.getMessage(), e);
        }
    }


    @Path("/summary/{id}")
    @GET
    @Operation(operationId = "getImageSummary", description = "All of the miscellaneous information about an Image (Extent, ISize, preview, etc...), but not the actual Image itself.")
    public VCImageSummary getSummary(@PathParam("id") String id) throws DataAccessWebException, NotFoundWebException, PermissionWebException {
        User user = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        try{
            VCImageInfo info = vcImageService.getVCImageInfo(user, new KeyValue(id));
            return new VCImageSummary(info.getISize(), info.getExtent(), info.getVersion(), info.getBrowseGif(),
                    info.getSoftwareVersion());
        }  catch (ObjectNotFoundException e){
            throw new NotFoundWebException(e.getMessage(), e);
        } catch (PermissionException e){
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }


    @Path("/summaries")
    @GET
    @Operation(operationId = "getImageSummaries", description = "Return Image summaries.")
    public ArrayList<VCImageSummary> getSummaries(@Parameter(required = false, description = "Include Image summaries that are public and shared with the requester. Default is true.")
                                           @QueryParam("includePublicAndShared") Optional<Boolean> includePublicAndShared) throws DataAccessWebException {
        User user = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        try{
            VCImageInfo[] infos = vcImageService.getVCImageInfos(user, includePublicAndShared.orElse(true));
            ArrayList<VCImageSummary> summaries = new ArrayList<VCImageSummary>();
            for (VCImageInfo info : infos) {
                summaries.add(new VCImageSummary(info.getISize(), info.getExtent(), info.getVersion(),
                        info.getBrowseGif(), info.getSoftwareVersion()));
            }
            return summaries;
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }


    @Path("")
    @POST
    @Operation(operationId = "saveImageVCML", description = "Save the VCML representation of an image.")
    public String saveVCImage(@RequestBody(name = "imageVCML", required = true) String imageVCML,
                              @Parameter(name = "name", required = false, description = "Name to save new ImageVCML under. Leave blank if re-saving existing ImageVCML.")
                              @QueryParam("name") Optional<String> name) throws DataAccessWebException, NotAuthenticatedWebException, UnprocessableContentWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try{
            XmlUtil.vetXMLForMaliciousEntities(imageVCML);
            String result = vcImageService.saveVCImage(user, imageVCML, name.orElse(null));
            XmlUtil.vetXMLForMaliciousEntities(result);
            return result;
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (IOException | JDOMException e) {
            throw new UnprocessableContentWebException(e.getMessage(), e);
        }
    }

    public record VCImageSummary(
            ISize size,
            Extent extent,
            Version version,
            GIFImage preview,
            VCellSoftwareVersion softwareVersion
    ) { }

}
