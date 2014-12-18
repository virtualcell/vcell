/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solvers;
import java.beans.PropertyVetoException;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.io.output.StringBuilderWriter;
import org.jdom.Document;
import org.jdom.Element;
import org.vcell.chombo.ChomboSolverSpec;
import org.vcell.chombo.RefinementLevel;
import org.vcell.solver.nfsim.NFsimXMLWriter;
import org.vcell.solver.nfsim.NFsimXMLWriter.BondSites;
import org.vcell.solver.nfsim.NFsimXMLWriter.ComponentOfMolecularTypeOfReactionParticipant;
import org.vcell.solver.nfsim.NFsimXMLWriter.MappingOfReactionParticipants;
import org.vcell.solver.nfsim.NFsimXMLWriter.MolecularTypeOfReactionParticipant;
import org.vcell.solver.smoldyn.SmoldynFileWriter;
import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.NullSessionLog;
import org.vcell.util.Origin;
import org.vcell.util.PropertyLoader;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.image.VCPixelClass;
import cbit.util.xml.XmlUtil;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.surface.DistanceMapGenerator;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.RayCaster;
import cbit.vcell.geometry.surface.SubvolumeSignedDistanceMap;
import cbit.vcell.mapping.FastSystemAnalyzer;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.ConvolutionDataGenerator;
import cbit.vcell.math.ConvolutionDataGenerator.ConvolutionDataGeneratorKernel;
import cbit.vcell.math.ConvolutionDataGenerator.GaussianConvolutionDataGeneratorKernel;
import cbit.vcell.math.Action;
import cbit.vcell.math.DataGenerator;
import cbit.vcell.math.Distribution;
import cbit.vcell.math.Equation;
import cbit.vcell.math.FastSystem;
import cbit.vcell.math.FilamentVariable;
import cbit.vcell.math.GaussianDistribution;
import cbit.vcell.math.JumpCondition;
import cbit.vcell.math.JumpProcessRateDefinition;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneParticleVariable;
import cbit.vcell.math.MembraneRandomVariable;
import cbit.vcell.math.MembraneRegionEquation;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.ParameterVariable;
import cbit.vcell.math.ParticleJumpProcess;
import cbit.vcell.math.ParticleVariable;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.VolumeParticleSpeciesPattern;
import cbit.vcell.math.PdeEquation.BoundaryConditionValue;
import cbit.vcell.math.PostProcessingBlock;
import cbit.vcell.math.ProjectionDataGenerator;
import cbit.vcell.math.PseudoConstant;
import cbit.vcell.math.RandomVariable;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.SubDomain.BoundaryConditionSpec;
import cbit.vcell.math.UniformDistribution;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.VolumeParticleVariable;
import cbit.vcell.math.VolumeRandomVariable;
import cbit.vcell.math.VolumeRegionEquation;
import cbit.vcell.math.VolumeRegionVariable;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.Discontinuity;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.parser.RvachevFunctionUtils;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.render.Vect3d;
import cbit.vcell.simdata.DataSet;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.NFsimSimulationOptions;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.server.SolverFileWriter;
import cbit.vcell.solvers.FiniteVolumeFileWriter.FVInputFileKeyword;
import cbit.vcell.xml.XMLTags;

/**
 * Insert the type's description here.
 * Creation date: (5/9/2005 2:51:48 PM)
 * @author: Fei Gao
 */
