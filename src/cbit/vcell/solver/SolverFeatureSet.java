package cbit.vcell.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import cbit.vcell.solver.SolverDescription.SolverFeature;

//public class SolverFeatureSet extends ArrayList<SolverFeature> {
public class SolverFeatureSet implements Collection<SolverFeature>{ 
	public interface Filter {		/**		 * @param desc SolverSelector to filter		 * @return true if desc has desired property		 */		boolean supports(SolverSelector desc);	}

	private Collection<SolverFeature> features;
	private final Filter filter;
	private final SolverDescription defaultSolver; 
	private final int solverPriority; 

	private static ArrayList<SolverFeatureSet> sets = new ArrayList<SolverFeatureSet>();


	private SolverFeatureSet(Filter filter, SolverDescription defaultExecutable, int priority) {
		features = new ArrayList<SolverDescription.SolverFeature>();
		this.filter = filter;
		this.defaultSolver = defaultExecutable;
		this.solverPriority = priority;
		sets.add(this);
	}

	/**
	 * one feature constructor
	 * @param sf0
	 * @param filter if null, matches ??
	 * @param defaultExecutable may be null
	 */
	public SolverFeatureSet(SolverFeature sf0,
			Filter filter, SolverDescription defaultExecutable,
			int solverPriority) {
		this(filter,defaultExecutable, solverPriority);
		features.add(sf0);
		//use unmodifiable collection so iterator returned by  Collection#iterator( ) is read only 
		features = Collections.unmodifiableCollection(features);
	}

	/**
	 * two feature constructor
	 * @param sf0
	 * @param filter non null
	 * @param defaultExecutable may be null
	 */
	public SolverFeatureSet(SolverFeature sf0,SolverFeature sf1,
			Filter filter, SolverDescription defaultExecutable,
			int solverPriority) {
		this(filter,defaultExecutable, solverPriority);
		features.add(sf0);
		features.add(sf1);
		//use unmodifiable collection so iterator returned by  Collection#iterator( ) is read only 
		features = Collections.unmodifiableCollection(features);
	}

	public SolverFeatureSet(SolverFeature sf0,SolverFeature sf1,SolverFeature sf2,
			Filter filter, SolverDescription defaultExecutable,
			int solverPriority) {
		this(filter,defaultExecutable, solverPriority);
		features.add(sf0);
		features.add(sf1);
		features.add(sf2);
		//use unmodifiable collection so iterator returned by  Collection#iterator( ) is read only 
		features = Collections.unmodifiableCollection(features);
	}

	/**
	 * @return all sets that have been created
	 */
	public static Collection<SolverFeatureSet> getSets( ) {
		return sets;
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
	public boolean supports(SolverSelector selector) {
		return filter.supports(selector);
	}

	/**
	 * default solver, if any
	 * @return default or null
	 */
	public SolverDescription getDefaultSolver() {
		return defaultSolver;
	}

	/*****
	 * following methods implement unmodifiable Collection as pass through to {@link #features}
	 */

	@Override
	public boolean add(SolverFeature arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends SolverFeature> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object arg0) {
		return  features.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return  features.containsAll(arg0);
	}

	@Override
	public boolean isEmpty() {
		return features.isEmpty(); 
	}

	@Override
	public Iterator<SolverFeature> iterator() {
		return features.iterator();
	}

	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return features.size( );
	}

	@Override
	public Object[] toArray() {
		return features.toArray( );
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return features.toArray(arg0);
	}
}