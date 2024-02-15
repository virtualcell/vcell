package org.vcell.sbml;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.vcell.sedml.ModelFormat;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag("SEDML_SBML_IT")
public class SEDMLExporterSBMLTest extends SEDMLExporterCommon {

	/**
	 * 	each file in the slowTestSet takes > 10s on disk and is not included in the unit test (move to integration testing)
	 */
	static Set<String> slowTestSet() {
		Set<String> slowModels = new HashSet<>();
		slowModels.add("biomodel_101981216.vcml");    // 29s
		slowModels.add("biomodel_147699816.vcml");    // 23s
		slowModels.add("biomodel_17326658.vcml");     // 24s
		slowModels.add("biomodel_188880263.vcml");    // 89s
		slowModels.add("biomodel_200301029.vcml");    // 124s (and fails with java.lang.OutOfMemoryError: GC overhead limit exceeded)
		slowModels.add("biomodel_200301683.vcml");    // 37s
		slowModels.add("biomodel_28625786.vcml");     // 57s
		slowModels.add("biomodel_34826524.vcml");     // 129s
		slowModels.add("biomodel_47429473.vcml");     // 194s
		slowModels.add("biomodel_59361239.vcml");     // 116s
		slowModels.add("biomodel_60799209.vcml");     // 41s
		slowModels.add("biomodel_61699798.vcml");     // 69s
		slowModels.add("biomodel_62467093.vcml");     // 23s
		slowModels.add("biomodel_62477836.vcml");     // 30s
		slowModels.add("biomodel_62585003.vcml");     // 24s
		slowModels.add("biomodel_66264206.vcml");     // 45s
		slowModels.add("biomodel_93313420.vcml");     // 137s
		slowModels.add("biomodel_9590643.vcml");      // 40s
		return slowModels;
	}

	static Set<String> outOfMemorySet() {
		Set<String> outOfMemoryModels = new HashSet<>();
		outOfMemoryModels.add("biomodel_101963252.vcml"); // FAULT.JAVA_HEAP_SPACE
		outOfMemoryModels.add("biomodel_200301029.vcml"); // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded (and 124s)
		outOfMemoryModels.add("biomodel_26455186.vcml");  // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded
		outOfMemoryModels.add("biomodel_27192647.vcml");  // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded
		outOfMemoryModels.add("biomodel_27192717.vcml");  // FAULT.OUT_OF_MEMORY) - Java heap space: failed reallocation of scalar replaced objects
		return outOfMemoryModels;
	}

	@Override
	Map<String, SEDMLExporterCommon.FAULT> knownFaults() {
		HashMap<String, FAULT> faults = new HashMap<>();
		faults.put("biomodel_123269393.vcml", FAULT.MATHOVERRIDES_INVALID); // Kf_r7 - biomodel needs fixing - MathOverrides has entry for non-existent parameter Kf_r7
		faults.put("biomodel_124562627.vcml", FAULT.NULL_POINTER_EXCEPTION); // CSG/analytic geometry issue - SBML model does not have any geometryDefinition. Cannot proceed with import
		faults.put("biomodel_156134818.vcml", FAULT.UNKNOWN_IDENTIFIER);  // species named 'I' conflicts with membrane parameter I - Unable to sort, unknown identifier I_Gs_GDI_accel_deg_copy6
		faults.put("biomodel_220138948.vcml", FAULT.MATHOVERRIDES_INVALID); // Kf_Uptake invalid override - MathOverrides has entry for non-existent parameter Kf_Uptake
		faults.put("biomodel_84982474.vcml", FAULT.UNSUPPORTED_NONSPATIAL_STOCH_HISTOGRAM); // not supported nonspatial histogram


		return faults;
	}

