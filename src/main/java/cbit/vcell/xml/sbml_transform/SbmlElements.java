/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

/**
 * 
 */
package cbit.vcell.xml.sbml_transform;

/**
 * @author mlevin
 *
 */
public class SbmlElements {
	public static final String ListOfUnitDefs = "   <listOfUnitDefinitions>\r\n" + 
		"      <unitDefinition id=\"substance\">\r\n" + 
		"        <listOfUnits>\r\n" + 
		"          <unit kind=\"mole\" scale=\"-6\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
		"        </listOfUnits>\r\n" + 
		"      </unitDefinition>\r\n" + 
		
		"      <unitDefinition id=\"volume\">\r\n" + 
		"        <listOfUnits>\r\n" + 
		"          <unit kind=\"litre\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
		"        </listOfUnits>\r\n" + 
		"      </unitDefinition>\r\n" + 
		
	//	"      <unitDefinition id=\"area\">\r\n" + 
	//	"        <listOfUnits>\r\n" + 
	//	"          <unit kind=\"metre\" exponent=\"2\" scale=\"-6\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
	//	"        </listOfUnits>\r\n" + 
	//	"      </unitDefinition>\r\n" + 
		
		"      <unitDefinition id=\"molecules\">\r\n" + 
		"        <listOfUnits>\r\n" + 
		"          <unit kind=\"item\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
		"        </listOfUnits>\r\n" + 
		"      </unitDefinition>\r\n" + 
		
		"      <unitDefinition id=\"umol_litre_um_3\">\r\n" + 
		"        <listOfUnits>\r\n" + 
		"          <unit kind=\"mole\" scale=\"-21\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
		"        </listOfUnits>\r\n" + 
		"      </unitDefinition>\r\n" + 
		
		"      <unitDefinition id=\"um2\">\r\n" + 
		"        <listOfUnits>\r\n" + 
		"          <unit kind=\"metre\" exponent=\"2\" scale=\"-6\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
		"        </listOfUnits>\r\n" + 
		"      </unitDefinition>\r\n" + 
		
		"      <unitDefinition id=\"um3\">\r\n" + 
		"        <listOfUnits>\r\n" + 
		"          <unit kind=\"metre\" exponent=\"3\" scale=\"-6\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
		"        </listOfUnits>\r\n" + 
		"      </unitDefinition>\r\n" + 
		
		"      <unitDefinition id=\"firstOrder\">\r\n" + 
		"        <listOfUnits>\r\n" + 
		"          <unit kind=\"second\" exponent=\"-1\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
		"        </listOfUnits>\r\n" + 
		"      </unitDefinition>\r\n" + 
		
		"      <unitDefinition id=\"secondOrder\">\r\n" + 
		"        <listOfUnits>\r\n" + 
		"          <unit kind=\"mole\" multiplier=\"-1\" scale=\"-6\"/>\r\n" + 
		"          <unit kind=\"litre\" exponent=\"1\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
		"          <unit kind=\"second\" exponent=\"-1\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
		"        </listOfUnits>\r\n" + 
		"      </unitDefinition>\r\n" + 
		
		"      <unitDefinition id=\"thirdOrder\">\r\n" + 
		"        <listOfUnits>\r\n" + 
		"          <unit kind=\"mole\" multiplier=\"-2\" scale=\"-12\"/>\r\n" + 
		"          <unit kind=\"litre\" exponent=\"2\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
		"          <unit kind=\"second\" exponent=\"-1\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
		"        </listOfUnits>\r\n" + 
		"      </unitDefinition>\r\n" + 
		
		"      <unitDefinition id=\"uM_s_1\">\r\n" + 
		"        <listOfUnits>\r\n" + 
		"          <unit kind=\"dimensionless\" multiplier=\"0.001\" offset=\"0\"/>\r\n" + 
		"          <unit kind=\"metre\" exponent=\"-3\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
		"          <unit kind=\"mole\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
		"          <unit kind=\"second\" exponent=\"-1\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
		"        </listOfUnits>\r\n" + 
		"      </unitDefinition>\r\n" + 
		
	//	"      <unitDefinition id=\"s_1\">\r\n" + 
	//	"        <listOfUnits>\r\n" + 
	//	"          <unit kind=\"second\" exponent=\"-1\" multiplier=\"1\" offset=\"0\"/>\r\n" + 
	//	"        </listOfUnits>\r\n" + 
	//	"      </unitDefinition>\r\n" + 
		"    </listOfUnitDefinitions>";
	private SbmlElements() {}

	public static final String Id_attrib = "id";
	public static final String Compart_attrib = "compartment";
	public static final String Name_attrib = "name";
	public static final String SBML_tag = "sbml";
	public static final String Model_tag = "model";
	
	public static final String ListOfUnitDefs_tag = "listOfUnitDefinitions";
	public static final String ListOfUnits_tag = "listOfUnits";
	public static final String Litre_val = "litre";
	public static final String Um3_val = "um3";
	public static final String Um2_val = "um2";
	
	public static final String ListOfCompartm_tag = "listOfCompartments";
	public static final String Compartment_tag = "compartment";
	public static final String CompOutside_attrib = "outside";
	public static final String Size_attrib = "size";
	public static final String CompSpatialDim_attrib = "spatialDimensions";
	public static final String Units_attrib = "units";
	
	public static final String ListOfReactions_tag = "listOfReactions";
	public static final String React_tag = "reaction";
	public static final String Rev_attrib = "reversible";
	public static final String ListofReactants_tag = "listOfReactants";
	public static final String SpRef_tag = "speciesReference";
	public static final String Species_tag = "species";
	public static final String Species_attr = "species";
	public static final String ListOfProds_tag = "listOfProducts";
	public static final String KinLaw_tag = "kineticLaw";
	public static final String Math_tag = "math";
	public static final String CiMath_tag = "ci";
	public static final String Apply_tag = "apply";
	public static final String Times_tag = "times";
	public static final String Minus_tag = "minus";
	public static final String Plus_tag = "plus";

	
	
}
