package cbit.vcell.microscopy;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.math.BigDecimal;
import java.util.BitSet;
import java.util.Date;
import java.util.Vector;

import javax.media.jai.BorderExtender;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.AddConstDescriptor;
import javax.media.jai.operator.AndDescriptor;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.DilateDescriptor;
import javax.media.jai.operator.ErodeDescriptor;
import javax.media.jai.operator.ExtremaDescriptor;
import javax.media.jai.operator.LookupDescriptor;

import loci.formats.ImageTools;
import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.sql.KeyValue;
import cbit.sql.SimulationVersion;
import cbit.sql.VersionFlag;
import cbit.util.Extent;
import cbit.util.FileUtils;
import cbit.util.ISize;
import cbit.util.Issue;
import cbit.util.Matchable;
import cbit.util.NumberUtils;
import cbit.util.Origin;
import cbit.util.TokenMangler;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.MathDescription;
import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.model.Feature;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.server.GroupAccessNone;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.server.SessionLog;
import cbit.vcell.server.User;
import cbit.vcell.simdata.ExternalDataIdentifier;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.simdata.DataSetControllerImpl.ProgressListener;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.FVSolverStandalone;

public class FRAPStudy implements Matchable{
	private transient String xmlFilename = null;
	private transient String directory = null;
	private String name = null;
	private String description = null;
	private String originalImageFilePath = null;
	private FRAPData frapData = null;
	private BioModel bioModel = null;
	private FrapDataAnalysisResults frapDataAnalysisResults = null;
	private ExternalDataInfo frapDataExternalDataInfo = null;
	private ExternalDataInfo roiExternalDataInfo = null;
	
	PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	//
	// store the external data identifiers as annotation within a MathModel.
	//
	
	public ExternalDataInfo getFrapDataExternalDataInfo() {
		return frapDataExternalDataInfo;
	}
	
	public static void runFVSolverStandalone(
		File simulationDataDir,
		SessionLog sessionLog,
		Simulation sim,
		ExternalDataIdentifier imageDataExtDataID,
		ExternalDataIdentifier roiExtDataID,
		ProgressListener progressListener) throws Exception{

		FieldFunctionArguments[] fieldFunctionArgs = sim.getMathDescription().getFieldFunctionArguments();
		FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = new FieldDataIdentifierSpec[fieldFunctionArgs.length];
		for (int i = 0; i < fieldDataIdentifierSpecs.length; i++) {
			if (fieldFunctionArgs[i].getFieldName().equals(imageDataExtDataID.getName())){
				fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],imageDataExtDataID);
			}else if (fieldFunctionArgs[i].getFieldName().equals(roiExtDataID.getName())){
				fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],roiExtDataID);
			}else{
				throw new RuntimeException("failed to resolve field named "+fieldFunctionArgs[i].getFieldName());
			}
		}
		
		int jobIndex = 0;
		SimulationJob simJob = new SimulationJob(sim,fieldDataIdentifierSpecs,jobIndex);
		
		System.setProperty(PropertyLoader.jmsURL, "abc");
		System.setProperty(PropertyLoader.jmsUser, "abc");
		System.setProperty(PropertyLoader.jmsPassword, "abc");
		System.setProperty(PropertyLoader.jmsSimJobQueue, "abc");
		System.setProperty(PropertyLoader.jmsWorkerEventQueue, "abc");
		System.setProperty(PropertyLoader.jmsServiceControlTopic, "abc");
//		ResourceUtils.writeFVExecutable();
		FVSolverStandalone fvSolver = new FVSolverStandalone(simJob,simulationDataDir,sessionLog,false);
		fvSolver.startSolver();
		
		SolverStatus status = fvSolver.getSolverStatus();
		while (status.getStatus() != SolverStatus.SOLVER_FINISHED && status.getStatus() != SolverStatus.SOLVER_ABORTED )
		{
			if(progressListener != null){
				progressListener.updateProgress(fvSolver.getProgress());
			}
			Thread.sleep(100);
			status = fvSolver.getSolverStatus();
		}

		if(status.getStatus() == SolverStatus.SOLVER_FINISHED){
			String roiMeshFileName =
				ExternalDataIdentifier.createCanonicalMeshFileName(
					roiExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT, false);
			String imageDataMeshFileName =
				ExternalDataIdentifier.createCanonicalMeshFileName(
					imageDataExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT, false);
			String simulationMeshFileName =
				ExternalDataIdentifier.createCanonicalMeshFileName(
					sim.getVersion().getVersionKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT, false);
			// delete old external data mesh files and copy simulation mesh file to them
			File roiMeshFile = new File(simulationDataDir,roiMeshFileName);
			File imgMeshFile = new File(simulationDataDir,imageDataMeshFileName);
			File simMeshFile = new File(simulationDataDir,simulationMeshFileName);
			if(!roiMeshFile.delete()){throw new Exception("Couldn't delete ROI Mesh file "+roiMeshFile.getAbsolutePath());}
			if(!imgMeshFile.delete()){throw new Exception("Couldn't delete ImageData Mesh file "+imgMeshFile.getAbsolutePath());}
			FileUtils.copyFile(simMeshFile, roiMeshFile);
			FileUtils.copyFile(simMeshFile, imgMeshFile);
		}
		else{
			throw new Exception("Sover did not finish normally." + status.toString());
		}
	}

	public static ExternalDataInfo createNewExternalDataInfo(LocalWorkspace localWorkspace,String extDataIDName){
		File targetDir = new File(localWorkspace.getSimDataDirectory(),localWorkspace.getOwner().getName());
		ExternalDataIdentifier newImageDataExtDataID =
			new ExternalDataIdentifier(LocalWorkspace.createNewKeyValue(),
					localWorkspace.getOwner(),extDataIDName);
		ExternalDataInfo newImageDataExtDataInfo =
			new ExternalDataInfo(newImageDataExtDataID,
				new File(targetDir,newImageDataExtDataID.getID()+SimDataConstants.LOGFILE_EXTENSION).getAbsolutePath());
		return newImageDataExtDataInfo;
	}
	public void setFrapDataExternalDataInfo(ExternalDataInfo imageDatasetExternalDataInfo) {
		ExternalDataInfo oldValue = this.frapDataExternalDataInfo;
		this.frapDataExternalDataInfo = imageDatasetExternalDataInfo;
		propertyChangeSupport.firePropertyChange("imageDatasetExternalDataInfo", oldValue, imageDatasetExternalDataInfo);
	}

	public ExternalDataInfo getRoiExternalDataInfo() {
		return roiExternalDataInfo;
	}

	public void setRoiExternalDataInfo(ExternalDataInfo roiExternalDataInfo) {
		ExternalDataInfo oldValue = this.roiExternalDataInfo;
		this.roiExternalDataInfo = roiExternalDataInfo;
		propertyChangeSupport.firePropertyChange("roiExternalDataInfo", oldValue, roiExternalDataInfo);
	}
	
