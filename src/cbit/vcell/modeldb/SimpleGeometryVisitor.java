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
