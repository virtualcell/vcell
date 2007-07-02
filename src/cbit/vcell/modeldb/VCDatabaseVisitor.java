package cbit.vcell.modeldb;

import java.io.PrintStream;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;

public interface VCDatabaseVisitor {
	
	public boolean filterBioModel(BioModelInfo bioModelInfo);
//	public boolean filterMathModel(MathModelInfo mathModelInfo);
//	public boolean filterGeometry(GeometryInfo bioModelInfo);
//	public boolean filterSimulation(SimulationInfo simInfo);
//	public boolean filterSimulationContext(BioModelInfo bioModelInfo, String simContextName);
	
	public void visitBioModel(BioModel bioModel, PrintStream arg_p);
//	public void visitMathModel(MathModel mathModel);
//	public void visitGeometry(Geometry geometry);
//	public void visitSimulation(Simulation simulation);
//	public void visitSimulationContext(BioModel bioModel, String simContextName);

}
