package cbit.vcell.solvers;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.Enumeration;
import cbit.vcell.solver.*;
import cbit.vcell.math.*;

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
	  return new String[] { 
			  "<assert.h>",	  
			  "<math.h>",
			  "<stdlib.h>",
			  "<VCELL/SimTypes.h>",
			  "<VCELL/VolumeVariable.h>",
			  "<VCELL/VolumeRegionVariable.h>",
			  "<VCELL/MembraneVariable.h>",
			  "<VCELL/MembraneRegionVariable.h>",
			  "<VCELL/CartesianMesh.h>",
			  "<VCELL/SimTool.h>",
			  "<VCELL/ODESolver.h>",
			  "<VCELL/DataSet.h>",
			  "<VCELL/EqnBuilderReactionDiffusion.h>",
			  "<VCELL/EqnBuilderReactionForward.h>",
			  "<VCELL/MembraneEqnBuilderForward.h>",
			  "<VCELL/Contour.h>",
			  "<VCELL/Element.h>",
			  "<VCELL/PdeSolverDiana.h>",
			  "<VCELL/VolumeRegion.h>",
			  "<VCELL/MembraneRegion.h>",
			  "<VCELL/VolumeRegionEqnBuilder.h>",
			  "<VCELL/MembraneRegionEqnBuilder.h>",
			  "<VCELL/EqnBuilderReactionDiffusionConvection.h>",
			  "<VCELL/MembraneEqnBuilderDiffusion.h>",
			  "<VCELL/SparseLinearSolver.h>",
			  "<VCELL/SparseVolumeEqnBuilder.h>",
			  "<VCELL/EllipticVolumeEqnBuilder.h>",
			  "<VCELL/SimulationMessaging.h>",
			  "<VCELL/FieldData.h>"
			 };
}                                    

 protected String[] getHeaderClassDefines() 
 {
	 return new String[] {
		  "CartesianMesh",
		  "VolumeVariable",
		  "MembraneVariable",
		  "ContourVariable",
		  "VolumeRegion"
	 };
}                     

 /*
protected String[] getHeaderConstants() {
	
	java.util.Vector<Constant> constantVector = new java.util.Vector<Constant>();
	
	Variable variables[] = simulationJob.getWorkingSim().getVariables();
	for (int i = 0; i < variables.length; i++){
		Variable var = variables[i];
		if (var instanceof Constant){
			constantVector.addElement((Constant)var);
		}
	}		

	int numConstants = constantVector.size();
	String constantString[] = new String[numConstants];
	
	for (int i=0;i<numConstants;i++){
		Constant constant = (Constant)constantVector.elementAt(i);
		try {
			constantString[i] = "double "+constant.getName()+" = " +constant.getConstantValue()+";";
		}catch (cbit.vcell.parser.ExpressionException e){
			constantString[i] = "double "+constant.getName()+" = " +constant.getExpression().toString()+";";
		}
	}	
	return constantString;
}            
*/

 protected String[] getHeaderIncludes()
 {
	  return new String[] {
			  "<VCELL/SimTypes.h>",
			  "<VCELL/VolumeVarContext.h>",
			  "<VCELL/VolumeRegionVarContext.h>",
			  "<VCELL/MembraneVarContext.h>",
			  "<VCELL/MembraneRegionVarContext.h>",
			  "<VCELL/Simulation.h>",
			  "<VCELL/VCellModel.h>",
			  "<VCELL/Feature.h>",
			  "<VCELL/FastSystem.h>"
	  };
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