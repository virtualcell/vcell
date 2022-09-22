package org.vcell.sbml;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import com.google.common.io.Files;
import org.jlibsedml.SEDMLDocument;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sedml.*;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class SEDMLExporterTest {

	@Rule
	public final ProvideSystemProperty myPropertyHasMyValue = new ProvideSystemProperty("vcell.installDir", "..");

	private String filename;

	public SEDMLExporterTest(String filename){
		this.filename = filename;
	}

	public enum FAULT {
		SKIP,
		JAVA_HEAP_SPACE,
		EXPRESSIONS_DIFFERENT,
		MATHOVERRIDES_INVALID,
		TOO_SLOW,
		GEOMETRYSPEC_DIFFERENT,
		NULL_POINTER_EXCEPTION
	};

	@Parameterized.Parameters
	public static Collection<String> testCases() {
		HashMap<String, FAULT> faults = new HashMap();
		faults.put("biomodel_102061382.vcml", FAULT.EXPRESSIONS_DIFFERENT);
		faults.put("biomodel_102061382.vcml", FAULT.EXPRESSIONS_DIFFERENT);
		faults.put("biomodel_101963252.vcml", FAULT.JAVA_HEAP_SPACE);
		faults.put("biomodel_10829774.vcml", FAULT.MATHOVERRIDES_INVALID);
//		faults.put("biomodel_101962320.vcml", FAULT.TOO_SLOW); // 7s
//		faults.put("biomodel_101981216.vcml", FAULT.TOO_SLOW); // 10s
		faults.put("biomodel_12119723.vcml", FAULT.GEOMETRYSPEC_DIFFERENT);
		faults.put("biomodel_123269393.vcml", FAULT.MATHOVERRIDES_INVALID);
		faults.put("biomodel_124562627.vcml", FAULT.NULL_POINTER_EXCEPTION);
		faults.put("biomodel_14647285.vcml", FAULT.MATHOVERRIDES_INVALID);

		return Arrays.stream(VcmlTestSuiteFiles.getVcmlTestCases()).filter(t -> !faults.containsKey(t)).collect(Collectors.toList());
	}

	@Test
	public void test_parse_vcml() throws XmlParseException, PropertyVetoException {
		InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);
		String vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
				.lines().collect(Collectors.joining("\n"));
		BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));

		int sedmlLevel = 1;
		int sedmlVersion = 2;
		Predicate<Simulation> simulationExportFilter = sim -> true;
		List<Simulation> simsToExport = Arrays.stream(bioModel.getSimulations()).filter(simulationExportFilter).collect(Collectors.toList());

		// we replace the obsolete solver with the fully supported equivalent
		for (Simulation simulation : simsToExport) {
			if (simulation.getSolverTaskDescription().getSolverDescription().equals(SolverDescription.FiniteVolume)) {
				simulation.getSolverTaskDescription().setSolverDescription(SolverDescription.SundialsPDE);
			}
		}
		File outputDir = Files.createTempDir();
		String jsonFullyQualifiedName = new File(outputDir, filename+".json").getAbsolutePath();
		System.out.println(jsonFullyQualifiedName);
		SEDMLExporter sedmlExporter = new SEDMLExporter(filename, bioModel, sedmlLevel, sedmlVersion, simsToExport, jsonFullyQualifiedName);
		boolean bValidate = true;
		SEDMLDocument sedmlDocument = sedmlExporter.getSEDMLDocument(outputDir.getAbsolutePath(), filename.replace(".vcml",""),
				ModelFormat.SBML, true, bValidate);

		SEDMLRecorder sedmlLogger = sedmlExporter.getSedmlLogger();
		for (SEDMLTaskRecord sedmlTaskRecord : sedmlLogger.getLogs()){
			boolean bFailed = false;
			switch (sedmlTaskRecord.getTaskType()){
				case BIOMODEL: {
					break;
				}
				case SIMCONTEXT:{
					bFailed = sedmlTaskRecord.getTaskResult()==TaskResult.FAILED &&
							(sedmlTaskRecord.getException()==null || !sedmlTaskRecord.getException().getClass().equals(UnsupportedSbmlExportException.class));
					break;
				}
				case UNITS:
				case SIMULATION:{
					bFailed = sedmlTaskRecord.getTaskResult()==TaskResult.FAILED;
					break;
				}
			}
			Assert.assertFalse(sedmlTaskRecord.getCSV(), bFailed);
		}
	}

}
