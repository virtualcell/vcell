/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.graph.manipulator.categorizer;

/*   EvaluatorManipulator  --- by Oliver Ruebenacker, UCHC --- January 2008
 *   Generic interface for object executing graph manipulations based on a manipulator
 */

import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.manipulator.GraphManipulationException;
import org.vcell.sybil.models.graph.manipulator.GraphManipulator;
import org.vcell.sybil.models.ontology.Evaluator;

public abstract class EvaluatorGraphManipulator<S extends UIShape<S>, G extends UIGraph<S, G>> 
implements GraphManipulator<S, G> {

	protected Evaluator evaluator;
	
	public EvaluatorGraphManipulator() {};
	public EvaluatorGraphManipulator(Evaluator evaluator) { this.evaluator = evaluator; }
	
	public void setEvaluator(Evaluator evaluator) { this.evaluator = evaluator; }
	public Evaluator evaluator() { return evaluator; }
	
	public abstract void applyToGraph(G graph) throws GraphManipulationException;
}
