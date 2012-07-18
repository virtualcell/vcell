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

@Deprecated
public class SBOList {

	public static final String NO_SYMBOL = "?";

	protected static Map<String, SBOTerm> indexToTerm = new HashMap<String, SBOTerm>();

	@Deprecated
	public static SBOTerm createTerm(String index, String symbol, String name, String description) {
		SBOTerm sboTerm = SBOUtil.createSBOTermFromIndex(index, symbol, name, description, "");
		indexToTerm.put(index, sboTerm);
		return sboTerm;
	}

	public static SBOTerm getTermFromIndex(String index) { return indexToTerm.get(index); }

	public static final SBOTerm sbo0000000SystBiolRepresentation = 
		createTerm("0000000", NO_SYMBOL, "systems biology representation",
				"Representation of an entity used in a systems biology knowledge reconstruction, " + 
		"such as a model, pathway, network.");
	public static final SBOTerm sbo0000001RateLaw = 
		createTerm("0000001", NO_SYMBOL, "rate law",
				"mathematical description that relates quantities " + 
		"of reactants to the reaction velocity.");
	public static final SBOTerm sbo0000002QuanSystDescParam = 
		createTerm("0000002", NO_SYMBOL, "quantitative systems description parameter",
				"A numerical value that defines certain characteristics of systems or system " + 
				"functions. It may be part of a calculation, but its value is not determined by " + 
		"the form of the equation itself, and may be arbitrarily assigned.");
	public static final SBOTerm sbo0000005ObsoMathExpr = 
		createTerm("0000005", NO_SYMBOL, "obsolete mathematical expression",
		"The description of a system in mathematical terms.");
	public static final SBOTerm sbo0000009KineticConst = 
		createTerm("0000009", NO_SYMBOL, "kinetic constant",
		"Numerical parameter that quantifies the velocity of a chemical reaction.");
	public static final SBOTerm sbo0000016UnimolecRateConst = 
		createTerm("0000016", NO_SYMBOL, "unimolecular rate constant",
				"Numerical parameter that quantifies the velocity of a chemical reaction involving only " + 
		"one reactant. ");
	public static final SBOTerm sbo0000022ForwUnimolecRateConst = 
		createTerm("0000022", NO_SYMBOL, "forward unimolecular rate constant",
				"Numerical parameter that quantifies the forward velocity of a chemical reaction " + 
				"involving only one reactant. This parameter encompasses all the contributions to the " + 
		"velocity except the quantity of the reactant.");
	public static final SBOTerm sbo0000025CatalRateConstant = 
		createTerm("0000025", "kcat", "catalytic rate constant",
		"Numerical parameter that quantifies the velocity of an enzymatic reaction.");
	public static final SBOTerm sbo0000027MichaelisConstant = 
		createTerm("0000027", "Km", "Michaelis constant",
				"Substrate concentration at which the velocity of reaction is half its maximum. " + 
				"Michaelis constant is an experimental parameter. According to the underlying " + 
				"molecular mechanism it can be interpreted differently in terms of microscopic " + 
		"constants.");
	public static final SBOTerm sbo0000028EnzyRateLawIrrevNomodNointUniReact = 
		createTerm("0000028", NO_SYMBOL, "enzymatic rate law for irreversible non-modulated non-interacting unireactant enzymes",
				"Substrate concentration at which the velocity of reaction is half its maximum. " + 
				"Michaelis constant is an experimental parameter. According to the underlying " + 
				"molecular mechanism it can be interpreted differently in terms of microscopic " + 
		"constants.");
	public static final SBOTerm sbo0000029HMMRateLaw = 
		createTerm("0000029", NO_SYMBOL, "Henri-Michaelis-Menten rate law",
				"First general rate equation for reactions involving " + 
				"enzymes, it was presented in \"Victor Henri. " + 
				"Lois Générales de l'Action des Diastases. " + 
				"Paris, Hermann, 1903.\". The reaction is assumed to be made " + 
				"of a reversible of the binding of the " + 
				"substrate to the enzyme, followed by the breakdown of the " + 
				"complex generating the product. Ten years " + 
				"after Henri, Michaelis and Menten presented a variant of " + 
				"his equation, based on the hypothesis that " + 
				"the dissociation rate of the substrate was much larger " + 
				"than the rate of the product generation. " + 
				"Leonor Michaelis, Maud Menten (1913). Die Kinetik der " + 
		"Invertinwirkung, Biochem. Z. 49:333-369.");
	public static final SBOTerm sbo0000030VanSlykeCullenRateLaw = 
		createTerm("0000030", NO_SYMBOL, "Van Slyke-Cullen rate law",
				"First general rate equation for reactions involving " + 
				"enzymes, it was presented in \"Victor Henri. " + 
				"Lois Générales de l'Action des Diastases. " + 
				"Paris, Hermann, 1903.\". The reaction is assumed to be made " + 
				"of a reversible of the binding of the " + 
				"substrate to the enzyme, followed by the breakdown of the " + 
				"complex generating the product. Ten years " + 
				"after Henri, Michaelis and Menten presented a variant of " + 
				"his equation, based on the hypothesis that " + 
				"the dissociation rate of the substrate was much larger " + 
				"than the rate of the product generation. " + 
				"Leonor Michaelis, Maud Menten (1913). Die Kinetik der " + 
		"Invertinwirkung, Biochem. Z. 49:333-369.");
	public static final SBOTerm sbo0000031BriggsHaldaneRateLaw = 
		createTerm("0000031", NO_SYMBOL, "Briggs-Haldane rate law",
				"The Briggs-Haldane rate law is a general rate equation that does not require " + 
				"the restriction of equilibrium of Henri-Michaelis-Menten or irreversible " + 
				"reactions of Van Slyke, but instead make the hypothesis that the complex " + 
				"enzyme-substrate is in quasi-steady-state. Although of the same form than " + 
				"the Henri-Michaelis-Menten equation, it is semantically different since Km " + 
				"now represents a pseudo-equilibrium constant, and is equal to the ratio " + 
				"between the rate of consumption of the complex (sum of dissociation of " + 
				"substrate and generation of product) and the association rate of the enzyme " + 
				"and the substrate.");
	public static final SBOTerm sbo0000035ForwUnimolecRateConstContin = 
		createTerm("0000035", NO_SYMBOL, "forward unimolecular rate constant, continuous case",
				"Numerical parameter that quantifies the forward velocity of a chemical reaction " + 
				"involving only one reactant. This parameter encompasses all the contributions to the " + 
				"velocity except the quantity of the reactant. It is to be used in a reaction modelled " + 
		"using a continuous framework.");
	public static final SBOTerm sbo0000046ZeroOrdRateConst = 
		createTerm("0000046", NO_SYMBOL, "zeroth order rate constant",
				"Numerical parameter that quantifies the velocity of a chemical reaction independant " + 
				"of the reactant quantities. This parameter encompasses all the contributions to the " + 
		"velocity.");
	public static final SBOTerm sbo0000048ForwZeroOrdRateConstContin = 
		createTerm("0000048", NO_SYMBOL, "forward zeroth order rate constant, continuous case",
				"Numerical parameter that quantifies the velocity of a chemical reaction independant " + 
				"of the reactant quantities. This parameter encompasses all the contributions to the " + 
		"velocity.");
	public static final SBOTerm sbo0000064MathExpr = 
		createTerm("0000064", NO_SYMBOL, "mathematical expression",
				"Formal representation of a calculus linking parameters " + 
		"and variables of a model.");
	public static final SBOTerm sbo0000147Temperature = 
		createTerm("0000147", "T", "thermodynamic temperature",
				"Temperature is the physical property of a system which underlies the common " + 
				"notions of \"hot\" and \"cold\"; the material with the higher temperature is said " + 
				"to be hotter. Temperature is a quantity related to the average kinetic energy of " + 
				"the particles in a substance. The 10th Conference Generale des Poids et Mesures " + 
				"decided to define the thermodynamic temperature scale by choosing the triple " + 
				"point of water as the fundamental fixed point, and assigning to it the " + 
		"temperature 273,16 degrees Kelvin, exactly (0.01 degree Celsius).");
	public static final SBOTerm sbo0000150EnzyRateLawIrrevNomodNointReact = 
		createTerm("0000150", NO_SYMBOL, 
				"enzymatic rate law for irreversible non-modulated " + 
				"non-interacting reactant enzymes",
				"Kinetics of enzymes that react with one or several " + 
				"substances, their substrates, that bind " + 
				"independently. The enzymes do not catalyse " + 
		"the reactions in both directions. ");
	public static final SBOTerm sbo0000153ForwRateConst = 
		createTerm("0000153", NO_SYMBOL, "forward rate constant",
				"Numerical parameter that quantifies the forward velocity of a chemical reaction. " + 
				"This parameter encompasses all the contributions to the velocity except the " + 
		"quantity of the reactants.");
	public static final SBOTerm sbo0000154ForwRateConstContin = 
		createTerm("0000154", NO_SYMBOL, "forward rate constant, continuous case",
				"Numerical parameter that quantifies the forward velocity of a chemical reaction. " + 
				"This parameter encompasses all the contributions to the velocity except the " + 
				"quantity of the reactants. It is to be used in a reaction modelled using a " + 
		"continuous framework.");
	public static final SBOTerm sbo0000156RevRateConst = 
		createTerm("0000156", NO_SYMBOL, "reverse rate constant",
				"Numerical parameter that quantifies the forward velocity of a chemical " + 
				"reaction. This parameter encompasses all the contributions to the velocity " + 
		"except the quantity of the reactants.");
	public static final SBOTerm sbo0000162ForwZeroOrdRateConst = 
		createTerm("0000162", NO_SYMBOL, "forward zeroth order rate constant",
				"Numerical parameter that quantifies the forward " + 
				"velocity of a chemical reaction independant of the reactant " + 
				"quantities. This parameter encompasses all the " + 
		"contributions to the velocity.");
	public static final SBOTerm sbo0000186MaximalVelocity = 
		createTerm("0000186", "Vmax", "maximal velocity",
				"Limiting maximal velocity of an enzymatic reaction, reached when the substrate " + 
		"is in large excess and all the enzyme is complexed.");
	public static final SBOTerm sbo0000187HHMEquatVMaxForm = 
		createTerm("0000187", NO_SYMBOL, "Henri-Michaelis-Menten equation, Vmax form",
				"Version of Henri-Michaelis-Menten equation where " + 
				"kp*[E]t is replaced by the maximal velocity, Vmax, " + 
		"reached when all the enzyme is active. ");
	public static final SBOTerm sbo0000193EquilOrSteadStateConst = 
		createTerm("0000193", NO_SYMBOL, "equilibrium or steady-state constant",
				"Constant with the dimension of a powered concentration. It is determined at " + 
		"half-saturation, half-activity etc.");	
	public static final SBOTerm sbo0000196ConcOfEntityPool = 
		createTerm("0000196", "[X]", "concentration of an entity pool",
		"The amount of an entity per unit of volume. ");	
	public static final SBOTerm sbo0000199NormEnzyRateLawUnireact = 
		createTerm("0000199", NO_SYMBOL, "normalised enzymatic rate law for unireactant enzymes",
		"Kinetics of enzymes that react only with one substance, their substrate. The total " + 
		"enzyme concentration is considered to be equal to 1, therefore the maximal velocity " + 
		"equals the catalytic constant.");	
	public static final SBOTerm sbo0000226DensityOfEntityPool = 
		createTerm("0000226", NO_SYMBOL, "density of an entity pool",
				"A quantitative measure of an amount or property of an entity expressed in terms of " + 
		"another dimension, such as unit length, area or volume.");	
	public static final SBOTerm sbo0000255PhysicCharact = 
		createTerm("0000255", NO_SYMBOL, "physical characteristic",
				"Parameter characterising a physical system or the " + 
		"environment, and independent of life's influence.");	
	public static final SBOTerm sbo0000261InhibitoryConstant = 
		createTerm("0000261", "Ki", "inhibitory constant",
		"Dissociation constant of a compound from a target of which it inhibits the function. ");	
	public static final SBOTerm sbo0000268EnzyRateLaw = 
		createTerm("0000268", NO_SYMBOL, "enzymatic rate law",
				"Enzyme kinetics is the study of the rates of " + 
				"chemical reactions that are catalysed by enzymes, how this " + 
				"rate is controlled, and how drugs and " + 
		"poisons can inhibit its activity.");	
	public static final SBOTerm sbo0000269EnzyRateLawUnireact = 
		createTerm("0000269", NO_SYMBOL, "enzymatic rate law for unireactant enzymes",
				"Kinetics of enzymes that catalyse the transformation " + 
		"of only one substrate. ");	
	public static final SBOTerm sbo0000281EquiliConst = 
		createTerm("0000281", "Keq", "equilibrium constant",
				"Quantity characterizing a chemical equilibrium in a chemical reaction, " + 
				"which is a useful tool to determine the concentration of various reactants or " + 
		"products in a system where chemical equilibrium occurs.");	
	public static final SBOTerm sbo0000282DissociationConstant = 
		createTerm("0000282", "Kd", "dissociation constant",
				"Equilibrium constant that measures the propensity of a larger object to separate " + 
				"(dissociate) reversibly into smaller components, as when a complex falls apart " + 
				"into its component molecules, or when a salt splits up into its component ions. " + 
				"The dissociation constant is usually denoted Kd and is the inverse of the " + 
		"affinity constant.");	
	public static final SBOTerm sbo0000302CatalyticEfficiency = 
		createTerm("0000302", "kcat/Km", "catalytic efficiency",
				"Constant representing the actual efficiency of an enzyme, taking into account " + 
				"its microscopic catalytic activity and the rates of substrate binding and " + 
		"dissociation.");
	public static final SBOTerm sbo0000304PH = createTerm("0000304", "pH", "pH",
			"Negative logarithm (base 10) of the activity of hydrogen in a solution. " + 
			"Ina diluted solution, this activity is equal to the concentration of protons " + 
			"(in fact of ions H3O+). The pH is proportional to the chemical potential of " + 
	"hydrogen, by the relation: pH = -µH ÷ 2.3RT. (with µH=-RTln[H+]).");
	public static final SBOTerm sbo0000308EquilOrSteadStatCharact = 
		createTerm("0000308", NO_SYMBOL, "equilibrium or steady-state characteristic",
		"Quantitative parameter that characterises a biochemical equilibrium.");
	public static final SBOTerm sbo0000309DissociationCharact = 
		createTerm("0000309", NO_SYMBOL, "dissociation characteristic",
		"Quantitative parameter that characterises a biochemical equilibrium.");
	public static final SBOTerm sbo0000320ProductCatalRateConst = 
		createTerm("0000320", "kcatp", "product catalytic rate constant",
				"Numerical parameter that quantifies the velocity of product creation by a " + 
		"reversible enzymatic reaction. ");
	public static final SBOTerm sbo0000321SubstracteCatalRateConst = 
		createTerm("0000321", "kcats", "substrate catalytic rate constant",
				"Numerical parameter that quantifies the velocity of product creation by a " + 
		"reversible enzymatic reaction. ");
	public static final SBOTerm sbo0000322MichaelisConstantForSubstrate = 
		createTerm("0000322", "Kms", "Michaelis constant for substrate",
				"Substrate concentration at which the velocity of product production by the " + 
		"forward activity of a reversible enzyme is half its maximum.");
	public static final SBOTerm sbo0000323MichaelisConstantForProduct = 
		createTerm("0000323", "Kmp", "Michaelis constant for product",
				"Product concentration at which the velocity of " + 
				"substrate production by the reverse activity of a reversible " + 
		"enzyme is half its maximum. ");
	public static final SBOTerm sbo0000324ForwardMaximalVelocity = 
		createTerm("0000324", "Vmaxf", "forward maximal velocity",
				"Limiting maximal velocity of the forward reaction of a reversible enzyme, " + 
		"reached when the substrate is in large excess and all the enzyme is complexed.");
	public static final SBOTerm sbo0000325ReverseMaximalVelocity = 
		createTerm("0000325", "Vmaxr", "reverse maximal velocity",
				"Limiting maximal velocity of the forward reaction of a reversible enzyme, " + 
		"reached when the substrate is in large excess and all the enzyme is complexed.");
	public static final SBOTerm sbo0000326EnzyRateLawNomodUnireact = 
		createTerm("0000326", NO_SYMBOL, "enzymatic rate law for non-modulated unireactant enzymes",
				"Kinetics of enzymes that react only with one " + 
				"substance, their substrate, and are not modulated by " + 
		"other compounds.");
	public static final SBOTerm sbo0000345Time = 
		createTerm("0000345", "t", "time",
				"Fundmental quantity of the measuring system used to sequence events, to " + 
				"compare the durations of events and the intervals between them, and to quantify " + 
				"the motions or the transformation of entities. The SI base unit for time " + 
				"is the SI second. The second is the duration of 9,192,631,770 periods of the " + 
				"radiation corresponding to the transition between the two hyperfine levels of " + 
		"the ground state of the caesium 133 atom.");
	public static final SBOTerm sbo0000350ForwReactVelocity = 
		createTerm("0000350", NO_SYMBOL, "forward reaction velocity",
				"The speed of an enzymatic reaction at a defined " + 
		"concentration of substrate(s) and enzyme.");
	public static final SBOTerm sbo0000352RevZeroOrdRateConst = 
		createTerm("0000352", NO_SYMBOL, "reverse zeroth order rate constant",
				"Numerical parameter that quantifies the reverse velocity of a chemical reaction " + 
				"independant of the reactant quantities. This parameter encompasses all the " + 
				"contributions to the velocity. It is to be used in a reaction modelled using a " + 
				"continuous framework.");
	public static final SBOTerm sbo0000353RevReactVelocity = 
		createTerm("0000353", NO_SYMBOL, "reverse reaction velocity",
				"The speed of an enzymatic reaction at a defined concentration of substrate(s) " + 
		"and enzyme. ");
	public static final SBOTerm sbo0000360QuantityOfEntityPool = 
		createTerm("0000360", NO_SYMBOL, "quantity of an entity pool",
				"The enumeration of co-localised, identical biochemical entities of a specific " + 
				"state, which constitute a pool. The form of enumeration may be purely numerical, " + 
		"or may be given in relation to another dimension such as length or volume.");
	public static final SBOTerm sbo0000370MichaelisConstNonEquilib = 
		createTerm("0000370", "Km", "Michaelis constant in non-equilibrium situation",
				"Michaelis constant derived or experimentally measured " + 
		"under non-equilibrium conditions.");
	public static final SBOTerm sbo0000371MichaelisConstQuasiSteadyState = 
		createTerm("0000371", "Km", "Michaelis constant in quasi-steady state situation",
				"Michaelis constant derived using a steady-state " + 
				"assumption for enzyme-substrate and enzyme-product " + 
				"intermediates. For example see " + 
		"Briggs-Haldane equation (SBO:0000031).");
	public static final SBOTerm sbo0000372MichaelisConstIrrev = 
		createTerm("0000372", "Km", "Michaelis constant in irreversible situation",
				"Michaelis constant derived assuming enzyme-substrate " + 
				"and enzyme-product intermediates are formed in " + 
				"consecutive irreversible reactions. " + 
				"The constant K is the ratio of the forward rate constants. " + 
				"For example see Van Slyke-Cullen equation " + 
		"(SBO:0000030).");
	public static final SBOTerm sbo0000373MichaelisConstFastEquilib = 
		createTerm("0000373", "Km", "Michaelis constant in fast equilibrium situation",
				"Michaelis constant as determined in a reaction where " + 
				"the formation of the enzyme-substrate complex occurs at a " + 
				"much faster rate than subsequent steps, " + 
				"and so are assumed to be in a quasi-equilibrium situation. " + 
				"K is equivalent to an equilibrium constant. " + 
		"For example see Henri-Michaelis-Menten equation (SBO:0000029).");
	public static final SBOTerm sbo0000545SystDescParam = 
		createTerm("0000545", "?", "systems description parameter",
				"A value, numerical or symbolic, that defines certain characteristics of systems " + 
		"or system functions, or is necessary in their derivation.");

