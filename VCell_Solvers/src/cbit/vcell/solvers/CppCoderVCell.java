package cbit.vcell.solvers;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.Enumeration;

import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Equation;
import cbit.vcell.math.FastSystem;
import cbit.vcell.math.FilamentRegionEquation;
import cbit.vcell.math.FilamentSubDomain;
import cbit.vcell.math.MembraneRegionEquation;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolumeRegionEquation;
import cbit.vcell.simulation.Simulation;

public class CppCoderVCell extends CppCoder
{
	private SimulationJob simulationJob = null;

public CppCoderVCell(String baseFilename, java.io.File directory, SimulationJob argSimulationJob){
	super(baseFilename,directory);
	this.simulationJob = argSimulationJob;
}                              


   protected String[] getCodeDefines()
   {
	  String defines[] = new String[1];

	  defines[0] = "abs(x)      fabs(x)";

//	  defines[0] = "parm(var)      ((var)->getValue())";
//	  defines[1] = "conc(var)      ((var)->getOld(index))";
//	  defines[2] = "valence(var)   ((var)->getValence())";
//	  defines[3] = "inside(var)    ((var)->getCurr(insideIndex))";
//	  defines[4] = "surface(var)   ((var)->getCurr(insideIndex))";
//	  defines[5] = "outside(var)   ((var)->getCurr(outsideIndex))";
//	  defines[6] = "diffusion(var) ((var)->defaultDiffusionRate)";
//	  defines[7] = "mobility(var)  ((var)->defaultMobility)";

//	  return defines;

		return defines;
   }                           


protected String[] getCodeIncludes()
{
	  String includes[] = new String[22];

	  includes[0] = "<assert.h>";
	  includes[1] = "<math.h>";
	  includes[2] = "<stdlib.h>";
	  includes[3] = "<VCELL/SimTypes.h>";
	  includes[4] = "<VCELL/Variable.h>";
	  includes[5] = "<VCELL/Feature.h>";
	  includes[6] = "<VCELL/Mesh.h>";
	  includes[7] = "<VCELL/SimTool.h>";
	  includes[8] = "<VCELL/Solver.h>";
	  includes[9] = "<VCELL/DataSet.h>";
	  includes[10] = "<VCELL/EqnBuilderReactionDiffusion.h>";	  
	  includes[11] = "<VCELL/EqnBuilderReactionForward.h>";
	  includes[12] = "<VCELL/MembraneEqnBuilderForward.h>";	  
	  includes[13] = "<VCELL/App.h>";
	  includes[14] = "<VCELL/Contour.h>";
	  includes[15] = "<VCELL/Element.h>";
	  includes[16] = "<VCELL/PdeSolverDiana.h>";
	  includes[17] = "<VCELL/Region.h>";
	  includes[18] = "<VCELL/RegionEqnBuilder.h>";
	  includes[19] = "<VCELL/EqnBuilderReactionDiffusionConvection.h>";
	  includes[20] = "<VCELL/MembraneEqnBuilderDiffusion.h>";
	  includes[21] = "<VCELL/SparseLinearSolver.h>";

	  return includes;
}                                    


   protected String[] getHeaderClassDefines()
   {
	  String defines[] = new String[5];

	  defines[0] = "CartesianMesh";
	  defines[1] = "Feature";
	  defines[2] = "VolumeVariable";
	  defines[3] = "MembraneVariable";
	  defines[3] = "ContourVariable";
	  defines[4] = "VolumeRegion";

	  return defines;
   }                     


protected String[] getHeaderConstants() {
	
	java.util.Vector constantVector = new java.util.Vector();
	
	Variable variables[] = simulationJob.getWorkingSim().getVariables();
	for (int i = 0; i < variables.length; i++){
		Variable var = variables[i];
		if (var instanceof Constant){
			constantVector.addElement(var);
		}
	}		

	int numConstants = constantVector.size();
	String constantString[] = new String[numConstants];
	
	for (int i=0;i<numConstants;i++){
		Constant constant = (Constant)constantVector.elementAt(i);
		try {
			constantString[i] = "double "+constant.getName()+" = " +constant.getConstantValue()+";";
		}catch (org.vcell.expression.ExpressionException e){
			constantString[i] = "double "+constant.getName()+" = " +constant.getExpression().toString()+";";
		}
	}	
	return constantString;
}            