//	public void updateFrapDataAnalysisResults(int bleachType) throws ExpressionException {
//		//amended in March 2008, check if all reqired data for curve fitting are ready
//		if(getFrapData() == null || getFrapData().getImageDataset() == null || getFrapData().getImageDataset().getISize().getXYZ() <= 0)
//		{
//			throw new NullPointerException("Data have to be loaded before fitting recovery curve.");
//		}
//		if(getFrapData().getImageDataset().getImageTimeStamps().length < 1)
//		{
//			throw new NullPointerException("Data doesn't have any time stamp. It is required for fitting recovery curve.");
//		}
//		if(getFrapData().getRoi(RoiType.ROI_BLEACHED).isAllPixelsZero() || getFrapData().getRoi(RoiType.ROI_BACKGROUND).isAllPixelsZero())
//		{
//			throw new NullPointerException("ROI_bleached and ROI_background are reqired for fitting recovery curve. Please go to \'images\' tab to set these ROIS.");
//		}
//		if(bleachType == 0)
//		{
//			throw new NullPointerException("Bleach Type is not recognizable.");
//		}
//		FrapDataAnalysisResults fdar = FRAPDataAnalysis.fitRecovery2(getFrapData(), bleachType);
//		setFrapDataAnalysisResults(fdar);
//	}
	
	public BioModel getBioModel() {
		return bioModel;
	}
	
	public static BioModel createNewBioModel(
			FRAPStudy sourceFrapStudy,
			Double baseDiffusionRate,
			Double bleachWhileMonitoringRate,
			KeyValue simKey,
			User owner) throws Exception {

		if (sourceFrapStudy==null){
			throw new Exception("No FrapStudy is defined");
		}
		if (sourceFrapStudy.getFrapData()==null){
			throw new Exception("No FrapData is defined");
		}
		if (sourceFrapStudy.getFrapData().getImageDataset()==null){
			throw new Exception("No ImageDataSet is defined");
		}
		if(sourceFrapStudy.getFrapDataAnalysisResults() == null ||
				sourceFrapStudy.getFrapDataAnalysisResults().getStartingIndexForRecovery() == null){
			throw new Exception("No Starting Index for Recovery is defined");
		}
		ROI cellROI_2D = sourceFrapStudy.getFrapData().getRoi(RoiType.ROI_CELL);
		if (cellROI_2D==null){
			throw new Exception("No Cell ROI is defined");
		}
		if(baseDiffusionRate == null){
			throw new Exception("Diffusion Rate is not defined");
		}
//		if(simKey == null){
//			throw new Exception("Sim Key is not defined");
//		}
		if(owner == null){
			throw new Exception("Owner is not defined");
		}

		Extent extent = sourceFrapStudy.getFrapData().getImageDataset().getExtent();

		double[] timeStamps = sourceFrapStudy.getFrapData().getImageDataset().getImageTimeStamps();
		int startingIndexForRecovery = 
			sourceFrapStudy.getFrapDataAnalysisResults().getStartingIndexForRecovery().intValue();
		TimeBounds timeBounds = new TimeBounds(0.0,timeStamps[timeStamps.length-1]-timeStamps[startingIndexForRecovery]);
		double timeStepVal = timeStamps[timeStamps.length-1] - timeStamps[timeStamps.length-2];
		TimeStep timeStep = new TimeStep(timeStepVal, timeStepVal, timeStepVal);

//		Extent extent = getFrapData().getImageDataset().getImage(0,0,0).getExtent();
		int numX = cellROI_2D.getRoiImages()[0].getNumX();
		int numY = cellROI_2D.getRoiImages()[0].getNumY();
		int numZ = cellROI_2D.getRoiImages().length;
		short[] shortPixels = cellROI_2D.getRoiImages()[0].getPixels();
		byte[] bytePixels = new byte[numX*numY*numZ];
		final byte EXTRACELLULAR_PIXVAL = 0;
		final byte CYTOSOL_PIXVAL = 1;
		final String EXTRACELLULAR_NAME = "extracellular";
		final String CYTOSOL_NAME = "cytosol";
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
		int subVolume0PixVal = ((ImageSubVolume)geometry.getGeometrySpec().getSubVolume(0)).getPixelValue();
		geometry.getGeometrySpec().getSubVolume(0).setName((subVolume0PixVal == EXTRACELLULAR_PIXVAL?EXTRACELLULAR_NAME:CYTOSOL_NAME));
		int subVolume1PixVal = ((ImageSubVolume)geometry.getGeometrySpec().getSubVolume(1)).getPixelValue();
		geometry.getGeometrySpec().getSubVolume(1).setName((subVolume1PixVal == CYTOSOL_PIXVAL?CYTOSOL_NAME:EXTRACELLULAR_NAME));
		geometry.getGeometrySurfaceDescription().updateAll();

		BioModel bioModel = new BioModel(null);
		bioModel.setName("unnamed");
		Model model = new Model("model");
		bioModel.setModel(model);
		model.addFeature(EXTRACELLULAR_NAME, null, null);
		Feature extracellular = (Feature)model.getStructure(EXTRACELLULAR_NAME);
		model.addFeature(CYTOSOL_NAME, extracellular, "plasmaMembrane");
		Feature cytosol = (Feature)model.getStructure(CYTOSOL_NAME);
		double factor = Math.pow(10,.25);
		Expression[] diffusionConstants = new Expression[] {
				new Expression(NumberUtils.formatNumber(baseDiffusionRate*Math.pow(factor, -4),3)),
				new Expression(NumberUtils.formatNumber(baseDiffusionRate*Math.pow(factor, -3),3)),
				new Expression(NumberUtils.formatNumber(baseDiffusionRate*Math.pow(factor, -2),3)),
				new Expression(NumberUtils.formatNumber(baseDiffusionRate*Math.pow(factor, -1),3)),
				new Expression(NumberUtils.formatNumber(baseDiffusionRate*Math.pow(factor, 0),3)),
				new Expression(NumberUtils.formatNumber(baseDiffusionRate*Math.pow(factor, 1),3)),
				new Expression(NumberUtils.formatNumber(baseDiffusionRate*Math.pow(factor, 2),3)),
				new Expression(NumberUtils.formatNumber(baseDiffusionRate*Math.pow(factor, 3),3)),
				new Expression(NumberUtils.formatNumber(baseDiffusionRate*Math.pow(factor, 4),3)),
//					new Expression("(t>1000)"), // so that it creates a PDE equation
//					new Expression("(t>1000)"), // so that it creates a PDE equation
//					new Expression("(t>1000)"), // so that it creates a PDE equation
//					new Expression("(t>1000)"), // so that it creates a PDE equation
//					new Expression("(t>1000)"), // so that it creates a PDE equation
//					new Expression("(t>1000)"), // so that it creates a PDE equation
//					new Expression("(t>1000)"), // so that it creates a PDE equation
//					new Expression("(t>1000)"), // so that it creates a PDE equation
//					new Expression("(t>1000)"), // so that it creates a PDE equation
		};
		Species[] species = new Species[] {
				new Species("fluor_D_"+TokenMangler.fixTokenStrict(diffusionConstants[0].infix()), "fluorescent molecule1"),
				new Species("fluor_D_"+TokenMangler.fixTokenStrict(diffusionConstants[1].infix()), "fluorescent molecule2"),
				new Species("fluor_D_"+TokenMangler.fixTokenStrict(diffusionConstants[2].infix()), "fluorescent molecule3"),
				new Species("fluor_D_"+TokenMangler.fixTokenStrict(diffusionConstants[3].infix()), "fluorescent molecule4"),
				new Species("fluor_D_"+TokenMangler.fixTokenStrict(diffusionConstants[4].infix()), "fluorescent molecule5"),
				new Species("fluor_D_"+TokenMangler.fixTokenStrict(diffusionConstants[5].infix()), "fluorescent molecule6"),
				new Species("fluor_D_"+TokenMangler.fixTokenStrict(diffusionConstants[6].infix()), "fluorescent molecule7"),
				new Species("fluor_D_"+TokenMangler.fixTokenStrict(diffusionConstants[7].infix()), "fluorescent molecule8"),
				new Species("fluor_D_"+TokenMangler.fixTokenStrict(diffusionConstants[8].infix()), "fluorescent molecule9"),
//					new Species("prebleach_avg", "prebleach_avg"),
//					new Species("postbleach_first", "postbleach_first"),
//					new Species("postbleach_last", "postbleach_last"),
//					new Species("bleached_mask", "bleached_mask"),
//					new Species("ring1_mask", "ring1_mask"),
//					new Species("ring2_mask", "ring2_mask"),
//					new Species("ring3_mask", "ring3_mask"),
//					new Species("ring4_mask", "ring4_mask"),
//					new Species("ring5_mask", "ring5_mask"),
		};
		SpeciesContext[] speciesContexts = new SpeciesContext[] {
				new SpeciesContext(null,species[0].getCommonName(),species[0],cytosol,true),
				new SpeciesContext(null,species[1].getCommonName(),species[1],cytosol,true),
				new SpeciesContext(null,species[2].getCommonName(),species[2],cytosol,true),
				new SpeciesContext(null,species[3].getCommonName(),species[3],cytosol,true),
				new SpeciesContext(null,species[4].getCommonName(),species[4],cytosol,true),
				new SpeciesContext(null,species[5].getCommonName(),species[5],cytosol,true),
				new SpeciesContext(null,species[6].getCommonName(),species[6],cytosol,true),
				new SpeciesContext(null,species[7].getCommonName(),species[7],cytosol,true),
				new SpeciesContext(null,species[8].getCommonName(),species[8],cytosol,true),
//					new SpeciesContext(null,species[9].getCommonName(),species[9],cytosol,true),
//					new SpeciesContext(null,species[10].getCommonName(),species[10],cytosol,true),
//					new SpeciesContext(null,species[11].getCommonName(),species[11],cytosol,true),
//					new SpeciesContext(null,species[12].getCommonName(),species[12],cytosol,true),
//					new SpeciesContext(null,species[13].getCommonName(),species[13],cytosol,true),
//					new SpeciesContext(null,species[14].getCommonName(),species[14],cytosol,true),
//					new SpeciesContext(null,species[15].getCommonName(),species[15],cytosol,true),
//					new SpeciesContext(null,species[16].getCommonName(),species[16],cytosol,true),
//					new SpeciesContext(null,species[17].getCommonName(),species[17],cytosol,true)
		};
		String roiDataName = "roiData";
		Expression[] initialConditions = new Expression[] {
				new Expression("field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0)"),
				new Expression("field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0)"),
				new Expression("field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0)"),
				new Expression("field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0)"),
				new Expression("field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0)"),
				new Expression("field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0)"),
				new Expression("field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0)"),
				new Expression("field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0)"),
				new Expression("field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0)"),
//					new Expression("field("+roiDataName+","+species[9].getCommonName()+",0)"),
//					new Expression("field("+roiDataName+","+species[10].getCommonName()+",0)"),
//					new Expression("field("+roiDataName+","+species[11].getCommonName()+",0)"),
//					new Expression("field("+roiDataName+","+species[12].getCommonName()+",0)"),
//					new Expression("field("+roiDataName+","+species[13].getCommonName()+",0)"),
//					new Expression("field("+roiDataName+","+species[14].getCommonName()+",0)"),
//					new Expression("field("+roiDataName+","+species[15].getCommonName()+",0)"),
//					new Expression("field("+roiDataName+","+species[16].getCommonName()+",0)"),
//					new Expression("field("+roiDataName+","+species[17].getCommonName()+",0)"),
		};
		// for parameter scans, use cube root of 10 (3 per decade) = factor of 2.154434690030230132025595313452
		// add reactions to species if there is bleachWhileMonitoring rate.
		for (int i = 0; i < initialConditions.length; i++) {
			model.addSpecies(species[i]);
			model.addSpeciesContext(speciesContexts[i]);
			if (bleachWhileMonitoringRate != null){
				SimpleReaction simpleReaction = new SimpleReaction(cytosol,speciesContexts[i].getName()+"_bleach");
				simpleReaction.addReactant(speciesContexts[i], 1);
				MassActionKinetics massActionKinetics = new MassActionKinetics(simpleReaction);
				simpleReaction.setKinetics(massActionKinetics);
				KineticsParameter kforward = massActionKinetics.getForwardRateParameter();
				simpleReaction.getKinetics().setParameterValue(kforward, new Expression(bleachWhileMonitoringRate));
				model.addReactionStep(simpleReaction);
				//we save bleachWhileMonitoringRate during generating the bio model, this was saved to nowhere previously.
			}
		}

		SimulationContext simContext = new SimulationContext(bioModel.getModel(),geometry);
		bioModel.addSimulationContext(simContext);
		FeatureMapping cytosolFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(cytosol);
		cytosolFeatureMapping.setSubVolume(geometry.getGeometrySpec().getSubVolume(CYTOSOL_NAME));
		cytosolFeatureMapping.setResolved(true);
		FeatureMapping extracellularFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(extracellular);
		extracellularFeatureMapping.setSubVolume(geometry.getGeometrySpec().getSubVolume(EXTRACELLULAR_NAME));
		extracellularFeatureMapping.setResolved(true);

//		double[] timeStamps = getFrapData().getImageDataset().getImageTimeStamps();
		
		for (int i = 0; i < speciesContexts.length; i++) {
			SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(speciesContexts[i]);
			scs.getInitialConditionParameter().setExpression(initialConditions[i]);
			scs.getDiffusionParameter().setExpression(diffusionConstants[i]);
		}

		MathMapping mathMapping = new MathMapping(simContext);
		MathDescription mathDesc = mathMapping.getMathDescription();
		simContext.setMathDescription(mathDesc);

		SimulationVersion simVersion = new SimulationVersion(simKey,"sim1",owner,new GroupAccessNone(),new KeyValue("0"),new BigDecimal(0),new Date(),VersionFlag.Current,"",null);
		Simulation newSimulation = new Simulation(simVersion,mathDesc);
		simContext.addSimulation(newSimulation);
//		newSimulation.getSolverTaskDescription().setTimeBounds(new TimeBounds(0.0, (timeStamps[timeStamps.length-1]-timeStamps[getFrapDataAnalysisResults().getStartingIndexForRecovery().intValue()])));
//		newSimulation.getMeshSpecification().setSamplingSize(new ISize(firstImage.getNumX(),firstImage.getNumY(),getFrapData().getImageDataset().getSizeZ()));
		newSimulation.getSolverTaskDescription().setTimeBounds(timeBounds);
		newSimulation.getMeshSpecification().setSamplingSize(cellROI_2D.getISize());
//		double timeStep = timeStamps[timeStamps.length-1] - timeStamps[timeStamps.length-2];
//		newSimulation.getSolverTaskDescription().setTimeStep(new TimeStep(timeStep, timeStep, timeStep));
		newSimulation.getSolverTaskDescription().setTimeStep(timeStep);
		
		return bioModel;
	}
	
	
	private KernelJAI createCircularBinaryKernel(int radius){
		int enclosingBoxSideLength = radius*2+1;
		float[] kernalData = new float[enclosingBoxSideLength*enclosingBoxSideLength];
		Point2D kernalPoint = new Point2D.Float(0f,0f);
		int index = 0;
//		System.err.println("radius="+radius);
		for (int y = -radius; y <= radius; y++) {
			for (int x = -radius; x <= radius; x++) {
				if(kernalPoint.distance(x, y) <= radius){
					kernalData[index] = 1.0f;
				}
//				System.err.print(kernalData[index]+" ");
				index++;
			}
//			System.err.println();
		}
		return new KernelJAI(enclosingBoxSideLength,enclosingBoxSideLength,radius,radius,kernalData);
	}
	private PlanarImage binarize(UShortImage source){
		return binarize(ImageTools.makeImage(source.getPixels(), source.getNumX(), source.getNumY()));
	}
	private PlanarImage binarize(BufferedImage source){
		PlanarImage planarSource = PlanarImage.wrapRenderedImage(source);
		double[][] minmaxArr = (double[][])ExtremaDescriptor.create(planarSource, null, 1, 1, false, 1,null).getProperty("extrema");
//		System.err.println("min="+minmaxArr[0][0]+" max="+minmaxArr[1][0]);
		short[] lookupData = new short[(int)0x010000];
		lookupData[(int)minmaxArr[1][0]] = 1;
		LookupTableJAI lookupTable = new LookupTableJAI(lookupData,true);
		planarSource = LookupDescriptor.create(planarSource, lookupTable, null).createInstance();
//		planarSource =
//			ThresholdDescriptor.create(planarSource, new double[] {1},new double[] {(int)0xFFFF},new double[] {1},null).createInstance();
		return planarSource;		
	}
	private UShortImage convertToUShortImage(PlanarImage source,Extent extent) throws ImageException{
    	short[] shortData = new short[source.getWidth() * source.getHeight()];
    	source.getData().getDataElements(0, 0, source.getWidth(),source.getHeight(), shortData);
    	return new UShortImage(shortData,extent,source.getWidth(),source.getHeight(),1);	
	}
	private UShortImage erodeDilate(UShortImage source,KernelJAI dilateErodeKernel,UShortImage mask,boolean bErode) throws ImageException{
		PlanarImage completedImage = null;
		PlanarImage operatedImage = null;
		PlanarImage planarSource = binarize(source);
		Integer borderPad = dilateErodeKernel.getWidth()/2;
		planarSource = 
			BorderDescriptor.create(planarSource,
				borderPad, borderPad, borderPad, borderPad,
				BorderExtender.createInstance(BorderExtender.BORDER_ZERO), null).createInstance();
//		System.err.println(source.getNumX()+" "+planarSource.getWidth()+" -- "+source.getNumY()+" "+planarSource.getHeight());
//		writeBufferedImageFile(planarSource.getAsBufferedImage(),
//			new File("D:\\developer\\eclipse\\workspace\\"+(bErode?"erode":"dilate")+"_beforeoperated_"+dilateErodeKernel.getWidth()+".bmp"));
		if(bErode){
			planarSource = AddConstDescriptor.create(planarSource, new double[] {1.0}, null).createInstance();
	    	RenderedOp erodeOP = ErodeDescriptor.create(planarSource, dilateErodeKernel, null);
	    	operatedImage = erodeOP.createInstance();
			
		}else{
	    	RenderedOp dilationOP = DilateDescriptor.create(planarSource, dilateErodeKernel, null);
	    	operatedImage = dilationOP.createInstance();
		}
		operatedImage =
    		CropDescriptor.create(operatedImage,
    			new Float(0), new Float(0),
    			new Float(source.getNumX()), new Float(source.getNumY()), null);
//		System.err.println(source.getNumX()+" "+operatedImage.getWidth()+" -- "+source.getNumY()+" "+operatedImage.getHeight());
    	operatedImage = binarize(operatedImage.getAsBufferedImage());
//		writeBufferedImageFile(operatedImage.getAsBufferedImage(),
//			new File("D:\\developer\\eclipse\\workspace\\"+(bErode?"erode":"dilate")+"_operated_"+dilateErodeKernel.getWidth()+".bmp"));
		if (mask != null) {
			RenderedOp andDescriptor = AndDescriptor.create(operatedImage,binarize(mask), null);
			completedImage = andDescriptor.createInstance();
		}else{
			completedImage = operatedImage;
		}
//		writeBufferedImageFile(completedImage.getAsBufferedImage(),
//			new File("D:\\developer\\eclipse\\workspace\\"+(bErode?"erode":"dilate")+"_"+dilateErodeKernel.getWidth()+".bmp"));
		return convertToUShortImage(completedImage, source.getExtent());
    	
}
//	private void writeUShortFile(UShortImage uShortImage,File outFile){
//		writeBufferedImageFile(
//			ImageTools.makeImage(uShortImage.getPixels(),uShortImage.getNumX(), uShortImage.getNumY()),outFile);
//
//	}
//	private void writeBufferedImageFile(BufferedImage bufferedImage,File outFile){
//		try{
//		ImageIO.write(
//			FormatDescriptor.create(bufferedImage, DataBuffer.TYPE_BYTE,null).createInstance(),
//			"bmp", outFile);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//	}
	private UShortImage fastDilate(UShortImage dilateSource,int radius,UShortImage mask) throws ImageException{
		short[] sourcePixels = dilateSource.getPixels();
		short[] targetPixels = dilateSource.getPixels().clone();
		KernelJAI dilateKernel = createCircularBinaryKernel(radius);
		float[] kernelData = dilateKernel.getKernelData();
		BitSet kernelBitSet = new BitSet(kernelData.length);
		for (int i = 0; i < kernelData.length; i++) {
			if(kernelData[i] == 1.0f){
				kernelBitSet.set(i);
			}
		}
		boolean bNeedsFill = false;
		for (int y = 0; y < dilateSource.getNumY(); y++) {
			int yOffset = y*dilateSource.getNumX();
			int yMinus = yOffset-dilateSource.getNumX();
			int yPlus = yOffset+dilateSource.getNumX();
			for (int x = 0; x < dilateSource.getNumX(); x++) {
				bNeedsFill = false;
				if(sourcePixels[x+yOffset] != 0){
					if(x-1 >= 0 && sourcePixels[(x-1)+yOffset] == 0){
						bNeedsFill = true;
					}else
					if(y-1 >= 0 && sourcePixels[x+yMinus] == 0){
						bNeedsFill = true;
					}else
					if(x+1 < dilateSource.getNumX() && sourcePixels[(x+1)+yOffset] == 0){
						bNeedsFill = true;
					}else
					if(y+1 < dilateSource.getNumY() && sourcePixels[x+yPlus] == 0){
						bNeedsFill = true;
					}
					if(bNeedsFill){
						int masterKernelIndex = 0;
						for (int y2 = y-radius; y2 <= y+radius; y2++) {
							if(y2>= 0 && y2 <dilateSource.getNumY()){
								int kernelIndex = masterKernelIndex;
								int targetYIndex = y2*dilateSource.getNumX();
								for (int x2 = x-radius; x2 <= x+radius; x2++) {
									if(kernelBitSet.get(kernelIndex) &&
										x2>= 0 && x2 <dilateSource.getNumX()){
										targetPixels[targetYIndex+x2] = 1;
									}
									kernelIndex++;
								}
							}
							masterKernelIndex+= dilateKernel.getWidth();
						}
					}
				}
			}
		}
		UShortImage resultImage =
			new UShortImage(targetPixels,
					dilateSource.getExtent(),
					dilateSource.getNumX(),
					dilateSource.getNumY(),
					dilateSource.getNumZ());
		resultImage.and(mask);
		return resultImage;
	}
	public void refreshDependentROIs(){
		UShortImage cellROI_2D = null;
		UShortImage bleachedROI_2D = null;
		UShortImage dilatedROI_2D_1 = null;
    	UShortImage dilatedROI_2D_2 = null;
    	UShortImage dilatedROI_2D_3 = null;
    	UShortImage dilatedROI_2D_4 = null;
    	UShortImage dilatedROI_2D_5 = null;
    	UShortImage erodedROI_2D_0 = null;
    	UShortImage erodedROI_2D_1 = null;
    	UShortImage erodedROI_2D_2 = null;

    	try {
    		cellROI_2D =
    			convertToUShortImage(binarize(getFrapData().getRoi(RoiType.ROI_CELL).getRoiImages()[0]),
    				getFrapData().getRoi(RoiType.ROI_CELL).getRoiImages()[0].getExtent());
    		bleachedROI_2D =
    			convertToUShortImage(
    					AndDescriptor.create(binarize(getFrapData().getRoi(RoiType.ROI_BLEACHED).getRoiImages()[0]),
    						binarize(cellROI_2D), null).createInstance(),
    					getFrapData().getRoi(RoiType.ROI_BLEACHED).getRoiImages()[0].getExtent());
//			writeUShortFile(cellROI_2D, new File("D:\\developer\\eclipse\\workspace\\cellROI_2D.bmp"));
//			writeUShortFile(bleachedROI_2D, new File("D:\\developer\\eclipse\\workspace\\bleachedROI_2D.bmp"));

    		dilatedROI_2D_1 =
    			fastDilate(bleachedROI_2D, 8, cellROI_2D);
//    			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(8), cellROI_2D,false);

			dilatedROI_2D_2 = 
				fastDilate(bleachedROI_2D, 16, cellROI_2D);
//    			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(16), cellROI_2D,false);

	    	dilatedROI_2D_3 = 
	    		fastDilate(bleachedROI_2D, 24, cellROI_2D);
//    			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(24), cellROI_2D,false);

	    	dilatedROI_2D_4 = 
	    		fastDilate(bleachedROI_2D, 32, cellROI_2D);
//    			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(32), cellROI_2D,false);

	    	dilatedROI_2D_5 = 
	    		fastDilate(bleachedROI_2D, 40, cellROI_2D);
//    			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(40), cellROI_2D,false);
			
			erodedROI_2D_0 = new UShortImage(bleachedROI_2D);
			
			erodedROI_2D_1 = 
    			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(3), null,true);
			
			erodedROI_2D_2 = 
    			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(6), null,true);
			
			UShortImage reverseErodeROI_2D_1 = new UShortImage(erodedROI_2D_1);
			reverseErodeROI_2D_1.reverse();
			erodedROI_2D_0.and(reverseErodeROI_2D_1);
			
			UShortImage reverseErodeROI_2D_2 = new UShortImage(erodedROI_2D_2);
			reverseErodeROI_2D_2.reverse();
			erodedROI_2D_1.and(reverseErodeROI_2D_2);
			
			UShortImage reverseDilateROI_2D_4 = new UShortImage(dilatedROI_2D_4);
			reverseDilateROI_2D_4.reverse();
			dilatedROI_2D_5.and(reverseDilateROI_2D_4);

			UShortImage reverseDilateROI_2D_3 = new UShortImage(dilatedROI_2D_3);
