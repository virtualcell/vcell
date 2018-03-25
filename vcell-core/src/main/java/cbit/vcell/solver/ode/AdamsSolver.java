/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode;

import java.io.File;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.SolverException;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:55:35 PM)
 * @author: John Wagner
 */
public abstract class AdamsSolver extends DefaultODESolver {
	int fieldWorkArrayCount;
	double[][] f;
/**
 * AdamsIntegrator constructor comment.
 * @param mathDescription cbit.vcell.math.MathDescription
 * @param sessionLog cbit.vcell.server.SessionLog
 * @param valueVectorCount int
 * @param temporaryVectorCount int
 */
public AdamsSolver(SimulationTask simTask, File directory, int valueVectorCount, int workArrayCount)  throws SolverException {
	super(simTask, directory, valueVectorCount);
	fieldWorkArrayCount = workArrayCount;
}
protected void initialize() throws cbit.vcell.solver.SolverException {
	super.initialize();
	f = new double[fieldWorkArrayCount][];
	for (int i = 0; i < fieldWorkArrayCount; i++) f[i] = createWorkArray();
}
protected void shiftWorkArrays() {
	double[] t = f[0];
	for (int i = 0; i < f.length - 1; i++) f[i] = f[i+1];
	f[f.length-1] = t;
}
}
