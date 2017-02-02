/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model.common;

import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularType;

import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.VCML;
import cbit.vcell.math.Variable;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;


public class VCellErrorMessages {
//	public static void main(String[] args) {
//		try {
//			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
//			
//			javax.swing.JFrame frame = new javax.swing.JFrame();
//			DialogUtils.showErrorDialog(frame, getErrorMessage(NETWORK_FAIL_MESSAGE, ""));			
//		} catch (Throwable exception) {
//			System.err.println("Exception occurred in main() of BioModelDbTreePanelTest");
//			exception.printStackTrace(System.out);
//		}
//	}
	public static String getMassActionSolverMessage(String reactionStepName, String reasonStr) 
	{
		return "Failed to intepret kinetic rate for reaction '" + reactionStepName + "' as mass action.\n" + "     " + reasonStr;
	}
	
	
	public static String getSemiFVSolverCompiledSolverDeprecated(Simulation sim) {
		return "<html>Simulation '" + sim.getName() + "' : <br>The selected solver is no longer fully supported. " +				
				" We recommend using <font color=blue>" + SolverDescription.SundialsPDE.getDisplayLabel() + "</font>. " +
				"<br>However to proceed with the equivalent solver, <font color=blue>" 
				+ SolverDescription.FiniteVolumeStandalone.getDisplayLabel() + "</font>. click OK.</html>";
	}
	public static String getSmoldynUnexpectedSurface(CompartmentSubDomain compart0, CompartmentSubDomain compart1) {
		return "Found an unexpected surface between compartment '" + compart0.getName() + "' and compartment '" + compart1.getName() + "'. Finer mesh is recommended.";
	}
	
	public static String getSmoldynMaxMolReachedErrorMessage(long estimateMaxMol, int maxMolLimit) {
		return "Estimated total number of molecules ("+estimateMaxMol+") exceeded limit(" + maxMolLimit + ")";
	}
	
	public static String getSmoldynWrongCoordinates(String coordName, int dimension, Variable var, Expression exp) {
		return "Cannot use coordinate " + coordName + " in initial concentration for variable '" + var.getName() + "' in " + dimension + "D simulation. \n" +
				"The expression is: " + exp.infix() + ".";
	}
	
	private final static String PLACE_HOLDER = "__PLACE_HOLDER__";
	private final static String VCellSupport = "VCell Support(vcell_support@uchc.edu)";
	
	// =============== connection problems start ========== //
	public static final String BAD_CONNECTION_MESSAGE = "Your computer is unable to " + PLACE_HOLDER + " to the Virtual Cell server" + PLACE_HOLDER 
			+ ". To save a copy of an open model to your computer, export to VCML format (File > Export > VCML).\nIf problem persists, it may be due to a firewall problem. " +
			"Contact your network administrator and send the error message below to " + VCellSupport + ".";
	public static final String FAIL_LOAD_MESSAGE = "Failed to load document. Possible reasons :\n" +
						"1. You are no longer connected to the server. Reconnect in order to retrieve model. \n" +
						"2. You previously saved this model using a newer version of VCell. Open model in that version.";

	public static final String FAIL_SAVE_MESSAGE = "Failed to save document. Possible reason:\n" +
		"You are no longer connected to the server. Reconnect in order to save model or export in VCML format (File > Export > VCML) to save a copy to your computer.";
	public static final String NETWORK_FAIL_MESSAGE = "The Virtual Cell server is unavailable. Please check VCell website or your email for maintenance updates. Contact" 
		+ VCellSupport + " for more details."; 

