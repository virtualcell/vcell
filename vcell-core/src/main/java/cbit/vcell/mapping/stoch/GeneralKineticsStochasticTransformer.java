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

import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

import static cbit.vcell.mapping.stoch.StochasticTransformer.StochasticTransformException;

class GeneralKineticsStochasticTransformer {
	private final static Logger lg = LogManager.getLogger(GeneralKineticsStochasticTransformer.class);


	static StochasticFunction solveGeneralKineticsStochasticFunction(ReactionStep rs) throws StochasticTransformException {
		if(rs.getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL || rs.getPhysicsOptions() == ReactionStep.PHYSICS_ELECTRICAL_ONLY)
		{
			throw new StochasticTransformException(rs, StochasticTransformer.StochasticTransformErrorType.ELECTRICAL_CURRENT_NOT_SUPPORTED);
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
		try {
			Expression forwardNetRate = Expression.max(netRate, new Expression(0.0));

			if (rs.isReversible()) {
				Expression reverseNetRate = Expression.max(Expression.negate(netRate), new Expression(0.0));
				return new GeneralKineticsStochasticFunction(forwardNetRate, reverseNetRate, reactants, products);
			} else {
				return new GeneralKineticsStochasticFunction(forwardNetRate, null, reactants, products);
			}
		} catch (ExpressionException e) {
			throw new StochasticTransformException(rs, e);
		}
	}
	
}
