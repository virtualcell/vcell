package cbit.vcell.solvers;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.vcell.util.DataAccessException;
import org.vcell.util.ISize;
import org.vcell.util.NullSessionLog;

import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.surface.GeometryFileWriter;
import cbit.vcell.mapping.FastSystemAnalyzer;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.math.CompartmentSubDomain;
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
import cbit.vcell.math.MembraneRandomVariable;
import cbit.vcell.math.MembraneRegionEquation;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.PseudoConstant;
import cbit.vcell.math.RandomVariable;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.UniformDistribution;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.VolumeRandomVariable;
import cbit.vcell.math.VolumeRegionEquation;
import cbit.vcell.math.VolumeRegionVariable;
import cbit.vcell.parser.Discontinuity;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.simdata.DataSet;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.DataProcessingInstructions;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverFileWriter;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.VCSimulationDataIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (5/9/2005 2:51:48 PM)
 * @author: Fei Gao
 */
public class FiniteVolumeFileWriter extends SolverFileWriter {
	private static final String VCG_FILE_EXTENSION = ".vcg";
	private static final String RANDOM_VARIABLE_FILE_EXTENSION = ".rv";
	private File userDirectory = null;
	private boolean bInlineVCG = false;
	private Geometry resampledGeometry = null;
	private int psfFieldIndex = -1;
	
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

public FiniteVolumeFileWriter(PrintWriter pw, SimulationJob simJob, Geometry geo, File dir) {	// for optimization only, no messaging
	this (pw, simJob, geo, dir, false);
	bInlineVCG = true; 
}

public FiniteVolumeFileWriter(PrintWriter pw, SimulationJob simJob, Geometry geo, File dir, boolean arg_bMessaging) {
	super(pw, simJob, arg_bMessaging);
	resampledGeometry = geo;
	userDirectory = dir;
}

private Expression subsituteExpression(Expression exp) throws ExpressionException  {
	return subsituteExpression(exp, simulationJob.getSimulationSymbolTable());
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 * @throws ExpressionException 
 */
private Expression subsituteExpression(Expression exp, SymbolTable symbolTable) throws ExpressionException {
	Expression exp2 = MathUtilities.substituteFunctions(exp, symbolTable).flatten();
	FieldFunctionArguments[] fieldFunctionArguments = FieldUtilities.getFieldFunctionArguments(exp2);
	if (fieldFunctionArguments != null && fieldFunctionArguments.length > 0) {
		if (uniqueFieldDataNSet != null && uniqueFieldDataNSet.size() > 0) {	
			for (FieldDataNumerics fdn: uniqueFieldDataNSet) {
				exp2.substituteInPlace(new Expression(fdn.getFieldFunction()), new Expression(fdn.getNumericsSubsitute()));
			}
		} else {
			throw new RuntimeException("Didn't find field functions in simulation when preprocessing, but expression [" + exp.infix() + "] has field function " + exp2.infix() + " in it");
		}
	}
	return exp2;
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 * @throws MathException 
 * @throws ExpressionException 
 * @throws DataAccessException 
 * @throws IOException 
 */
public void write(String[] parameterNames) throws ExpressionException, MathException, DataAccessException, IOException {	
	writeJMSParamters();	
	writeSimulationParamters();	
	writeModelDescription();	
	writeMeshFile();	
	writeVariables();
	writeParameters(parameterNames);	
	writeFieldData();	
	writeDataProcessor();
	writeCompartments();	
	writeMembranes();
}

/**
DATA_PROCESSOR_BEGIN VFRAP
VolumePoints 49
2667 2676 2679 2771 2969 2877 3067 3277 3185 3283 3473 3580 3690 3687 3878 4086 3990 4182 4193 1077
2257 1984 2269 2648 3561 2890 3116 4104 4383 3995 4561 3909 3816 3820 5024 4429 4979 5102 6011 6094
6338 6081 6527 7705 7305 8040 7423 8105 8023
SampleImage 41 0 32742266 field(imageFieldDataName1,mask,0.0,Volume)
StoreEnabled false

SampleImageFile mask 0.0 \\\\cfs01.vcell.uchc.edu\\raid\\Vcell\\users\\schaff\\SimID_32742646_0_imageFieldDataName1_mask_0_0_Volume.fdat
DATA_PROCESSOR_END
 * @throws MathException 
 * @throws IOException 
 * @throws DataAccessException 
 * @throws ExpressionException 
 * @throws DivideByZeroException 
*/
private void writeDataProcessor() throws DataAccessException, IOException, MathException, DivideByZeroException, ExpressionException {
	Simulation simulation = simulationJob.getSimulation();
	DataProcessingInstructions dpi = simulation.getDataProcessingInstructions();
	if (dpi == null) {
		return;
	}
	
	FieldDataIdentifierSpec fdis = dpi.getSampleImageFieldData(simulation.getVersion().getOwner());
	
	DataSetControllerImpl dsci = new DataSetControllerImpl(new NullSessionLog(),null,userDirectory.getParentFile(),null);
	CartesianMesh origMesh = dsci.getMesh(fdis.getExternalDataIdentifier());
	SimDataBlock simDataBlock = dsci.getSimDataBlock(fdis.getExternalDataIdentifier(), fdis.getFieldFuncArgs().getVariableName(), fdis.getFieldFuncArgs().getTime().evaluateConstant());
	VariableType varType = fdis.getFieldFuncArgs().getVariableType();
	VariableType dataVarType = simDataBlock.getVariableType();
	if (!varType.equals(VariableType.UNKNOWN) && !varType.equals(dataVarType)) {
		throw new IllegalArgumentException("field function variable type (" + varType.getTypeName() + ") doesn't match real variable type (" + dataVarType.getTypeName() + ")");
	}
	double[] origData = simDataBlock.getData();	

	String filename = SimulationJob.createSimulationJobID(Simulation.createSimulationID(simulation.getKey()), simulationJob.getJobIndex()) + FieldDataIdentifierSpec.getDefaultFieldDataFileNameForSimulation(fdis.getFieldFuncArgs());
	
	File fdatFile = new File(userDirectory, filename);
	
	
	cbit.vcell.simdata.DataSet.writeNew(fdatFile,
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

/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 */
private void writeCompartment_boundaryConditions(CompartmentSubDomain csd) {
	printWriter.print("BOUNDARY_CONDITIONS ");
	writeFeature_boundaryConditions(csd);
	printWriter.println();
}


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
private void writeCompartment_FastSystem(CompartmentSubDomain volSubDomain) throws ExpressionException, MathException {
	FastSystem fastSystem = volSubDomain.getFastSystem();
	if (fastSystem == null) {
		return;
	}
	FastSystemAnalyzer fs_analyzer = new FastSystemAnalyzer(fastSystem, simulationJob.getSimulationSymbolTable());
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
			printWriter.println(pc.getName() + " " + subsituteExpression(pc.getPseudoExpression(), fs_analyzer).infix() + ";");
		}
		printWriter.println("PSEUDO_CONSTANT_END");
		printWriter.println();			
	}
	
	if (numIndep != 0) {
		printWriter.println("FAST_RATE_BEGIN" );
		Enumeration<Expression> enum1 = fs_analyzer.getFastRateExpressions();
		while (enum1.hasMoreElements()) {
			Expression exp = enum1.nextElement();	
			printWriter.println(subsituteExpression(exp, fs_analyzer).infix() + ";");
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
			printWriter.println(depVar.getName() + " " + subsituteExpression(exp, fs_analyzer).infix() + ";");
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
				Expression exp = subsituteExpression(fre, fs_analyzer).flatten();
				Expression differential = exp.differentiate(var.getName());
				printWriter.println(subsituteExpression(differential, fs_analyzer).infix() + ";");
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
	Simulation simulation = simulationJob.getSimulation();
	//
	// get list of volVariables participating in PDEs (anywhere).
	//
	Vector<VolVariable> pdeVolVariableList = new Vector<VolVariable>();
	Variable[] variables = simulationJob.getSimulationSymbolTable().getVariables();
	for (int i = 0; i < variables.length; i++){
		if (variables[i] instanceof VolVariable && simulation.getMathDescription().isPDE((VolVariable)variables[i])){
			pdeVolVariableList.add((VolVariable)variables[i]);
		}
	}

	Enumeration<Equation> enum_equ = volSubDomain.getEquations();
	while (enum_equ.hasMoreElements()){
		Equation equation = enum_equ.nextElement();
		if (equation instanceof VolumeRegionEquation){
			writeCompartmentRegion_VarContext_Equation(volSubDomain, (VolumeRegionEquation)equation);
		} else {
			writeCompartment_VarContext_Equation(volSubDomain, equation);
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
	for (int i = 0; i < pdeVolVariableList.size(); i++){
		VolVariable volVar = pdeVolVariableList.elementAt(i);
		boolean bSteady = simulation.getMathDescription().isPdeSteady(volVar);
		PdeEquation dummyPdeEquation = new PdeEquation(volVar, bSteady, new Expression(0.0), new Expression(0.0), new Expression(0.0));
		writeCompartment_VarContext_Equation(volSubDomain, dummyPdeEquation);
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
	printWriter.println("EQUATION_BEGIN " + equation.getVariable().getName());
	printWriter.println("INITIAL " + subsituteExpression(equation.getInitialExpression()).infix() + ";");
	Expression rateExpression = subsituteExpression(equation.getRateExpression());
	printWriter.println("RATE " + rateExpression.infix() + ";");
	if (equation instanceof PdeEquation) {
		printWriter.println("DIFFUSION " + subsituteExpression(((PdeEquation)equation).getDiffusionExpression()).infix() + ";");
		if (((PdeEquation)equation).getVelocityX() != null) {
			printWriter.println("VELOCITY_X " + subsituteExpression(((PdeEquation)equation).getVelocityX()).infix() + ";");
		} else {
			printWriter.println("VELOCITY_X 0.0;");
		}
		if (((PdeEquation)equation).getVelocityY() != null) {
			printWriter.println("VELOCITY_Y " + subsituteExpression(((PdeEquation)equation).getVelocityY()).infix() + ";");
		} else if (resampledGeometry.getDimension() > 1) {
			printWriter.println("VELOCITY_Y 0.0;");
		}
		if (((PdeEquation)equation).getVelocityZ() != null) {
			printWriter.println("VELOCITY_Z " + subsituteExpression(((PdeEquation)equation).getVelocityZ()).infix() + ";");			
		} else if (resampledGeometry.getDimension() > 2) {
			printWriter.println("VELOCITY_Z 0.0;");
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
		writeBoundaryValues(bctypes, pde);
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
	printWriter.println("EQUATION_END");
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
	Simulation simulation = simulationJob.getSimulation();
	MathDescription mathDesc = simulation.getMathDescription();
	Enumeration<SubDomain> enum1 = mathDesc.getSubDomains();
	while (enum1.hasMoreElements()) {		
		SubDomain sd = enum1.nextElement();
		if (sd instanceof CompartmentSubDomain) {
			CompartmentSubDomain csd = (CompartmentSubDomain)sd;
			printWriter.println("COMPARTMENT_BEGIN " + csd.getName());
			printWriter.println();
			
			writeCompartment_boundaryConditions(csd);			
			writeCompartment_VarContext(csd);			
			writeCompartment_FastSystem(csd);			
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
	printWriter.print("BOUNDARY_CONDITIONS ");
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


/**
JUMP_CONDITION_BEGIN r
INFLUX 0.0;
OUTFLUX 0.0;
JUMP_CONDITION_END
 * @throws ExpressionException 
*/
private void writeMembrane_jumpConditions(MembraneSubDomain msd) throws ExpressionException {
	Enumeration<JumpCondition> enum1 = msd.getJumpConditions();
	while (enum1.hasMoreElements()) {
		JumpCondition jc = enum1.nextElement();
		printWriter.println("JUMP_CONDITION_BEGIN " + jc.getVariable().getName());
		printWriter.println("INFLUX " + subsituteExpression(jc.getInFluxExpression()).infix() + ";");
		printWriter.println("OUTFLUX " + subsituteExpression(jc.getOutFluxExpression()).infix() + ";");
		printWriter.println("JUMP_CONDITION_END");
		printWriter.println();
	}		
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 * @throws ExpressionException 
 */
private void writeMembrane_VarContext(MembraneSubDomain memSubDomain) throws ExpressionException {	
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
 */
private void writeMembraneRegion_VarContext_Equation(MembraneSubDomain memSubDomain, MembraneRegionEquation equation) throws ExpressionException {	
	printWriter.println("EQUATION_BEGIN " + equation.getVariable().getName());
	printWriter.println("INITIAL " + subsituteExpression(equation.getInitialExpression()).infix() + ";");
	printWriter.println("RATE " + subsituteExpression(((MembraneRegionEquation)equation).getMembraneRateExpression()).infix() + ";");
	printWriter.println("UNIFORMRATE " + subsituteExpression(((MembraneRegionEquation)equation).getUniformRateExpression()).infix() + ";");
	printWriter.println("INFLUX 0.0;");
	printWriter.println("OUTFLUX 0.0;");
	printWriter.println("EQUATION_END");
	printWriter.println();
}


private void writeBoundaryValues(BoundaryConditionType[] bctypes, PdeEquation pde) throws ExpressionException {
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
			printWriter.println(bctitles[i] + " " + subsituteExpression(valueExp).infix() + ";");
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
 */
private void writeMembranes() throws ExpressionException {
	Simulation simulation = simulationJob.getSimulation();
	MathDescription mathDesc = simulation.getMathDescription();
	Enumeration<SubDomain> enum1 = mathDesc.getSubDomains();
	while (enum1.hasMoreElements()) {		
		SubDomain sd = enum1.nextElement();
		if (sd instanceof MembraneSubDomain) {
			MembraneSubDomain msd = (MembraneSubDomain)sd;
			printWriter.println("MEMBRANE_BEGIN " + msd.getName() + " " + msd.getInsideCompartment().getName() + " " + msd.getOutsideCompartment().getName());
			printWriter.println();			
			writeMembrane_boundaryConditions(msd);			
			writeMembrane_VarContext(msd);			
			writeMembrane_jumpConditions(msd);				
			printWriter.println("MEMBRANE_END");
			printWriter.println();
		}
	}
	
	
}


/**
# Model description: FEATURE name handle priority boundary_conditions
MODEL_BEGIN
FEATURE nucleus 1 200 value value value value 
FEATURE cytosol 0 101 value value value value 
MODEL_END
 * @throws MathException 
*/
private void writeModelDescription() throws MathException {
	Simulation simulation = simulationJob.getSimulation();
	
	printWriter.println("# Model description: FEATURE name handle priority boundary_conditions");
	printWriter.println("MODEL_BEGIN");
	MathDescription mathDesc = simulation.getMathDescription();
	Enumeration<SubDomain> enum1 = mathDesc.getSubDomains();
	while (enum1.hasMoreElements()) {
		SubDomain sd = enum1.nextElement();
		if (sd instanceof CompartmentSubDomain) {
			CompartmentSubDomain csd = (CompartmentSubDomain)sd;
			printWriter.print("FEATURE " + csd.getName() + " " + mathDesc.getHandle(csd) + " " + csd.getPriority() + " ");
			writeFeature_boundaryConditions(csd);
		}
	}
	printWriter.println("MODEL_END");
	printWriter.println();	
}


private void getDiscontinuityTimes(Vector<Discontinuity> discontinuities, TreeSet<Double> discontinuityTimes) throws ExpressionException, MathException {
	Simulation simulation = simulationJob.getSimulation();
	SimulationSymbolTable simSymbolTable = simulationJob.getSimulationSymbolTable();
	
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
}
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
	Simulation simulation = simulationJob.getSimulation();
	SolverTaskDescription solverTaskDesc = simulation.getSolverTaskDescription();
	
	printWriter.println("# Simulation Parameters");
	printWriter.println("SIMULATION_PARAM_BEGIN");
	if (solverTaskDesc.getSolverDescription().equals(SolverDescription.SundialsPDE)) {
		printWriter.println("SOLVER SUNDIALS_PDE_SOLVER " + solverTaskDesc.getErrorTolerance().getRelativeErrorTolerance() 
				+ " " + solverTaskDesc.getErrorTolerance().getAbsoluteErrorTolerance() + " " + solverTaskDesc.getTimeStep().getMaximumTimeStep());
		Vector<Discontinuity> discontinuities = new Vector<Discontinuity>();
		TreeSet<Double> discontinuityTimes = new TreeSet<Double>();
		cbit.vcell.math.MathDescription mathDesc = simulation.getMathDescription();
		Enumeration<SubDomain> enum1 = mathDesc.getSubDomains();
		while (enum1.hasMoreElements()) {		
			SubDomain sd = enum1.nextElement();
			Enumeration<Equation> enum_equ = sd.getEquations();
			while (enum_equ.hasMoreElements()){
				Equation equation = enum_equ.nextElement();
				equation.getDiscontinuities(simulationJob.getSimulationSymbolTable(), discontinuities);
			}
		}
		getDiscontinuityTimes(discontinuities, discontinuityTimes);
		if (discontinuityTimes.size() > 0) {
			printWriter.print("DISCONTINUITY_TIMES " + discontinuityTimes.size());
			for (double d : discontinuityTimes) {
				printWriter.print(" " + d);			
			}
			printWriter.println();
		}
	} else { 
		printWriter.println("SOLVER FV_SOLVER " + solverTaskDesc.getErrorTolerance().getRelativeErrorTolerance());
	}
	printWriter.println("BASE_FILE_NAME " + new File(userDirectory, simulationJob.getSimulationJobID()).getAbsolutePath());
    printWriter.println("ENDING_TIME " + solverTaskDesc.getTimeBounds().getEndingTime());    
    OutputTimeSpec outputTimeSpec = solverTaskDesc.getOutputTimeSpec();	
	if (solverTaskDesc.getSolverDescription().equals(SolverDescription.SundialsPDE)) {
		if (outputTimeSpec.isDefault()) {
			DefaultOutputTimeSpec defaultOutputTimeSpec = (DefaultOutputTimeSpec)outputTimeSpec;
			printWriter.println("KEEP_EVERY ONE_STEP " + defaultOutputTimeSpec.getKeepEvery());
			printWriter.println("KEEP_AT_MOST " + defaultOutputTimeSpec.getKeepAtMost());
		} else {
			printWriter.println("TIME_STEP " + ((UniformOutputTimeSpec)outputTimeSpec).getOutputTimeStep());
			printWriter.println("KEEP_EVERY 1");
		}
    } else {
    	double defaultTimeStep = solverTaskDesc.getTimeStep().getDefaultTimeStep();
    	printWriter.println("TIME_STEP " + defaultTimeStep);
    	int keepEvery = 1;
		if (outputTimeSpec.isDefault()) {
        	keepEvery = ((DefaultOutputTimeSpec)outputTimeSpec).getKeepEvery();
		} else if (outputTimeSpec.isUniform()) {
	    	keepEvery = (int)(float)(((UniformOutputTimeSpec)outputTimeSpec).getOutputTimeStep()/defaultTimeStep);
		} else {
			throw new RuntimeException("unexpected OutputTime specification type :"+outputTimeSpec.getClass().getName());
		}
		if (keepEvery <= 0) {
			throw new RuntimeException(" Output KeepEvery must be a positive integer. Try to change the output option.");
		}
		printWriter.println("KEEP_EVERY " +  keepEvery);
    }
    ErrorTolerance stopAtSpatiallyUniformErrorTolerance = solverTaskDesc.getStopAtSpatiallyUniformErrorTolerance();
	if (stopAtSpatiallyUniformErrorTolerance != null) {
    	printWriter.println("CHECK_SPATIALLY_UNIFORM " + stopAtSpatiallyUniformErrorTolerance.getAbsoluteErrorTolerance() 
    			+ " " + stopAtSpatiallyUniformErrorTolerance.getRelativeErrorTolerance());
    }
	printWriter.println("SIMULATION_PARAM_END");	
	printWriter.println();
}

/**
# Mesh file
MESH_BEGIN
VCG_FILE \\\\SAN2\\raid\\Vcell\\users\\fgao\\SimID_22489731_0_.vcg
MESH_END
 * @throws IOException 
*/
private void writeMeshFile() throws IOException {
	printWriter.println("# Mesh file");
	printWriter.println("MESH_BEGIN");
	if (bInlineVCG) {
		GeometryFileWriter.write(printWriter, resampledGeometry);
	} else {
		printWriter.println("VCG_FILE " + new File(userDirectory, simulationJob.getSimulationJobID() + VCG_FILE_EXTENSION).getAbsolutePath());
	}	
	printWriter.println("MESH_END");
	printWriter.println();
}

private double[] generateRandomNumbers(RandomVariable rv, int numRandomNumbers) throws ExpressionException {
	Expression seedExpr = subsituteExpression(rv.getSeed());
	if (!seedExpr.isNumeric()) {
		throw new ExpressionException("Seed for RandomVariable '" + rv.getName() + " is not Constant!");
	}
	int seed = (int)rv.getSeed().evaluateConstant();
	Distribution distribution = rv.getDistribution();
	
	double[] randomNumbers = new double[numRandomNumbers];
	Random random = new Random(seed);
	
	if (distribution instanceof UniformDistribution) {
		UniformDistribution ud = (UniformDistribution)distribution;
		Expression minFlattened = subsituteExpression(ud.getMinimum());
		Expression maxFlattened = subsituteExpression(ud.getMaximum());
		
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
		Expression meanFlattened = subsituteExpression(gd.getMean());
		Expression sdFlattened = subsituteExpression(gd.getStandardDeviation());
		
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
	SimulationSymbolTable simSymbolTable = simulationJob.getSimulationSymbolTable();
	
	String units;
	
	printWriter.println("# Variables : type name unit time_dependent_flag advection_flag solve_whole_mesh_flag solve_regions");
	printWriter.println("VARIABLE_BEGIN");
	MathDescription mathDesc = simSymbolTable.getSimulation().getMathDescription();
	Variable[] vars = simSymbolTable.getVariables();
	ArrayList<RandomVariable> rvList = new ArrayList<RandomVariable>();
	for (int i = 0; i < vars.length; i ++) {
		if (vars[i] instanceof VolumeRandomVariable || vars[i] instanceof MembraneRandomVariable) {
			rvList.add((RandomVariable)vars[i]);		
		} else if (vars[i] instanceof VolVariable) {
			Vector<SubDomain> listOfSubDomains = new Vector<SubDomain>();
			int totalNumCompartments = 0;
			StringBuffer compartmentNames = new StringBuffer();
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
							int handle = mathDesc.getHandle(compartmentSubDomain);
							compartmentNames.append(compartmentSubDomain.getName()+"("+handle+") ");					
				  		}
			  		}
		 		}
		  	}
			
			units = "uM";
			VolVariable volVar = (VolVariable)vars[i];
			if (mathDesc.isPDE(volVar)) {
				boolean hasTimeVaryingDiffusionOrAdvection = simSymbolTable.hasTimeVaryingDiffusionOrAdvection(volVar);
				if (mathDesc.isPdeSteady(volVar)) {
					printWriter.print("VOLUME_PDE_STEADY " + volVar.getName() + " " + units + " " +	hasTimeVaryingDiffusionOrAdvection + " " + mathDesc.hasVelocity(volVar));
				} else {
					printWriter.print("VOLUME_PDE " + volVar.getName() + " " + units + " " + hasTimeVaryingDiffusionOrAdvection + " " + mathDesc.hasVelocity(volVar));
				}
			} else {
				printWriter.print("VOLUME_ODE " + volVar.getName() + " " + units);
			}

			if (totalNumCompartments == listOfSubDomains.size()) {
				printWriter.print(" true");
			} else {
				printWriter.print(" false");
			  	for (int j = 0; j < listOfSubDomains.size(); j++){
					CompartmentSubDomain compartmentSubDomain = (CompartmentSubDomain)listOfSubDomains.elementAt(j);				  	
				  	printWriter.print(" " + compartmentSubDomain.getName());
			  	}
				
			}
			printWriter.println();
		} else if (vars[i] instanceof VolumeRegionVariable) {
			units = "uM";
			printWriter.println("VOLUME_REGION " + vars[i].getName() + " " + units);
		} else if (vars[i] instanceof MemVariable) {
			units = "molecules/squm";
			MemVariable memVar = (MemVariable)vars[i];
			if (mathDesc.isPDE(memVar)) {
				printWriter.println("MEMBRANE_PDE " + memVar.getName() + " " + units + " " + simSymbolTable.hasTimeVaryingDiffusionOrAdvection(memVar));
			} else {
				printWriter.println("MEMBRANE_ODE " + memVar.getName() + " " + units);
			}
		} else if (vars[i] instanceof MembraneRegionVariable) {
			units = "molecules/um^2";
			printWriter.println("MEMBRANE_REGION " + vars[i].getName() + " " + units);
		} else if (vars[i] instanceof FilamentVariable) {
			units = "molecules/um";
			throw new RuntimeException("Filament application not supported yet");
		}
	}
	
	int numRandomVariables = rvList.size();
	if (numRandomVariables > 0) {
		ISize samplingSize = simulationJob.getSimulation().getMeshSpecification().getSamplingSize();
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
		File rvFile = new File(userDirectory, simulationJob.getSimulationJobID() + RANDOM_VARIABLE_FILE_EXTENSION);
		DataSet.writeNew(rvFile, varNameArr, varTypeArr, samplingSize, dataArr);
	}
	printWriter.println("VARIABLE_END");
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
		printWriter.println("PARAMETER_BEGIN " + parameterNames.length);
		for (int i = 0; i < parameterNames.length; i ++) {
			printWriter.println(parameterNames[i]);
		}
		printWriter.println("PARAMETER_END");
		printWriter.println();
	}
}
/**
 * # Field Data
 * FIELD_DATA_BEGIN
 * #id, name, varname, time filename
 * 0 _VCell_FieldData_0 FRAP_binding_ALPHA rfB 0.1 \\\\SAN2\\raid\\Vcell\\users\\fgao\\SimID_22489731_0_FRAP_binding_ALPHA_rfB_0_1.fdat
 * FIELD_DATA_END
 * @throws FileNotFoundException 
 * @throws ExpressionException 
 * @throws DataAccessException 
*/

private void writeFieldData() throws FileNotFoundException, ExpressionException, DataAccessException {
	FieldDataIdentifierSpec[] fieldDataIDSpecs = simulationJob.getFieldDataIdentifierSpecs();
	if (fieldDataIDSpecs == null || fieldDataIDSpecs.length == 0) {
		return;
	}

	DataSetControllerImpl dsci = new DataSetControllerImpl(new NullSessionLog(),null,userDirectory.getParentFile(),null);
	
	printWriter.println("# Field Data");
	printWriter.println("FIELD_DATA_BEGIN");
	printWriter.println("#id, type, new name, name, varname, time, filename");
	
	FieldFunctionArguments psfFieldFunc = null;
	
	Variable var = simulationJob.getSimulationSymbolTable().getVariable(SimDataConstants.PSF_FUNCTION_NAME);
	if (var != null) {
		FieldFunctionArguments[] ffas = FieldUtilities.getFieldFunctionArguments(var.getExpression());
		if (ffas == null || ffas.length == 0) {
			throw new DataAccessException("Point Spread Function " + SimDataConstants.PSF_FUNCTION_NAME + " can only be a single field function.");
		} else {				
			Expression newexp = new Expression(ffas[0].infix());
			if (!var.getExpression().compareEqual(newexp)) {
				throw new DataAccessException("Point Spread Function " + SimDataConstants.PSF_FUNCTION_NAME + " can only be a single field function.");
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
			File newResampledFieldDataFile = new File(userDirectory,
					SimulationData.createCanonicalResampleFileName((VCSimulationDataIdentifier)simulationJob.getVCDataIdentifier(),
							fieldDataIDSpecs[i].getFieldFuncArgs())
				);
			uniqueFieldDataIDSpecs.add(fieldDataIDSpecs[i]);
			VariableType varType = fieldDataIDSpecs[i].getFieldFuncArgs().getVariableType();
			SimDataBlock simDataBlock = dsci.getSimDataBlock(fieldDataIDSpecs[i].getExternalDataIdentifier(),fieldDataIDSpecs[i].getFieldFuncArgs().getVariableName(), fieldDataIDSpecs[i].getFieldFuncArgs().getTime().evaluateConstant());
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
 */
private void writeMembrane_VarContext_Equation(MembraneSubDomain memSubDomain, Equation equation) throws ExpressionException {	
	printWriter.println("EQUATION_BEGIN " + equation.getVariable().getName());
	printWriter.println("INITIAL " + subsituteExpression(equation.getInitialExpression()).infix() + ";");
	Expression rateExpression = subsituteExpression(equation.getRateExpression());
	printWriter.println("RATE " + rateExpression.infix() + ";");
	if (equation instanceof PdeEquation) {
		printWriter.println("DIFFUSION " + subsituteExpression(((PdeEquation)equation).getDiffusionExpression()).infix() + ";");
		
		PdeEquation pde = (PdeEquation)equation;
		BoundaryConditionType[] bctypes = new BoundaryConditionType[] {
				memSubDomain.getInsideCompartment().getBoundaryConditionXm(),
				memSubDomain.getInsideCompartment().getBoundaryConditionXp(),
				memSubDomain.getInsideCompartment().getBoundaryConditionYm(),
				memSubDomain.getInsideCompartment().getBoundaryConditionYp(),
				memSubDomain.getInsideCompartment().getBoundaryConditionZm(),
				memSubDomain.getInsideCompartment().getBoundaryConditionZp()
		};
		writeBoundaryValues(bctypes, pde);		
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
	printWriter.println("INITIAL " + subsituteExpression(equation.getInitialExpression()).infix() + ";");
	printWriter.println("RATE " + subsituteExpression(equation.getVolumeRateExpression()).infix() + ";");
	printWriter.println("UNIFORMRATE " + subsituteExpression(equation.getUniformRateExpression()).infix() + ";");
	printWriter.println("INFLUX " + subsituteExpression(equation.getMembraneRateExpression()).infix() + ";");
	printWriter.println("OUTFLUX 0.0;");
	printWriter.println("EQUATION_END");
	printWriter.println();
}
}