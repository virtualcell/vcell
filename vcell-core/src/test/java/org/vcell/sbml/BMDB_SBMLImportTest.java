package org.vcell.sbml;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.test.SBML_IT;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Category(SBML_IT.class)
@RunWith(Parameterized.class)
public class BMDB_SBMLImportTest {

	public static class TestVCLogger extends VCLogger {
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
			}
		}
	}

	private Integer biomodelsDbModelNumber;
	private static File codeKnownProblemFile;
	private static File csvKnownProblemFile;

	@BeforeClass
	public static void setup() throws IOException {
		codeKnownProblemFile = Files.createTempFile("MathGenCompareTest","code_KnownProblems").toFile();
		csvKnownProblemFile = Files.createTempFile("MathGenCompareTest","csv_KnownProblems").toFile();
		System.err.println("code known problem file: "+codeKnownProblemFile.getAbsolutePath());
		System.err.println("csv known problem file: "+csvKnownProblemFile.getAbsolutePath());
	}

	@AfterClass
	public static void teardown() {
		System.err.println("code known problem file: "+codeKnownProblemFile.getAbsolutePath());
		System.err.println("csv known problem file: "+csvKnownProblemFile.getAbsolutePath());
	}


	public BMDB_SBMLImportTest(Integer biomodelsDbModelNumber) {
		this.biomodelsDbModelNumber = biomodelsDbModelNumber;
	}

	/**
	 * 	each model number in the slowTestSet is not included in the unit test (move to integration testing)
	 */
	public static Set<Integer> slowModelSet() {
		Set<Integer> slowModels = new HashSet<>();
		slowModels.add(233); // 192 seconds
		slowModels.add(235); // > 2 minutes
		slowModels.add(292); // 15 seconds
		slowModels.add(353); // > 5 minutes
		slowModels.add(463); // 5 min 28 seconds
		slowModels.add(469); // 22 seconds
		slowModels.add(470); // 27 seconds
		slowModels.add(471); // 13 seconds (and non-integer stoichiometry)
		slowModels.add(472); // 22 seconds
		slowModels.add(473); // 23 seconds
		slowModels.add(496); // 6 min 6 seconds
		slowModels.add(497); // > 2 minutes
		slowModels.add(574); // 15 seconds
		slowModels.add(595);
		slowModels.add(835); // 10.5 seconds
		return slowModels;
	}

	public static Map<Integer, SBMLTestSuiteImportTest.FAULT> knownFaults() {
		HashMap<Integer,SBMLTestSuiteImportTest.FAULT> faults = new HashMap();
		faults.put(15, SBMLTestSuiteImportTest.FAULT.UNCATEGORIZED);  // cause: Failed to translate SBML model into BioModel: cannot use reserved symbol 'x' as a Reaction name
		faults.put(24, SBMLTestSuiteImportTest.FAULT.DELAY);  // cause: HighPriority UnsupportedConstruct: unsupported SBML element 'delay' in expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <apply
		faults.put(25, SBMLTestSuiteImportTest.FAULT.DELAY);  // cause: HighPriority UnsupportedConstruct: unsupported SBML element 'delay' in expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <piece
		faults.put(34, SBMLTestSuiteImportTest.FAULT.DELAY);  // cause: HighPriority UnsupportedConstruct: unsupported SBML element 'delay' in expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <apply
		faults.put(39, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('0.25' for reactant 'CaER' in reaction 'v1') or stoichiometryMath not handled in VCell at this time. | HighPriority Un
		faults.put(59, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('0.01' for reactant 'Ca_cyt' in reaction 'Calcium_cyt_Jerp') or stoichiometryMath not handled in VCell at this time. |
		faults.put(63, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'VratioVmax' to model: 'Vhk' is either not found in your model or is not allowed to be used in the curr
		faults.put(81, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('9.967E-4' for reactant 'ATP_C' in reaction 'PIP5kinase') or stoichiometryMath not handled in VCell at this time. | Hi
		faults.put(112, SBMLTestSuiteImportTest.FAULT.UNCATEGORIZED);  // cause: Failed to translate SBML model into BioModel: null
		faults.put(145, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('0.001' for reactant 'Ca_ER' in reaction 'R9') or stoichiometryMath not handled in VCell at this time. | HighPriority
		faults.put(151, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('0.5' for product 'x13' in reaction 'R23') or stoichiometryMath not handled in VCell at this time.
		faults.put(154, SBMLTestSuiteImportTest.FAULT.DELAY);  // cause: HighPriority UnsupportedConstruct: unsupported SBML element 'delay' in expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <apply
		faults.put(155, SBMLTestSuiteImportTest.FAULT.DELAY);  // cause: HighPriority UnsupportedConstruct: unsupported SBML element 'delay' in expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <apply
		faults.put(175, SBMLTestSuiteImportTest.FAULT.REACTANT_AND_MODIFIER);  // cause: Failed to translate SBML model into BioModel: reactionParticipant E_E1 already defined as a Reactant or a Catalyst of the reaction.
		faults.put(196, SBMLTestSuiteImportTest.FAULT.DELAY);  // cause: HighPriority UnsupportedConstruct: unsupported SBML element 'delay' in expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <apply
		faults.put(199, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('0.5' for reactant 'NADPH' in reaction 'r4') or stoichiometryMath not handled in VCell at this time. | HighPriority Un
		faults.put(206, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('0.1' for product 's6o' in reaction 'v10') or stoichiometryMath not handled in VCell at this time.
		faults.put(220, SBMLTestSuiteImportTest.FAULT.REACTANT_AND_MODIFIER);  // cause: Failed to translate SBML model into BioModel: reactionParticipant Baxm already defined as a Reactant or a Catalyst of the reaction.
		faults.put(232, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('0.5' for reactant 'O2' in reaction 'vresp') or stoichiometryMath not handled in VCell at this time.
		faults.put(243, SBMLTestSuiteImportTest.FAULT.REACTANT_AND_MODIFIER);  // cause: Failed to translate SBML model into BioModel: reactionParticipant p43_p41 already defined as a Product or a Catalyst of the reaction.
		faults.put(244, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('0.5' for reactant 'FBP' in reaction 'e_Emp') or stoichiometryMath not handled in VCell at this time.
		faults.put(245, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Unable to create and add rate rule to VC model : 'r7' is either not found in your model or is not allowed to be used in the current co
		faults.put(246, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('0.001' for product 'Ca_in' in reaction 'vo') or stoichiometryMath not handled in VCell at this time. | HighPriority U
		faults.put(248, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Unable to create and add rate rule to VC model : 'Capillary.Capillary' is either not found in your model or is not allowed to be used
		faults.put(255, SBMLTestSuiteImportTest.FAULT.REACTANT_AND_MODIFIER);  // cause: Failed to translate SBML model into BioModel: reactionParticipant c499 already defined as a Reactant or a Catalyst of the reaction.
		faults.put(256, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'XIAP_ini' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be us
		faults.put(305, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'V' to model: 'Fw_1st_step' is either not found in your model or is not allowed to be used in the curre
		faults.put(319, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('0.02' for product 'gamma' in reaction 'r3') or stoichiometryMath not handled in VCell at this time.
		faults.put(340, SBMLTestSuiteImportTest.FAULT.MATHML_PARSING);  // cause: HighPriority UnsupportedConstruct: error parsing expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <apply>     <gt/>     <piece
		faults.put(342, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'TGF_beta_dose_mol_per_cell' to model: 'UNRESOLVED.initConc' is either not found in your model or is no
		faults.put(351, SBMLTestSuiteImportTest.FAULT.REACTANT_AND_MODIFIER);  // cause: Failed to translate SBML model into BioModel: reactionParticipant I already defined as a Reactant or a Catalyst of the reaction.
		faults.put(352, SBMLTestSuiteImportTest.FAULT.REACTANT_AND_MODIFIER);  // cause: Failed to translate SBML model into BioModel: reactionParticipant I already defined as a Reactant or a Catalyst of the reaction.
		faults.put(383, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('1.5' for product 'PGA' in reaction 'PGA_prod_Vo') or stoichiometryMath not handled in VCell at this time.
		faults.put(384, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('1.5' for product 'PGA' in reaction 'PGA_prod_Vo') or stoichiometryMath not handled in VCell at this time.
		faults.put(385, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('1.5' for product 'PGA' in reaction 'PGA_prod_Vo') or stoichiometryMath not handled in VCell at this time.
		faults.put(386, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('1.5' for product 'PGA' in reaction 'PGA_prod_Vo') or stoichiometryMath not handled in VCell at this time.
		faults.put(387, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('1.5' for product 'PGA' in reaction 'PGA_prod_Vo') or stoichiometryMath not handled in VCell at this time.
		faults.put(388, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('0.6' for product 'Ru5P' in reaction 'GAP2Ru5P') or stoichiometryMath not handled in VCell at this time.
		faults.put(392, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('0.5' for reactant 'ATP' in reaction 'RuBisCO_6_O2') or stoichiometryMath not handled in VCell at this time. | HighPri
		faults.put(397, SBMLTestSuiteImportTest.FAULT.REACTANT_AND_MODIFIER);  // cause: Failed to translate SBML model into BioModel: reactionParticipant s32 already defined as a Reactant or a Catalyst of the reaction.
		faults.put(415, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('0.574' for product 'species_7' in reaction 'reaction_1') or stoichiometryMath not handled in VCell at this time. | Hi
		faults.put(426, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('0.08' for reactant 'species_31' in reaction 'reaction_28') or stoichiometryMath not handled in VCell at this time. |
		faults.put(429, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'parameter_18' to model: 'UNRESOLVED.Size' is either not found in your model or is not allowed to be us
		faults.put(452, SBMLTestSuiteImportTest.FAULT.REACTANT_AND_MODIFIER);  // cause: Failed to translate SBML model into BioModel: reactionParticipant mw7eacabf9_d68c_491a_aba2_ec0809a8ecc8 already defined as a Reactant or a Catalyst of the reaction.
		faults.put(453, SBMLTestSuiteImportTest.FAULT.REACTANT_AND_MODIFIER);  // cause: Failed to translate SBML model into BioModel: reactionParticipant mw7eacabf9_d68c_491a_aba2_ec0809a8ecc8 already defined as a Reactant or a Catalyst of the reaction.
		faults.put(457, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'parameter_1' to model: 'UNRESOLVED.Size' is either not found in your model or is not allowed to be use
		faults.put(468, SBMLTestSuiteImportTest.FAULT.UNCATEGORIZED);  // cause: Failed to translate SBML model into BioModel: found more than one SBase match for sid=unitime, matched [org.vcell.sbml.vcell.SBMLSymbolMapping$SBaseWrapper@178dd7f7, org.vcell.sbml
		faults.put(474, SBMLTestSuiteImportTest.FAULT.UNCATEGORIZED);  // cause: Failed to translate SBML model into BioModel: found more than one SBase match for sid=v, matched [org.vcell.sbml.vcell.SBMLSymbolMapping$SBaseWrapper@2f210662, org.vcell.sbml.vcell
		faults.put(499, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_9' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to b
		faults.put(520, SBMLTestSuiteImportTest.FAULT.UNCATEGORIZED);  // cause: HighPriority OverallWarning: missing or unexpected value attribute 'NaN' for SBML object id T
		faults.put(534, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_6' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to b
		faults.put(535, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_3' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to b
		faults.put(536, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_80' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to
		faults.put(537, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_40' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to
		faults.put(542, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Unable to create and add rate rule to VC model : 'r77' is either not found in your model or is not allowed to be used in the current c
		faults.put(547, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Compartment_3' to model: 'UNRESOLVED.Size' is either not found in your model or is not allowed to be u
		faults.put(556, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Unable to create and add rate rule to VC model : 'r0' is either not found in your model or is not allowed to be used in the current co
		faults.put(562, SBMLTestSuiteImportTest.FAULT.QUAL_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'qual', which is required for simulating the model but is not supported.
		faults.put(570, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Unable to create and add rate rule to VC model : 'reaction_4' is either not found in your model or is not allowed to be used in the cu
		faults.put(575, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Unable to create and add rate rule to VC model : 'J21' is either not found in your model or is not allowed to be used in the current c
		faults.put(577, SBMLTestSuiteImportTest.FAULT.MATHML_PARSING);  // cause: HighPriority UnsupportedConstruct: error parsing expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <piecewise>     <piece>
		faults.put(578, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Unable to create and add rate rule to VC model : 'v_r33' is either not found in your model or is not allowed to be used in the current
		faults.put(589, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_17' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to
		faults.put(592, SBMLTestSuiteImportTest.FAULT.QUAL_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'qual', which is required for simulating the model but is not supported.
		faults.put(593, SBMLTestSuiteImportTest.FAULT.QUAL_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'qual', which is required for simulating the model but is not supported.
		faults.put(599, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_1' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to b
		faults.put(608, SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH);  // cause: HighPriority UnsupportedConstruct: Non-integer stoichiometry ('0.5' for reactant 'x_18' in reaction 'R_18') or stoichiometryMath not handled in VCell at this time. | HighPriority U
		faults.put(613, SBMLTestSuiteImportTest.FAULT.COMP_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'comp', which is required for simulating the model but is not supported.
		faults.put(627, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_123' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to
		faults.put(628, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_8' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to b
		faults.put(632, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'k4b' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used in
		faults.put(696, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_16' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to
		faults.put(705, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_21' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to
		faults.put(706, SBMLTestSuiteImportTest.FAULT.UNCATEGORIZED);  // cause: Failed to translate SBML model into BioModel: found more than one SBase match for sid=v, matched [org.vcell.sbml.vcell.SBMLSymbolMapping$SBaseWrapper@7621d697, org.vcell.sbml.vcell
		faults.put(710, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_0_0' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to
		faults.put(731, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Treg_origin_fraction_CD4' to model: 'func_TRegs_Production_from_CD4' is either not found in your model
		faults.put(739, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_19' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to
		faults.put(764, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_3' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to b
		faults.put(775, SBMLTestSuiteImportTest.FAULT.MATHML_PARSING);  // cause: HighPriority UnsupportedConstruct: error parsing expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <notanumber/> </math>': node
		faults.put(804, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_1' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to b
		faults.put(808, SBMLTestSuiteImportTest.FAULT.MATHML_PARSING);  // cause: HighPriority UnsupportedConstruct: error parsing expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <piecewise>     <piece>
		faults.put(814, SBMLTestSuiteImportTest.FAULT.MATHML_PARSING);  // cause: HighPriority UnsupportedConstruct: error parsing expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <apply>     <and/>     <appl
		faults.put(822, SBMLTestSuiteImportTest.FAULT.MATHML_PARSING);  // cause: HighPriority UnsupportedConstruct: error parsing expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <apply>     <and/>     <appl
		faults.put(828, SBMLTestSuiteImportTest.FAULT.MATHML_PARSING);  // cause: HighPriority UnsupportedConstruct: error parsing expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <apply>     <eq/>     <piece
		faults.put(829, SBMLTestSuiteImportTest.FAULT.MATHML_PARSING);  // cause: HighPriority UnsupportedConstruct: error parsing expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <apply>     <eq/>     <piece
		faults.put(832, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_4' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to b
		faults.put(841, SBMLTestSuiteImportTest.FAULT.DELAY);  // cause: HighPriority UnsupportedConstruct: unsupported SBML element 'delay' in expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <apply
		faults.put(858, SBMLTestSuiteImportTest.FAULT.MATHML_PARSING);  // cause: HighPriority UnsupportedConstruct: error parsing expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <apply>     <minus/>     <pi
		faults.put(859, SBMLTestSuiteImportTest.FAULT.MATHML_PARSING);  // cause: HighPriority UnsupportedConstruct: error parsing expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <apply>     <minus/>     <pi
		faults.put(867, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_9' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to b
		faults.put(872, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'beta' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to be used i
		faults.put(908, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_2' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to b
		faults.put(925, SBMLTestSuiteImportTest.FAULT.UNCATEGORIZED);  // cause: HighPriority OverallWarning: missing or unexpected value attribute '-Infinity' for SBML object id log_time
		faults.put(952, SBMLTestSuiteImportTest.FAULT.MATHML_PARSING);  // cause: HighPriority UnsupportedConstruct: error parsing expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <notanumber/> </math>': node
		faults.put(956, SBMLTestSuiteImportTest.FAULT.MATHML_PARSING);  // cause: HighPriority UnsupportedConstruct: error parsing expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <notanumber/> </math>': node
		faults.put(961, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'rateOf_re15' to model: 're15' is either not found in your model or is not allowed to be used in the cu
		faults.put(969, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_11' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to
		faults.put(1005, SBMLTestSuiteImportTest.FAULT.MATHML_PARSING);  // cause: HighPriority UnsupportedConstruct: error parsing expression '<?xml version='1.0' encoding='UTF-8'?> <math xmlns="http://www.w3.org/1998/Math/MathML">   <piecewise>     <piece>
		faults.put(1021, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Metabolite_0' to model: 'UNRESOLVED.initConc' is either not found in your model or is not allowed to b
		faults.put(1026, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Summary_flux_to_RBC' to model: 'Vin' is either not found in your model or is not allowed to be used in
		faults.put(1027, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Compartment_10' to model: 'UNRESOLVED.Size' is either not found in your model or is not allowed to be
		faults.put(1028, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Compartment_10' to model: 'UNRESOLVED.Size' is either not found in your model or is not allowed to be
		faults.put(1029, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Compartment_10' to model: 'UNRESOLVED.Size' is either not found in your model or is not allowed to be
		faults.put(1039, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Compartment_10' to model: 'UNRESOLVED.Size' is either not found in your model or is not allowed to be
		faults.put(1040, SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION);  // cause: Failed to translate SBML model into BioModel: Error binding global parameter 'Summary_flux_to_RBC' to model: 'Vin' is either not found in your model or is not allowed to be used in
		faults.put(1046, SBMLTestSuiteImportTest.FAULT.FBC_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'fbc', which is required for simulating the model but is not supported.
		faults.put(1061, SBMLTestSuiteImportTest.FAULT.FBC_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'fbc', which is required for simulating the model but is not supported.
		faults.put(1062, SBMLTestSuiteImportTest.FAULT.FBC_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'fbc', which is required for simulating the model but is not supported.
		faults.put(1063, SBMLTestSuiteImportTest.FAULT.FBC_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'fbc', which is required for simulating the model but is not supported.
		faults.put(1064, SBMLTestSuiteImportTest.FAULT.FBC_PACKAGE);  // cause: Unable to import the SBML file. The model includes elements of SBML extension 'fbc', which is required for simulating the model but is not supported.
		return faults;
	}

	@Parameterized.Parameters
	public static Collection<Integer> testCases() {
		return Arrays.stream(BMDB_SBML_Files.getBiomodelDB_curatedModelNumbers()).boxed()
				.filter(n -> !slowModelSet().contains(n)).collect(Collectors.toList());
	}

	@Test
	public void testSbmlTestSuiteImport() throws Exception{
		System.out.println("testing Biomodels DB model "+biomodelsDbModelNumber);
		InputStream testFileInputStream = BMDB_SBML_Files.getBiomodelsDbCuratedModel(biomodelsDbModelNumber);
		TestVCLogger vcl = new TestVCLogger();
		boolean bValidateSBML = true;
		SBMLImporter importer = new SBMLImporter(testFileInputStream, vcl, bValidateSBML);
		boolean bFailed = false;
		SBMLTestSuiteImportTest.FAULT knownFault = knownFaults().get(biomodelsDbModelNumber);
		Exception exception = null;
		try {
			BioModel bioModel = importer.getBioModel();
			if (vcl.highPriority.size()>0){
				bFailed = true;
			}
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
			cause = cause.substring(0, Math.min(cause.length(), 180));
			SBMLTestSuiteImportTest.FAULT fault = SBMLTestSuiteImportTest.FAULT.UNCATEGORIZED;
			if (cause.contains("already defined as a Reactant or a Catalyst of the reaction") ||
				cause.contains("already defined as a Product or a Catalyst of the reaction")){
				fault = SBMLTestSuiteImportTest.FAULT.REACTANT_AND_MODIFIER;
			}else if (cause.contains("unsupported SBML element 'delay'")){
				fault = SBMLTestSuiteImportTest.FAULT.DELAY;
			}else if (cause.contains("Non-integer stoichiometry")){
				fault = SBMLTestSuiteImportTest.FAULT.NONINTEGER_STOICH;
			}else if (cause.contains("is either not found in your model")){
				fault = SBMLTestSuiteImportTest.FAULT.EXPRESSION_BINDING_EXCEPTION;
			}else if (cause.contains("SBML extension 'qual'")){
				fault = SBMLTestSuiteImportTest.FAULT.QUAL_PACKAGE;
			}else if (cause.contains("SBML extension 'comp'")){
				fault = SBMLTestSuiteImportTest.FAULT.COMP_PACKAGE;
			}else if (cause.contains("SBML extension 'fbc'")){
				fault = SBMLTestSuiteImportTest.FAULT.FBC_PACKAGE;
			}else if (cause.contains("error parsing expression '<?xml version='1.0' encoding='UTF-8'?> <math")){
				fault = SBMLTestSuiteImportTest.FAULT.MATHML_PARSING;
			}
			try (BufferedWriter codeProblemFileWriter = new BufferedWriter(new FileWriter(codeKnownProblemFile, true));
				 BufferedWriter csvProblemFileWriter = new BufferedWriter(new FileWriter(csvKnownProblemFile, true));
			) {
				codeProblemFileWriter.write("faults.put(" + biomodelsDbModelNumber +
						", SBMLTestSuiteImportTest.FAULT."+fault.name()+");  // cause: " + cause + "\n");
				csvProblemFileWriter.write(biomodelsDbModelNumber+"|"+cause+"\n");
			}
			if (knownFault == null) {
				Assert.fail("Biomodel DB model " + biomodelsDbModelNumber + " expecting pass but failed, add to faults: ("+fault.name()+"): "+cause);
			}else if (fault != knownFault){
				Assert.fail("Biomodel DB model " + biomodelsDbModelNumber + " expecting '"+knownFault.name()+"' but failed with '"+fault.name()+"', add to faults: ("+fault.name()+"): "+cause);
			}
		}else{ // passed
			if (knownFault != null){
				Assert.fail("BiomodelsDB model "+biomodelsDbModelNumber+" passed, but expected fault "+knownFault.name()+", remove from known faults");
			}
		}
	}
}
