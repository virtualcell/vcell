package cbit.vcell.modeldb;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.vcell.util.document.User;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.solver.Simulation;

public class HybridBioModelVisitor extends VisitorAdapter implements VCMultiBioVisitor { 
	/**
	 * user key to use for tests
	 */
	final static String USER_KEY = "gerardw" ;
	//private final static String USER_KEY = VCDatabaseScanner.ALL_USERS;
	/**
	 * output file name
	 */
	final static String OUTPUT = "regen.txt";
	
	private List<SimulationContext> applications = new ArrayList<SimulationContext>();
	private int currentAppIndex;
	private SpeciesContextSpec[] currentSpeciesContext;
	private boolean[] toggledForceContinuous;
	private boolean moreStates;
	private BioModel currentModel;
	
	
	@Override
	protected int minimumModelVersion() {
		return 5;
	}

	@Override
	protected void scan(VCDatabaseScanner scanner, User[] users) throws Exception {
		PrintWriter pw = new PrintWriter(OUTPUT);
		scanner.multiScanBioModels(this, pw, users, false);		// TODO Auto-generated method stub
	}

	/**
	 * return true if any solver is spatial stochastic
	 */
	@Override
	public boolean filterBioModel(BioModel bioModel) {
		Simulation[] sims = bioModel.getSimulations();
		return isSpatialStoch(sims);
	}
	
	/**
	 * return true if any simulation is spatial stochastic
	 * @param sims not null
	 * @return true if any are
	 */
	private static boolean isSpatialStoch(Simulation[] sims) {
		for (Simulation s: sims) {
			if (s.getSolverTaskDescription().getSolverDescription().isSpatialStochasticSolver()) {
				return true;
			}
		}
		return false;
	}
	

	public void setBioModel(BioModel bioModel, PrintWriter pw) {
		currentModel = bioModel;
		applications.clear( );
		SimulationContext[] scs = currentModel.getSimulationContexts();
		
		for (SimulationContext sc : scs) {
			if (isSpatialStoch(sc.getSimulations())) {
				applications.add(sc);
			}
		}
		assert !applications.isEmpty();
		moreStates = true;
		currentAppIndex = -1;
		incrementApp();
	}
	
	/**
	 * increments {@link #currentAppIndex} and set ups {@link #currentSpeciesContext}
	 * detects when no more states
	 */
	private void incrementApp( ) {
		if (++currentAppIndex < applications.size()) {
			SimulationContext app = applications.get(currentAppIndex);
			currentSpeciesContext = app.getReactionContext().getSpeciesContextSpecs();
			toggledForceContinuous = new boolean[currentSpeciesContext.length];
			return;
		}
		moreStates = false;
	}
	
	/**
	 * moves to next state, if available. If not, calls incrementApp
	 */
	private void nextState( ) {
		for (int i = 0; i < toggledForceContinuous.length;i++){ 
			if (!toggledForceContinuous[i]) {
				toggle(currentSpeciesContext[i]);
				toggledForceContinuous[i] = true;
				for (int r = 0; r < i; r++) {
					toggledForceContinuous[r] = false;
				}
				return;
			}
		}
		incrementApp( );
		//recursively call self if more states avaiable
		if (moreStates) {
			nextState( );
		}
	}
	

	/**
	 * set force continues to opposite state
	 * @param speciesContextSpec
	 */
	private void toggle(SpeciesContextSpec speciesContextSpec) {
		speciesContextSpec.setForceContinuous( ! speciesContextSpec.isForceContinuous());
	}

	@Override
	public Iterator<BioModel> iterator() {
		return new Changer(); 
	}
	
	@Test
	public void tryit() throws IOException {
		super.setupScan(USER_KEY);
	}
	/**
	 * implement an iterator which cycles through all states <i>except</i> the first one.
	 * states are defined by a setting of SpeciesContextSpec force continuous
	 */
	private class Changer implements Iterator<BioModel> {

		/**
		 * its simpler to find there are no more states by attempting to go to next one
		 * @return true more states after attempting increment
		 */
		@Override
		public boolean hasNext() {
			nextState( );
			return moreStates; 
		}

		@Override
		public BioModel next() {
			return currentModel; 
		}

		/**
		 * @throws UnsupportedOperationException
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}