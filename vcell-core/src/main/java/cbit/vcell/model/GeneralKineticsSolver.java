/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;

import cbit.vcell.matrix.MatrixException;
import cbit.vcell.matrix.RationalExp;
import cbit.vcell.matrix.RationalExpMatrix;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.parser.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.*;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GeneralKineticsSolver {
	private final static Logger lg = LogManager.getLogger(GeneralKineticsSolver.class);


	public static class GeneralKineticsStochasticFunction
	{
		private Expression fRate = null;
		private Expression rRate = null;
		private List<ReactionParticipant> reactants = null;
		private List<ReactionParticipant> products = null;
		
		public GeneralKineticsStochasticFunction(Expression forwardRate, Expression reverseRate, List<ReactionParticipant> reactants, List<ReactionParticipant> products)
		{
			this.fRate = forwardRate;
			this.rRate = reverseRate;
			this.reactants = reactants;
			this.products = products;
		}
		public Expression getForwardRate() {
			return fRate;
		}
		public Expression getReverseRate() {
			return rRate;
		}
		public List<ReactionParticipant> getReactants() {
			return reactants;
		}
		public List<ReactionParticipant> getProducts() {
			return products;
		}
	}
	
	public static GeneralKineticsStochasticFunction solveGeneralKineticsStochasticFunction(ReactionStep rs) throws ExpressionException, ModelException, DivideByZeroException{
		if(rs.getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL || rs.getPhysicsOptions() == ReactionStep.PHYSICS_ELECTRICAL_ONLY)
		{
			throw new ModelException("Kinetics of reaction " + rs.getName() + " has membrane current. It can not be automatically transformed to a stochastic reaction.");
		}

		ArrayList<ReactionParticipant> reactants = new ArrayList<ReactionParticipant>();
		ArrayList<ReactionParticipant> products = new ArrayList<ReactionParticipant>();
		for (var rp : rs.getReactionParticipants()) {
			if (rp instanceof Reactant) {
				reactants.add(rp);
			} else if (rp instanceof Product) {
				products.add(rp);
			}
		}

		Expression netRate = rs.getKinetics().getAuthoritativeParameter().getExpression();
		Expression forwardRate = Expression.max(new Expression(0.0), netRate);
		Expression reverseRate = Expression.max(new Expression(0.0), Expression.negate(netRate));
		return new GeneralKineticsStochasticFunction(forwardRate, reverseRate, reactants, products);
	}
	
}
