/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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

import org.vcell.optimization.ProfileData;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Compare;
import org.vcell.util.Extent;
import org.vcell.util.FileUtils;
import org.vcell.util.ISize;
import org.vcell.util.Issue;
import org.vcell.util.Matchable;
import org.vcell.util.Origin;
import org.vcell.util.SessionLog;
import org.vcell.util.UserCancelException;
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
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.VariableType;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.server.FrapDataUtils;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.parser.Expression;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.FVSolverStandalone;

public class FRAPStudy implements Matchable{
	public static final String EXTRACELLULAR_NAME = "extracellular";
	public static final String PLASMAMEMBRANE_NAME = "plasmaMembrane";
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
	//Added in August 2010, store the profile data for confidence intervals of the estimates
	private ProfileData[] profileData_oneDiffComponent = null;
	private ProfileData[] profileData_twoDiffComponents = null;
	private ProfileData[] profileData_reactionOffRate = null;
	
	//models
	private FRAPModel[] models = new FRAPModel[FRAPModel.NUM_MODEL_TYPES];
	private Integer bestModelIndex = null;
	
	//Selected ROIs for error calculation, length 11, according to the order in FRAPData.VFRAP_ROI_ENUM
	private boolean[] selectedROIsForErrCalculation = null; 
	
	PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	//to store movie file info. movie file will be refreshed after each simulation run.
	private transient String movieURLString = null;
	private transient String movieFileString = null;
	//temporary data structure for reference data used in optimization
	private transient FRAPOptData frapOptData = null;
	//temporary data structure for optimization with explicit functions
	private transient FRAPOptFunctions frapOptFunc = null;
	//temporary data structure to store MSE for different models under different ROIs. (may not be all the models with all ROIs)
	//first dimentsion: 3 models, second dimension: bleahed + 8 Rings + sum of MSE, any element that is not applicable should fill with 1e8.
	private transient double[][] analysisMSESummaryData = null; 
	//temporary data structure to store dimension reduced experimental data (all ROIs)
	private transient double[][] dimensionReducedExpData = null;
	//temporary data structure to store reduced experimental time points (take recovery start index as time 0)
	private transient double[] reducedExpTimePoints = null;
	//temporary data structure to identify whether the current frapStudy needs a save or not
	private transient boolean bSaveNeeded = false;
	
	
	//static functions
	public static void removeExternalDataAndSimulationFiles(
			KeyValue simulationKeyValue,
			ExternalDataIdentifier frapDataExtDataId,ExternalDataIdentifier roiExtDataId,
			LocalWorkspace localWorkspace) throws Exception
	{
		removeSimulationFiles(simulationKeyValue, localWorkspace);
		removeExternalFiles(frapDataExtDataId, roiExtDataId, localWorkspace);
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

		TimeBounds timeBounds = FRAPOptData.getEstimatedRefTimeBound(sourceFrapStudy);
		double timeStepVal = FRAPOptData.REFERENCE_DIFF_DELTAT;
		
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
		Feature extracellular = model.addFeature(EXTRACELLULAR_NAME);
		Feature cytosol = model.addFeature(CYTOSOL_NAME);
		Membrane plasmaMembrane = model.addMembrane(PLASMAMEMBRANE_NAME);

		String roiDataName = FRAPStudy.ROI_EXTDATA_NAME;
		
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
				new SpeciesContext(null,species[MOBILE_SPECIES_INDEX].getCommonName(),species[MOBILE_SPECIES_INDEX],cytosol);
		FieldFunctionArguments postBleach_first = new FieldFunctionArguments(roiDataName,"postbleach_first", new Expression(0), VariableType.VOLUME);
		FieldFunctionArguments prebleach_avg = new FieldFunctionArguments(roiDataName,"prebleach_avg", new Expression(0), VariableType.VOLUME);
		Expression expPostBleach_first = new Expression(postBleach_first.infix());
		Expression expPreBleach_avg = new Expression(prebleach_avg.infix());
		initialConditions[MOBILE_SPECIES_INDEX] = Expression.div(expPostBleach_first, expPreBleach_avg);
		
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
		
		
		for (int i = 0; i < initialConditions.length; i++) {
			model.addSpecies(species[i]);
			model.addSpeciesContext(speciesContexts[i]);
		}
		
		for (int i = 0; i < speciesContexts.length; i++) {
			SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(speciesContexts[i]);
			scs.getInitialConditionParameter().setExpression(initialConditions[i]);
			scs.getDiffusionParameter().setExpression(diffusionConstants[i]);
		}
		MathMapping mathMapping = simContext.createNewMathMapping();
		MathDescription mathDesc = mathMapping.getMathDescription();
		
		//Add PSF function
		
		mathDesc.addVariable(new Function(Simulation.PSF_FUNCTION_NAME, new Expression(psfFDIS.getFieldFuncArgs().infix()), null));
		
		simContext.setMathDescription(mathDesc);
				
		SimulationVersion simVersion = new SimulationVersion(simKey,"sim1",owner,new GroupAccessNone(),new KeyValue("0"),new BigDecimal(0),new Date(),VersionFlag.Current,"",null);
		Simulation newSimulation = new Simulation(simVersion,simContext.getMathDescription());
		newSimulation.getSolverTaskDescription().setSolverDescription(SolverDescription.FiniteVolumeStandalone);
		simContext.addSimulation(newSimulation);
		newSimulation.getSolverTaskDescription().setTimeBounds(timeBounds);
		newSimulation.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(timeStepVal));
		newSimulation.getMeshSpecification().setSamplingSize(cellROI_2D.getISize());
		newSimulation.getSolverTaskDescription().setTimeStep(new TimeStep(timeStepVal, timeStepVal, timeStepVal));
		
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
		if(fimm < FRAPOptimizationUtils.epsilon && fimm > (0 - FRAPOptimizationUtils.epsilon))
		{
			fimm = 0;
		}
		if(fimm < (1+FRAPOptimizationUtils.epsilon) && fimm > (1 - FRAPOptimizationUtils.epsilon))
		{
			fimm = 1;
		}

