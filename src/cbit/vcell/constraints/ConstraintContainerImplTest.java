/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.constraints;
import cbit.vcell.parser.Expression;
import net.sourceforge.interval.ia_math.RealInterval;
import cbit.util.graph.*;
/**
 * Insert the type's description here.
 * Creation date: (6/26/01 8:25:12 AM)
 * @author: Jim Schaff
 */
public class ConstraintContainerImplTest {
/**
 * Insert the method's description here.
 * Creation date: (7/8/2003 8:17:52 AM)
 * @return cbit.vcell.mapping.potential.Graph
 * @param constraintContainer cbit.vcell.constraints.ConstraintContainerImpl
 */
public static Graph getConstraintGraph(ConstraintContainerImpl constraintContainer) {
	Graph constraintGraph = new Graph();
	//
	// add GeneralConstraints to constraint graph
	//
	GeneralConstraint generalConstraints[] = constraintContainer.getGeneralConstraints();
	for (int i = 0; i < generalConstraints.length; i++){
		//
		// add constraint as node
		//
		Node constraintNode = new Node(generalConstraints[i].getExpression().infix(),generalConstraints[i]);
		constraintGraph.addNode(constraintNode);
		//
		// add variable(s) as nodes and connect to constraint with a new edge
		//
		String symbols[] = generalConstraints[i].getExpression().getSymbols();
		for (int j = 0; j < symbols.length; j++){
			Node varNode = constraintGraph.getNode(symbols[j]);
			if (varNode == null){
				varNode = new Node(symbols[j]);
				constraintGraph.addNode(varNode);
			}
			constraintGraph.addEdge(new Edge(varNode,constraintNode));
		}
	}
	//
	// add SimpleBounds to constraint graph
	//
	SimpleBounds simpleBounds[] = constraintContainer.getSimpleBounds();
	for (int i = 0; i < simpleBounds.length; i++){
		//
		// add constraint as node
		//
		Node constraintNode = new Node(simpleBounds[i].getIdentifier()+"==["+simpleBounds[i].getBounds().lo()+","+simpleBounds[i].getBounds().hi()+"]",simpleBounds[i]);
		constraintGraph.addNode(constraintNode);
		//
		// add variable as node and connect to constraint with a new edge
		//
		Node varNode = constraintGraph.getNode(simpleBounds[i].getIdentifier());
		if (varNode == null){
			varNode = new Node(simpleBounds[i].getIdentifier());
			constraintGraph.addNode(varNode);
		}
		constraintGraph.addEdge(new Edge(varNode,constraintNode));
	}
	return constraintGraph;
}


/**
 * Insert the method's description here.
 * Creation date: (6/26/01 8:25:55 AM)
 * @return cbit.vcell.constraints.ConstraintContainerImpl
 */
public static ConstraintContainerImpl getExample() {
	try {
		ConstraintContainerImpl ccImpl = new ConstraintContainerImpl();
		ccImpl.addSimpleBound(new SimpleBounds("a",new RealInterval(5,2000),AbstractConstraint.OBSERVED_CONSTRAINT,"no comment"));
		ccImpl.addSimpleBound(new SimpleBounds("b",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"no comment"));
		ccImpl.addSimpleBound(new SimpleBounds("c",new RealInterval(0,200),AbstractConstraint.OBSERVED_CONSTRAINT,"no comment"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("a+b<c"),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("a/b==25"),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("a+c==15"),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("a==K"),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		return ccImpl;
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		return null;
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/26/01 8:25:55 AM)
 * @return cbit.vcell.constraints.ConstraintContainerImpl
 */
public static ConstraintContainerImpl getMichaelisMentenExample() {
	try {
		ConstraintContainerImpl ccImpl = new ConstraintContainerImpl();

		//
		// Model Assumptions (from paper)
		//
		// 3)
		//
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("Ks==km1/k1"),AbstractConstraint.MODELING_ASSUMPTION,"definition"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("Vmax==kp*Etotal"),AbstractConstraint.MODELING_ASSUMPTION,"definition"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("Etotal==E+ES"),AbstractConstraint.MODELING_ASSUMPTION,"definition"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("Ks==E*S/ES"),AbstractConstraint.MODELING_ASSUMPTION,"definition"));

		//
		// 4)  physical limit on model parameters and concentrations
		//
		double HYDROGEN_RADIUS = 0.0001; // 1 angstrom (in microns)
		double MAX_RADIUS = 1;           // 1 micron
		double ABSOLUTE_TEMPERATURE = 293;
//		double BOLTZMANN_CONSTANT = 1.380e-16;  // 1.380e-16 [g cm^2 s^-2 deg^-1 molecule^-1];
		double BOLTZMANN_CONSTANT = 1.380e-8;  // 1.380e-16 [g um^2 s^-2 deg^-1 molecule^-1];
		double VISCOSITY_DILUTE_AQUEOUS = 0.01;  // 1e-2 [g s^-1 cm^-1]
		double PI = 3.1415926535897932384626433832795;
		ccImpl.addSimpleBound(new SimpleBounds("PI",new RealInterval(PI),AbstractConstraint.PHYSICAL_LIMIT,"definition"));
		ccImpl.addSimpleBound(new SimpleBounds("K_Boltzmann",new RealInterval(BOLTZMANN_CONSTANT),AbstractConstraint.PHYSICAL_LIMIT,"definition"));
		ccImpl.addSimpleBound(new SimpleBounds("Temperature",new RealInterval(ABSOLUTE_TEMPERATURE),AbstractConstraint.PHYSICAL_LIMIT,"constant temperature"));
		ccImpl.addSimpleBound(new SimpleBounds("VISCOSITY",new RealInterval(VISCOSITY_DILUTE_AQUEOUS,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"viscosity > water"));
		ccImpl.addSimpleBound(new SimpleBounds("VISCOSITY",new RealInterval(2*VISCOSITY_DILUTE_AQUEOUS,3*VISCOSITY_DILUTE_AQUEOUS),AbstractConstraint.MODELING_ASSUMPTION,"viscosity 2-3 times that of water"));

			
		ccImpl.addSimpleBound(new SimpleBounds("Ks",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative parameter"));
		ccImpl.addSimpleBound(new SimpleBounds("Vmax",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative parameter"));
		ccImpl.addSimpleBound(new SimpleBounds("E",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative parameter"));
		ccImpl.addSimpleBound(new SimpleBounds("S",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative parameter"));
		ccImpl.addSimpleBound(new SimpleBounds("ES",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative parameter"));
		ccImpl.addSimpleBound(new SimpleBounds("k1",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative parameter"));
		ccImpl.addSimpleBound(new SimpleBounds("km1",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative parameter"));
		ccImpl.addSimpleBound(new SimpleBounds("kp",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative parameter"));
		ccImpl.addSimpleBound(new SimpleBounds("TAUes",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative parameter"));
		ccImpl.addSimpleBound(new SimpleBounds("TAUp",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative parameter"));
//		ccImpl.addSimpleBound(new SimpleBounds("P",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative parameter"));



		//
		// Diffusion Relationships
		//
		ccImpl.addSimpleBound(new SimpleBounds("Ds",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative diffusion"));
		ccImpl.addSimpleBound(new SimpleBounds("De",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative diffusion"));
//		ccImpl.addSimpleBound(new SimpleBounds("Dp",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative diffusion"));
		ccImpl.addSimpleBound(new SimpleBounds("Re",new RealInterval(HYDROGEN_RADIUS,MAX_RADIUS),AbstractConstraint.PHYSICAL_LIMIT,"bigger than a proton"));
		ccImpl.addSimpleBound(new SimpleBounds("Rs",new RealInterval(HYDROGEN_RADIUS,MAX_RADIUS),AbstractConstraint.PHYSICAL_LIMIT,"bigger than a proton"));
//		ccImpl.addSimpleBound(new SimpleBounds("Rp",new RealInterval(HYDROGEN_RADIUS,MAX_RADIUS),AbstractConstraint.PHYSICAL_LIMIT,"bigger than a proton"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("Ds==K_Boltzmann*Temperature/FRICTIONs"),AbstractConstraint.PHYSICAL_LIMIT,"Einstein approximation???"));
		ccImpl.addSimpleBound(new SimpleBounds("FRICTIONs",new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative viscous friction"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("FRICTIONs==6*PI*VISCOSITY*Rs"),AbstractConstraint.PHYSICAL_LIMIT,"Einstein approximation???"));
		
		//
		// 5) k1 < diffusion limited rate
		//
		//        4*pi*Res*(De+Ds)*No   
		//  k1 < ---------------------  [1/(M s)]    where Res [cm] and De,Ds [cm^2/sec], No = Avagadro's number
		//              1e3
		//
		// with substitution of constants and change of units
		//
		//        4*pi*(Res/1e4)*((De/1e8)+(Ds/1e8))*6.02e23   
		//  k1 < --------------------------------------------  [1/(uM s)]    where Res [um] and De,Ds [um^2/sec]  
		//              1e3 * 1e6
		//
		// simplify
		//
		//           
		//  k1 < 4*pi*602*Res*(De+Ds)  [1/(uM s)]    where Res [um] and De,Ds [um^2/sec]
		//
		//  k1 < 7564.9551098*(Re+Rs)*(De+Ds)  [1/(uM s)]
		//              
		//
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("k1<4*PI*602*(Re+Rs)*(De+Ds)"),AbstractConstraint.PHYSICAL_LIMIT,"diffusion limited rate"));
		
		//
		// 6) Modeling Assumption
		//
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("TAUes==1/(km1+k1*Etotal)"),AbstractConstraint.MODELING_ASSUMPTION,"time of enzyme + substrate ==> complex"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("TAUp==1/(kp)"),AbstractConstraint.MODELING_ASSUMPTION,"time of complex ==> product"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("TAUp>100*TAUes"),AbstractConstraint.MODELING_ASSUMPTION,"complex formation much faster than product"));
		
		//
		// 7) Modeling Assumption
		//
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("TAUes<TAUfast"),AbstractConstraint.MODELING_ASSUMPTION,"complex formation faster than other fast processes"));


		//
		// 8) experimental observations
		//
		ccImpl.addSimpleBound(new SimpleBounds("Ks",new RealInterval(0.55,1.05),AbstractConstraint.OBSERVED_CONSTRAINT,"in pH=7, Watras unpublished"));

		//
		// 9) experimental observations
		//
		ccImpl.addSimpleBound(new SimpleBounds("Vmax",new RealInterval(1.5,2.5),AbstractConstraint.OBSERVED_CONSTRAINT,"in pH=7, PubmedID 2939484"));

		
		ccImpl.addSimpleBound(new SimpleBounds("Etotal",new RealInterval(0.1,1),AbstractConstraint.OBSERVED_CONSTRAINT,"in pH=7, Mass Spec, Han"));
		ccImpl.addSimpleBound(new SimpleBounds("S",new RealInterval(0.1,1.0),AbstractConstraint.OBSERVED_CONSTRAINT,"in pH=7, invitro, Watras unpublished"));
		ccImpl.addSimpleBound(new SimpleBounds("De",new RealInterval(0.5,1.5),AbstractConstraint.OBSERVED_CONSTRAINT,"in vitro FCS, Carson unpublished"));
		ccImpl.addSimpleBound(new SimpleBounds("Ds",new RealInterval(0.5,1.5),AbstractConstraint.OBSERVED_CONSTRAINT,"in vitro FCS, Carson unpublished"));
		ccImpl.addSimpleBound(new SimpleBounds("Res",new RealInterval(0.005,0.0055),AbstractConstraint.OBSERVED_CONSTRAINT,"radius of enzyme-substrate complex"));

		
		return ccImpl;
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		return null;
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/26/01 8:25:55 AM)
 * @return cbit.vcell.constraints.ConstraintContainerImpl
 */
public static ConstraintContainerImpl getProteomicsExample() {
	try {
		ConstraintContainerImpl ccImpl = new ConstraintContainerImpl();

		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("exp.ratio == exp.ic_final / exp.ic_init"),AbstractConstraint.MODELING_ASSUMPTION,"definition of relative change"));
		//ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("e.ratio2 == e.A2_ic / e.A0_ic"),AbstractConstraint.MODELING_ASSUMPTION,"definition of relative change"));
		//ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("e.ratio3 == e.A3_ic / e.A0_ic"),AbstractConstraint.MODELING_ASSUMPTION,"definition of relative change"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("exp.ratio == sim.P_final / sim.P_init"),AbstractConstraint.MODELING_ASSUMPTION,"mapping exp to sim"));
		//ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("e.ratio2 == s.A2 / s.A0"),AbstractConstraint.MODELING_ASSUMPTION,"mapping exp to sim"));
		//ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("e.ratio3 == s.A3 / s.A0"),AbstractConstraint.MODELING_ASSUMPTION,"mapping exp to sim"));

		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("exp.P_init == exp.ic_init * exp.K_mpc"),AbstractConstraint.MODELING_ASSUMPTION,"molecules per cell"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("exp.P_final == exp.ic_final * exp.K_mpc"),AbstractConstraint.MODELING_ASSUMPTION,"molecules per cell"));
		//ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("e.A2_mpc == e.A2_ic * K_ic_to_mpc"),AbstractConstraint.MODELING_ASSUMPTION,"molecules per cell"));
		//ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("e.A3_mpc == e.A3_ic * K_ic_to_mpc"),AbstractConstraint.MODELING_ASSUMPTION,"molecules per cell"));
				//ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("e.internalControl_ic = ionEfficiency * internalControl_mass == ee.A3_ic * K_ic_to_mpc"),AbstractConstraint.MODELING_ASSUMPTION,"mapping"));

		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("exp.K_mpc == exp.ionEff / exp.nCells"),AbstractConstraint.MODELING_ASSUMPTION,"molecules / cell / ion"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("exp.nCells == exp.fractMass/exp.yield"),AbstractConstraint.MODELING_ASSUMPTION,"num cells in fraction"));
		
				//ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("numCells == fractionMass/fractionYield"),AbstractConstraint.MODELING_ASSUMPTION,"mapping"));

		ccImpl.addSimpleBound(new SimpleBounds("exp.ionEff",new RealInterval(0.001,0.01),AbstractConstraint.OBSERVED_CONSTRAINT,"ions per protein"));
		ccImpl.addSimpleBound(new SimpleBounds("exp.yield",new RealInterval(100),AbstractConstraint.OBSERVED_CONSTRAINT,"micrograms per 1e6 cells"));
		ccImpl.addSimpleBound(new SimpleBounds("exp.fractMass",new RealInterval(5),AbstractConstraint.OBSERVED_CONSTRAINT,"micrograms"));

		ccImpl.addSimpleBound(new SimpleBounds("exp.ic_init",new RealInterval(1.0e8,1.1e8),AbstractConstraint.OBSERVED_CONSTRAINT,"initial protein ion count"));
		ccImpl.addSimpleBound(new SimpleBounds("exp.ic_final",new RealInterval(0.7e8,0.82e8),AbstractConstraint.OBSERVED_CONSTRAINT,"final protein ion count"));
		//ccImpl.addSimpleBound(new SimpleBounds("e.A2_ic",new RealInterval(0.65e8,0.72e8),AbstractConstraint.OBSERVED_CONSTRAINT,"measured ion counts"));
		//ccImpl.addSimpleBound(new SimpleBounds("e.A3_ic",new RealInterval(1.2e8,1.31e8),AbstractConstraint.OBSERVED_CONSTRAINT,"measured ion counts"));

		ccImpl.addSimpleBound(new SimpleBounds("sim.P_init",new RealInterval(10),AbstractConstraint.MODELING_ASSUMPTION,"model initial concentration"));
				//ccImpl.addSimpleBound(new SimpleBounds("e.internalControl_ic",new RealInterval(1e8),AbstractConstraint.MODELING_ASSUMPTION,"measured ion counts"));
				//ccImpl.addSimpleBound(new SimpleBounds("e.internalControl_amount",new RealInterval(10),AbstractConstraint.MODELING_ASSUMPTION,"measured ion counts"));


		
		return ccImpl;
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		return null;
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/26/01 8:25:55 AM)
 * @return cbit.vcell.constraints.ConstraintContainerImpl
 */
public static ConstraintContainerImpl getTaubinExample() {
	try {
		ConstraintContainerImpl ccImpl = new ConstraintContainerImpl();
		final double e = 1e-12;
		ccImpl.addSimpleBound(new SimpleBounds("Kpb",new RealInterval(e,2.0-e),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addSimpleBound(new SimpleBounds("Ksb",new RealInterval(e,2.0-e),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addSimpleBound(new SimpleBounds("Dpb",new RealInterval(e,Double.POSITIVE_INFINITY),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addSimpleBound(new SimpleBounds("Dsb",new RealInterval(e,1.0-e),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addSimpleBound(new SimpleBounds("Kpb",new RealInterval(0.3),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addSimpleBound(new SimpleBounds("Ksb",new RealInterval(1.1),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addSimpleBound(new SimpleBounds("Dpb",new RealInterval(0.05),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addSimpleBound(new SimpleBounds("Dsb",new RealInterval(0.05),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addSimpleBound(new SimpleBounds("lambda",new RealInterval(e,Double.POSITIVE_INFINITY),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addSimpleBound(new SimpleBounds("mu",new RealInterval(Double.NEGATIVE_INFINITY,-e),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addSimpleBound(new SimpleBounds("N",new RealInterval(3.0,3.0),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addSimpleBound(new SimpleBounds("NN",new RealInterval(1.0/3.0,1.0/3.0),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("Kpb<Ksb"),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("lambda<-mu"),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("lambda<1/Ksb"),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("1/lambda + 1/mu == kpb"),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("lambda == 1 / (Kpb - 1.0/mu)"),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("mu == 1 / (Kpb - 1.0/lambda)"),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("A == (pow(Dsb,NN) - 1)/Ksb"),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("lambda>(A+mu)/(mu*Ksb-1)"),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("mu>(A+lambda)/(lambda*Ksb-1)"),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("pow((1-Ksb)*(1-mu*Ksb),N)<Dsb"),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression("pow(pow(lambda-mu,2)/(-4*lambda*mu),N)<1+Dpb"),AbstractConstraint.MODELING_ASSUMPTION,"no comment"));
		return ccImpl;
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		return null;
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		return null;
	}
}
}
