package org.vcell;

import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.vcell.sbml.FiniteVolumeRunUtil;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.SBMLImporter;

import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;


public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    // Store allocated memory to prevent premature deallocation
    private static final ConcurrentHashMap<Long, CTypeConversion.CCharPointerHolder> allocatedMemory = new ConcurrentHashMap<>();


    private static CCharPointer createString(String str) {
        CTypeConversion.CCharPointerHolder holder = CTypeConversion.toCString(str);
        CCharPointer ptr = holder.get();
        allocatedMemory.put(ptr.rawValue(), holder);
        return ptr;
    }


    @CEntryPoint(name = "freeString", documentation = "Release memory allocated for a string")
    public static void freeString(
            IsolateThread thread,
            CCharPointer ptr) {
        if (ptr.isNonNull()) {
            allocatedMemory.remove(ptr.rawValue());
        }
    }

    // serialized in JSON and returned as a String (CCharPointer)
    public record ReturnValue(boolean success, String message) {
    }


    @CEntryPoint(
            name = "vcmlToFiniteVolumeInput",
            documentation = """
                    Converts VCML file into Finite Volume Input files.
                      vcml_content: text of VCML XML document
                      output_dir_path: path to the output directory
                      Returns a JSON string with success status and message"""
    )
    public static CCharPointer entrypoint_vcmlToFiniteVolumeInput(
            IsolateThread thread,
            CCharPointer vcml_content,
            CCharPointer simulation_name,
            CCharPointer output_dir_path) {
        ReturnValue returnValue;
        try {
            String vcmlContentStr = CTypeConversion.toJavaString(vcml_content);
            String simulationName = CTypeConversion.toJavaString(simulation_name);
            String outputDirPathStr = CTypeConversion.toJavaString(output_dir_path);

            vcmlToFiniteVolumeInput(vcmlContentStr, simulationName, new File(outputDirPathStr));
            returnValue = new ReturnValue(true, "Success");
        }catch (Throwable t) {
            logger.error("Error processing spatial model", t);
            returnValue = new ReturnValue(false, t.getMessage());
        }
        // return result as a json string
        Gson gson = new Gson();
        String json = gson.toJson(returnValue);
        System.out.println("Returning: " + json);
        logger.info("Returning: " + json);
        return createString(json);
    }

    @CEntryPoint(
            name = "sbmlToFiniteVolumeInput",
            documentation = """
                    Converts SBML file into Finite Volume Input files
                      sbml_content: text content of SBML XML document
                      output_dir_path: path to the output directory
                      Returns a JSON string with success status and message"""
    )
    public static CCharPointer entrypoint_sbmlToFiniteVolumeInput(
            IsolateThread thread,
            CCharPointer sbml_content,
            CCharPointer output_dir_path) {
        ReturnValue returnValue;
        try {
            String sbmlContent = CTypeConversion.toJavaString(sbml_content);
            String outputDirPathStr = CTypeConversion.toJavaString(output_dir_path);
            sbmlToFiniteVolumeInput(sbmlContent, new File(outputDirPathStr));
            returnValue = new ReturnValue(true, "Success");
        }catch (Throwable t) {
            logger.error("Error processing spatial model", t);
            returnValue = new ReturnValue(false, t.getMessage());
        }
        // return result as a json string
        String json = new Gson().toJson(returnValue);
        System.out.println("Returning: " + json);
        logger.info("Returning: " + json);
        return createString(json);
    }


    public static void vcmlToFiniteVolumeInput(String vcml_content, String simulation_name, File outputDir) throws XmlParseException, MappingException, SolverException, ExpressionException {
        GeometrySpec.avoidAWTImageCreation = true;
        VCMongoMessage.enabled = false;
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcml_content));
        bioModel.updateAll(false);
        Simulation sim = bioModel.getSimulation(simulation_name);
        FiniteVolumeRunUtil.writeInputFilesOnly(outputDir, sim);
    }


    public static void sbmlToFiniteVolumeInput(String sbml_content, File outputDir) throws MappingException, PropertyVetoException, SolverException, ExpressionException, VCLoggerException {
        GeometrySpec.avoidAWTImageCreation = true;
        VCMongoMessage.enabled = false;
        SBMLExporter.MemoryVCLogger vcl = new SBMLExporter.MemoryVCLogger();
        boolean bValidateSBML = true;
        // input stream from sbml_content String
        InputStream is = new ByteArrayInputStream(sbml_content.getBytes());
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

        FiniteVolumeRunUtil.writeInputFilesOnly(outputDir, sim);
    }



    // Input Goes as Follows: SBML Input, Output dir
    //    "/Users/evalencia/Documents/VCell_Repositories/vcell/vcell-rest/src/test/resources/TinySpacialProject_Application0.xml"
    //    "/Users/evalencia/Documents/VCell_Repositories/vcell/vcell-nativelib/target/sbml-input"
    public static void main(String[] args) {
        try {
            logger.info("Logger logging");
            PropertyLoader.setProperty(PropertyLoader.vcellServerIDProperty, "none");
            PropertyLoader.setProperty(PropertyLoader.mongodbDatabase, "none");
            File sbml_file = new File(args[0]);
            // read sbml_file and create a string object
            try (FileInputStream fis = new FileInputStream(sbml_file)) {
                byte[] data = fis.readAllBytes();
                logger.info("Read " + data.length + " bytes from " + sbml_file);
                String sbml_str = new String(data);
                sbmlToFiniteVolumeInput(sbml_str, new File(args[1]));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("Error processing spatial model", e);
        }
    }
}