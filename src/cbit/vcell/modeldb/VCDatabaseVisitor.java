package cbit.vcell.modeldb;

import java.io.PrintStream;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.SimulationContextInfo;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;

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