		Extent extent = sourceFrapStudy.getFrapData().getImageDataset().getExtent();

		double[] timeStamps = sourceFrapStudy.getFrapData().getImageDataset().getImageTimeStamps();
		TimeBounds timeBounds = new TimeBounds(0.0,timeStamps[timeStamps.length-1]-timeStamps[startingIndexForRecovery]);
		double timeStepVal = timeStamps[startingIndexForRecovery+1] - timeStamps[startingIndexForRecovery];
		
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
		model.addFeature(EXTRACELLULAR_NAME);
		Feature extracellular = (Feature)model.getStructure(EXTRACELLULAR_NAME);
		model.addFeature(CYTOSOL_NAME);
		Feature cytosol = (Feature)model.getStructure(CYTOSOL_NAME);
//		Membrane mem = model.addMembrane(EXTRACELLULAR_CYTOSOL_MEM_NAME);
//		model.getStructureTopology().setInsideFeature(mem, cytosol);
//		model.getStructureTopology().setOutsideFeature(mem, extracellular);
		

		String roiDataName = FRAPStudy.ROI_EXTDATA_NAME;
		
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
				
		//total initial condition
		FieldFunctionArguments postBleach_first = new FieldFunctionArguments(roiDataName,"postbleach_first", new Expression(0), VariableType.VOLUME);
		FieldFunctionArguments prebleach_avg = new FieldFunctionArguments(roiDataName,"prebleach_avg", new Expression(0), VariableType.VOLUME);
		Expression expPostBleach_first = new Expression(postBleach_first.infix());
		Expression expPreBleach_avg = new Expression(prebleach_avg.infix());
		Expression totalIniCondition = Expression.div(expPostBleach_first, expPreBleach_avg);
		//Free Species
		diffusionConstants[FREE_SPECIES_INDEX] = new Expression(df);
		species[FREE_SPECIES_INDEX] = new Species(FRAPStudy.SPECIES_NAME_PREFIX_MOBILE,	"Mobile bleachable species");
		speciesContexts[FREE_SPECIES_INDEX] = new SpeciesContext(null,species[FREE_SPECIES_INDEX].getCommonName(),species[FREE_SPECIES_INDEX],cytosol);
		initialConditions[FREE_SPECIES_INDEX] = Expression.mult(new Expression(ff), totalIniCondition);
		
