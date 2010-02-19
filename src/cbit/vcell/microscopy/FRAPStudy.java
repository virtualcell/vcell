package cbit.vcell.microscopy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileFilter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.vcell.util.Compare;
import org.vcell.util.Extent;
import org.vcell.util.FileUtils;
import org.vcell.util.ISize;
import org.vcell.util.Issue;
import org.vcell.util.Matchable;
import org.vcell.util.Origin;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VersionFlag;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.server.PDEDataManager;
import cbit.vcell.client.task.ClientTaskStatusSupport;
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
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.model.Feature;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.parser.Expression;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.DataProcessingInstructions;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverEvent;
import cbit.vcell.solver.SolverListener;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.FVSolverStandalone;

public class FRAPStudy implements Matchable{
	public static final String EXTRACELLULAR_NAME = "extracellular";
	public static final String CYTOSOL_NAME = "cytosol";
	public static final String SPECIES_NAME_PREFIX_MOBILE = "fluor_primary_mobile"; 
	public static final String SPECIES_NAME_PREFIX_SLOW_MOBILE = "fluor_secondary_mobile";
	public static final String SPECIES_NAME_PREFIX_BINDING_SITE = "binding_site";
	public static final String SPECIES_NAME_PREFIX_IMMOBILE = "fluor_immobile"; 
	public static final String SPECIES_NAME_PREFIX_COMBINED = "fluor_combined"; 
	
	public static final String IMAGE_EXTDATA_NAME = "timeData";
	public static final String ROI_EXTDATA_NAME = "roiData";
	public static final String REF_EXTDATA_NAME = "refData";
	public static final String ROI_SUMDATA_NAME = "sumROIData";
	public static final String PSF_DATA_NAME = "psfData";
	
	private String name = null; 
	private String description = null;
	private transient String xmlFilename = null;
	private String originalImageFilePath = null;
	private FRAPData frapData = null;
	private Integer startingIndexForRecovery = null;

	private BioModel bioModel = null;
	private ExternalDataInfo frapDataExternalDataInfo = null;
	private ExternalDataInfo roiExternalDataInfo = null;
	//Added in Feb 2009, we want to store reference data together with the model in .vfrap file. 
	private SimpleReferenceData storedRefData = null;
		
	//models
	private FRAPModel[] models = new FRAPModel[FRAPModel.NUM_MODEL_TYPES];
	private Integer bestModelIndex = null;
	
	//Selected ROIs for error calculation, length 11, according to the order in FRAPData.VFRAP_ROI_ENUM
	private boolean[] selectedROIsForErrCalculation = null; 
	
	PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	//temporary data structure for reference data used in optimization
	private transient FRAPOptData frapOptData = null;
	//temporary data structure to store MSE for different models under different ROIs. (may not be all the models with all ROIs)
	//first dimentsion: 3 models, second dimension: bleahed + 8 Rings + sum of MSE, any element that is not applicable should fill with 1e8.
	private transient double[][] analysisMSESummaryData = null; 
	//temporary data structure to store dimension reduced experimental data (all ROIs)
	private transient double[][] dimensionReducedExpData = null;
	//temporary data structure to identify whether the current frapStudy needs a save or not
	private transient boolean bSaveNeeded = false;
	
	//static functions
	public static void removeExternalDataAndSimulationFiles(
			KeyValue simulationKeyValue,
			ExternalDataIdentifier frapDataExtDataId,ExternalDataIdentifier roiExtDataId,
			LocalWorkspace localWorkspace) throws Exception{
		
		if(frapDataExtDataId != null){
			FRAPStudy.deleteCanonicalExternalData(localWorkspace,frapDataExtDataId);
		}
		if(roiExtDataId != null){
			FRAPStudy.deleteCanonicalExternalData(localWorkspace,roiExtDataId);
		}
		if(frapDataExtDataId != null && roiExtDataId != null){
			File mergedFunctionFile = 
				FRAPStudy.getMergedFunctionFile(frapDataExtDataId,roiExtDataId,
						new File(localWorkspace.getDefaultSimDataDirectory()));
			if(mergedFunctionFile != null){
				mergedFunctionFile.delete();
			}
		}
		if(simulationKeyValue != null){
			File userDir =
				new File(localWorkspace.getDefaultSimDataDirectory());
			FRAPStudy.deleteSimulationFiles(userDir, simulationKeyValue);
		}
	}

	public static void removeExternalFiles(ExternalDataIdentifier frapDataExtDataId, ExternalDataIdentifier roiExtDataId, LocalWorkspace localWorkspace) throws Exception
	{
		
		if(frapDataExtDataId != null)
		{
			FRAPStudy.deleteCanonicalExternalData(localWorkspace,frapDataExtDataId);
		}
		if(roiExtDataId != null)
		{
			FRAPStudy.deleteCanonicalExternalData(localWorkspace,roiExtDataId);
		}
		if(frapDataExtDataId != null && roiExtDataId != null)
		{
			File mergedFunctionFile = 
				FRAPStudy.getMergedFunctionFile(frapDataExtDataId,roiExtDataId,
						new File(localWorkspace.getDefaultSimDataDirectory()));
			if(mergedFunctionFile != null){
				mergedFunctionFile.delete();
			}
		}
	}
	
	public static void removeSimulationFiles(KeyValue simulationKeyValue, LocalWorkspace localWorkspace) throws Exception
	{
		if(simulationKeyValue != null)
		{
			File userDir = new File(localWorkspace.getDefaultSimDataDirectory());
			FRAPStudy.deleteSimulationFiles(userDir, simulationKeyValue);
		}
	}
	
	private static File getMergedFunctionFile(ExternalDataIdentifier frapDataExtDataId,ExternalDataIdentifier roiExtDataId,
			File simDataDirectory)
	{
			MergedDataInfo mergedDataInfo =
				new MergedDataInfo(LocalWorkspace.getDefaultOwner(),
					new VCDataIdentifier[]{frapDataExtDataId,roiExtDataId}, FRAPStudyPanel.VFRAP_DS_PREFIX);
			return
				new File(simDataDirectory,
					mergedDataInfo.getID()+SimDataConstants.FUNCTIONFILE_EXTENSION);
	}

	private static void deleteSimulationFiles(File rootDir,KeyValue simKey){
		File[] oldSimFilesToDelete = FRAPStudy.getSimulationFileNames(rootDir,simKey);
		for (int i = 0; oldSimFilesToDelete != null && i < oldSimFilesToDelete.length; i++) {
			System.out.println("delete "+oldSimFilesToDelete[i].delete()+"  "+oldSimFilesToDelete[i].getAbsolutePath());
		}
	}

	public static File[] getSimulationFileNames(File rootDir,KeyValue simKey){
		final String deleteTheseSimID = Simulation.createSimulationID(simKey);
		return
			rootDir.listFiles(
				new FileFilter(){
					public boolean accept(File pathname) {
						if (pathname.getName().startsWith(deleteTheseSimID)){
							return true;
						}
						return false;
					}
				}
			);
	}
	
