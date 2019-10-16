/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.constants;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import org.vcell.model.rbm.MolecularType;

import cbit.vcell.mapping.SimulationContext.Application;

public class GuiConstants {
	
	public final static String PARAMETER_ESTIMATION_TAB_PARAMETERS = "Parameters";
	public final static String PARAMETER_ESTIMATION_TAB_EXPDATAIMPORT = "Experimental Data Import";
	public final static String PARAMETER_ESTIMATION_TAB_EXPDATAMAPPING = "Experimental Data Mapping";
	public final static String PARAMETER_ESTIMATION_TAB_RUNTASK = "Run Task";
	
	public final static String DOCUMENT_EDITOR_FOLDERNAME_PATHWAY = "Pathway";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_MODEL = "Physiology";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_DATA = "Experimental Data";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_APPLICATIONS = "Applications";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_BIOMODEL_PARAMETERS = "Parameters, functions, units, etc.";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_SCRIPTING = "Scripting";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_REACTIONS = "Reactions";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_DIAGRAM = "Reaction Diagram";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_STRUCTURES = "Structures";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_SPECIES = "Species";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_MOLECULAR_TYPE = MolecularType.typeName + "s";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_OBSERVABLES = "Observables";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_PATHWAYDIAGRAM = "Pathway Diagram";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_PATHWAYOBJECTS = "Pathway Objects";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_BIOPAXSUMMARY = "BioPAX Summary";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_BIOPAXTREE = "BioPAX Tree";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_GEOMETRY = "Geometry";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_SPECIFICATIONS = "Specifications";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_PROTOCOLS = "Protocols";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_SIMULATIONS = "Simulations";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_PARAMETERESTIMATION = "Parameter Estimation";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_MATH_ANNOTATION = "Annotation";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_MATH_VCML = "VCML Editor";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_MATH_GEOMETRY = "Geometry";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_MATH_SIMULATIONS = "Simulations";
	public final static String DOCUMENT_EDITOR_FOLDERNAME_MATH_OUTPUTFUNCTIONS = "Output Functions";

	public static final String ACTIONCMD_OPEN_APPLICATION_SIMULATION = "OpenApplicationSimulation";
	public static final String ACTIONCMD_OPEN_APPLICATION_GEOMETRY = "OpenApplicationGeometry";
	public static final String ACTIONCMD_OPEN_APPLICATION_MATH = "OpenApplicationMath";
	public static final String ACTIONCMD_OPEN_APPLICATION_DETSTOCH = "OpenApplicationDetStoch";

	public static final String ACTIONCMD_CREATE_STOCHASTIC_APPLICATION = 
			new ApplicationActionCommand.Create("Create Stochastic Application",Application.NETWORK_STOCHASTIC).getLabel();
	public static final String ACTIONCMD_CREATE_DETERMINISTIC_APPLICATION = 
			new ApplicationActionCommand.Create("Create Non-stochastic Application",Application.NETWORK_DETERMINISTIC).getLabel();
	public static final String ACTIONCMD_CREATE_RULEBASED_APPLICATION = 
			new ApplicationActionCommand.Create("Create Network-Free Application",Application.RULE_BASED_STOCHASTIC).getLabel();
	public static final String ACTIONCMD_EXPORT = "Export";
	
	public static final String ACTIONCMD_OPEN_APPLICATION = "Open";
	public static final String ACTIONCMD_COPY_APPLICATION = new ApplicationActionCommand.CopyAsIs("Copy").getLabel();
	public static final String ACTIONCMD_RENAME_APPLICATION = "Rename";
	public static final String ACTIONCMD_DELETE_APPLICATION = "Delete";
	public static final String ACTIONCMD_NON_SPATIAL_COPY_TO_DETERMINISTIC_APPLICATION = 
			new ApplicationActionCommand.CopyChange("Non-Spatial Copy To Deterministic Application",false,false,Application.NETWORK_DETERMINISTIC).getLabel();
	public static final String ACTIONCMD_NON_SPATIAL_COPY_TO_RULEBASED_APPLICATION = 
			new ApplicationActionCommand.CopyChange("Non-Spatial Copy To Network-Free Application",false,false,Application.RULE_BASED_STOCHASTIC).getLabel();
	public static final String ACTIONCMD_NON_SPATIAL_COPY_TO_STOCHASTIC_APPLICATION = 
			new ApplicationActionCommand.CopyChange("Non-Spatial Copy To Stochastic Application",false,false,Application.NETWORK_STOCHASTIC).getLabel();
	
