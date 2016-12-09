/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solvers;
import java.beans.PropertyVetoException;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.vcell.chombo.ChomboSolverSpec;
import org.vcell.chombo.RefinementRoi;
import org.vcell.chombo.RefinementRoi.RoiType;
import org.vcell.chombo.TimeInterval;
import org.vcell.solver.smoldyn.SmoldynFileWriter;
import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.NullSessionLog;
import org.vcell.util.Origin;
import org.vcell.util.PropertyLoader;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.image.VCPixelClass;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.surface.DistanceMapGenerator;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.RayCaster;
import cbit.vcell.geometry.surface.SubvolumeSignedDistanceMap;
import cbit.vcell.mapping.FastSystemAnalyzer;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.ConvolutionDataGenerator;
import cbit.vcell.math.ConvolutionDataGenerator.ConvolutionDataGeneratorKernel;
import cbit.vcell.math.ConvolutionDataGenerator.GaussianConvolutionDataGeneratorKernel;
import cbit.vcell.math.DataGenerator;
import cbit.vcell.math.Distribution;
import cbit.vcell.math.Equation;
import cbit.vcell.math.FastSystem;
import cbit.vcell.math.FilamentVariable;
import cbit.vcell.math.GaussianDistribution;
import cbit.vcell.math.JumpCondition;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneParticleVariable;
import cbit.vcell.math.MembraneRandomVariable;
import cbit.vcell.math.MembraneRegionEquation;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.ParameterVariable;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.PdeEquation.BoundaryConditionValue;
import cbit.vcell.math.PostProcessingBlock;
import cbit.vcell.math.ProjectionDataGenerator;
import cbit.vcell.math.PseudoConstant;
import cbit.vcell.math.RandomVariable;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.SubDomain.BoundaryConditionSpec;
import cbit.vcell.math.UniformDistribution;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.VolumeParticleVariable;
import cbit.vcell.math.VolumeRandomVariable;
import cbit.vcell.math.VolumeRegionEquation;
import cbit.vcell.math.VolumeRegionVariable;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.Discontinuity;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.parser.RvachevFunctionUtils;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.render.Vect3d;
import cbit.vcell.simdata.DataSet;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.server.SolverFileWriter;

/**
 * Insert the type's description here.
 * Creation date: (5/9/2005 2:51:48 PM)
 * @author: Fei Gao
 */
public class FiniteVolumeFileWriter extends SolverFileWriter {
	private static final String RANDOM_VARIABLE_FILE_EXTENSION = ".rv";
	private static final String DISTANCE_MAP_FILE_EXTENSION = ".dmf";
	private File workingDirectory = null;
	private File destinationDirectory = null;
	private boolean bInlineVCG = false;
	private Geometry resampledGeometry = null;
	private int psfFieldIndex = -1;
	private boolean bChomboSolver = false;
	
	Set<FieldDataNumerics> uniqueFieldDataNSet = null;	
	
	class FieldDataNumerics {
		String fieldFunction;
		String numericsSubsitute;
		
		FieldDataNumerics(String fieldFString, String numString) {
			fieldFunction = fieldFString;
			numericsSubsitute = numString;
		}

		public String getFieldFunction() {
			return fieldFunction;
		}

		public String getNumericsSubsitute() {
			return numericsSubsitute;
		}
	};
	
	enum FVInputFileKeyword {
		SIMULATION_PARAM_BEGIN,
		SOLVER,
		FV_SOLVER,
		CHOMBO_SEMIIMPLICIT_SOLVER,
		SUNDIALS_PDE_SOLVER,
		DISCONTINUITY_TIMES,
		BASE_FILE_NAME,
		PRIMARY_DATA_DIR, // parallelWorkingDirectory (temporary directory to store parallel results before solver copies to user data directory)
		ENDING_TIME,
		TIME_STEP,
		TIME_INTERVALS,
		KEEP_EVERY,
		ONE_STEP,
		KEEP_AT_MOST,
		CHECK_SPATIALLY_UNIFORM,
		SIMULATION_PARAM_END,
		
		VOLUME_PARTICLE,
		MEMBRANE_PARTICLE,
		
		SMOLDYN_BEGIN,
		SMOLDYN_INPUT_FILE,
		SMOLDYN_END,
		
		MODEL_BEGIN,
		FEATURE,
		MEMBRANE,
		MODEL_END,
		
		MESH_BEGIN,
		VCG_FILE,
		MESH_END,
		
		value,
		flux,
		periodic,
		
		EQUATION_BEGIN,
		INITIAL,
		RATE,
		DIFFUSION,
		EXACT,
		VELOCITY_X,
		VELOCITY_Y,
		VELOCITY_Z,
		GRADIENT_X,
		GRADIENT_Y,
		GRADIENT_Z,
		EQUATION_END,
		
		PARAMETER_BEGIN,
		PARAMETER_END,
		
		VARIABLE_BEGIN,
		VARIABLE_END,
		
		POST_PROCESSING_BLOCK_BEGIN,
		POST_PROCESSING_BLOCK_END,
		EXPLICIT_DATA_GENERATOR,
		PROJECTION_DATA_GENERATOR,
		GAUSSIAN_CONVOLUTION_DATA_GENERATOR,
		GAUSSIAN_CONVOLUTION_VOL_FUNCTION,
		GAUSSIAN_CONVOLUTION_MEM_FUNCTION,
		