public class MovingBoundaryFileWriter extends SolverFileWriter {
	private File userDirectory = null;
	private Geometry resampledGeometry = null;
	private File inputFile = null;
	
public MovingBoundaryFileWriter(PrintWriter pw, SimulationTask simTask, Geometry resampledGeometry, File dir) {	// for optimization only, no messaging
	this (pw, simTask, resampledGeometry, dir, false);
}

public MovingBoundaryFileWriter(PrintWriter pw, SimulationTask simTask,  Geometry resampledGeometry, File dir, boolean arg_bMessaging) {
	super(pw, simTask, arg_bMessaging);
	this.resampledGeometry = resampledGeometry;
	userDirectory = dir;
	inputFile = new File(userDirectory,"MovingBoundary.xml");
//	inputFile = new File(userDirectory,"SimID_"+simTask.getSimulationJobID()+".xml");
}



@Override
public void write(String[] parameterNames) throws Exception {
	// TODO Auto-generated method stub
	
}
@Override
public void write() throws Exception {
	FileOutputStream fos = new FileOutputStream(inputFile);
	try {
		NFsimSimulationOptions nfsimSimulationOptions = simTask.getSimulation().getSolverTaskDescription().getNFSimSimulationOptions();
		Element root = writeMovingBoundaryXML(simTask);
		XmlUtil.writeXmlToStream(root, false, fos);
	}finally{
		if (fos!=null){
			fos.close();
		}
	}
}

public static void test(SimulationTask simTask, Geometry resampledGeometry) {
	try {
		StringWriter simulationInputStringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(simulationInputStringWriter,true);		

		MovingBoundaryFileWriter movingBoundaryFileWriter = new MovingBoundaryFileWriter(printWriter, simTask, resampledGeometry, new File("c:\\temp\\MovingBoundary\\"));
		movingBoundaryFileWriter.write();
		// TODO Auto-generated method stub
	} catch (Exception e){
		e.printStackTrace(System.out);
	}
}

public Element writeMovingBoundaryXML(SimulationTask simTask) throws SolverException {
	MathDescription mathDesc = simTask.getSimulation().getMathDescription();
	
	Element rootElement = new Element(MBTags.MovingBoundarySetup);
	rootElement.addContent(getXMLProblem());	// problem
	rootElement.addContent(getXMLReport());		// report
	rootElement.addContent(getXMLProgress());	// progress
	rootElement.addContent(getXMLTrace());		// trace
	rootElement.addContent(getXMLDebug());		// debug



//	SimulationSymbolTable simulationSymbolTable = new SimulationSymbolTable(simTask.getSimulation(), simTask.getTaskID());
	
//	{	// for testing purposes only
//		Document doc = new Document();
//		Element clone = (Element)rootElement.clone();
//		doc.setRootElement(clone);
//		String xmlString = XmlUtil.xmlToString(doc, false);
//		System.out.println(xmlString);
//	}

	return rootElement;
}
// ------------------------------------------------- problem
private Element getXMLProblem() {
	Element e = new Element(MBTags.problem);
	
	e.addContent(getXMLnoOperation());
	e.addContent(getXMLspecialFront());
	e.addContent(getXMLxLimits());
	e.addContent(getXMyLimits());
	
	e.addContent(getnumNodesX());
	e.addContent(getnumNodesY());
	e.addContent(getfrontToNodeRatio());
	e.addContent(getmaxTime());
	e.addContent(gettimeStep());
	e.addContent(getdiffusionConstant());
	e.addContent(getLevelFunction());
	e.addContent(getadvectVelocityFunctionX());
	e.addContent(getadvectVelocityFunctionY());
	e.addContent(getfrontVelocityFunctionX());
	e.addContent(getfrontVelocityFunctionY());

	e.addContent(getXMLphysiology());
	e.addContent(getXMLconcentration());

	return e;
}
private Element getXMLnoOperation() {
	Element e = new Element(MBTags.noOperation);
	e.addContent(getcircle());
	return e;
}
private Element getcircle() {
	Element e = new Element(MBTags.circle);
	Element e1 = null;
	e1 = new Element(MBTags.originx);
	e1.setText("0");
	e.addContent(e1);
	e1 = new Element(MBTags.originy);
	e1.setText("0");
	e.addContent(e1);
	e1 = new Element(MBTags.radius);
	e1.setText("1");
	e.addContent(e1);
	e1 = new Element(MBTags.velocityx);
	e1.setText("1");
	e.addContent(e1);
	e1 = new Element(MBTags.thetaIncrement);
	e1.setText("THETA");
	e.addContent(e1);
	return e;
}
private Element getXMLspecialFront() {
	Element e = new Element(MBTags.specialFront);
	e.addContent(getexpandingCircle());
	return e;
}
private Element getexpandingCircle() {
	Element e = new Element(MBTags.expandingCircle);
	Element e1 = null;
	e1 = new Element(MBTags.theta);
	e1.setText(".01");
	e.addContent(e1);
	e1 = new Element(MBTags.radiusExpression);
	e1.setText("1+sin(t)");
	e.addContent(e1);
	return e;
}
private Element getXMLxLimits() {
	Element e = new Element(MBTags.xLimits);
	Element e1 = null;
	e1 = new Element(MBTags.low);
	e1.setText("-1.5");
	e.addContent(e1);
	e1 = new Element(MBTags.high);
	e1.setText("1.5");
	e.addContent(e1);
	return e;
}
private Element getXMyLimits() {
	Element e = new Element(MBTags.yLimits);
	Element e1 = null;
	e1 = new Element(MBTags.low);
	e1.setText("-1.5");
	e.addContent(e1);
	e1 = new Element(MBTags.high);
	e1.setText("1.5");
	e.addContent(e1);
	return e;
}
private Element getnumNodesX() {
	Element e = new Element(MBTags.numNodesX);
	e.setText("10");
	return e;
}
private Element getnumNodesY() {
	Element e = new Element(MBTags.numNodesY);
	e.setText("10");
	return e;
}
private Element getfrontToNodeRatio() {
	Element e = new Element(MBTags.frontToNodeRatio);
	e.setText("1");
	return e;
}
private Element getmaxTime() {
	Element e = new Element(MBTags.maxTime);
	e.setText(".2");
	return e;
}
private Element gettimeStep() {
	Element e = new Element(MBTags.timeStep);
	e.setText(".2");
	return e;
}
private Element getdiffusionConstant() {
	Element e = new Element(MBTags.diffusionConstant);
	e.setText("1");
	return e;
}
private Element getLevelFunction() {
	Element e = new Element(MBTags.levelFunction);
	e.setText("x^2 + y^2 - 1");

// geometry shape encode in "LevelFunction".
Geometry geometry = simTask.getSimulation().getMathDescription().getGeometry();
GeometrySpec geometrySpec = geometry.getGeometrySpec();
//if (geometry.getGeometrySpec().hasImage()){
//	throw new RuntimeException("image-based geometry not yet supported");
//}
//else{
//	Expression[] rvachevExps = FiniteVolumeFileWriter.convertAnalyticGeometryToRvachevFunction(geometrySpec);
//	if (rvachevExps.length == 2){
//		Expression levelFunction = rvachevExps[0];
//		Element.content = levelFunction.infix();
//	}
//}
	return e;
}
private Element getadvectVelocityFunctionX() {
	Element e = new Element(MBTags.advectVelocityFunctionX);
	e.setText("0");
	return e;
}
private Element getadvectVelocityFunctionY() {
	Element e = new Element(MBTags.advectVelocityFunctionY);
	e.setText("0");
	return e;
}
private Element getfrontVelocityFunctionX() {
	Element e = new Element(MBTags.frontVelocityFunctionX);
	e.setText("1");
	return e;
}
private Element getfrontVelocityFunctionY() {
	Element e = new Element(MBTags.frontVelocityFunctionY);
	e.setText("0");
	return e;
}
private Element getXMLphysiology() {
	Element e = new Element(MBTags.physiology);
	e.addContent(getspecies("a"));
	e.addContent(getspecies());
	return e;
}
private Element getspecies(String name) {
	Element e = new Element(MBTags.species);
	e.setAttribute("name", name);
	Element e1 = null;
	e1 = new Element(MBTags.source);
	e1.setText("x/(x*x+y*y)^0.5*j1(1.841183781340659*(x*x+y*y)^0.5)+j1(1.841183781340659)");
	e.addContent(e1);
	return e;
}
private Element getspecies() {
	Element e = new Element(MBTags.species);
	Element e1 = null;
	e1 = new Element(MBTags.initial);
	e1.setText("1");
	e.addContent(e1);
	e1 = new Element(MBTags.source);
	e1.setText("exp(-x)");
	e.addContent(e1);
	return e;
}
private Element getXMLconcentration() {
	Element e = new Element(MBTags.concentration);
	Element e1 = null;
	e1 = new Element(MBTags.species);
	e1.setAttribute("name", "u");
	e1.setText("1");
	e.addContent(e1);
	return e;
}
//------------------------------------------------ report
private Element getXMLReport() {
	Element e = new Element(MBTags.report);
	e.addContent(getdeleteExisting());
	e.addContent(getoutputFilename());
	e.addContent(getdatasetName());
	e.addContent(getannotation());
	e.addContent(gettimeReport());
	return e;
}
private Element getdeleteExisting() {
	Element e = new Element(MBTags.deleteExisting);
	e.setText("1");
	return e;
}
private Element getoutputFilename() {
	Element e = new Element(MBTags.outputFilename);
	e.setText("figsix-10-4.h5");
	return e;
}
private Element getdatasetName() {
	Element e = new Element(MBTags.datasetName);
	e.setText("10");
	return e;
}
private Element getannotation() {
	Element e = new Element(MBTags.annotation);
	Element e1 = null;
	e1 = new Element(MBTags.series);
	e1.setText("spatial convergence");
	e.addContent(e1);
	return e;
}
private Element gettimeReport() {
	Element e = new Element(MBTags.timeReport);
	Element e1 = null;
	OutputTimeSpec outTimeSpec = simTask.getSimulation().getSolverTaskDescription().getOutputTimeSpec();
	if (outTimeSpec instanceof UniformOutputTimeSpec){
		UniformOutputTimeSpec uniformOutputTimeSpec = (UniformOutputTimeSpec)outTimeSpec;
		double interval = uniformOutputTimeSpec.getOutputTimeStep();
		double startTime = 0.0; // hard coded.
		e1 = new Element(MBTags.startTime);
		e1.setText("0");
		e.addContent(e1);
		e1 = new Element(MBTags.interval);
		e1.setText(interval+"");
		e.addContent(e1);
	} else if (outTimeSpec instanceof DefaultOutputTimeSpec) {
		DefaultOutputTimeSpec defaultOutputTimeSpec = (DefaultOutputTimeSpec)outTimeSpec;
		double step = defaultOutputTimeSpec.getKeepEvery();
		double startTime = 0.0; // hard code
		e1 = new Element(MBTags.startTime);
		e1.setText("0");
		e.addContent(e1);
		e1 = new Element(MBTags.step);
		e1.setText(step+"");
		e.addContent(e1);
	}
	return e;
}
//---------------------------------------------------- progress
private Element getXMLProgress() {
	Element e = new Element(MBTags.progress);
	Element e1 = null;
	e1 = new Element(MBTags.percent);
	e1.setText("5");
	e.addContent(e1);
	e1 = new Element(MBTags.estimateProgress);
	e1.setText("true");
	e.addContent(e1);
	return e;
}
//----------------------------------------------------- trace
private Element getXMLTrace() {
	Element e = new Element(MBTags.trace);
	Element e1 = null;
	e1 = new Element(MBTags.level);
	e1.setText("warn");
	e.addContent(e1);
	e1 = new Element(MBTags.traceFilename);
	e1.setText("trace10-4.txt");
	e.addContent(e1);
	return e;
}
//------------------------------------------------------ debug
private Element getXMLDebug() {
	Element e = new Element(MBTags.matlabDebug);
	e.setText("false");
	return e;
}


public class MBTags {
	public static final String MovingBoundarySetup		= "MovingBoundarySetup";
	
