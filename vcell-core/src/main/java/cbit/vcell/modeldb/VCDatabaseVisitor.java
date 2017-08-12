/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;

import java.io.PrintStream;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mathmodel.MathModel;

public interface VCDatabaseVisitor {
	
	public boolean filterBioModel(BioModelInfo bioModelInfo);
	public void visitBioModel(BioModel bioModel, PrintStream logFilePrintStream);

	public boolean filterGeometry(GeometryInfo geometryInfo);
	public void visitGeometry(Geometry geometry, PrintStream logFilePrintStream);

	public boolean filterMathModel(MathModelInfo mathModelInfo);
	public void visitMathModel(MathModel mathModel, PrintStream logFilePrintStream);
	
//	public boolean filterSimulation(SimulationInfo simInfo);
//	public void visitSimulation(Simulation simulation, PrintStream logFilePrintStream);
	
//	public boolean filterSimulationContext(SimulationContextInfo simContextInfo, String simContextName);
//	public void visitSimulationContext(SimulationContext simContext, PrintStream logFilePrintStream);

}
