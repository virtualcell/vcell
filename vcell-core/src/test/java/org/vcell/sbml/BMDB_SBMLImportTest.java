package org.vcell.sbml;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.vcell.sbml.vcell.SBMLImporter;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

@Tag("SBML_IT")
public class BMDB_SBMLImportTest {

	private static class SBMLHighPriorityIssueException extends RuntimeException {
		SBMLHighPriorityIssueException(String msg) {
			super(msg);
		}
	}

	private static class TestVCLogger extends VCLogger {
		public final List<String> highPriority = new ArrayList<>();

		@Override
		public boolean hasMessages() {
			return false;
		}

		@Override
		public void sendAllMessages() {
		}

		@Override
		public void sendMessage(Priority p, ErrorType et, String message)
				throws VCLoggerException {
			String msg = p + " " + et + ": "  + message;
			if (p == Priority.HighPriority) {
				highPriority.add(msg);
				throw new SBMLHighPriorityIssueException(msg);
			}
		}
	}

	private static File codeKnownProblemFile;
	private static File csvKnownProblemFile;

	@BeforeAll
	public static void setup() throws IOException {
		codeKnownProblemFile = Files.createTempFile("MathGenCompareTest","code_KnownProblems").toFile();
		csvKnownProblemFile = Files.createTempFile("MathGenCompareTest","csv_KnownProblems").toFile();
		System.err.println("code known problem file: "+codeKnownProblemFile.getAbsolutePath());
		System.err.println("csv known problem file: "+csvKnownProblemFile.getAbsolutePath());
	}

	@AfterAll
	public static void teardown() {
		System.err.println("code known problem file: "+codeKnownProblemFile.getAbsolutePath());
		System.err.println("csv known problem file: "+csvKnownProblemFile.getAbsolutePath());
	}

	/**
	 * 	each model number in the slowTestSet is not included in the unit test (move to integration testing)
	 */
	public static Set<Integer> slowModelSet() {
		Set<Integer> slowModels = new HashSet<>();
		slowModels.add(235); // 3 minutes 19 seconds - it passes
		slowModels.add(255); // 6 minutes 33 seconds - it passes
		slowModels.add(595); // > 5 minutes - unknown if it would pass, didn't wait long enough
		return slowModels;
	}