		CHOMBO_SPEC_BEGIN,
		DIMENSION,
		MESH_SIZE,
		DOMAIN_SIZE,
		DOMAIN_ORIGIN,
		DISTANCE_MAP,
		FILL_RATIO,
		MAX_BOX_SIZE,
		RELATIVE_TOLERANCE,
		VIEW_LEVEL,
		TAGS_GROW,
		SAVE_VCELL_OUTPUT,
		SAVE_CHOMBO_OUTPUT,
		ACTIVATE_FEATURE_UNDER_DEVELOPMENT,
		SMALL_VOLFRAC_THRESHOLD,
		SUBDOMAINS,
		IF,
		USER,
		REFINEMENTS,
		REFINEMENT_ROIS,
		CHOMBO_SPEC_END,
	}

public FiniteVolumeFileWriter(PrintWriter pw, SimulationTask simTask, Geometry geo, File workingDir) {	// for optimization only, no messaging
	this (pw, simTask, geo, workingDir, false);
	bInlineVCG = true; 
}

public FiniteVolumeFileWriter(PrintWriter pw, SimulationTask simTask, Geometry geo, File workingDir, boolean arg_bMessaging) {
	this(pw, simTask, geo, workingDir, workingDir, arg_bMessaging);
}

public FiniteVolumeFileWriter(PrintWriter pw, SimulationTask simTask, Geometry geo, File workingDir, File destinationDirectory, boolean arg_bMessaging) {
	super(pw, simTask, arg_bMessaging);
	resampledGeometry = geo;
	this.workingDirectory = workingDir;
	this.destinationDirectory = destinationDirectory;
	bChomboSolver = simTask.getSimulation().getSolverTaskDescription().getSolverDescription().isChomboSolver();
}

private Expression subsituteExpression(Expression exp, VariableDomain variableDomain) throws ExpressionException  {
	return subsituteExpression(exp, simTask.getSimulationJob().getSimulationSymbolTable(), variableDomain);
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 * @throws ExpressionException 
 */
private Expression subsituteExpression(Expression exp, SymbolTable symbolTable, VariableDomain variableDomain) throws ExpressionException {
	Expression newExp = MathUtilities.substituteFunctions(exp, symbolTable).flatten();
	FieldFunctionArguments[] fieldFunctionArguments = FieldUtilities.getFieldFunctionArguments(newExp);
	if (fieldFunctionArguments != null && fieldFunctionArguments.length > 0) {
		if (uniqueFieldDataNSet != null && uniqueFieldDataNSet.size() > 0) {	
			for (FieldDataNumerics fdn: uniqueFieldDataNSet) {
				newExp.substituteInPlace(new Expression(fdn.getFieldFunction()), new Expression(fdn.getNumericsSubsitute()));
			}
		} else {
			throw new RuntimeException("Didn't find field functions in simulation when preprocessing, but expression [" + exp.infix() + "] has field function " + newExp.infix() + " in it");
		}
	}
	if (bChomboSolver)
	{
		Set<FunctionInvocation> sizeFunctions = SolverUtilities.getSizeFunctionInvocations(newExp);
		if (sizeFunctions.size() > 0)
		{
			throw new ExpressionException("Size functions are not supported by " + simTask.getSimulation().getSolverTaskDescription().getSolverDescription().getDisplayLabel());
		}
	}
	else
	{
		newExp = SolverUtilities.substituteSizeAndNormalFunctions(newExp, variableDomain);
	}
	return newExp;
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 * @throws MathException 
 * @throws ExpressionException 
 * @throws DataAccessException 
 * @throws IOException 
 */
public void write(String[] parameterNames) throws Exception {
	Variable originalVars[] = null;
	Simulation simulation = simTask.getSimulation();
	MathDescription mathDesc = simulation.getMathDescription();
	if (bChomboSolver)
	{
		writeJMSParamters();	
		writeSimulationParamters();	
		writeModelDescription();
		writeChomboSpec();
		writeVariables();
		writePostProcessingBlock();
		writeCompartments();
		writeMembranes();
	}
	else
	{
		if (simTask.getSimulation().isSerialParameterScan()) {
			originalVars = (Variable[])BeanUtils.getArray(mathDesc.getVariables(),Variable.class);
			Variable allVars[] = (Variable[])BeanUtils.getArray(mathDesc.getVariables(),Variable.class);
			MathOverrides mathOverrides = simulation.getMathOverrides();
			
			String[] scanParameters = mathOverrides.getOverridenConstantNames();
			for (int i = 0; i < scanParameters.length; i++){
				String scanParameter = scanParameters[i];
				Variable mathVariable = mathDesc.getVariable(scanParameter);
				//
				// replace constant values with "Parameter"
				//
				if (mathVariable instanceof Constant){
					Constant origConstant = (Constant)mathVariable;
					for (int j = 0; j < allVars.length; j++){
						if (allVars[j].equals(origConstant)){
							allVars[j] = new ParameterVariable(origConstant.getName());
							break;
						}
					}
				}
			}
			mathDesc.setAllVariables(allVars);
		}
		
		writeJMSParamters();
		writeSimulationParamters();
		writeModelDescription();
		writeMeshFile();
		writeVariables();
		if (mathDesc.isSpatialHybrid()) {
			writeSmoldyn();
		}
		writeParameters(parameterNames);
		writeSerialParameterScans();
		writeFieldData();
		writePostProcessingBlock();
		writeCompartments();
		writeMembranes();
		
		if (originalVars != null) {
			mathDesc.setAllVariables(originalVars);
		}
	}
}

private void writeSmoldyn() throws Exception {
	String baseName =  new File(workingDirectory, simTask.getSimulationJob().getSimulationJobID()).getPath();
	String inputFilename = baseName + SimDataConstants.SMOLDYN_INPUT_FILE_EXTENSION;
	try (PrintWriter pw = new PrintWriter(inputFilename)) {
		SmoldynFileWriter stFileWriter = new SmoldynFileWriter(pw, false, baseName, simTask, bUseMessaging);
		stFileWriter.write();
	} 

	printWriter.println("# Smoldyn Input");
	printWriter.println(FVInputFileKeyword.SMOLDYN_BEGIN);
	printWriter.println(FVInputFileKeyword.SMOLDYN_INPUT_FILE + " " + inputFilename);
	printWriter.println(FVInputFileKeyword.SMOLDYN_END);
	printWriter.println();
}

private void writePostProcessingBlock() throws Exception { // SolverException, ExpressionException and exceptions from roiDataGenerator.getROIDataGeneratorDescription()
	PostProcessingBlock postProcessingBlock = simTask.getSimulation().getMathDescription().getPostProcessingBlock();
	if (postProcessingBlock.getNumDataGenerators() == 0) { // to make c++ code write default var statisitcs, without the token it won't write anything
		printWriter.println(FVInputFileKeyword.POST_PROCESSING_BLOCK_BEGIN);
		printWriter.println(FVInputFileKeyword.POST_PROCESSING_BLOCK_END);
		printWriter.println();
		return;
	}
	if (bChomboSolver && postProcessingBlock.getDataGeneratorList().size() > 0)
	{
		throw new Exception("Data generators are not supported by " + simTask.getSimulation().getSolverTaskDescription().getSolverDescription().getDisplayLabel());
	}
	printWriter.println(" # Post Processing Block");
	printWriter.println(FVInputFileKeyword.POST_PROCESSING_BLOCK_BEGIN);
	for (DataGenerator dataGenerator : postProcessingBlock.getDataGeneratorList()) {
		String varName = dataGenerator.getName();
		Domain domain = dataGenerator.getDomain();
		String domainName = domain == null ? null : domain.getName();
		if (dataGenerator instanceof ProjectionDataGenerator) {
			ProjectionDataGenerator pdg = (ProjectionDataGenerator)dataGenerator;
			Expression function = subsituteExpression(pdg.getFunction(), VariableDomain.VARIABLEDOMAIN_VOLUME);
			printWriter.println(FVInputFileKeyword.PROJECTION_DATA_GENERATOR + " " + varName + " " + domainName 
					+ " " + pdg.getAxis() + " " + pdg.getOperation() + " " + function.infix()  +";");			
		} else if (dataGenerator instanceof ConvolutionDataGenerator) {
			ConvolutionDataGenerator convolutionDataGenerator = (ConvolutionDataGenerator) dataGenerator;
			ConvolutionDataGeneratorKernel kernel = convolutionDataGenerator.getKernel();
			if (kernel instanceof GaussianConvolutionDataGeneratorKernel) {
				GaussianConvolutionDataGeneratorKernel gck = (GaussianConvolutionDataGeneratorKernel)kernel;
				Expression volFunction = subsituteExpression(convolutionDataGenerator.getVolFunction(), VariableDomain.VARIABLEDOMAIN_VOLUME);
				Expression memFunction = subsituteExpression(convolutionDataGenerator.getMemFunction(), VariableDomain.VARIABLEDOMAIN_MEMBRANE);
				Expression sigmaXY = subsituteExpression(gck.getSigmaXY_um(), VariableDomain.VARIABLEDOMAIN_VOLUME);
				Expression sigmaZ = subsituteExpression(gck.getSigmaZ_um(), VariableDomain.VARIABLEDOMAIN_VOLUME);
				String volFuncStr = volFunction.infix();
				String memFuncStr = memFunction.infix();
				printWriter.println(FVInputFileKeyword.GAUSSIAN_CONVOLUTION_DATA_GENERATOR + " " + varName + " " + domainName 
						+ " " + sigmaXY.infix() + " " + sigmaZ.infix() + " " + FVInputFileKeyword.GAUSSIAN_CONVOLUTION_VOL_FUNCTION+ " " + volFuncStr + "; " + FVInputFileKeyword.GAUSSIAN_CONVOLUTION_MEM_FUNCTION+ " " + memFuncStr +";");
			}
		} else if (dataGenerator instanceof cbit.vcell.microscopy.ROIDataGenerator) {
			/*
			ROI_DATA_GENERATOR_BEGIN roidata
			VolumePoints 20
			2667 2676 2679 2771 2969 2877 3067 3277 3185 3283 3473 3580 3690 3687 3878 4086 3990 4182 4193 1077 (all points in one line)
			SampleImage 9 0 1341426862190 vcField('sumROIData','roiSumDataVar',0.0,'Volume')
			StoreEnabled false

			SampleImageFile roiSumDataVar 0.0 C:\Users\abcde\VirtualMicroscopy\SimulationData\SimID_1341426862125_0_sumROIData_roiSumDataVar_0_0_Volume.fdat
			DATA_PROCESSOR_END
			*/
			cbit.vcell.microscopy.ROIDataGenerator roiDataGenerator = (cbit.vcell.microscopy.ROIDataGenerator) dataGenerator;
			printWriter.println(roiDataGenerator.getROIDataGeneratorDescription(workingDirectory, simTask.getSimulationJob())); 
		} else if (dataGenerator instanceof org.vcell.vmicro.workflow.data.ROIDataGenerator) {
			/*
			ROI_DATA_GENERATOR_BEGIN roidata
			VolumePoints 20
			2667 2676 2679 2771 2969 2877 3067 3277 3185 3283 3473 3580 3690 3687 3878 4086 3990 4182 4193 1077 (all points in one line)
			SampleImage 9 0 1341426862190 vcField('sumROIData','roiSumDataVar',0.0,'Volume')
			StoreEnabled false

			SampleImageFile roiSumDataVar 0.0 C:\Users\abcde\VirtualMicroscopy\SimulationData\SimID_1341426862125_0_sumROIData_roiSumDataVar_0_0_Volume.fdat
			DATA_PROCESSOR_END
			*/
			org.vcell.vmicro.workflow.data.ROIDataGenerator roiDataGenerator = (org.vcell.vmicro.workflow.data.ROIDataGenerator) dataGenerator;
			printWriter.println(roiDataGenerator.getROIDataGeneratorDescription(workingDirectory, simTask.getSimulationJob())); 
		} else {
			throw new SolverException(dataGenerator.getClass() + " : data generator not supported yet.");
		}
	}
	printWriter.println(FVInputFileKeyword.POST_PROCESSING_BLOCK_END);
	printWriter.println();
}


/*private void writeDataProcessor() throws DataAccessException, IOException, MathException, DivideByZeroException, ExpressionException {
	Simulation simulation = simTask.getSimulation();
	DataProcessingInstructions dpi = simulation.getDataProcessingInstructions();
	if (dpi == null) {
		printWriter.println("DATA_PROCESSOR_BEGIN " + DataProcessingInstructions.ROI_TIME_SERIES);
		printWriter.println("DATA_PROCESSOR_END");
		printWriter.println();
		return;
	}
	
	FieldDataIdentifierSpec fdis = dpi.getSampleImageFieldData(simulation.getVersion().getOwner());	
	if (fdis == null) {
		throw new DataAccessException("Can't find sample image in data processing instructions");
	}
	String secondarySimDataDir = PropertyLoader.getProperty(PropertyLoader.secondarySimDataDirProperty, null);	
	DataSetControllerImpl dsci = new DataSetControllerImpl(new NullSessionLog(),null,userDirectory.getParentFile(),secondarySimDataDir == null ? null : new File(secondarySimDataDir));
	CartesianMesh origMesh = dsci.getMesh(fdis.getExternalDataIdentifier());
	SimDataBlock simDataBlock = dsci.getSimDataBlock(null,fdis.getExternalDataIdentifier(), fdis.getFieldFuncArgs().getVariableName(), fdis.getFieldFuncArgs().getTime().evaluateConstant());
	VariableType varType = fdis.getFieldFuncArgs().getVariableType();
	VariableType dataVarType = simDataBlock.getVariableType();
	if (!varType.equals(VariableType.UNKNOWN) && !varType.equals(dataVarType)) {
		throw new IllegalArgumentException("field function variable type (" + varType.getTypeName() + ") doesn't match real variable type (" + dataVarType.getTypeName() + ")");
	}
	double[] origData = simDataBlock.getData();	

	String filename = SimulationJob.createSimulationJobID(Simulation.createSimulationID(simulation.getKey()), simulationJob.getJobIndex()) + FieldDataIdentifierSpec.getDefaultFieldDataFileNameForSimulation(fdis.getFieldFuncArgs());
	
	File fdatFile = new File(userDirectory, filename);
	
	
	DataSet.writeNew(fdatFile,
			new String[] {fdis.getFieldFuncArgs().getVariableName()},
			new VariableType[]{simDataBlock.getVariableType()},
			new ISize(origMesh.getSizeX(),origMesh.getSizeY(),origMesh.getSizeZ()),
			new double[][]{origData});
	printWriter.println("DATA_PROCESSOR_BEGIN " + dpi.getScriptName());
	printWriter.println(dpi.getScriptInput());
	printWriter.println("SampleImageFile " + fdis.getFieldFuncArgs().getVariableName() + " " + fdis.getFieldFuncArgs().getTime().infix() + " " + fdatFile);
	printWriter.println("DATA_PROCESSOR_END");
	printWriter.println();

	
}
*/
/**
# fast system dimension num_dependents
FAST_SYSTEM_BEGIN 2 2
INDEPENDENT_VARIALBES rf r 
DEPENDENT_VARIALBES rB rfB 

PSEUDO_CONSTANT_BEGIN
__C0 (rfB + rf);
__C1 (r + rB);
PSEUDO_CONSTANT_END

FAST_RATE_BEGIN
 - ((0.02 * ( - ( - r + __C1) - ( - rf + __C0) + (20.0 * ((x > -3.0) && (x < 3.0) && (y > -5.0) && (y < 5.0))) + _VCell_FieldData_0) * rf) - (0.1 * ( - rf + __C0)));
((0.02 * r * ( - ( - r + __C1) - ( - rf + __C0) + (20.0 * ((x > -3.0) && (x < 3.0) && (y > -5.0) && (y < 5.0))) + _VCell_FieldData_0)) - (0.1 * ( - r + __C1)));
FAST_RATE_END

FAST_DEPENDENCY_BEGIN
rB ( - r + __C1);
rfB ( - rf + __C0);
FAST_DEPENDENCY_END

JACOBIAN_BEGIN
 - (0.1 + (0.02 * (1.0 + (0.0 * ((x > -3.0) && (x < 3.0) && (y > -5.0) && (y < 5.0)))) * rf) + (0.02 * ( - ( - r + __C1) - ( - rf + __C0) + (20.0 * ((x > -3.0) && (x < 3.0) && (y > -5.0) && (y < 5.0))) + _VCell_FieldData_0)));
 - (0.02 * (1.0 + (0.0 * ((x > -3.0) && (x < 3.0) && (y > -5.0) && (y < 5.0)))) * rf);
(0.02 * r * (1.0 + (0.0 * ((x > -3.0) && (x < 3.0) && (y > -5.0) && (y < 5.0)))));
(0.1 + (0.02 * ( - ( - r + __C1) - ( - rf + __C0) + (20.0 * ((x > -3.0) && (x < 3.0) && (y > -5.0) && (y < 5.0))) + _VCell_FieldData_0)) + (0.02 * r * (1.0 + (0.0 * ((x > -3.0) && (x < 3.0) && (y > -5.0) && (y < 5.0))))));
JACOBIAN_END

FAST_SYSTEM_END
 * @throws ExpressionException 
 * @throws MathException 
*/
private void writeFastSystem(SubDomain subDomain) throws MathException, ExpressionException  {
	VariableDomain variableDomain = (subDomain instanceof CompartmentSubDomain) ? VariableDomain.VARIABLEDOMAIN_VOLUME : VariableDomain.VARIABLEDOMAIN_MEMBRANE; 
	FastSystem fastSystem = subDomain.getFastSystem();
	if (fastSystem == null) {
		return;
	}
	FastSystemAnalyzer fs_analyzer = new FastSystemAnalyzer(fastSystem, simTask.getSimulationJob().getSimulationSymbolTable());
	int numIndep = fs_analyzer.getNumIndependentVariables();
	int numDep = fs_analyzer.getNumDependentVariables();
	int numPseudo = fs_analyzer.getNumPseudoConstants();	
	printWriter.println("# fast system dimension num_dependents");
	printWriter.println("FAST_SYSTEM_BEGIN " + numIndep + " "  + numDep);
	if (numIndep != 0) {
		printWriter.print("INDEPENDENT_VARIALBES ");
		Enumeration<Variable> enum1 = fs_analyzer.getIndependentVariables();
		while (enum1.hasMoreElements()) {
			Variable var = enum1.nextElement();
			printWriter.print(var.getName() + " ");
		}
		printWriter.println();
			
	}
	if (numDep != 0) {
		printWriter.print("DEPENDENT_VARIALBES ");
		Enumeration<Variable> enum1 = fs_analyzer.getDependentVariables();
		while (enum1.hasMoreElements()) {
			Variable var = enum1.nextElement();
			printWriter.print(var.getName() + " ");
		}
		printWriter.println();
	}
	printWriter.println();
				
	if (numPseudo != 0) {
		printWriter.println("PSEUDO_CONSTANT_BEGIN");
		Enumeration<PseudoConstant> enum1 = fs_analyzer.getPseudoConstants();
		while (enum1.hasMoreElements()) {
			PseudoConstant pc = enum1.nextElement();
			printWriter.println(pc.getName() + " " + subsituteExpression(pc.getPseudoExpression(), fs_analyzer, variableDomain).infix() + ";");
		}
		printWriter.println("PSEUDO_CONSTANT_END");
		printWriter.println();			
	}
	
	if (numIndep != 0) {
		printWriter.println("FAST_RATE_BEGIN" );
		Enumeration<Expression> enum1 = fs_analyzer.getFastRateExpressions();
		while (enum1.hasMoreElements()) {
			Expression exp = enum1.nextElement();	
			printWriter.println(subsituteExpression(exp, fs_analyzer, variableDomain).infix() + ";");
		}
		printWriter.println("FAST_RATE_END");
		printWriter.println();				
	}	

	if (numDep != 0) {
		printWriter.println("FAST_DEPENDENCY_BEGIN" );
		Enumeration<Expression> enum_exp = fs_analyzer.getDependencyExps();
		Enumeration<Variable> enum_var = fs_analyzer.getDependentVariables();
		while (enum_exp.hasMoreElements()){
			Expression exp = enum_exp.nextElement();
			Variable depVar = enum_var.nextElement();
			printWriter.println(depVar.getName() + " " + subsituteExpression(exp, fs_analyzer, variableDomain).infix() + ";");
		}
		printWriter.println("FAST_DEPENDENCY_END");
		printWriter.println();
	}
	
	if (numIndep != 0) {
		printWriter.println("JACOBIAN_BEGIN" );
		Enumeration<Expression> enum_fre = fs_analyzer.getFastRateExpressions();
		while (enum_fre.hasMoreElements()){
			Expression fre = enum_fre.nextElement();
			Enumeration<Variable> enum_var = fs_analyzer.getIndependentVariables();
			while (enum_var.hasMoreElements()){
				Variable var = enum_var.nextElement();
				Expression exp = subsituteExpression(fre, fs_analyzer, variableDomain).flatten();
				Expression differential = exp.differentiate(var.getName());
				printWriter.println(subsituteExpression(differential, fs_analyzer, variableDomain).infix() + ";");
			}
		}
		printWriter.println("JACOBIAN_END");
		printWriter.println();				
	}
	printWriter.println("FAST_SYSTEM_END");
	printWriter.println();
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 * @throws ExpressionException 
 */
private void writeCompartment_VarContext(CompartmentSubDomain volSubDomain) throws ExpressionException {
	Simulation simulation = simTask.getSimulation();
	//
	// get list of volVariables participating in PDEs (anywhere).
	//
	Vector<VolVariable> pdeVolVariableList = new Vector<VolVariable>();
	Variable[] variables = simTask.getSimulationJob().getSimulationSymbolTable().getVariables();
	for (int i = 0; i < variables.length; i++){
		if (variables[i] instanceof VolVariable && simulation.getMathDescription().isPDE((VolVariable)variables[i])){
			pdeVolVariableList.add((VolVariable)variables[i]);
		}
	}

	Enumeration<Equation> enum_equ = volSubDomain.getEquations();
	while (enum_equ.hasMoreElements()){
		Equation equation = enum_equ.nextElement();
		// for chombo solver, only write equations for variables that are defined in this compartment
		if (!bChomboSolver || equation.getVariable().getDomain().getName().equals(volSubDomain.getName()))
		{
			if (equation instanceof VolumeRegionEquation){
				writeCompartmentRegion_VarContext_Equation(volSubDomain, (VolumeRegionEquation)equation);
			} else {
				writeCompartment_VarContext_Equation(volSubDomain, equation);
			}
		}
		
		if (equation instanceof PdeEquation){
			pdeVolVariableList.remove(equation.getVariable());
		}	
	}
	//
	// add "dummy" volume variable context for any "pde volume variable" that doesn't have an equation defined in this subDomain
	// this is needed to encode possible jump conditions for PDE's defined in adjacent subDomains.
	//    THIS WILL BE NO LONGER NEEDED WHEN JUMP CONDITIONS ARE STORED WITH MEMBRANES (OR WITH THE VOLUME VAR CONTEXT THAT DEFINES THE PDE).
	//
	if (!bChomboSolver)
	{
		for (int i = 0; i < pdeVolVariableList.size(); i++){
			VolVariable volVar = pdeVolVariableList.elementAt(i);
			boolean bSteady = simulation.getMathDescription().isPdeSteady(volVar);
			PdeEquation dummyPdeEquation = new PdeEquation(volVar, bSteady, new Expression(0.0), new Expression(0.0), new Expression(0.0));
			writeCompartment_VarContext_Equation(volSubDomain, dummyPdeEquation);
		}
	}
}


/**
 EQUATION_BEGIN rf
INITIAL 5.0;
RATE ( - ((0.02 * ( - rB - rfB + (20.0 * ((x > -3.0) && (x < 3.0) && (y > -5.0) && (y < 5.0))) + _VCell_FieldData_0) * rf) - (0.1 * rfB)) - (50.0 * rf * ((x > -5.0) && (x < 5.0) && (y > -5.0) && (y < 5.0))));
DIFFUSION 10.0;
BOUNDARY_XM 5.0;
BOUNDARY_XP 5.0;
BOUNDARY_YM 5.0;
BOUNDARY_YP 5.0;
EQUATION_END
 * @throws ExpressionException 
 */
private void writeCompartment_VarContext_Equation(CompartmentSubDomain volSubDomain, Equation equation) throws ExpressionException {	
	printWriter.println(FVInputFileKeyword.EQUATION_BEGIN + " " + equation.getVariable().getName());
	printWriter.println(FVInputFileKeyword.INITIAL + " " + subsituteExpression(equation.getInitialExpression(), VariableDomain.VARIABLEDOMAIN_VOLUME).infix() + ";");
	Expression rateExpression = subsituteExpression(equation.getRateExpression(), VariableDomain.VARIABLEDOMAIN_VOLUME);
	printWriter.println(FVInputFileKeyword.RATE + " " + rateExpression.infix() + ";");
	if (bChomboSolver && equation.getExactSolution() != null)
	{
		printWriter.println(FVInputFileKeyword.EXACT + " " + subsituteExpression(equation.getExactSolution(), VariableDomain.VARIABLEDOMAIN_VOLUME).infix() + ";");
	}
	if (equation instanceof PdeEquation) {
		PdeEquation pdeEquation = (PdeEquation)equation;
		printWriter.println(FVInputFileKeyword.DIFFUSION + " " + subsituteExpression(pdeEquation.getDiffusionExpression(), VariableDomain.VARIABLEDOMAIN_VOLUME).infix() + ";");
		
		// Velocity
		String velocity = pdeEquation.getVelocityX() == null ? "0.0" : subsituteExpression(pdeEquation.getVelocityX(), VariableDomain.VARIABLEDOMAIN_VOLUME).infix();
		printWriter.println(FVInputFileKeyword.VELOCITY_X + " " + velocity + ";");
				
		if (resampledGeometry.getDimension() > 1) {
			velocity = pdeEquation.getVelocityY() == null ? "0.0" : subsituteExpression(pdeEquation.getVelocityY(), VariableDomain.VARIABLEDOMAIN_VOLUME).infix();
			printWriter.println(FVInputFileKeyword.VELOCITY_Y + " " + velocity + ";");
					
			if (resampledGeometry.getDimension() > 2) {
				velocity = pdeEquation.getVelocityZ() == null ? "0.0" : subsituteExpression(pdeEquation.getVelocityZ(), VariableDomain.VARIABLEDOMAIN_VOLUME).infix();
				printWriter.println(FVInputFileKeyword.VELOCITY_Z + " " + velocity + ";");
			}
		}
	
		// Gradient
		if (pdeEquation.getGradientX() != null) {
			printWriter.println(FVInputFileKeyword.GRADIENT_X + " " + subsituteExpression(pdeEquation.getGradientX(), VariableDomain.VARIABLEDOMAIN_VOLUME).infix() + ";");
		}
		if (pdeEquation.getGradientY() != null) { 
			printWriter.println(FVInputFileKeyword.GRADIENT_Y + " " + subsituteExpression(pdeEquation.getGradientY(), VariableDomain.VARIABLEDOMAIN_VOLUME).infix() + ";");
		}
		if (pdeEquation.getGradientZ() != null) {
			printWriter.println(FVInputFileKeyword.GRADIENT_Z + " " + subsituteExpression(pdeEquation.getGradientZ(), VariableDomain.VARIABLEDOMAIN_VOLUME).infix() + ";");			
		}
		
		PdeEquation pde = (PdeEquation)equation;		
		BoundaryConditionType[] bctypes = new BoundaryConditionType[] {
				volSubDomain.getBoundaryConditionXm(),
				volSubDomain.getBoundaryConditionXp(),
				volSubDomain.getBoundaryConditionYm(),
				volSubDomain.getBoundaryConditionYp(),
				volSubDomain.getBoundaryConditionZm(),
				volSubDomain.getBoundaryConditionZp()
		};
		if (bChomboSolver)
		{
			writeChomboBoundaryValues(bctypes, pde, VariableDomain.VARIABLEDOMAIN_VOLUME);
		}
		else
		{
			writeBoundaryValues(bctypes, pde, VariableDomain.VARIABLEDOMAIN_VOLUME);
		}
	}	

//	if (simulation.getSolverTaskDescription().getSolverDescription().equals(SolverDescription.SundialsPDE)) {
//		StringBuffer rateDerivativeString = new StringBuffer(); 
//		Variable[] vars = simulation.getVariables();
//		int count = 0;
//		for (Variable var : vars) {
//			if (var instanceof VolVariable) {
//				Expression exp = rateExpression.differentiate(var.getName());
//				rateDerivativeString.append(exp.flatten().infix() + ";\n");
//				count ++;
//			}
//		}
//		printWriter.println("RATE_DERIVATIVES " + count);
//		printWriter.print(rateDerivativeString);
//	}
	printWriter.println(FVInputFileKeyword.EQUATION_END);
	printWriter.println();
}


/**
COMPARTMENT_BEGIN nucleus

BOUNDARY_CONDITIONS value value value value 

EQUATION_BEGIN rfB
INITIAL _VCell_FieldData_0;
RATE ( - (50.0 * rfB * ((x > -5.0) && (x < 5.0) && (y > -5.0) && (y < 5.0))) + (0.02 * ( - rB - rfB + (20.0 * ((x > -3.0) && (x < 3.0) && (y > -5.0) && (y < 5.0))) + _VCell_FieldData_0) * rf) - (0.1 * rfB));
EQUATION_END

EQUATION_BEGIN r
INITIAL 5.0;
RATE ( - ((0.02 * r * ( - rB - rfB + (20.0 * ((x > -3.0) && (x < 3.0) && (y > -5.0) && (y < 5.0))) + _VCell_FieldData_0)) - (0.1 * rB)) + (50.0 * rf * ((x > -5.0) && (x < 5.0) && (y > -5.0) && (y < 5.0))));
DIFFUSION 10.0;
BOUNDARY_XM 5.0;
BOUNDARY_XP 5.0;
BOUNDARY_YM 5.0;
BOUNDARY_YP 5.0;
EQUATION_END

COMPARTMENT_END
 * @throws ExpressionException 
 * @throws MathException 
*/
private void writeCompartments() throws ExpressionException, MathException {
	Simulation simulation = simTask.getSimulation();
	MathDescription mathDesc = simulation.getMathDescription();
	Enumeration<SubDomain> enum1 = mathDesc.getSubDomains();
	while (enum1.hasMoreElements()) {		
		SubDomain sd = enum1.nextElement();
		if (sd instanceof CompartmentSubDomain) {
			CompartmentSubDomain csd = (CompartmentSubDomain)sd;
			printWriter.println("COMPARTMENT_BEGIN " + csd.getName());
			printWriter.println();
			
			writeCompartment_VarContext(csd);			
			writeFastSystem(csd);
			printWriter.println("COMPARTMENT_END");
			printWriter.println();
		}
	}
	printWriter.println();	
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 */
private void writeFeature_boundaryConditions(CompartmentSubDomain csd) {
	BoundaryConditionType[] bctypes = new BoundaryConditionType[] {
			csd.getBoundaryConditionXm(),
			csd.getBoundaryConditionXp(),
			csd.getBoundaryConditionYm(),
			csd.getBoundaryConditionYp(),
			csd.getBoundaryConditionZm(),
			csd.getBoundaryConditionZp()
	};
	writeBoundaryConditions(bctypes);
	printWriter.println();
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 */
private void writeMembrane_boundaryConditions(MembraneSubDomain msd) {
	BoundaryConditionType[] bctypes = new BoundaryConditionType[] {
			msd.getInsideCompartment().getBoundaryConditionXm(),
			msd.getInsideCompartment().getBoundaryConditionXp(),
			msd.getInsideCompartment().getBoundaryConditionYm(),
			msd.getInsideCompartment().getBoundaryConditionYp(),
			msd.getInsideCompartment().getBoundaryConditionZm(),
			msd.getInsideCompartment().getBoundaryConditionZp()
	};
	writeBoundaryConditions(bctypes);
	printWriter.println();
}


private void writeBoundaryConditions(BoundaryConditionType[] bctypes) {
	int dimension = resampledGeometry.getDimension();
	for (int i = 0; i < 2 * dimension; i ++) {
		if (bctypes[i].isDIRICHLET()) {
			printWriter.print("value ");
		} else if (bctypes[i].isNEUMANN()){
			printWriter.print("flux ");
		} else if (bctypes[i].isPERIODIC()) {
			printWriter.print("periodic ");
		}
	}
}

public static String replaceVolumeVariable(SimulationTask simTask, MembraneSubDomain msd, Expression exp) throws MathException, ExpressionException {
	Expression fluxExpr = new Expression(exp);
	String symbols[] = exp.getSymbols();
	String flux = null;
	// size function may be in the expression, they are not variables
	if (symbols == null) {
		flux = exp.infix();
	} else {
		for (String symbol : symbols) {
			Variable var = simTask.getSimulationJob().getSimulationSymbolTable().getVariable(symbol);
			if (var instanceof VolVariable || var instanceof VolumeRegionVariable) {
				fluxExpr.substituteInPlace(new Expression(var.getName()), new Expression(var.getName() + "_" + var.getDomain().getName() + "_membrane"));
			}
		}
	
		// for old models
		flux = fluxExpr.infix();
		flux = flux.replaceAll("_INSIDE", "_" + msd.getInsideCompartment().getName() + "_membrane");
		flux = flux.replaceAll("_OUTSIDE", "_" + msd.getOutsideCompartment().getName() + "_membrane");
	}
	return flux;
	
}
/**
JUMP_CONDITION_BEGIN r
INFLUX 0.0;
OUTFLUX 0.0;
JUMP_CONDITION_END
 * @throws ExpressionException
 * @throws MathException 
*/
private void writeMembrane_jumpConditions(MembraneSubDomain msd) throws ExpressionException, MathException {
	Enumeration<JumpCondition> enum1 = msd.getJumpConditions();
	// equations for boundaryValues for inner compartment subdomain
	CompartmentSubDomain innerCompSubDomain = msd.getInsideCompartment();
	Enumeration<Equation> innercompSubDomEqnsEnum = innerCompSubDomain.getEquations();
	while (innercompSubDomEqnsEnum.hasMoreElements()) {
		Equation eqn = innercompSubDomEqnsEnum.nextElement();
		if (eqn instanceof PdeEquation) {
			PdeEquation pdeEqn = (PdeEquation)eqn;
			BoundaryConditionValue boundaryValue = pdeEqn.getBoundaryConditionValue(msd.getName());
			if (boundaryValue != null) {
				// check if the type of BoundaryConditionSpec for this membraneSubDomain (msd) in the (inner) compartmentSubDomain is Flux; if not, it cannot be handled.
				BoundaryConditionSpec bcs = innerCompSubDomain.getBoundaryConditionSpec(msd.getName());
				if (bcs == null) {
					throw new MathException("No Boundary type specified for '" + msd.getName() + "' in '" + innerCompSubDomain.getName() + "'.");
				}
				if (bcs != null && !bcs.getBoundaryConditionType().compareEqual(BoundaryConditionType.getNEUMANN()) && !bChomboSolver) {
					throw new MathException("Boundary type '" + bcs.getBoundaryConditionType().boundaryTypeStringValue() + "' for compartmentSubDomain '" + innerCompSubDomain.getName() + "' not handled by the chosen solver. Expecting boundary condition of type 'Flux'.");
				}
				if (pdeEqn.getVariable().getDomain() == null || pdeEqn.getVariable().getDomain().getName().equals(msd.getInsideCompartment().getName())) {
					printWriter.println("JUMP_CONDITION_BEGIN " + pdeEqn.getVariable().getName());
					Expression flux = subsituteExpression(boundaryValue.getBoundaryConditionExpression(), VariableDomain.VARIABLEDOMAIN_MEMBRANE);
					String infix = replaceVolumeVariable(getSimulationTask(), msd, flux);
					printWriter.println(bcs.getBoundaryConditionType().boundaryTypeStringValue().toUpperCase()  + " " + msd.getInsideCompartment().getName() + " " + infix + ";");
					printWriter.println("JUMP_CONDITION_END");
					printWriter.println();
				}
			}
		}
	}
	
	// equations for boundaryValues for outer compartment subdomain
	CompartmentSubDomain outerCompSubDomain = msd.getOutsideCompartment();
	Enumeration<Equation> outerCompSubDomEqnsEnum = outerCompSubDomain.getEquations();
	while (outerCompSubDomEqnsEnum.hasMoreElements()) {
		Equation eqn = outerCompSubDomEqnsEnum.nextElement();
		if (eqn instanceof PdeEquation) {
			PdeEquation pdeEqn = (PdeEquation)eqn;
			BoundaryConditionValue boundaryValue = pdeEqn.getBoundaryConditionValue(msd.getName());
			if (boundaryValue != null) {
				// check if the type of BoundaryConditionSpec for this membraneSubDomain (msd) in the (inner) compartmentSubDomain is Flux; if not, it cannot be handled.
				BoundaryConditionSpec bcs = outerCompSubDomain.getBoundaryConditionSpec(msd.getName());
				if (bcs != null && !bcs.getBoundaryConditionType().compareEqual(BoundaryConditionType.getNEUMANN()) && !bChomboSolver) {
					throw new MathException("Boundary type '" + bcs.getBoundaryConditionType().boundaryTypeStringValue() + "' for compartmentSubDomain '" + outerCompSubDomain.getName() + "' not handled by the chosen solver. Expecting boundary condition of type 'Flux'.");
				}
				if (pdeEqn.getVariable().getDomain() == null || pdeEqn.getVariable().getDomain().getName().equals(msd.getOutsideCompartment().getName())) {
					printWriter.println("JUMP_CONDITION_BEGIN " + pdeEqn.getVariable().getName());
					Expression flux = subsituteExpression(boundaryValue.getBoundaryConditionExpression(), VariableDomain.VARIABLEDOMAIN_MEMBRANE);
					String infix = replaceVolumeVariable(getSimulationTask(), msd, flux);
					printWriter.println(bcs.getBoundaryConditionType().boundaryTypeStringValue().toUpperCase() + " " + msd.getOutsideCompartment().getName() + " " + infix + ";");
					printWriter.println("JUMP_CONDITION_END");
					printWriter.println();
				}
			}
		}
	}

	
	while (enum1.hasMoreElements()) {
		JumpCondition jc = enum1.nextElement();
		printWriter.println("JUMP_CONDITION_BEGIN " + jc.getVariable().getName());
		// influx
		if (jc.getVariable().getDomain() == null || jc.getVariable().getDomain().getName().equals(msd.getInsideCompartment().getName())) 
		{
			Expression flux = subsituteExpression(jc.getInFluxExpression(), VariableDomain.VARIABLEDOMAIN_MEMBRANE);
			String infix = replaceVolumeVariable(getSimulationTask(), msd, flux);
			printWriter.println(BoundaryConditionType.NEUMANN_STRING.toUpperCase() + " " + msd.getInsideCompartment().getName() + " " + infix + ";");
		}
		
		if (jc.getVariable().getDomain() == null || jc.getVariable().getDomain().getName().equals(msd.getOutsideCompartment().getName())) 
		{
			// outflux
			Expression flux = subsituteExpression(jc.getOutFluxExpression(), VariableDomain.VARIABLEDOMAIN_MEMBRANE);
			String infix = replaceVolumeVariable(simTask, msd, flux);
			printWriter.println(BoundaryConditionType.NEUMANN_STRING.toUpperCase() + " "  + msd.getOutsideCompartment().getName() + " " + infix + ";");
		}		
		printWriter.println("JUMP_CONDITION_END");
		printWriter.println();
	}		
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 * @throws ExpressionException 
 * @throws MathException 
 */
private void writeMembrane_VarContext(MembraneSubDomain memSubDomain) throws ExpressionException, MathException {	
	//
	// add MembraneVarContext coders
	//
	Enumeration<Equation> enum_equ = memSubDomain.getEquations();
	while (enum_equ.hasMoreElements()){
		Equation equation = enum_equ.nextElement();
		if (equation instanceof MembraneRegionEquation) {
			writeMembraneRegion_VarContext_Equation(memSubDomain, (MembraneRegionEquation)equation);
		} else{
			writeMembrane_VarContext_Equation(memSubDomain, equation);
		}
	}

}


/**
EQUATION_BEGIN rf
INITIAL 5.0;
RATE ( - ((0.02 * ( - rB - rfB + (20.0 * ((x > -3.0) && (x < 3.0) && (y > -5.0) && (y < 5.0))) + _VCell_FieldData_0) * rf) - (0.1 * rfB)) - (50.0 * rf * ((x > -5.0) && (x < 5.0) && (y > -5.0) && (y < 5.0))));
DIFFUSION 10.0;
BOUNDARY_XM 5.0;
BOUNDARY_XP 5.0;
BOUNDARY_YM 5.0;
BOUNDARY_YP 5.0;
EQUATION_END
 * @throws ExpressionException 
 * @throws MathException 
 */
private void writeMembraneRegion_VarContext_Equation(MembraneSubDomain memSubDomain, MembraneRegionEquation equation) throws ExpressionException, MathException {	
	printWriter.println("EQUATION_BEGIN " + equation.getVariable().getName());
	printWriter.println("INITIAL " + subsituteExpression(equation.getInitialExpression(), VariableDomain.VARIABLEDOMAIN_MEMBRANE).infix() + ";");
	
	Expression rateExp = subsituteExpression(((MembraneRegionEquation)equation).getMembraneRateExpression(), VariableDomain.VARIABLEDOMAIN_MEMBRANE);
	String rateStr = replaceVolumeVariable(simTask, memSubDomain, rateExp);
	printWriter.println("RATE " + rateStr + ";");
	
	printWriter.println("UNIFORMRATE " + subsituteExpression(((MembraneRegionEquation)equation).getUniformRateExpression(), VariableDomain.VARIABLEDOMAIN_MEMBRANE).infix() + ";");
	printWriter.println("EQUATION_END");
	printWriter.println();
}


private void writeBoundaryValues(BoundaryConditionType[] bctypes, PdeEquation pde, VariableDomain variableDomain) throws ExpressionException {
	int dimension = resampledGeometry.getDimension();
	
	String[] bctitles = new String[]{"BOUNDARY_XM", "BOUNDARY_XP", "BOUNDARY_YM", "BOUNDARY_YP", "BOUNDARY_ZM", "BOUNDARY_ZP"};
	Expression[] bcs = new Expression[] {
		pde.getBoundaryXm(),
		pde.getBoundaryXp(),
		pde.getBoundaryYm(),
		pde.getBoundaryYp(),
		pde.getBoundaryZm(),
		pde.getBoundaryZp()
	};
	
	for (int i = 0; i < 2 * dimension; i ++) {		 
		Expression valueExp = null;
		if (bcs[i] != null) {
			valueExp = bcs[i];
		} else {
			if (bctypes[i].isDIRICHLET()){		
				valueExp = pde.getInitialExpression();
			} else {
				valueExp = new Expression("0.0");
			}
		} 
		if (valueExp != null) {
			printWriter.println(bctitles[i] + " " + subsituteExpression(valueExp, variableDomain).infix() + ";");
		}
	}
}


/**
MEMBRANE_BEGIN subVolume0_subVolume1_membrane subVolume0 subVolume1

BOUNDARY_CONDITIONS value value value value 

EQUATION_BEGIN varMem
INITIAL (x > 0.75);
REACTION 0.0;
DIFFUSION 1.0;
BOUNDARY_XM (x > 0.75);
BOUNDARY_XP (x > 0.75);
BOUNDARY_YM (x > 0.75);
BOUNDARY_YP (x > 0.75);
EQUATION_END

JUMP_CONDITION_BEGIN varVol
INFLUX 0.0;
OUTFLUX 0.0;
JUMP_CONDITION_END

MEMBRANE_END
 * @throws ExpressionException 
 * @throws MathException 
 */
private void writeMembranes() throws ExpressionException, MathException {
	Simulation simulation = simTask.getSimulation();
	MathDescription mathDesc = simulation.getMathDescription();
	Enumeration<SubDomain> enum1 = mathDesc.getSubDomains();
	while (enum1.hasMoreElements()) {		
		SubDomain sd = enum1.nextElement();
		if (sd instanceof MembraneSubDomain) {
			MembraneSubDomain msd = (MembraneSubDomain)sd;
			printWriter.println("MEMBRANE_BEGIN " + msd.getName() + " " + msd.getInsideCompartment().getName() + " " + msd.getOutsideCompartment().getName());
			printWriter.println();
			
			writeMembrane_VarContext(msd);			
			writeMembrane_jumpConditions(msd);
			writeFastSystem(msd);
			printWriter.println("MEMBRANE_END");
			printWriter.println();
		}
	}
	
	
}


/**
# Model description: FEATURE name handle priority boundary_conditions
MODEL_BEGIN
FEATURE Nucleus 0 value value value value 
FEATURE Cytosol 1 flux flux value value 
FEATURE ExtraCellular 2 flux flux value value 
MEMBRANE Nucleus_Cytosol_membrane Nucleus Cytosol value value value value 
MEMBRANE Cytosol_ExtraCellular_membrane Cytosol ExtraCellular flux flux value value 
MODEL_END
 * @throws MathException 
*/
private void writeModelDescription() throws MathException {
	Simulation simulation = simTask.getSimulation();
	
	printWriter.println("# Model description: FEATURE name handle boundary_conditions");
	printWriter.println(FVInputFileKeyword.MODEL_BEGIN);
	MathDescription mathDesc = simulation.getMathDescription();
	Enumeration<SubDomain> enum1 = mathDesc.getSubDomains();
	while (enum1.hasMoreElements()) {
		SubDomain sd = enum1.nextElement();
		if (sd instanceof CompartmentSubDomain) {
			CompartmentSubDomain csd = (CompartmentSubDomain)sd;
			printWriter.print(FVInputFileKeyword.FEATURE + " " + csd.getName() + " " + mathDesc.getHandle(csd) + " ");
			writeFeature_boundaryConditions(csd);
		} else if (sd instanceof MembraneSubDomain) {
			MembraneSubDomain msd = (MembraneSubDomain)sd;
			printWriter.print(FVInputFileKeyword.MEMBRANE + " " + msd.getName() + " " + msd.getInsideCompartment().getName() + " " + msd.getOutsideCompartment().getName() + " ");
			writeMembrane_boundaryConditions(msd);
		}
	}
	printWriter.println(FVInputFileKeyword.MODEL_END);
	printWriter.println();	
}


private void getDiscontinuityTimes(Vector<Discontinuity> discontinuities, TreeSet<Double> discontinuityTimes) throws ExpressionException, MathException {
	Simulation simulation = simTask.getSimulation();
	SimulationSymbolTable simSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();
	
	for (Discontinuity discontinuity : discontinuities) {
		Expression rfexp = discontinuity.getRootFindingExp();
		rfexp.bindExpression(simSymbolTable);
		rfexp = simSymbolTable.substituteFunctions(rfexp).flatten();
		String[] symbols = rfexp.getSymbols();
		if (symbols == null){
			continue;
		}
		
		boolean bHasT = false;
		for (String symbol : symbols) {
			if (symbol.equals(ReservedVariable.TIME.getName())) {
				bHasT = true;
			}
		}
		if (bHasT) {
			if (symbols.length != 1) {
				throw new ExpressionException(simulation.getSolverTaskDescription().getSolverDescription().getDisplayLabel() 
						+  ": time discontinuity " + discontinuity.getDiscontinuityExp().infix() + " can only be a function of time");
			}
			Expression deriv = rfexp.differentiate(ReservedVariable.TIME.getName());
			double d = deriv.evaluateConstant(); // we don't allow 5t < 3 
			if (d != 1 && d != -1) {
				throw new ExpressionException(simulation.getSolverTaskDescription().getSolverDescription().getDisplayLabel() 
						+  ": time discontinuity " + discontinuity.getDiscontinuityExp().infix() + " is not allowed.");
			}
			rfexp.substituteInPlace(new Expression(ReservedVariable.TIME.getName()), new Expression(0));
			rfexp.flatten();
			double st = Math.abs(rfexp.evaluateConstant());
			discontinuityTimes.add(st);
		}
	}
	
	/*Simulation simulation = simTask.getSimulation();
	SimulationSymbolTable simSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();
	MonadicFunctionRootFinder rootFinder = new Bisection();
	
	for (Discontinuity discontinuity : discontinuities) {
		Expression rfexp = discontinuity.getRootFindingExp();
		rfexp.bindExpression(simSymbolTable);
		rfexp = simSymbolTable.substituteFunctions(rfexp).flatten();
		String[] symbols = rfexp.getSymbols();
		boolean bHasT = false;
		for (String symbol : symbols) {
			if (symbol.equals(ReservedVariable.TIME.getName())) {
				bHasT = true;
			}
		}
		if (bHasT) {
			if (symbols.length != 1) {
				System.err.println(simulation.getSolverTaskDescription().getSolverDescription().getDisplayLabel() 
						+  ": discontinuity " + discontinuity.getDiscontinuityExp().infix() + " is not just a function of time, not handled properly by solver");
				continue;
			}
		
			double startTime = simulation.getSolverTaskDescription().getTimeBounds().getStartingTime();
			double endTime = simulation.getSolverTaskDescription().getTimeBounds().getEndingTime();
			findAllRoots(rfexp,startTime,endTime,rootFinder,discontinuityTimes,false);
		}
	} ---------------------------------JIM's CODE COMMENTTED FOR FUTURE DEVELOPMENT*/
}

/**
 * 
 * @param timeFunction
 * @param startTime
 * @param endTime
 * @param rootFinder
 * @param uniqueRootTimes
 * @param bPrintIterations
 * @throws ExpressionException
 * 
 * for testing within scrapbook, see below:
 * 
 *  
 * try {
	edu.northwestern.at.utils.math.rootfinders.MonadicFunctionRootFinder rootFinder = 
//			new edu.northwestern.at.utils.math.rootfinders.Brent();
			new edu.northwestern.at.utils.math.rootfinders.Bisection();
//			new edu.northwestern.at.utils.math.rootfinders.NewtonRaphson();
//			new edu.northwestern.at.utils.math.rootfinders.Secant();
	cbit.vcell.parser.SimpleSymbolTable simpleSymbolTable = new cbit.vcell.parser.SimpleSymbolTable(new String[] { "t" });

	cbit.vcell.parser.Expression exp = new cbit.vcell.parser.Expression("t-0.56");
	
	exp.bindExpression(simpleSymbolTable);
	java.util.TreeSet<Double> rootTimes = new java.util.TreeSet<Double>();
	double startTime = 0.0;
	double endTime = 100.0;
	System.out.print("exp = '"+ exp.infix() + "'");
	long currentTimeMS = System.currentTimeMillis();
	cbit.vcell.solvers.FiniteVolumeFileWriter.findAllRoots(exp,startTime,endTime,rootFinder,rootTimes,false);
	long finalTimeMS = System.currentTimeMillis();
	for (double root : rootTimes){
		System.out.println("root = "+root);
	}
	System.out.println("elapsedTime of computation = "+(finalTimeMS-currentTimeMS)+" ms, found " + rootTimes.size() + " roots (not unique)");
	
}catch (Exception e){
	e.printStackTrace(System.out);
}

 */
/*public static void findAllRoots(Expression timeFunction, double startTime, double endTime, MonadicFunctionRootFinder rootFinder, TreeSet<Double> uniqueRootTimes, boolean bPrintIterations) throws ExpressionException{
	TreeSet<Double> allRootTimes = new TreeSet<Double>();
	final Expression function_exp = new Expression(timeFunction);
	MonadicFunction valueFunction = new MonadicFunction() {
		double[] values = new double[1];
		public double f(double t) {
			values[0] = t;
			try {
				return function_exp.evaluateVector(values);
			} catch (ExpressionException e) {
				e.printStackTrace();
				throw new RuntimeException("expression exception "+e.getMessage());
			}
		}
	};
	
	final Expression derivative_exp = new Expression(timeFunction.differentiate(ReservedVariable.TIME.getName()));
	MonadicFunction derivativeFunction = new MonadicFunction() {
		double[] values = new double[1];
		public double f(double t) {
			values[0] = t;
			try {
				return derivative_exp.evaluateVector(values);
			} catch (ExpressionException e) {
				e.printStackTrace();
				throw new RuntimeException("expression exception "+e.getMessage());
			}
		}
	};
	
	RootFinderConvergenceTest convergenceTest = new StandardRootFinderConvergenceTest();
	RootFinderIterationInformation iterationInformation = null;
	if (bPrintIterations){
		iterationInformation = new RootFinderIterationInformation() {				
			public void iterationInformation(double x, double fx, double dfx, int currentIteration) {
				System.out.println(currentIteration+") x="+x+", fx="+fx+", dfx="+dfx);
			}
		};
	}
	int NUM_BRACKETS = 1000;
	double simulationTime = endTime - startTime;
	double tolerance = simulationTime/1e10;
	int maxIter = 1000;
	
	for (int i=0;i<NUM_BRACKETS-1;i++){
		double bracketMin = startTime + simulationTime*i/NUM_BRACKETS;
		double bracketMax = startTime + simulationTime*(i+1)/NUM_BRACKETS;
	
		double root = rootFinder.findRoot(bracketMin, bracketMax, tolerance, maxIter, valueFunction, derivativeFunction, convergenceTest, iterationInformation);
		if (root>startTime && root<endTime && valueFunction.f(root)<=tolerance){
			allRootTimes.add(root);
		}
	}
	double uniqueTolerance = tolerance * 100;
	double lastUniqueRoot = Double.NEGATIVE_INFINITY;
	for (double root : allRootTimes){
		if (root-lastUniqueRoot > uniqueTolerance){
			uniqueRootTimes.add(root);
		}
		lastUniqueRoot = root;
	}

}  ---------------------------------JIM's CODE COMMENTTED FOR FUTURE DEVELOPMENT*/


/**
# Simulation Parameters
SIMULATION_PARAM_BEGIN
SOLVER SUNDIALS_PDE_SOLVER 1.0E-7 1.0E-9
DISCONTINUITY_TIMES 2 1.0E-4 3.0000000000000003E-4
BASE_FILE_NAME c:/Vcell/users/fgao/SimID_31746636_0_
ENDING_TIME 4.0E-4
KEEP_EVERY ONE_STEP 3
KEEP_AT_MOST 1000
SIMULATION_PARAM_END
 * @throws MathException 
 * @throws ExpressionException 
*/
private void writeSimulationParamters() throws ExpressionException, MathException {	
	Simulation simulation = simTask.getSimulation();
	SolverTaskDescription solverTaskDesc = simulation.getSolverTaskDescription();
	
	printWriter.println("# Simulation Parameters");
	printWriter.println(FVInputFileKeyword.SIMULATION_PARAM_BEGIN);
	if (solverTaskDesc.getSolverDescription().equals(SolverDescription.SundialsPDE)) {
		printWriter.print(FVInputFileKeyword.SOLVER + " " + FVInputFileKeyword.SUNDIALS_PDE_SOLVER +
				" " + solverTaskDesc.getErrorTolerance().getRelativeErrorTolerance() 
				+ " " + solverTaskDesc.getErrorTolerance().getAbsoluteErrorTolerance() + " " + solverTaskDesc.getTimeStep().getMaximumTimeStep());
		if (simulation.getMathDescription().hasVelocity()) {
			printWriter.print(" " + solverTaskDesc.getSundialsPdeSolverOptions().getMaxOrderAdvection());
		}
		printWriter.println();
		Vector<Discontinuity> discontinuities = new Vector<Discontinuity>();
		TreeSet<Double> discontinuityTimes = new TreeSet<Double>();
		MathDescription mathDesc = simulation.getMathDescription();
		Enumeration<SubDomain> enum1 = mathDesc.getSubDomains();
		while (enum1.hasMoreElements()) {		
			SubDomain sd = enum1.nextElement();
			Enumeration<Equation> enum_equ = sd.getEquations();
			while (enum_equ.hasMoreElements()){
				Equation equation = enum_equ.nextElement();
				equation.getDiscontinuities(simTask.getSimulationJob().getSimulationSymbolTable(), discontinuities);
			}
		}
		getDiscontinuityTimes(discontinuities, discontinuityTimes);
		if (discontinuityTimes.size() > 0) {
			printWriter.print(FVInputFileKeyword.DISCONTINUITY_TIMES + " " + discontinuityTimes.size());
			for (double d : discontinuityTimes) {
				printWriter.print(" " + d);			
			}
			printWriter.println();
		}
	} else if(solverTaskDesc.getSolverDescription().equals(SolverDescription.Chombo)){
		printWriter.println(FVInputFileKeyword.SOLVER + " " + FVInputFileKeyword.CHOMBO_SEMIIMPLICIT_SOLVER);
	} else { 
		printWriter.println(FVInputFileKeyword.SOLVER + " " + FVInputFileKeyword.FV_SOLVER + " " + solverTaskDesc.getErrorTolerance().getRelativeErrorTolerance());
	}
	printWriter.println(FVInputFileKeyword.BASE_FILE_NAME + " " + new File(workingDirectory, simTask.getSimulationJob().getSimulationJobID()).getAbsolutePath());
	if (solverTaskDesc.isParallel() && destinationDirectory != null && !destinationDirectory.equals(workingDirectory))
	{
		printWriter.println(FVInputFileKeyword.PRIMARY_DATA_DIR + " " + destinationDirectory.getAbsolutePath());
	}
	printWriter.println(FVInputFileKeyword.ENDING_TIME + " " + solverTaskDesc.getTimeBounds().getEndingTime());
	OutputTimeSpec outputTimeSpec = solverTaskDesc.getOutputTimeSpec();	
	if (solverTaskDesc.getSolverDescription().isChomboSolver())
	{
		List<TimeInterval> timeIntervalList = solverTaskDesc.getChomboSolverSpec().getTimeIntervalList();
		printWriter.println(FVInputFileKeyword.TIME_INTERVALS + " " + timeIntervalList.size());
		for (TimeInterval ti: timeIntervalList)
		{
			printWriter.println(ti.getEndingTime() + " " + ti.getTimeStep() + " " + ti.getKeepEvery());
		}
	}
	else if (solverTaskDesc.getSolverDescription().equals(SolverDescription.SundialsPDE)) {
		if (outputTimeSpec.isDefault()) {
			DefaultOutputTimeSpec defaultOutputTimeSpec = (DefaultOutputTimeSpec)outputTimeSpec;
			printWriter.println(FVInputFileKeyword.KEEP_EVERY + " " +FVInputFileKeyword.ONE_STEP + " " + defaultOutputTimeSpec.getKeepEvery());
			printWriter.println(FVInputFileKeyword.KEEP_AT_MOST + " " + defaultOutputTimeSpec.getKeepAtMost());
		} else {
			printWriter.println(FVInputFileKeyword.TIME_STEP + " " + ((UniformOutputTimeSpec)outputTimeSpec).getOutputTimeStep());
			printWriter.println(FVInputFileKeyword.KEEP_EVERY + " 1");
		}
  } else {
    	double defaultTimeStep = solverTaskDesc.getTimeStep().getDefaultTimeStep();
    	printWriter.println(FVInputFileKeyword.TIME_STEP + " " + defaultTimeStep);
    	int keepEvery = 1;
		if (outputTimeSpec.isDefault()) {
        	keepEvery = ((DefaultOutputTimeSpec)outputTimeSpec).getKeepEvery();
		} else if (outputTimeSpec.isUniform()) {
			UniformOutputTimeSpec uots = (UniformOutputTimeSpec) outputTimeSpec;
			double ots = uots.getOutputTimeStep();
	    	keepEvery = (int)(ots / defaultTimeStep);
		} else {
			throw new RuntimeException("unexpected OutputTime specification type :"+outputTimeSpec.getClass().getName());
		}
		if (keepEvery <= 0) {
			throw new RuntimeException("Output KeepEvery must be a positive integer. Try to change the output option.");
		}
		printWriter.println(FVInputFileKeyword.KEEP_EVERY + " " +  keepEvery);
  }
  ErrorTolerance stopAtSpatiallyUniformErrorTolerance = solverTaskDesc.getStopAtSpatiallyUniformErrorTolerance();
	if (stopAtSpatiallyUniformErrorTolerance != null) {
  	printWriter.println(FVInputFileKeyword.CHECK_SPATIALLY_UNIFORM + " " + stopAtSpatiallyUniformErrorTolerance.getAbsoluteErrorTolerance() 
  			+ " " + stopAtSpatiallyUniformErrorTolerance.getRelativeErrorTolerance());
  }
	printWriter.println(FVInputFileKeyword.SIMULATION_PARAM_END);	
	printWriter.println();
}

/**
# Mesh file
MESH_BEGIN
VCG_FILE \\users\\fgao\\SimID_22489731_0_.vcg
MESH_END
 * @throws IOException 
*/
private void writeMeshFile() throws IOException {
	printWriter.println("# Mesh file");
	printWriter.println(FVInputFileKeyword.MESH_BEGIN);
	if (bInlineVCG) {
		GeometryFileWriter.write(printWriter, resampledGeometry);
	} else {
		printWriter.println(FVInputFileKeyword.VCG_FILE + " " + new File(workingDirectory, simTask.getSimulationJobID() + SimDataConstants.VCG_FILE_EXTENSION).getAbsolutePath());
	}	
	printWriter.println(FVInputFileKeyword.MESH_END);
	printWriter.println();
}

private double[] generateRandomNumbers(RandomVariable rv, int numRandomNumbers) throws ExpressionException {
	VariableDomain variableDomain = (rv instanceof VolumeRandomVariable) ? VariableDomain.VARIABLEDOMAIN_VOLUME : VariableDomain.VARIABLEDOMAIN_MEMBRANE;
	Expression seedExpr = subsituteExpression(rv.getSeed(), variableDomain);
	if (!seedExpr.isNumeric()) {
		throw new ExpressionException("Seed for RandomVariable '" + rv.getName() + " is not Constant!");
	}
	int seed = (int)rv.getSeed().evaluateConstant();
	Distribution distribution = rv.getDistribution();
	
	double[] randomNumbers = new double[numRandomNumbers];
	Random random = new Random(seed);
	
	if (distribution instanceof UniformDistribution) {
		UniformDistribution ud = (UniformDistribution)distribution;
		Expression minFlattened = subsituteExpression(ud.getMinimum(), variableDomain);
		Expression maxFlattened = subsituteExpression(ud.getMaximum(), variableDomain);
		
		if (!minFlattened.isNumeric()) {		
			throw new ExpressionException("For RandomVariable '" + rv.getName() + "', minimum for UniformDistribution is not Constant!");
		}
		if (!maxFlattened.isNumeric()) {		
			throw new ExpressionException("For RandomVariable '" + rv.getName() + "', maximum for UniformDistribution is not Constant!");
		}

		double minVal = minFlattened.evaluateConstant();
		double maxVal = maxFlattened.evaluateConstant();		
		for (int i = 0; i < numRandomNumbers; i++) {
			double r = random.nextDouble();
			randomNumbers[i] = (maxVal - minVal) * r + minVal;
		}
	} else if (distribution instanceof GaussianDistribution) {
		GaussianDistribution gd = (GaussianDistribution)distribution;
		Expression meanFlattened = subsituteExpression(gd.getMean(), variableDomain);
		Expression sdFlattened = subsituteExpression(gd.getStandardDeviation(), variableDomain);
		
		if (!meanFlattened.isNumeric()) {		
			throw new ExpressionException("For RandomVariable '" + rv.getName() + "', mean for GaussianDistribution is not Constant!");
		}
		if (!sdFlattened.isNumeric()) {		
			throw new ExpressionException("For RandomVariable '" + rv.getName() + "', standard deviation for GaussianDistribution is not Constant!");
		}
		
		double muVal = meanFlattened.evaluateConstant();
		double sigmaVal = sdFlattened.evaluateConstant();
		for (int i = 0; i < numRandomNumbers; i++) {
			double r = random.nextGaussian();
			randomNumbers[i] = sigmaVal * r + muVal;
		}		
	}
	return randomNumbers;
}

/**
# Variables : type name unit time_dependent_flag advection_flag solve_whole_mesh_flag solve_regions
VARIABLE_BEGIN
VOLUME_ODE rB uM
VOLUME_PDE rf uM false false
VOLUME_PDE r uM false false
VOLUME_ODE rfB uM
VARIABLE_END
 * @throws MathException 
 * @throws ExpressionException 
 * @throws IOException 
 */
private void writeVariables() throws MathException, ExpressionException, IOException {
	SimulationSymbolTable simSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();
	
	printWriter.println("# Variables : type name domain time_dependent_flag advection_flag grad_flag solve_whole_mesh_flag solve_regions");
	printWriter.println(FVInputFileKeyword.VARIABLE_BEGIN);
	MathDescription mathDesc = simSymbolTable.getSimulation().getMathDescription();
	Variable[] vars = simSymbolTable.getVariables();
	ArrayList<RandomVariable> rvList = new ArrayList<RandomVariable>();
	for (int i = 0; i < vars.length; i ++) {
		String varName = vars[i].getName();
		String domainName = vars[i].getDomain() == null ? null : vars[i].getDomain().getName();
		if (vars[i] instanceof VolumeRandomVariable || vars[i] instanceof MembraneRandomVariable) {
			rvList.add((RandomVariable)vars[i]);		
		} else if (vars[i] instanceof VolVariable) {
			if (bChomboSolver && domainName == null)
			{
				throw new MathException(simTask.getSimulation().getSolverTaskDescription().getSolverDescription().getDisplayLabel() + " requires that every variable is defined in a single domain");
			}
			VolVariable volVar = (VolVariable)vars[i];
			if (mathDesc.isPDE(volVar)) {
				boolean hasTimeVaryingDiffusionOrAdvection = simSymbolTable.hasTimeVaryingDiffusionOrAdvection(volVar);
				final boolean hasVelocity = mathDesc.hasVelocity(volVar);
				final boolean hasGradient = mathDesc.hasGradient(volVar);
				if (mathDesc.isPdeSteady(volVar)) {
					printWriter.print("VOLUME_PDE_STEADY ");
				} else {
					printWriter.print("VOLUME_PDE ");
				}
				printWriter.print(varName + " " + domainName + " " + hasTimeVaryingDiffusionOrAdvection + " " + hasVelocity + " " + hasGradient);
			} else {
				printWriter.print("VOLUME_ODE " + varName + " " + domainName);
			}

			if (domainName == null) {
				Vector<SubDomain> listOfSubDomains = new Vector<SubDomain>();
				int totalNumCompartments = 0;
				Enumeration<SubDomain> subDomainEnum = mathDesc.getSubDomains();
				while (subDomainEnum.hasMoreElements()){
			  		SubDomain subDomain = subDomainEnum.nextElement();
			 		if (subDomain instanceof CompartmentSubDomain){
				  		CompartmentSubDomain compartmentSubDomain = (CompartmentSubDomain)subDomain;
				  		totalNumCompartments++;
				  		Equation varEquation = subDomain.getEquation(vars[i]);
				  		if (varEquation != null) {
					  		if (!(varEquation instanceof PdeEquation) || !((PdeEquation)varEquation).isDummy(simSymbolTable, compartmentSubDomain)){
								listOfSubDomains.add(compartmentSubDomain);
					  		}
				  		}
			 		}
			  	}
				if ( (totalNumCompartments == listOfSubDomains.size()) ||
					 (listOfSubDomains.size() == 0 && simTask.getSimulation().getSolverTaskDescription().getSolverDescription().equals(SolverDescription.SundialsPDE))) {
					printWriter.print(" true");
				} else {
					printWriter.print(" false");
					for (int j = 0; j < listOfSubDomains.size(); j++){
						CompartmentSubDomain compartmentSubDomain = (CompartmentSubDomain)listOfSubDomains.elementAt(j);				  	
						printWriter.print(" " + compartmentSubDomain.getName());
					}
					
				}
				printWriter.println();
			} else {
				printWriter.println(" false " + domainName);
			}
		} else if (vars[i] instanceof VolumeParticleVariable) {
			printWriter.println(FVInputFileKeyword.VOLUME_PARTICLE + " " + varName + " " + domainName);
		} else if (vars[i] instanceof MembraneParticleVariable) {
			printWriter.println(FVInputFileKeyword.MEMBRANE_PARTICLE + " " + varName + " " + domainName);
		} else if (vars[i] instanceof VolumeRegionVariable) {
			printWriter.println("VOLUME_REGION " + varName + " " + domainName);
		} else if (vars[i] instanceof MemVariable) {
			if (bChomboSolver && domainName == null)
			{
				throw new MathException(simTask.getSimulation().getSolverTaskDescription().getSolverDescription().getDisplayLabel() + " requires that every variable is defined in a single domain");
			}
			MemVariable memVar = (MemVariable)vars[i];
			if (mathDesc.isPDE(memVar)) {
				printWriter.println("MEMBRANE_PDE " + varName + " " + domainName + " " + simSymbolTable.hasTimeVaryingDiffusionOrAdvection(memVar));
			} else {
				printWriter.println("MEMBRANE_ODE " + varName + " " + domainName);
			}
		} else if (vars[i] instanceof MembraneRegionVariable) {
			printWriter.println("MEMBRANE_REGION " + varName + " " + domainName);
		} else if (vars[i] instanceof FilamentVariable) {
			throw new RuntimeException("Filament application not supported yet");
		}
	}
	
	int numRandomVariables = rvList.size();
	if (numRandomVariables > 0) {
		ISize samplingSize = simTask.getSimulation().getMeshSpecification().getSamplingSize();
		String[] varNameArr = new String[numRandomVariables];
		VariableType[] varTypeArr = new VariableType[numRandomVariables];
		double[][] dataArr = new double[numRandomVariables][];
		for (int i = 0; i < numRandomVariables; i ++) {
			RandomVariable rv = rvList.get(i);
			varNameArr[i] = rv.getName();
			int numRandomNumbers = 0;
			if (rv instanceof VolumeRandomVariable) {
				printWriter.print("VOLUME_RANDOM");
				varTypeArr[i] = VariableType.VOLUME;
				numRandomNumbers = samplingSize.getXYZ();
			} else if (rv instanceof MembraneRandomVariable) {
				printWriter.print("MEMBRANE_RANDOM");
				varTypeArr[i] = VariableType.MEMBRANE;
				numRandomNumbers = resampledGeometry.getGeometrySurfaceDescription().getSurfaceCollection().getTotalPolygonCount();
			} else {
				throw new RuntimeException("Unknown RandomVariable type");
			}
			printWriter.println(" " + varNameArr[i]);
			dataArr[i] = generateRandomNumbers(rv, numRandomNumbers);
		}
		File rvFile = new File(workingDirectory, simTask.getSimulationJobID() + RANDOM_VARIABLE_FILE_EXTENSION);
		DataSet.writeNew(rvFile, varNameArr, varTypeArr, samplingSize, dataArr);
	}
	printWriter.println(FVInputFileKeyword.VARIABLE_END);
	printWriter.println();
}

/**
 * PARAMETER_BEGIN 3
 * D
 * U0
 * U1
 * PARAMETER_END
 */
private void writeParameters(String[] parameterNames) {
	if (parameterNames != null) {
		printWriter.println("# Parameters");
		printWriter.println(FVInputFileKeyword.PARAMETER_BEGIN + " " + parameterNames.length);
		for (int i = 0; i < parameterNames.length; i ++) {
			printWriter.println(parameterNames[i]);
		}
		printWriter.println(FVInputFileKeyword.PARAMETER_END);
		printWriter.println();
	}
}

private void writeSerialParameterScans() throws ExpressionException,  DivideByZeroException {
	Simulation simulation = simTask.getSimulation();
	if (!simulation.isSerialParameterScan()) {
		return;
	}

	int scanCount = simulation.getScanCount();
	printWriter.println("# Serial Scan Parameters");			
	String[] scanParameters = simulation.getMathOverrides().getOverridenConstantNames();
	printWriter.println("SERIAL_SCAN_PARAMETER_BEGIN " + scanParameters.length);
	for (int i = 0; i < scanParameters.length; i++){
		String scanParameter = scanParameters[i];
		printWriter.println(scanParameter);
	}
	printWriter.println("SERIAL_SCAN_PARAMETER_END");
	printWriter.println();
	
	printWriter.println("# Parameter Scan Values");
	printWriter.println("SERIAL_SCAN_PARAMETER_VALUE_BEGIN " + scanCount);
	for (int i = 0; i < scanCount; i ++) {
		for (int j = 0; j < scanParameters.length; j ++){
			String scanParameter = scanParameters[j];
			Expression exp = simulation.getMathOverrides().getActualExpression(scanParameter, i);
			double value = exp.evaluateConstant();
			printWriter.print(value + " ");
		}
		printWriter.println();
	}
	printWriter.println("SERIAL_SCAN_PARAMETER_VALUE_END");
	printWriter.println();
}
/**
 * # Field Data
 * FIELD_DATA_BEGIN
 * #id, name, varname, time filename
 * 0 _VCell_FieldData_0 FRAP_binding_ALPHA rfB 0.1 \\users\\fgao\\SimID_22489731_0_FRAP_binding_ALPHA_rfB_0_1.fdat
 * FIELD_DATA_END
 * @throws FileNotFoundException 
 * @throws ExpressionException 
 * @throws DataAccessException 
*/

private void writeFieldData() throws FileNotFoundException, ExpressionException, DataAccessException {
	FieldDataIdentifierSpec[] fieldDataIDSpecs = simTask.getSimulationJob().getFieldDataIdentifierSpecs();
	if (fieldDataIDSpecs == null || fieldDataIDSpecs.length == 0) {
		return;
	}

	String secondarySimDataDir = PropertyLoader.getProperty(PropertyLoader.secondarySimDataDirProperty, null);	
	DataSetControllerImpl dsci = new DataSetControllerImpl(new NullSessionLog(),null,workingDirectory.getParentFile(),
			secondarySimDataDir == null ? null : new File(secondarySimDataDir));
	
	printWriter.println("# Field Data");
	printWriter.println("FIELD_DATA_BEGIN");
	printWriter.println("#id, type, new name, name, varname, time, filename");
	
	FieldFunctionArguments psfFieldFunc = null;
	
	Variable var = simTask.getSimulationJob().getSimulationSymbolTable().getVariable(Simulation.PSF_FUNCTION_NAME);
	if (var != null) {
		FieldFunctionArguments[] ffas = FieldUtilities.getFieldFunctionArguments(var.getExpression());
		if (ffas == null || ffas.length == 0) {
			throw new DataAccessException("Point Spread Function " + Simulation.PSF_FUNCTION_NAME + " can only be a single field function.");
		} else {				
			Expression newexp = new Expression(ffas[0].infix());
			if (!var.getExpression().compareEqual(newexp)) {
				throw new DataAccessException("Point Spread Function " + Simulation.PSF_FUNCTION_NAME + " can only be a single field function.");
			}
			psfFieldFunc = ffas[0];
		}
	}
	
	int index = 0;
	HashSet<FieldDataIdentifierSpec> uniqueFieldDataIDSpecs = new HashSet<FieldDataIdentifierSpec>();
	uniqueFieldDataNSet = new HashSet<FieldDataNumerics>();
	for (int i = 0; i < fieldDataIDSpecs.length; i ++) {
		if(!uniqueFieldDataIDSpecs.contains(fieldDataIDSpecs[i])){
			FieldFunctionArguments ffa = fieldDataIDSpecs[i].getFieldFuncArgs();
			File newResampledFieldDataFile = new File(workingDirectory,
					SimulationData.createCanonicalResampleFileName((VCSimulationDataIdentifier)simTask.getSimulationJob().getVCDataIdentifier(),
							fieldDataIDSpecs[i].getFieldFuncArgs())
				);
			uniqueFieldDataIDSpecs.add(fieldDataIDSpecs[i]);
			VariableType varType = fieldDataIDSpecs[i].getFieldFuncArgs().getVariableType();
			SimDataBlock simDataBlock = dsci.getSimDataBlock(null,fieldDataIDSpecs[i].getExternalDataIdentifier(),fieldDataIDSpecs[i].getFieldFuncArgs().getVariableName(), fieldDataIDSpecs[i].getFieldFuncArgs().getTime().evaluateConstant());
			VariableType dataVarType = simDataBlock.getVariableType();			
			if (varType.equals(VariableType.UNKNOWN)) {
				varType = dataVarType;
			} else if (!varType.equals(dataVarType)) {
				throw new IllegalArgumentException("field function variable type (" + varType.getTypeName() + ") doesn't match real variable type (" + dataVarType.getTypeName() + ")");
			}			
			if (psfFieldFunc != null && psfFieldFunc.equals(ffa)) {
				psfFieldIndex = index;
			}
			String fieldDataID = "_VCell_FieldData_" + index;
			printWriter.println(index + " " + varType.getTypeName() + " " + fieldDataID + " " + ffa.getFieldName() + " " + ffa.getVariableName() + " " + ffa.getTime().infix() + " " + newResampledFieldDataFile);
			uniqueFieldDataNSet.add(
				new FieldDataNumerics(
					SimulationData.createCanonicalFieldFunctionSyntax(
						ffa.getFieldName(),
						ffa.getVariableName(),
						ffa.getTime().evaluateConstant(),
						ffa.getVariableType().getTypeName()),
					fieldDataID));
			index ++;
		}
	}	
	
	if (psfFieldIndex >= 0) {
		printWriter.println("PSF_FIELD_DATA_INDEX " + psfFieldIndex);
	}
	printWriter.println("FIELD_DATA_END");
	printWriter.println();
}


/**
EQUATION_BEGIN rf
INITIAL 5.0;
RATE ( - ((0.02 * ( - rB - rfB + (20.0 * ((x > -3.0) && (x < 3.0) && (y > -5.0) && (y < 5.0))) + _VCell_FieldData_0) * rf) - (0.1 * rfB)) - (50.0 * rf * ((x > -5.0) && (x < 5.0) && (y > -5.0) && (y < 5.0))));
DIFFUSION 10.0;
BOUNDARY_XM 5.0;
BOUNDARY_XP 5.0;
BOUNDARY_YM 5.0;
BOUNDARY_YP 5.0;
EQUATION_END
 * @throws ExpressionException 
 * @throws MathException 
 */
private void writeMembrane_VarContext_Equation(MembraneSubDomain memSubDomain, Equation equation) throws ExpressionException, MathException {	
	printWriter.println("EQUATION_BEGIN " + equation.getVariable().getName());
	printWriter.println("INITIAL " + subsituteExpression(equation.getInitialExpression(), VariableDomain.VARIABLEDOMAIN_MEMBRANE).infix() + ";");
	Expression rateExpression = subsituteExpression(equation.getRateExpression(), VariableDomain.VARIABLEDOMAIN_MEMBRANE);
	printWriter.println("RATE " + replaceVolumeVariable(simTask, memSubDomain, rateExpression) + ";");
	if (bChomboSolver && equation.getExactSolution() != null)
	{
		printWriter.println(FVInputFileKeyword.EXACT + " " + subsituteExpression(equation.getExactSolution(), VariableDomain.VARIABLEDOMAIN_VOLUME).infix() + ";");
	}
	if (equation instanceof PdeEquation) {
		printWriter.println("DIFFUSION " + subsituteExpression(((PdeEquation)equation).getDiffusionExpression(), VariableDomain.VARIABLEDOMAIN_MEMBRANE).infix() + ";");
		
		PdeEquation pde = (PdeEquation)equation;
		BoundaryConditionType[] bctypes = new BoundaryConditionType[] {
				memSubDomain.getInsideCompartment().getBoundaryConditionXm(),
				memSubDomain.getInsideCompartment().getBoundaryConditionXp(),
				memSubDomain.getInsideCompartment().getBoundaryConditionYm(),
				memSubDomain.getInsideCompartment().getBoundaryConditionYp(),
				memSubDomain.getInsideCompartment().getBoundaryConditionZm(),
				memSubDomain.getInsideCompartment().getBoundaryConditionZp()
		};
		writeBoundaryValues(bctypes, pde, VariableDomain.VARIABLEDOMAIN_MEMBRANE);		
	}	

//	if (simulation.getSolverTaskDescription().getSolverDescription().equals(SolverDescription.SundialsPDE)) {
//		StringBuffer rateDerivativeString = new StringBuffer(); 
//		Variable[] vars = simulation.getVariables();
//		int count = 0;
//		for (Variable var : vars) {
//			if (var instanceof MemVariable) {
//				Expression exp = rateExpression.differentiate(var.getName());
//				rateDerivativeString.append(exp.flatten().infix() + ";\n");
//				count ++;
//			}
//		}
//		printWriter.println("RATE_DERIVATIVES " + count);
//		printWriter.print(rateDerivativeString);
//	}
	
	printWriter.println("EQUATION_END");
	printWriter.println();
}


/**
 EQUATION_BEGIN rf
INITIAL 5.0;
RATE ( - ((0.02 * ( - rB - rfB + (20.0 * ((x > -3.0) && (x < 3.0) && (y > -5.0) && (y < 5.0))) + _VCell_FieldData_0) * rf) - (0.1 * rfB)) - (50.0 * rf * ((x > -5.0) && (x < 5.0) && (y > -5.0) && (y < 5.0))));
DIFFUSION 10.0;
BOUNDARY_XM 5.0;
BOUNDARY_XP 5.0;
BOUNDARY_YM 5.0;
BOUNDARY_YP 5.0;
EQUATION_END
 * @throws ExpressionException 
 */
private void writeCompartmentRegion_VarContext_Equation(CompartmentSubDomain volSubDomain, VolumeRegionEquation equation) throws ExpressionException {	
	printWriter.println("EQUATION_BEGIN " + equation.getVariable().getName());
	printWriter.println("INITIAL " + subsituteExpression(equation.getInitialExpression(), VariableDomain.VARIABLEDOMAIN_VOLUME).infix() + ";");
	printWriter.println("RATE " + subsituteExpression(equation.getVolumeRateExpression(), VariableDomain.VARIABLEDOMAIN_VOLUME).infix() + ";");
	printWriter.println("UNIFORMRATE " + subsituteExpression(equation.getUniformRateExpression(), VariableDomain.VARIABLEDOMAIN_VOLUME).infix() + ";");
//	printWriter.println("FLUX " + subsituteExpression(equation.getMembraneRateExpression()).infix() + ";");
	printWriter.println("EQUATION_END");
	printWriter.println();
}

	private void assignPhases(CompartmentSubDomain[] subDomains, int startingIndex, int[] phases, int[] numAssigned) throws SolverException 
	{
		if (phases[startingIndex] == -1) 
		{
			return;
		}
		if (numAssigned[0] == subDomains.length) 
		{
			return;
		}
		for (int j = subDomains.length - 1; j >= 0 ; j --) 
		{
			if (startingIndex == j) 
			{
				continue;
			}
			 
			MembraneSubDomain mem = getSimulationTask().getSimulation().getMathDescription().getMembraneSubDomain(subDomains[startingIndex], subDomains[j]);
			if (mem != null) 
			{
				int newPhase = phases[startingIndex] == 0 ? 1 : 0;
				if (phases[j] == -1)
				{
					numAssigned[0] ++;
					// membrane between i and j, different phase
					phases[j] = newPhase;
					assignPhases(subDomains, j, phases, numAssigned);
				}
				else if (phases[j] != newPhase)
				{
					throw new SolverException("Geometry is invalid for " + getSimulationTask().getSimulation().getSolverTaskDescription().getSolverDescription().getDisplayLabel() 
							+ ": domains are not properly nested (membrane branching). \n\n(domain " + subDomains[j].getName() + " was assigned to phase " + phases[j] + ", is again being assigned to phase " +  newPhase + ".)");
				}
			}
		}
	}

	private void writeDistanceMapFile(Vect3d deltaX, SubvolumeSignedDistanceMap distanceMap, File file) throws IOException {
		Origin origin = resampledGeometry.getOrigin();
		String headerNames[] = {
				"dimension",
				"Nx",
				"Ny",
				"Nz",
				"Dx",
				"Dy",
				"Dz",
				"firstX",
				"firstY",
				"firstZ",
			};
		double[] pointsX = distanceMap.getSamplesX();
		double[] pointsY = distanceMap.getSamplesY();
		double[] pointsZ = distanceMap.getSamplesZ();
		double[] distances = distanceMap.getSignedDistances();
		int Nx = pointsX.length;
		int Ny = pointsY.length;
		int Nz = pointsZ.length;
		double Dx = deltaX.getX();
		double Dy = deltaX.getY();
		double Dz = deltaX.getZ();
		
		assert Math.abs(origin.getX() - pointsX[0]) < 2e-8;
		assert Math.abs(origin.getY() - pointsY[0]) < 2e-8;
		//Chombo 2D uses middle slice, so skip assert in that case
		assert Math.abs(origin.getZ() - pointsZ[0]) < 2e-8 || bChomboSolver && resampledGeometry.getDimension() == 2 : "z not starting at origin";
		
		
		double[] header = new double[headerNames.length];
		int count = -1;
		header[++count] = resampledGeometry.getDimension();
		header[++count] = Nx;
		header[++count] = Ny;
		header[++count] = Nz;
		header[++count] = Dx;
		header[++count] = Dy;
		header[++count] = Dz;
		header[++count] = origin.getX();
		header[++count] = origin.getY();
		header[++count] = origin.getZ();
		
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			for (double d : header) {
				dos.writeDouble(d);			
			}
			
			for (double d : distances) {
				dos.writeDouble(d);
			}
		} finally {
			if (dos != null) {
				dos.close();
			}
		}
	}
	
	private void writeChomboSpec() throws ExpressionException, SolverException, PropertyVetoException, ClassNotFoundException, IOException, GeometryException, ImageException {
		if (!bChomboSolver) {
			return;
		}
		
		GeometrySpec geometrySpec = resampledGeometry.getGeometrySpec();
		int dimension = geometrySpec.getDimension();
		if (dimension == 1)
		{
			throw new SolverException(simTask.getSimulation().getSolverTaskDescription().getSolverDescription().getDisplayLabel() + " is only supported for simulations with 2D or 3D geometry.");
		}
		Simulation simulation = getSimulationTask().getSimulation();
		SolverTaskDescription solverTaskDescription = simulation.getSolverTaskDescription();
		ChomboSolverSpec chomboSolverSpec = solverTaskDescription.getChomboSolverSpec();
		printWriter.println(FVInputFileKeyword.CHOMBO_SPEC_BEGIN);
		printWriter.println(FVInputFileKeyword.DIMENSION + " " + geometrySpec.getDimension());
		Extent extent = geometrySpec.getExtent();
		Origin origin = geometrySpec.getOrigin();	
		ISize isize = simulation.getMeshSpecification().getSamplingSize();
		switch (geometrySpec.getDimension()) {
			case 2:
				printWriter.println(FVInputFileKeyword.MESH_SIZE + " " + isize.getX() + " " + isize.getY());
				printWriter.println(FVInputFileKeyword.DOMAIN_SIZE + " " + extent.getX() + " " + extent.getY());
				printWriter.println(FVInputFileKeyword.DOMAIN_ORIGIN + " " + origin.getX() + " " + origin.getY());
				break;
			case 3:
				printWriter.println(FVInputFileKeyword.MESH_SIZE + " " + isize.getX() + " " + isize.getY() + " " + isize.getZ());
				printWriter.println(FVInputFileKeyword.DOMAIN_SIZE + " " + extent.getX() + " " + extent.getY() + " " + extent.getZ());
				printWriter.println(FVInputFileKeyword.DOMAIN_ORIGIN + " " + origin.getX() + " " + origin.getY() + " " + origin.getZ());
				break;
		}
		List<CompartmentSubDomain> featureList = new ArrayList<CompartmentSubDomain>();
		Enumeration<SubDomain> enum1 = simulation.getMathDescription().getSubDomains();
		while (enum1.hasMoreElements())
		{
			SubDomain sd = enum1.nextElement();
			if (sd instanceof CompartmentSubDomain)
			{
				featureList.add((CompartmentSubDomain) sd);
			}
		}
		int numFeatures = featureList.size();
		CompartmentSubDomain[] features = featureList.toArray(new CompartmentSubDomain[0]);
		int phases[] = new int[numFeatures];
		Arrays.fill(phases, -1);
		phases[numFeatures - 1] = 0;
		int[] numAssigned = new int[] {1};
		assignPhases(features, numFeatures - 1, phases, numAssigned);
			
		Map<String, Integer> subDomainPhaseMap = new HashMap<String, Integer>();
		for (int i = 0; i < phases.length; ++ i)
		{
			if (phases[i] == -1)
			{
				throw new SolverException("Failed to assign a phase to CompartmentSubdomain '" + features[i].getName()  + "'. It might be caused by too coarsh a mesh.");
			}
			subDomainPhaseMap.put(features[i].getName(), phases[i]);
		}
		
		SubVolume[] subVolumes = geometrySpec.getSubVolumes();
		if (geometrySpec.hasImage()) {
			Geometry geometry = (Geometry) BeanUtils.cloneSerializable(simulation.getMathDescription().getGeometry());
			Geometry simGeometry = geometry;
			VCImage img = geometry.getGeometrySpec().getImage();
			
			int factor = Math.max(Math.max(img.getNumX(), img.getNumY()), img.getNumZ()) < 512 ? 2 : 1;
			ISize distanceMapMeshSize = new ISize(img.getNumX() * factor, img.getNumY() * factor, img.getNumZ() * factor);
			Vect3d deltaX = null;
			boolean bCellCentered = false; 
			double dx = 0.5;
			double dy = 0.5;
			double dz = 0.5;
			int Nx = distanceMapMeshSize.getX();
			int Ny = distanceMapMeshSize.getY();
			int Nz = distanceMapMeshSize.getZ();
			if (dimension == 2) {
				// pad the 2D image with itself in order to obtain a 3D image used to compute the distance map
				// because the distance map algorithm is 3D only (using distance to triangles)
				byte[] oldPixels = img.getPixels();
				byte[] newPixels = new byte[oldPixels.length*3];
				System.arraycopy(oldPixels, 0, newPixels, 0, oldPixels.length);
				System.arraycopy(oldPixels, 0, newPixels, oldPixels.length, oldPixels.length);
				System.arraycopy(oldPixels, 0, newPixels, oldPixels.length*2, oldPixels.length);
			
				double distX = geometry.getExtent().getX()/img.getNumX();
				double distY = geometry.getExtent().getY()/img.getNumY();
				double distZ = Math.max(distX, distY);	// we set the distance on the z axis to something that makes sense
				Extent newExtent = new Extent(geometry.getExtent().getX(), geometry.getExtent().getY(), distZ * 3);
				VCImage newImage = new VCImageUncompressed(null, newPixels, newExtent, img.getNumX(), img.getNumY(), 3);
				// copy the pixel classes too
				ArrayList<VCPixelClass> newPixelClasses = new ArrayList<VCPixelClass>();
				for (VCPixelClass origPixelClass : geometry.getGeometrySpec().getImage().getPixelClasses()){
					SubVolume origSubvolume = geometry.getGeometrySpec().getImageSubVolumeFromPixelValue(origPixelClass.getPixel());
					newPixelClasses.add(new VCPixelClass(null,  origSubvolume.getName(), origPixelClass.getPixel()));
				}
				newImage.setPixelClasses(newPixelClasses.toArray(new VCPixelClass[newPixelClasses.size()]));
				simGeometry = new Geometry(geometry, newImage);
				
				Nz = 3;
			}
			GeometrySpec simGeometrySpec = simGeometry.getGeometrySpec();
			Extent simExtent = simGeometrySpec.getExtent();
			dx = simExtent.getX()/(Nx - 1);
			dy = simExtent.getY()/(Ny - 1);
			dz = simExtent.getZ()/(Nz - 1);
			if (Math.abs(dx - dy) > 0.1 * Math.max(dx, dy))
			{
				dx = Math.min(dx, dy);
				dy = dx;
				Nx = (int)(simExtent.getX()/dx + 1);
				Ny = (int)(simExtent.getY()/dx + 1);
				if (dimension == 3)
				{
					dz = dx;
					Nz = (int)(simExtent.getZ()/dx + 1);
				}
			}
			deltaX = new Vect3d(dx, dy, dz);
			//one more point in each direction
			distanceMapMeshSize = new ISize(Nx+1, Ny+1, Nz+1);
			Extent distanceMapExtent = new Extent(simExtent.getX() + dx, simExtent.getY() + dy, simExtent.getZ() + dz);
			simGeometrySpec.setExtent(distanceMapExtent);
			GeometrySurfaceDescription geoSurfaceDesc = simGeometry.getGeometrySurfaceDescription();
			geoSurfaceDesc.setVolumeSampleSize(distanceMapMeshSize);
			geoSurfaceDesc.updateAll();
			VCImage vcImage = RayCaster.sampleGeometry(simGeometry, distanceMapMeshSize, bCellCentered);
			SubvolumeSignedDistanceMap[] distanceMaps = DistanceMapGenerator.computeDistanceMaps(simGeometry, vcImage, bCellCentered);
//			for(int i=0; i<distanceMaps.length; i++) {
//				// save the maps in VisIt compatible format
//				SubvolumeSignedDistanceMap ssdm = distanceMaps[i];
//				ssdm.to3DFile(i);
//			}
			
			if (dimension == 2)
			{
				distanceMaps = DistanceMapGenerator.extractMiddleSlice(distanceMaps);
			}
			
			printWriter.println(FVInputFileKeyword.SUBDOMAINS + " " + simGeometrySpec.getNumSubVolumes() + " " + FVInputFileKeyword.DISTANCE_MAP);
			for (int i = 0; i < subVolumes.length; i++) {
				File distanceMapFile = new File(workingDirectory, getSimulationTask().getSimulationJobID() + "_" + subVolumes[i].getName() + DISTANCE_MAP_FILE_EXTENSION);
				writeDistanceMapFile(deltaX, distanceMaps[i], distanceMapFile);
				int phase = subDomainPhaseMap.get(subVolumes[i].getName());
				printWriter.println(subVolumes[i].getName() + " " + phase + " " + distanceMapFile.getAbsolutePath());
			}
		} else {
			printWriter.println(FVInputFileKeyword.SUBDOMAINS + " " + geometrySpec.getNumSubVolumes());
			Expression[] rvachevExps = convertAnalyticGeometryToRvachevFunction(geometrySpec);
			for (int i = 0; i < subVolumes.length; i++) {
				if (subVolumes[i] instanceof AnalyticSubVolume) {
					String name = subVolumes[i].getName();
					int phase = subDomainPhaseMap.get(name);
					printWriter.println(name + " " + phase + " ");
					printWriter.println(FVInputFileKeyword.IF + " "  + rvachevExps[i].infix() + ";");			
					printWriter.println(FVInputFileKeyword.USER + " "  + ((AnalyticSubVolume)subVolumes[i]).getExpression().infix() + ";");			
				}
			}
		}
		
		printWriter.println(FVInputFileKeyword.MAX_BOX_SIZE + " " + chomboSolverSpec.getMaxBoxSize());
		printWriter.println(FVInputFileKeyword.FILL_RATIO + " " + chomboSolverSpec.getFillRatio());
		printWriter.println(FVInputFileKeyword.RELATIVE_TOLERANCE + " " + simulation.getSolverTaskDescription().getErrorTolerance().getRelativeErrorTolerance());
		printWriter.println(FVInputFileKeyword.SAVE_VCELL_OUTPUT + " " + chomboSolverSpec.isSaveVCellOutput());
		printWriter.println(FVInputFileKeyword.SAVE_CHOMBO_OUTPUT + " " + chomboSolverSpec.isSaveChomboOutput());
		printWriter.println(FVInputFileKeyword.ACTIVATE_FEATURE_UNDER_DEVELOPMENT + " " + chomboSolverSpec.isActivateFeatureUnderDevelopment());
		printWriter.println(FVInputFileKeyword.SMALL_VOLFRAC_THRESHOLD + " " + chomboSolverSpec.getSmallVolfracThreshold());
		
		// Refinement
		int numLevels = chomboSolverSpec.getNumRefinementLevels();
		// Refinements #Levels ratio 1, ratio 2, etc
		printWriter.print(FVInputFileKeyword.REFINEMENTS + " " + (numLevels + 1));
		List<Integer> ratios = chomboSolverSpec.getRefineRatioList();
		for (int i: ratios) {		
			printWriter.print(" " + i); 
		}
		printWriter.println(" 2"); // write last refinement ratio, fake
		
		// membrane rois
		List<RefinementRoi> memRios = chomboSolverSpec.getMembraneRefinementRois();
		printWriter.println(FVInputFileKeyword.REFINEMENT_ROIS + " " + RoiType.Membrane + " " + memRios.size());
		for (RefinementRoi roi: memRios) {
			if (roi.getRoiExpression() == null)
			{
				throw new SolverException("ROI expression cannot be null");
			}
			// level tagsGrow ROIexpression
			printWriter.println(roi.getLevel() + " " + roi.getTagsGrow() + " " + roi.getRoiExpression().infix()  + ";");
		}
		
		List<RefinementRoi> volRios = chomboSolverSpec.getVolumeRefinementRois();
		printWriter.println(FVInputFileKeyword.REFINEMENT_ROIS + " " + RoiType.Volume + " " + volRios.size());
		for (RefinementRoi roi: volRios) {
			if (roi.getRoiExpression() == null)
			{
				throw new SolverException("ROI expression cannot be null");
			}
			printWriter.println(roi.getLevel() + " " + roi.getTagsGrow() + " " + roi.getRoiExpression().infix()  + ";");
		}
		printWriter.println(FVInputFileKeyword.VIEW_LEVEL + " " + chomboSolverSpec.getViewLevel());
		printWriter.println(FVInputFileKeyword.CHOMBO_SPEC_END);
		printWriter.println();
	}

	public static Expression[] convertAnalyticGeometryToRvachevFunction(GeometrySpec geoSpec) throws ExpressionException {
		SubVolume[] subVolumes = geoSpec.getSubVolumes();
		int numSubVolumes = subVolumes.length;
		Expression[] newExps = new Expression[numSubVolumes];
		if (numSubVolumes == 1) {
			newExps[0] = new Expression(-1.0);			
		} else {
			for (int i = 0; i < numSubVolumes - 1; i ++) {
				if (!(subVolumes[i] instanceof AnalyticSubVolume)) {
					throw new RuntimeException("Subdomain " + i + " is not a analytic subdomain.");
				}
				AnalyticSubVolume subvolume = (AnalyticSubVolume)subVolumes[i];
				try
				{
					newExps[i] = RvachevFunctionUtils.convertToRvachevFunction(subvolume.getExpression());
					if (newExps[numSubVolumes - 1] == null) {
						newExps[numSubVolumes - 1] = Expression.negate(newExps[i]);
					} else {
						newExps[numSubVolumes - 1] = Expression.max(newExps[numSubVolumes - 1], Expression.negate(newExps[i]));
					}
				}
				catch (ExpressionException ex)
				{
					throw new ExpressionException("Domain " + subvolume.getName() + ": " + ex.getMessage());
				}
			}
			for (int i = 1; i < numSubVolumes - 1; i ++) {
				for (int j = 0; j < i; j ++) {
					newExps[i] = Expression.max(newExps[i], Expression.negate(newExps[j]));
				}			
			}
		}		
		return newExps;
	}
	
	private void writeChomboBoundaryValues(BoundaryConditionType[] bctypes, PdeEquation pde, VariableDomain variableDomain) throws ExpressionException {
		int dimension = resampledGeometry.getDimension();
		
		String[] bctitles = new String[]{"BOUNDARY_XM", "BOUNDARY_XP", "BOUNDARY_YM", "BOUNDARY_YP", "BOUNDARY_ZM", "BOUNDARY_ZP"};
		Expression[] bcs = new Expression[] {
			pde.getBoundaryXm(),
			pde.getBoundaryXp(),
			pde.getBoundaryYm(),
			pde.getBoundaryYp(),
			pde.getBoundaryZm(),
			pde.getBoundaryZp()
		};
		
		for (int i = 0; i < 2 * dimension; i ++) {		 
			Expression valueExp = null;
			if (bcs[i] == null) { // not defined by user
				if (bctypes[i].isDIRICHLET()){		
					valueExp = pde.getInitialExpression();   // use initial condition for VALUE
				} else {
					valueExp = new Expression("0.0");        // user 0 for FLUX
				}
			} else { // defined by user
				if (bctypes[i].isDIRICHLET())
				{
					valueExp = bcs[i];
				}
				else if (bctypes[i].isNEUMANN())
				{
					// for EBChombo operators, flux is replaced by flux/D because the operator multiplies by D
					valueExp = Expression.div(bcs[i], pde.getDiffusionExpression());
					if (i % 2 == 0)
					{
					  // for EBChombo operators, the flux is defined in the inward direction, i.e. change sign for xm,ym,zm
						valueExp = Expression.negate(valueExp);
					}
				}
				
			}
			if (valueExp != null) {
				printWriter.println(bctitles[i] + " " + subsituteExpression(valueExp, variableDomain).infix() + ";");
			}
		}
	}

}
