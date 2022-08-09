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
		faults.put(96, FAULT.COMPARTMENT_1D);
		faults.put(97, FAULT.COMPARTMENT_0D);
		faults.put(99, FAULT.COMPARTMENT_1D);
		faults.put(100, FAULT.COMPARTMENT_0D);
		faults.put(102, FAULT.COMPARTMENT_1D);
		faults.put(103, FAULT.COMPARTMENT_0D);
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
		// consider using ObjectReferenceWrapper.java to avoid .equals() and .hashCode() for getSte() in SBMLSymbolMapping
		faults.put(436, FAULT.SBML_SYMBOL_MAPPING); // JSBML's event delay objects don't hash properly, delay1.equals(delay2) == true for different events
		faults.put(437, FAULT.SBML_SYMBOL_MAPPING); // JSBML's event delay objects don't hash properly, delay1.equals(delay2) == true for different events
		faults.put(486, FAULT.COMPARTMENT_1D);
		faults.put(487, FAULT.COMPARTMENT_1D);
		faults.put(488, FAULT.COMPARTMENT_1D);
		faults.put(489, FAULT.COMPARTMENT_0D);
		faults.put(490, FAULT.COMPARTMENT_0D);
		faults.put(491, FAULT.COMPARTMENT_0D);
		faults.put(531, FAULT.ALGEBRAIC_RULES);
		faults.put(532, FAULT.ALGEBRAIC_RULES);
		faults.put(533, FAULT.ALGEBRAIC_RULES);
		faults.put(534, FAULT.ALGEBRAIC_RULES);
		faults.put(535, FAULT.ALGEBRAIC_RULES);
		faults.put(536, FAULT.ALGEBRAIC_RULES);
		faults.put(537, FAULT.ALGEBRAIC_RULES);
		faults.put(538, FAULT.ALGEBRAIC_RULES);
		faults.put(539, FAULT.ALGEBRAIC_RULES);
		faults.put(540, FAULT.ALGEBRAIC_RULES);
		faults.put(541, FAULT.ALGEBRAIC_RULES);
		faults.put(542, FAULT.ALGEBRAIC_RULES);
		faults.put(543, FAULT.COMPARTMENT_0D);
		faults.put(544, FAULT.ALGEBRAIC_RULES);
		faults.put(545, FAULT.ALGEBRAIC_RULES);
		faults.put(546, FAULT.COMPARTMENT_0D);
		faults.put(547, FAULT.ALGEBRAIC_RULES);
		faults.put(548, FAULT.ALGEBRAIC_RULES);
		faults.put(549, FAULT.ALGEBRAIC_RULES);
		faults.put(550, FAULT.ALGEBRAIC_RULES);
		faults.put(551, FAULT.ALGEBRAIC_RULES);
		faults.put(552, FAULT.ALGEBRAIC_RULES);
		faults.put(553, FAULT.ALGEBRAIC_RULES);
		faults.put(554, FAULT.ALGEBRAIC_RULES);
		faults.put(555, FAULT.ALGEBRAIC_RULES);
		faults.put(556, FAULT.ALGEBRAIC_RULES);
		faults.put(557, FAULT.ALGEBRAIC_RULES);
		faults.put(558, FAULT.ALGEBRAIC_RULES);
		faults.put(559, FAULT.ALGEBRAIC_RULES);
		faults.put(560, FAULT.ALGEBRAIC_RULES);
		faults.put(561, FAULT.ALGEBRAIC_RULES);
		faults.put(562, FAULT.ALGEBRAIC_RULES);
		faults.put(563, FAULT.ALGEBRAIC_RULES);
		faults.put(564, FAULT.ALGEBRAIC_RULES);
		faults.put(565, FAULT.ALGEBRAIC_RULES);
		faults.put(566, FAULT.ALGEBRAIC_RULES);
		faults.put(567, FAULT.ALGEBRAIC_RULES);
		faults.put(568, FAULT.ALGEBRAIC_RULES);
		faults.put(569, FAULT.ALGEBRAIC_RULES);
		faults.put(570, FAULT.ALGEBRAIC_RULES);
		faults.put(571, FAULT.ALGEBRAIC_RULES);
		faults.put(572, FAULT.ALGEBRAIC_RULES);
		faults.put(573, FAULT.ALGEBRAIC_RULES);
		faults.put(574, FAULT.ALGEBRAIC_RULES);
		faults.put(575, FAULT.ALGEBRAIC_RULES);
		faults.put(576, FAULT.ALGEBRAIC_RULES);
		faults.put(592, FAULT.COMPARTMENT_1D);
		faults.put(593, FAULT.COMPARTMENT_1D);
		faults.put(594, FAULT.COMPARTMENT_1D);
		faults.put(613, FAULT.ALGEBRAIC_RULES);
		faults.put(614, FAULT.ALGEBRAIC_RULES);
		faults.put(615, FAULT.ALGEBRAIC_RULES);
		faults.put(628, FAULT.ALGEBRAIC_RULES);
		faults.put(629, FAULT.ALGEBRAIC_RULES);
		faults.put(630, FAULT.ALGEBRAIC_RULES);
		faults.put(658, FAULT.ALGEBRAIC_RULES);
		faults.put(659, FAULT.ALGEBRAIC_RULES);
		faults.put(660, FAULT.ALGEBRAIC_RULES);
		faults.put(661, FAULT.ALGEBRAIC_RULES);
		faults.put(662, FAULT.ALGEBRAIC_RULES);
		faults.put(663, FAULT.ALGEBRAIC_RULES);
		faults.put(664, FAULT.ALGEBRAIC_RULES);
		faults.put(665, FAULT.ALGEBRAIC_RULES);
		faults.put(666, FAULT.ALGEBRAIC_RULES);
		faults.put(673, FAULT.ALGEBRAIC_RULES);
		faults.put(674, FAULT.ALGEBRAIC_RULES);
		faults.put(675, FAULT.ALGEBRAIC_RULES);
		faults.put(687, FAULT.ALGEBRAIC_RULES);
		faults.put(695, FAULT.ALGEBRAIC_RULES);
		faults.put(696, FAULT.ALGEBRAIC_RULES);
		faults.put(705, FAULT.ALGEBRAIC_RULES);
		faults.put(760, FAULT.ALGEBRAIC_RULES);
		faults.put(761, FAULT.ALGEBRAIC_RULES);
		faults.put(762, FAULT.ALGEBRAIC_RULES);
		faults.put(777, FAULT.ALGEBRAIC_RULES);
		faults.put(778, FAULT.ALGEBRAIC_RULES);
		faults.put(779, FAULT.ALGEBRAIC_RULES);
		faults.put(780, FAULT.ALGEBRAIC_RULES);
		faults.put(844, FAULT.ALGEBRAIC_RULES);
		faults.put(876, FAULT.ALGEBRAIC_RULES);
		faults.put(905, FAULT.COMPARTMENT_1D);
		faults.put(907, FAULT.COMPARTMENT_1D);
		faults.put(909, FAULT.COMPARTMENT_1D);
		faults.put(910, FAULT.COMPARTMENT_1D);
		faults.put(915, FAULT.COMPARTMENT_1D);
		faults.put(917, FAULT.COMPARTMENT_1D);
		faults.put(919, FAULT.COMPARTMENT_1D);
		faults.put(930, FAULT.SBML_SYMBOL_MAPPING); // this time it is the event trigger which fails mapping.
		faults.put(931, FAULT.SBML_SYMBOL_MAPPING); // this time it is the event trigger which fails mapping.
		faults.put(934, FAULT.SBML_SYMBOL_MAPPING); // this time it is the event trigger which fails mapping.
		faults.put(935, FAULT.SBML_SYMBOL_MAPPING); // this time it is the event trigger which fails mapping.

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
	public void testSbmlTestSuiteImport() throws Exception{
		HashMap<Integer,FAULT> faults = new HashMap();
		faults.put(22, FAULT.NONINTEGER_STOICH);

		TLogger vcl = new TLogger();
		InputStream testFileInputStream = SbmlTestSuiteFiles.getSbmlTestCase(testCase);
		boolean bValidateSBML = true;
		SBMLImporter importer = new SBMLImporter(testFileInputStream, vcl, bValidateSBML);
		try {
			BioModel bioModel = importer.getBioModel();
		}catch (Exception e){
			System.err.println("unexpected exception in test case "+testCase);
			throw e;
		}
		Assert.assertArrayEquals("testCase "+testCase+" failed", new String[0], vcl.highPriority.toArray());
	}
}