//			writeUShortFile(dilatedROI_2D_3, new File("D:\\developer\\eclipse\\workspace\\dilatedROI_2D_3.bmp"));
			reverseDilateROI_2D_3.reverse();
//			writeUShortFile(reverseDilateROI_2D_3, new File("D:\\developer\\eclipse\\workspace\\dilatedROI_2D_3_reverse.bmp"));
//			writeUShortFile(dilatedROI_2D_4, new File("D:\\developer\\eclipse\\workspace\\dilatedROI_2D_4.bmp"));
			dilatedROI_2D_4.and(reverseDilateROI_2D_3);
//			writeUShortFile(dilatedROI_2D_4, new File("D:\\developer\\eclipse\\workspace\\dilatedROI_2D_4_anded.bmp"));

			UShortImage reverseDilateROI_2D_2 = new UShortImage(dilatedROI_2D_2);
			reverseDilateROI_2D_2.reverse();
			dilatedROI_2D_3.and(reverseDilateROI_2D_2);

			UShortImage reverseDilateROI_2D_1 = new UShortImage(dilatedROI_2D_1);
			reverseDilateROI_2D_1.reverse();
			dilatedROI_2D_2.and(reverseDilateROI_2D_1);

			UShortImage reverseBleach_2D = new UShortImage(bleachedROI_2D);
			reverseBleach_2D.reverse();
			dilatedROI_2D_1.and(reverseBleach_2D);

    	}catch (ImageException e){
    		e.printStackTrace(System.out);
    	}
		ROI roiBleachedRing1_2D = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING1);
		if (roiBleachedRing1_2D==null){
			roiBleachedRing1_2D = new ROI(erodedROI_2D_2,RoiType.ROI_BLEACHED_RING1);
			getFrapData().addReplaceRoi(roiBleachedRing1_2D);
		}else{
			System.arraycopy(erodedROI_2D_2.getPixels(), 0, roiBleachedRing1_2D.getRoiImages()[0].getPixels(), 0, erodedROI_2D_2.getPixels().length);
		}
		ROI roiBleachedRing2_2D = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING2);
		if (roiBleachedRing2_2D==null){
			roiBleachedRing2_2D = new ROI(erodedROI_2D_1,RoiType.ROI_BLEACHED_RING2);
			getFrapData().addReplaceRoi(roiBleachedRing2_2D);
		}else{
			System.arraycopy(erodedROI_2D_1.getPixels(), 0, roiBleachedRing2_2D.getRoiImages()[0].getPixels(), 0, erodedROI_2D_1.getPixels().length);
		}
		ROI roiBleachedRing3_2D = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING3);
		if (roiBleachedRing3_2D==null){
			roiBleachedRing3_2D = new ROI(erodedROI_2D_0,RoiType.ROI_BLEACHED_RING3);
			getFrapData().addReplaceRoi(roiBleachedRing3_2D);
		}else{
			System.arraycopy(erodedROI_2D_0.getPixels(), 0, roiBleachedRing3_2D.getRoiImages()[0].getPixels(), 0, erodedROI_2D_0.getPixels().length);
		}
		ROI roiBleachedRing4_2D = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING4);
		if (roiBleachedRing4_2D==null){
			roiBleachedRing4_2D = new ROI(dilatedROI_2D_1,RoiType.ROI_BLEACHED_RING4);
			getFrapData().addReplaceRoi(roiBleachedRing4_2D);
		}else{
			System.arraycopy(dilatedROI_2D_1.getPixels(), 0, roiBleachedRing4_2D.getRoiImages()[0].getPixels(), 0, dilatedROI_2D_1.getPixels().length);

		}
		ROI roiBleachedRing5_2D = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING5);
		if (roiBleachedRing5_2D==null){
			roiBleachedRing5_2D = new ROI(dilatedROI_2D_2,RoiType.ROI_BLEACHED_RING5);
			getFrapData().addReplaceRoi(roiBleachedRing5_2D);
		}else{
			System.arraycopy(dilatedROI_2D_2.getPixels(), 0, roiBleachedRing5_2D.getRoiImages()[0].getPixels(), 0, dilatedROI_2D_2.getPixels().length);
		}
		ROI roiBleachedRing6_2D = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING6);
		if (roiBleachedRing6_2D==null){
			roiBleachedRing6_2D = new ROI(dilatedROI_2D_3,RoiType.ROI_BLEACHED_RING6);
			getFrapData().addReplaceRoi(roiBleachedRing6_2D);
		}else{
			System.arraycopy(dilatedROI_2D_3.getPixels(), 0, roiBleachedRing6_2D.getRoiImages()[0].getPixels(), 0, dilatedROI_2D_3.getPixels().length);
		}
		ROI roiBleachedRing7_2D = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING7);
		if (roiBleachedRing7_2D==null){
			roiBleachedRing7_2D = new ROI(dilatedROI_2D_4,RoiType.ROI_BLEACHED_RING7);
			getFrapData().addReplaceRoi(roiBleachedRing7_2D);
		}else{
			System.arraycopy(dilatedROI_2D_4.getPixels(), 0, roiBleachedRing7_2D.getRoiImages()[0].getPixels(), 0, dilatedROI_2D_4.getPixels().length);
//			writeUShortFile(roiBleachedRing7_2D.getRoiImages()[0], new File("D:\\developer\\eclipse\\workspace\\ROI_BLEACHED_RING7.bmp"));
		}
		ROI roiBleachedRing8_2D = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING8);
		if (roiBleachedRing8_2D==null){
			roiBleachedRing8_2D = new ROI(dilatedROI_2D_5,RoiType.ROI_BLEACHED_RING8);
			getFrapData().addReplaceRoi(roiBleachedRing8_2D);
		}else{
			System.arraycopy(dilatedROI_2D_5.getPixels(), 0, roiBleachedRing8_2D.getRoiImages()[0].getPixels(), 0, dilatedROI_2D_5.getPixels().length);
		}
	}
	public static final String IMAGE_EXTDATA_NAME = "timeData";
	public void  saveImageDatasetAsExternalData(LocalWorkspace localWorkspace,ExternalDataIdentifier newImageExtDataID) throws Exception{
			ImageDataset imageDataset = getFrapData().getImageDataset();
			if (imageDataset.getSizeC()>1){
				throw new RuntimeException("FRAPStudy.saveImageDatasetAsExternalData(): multiple channels not yet supported");
			}
			Extent extent = imageDataset.getExtent();
			ISize isize = imageDataset.getISize();
			//Changed in March 2008.
			int startingIndexForRecovery = 0;
			if(getFrapDataAnalysisResults()!= null && getFrapDataAnalysisResults().getStartingIndexForRecovery() != null)
			{
				startingIndexForRecovery = getFrapDataAnalysisResults().getStartingIndexForRecovery();
			}
			int numImageToStore = imageDataset.getSizeT()-startingIndexForRecovery; //not include the prebleach 
	    	short[][][] pixData = new short[numImageToStore][1][]; //original fluor data only
	    	double[] timesArray = new double[numImageToStore];
//	    	float[] averagePreBleach = new float[imageDataset.getISize().getXYZ()];//store averagePreBleach. dimension:startingIndex to RecoveryIndex
//	    	for (int timeIndex = 0; timeIndex < startingIndexForRecovery; timeIndex++) {
//	    		short[] prebleachPixels = imageDataset.getPixelsZ(0, timeIndex);
//	    		for (int pixelIndex = 0; pixelIndex < averagePreBleach.length; pixelIndex++) {
//					averagePreBleach[pixelIndex] += ((float)(0xffff&((int)prebleachPixels[pixelIndex])))/startingIndexForRecovery;
//				}
//	    	}
	    	
	    	for (int tIndex = startingIndexForRecovery; tIndex < imageDataset.getSizeT(); tIndex++) {
	    		short[] originalData = imageDataset.getPixelsZ(0, tIndex);// images according to zIndex at time points(from recovery index to the end)
//	    		short[] normalizedData = new short[originalData.length];
//	    		for (int pixelIndex = 0; pixelIndex < normalizedData.length; pixelIndex++) {
//	    			float imageFloat = (0xffff&((int)originalData[pixelIndex]));
//	    			float weight = 40000.0f;
//					normalizedData[pixelIndex] = (short)(weight*(imageFloat/averagePreBleach[pixelIndex]));
//				}
	    		pixData[tIndex-startingIndexForRecovery][0] = originalData;
//	    		pixData[tIndex-startingIndexForRecovery][1] = normalizedData;
	    		timesArray[tIndex-startingIndexForRecovery] = imageDataset.getImageTimeStamps()[tIndex]-imageDataset.getImageTimeStamps()[startingIndexForRecovery];
	    	}
	    	//changed in March 2008. Though biomodel is not created, we still let user save to xml file.
	    	Origin origin = new Origin(0,0,0);
	    	CartesianMesh cartesianMesh  = getCartesianMesh();
//	    	CartesianMesh cartesianMesh  = null;
//	    	if (getBioModel()==null){
//	    		//throw new RuntimeException("can't save External Data, must generate BioModel first (to create corresponding mesh)");
//	    		cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent, isize, 
//	    						new RegionImage( new VCImageUncompressed(null, new byte[isize.getXYZ()], extent, isize.getX(),isize.getY(),isize.getZ()),
//	    						0,null,null,RegionImage.NO_SMOOTHING));
//	    	}
//	    	else
//	    	{
//		    	RegionImage regionImage = getBioModel().getSimulationContexts()[0].getGeometry().getGeometrySurfaceDescription().getRegionImage();
//		    	if(regionImage == null){
//		    		getBioModel().getSimulationContexts()[0].getGeometry().getGeometrySurfaceDescription().updateAll();
//		    		regionImage = getBioModel().getSimulationContexts()[0].getGeometry().getGeometrySurfaceDescription().getRegionImage();
//		    	}
//		    	cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent, isize, regionImage);
//	    	}
	    	FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
	    	fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
	    	fdos.cartesianMesh = cartesianMesh;
	    	fdos.shortSpecData =  pixData;
	    	fdos.specEDI = newImageExtDataID;
	    	fdos.varNames = new String[] {"fluor"};
	    	fdos.owner = localWorkspace.getOwner();
	    	fdos.times = timesArray;
	    	fdos.variableTypes = new VariableType[] {VariableType.VOLUME};
	    	fdos.origin = origin;
	    	fdos.extent = extent;
	    	fdos.isize = isize;
			localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);
