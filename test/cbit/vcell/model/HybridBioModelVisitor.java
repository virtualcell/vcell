package cbit.vcell.model;

import java.awt.GraphicsEnvironment;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import cbit.vcell.solver.SolverDescription;

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
	
	/**
	 * maximum number of species to toggle for evaluating force continuous toggling
	 */
	private static final int MAX_NUMBER_SPECIES_TO_TOGGLE = 10;

	
	private BioModel currentModel = null;
	private List<SimulationContext> applications = new ArrayList<SimulationContext>();
	private int currentAppIndex;
	private SpeciesContextSpec[] currentSpeciesContext;
	private boolean[] toggledForceContinuous;
	private int speciesIndexes[]  = new int [MAX_NUMBER_SPECIES_TO_TOGGLE];
	private boolean moreStates;
	private PrintWriter statusWriter;
	
	
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
	 * return true if any solver is 3D spatial stochastic
	 */
	@Override
	public boolean filterBioModel(BioModel bioModel) {
		SimulationContext[] ctxs = bioModel.getSimulationContexts();
		for (SimulationContext sc : ctxs) {
			if (is3D(sc)) {
				Simulation[] sims = sc.getSimulations();
				if (isSpatialStoch(sims)) {
					return true;
				}
			}
		}
		return false; 
	}
	
	/**
	 * return true if any simulation is spatial stochastic
	 * @param sims not null
	 * @return true if any are
	 */
	private static boolean isSpatialStoch(Simulation[] sims) {
		for (Simulation s: sims) {
			SolverDescription sd = s.getSolverTaskDescription().getSolverDescription();
			if (sd.isSpatialStochasticSolver()) {
				
				return true;
			}
		}
		return false;
	}
	
	/**
	 * see if is 3D Applcation
	 * @param sc
	 * @return true if is
	 */
	private static boolean is3D(SimulationContext sc) {
		return sc.getGeometry().getDimension() == 3;
	}
	

	public void setBioModel(BioModel bioModel, PrintWriter pw) {
		currentModel = bioModel;
		applications.clear( );
		statusWriter = pw;
		SimulationContext[] scs = currentModel.getSimulationContexts();
		
		for (SimulationContext sc : scs) {
			if (is3D(sc) && isSpatialStoch(sc.getSimulations())) {
				applications.add(sc);
			}
		}
		moreStates = !applications.isEmpty();
		if (!moreStates) {
			throw new IllegalArgumentException(bioModel.getName() + " "  + bioModel.getModel().getKey() + " no spatial stochastic?");
		}
		currentAppIndex = -1;
		incrementApp();
	}
	
	/**
	 * randomly assign a portion of a sequence into an array. Sequence is [0 ... (available - 1)]
	 * @param target where to store results must not be null
	 * @param available conceptual space from which to assign values
	 */
	private static void randomlyAssign(int[] target, int available) {
		ArrayList<Integer> avail = new ArrayList<Integer>(available);
		for (int i = 0; i < available; i++) {
			avail.add(i);
		}
		Collections.shuffle(avail);
		for (int i = 0; i < target.length; i++) {
			target[i] = avail.get(i);
		}
	}
	
	/**
	 * increments {@link #currentAppIndex} and set ups {@link #currentSpeciesContext}
	 * detects when no more states
	 */
	private void incrementApp( ) {
		if (++currentAppIndex < applications.size()) {
			SimulationContext app = applications.get(currentAppIndex);
			currentSpeciesContext = app.getReactionContext().getSpeciesContextSpecs();
			if (currentSpeciesContext.length < MAX_NUMBER_SPECIES_TO_TOGGLE) {
				toggledForceContinuous = new boolean[currentSpeciesContext.length];
				for (int i  = 0;  i < MAX_NUMBER_SPECIES_TO_TOGGLE; ++i) {
					speciesIndexes[i] = i;
				}

			}
			else {
				toggledForceContinuous = new boolean[MAX_NUMBER_SPECIES_TO_TOGGLE];
				randomlyAssign(speciesIndexes,currentSpeciesContext.length);
			}
			statusWriter.println("incremented app, setting " + toggledForceContinuous.length + " force continuous toggles");
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
				toggle(currentSpeciesContext[speciesIndexes[i]]);
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
	public void populateDatabaseTable( ) throws IOException {
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
			//scanner.keyScanBioModels(visitor, w, users, bAbortOnDataAccessException, STATUS_TABLE);
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
		try{
		PropertyLoader.loadProperties();
		//String args[] = {USER_KEY,OUTPUT};
		HybridBioModelVisitor visitor = new HybridBioModelVisitor();
		boolean bAbortOnDataAccessException = false;
			BatchTester scanner = new BatchTester(new NullSessionLog()); 
			scanner.batchScanBioModels(visitor, STATUS_TABLE, 5);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			System.err.flush();
		}
	}
	
	//@Test
	public void rAssignTest( ) {
		int sample[] = new int[5];
		for (int i = 0; i < 20 ;i++) {
			randomlyAssign(sample, 10);
			System.out.println(Arrays.toString(sample));
		}
	}

	public static void main(String[] args) throws Exception {
		JUnitCore.main(HybridBioModelVisitor.class.getName());
	}

}