	public static final String AUTHEN_FAIL_MESSAGE = "The userid (" + PLACE_HOLDER + ") or password you " +
			"entered is not correct. Please go to Server->Change User... to reenter your userid and password or click \"Forgot Login Password\".";
	// ============ connection problems end ========== //
	
		
	// ========== BioModel start ==========================//
	public static final String PressEscToUndo = "<br><font color=red>Press Esc to undo this change.</font>";
	public static final String RightClickComponentForBond = "<br><font color=blue>Right click on the " + MolecularComponent.typeName + " above to edit a Bond.</font>";
	public static final String RightClickComponentForState = "<br><font color=blue>Right click on the " + MolecularComponent.typeName + " above to edit a State.</font>";
	public static final String RightClickComponentToEdit = "<br><font color=blue>Right click on this " + MolecularComponent.typeName + " to edit it.</font>";
	public static final String ClickShowAllComponents = "<br><font color=blue>Check the 'Show all " + MolecularComponent.typeName + "s' box above to see all possible " + MolecularComponent.typeName + ".</font>";
	public static final String RightClickToAddMolecules = "<br><font color=blue>Right click on this Pattern to add more " + MolecularType.typeName + "s.</font>";

	public static final String TripleClickOrRightClick = "<br><font color=blue>Triple click to edit an element, Right click for more options.</font>";

	public static final String SpecifyMolecularTypes = "Specify " + MolecularType.typeName;
	public static final String AddMolecularTypes = "Add " + MolecularType.typeName;

	public static final String MustBeRuleBased = "Feature available for Network-Free models only.\nPlease define a " + MolecularType.typeName + " first.";
//	public static final String MustNotBeRuleBased = "Feature available for non Network-Free models only.";
//	public static final String OneStructureOnly = "Currently Network-Free modeling is only available for single structure.";
//	public static final String NFSimAppNotAllowedForMultipleStructures = "Unable to create a Network-Free Application when multiple Structures are present.";
	
	// ========== BioModel end =======================//
	
	
	// ========== math description start ================== //
	public final static String MATH_DESCRIPTION_GEOMETRY_1 = "no geometry defined";
	public final static String MATH_DESCRIPTION_GEOMETRY_2 = "unable to get region information from geometry";
	public final static String MATH_DESCRIPTION_GEOMETRY_3 = "Geometry '" + PLACE_HOLDER + "' SubDomain name '" + PLACE_HOLDER +"' does not match any "+VCML.CompartmentSubDomain+" name in "+VCML.MathDescription+" -- check spelling/case";
	public final static String MATH_DESCRIPTION_GEOMETRY_4 = "There should be a MembraneSubDomain between compartments '"+PLACE_HOLDER+"' and '"+PLACE_HOLDER+"'";
	public final static String MATH_DESCRIPTION_GEOMETRY_5 = "MembraneSubDomain inside='"+PLACE_HOLDER+"', outside='"+PLACE_HOLDER+"' is not in geometry";
	
	public final static String MATH_DESCRIPTION_CONSTANT = "Constant cannot be evaluated to a number: " + PLACE_HOLDER;	
	
	// non spatial model
	public final static String MATH_DESCRIPTION_COMPARTMENT_MODEL_1 = "Compartmental Model requires exactly one "+ VCML.CompartmentSubDomain;
	public final static String MATH_DESCRIPTION_COMPARTMENT_MODEL_2 = "Compartmental Model requires the subdomain be a " + VCML.CompartmentSubDomain;
	public final static String MATH_DESCRIPTION_COMPARTMENT_MODEL_3 = "Compartmental model, unexpected equation of type " + VCML.PdeEquation + ", must include only " + VCML.OdeEquation;
	public final static String MATH_DESCRIPTION_COMPARTMENT_MODEL_4 = "Compartmental model, expecting at least one " + VCML.OdeEquation;
	public final static String MATH_DESCRIPTION_COMPARTMENT_MODEL_5 = "Compartmental model, must declare an " + VCML.OdeEquation + " for each " + VCML.VolumeVariable;
	public final static String MATH_DESCRIPTION_COMPARTMENT_MODEL_6 = "Compartmental model, must not declare any " + PLACE_HOLDER;
	
