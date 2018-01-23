package org.vcell.api.client.examples;

import java.io.IOException;

import org.vcell.api.client.VCellApiClient;
import org.vcell.api.client.query.BioModelsQuerySpec;
import org.vcell.api.client.query.SimTasksQuerySpec;
import org.vcell.api.common.ApplicationRepresentation;
import org.vcell.api.common.BiomodelRepresentation;
import org.vcell.api.common.SimulationRepresentation;
import org.vcell.api.common.SimulationTaskRepresentation;

public class VCellApiClientTest {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		VCellApiClient vcellApiClient = null;
		try {
			if (args.length != 4 && args.length != 5){
				System.out.println("usage: VCellApiClient host port userid password [clientID]");
				System.exit(1);
			}
			String host = args[0];
			int port = Integer.parseInt(args[1]);
			String username = args[2];
			String password = args[3];
			
			boolean bIgnoreCertProblems = true;
			boolean bIgnoreHostMismatch = true;
			vcellApiClient = new VCellApiClient(host,port,bIgnoreCertProblems,bIgnoreHostMismatch);

			vcellApiClient.authenticate(username,password,false);
			
			// test /biomodel[? query string]
			BiomodelRepresentation[] biomodelReps = vcellApiClient.getBioModels(new BioModelsQuerySpec());
			for (BiomodelRepresentation biomodelRep : biomodelReps){
				System.out.println("biomodel : "+biomodelRep.getBmKey()+" : "+biomodelRep.getName());
				for (ApplicationRepresentation appRep : biomodelRep.getApplications()){
					System.out.println("   app : "+appRep.getName());
				}
				for (SimulationRepresentation simRep : biomodelRep.getSimulations()){
					System.out.println("   sim : "+simRep.getName());
				}
			}

			boolean bFirstSimulationToStartStop = true;
			if (biomodelReps.length>0){
				// test /biomodel/[bmkey]
				System.out.println(" ... re-fetching first biomodel owned by me ...");
				BioModelsQuerySpec bioModelsQuerySpec = new BioModelsQuerySpec();
				bioModelsQuerySpec.owner = username;
				BiomodelRepresentation firstBiomodelRep = vcellApiClient.getBioModels(bioModelsQuerySpec)[0];
				System.out.println("biomodel : "+firstBiomodelRep.getBmKey()+" : "+firstBiomodelRep.getName());
				for (ApplicationRepresentation appRep : firstBiomodelRep.getApplications()){
					System.out.println("   appRep : "+appRep.getName());
				}
				for (SimulationRepresentation simRep : firstBiomodelRep.getSimulations()){
					System.out.println("   simRep (returned with BioModelRep) : "+simRep.getKey()+" : "+simRep.getName());

					// test /biomodel/[bmkey]/simulation/simkey
					SimulationRepresentation simulation = vcellApiClient.getSimulation(firstBiomodelRep.getBmKey(), simRep.getKey());
					System.out.println("   simRep (retrieved separately) : "+simulation.getKey()+" : "+simulation.getName());
					
					if (bFirstSimulationToStartStop){
						bFirstSimulationToStartStop = false;
						// test /biomodel/[bmkey]/simulation/[simkey]/startSimulation
						SimTasksQuerySpec simTasksQuerySpec = new SimTasksQuerySpec();
						simTasksQuerySpec.simId = simRep.getKey();
						SimulationTaskRepresentation[] beforeStartSimTasks = vcellApiClient.getSimTasks(simTasksQuerySpec);
						System.out.println("SENDING START SIMULATION");
						SimulationTaskRepresentation[] justAfterStartSimTasks = vcellApiClient.startSimulation(firstBiomodelRep.getBmKey(), simRep.getKey());
						System.out.println("SENT START SIMULATION");
						
						System.out.println("WAITING 5 seconds");
						try {
							Thread.sleep(5000);
						}catch (Exception e){}
						SimulationTaskRepresentation[] longAfterStartSimTasks = vcellApiClient.getSimTasks(simTasksQuerySpec);
						
						
						System.out.println("SENDING STOP SIMULATION");
						SimulationTaskRepresentation[] justAfterStopSimTasks = vcellApiClient.stopSimulation(firstBiomodelRep.getBmKey(), simRep.getKey());
						System.out.println("SENT STOP SIMULATION");
						
						System.out.println("WAITING 5 seconds");
						try {
							Thread.sleep(5000);
						}catch (Exception e){}
						SimulationTaskRepresentation[] longAfterStopSimTasks = vcellApiClient.getSimTasks(simTasksQuerySpec);
						
						System.out.println("\n\nsimulation status:");
						for (SimulationTaskRepresentation simTaskRep : beforeStartSimTasks){
							System.out.println("    BEFORE START Job = "+simTaskRep.getJobIndex()+", Task = "+simTaskRep.getTaskId()+", Status = "+simTaskRep.getStatus());
						}
						for (SimulationTaskRepresentation simTaskRep : justAfterStartSimTasks){
							System.out.println("    JUST AFTER START Job = "+simTaskRep.getJobIndex()+", Task = "+simTaskRep.getTaskId()+", Status = "+simTaskRep.getStatus());
						}
						for (SimulationTaskRepresentation simTaskRep : longAfterStartSimTasks){
							System.out.println("    LONG AFTER START Job = "+simTaskRep.getJobIndex()+", Task = "+simTaskRep.getTaskId()+", Status = "+simTaskRep.getStatus());
						}
						for (SimulationTaskRepresentation simTaskRep : justAfterStopSimTasks){
							System.out.println("    JUST AFTER STOP Job = "+simTaskRep.getJobIndex()+", Task = "+simTaskRep.getTaskId()+", Status = "+simTaskRep.getStatus());
						}
						for (SimulationTaskRepresentation simTaskRep : longAfterStopSimTasks){
							System.out.println("    LONG AFTER STOP Job = "+simTaskRep.getJobIndex()+", Task = "+simTaskRep.getTaskId()+", Status = "+simTaskRep.getStatus());
						}
						System.out.println("\n\n");
					}					
					System.out.println("\n");
				}
			}
			
			// test /simtask
			SimulationTaskRepresentation[] simTaskReps = vcellApiClient.getSimTasks(new SimTasksQuerySpec());
			for (SimulationTaskRepresentation simTaskRep : simTaskReps){
				System.out.println("simTask : "+simTaskRep.getSimKey()+" : "+simTaskRep.getSimName());
			}
			
		} catch (Throwable e){
			e.printStackTrace(System.out);
		} finally {
			if (vcellApiClient!=null){
				try {
					vcellApiClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

}