//			setFrapDataExternalDataInfo(new ExternalDataInfo(externalDataID,new File(rootDir,externalDataID.getID()+".log").getAbsolutePath()));
//			deleteExternalData(localWorkspace, originalExtDataID);
//		}
	}
	
	public static File[] getCanonicalExternalDataFiles(LocalWorkspace localWorkspace,ExternalDataIdentifier originalExtDataID){
		if(originalExtDataID != null){
			File userDir = new File(localWorkspace.getSimDataDirectory(),localWorkspace.getOwner().getName());
			File fdLogFile =
				new File(userDir,
						ExternalDataIdentifier.createCanonicalSimLogFileName(originalExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false));
			File fdMeshFile =
				new File(userDir,
						ExternalDataIdentifier.createCanonicalMeshFileName(originalExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false));
			File fdFunctionFile =
				new File(userDir,
						ExternalDataIdentifier.createCanonicalFunctionsFileName(originalExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false));
			File fdZipFile =
				new File(userDir,
						ExternalDataIdentifier.createCanonicalSimZipFileName(originalExtDataID.getKey(), 0,FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false));
			return new File[] {fdLogFile,fdMeshFile,fdFunctionFile,fdZipFile};
		}
		return null;
	}
	public static void deleteCanonicalExternalData(LocalWorkspace localWorkspace,ExternalDataIdentifier originalExtDataID) throws Exception{
		File[] externalDataFiles = getCanonicalExternalDataFiles(localWorkspace, originalExtDataID);
		for (int i = 0;externalDataFiles != null && i < externalDataFiles.length; i++) {
			externalDataFiles[i].delete();
		}
	}
	private CartesianMesh getCartesianMesh() throws Exception{
		CartesianMesh cartesianMesh = null;
		ImageDataset imgDataSet = getFrapData().getImageDataset();
		Extent extent = imgDataSet.getExtent();
		ISize isize = imgDataSet.getISize();
		Origin origin = new Origin(0,0,0);
    	if (getBioModel()==null){
    		cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent,isize, 
    						new RegionImage( new VCImageUncompressed(null, new byte[isize.getXYZ()], extent, isize.getX(),isize.getY(),isize.getZ()),
    						0,null,null,RegionImage.NO_SMOOTHING));
    	}
    	else
    	{
	    	RegionImage regionImage = getBioModel().getSimulationContexts()[0].getGeometry().getGeometrySurfaceDescription().getRegionImage();
	    	if(regionImage == null){
	    		getBioModel().getSimulationContexts()[0].getGeometry().getGeometrySurfaceDescription().updateAll();
	    		regionImage = getBioModel().getSimulationContexts()[0].getGeometry().getGeometrySurfaceDescription().getRegionImage();
	    	}
	    	cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent, isize, regionImage);
    	}
    	return cartesianMesh;
	}
	public static final String ROI_EXTDATA_NAME = "roiData";
	public void saveROIsAsExternalData(LocalWorkspace localWorkspace,ExternalDataIdentifier newROIExtDataID) throws Exception{
			System.out.println("Saving ROIs (and statistics images) as External Data");
			ImageDataset imageDataset = getFrapData().getImageDataset();
			Extent extent = imageDataset.getExtent();
			ISize isize = imageDataset.getISize();
			int NumTimePoints = 1;
			int NumChannels = 13;//actually it is total number of ROIs(cell,bleached + 8 rings)+prebleach+firstPostBleach+lastPostBleach
	    	short[][][] pixData = new short[NumTimePoints][NumChannels][]; // dimensions: time points, channels, whole image ordered by z slices. 
	    	
	    	// take average of prebleach images 
	    	// changed in March, 2008.
	    	int startingIndexForRecovery = 0;
	    	if(getFrapDataAnalysisResults() != null )
	    	{
	    		startingIndexForRecovery = getFrapDataAnalysisResults().getStartingIndexForRecovery();//starting index for recovery is the first postbleaching index.
	    	}
	    	float[] accumPrebleachImage = new float[imageDataset.getISize().getXYZ()];//ISize: Image size including x, y, z. getXYZ()=x*y*z
			for (int timeIndex = 0; timeIndex < startingIndexForRecovery; timeIndex++) {
				short[] pixels = getFrapData().getImageDataset().getPixelsZ(0, timeIndex);//channel index is 0. it is not supported yet. get image size X*Y*Z stored by time index. image store by UShortImage[Z*T]
				for (int i = 0; i < accumPrebleachImage.length; i++) {
					float pixelValue = 0xffff&((int)pixels[i]);
					if (pixelValue<0){
						System.out.println("pixelValue = "+pixelValue);
					}
					accumPrebleachImage[i] += pixelValue;
				}
			}
			short[] avgPrebleach = new short[accumPrebleachImage.length];
			//changed in March, 2008
			if(startingIndexForRecovery > 0)
			{
				for (int i = 0; i < avgPrebleach.length; i++) {
					avgPrebleach[i] = (short)Math.round(accumPrebleachImage[i]/startingIndexForRecovery);
//					since prebleach will be used to normalize image data, we have to eliminate the 0
//					if(avgPrebleach[i] <= 0)
//					{
//						avgPrebleach[i] = Epsilon;
//					}
				}
			}
    		pixData[0][0] = avgPrebleach; // average of prebleach
    		pixData[0][1] = getFrapData().getImageDataset().getPixelsZ(0, startingIndexForRecovery); // first post-bleach
    		pixData[0][2] = getFrapData().getImageDataset().getPixelsZ(0, imageDataset.getSizeT()-1); // last post-bleach image, actually at last time point
    		pixData[0][3] = getFrapData().getRoi(RoiType.ROI_BLEACHED).getBinaryPixelsXYZ(1);
    		pixData[0][4] = getFrapData().getRoi(RoiType.ROI_CELL).getBinaryPixelsXYZ(1);
    		if (getFrapData().getRoi(RoiType.ROI_BLEACHED_RING1) == null){
    			//throw new RuntimeException("must first generate \"derived masks\"");
    			pixData[0][5] = new short[imageDataset.getISize().getXYZ()];
	    		pixData[0][6] = new short[imageDataset.getISize().getXYZ()];
	    		pixData[0][7] = new short[imageDataset.getISize().getXYZ()];
	    		pixData[0][8] = new short[imageDataset.getISize().getXYZ()];
	    		pixData[0][9] = new short[imageDataset.getISize().getXYZ()];
	    		pixData[0][10] = new short[imageDataset.getISize().getXYZ()];
	    		pixData[0][11] = new short[imageDataset.getISize().getXYZ()];
	    		pixData[0][12] = new short[imageDataset.getISize().getXYZ()];
    		}
    		else
    		{
	    		pixData[0][5] = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING1).getBinaryPixelsXYZ(1);
	    		pixData[0][6] = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING2).getBinaryPixelsXYZ(1);
	    		pixData[0][7] = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING3).getBinaryPixelsXYZ(1);
	    		pixData[0][8] = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING4).getBinaryPixelsXYZ(1);
	    		pixData[0][9] = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING5).getBinaryPixelsXYZ(1);
	    		pixData[0][10] = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING6).getBinaryPixelsXYZ(1);
