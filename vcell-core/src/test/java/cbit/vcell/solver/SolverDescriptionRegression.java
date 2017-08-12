package cbit.vcell.solver;


import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import cbit.vcell.math.ProblemRequirements;
import cbit.vcell.solver.SolverDescription.SolverFeature;
public class SolverDescriptionRegression {
	public static final String SOLVER_SEP = "BEGIN Solver";
	private static String[] SolverNames = { 
		"ForwardEuler",
		"RungeKutta2",
		"RungeKutta4",
		"RungeKuttaFehlberg",
		"AdamsMoulton",
		"IDA",
		"FiniteVolume",
		"StochGibson",
		"HybridEuler",
		"HybridMilstein",
		"HybridMilAdaptive",
		"CVODE",
		"FiniteVolumeStandalone",
		"CombinedSundials",
		"SundialsPDE",
		"Smoldyn",
		"Chombo",
};
	private static SolverDescription getSolverDescription(String name) { 
		try {
			Field f = SolverDescription.class.getField(name);
			SolverDescription sd = (SolverDescription) f.get(null);
			return sd;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error("failing");
		} 
	}
	
	
	//@Test
	public void testDump( )  {
		System.out.println("commencing dump");
		PrintWriter console = new PrintWriter(System.out,true);
		dumpOne("IDA", console);
	}
	
	//@Test
	public void dumpSolvers( ) throws IOException {
		PrintWriter record = new PrintWriter(new FileWriter("solvers.txt") );
		for (String name : SolverNames) {
			dumpOne(name,record);
		}
		record.close();
	}
	
	//@Test
	public void readback( ) throws IOException  {
		CharArrayWriter buffer = new CharArrayWriter();
		
		PrintWriter record = new PrintWriter(buffer);
		for (String name : SolverNames) {
			dumpOne(name,record);
		}
		record.flush();
		char b[] = buffer.toCharArray(); 
		BufferedReader current = new BufferedReader(new CharArrayReader(b) );
		LineNumberReader reference = new LineNumberReader(new FileReader("solvers.ref"));
		boolean nameNext = false;
		String currentSolver = null; 
		while (reference.ready()) {
			String currentLine = current.readLine();
			String referenceLine = reference.readLine();
			if (nameNext) {
				currentSolver = currentLine;
				nameNext = false;
			}
			else if (currentLine.equals(SOLVER_SEP)) {
				nameNext = true;
			}
			if (!currentLine.equals(referenceLine)) {
				System.err.println("mismatch in " + currentSolver + " line " + reference.getLineNumber());
				System.err.println("\treference " + referenceLine);
				System.err.println("\tcurrent " + currentLine);
			}
		}
		reference.close( );
	}
	
	public void dumpOne(String name, PrintWriter dest) {
		SolverDescription se = getSolverDescription(name);
		dest.println(SOLVER_SEP);
		dest.println("Name: " + name);
		dest.println(se.toString());
		dest.println(se.hashCode( ));
		dest.println(se.getDatabaseName());
		dest.println(se.getDisplayLabel());
		dest.println(se.getFullDescription());
		dest.println(se.getShortDisplayLabel());
		//supported features come from hashset with non-deterministic order
		Set<SolverFeature> sf = se.getSupportedFeatures();
		SortedSet<SolverFeature> sorted = new TreeSet<SolverFeature>(sf);
		dest.println(sorted);
		dest.println(se.getTimeOrder());
		dest.println(se.hasErrorTolerance());
		dest.println(se.hasVariableTimestep());
		dest.println(se.isChomboSolver());
		dest.println(se.isGibsonSolver());
		dest.println(se.isJavaSolver());
		dest.println(se.isNonSpatialStochasticSolver());
		dest.println(se.isSemiImplicitPdeSolver());
		dest.println(se.isSpatialStochasticSolver());
		dest.println(se.hasSundialsTimeStepping());
	}
	