	public static BioModel createNewRefBioModel(
			FRAPStudy sourceFrapStudy,
			String baseDiffusionRate,
			TimeStep tStep,
			KeyValue simKey,
			User owner,
			FieldDataIdentifierSpec psfFDIS,
			int startingIndexForRecovery) throws Exception {

		if(owner == null){
			throw new Exception("Owner is not defined");
		}
		
		ROI cellROI_2D = sourceFrapStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		Extent extent = sourceFrapStudy.getFrapData().getImageDataset().getExtent();

		double[] timeStamps = sourceFrapStudy.getFrapData().getImageDataset().getImageTimeStamps();
		TimeBounds timeBounds = new TimeBounds(0.0,timeStamps[timeStamps.length-1]-timeStamps[startingIndexForRecovery]);
		double timeStepVal = timeStamps[startingIndexForRecovery+1] - timeStamps[startingIndexForRecovery];
		TimeStep timeStep = null;
		if(tStep != null)
		{
			timeStep = tStep;
		}
		else
		{
			timeStep = new TimeStep(timeStepVal, timeStepVal, timeStepVal);
		}
		
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
		if(geometry.getGeometrySpec().getNumSubVolumes() != 2){
			throw new Exception("Cell ROI has no ExtraCellular.");
		}
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

		String roiDataName = "roiData";
		
		final int ONE_DIFFUSION_SPECIES_COUNT = 1;
		final int MOBILE_SPECIES_INDEX = 0;
				
		Expression[] diffusionConstants = new Expression[ONE_DIFFUSION_SPECIES_COUNT];
		Species[] species = new Species[ONE_DIFFUSION_SPECIES_COUNT]; 
		SpeciesContext[] speciesContexts = new SpeciesContext[ONE_DIFFUSION_SPECIES_COUNT];
		Expression[] initialConditions = new Expression[ONE_DIFFUSION_SPECIES_COUNT];
		
		//Mobile Species
		diffusionConstants[MOBILE_SPECIES_INDEX] = new Expression(baseDiffusionRate);
		species[MOBILE_SPECIES_INDEX] =
				new Species(SPECIES_NAME_PREFIX_MOBILE, "Mobile bleachable species");
		speciesContexts[MOBILE_SPECIES_INDEX] = 
				new SpeciesContext(null,species[MOBILE_SPECIES_INDEX].getCommonName(),species[MOBILE_SPECIES_INDEX],cytosol,true);
		initialConditions[MOBILE_SPECIES_INDEX] =
				new Expression("(field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0))");
		
		SimulationContext simContext = new SimulationContext(bioModel.getModel(),geometry);
		bioModel.addSimulationContext(simContext);
		FeatureMapping cytosolFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(cytosol);
		cytosolFeatureMapping.setSubVolume(geometry.getGeometrySpec().getSubVolume(CYTOSOL_NAME));
		cytosolFeatureMapping.setResolved(true);
		FeatureMapping extracellularFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(extracellular);
		extracellularFeatureMapping.setSubVolume(geometry.getGeometrySpec().getSubVolume(EXTRACELLULAR_NAME));
		extracellularFeatureMapping.setResolved(true);
		
		for (int i = 0; i < initialConditions.length; i++) {
			model.addSpecies(species[i]);
			model.addSpeciesContext(speciesContexts[i]);
		}
		
		for (int i = 0; i < speciesContexts.length; i++) {
			SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(speciesContexts[i]);
			scs.getInitialConditionParameter().setExpression(initialConditions[i]);
			scs.getDiffusionParameter().setExpression(diffusionConstants[i]);
		}
		MathMapping mathMapping = new MathMapping(simContext);
		MathDescription mathDesc = mathMapping.getMathDescription();
		
		//Add PSF function
		
		mathDesc.addVariable(new Function(SimDataConstants.PSF_FUNCTION_NAME,
					new Expression(psfFDIS.getFieldFuncArgs().infix())));
		
		simContext.setMathDescription(mathDesc);
				
		SimulationVersion simVersion = new SimulationVersion(simKey,"sim1",owner,new GroupAccessNone(),new KeyValue("0"),new BigDecimal(0),new Date(),VersionFlag.Current,"",null);
		Simulation newSimulation = new Simulation(simVersion,simContext.getMathDescription());
//		newSimulation.getSolverTaskDescription().setSolverDescription(SolverDescription.SundialsPDE);
//		newSimulation.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(timeStepVal));
		simContext.addSimulation(newSimulation);
		newSimulation.getSolverTaskDescription().setTimeBounds(timeBounds);
		newSimulation.getMeshSpecification().setSamplingSize(cellROI_2D.getISize());
		newSimulation.getSolverTaskDescription().setTimeStep(timeStep);
		
		return bioModel;
	}
	
