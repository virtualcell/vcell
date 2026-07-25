package org.vcell.restq.handlers;

import cbit.image.VCImageInfo;
import cbit.vcell.geometry.GeometryInfo;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.vcell.restq.errors.exceptions.DataAccessWebException;
import org.vcell.restq.services.UserRestService;
import org.vcell.restq.services.VCInfoContainerService;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCInfoContainer;

import java.util.ArrayList;

@Path("/api/v1/vcInfoContainer")
public class VCInfoContainerResource {

    @Inject
    SecurityIdentity securityIdentity;
    @Inject
    UserRestService userRestService;
    @Inject
    VCInfoContainerService vcInfoContainerService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "getVCInfoContainer",
            summary = "Return a single bulk collection of metadata summaries (BioModels, MathModels, Geometries, Images) " +
                    "visible to the requester. Anonymous requests return public records only; an authenticated request " +
                    "additionally includes the requester's own and shared records.")
    public VCInfoContainerSummary getVCInfoContainer() throws DataAccessWebException {
        User user = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
        try {
            VCInfoContainer vcInfoContainer = vcInfoContainerService.getVCInfoContainer(user);

            ArrayList<BioModelResource.BioModelSummary> bioModelSummaries = new ArrayList<>();
            for (BioModelInfo info : vcInfoContainer.getBioModelInfos()) {
                bioModelSummaries.add(new BioModelResource.BioModelSummary(info.getVersion(),
                        info.getBioModelChildSummary(), info.getPublicationInfos(), info.getSoftwareVersion()));
            }

            ArrayList<MathModelResource.MathModelSummary> mathModelSummaries = new ArrayList<>();
            for (MathModelInfo info : vcInfoContainer.getMathModelInfos()) {
                mathModelSummaries.add(new MathModelResource.MathModelSummary(info.getVersion(), info.getMathKey(),
                        info.getMathModelChildSummary(), info.getSoftwareVersion(), info.getPublicationInfos(),
                        info.getAnnotatedFunctionsStr()));
            }

            ArrayList<GeometryResource.GeometrySummary> geometrySummaries = new ArrayList<>();
            for (GeometryInfo info : vcInfoContainer.getGeometryInfos()) {
                geometrySummaries.add(new GeometryResource.GeometrySummary(info.getDimension(), info.getOrigin(),
                        info.getExtent(), info.getImageRef(), info.getVersion(), info.getSoftwareVersion()));
            }

            ArrayList<VCImageResource.VCImageSummary> vcImageSummaries = new ArrayList<>();
            for (VCImageInfo info : vcInfoContainer.getVCImageInfos()) {
                vcImageSummaries.add(new VCImageResource.VCImageSummary(info.getISize(), info.getExtent(),
                        info.getVersion(), info.getBrowseGif(), info.getSoftwareVersion()));
            }

            return new VCInfoContainerSummary(bioModelSummaries, mathModelSummaries, geometrySummaries, vcImageSummaries);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    public record VCInfoContainerSummary(
            ArrayList<BioModelResource.BioModelSummary> bioModelSummaries,
            ArrayList<MathModelResource.MathModelSummary> mathModelSummaries,
            ArrayList<GeometryResource.GeometrySummary> geometrySummaries,
            ArrayList<VCImageResource.VCImageSummary> vcImageSummaries
    ) { }
}
