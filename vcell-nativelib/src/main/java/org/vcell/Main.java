package org.vcell;

import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.sbml.FiniteVolumeRunUtil;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.SBMLImporter;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void sbmlToFiniteVolumeInput(File sbmlFile, File outputDir) throws FileNotFoundException, MappingException, PropertyVetoException, SolverException, ExpressionException, VCLoggerException {
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
            sbmlToFiniteVolumeInput(sbml_file, new File(args[1]));
            System.out.println("Hello, World!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("Error processing spatial model", e);
        }
    }
}