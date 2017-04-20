package cbit.vcell.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cbit.vcell.math.ProblemRequirements;
import cbit.vcell.solver.SolverDescription.SolverFeature;
import cbit.vcell.solver.SolverDescription.SupportedProblemRequirements;

//public class SolverFeatureSet extends ArrayList<SolverFeature> {
public class SolverFeatureSet { 

	private ArrayList<SolverFeature> solverFeatures = new ArrayList<SolverFeature>();
	private final SupportedProblemRequirements supportedProblemRequirements;
	private final SolverDescription defaultSolver; 
	private final int solverPriority; 

	private static ArrayList<SolverFeatureSet> sets = new ArrayList<SolverFeatureSet>();
	/**
	 * @return all sets that have been created
	 */
	public static Collection<SolverFeatureSet> getSets( ) {
		return sets;
	}

	private SolverFeatureSet(SupportedProblemRequirements supportedProblemRequirements, SolverDescription defaultExecutable, int priority) {
		this.supportedProblemRequirements = supportedProblemRequirements;
		this.defaultSolver = defaultExecutable;
		this.solverPriority = priority;
		sets.add(this);
	}

	/**
	 * @param defaultExecutable may be null
	 */
	public SolverFeatureSet(SolverFeature[] solverFeatures,
			SupportedProblemRequirements supportedProblemRequirements, SolverDescription defaultExecutable,
			int solverPriority) {
		this(supportedProblemRequirements,defaultExecutable, solverPriority);
		this.solverFeatures.addAll(Arrays.asList(solverFeatures));
	}


	public List<SolverFeature> getSolverFeatures(){
		return Collections.unmodifiableList(solverFeatures);
	}

	/**
	 * @param sfs
	 * @return null if sfs null or doesn't have a solver
	 */
	private static SolverFeatureSet exePriorityFilter(SolverFeatureSet sfs) {
		if (sfs == null || sfs.defaultSolver == null) {
			return null;
		}
		return sfs;
	}
	
	/**
	 * return SolverFeatureSet with higher priority
	 * @param lhs may be null
	 * @param rhs may be null
	 * @return SolverFeatureSet with higher solver priority or null 
	 */
	public static SolverFeatureSet getHigherSolverPriority(SolverFeatureSet lhs, SolverFeatureSet rhs) {
		final SolverFeatureSet lhsFiltered = exePriorityFilter(lhs);
		final SolverFeatureSet rhsFiltered = exePriorityFilter(rhs);
		if (lhsFiltered == null) {
			return rhsFiltered;
		}
		if (rhsFiltered == null) {
			return lhsFiltered;
		}
		if (lhsFiltered.solverPriority > rhsFiltered.solverPriority) {
			return lhsFiltered;
		}
		if (rhsFiltered.solverPriority > lhsFiltered.solverPriority) {
			return rhsFiltered;
		}
		throw new IllegalStateException("SolverFeatureSets " + lhs + " and " + rhs + " have equal solver priorties");
	}

	/**
	 * @param selector
	 * @return true if this feature set supports given selector
	 */
	public boolean supports(ProblemRequirements selector) {
		return supportedProblemRequirements.supports(selector);
	}

	/**
	 * default solver, if any
	 * @return default or null
	 */
	public SolverDescription getDefaultSolver() {
		return defaultSolver;
	}

}