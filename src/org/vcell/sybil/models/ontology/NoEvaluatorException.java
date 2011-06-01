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
package org.vcell.sybil.models.ontology;

public class NoEvaluatorException extends Exception {
	private static final long serialVersionUID = 2726076296973459313L;
	public NoEvaluatorException() { super("No evaluator present"); }
}
