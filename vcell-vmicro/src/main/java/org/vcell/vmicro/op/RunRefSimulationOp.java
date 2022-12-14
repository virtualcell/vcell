package org.vcell.vmicro.op;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import cbit.vcell.solver.*;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.Origin;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VersionFlag;
import org.vcell.vmicro.workflow.data.ExternalDataInfo;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.LocalWorkspace;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.VariableType;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.FVSolverStandalone;

public class RunRefSimulationOp {
	
	public ImageTimeSeries<FloatImage> compute0(ROI cellROI_2D, ImageTimeSeries<FloatImage> normTimeSeries, double refSimDiffusionRate, LocalWorkspace localWorkspace, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		double[] timeStamps = normTimeSeries.getImageTimeStamps();
		TimeBounds timeBounds = new TimeBounds(0, 20*timeStamps[timeStamps.length-1]);
		FloatImage initRefConc = normTimeSeries.getAllImages()[0];
		double timeStepVal = timeStamps[1] - timeStamps[0];
		double baseDiffusionRate = 1.0; // arbitrary for now.
		ImageTimeSeries<FloatImage> solution = runRefSimulation(localWorkspace, cellROI_2D, timeStepVal, timeBounds, initRefConc, baseDiffusionRate, clientTaskStatusSupport);
		return solution;
	}
	
	private static ExternalDataInfo createNewExternalDataInfo(LocalWorkspace localWorkspace, String extDataIDName){
		return LocalWorkspace.createNewExternalDataInfo(new File(localWorkspace.getDefaultSimDataDirectory()), extDataIDName);
	}

	private static ImageTimeSeries<FloatImage> runRefSimulation(LocalWorkspace localWorkspace, ROI cellROI, double timeStepVal, TimeBounds timeBounds, FloatImage initRefConc, double baseDiffusionRate, ClientTaskStatusSupport progressListener) throws Exception
	{
		String INITCONC_EXTDATA_NAME = "initConc";
		String INITCONC_EXTDATA_VARNAME = "conc";
		String VAR_NAME = "species";
		User owner = LocalWorkspace.getDefaultOwner();
		KeyValue simKey = LocalWorkspace.createNewKeyValue();
		
		ExternalDataInfo initialConcentrationExtData = createNewExternalDataInfo(localWorkspace, INITCONC_EXTDATA_NAME);
		Extent extent = initRefConc.getExtent();
		Origin origin = initRefConc.getOrigin();
		
		ISize isize = new ISize(initRefConc.getNumX(),initRefConc.getNumY(),initRefConc.getNumZ());
		
		saveExternalData(initRefConc, INITCONC_EXTDATA_VARNAME, initialConcentrationExtData.getExternalDataIdentifier(), localWorkspace);

		FieldFunctionArguments initConditionFFA = new FieldFunctionArguments(INITCONC_EXTDATA_NAME, INITCONC_EXTDATA_VARNAME, new Expression(0.0), VariableType.VOLUME);
		BioModel bioModel = createRefSimBioModel(simKey,owner,origin,extent,cellROI,timeStepVal,timeBounds,VAR_NAME,new Expression(initConditionFFA.infix()),baseDiffusionRate);
		
		if(progressListener != null){
			progressListener.setMessage("Running Reference Simulation...");
		}
		
		//run simulation
		runFVSolverStandalone(
			new File(localWorkspace.getDefaultSimDataDirectory()),
			bioModel.getSimulation(0),
			initialConcentrationExtData.getExternalDataIdentifier(),
			progressListener, true);

		
		VCDataIdentifier vcDataIdentifier = new VCSimulationDataIdentifier(new VCSimulationIdentifier(simKey, owner),0);
		double[] dataTimes = localWorkspace.getDataSetControllerImpl().getDataSetTimes(vcDataIdentifier);
		FloatImage[] solutionImages = new FloatImage[dataTimes.length];
		for (int i=0;i<dataTimes.length;i++){
			SimDataBlock simDataBlock = localWorkspace.getDataSetControllerImpl().getSimDataBlock(null, vcDataIdentifier, VAR_NAME, dataTimes[i]);
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
			Simulation sim,
			ExternalDataIdentifier initialConditionExtDataID,
			ClientTaskStatusSupport progressListener,
			boolean bCheckSteadyState) throws Exception{

			FieldFunctionArguments[] fieldFunctionArgs = FieldUtilities.getFieldFunctionArguments(sim.getMathDescription());
			FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = new FieldDataIdentifierSpec[fieldFunctionArgs.length];
			for (int i = 0; i < fieldDataIdentifierSpecs.length; i++) {
				if (fieldFunctionArgs[i].getFieldName().equals(initialConditionExtDataID.getName())){
					fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],initialConditionExtDataID);
				}else{
					throw new RuntimeException("failed to resolve field named "+fieldFunctionArgs[i].getFieldName());
				}
			}
			