		//Immobile Species (No diffusion)
		//Set very small diffusion rate on immobile to force evaluation as state variable (instead of FieldData function)
		//If left as a function errors occur because functions involving FieldData require a database connection
		final String IMMOBILE_DIFFUSION_KLUDGE = "1e-14";
		diffusionConstants[IMMOBILE_SPECIES_INDEX] = new Expression(IMMOBILE_DIFFUSION_KLUDGE);
		species[IMMOBILE_SPECIES_INDEX] = new Species(FRAPStudy.SPECIES_NAME_PREFIX_IMMOBILE,"Immobile bleachable species");
		speciesContexts[IMMOBILE_SPECIES_INDEX] = new SpeciesContext(null,species[IMMOBILE_SPECIES_INDEX].getCommonName(),species[IMMOBILE_SPECIES_INDEX],cytosol);
		initialConditions[IMMOBILE_SPECIES_INDEX] = Expression.mult(new Expression(fimm), totalIniCondition);

		//BS Species
		diffusionConstants[BS_SPECIES_INDEX] = new Expression(IMMOBILE_DIFFUSION_KLUDGE);
		species[BS_SPECIES_INDEX] = new Species(FRAPStudy.SPECIES_NAME_PREFIX_BINDING_SITE,"Binding Site species");
		speciesContexts[BS_SPECIES_INDEX] = new SpeciesContext(null,species[BS_SPECIES_INDEX].getCommonName(),species[BS_SPECIES_INDEX],cytosol);
		initialConditions[BS_SPECIES_INDEX] = Expression.mult(new Expression(bs), totalIniCondition);
	
		//Complex species
		diffusionConstants[COMPLEX_SPECIES_INDEX] = new Expression(dc);
		species[COMPLEX_SPECIES_INDEX] = new Species(FRAPStudy.SPECIES_NAME_PREFIX_SLOW_MOBILE, "Slower mobile bleachable species");
		speciesContexts[COMPLEX_SPECIES_INDEX] = new SpeciesContext(null,species[COMPLEX_SPECIES_INDEX].getCommonName(),species[COMPLEX_SPECIES_INDEX],cytosol);
		initialConditions[COMPLEX_SPECIES_INDEX] = Expression.mult(new Expression(fc), totalIniCondition);
	
		// add reactions to species if there is bleachWhileMonitoring rate.
		for (int i = 0; i < initialConditions.length; i++) {
			model.addSpecies(species[i]);
			model.addSpeciesContext(speciesContexts[i]);
			//reaction with BMW rate, which should not be applied to binding site
			if(!(species[i].getCommonName().equals(FRAPStudy.SPECIES_NAME_PREFIX_BINDING_SITE)))
			{
				SimpleReaction simpleReaction = new SimpleReaction(model, cytosol,speciesContexts[i].getName()+"_bleach", true);
				model.addReactionStep(simpleReaction);
				simpleReaction.addReactant(speciesContexts[i], 1);
				MassActionKinetics massActionKinetics = new MassActionKinetics(simpleReaction);
				simpleReaction.setKinetics(massActionKinetics);
				KineticsParameter kforward = massActionKinetics.getForwardRateParameter();
				simpleReaction.getKinetics().setParameterValue(kforward, new Expression(new Double(bwmRate)));
			}
		}

