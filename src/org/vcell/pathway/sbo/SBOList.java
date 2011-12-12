/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.sbo;

import java.util.HashMap;
import java.util.Map;


public class SBOList {

	protected static Map<String, SBOTerm> indexToTerm = new HashMap<String, SBOTerm>();
	
	public static SBOTerm createTerm(String index, String symbol, String name, String description) {
		SBOTerm sboTerm = SBOUtil.createSBOTermFromIndex(index, symbol, name, description);
		indexToTerm.put(index, sboTerm);
		return sboTerm;
	}
	
	public static SBOTerm getTermFromIndex(String index) { return indexToTerm.get(index); }
	
	public static final SBOTerm sbo0000009 = createTerm("0000009", "?", "kinetic constant",
			"Numerical parameter that quantifies the velocity of a chemical reaction.");
	public static final SBOTerm sbo0000025 = createTerm("0000025", "kcat", "catalytic rate constant",
			"Numerical parameter that quantifies the velocity of an enzymatic reaction.");
	public static final SBOTerm sbo0000027 = createTerm("0000027", "Km", "Michaelis constant",
			"Substrate concentration at which the velocity of reaction is half its maximum. Michaelis constant is an experimental " + 
			"parameter. According to the underlying molecular mechanism it can be interpreted differently in terms of microscopic " + 
			"constants.");
	public static final SBOTerm sbo0000147 = createTerm("0000147", "T", "thermodynamic temperature",
			"Temperature is the physical property of a system which underlies the common notions of \"hot\" and \"cold\"; the " + 
			"material with the higher temperature is said to be hotter. Temperature is a quantity related to the average kinetic " + 
			"energy of the particles in a substance. The 10th Conference Generale des Poids et Mesures decided to define " + 
			"the thermodynamic temperature scale by choosing the triple point of water as the fundamental fixed point, " + 
			"and assigning to it the temperature 273,16 degrees Kelvin, exactly (0.01 degree Celsius).");
	public static final SBOTerm sbo0000186 = createTerm("0000186", "Vmax", "maximal velocity",
			"Limiting maximal velocity of an enzymatic reaction, reached when the substrate is in large excess and all the enzyme " + 
			"is complexed.");
	public static final SBOTerm sbo0000196 = createTerm("0000196", "[X]", "concentration of an entity pool",
			"The amount of an entity per unit of volume. ");	
	public static final SBOTerm sbo0000261 = createTerm("0000261", "Ki", "inhibitory constant",
			"Dissociation constant of a compound from a target of which it inhibits the function. ");	
	public static final SBOTerm sbo0000282 = createTerm("0000282", "Kd", "dissociation constant",
			"Equilibrium constant that measures the propensity of a larger object to separate (dissociate) reversibly into " + 
			"smaller components, as when a complex falls apart into its component molecules, or when a salt splits up into its " + 
			"component ions. The dissociation constant is usually denoted Kd and is the inverse of the affinity constant.");	
	public static final SBOTerm sbo0000302 = createTerm("0000302", "kcat/Km", "catalytic efficiency",
			"Constant representing the actual efficiency of an enzyme, taking into account its microscopic catalytic activity " + 
			"and the rates of substrate binding and dissociation.");
	public static final SBOTerm sbo0000304 = createTerm("0000304", "pH", "pH",
			"Negative logarithm (base 10) of the activity of hydrogen in a solution. Ina diluted solution, this activity is " + 
			"equal to the concentration of protons (in fact of ions H3O+). The pH is proportional to the chemical potential of " + 
			"hydrogen, by the relation: pH = -µH ÷ 2.3RT. (with µH=-RTln[H+]).");
	public static final SBOTerm sbo0000320 = createTerm("0000320", "kcatp", "product catalytic rate constant",
			"Numerical parameter that quantifies the velocity of product creation by a reversible enzymatic reaction. ");
	public static final SBOTerm sbo0000322 = createTerm("0000322", "Kms", "Michaelis constant for substrate",
			"Substrate concentration at which the velocity of product production by the forward activity of a reversible enzyme " + 
			"is half its maximum.");
	public static final SBOTerm sbo0000324 = createTerm("0000324", "Vmaxf", "forward maximal velocity",
			"Limiting maximal velocity of the forward reaction of a reversible enzyme, reached when the substrate is in large " + 
			"excess and all the enzyme is complexed.");
	public static final SBOTerm sbo0000345 = createTerm("0000345", "t", "time",
			"Fundmental quantity of the measuring system used to sequence events, to compare the durations of events and the " + 
			"intervals between them, and to quantify the motions or the transformation of entities. The SI base unit for time " + 
			"is the SI second. The second is the duration of 9,192,631,770 periods of the radiation corresponding to the " + 
			"transition between the two hyperfine levels of the ground state of the caesium 133 atom.");

}