			int jobIndex = 0;
			SimulationTask simTask = new SimulationTask(new SimulationJob(sim,jobIndex, fieldDataIdentifierSpecs),0);
			SolverUtilities.prepareSolverExecutable(sim.getSolverTaskDescription().getSolverDescription());
			
			//if we need to check steady state, do the following two lines
			if(bCheckSteadyState)
			{
				simTask.getSimulation().getSolverTaskDescription().setStopAtSpatiallyUniformErrorTolerance(ErrorTolerance.getDefaultSpatiallyUniformErrorTolerance());
			}
			
			FVSolverStandalone fvSolver = new FVSolverStandalone(simTask,simulationDataDir,false);		
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

	private static void saveExternalData(Image image, String varName, ExternalDataIdentifier newROIExtDataID, LocalWorkspace localWorkspace) throws ObjectNotFoundException, ImageException, IOException 
	{
		LocalWorkspace.saveExternalData(image.getExtent(),image.getOrigin(),image.getISize(),image.getDoublePixels(),LocalWorkspace.getDefaultOwner(), varName, newROIExtDataID, localWorkspace.getDataSetControllerImpl());
	}

	private static BioModel createRefSimBioModel(KeyValue simKey, User owner, Origin origin, Extent extent, ROI cellROI_2D, double timeStepVal, TimeBounds timeBounds, String varName, Expression initialConcentration, double baseDiffusionRate) throws Exception {

		int numX = cellROI_2D.getRoiImages()[0].getNumX();
		int numY = cellROI_2D.getRoiImages()[0].getNumY();
		int numZ = cellROI_2D.getRoiImages().length;
		short[] shortPixels = cellROI_2D.getRoiImages()[0].getPixels();
		byte[] bytePixels = new byte[numX*numY*numZ];
		final byte EXTRACELLULAR_PIXVAL = 0;
		final byte CYTOSOL_PIXVAL = 1;
		for (int i = 0; i < bytePixels.length; i++) {
			if (shortPixels[i]!=0){
				bytePixels[i] = CYTOSOL_PIXVAL;
			}
		}
		VCImage maskImage;
		try {
			maskImage = new VCImageUncompressed(null,bytePixels,extent,numX,numY,numZ);
		} catch (ImageException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create mask image for geometry");
		}
		Geometry geometry = new Geometry("geometry",maskImage);
		geometry.getGeometrySpec().setOrigin(origin);
		if(geometry.getGeometrySpec().getNumSubVolumes() != 2){
			throw new Exception("Cell ROI has no ExtraCellular.");
		}
		String EXTRACELLULAR_NAME = "ec";
		String CYTOSOL_NAME = "cyt";
		String PLASMAMEMBRANE_NAME = "pm";
		
		ImageSubVolume subVolume0 = (ImageSubVolume)geometry.getGeometrySpec().getSubVolume(0);
		ImageSubVolume subVolume1 = (ImageSubVolume)geometry.getGeometrySpec().getSubVolume(1);
		if (subVolume0.getPixelValue() == EXTRACELLULAR_PIXVAL){
			subVolume0.setName(EXTRACELLULAR_NAME);
			subVolume1.setName(CYTOSOL_NAME);
		}else{
			subVolume0.setName(CYTOSOL_NAME);
			subVolume1.setName(EXTRACELLULAR_NAME);
		}
		geometry.getGeometrySurfaceDescription().updateAll();

		BioModel bioModel = new BioModel(null);
		bioModel.setName("unnamed");
		Model model = new Model("model");
		bioModel.setModel(model);
		Feature extracellular = model.addFeature(EXTRACELLULAR_NAME);
		Feature cytosol = model.addFeature(CYTOSOL_NAME);
		Membrane plasmaMembrane = model.addMembrane(PLASMAMEMBRANE_NAME);

		SimulationContext simContext = new SimulationContext(bioModel.getModel(),geometry);
		
		bioModel.addSimulationContext(simContext);
		FeatureMapping cytosolFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(cytosol);
		FeatureMapping extracellularFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(extracellular);
		MembraneMapping plasmaMembraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(plasmaMembrane);
		
		SubVolume cytSubVolume = geometry.getGeometrySpec().getSubVolume(CYTOSOL_NAME);
		SubVolume exSubVolume = geometry.getGeometrySpec().getSubVolume(EXTRACELLULAR_NAME);
		SurfaceClass pmSurfaceClass = geometry.getGeometrySurfaceDescription().getSurfaceClass(exSubVolume, cytSubVolume);
		
		cytosolFeatureMapping.setGeometryClass(cytSubVolume);
		extracellularFeatureMapping.setGeometryClass(exSubVolume);
		plasmaMembraneMapping.setGeometryClass(pmSurfaceClass);
		
		cytosolFeatureMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		extracellularFeatureMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		plasmaMembraneMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		
		
		//Mobile Species
		Species diffusingSpecies = model.addSpecies(new Species("species", "Mobile bleachable species"));
		SpeciesContext diffusingSpeciesContext = model.addSpeciesContext(diffusingSpecies,cytosol);
		diffusingSpeciesContext.setName(varName);

		SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(diffusingSpeciesContext);
		scs.getInitialConditionParameter().setExpression(initialConcentration);
		scs.getDiffusionParameter().setExpression(new Expression(baseDiffusionRate));
		
//		simContext.getMicroscopeMeasurement().addFluorescentSpecies(speciesContexts[0]);
//		simContext.getMicroscopeMeasurement().setConvolutionKernel(new MicroscopeMeasurement.ProjectionZKernel());
		simContext.setMathDescription(simContext.createNewMathMapping().getMathDescription());
				
		SimulationVersion simVersion = new SimulationVersion(simKey,"sim1",owner,new GroupAccessNone(),new KeyValue("0"),new BigDecimal(0),new Date(),VersionFlag.Current,"",null);
		Simulation newSimulation = new Simulation(simVersion,simContext.getMathDescription(), new SimulationOwner.StandaloneSimulationOwner());
		newSimulation.getSolverTaskDescription().setSolverDescription(SolverDescription.FiniteVolumeStandalone);
		simContext.addSimulation(newSimulation);
		newSimulation.getSolverTaskDescription().setTimeBounds(timeBounds);
		newSimulation.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(timeStepVal));
		newSimulation.getMeshSpecification().setSamplingSize(cellROI_2D.getISize());
		newSimulation.getSolverTaskDescription().setTimeStep(new TimeStep(timeStepVal, timeStepVal, timeStepVal));
		