		// add the binding reaction: F + BS <-> C
		SimpleReaction simpleReaction2 = new SimpleReaction(model, cytosol,"reac_binding", true);
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
		FeatureMapping extracellularFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(extracellular);
//		Membrane plasmaMembrane = model.getStructureTopology().getMembrane(cytosol, extracellular);
//		MembraneMapping plasmaMembraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(plasmaMembrane);
		
		SubVolume cytSubVolume = geometry.getGeometrySpec().getSubVolume(CYTOSOL_NAME);
		SubVolume exSubVolume = geometry.getGeometrySpec().getSubVolume(EXTRACELLULAR_NAME);
		SurfaceClass pmSurfaceClass = geometry.getGeometrySurfaceDescription().getSurfaceClass(exSubVolume, cytSubVolume);
		
		cytosolFeatureMapping.setGeometryClass(cytSubVolume);
		extracellularFeatureMapping.setGeometryClass(exSubVolume);
//		plasmaMembraneMapping.setGeometryClass(pmSurfaceClass);
		
		cytosolFeatureMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		extracellularFeatureMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
//		plasmaMembraneMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		
		for (int i = 0; i < speciesContexts.length; i++) {
			SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(speciesContexts[i]);
			scs.getInitialConditionParameter().setExpression(initialConditions[i]);
			scs.getDiffusionParameter().setExpression(diffusionConstants[i]);
		}

		MathMapping mathMapping = simContext.createNewMathMapping();
		MathDescription mathDesc = mathMapping.getMathDescription();
		//Add total fluorescence as function of mobile(optional: and slower mobile) and immobile fractions
		mathDesc.addVariable(new Function(FRAPStudy.SPECIES_NAME_PREFIX_COMBINED,
					             new Expression(species[FREE_SPECIES_INDEX].getCommonName()+"+"+species[COMPLEX_SPECIES_INDEX].getCommonName()+"+"+species[IMMOBILE_SPECIES_INDEX].getCommonName()), null));
		simContext.setMathDescription(mathDesc);