	static {
		SBOUtil.setChild(sbo0000000SystBiolRepresentation, sbo0000545SystDescParam);
		SBOUtil.setChild(sbo0000000SystBiolRepresentation, sbo0000064MathExpr);
		SBOUtil.setChild(sbo0000001RateLaw, sbo0000028EnzyRateLawIrrevNomodNointUniReact);
		SBOUtil.setChild(sbo0000002QuanSystDescParam, sbo0000009KineticConst);
		SBOUtil.setChild(sbo0000002QuanSystDescParam, sbo0000147Temperature);
		SBOUtil.setChild(sbo0000002QuanSystDescParam, sbo0000255PhysicCharact);
		SBOUtil.setChild(sbo0000002QuanSystDescParam, sbo0000308EquilOrSteadStatCharact);
		SBOUtil.setChild(sbo0000002QuanSystDescParam, sbo0000360QuantityOfEntityPool);
		SBOUtil.setChild(sbo0000005ObsoMathExpr, sbo0000187HHMEquatVMaxForm);
		SBOUtil.setChild(sbo0000009KineticConst, sbo0000016UnimolecRateConst);
		SBOUtil.setChild(sbo0000009KineticConst, sbo0000046ZeroOrdRateConst);
		SBOUtil.setChild(sbo0000009KineticConst, sbo0000153ForwRateConst);
		SBOUtil.setChild(sbo0000009KineticConst, sbo0000156RevRateConst);
		SBOUtil.setChild(sbo0000016UnimolecRateConst, sbo0000022ForwUnimolecRateConst);
		SBOUtil.setChild(sbo0000022ForwUnimolecRateConst, sbo0000035ForwUnimolecRateConstContin);
		SBOUtil.setChild(sbo0000025CatalRateConstant, sbo0000320ProductCatalRateConst);
		SBOUtil.setChild(sbo0000025CatalRateConstant, sbo0000321SubstracteCatalRateConst);
		SBOUtil.setChild(sbo0000027MichaelisConstant, sbo0000322MichaelisConstantForSubstrate);
		SBOUtil.setChild(sbo0000027MichaelisConstant, sbo0000323MichaelisConstantForProduct);
		SBOUtil.setChild(sbo0000027MichaelisConstant, sbo0000370MichaelisConstNonEquilib);
		SBOUtil.setChild(sbo0000027MichaelisConstant, sbo0000373MichaelisConstFastEquilib);
		SBOUtil.setChild(sbo0000028EnzyRateLawIrrevNomodNointUniReact, sbo0000029HMMRateLaw);
		SBOUtil.setChild(sbo0000028EnzyRateLawIrrevNomodNointUniReact, sbo0000030VanSlykeCullenRateLaw);
		SBOUtil.setChild(sbo0000028EnzyRateLawIrrevNomodNointUniReact, sbo0000031BriggsHaldaneRateLaw);
		SBOUtil.setChild(sbo0000028EnzyRateLawIrrevNomodNointUniReact, sbo0000199NormEnzyRateLawUnireact);
		SBOUtil.setChild(sbo0000035ForwUnimolecRateConstContin, sbo0000025CatalRateConstant);
		SBOUtil.setChild(sbo0000046ZeroOrdRateConst, sbo0000186MaximalVelocity);
		SBOUtil.setChild(sbo0000046ZeroOrdRateConst, sbo0000162ForwZeroOrdRateConst);
		SBOUtil.setChild(sbo0000046ZeroOrdRateConst, sbo0000352RevZeroOrdRateConst);
		SBOUtil.setChild(sbo0000048ForwZeroOrdRateConstContin, sbo0000350ForwReactVelocity);
		SBOUtil.setChild(sbo0000064MathExpr, sbo0000001RateLaw);
		SBOUtil.setChild(sbo0000064MathExpr, sbo0000005ObsoMathExpr);
		SBOUtil.setChild(sbo0000153ForwRateConst, sbo0000022ForwUnimolecRateConst);
		SBOUtil.setChild(sbo0000153ForwRateConst, sbo0000154ForwRateConstContin);
		SBOUtil.setChild(sbo0000153ForwRateConst, sbo0000162ForwZeroOrdRateConst);
		SBOUtil.setChild(sbo0000154ForwRateConstContin, sbo0000035ForwUnimolecRateConstContin);
		SBOUtil.setChild(sbo0000154ForwRateConstContin, sbo0000048ForwZeroOrdRateConstContin);
		SBOUtil.setChild(sbo0000156RevRateConst, sbo0000352RevZeroOrdRateConst);
		SBOUtil.setChild(sbo0000162ForwZeroOrdRateConst, sbo0000048ForwZeroOrdRateConstContin);
		SBOUtil.setChild(sbo0000186MaximalVelocity, sbo0000324ForwardMaximalVelocity);
		SBOUtil.setChild(sbo0000186MaximalVelocity, sbo0000325ReverseMaximalVelocity);
		SBOUtil.setChild(sbo0000193EquilOrSteadStateConst, sbo0000027MichaelisConstant);
		SBOUtil.setChild(sbo0000193EquilOrSteadStateConst, sbo0000281EquiliConst);
		SBOUtil.setChild(sbo0000226DensityOfEntityPool, sbo0000196ConcOfEntityPool);
		SBOUtil.setChild(sbo0000255PhysicCharact, sbo0000345Time);
		SBOUtil.setChild(sbo0000268EnzyRateLaw, sbo0000150EnzyRateLawIrrevNomodNointReact);
		SBOUtil.setChild(sbo0000268EnzyRateLaw, sbo0000269EnzyRateLawUnireact);
		SBOUtil.setChild(sbo0000269EnzyRateLawUnireact, sbo0000326EnzyRateLawNomodUnireact);
		SBOUtil.setChild(sbo0000281EquiliConst, sbo0000282DissociationConstant);
		SBOUtil.setChild(sbo0000282DissociationConstant, sbo0000261InhibitoryConstant);
		SBOUtil.setChild(sbo0000308EquilOrSteadStatCharact, sbo0000193EquilOrSteadStateConst);
		SBOUtil.setChild(sbo0000308EquilOrSteadStatCharact, sbo0000309DissociationCharact);
		SBOUtil.setChild(sbo0000326EnzyRateLawNomodUnireact, sbo0000028EnzyRateLawIrrevNomodNointUniReact);
		SBOUtil.setChild(sbo0000350ForwReactVelocity, sbo0000324ForwardMaximalVelocity);
		SBOUtil.setChild(sbo0000352RevZeroOrdRateConst, sbo0000353RevReactVelocity);
		SBOUtil.setChild(sbo0000353RevReactVelocity, sbo0000325ReverseMaximalVelocity);
		SBOUtil.setChild(sbo0000360QuantityOfEntityPool, sbo0000226DensityOfEntityPool);
		SBOUtil.setChild(sbo0000370MichaelisConstNonEquilib, sbo0000371MichaelisConstQuasiSteadyState);		
		SBOUtil.setChild(sbo0000370MichaelisConstNonEquilib, sbo0000372MichaelisConstIrrev);		
		SBOUtil.setChild(sbo0000545SystDescParam, sbo0000002QuanSystDescParam);
	}