		return bioModel;
	}

//	private static double REF_STARTINGTIME = 0;
//	private static double MAX_DIFF_RATE_FOR_TIMEBOUNDS = 50;
//	//Variable reference diffusion rate actually used in simulation in order to reach spatial uniform faster
//	//after the simulation, the results will convert to the results as if it was run by diffusion rate  1.
//	public static final double REFERENCE_DIFF_DELTAT = 0.05;
//	private static final double REFERENCE_DIFF_RATE_COEFFICIENT = 1;
//	
//	//estimated timebounds by knowing the image data time bounds 
//	//and the possible diffusion rate upper bound (e.g 50)
//	// g[0..n] = time points for diff =1, t[0..n) = time points for adaptive diff rate from ref sim, a=deltaT time interval in ref sim which is 0.05 by default
//	// g_n = g_0 + (a^2*n +a^2*n^2)/2, solve n then use n*deltaT to get ref sim time bounds
//	public static TimeBounds getEstimatedRefTimeBound(ImageTimeSeries<FloatImage> normalizedTimeSeries)
//	{
//		double[] timeStamps = normalizedTimeSeries.getImageTimeStamps();
//		double imgTimePeriod = timeStamps[timeStamps.length -1] - timeStamps[0];
//		double timePeriod_D1 = MAX_DIFF_RATE_FOR_TIMEBOUNDS * imgTimePeriod;
//		double refEndTime = REFERENCE_DIFF_DELTAT * Math.sqrt((timePeriod_D1 * 2)/(REFERENCE_DIFF_DELTAT * REFERENCE_DIFF_DELTAT));
//		return new TimeBounds(REF_STARTINGTIME, refEndTime);
//	}
//
}
