package org.vcell.sbml;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;


import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sbml.vcell.SBMLImporter;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import org.vcell.test.SBML_IT;

@Category(SBML_IT.class)
@RunWith(Parameterized.class)
public class SBMLImporterTest {

	private String fileName;

	public SBMLImporterTest(String fileName){
		this.fileName = fileName;
	}


	public static Map<String, SBMLTestSuiteImportTest.FAULT> knownFaults() {
		HashMap<String, SBMLTestSuiteImportTest.FAULT> faults = new HashMap<>();
//		faults.put(6, FAULT.RESERVED_WORD);
		return faults;
	}


	@Parameterized.Parameters
	public static Collection<String> testCases() {
//		IntPredicate expressionBindingFilter = (t) -> knownFaults().containsKey(t) && knownFaults().get(t) == FAULT.EXPRESSION_BINDING;
		Predicate<String> allTestsFilter = (t) -> true;
//		IntPredicate allFailures = (t) -> knownFaults().containsKey(t) && skipFilter.test(t);
		Predicate<String> oneModelFilter = (t) -> t.equals("");
		return Arrays.stream(SBMLTestFiles.allTestFiles).filter(allTestsFilter).collect(Collectors.toList());
	}

	@Test
	public void testBioModelsCuratedImport() throws Exception{
		InputStream sbmlInputStream = SBMLTestFiles.getSBMLTestCase(fileName);
		System.out.println("testing "+fileName);
		VCLogger vcl = new TLogger();
		SBMLImporter importer = new SBMLImporter(sbmlInputStream, vcl, true);
		BioModel bioModel = importer.getBioModel();
		bioModel.updateAll(false);
	}

}