	@Override
	Map<String, SEDMLExporterCommon.SEDML_FAULT> knownSEDMLFaults() {
		HashMap<String, SEDML_FAULT> faults = new HashMap<>();
		faults.put("__export_adv_test.vcml", SEDML_FAULT.SIMULATION_NOT_FOUND_BY_NAME);  // round-tripped simulation not found with name 'spatialnoscan'
		faults.put("biomodel_100596964.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_100961371.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_113655498.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_116929912.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_116929971.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_116930032.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_123269393.vcml", SEDML_FAULT.SIMULATION_NOT_FOUND_BY_NAME); // round-tripped simulation not found with name 'Simulation2'
		faults.put("biomodel_123465498.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_123465505.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_124562627.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_12522025.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT);  // simulation 'Simulation 1' in simContext 'purkinje9-ss'
		faults.put("biomodel_13717231.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME);  // round-tripped simulationContext not found with name 'double -'
		faults.put("biomodel_13736736.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_145545992.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_148700996.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT); // simulation 'Full sim' in simContext 'Fast B Compartmental'
		faults.put("biomodel_154961582.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
//		faults.put("biomodel_155016832.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_156134818.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_16763273.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_16804037.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
//		faults.put("biomodel_168717401.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_169993006.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_17098642.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_17257105.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT);  // simulation '1 uM iso' in simContext 'compartmental'
		faults.put("biomodel_188880263.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_18894555.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation 'baseline' in simContext 'compartmental'
		faults.put("biomodel_189321805.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);  // new
		faults.put("biomodel_189512756.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_189513183.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_20253928.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_211839191.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_220138948.vcml", SEDML_FAULT.SIMULATION_NOT_FOUND_BY_NAME); // round-tripped simulation not found with name 'Uptake_Larger Jumps'
		faults.put("biomodel_22403233.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403238.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403244.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403250.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403358.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403576.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_225440511.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_232498815.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_259882394.vcml", SEDML_FAULT.MATH_DIFFERENT); // MathDifferent:DifferentExpression:expressions are different: '1.0000000000000001E-19' vs '0.0'
		faults.put("biomodel_2912851.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2913730.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2915537.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2917738.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2917788.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2917999.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2930915.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2962862.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_32568171.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_32568356.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_34826524.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Cap=1, cof, prof varied' in simContext 'Steady State Turnover'
		faults.put("biomodel_34855932.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'INIT' in simContext 'cell4'
		faults.put("biomodel_38086434.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX); // new
		faults.put("biomodel_55178308.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX); // new
		faults.put("biomodel_55396830.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_59280306.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT); // simulation '1 uM iso' in simContext 'compartmental'
		faults.put("biomodel_60203358.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_60227051.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_63307133.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_65311813.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // simulation 'Copy of Simulation 101x101x36' in simContext '3d image'
		faults.put("biomodel_66264973.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT); // simulation 'Simulation - local sequestration' in simContext 'compartmental - IC-G2736X'
		faults.put("biomodel_66265579.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_74924130.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT); // simulation 'Simulation0' in simContext 'Application0'
		faults.put("biomodel_77305266.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_7803961.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_7803976.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_81284732.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_82250339.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_83446023.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT); // can't find math override for Kf_dimerization
		faults.put("biomodel_83462243.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // round-tripped simulationContext not found with name 'spatial-hybrid'
		faults.put("biomodel_83651737.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME);
		faults.put("biomodel_84982474.vcml", SEDML_FAULT.DIFF_NUMBER_OF_BIOMODELS); // not supported non-spatial histogram
		faults.put("biomodel_9254662.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_92705462.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME);  // round-tripped simulationContext not found with name 'stoch'
		faults.put("biomodel_97075423.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_97705317.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_97786619.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_97786886.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // round-tripped simulationContext not found with name 'hybrid'
		faults.put("biomodel_97787114.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // round-tripped simulationContext not found with name 'hybrid'
		faults.put("biomodel_98147638.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_98150237.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX); // new
		faults.put("biomodel_98174143.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_98296160.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_98730962.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // round-tripped simulationContext not found with name 'Application0'
		faults.put("biomodel_165181964.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);


		// SEDML Validator Errors
		faults.put("biomodel_82065439.vcml", SEDML_FAULT.OMEX_PARSER_ERRORS);  //  NON_UNIQUE_IDS:    Each identified SED object must have a unique id. Multiple objects have the following ids:",[["compartmental"]]
		faults.put("biomodel_220138948.vcml",SEDML_FAULT.OMEX_VALIDATION_ERRORS);  //  XPATH_BAD:  XPath `/sbml:sbml/sbml:model/sbml:listOfSpecies/sbml:species[@id='OAT1']/@initialConcentration` does not match any elements of model `_0D`.
		faults.put("biomodel_31523791.vcml", SEDML_FAULT.OMEX_PARSER_ERRORS);      //  XPATH_BAD:  XPath `/sbml:sbml/sbml:model/sbml:listOfSpecies/sbml:species[@id='cAMP_Intracellular']/@initialConcentration` does not match any elements of model `Dose_response`.
		faults.put("biomodel_34855932.vcml", SEDML_FAULT.OMEX_PARSER_ERRORS);      //  XPATH_BAD:  XPath `/sbml:sbml/sbml:model/sbml:listOfParameters/sbml:parameter[@id='Kf_GPCR_to_ICSC']/@value` does not match any elements of model `cell5`
		faults.put("biomodel_40882931.vcml", SEDML_FAULT.OMEX_PARSER_ERRORS);  //  XPATH_BAD:  XPath `/sbml:sbml/sbml:model/sbml:listOfSpecies/sbml:species[@id='ZO1staticF_PM']/@initialConcentration` does not match any elements of model `_3d_image`
		faults.put("biomodel_40883509.vcml", SEDML_FAULT.OMEX_PARSER_ERRORS);  //  XPATH_BAD:  XPath `/sbml:sbml/sbml:model/sbml:listOfSpecies/sbml:species[@id='PIK_PM']/@initialConcentration` does not match any elements of model `_3d_image`
		faults.put("biomodel_65311813.vcml", SEDML_FAULT.OMEX_PARSER_ERRORS);  //  XPATH_BAD:  XPath `/sbml:sbml/sbml:model/sbml:listOfParameters/sbml:parameter[@id='Ran_nuc_diff']/@value` does not match any elements of model `_3d_image_0`
		return faults;
	}

	@Override
	Set<UnsupportedApplication> unsupportedApplications() {
		Set<UnsupportedApplication> unsupportedApplications = new HashSet<>();
		unsupportedApplications.add(new UnsupportedApplication("biomodel_100596964.vcml","compartment","Membrane 'plasma membrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_100961371.vcml","modelinOct2010","Reaction 'KCNQ2/3' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_113655498.vcml","Relative_gradient_0","Reaction 'PLC catalyzed PIP2 hydrolysis' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_113655498.vcml","Relative_gradient_p67","Reaction 'PLC catalyzed PIP2 hydrolysis' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_113655498.vcml"," Relative_gradient_p1","Reaction 'PLC catalyzed PIP2 hydrolysis' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_113655498.vcml"," Relative_gradient_p3","Reaction 'PLC catalyzed PIP2 hydrolysis' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_116929912.vcml","Channel_test","Membrane 'membrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_116929971.vcml","Application0","Membrane 'fungus membrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_116930032.vcml","Application0","Membrane 'fungus membrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_123465498.vcml","Deterministic","Application 'Deterministic' has reaction rules, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_123465505.vcml","Deterministic","Application 'Deterministic' has reaction rules, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_145545992.vcml","modelinOct2010","Reaction 'KCNQ2/3' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_154961582.vcml","Network Free","Application 'Network Free' has reaction rules, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_154961582.vcml","Deterministic","Application 'Deterministic' has reaction rules, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_154961582.vcml","Stochastic","Application 'Stochastic' has reaction rules, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_16763273.vcml","PipDecay","Reaction 'KCNQcurrent' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_16804037.vcml","PipDecay","Reaction 'KCNQcurrent' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_169993006.vcml","Pattern_formation","Species 'A' has FieldData as initial condition, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_17098642.vcml","PipDecay","Reaction 'KCNQcurrent' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_189321805.vcml","Application0","Application 'Application0' has reaction rules, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_189512756.vcml","Modeling PI cycle","Reaction 'KCNQ2/3' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_189513183.vcml","modeling PI cycle","Reaction 'KCNQ2/3' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_20253928.vcml","PipDecay","Reaction 'KCNQcurrent' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_211839191.vcml","Application0","Reaction 'IP3R' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_22403233.vcml","Spatial","Reaction 'Ca_channel' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_22403238.vcml","Spatial","Reaction 'Ca_channel' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_22403244.vcml","spatial","Reaction 'Ca_channel' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_22403250.vcml","Spatial","Reaction 'Ca_channel' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_22403358.vcml","Spatial","Reaction 'Ca_channel' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_22403576.vcml","Spatial","Reaction 'Ca_channel' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_225440511.vcml","Application0","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_232498815.vcml","Application0","Application 'Application0' has reaction rules, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2912851.vcml","Figure 2.9A: Current 60pA","Membrane 'PlasmaMembrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2912851.vcml","Figure 2.9A: Current 150pA","Membrane 'PlasmaMembrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2912851.vcml","Figure 2.9A: Steady State","Membrane 'PlasmaMembrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2912851.vcml","Figure 2.9A: Current 300pA","Membrane 'PlasmaMembrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2912851.vcml","Figure 2.11A:  -17mV","Membrane 'PlasmaMembrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2912851.vcml","Figure 2.11A: -22mV","Membrane 'PlasmaMembrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2913730.vcml","Figure 2.13A: SteadyState","Membrane 'PlasmaMembrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2913730.vcml","Figure 2.13A: -60 mV","Membrane 'PlasmaMembrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2913730.vcml","Figure 2.13B: 15pA","Membrane 'PlasmaMembrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2913730.vcml","Figure 2.13A: -57 mV","Membrane 'PlasmaMembrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2915537.vcml","Figure 5.21","Membrane 'PlasmaMembrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2917738.vcml","Figure 6.9","Membrane 'PlasmaMembrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2917788.vcml","Figure 4.3: Fast Approximation","Reaction 'Ca Current' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2917788.vcml","Figure 4.3: Full Model","Reaction 'Ca Current' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2917999.vcml","Figure 5.8","Reaction 'CA PMCA Flux' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2930915.vcml","Figure 6.6A","Membrane 'PlasmaMembrane2 has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2930915.vcml","Figure 6.6C","Membrane 'PlasmaMembrane2 has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_2962862.vcml","Figure 6.7","Membrane 'PlasmaMembrane1 has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","GoodModel17_5","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","Furaptra","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","Sim2","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","Sim5_1","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","SyGCaMP2_1AP_v3","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","SyGCaMP2_1AP_v2","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","SyGCaMP2_20AP","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","Furaptra_1AP_v3","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","GCaMP2_1AP_v3","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","SyGCaMP2_1AP","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","Furaptra_1AP_v4","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","GCaMP2_1AP_v2","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","Furaptra_1AP_v2","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","GCaMP2_1AP_v4","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","GCaMP2_20AP_v2","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","GCaMP2_1AP","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","SyGCaMP2_80Hz_v4","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","SyGCaMP2_20AP_v2","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","SyGCaMP2_20AP_v3","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","Furaptra_1AP","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568171.vcml","SyGCaMP2_20AP_v4","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568356.vcml","SyGCaMP2_20AP","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568356.vcml","SyGCaMP2_1AP_v3","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568356.vcml","GCaMP2_1AP_v2","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568356.vcml","SyGCaMP2_1AP","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568356.vcml","SyGCaMP2_20AP_v2","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568356.vcml","SyGCaMP2_20AP_v4","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568356.vcml","GCaMP2_1AP_v4","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568356.vcml","Furaptra_1AP_v4","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568356.vcml","GCaMP2_20AP_v2","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568356.vcml","SyGCaMP2_80Hz_v4","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_32568356.vcml","SyGCaMP2_1AP_v2","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_38086434.vcml","CALI, fine mesh","Species 'PointedT_Cyt' has FieldData as initial condition, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_38086434.vcml","CALI without KND CP=1.5","Species 'PointedT_Cyt' has FieldData as initial condition, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_38086434.vcml","CALI experiment with R=5","Species 'PointedT_Cyt' has FieldData as initial condition, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_38086434.vcml","CALI, VASP=0, fine mesh","Species 'PointedT_Cyt' has FieldData as initial condition, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_38086434.vcml","CALI experiment with FRAP intensities","Species 'PointedT_Cyt' has FieldData as initial condition, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_55178308.vcml","Spatial 1 - PIP2 seq at PSD - 5cylinders further away - HALF","Reaction 'CaP2_spine' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_55178308.vcml","Compartmental 1 - electrophysiology","Membrane 'SM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_55178308.vcml","Spatial 1 - PIP2 seq at PSD - 6cylinders scattered - HALF","Reaction 'CaP2_spine' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_55178308.vcml","Spatial 1 - PIP2 seq at PSD - 5cylinders closer - HALF","Reaction 'CaP2_spine' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_55178308.vcml","Spatial 1 - 3D -  electrophysiology","Membrane 'SM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_55178308.vcml","Spatial 1 - PIP2 seq at PSD - 5cylinders - HALF","Reaction 'CaP2_spine' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_55178308.vcml","Spatial 1 - PIP2 seq at PSD - HALF","Reaction 'CaP2_spine' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_55178308.vcml","Compartmental 1 - biochemistry","Reaction 'CaP2_spine' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_55178308.vcml","Spatial 1 - PIP2 seq at PSD - 6cylinders scattered","Reaction 'CaP2_spine' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_55396830.vcml","phloem_test","Membrane 'membrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60203358.vcml"," Fast Na channel","Membrane 'Sarcoplasm has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60203358.vcml","AP with pulse","Membrane 'Sarcoplasm has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60203358.vcml","AP","Membrane 'Sarcoplasm has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60203358.vcml","IKsum","Membrane 'Sarcoplasm has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60203358.vcml","L-type Ca channel","Membrane 'Sarcoplasm has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60203358.vcml","Kto,f","Membrane 'Sarcoplasm has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60203358.vcml","NaKpump","Membrane 'Sarcoplasm has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60227051.vcml","clust pic double","Reaction 'NOAC' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60227051.vcml","3DclustB","Reaction 'NOAC' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60227051.vcml","clust pic double sx dx","Reaction 'NOAC' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60227051.vcml","3D","Reaction 'NOAC' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60227051.vcml","3Dsph","Reaction 'NOAC' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60227051.vcml","3D clust ch piccolo","Reaction 'NOAC' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60227051.vcml","3D clust Ch","Reaction 'NOAC' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60227051.vcml","2Dprova3D","Reaction 'NOAC' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_60227051.vcml","3DclustBCh","Reaction 'NOAC' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_63307133.vcml","Kir2.1-1","Reaction 'flux0' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_65311813.vcml","Spatial Stochastic","Application 'Spatial Stochastic' is a spatial stochastic application, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_66265579.vcml","compartmental - IC4","Reaction 'PP1a bind ICt' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_77305266.vcml","VSP activation","Reaction 'KCNQ2/3' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_77305266.vcml","M1R activation, manuscript 2","Reaction 'KCNQ2/3' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_77305266.vcml","M1R activation, manuscript 1","Reaction 'KCNQ2/3' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_7803961.vcml","PipDecay","Reaction 'KCNQcurrent' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_7803976.vcml","PipDecay","Reaction 'KCNQcurrent' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_81284732.vcml","modelinOct2010","Reaction 'KCNQ2/3' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_82250339.vcml","compartment","Membrane 'plasma membrane has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_9254662.vcml","FastBuffExpReceptor","Membrane 'PM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_92705462.vcml","stoch","Application 'stoch' is a spatial stochastic application, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_98147638.vcml","compartmental - IC4","Reaction 'PP1a bind ICt' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_98150237.vcml","current injection at soma","Membrane 'IO3smM has membrane potential enabled, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_98174143.vcml","modelinOct2010","Reaction 'KCNQ2/3' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_98296160.vcml","simulations","Reaction 'KCNQ2/3' has electric current defined, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_165181964.vcml","Application0","Spatial processes 'sproc_0' (for cell kinematics) defined in spatial application, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_83462243.vcml","spatial-hybrid","Application 'spatial-hybrid' is a spatial stochastic application, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_83462243.vcml","spatial-stoch","Application 'spatial-stoch' is a spatial stochastic application, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_83651737.vcml","pde","MicroscopyMeasurement 'fluor' defined involving convolution with kernel (point spread function), SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_83651737.vcml","hybrid","Application 'hybrid' is a spatial stochastic application, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_83651737.vcml","particle","Application 'particle' is a spatial stochastic application, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_97075423.vcml","Application0","Application 'Application0' has reaction rules, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_97075423.vcml","NFSim","Application 'NFSim' has reaction rules, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_97705317.vcml","NFSim app","Application 'NFSim app' has reaction rules, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_97705317.vcml","BioNetGen app","Application 'BioNetGen app' has reaction rules, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_97786619.vcml","BioNetGen app","Application 'BioNetGen app' has reaction rules, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_97786619.vcml","NFSim app","Application 'NFSim app' has reaction rules, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_97786886.vcml","pde","MicroscopyMeasurement 'fluor' defined involving convolution with kernel (point spread function), SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_97786886.vcml","hybrid","Application 'hybrid' is a spatial stochastic application, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_97786886.vcml","particle","Application 'particle' is a spatial stochastic application, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_97787114.vcml","hybrid","Application 'hybrid' is a spatial stochastic application, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_97787114.vcml","particle","Application 'particle' is a spatial stochastic application, SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_97787114.vcml","pde","MicroscopyMeasurement 'fluor' defined involving convolution with kernel (point spread function), SBML Export is not supported"));
		unsupportedApplications.add(new UnsupportedApplication("biomodel_98730962.vcml","Application0","Application 'Application0' is a spatial stochastic application, SBML Export is not supported"));
		return unsupportedApplications;
	}

	public static Collection<TestCase> testCases() {
		Predicate<String> skipFilter_SBML = (t) ->
				!outOfMemorySet().contains(t) &&
				!largeFileSet().contains(t) &&
				!slowTestSet().contains(t);
		Stream<TestCase> sbml_test_cases = Arrays.stream(VcmlTestSuiteFiles.getVcmlTestCases()).filter(skipFilter_SBML).map(fName -> new TestCase(fName, ModelFormat.SBML));
		return sbml_test_cases.collect(Collectors.toList());
//		return Arrays.asList(
//				new TestCase("biomodel_31523791.vcml", ModelFormat.SBML),
//				new TestCase("biomodel_34855932.vcml", ModelFormat.SBML),
//				new TestCase("biomodel_40882931.vcml", ModelFormat.SBML),
//				new TestCase("biomodel_40883509.vcml", ModelFormat.SBML),
//				new TestCase("biomodel_65311813.vcml", ModelFormat.SBML),
//				new TestCase("biomodel_155016832.vcml", ModelFormat.SBML)
//				);
	}

	@ParameterizedTest
	@MethodSource("testCases")
	public void test_sedml_roundtrip_SBML(TestCase testCase) throws Exception {
		if (knownFaults().containsKey(testCase.filename)) {
			return; // skip known faults
		}
//		if (knownSEDMLFaults().get(testCase.filename) != SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT
//				&& knownSEDMLFaults().get(testCase.filename) != SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT){
//			return;
//		}
		sedml_roundtrip_common(testCase);
	}

}
