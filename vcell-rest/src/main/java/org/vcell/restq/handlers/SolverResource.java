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
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.lingala.zip4j.ZipFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.RestForm;
import org.vcell.restq.errors.exceptions.UnprocessableContentWebException;
import org.vcell.sbml.FiniteVolumeRunUtil;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.SBMLImporter;
import org.w3c.www.http.HTTP;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Path("/api/v1/solver")
@RequestScoped
public class SolverResource {
    public SolverResource() { }

    private static final Logger lg = LogManager.getLogger(SolverResource.class);

    @POST
    @Path("/getFVSolverInput")
    @Operation(operationId = "getFVSolverInputFromSBML", summary = "Retrieve finite volume input from SBML spatial model.")
    @Parameter(name = "duration", description = "Duration of simulation in sbml time units (defaults to seconds)")
    @Parameter(name = "output_time_step", description = "Output time step in sbml time units (defaults to seconds)")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public File retrieveFiniteVolumeInputFromSBMLSpatial(@RestForm File sbmlFile,
                                                         @RestForm @DefaultValue("5.0") double duration,
                                                         @RestForm @DefaultValue("0.1") double output_time_step) throws IOException {
        File workingDir = Files.createTempDirectory("vcell-").toFile();
        File zipFile = Files.createTempFile("finite-volume", ".zip").toFile();
        try {
            sbmlToFiniteVolumeInput(sbmlFile, workingDir, duration, output_time_step);
            ZipFile zip = new ZipFile(zipFile);
            for (File file : workingDir.listFiles()) {
                zip.addFile(file);
            }
            zip.close();
            return zipFile;
        }catch (Exception e){
            lg.error(e);
            throw new WebApplicationException("Error processing spatial model", HTTP.INTERNAL_SERVER_ERROR);
        } finally {
            for (File file: workingDir.listFiles()){
                file.delete();
            }
            workingDir.delete();
        }
    }

    public static void sbmlToFiniteVolumeInput(File sbmlFile, File outputDir, double duration, double output_time_step)
            throws IOException, MappingException, VCLoggerException, PropertyVetoException, SolverException, ExpressionException {
        SimulationContext simContext = getSimulationContext(sbmlFile);
        Simulation sim = new Simulation(simContext.getMathDescription(), simContext);
        sim.getSolverTaskDescription().setTimeBounds(new TimeBounds(0.0, duration));
        sim.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(output_time_step));

        FiniteVolumeRunUtil.writeInputFilesOnly(outputDir, sim);
    }

    private static SimulationContext getSimulationContext(File sbmlFile) throws IOException, VCLoggerException, MappingException {
        SBMLExporter.MemoryVCLogger vcl = new SBMLExporter.MemoryVCLogger();
        boolean bValidateSBML = true;
        InputStream is = new FileInputStream(sbmlFile);
        SBMLImporter importer = new SBMLImporter(is, vcl, bValidateSBML);
        if (!vcl.highPriority.isEmpty()) {
            throw new IOException("Error parsing SBML: "+vcl.highPriority);
        }
        BioModel bioModel = importer.getBioModel();
        bioModel.updateAll(false);

        return bioModel.getSimulationContext(0);
    }


    @POST
    @Path("/getFVSolverInputFromVCML")
    @Operation(operationId = "getFVSolverInputFromVCML", summary = "Retrieve finite volume input from SBML spatial model.")
    @Parameter(name = "simulation_name", description = "name of simulation in VCML file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public File retrieveFiniteVolumeInputFromVCML(@RestForm File vcmlFile, @RestForm String simulation_name) throws IOException, UnprocessableContentWebException {
        File workingDir = Files.createTempDirectory("vcell-").toFile();
        File zipFile = Files.createTempFile("finite-volume", ".zip").toFile();
        try {
            vcmlToFiniteVolumeInput(vcmlFile, simulation_name, workingDir);
            ZipFile zip = new ZipFile(zipFile);
            for (File file : workingDir.listFiles()) {
                zip.addFile(file);
            }
            zip.close();
            return zipFile;
        }catch (XmlParseException | MappingException | SolverException | ExpressionException e){
            throw new UnprocessableContentWebException("Error processing spatial model: "+e.getMessage(), e);
        } finally {
            for (File file: workingDir.listFiles()){
                file.delete();
            }
            workingDir.delete();
        }
    }

    public static void vcmlToFiniteVolumeInput(File vcmlFile, String simulation_name, File outputDir) throws XmlParseException, MappingException, SolverException, ExpressionException {
        SBMLExporter.MemoryVCLogger vcl = new SBMLExporter.MemoryVCLogger();
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlFile));
        bioModel.updateAll(false);
        Simulation sim = bioModel.getSimulation(simulation_name);
        FiniteVolumeRunUtil.writeInputFilesOnly(outputDir, sim);
    }

}