//				writeUShortFile(getFrapData().getRoi(RoiType.ROI_BLEACHED_RING7).getRoiImages()[0], new File("D:\\developer\\eclipse\\workspace\\ROI_BLEACHED_RING7_prebinary.bmp"));
	    		pixData[0][11] = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING7).getBinaryPixelsXYZ(1);
//	    		UShortImage tempImage =
//	    			new UShortImage(
//	    				pixData[0][11],
//	    				getFrapData().getRoi(RoiType.ROI_BLEACHED_RING7).getRoiImages()[0].getExtent(),
//	    				getFrapData().getRoi(RoiType.ROI_BLEACHED_RING7).getRoiImages()[0].getNumX(),
//	    				getFrapData().getRoi(RoiType.ROI_BLEACHED_RING7).getRoiImages()[0].getNumY(),
//	    				getFrapData().getRoi(RoiType.ROI_BLEACHED_RING7).getRoiImages()[0].getNumZ());
//				writeUShortFile(tempImage, new File("D:\\developer\\eclipse\\workspace\\ROI_BLEACHED_RING7_binary.bmp"));
	    		pixData[0][12] = getFrapData().getRoi(RoiType.ROI_BLEACHED_RING8).getBinaryPixelsXYZ(1);
    		}
    		CartesianMesh cartesianMesh = getCartesianMesh();