	public static Map<Integer, SBMLTestSuiteTest.FAULT> knownFaults() {
		HashMap<Integer, SBMLTestSuiteTest.FAULT> faults = new HashMap();
		faults.put(24, SBMLTestSuiteTest.FAULT.DELAY);  // cause:  UnsupportedConstruct: unsupported SBML element 'delay' in expression ' <math><apply><times/><ci> compartment_0000004 </ci><ci> rP </ci><apply><power/><apply><csymbol encoding="tex
		faults.put(25, SBMLTestSuiteTest.FAULT.DELAY);  // cause:  UnsupportedConstruct: unsupported SBML element 'delay' in expression ' <math><piecewise><piece><cn> 0 </cn><apply><lt/><apply><minus/><apply><csymbol encoding="text" definitionURL
		faults.put(34, SBMLTestSuiteTest.FAULT.DELAY);  // cause:  UnsupportedConstruct: unsupported SBML element 'delay' in expression ' <math><apply><times/><ci> compartment_0000001 </ci><apply><csymbol encoding="text" definitionURL="http://www
		faults.put(39, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.25' for reactant 'CaER' in reaction 'v1') or stoichiometryMath not handled in VCell at this time.
		faults.put(59, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.01' for reactant 'Ca_cyt' in reaction 'Calcium_cyt_Jerp') or stoichiometryMath not handled in VCell at this time.
		faults.put(63, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.5' for reactant 'FDP' in reaction 'Vgol') or stoichiometryMath not handled in VCell at this time.
		faults.put(81, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('9.967E-4' for reactant 'ATP_C' in reaction 'PIP5kinase') or stoichiometryMath not handled in VCell at this time.
		faults.put(112, SBMLTestSuiteTest.FAULT.UNCATEGORIZED);  // cause:  null
		faults.put(145, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.001' for reactant 'Ca_ER' in reaction 'R9') or stoichiometryMath not handled in VCell at this time.
		faults.put(151, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.5' for product 'x13' in reaction 'R23') or stoichiometryMath not handled in VCell at this time.
		faults.put(154, SBMLTestSuiteTest.FAULT.DELAY);  // cause:  UnsupportedConstruct: unsupported SBML element 'delay' in expression ' <math><apply><times/><ci> compartment </ci><ci> beta_y </ci><ci> psi </ci><apply><csymbol encoding="text" de
		faults.put(155, SBMLTestSuiteTest.FAULT.DELAY);  // cause:  UnsupportedConstruct: unsupported SBML element 'delay' in expression ' <math><apply><times/><ci> compartment </ci><ci> beta_y </ci><ci> psi </ci><apply><csymbol encoding="text" de
		faults.put(196, SBMLTestSuiteTest.FAULT.DELAY);  // cause:  UnsupportedConstruct: unsupported SBML element 'delay' in expression ' <math><apply><times/><ci> vM4 </ci><apply><divide/><apply><times/><apply><plus/><cn> 1 </cn><apply><divide/>
		faults.put(199, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.5' for reactant 'NADPH' in reaction 'r4') or stoichiometryMath not handled in VCell at this time.
		faults.put(206, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.1' for product 's6o' in reaction 'v10') or stoichiometryMath not handled in VCell at this time.
		faults.put(232, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.5' for reactant 'O2' in reaction 'vresp') or stoichiometryMath not handled in VCell at this time.
		faults.put(244, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.5' for reactant 'FBP' in reaction 'e_Emp') or stoichiometryMath not handled in VCell at this time.
		faults.put(245, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.978' for product 's_pyr' in reaction 'r1') or stoichiometryMath not handled in VCell at this time.
		faults.put(246, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.001' for product 'Ca_in' in reaction 'vo') or stoichiometryMath not handled in VCell at this time.
		faults.put(248, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Unable to create and add rate rule to VC model : 'Capillary.Capillary' is either not found in your model or is not allowed to be used in the current context. Check that you have p
		faults.put(256, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'XIAP_ini' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that you hav
//		faults.put(264, SBMLTestSuiteTest.FAULT.INCONSISTENT_UNIT_SYSTEM);  // cause:  failed to convert lumped reaction kinetics to distributed: volume substance unit [ng] must be compatible with mole or molecules
		faults.put(305, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'V' to model: 'Fw_1st_step' is either not found in your model or is not allowed to be used in the current context. Check that you have provided the
		faults.put(319, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.02' for product 'gamma' in reaction 'r3') or stoichiometryMath not handled in VCell at this time.
		faults.put(340, SBMLTestSuiteTest.FAULT.MATHML_PARSING);  // cause:  UnsupportedConstruct: error parsing expression ' <math><apply><gt/><piecewise><piece><apply><minus/><csymbol encoding="text" definitionURL="http://www.sbml.org/sbml/symbols/time">
		faults.put(342, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'TGF_beta_dose_mol_per_cell' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context.
		faults.put(353, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('12345.7' for reactant 'cpd_C00369Glc_CS' in reaction 'rn_R02112CS_G2') or stoichiometryMath not handled in VCell at this time.
		faults.put(383, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('1.5' for product 'PGA' in reaction 'PGA_prod_Vo') or stoichiometryMath not handled in VCell at this time.
		faults.put(384, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('1.5' for product 'PGA' in reaction 'PGA_prod_Vo') or stoichiometryMath not handled in VCell at this time.
		faults.put(385, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('1.5' for product 'PGA' in reaction 'PGA_prod_Vo') or stoichiometryMath not handled in VCell at this time.
		faults.put(386, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('1.5' for product 'PGA' in reaction 'PGA_prod_Vo') or stoichiometryMath not handled in VCell at this time.
		faults.put(387, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('1.5' for product 'PGA' in reaction 'PGA_prod_Vo') or stoichiometryMath not handled in VCell at this time.
		faults.put(388, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.6' for product 'Ru5P' in reaction 'GAP2Ru5P') or stoichiometryMath not handled in VCell at this time.
		faults.put(392, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.5' for reactant 'ATP' in reaction 'RuBisCO_6_O2') or stoichiometryMath not handled in VCell at this time.
		faults.put(415, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.574' for product 'species_7' in reaction 'reaction_1') or stoichiometryMath not handled in VCell at this time.
		faults.put(426, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.08' for reactant 'species_31' in reaction 'reaction_28') or stoichiometryMath not handled in VCell at this time.
		faults.put(429, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'parameter_18' to model: 'UNRESOLVED.Size' is either not found in your model or is not allowed to be used in the current context. Check that you hav
		faults.put(457, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'parameter_1' to model: 'UNRESOLVED.Size' is either not found in your model or is not allowed to be used in the current context. Check that you have
		faults.put(463, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('8.5' for reactant 'species_14' in reaction 'reaction_11') or stoichiometryMath not handled in VCell at this time.
		faults.put(468, SBMLTestSuiteTest.FAULT.UNCATEGORIZED);  // cause:  found more than one SBase match for sid=unitime, matched [org.vcell.sbml.vcell.SBMLSymbolMapping$SBaseWrapper@2c4d648b, org.vcell.sbml.vcell.SBMLSymbolMapping$SBaseWrapper@7d7dd2b
		faults.put(469, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('1.5' for reactant 's_1372' in reaction 'r_1230') or stoichiometryMath not handled in VCell at this time.
		faults.put(470, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('1.5' for reactant 's_1372' in reaction 'r_1230') or stoichiometryMath not handled in VCell at this time.
		faults.put(471, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('4.33333333333333' for reactant 's_0056' in reaction 'r_1052') or stoichiometryMath not handled in VCell at this time.
		faults.put(472, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('4.33333333333333' for reactant 's_0056' in reaction 'r_1052') or stoichiometryMath not handled in VCell at this time.
		faults.put(473, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('1.8' for reactant 's_0595' in reaction 'r_1014') or stoichiometryMath not handled in VCell at this time.
		faults.put(474, SBMLTestSuiteTest.FAULT.UNCATEGORIZED);  // cause:  found more than one SBase match for sid=v, matched [org.vcell.sbml.vcell.SBMLSymbolMapping$SBaseWrapper@5d2c4564, org.vcell.sbml.vcell.SBMLSymbolMapping$SBaseWrapper@4f269c94]
		faults.put(496, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('1.1358' for reactant 's_0001' in reaction 'r_1812') or stoichiometryMath not handled in VCell at this time.
		faults.put(497, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('1.1358' for reactant 's_0001' in reaction 'r_1812') or stoichiometryMath not handled in VCell at this time.
		faults.put(499, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_9' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that you
		faults.put(534, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_6' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that you
		faults.put(535, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_3' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that you
		faults.put(536, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_80' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that yo
		faults.put(537, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_40' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that yo
		faults.put(542, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Unable to create and add rate rule to VC model : 'r77' is either not found in your model or is not allowed to be used in the current context. Check that you have provided the corr
		faults.put(547, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Compartment_3' to model: 'UNRESOLVED.Size' is either not found in your model or is not allowed to be used in the current context. Check that you ha
		faults.put(556, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Unable to create and add rate rule to VC model : 'r0' is either not found in your model or is not allowed to be used in the current context. Check that you have provided the corre
		faults.put(562, SBMLTestSuiteTest.FAULT.QUAL_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'qual', which is required for simulating the model but is not supported.
		faults.put(570, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Unable to create and add rate rule to VC model : 'reaction_4' is either not found in your model or is not allowed to be used in the current context. Check that you have provided t
		faults.put(575, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Unable to create and add rate rule to VC model : 'J21' is either not found in your model or is not allowed to be used in the current context. Check that you have provided the corr
		faults.put(577, SBMLTestSuiteTest.FAULT.MATHML_PARSING);  // cause:  UnsupportedConstruct: error parsing expression ' <math><piecewise><piece><cn> 1 </cn><apply><lt/><csymbol encoding="text" definitionURL="http://www.sbml.org/sbml/symbols/time"> ti
		faults.put(578, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Unable to create and add rate rule to VC model : 'v_r33' is either not found in your model or is not allowed to be used in the current context. Check that you have provided the co
		faults.put(589, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_17' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that yo
		faults.put(592, SBMLTestSuiteTest.FAULT.QUAL_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'qual', which is required for simulating the model but is not supported.
		faults.put(593, SBMLTestSuiteTest.FAULT.QUAL_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'qual', which is required for simulating the model but is not supported.
		faults.put(596, SBMLTestSuiteTest.FAULT.UNCATEGORIZED);  // cause:  JSBML Error class org.sbml.jsbml.xml.XMLNode cannot be cast to class org.sbml.jsbml.Annotation
		faults.put(599, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_1' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that you
		faults.put(608, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // cause:  UnsupportedConstruct: Non-integer stoichiometry ('0.5' for reactant 'x_18' in reaction 'R_18') or stoichiometryMath not handled in VCell at this time.
		faults.put(613, SBMLTestSuiteTest.FAULT.COMP_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'comp', which is required for simulating the model but is not supported.
		faults.put(627, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_123' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that y
		faults.put(628, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_8' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that you
		faults.put(632, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'k4b' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that you have pro
		faults.put(696, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_16' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that yo
		faults.put(705, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_21' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that yo
		faults.put(706, SBMLTestSuiteTest.FAULT.UNCATEGORIZED);  // cause:  found more than one SBase match for sid=v, matched [org.vcell.sbml.vcell.SBMLSymbolMapping$SBaseWrapper@67cc48df, org.vcell.sbml.vcell.SBMLSymbolMapping$SBaseWrapper@483ac21f]
		faults.put(710, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_0_0' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that y
		faults.put(731, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Treg_origin_fraction_CD4' to model: 'func_TRegs_Production_from_CD4'		faults.put(739, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_19' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that yo
		faults.put(764, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_3' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that you
		faults.put(775, SBMLTestSuiteTest.FAULT.MATHML_PARSING);  // cause:  Error adding Lambda function UnsupportedConstruct: error parsing expression ' <math><notanumber/></math>': node type 'notanumber' not supported yet
		faults.put(804, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_1' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that you
		faults.put(808, SBMLTestSuiteTest.FAULT.MATHML_PARSING);  // cause:  UnsupportedConstruct: error parsing expression ' <math><piecewise><piece><apply><divide/><ci> ModelValue_28 </ci><ci> ModelValue_30 </ci></apply><apply><and/><apply><and/><apply><
		faults.put(814, SBMLTestSuiteTest.FAULT.MATHML_PARSING);  // cause:  UnsupportedConstruct: error parsing expression ' <math><apply><and/><apply><and/><apply><eq/><piecewise><piece><apply><minus/><csymbol encoding="text" definitionURL="http://www.sb
		faults.put(822, SBMLTestSuiteTest.FAULT.MATHML_PARSING);  // cause:  UnsupportedConstruct: error parsing expression ' <math><apply><and/><apply><geq/><csymbol encoding="text" definitionURL="http://www.sbml.org/sbml/symbols/time"> time </csymbol><cn
		faults.put(828, SBMLTestSuiteTest.FAULT.MATHML_PARSING);  // cause:  UnsupportedConstruct: error parsing expression ' <math><apply><eq/><piecewise><piece><apply><minus/><csymbol encoding="text" definitionURL="http://www.sbml.org/sbml/symbols/time">
		faults.put(829, SBMLTestSuiteTest.FAULT.MATHML_PARSING);  // cause:  UnsupportedConstruct: error parsing expression ' <math><apply><eq/><piecewise><piece><apply><minus/><csymbol encoding="text" definitionURL="http://www.sbml.org/sbml/symbols/time">
		faults.put(832, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_4' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that you
		faults.put(841, SBMLTestSuiteTest.FAULT.DELAY);  // cause:  UnsupportedConstruct: unsupported SBML element 'delay' in expression ' <math><apply><times/><ci> compartment </ci><apply><ci> Function_for_miRNA_Activation_Transcription_Factor </
		faults.put(858, SBMLTestSuiteTest.FAULT.MATHML_PARSING);  // cause:  UnsupportedConstruct: error parsing expression ' <math><apply><minus/><piecewise><piece><apply><minus/><csymbol encoding="text" definitionURL="http://www.sbml.org/sbml/symbols/tim
		faults.put(859, SBMLTestSuiteTest.FAULT.MATHML_PARSING);  // cause:  UnsupportedConstruct: error parsing expression ' <math><apply><minus/><piecewise><piece><apply><minus/><csymbol encoding="text" definitionURL="http://www.sbml.org/sbml/symbols/tim
		faults.put(867, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_9' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that you
		faults.put(872, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'beta' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that you have pr
		faults.put(908, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_2' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that you
		faults.put(925, SBMLTestSuiteTest.FAULT.UNCATEGORIZED);  // cause:  OverallWarning: missing or unexpected value attribute '-Infinity' for SBML object id log_time
		faults.put(961, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'rateOf_re15' to model: 're15' is either not found in your model or is not allowed to be used in the current context. Check that you have provided t
		faults.put(969, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_11' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that yo
		faults.put(1005, SBMLTestSuiteTest.FAULT.MATHML_PARSING);  // cause:  UnsupportedConstruct: error parsing expression ' <math><piecewise><piece><cn> 1 </cn><apply><and/><apply><geq/><piecewise><piece><apply><minus/><csymbol encoding="text" definition
		faults.put(1021, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Metabolite_0' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in the current context. Check that you
		faults.put(1026, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Summary_flux_to_RBC' to model: 'Vin' is either not found in your model or is not allowed to be used in the current context. Check that you have pro
		faults.put(1027, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Compartment_10' to model: 'UNRESOLVED.Size' is either not found in your model or is not allowed to be used in the current context. Check that you h
		faults.put(1028, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Compartment_10' to model: 'UNRESOLVED.Size' is either not found in your model or is not allowed to be used in the current context. Check that you h
		faults.put(1029, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Compartment_10' to model: 'UNRESOLVED.Size' is either not found in your model or is not allowed to be used in the current context. Check that you h
		faults.put(1039, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Compartment_10' to model: 'UNRESOLVED.Size' is either not found in your model or is not allowed to be used in the current context. Check that you h
		faults.put(1040, SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause:  Error binding global parameter 'Summary_flux_to_RBC' to model: 'Vin' is either not found in your model or is not allowed to be used in the current context. Check that you have pro
		faults.put(1046, SBMLTestSuiteTest.FAULT.FBC_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'fbc', which is required for simulating the model but is not supported.
		faults.put(1062, SBMLTestSuiteTest.FAULT.FBC_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'fbc', which is required for simulating the model but is not supported.
		faults.put(1063, SBMLTestSuiteTest.FAULT.FBC_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'fbc', which is required for simulating the model but is not supported.
		faults.put(1064, SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH);  // UnsupportedConstruct: Non-integer stoichiometry ('0.5' for reactant 'M_O2' in reaction 'R_R181') not handled in VCell at this time.
		return faults;
	}

	public static Collection<Integer> testCases() {
		return Arrays.stream(BMDB_SBML_Files.getBiomodelDB_curatedModelNumbers()).boxed()
				.filter(n -> !slowModelSet().contains(n) && n==264).collect(Collectors.toList());
	}

	@ParameterizedTest
	@MethodSource("testCases")
	public void testSbmlTestSuiteImport(Integer biomodelsDbModelNumber) throws Exception{
		System.out.println("testing Biomodels DB model "+biomodelsDbModelNumber);
		//if (knownFaults().get(biomodelsDbModelNumber) != SBMLTestSuiteTest.FAULT.REACTANT_AND_MODIFIER) { System.out.println("skipping"); return; }
		InputStream testFileInputStream = BMDB_SBML_Files.getBiomodelsDbCuratedModel(biomodelsDbModelNumber);
		TestVCLogger vcl = new TestVCLogger();
		boolean bValidateSBML = true;
		SBMLImporter importer = new SBMLImporter(testFileInputStream, vcl, bValidateSBML);
		boolean bFailed = false;
		SBMLTestSuiteTest.FAULT knownFault = knownFaults().get(biomodelsDbModelNumber);
		Exception exception = null;
		try {
			BioModel bioModel = importer.getBioModel();
			if (vcl.highPriority.size() > 0) {
				bFailed = true;
			}
		}catch (SBMLHighPriorityIssueException e){
			bFailed = true;
            Assertions.assertTrue(vcl.highPriority.size() == 1);
		}catch (Exception e) {
			exception = e;
			bFailed = true;
		}
		if (bFailed) {
			String cause = "unknown";
			if (exception != null){
				cause = (exception.getMessage()!=null) ? exception.getMessage() : "null";
			}else if (vcl.highPriority.size()>0){
				cause = String.join(" | ", vcl.highPriority);
			}
			cause = cause.replace("\n"," ");
			cause = cause.replace("Failed to translate SBML model into BioModel:"," ");
			cause = cause.replace("HighPriority"," ");
			cause = cause.replace("<?xml version='1.0' encoding='UTF-8'?>", "");
			cause = cause.replace("<math xmlns=\"http://www.w3.org/1998/Math/MathML\">","<math>");
			cause = cause.replaceAll("\\s+", " ");
			cause = cause.replaceAll("> <","><");
			cause = cause.substring(0, Math.min(cause.length(), 180));
			SBMLTestSuiteTest.FAULT fault = SBMLTestSuiteTest.FAULT.UNCATEGORIZED;
			if (cause.contains("already defined as a Reactant or a Catalyst of the reaction") ||
				cause.contains("already defined as a Product or a Catalyst of the reaction")){
				fault = SBMLTestSuiteTest.FAULT.REACTANT_AND_MODIFIER;
			}else if (cause.contains("unsupported SBML element 'delay'")){
				fault = SBMLTestSuiteTest.FAULT.DELAY;
			}else if (cause.contains("Non-integer stoichiometry")) {
				fault = SBMLTestSuiteTest.FAULT.NONINTEGER_STOICH;
			}else if (cause.contains("must be compatible with mole or molecules")){
				fault = SBMLTestSuiteTest.FAULT.INCONSISTENT_UNIT_SYSTEM;
			}else if (cause.contains("is either not found in your model")){
				fault = SBMLTestSuiteTest.FAULT.EXPRESSION_BINDING_EXCEPTION;
			}else if (cause.contains("SBML extension 'qual'")){
				fault = SBMLTestSuiteTest.FAULT.QUAL_PACKAGE;
			}else if (cause.contains("SBML extension 'comp'")){
				fault = SBMLTestSuiteTest.FAULT.COMP_PACKAGE;
			}else if (cause.contains("SBML extension 'fbc'")){
				fault = SBMLTestSuiteTest.FAULT.FBC_PACKAGE;
			}else if (cause.contains("error parsing expression")){
				fault = SBMLTestSuiteTest.FAULT.MATHML_PARSING;
			}else if (cause.contains("class org.sbml.jsbml.Constraint cannot be cast to class org.sbml.jsbml.SBMLDocument")) {
				fault = SBMLTestSuiteTest.FAULT.CONSTRAINT_CLASS_CAST_EXCEPTION;
			}

			try (BufferedWriter codeProblemFileWriter = new BufferedWriter(new FileWriter(codeKnownProblemFile, true));
				 BufferedWriter csvProblemFileWriter = new BufferedWriter(new FileWriter(csvKnownProblemFile, true));
			) {
				codeProblemFileWriter.write("faults.put(" + biomodelsDbModelNumber +
						", SBMLTestSuiteTest.FAULT."+fault.name()+");  // cause: " + cause + "\n");
				csvProblemFileWriter.write(biomodelsDbModelNumber+"|"+cause+"\n");
			}
			if (knownFault == null) {
				fail("Biomodel DB model " + biomodelsDbModelNumber + " expecting pass but failed, add to faults: (" + fault.name() + "): " + cause);
			}else if (fault != knownFault){
				fail("Biomodel DB model " + biomodelsDbModelNumber + " expecting '" + knownFault.name() + "' but failed with '" + fault.name() + "', add to faults: (" + fault.name() + "): " + cause);
			}
		}else{ // passed
			if (knownFault != null){
				fail("BiomodelsDB model " + biomodelsDbModelNumber + " passed, but expected fault " + knownFault.name() + ", remove from known faults");
			}
		}
	}

	public static void main(String[] args) {
		try {
			if (args.length != 1){
				System.out.println("usage: BMDB_SBMLImportTest xmlManifestFile");
				System.out.println("   e.g. /path/to/vcell/vcell-client/src/main/resources/bioModelsNetInfo.xml");
				System.exit(1);
			}
			File bioModelsNetInfoFile = new File(args[0]);
			Document document = new Document();
			Element root = new Element("Supported_BioModelsNet");
			for (int model_id : BMDB_SBML_Files.allCuratedModels) {
				Element row = new Element("BioModelInfo");
				row.setAttribute("ID",String.format("BIOMD%010d", model_id));
				SBMLTestSuiteTest.FAULT fault = knownFaults().get(model_id);
				row.setAttribute("Supported", (fault == null) ? "true" : "false");
				if (slowModelSet().contains(model_id)){
					row.setAttribute("Slow", "true");
				}
				row.setAttribute("vcell_ran", "false");
				row.setAttribute("COPASI_ran", "false");
				row.setAttribute("mSBML_ran", "false");
				SBMLReader reader = new SBMLReader();
				try (InputStream sbmlInputStream = new BufferedInputStream(BMDB_SBML_Files.getFileFromResourceAsStream(model_id))) {
					SBMLDocument sbmlDocument = reader.readSBMLFromStream(sbmlInputStream);
					String sbmlModelName = sbmlDocument.getModel().getName();
					row.setAttribute("Name", sbmlModelName);
				}
				if (fault != null){
					row.setAttribute("exception", fault.name());
				}
				root.addContent(row);
			}
			document.setContent(root);
			try (FileWriter xmlFileWriter = new FileWriter(bioModelsNetInfoFile)){
				 XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
				 outputter.output(document, xmlFileWriter);
			}
		}catch (Throwable e){
			e.printStackTrace();
		}
	}
}
