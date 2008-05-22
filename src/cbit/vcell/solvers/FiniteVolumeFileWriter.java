package cbit.vcell.solvers;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.MathMLTags;

import java.util.Enumeration;
import java.io.File;
import java.io.PrintWriter;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.ExternalDataIdentifier;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.geometry.surface.GeometryFileWriter;
import cbit.vcell.messaging.JmsUtils;
import cbit.vcell.mapping.FastSystemAnalyzer;
import cbit.vcell.modeldb.NullSessionLog;
import cbit.vcell.math.*;

/**
 * Insert the type's description here.
 * Creation date: (5/9/2005 2:51:48 PM)
 * @author: Fei Gao
 */
public class FiniteVolumeFileWriter {
	SimulationJob simulationJob = null;
	Simulation simulation = null;
	File userDirectory = null;
	PrintWriter writer = null;
	boolean bInlineVCG = false;
	String[] parameterNames = null;
	
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
/**
 * FiniteVolumeFileWriter constructor comment.
 */
public FiniteVolumeFileWriter(SimulationJob simJob, File dir, PrintWriter pw) {
	super();
	simulationJob = simJob;
	simulation = simulationJob.getWorkingSim();
	userDirectory = dir;
	writer = pw;
}

public FiniteVolumeFileWriter(SimulationJob simJob, String[] paramNames, PrintWriter pw) {	
	simulationJob = simJob;
	simulation = simulationJob.getWorkingSim();
	parameterNames = paramNames;
	bInlineVCG = true;	
}

/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 */
private Expression subsituteExpression(Expression exp) throws Exception {
	exp.bindExpression(simulation);
	Expression exp2 = simulation.substituteFunctions(exp).flatten();
	if (exp2.getFieldFunctionArguments() != null && exp2.getFieldFunctionArguments().length > 0) {
		if (uniqueFieldDataNSet != null && uniqueFieldDataNSet.size() > 0) {	
			for (FieldDataNumerics fdn: uniqueFieldDataNSet) {
				exp2.substituteInPlace(new Expression(fdn.getFieldFunction()), new Expression(fdn.getNumericsSubsitute()));
			}
		} else {
			throw new RuntimeException("Didn't find field functions in simulation when preprocessing, but expression [" + exp.infix() + "] has field function in it");
		}
	}
	return exp2;
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 */
public void write() throws Exception {	
	writeJMSParamters();
	writer.println();
	
	writeSimulationParamters();
	writer.println();
	
	writeModelDescription();
	writer.println();
	
	writeMeshFile();
	writer.println();
	
	writeVariables();
	writer.println();
	
	if (parameterNames != null) {
		writeParameters();
		writer.println();		
	}
	
	writeFieldData();
	
	writeCompartments();
	writer.println();
	
	writeMembranes();
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 */
private void writeCompartment_boundaryConditions(CompartmentSubDomain csd) throws Exception {
	writer.print("BOUNDARY_CONDITIONS ");
	writeFeature_boundaryConditions(csd);
	writer.println();	
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
*/
private void writeCompartment_FastSystem(CompartmentSubDomain volSubDomain) throws Exception {
	FastSystem fastSystem = volSubDomain.getFastSystem();
	FastSystemAnalyzer fs_analyzer = new FastSystemAnalyzer(fastSystem);
	if (fastSystem != null){
		int numIndep = fs_analyzer.getNumIndependentVariables();
		int numDep = fs_analyzer.getNumDependentVariables();
		int numPseudo = fs_analyzer.getNumPseudoConstants();	
		writer.println("# fast system dimension num_dependents");
		writer.println("FAST_SYSTEM_BEGIN " + numIndep + " "  + numDep);
		if (numIndep != 0) {
			writer.print("INDEPENDENT_VARIALBES ");
			Enumeration<Variable> enum1 = fs_analyzer.getIndependentVariables();
			while (enum1.hasMoreElements()) {
				Variable var = enum1.nextElement();
				writer.print(var.getName() + " ");
			}
			writer.println();
				
		}
		if (numDep != 0) {
			writer.print("DEPENDENT_VARIALBES ");
			Enumeration<Variable> enum1 = fs_analyzer.getDependentVariables();
			while (enum1.hasMoreElements()) {
				Variable var = enum1.nextElement();
				writer.print(var.getName() + " ");
			}
			writer.println();
		}
		writer.println();
					
		if (numPseudo != 0) {
			writer.println("PSEUDO_CONSTANT_BEGIN");
			Enumeration<PseudoConstant> enum1 = fs_analyzer.getPseudoConstants();
			while (enum1.hasMoreElements()) {
				PseudoConstant pc = enum1.nextElement();
				writer.println(pc.getName() + " " + subsituteExpression(pc.getPseudoExpression()).infix() + ";");
			}
			writer.println("PSEUDO_CONSTANT_END");
			writer.println();			
		}
		
		if (numIndep != 0) {
			writer.println("FAST_RATE_BEGIN" );
			Enumeration<Expression> enum1 = fs_analyzer.getFastRateExpressions();
			while (enum1.hasMoreElements()) {
				Expression exp = enum1.nextElement();	
				writer.println(subsituteExpression(exp).infix() + ";");
			}
			writer.println("FAST_RATE_END");
			writer.println();				
		}	

		if (numDep != 0) {
			writer.println("FAST_DEPENDENCY_BEGIN" );
			Enumeration<Expression> enum_exp = fs_analyzer.getDependencyExps();
			Enumeration<Variable> enum_var = fs_analyzer.getDependentVariables();
			while (enum_exp.hasMoreElements()){
				Expression exp = enum_exp.nextElement();
				Variable depVar = enum_var.nextElement();
				writer.println(depVar.getName() + " " + subsituteExpression(exp).infix() + ";");
			}
			writer.println("FAST_DEPENDENCY_END");
			writer.println();
		}
		
		if (numIndep != 0) {
			writer.println("JACOBIAN_BEGIN" );
			Enumeration<Expression> enum_fre = fs_analyzer.getFastRateExpressions();
			while (enum_fre.hasMoreElements()){
				Expression fre = enum_fre.nextElement();
				Enumeration<Variable> enum_var = fs_analyzer.getIndependentVariables();
				while (enum_var.hasMoreElements()){
					Variable var = enum_var.nextElement();
					Expression exp = simulation.substituteFunctions(fre).flatten();
					Expression differential = exp.differentiate(var.getName());
					writer.println(subsituteExpression(differential).infix() + ";");					
				}
			}
			writer.println("JACOBIAN_END");
			writer.println();				
		}
		writer.println("FAST_SYSTEM_END");
		writer.println();
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 */
private void writeCompartment_VarContext(CompartmentSubDomain volSubDomain) throws Exception {
	//
	// get list of volVariables participating in PDEs (anywhere).
	//
	java.util.Vector<VolVariable> pdeVolVariableList = new java.util.Vector<VolVariable>();
	Variable[] variables = simulation.getVariables();
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
 */
private void writeCompartment_VarContext_Equation(CompartmentSubDomain volSubDomain, Equation equation) throws Exception {	
	writer.println("EQUATION_BEGIN " + equation.getVariable().getName());
	writer.println("INITIAL " + subsituteExpression(equation.getInitialExpression()).infix() + ";");
	writer.println("RATE " + subsituteExpression(equation.getRateExpression()).infix() + ";");
	if (equation instanceof PdeEquation) {
		writer.println("DIFFUSION " + subsituteExpression(((PdeEquation)equation).getDiffusionExpression()).infix() + ";");
		if (((PdeEquation)equation).getVelocityX() != null) {
			writer.println("VELOCITY_X " + subsituteExpression(((PdeEquation)equation).getVelocityX()).infix() + ";");
		} else {
			writer.println("VELOCITY_X 0.0;");
		}
		if (((PdeEquation)equation).getVelocityY() != null) {
			writer.println("VELOCITY_Y " + subsituteExpression(((PdeEquation)equation).getVelocityY()).infix() + ";");
		} else if (simulation.getMathDescription().getGeometry().getDimension() > 1){
			writer.println("VELOCITY_Y 0.0;");
		}
		if (((PdeEquation)equation).getVelocityZ() != null) {
			writer.println("VELOCITY_Z " + subsituteExpression(((PdeEquation)equation).getVelocityZ()).infix() + ";");			
		} else if (simulation.getMathDescription().getGeometry().getDimension() > 2){
			writer.println("VELOCITY_Z 0.0;");
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

	writer.println("EQUATION_END");
	writer.println();
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
*/
private void writeCompartments() throws Exception {
	cbit.vcell.math.MathDescription mathDesc = simulation.getMathDescription();
	java.util.Enumeration<SubDomain> enum1 = mathDesc.getSubDomains();
	while (enum1.hasMoreElements()) {		
		SubDomain sd = enum1.nextElement();
		if (sd instanceof cbit.vcell.math.CompartmentSubDomain) {
			CompartmentSubDomain csd = (CompartmentSubDomain)sd;
			writer.println("COMPARTMENT_BEGIN " + csd.getName());
			writer.println();
			
			writeCompartment_boundaryConditions(csd);			
			writeCompartment_VarContext(csd);			
			writeCompartment_FastSystem(csd);			
			writer.println("COMPARTMENT_END");
			writer.println();
		}
	}
	
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 */
private void writeFeature_boundaryConditions(CompartmentSubDomain csd) throws Exception {
	BoundaryConditionType[] bctypes = new BoundaryConditionType[] {
			csd.getBoundaryConditionXm(),
			csd.getBoundaryConditionXp(),
			csd.getBoundaryConditionYm(),
			csd.getBoundaryConditionYp(),
			csd.getBoundaryConditionZm(),
			csd.getBoundaryConditionZp()
	};
	writeBoundaryConditions(bctypes);
	writer.println();
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 */
private void writeMembrane_boundaryConditions(MembraneSubDomain msd) throws Exception {
	writer.print("BOUNDARY_CONDITIONS ");
	BoundaryConditionType[] bctypes = new BoundaryConditionType[] {
			msd.getInsideCompartment().getBoundaryConditionXm(),
			msd.getInsideCompartment().getBoundaryConditionXp(),
			msd.getInsideCompartment().getBoundaryConditionYm(),
			msd.getInsideCompartment().getBoundaryConditionYp(),
			msd.getInsideCompartment().getBoundaryConditionZm(),
			msd.getInsideCompartment().getBoundaryConditionZp()
	};
	writeBoundaryConditions(bctypes);
	writer.println();
	writer.println();
}


private void writeBoundaryConditions(BoundaryConditionType[] bctypes) {
	int dimension = simulation.getMathDescription().getGeometry().getDimension();
	for (int i = 0; i < 2 * dimension; i ++) {
		if (bctypes[i].isDIRICHLET()) {
			writer.print("value ");
		} else if (bctypes[i].isNEUMANN()){
			writer.print("flux ");
		} else if (bctypes[i].isPERIODIC()) {
			writer.print("periodic ");
		}
	}
}


/**
JUMP_CONDITION_BEGIN r
INFLUX 0.0;
OUTFLUX 0.0;
JUMP_CONDITION_END
*/
private void writeMembrane_jumpConditions(MembraneSubDomain msd) throws Exception {
	Enumeration<JumpCondition> enum1 = msd.getJumpConditions();
	while (enum1.hasMoreElements()) {
		JumpCondition jc = enum1.nextElement();
		writer.println("JUMP_CONDITION_BEGIN " + jc.getVariable().getName());
		writer.println("INFLUX " + subsituteExpression(jc.getInFluxExpression()).infix() + ";");
		writer.println("OUTFLUX " + subsituteExpression(jc.getOutFluxExpression()).infix() + ";");
		writer.println("JUMP_CONDITION_END");
		writer.println();
	}		
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2005 2:52:48 PM)
 */
private void writeMembrane_VarContext(MembraneSubDomain memSubDomain) throws Exception {	
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
 */
private void writeMembraneRegion_VarContext_Equation(MembraneSubDomain memSubDomain, MembraneRegionEquation equation) throws Exception {	
	writer.println("EQUATION_BEGIN " + equation.getVariable().getName());
	writer.println("INITIAL " + subsituteExpression(equation.getInitialExpression()).infix() + ";");
	writer.println("RATE " + subsituteExpression(((MembraneRegionEquation)equation).getMembraneRateExpression()).infix() + ";");
	writer.println("UNIFORMRATE " + subsituteExpression(((MembraneRegionEquation)equation).getUniformRateExpression()).infix() + ";");
	writer.println("INFLUX 0.0;");
	writer.println("OUTFLUX 0.0;");
	writer.println("EQUATION_END");
	writer.println();
}


private void writeBoundaryValues(BoundaryConditionType[] bctypes, PdeEquation pde) throws Exception {
	int dimension = simulation.getMathDescription().getGeometry().getDimension();
	
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
			writer.println(bctitles[i] + " " + subsituteExpression(valueExp).infix() + ";");
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
 */
private void writeMembranes() throws Exception {
	cbit.vcell.math.MathDescription mathDesc = simulation.getMathDescription();
	java.util.Enumeration<SubDomain> enum1 = mathDesc.getSubDomains();
	while (enum1.hasMoreElements()) {		
		SubDomain sd = enum1.nextElement();
		if (sd instanceof MembraneSubDomain) {
			MembraneSubDomain msd = (MembraneSubDomain)sd;
			writer.println("MEMBRANE_BEGIN " + msd.getName() + " " + msd.getInsideCompartment().getName() + " " + msd.getOutsideCompartment().getName());
			writer.println();			
			writeMembrane_boundaryConditions(msd);			
			writeMembrane_VarContext(msd);			
			writeMembrane_jumpConditions(msd);				
			writer.println("MEMBRANE_END");
			writer.println();
		}
	}
	
	
}


/**
# Model description: FEATURE name handle priority boundary_conditions
MODEL_BEGIN
FEATURE nucleus 1 200 value value value value 
FEATURE cytosol 0 101 value value value value 
MODEL_END
*/
private void writeModelDescription() throws Exception {
	writer.println("# Model description: FEATURE name handle priority boundary_conditions");
	writer.println("MODEL_BEGIN");
	cbit.vcell.math.MathDescription mathDesc = simulation.getMathDescription();
	java.util.Enumeration<SubDomain> enum1 = mathDesc.getSubDomains();
	while (enum1.hasMoreElements()) {
		SubDomain sd = enum1.nextElement();
		if (sd instanceof cbit.vcell.math.CompartmentSubDomain) {
			CompartmentSubDomain csd = (CompartmentSubDomain)sd;
			writer.print("FEATURE " + csd.getName() + " " + mathDesc.getHandle(csd) + " " + csd.getPriority() + " ");
			writeFeature_boundaryConditions(csd);
		}
	}
	writer.println("MODEL_END");
	
}


/**
# Simulation Parameters
SIMULATION_PARAM_BEGIN
BASE_FILE_NAME \\\\SAN2\\raid\\Vcell\\users\\fgao\\SimID_22489731_0_
ENDING_TIME 1.0
TIME_STEP 0.0010
KEEP_EVERY 10
SIMULATION_PARAM_END
*/
private void writeSimulationParamters() {
	Simulation simulation = simulationJob.getWorkingSim();
	
	writer.println("# Simulation Parameters");
	writer.println("SIMULATION_PARAM_BEGIN");
	writer.println("BASE_FILE_NAME " + new File(userDirectory, simulationJob.getSimulationJobID()).getAbsolutePath());
    writer.println("ENDING_TIME " + simulation.getSolverTaskDescription().getTimeBounds().getEndingTime());
    writer.println("TIME_STEP " + simulation.getSolverTaskDescription().getTimeStep().getDefaultTimeStep());  
	writer.println("KEEP_EVERY " + ((DefaultOutputTimeSpec)simulation.getSolverTaskDescription().getOutputTimeSpec()).getKeepEvery());
	writer.println("SIMULATION_PARAM_END");	
}

/**
# Mesh file
MESH_BEGIN
VCG_FILE \\\\SAN2\\raid\\Vcell\\users\\fgao\\SimID_22489731_0_.vcg
MESH_END
*/
private void writeMeshFile() {
	writer.println("# Mesh file");
	writer.println("MESH_BEGIN");
	if (bInlineVCG) {
		cbit.vcell.geometry.Geometry geo = simulation.getMathDescription().getGeometry();
		try {
			GeometryFileWriter.write(geo, simulation.getMeshSpecification().getSamplingSize(),writer);
		} catch (Exception e) {			 
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	} else {
		writer.println("VCG_FILE " + new File(userDirectory, simulationJob.getSimulationJobID() + ".vcg").getAbsolutePath());
	}	
	writer.println("MESH_END");
}

/**
# JMS_Paramters
JMS_PARAM_BEGIN
JMS_BROKER tcp://code:2506
JMS_USER serverUser cbittech
JMS_QUEUE workerEventDev
JMS_TOPIC serviceControlDev
VCELL_USER fgao
SIMULATION_KEY 22489731
JOB_INDEX 0
JMS_PARAM_END
 */
private void writeJMSParamters() {	
	writer.println("# JMS_Paramters");
	writer.println("JMS_PARAM_BEGIN");
	writer.println("JMS_BROKER " + JmsUtils.getJmsUrl());
    writer.println("JMS_USER " + JmsUtils.getJmsUserID() + " " + JmsUtils.getJmsPassword());
    writer.println("JMS_QUEUE " + JmsUtils.getQueueWorkerEvent());  
	writer.println("JMS_TOPIC " + JmsUtils.getTopicServiceControl());
	writer.println("VCELL_USER " + simulation.getVersion().getOwner().getName());
	writer.println("SIMULATION_KEY " + simulation.getVersion().getVersionKey());
	writer.println("JOB_INDEX " + simulationJob.getJobIndex());
	writer.println("JMS_PARAM_END");
}


/**
# Variables : type name unit time_dependent_flag advection_flag solve_whole_mesh_flag solve_regions
VARIABLE_BEGIN
VOLUME_ODE rB uM
VOLUME_PDE rf uM false false
VOLUME_PDE r uM false false
VOLUME_ODE rfB uM
VARIABLE_END
 */
private void writeVariables() throws Exception {	  			
	String units;
	
	writer.println("# Variables : type name unit time_dependent_flag advection_flag solve_whole_mesh_flag solve_regions");
	writer.println("VARIABLE_BEGIN");
	cbit.vcell.math.MathDescription mathDesc = simulation.getMathDescription();
	Variable[] vars = simulation.getVariables();
	for (int i = 0; i < vars.length; i ++) {
		if (vars[i] instanceof VolVariable) {
			Vector<SubDomain> listOfSubDomains = new Vector<SubDomain>();
			int totalNumCompartments = 0;
			StringBuffer compartmentNames = new StringBuffer();
			Enumeration<SubDomain> subDomainEnum = simulation.getMathDescription().getSubDomains();
			while (subDomainEnum.hasMoreElements()){
		  		SubDomain subDomain = subDomainEnum.nextElement();
		 		if (subDomain instanceof CompartmentSubDomain){
			  		CompartmentSubDomain compartmentSubDomain = (CompartmentSubDomain)subDomain;
			  		totalNumCompartments++;
			  		Equation varEquation = subDomain.getEquation(vars[i]);
			  		if (varEquation != null) {
				  		if (!(varEquation instanceof PdeEquation) || !((PdeEquation)varEquation).isDummy(simulation, compartmentSubDomain)){
							listOfSubDomains.add(compartmentSubDomain);
							int handle = simulation.getMathDescription().getHandle(compartmentSubDomain);
							compartmentNames.append(compartmentSubDomain.getName()+"("+handle+") ");					
				  		}
			  		}
		 		}
		  	}
			
			units = "uM";
			VolVariable volVar = (VolVariable)vars[i];
			if (mathDesc.isPDE(volVar)) {
				if (mathDesc.isPdeSteady(volVar)) {
					writer.print("VOLUME_PDE_STEADY " + volVar.getName() + " " + units + " " +	simulation.hasTimeVaryingDiffusionOrAdvection(volVar) + " " + mathDesc.hasVelocity(volVar));
				} else {
					writer.print("VOLUME_PDE " + volVar.getName() + " " + units + " " +	simulation.hasTimeVaryingDiffusionOrAdvection(volVar) + " " + mathDesc.hasVelocity(volVar));
				}
			} else {
				writer.print("VOLUME_ODE " + volVar.getName() + " " + units);
			}

			if (totalNumCompartments == listOfSubDomains.size()) {
				writer.print(" true");
			} else {
				writer.print(" false");
			  	for (int j = 0; j < listOfSubDomains.size(); j++){
					CompartmentSubDomain compartmentSubDomain = (CompartmentSubDomain)listOfSubDomains.elementAt(j);				  	
				  	writer.print(" " + compartmentSubDomain.getName());
			  	}
				
			}
			writer.println();
		} else if (vars[i] instanceof VolumeRegionVariable) {
			units = "uM";
			writer.println("VOLUME_REGION " + vars[i].getName() + " " + units);
		} else if (vars[i] instanceof MemVariable) {
			units = "molecules/squm";
			MemVariable memVar = (MemVariable)vars[i];
			if (mathDesc.isPDE(memVar)) {
				writer.println("MEMBRANE_PDE " + memVar.getName() + " " + units + " " +	simulation.hasTimeVaryingDiffusionOrAdvection(memVar));
			} else {
				writer.println("MEMBRANE_ODE " + memVar.getName() + " " + units);
			}
		} else if (vars[i] instanceof MembraneRegionVariable) {
			units = "molecules/um^2";
			writer.println("MEMBRANE_REGION " + vars[i].getName() + " " + units);
		} else if (vars[i] instanceof FilamentVariable) {
			units = "molecules/um";
			throw new Exception("Filament application not supported yet");
		}
	}
	writer.println("VARIABLE_END");
	
}

/**
 * PARAMETER_BEGIN 3
 * D
 * U0
 * U1
 * PARAMETER_END
 * @throws Exception
 */
private void writeParameters() throws Exception {
	writer.println("# Parameters");
	writer.println("PARAMETER_BEGIN " + parameterNames.length);
	for (int i = 0; i < parameterNames.length; i ++) {
		writer.println(parameterNames[i]);
	}
	writer.println("PARAMETER_END");
}
/**
 * # Field Data
 * FIELD_DATA_BEGIN
 * #id, name, varname, time filename
 * 0 _VCell_FieldData_0 FRAP_binding_ALPHA rfB 0.1 \\\\SAN2\\raid\\Vcell\\users\\fgao\\SimID_22489731_0_FRAP_binding_ALPHA_rfB_0_1.fdat
 * FIELD_DATA_END
*/

public String getFieldFunctionSyntax(FieldFunctionArguments ffa){
	if (ffa.getVariableType().equals(VariableType.UNKNOWN)) {
		return MathMLTags.FIELD+"("+ffa.getFieldName()+","+ffa.getVariableName()+","+ffa.getTime().infix() +")";
	} else {
		return MathMLTags.FIELD+"("+ffa.getFieldName()+","+ffa.getVariableName()+","+ffa.getTime().infix() +","+ffa.getVariableType().getTypeName()+")";
	}
}

private void writeFieldData() throws Exception {
	FieldDataIdentifierSpec[] fieldDataIDSpecs = simulationJob.getFieldDataIdentifierSpecs();
	if (fieldDataIDSpecs == null || fieldDataIDSpecs.length == 0) {
		return;
	}

	DataSetControllerImpl dsci = new DataSetControllerImpl(new NullSessionLog(),null,userDirectory.getParentFile(),null);
	
	writer.println("# Field Data");
	writer.println("FIELD_DATA_BEGIN");
	writer.println("#id, type, new name, name, varname, time, filename");
	
	int index = 0;
	HashSet<FieldDataIdentifierSpec> uniqueFieldDataIDSpecs = new HashSet<FieldDataIdentifierSpec>();
	uniqueFieldDataNSet = new HashSet<FieldDataNumerics>();
	for (int i = 0; i < fieldDataIDSpecs.length; i ++) {
		if(!uniqueFieldDataIDSpecs.contains(fieldDataIDSpecs[i])){
			FieldFunctionArguments ffa = fieldDataIDSpecs[i].getFieldFuncArgs();
			File newResampledFieldDataFile = new File(userDirectory,
					ExternalDataIdentifier.createCanonicalResampleFileName((VCSimulationDataIdentifier)simulationJob.getVCDataIdentifier(),
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
			String fieldDataID = "_VCell_FieldData_" + index;
			writer.println(index + " " + varType.getTypeName() + " " + fieldDataID + " " + ffa.getFieldName() + " " + ffa.getVariableName() + " " + ffa.getTime().infix() + " " + newResampledFieldDataFile);
			uniqueFieldDataNSet.add(new FieldDataNumerics(getFieldFunctionSyntax(ffa), fieldDataID));
			index ++;
		}
	}	
	
	writer.println("FIELD_DATA_END");
	writer.println();
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
 */
private void writeMembrane_VarContext_Equation(MembraneSubDomain memSubDomain, Equation equation) throws Exception {	
	writer.println("EQUATION_BEGIN " + equation.getVariable().getName());
	writer.println("INITIAL " + subsituteExpression(equation.getInitialExpression()).infix() + ";");
	writer.println("RATE " + subsituteExpression(equation.getRateExpression()).infix() + ";");
	if (equation instanceof PdeEquation) {
		writer.println("DIFFUSION " + subsituteExpression(((PdeEquation)equation).getDiffusionExpression()).infix() + ";");
		
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

	writer.println("EQUATION_END");
	writer.println();
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
 */
private void writeCompartmentRegion_VarContext_Equation(CompartmentSubDomain volSubDomain, VolumeRegionEquation equation) throws Exception {	
	writer.println("EQUATION_BEGIN " + equation.getVariable().getName());
	writer.println("INITIAL " + subsituteExpression(equation.getInitialExpression()).infix() + ";");
	writer.println("RATE " + subsituteExpression(equation.getVolumeRateExpression()).infix() + ";");
	writer.println("UNIFORMRATE " + subsituteExpression(equation.getUniformRateExpression()).infix() + ";");
	writer.println("INFLUX 0.0;");
	writer.println("OUTFLUX 0.0;");	
	writer.println("EQUATION_END");
	writer.println();
}
}