//    		CartesianMesh cartesianMesh = null;
    		Origin origin = new Origin(0,0,0);
//    		if (getBioModel()==null){
//    			//throw new RuntimeException("cannot save ROIs as external data, need geometrys (for mesh)");
//    			cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent, isize, 
//	    				new RegionImage(
//								new VCImageUncompressed(
//										null,
//										new byte[isize.getXYZ()],//empty regions
//										extent,
//										isize.getX(),isize.getY(),isize.getZ()
//										),0,null,null,RegionImage.NO_SMOOTHING));
//    		}
//    		else
//    		{
//    			RegionImage regionImage = getBioModel().getSimulationContexts()[0].getGeometry().getGeometrySurfaceDescription().getRegionImage();
//    			cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent, isize, regionImage);
//    		}
	    		    	
	    	FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
	    	fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
	    	fdos.cartesianMesh = cartesianMesh;
	    	fdos.shortSpecData =  pixData;
	    	fdos.specEDI = newROIExtDataID;
	    	fdos.varNames = new String[] {
	    			"prebleach_avg",
	    			"postbleach_first",
	    			"postbleach_last",
	    			"bleached_mask", 
	    			"cell_mask", 
	    			"ring1_mask",
	    			"ring2_mask",
	    			"ring3_mask",
	    			"ring4_mask",
	    			"ring5_mask",
	    			"ring6_mask",
	    			"ring7_mask",
	    			"ring8_mask"};
	    	fdos.owner = localWorkspace.getOwner();
	    	fdos.times = new double[] { 0.0 };
	    	fdos.variableTypes = new VariableType[] {
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME};
	    	fdos.origin = origin;
	    	fdos.extent = extent;
	    	fdos.isize = isize;
	    	localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);
