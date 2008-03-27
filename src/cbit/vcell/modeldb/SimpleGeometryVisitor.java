package cbit.vcell.modeldb;

import java.io.PrintStream;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.mathmodel.MathModelInfo;

public class SimpleGeometryVisitor implements VCDatabaseVisitor {

	public boolean filterGeometry(GeometryInfo geometryInfo) {
		return (geometryInfo.getDimension()>0);
	}

	public void visitGeometry(Geometry geometry, PrintStream logFilePrintStream) {
		logFilePrintStream.println(geometry.getName()+" extent = "+geometry.getExtent());
	}

	/**
	 * @param args
	 * for usage, please see command-line argument instructions in VCDatabaseVisitor
	 */
	public static void main(String[] args) {
		SimpleGeometryVisitor visitor = new SimpleGeometryVisitor();
		boolean bAbortOnDataAccessException = false;
		try{
			VCDatabaseScanner.scanGeometries(args, visitor, bAbortOnDataAccessException);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
	}


	//
	// not used for geometries
	//
	public boolean filterBioModel(BioModelInfo bioModelInfo) {
		return false;
	}
	//
	// not used for geometries
	//
	public void visitBioModel(BioModel bioModel, PrintStream logFilePrintStream) {
	}

	//
	// not used for geometries
	//
	public boolean filterMathModel(MathModelInfo mathModelInfo) {
		return false;
	}
	//
	// not used for geometries
	//
	public void visitMathModel(MathModel mathModel, PrintStream logFilePrintStream) {
	}


}
