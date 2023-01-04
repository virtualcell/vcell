package org.vcell.sbml;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sedml.ModelFormat;
import org.vcell.test.SEDML_SBML_IT;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
@Category({SEDML_SBML_IT.class})
public class SEDMLExporterSBMLTest extends SEDMLExporterCommon {

	public SEDMLExporterSBMLTest(TestCase testCase){
		super(testCase);
	}

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
		HashMap<String, FAULT> faults = new HashMap();
		faults.put("biomodel_123269393.vcml", FAULT.MATHOVERRIDES_INVALID); // Kf_r7 - biomodel needs fixing
		faults.put("biomodel_124562627.vcml", FAULT.NULL_POINTER_EXCEPTION); // CSG/analytic geometry issue
		faults.put("biomodel_156134818.vcml", FAULT.UNKNOWN_IDENTIFIER);  // species named I conflicts with membrane parameter I
		faults.put("biomodel_220138948.vcml", FAULT.MATHOVERRIDES_INVALID); // Kf_Uptake invalid override.
		faults.put("biomodel_84982474.vcml", FAULT.UNSUPPORTED_NONSPATIAL_STOCH_HISTOGRAM); // not supported nonspatial histogram
		return faults;
	}

	@Override
	Map<String, SEDMLExporterCommon.SEDML_FAULT> knownSEDMLFaults() {
		HashMap<String, SEDML_FAULT> faults = new HashMap();
		faults.put("__export_adv_test.vcml", SEDML_FAULT.SIMULATION_NOT_FOUND_BY_NAME);  // roundtripped simulation not found with name 'spatialnoscan'
		faults.put("biomodel_100596964.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_100961371.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_102061382.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Arp23 variation' in simContext 'Application0'
		faults.put("biomodel_105608907.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'F/G=10/190, Kd=0.01-0.2'  in simContext 'wt uniform'
		faults.put("biomodel_10829774.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation 'set1' in simContext 'maximumAmplificationFig6b'
		faults.put("biomodel_113655498.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_116929912.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_116929971.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_116930032.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_123269393.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Simulation1' in simContext 'Application2'
		faults.put("biomodel_123269480.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Simulation1' in simContext 'Application2'
		faults.put("biomodel_123465498.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_123465505.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_124562627.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_12522025.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation 'Simulation 1' in simContext 'purkinje9-ss'
		faults.put("biomodel_13717231.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME);  // roundtripped simulationContext not found with name 'double -'
		faults.put("biomodel_13736736.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_145545992.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_148700996.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Full sim' in simContext 'Fast B Compartmental'
		faults.put("biomodel_149491513.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'ss check 1' in simContext 'steady state compartment'
		faults.put("biomodel_154208982.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'RISC_mRNA_Deg_MA' in simContext 'Application0'
		faults.put("biomodel_154210963.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Simulation0' in simContext 'Deterministic'
		faults.put("biomodel_154961582.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_155016832.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_156134818.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_158495696.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Clofazimine' in simContext 'Matching w/ Matlab data'
		faults.put("biomodel_16404713.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation '[L] = 0.3 nM, delta = 0.5' in simContext 'PDGFgradient_TIRF'
		faults.put("biomodel_16763273.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_16804037.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_168717401.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_169993006.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_17098642.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_171423478.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Parameter scan for n1' in simContext 'Simple ODE'
		faults.put("biomodel_171423486.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Parameter scan for n1' in simContext 'Simple ODE'
		faults.put("biomodel_171423593.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Parameter scan for hill coefficients' in simContext 'Simple ODE'
		faults.put("biomodel_171423851.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Prob 3 var kf, A and B' in simContext 'Conformational change'
		faults.put("biomodel_171423920.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Prob 6  kf2 set to 10, A varied 1 to 500 (expand scale to see early behavior)' in simContext 'default for prob 5; multi sims for probs 5, 6 & 7'
		faults.put("biomodel_171423957.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'kf=70' in simContext 'Conformational change'
		faults.put("biomodel_172076998.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Fig 2A: PFL 1 scan 0% gradient' in simContext 'Symmetric Geometry'
		faults.put("biomodel_17257105.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation '1 uM iso' in simContext 'compartmental'
		faults.put("biomodel_17263179.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation '0.1 uM iso DcAMP 300' in simContext 'Figure 3'
		faults.put("biomodel_185577495.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'done 1 2 1' in simContext 'Copy of Application0 1'
		faults.put("biomodel_188880263.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_18894555.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation 'baseline' in simContext 'compartmental'
		faults.put("biomodel_189321805.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);  // new
		faults.put("biomodel_189512756.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_189513183.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_200965116.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Simulation0' in simContext 'Deterministic'
		faults.put("biomodel_200999311.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Simulation1' in simContext 'Deterministic'
		faults.put("biomodel_20253928.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_209284198.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Microinjection of FRanGDP' in simContext 'Normal model parameters'
		faults.put("biomodel_211839191.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_220138697.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Fitting Results' in simContext '0D'
		faults.put("biomodel_220138948.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Scan of albumin conc' in simContext '0D'
		faults.put("biomodel_22403233.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403238.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403244.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403250.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403358.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403576.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22523922.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation 'Simulation 3' in simContext 'pyro_nonspacial'
		faults.put("biomodel_225440511.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_229605883.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Fig 6A: PFL1* + MARCKS' in simContext 'Deterministic / Spatial'
		faults.put("biomodel_232498815.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_26454052.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation 'Spine_using spine parameter values that fall in ranges of Harris et al (28)' in simContext '1D PIP2_fits 3D model that uses constructed 3D geometry'
		faults.put("biomodel_26454463.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation 'lat diff & stim syn - decreased IP3' in simContext '3D PIP2_constructed geometry'
		faults.put("biomodel_26581203.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation 'CF XXms after start of 4 PF stimuli_375uM MgG_stimulated PIP2 synthesis with PIP2 lateral diffusion' in simContext 'Calcium transients from Local Sequestration vs. Stimulated Synthesis'
		faults.put("biomodel_26928347.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation 'Actin Alone Opt' in simContext '0.5 ?M PRF'
		faults.put("biomodel_27088120.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation 'Simulation36' in simContext 'DecayPC12-4block'
		faults.put("biomodel_28139443.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation 'Actin Alone Opt' in simContext '0.5 ?M PRF'
		faults.put("biomodel_2912851.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2913730.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2915537.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2917738.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2917788.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2917999.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2930915.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2962862.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_29897263.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation 'less GEF, broader range' in simContext 'Simulations for switch paper'
		faults.put("biomodel_31523791.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation 'various cAMP conc steady state' in simContext '10 spines'
		faults.put("biomodel_31584491.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation 'Simulation0' in simContext 'baseline condition'
		faults.put("biomodel_32568171.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_32568356.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_34826524.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Cap=1, cof, prof varied' in simContext 'Steady State Turnover'
		faults.put("biomodel_34855932.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'INIT' in simContext 'cell4'
		faults.put("biomodel_36053554.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'WT' in simContext 'steady-state'
		faults.put("biomodel_36230715.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'clonidine dose response' in simContext 'Simulations for switch paper'
		faults.put("biomodel_36275161.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation '[L] = 0.3 nM, delta = 0.5' in simContext 'PDGFgradient_TIRF'
		faults.put("biomodel_38086434.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX); // new
		faults.put("biomodel_40882931.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'with laser' in simContext '3d image'
		faults.put("biomodel_40883509.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'with laser' in simContext '3d image'
		faults.put("biomodel_49411430.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Simulation4' in simContext 'pombe actin polymerization'
		faults.put("biomodel_55178308.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX); // new
		faults.put("biomodel_55396830.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_59280306.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation '1 uM iso' in simContext 'compartmental'
		faults.put("biomodel_60203358.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_60227051.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_62849940.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'intramolecular' in simContext 'Temporal stimulation (pulse)'
		faults.put("biomodel_63307133.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_65311813.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // simulation 'Copy of Copy of Simulation 101x101x36' in simContext '3d image'
		faults.put("biomodel_66264973.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Simulation - local sequestration' in simContext 'compartmental - IC-G2736X'
		faults.put("biomodel_66265579.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_74924130.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Simulation0' in simContext 'Application0'
		faults.put("biomodel_77305266.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_7803961.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_7803976.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_81284732.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_81992349.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'rpcamp' in simContext 'Compart'
		faults.put("biomodel_82250339.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_83446023.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Simulation0' in simContext 'compartmental'
		faults.put("biomodel_83932806.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Fig 6A influx pKa 9.6, logKow=1.88, c=1' in simContext 'Matching w/ Matlab data'
		faults.put("biomodel_84982474.vcml", SEDML_FAULT.DIFF_NUMBER_OF_BIOMODELS); // not supported nonspatial histogram
		faults.put("biomodel_84985561.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'copy of Simulation 2' in simContext 'comp'
		faults.put("biomodel_89712092.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'ss check 1' in simContext 'steady state compartment'
		faults.put("biomodel_89712092_nonspatial.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'First Try' in simContext 'uncaging'
		faults.put("biomodel_91133993.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation Simulation0 in simContext test
		faults.put("biomodel_91134220.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);
		faults.put("biomodel_91134296.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);
		faults.put("biomodel_91134339.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);
		faults.put("biomodel_91141200.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);
		faults.put("biomodel_91141358.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);
		faults.put("biomodel_91147280.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);
		faults.put("biomodel_91162809.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);
		faults.put("biomodel_91162818.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);
		faults.put("biomodel_91164078.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);
		faults.put("biomodel_91164682.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);
		faults.put("biomodel_92942045.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);
		faults.put("biomodel_9254662.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_92705462.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME);  // roundtripped simulationContext not found with name 'stoch'
		faults.put("biomodel_94538871.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);
		faults.put("biomodel_94891280.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);
		faults.put("biomodel_95094548.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);
		faults.put("biomodel_95177642.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);
		faults.put("biomodel_98147638.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_98150237.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX); // new
		faults.put("biomodel_98174143.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_98296160.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_165181964.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		return faults;
	}

	@Parameterized.Parameters
	public static Collection<TestCase> testCases() {
		Predicate<String> skipFilter_SBML = (t) -> !outOfMemorySet().contains(t) && !largeFileSet().contains(t) && !slowTestSet().contains(t);
		Stream<TestCase> sbml_test_cases = Arrays.stream(VcmlTestSuiteFiles.getVcmlTestCases()).filter(skipFilter_SBML).map(fname -> new TestCase(fname, ModelFormat.SBML));
		List<TestCase> testCases = sbml_test_cases.collect(Collectors.toList());
		return testCases;
		//return Arrays.asList(new TestCase("biomodel_101981216.vcml", ModelFormat.VCML));
	}

	@Test
	public void test_sedml_roundtrip_SBML() throws Exception {
		if (knownSEDMLFaults().get(testCase.filename) != SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT){
			return;
		}
		sedml_roundtrip_common();
	}

}