	public static BioModel createNewSimBioModel(
			FRAPStudy sourceFrapStudy,
			Parameter[] params,
			TimeStep tStep,
			KeyValue simKey,
			User owner,
			int startingIndexForRecovery) throws Exception {

		if(owner == null){
			throw new Exception("Owner is not defined");
		}
		ROI cellROI_2D = sourceFrapStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		
		double df = params[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess();
		double ff = params[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess();
		double bwmRate = params[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess();
		double dc = 0;
		double fc = 0; 
		double bs = 0;
		double onRate = 0;
		double offRate = 0;
		if(params.length == FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF)
		{
			dc = params[FRAPModel.INDEX_SECONDARY_DIFF_RATE].getInitialGuess();
			fc =params[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess();
		}
		else if(params.length == FRAPModel.NUM_MODEL_PARAMETERS_BINDING)
		{
			dc = params[FRAPModel.INDEX_SECONDARY_DIFF_RATE].getInitialGuess();
			fc =params[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess();
			bs = params[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION].getInitialGuess();
			onRate = params[FRAPModel.INDEX_ON_RATE].getInitialGuess();
			offRate = params[FRAPModel.INDEX_OFF_RATE].getInitialGuess();
		} 
		//immobile fraction
		double fimm = 1-ff-fc;
		if(fimm < FRAPOptimization.epsilon && fimm > (0 - FRAPOptimization.epsilon))
		{
			fimm = 0;
		}
		if(fimm < (1+FRAPOptimization.epsilon) && fimm > (1 - FRAPOptimization.epsilon))
		{
			fimm = 1;
		}

		Extent extent = sourceFrapStudy.getFrapData().getImageDataset().getExtent();

		double[] timeStamps = sourceFrapStudy.getFrapData().getImageDataset().getImageTimeStamps();
		TimeBounds timeBounds = new TimeBounds(0.0,timeStamps[timeStamps.length-1]-timeStamps[startingIndexForRecovery]);
		double timeStepVal = timeStamps[startingIndexForRecovery+1] - timeStamps[startingIndexForRecovery];
//		double defaultReacDiffTimeStep = 0.001;
//		TimeStep timeStep = new TimeStep(defaultReacDiffTimeStep, defaultReacDiffTimeStep, defaultReacDiffTimeStep);//not used, too small that run very slowly
		int keepEvery =1;
		DefaultOutputTimeSpec timeSpec = new DefaultOutputTimeSpec(keepEvery, 1000);//not used until we use smaller time step

		TimeStep timeStep = null;
		if(tStep != null)
		{
			timeStep = tStep;
		}
		else
		{
			timeStep = new TimeStep(timeStepVal, timeStepVal, timeStepVal);
		}
		
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
		if(geometry.getGeometrySpec().getNumSubVolumes() != 2){
			throw new Exception("Cell ROI has no ExtraCellular.");
		}
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

		String roiDataName = "roiData";
		
		final int SPECIES_COUNT = 4;
		final int FREE_SPECIES_INDEX = 0;
		final int BS_SPECIES_INDEX = 1;
		final int COMPLEX_SPECIES_INDEX = 2;
		final int IMMOBILE_SPECIES_INDEX =3;
		
		Expression[] diffusionConstants = null;
		Species[] species = null;
		SpeciesContext[] speciesContexts = null;
		Expression[] initialConditions = null;
		
		
		diffusionConstants = new Expression[SPECIES_COUNT];
		species = new Species[SPECIES_COUNT];
		speciesContexts = new SpeciesContext[SPECIES_COUNT];
		initialConditions = new Expression[SPECIES_COUNT];
				
		//Free Species
		diffusionConstants[FREE_SPECIES_INDEX] = new Expression(df);
		species[FREE_SPECIES_INDEX] =
				new Species(FRAPStudy.SPECIES_NAME_PREFIX_MOBILE,	"Mobile bleachable species");
		speciesContexts[FREE_SPECIES_INDEX] = 
				new SpeciesContext(null,species[FREE_SPECIES_INDEX].getCommonName(),species[FREE_SPECIES_INDEX],cytosol,true);
		initialConditions[FREE_SPECIES_INDEX] =
				new Expression(ff+"*(field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0))");
		
		//Immobile Species (No diffusion)
		//Set very small diffusion rate on immobile to force evaluation as state variable (instead of FieldData function)
		//If left as a function errors occur because functions involving FieldData require a database connection
		final String IMMOBILE_DIFFUSION_KLUDGE = "1e-14";
		diffusionConstants[IMMOBILE_SPECIES_INDEX] = new Expression(IMMOBILE_DIFFUSION_KLUDGE);
		species[IMMOBILE_SPECIES_INDEX] =
				new Species(FRAPStudy.SPECIES_NAME_PREFIX_IMMOBILE,"Immobile bleachable species");
		speciesContexts[IMMOBILE_SPECIES_INDEX] = 
				new SpeciesContext(null,species[IMMOBILE_SPECIES_INDEX].getCommonName(),species[IMMOBILE_SPECIES_INDEX],cytosol,true);
		initialConditions[IMMOBILE_SPECIES_INDEX] =
				new Expression(fimm+"*(field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0))");

		//BS Species
		diffusionConstants[BS_SPECIES_INDEX] = new Expression(IMMOBILE_DIFFUSION_KLUDGE);
		species[BS_SPECIES_INDEX] =
				new Species(FRAPStudy.SPECIES_NAME_PREFIX_BINDING_SITE,"Binding Site species");
		speciesContexts[BS_SPECIES_INDEX] = 
				new SpeciesContext(null,species[BS_SPECIES_INDEX].getCommonName(),species[BS_SPECIES_INDEX],cytosol,true);
		initialConditions[BS_SPECIES_INDEX] =
				new Expression(bs+"* field("+roiDataName+",prebleach_avg,0)");
	
		//Complex species
		diffusionConstants[COMPLEX_SPECIES_INDEX] = new Expression(dc);
		species[COMPLEX_SPECIES_INDEX] =
				new Species(FRAPStudy.SPECIES_NAME_PREFIX_SLOW_MOBILE, "Slower mobile bleachable species");
		speciesContexts[COMPLEX_SPECIES_INDEX] = 
				new SpeciesContext(null,species[COMPLEX_SPECIES_INDEX].getCommonName(),species[COMPLEX_SPECIES_INDEX],cytosol,true);
		initialConditions[COMPLEX_SPECIES_INDEX] =
				new Expression(fc+"*(field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0))");
				
	
		// add reactions to species if there is bleachWhileMonitoring rate.
		for (int i = 0; i < initialConditions.length; i++) {
			model.addSpecies(species[i]);
			model.addSpeciesContext(speciesContexts[i]);
			//reaction with BMW rate, which should not be applied to binding site
			if(!(species[i].getCommonName().equals(FRAPStudy.SPECIES_NAME_PREFIX_BINDING_SITE)))
			{
				SimpleReaction simpleReaction = new SimpleReaction(cytosol,speciesContexts[i].getName()+"_bleach");
				model.addReactionStep(simpleReaction);
				simpleReaction.addReactant(speciesContexts[i], 1);
				MassActionKinetics massActionKinetics = new MassActionKinetics(simpleReaction);
				simpleReaction.setKinetics(massActionKinetics);
				KineticsParameter kforward = massActionKinetics.getForwardRateParameter();
				simpleReaction.getKinetics().setParameterValue(kforward, new Expression(new Double(bwmRate)));
			}
		}

		// add the binding reaction: F + BS <-> C
		SimpleReaction simpleReaction2 = new SimpleReaction(cytosol,"reac_binding");
		model.addReactionStep(simpleReaction2);
		simpleReaction2.addReactant(speciesContexts[FREE_SPECIES_INDEX], 1);
		simpleReaction2.addReactant(speciesContexts[BS_SPECIES_INDEX], 1);
		simpleReaction2.addProduct(speciesContexts[COMPLEX_SPECIES_INDEX], 1);
		MassActionKinetics massActionKinetics = new MassActionKinetics(simpleReaction2);
		simpleReaction2.setKinetics(massActionKinetics);
		KineticsParameter kforward = massActionKinetics.getForwardRateParameter();
		KineticsParameter kreverse = massActionKinetics.getReverseRateParameter();
		simpleReaction2.getKinetics().setParameterValue(kforward, new Expression(new Double(onRate)));
		simpleReaction2.getKinetics().setParameterValue(kreverse, new Expression(new Double(offRate)));
		
		//create simulation context		
		SimulationContext simContext = new SimulationContext(bioModel.getModel(),geometry);
		bioModel.addSimulationContext(simContext);
		FeatureMapping cytosolFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(cytosol);
		cytosolFeatureMapping.setSubVolume(geometry.getGeometrySpec().getSubVolume(FRAPStudy.CYTOSOL_NAME));
		cytosolFeatureMapping.setResolved(true);
		FeatureMapping extracellularFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(extracellular);
		extracellularFeatureMapping.setSubVolume(geometry.getGeometrySpec().getSubVolume(FRAPStudy.EXTRACELLULAR_NAME));
		extracellularFeatureMapping.setResolved(true);
		
		for (int i = 0; i < speciesContexts.length; i++) {
			SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(speciesContexts[i]);
			scs.getInitialConditionParameter().setExpression(initialConditions[i]);
			scs.getDiffusionParameter().setExpression(diffusionConstants[i]);
		}

		MathMapping mathMapping = new MathMapping(simContext);
		MathDescription mathDesc = mathMapping.getMathDescription();
		//Add total fluorescence as function of mobile(optional: and slower mobile) and immobile fractions
		mathDesc.addVariable(
				new Function(FRAPStudy.SPECIES_NAME_PREFIX_COMBINED,
					new Expression(species[FREE_SPECIES_INDEX].getCommonName()+"+"+species[COMPLEX_SPECIES_INDEX].getCommonName()+"+"+species[IMMOBILE_SPECIES_INDEX].getCommonName())));
		simContext.setMathDescription(mathDesc);

		SimulationVersion simVersion = new SimulationVersion(simKey,"sim1",owner,new GroupAccessNone(),new KeyValue("0"),new BigDecimal(0),new Date(),VersionFlag.Current,"",null);
		Simulation newSimulation = new Simulation(simVersion,mathDesc);
		simContext.addSimulation(newSimulation);
		newSimulation.getSolverTaskDescription().setTimeBounds(timeBounds);
		newSimulation.getMeshSpecification().setSamplingSize(cellROI_2D.getISize());
		newSimulation.getSolverTaskDescription().setTimeStep(timeStep);
		newSimulation.getSolverTaskDescription().setOutputTimeSpec(timeSpec);//not used util we apply smaller time step
		
		return bioModel;
	}
	
	public static SpatialAnalysisResults spatialAnalysis(
			PDEDataManager simulationDataManager,
			int startingIndexForRecovery,
			double startingForRecoveryExperimentalTime,
			Parameter[] modelParameters,
			FRAPData frapData,
			double[] preBleachAverageXYZ) throws Exception{
		

		String[] varNames = new String[] {SPECIES_NAME_PREFIX_COMBINED};
		AnalysisParameters analysisParameters = new AnalysisParameters(modelParameters);
		
		
		//
		// for each simulation time step, get the data under each ROI <indexed by ROI, then diffusion
		//
		// preallocate arrays for all data first
		Hashtable<CurveInfo, double[]> curveHash = new Hashtable<CurveInfo, double[]>();
		ArrayList<int[]> nonZeroIndicesForROI = new ArrayList<int[]>();
		for (int i = 0; i < SpatialAnalysisResults.ORDERED_ROINAMES.length; i++) {
//			for (int j = 0; j < diffusionRates.length; j++) {
				curveHash.put(new CurveInfo(analysisParameters,SpatialAnalysisResults.ORDERED_ROINAMES[i]), new double[frapData.getImageDataset().getImageTimeStamps().length-startingIndexForRecovery]);
//			}
			ROI roi_2D = frapData.getRoi(SpatialAnalysisResults.ORDERED_ROINAMES[i]);
			nonZeroIndicesForROI.add(roi_2D.getRoiImages()[0].getNonzeroIndices());
		}
		
		//
		// collect data for experiment (over all ROIs), normalized with pre-bleach average
		//
		double[] avgBkIntensity = frapData.getAvgBackGroundIntensity();
		for (int i = 0; i < SpatialAnalysisResults.ORDERED_ROINAMES.length; i++) {
			double[] normalizedAverageFluorAtEachTimeUnderROI = new double[frapData.getImageDataset().getImageTimeStamps().length];
			for (int j = 0; j < normalizedAverageFluorAtEachTimeUnderROI.length; j++) {
				normalizedAverageFluorAtEachTimeUnderROI[j] = frapData.getAverageUnderROI(j,frapData.getRoi(SpatialAnalysisResults.ORDERED_ROINAMES[i]), preBleachAverageXYZ, avgBkIntensity[j]);				
			}
			curveHash.put(new CurveInfo(null,SpatialAnalysisResults.ORDERED_ROINAMES[i]), normalizedAverageFluorAtEachTimeUnderROI);
		}
		
		//
		// get times to compare experimental data with simulated results.
		//
		double[] shiftedSimTimes = null;
		if(simulationDataManager != null)
		{
			double[] simTimes = simulationDataManager.getDataSetTimes();
			shiftedSimTimes = simTimes.clone();
			for (int j = 0; j < simTimes.length; j++) {
				shiftedSimTimes[j] = simTimes[j] + startingForRecoveryExperimentalTime;//timeStamps[startingIndexOfRecovery];
			}
			//
			// get Simulation Data
			//
			int totalLen = simTimes.length*varNames.length*SpatialAnalysisResults.ORDERED_ROINAMES.length;
//			if(progressListener != null){progressListener.updateMessage("Spatial Analysis - normalization and data reduction");}
//			if(progressListener != null){progressListener.updateProgress(0);}
			
			//simulation may have different number of time points(1 more or 1 less) compare with experimental data.
			//curveHash was intialized with exp time data length. so, we have to replace it with the real sim data length.
			if(simTimes.length != (frapData.getImageDataset().getImageTimeStamps().length-startingIndexForRecovery))
			{
				for (int k = 0; k < SpatialAnalysisResults.ORDERED_ROINAMES.length; k++) {
					CurveInfo ci = new CurveInfo(analysisParameters,SpatialAnalysisResults.ORDERED_ROINAMES[k]);
					curveHash.remove(ci);
					curveHash.put(ci, new double[simTimes.length]);
				}
			}
			for (int i = 0; i < simTimes.length; i++) {
				for (int j = 0; j < varNames.length; j++) {
					SimDataBlock simDataBlock = simulationDataManager.getSimDataBlock(varNames[j], simTimes[i]);
					double[] data = simDataBlock.getData();
					for (int k = 0; k < SpatialAnalysisResults.ORDERED_ROINAMES.length; k++) {
						CurveInfo ci = new CurveInfo(analysisParameters,SpatialAnalysisResults.ORDERED_ROINAMES[k]);
						int[] roiIndices = nonZeroIndicesForROI.get(k);
						if(roiIndices != null && roiIndices.length > 0){
							double accum = 0.0;
							for (int index = 0; index < roiIndices.length; index++) {
								accum += data[roiIndices[index]];
							}
							double[] values = curveHash.get(ci);
							values[i] = accum/roiIndices.length;
						}
						double currentLen = i*varNames.length*SpatialAnalysisResults.ORDERED_ROINAMES.length + j*SpatialAnalysisResults.ORDERED_ROINAMES.length + k;
//						if(progressListener != null){progressListener.updateProgress(currentLen/totalLen);}
					}
				}
			}
		}
		SpatialAnalysisResults spatialAnalysisResults = new SpatialAnalysisResults(new AnalysisParameters[] {analysisParameters},shiftedSimTimes,curveHash);
		return spatialAnalysisResults;
	}
	
	public static void runFVSolverStandalone(
			File simulationDataDir,
			SessionLog sessionLog,
			Simulation sim,
			ExternalDataIdentifier imageDataExtDataID,
			ExternalDataIdentifier roiExtDataID,
			ClientTaskStatusSupport progressListener) throws Exception{
		runFVSolverStandalone(simulationDataDir, sessionLog, sim, imageDataExtDataID, roiExtDataID, progressListener, false);
	}
	
	public static void runFVSolverStandalone_ref(
		File simulationDataDir,
		SessionLog sessionLog,
		Simulation sim,
		ExternalDataIdentifier imageDataExtDataID,
		ExternalDataIdentifier roiExtDataID,
		ExternalDataIdentifier psfExtDataID,
		ClientTaskStatusSupport progressListener,
		boolean bCheckSteadyState) throws Exception{

		FieldFunctionArguments[] fieldFunctionArgs = sim.getMathDescription().getFieldFunctionArguments();
		FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = new FieldDataIdentifierSpec[fieldFunctionArgs.length];
		for (int i = 0; i < fieldDataIdentifierSpecs.length; i++) {
			if (fieldFunctionArgs[i].getFieldName().equals(imageDataExtDataID.getName())){
				fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],imageDataExtDataID);
			}else if (fieldFunctionArgs[i].getFieldName().equals(roiExtDataID.getName())){
				fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],roiExtDataID);
			}else if (fieldFunctionArgs[i].getFieldName().equals(psfExtDataID.getName())){
				fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],psfExtDataID);
			}else{
				throw new RuntimeException("failed to resolve field named "+fieldFunctionArgs[i].getFieldName());
			}
		}
		
