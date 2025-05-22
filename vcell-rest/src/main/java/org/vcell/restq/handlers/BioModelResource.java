package org.vcell.restq.handlers;

import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.vcell.restq.Main;
import org.vcell.restq.services.BioModelRestService;
import org.vcell.restq.services.UserRestService;
import org.vcell.restq.errors.exceptions.*;
import org.vcell.restq.errors.exceptions.NotFoundWebException;
import org.vcell.restq.models.BioModel;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.util.Optional;

@Path("/api/v1/bioModel")
public class BioModelResource {
    @Inject
    SecurityIdentity securityIdentity;

    private final BioModelRestService bioModelRestService;
    private final UserRestService userRestService;

    @Inject
    public BioModelResource(BioModelRestService bioModelRestService, UserRestService userRestService) {
        this.bioModelRestService = bioModelRestService;
        this.userRestService = userRestService;
    }

    @GET
    @Path("{bioModelID}")
    @Operation(operationId = "getBioModel", summary = "Get BioModel.")
    @Produces(MediaType.APPLICATION_JSON)
    public BioModel getBioModelInfo(@PathParam("bioModelID") String bioModelID) throws DataAccessWebException, NotFoundWebException, PermissionWebException, NotAuthenticatedWebException {
        User vcellUser = userRestService.getUserFromIdentity(securityIdentity, UserRestService.UserRequirement.ALLOW_ANONYMOUS);
        if (vcellUser == null) {
            vcellUser = Main.DUMMY_USER;
        }
        try {
            BioModelRep bioModelRep = bioModelRestService.getBioModelRep(new KeyValue(bioModelID), vcellUser);
            return BioModel.fromBioModelRep(bioModelRep);
        }catch (ObjectNotFoundException e) {
            throw new NotFoundWebException(e.getMessage(), e);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @GET
    @Path("{bioModelID}/vcml_download")
    @Operation(operationId = "getBioModelVCML", summary = "Get the BioModel in VCML format.")
    @Produces(MediaType.TEXT_XML)
    public String getBioModelVCML(@PathParam("bioModelID") String bioModelID) throws DataAccessWebException, NotFoundWebException, PermissionWebException, NotAuthenticatedWebException {
        User vcellUser = userRestService.getUserFromIdentity(securityIdentity, UserRestService.UserRequirement.ALLOW_ANONYMOUS);
        if (vcellUser == null) {
            vcellUser = Main.DUMMY_USER;
        }
        try {
            return bioModelRestService.getBioModel(vcellUser, new KeyValue(bioModelID));
        }catch (ObjectNotFoundException e) {
            throw new NotFoundWebException(e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @GET
    @Path("{bioModelID}/sbml_download")
    @Operation(operationId = "getBioModelSBML", summary = "Get the BioModel in SBML format.", hidden = true)
    @Produces(MediaType.APPLICATION_XML)
    public void getBioModelSBML(@PathParam("bioModelID") String bioModelID){
        throw new WebApplicationException("Not implemented", 501);
    }

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
        User user = userRestService.getUserFromIdentity(securityIdentity, UserRestService.UserRequirement.REQUIRE_USER);;
        try {
            bioModelRestService.deleteBioModel(user, new KeyValue(bioModelID));
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @POST
    @Path("/save")
    @RolesAllowed("user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_XML)
    @Operation(operationId = "saveBioModel", summary = "Save's the given BioModel. Optional parameters of name and simulations to update due to math changes." +
            " Returns saved BioModel as VCML.")
    public String save(@Valid SaveBioModel saveBioModel) throws DataAccessWebException, UnprocessableContentWebException, PermissionWebException, NotAuthenticatedWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity, UserRestService.UserRequirement.REQUIRE_USER);
        try {
            cbit.vcell.biomodel.BioModel savedBioModel = bioModelRestService.save(user, saveBioModel.bioModelXML,
                    saveBioModel.name.orElse(null), saveBioModel.simsRequiringUpdates.orElse(null));
            return XmlHelper.bioModelToXML(savedBioModel);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (XmlParseException e){
            throw new UnprocessableContentWebException(e.getMessage(), e);
        }
    }




    public record SaveBioModel(
            @NotBlank(message = "BioModel can not be an empty string.")
            @NotNull String bioModelXML,
            Optional<String> name,
            Optional<String[]> simsRequiringUpdates
    ){ }
}
