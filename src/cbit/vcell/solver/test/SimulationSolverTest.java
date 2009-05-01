package cbit.vcell.solver.test;
import cbit.vcell.solver.ode.gui.SimulationStatus;
/**
 * Insert the type's description here.
 * Creation date: (9/23/2004 2:22:47 PM)
 * @author: Jim Schaff
 */
public class SimulationSolverTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		if (args.length!=5){
			System.out.println("usage: SimulationSolverTest -local username password biomodelname simulationname");
			System.exit(1);
		}
		String location = args[0];
		String username = args[1];
		String password = args[2];
		String bioModelName = args[3];
		String simulationName = args[4];
		String clientTesterArgs[] = new String[] { location, username, password };
		
		cbit.vcell.client.server.ClientServerManager sessionManager = cbit.vcell.client.test.ClientTester.mainInit(clientTesterArgs,"SimulationSolverTest");
		org.vcell.util.document.BioModelInfo bmInfos[] = sessionManager.getDocumentManager().getBioModelInfos();

		//
		// select BioModel by name
		//
		org.vcell.util.document.BioModelInfo selectedBMInfo = null;
		for (int i = 0; i < bmInfos.length; i++){
			org.vcell.util.document.BioModelInfo bmInfo = bmInfos[i];
			if (bmInfo.getVersion().getName().equals(bioModelName)){
				selectedBMInfo = bmInfo;
				break;
			}
		}
		if (selectedBMInfo==null){
			throw new RuntimeException("cannot find BioModel '"+bioModelName+"' accessible by user '"+username+"'");
		}
		//
		//
		//
		cbit.vcell.biomodel.BioModel bioModel = sessionManager.getDocumentManager().getBioModel(selectedBMInfo);
		cbit.vcell.solver.Simulation selectedSimulation = null;
		cbit.vcell.solver.Simulation simulations[] = bioModel.getSimulations();
		for (int i = 0; i < simulations.length; i++){
			if (simulations[i].getName().equals(simulationName)){
				selectedSimulation = simulations[i];
				break;
			}
		}
		if (selectedSimulation==null){
			throw new RuntimeException("cannot find Simulation '"+simulationName+"' in BioModel '"+bioModelName+"'");
		}

		//
		// make a change
		//
		cbit.vcell.solver.TimeBounds oldTimeBounds = selectedSimulation.getSolverTaskDescription().getTimeBounds();
		selectedSimulation.getSolverTaskDescription().setTimeBounds(new cbit.vcell.solver.TimeBounds(oldTimeBounds.getStartingTime(),oldTimeBounds.getEndingTime()+1.0));
		
		//
		// save simulation
		//
		cbit.vcell.solver.Simulation savedSimulation = sessionManager.getDocumentManager().save(selectedSimulation,false);

		//
		// run simulation
		//
		cbit.vcell.solver.VCSimulationIdentifier vcSimulationIdentifier = savedSimulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
		sessionManager.getJobManager().startSimulation(vcSimulationIdentifier);
		while (true){
			SimulationStatus simStatus = sessionManager.getJobManager().getServerSimulationStatus(vcSimulationIdentifier);

			System.out.println((new java.util.Date()).toString()+" status = "+simStatus.toString());
			if (simStatus.numberOfJobsDone() == savedSimulation.getScanCount()){
				break;
			}
			try {
				Thread.currentThread().sleep(1000);
			}catch (Exception e){
			}
		}
		System.out.println("done");
		
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}

	System.exit(0);
}
}