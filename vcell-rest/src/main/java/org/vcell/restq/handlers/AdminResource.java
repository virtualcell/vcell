package org.vcell.restq.handlers;

import com.lowagie.text.Document;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.NoCache;
import org.vcell.restq.db.AdminRestDB;
import org.vcell.restq.db.UserRestDB;
import org.vcell.restq.errors.exceptions.DataAccessWebException;
import org.vcell.restq.errors.exceptions.NotAuthenticatedWebException;
import org.vcell.restq.errors.exceptions.PermissionWebException;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.io.StringReader;
import java.sql.SQLException;

@Path("/api/v1/admin")
@RequestScoped
public class AdminResource {

    // get Quarkus logger
    private static final Logger lg = LogManager.getLogger(AdminResource.class);

    @Inject
    SecurityIdentity securityIdentity;

    private final AdminRestDB adminRestDB;
    private final UserRestDB userRestDB;

    @Inject
    public AdminResource(AdminRestDB adminRestDB, UserRestDB userRestDB) {
        this.adminRestDB = adminRestDB;
        this.userRestDB = userRestDB;
    }

    @GET
    @Path("/usage")
//    @RolesAllowed("admin")
    @RolesAllowed("user")
    @Operation(operationId = "getUsage", summary = "Get usage summary")
    @NoCache
    @APIResponse(
            responseCode = "200",
            description = "The PDF report",
            content = @Content(mediaType = "application/pdf", schema = @Schema(type = SchemaType.STRING, format = "binary"))
    )
    public Response getUsage() throws DataAccessWebException, NotAuthenticatedWebException, PermissionWebException {
        if (securityIdentity.isAnonymous()){
            throw new NotAuthenticatedWebException("not authenticated");
        }
        User vcellUser = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
        try {
            String htmlString = adminRestDB.getUsageSummaryHtml(vcellUser);
            StreamingOutput fileStream = output -> {
                try {
                    Document document = new Document();
                    document.setPageSize(com.lowagie.text.PageSize.A2);  // large enough page size for wide table
                    PdfWriter writer = PdfWriter.getInstance(document, output);
                    document.open();
                    HTMLWorker htmlWorker = new HTMLWorker(document);
                    htmlWorker.parse(new StringReader(htmlString));
                    document.close();
                } catch (Exception e) {
                    throw new WebApplicationException("Error while generating PDF: " + e.getMessage(), e);
                }
            };
            return Response
                    .ok(fileStream, "application/pdf")
                    .header("content-disposition","attachment; filename = usage_summary.pdf")
                    .build();
        } catch (SQLException | DataAccessException e) {
            lg.error("database error", e);
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }
}