	// spatial model
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_1 = "Spatial model, can't find a matching geometry subdomain for math subdomain '" + PLACE_HOLDER + "'. Math subdomain names must match geometry subdomain names .";	
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_2 = "Spatial model, unexpected subdomain type for subdomain " + PLACE_HOLDER;
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_3 = "Spatial model, there are " + PLACE_HOLDER + " subdomains in geometry, but "+PLACE_HOLDER+" "+VCML.CompartmentSubDomain+"s in math description. They must match.";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_4 = "Spatial model, there are no " + VCML.FilamentSubDomain + "s defined, cannot define "+VCML.FilamentVariable + " or " + VCML.FilamentRegionVariable;
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_5 = "Spatial model, there are no " + VCML.MembraneSubDomain + "s defined, cannot define " + VCML.MembraneVariable + " or " + VCML.MembraneRegionVariable;
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_6 = "Duplicate subDomains "+PLACE_HOLDER+" and "+PLACE_HOLDER;
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_7 = "Duplicate membrane subdomains between compartments "+PLACE_HOLDER+" and "+PLACE_HOLDER;
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_8 = "CompartmentSubDomain priorities must be unique see '"+PLACE_HOLDER+"' and '"+PLACE_HOLDER+"'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_9 = PLACE_HOLDER + " and " + PLACE_HOLDER + " must both have periodic boundary condition for math subdomain '" + PLACE_HOLDER + "'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_10 = "Spatial Model, PDE for '" + PLACE_HOLDER + "' in '"+PLACE_HOLDER + "' must have a boundary condition at membrane '"+ PLACE_HOLDER + "'. Specify EITHER BoundaryValue in the PDE OR a JumpCondition on the membrane.";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_10A = "Spatial Model, PDE for '" + PLACE_HOLDER + "' in '"+PLACE_HOLDER + "' has a boundary value for membrane '"+ PLACE_HOLDER + "'. It requires \"Boundary " + PLACE_HOLDER + " [Flux OR Value]\" in '" + PLACE_HOLDER + "'.";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_11 = "Spatial Model, Jump condition for '" + PLACE_HOLDER + "' on membrane '" + PLACE_HOLDER+"' has no matching PDE in either '"+PLACE_HOLDER+"' or '"+PLACE_HOLDER+"'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_12 = "Spatial Model, Jump condition for '" + PLACE_HOLDER + "' on membrane '" + PLACE_HOLDER+"' has a non-zero inward flux but no PDE in compartment '" + PLACE_HOLDER+"'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_13 = "Spatial Model, Jump condition for '" + PLACE_HOLDER + "' on membrane '" + PLACE_HOLDER+"' has a non-zero outward flux but no PDE in compartment '"+PLACE_HOLDER+"'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_14 = "Spatial Model, cannot mix " + VCML.OdeEquation + " and " + VCML.PdeEquation +" for variable '"+PLACE_HOLDER+ "'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_15 = "Spatial Model, cannot mix " + VCML.Steady + " and " + VCML.Unsteady + " " + VCML.PdeEquation + "s for variable '" +PLACE_HOLDER+ "'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_16 = "Spatial Model, there is neither a "+VCML.PdeEquation+" nor an " + VCML.OdeEquation+" for variable '"+PLACE_HOLDER + "'";	
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_17 = "Spatial Model, VolumeRegionEquation for '" + PLACE_HOLDER+"' in '" + PLACE_HOLDER+"' does not have a jump condition on membrane '" + PLACE_HOLDER + "'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_18 = "There should be at least one " + VCML.VolumeRegionEquation+" defined for volume region variable '" + PLACE_HOLDER + "'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_19 = "There should be at least one " + VCML.MembraneRegionEquation+" defined for membrane region variable '" + PLACE_HOLDER + "'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_20 = "There should be a " + VCML.FilamentRegionEquation +" defined for filament region variable "+ PLACE_HOLDER + " in " + VCML.FilamentSubDomain + " " + PLACE_HOLDER;
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_21 = "There should be a "+ VCML.OdeEquation+" defined for variable "+PLACE_HOLDER+" for " + VCML.FilamentSubDomain + " " + PLACE_HOLDER;
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_22 = "Events are not supported in spatial models.";
	
	// non spatial stochastic
	public final static String MATH_DESCRIPTION_COMPARTMENT_STOCHASTIC_MODEL_1 = "stochastic model requires at least one stochastic volume variable";
	public final static String MATH_DESCRIPTION_COMPARTMENT_STOCHASTIC_MODEL_2 = "stochastic model requires at least one jump process";
	
