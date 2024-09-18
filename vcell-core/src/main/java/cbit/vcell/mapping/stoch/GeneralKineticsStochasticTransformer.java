/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.stoch;

import cbit.vcell.model.*;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

class GeneralKineticsStochasticTransformer {
	private final static Logger lg = LogManager.getLogger(GeneralKineticsStochasticTransformer.class);


	static StochasticFunction solveGeneralKineticsStochasticFunction(ReactionStep rs) throws ExpressionException, ModelException, DivideByZeroException{
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
		return new StochasticFunction(false, forwardRate, reverseRate, reactants, products);
	}
	
}
