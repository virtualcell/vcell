package org.vcell.vmicro.op;

import java.io.File;
import java.util.ArrayList;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.SessionLog;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.LocalWorkspace;

import cbit.image.SourceDataInfo;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.StdoutSessionLog;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.DataIndexHelper;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.TimePointHelper;
import cbit.vcell.simdata.DataOperationResults.DataProcessingOutputDataValues;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.FVSolverStandalone;
import edu.northwestern.at.utils.math.randomnumbers.RandomVariable;

public class RunFakeSimOp {
	

	public ImageTimeSeries<UShortImage> runRefSimulation(LocalWorkspace localWorkspace, Simulation simulation, double max_intensity, double bleachBlackoutStartTime, double bleachBlackoutStopTime, boolean hasNoise, ClientTaskStatusSupport progressListener) throws Exception
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
		VCDataIdentifier vcDataIdentifier = new VCSimulationDataIdentifier(new VCSimulationIdentifier(simulation.getKey(), simulation.getVersion().getOwner()),0);
		CartesianMesh mesh = localWorkspace.getDataSetControllerImpl().getMesh(vcDataIdentifier);
		ISize isize = new ISize(mesh.getSizeX(),mesh.getSizeY(),mesh.getSizeZ());
		
		double[] dataTimes = localWorkspace.getDataSetControllerImpl().getDataSetTimes(vcDataIdentifier);

		DataProcessingOutputDataValues dataProcessingOutputDataValues = (DataProcessingOutputDataValues)localWorkspace.getDataSetControllerImpl().doDataOperation(new DataProcessingOutputDataValuesOP(
				vcDataIdentifier, 
				SimulationContext.FLUOR_DATA_NAME,
				TimePointHelper.createAllTimeTimePointHelper(),
				DataIndexHelper.createSliceDataIndexHelper(0),
				null,
				null));
		
		ArrayList<SourceDataInfo> sourceDataInfoArr = dataProcessingOutputDataValues.createSourceDataInfos(new ISize(mesh.getSizeX(),mesh.getSizeY(),1), origin, extent);

		// find scale factor to scale up the data to avoid losing precision when casting double to short
		double maxDataValue = 0;
		for (int i = 0; i < dataTimes.length; i++) {
			if(sourceDataInfoArr.get(i).getMinMax() != null){
				maxDataValue = Math.max(maxDataValue, sourceDataInfoArr.get(i).getMinMax().getMax());
			}else{
				double[] doubleData = (double[])sourceDataInfoArr.get(i).getData();
				for(int j=0; j<doubleData.length; j++){
					maxDataValue = Math.max(maxDataValue, doubleData[j]);
				}
			}
		}
		double scale = max_intensity/maxDataValue;
		
		ArrayList<UShortImage> outputImages = new ArrayList<UShortImage>();
		ArrayList<Double> outputTimes = new ArrayList<Double>();
		for (int i=0;i<dataTimes.length;i++){
			if (dataTimes[i] < bleachBlackoutStartTime || dataTimes[i] > bleachBlackoutStopTime){
				//saving each time step 2D double array to a UShortImage
				double[] doubleData = (double[])sourceDataInfoArr.get(i).getData();
				short[] shortData = new short[isize.getX()*isize.getY()];
				for(int j=0; j<shortData.length; j++)
				{
					double dData = doubleData[j]*scale;
					if (dData<0 || dData>65535.0){
						throw new RuntimeException("scaled pixel out of range of unsigned 16 bit integer, original simulated value = "+doubleData[j]+", scale = "+scale+", scaled value = "+dData);
					}
					short sData = (short)(0x0000ffff & ((int)dData));
					if (hasNoise && dData>0.0){
						if (dData>20){
							shortData[j] = (short)(0x0000ffff & (int)RandomVariable.normal(dData, Math.sqrt(dData)));
						}else{
							shortData[j] = (short)(0x0000ffff & RandomVariable.poisson(dData));
						}
					}else{
						shortData[j] = sData;
					}
				}
				outputTimes.add(dataTimes[i]);
				outputImages.add(new UShortImage(
							shortData,
							sourceDataInfoArr.get(i).getOrigin(),
							sourceDataInfoArr.get(i).getExtent(),
							sourceDataInfoArr.get(i).getXSize(),sourceDataInfoArr.get(i).getYSize(),1));
				
				if(progressListener != null){
					int progress = (int)(((i+1)*1.0/dataTimes.length)*100);
					progressListener.setProgress(progress);
				}
			}
		}
		
		double[] outputTimesArray = new double[outputTimes.size()];
		for (int i=0;i<outputTimes.size();i++){
			outputTimesArray[i] = outputTimes.get(i);
		}
		ImageTimeSeries<UShortImage> fakeFluorTimeSeries = new ImageTimeSeries<UShortImage>(UShortImage.class, outputImages.toArray(new UShortImage[0]), outputTimesArray, 1);

		return fakeFluorTimeSeries;
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
