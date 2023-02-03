package cbit.vcell.mapping;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelTransforms;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sbml.VcmlTestSuiteFiles;
import org.vcell.test.MathGen_IT;
import org.vcell.util.document.Version;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Category(MathGen_IT.class)
@RunWith(Parameterized.class)
public class MathGenCompareTest {

	private String filename_colon_appname;

	private static String previousInstalldirPropertyValue;
	private static File codeKnownProblemFile;
	private static File csvKnownProblemFile;

	public MathGenCompareTest(String filename_colon_appname){
		this.filename_colon_appname = filename_colon_appname;
	}


	@BeforeClass
	public static void setup() throws IOException {
		previousInstalldirPropertyValue = System.getProperty("vcell.installDir");
		System.setProperty("vcell.installDir", "..");
		NativeLib.combinej.load();
		codeKnownProblemFile = Files.createTempFile("MathGenCompareTest","code_KnownProblems").toFile();
		csvKnownProblemFile = Files.createTempFile("MathGenCompareTest","csv_KnownProblems").toFile();
		System.err.println("code known problem file: "+codeKnownProblemFile.getAbsolutePath());
		System.err.println("csv known problem file: "+csvKnownProblemFile.getAbsolutePath());
	}

	@AfterClass
	public static void teardown() {
		if (previousInstalldirPropertyValue!=null) {
			System.setProperty("vcell.installDir", previousInstalldirPropertyValue);
		}
		System.err.println("code known problem file: "+codeKnownProblemFile.getAbsolutePath());
		System.err.println("csv known problem file: "+csvKnownProblemFile.getAbsolutePath());
	}

	@BeforeClass
	public static void printSkippedModels() {
		for (String filename : outOfMemoryFileSet()){
			System.err.println("skipping - out of memory: "+filename);
		}
		for (String filename : largeFileSet()){
			System.err.println("skipping - too large (model not in repo): "+filename);
		}
		for (String filename : slowFileSet()){
			System.err.println("skipping - Math generation too slow for unit tests: "+filename);
		}
	}

	/**
	 * each file in largeFileSet too large on disk and is not included in the test suite.
	 */
	public static Set<String> largeFileSet() {
		Set<String> largeFiles = new HashSet<>();
		return largeFiles;
	}

	/**
	 * 	each file in the slowTestSet is not included in the unit test (move to integration testing)
	 */
	public static Set<String> slowFileSet() {
		Set<String> slowModels = new HashSet<>();
		return slowModels;
	}

	/**
	 * each file in outOfMemoryFileSet cannot be run without large memory (not included in test suite).
	 */
	public static Set<String> outOfMemoryFileSet() {
		Set<String> outOfMemoryModels = new HashSet<>();
		outOfMemoryModels.add("biomodel_101963252.vcml"); // FAULT.JAVA_HEAP_SPACE
		return outOfMemoryModels;
	}

	/**
	 * each file in the knownFaultsMap hold known problems in the current software
	 */
	public static Set<String> knownMathGenerationFailures() {
		Set<String> faults = new HashSet<>();
		// public models

		// all flux models
		faults.add("biomodel_158988094.vcml:Application1"); // local/flux: NPE in StructureSizeSolver.updateUnitStructureSizes_symbolic(StructureSizeSolver.java:649)
		faults.add("biomodel_90935642.vcml:non-spatial stoch"); // local/flux: Failed to intepret kinetic rate for reaction 'flux0' as mass action.
		faults.add("biomodel_90935642.vcml:3D stoch"); // local/flux: Failed to intepret kinetic rate for reaction 'flux0' as mass action.

		// all private hybrid models
		faults.add("biomodel_101106339.vcml:Application0"); // local/massaction: 'FB-ECM' not legal identifier for rule-based modeling, try 'FB_ECM'.
		faults.add("biomodel_211551763.vcml:Application1"); // local/massaction: NullPointerException in BioEvent.getConstantParameterValue()
		faults.add("biomodel_2177828.vcml:ttt"); // local/massaction: Enable diffusion in Application 'ttt'. This must be done for any species (e.g 'calcium_Feature') in flux reactions.
		faults.add("biomodel_88592639.vcml:tissue"); // local/massaction:

		// all private Hybrid PDE/Particle models
		faults.add("biomodel_179548819.vcml:Application0"); // local/hybrid: The Min Trigger Condition for BioEvent 'event0' cannot be <= 0.
		faults.add("biomodel_82790975.vcml:NONSPATIAL Stochastic/Deterministic Integrin Ligand Activation"); // local/hybrid: Non-constant species is forced continuous, not supported for nonspatial stochastic applications.
		faults.add("biomodel_82800592.vcml:Stochastic Nonsptial"); // local/hybrid: Non-constant species is forced continuous, not supported for nonspatial stochastic applications.
		faults.add("biomodel_82805340.vcml:Stochastic/Deterministic Integrin Ligand Activation"); // local/hybrid: Failed to intepret kinetic rate for reaction 'r2' as mass action.
		faults.add("biomodel_82807862.vcml:Stochastic Spatial QUARTER CELL SHORT"); // local/hybrid: Failed to intepret kinetic rate for reaction 'r2' as mass action.
		faults.add("biomodel_82807862.vcml:Stochastic/Deterministic Integrin Ligand Activation"); // local/hybrid: Failed to intepret kinetic rate for reaction 'r2' as mass action.
		faults.add("biomodel_82808340.vcml:Stochastic Spatial QUARTER CELL SHORT"); // local/hybrid: Failed to intepret kinetic rate for reaction 'r2' as mass action.
		faults.add("biomodel_82809548.vcml:Stochastic Spatial QUARTER CELL SHORT"); // local/hybrid: Failed to intepret kinetic rate for reaction 'r2' as mass action.
		faults.add("biomodel_82809548.vcml:Stochastic/Deterministic Integrin Ligand Activation"); // local/hybrid: Failed to intepret kinetic rate for reaction 'r2' as mass action.
		faults.add("biomodel_82809548.vcml:Copy of Stochastic Spatial QUARTER CELL SHORT"); // local/hybrid: Failed to intepret kinetic rate for reaction 'r2' as mass action.
		faults.add("biomodel_82809561.vcml:Stochastic Spatial QUARTER CELL SHORT"); // local/hybrid: Failed to intepret kinetic rate for reaction 'r2' as mass action.
		faults.add("biomodel_82809561.vcml:Stochastic/Deterministic Integrin Ligand Activation"); // local/hybrid: Failed to intepret kinetic rate for reaction 'r2' as mass action.
		faults.add("biomodel_82809561.vcml:Copy of Stochastic Spatial QUARTER CELL SHORT"); // local/hybrid: Failed to intepret kinetic rate for reaction 'r2' as mass action.
		faults.add("biomodel_88789981.vcml:Copy of Copy of Application0"); // local/hybrid: Clamped Species must be continuous rather than particles.
		faults.add("biomodel_88820373.vcml:Copy of Copy of Application0"); // local/hybrid:
		faults.add("biomodel_88834881.vcml:Copy of Copy of Application0"); // local/hybrid: Clamped Species must be continuous rather than particles.
		return faults;
	}