		int jobIndex = 0;
		SimulationJob simJob = new SimulationJob(sim,jobIndex, fieldDataIdentifierSpecs);
		
		//FVSolverStandalone class expects the PropertyLoader.finiteVolumeExecutableProperty to exist
		System.setProperty(PropertyLoader.finiteVolumeExecutableProperty, LocalWorkspace.getFinitVolumeExecutableFullPathname());
		//if we need to check steady state, do the following two lines
		if(bCheckSteadyState)
		{
			simJob.getSimulation().getSolverTaskDescription().setStopAtSpatiallyUniform(true);
			simJob.getSimulation().getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-6, 1e-2));
		}
		
		FVSolverStandalone fvSolver = new FVSolverStandalone(simJob,simulationDataDir,sessionLog,false);		
		fvSolver.startSolver();
		
		SolverStatus status = fvSolver.getSolverStatus();
		while (status.getStatus() != SolverStatus.SOLVER_FINISHED && status.getStatus() != SolverStatus.SOLVER_ABORTED )
		{
			if(progressListener != null)
			{
				progressListener.setProgress((int)(fvSolver.getProgress()*100));
			}
			Thread.sleep(1000);
			status = fvSolver.getSolverStatus();
		}

		if(status.getStatus() == SolverStatus.SOLVER_FINISHED){
			String roiMeshFileName =
				SimulationData.createCanonicalMeshFileName(
					roiExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT, false);
			String imageDataMeshFileName =
				SimulationData.createCanonicalMeshFileName(
					imageDataExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT, false);
			String simulationMeshFileName =
				SimulationData.createCanonicalMeshFileName(
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
	
	public static void runFVSolverStandalone(
			File simulationDataDir,
			SessionLog sessionLog,
			Simulation sim,
			ExternalDataIdentifier imageDataExtDataID,
			ExternalDataIdentifier roiExtDataID,
			ClientTaskStatusSupport progressListener,
			boolean bCheckSteadyState) throws Exception{

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
			SimulationJob simJob = new SimulationJob(sim,jobIndex, fieldDataIdentifierSpecs);
			
			//FVSolverStandalone class expects the PropertyLoader.finiteVolumeExecutableProperty to exist
			System.setProperty(PropertyLoader.finiteVolumeExecutableProperty, LocalWorkspace.getFinitVolumeExecutableFullPathname());
			//if we need to check steady state, do the following two lines
			if(bCheckSteadyState)
			{
				simJob.getSimulation().getSolverTaskDescription().setStopAtSpatiallyUniform(true);
				simJob.getSimulation().getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-6, 1e-2));
			}
			
			FVSolverStandalone fvSolver = new FVSolverStandalone(simJob,simulationDataDir,sessionLog,false);		
			fvSolver.startSolver();
			
			SolverStatus status = fvSolver.getSolverStatus();
			while (status.getStatus() != SolverStatus.SOLVER_FINISHED && status.getStatus() != SolverStatus.SOLVER_ABORTED )
			{
				if(progressListener != null)
				{
					progressListener.setProgress((int)(fvSolver.getProgress()*100));
				}
				Thread.sleep(1000);
				status = fvSolver.getSolverStatus();
			}

			if(status.getStatus() == SolverStatus.SOLVER_FINISHED){
				String roiMeshFileName =
					SimulationData.createCanonicalMeshFileName(
						roiExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT, false);
				String imageDataMeshFileName =
					SimulationData.createCanonicalMeshFileName(
						imageDataExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT, false);
				String simulationMeshFileName =
					SimulationData.createCanonicalMeshFileName(
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
		File targetDir = new File(localWorkspace.getDefaultSimDataDirectory());
		ExternalDataIdentifier newImageDataExtDataID =
			new ExternalDataIdentifier(LocalWorkspace.createNewKeyValue(),
					LocalWorkspace.getDefaultOwner(),extDataIDName);
		ExternalDataInfo newImageDataExtDataInfo =
			new ExternalDataInfo(newImageDataExtDataID,
				new File(targetDir,newImageDataExtDataID.getID()+SimDataConstants.LOGFILE_EXTENSION).getAbsolutePath());
		return newImageDataExtDataInfo;
	}
	
	public static File[] getCanonicalExternalDataFiles(LocalWorkspace localWorkspace,ExternalDataIdentifier originalExtDataID){
		if(originalExtDataID != null){
			File userDir = new File(localWorkspace.getDefaultSimDataDirectory());
			File fdLogFile =
				new File(userDir,
						SimulationData.createCanonicalSimLogFileName(originalExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false));
			File fdMeshFile =
				new File(userDir,
						SimulationData.createCanonicalMeshFileName(originalExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false));
			File fdFunctionFile =
				new File(userDir,
						SimulationData.createCanonicalFunctionsFileName(originalExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false));
			File fdZipFile =
				new File(userDir,
						SimulationData.createCanonicalSimZipFileName(originalExtDataID.getKey(), 0,FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false));
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
	
	public ExternalDataInfo getFrapDataExternalDataInfo() {
		return frapDataExternalDataInfo;
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
		
	public void setModels(FRAPModel[] arg_models)
	{
		models = arg_models;
	}
	
	public BioModel getBioModel() {
		return bioModel;
	}
	/*
	 * for selected models
	 */
	public FRAPModel[] getModels()
	{
		return models;
	}
	
	public ArrayList<Integer> getSelectedModels()
	{
		ArrayList<Integer> result = new ArrayList<Integer>();
		if(models != null)
		{
			for(int i=0 ; i<FRAPModel.NUM_MODEL_TYPES; i++)
			{
				if(models[i] != null)
				{
					result.add(new Integer(i));
				}
			}
		}
		return result;
	}
	
	public void refreshModels(boolean[] modelBooleans)
	{
		for(int i =0; i<FRAPModel.NUM_MODEL_TYPES; i++)
		{
			if(modelBooleans[i])
			{
				if(models[i] == null)
				{
					models[i] = new FRAPModel(FRAPModel.MODEL_TYPE_ARRAY[i], null, null, null);
				}
			}
			else
			{
				models[i] = null;
			}
		}
	}
	
	public Integer getBestModelIndex() {
		return bestModelIndex;
	}


	public void setBestModelIndex(Integer bestModelIdx) 
	{
		Integer oldModelIndex  = this.bestModelIndex;
		this.bestModelIndex = bestModelIdx;
		propertyChangeSupport.firePropertyChange(FRAPSingleWorkspace.PROPERTY_CHANGE_BEST_MODEL, oldModelIndex, bestModelIdx);
	}
	
	
	
	
	public void refreshDependentROIs(){
		getFrapData().refreshDependentROIs();
	}
	
	public void  saveImageDatasetAsExternalData(LocalWorkspace localWorkspace,ExternalDataIdentifier newImageExtDataID,int startingIndexForRecovery) throws Exception{
		ImageDataset imageDataset = getFrapData().getImageDataset();
		if (imageDataset.getSizeC()>1){
			throw new RuntimeException("FRAPStudy.saveImageDatasetAsExternalData(): multiple channels not yet supported");
		}
		Extent extent = imageDataset.getExtent();
		ISize isize = imageDataset.getISize();
		int numImageToStore = imageDataset.getSizeT()-startingIndexForRecovery; //not include the prebleach 
    	double[][][] pixData = new double[numImageToStore][2][]; //original fluor data and back ground average
    	double[] timesArray = new double[numImageToStore];
    	double[] bgAvg = getFrapData().getAvgBackGroundIntensity();
    	
    	for (int tIndex = startingIndexForRecovery; tIndex < imageDataset.getSizeT(); tIndex++) {
    		short[] originalData = imageDataset.getPixelsZ(0, tIndex);// images according to zIndex at specific time points(tIndex)
    		double[] doubleData = new double[originalData.length];
    		double[] expandBgAvg = new double[originalData.length];
    		for(int i = 0; i < originalData.length; i++)
    		{
    			doubleData[i] = 0x0000ffff & originalData[i];
    			expandBgAvg[i] = bgAvg[tIndex];
    		}
    		pixData[tIndex-startingIndexForRecovery][0] = doubleData;
    		pixData[tIndex-startingIndexForRecovery][1] = expandBgAvg;
    		timesArray[tIndex-startingIndexForRecovery] = imageDataset.getImageTimeStamps()[tIndex]-imageDataset.getImageTimeStamps()[startingIndexForRecovery];
    	}
    	//changed in March 2008. Though biomodel is not created, we still let user save to xml file.
    	Origin origin = new Origin(0,0,0);
    	CartesianMesh cartesianMesh  = getCartesianMesh();
    	FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
    	fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
    	fdos.cartesianMesh = cartesianMesh;
    	fdos.doubleSpecData =  pixData;
    	fdos.specEDI = newImageExtDataID;
    	fdos.varNames = new String[] {"fluor","bg_average"};
    	fdos.owner = LocalWorkspace.getDefaultOwner();
    	fdos.times = timesArray;
    	fdos.variableTypes = new VariableType[] {VariableType.VOLUME,VariableType.VOLUME};
    	fdos.origin = origin;
    	fdos.extent = extent;
    	fdos.isize = isize;
		localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);
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
	//this method calculates prebleach average for each pixel at different time points. back ground has been subtracted.
	//should not subtract background from it when using it as a normalized factor.
	public static double[] calculatePreBleachAverageXYZ(FRAPData frapData,int startingIndexForRecovery){
    	long[] accumPrebleachImage = new long[frapData.getImageDataset().getISize().getXYZ()];//ISize: Image size including x, y, z. getXYZ()=x*y*z
    	double[] avgPrebleachDouble = new double[accumPrebleachImage.length];
    	double[] backGround = frapData.getAvgBackGroundIntensity();
    	double accumAvgBkGround = 0; //accumulate background before starting index for recovery. used to subtract back ground.
    	// changed in June, 2008 average prebleach depends on if there is prebleach images. 
    	// Since the initial condition is normalized by prebleach avg, we have to take care the divided by zero error.
		if(startingIndexForRecovery > 0){
			if(backGround != null)//subtract background
			{
				for (int timeIndex = 0; timeIndex < startingIndexForRecovery; timeIndex++) {
					short[] pixels = frapData.getImageDataset().getPixelsZ(0, timeIndex);//channel index is 0. it is not supported yet. get image size X*Y*Z stored by time index. image store by UShortImage[Z*T]
					for (int i = 0; i < accumPrebleachImage.length; i++) {
						accumPrebleachImage[i] += 0x0000ffff&pixels[i];
					}
					accumAvgBkGround += backGround[timeIndex];
				}
				for (int i = 0; i < avgPrebleachDouble.length; i++) {
					avgPrebleachDouble[i] = ((double)accumPrebleachImage[i] - accumAvgBkGround)/(double)startingIndexForRecovery;
				}
			}
			else //don't subtract background
			{
				for (int timeIndex = 0; timeIndex < startingIndexForRecovery; timeIndex++) {
					short[] pixels = frapData.getImageDataset().getPixelsZ(0, timeIndex);//channel index is 0. it is not supported yet. get image size X*Y*Z stored by time index. image store by UShortImage[Z*T]
					for (int i = 0; i < accumPrebleachImage.length; i++) {
						accumPrebleachImage[i] += 0x0000ffff&pixels[i];
					}
				}
				for (int i = 0; i < avgPrebleachDouble.length; i++) {
					avgPrebleachDouble[i] = (double)accumPrebleachImage[i]/(double)startingIndexForRecovery;
				}
			}
		}
		else{
			//if no prebleach image, use the last recovery image intensity as prebleach average.
			System.err.println("need to determine factor for prebleach average if no pre bleach images.");
			short[] pixels = frapData.getImageDataset().getPixelsZ(0, (frapData.getImageDataset().getSizeT() - 1));
			for (int i = 0; i < pixels.length; i++) {
				avgPrebleachDouble[i] = ((double)(0x0000ffff&pixels[i]) - backGround[frapData.getImageDataset().getSizeT() - 1]);
			}
		}
		//for each pixel if it's grater than 0, we add 1 offset to it. 
		//if it is smaller or equal to 0 , we set it to 1.
		for (int i = 0; i < avgPrebleachDouble.length; i++) {
			if(avgPrebleachDouble[i] <= FRAPOptimization.epsilon){
				avgPrebleachDouble[i] = 1;
			}
			else
			{
				avgPrebleachDouble[i]=avgPrebleachDouble[i] - FRAPOptimization.epsilon +1;
			}
		}
		return avgPrebleachDouble;
	}
	
	public static double calculatePrebleachAvg_oneValue(FRAPData arg_frapData,int arg_startingIndexForRecovery)
	{
		double accum = 0;
		int counter = 0;
		double[] prebleachAvgXYZ = calculatePreBleachAverageXYZ(arg_frapData, arg_startingIndexForRecovery);
		ROI cellROI = arg_frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		for(int i=0; i<prebleachAvgXYZ.length; i++)
		{
			if(cellROI.getPixelsXYZ()[i] != 0)
			{
				accum = accum + prebleachAvgXYZ[i];
				counter ++;
			}
		}
		return (accum/counter);
	}
	
	public void saveROIsAsExternalData(LocalWorkspace localWorkspace,ExternalDataIdentifier newROIExtDataID,int startingIndexForRecovery) throws Exception{
			ImageDataset imageDataset = getFrapData().getImageDataset();
			Extent extent = imageDataset.getExtent();
			ISize isize = imageDataset.getISize();
			int NumTimePoints = 1; 
			int NumChannels = 13;//actually it is total number of ROIs(cell,bleached + 8 rings)+prebleach+firstPostBleach+lastPostBleach
	    	double[][][] pixData = new double[NumTimePoints][NumChannels][]; // dimensions: time points, channels, whole image ordered by z slices. 
	    	

	    	double[] temp_background = getFrapData().getAvgBackGroundIntensity();
	    	double[] avgPrebleachDouble = calculatePreBleachAverageXYZ(getFrapData(),startingIndexForRecovery);
	    	
	    	pixData[0][0] = avgPrebleachDouble; // average of prebleach with background subtracted
	    	// first post-bleach with background subtracted
    		pixData[0][1] = createDoubleArray(getFrapData().getImageDataset().getPixelsZ(0, startingIndexForRecovery), temp_background[startingIndexForRecovery], true);
//    		adjustPrebleachAndPostbleachData(avgPrebleachDouble, pixData[0][1]);
    		// last post-bleach image (at last time point) with background subtracted
    		pixData[0][2] = createDoubleArray(getFrapData().getImageDataset().getPixelsZ(0, imageDataset.getSizeT()-1), temp_background[imageDataset.getSizeT()-1], true);
    		//below are ROIs, we don't need to subtract background for them.
    		pixData[0][3] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).getBinaryPixelsXYZ(1), 0, false);
    		pixData[0][4] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getBinaryPixelsXYZ(1), 0, false);
    		if (getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name()) == null){
    			//throw new RuntimeException("must first generate \"derived masks\"");
    			pixData[0][5] = new double[imageDataset.getISize().getXYZ()];
	    		pixData[0][6] = new double[imageDataset.getISize().getXYZ()];
	    		pixData[0][7] = new double[imageDataset.getISize().getXYZ()];
	    		pixData[0][8] = new double[imageDataset.getISize().getXYZ()];
	    		pixData[0][9] = new double[imageDataset.getISize().getXYZ()];
	    		pixData[0][10] = new double[imageDataset.getISize().getXYZ()];
	    		pixData[0][11] = new double[imageDataset.getISize().getXYZ()];
	    		pixData[0][12] = new double[imageDataset.getISize().getXYZ()];
    		}
    		else{
    			pixData[0][5] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name()).getBinaryPixelsXYZ(1), 0, false);
	    		pixData[0][6] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name()).getBinaryPixelsXYZ(1), 0, false);
	    		pixData[0][7] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name()).getBinaryPixelsXYZ(1), 0, false);
	    		pixData[0][8] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4.name()).getBinaryPixelsXYZ(1), 0, false);
	    		pixData[0][9] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5.name()).getBinaryPixelsXYZ(1), 0, false);
	    		pixData[0][10] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6.name()).getBinaryPixelsXYZ(1), 0, false);
	    		pixData[0][11] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7.name()).getBinaryPixelsXYZ(1), 0, false);
	    		pixData[0][12] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8.name()).getBinaryPixelsXYZ(1), 0, false);
    		}
    		CartesianMesh cartesianMesh = getCartesianMesh();
    		Origin origin = new Origin(0,0,0);
	    		    	
	    	FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
	    	fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
	    	fdos.cartesianMesh = cartesianMesh;
	    	fdos.doubleSpecData =  pixData;
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
	    	fdos.owner = LocalWorkspace.getDefaultOwner();
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
	}
	
	public DataProcessingInstructions getDataProcessInstructions(LocalWorkspace localWorkspace)
	{
		//create ROI image
		short[] roiFieldData = null;
		ROI[] rois = getFrapData().getRois();
		if(rois.length > 0)
		{
			Extent extent = rois[0].getRoiImages()[0].getExtent();
			ISize isize = rois[0].getISize();
			int numROIX = rois[0].getISize().getX();
			int numROIY = rois[0].getISize().getY();
			roiFieldData = new short[numROIX * numROIY];
			short regionCounter = 1;
			for(int roiIdx = FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.ordinal(); roiIdx<rois.length; roiIdx++)
			{
				short[] roiImg = rois[roiIdx].getPixelsXYZ();
				for(int pixelIdx=0; pixelIdx<(numROIX*numROIY); pixelIdx++)
				{
					if(roiImg[pixelIdx] > 0)
					{
						roiFieldData[pixelIdx] = regionCounter;
					}
				}
				regionCounter ++;
			}
			//create field data
			int NumTimePoints = 1;
			int NumChannels = 1; //8 rois integrated into 1 image
			short[][][] pixData = new short[NumTimePoints][NumChannels][];
			pixData[0][0] = roiFieldData;
			//get extental data id
			ExternalDataIdentifier newROIExtDataID = FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.ROI_SUMDATA_NAME).getExternalDataIdentifier();
			CartesianMesh cartesianMesh;
			try {
				cartesianMesh = getCartesianMesh();
			
	    		Origin origin = new Origin(0,0,0);
		    		    	
		    	FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
		    	fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
		    	fdos.cartesianMesh = cartesianMesh;
		    	fdos.shortSpecData =  pixData;
		    	fdos.specEDI = newROIExtDataID;
		    	fdos.varNames = new String[] {"roiSumDataVar"};
		    	fdos.owner = LocalWorkspace.getDefaultOwner();
		    	fdos.times = new double[] { 0.0 };
		    	fdos.variableTypes = new VariableType[] {VariableType.VOLUME};
		    	fdos.origin = origin;
		    	fdos.extent = extent;
		    	fdos.isize = isize;
		    	localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return DataProcessingInstructions.getVFrapInstructions(new int[] {0}/* volumePoints*/, 
					                                               new int[0]/* membranePoints*/,
					                                               regionCounter /*numRegions*/, 
					                                               0 /*zSlice*/, 
					                                               newROIExtDataID.getKey()/* fieldDataKey*/, 
					                                               new FieldFunctionArguments(FRAPStudy.ROI_SUMDATA_NAME, "roiSumDataVar", new Expression(0), VariableType.VOLUME)/*FieldFunctionArguments*/, 
					                                               false/* bStoreEnabled*/); 
					                                               
		}
		return null;
	}
	
	public static FieldDataIdentifierSpec getPSFFieldData(LocalWorkspace localWorkspace)
	{
		//create ROI image
		short[] psfFieldData = null;
		psfFieldData = new short[9];
		psfFieldData[4] = (short)1;
		
		//create field data
		int NumTimePoints = 1;
		int NumChannels = 1; //8 rois integrated into 1 image
		short[][][] pixData = new short[NumTimePoints][NumChannels][1];
		pixData[0][0] = psfFieldData;
		//get extental data id
		ExternalDataIdentifier newPsfExtDataID = FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.PSF_DATA_NAME).getExternalDataIdentifier();
		CartesianMesh cartesianMesh;
		try {
			Origin origin = new Origin(0,0,0);
			Extent ext =new Extent(1, 1, 1);
			ISize isize = new ISize(3, 3, 1);
			cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, ext, isize, new RegionImage( new VCImageUncompressed(null, new byte[isize.getXYZ()], ext, isize.getX(),isize.getY(),isize.getZ()),
					0,null,null,RegionImage.NO_SMOOTHING));	
    		
	    		    	
	    	FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
	    	fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
	    	fdos.cartesianMesh = cartesianMesh;
	    	fdos.shortSpecData =  pixData;
	    	fdos.specEDI = newPsfExtDataID;
	    	fdos.varNames = new String[] {"psfVar"};
	    	fdos.owner = LocalWorkspace.getDefaultOwner();
	    	fdos.times = new double[] { 0.0 };
	    	fdos.variableTypes = new VariableType[] {VariableType.VOLUME};
	    	fdos.origin = origin;
	    	fdos.extent = ext;
	    	fdos.isize = isize;
	    	localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);
	    	
	    	FieldFunctionArguments psfFieldFunc = new FieldFunctionArguments(
	    			PSF_DATA_NAME, "psfVar", new Expression(0.0), VariableType.VOLUME);
	    	
	    	FieldDataIdentifierSpec fdis = new FieldDataIdentifierSpec(psfFieldFunc, newPsfExtDataID);
	    	return fdis;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}					                                               
	}
	
	//when creating double array for firstPostBleach and last PostBleach, etc images
	//We'll clamp all pixel value <= 0 to 0 and add offset 1 to the whole image.
	//For ROI images, we don't have to do so.
	private double[] createDoubleArray(short[] shortData, double bkGround, boolean isOffset1ProcessNeeded){
		double[] doubleData = new double[shortData.length];
		for (int i = 0; i < doubleData.length; i++) {
			doubleData[i] = ((0x0000FFFF&shortData[i]) - bkGround);
			if(isOffset1ProcessNeeded)
			{
				if(doubleData[i] <= FRAPOptimization.epsilon)
				{
					doubleData[i] = 1;
				}
				else
				{
					doubleData[i] = doubleData[i] - FRAPOptimization.epsilon + 1;
				}
			}
		}
		return doubleData;
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
//		propertyChangeSupport.firePropertyChange("xmlFilename", oldValue, xmlFilename);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = this.name;
		this.name = name;
		propertyChangeSupport.firePropertyChange("name", oldValue, name);
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
			if(!Compare.isEqualOrNull(name, fStudy.name))
			{
				return false;
			}
			if(!Compare.isEqualOrNull(description, fStudy.description))
			{
				return false;
			}
			if(!Compare.isEqualOrNull(originalImageFilePath, fStudy.originalImageFilePath))
			{
				return false;
			}
			if(!this.getFrapData().compareEqual(fStudy.getFrapData()))
			{
				return false;
			}
			if(!Compare.isEqualOrNull(getBioModel(), fStudy.getBioModel()))
			{
				return false;
			}
			if(!Compare.isEqualOrNull(getFrapDataExternalDataInfo(),fStudy.getFrapDataExternalDataInfo()))
			{
				return false;
			}
			if (!Compare.isEqualOrNull(getRoiExternalDataInfo(),fStudy.getRoiExternalDataInfo()))
			{
				return false;
			}
			if (!Compare.isEqualOrNull(getStoredRefData(),fStudy.getStoredRefData()))
			{
				return false;
			}
			return true;
		}

		return false;
	}

	public FRAPModel getFrapModel(int modelType)
	{
		if(modelType > -1 && modelType < FRAPModel.NUM_MODEL_TYPES)
		{
			return models[modelType];
		}
		return null;
	}
	
	public SimpleReferenceData getStoredRefData() {
		return storedRefData;
	}

	public void setStoredRefData(SimpleReferenceData storedRefData) {
		this.storedRefData = storedRefData;
	}
	
	public FRAPOptData getFrapOptData() {
		return frapOptData;
	}


	public void setFrapOptData(FRAPOptData frapOptData) {
		this.frapOptData = frapOptData;
	}

	public Integer getStartingIndexForRecovery() {
		return startingIndexForRecovery;
	}


	public void setStartingIndexForRecovery(int startingIndexForRecovery) {
		this.startingIndexForRecovery = new Integer(startingIndexForRecovery);
	}
	
	public void setSelectedROIsForErrorCalculation(boolean[] arg_selectedROIs)
	{
		selectedROIsForErrCalculation = arg_selectedROIs;
	}
	
	public boolean[] getSelectedROIsForErrorCalculation()
	{
		return selectedROIsForErrCalculation;
	}
	
	public boolean hasOptModel()
	{
		if(getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] != null)
		{
			return true;
		}
		if(getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] != null)
		{
			return true;
		}
		return false;
	}
	
	public double[][] getAnalysisMSESummaryData() {
		return analysisMSESummaryData;
	}

	public void setAnalysisMSESummaryData(double[][] analysisMSESummaryData) {
		this.analysisMSESummaryData = analysisMSESummaryData;
	}
	
	//returns the dimension reduced exp data. If it is null, create one.
	public double[][] getDimensionReducedExpData() {
		if(dimensionReducedExpData == null)
		{
			int startRecoveryIndex = getStartingIndexForRecovery();
			double[] prebleachAvg = FRAPStudy.calculatePreBleachAverageXYZ(getFrapData(), startRecoveryIndex);
			dimensionReducedExpData = FRAPOptimization.dataReduction(getFrapData(),startRecoveryIndex, getFrapData().getRois(), prebleachAvg);
		}
		return dimensionReducedExpData;
	}

	public void setDimensionReducedExpData(double[][] dimensionReducedExpData) {
		this.dimensionReducedExpData = dimensionReducedExpData;
	}
		
	public boolean isSaveNeeded() {
		return bSaveNeeded;
	}

	public void setSaveNeeded(boolean bNeedSave) {
		this.bSaveNeeded = bNeedSave;
	}
		
	public boolean areFRAPModelsEqual(FRAPModel[] arg_frapModels)
	{
		if((getModels() != null && arg_frapModels == null) ||
		   (getModels() == null && arg_frapModels != null))
		{
			return false;
		}
		else 
		{
			for(int i=0; i<FRAPModel.NUM_MODEL_TYPES; i++)
			{
				if(!Compare.isEqualOrNull(getModels()[i], arg_frapModels[i]))
				{
					return false;
				}
			}
			
		}
		return true;
	}
	
	public void createAnalysisMSESummaryData()
	{
		double[][] sumData = new double[FRAPModel.NUM_MODEL_TYPES][getFrapData().getROILength()-2+1];
		
		//get dimension reduced exp data
		double[][] expData = getDimensionReducedExpData();
		//calculate summary data
		for(int i=0; i < FRAPModel.NUM_MODEL_TYPES; i++)
		{
			//fill all elements with 1e8 first
			Arrays.fill(sumData[i], FRAPOptimization.largeNumber);
			
			if(getFrapModel(i) != null && getFrapModel(i).getData() != null)
			{
				sumData[i]=calculateMSEForEachModel(expData, getFrapModel(i).getData());
			}
		}
		setAnalysisMSESummaryData(sumData);
	}
	
	//called by createAnalysisMSESummaryData, calculate MSE for one frap model
	private double[] calculateMSEForEachModel(double[][] expData, double[][] simData)
	{
		double[] result = new double[getFrapData().getROILength()-2+1];//len: all ROIS except cellROI and bkgroundROI, plus a sum of error field
		//fill all elements with 1e8 first
		Arrays.fill(result, FRAPOptimization.largeNumber);
		
		boolean[] selectedROIS = getSelectedROIsForErrorCalculation();
		double sumError = 0;
		for(int i=0; i<FRAPData.VFRAP_ROI_ENUM.values().length; i++)
		{
			if(FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED))
			{
				if(selectedROIS[i])
				{
					result[i] = calculateMSEForEachROI(expData[i], simData[i]);
					sumError = sumError + result[i];
				}
			}
			else if(FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1) ||
					FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2) ||
					FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3) ||
					FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4) ||
					FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5) ||
					FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6) ||
					FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7) ||
					FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8))
			{
				if(selectedROIS[i])
				{
					result[i-2] = calculateMSEForEachROI(expData[i], simData[i]);
					sumError = sumError + result[i-2];
				}
			}
		}
		
		result[result.length -1] = sumError;
		
		return result;
	}
	
	//called by calculateMSEForEachModel, calculate MSE for each model under certain ROI
	private double calculateMSEForEachROI(double[] expData, double[] simData)
	{
		int len = Math.min(expData.length, simData.length);
		double error = 0;
		for(int i=0; i<len; i++)
		{
			double diff = expData[i]-simData[i];
			error = error + diff*diff;
		}
//		double result = Math.sqrt(error/(len-1));
		double result = error;
		
		return result;
	}
}
