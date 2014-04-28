package cbit.vcell.model;

import java.awt.GraphicsEnvironment;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.help.UnsupportedOperationException;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.vcell.util.NullSessionLog;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.modeldb.BatchTester;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.solver.Simulation;

public class HybridBioModelVisitor implements VCMultiBioVisitor { 
	/**
	 * user key to use for tests
	 */
	//private final static String USER_KEY = "gerardw" ;
	private final static String USER_KEY = VCDatabaseScanner.ALL_USERS;
	/**
	 * output file name
	 */
	private final static String OUTPUT = "regen.txt";
	
	/**
	 * name of status table to use
	 */
	private final static String STATUS_TABLE = "gerard.models_to_scan";

	
	private BioModel currentModel = null;
	private List<SimulationContext> applications = new ArrayList<SimulationContext>();
	private int currentAppIndex;
	private SpeciesContextSpec[] currentSpeciesContext;
	private boolean[] toggledForceContinuous;
	private boolean moreStates;
	
	
	/**
	 * return true if major version == 5
	 */
	public boolean filterBioModel(BioModelInfo bioModelInfo) {
		VCellSoftwareVersion sv = bioModelInfo.getSoftwareVersion();
		//only look at 5.0, 5.1 stored models
		final boolean recentEnough = ( sv.getMajorVersion() == 5 ); 
		//don't look at 5.3 models
		final boolean oldEnough = ( sv.getMinorVersion() < 3);
		return recentEnough && oldEnough;
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
		moreStates = !applications.isEmpty();
		if (!moreStates) {
			System.err.println(bioModel.getName() + " "  + bioModel.getModel().getKey() + " no spatial stochastic?");
		}
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
			if (!moreStates) {
				return false;
			}
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

	@SuppressWarnings("unused")
	//@Test
	public void tryit( ) throws IOException {
		//Logging.init();
		PropertyLoader.loadProperties();
		//String args[] = {USER_KEY,OUTPUT};
		HybridBioModelVisitor visitor = new HybridBioModelVisitor();
		boolean bAbortOnDataAccessException = false;
		try{
			BatchTester scanner = new BatchTester(new NullSessionLog()); 
			Writer w = new FileWriter(OUTPUT);
			User[] users = null; 
			if (USER_KEY != VCDatabaseScanner.ALL_USERS) { 
				User u = scanner.getUser(USER_KEY);
				if (u == null) {
					throw new IllegalArgumentException("Can't find user " + USER_KEY + " in database");
				}
				users =  new User[1];
				users[0] = u;
			}
			else {
				users = scanner.getAllUsers();
			}
			//scanner.multiScanBioModels(visitor, w, users, bAbortOnDataAccessException);
			scanner.keyScanBioModels(visitor, w, users, bAbortOnDataAccessException, STATUS_TABLE);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			System.err.flush();
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	public void batchLoad( ) throws IOException {
		//Logging.init();
		if (!GraphicsEnvironment.isHeadless()) {
			throw new Error ("set headless (java.awt.headless=true");
		}
		PropertyLoader.loadProperties();
		//String args[] = {USER_KEY,OUTPUT};
		HybridBioModelVisitor visitor = new HybridBioModelVisitor();
		boolean bAbortOnDataAccessException = false;
		try{
			BatchTester scanner = new BatchTester(new NullSessionLog()); 
			scanner.batchScanBioModels(visitor, "gerard.models_to_scan", 10);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			System.err.flush();
		}
	}

	public static void main(String[] args) throws Exception {
		JUnitCore.main(HybridBioModelVisitor.class.getName());
	}

}