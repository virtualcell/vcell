package org.vcell.sbml;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.*;
import cbit.vcell.solvers.FVSolverStandalone;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.SBMLImporter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Tag("SBML_IT")
public class SBMLSpatialTest {

	private static File workingDir;

	@BeforeAll
	public static void before() throws IOException {
		PropertyLoader.setProperty(PropertyLoader.installationRoot, "..");
		Logger.getLogger(SBMLExporter.class).addAppender(new ConsoleAppender());
		// create temporary working directory
		workingDir = Files.createTempDirectory("sbml-test-suite-working-dir-").toFile();
	}

	@AfterAll
	public static void after() {
		// delete temporary working directory
		try {
			Files.delete(workingDir.toPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static InputStream getFileFromResourceAsStream(String path) {
		InputStream inputStream = SBMLSpatialTest.class.getResourceAsStream(path);
		if (inputStream == null) {
			throw new RuntimeException("file not found! " + path);
		} else {
			return inputStream;
		}
	}

	@Test
	public void testSbmlTestSuiteImport() throws Exception{
		TLogger vcl = new TLogger();
		InputStream testFileInputStream = getFileFromResourceAsStream("/sbmlspatial/TinySpacialProject_Application0.xml"); // endpoint arg
		boolean bValidateSBML = true;
		SBMLImporter importer = new SBMLImporter(testFileInputStream, vcl, bValidateSBML);
		try {
			BioModel bioModel = importer.getBioModel();
			bioModel.updateAll(false);

			final double duration = 5.0;  // endpoint arg
			final double time_step = 0.1;  // endpoint arg
			//final ISize meshSize = new ISize(10, 10, 10);  // future endpoint arg
			SimulationContext simContext = bioModel.getSimulationContext(0);
			Simulation sim = new Simulation(simContext.getMathDescription(), simContext);
			sim.getSolverTaskDescription().setTimeBounds(new TimeBounds(0.0, duration));
			sim.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(time_step));

			System.out.println("INPUT FILES ONLY");
			SBMLFakeSpatialBioModel.writeInputFilesOnly(workingDir, sim);
			printWorkingDir();

			System.out.println("ALL FILES INCLUDING OUTPUT");
			SBMLFakeSpatialBioModel.simulate(workingDir, sim);
			printWorkingDir();

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private static void printWorkingDir() {
		// print contents of workingDir
		File[] files = workingDir.listFiles();
		for (File file : files) {
			System.out.println(file.getName());
		}
	}

}
