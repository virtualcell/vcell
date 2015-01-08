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
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Vector;

import org.jdom.Element;

import cbit.util.xml.XmlUtil;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Equation;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.NFsimSimulationOptions;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.server.SolverFileWriter;

/**
 * Exporting simulation data to Moving Boundary XML format
 * Creation date: (12/18/2014 3:12:57 PM)
 * @author: Dan Vasilescu
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

private int subdomainsSanityCheck() {
	int subdomainsWithPdeEquations = 0;
	Simulation simulation = simTask.getSimulation();
	MathDescription mathDesc = simulation.getMathDescription();
	Enumeration<SubDomain> enum1 = mathDesc.getSubDomains();
	while (enum1.hasMoreElements()) {
		SubDomain sd = enum1.nextElement();
		if (sd instanceof CompartmentSubDomain) {
			CompartmentSubDomain csd = (CompartmentSubDomain)sd;
			System.out.println("compartment " + csd.getName());
			Enumeration<Equation> enum_equ = csd.getEquations();
			while (enum_equ.hasMoreElements()){
				Equation equation = enum_equ.nextElement();
				if (equation instanceof PdeEquation){
					subdomainsWithPdeEquations++;
					break;
				}
			}
		}
	}
	return subdomainsWithPdeEquations;
}

//
//	XML producer - here all the work is done
//
public Element writeMovingBoundaryXML(SimulationTask simTask) throws SolverException {
	int subdomainsWithPdeEquations = subdomainsSanityCheck();
	if(subdomainsWithPdeEquations != 1) {
		throw new RuntimeException("MovingBoundary Solver only accepts ONE subdomain containing PDE equations.");
	}
	
	Element rootElement = new Element(MBTags.MovingBoundarySetup);
	rootElement.addContent(getXMLProblem());	// problem
	rootElement.addContent(getXMLReport());		// report
	rootElement.addContent(getXMLProgress());	// progress
	rootElement.addContent(getXMLTrace());		// trace
	rootElement.addContent(getXMLDebug());		// debug
	return rootElement;
}
// ------------------------------------------------- problem
private Element getXMLProblem() {
	Element e = new Element(MBTags.problem);
	
//	e.addContent(getXMLnoOperation());
//	e.addContent(getXMLspecialFront());
	e.addContent(getXMLxLimits());
	e.addContent(getXMLyLimits());
	
	e.addContent(getnumNodesX());
	e.addContent(getnumNodesY());
	e.addContent(getfrontToNodeRatio());
	e.addContent(getmaxTime());
	e.addContent(gettimeStep());
	e.addContent(getLevelFunction());
	e.addContent(getfrontVelocityFunctionX());
	e.addContent(getfrontVelocityFunctionY());

	e.addContent(getXMLphysiology());

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
	e1.setAttribute("mode", "HARDCODED");
	e1.setText("0");
	e.addContent(e1);
	e1 = new Element(MBTags.originy);
	e1.setAttribute("mode", "HARDCODED");
	e1.setText("0");
	e.addContent(e1);
	e1 = new Element(MBTags.radius);
	e1.setAttribute("mode", "HARDCODED");
	e1.setText("1");
	e.addContent(e1);
	e1 = new Element(MBTags.velocityx);
	e1.setAttribute("mode", "HARDCODED");
	e1.setText("1");
	e.addContent(e1);
	e1 = new Element(MBTags.thetaIncrement);
	e1.setAttribute("mode", "HARDCODED");
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
	e1.setAttribute("mode", "HARDCODED");
	e1.setText(".01");
	e.addContent(e1);
	e1 = new Element(MBTags.radiusExpression);
	e1.setAttribute("mode", "HARDCODED");
	e1.setText("1+sin(t)");
	e.addContent(e1);
	return e;
}
private Element getXMLxLimits() {
	Element e = new Element(MBTags.xLimits);
	Element e1 = null;
	e1 = new Element(MBTags.low);
	double l = simTask.getSimulation().getMathDescription().getGeometry().getOrigin().getX();
	e1.setText(l+"");
	e.addContent(e1);
	e1 = new Element(MBTags.high);		// sim.math.geometry.origin.x + sim.math.geometry.extent.x
	double h = l + simTask.getSimulation().getMathDescription().getGeometry().getExtent().getX();
	e1.setText(h+"");
	e.addContent(e1);
	return e;
}
private Element getXMLyLimits() {
	Element e = new Element(MBTags.yLimits);
	Element e1 = null;
	e1 = new Element(MBTags.low);
	double l = simTask.getSimulation().getMathDescription().getGeometry().getOrigin().getY();
	e1.setText(l+"");
	e.addContent(e1);
	e1 = new Element(MBTags.high);
	double h = l + simTask.getSimulation().getMathDescription().getGeometry().getExtent().getY();
	e1.setText(h+"");
	e.addContent(e1);
	return e;
}
private Element getnumNodesX() {
	Element e = new Element(MBTags.numNodesX);
	int x = simTask.getSimulation().getMeshSpecification().getSamplingSize().getX();
	e.setText(x+"");
	return e;
}
private Element getnumNodesY() {
	Element e = new Element(MBTags.numNodesY);
	int y = simTask.getSimulation().getMeshSpecification().getSamplingSize().getY();
	e.setText(y+"");
	return e;
}
private Element getfrontToNodeRatio() {
	Element e = new Element(MBTags.frontToNodeRatio);
	e.setAttribute("mode", "HARDCODED");
	e.setText("5");
	return e;
}
private Element getmaxTime() {
	Element e = new Element(MBTags.maxTime);
	double endingTime = simTask.getSimulation().getSolverTaskDescription().getTimeBounds().getEndingTime();
	e.setText(endingTime+"");
	return e;
}
private Element gettimeStep() {
	Element e = new Element(MBTags.timeStep);
	double defaultTimeStep = simTask.getSimulation().getSolverTaskDescription().getTimeStep().getDefaultTimeStep();
	e.setText(defaultTimeStep+"");
	return e;
}
private Element getLevelFunction() {
	Element e = new Element(MBTags.levelFunction);
	// geometry shape encode in "LevelFunction".
	Geometry geometry = simTask.getSimulation().getMathDescription().getGeometry();
	GeometrySpec geometrySpec = geometry.getGeometrySpec();
	if (geometry.getGeometrySpec().hasImage()){
		throw new RuntimeException("image-based geometry not yet supported");
	}
	else{
		Expression[] rvachevExps;
		try {
			rvachevExps = FiniteVolumeFileWriter.convertAnalyticGeometryToRvachevFunction(geometrySpec);
			if (rvachevExps.length == 2){
				Expression levelFunction = rvachevExps[0];
				String content = levelFunction.infix();
				e.setText(content);
			}
		} catch (ExpressionException e1) {
			e1.printStackTrace();
		}
	}
	return e;
}
private Element getfrontVelocityFunctionX() {
	Element e = new Element(MBTags.frontVelocityFunctionX);
	e.setAttribute("mode", "HARDCODED");
	e.setText("1");
	return e;
}
private Element getfrontVelocityFunctionY() {
	Element e = new Element(MBTags.frontVelocityFunctionY);
	e.setAttribute("mode", "HARDCODED");
	e.setText("0");
	return e;
}
private Element getXMLphysiology() {
	Element e = new Element(MBTags.physiology);
	
//	SimulationSymbolTable simulationSymbolTable = new SimulationSymbolTable(simTask.getSimulation(), simTask.getTaskID());
//	Variable[] variables = simulationSymbolTable.getVariables();
//	for(Variable v : variables) {
//		if(v instanceof VolVariable && simTask.getSimulation().getMathDescription().isPDE((VolVariable)v)) {
//			System.out.println(v.getName() + ", " + v.getClass().getSimpleName());
//			e.addContent(getSpecies((VolVariable)v));
//		}
//	}
	
	Simulation simulation = simTask.getSimulation();
	MathDescription mathDesc = simulation.getMathDescription();
	Enumeration<SubDomain> enum1 = mathDesc.getSubDomains();
	while (enum1.hasMoreElements()) {		
		SubDomain sd = enum1.nextElement();
		if (sd instanceof CompartmentSubDomain) {
			CompartmentSubDomain csd = (CompartmentSubDomain)sd;
			System.out.println("compartment " + csd.getName());
			
			e = manageCompartment(e, csd);
		}
	}
	return e;
}
private Element manageCompartment(Element e, CompartmentSubDomain csd)
{
	Enumeration<Equation> enum_equ = csd.getEquations();
	while (enum_equ.hasMoreElements()){
		Equation equation = enum_equ.nextElement();
		if (equation.getVariable().getDomain().getName().equals(csd.getName()))
		{
			System.out.println("ignore:   " + equation.getVariable().getName());
		}
		
		if (equation instanceof PdeEquation){
			System.out.println("add this: " + equation.getVariable().getName());
			e.addContent(getSpecies((PdeEquation)equation));
		}	
	}
	return e;
}
// TODO: flatten here
private Element getSpecies(PdeEquation eq) {
	Element e = new Element(MBTags.species);
	e.setAttribute("name", eq.getVariable().getName());
	
	Element e1 = null;
	e1 = new Element(MBTags.initial);
	Expression ex = eq.getInitialExpression();
	if(ex != null) {
		e1.setAttribute("value", ex.infix());
		e1.setText(flattenExpression(ex));
	}
	e.addContent(e1);

	e1 = new Element(MBTags.source);
	ex = eq.getDiffusionExpression();
	if(ex != null) {
		e1.setAttribute("value", ex.infix());
		e1.setText(flattenExpression(ex));
	}
	e.addContent(e1);
	
	e1 = new Element(MBTags.diffusionConstant);
	ex = eq.getRateExpression();
	if(ex != null) {
		e1.setAttribute("value", ex.infix());
		e1.setText(flattenExpression(ex));
	}
	e.addContent(e1);

	e1 = new Element(MBTags.advectVelocityFunctionX);
	ex = eq.getVelocityX();
	if(ex != null) {
		e1.setAttribute("value", ex.infix());
		e1.setText(flattenExpression(ex));
	} else {
		e1.setText("0");
	}
	e.addContent(e1);
	e1 = new Element(MBTags.advectVelocityFunctionY);
	ex = eq.getVelocityY();
	if(ex != null) {
		e1.setAttribute("value", ex.infix());
		e1.setText(flattenExpression(ex));
	} else {
		e1.setText("0");
	}
	e.addContent(e1);
	return e;
}

private String flattenExpression(Expression ex) {
	String name = ex.infix();
//	Simulation simulation = simTask.getSimulation();
	SimulationSymbolTable simSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();

	Variable[] variables = simTask.getSimulationJob().getSimulationSymbolTable().getVariables();
	for (int i = 0; i < variables.length; i++){
		if(variables[i].getName().equals(name)) {
		
			Expression rfexp = new Expression(ex);
			try {
				rfexp.bindExpression(simSymbolTable);
				rfexp = simSymbolTable.substituteFunctions(rfexp).flatten();
			} catch (ExpressionException e) {
				e.printStackTrace();
			} catch (MathException e) {
				e.printStackTrace();
			}
			return rfexp.infix();
		}
	}
	return null;
}

private Element getSpecies(VolVariable v) {
	Element e = new Element(MBTags.species);
	e.setAttribute("name", v.getName());
	Element e1 = null;
	e1 = new Element(MBTags.source);
	
	Expression ex = v.getExpression();
	if(ex != null) {
		e1.setText(ex.infix());
	}
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
	e.setAttribute("mode", "HARDCODED");
	e.setText("1");
	return e;
}
private Element getoutputFilename() {
	Element e = new Element(MBTags.outputFilename);
	e.setAttribute("mode", "HARDCODED");
	e.setText("figsix-10-4.h5");
	return e;
}
private Element getdatasetName() {
	Element e = new Element(MBTags.datasetName);
	e.setAttribute("mode", "HARDCODED");
	e.setText("10");
	return e;
}
private Element getannotation() {
	Element e = new Element(MBTags.annotation);
	Element e1 = null;
	e1 = new Element(MBTags.series);
	e1.setAttribute("mode", "HARDCODED");
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
	e1.setAttribute("mode", "HARDCODED");
	e1.setText("5");
	e.addContent(e1);
	e1 = new Element(MBTags.estimateProgress);
	e1.setAttribute("mode", "HARDCODED");
	e1.setText("true");
	e.addContent(e1);
	return e;
}
//----------------------------------------------------- trace
private Element getXMLTrace() {
	Element e = new Element(MBTags.trace);
	Element e1 = null;
	e1 = new Element(MBTags.level);
	e1.setAttribute("mode", "HARDCODED");
	e1.setText("warn");
	e.addContent(e1);
	e1 = new Element(MBTags.traceFilename);
	e1.setAttribute("mode", "HARDCODED");
	e1.setText("trace10-4.txt");
	e.addContent(e1);
	return e;
}
//------------------------------------------------------ debug
private Element getXMLDebug() {
	Element e = new Element(MBTags.matlabDebug);
	e.setAttribute("mode", "HARDCODED");
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

//{	// for testing purposes only
//Document doc = new Document();
//Element clone = (Element)rootElement.clone();
//doc.setRootElement(clone);
//String xmlString = XmlUtil.xmlToString(doc, false);
//System.out.println(xmlString);
//}

//<?xml version="1.0" encoding="UTF-8"?>