	public static Collection<SolverDescription> getSupportingSolverDescriptionse(ProblemRequirements mathDescription) {
		if (mathDescription.isSpatial()) {
			if (mathDescription.isSpatialHybrid()) {
				return SolverDescription.getSolverDescriptions(SolverDescription.SpatialHybridFeatureSet.getSolverFeatures());
			} else if (mathDescription.isSpatialStoch()) {
				return SolverDescription.getSolverDescriptions(SolverDescription.SpatialStochasticFeatureSet.getSolverFeatures());
			} else {
				if (mathDescription.hasFastSystems()) { // PDE with FastSystem
					return SolverDescription.getSolverDescriptions(SolverDescription.PdeFastSystemFeatureSet.getSolverFeatures());
				} else if (mathDescription.hasDirichletAtMembrane()) { 
					return SolverDescription.getSolverDescriptions(SolverDescription.PdeFeatureSetWithDirichletAtMembrane.getSolverFeatures());
				} else {
					return SolverDescription.getSolverDescriptions(SolverDescription.PdeFeatureSetWithoutDirichletAtMembrane.getSolverFeatures());
				}
			}
		} else if (mathDescription.isNonSpatialStoch()) {
			return SolverDescription.getSolverDescriptions(SolverDescription.NonSpatialStochasticFeatureSet.getSolverFeatures());
		} else {
			if (mathDescription.hasFastSystems()) { // ODE with FastSystem
				return SolverDescription.getSolverDescriptions(SolverDescription.OdeFastSystemFeatureSet.getSolverFeatures());
			} else {
				return SolverDescription.getSolverDescriptions(SolverDescription.OdeFeatureSet.getSolverFeatures());
			}
		}
	}


	//@Test
	public void recordMath( ) throws IOException  {
		PrintWriter console = new PrintWriter(new FileWriter("mathdescs.txt"));
		dumpSupportingMath(console);
	}
	//@Test
	public void testMathGen( ) {
		System.out.println("commencing generation"); 
		PrintWriter console = new PrintWriter(System.out,true);
		dumpSupportingMath(console);
	}
	
	public void dumpSupportingMath(PrintWriter out ) {
		TestMathDescription tmd = new TestMathDescription();
		out.print(tmd.getStateNum() + ":" );
		Collection<SolverDescription> matches = SolverDescription.getSupportingSolverDescriptions(tmd);
		//out.println(Arrays.toString(matches));
		out.println(matches.hashCode());
		
		while (tmd.hasMoreStates()) {
			tmd.nextState();
			out.print(tmd.getStateNum() + ":" );
			matches = SolverDescription.getSupportingSolverDescriptions(tmd);
			//out.println(Arrays.toString(matches));
			out.println(matches.hashCode());
		}
			
	}
	
	static <T> boolean equals(Collection<T> lhs, Collection<T> rhs) {
		return lhs.size( ) == rhs.size( ) && lhs.containsAll(rhs)  && rhs.containsAll(lhs);
	}
	
	private void listMath(Collection<SolverDescription> coll, ProblemRequirements sel, PrintWriter where) {
		where.println(ProblemRequirements.Explain.describe(sel));
		EnumSet<SolverDescription> es = EnumSet.noneOf(SolverDescription.class);
		es.addAll(coll);
		for (SolverDescription sd: es) {
			where.println("\t" + sd); 
		}
	}
	
	//@Test
	public void analyzeMaths( ) throws IOException {
		TestMathDescription tmd = new TestMathDescription();
		PrintWriter current = new PrintWriter(new FileWriter("orig.txt"));
		PrintWriter proposed = new PrintWriter(new FileWriter("prop.txt"));
		
		Collection<SolverDescription> orig = getSupportingSolverDescriptionse(tmd);
		Collection<SolverDescription> now = SolverDescription.getSupportingSolverDescriptions(tmd);
		listMath(orig,tmd,current);
		listMath(now,tmd,proposed);
		while (tmd.hasMoreStates()) {
			tmd.nextState();
			System.out.println(ProblemRequirements.Explain.describe(tmd));
			orig = getSupportingSolverDescriptionse(tmd);
			now = SolverDescription.getSupportingSolverDescriptions(tmd);
			listMath(orig,tmd,current);
			listMath(now,tmd,proposed);
		}
		current.close();
		proposed.close();
	}
	
