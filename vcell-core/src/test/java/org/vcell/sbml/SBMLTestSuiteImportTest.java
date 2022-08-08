package org.vcell.sbml;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sbml.vcell.SBMLImporter;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class SBMLTestSuiteImportTest {
	private Integer testCase;

	public SBMLTestSuiteImportTest(Integer testCase) {
		this.testCase = testCase;
	}

	@Parameterized.Parameters
	public static Collection<Integer> testCases() {
		HashMap<Integer,FAULT> faults = new HashMap();
		faults.put(22, FAULT.NONINTEGER_STOICH);
		faults.put(45, FAULT.COMPARTMENT_1D);
		faults.put(39, FAULT.ALGEBRAIC_RULES);
		faults.put(40, FAULT.ALGEBRAIC_RULES);
		faults.put(46, FAULT.COMPARTMENT_1D);
		faults.put(47, FAULT.COMPARTMENT_1D);
		faults.put(48, FAULT.COMPARTMENT_0D);
		faults.put(49, FAULT.COMPARTMENT_0D);
		faults.put(50, FAULT.COMPARTMENT_0D);
		faults.put(58, FAULT.JSBML_ERROR);
		faults.put(97, FAULT.COMPARTMENT_0D);
		faults.put(99, FAULT.COMPARTMENT_1D);
		faults.put(100, FAULT.COMPARTMENT_0D);
		faults.put(102, FAULT.COMPARTMENT_1D);
		faults.put(103, FAULT.COMPARTMENT_0D);
		faults.put(133, FAULT.JSBML_ERROR);
		faults.put(153, FAULT.COMPARTMENT_1D);
		faults.put(154, FAULT.COMPARTMENT_1D);
		faults.put(155, FAULT.COMPARTMENT_0D);
		faults.put(156, FAULT.COMPARTMENT_0D);
		faults.put(182, FAULT.ALGEBRAIC_RULES);
		faults.put(184, FAULT.ALGEBRAIC_RULES);
		faults.put(201, FAULT.XOR_MISSING);
		faults.put(205, FAULT.COMPARTMENT_DIM_MISSING);
		faults.put(206, FAULT.COMPARTMENT_1D);
		faults.put(207, FAULT.COMPARTMENT_1D);
		faults.put(223, FAULT.COMPARTMENT_1D);
		faults.put(224, FAULT.COMPARTMENT_1D);
		faults.put(225, FAULT.COMPARTMENT_1D);
		faults.put(226, FAULT.COMPARTMENT_1D);
		faults.put(227, FAULT.COMPARTMENT_1D);
		faults.put(228, FAULT.COMPARTMENT_1D);
		faults.put(229, FAULT.COMPARTMENT_1D);
		faults.put(230, FAULT.COMPARTMENT_1D);
		faults.put(231, FAULT.COMPARTMENT_1D);
		faults.put(232, FAULT.COMPARTMENT_1D);
		faults.put(233, FAULT.COMPARTMENT_1D);
		faults.put(234, FAULT.COMPARTMENT_1D);
		faults.put(235, FAULT.COMPARTMENT_1D);
		faults.put(236, FAULT.COMPARTMENT_1D);
		faults.put(237, FAULT.COMPARTMENT_1D);
		faults.put(238, FAULT.COMPARTMENT_0D);
		faults.put(239, FAULT.COMPARTMENT_0D);
		faults.put(240, FAULT.COMPARTMENT_0D);
		faults.put(241, FAULT.COMPARTMENT_0D);
		faults.put(242, FAULT.COMPARTMENT_0D);
		faults.put(243, FAULT.COMPARTMENT_0D);
		faults.put(244, FAULT.COMPARTMENT_0D);
		faults.put(245, FAULT.COMPARTMENT_0D);
		faults.put(246, FAULT.COMPARTMENT_0D);
		faults.put(247, FAULT.COMPARTMENT_0D);
		faults.put(254, FAULT.COMPARTMENT_1D);
		faults.put(255, FAULT.COMPARTMENT_1D);
		faults.put(256, FAULT.COMPARTMENT_1D);
		faults.put(257, FAULT.COMPARTMENT_0D);
		faults.put(258, FAULT.COMPARTMENT_0D);
		faults.put(259, FAULT.COMPARTMENT_0D);
		faults.put(261, FAULT.COMPARTMENT_1D);
		faults.put(262, FAULT.COMPARTMENT_0D);
		faults.put(264, FAULT.COMPARTMENT_1D);
		faults.put(265, FAULT.COMPARTMENT_0D);
		faults.put(267, FAULT.COMPARTMENT_1D);
		faults.put(268, FAULT.COMPARTMENT_0D);
		faults.put(279, FAULT.XOR_MISSING);
		faults.put(281, FAULT.COMPARTMENT_1D);
		faults.put(283, FAULT.COMPARTMENT_1D);
		faults.put(285, FAULT.COMPARTMENT_1D);
		faults.put(320, FAULT.COMPARTMENT_1D);
		faults.put(321, FAULT.COMPARTMENT_0D);
		faults.put(323, FAULT.COMPARTMENT_1D);
		faults.put(324, FAULT.COMPARTMENT_0D);
		faults.put(326, FAULT.COMPARTMENT_1D);
		faults.put(327, FAULT.COMPARTMENT_0D);
		faults.put(361, FAULT.COMPARTMENT_1D);
		faults.put(362, FAULT.COMPARTMENT_0D);
		faults.put(364, FAULT.COMPARTMENT_1D);
		faults.put(365, FAULT.COMPARTMENT_0D);
		faults.put(367, FAULT.COMPARTMENT_1D);
		faults.put(368, FAULT.COMPARTMENT_0D);
		faults.put(418, FAULT.COMPARTMENT_1D);
		faults.put(419, FAULT.COMPARTMENT_0D);
		faults.put(421, FAULT.COMPARTMENT_1D);
		faults.put(422, FAULT.COMPARTMENT_0D);
		faults.put(424, FAULT.COMPARTMENT_1D);
		faults.put(425, FAULT.COMPARTMENT_0D);
		faults.put(436, FAULT.SBML_SYMBOL_MAPPING); // Failed to translate SBML model into BioModel: sbmlSid is already bound to runtime ste delay
		faults.put(437, FAULT.SBML_SYMBOL_MAPPING); // Failed to translate SBML model into BioModel: sbmlSid is already bound to runtime ste delay
		faults.put(486, FAULT.COMPARTMENT_1D);
		faults.put(487, FAULT.COMPARTMENT_1D);
		faults.put(488, FAULT.COMPARTMENT_1D);
		faults.put(489, FAULT.COMPARTMENT_0D);
		faults.put(490, FAULT.COMPARTMENT_0D);
		faults.put(491, FAULT.COMPARTMENT_0D);
		faults.put(525, FAULT.JSBML_ERROR);
		faults.put(526, FAULT.JSBML_ERROR);
		faults.put(527, FAULT.JSBML_ERROR);

		return Arrays.stream(SbmlTestSuiteFiles.getSbmlTestSuiteCases()).boxed().filter(t -> !faults.containsKey(t)).collect(Collectors.toList());
	}

	public enum FAULT {
		SBML_SYMBOL_MAPPING,

		RESERVED_WORD,
		DELAY,
		NONINTEGER_STOICH,
		COMPARTMENT_0D,
		COMPARTMENT_1D,
		COMPARTMENT_DIM_MISSING,
		ALGEBRAIC_RULES,
		INCONSISTENT_UNIT_SYSTEM,
		EXPRESSION_BINDING_EXCEPTION,
		XOR_MISSING,
		JSBML_ERROR		// seems like a bug in jsbml  RenderParser.processEndDocument() ... line 403 ... wrong constant for extension name
	};



	@Test
	public void testSbmlTestSuiteImport() throws XMLStreamException, IOException{
		HashMap<Integer,FAULT> faults = new HashMap();
		faults.put(22, FAULT.NONINTEGER_STOICH);

		TLogger vcl = new TLogger();
		InputStream testFileInputStream = SbmlTestSuiteFiles.getSbmlTestCase(testCase);
		boolean bValidateSBML = true;
		SBMLImporter importer = new SBMLImporter(testFileInputStream, vcl, bValidateSBML);
		BioModel bioModel = importer.getBioModel();
		Assert.assertArrayEquals("testCase "+testCase+" failed", vcl.highPriority.toArray(),new String[0]);
	}
}
