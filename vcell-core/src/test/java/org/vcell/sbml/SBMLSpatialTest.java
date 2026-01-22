package org.vcell.sbml;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
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
        PropertyLoader.setProperty(PropertyLoader.installationRoot, "..");
        Logger logger = LogManager.getLogger(SBMLExporter.class);
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(logger.getName());

        // If this logger doesn't have its own config, create one
        if (!loggerConfig.getName().equals(logger.getName())) {
            loggerConfig = new LoggerConfig(logger.getName(), Level.DEBUG, true);
            config.addLogger(logger.getName(), loggerConfig);
        }
        // Make a Console Appender
        org.apache.logging.log4j.core.appender.ConsoleAppender appender = org.apache.logging.log4j.core.appender.ConsoleAppender.newBuilder()
                .setName("DefaultConsole")
                .setTarget(org.apache.logging.log4j.core.appender.ConsoleAppender.Target.SYSTEM_OUT)
                .setLayout(PatternLayout.newBuilder().withPattern("%d{HH:mm:ss} %-5level %c - %msg%n").withConfiguration(config).build())
                .setConfiguration(config)
                .build();
        appender.start();

        loggerConfig.addAppender(appender, logger.getLevel(), null);
        // Update loggers
        context.updateLoggers();
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
			FiniteVolumeRunUtil.writeInputFilesOnly(workingDir, sim);
			printWorkingDir();

			System.out.println("ALL FILES INCLUDING OUTPUT");
			FiniteVolumeRunUtil.simulate(workingDir, sim);
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