//			setRoiExternalDataInfo(new ExternalDataInfo(externalDataID,new File(rootDir,externalDataID.getID()+".log").getAbsolutePath()));
//			deleteExternalData(localWorkspace, originalExtDataID);
//		}
	}
	
	//get external image dataset file name or ROI file name
	public File getExternalDataFile(ExternalDataInfo arg_extDataInfo)
	{
		final ExternalDataInfo extDataInfo = arg_extDataInfo;
		File extFile = new File(extDataInfo.getFilename());
		File f = new File(extFile.getParent(), extDataInfo.getExternalDataIdentifier().getOwner().getName());
		f= new  File(f,extFile.getName());
		return f;
	}
	
	public void clearBioModel(){
		setBioModel(null);
	}
	public void setBioModel(BioModel argBioModel) {
		BioModel oldValue = this.bioModel;
		this.bioModel = argBioModel;
		propertyChangeSupport.firePropertyChange("bioModel", oldValue, argBioModel);
	}
	public FRAPData getFrapData() {
		return frapData;
	}
	
	public void setFrapData(FRAPData frapData) {
		this.frapData = frapData;
	}
	
	public void gatherIssues(Vector<Issue> issueList){
//		if (mathModel!=null){
//			mathModel.gatherIssues(issueList);
//		}
		if (frapData!=null){
			frapData.gatherIssues(issueList);
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		String oldValue = this.description;
		this.description = description;
		propertyChangeSupport.firePropertyChange("description", oldValue, description);
	}

	public FrapDataAnalysisResults getFrapDataAnalysisResults() {
		return frapDataAnalysisResults;
	}
	//changed to be public in March 2008
	public void setFrapDataAnalysisResults(FrapDataAnalysisResults frapDataAnalysisResults) {
		FrapDataAnalysisResults oldValue = this.frapDataAnalysisResults;
		this.frapDataAnalysisResults = frapDataAnalysisResults;
		propertyChangeSupport.firePropertyChange("frapDataAnalysisResults", oldValue, frapDataAnalysisResults);
	}

	public String getOriginalImageFilePath() {
		return originalImageFilePath;
	}

	public void setOriginalImageFilePath(String originalImageFilePath) {
		String oldValue = this.originalImageFilePath;
		this.originalImageFilePath = originalImageFilePath;
		propertyChangeSupport.firePropertyChange("originalImageFilePath", oldValue, originalImageFilePath);
	}
	public String getXmlFilename() {
		return xmlFilename;
	}

	public void setXmlFilename(String xmlFilename) {
		String oldValue = this.xmlFilename;
		this.xmlFilename = xmlFilename;
		propertyChangeSupport.firePropertyChange("xmlFilename", oldValue, xmlFilename);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = this.name;
		this.name = name;
		propertyChangeSupport.firePropertyChange("name", oldValue, name);
	}

	public String getDirectory() {
		return directory;
	}

	void setDirectory(String directory) {
		String oldValue = this.directory;
		this.directory = directory;
		propertyChangeSupport.firePropertyChange("directory", oldValue, directory);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public boolean compareEqual(Matchable obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof FRAPStudy) 
		{
			FRAPStudy fStudy = (FRAPStudy)obj;
			if(!cbit.util.Compare.isEqualOrNull(name, fStudy.name))
			{
				return false;
			}
			if(!cbit.util.Compare.isEqualOrNull(description, fStudy.description))
			{
				return false;
			}
			if(!cbit.util.Compare.isEqualOrNull(originalImageFilePath, fStudy.originalImageFilePath))
			{
				return false;
			}
			if(!this.getFrapData().compareEqual(fStudy.getFrapData()))
			{
				return false;
			}
//			if(!this.getCellImageStack().compareEqual(fStudy.getCellImageStack()))
//			{
//				return false;
//			}	
			if(!cbit.util.Compare.isEqualOrNull(getBioModel(), fStudy.getBioModel()))
			{
				return false;
			}
			if(!cbit.util.Compare.isEqualOrNull(getFrapDataAnalysisResults(), fStudy.getFrapDataAnalysisResults()))
			{
				return false;
			}
			if(!cbit.util.Compare.isEqualOrNull(getFrapDataExternalDataInfo(),fStudy.getFrapDataExternalDataInfo()))
			{
				return false;
			}
			if (!cbit.util.Compare.isEqualOrNull(getRoiExternalDataInfo(),fStudy.getRoiExternalDataInfo()))
			{
				return false;
			}
			return true;
		}

		return false;
	}

}