	public static Map<String, MathCompareResults.Decision> knownLegacyFaults() {
		HashMap<String, MathCompareResults.Decision> faults = new HashMap();
		faults.put("lumped_reaction_proper_size_in_rate.vcml:Application0", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // (not saved): expressions are different: ' - (3.321077566325453E-8 * s1)' vs ' - (0.001660538783162726 * s1)'
		faults.put("biomodel_47429473.vcml:NWASP at Lam Tip in 3D Geometry", MathCompareResults.Decision.MathDifferent_DIFFERENT_FASTINV_EXPRESSION); // (les:6:2010-08-12:Public): could not find a match for fast invariant expression'Expression@b29ced42 '(BarbedD_Cyt - Prof_Cyt + BarbedDPi_Cyt + BarbedT_Cyt)''
		faults.put("biomodel_55178308.vcml:Spatial 1 - 3D -  electrophysiology", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // (Brown:26331356:2011-03-12:Private): line #630 Exception: variable Na not defined
		faults.put("biomodel_97075423.vcml:NFSim", MathCompareResults.Decision.MathDifferent_LEGACY_SYMMETRY_PARTICLE_JUMP_PROCESS); // (mblinov:12487253:2015-10-07:Private): PJP='r1', ProcessSymmetryFactor: old='1.0', new='0.5'
		faults.put("biomodel_97705317.vcml:NFSim app", MathCompareResults.Decision.MathDifferent_DIFFERENT_PARTICLE_JUMP_PROCESS); // (BioNetGen:95093638:2015-11-23:Public): PJP='r10', SymmetryFactor: old='0.5' new='0.5', rate: old='[0.0016611295681063123]', new='[0.0033222591362126247]'
		faults.put("biomodel_97786619.vcml:NFSim app", MathCompareResults.Decision.MathDifferent_DIFFERENT_PARTICLE_JUMP_PROCESS); // (BioNetGen:95093638:2015-12-02:Public): PJP='r10', SymmetryFactor: old='0.5' new='0.5', rate: old='[0.0016611295681063123]', new='[0.0033222591362126247]'

		// all private/public legacy Flux Reaction problems (bad cached XML) rest of 309 models are okay (this model is user 'schaff')
		faults.put("biomodel_90935642.vcml:3D pde",MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // (17:2014-09-10:Public): expressions are different: ' - (2.0 * (RanC_cyt - RanC_nuc))' vs '0.0'
		faults.put("biomodel_90935642.vcml:non-spatial ODE",MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // (17:2014-09-10:Public): expressions are different: '( - (RanC_cyt - (1000.0 * C_cyt * C_cyt)) - (0.1889313531211006 * (RanC_cyt - (2.704885868157603E-4 * (1.6611295681063125 - (14891.899581611733 * C_cyt)

		// all private legacy Mass Action models with no reactant and no-zero Kf (all rest of models identified in issue 536 are fixed)
		faults.put("biomodel_115999897.vcml:Application0",MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // (60339657:2017-08-06:Private): expressions are different: '(PI45P2 - (0.03 * IP3))' vs '((0.0928262 * PI45P2) - (0.03 * IP3))'
		faults.put("biomodel_18978432.vcml:Aggregation",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // (17350612:2006-08-27:Private): equations have different number of expressions
		faults.put("biomodel_196348974.vcml:prise2",MathCompareResults.Decision.MathDifferent_SUBDOMAINS_DONT_MATCH); // (195757411:2020-12-17:Private): subdomain Compartment
		faults.put("biomodel_2027455.vcml:FrogEggExtractSimple",MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // (120904:2001-06-07:Private): expressions are different: ' - ( - (2.0E-5 * (0.1 - MPFia_Cell - Cdc2_Cell - MPF_Cell - MPFi_Cell) * Cdc25i_Cell / (0.1 + Cdc25i_Cell)) + (0.1 * (0.001 - Cdc25i_Cell) / (1.001 - Cd
		faults.put("biomodel_2027455.vcml:testing",MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED); // (120904:2001-06-07:Private): only one mathDescription had equation for 'Compartment::APCa_Cell' in SubDomain 'Compartment'
		faults.put("biomodel_2027747.vcml:FrogEggExtractSimple",MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // (120904:2001-06-07:Private): expressions are different: ' - ( - (2.0E-5 * (0.1 - MPFia_Cell - Cdc2_Cell - MPF_Cell - MPFi_Cell) * Cdc25i_Cell / (0.1 + Cdc25i_Cell)) + (0.1 * (0.001 - Cdc25i_Cell) / (1.001 - Cd
		faults.put("biomodel_2027747.vcml:testing",MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED); // (120904:2001-06-07:Private): only one mathDescription had equation for 'Compartment::APCa_Cell' in SubDomain 'Compartment'
		faults.put("biomodel_2028176.vcml:FrogEggExtractSimple",MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // (120904:2001-06-07:Private): expressions are different: ' - ( - (2.0E-5 * (0.1 - MPFia_Cell - Cdc2_Cell - MPF_Cell - MPFi_Cell) * Cdc25i_Cell / (0.1 + Cdc25i_Cell)) + (0.1 * (0.001 - Cdc25i_Cell) / (1.001 - Cd
		faults.put("biomodel_2028176.vcml:testing",MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED); // (120904:2001-06-07:Private): only one mathDescription had equation for 'Compartment::APCa_Cell' in SubDomain 'Compartment'
		faults.put("biomodel_2028610.vcml:FrogEggExtractSimple",MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // (120904:2001-06-07:Private): expressions are different: ' - ( - (2.0E-5 * (0.1 - MPFia_Cell - Cdc2_Cell - MPF_Cell - MPFi_Cell) * Cdc25i_Cell / (0.1 + Cdc25i_Cell)) + (0.1 * (0.001 - Cdc25i_Cell) / (1.001 - Cd
		faults.put("biomodel_2028610.vcml:testing",MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED); // (120904:2001-06-07:Private): only one mathDescription had equation for 'Compartment::APCa_Cell' in SubDomain 'Compartment'
		faults.put("biomodel_2028855.vcml:testing",MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED); // (120904:2001-06-07:Private): only one mathDescription had equation for 'Compartment::APCa_Cell' in SubDomain 'Compartment'
		faults.put("biomodel_2030592.vcml:FrogEggExtractSimple",MathCompareResults.Decision.MathDifferent_EQUATION_ADDED); // (120904:2001-06-07:Private): only one mathDescription had equation for 'APCa_Cell' in SubDomain 'Compartment'
		faults.put("biomodel_24470582.vcml:comp2",MathCompareResults.Decision.MathDifferent_DIFFERENT_FASTRATE_EXPRESSION); // (17:2007-11-15:Private): fast rate expressions are different Old: 'Expression@d009a7a0 ' - ( - (100.0 * IP3_cyt * RI) - (30.000000030000002 * RI))''fast rate expressions are different New: 'Expression@ef6
		faults.put("biomodel_29476020.vcml:s",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES); // (2302355:2008-10-28:Private): 2 vs 3
		faults.put("biomodel_29476020.vcml:stoch",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES); // (2302355:2008-10-28:Private): 2 vs 3
		faults.put("biomodel_29476020.vcml:s2",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES); // (2302355:2008-10-28:Private): 2 vs 3
		faults.put("biomodel_36587627.vcml:gradient_sensing",MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED); // (35091731:2009-11-07:Group=[jmhaugh(13213103)]):
		faults.put("biomodel_36588994.vcml:RL_binding",MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED); // (35091731:2009-11-07:Group=[jmhaugh(13213103)]):
		faults.put("biomodel_36588994.vcml:gradient",MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED); // (35091731:2009-11-07:Group=[jmhaugh(13213103)]):
		faults.put("biomodel_36800083.vcml:RL_binding",MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED); // (35091731:2009-11-17:Group=[jmhaugh(13213103)]):
		faults.put("biomodel_36800083.vcml:gradient",MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED); // (35091731:2009-11-17:Group=[jmhaugh(13213103)]):
		faults.put("biomodel_38770411.vcml:RL_binding",MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED); // (35091731:2010-02-11:Private):
		faults.put("biomodel_40500269.vcml:3D",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // (15957189:2010-03-19:Private): fast invariants have different number of expressions
		faults.put("biomodel_40500269.vcml:2Dprova3D",MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // (15957189:2010-03-19:Private): line #128 Exception: variable Ca1_green not defined
		faults.put("biomodel_40500269.vcml:3Dsph",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // (15957189:2010-03-19:Private): fast invariants have different number of expressions
		faults.put("biomodel_40500269.vcml:3DclustB",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // (15957189:2010-03-19:Private): fast invariants have different number of expressions
		faults.put("biomodel_40500269.vcml:3DclustBCh",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // (15957189:2010-03-19:Private): fast invariants have different number of expressions
		faults.put("biomodel_40500269.vcml:3D clust Ch",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // (15957189:2010-03-19:Private): fast invariants have different number of expressions
		faults.put("biomodel_40500269.vcml:3D clust ch piccolo",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // (15957189:2010-03-19:Private): fast invariants have different number of expressions
		faults.put("biomodel_40500269.vcml:clust pic double",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // (15957189:2010-03-19:Private): fast invariants have different number of expressions
		faults.put("biomodel_40500269.vcml:clust pic double sx dx",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // (15957189:2010-03-19:Private): fast invariants have different number of expressions
		faults.put("biomodel_44582721.vcml:1",MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED); // (37651188:2010-05-16:Private):
		faults.put("biomodel_50946331.vcml:C:\\Documents and Settings\\cfalkenberg\\.vcell\\BioNetGen\\vcell_bng_24405\\vcell_bng_24405_Compartmental",MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // (3886845:2010-11-03:Private):
		faults.put("biomodel_50946331.vcml:v2",MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // (3886845:2010-11-03:Private):
		faults.put("biomodel_5560175.vcml:SpatialModelUSASmall",MathCompareResults.Decision.MathDifferent_UNKNOWN_DIFFERENCE_IN_EQUATION); // (418:2003-06-19:Private): couldn't find problem with jumpCondition for I in compartment Inside_Outside_membrane
		faults.put("biomodel_56588035.vcml:Smol_r4",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_PARTICLE_JUMP_PROCESS); // (17096841:2011-04-27:Private): removed PJPs=[r4_reverse], added PJPs=[]
		faults.put("biomodel_56588035.vcml:Smol_r3",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_PARTICLE_JUMP_PROCESS); // (17096841:2011-04-27:Private): removed PJPs=[r3_reverse], added PJPs=[]
		faults.put("biomodel_56588035.vcml:Smol_r1",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_PARTICLE_JUMP_PROCESS); // (17096841:2011-04-27:Private): removed PJPs=[r1], added PJPs=[]
		faults.put("biomodel_56588035.vcml:Smol_r0",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_PARTICLE_JUMP_PROCESS); // (17096841:2011-04-27:Private): removed PJPs=[r0], added PJPs=[]
		faults.put("biomodel_56588035.vcml:Smol_r5",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_PARTICLE_JUMP_PROCESS); // (17096841:2011-04-27:Private): removed PJPs=[r5], added PJPs=[]
		faults.put("biomodel_56588035.vcml:Smol_r6",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_PARTICLE_JUMP_PROCESS); // (17096841:2011-04-27:Private): removed PJPs=[r6_reverse], added PJPs=[]
		faults.put("biomodel_85221354.vcml:Application1",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // (84148778:2013-11-25:Private): equations have different number of expressions
		faults.put("biomodel_85241086.vcml:Application1",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // (84148778:2013-11-27:Private): equations have different number of expressions
		faults.put("biomodel_85305863.vcml:Application0",MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // (84148778:2013-12-03:Private): equations have different number of expressions
		faults.put("biomodel_87760173.vcml:Application0",MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // (34302033:2014-04-30:Private): expressions are different: ' - (4.697674418604652E-5 * Ste2_active * ((0.0012 * Ste2_active * Alfa) - (0.6 * Ste2_active) - (0.24 * Ste2_active)) * Alfa)' vs ' - (4.697674418604652
		faults.put("biomodel_94794583.vcml:Steady state",MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // (94778569:2015-05-13:Private): expressions are different: ' - (20.0 * IP3_Cyt)' vs '0.0'


		// all private Hybrid models (files not committed to repo)
		faults.put("biomodel_100059482.vcml:3D hybrid", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (gerardw:81356985:2016-05-20:Group=[fgao(6606010),schaff(17)]): PJP='flux0_reverse', old='[(2.0 * RanC_nuc)]', new='[(1204.0 * RanC_nuc)]'
		faults.put("biomodel_101986247.vcml:Application0", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (boris:21:2016-08-18:Private): PJP='r0', old='[(0.0016611295681063123 * A)]', new='[A]'
		faults.put("biomodel_102370928.vcml:single cycle_hybrid_stirred", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (boris:21:2016-09-15:Private): PJP='r0', old='[(1.920265780730897E-4 * S)]', new='[(0.1156 * S)]'
		faults.put("biomodel_111277118.vcml:single cycle_hybrid_stirred", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // (boris:21:2017-04-14:Group=[DCResasco(14570623),schaff(17)]): infinite loop in eliminating function nesting
		faults.put("biomodel_111277118.vcml:Copy of single cycle_hybrid_stirred", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // (boris:21:2017-04-14:Group=[DCResasco(14570623),schaff(17)]):
		faults.put("biomodel_82456311.vcml:smoldyn", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_PARTICLE_JUMP_PROCESS); // (schaff:17:2013-05-30:Private): removed PJPs=[], added PJPs=[r0_reverse, r0]
		faults.put("biomodel_82456311.vcml:hybrid", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // (schaff:17:2013-05-30:Private): expressions are different: ' - ((s0 * s2) - (2.0 * s1))' vs ' - ((0.0016611295681063123 * s0 * s2) - (0.0033222591362126247 * s1))'
		faults.put("biomodel_82456701.vcml:smoldyn", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_PARTICLE_JUMP_PROCESS); // (schaff:17:2013-05-30:Private): removed PJPs=[], added PJPs=[r0_reverse, r0]
		faults.put("biomodel_82456701.vcml:hybrid", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // (schaff:17:2013-05-30:Private): expressions are different: ' - ((s0 * s2) - (2.0 * s1))' vs ' - ((0.0016611295681063123 * s0 * s2) - (0.0033222591362126247 * s1))'
		faults.put("biomodel_82457170.vcml:smoldyn", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_PARTICLE_JUMP_PROCESS); // (schaff:17:2013-05-30:Private): removed PJPs=[], added PJPs=[r0_reverse, r0]
		faults.put("biomodel_82457170.vcml:hybrid", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (schaff:17:2013-05-30:Private): PJP='r0', old='[(0.0016611295681063123 * s2)]', new='[s2]'
		faults.put("biomodel_82457836.vcml:3D stoch", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_PARTICLE_JUMP_PROCESS); // (schaff:17:2013-05-31:Private): removed PJPs=[], added PJPs=[flux0_reverse, flux0]
		faults.put("biomodel_82457836.vcml:Copy of 3D stoch", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (schaff:17:2013-05-31:Private): PJP='flux0_reverse', old='[(2.0 * RanC_nuc)]', new='[(1204.0 * RanC_nuc)]'
		faults.put("biomodel_82790975.vcml:Stochastic Spatial QUARTER CELL SHORT", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (user_3:80664633:2013-06-19:Private): PJP='IntLigandBinding', old='[(0.008305647840531562 * IntLigand)]', new='[(5.0 * IntLigand)]'
		faults.put("biomodel_82790975.vcml:Stochastic/Deterministic Integrin Ligand Activation", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (user_3:80664633:2013-06-19:Private): PJP='IntLigandBinding', old='[(0.008305647840531562 * IntLigand)]', new='[(5.0 * IntLigand)]'
		faults.put("biomodel_82800592.vcml:Test of Hybrid", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (user_3:80664633:2013-06-19:Private): PJP='r0', old='[(0.0049833887043189366 * IntLigand)]', new='[(3.0 * IntLigand)]'
		faults.put("biomodel_88789981.vcml:Copy of Application0", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (user_2:88443094:2014-06-10:Private): PJP='ATP_r', old='[(0.8305647840531561 * Ca_cyt)]', new='[(500.0 * Ca_cyt)]'
		faults.put("biomodel_88820373.vcml:Stochastic_VGCCopening", MathCompareResults.Decision.MathDifferent_DIFFERENT_PARTICLE_JUMP_PROCESS); // (user_2:88443094:2014-06-10:Private): PJP='Syt_r', SymmetryFactor: old='1.0' new='1.0', rate: old='[(1.4545055544473134E-12 * Ca_cyt)]', new='[(115.0 * Ca_cyt)]'
		faults.put("biomodel_88834881.vcml:Stochastic_VGCCopening", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // (user_2:88443094:2014-06-11:Private):
		faults.put("biomodel_89716975.vcml:Hybrid", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (schaff:17:2014-07-18:Group=[boris(21)]): PJP='rAC', old='[A]', new='[(602.0 * A)]'
		faults.put("biomodel_95401413.vcml:3D Stochastic", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (user_1:93509706:2015-06-26:Private): PJP='V_MARCKS', old='[(0.016611295681063124 * M)]', new='[(10.0 * M)]'
		faults.put("biomodel_95401413.vcml:Application0", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (user_1:93509706:2015-06-26:Private): PJP='PDGF_binding_active_gradient', old='[(1.107419712070875E-5 * (1.0 + (0.001667 * x)))]', new='[(0.006666666666666667 * (1.0 + (0.001667 * x)))]'
		faults.put("biomodel_95401686.vcml:3D Stochastic", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (user_1:93509706:2015-06-26:Private): PJP='V_MARCKS', old='[(0.016611295681063124 * M)]', new='[(10.0 * M)]'
		faults.put("biomodel_95401705.vcml:3D Stochastic", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (user_1:93509706:2015-06-26:Private): PJP='V_MARCKS', old='[(0.016611295681063124 * M)]', new='[(10.0 * M)]'
		faults.put("biomodel_95401705.vcml:Application0", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (user_1:93509706:2015-06-26:Private): PJP='PDGF_binding_active_gradient', old='[1.107419712070875E-5]', new='[0.006666666666666667]'
		faults.put("biomodel_95420572.vcml:3D Stochastic Spatial", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (user_1:93509706:2015-06-29:Private): PJP='V_MARCKS', old='[(0.016611295681063124 * M)]', new='[(10.0 * M)]'
		faults.put("biomodel_95420572.vcml:3D Stochastic Spatial Small Cube", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (user_1:93509706:2015-06-29:Private): PJP='PDGF_binding_active_gradient', old='[(0.70272 * (1.0 + (0.001667 * x)))]', new='[(423.03744000000006 * (1.0 + (0.001667 * x)))]'
		faults.put("biomodel_95439383.vcml:3D Stochastic", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (user_1:93509706:2015-07-01:Private): PJP='V_MARCKS', old='[(0.016611295681063124 * M)]', new='[(10.0 * M)]'
		faults.put("biomodel_95439383.vcml:Application0", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (user_1:93509706:2015-07-01:Private): PJP='PDGF_binding_active_gradient', old='[(0.70272 * (1.0 + (0.001667 * x)))]', new='[(423.03744000000006 * (1.0 + (0.001667 * x)))]'
		faults.put("biomodel_97188386.vcml:3D hybrid", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (gerardw:81356985:2015-10-21:Private): PJP='flux0_reverse', old='[(2.0 * RanC_nuc)]', new='[(1204.0 * RanC_nuc)]'
		faults.put("biomodel_97210786.vcml:3D hybrid", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (gerardw:81356985:2015-10-23:Private): PJP='flux0_reverse', old='[(2.0 * RanC_nuc)]', new='[(1204.0 * RanC_nuc)]'
		faults.put("biomodel_97236290.vcml:3D hybrid", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (gerardw:81356985:2015-10-26:Private): PJP='flux0_reverse', old='[(2.0 * RanC_nuc)]', new='[(1204.0 * RanC_nuc)]'
		faults.put("biomodel_97236377.vcml:3D hybrid", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (gerardw:81356985:2015-10-26:Private): PJP='flux0_reverse', old='[(2.0 * RanC_nuc)]', new='[(1204.0 * RanC_nuc)]'
		faults.put("biomodel_97536525.vcml:3D hybrid", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (gerardw:81356985:2015-11-04:Group=[fgao(6606010),schaff(17)]): PJP='flux0_reverse', old='[(2.0 * RanC_nuc)]', new='[(1204.0 * RanC_nuc)]'
		faults.put("biomodel_97553821.vcml:3D hybrid", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (gerardw:81356985:2015-11-05:Group=[fgao(6606010),schaff(17)]): PJP='flux0_reverse', old='[(2.0 * RanC_nuc)]', new='[(1204.0 * RanC_nuc)]'
		faults.put("biomodel_97557776.vcml:3D hybrid", MathCompareResults.Decision.MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS); // (gerardw:81356985:2015-11-06:Group=[fgao(6606010),schaff(17)]): PJP='flux0_reverse', old='[(2.0 * RanC_nuc)]', new='[(1204.0 * RanC_nuc)]'
		return faults;
	}

	public static Map<String, MathCompareResults.Decision> knownReductionFaults() {
		HashMap<String, MathCompareResults.Decision> faults = new HashMap();
		faults.put("biomodel_100961371.vcml:modelinOct2010", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: ' - (9.963232698976356E-4 * ((1.2000000
		faults.put("biomodel_148700996.vcml:Fast B Compartmental", MathCompareResults.Decision.MathDifferent_DIFFERENT_FASTINV_EXPRESSION); // =REDUCED= MathDifferent:DifferentFastInvExpression:could not find a match for fast invariant expression'Expres
		faults.put("biomodel_148700996.vcml:2DGeom nonuniform dens v2", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_156134818.vcml:unnamed", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '( - (7.961783439490447E-5 * EGF_EGFR_2
		faults.put("biomodel_17257105.vcml:compartmental", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((0.0011 * (L_b1AR_cell + L_b1AR_Gs_ce
		faults.put("biomodel_17257105.vcml:spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_209284198.vcml:fast compart with 3D S:V & V:V", MathCompareResults.Decision.MathDifferent_DIFFERENT_FASTINV_EXPRESSION); // =REDUCED= MathDifferent:DifferentFastInvExpression:could not find a match for fast invariant expression'Expres
		faults.put("biomodel_2917788.vcml:Figure 4.3: Fast Approximation", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '0.0' vs '( - ((20.0 * x_o) - (50.0 * x
		faults.put("biomodel_32568171.vcml:Sim5_1", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:GoodModel17_5", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:Furaptra", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_1AP", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_20AP", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:Furaptra_1AP", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_1AP_v2", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_20AP_v2", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_1AP_v3", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_20AP_v3", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:Furaptra_1AP_v3", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_20AP_v4", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_80Hz_v4", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568356.vcml:SyGCaMP2_1AP", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568356.vcml:SyGCaMP2_20AP", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568356.vcml:SyGCaMP2_1AP_v2", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568356.vcml:SyGCaMP2_20AP_v2", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568356.vcml:SyGCaMP2_1AP_v3", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568356.vcml:SyGCaMP2_20AP_v4", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568356.vcml:SyGCaMP2_80Hz_v4", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_38086434.vcml:CALI, fine mesh", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_38086434.vcml:CALI, VASP=0, fine mesh", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_38086434.vcml:CALI experiment with FRAP intensities", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_38086434.vcml:CALI without KND CP=1.5", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_38086434.vcml:CALI experiment with R=5", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_49411430.vcml:muscle PI release", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((5.0E-4 * ADPPIFactin_PI_release) - (
		faults.put("biomodel_49411430.vcml:POmbe actin pi release", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((5.0E-4 * ADPPIFactin_PI_release) - (
		faults.put("biomodel_49411430.vcml:pombe actin polymerization", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((5.0E-4 * ADPPIFactin_PI_release) - (
		faults.put("biomodel_49411430.vcml:0913", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((5.0E-4 * ADPPIFactin_PI_release) - (
		faults.put("biomodel_49411430.vcml:0930", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((5.0E-4 * ADPPIFactin_PI_release) - (
		faults.put("biomodel_49411430.vcml:1002", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((5.0E-4 * ADPPIFactin_PI_release) - (
		faults.put("biomodel_59280306.vcml:compartmental", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((0.0011 * (L_b1AR_cell + L_b1AR_Gs_ce
		faults.put("biomodel_59280306.vcml:spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60203358.vcml:AP", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((0.010526315789473684 * I1Na_Sarcopla
		faults.put("biomodel_60203358.vcml:IKsum", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // =REDUCED= MathDifferent:FailedUnknown:failed to compare math: too many failed evaluations (0 of 20) (java.lang
		faults.put("biomodel_60203358.vcml:AP with pulse", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((0.010526315789473684 * I1Na_Sarcopla
		faults.put("biomodel_60227051.vcml:3D", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:2Dprova3D", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:3Dsph", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:3DclustB", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:3DclustBCh", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:3D clust Ch", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:3D clust ch piccolo", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:clust pic double", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:clust pic double sx dx", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_66264973.vcml:compartmental - IC-G2736X", MathCompareResults.Decision.MathDifferent_EQUATION_ADDED); // =REDUCED= MathDifferent:EquationAdded:only one mathDescription had equation for 'Compartment::ICpeptide_mAtaxi
		faults.put("biomodel_66265579.vcml:compartmental - IC4", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '0.0' vs '((0.005860724109735934 * ((17
		faults.put("biomodel_91134339.vcml:test", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '( - ((0.03 * C_Unnamed_compartment * (
		faults.put("biomodel_9254662.vcml:FastBuffExpReceptor", MathCompareResults.Decision.MathDifferent_DIFFERENT_FASTINV_EXPRESSION); // =REDUCED= MathDifferent:DifferentFastInvExpression:could not find a match for fast invariant expression'Expres
		faults.put("biomodel_98147638.vcml:compartmental - IC4", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '0.0' vs '((0.005860724109735934 * ((17
		return faults;
	}

	/**
	 * process all tests that are available - the slow set is parsed only and not processed.
	 */
	@Parameterized.Parameters
	public static Collection<String> testCases() throws XmlParseException, IOException {
		Predicate<String> skipFilter = (t) -> !outOfMemoryFileSet().contains(t) && !largeFileSet().contains(t) && !slowFileSet().contains(t);
		List<String> filenames = Arrays.stream(VcmlTestSuiteFiles.getVcmlTestCases()).filter(skipFilter).collect(Collectors.toList());

		ArrayList<String> appTestCases = new ArrayList<>();
		for (String filename : filenames){
//if (true
//&& !filename.equals("biomodel_28555193.vcml")
//&& !filename.equals("biomodel_28572365.vcml")
//&& !filename.equals("biomodel_29136875.vcml")
//&& !filename.equals("biomodel_29136972.vcml")
//&& !filename.equals("biomodel_39781155.vcml")
//&& !filename.equals("biomodel_39781314.vcml")
//&& !filename.equals("biomodel_94794583.vcml")
//) continue;
			String vcmlStr;
			try (InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);) {
				vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
						.lines().collect(Collectors.joining("\n"));
			}
			BioModel biomodel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
//if (biomodel.getModel().getStructureTopology().isEmpty()) continue;
			for (SimulationContext simContext : biomodel.getSimulationContexts()){
				String test_case_name = filename + ":" + simContext.getName();
//if (!knownLegacyFaults().containsKey(test_case_name)) continue;
				appTestCases.add(test_case_name);
			}
		}
		return appTestCases;
//		return Arrays.asList("biomodel_12522025.vcml:purkinje9");
	}


	@Test
	public void test_legacy_math_compare() throws Exception {
		String[] tokens = filename_colon_appname.split(":");
		String filename = tokens[0];
		String simContextName = filename_colon_appname.substring(filename.length()+1);
		String vcmlStr;
		try (InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);) {
			vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
					.lines().collect(Collectors.joining("\n"));
		}

		System.out.println("testing test case "+filename_colon_appname);
		BioModel orig_biomodel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
		orig_biomodel.refreshDependencies();

		BioModel transformed_biomodel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
		transformed_biomodel.refreshDependencies();

		boolean bTransformKMOLE = true;
		boolean bTransformVariables = false;

		SimulationContext orig_simContext = orig_biomodel.getSimulationContext(simContextName);
		MathDescription originalMath = orig_simContext.getMathDescription();
		MathDescription origMathClone = new MathDescription(originalMath); // test round trip to/from MathDescription.readFromDatabase()
		SimulationContext new_simContext = transformed_biomodel.getSimulationContexts(orig_simContext.getName());
		//new_simContext.setUsingMassConservationModelReduction(false);
		boolean bKnownMathGenerationFailure = knownMathGenerationFailures().contains(filename_colon_appname);
		try {
			if (bTransformKMOLE) {
				boolean bWasTransformed = false;
				try {
					bWasTransformed = BioModelTransforms.restoreOldReservedSymbolsIfNeeded(transformed_biomodel);
					new_simContext.updateAll(false);
				} finally {
					if (bWasTransformed) BioModelTransforms.restoreLatestReservedSymbols(transformed_biomodel);
				}
			}else{
				new_simContext.updateAll(false);
			}
			Assert.assertFalse("math generation succeeded for '"+filename_colon_appname+"', " +
					"but expecting math generation failure, remove from knownMathGenerationFailures()",
					bKnownMathGenerationFailure);
		} catch (Exception e){
			e.printStackTrace();
			if (!bKnownMathGenerationFailure){
				Assert.fail("math generation failed for '"+filename_colon_appname+"', but expecting math to generate, add to knownMathGenerationFailures()");
			}
		}
		MathDescription newMath = new_simContext.getMathDescription();
		MathDescription newMathClone = new MathDescription(newMath); // test round trip to/from MathDescription.readFromDatabase()
		MathCompareResults results = null;
		if (bTransformVariables) {
			results = MathDescription.testEquivalencyWithRename(SimulationSymbolTable.createMathSymbolTableFactory(), originalMath, newMath);
		}else {
			results = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), originalMath, newMath);
		}
		MathCompareResults.Decision knownFault = knownLegacyFaults().get(filename_colon_appname);
		if (results.isEquivalent() && knownFault != null){
			Assert.fail("math equivalent for '"+filename_colon_appname+"', but expecting '"+knownFault+"', remove known fault");
		}
		if (!results.isEquivalent()){
			// try again using non-reduced math
			new_simContext.setUsingMassConservationModelReduction(false);
			try {
				if (bTransformKMOLE) {
					boolean bWasTransformed = false;
					try {
						bWasTransformed = BioModelTransforms.restoreOldReservedSymbolsIfNeeded(transformed_biomodel);
						new_simContext.updateAll(false);
					} finally {
						if (bWasTransformed) BioModelTransforms.restoreLatestReservedSymbols(transformed_biomodel);
					}
				}else{
					new_simContext.updateAll(false);
				}
				Assert.assertFalse("math generation succeeded for '"+filename_colon_appname+"', " +
									"but expecting math generation failure, remove from knownMathGenerationFailures()",
							bKnownMathGenerationFailure);
			} catch (Exception e) {
				e.printStackTrace();
				if (!bKnownMathGenerationFailure) {
					Assert.fail("math generation failed for '" + filename_colon_appname + "', but expecting math to generate, add to knownMathGenerationFailures()");
				}
			}
			newMath = new_simContext.getMathDescription();
			MathCompareResults results2 = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), originalMath, newMath);

			if (!results2.isEquivalent()) {
				try (BufferedWriter codeProblemFileWriter = new BufferedWriter(new FileWriter(codeKnownProblemFile, true));
					 BufferedWriter csvProblemFileWriter = new BufferedWriter(new FileWriter(csvKnownProblemFile, true));
				) {
					Version version = orig_biomodel.getVersion();
					String ownerUserid = "", ownerKey = "", date = "", privacy = "";
					if (version != null && version.getOwner() != null){
						ownerUserid = version.getOwner().getName();
						ownerKey = version.getOwner().getID().toString();
						date = new java.sql.Date(version.getDate().getTime()).toLocalDate().toString();
						privacy = version.getGroupAccess().toString();
					}
					String cause = results2.toCause().substring(0, Math.min(results2.toCause().length(), 180));
					codeProblemFileWriter.write("faults.put(\"" + filename_colon_appname + "\"" +
							",MathCompareResults.Decision." + results2.decision + ");" +
							" // ("+ownerKey+":"+date+":"+privacy+"): " + cause + "\n");
					csvProblemFileWriter.write(filename+"|"+date+"|"+ownerUserid+"("+ownerKey+")"+"|"+privacy+
							"|"+orig_biomodel.getName()+"|"+simContextName+"|"+results2.decision+"|"+cause+"\n");
				}
				if (knownFault == null) {
					Assert.fail("'" + filename_colon_appname + "' expecting equivalent, " +
							"computed '" + results2.decision + "', " +
							"details: " + results2.toDatabaseStatus());
				} else {
					Assert.assertTrue("'" + filename_colon_appname + "' expecting '" + knownFault + "', " +
									"computed '" + results2.decision + "', " +
									"details: " + results2.toDatabaseStatus(),
							knownFault == results2.decision);
				}
			}
		}
	}

	@Test
	@Ignore
	public void test_identity_math_compare() throws Exception {
		String[] tokens = filename_colon_appname.split(":");
		String filename = tokens[0];
		String simContextName = filename_colon_appname.substring(filename.length() + 1);
		String vcmlStr;
		try (InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);) {
			vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
					.lines().collect(Collectors.joining("\n"));
		}

		System.out.println("testing test case " + filename_colon_appname);

		//
		// read in 'reduced' model: generate math for application <<with>> model reduction from mass conservation
		//
		BioModel reducedBiomodel1 = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
		reducedBiomodel1.refreshDependencies();
		SimulationContext reducedSimContext1 = reducedBiomodel1.getSimulationContext(simContextName);
		reducedSimContext1.setUsingMassConservationModelReduction(true);
		reducedSimContext1.updateAll(false);
		MathDescription reducedMath1 = reducedSimContext1.getMathDescription();

		BioModel reducedBiomodel2 = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
		reducedBiomodel2.refreshDependencies();
		SimulationContext reducedSimContext2 = reducedBiomodel2.getSimulationContext(simContextName);
		reducedSimContext2.setUsingMassConservationModelReduction(true);
		reducedSimContext2.updateAll(false);
		MathDescription reducedMath2 = reducedSimContext2.getMathDescription();

		//
		// compare identity (math against clone of itself - using the reduced model)
		//
		MathCompareResults identityCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), reducedMath1, reducedMath2);
		Assert.assertTrue("math not equivalent for '" + filename_colon_appname + "': " + identityCompareResults.toDatabaseStatus(), identityCompareResults.isEquivalent());
	}

	@Test
	@Ignore
	public void test_model_reduction_math_compare() throws Exception {
		String[] tokens = filename_colon_appname.split(":");
		String filename = tokens[0];
		String simContextName = filename_colon_appname.substring(filename.length()+1);
		String vcmlStr;
		try (InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);) {
			vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
					.lines().collect(Collectors.joining("\n"));
		}

		System.out.println("testing test case "+filename_colon_appname);

		//
		// read in 'reduced' model: generate math for application <<with>> model reduction from mass conservation
		//  - but skip entire test if not ODE or PDE (we don't do model reduction for stochastic models)
		//
		BioModel reducedBiomodel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
		SimulationContext reducedSimContext = reducedBiomodel.getSimulationContext(simContextName);
		if (reducedSimContext.getApplicationType() != SimulationContext.Application.NETWORK_DETERMINISTIC){
			return; // not ODEs or PDEs, so no model reduction - nothing to test.
		}
		reducedBiomodel.refreshDependencies();
		reducedSimContext.setUsingMassConservationModelReduction(true);
		reducedSimContext.updateAll(false);
		MathDescription reducedMath = reducedSimContext.getMathDescription();


		//
		// for ODE applications, ensure that disabling mass conservation compares as equivalent to generating with mass conservation
		//
		// read in 'full' model: generate math for application <<without>> model reduction from mass conservation
		//
		BioModel fullBiomodel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
		fullBiomodel.refreshDependencies();
		SimulationContext fullSimContext = fullBiomodel.getSimulationContexts(simContextName);
		fullSimContext.setUsingMassConservationModelReduction(false);
		fullSimContext.updateAll(false);
		MathDescription fullMath = fullSimContext.getMathDescription();

		MathCompareResults reductionCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), reducedMath, fullMath);

		MathCompareResults.Decision knownReductionFault = knownReductionFaults().get(filename_colon_appname);
		if (reductionCompareResults.isEquivalent() && knownReductionFault != null){
			Assert.fail("math equivalent for '"+filename_colon_appname+"', but expecting '"+knownReductionFault+"', remove known fault");
		}
		if (!reductionCompareResults.isEquivalent()){
			try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(codeKnownProblemFile, true));){
				bufferedWriter.write("faults.put(\""+filename_colon_appname+"\", MathCompareResults.Decision."+reductionCompareResults.decision+"); // =REDUCED= "
						+reductionCompareResults.toDatabaseStatus().substring(0, Math.min(reductionCompareResults.toDatabaseStatus().length(), 100))+"\n");
			}
			if (knownReductionFault == null) {
				Assert.fail("'"+filename_colon_appname+"' expecting equivalent, " +
						"computed '"+reductionCompareResults.decision+"', " +
						"details: " + reductionCompareResults.toDatabaseStatus());
			}else{
				Assert.assertTrue("'"+filename_colon_appname+"' expecting '"+knownReductionFault+"', " +
								"computed '"+reductionCompareResults.decision+"', " +
								"details: " + reductionCompareResults.toDatabaseStatus(),
						knownReductionFault == reductionCompareResults.decision);
			}
		}
	}


}