		SimulationVersion simVersion = new SimulationVersion(simKey,"sim1",owner,new GroupAccessNone(),new KeyValue("0"),new BigDecimal(0),new Date(),VersionFlag.Current,"",null);
		Simulation newSimulation = new Simulation(simVersion,mathDesc);
		simContext.addSimulation(newSimulation);
		newSimulation.getSolverTaskDescription().setTimeBounds(timeBounds);
		newSimulation.getMeshSpecification().setSamplingSize(cellROI_2D.getISize());
//		newSimulation.getSolverTaskDescription().setTimeStep(timeStep); // Sundials doesn't need time step
		newSimulation.getSolverTaskDescription().setSolverDescription(SolverDescription.SundialsPDE);
		newSimulation.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(timeStepVal));//use exp time step as output time spec
		
		return bioModel;
	}
	//used in EstParams_ReactionOffRatePanel for showing selected ROIs
	public static boolean[] createSelectedROIsForReactionOffRateModel()
	{
		boolean[] selectedROIs = new boolean[FRAPData.VFRAP_ROI_ENUM.values().length];
		int bleachedIdx = 0;
		for(FRAPData.VFRAP_ROI_ENUM enu: FRAPData.VFRAP_ROI_ENUM.values())
		{
			if(enu.equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED))
			{
				break;
			}
			else
			{
				bleachedIdx ++;
			}
		}
		selectedROIs[bleachedIdx] = true;
		
		return selectedROIs;
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

		FieldFunctionArguments[] fieldFunctionArgs = FieldUtilities.getFieldFunctionArguments(sim.getMathDescription());
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
		SimulationTask simTask = new SimulationTask(new SimulationJob(sim,jobIndex, fieldDataIdentifierSpecs),0);
		SolverUtilities.prepareSolverExecutable(sim.getSolverTaskDescription().getSolverDescription());
		//if we need to check steady state, do the following two lines
		if(bCheckSteadyState)
		{
			simTask.getSimulation().getSolverTaskDescription().setStopAtSpatiallyUniformErrorTolerance(ErrorTolerance.getDefaultSpatiallyUniformErrorTolerance());
//			simJob.getSimulation().getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-6, 1e-2));
		}
		
		FVSolverStandalone fvSolver = new FVSolverStandalone(simTask,simulationDataDir,sessionLog,false);		
		fvSolver.startSolver();
		
		SolverStatus status = fvSolver.getSolverStatus();
		while (status.getStatus() != SolverStatus.SOLVER_FINISHED && status.getStatus() != SolverStatus.SOLVER_ABORTED )
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
	
	public static void runFVSolverStandalone(
			File simulationDataDir,
			SessionLog sessionLog,
			Simulation sim,
			ExternalDataIdentifier imageDataExtDataID,
			ExternalDataIdentifier roiExtDataID,
			ClientTaskStatusSupport progressListener,
			boolean bCheckSteadyState) throws Exception{

			FieldFunctionArguments[] fieldFunctionArgs = FieldUtilities.getFieldFunctionArguments(sim.getMathDescription());
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
			SimulationTask simTask = new SimulationTask(new SimulationJob(sim,jobIndex, fieldDataIdentifierSpecs),0);
			//if we need to check steady state, do the following two lines
			if(bCheckSteadyState)
			{
				simTask.getSimulation().getSolverTaskDescription().setStopAtSpatiallyUniformErrorTolerance(ErrorTolerance.getDefaultSpatiallyUniformErrorTolerance());
				simTask.getSimulation().getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-6, 1e-2));
			}
			
			SolverUtilities.prepareSolverExecutable(sim.getSolverTaskDescription().getSolverDescription());
			
			FVSolverStandalone fvSolver = new FVSolverStandalone(simTask,simulationDataDir,sessionLog,false);		
			fvSolver.startSolver();
			
			SolverStatus status = fvSolver.getSolverStatus();
			while (status.getStatus() != SolverStatus.SOLVER_FINISHED && status.getStatus() != SolverStatus.SOLVER_ABORTED )
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
						SimulationData.createCanonicalSimZipFileName(originalExtDataID.getKey(), 0,FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false,false));
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
	//refresh selected models and reset best model index(to null, if models have been changed)
	public void refreshModels(boolean[] modelBooleans)
	{
		boolean isSelectedModelChanged = false;
		for(int i =0; i<FRAPModel.NUM_MODEL_TYPES; i++)
		{
			if(modelBooleans[i])
			{
				if(models[i] == null)
				{
					models[i] = new FRAPModel(FRAPModel.MODEL_TYPE_ARRAY[i], null, null, null);
					isSelectedModelChanged = true;
				}
			}
			else
			{
				if(models[i] != null)
				{
					isSelectedModelChanged = true;
				}
				models[i] = null;
			}
		}
		if(isSelectedModelChanged)
		{
			setBestModelIndex(null);
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
		FrapDataUtils.saveImageDatasetAsExternalData(getFrapData(), localWorkspace, newImageExtDataID, startingIndexForRecovery, getCartesianMesh());
	}
	
	public CartesianMesh getCartesianMesh() throws Exception{
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
	public static double calculatePrebleachAvg_oneValue(FRAPData arg_frapData,int arg_startingIndexForRecovery)
	{
		double accum = 0;
		int counter = 0;
		double[] prebleachAvgXYZ = FrapDataUtils.calculatePreBleachAverageXYZ(arg_frapData, arg_startingIndexForRecovery);
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
		FrapDataUtils.saveROIsAsExternalData(getFrapData(), localWorkspace, newROIExtDataID, startingIndexForRecovery, getCartesianMesh());
	}
	
	public ROIDataGenerator getROIDataGenerator(LocalWorkspace localWorkspace)
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
			return new ROIDataGenerator(ROI_EXTDATA_NAME, /*name*/
					                    new int[] {0}/* volumePoints*/, 
					                                               new int[0]/* membranePoints*/,
					                                               regionCounter /*numRegions*/, 
					                                               0 /*zSlice*/, 
					                                               newROIExtDataID.getKey()/* fieldDataKey, sample image*/, 
					                                               new FieldFunctionArguments(FRAPStudy.ROI_SUMDATA_NAME, "roiSumDataVar", new Expression(0), VariableType.VOLUME)/*FieldFunctionArguments, sample image*/,
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

	public String getMovieURLString() {
		return movieURLString;
	}

	public void setMovieURLString(String movieURLString) {
		this.movieURLString = movieURLString;
	}

	public String getMovieFileString() {
		return movieFileString;
	}

	public void setMovieFileString(String movieFileString) {
		this.movieFileString = movieFileString;
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
		this.xmlFilename = xmlFilename;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	public FRAPOptFunctions getFrapOptFunc() {
		return frapOptFunc;
	}

	public void setFrapOptFunc(FRAPOptFunctions frapOptFunc) {
		this.frapOptFunc = frapOptFunc;
	}
	
	public ProfileData[] getProfileData_oneDiffComponent() {
		return profileData_oneDiffComponent;
	}


	public void setProfileData_oneDiffComponent(ProfileData[] profileData) {
		this.profileData_oneDiffComponent = profileData;
	}
	
	public ProfileData[] getProfileData_twoDiffComponents() {
		return profileData_twoDiffComponents;
	}


	public void setProfileData_twoDiffComponents(ProfileData[] profileData) {
		this.profileData_twoDiffComponents = profileData;
	}
	
	public ProfileData[] getProfileData_reactionOffRate() {
		return profileData_reactionOffRate;
	}

	public void setProfileData_reactionOffRate(ProfileData[] profileData) {
		profileData_reactionOffRate = profileData;
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
	
	public int getNumSelectedROIs()
	{
		int numSelectedROIs = 0;
		for(boolean bSelected:selectedROIsForErrCalculation)
		{
			if(bSelected)
			{
				numSelectedROIs++;
			}
		}
		return numSelectedROIs;
	}
	
	public boolean isROISelectedForErrorCalculation(String roiName)
	{
		int roiIdx = 0;
		for(FRAPData.VFRAP_ROI_ENUM roiEnum:FRAPData.VFRAP_ROI_ENUM.values())
		{
			if(!roiName.equals(roiEnum.name()))
			{
				roiIdx++;
			}
			else
			{
				break;
			}
		}
		return selectedROIsForErrCalculation[roiIdx];
	}
	
	public boolean hasDiffusionOnlyModel()
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
	
	public int getNumDiffusionOnlyModels()
	{
		int numDiffOnlyModels = 0;
		if(getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] != null)
		{
			numDiffOnlyModels ++;
		}
		if(getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] != null)
		{
			numDiffOnlyModels ++;
		}
		return numDiffOnlyModels;
	}
	
	public boolean hasReactionOnlyOffRateModel()
	{
		if(getModels()[FRAPModel.IDX_MODEL_REACTION_OFF_RATE] != null)
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
			double[] prebleachAvg = FrapDataUtils.calculatePreBleachAverageXYZ(getFrapData(), startRecoveryIndex);
			dimensionReducedExpData = FRAPOptimizationUtils.dataReduction(getFrapData(),startRecoveryIndex, getFrapData().getRois(), prebleachAvg);
		}
		return dimensionReducedExpData;
	}

	public void setDimensionReducedExpData(double[][] dimensionReducedExpData) {
		this.dimensionReducedExpData = dimensionReducedExpData;
	}
	
	public double[] getReducedExpTimePoints() {
		if(reducedExpTimePoints == null)
		{
			int startRecoveryIndex = getStartingIndexForRecovery();
			reducedExpTimePoints = FRAPOptimizationUtils.timeReduction(getFrapData().getImageDataset().getImageTimeStamps(), startRecoveryIndex); 
		}
		return reducedExpTimePoints;
	}
	
	public void setReducedExpTimePoints(double[] reducedExpTimePoints)
	{
		this.reducedExpTimePoints = reducedExpTimePoints;
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
	//the summary is for errors of different models under ROIs
	public void createAnalysisMSESummaryData()
	{
		if(getFrapOptData() != null || getFrapOptFunc() != null)
		{
			double[][] sumData = new double[FRAPModel.NUM_MODEL_TYPES][getFrapData().getROILength()-2+1];//len: all ROIS except cellROI and bkgroundROI, plus a sum of error field
			//fill sumData with big numbers
			for(int i=0; i < FRAPModel.NUM_MODEL_TYPES; i++)
			{
				Arrays.fill(sumData[i], FRAPOptimizationUtils.largeNumber);
			}
			//get dimension reduced exp data
			double[][] expData = getDimensionReducedExpData();
			//calculate summary data for diffusion only models
			if(getFrapOptData() != null)
			{
				for(int i=0; i < FRAPModel.NUM_MODEL_TYPES; i++)
				{
					if((i == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT || i == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS) &&
						getFrapModel(i) != null && getFrapModel(i).getData() != null)
					{
						sumData[i]=calculateMSE_OneModel(expData, getFrapModel(i).getData(), i);
					}
				}
			}
			if(getFrapOptFunc() != null)
			{
				if(getFrapModel(FRAPModel.IDX_MODEL_REACTION_OFF_RATE) != null && getFrapModel(FRAPModel.IDX_MODEL_REACTION_OFF_RATE).getData() != null)
				{
					sumData[FRAPModel.IDX_MODEL_REACTION_OFF_RATE]=calculateMSE_OneModel(expData, getFrapModel(FRAPModel.IDX_MODEL_REACTION_OFF_RATE).getData(), FRAPModel.IDX_MODEL_REACTION_OFF_RATE);
				}
			}
			setAnalysisMSESummaryData(sumData);
		}
		else
		{
			setAnalysisMSESummaryData(null);
		}
	}
	
	//called by createAnalysisMSESummaryData, calculate MSE for one frap model
	private double[] calculateMSE_OneModel(double[][] expData, double[][] simData, int modelType)
	{
		double[] result = new double[getFrapData().getROILength()-2+1];//len: all ROIS except cellROI and bkgroundROI, plus a sum of error field
		//fill all elements with 1e8 first
		Arrays.fill(result, FRAPOptimizationUtils.largeNumber);
		
		boolean[] selectedROIS = getSelectedROIsForErrorCalculation();
		int noSelectedROIs = 0;
		double sumError = 0;
		for(int i=0; i<FRAPData.VFRAP_ROI_ENUM.values().length; i++)
		{
			if(FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED))
			{
				if(selectedROIS[i])
				{
					result[i] = calculateMSEForEachROI(expData[i], simData[i]);
					sumError = sumError + result[i];
					noSelectedROIs ++;
				}
			}
			else if((modelType != FRAPModel.IDX_MODEL_REACTION_OFF_RATE) && 
					(FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1) ||
					 FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2) ||
					 FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3) ||
					 FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4) ||
					 FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5) ||
					 FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6) ||
					 FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7) ||
					 FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8)))
			{
				if(selectedROIS[i])
				{
					result[i-2] = calculateMSEForEachROI(expData[i], simData[i]);
					sumError = sumError + result[i-2];
					noSelectedROIs ++;
				}
			}
		}
		if(hasReactionOnlyOffRateModel())//if has reaction off rate model, use error under bleached region to compare
		{
			result[result.length -1] = result[0];
		}
		else//if no reaction off rate model involved, use average error to compare
		{
			if(noSelectedROIs > 0)
			{
				result[result.length -1] = sumError/noSelectedROIs;
			}
		}
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
