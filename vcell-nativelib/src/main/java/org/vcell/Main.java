package org.vcell;

import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;
import org.apache.logging.log4j.Logger;
import org.vcell.sbml.SBMLFakeSpatialBioModel;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.SBMLImporter;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Main {

    private static Logger logger = org.apache.logging.log4j.LogManager.getLogger(Main.class);

    public static void sbmlToFiniteVolumeInput(File sbmlFile, File outputDir) throws FileNotFoundException, MappingException, VCLoggerException, PropertyVetoException, SolverException, ExpressionException {
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




    public static void main(String[] args) {
        try {
            File sbml_file = new File("/Users/jimschaff/Documents/workspace/vcell/vcell-rest/src/test/resources/TinySpacialProject_Application0.xml");
            sbmlToFiniteVolumeInput(sbml_file, new File("/Users/jimschaff/Documents/workspace/vcell/vcell-rest/src/test/resources/"));
            System.out.println("Hello, World!");
        } catch (Exception e) {
            logger.error("Error processing spatial model", e);
        }
    }
}