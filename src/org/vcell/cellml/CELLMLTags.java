/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.cellml;
import java.util.ArrayList;
import java.util.TreeMap;
/**
 * Controlled vocabulary elements for cell ml. Although this violates the 'standards' in naming static variables, the
 'element' names are all caps, and the 'attribute' names are small letters.
 * Creation date: (8/26/2003 9:56:07 AM)
 * @author: Rashad Badrawi
 */
public class CELLMLTags {

	private static ArrayList<String> CellUnits = new ArrayList<String>();
	private static TreeMap<String, String> prefixConverter = new TreeMap<String, String>();

	// CellML namespace : was in cbit.vcell.vcml.Translator in project VCell; moving it here to remove dependency of VCell Util on VCell project
	public static final String CELLML_NS = "http://www.cellml.org/cellml/1.0#";
	
	//elements
	public static final String MODEL = "model";
	public static final String COMPONENT = "component";
	public static final String VARIABLE = "variable";
	public static final String REACTION = "reaction";
	public static final String VAR_REF = "variable_ref";
	public static final String ROLE = "role";
	public static final String MATH = "math";
	public static final String CONNECTION = "connection";
	public static final String MAP_COMP = "map_components";
	public static final String MAP_VAR = "map_variables";
	public static final String GROUP = "group";
	public static final String REL_REF = "relationship_ref";
	public static final String COMP_REF = "component_ref";
	public static final String UNITS = "units";
	public static final String UNIT = "unit";
	public static final String MOLE = "mole";
	public static final String ITEM = "item";
	//attributes
	public static final String initial_value = "initial_value";
	public static final String name = "name";
	public static final String public_interface = "public_interface";
	public static final String private_interface = "private_interface";
	public static final String reversible = "reversible";
	public static final String units = "units";
	public static final String variable = "variable";
	public static final String role = "role";
	public static final String direction = "direction";
	public static final String delta_variable = "delta_variable";
	public static final String stoichiometry = "stoichiometry";
	public static final String comp1 = "component_1";
	public static final String comp2 = "component_2";
	public static final String var1 = "variable_1";
	public static final String var2 = "variable_2";
	public static final String relationship = "relationship";
	public static final String comp = "component";
	public static final String baseUnits = "base_units";
	public static final String exponent = "exponent";
	public static final String prefix = "prefix";
	public static final String mult = "multiplier";
	public static final String offset = "offset";

	//attribute values
	public static final String reactantRole = "reactant";
	public static final String productRole = "product";
	public static final String modifierRole = "modifier";
	public static final String activatorRole = "activator";
	public static final String inhibitorRole = "inhibitor";
	public static final String catalystRole = "catalyst";
	public static final String rateRole = "rate";
	public static final String rxnForward = "forward";
	
	public static final String inInterface = "in";
	public static final String outInterface = "out";
	public static final String noneInterface = "none";
	public static final String noDimUnit = "dimensionless";
	public static final String envComp = "environment";
	public static final String timeVar = "t";
	public static final String timeVarCell = "time";
	public static final String defTimeUnit = "second";
	//public static final String encapRelation = "encapsulation";
	//public static final String containRelation = "containment";
		
	static {
		prefixConverter.put("yotta", "24");
		prefixConverter.put("zetta", "21");
		prefixConverter.put("exa", "18");
		prefixConverter.put("peta", "15");
		prefixConverter.put("tera", "12");
		prefixConverter.put("giga", "9");
		prefixConverter.put("mega", "6");
		prefixConverter.put("kilo", "3");
		prefixConverter.put("hecto", "2");
		prefixConverter.put("deka", "1");
		prefixConverter.put("deci", "-1");
		prefixConverter.put("centi", "-2");
		prefixConverter.put("milli", "-3");
		prefixConverter.put("micro", "-6");
		prefixConverter.put("nano", "-9");
		prefixConverter.put("pico", "-12");
		prefixConverter.put("femto", "-15");
		prefixConverter.put("atto", "-18");
		prefixConverter.put("zepto", "-21");
		prefixConverter.put("yocto", "-24");

		CellUnits.add("ampere");
		CellUnits.add("becquerel");
		CellUnits.add("candela");
		CellUnits.add("Celsius");
		CellUnits.add("coulomb");
		CellUnits.add("dimensionless");
		CellUnits.add("farad");
		CellUnits.add("gram");
		CellUnits.add("gray");
		CellUnits.add("henry");
		CellUnits.add("hertz");
		CellUnits.add("joule");
		CellUnits.add("katal");
		CellUnits.add("kelvin");
		CellUnits.add("kilogram");
		CellUnits.add("litre");
		CellUnits.add("lumen");
		CellUnits.add("lux");
		CellUnits.add("metre");
		CellUnits.add("mole");
		CellUnits.add("newton");
		CellUnits.add("ohm");
		CellUnits.add("pascal");
		CellUnits.add("radian");
		CellUnits.add("second");
		CellUnits.add("siemens");
		CellUnits.add("sievert");
		CellUnits.add("steradian");
		CellUnits.add("tesla");
		CellUnits.add("volt");
		CellUnits.add("watt");
		CellUnits.add("weber");	
	}

	private CELLMLTags() {}


	public static boolean isCellBaseUnit(String symbol) {

		return CellUnits.contains(symbol);
	}


	public static int prefixToScale(String prefix) {

		String scaleStr = (String)prefixConverter.get(prefix);
		if (scaleStr == null) {
			System.err.println("Unit Prefix: " + prefix + " not found. May result in unit inconsistency.");
			return 0;
		}
		return Integer.parseInt(scaleStr);
	}
}
