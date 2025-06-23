package org.vcell.restq.handlers;

import cbit.image.VCImageInfo;
import cbit.util.xml.XmlUtil;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jdom2.JDOMException;
import org.vcell.restq.Main;
import org.vcell.restq.errors.exceptions.*;
import org.vcell.restq.models.BioModel;
import org.vcell.restq.services.BioModelRestService;
import org.vcell.restq.services.UserRestService;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/api/v1/bioModel")
public class BioModelResource {
    @Inject
    SecurityIdentity securityIdentity;

    private final BioModelRestService bioModelRestService;
    private final UserRestService userRestService;

    public static final String simsRequiringUpdatesDescription = "The name of simulations that will be prepared for future execution.";

    @Inject
    public BioModelResource(BioModelRestService bioModelRestService, UserRestService userRestService) {
        this.bioModelRestService = bioModelRestService;
        this.userRestService = userRestService;
    }

    @GET
    @Path("{bioModelID}")
    @Operation(operationId = "getBioModel", summary = "Get BioModel.")
    @Produces(MediaType.APPLICATION_JSON)
    public BioModel getBioModelInfo(@PathParam("bioModelID") String bioModelID) throws DataAccessWebException, NotFoundWebException, PermissionWebException {
        User vcellUser = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        if (vcellUser == null) {
            vcellUser = Main.DUMMY_USER;
        }
        try {
            BioModelRep bioModelRep = bioModelRestService.getBioModelRep(new KeyValue(bioModelID), vcellUser);
            return BioModel.fromBioModelRep(bioModelRep);
        }catch (ObjectNotFoundException e) {
            throw new NotFoundWebException(e.getMessage(), e);
        } catch (PermissionException e){
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @GET
    @Path("{bioModelID}/summary")
    @Operation(operationId = "getBioModelSummary", summary = "All of the text based information about a BioModel (summary, version, publication status, etc...), but not the actual BioModel itself.")
    @Produces(MediaType.APPLICATION_JSON)
    public BioModelSummary getBioModelContext(@PathParam("bioModelID") String bioModelID) throws PermissionWebException, DataAccessWebException {
        User vcellUser = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        try{
            BioModelInfo info = bioModelRestService.getBioModelInfo(vcellUser, new KeyValue(bioModelID));
            return new BioModelSummary(info.getVersion(), info.getBioModelChildSummary(), info.getPublicationInfos(), info.getSoftwareVersion());
        } catch (PermissionException e){
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @GET
    @Path("summaries")
    @Operation(operationId = "getBioModelSummaries", summary = "Return BioModel summaries.")
    @Produces(MediaType.APPLICATION_JSON)
    public BioModelSummary[] getBioModelContexts(
            @Parameter(description = "Includes BioModel summaries that are public or shared with requester. Default is true.")
            @QueryParam("includePublicAndShared") Optional<Boolean> includePublicAndShared) throws DataAccessWebException {
        try{
            User vcellUser = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
            BioModelInfo[] infos = bioModelRestService.getBioModelInfos(vcellUser, includePublicAndShared.orElse(true));
            BioModelSummary[] contexts = new BioModelSummary[infos.length];
            for (int i = 0; i < infos.length; i ++){
                contexts[i] = new BioModelSummary(infos[i].getVersion(), infos[i].getBioModelChildSummary(), infos[i].getPublicationInfos(), infos[i].getSoftwareVersion());
            }
            return contexts;
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @GET
    @Path("{bioModelID}/vcml_download")
    @Operation(operationId = "getBioModelVCML", summary = "Get the BioModel in VCML format.")
    @Produces(MediaType.TEXT_XML)
    public String getBioModelVCML(@PathParam("bioModelID") String bioModelID) throws DataAccessWebException, NotFoundWebException, PermissionWebException {
        User vcellUser = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        if (vcellUser == null) {
            vcellUser = Main.DUMMY_USER;
        }
        try {
            String vcml = bioModelRestService.getBioModel(vcellUser, new KeyValue(bioModelID));
            XmlUtil.vetXMLForMaliciousEntities(vcml);
            return vcml;
        }catch (ObjectNotFoundException e) {
            throw new NotFoundWebException(e.getMessage(), e);
        } catch (PermissionException e){
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (IOException | JDOMException e) {
            throw new RuntimeWebException("The BioModel you are trying to retrieve seems to be malformed. Please contact VCell support with the BioModel ID: " + bioModelID, e);
        }
    }

    @GET
    @Path("{bioModelID}/sbml_download")
    @Operation(operationId = "getBioModelSBML", summary = "Get the BioModel in SBML format.", hidden = true)
    @Produces(MediaType.APPLICATION_XML)
    public void getBioModelSBML(@PathParam("bioModelID") String bioModelID){
        throw new WebApplicationException("Not implemented", 501);
    }

    // TODO: Needs to be threaded, expensive in memory and compute, requires innovative and non-naive implementation
    @GET
    @Path("{bioModelID}/omex_download")
    @Operation(operationId = "getBioModelOMEX", summary = "Get the BioModel in OMEX format.", hidden = true)
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    public void getBioModelOMEX(@PathParam("bioModelID") String bioModelID){
        throw new WebApplicationException("Not implemented", 501);
    }

    @GET
    @Path("{bioModelID}/bngl_download")
    @Operation(operationId = "getBioModelBNGL", summary = "Get the BioModel in BNGL format.", hidden = true)
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    public void getBioModelBNGL(@PathParam("bioModelID") String bioModelID){
        throw new WebApplicationException("Not implemented", 501);
    }

    @GET
    @Path("{bioModelID}/diagram_download")
    @Operation(operationId = "getBioModelDIAGRAM", summary = "Get the BioModels diagram.", hidden = true)
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    public void getBioModelDiagram(@PathParam("bioModelID") String bioModelID){
        throw new WebApplicationException("Not implemented", 501);
    }

    @DELETE
    @Path("{bioModelID}")
    @Operation(operationId = "deleteBioModel", summary = "Delete the BioModel from VCell's database.")
    public void deleteBioModel(@PathParam("bioModelID") String bioModelID) throws DataAccessWebException, PermissionWebException, NotAuthenticatedWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);;
        try {
            bioModelRestService.deleteBioModel(user, new KeyValue(bioModelID));
        } catch (PermissionException e){
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Operation(operationId = "saveBioModel", summary = "Save's the given BioModel. Optional parameters of name and simulations to update due to math changes." +
            " Returns saved BioModel as VCML.")
    @RolesAllowed("user")
    public String save(@RequestBody(name = "bioModelVCML", required = true, description = "BioModelVCML which will be saved.") String bioModelVCML,
                       @QueryParam("newName") @Parameter(required = false, allowEmptyValue = true, description = "Name to save new BioModel under. Leave blank if re-saving existing BioModel.") Optional<String> newName,
                       @QueryParam("simsRequiringUpdates") @Parameter(required = false, allowEmptyValue = true, description = simsRequiringUpdatesDescription) List<String> simNames) throws DataAccessWebException, UnprocessableContentWebException, NotAuthenticatedWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try {
            XmlUtil.vetXMLForMaliciousEntities(bioModelVCML);
            cbit.vcell.biomodel.BioModel savedBioModel = bioModelRestService.save(user, bioModelVCML,
                    newName.orElse(null), simNames.toArray(new String[0]));
            String result = XmlHelper.bioModelToXML(savedBioModel);
            XmlUtil.vetXMLForMaliciousEntities(result);
            return result;
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (XmlParseException | IOException | JDOMException e){
            throw new UnprocessableContentWebException(e.getMessage(), e);
        }
    }

    @Path("/infoContainers")
    @GET
    @Operation(operationId = "getInfoContainers", description = "All of the summary objects for this particular user.")
    public VCellInfoContainer getInfoContainers() throws DataAccessWebException {
        User user = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        try{
            VCInfoContainer container = bioModelRestService.getVCInfoContainer(user);
            return VCellInfoContainer.infoContainerToDTO(container);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    public record VCellInfoContainer(
            ArrayList<VCImageResource.VCImageSummary> imageInfos,
            ArrayList<GeometryResource.GeometrySummary> geometryInfo,
            ArrayList<MathModelResource.MathModelSummary> mathModelInfos,
            ArrayList<BioModelSummary> bioModelInfos
    ){
        public static VCellInfoContainer infoContainerToDTO(VCInfoContainer vcInfoContainer) {
            ArrayList<VCImageResource.VCImageSummary> imageInfos = new ArrayList<>();
            for (VCImageInfo info : vcInfoContainer.getVCImageInfos()){
                imageInfos.add(new VCImageResource.VCImageSummary(info.getISize(),
                        info.getExtent(), info.getVersion(), info.getBrowseGif(), info.getSoftwareVersion()));
            }
            ArrayList<GeometryResource.GeometrySummary> geometryInfos = new ArrayList<>();
            for (GeometryInfo info : vcInfoContainer.getGeometryInfos()){
                geometryInfos.add(new GeometryResource.GeometrySummary(info.getDimension(), info.getOrigin(), info.getExtent(),
                        info.getImageRef(), info.getVersion(), info.getSoftwareVersion()));
            }
            ArrayList<MathModelResource.MathModelSummary> mathModelInfos = new ArrayList<>();
            for (MathModelInfo info : vcInfoContainer.getMathModelInfos()){
                mathModelInfos.add(new MathModelResource.MathModelSummary(info.getVersion(), info.getMathKey(),
                        info.getMathModelChildSummary(), info.getSoftwareVersion(), info.getPublicationInfos(),
                        info.getAnnotatedFunctionsStr()));
            }
            ArrayList<BioModelSummary> bioModelInfos = new ArrayList<>();
            for (BioModelInfo info : vcInfoContainer.getBioModelInfos()){
                bioModelInfos.add(new BioModelSummary(info.getVersion(), info.getBioModelChildSummary(),
                        info.getPublicationInfos(), info.getSoftwareVersion()));
            }
            return new VCellInfoContainer(
                imageInfos, geometryInfos, mathModelInfos, bioModelInfos
            );
        }
    }

    public record BioModelSummary(
        Version version, // Problematic
        BioModelChildSummary summary,
        PublicationInfo[] publicationInformation, // Need separate DTO
        VCellSoftwareVersion vCellSoftwareVersion
    ){ }
}