   protected String[] getHeaderIncludes()
   {
	  String includes[] = new String[6];

	  includes[0] = "<VCELL/SimTypes.h>";
	  includes[1] = "<VCELL/VarContext.h>";
	  includes[2] = "<VCELL/Simulation.h>";
	  includes[3] = "<VCELL/VCellModel.h>";
	  includes[4] = "<VCELL/Feature.h>";
	  includes[5] = "<VCELL/FastSystem.h>";

	  return includes;
   }                                 


/**
 * Builds list of CppClassCoders from SimulationContext information
 */
public void initialize() throws Exception {
	
	super.initialize();
	//
	// add Simulation coder
	//
	Simulation simulation = simulationJob.getWorkingSim();
	addCppClassCoder(new CppClassCoderSimulation(this,simulationJob,new java.io.File(dir,baseFilename).getPath()));
	
	//
	// add Model coder
	//
	addCppClassCoder(new CppClassCoderVCellModel(this,simulation));
	
	//
	// add Feature coders
	//
	Enumeration enum1 = simulation.getMathDescription().getSubDomains();
	while (enum1.hasMoreElements()){
		SubDomain subDomain = (SubDomain)enum1.nextElement();
		if (subDomain instanceof CompartmentSubDomain){
			CompartmentSubDomain volSubDomain = (CompartmentSubDomain)subDomain;
			addCppClassCoder(new CppClassCoderFeature(this,simulation,volSubDomain,null));
			
			//
			// add VolumeVarContext coders
			//
			Enumeration enum_equ = volSubDomain.getEquations();
			while (enum_equ.hasMoreElements()){
				Equation equation = (Equation)enum_equ.nextElement();
				if (equation instanceof VolumeRegionEquation){
					addCppClassCoder(new CppClassCoderVolumeRegionVarContext(this,equation,volSubDomain,simulation,"VolumeRegionVarContext"));
				}else{
					addCppClassCoder(new CppClassCoderVolumeVarContext(this,equation,volSubDomain,simulation,"VolumeVarContext"));
				}	
			}	
		}else if (subDomain instanceof MembraneSubDomain){
			MembraneSubDomain memSubDomain = (MembraneSubDomain)subDomain;
			
			//
			// add MembraneVarContext coders
			//
			Enumeration enum_equ = memSubDomain.getEquations();
			while (enum_equ.hasMoreElements()){
				Equation equation = (Equation)enum_equ.nextElement();
				if (equation instanceof MembraneRegionEquation){
					addCppClassCoder(new CppClassCoderMembraneRegionVarContext(this,equation,memSubDomain,simulation,"MembraneRegionVarContext"));
				}else{
					addCppClassCoder(new CppClassCoderMembraneVarContext(this,equation,memSubDomain,simulation,"MembraneVarContext"));
				}
			}	
		}else if (subDomain instanceof FilamentSubDomain){
			FilamentSubDomain filamentSubDomain = (FilamentSubDomain)subDomain;
			
			//
			// add FilamentVarContext coders
			//
			Enumeration enum_equ = filamentSubDomain.getEquations();
			while (enum_equ.hasMoreElements()){
				Equation equation = (Equation)enum_equ.nextElement();
				if (equation instanceof FilamentRegionEquation){
//					addCppClassCoder(new CppClassCoderFilamentRegionVarContext(this,equation,filamentSubDomain,simulation,"ContourRegionVarContext"));
				}else if (equation.getSolutionType() == Equation.UNKNOWN_SOLUTION){
					addCppClassCoder(new CppClassCoderContourVarContext(this,equation,filamentSubDomain,simulation,"ContourVarContext"));
				}else if (equation.getSolutionType() == Equation.EXACT_SOLUTION){
//					addCppClassCoder(new CppClassCoderExactContourVarContext(this,equation,filamentSubDomain,mathDesc,"ExactContourVarContext"));	
					throw new Exception("membrane equation has unsupported solution type <EXACT SOLUTION>");
				}else{
					throw new Exception("equation has illegal solution type");
				}	
			}	
		}	
		//
		// add FastSystem coder
		//
		FastSystem fastSystem = subDomain.getFastSystem();
		if (fastSystem!=null){
			addCppClassCoder(new CppClassCoderFastSystem(this,fastSystem,subDomain,simulation,"FastSystem"));
		}	
	}	

	initializeCppClassCoders();
}
}