	public static final SBOSet HMM_RATE_LAW_IRREV = 
		new SBOBranch(sbo0000028EnzyRateLawIrrevNomodNointUniReact);
	public static final SBOSet MICHAELIS_CONST = new SBOBranch(sbo0000027MichaelisConstant);
	public static final SBOSet MICHAELIS_CONST_REV = 
		new SBOBranch(sbo0000323MichaelisConstantForProduct);
	public static final SBOSet MICHAELIS_CONST_FORW = 
		new SBOSetDiff(MICHAELIS_CONST, MICHAELIS_CONST_REV);
	public static final SBOSet CATALYTIC_RATE_CONST = new SBOBranch(sbo0000025CatalRateConstant);
	public static final SBOSet CATALYTIC_RATE_CONST_REV = 
		new SBOBranch(sbo0000321SubstracteCatalRateConst);
	public static final SBOSet CATALYTIC_RATE_CONST_FORW = 
		new SBOSetDiff(CATALYTIC_RATE_CONST, CATALYTIC_RATE_CONST_REV);
	public static final SBOSet MAXIMAL_VELOCITY = new SBOBranch(sbo0000186MaximalVelocity);
	public static final SBOSet MAXIMAL_VELOCITY_REV = 
		new SBOBranch(sbo0000325ReverseMaximalVelocity);
	public static final SBOSet MAXIMAL_VELOCITY_FORW = 
		new SBOSetDiff(MAXIMAL_VELOCITY, MAXIMAL_VELOCITY_REV);
	public static final SBOSet REVERSE_RATE = new SBOBranch(sbo0000156RevRateConst);
	public static final SBOSet REVERSE_PARAM = 
		new SBOSetUnion(REVERSE_RATE, MICHAELIS_CONST_REV, CATALYTIC_RATE_CONST_REV);
	
}