	// ========== math description end ================== //
	
	// simulation problems
	public final static String SIMULATION_OVERRIDE_NOTFOUND = "Overridden parameter '"+PLACE_HOLDER+"' in simulation '"+PLACE_HOLDER+"' not no longer exists, please edit this parameter override list";
	public static final String SIMULATION_SENSANAL_FASTSYSTEM = "Sensitivity Analysis for a math with Fast Systems not supported. disable sensitivity analysis for simulation '"+PLACE_HOLDER+"'";

	
	public final static String RunSims_1 = "Simulation '" + PLACE_HOLDER + "' is completed already. "
		+ "\n\nDo you want to continue anyway?";
	public static final String RunSims_2 = "Simulation '" + PLACE_HOLDER + "' has non-uniform spatial step. " 
		+ "This might affect the accuracy of the solution.\n\n"
		+ PLACE_HOLDER + "\n\nDo you want to continue anyway?";
	public static final String RunSims_3 =  "<html>The time step for " + SolverDescription.Smoldyn.getDisplayLabel()
			+ " needs to satisfy stability constraint" 
			+ "<dl><dd><i>\t\u0394t &lt; s<sup>2</sup> / ( 2D<sub>max</sub> )</i></dd></dl>" 
			+ "Where <i>s</i> is spatial resolution and <i>D<sub>max</sub></i> is the diffusion " 
			+ "coefficient of the fastest diffusing species. For this simulation, <i>s</i> = <i>" + PLACE_HOLDER  
			+ "</i> and <i>D<sub>max</sub></i> = <i>" + PLACE_HOLDER + "</i>. Hence choose <i>\t\u0394t</i> such that "
			+ "<dl><dd><i>\t\u0394t &lt;" + PLACE_HOLDER
			+ "</dd></dl></html>"; 
	public static final String RunSims_4 = "The simulation mesh size (" + PLACE_HOLDER + ")" +
			" for '" + PLACE_HOLDER + "' results in different number of geometric regions [" + PLACE_HOLDER + "] than " +
			"the number of geometric regions [" + PLACE_HOLDER + "] resolved in the Geometry Viewer." +
			"\n\nThis can affect the accuracy of the solution. Finer simulation mesh is recommended."
			 + "\n\nDo you want to continue anyway?"; 
	public static final String RunSims_5 = "The mesh size (" + PLACE_HOLDER + ")" 
		+ " for simulation '" + PLACE_HOLDER + "' is finer than the original image resolution (" + PLACE_HOLDER + ")" 
		+ ".\n\nThis will not improve the accuracy of the solution and can take longer to run. Original resolution (" 
		+ PLACE_HOLDER + ") or coarser mesh is recommended."  + "\n\nDo you want to continue anyway?"; 
	public final static String RunSims_6 = "You have choosen " + SolverDescription.SundialsPDE.getDisplayLabel()  
		+ " to solve a time dependent diffusion/advection system. This could take long time. You can choose "
		+ SolverDescription.FiniteVolumeStandalone.getDisplayLabel() + ".\n\nDo you want to continue anyway?";

	
	public final static String SensitivityAnalysis_Help = "Sensitivity Analysis " +
			"is a technique for determining the influence of small parameter changes on model output.";
	
	public static final String getErrorMessage(String baseErrorMessage, Object... objects) {
		String returnMessage = baseErrorMessage;
		if (baseErrorMessage.contains(PLACE_HOLDER)) {
			int counter = 0;
			while (returnMessage.contains(PLACE_HOLDER)) {
				if (counter >= objects.length) {
					throw new RuntimeException("VCellErrorMessages.getErrorMessage(), not enough arguments for place holders");
				}
				returnMessage = returnMessage.replaceFirst(PLACE_HOLDER, objects[counter].toString());
				counter ++;
			}
		}
		if (returnMessage.contains(PLACE_HOLDER)) {
			throw new RuntimeException("VCellErrorMessages.getErrorMessage(), not enough arguments for place holders, the message is " + returnMessage);
		}
		return returnMessage;
	}
}