	public static final String MENU_TEXT_APP_NEWBIOMODEL = "New BioModel From App";
	public static final String MENU_TEXT_APP_COPY = "Copy";
	public static final String MENU_TEXT_APP_COPYAS = "Copy As";
	public static final String MENU_TEXT_SPATIAL_APPLICATION = "Spatial";
	public static final String MENU_TEXT_NON_SPATIAL_APPLICATION = "Non-Spatial";
	public static final String MENU_TEXT_DETERMINISTIC_APPLICATION = "Deterministic";
	public static final String MENU_TEXT_RULEBASED_APPLICATION = "Network-Free";
	public static final String MENU_TEXT_STOCHASTIC_APPLICATION = "Stochastic";

	public static final String ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_DETERMINISTIC_APPLICATION = 
			new ApplicationActionCommand.CopyChange("Spatial Copy To NonSpatial Deterministic Application",true,false,Application.NETWORK_DETERMINISTIC).getLabel();
	public static final String ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_STOCHASTIC_APPLICATION = 
			new ApplicationActionCommand.CopyChange("Spatial Copy To NonSpatial Stochastic Application",true,false,Application.NETWORK_STOCHASTIC).getLabel();
	public static final String ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_RULEBASED_APPLICATION = 
			new ApplicationActionCommand.CopyChange("Spatial Copy To NonSpatial Rulebased Application",true,false,Application.RULE_BASED_STOCHASTIC).getLabel();
	
	public static final String ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_DETERMINISTIC_APPLICATION = 
			new ApplicationActionCommand.CopyChange("Spatial Copy To Spatial Deterministic Application",true,true,Application.NETWORK_DETERMINISTIC).getLabel();
	public static final String ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_STOCHASTIC_APPLICATION = 
			new ApplicationActionCommand.CopyChange("Spatial Copy To Spatial Stochastic Application",true,true,Application.NETWORK_STOCHASTIC).getLabel();
	public static final String ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_RULEBASED_APPLICATION = 
			new ApplicationActionCommand.CopyChange("Spatial Copy To Spatial Rulebased Application",true,true,Application.RULE_BASED_STOCHASTIC).getLabel();
	
	public static final String ACTIONCMD_CHANGE_GEOMETRY = "Change Geometry...";
//	public static final String ACTIONCMD_VIEW_CHANGE_GEOMETRY = "View / Change Geometry";
	public static final String ACTIONCMD_CREATE_MATH_MODEL = "Create Math Model";
//	public static final String ACTIONCMD_OPEN_GEOMETRY = "Open Geometry";
	public static final String ACTIONCMD_VIEW_SURFACES = "View Surfaces";
	public static final String ACTIONCMD_EDIT_OCCURRED_GEOMETRY = "Edit Occurred Geometry";
	public static final String ACTIONCMD_CREATE_GEOMETRY = "Create Geometry";
	public static final String ACTIONCMD_EDITCURRENTSPATIAL_GEOMETRY = "Edit Current Spatial Geometry";

	public static final String ACTIONCMD_ADD_EVENT = "Add Event";
	
	public static final String ACTIONCMD_CREATE_DATA_SYMBOL = "CreateDataSymbol";
	public static final String ACTIONCMD_DELETE_DATA_SYMBOL = "DeleteDataSymbol";
	public static final String ACTIONCMD_VIEW_FIELD_DATA = "OpenFieldDataManager";
	public static final String ACTIONCMD_ADD_VFAP_DATASET = "AddDatasetFromvFrap";
	public static final String ACTIONCMD_ADD_VFRAP_DERIVED_DATA = "AddDerivedDataFromvFrap";
	
	public static final String VCELL_SUPPORT_ACCOUNT_ID = "VCellSupport";
	
	public static final Border ProblematicTextFieldBorder = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.red), BorderFactory.createEmptyBorder(2, 2, 2, 2));
	public static final Border TAB_PANEL_BORDER = new LineBorder(new Color(127, 157,185));
}