	public static final String problem					= "problem";
	public static final String noOperation				= "noOperation";
	public static final String circle					= "circle";
	public static final String originx					= "originx";
	public static final String originy					= "originy";
	public static final String radius					= "radius";
	public static final String velocityx				= "velocityx";
	public static final String theta					= "theta";
	public static final String thetaIncrement			= "thetaIncrement";
	
	public static final String specialFront				= "specialFront";
	public static final String expandingCircle			= "expandingCircle";
	public static final String radiusExpression			= "radiusExpression";
	
	public static final String xLimits					= "xLimits";
	public static final String yLimits					= "yLimits";
	public static final String low						= "low";
	public static final String high						= "high";
	public static final String numNodesX				= "numNodesX";
	public static final String numNodesY				= "numNodesY";
	public static final String frontToNodeRatio			= "frontToNodeRatio";
	public static final String maxTime					= "maxTime";
	public static final String timeStep					= "timeStep";
	public static final String diffusionConstant		= "diffusionConstant";
	public static final String levelFunction			= "levelFunction";
	public static final String advectVelocityFunctionX	= "advectVelocityFunctionX";
	public static final String advectVelocityFunctionY	= "advectVelocityFunctionY";
	public static final String frontVelocityFunctionX	= "frontVelocityFunctionX";
	public static final String frontVelocityFunctionY	= "frontVelocityFunctionY";
	
	public static final String physiology				= "physiology";
	public static final String species					= "species";
	public static final String name						= "name";
	public static final String source					= "source";
	public static final String initial					= "initial";
	public static final String concentration			= "concentration";

	public static final String report					= "report";
	public static final String deleteExisting			= "deleteExisting";
	public static final String outputFilename			= "outputFilename";
	public static final String datasetName				= "datasetName";
	public static final String annotation				= "annotation";
	public static final String series					= "series";
	public static final String timeReport				= "timeReport";
	public static final String startTime				= "startTime";
	public static final String step						= "step";
	public static final String interval					= "interval";

	public static final String progress					= "progress";
	public static final String percent					= "percent";
	public static final String estimateProgress			= "estimateProgress";
	
	public static final String trace					= "trace";
	public static final String level					= "level";
	public static final String traceFilename			= "traceFilename";

	public static final String matlabDebug				= "matlabDebug";
}


}








//<?xml version="1.0" encoding="UTF-8"?>