	@Test
	public void diffSupportingMath( ) {
		TestMathDescription tmd = new TestMathDescription();
		Collection<SolverDescription> orig = getSupportingSolverDescriptionse(tmd);
		Collection<SolverDescription> now = SolverDescription.getSupportingSolverDescriptions(tmd);
		assertTrue(equals(orig,now));
		if (!equals(orig,now)) {
			System.err.println("mismatch " + ProblemRequirements.Explain.describe(tmd));
		}

		while (tmd.hasMoreStates()) {
			tmd.nextState();
			orig = getSupportingSolverDescriptionse(tmd);
			now = SolverDescription.getSupportingSolverDescriptions(tmd);
			//assertTrue(equals(orig,now));
			if (!equals(orig,now)) {
				System.err.println("mismatch " + ProblemRequirements.Explain.describe(tmd));
				now = SolverDescription.getSupportingSolverDescriptions(tmd);
			}
		}
	}
	
	//@Test
	public void recordDef( ) throws IOException  {
		PrintWriter file = new PrintWriter(new FileWriter("defSolv.txt"));
		dumpDefault(file);
		file.close();
	}
	//@Test
	public void testDef( ) {
		System.out.println("commencing generation"); 
		PrintWriter console = new PrintWriter(System.out,true);
		dumpDefault(console);
	}
	
	public void dumpDefault(PrintWriter out ) {
		TestMathDescription tmd = new TestMathDescription();
		out.print(tmd.getStateNum() + ":" );
		SolverDescription def = SolverDescription.getDefaultSolverDescription(tmd);
		out.println(def.getShortDisplayLabel());
		
		while (tmd.hasMoreStates()) {
			tmd.nextState();
			out.print(tmd.getStateNum() + ":" );
			def = SolverDescription.getDefaultSolverDescription(tmd);
			out.println(def.getShortDisplayLabel());
		}
			
	}	
	
	public static SolverDescription getDefaultSolverDescriptione(ProblemRequirements mathDescription) {
		if (mathDescription.isSpatial())	{
			if (mathDescription.isSpatialHybrid()) {
				return SolverDescription.FiniteVolumeStandalone;
			} else if (mathDescription.isSpatialStoch()) {
				return SolverDescription.Smoldyn;
			} else if (mathDescription.hasFastSystems()) {
				return SolverDescription.FiniteVolumeStandalone;
			} else if (mathDescription.hasDirichletAtMembrane()) {
				return SolverDescription.Chombo;
			} else {
				return SolverDescription.SundialsPDE;
			}
		} else {
			if (mathDescription.isNonSpatialStoch()) {
				return SolverDescription.StochGibson;
			}
			else {
				return SolverDescription.CombinedSundials;
			}
		}
	}

	@Test
	public void diffDef( ) {
		TestMathDescription tmd = new TestMathDescription();
		SolverDescription orig = getDefaultSolverDescriptione(tmd);
		SolverDescription now = SolverDescription.getDefaultSolverDescription(tmd);
		if (orig != now) {
			System.err.println("def mismatch " + tmd.getStateNum());
		}

		while (tmd.hasMoreStates()) {
			tmd.nextState();
			orig = getDefaultSolverDescriptione(tmd);
			now = SolverDescription.getDefaultSolverDescription(tmd);
			if (orig != now) {
				System.err.println("def mismatch " + tmd.getStateNum());
			}
		}
	}
	
	@Test
	public void testDbLookups( ) {
		assertTrue(SolverDescription.fromDatabaseName(null) == null);
		for (SolverDescription sd : SolverDescription.values( )) {
			assertTrue(SolverDescription.fromDatabaseName(sd.getDatabaseName()) == sd);
		}
		assertTrue(SolverDescription.fromDatabaseName(SolverDescription.ALTERNATE_CVODE_Description) == SolverDescription.CVODE);
	}
	
	@Test
	public void testNameLookups( ) {
		assertTrue(SolverDescription.fromDisplayLabel(null) == null);
		for (SolverDescription sd : SolverDescription.values( )) {
			assertTrue(SolverDescription.fromDisplayLabel(sd.getDisplayLabel()) == sd);
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBadDbName( ) {
		SolverDescription.fromDatabaseName("NoSolverHere");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBadDisplayName( ) {
		SolverDescription.fromDisplayLabel("NoSolverHere");
	}
}
