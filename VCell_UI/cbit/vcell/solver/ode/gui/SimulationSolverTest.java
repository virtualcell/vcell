/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cbit.vcell.solver.ode.gui;

import cbit.vcell.server.SimulationStatus;

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
		
		cbit.vcell.client.server.ClientServerManager sessionManager = cbit.vcell.client.server.ClientTester.mainInit(clientTesterArgs,"SimulationSolverTest");
		cbit.vcell.biomodel.BioModelInfo bmInfos[] = sessionManager.getDocumentManager().getBioModelInfos();

		//
		// select BioModel by name
		//
		cbit.vcell.biomodel.BioModelInfo selectedBMInfo = null;
		for (int i = 0; i < bmInfos.length; i++){
			cbit.vcell.biomodel.BioModelInfo bmInfo = bmInfos[i];
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
		cbit.vcell.simulation.Simulation selectedSimulation = null;
		cbit.vcell.simulation.Simulation simulations[] = bioModel.getSimulations();
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
		cbit.vcell.simulation.TimeBounds oldTimeBounds = selectedSimulation.getSolverTaskDescription().getTimeBounds();
		selectedSimulation.getSolverTaskDescription().setTimeBounds(new cbit.vcell.simulation.TimeBounds(oldTimeBounds.getStartingTime(),oldTimeBounds.getEndingTime()+1.0));
		
		//
		// save simulation
		//
		cbit.vcell.simulation.Simulation savedSimulation = sessionManager.getDocumentManager().save(selectedSimulation,false);

		//
		// run simulation
		//
		cbit.vcell.simulation.VCSimulationIdentifier vcSimulationIdentifier = savedSimulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
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