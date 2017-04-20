package org.vcell.vmicro.op;

import java.io.File;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.LocalWorkspace;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.FVSolverStandalone;

public class RunSimulation2DOp {
	
	public ImageTimeSeries<FloatImage> runRefSimulation(LocalWorkspace localWorkspace, Simulation simulation, String varName, ClientTaskStatusSupport progressListener) throws Exception
	{
		User owner = LocalWorkspace.getDefaultOwner();
		KeyValue simKey = LocalWorkspace.createNewKeyValue();
		
		runFVSolverStandalone(
			new File(localWorkspace.getDefaultSimDataDirectory()),
			new StdoutSessionLog(LocalWorkspace.getDefaultOwner().getName()),
			simulation,
			progressListener);

		Extent extent = simulation.getMathDescription().getGeometry().getExtent();
		Origin origin = simulation.getMathDescription().getGeometry().getOrigin();
		VCDataIdentifier vcDataIdentifier = new VCSimulationDataIdentifier(new VCSimulationIdentifier(simKey, owner),0);
		CartesianMesh mesh = localWorkspace.getDataSetControllerImpl().getMesh(vcDataIdentifier);
		ISize isize = new ISize(mesh.getSizeX(),mesh.getSizeY(),mesh.getSizeZ());
		
		double[] dataTimes = localWorkspace.getDataSetControllerImpl().getDataSetTimes(vcDataIdentifier);
		FloatImage[] solutionImages = new FloatImage[dataTimes.length];
		for (int i=0;i<dataTimes.length;i++){
			SimDataBlock simDataBlock = localWorkspace.getDataSetControllerImpl().getSimDataBlock(null, vcDataIdentifier, varName, dataTimes[i]);
			double[] doubleData = simDataBlock.getData();
			float[] floatPixels = new float[doubleData.length];
			for (int j=0;j<doubleData.length;j++){
				floatPixels[j] = (float)doubleData[j];
			}
			solutionImages[i] = new FloatImage(floatPixels,origin,extent,isize.getX(),isize.getY(),isize.getZ());
		}
		
		ImageTimeSeries<FloatImage> solution = new ImageTimeSeries<FloatImage>(FloatImage.class, solutionImages, dataTimes, 1);
		
		return solution;
	}
	
	private static void runFVSolverStandalone(
			File simulationDataDir,
			SessionLog sessionLog,
			Simulation sim,
			ClientTaskStatusSupport progressListener) throws Exception{

			int jobIndex = 0;
			SimulationTask simTask = new SimulationTask(new SimulationJob(sim,jobIndex, null),0);
			SolverUtilities.prepareSolverExecutable(sim.getSolverTaskDescription().getSolverDescription());
			
			FVSolverStandalone fvSolver = new FVSolverStandalone(simTask,simulationDataDir,sessionLog,false);		
			fvSolver.startSolver(); 
//			fvSolver.runSolver();
			
			SolverStatus status = fvSolver.getSolverStatus();
			while (status.getStatus() != SolverStatus.SOLVER_FINISHED && status.getStatus() != SolverStatus.SOLVER_ABORTED  && status.getStatus() != SolverStatus.SOLVER_STOPPED )
			{
				if(progressListener != null)
				{
					progressListener.setProgress((int)(fvSolver.getProgress()*100));
					if (progressListener.isInterrupted())
					{
						fvSolver.stopSolver();
						throw UserCancelException.CANCEL_GENERIC;
					}
				}
				try{
					Thread.sleep(1000);
				}catch(InterruptedException ex)
				{
					ex.printStackTrace(System.out);
					//catch interrupted exception and ignore it, otherwise it will popup a dialog in user interface saying"sleep interrupted"
				}
				status = fvSolver.getSolverStatus();
			}

			if(status.getStatus() != SolverStatus.SOLVER_FINISHED){
				throw new Exception("Sover did not finish normally." + status);
			}
		}

}
