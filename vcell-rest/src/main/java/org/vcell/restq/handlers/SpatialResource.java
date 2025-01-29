package org.vcell.restq.handlers;

import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import net.lingala.zip4j.ZipFile;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.vcell.sbml.SBMLFakeSpatialBioModel;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.SBMLImporter;
import org.w3c.www.http.HTTP;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Path("/api/v1/spatial")
@RequestScoped
public class SpatialResource {
    public SpatialResource() { }

    @GET
    @Path("/retrieveFiniteVolumeInputFromSpatialModel")
    @Operation(operationId = "retrieveFiniteVolumeInputFromSpatialModel", summary = "Retrieve finite volume input from spatial model")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public File retrieveFiniteVolumeInputFromSpatialModel(@RestForm File sbmlFile) {
        try {
            File workingDir = Files.createTempDirectory("vcell-").toFile();
            File zipFile = Files.createTempFile("finite-volume", ".zip").toFile();
            sbmlToFiniteVolumeInput(sbmlFile, workingDir);
            ZipFile zip = new ZipFile(zipFile);

            zip.addFolder(workingDir);
            zip.close();

            return zipFile;
        }catch (Exception e){
            throw new WebApplicationException("Error processing spatial model", HTTP.INTERNAL_SERVER_ERROR);
        }
    }

    public record FiniteVolumeInput(
            @RestForm @PartType(MediaType.APPLICATION_OCTET_STREAM)
            File zipFile
    ) { }



    public static void sbmlToFiniteVolumeInput(File sbmlFile, File outputDir) throws IOException, MappingException, VCLoggerException, PropertyVetoException, SolverException, ExpressionException {
        SBMLExporter.MemoryVCLogger vcl = new SBMLExporter.MemoryVCLogger();
        boolean bValidateSBML = true;
        InputStream is = new FileInputStream(sbmlFile);
        SBMLImporter importer = new SBMLImporter(is, vcl, bValidateSBML);
        BioModel bioModel = importer.getBioModel();
        bioModel.updateAll(false);

        final double duration = 5.0;  // endpoint arg
        final double time_step = 0.1;  // endpoint arg
        //final ISize meshSize = new ISize(10, 10, 10);  // future endpoint arg
        SimulationContext simContext = bioModel.getSimulationContext(0);
        Simulation sim = new Simulation(simContext.getMathDescription(), simContext);
        sim.getSolverTaskDescription().setTimeBounds(new TimeBounds(0.0, duration));
        sim.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(time_step));

        SBMLFakeSpatialBioModel.writeInputFilesOnly(outputDir, sim);
    }

}
