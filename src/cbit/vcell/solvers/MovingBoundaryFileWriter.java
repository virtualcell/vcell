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
import java.io.PrintWriter;
import java.util.Objects;

import org.jdom.Document;
import org.jdom.Element;
import org.vcell.movingboundary.MovingBoundarySolverSpec;
import org.vcell.util.BeanUtils;
import org.vcell.util.ISize;

import cbit.util.xml.XmlUtil;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Equation;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.server.SolverFileWriter;

/**
 * Exporting simulation data to Moving Boundary XML format
 * Creation date: (12/18/2014 3:12:57 PM)
 * @author: Dan Vasilescu
 */
public class MovingBoundaryFileWriter extends SolverFileWriter {
//	private final File inputFile;
//	private final PrintWriter writer;
	private final Simulation simulation;
	private final MathDescription mathDesc;
	private final Geometry geometry;
	private MembraneSubDomain theMembrane = null;
	private final String outputPrefix;
	private final MovingBoundarySolverSpec solverSpec;
	/**
	 * temporary pending fixing MovingBoundary C++ to handle advection on per-species basis
	 */
	private Element problem;

public MovingBoundaryFileWriter(PrintWriter pw, SimulationTask simTask,  Geometry resampledGeometry, boolean arg_bMessaging, String outputPrefix,
		MovingBoundarySolverSpec mbss) {
	super(pw, simTask, arg_bMessaging);

	simulation = simTask.getSimulation();
	mathDesc = simulation.getMathDescription();
	geometry = mathDesc.getGeometry();
	this.outputPrefix = outputPrefix;
	solverSpec = mbss;
}

public MovingBoundaryFileWriter(PrintWriter pw, SimulationTask simTask,  Geometry resampledGeometry, boolean arg_bMessaging, String outputPrefix) {
	this(pw,simTask,resampledGeometry,arg_bMessaging,outputPrefix, new MovingBoundarySolverSpec( ));
}


/**
 * @throws UnsupportedOperationException
 */
@Override
public void write(String[] parameterNames) throws Exception {
	throw new UnsupportedOperationException( );

}
@Override
public void write() throws Exception {
	try {
		Document doc = new Document();
		Element root = writeMovingBoundaryXML(simTask);
		doc.setRootElement(root);
		XmlUtil.writeXml(doc, printWriter,false);
	} catch (Exception e) {
		throw new SolverException("Can't write input to solver",e);
	}
}

/**
 * @return number of subdomain which are {@link CompartmentSubDomain}s with {@link PdeEquation}s
 */
private int subdomainsSanityCheck() {
	int subdomainsWithPdeEquations = 0;
	for (SubDomain sd : mathDesc.getSubDomainCollection()) {
		if (sd instanceof CompartmentSubDomain) {
			for (Equation eq : sd.getEquationCollection())  {
				if (eq instanceof PdeEquation){
					subdomainsWithPdeEquations++;
					break;
				}
			}
			continue;
		}
		MembraneSubDomain msd = BeanUtils.downcast(MembraneSubDomain.class, sd);
		if (msd != null) {
			if (theMembrane != null) {
				throw new IllegalArgumentException("only one membrane currently supported");
			}
			theMembrane = msd;
		}
	}
	if (theMembrane == null) {
         throw new IllegalArgumentException("non membrane found");
	}
	return subdomainsWithPdeEquations;
}

//
//	XML producer - here all the work is done
//
public Element writeMovingBoundaryXML(SimulationTask simTask) throws SolverException {
	try {
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
	Element tr = getXMLTextReport( );
	if (tr != null) {
		rootElement.addContent(tr);
	}
	return rootElement;
	} catch (Exception e) {
		throw new SolverException("Can't generate MovingBoundary input", e);
	}
}
// ------------------------------------------------- problem
private Element getXMLProblem() throws ExpressionException, MathException {
	Element e = problem = new Element(MBTags.problem);

	e.addContent(getXMLxLimits());
	e.addContent(getXMLyLimits());
	{
		ISize isize = simulation.getMeshSpecification().getSamplingSize();
		e.addContent(getnumNodesX(isize));
		e.addContent(getnumNodesY(isize));
	}

	e.addContent(getfrontToNodeRatio());

	{
		SolverTaskDescription std = simulation.getSolverTaskDescription();
		e.addContent(getmaxTime(std));
		e.addContent(gettimeStep(std));
		Element ots = getOutputTimeStep(std);
		if (ots != null)
		{
			e.addContent(ots);
		}
	}

	e.addContent(getLevelFunction());
	e.addContent(getfrontVelocityFunctionX());
	e.addContent(getfrontVelocityFunctionY());
	e.addContent(getXMLphysiology());

	return e;
}

private Element getGeoLimit(String coordinateLabel, Double low, Double high) {
	Element e = new Element(coordinateLabel);
	Element e1 = new Element(MBTags.low);
	e1.setText(low.toString());
	e.addContent(e1);
	e1 = new Element(MBTags.high);		// sim.math.geometry.origin.x + sim.math.geometry.extent.x
	e1.setText(high.toString());
	e.addContent(e1);
	return e;
}

private Element getXMLxLimits() {
	double low = geometry.getOrigin().getX();
	double high = low + geometry.getExtent().getX();
	return getGeoLimit(MBTags.xLimits,low,high);
}

private Element getXMLyLimits() {
	double low = geometry.getOrigin().getY();
	double high = low + geometry.getExtent().getY();
	return getGeoLimit(MBTags.yLimits,low,high);
}

private Element getnumNodesX(ISize isize) {
	Objects.requireNonNull(isize);
	Element e = new Element(MBTags.numNodesX);
	Integer x = isize.getX();
	e.setText(x.toString());
	return e;
}

private Element getnumNodesY(ISize isize) {
	Objects.requireNonNull(isize);
	Element e = new Element(MBTags.numNodesY);
	Integer y = isize.getY();
	e.setText(y.toString());
	return e;
}

private Element getfrontToNodeRatio() {
	Element e = new Element(MBTags.frontToNodeRatio);
	e.setAttribute("mode", "HARDCODED");
	e.setText("5");
	return e;
}
private Element getmaxTime(SolverTaskDescription std) {
	Objects.requireNonNull(std);
	Element e = new Element(MBTags.maxTime);
	double endingTime = std.getTimeBounds().getEndingTime();
	e.setText(endingTime+"");
	return e;
}
private Element gettimeStep(SolverTaskDescription std) {
	Objects.requireNonNull(std);
	Element e = new Element(MBTags.timeStep);
	double defaultTimeStep = std.getTimeStep().getDefaultTimeStep();
	e.setText(defaultTimeStep+"");
	return e;
}
private Element getOutputTimeStep(SolverTaskDescription std) {
	Objects.requireNonNull(std);
	if (std.getOutputTimeSpec().isUniform())
	{
		Element e = new Element(MBTags.outputTimeStep);
		double outputTimeStep = ((UniformOutputTimeSpec)std.getOutputTimeSpec()).getOutputTimeStep();
		e.setText(outputTimeStep+"");
		return e;
	}
	return null;
}
private Element getLevelFunction() throws ExpressionException {
	Element e = new Element(MBTags.levelFunction);
	// geometry shape encode in "LevelFunction".
	GeometrySpec geometrySpec = geometry.getGeometrySpec();
	if (geometry.getGeometrySpec().hasImage()){
		throw new RuntimeException("image-based geometry not yet supported");
	}
	else{
			Expression[] rvachevExps = FiniteVolumeFileWriter.convertAnalyticGeometryToRvachevFunction(geometrySpec);
			if (rvachevExps.length == 2){
				Expression levelFunction = rvachevExps[0];
				String content = levelFunction.infix();
				e.setText(content);
			}
			else {
				throw new IllegalArgumentException("Can't get level function, expected 2 RvachevFunction expressions, got " + rvachevExps.length);
			}
	}
	return e;
}

/**
 * @param e can be null
 * @return flatted expression or "0" if e null or equivalent to 0
 * @throws ExpressionException
 * @throws MathException
 */
private String expressionAsString(Expression e) throws ExpressionException, MathException {
	if (Expression.notZero(e)) {
		return flattenExpression(e);
	}
	return "0";
}
private Element getFrontVelocity(String tag, Expression ex) throws ExpressionException, MathException {
	Element e = new Element(tag);
	e.setText(expressionAsString(ex));
	return e;

}
private Element getfrontVelocityFunctionX() throws ExpressionException, MathException {
	Objects.requireNonNull(theMembrane);
//	Expression velocityX = Expression.mult(theMembrane.getVelocityX(), new Expression("normalX"));
	Expression velocityX = theMembrane.getVelocityX();
	return getFrontVelocity(MBTags.frontVelocityFunctionX, velocityX);
}
private Element getfrontVelocityFunctionY() throws ExpressionException, MathException {
	Objects.requireNonNull(theMembrane);
	//Expression velocityY = Expression.mult(theMembrane.getVelocityY(), new Expression("normalY"));
	Expression velocityY = theMembrane.getVelocityY();
	return getFrontVelocity(MBTags.frontVelocityFunctionY,velocityY);
}
private Element getXMLphysiology() throws ExpressionException, MathException {
	final Element e = new Element(MBTags.physiology);

	mathDesc.getSubDomainCollection().stream().forEach( s -> manageCompartment(e,  s));
	return e;

}
private void manageCompartment(Element e, SubDomain sd) {
	try {
		if (sd instanceof CompartmentSubDomain) {
			for ( Equation equation : sd.getEquationCollection()) {
				if (equation.getVariable().getDomain().getName().equals(sd.getName()))
				{
					System.out.println("ignore:   " + equation.getVariable().getName());
				}

				if (equation instanceof PdeEquation){
					System.out.println("add this: " + equation.getVariable().getName());
					e.addContent(getSpecies((PdeEquation)equation));
				}
			}
		}
	} catch(Exception exc) {
		throw new RuntimeException("error managing compartment",exc);
	}
}

/**
 * set and annotate expression value; optionally include even if null. If ex is null and always == false, does nothing
 * @param dest element to set
 * @param ex to evaluate, could be null
 * @param always if true, set element even if expression null
 * @throws ExpressionException
 * @throws MathException
 */
private void setExpression(Element dest, Expression ex, boolean always) throws ExpressionException, MathException {
	if (ex !=null) {
		dest.setAttribute("value",ex.infix( ));
		dest.setText(flattenExpression(ex));
		return;
	}
	if (always) {
		dest.setAttribute("value","null");
		dest.setText("0");
	}
}

// TODO: flatten here
private Element getSpecies(PdeEquation eq) throws ExpressionException, MathException {
	Element e = new Element(MBTags.species);
	e.setAttribute("name", eq.getVariable().getName());

	Element e1 = null;
	e1 = new Element(MBTags.initial);
	Expression ex = eq.getInitialExpression();
	setExpression(e1, ex, false);
	e.addContent(e1);

	e1 = new Element(MBTags.source);
	ex = eq.getRateExpression();
	setExpression(e1, ex, false);
	e.addContent(e1);

	e1 = new Element(MBTags.diffusion);
	ex = eq.getDiffusionExpression();
	setExpression(e1, ex, false);
	e.addContent(e1);

	e1 = new Element(MBTags.advectVelocityFunctionX);
	ex = eq.getVelocityX();
	setExpression(e1, ex, true);
	e.addContent(e1);
	problem.addContent((Element)e1.clone());

	e1 = new Element(MBTags.advectVelocityFunctionY);
	ex = eq.getVelocityY();
	setExpression(e1, ex, true);
	e.addContent(e1);
	problem.addContent((Element)e1.clone());
	return e;
}

private String flattenExpression(Expression ex) throws ExpressionException, MathException {
	String name = ex.infix();
//	Simulation simulation = simTask.getSimulation();
	SimulationSymbolTable simSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();

	Variable[] variables = simTask.getSimulationJob().getSimulationSymbolTable().getVariables();
	for (int i = 0; i < variables.length; i++){
		if(variables[i].getName().equals(name)) {

			Expression rfexp = new Expression(ex);
				rfexp.bindExpression(simSymbolTable);
				rfexp = simSymbolTable.substituteFunctions(rfexp).flatten();
			return rfexp.infix();
		}
	}
	return name;
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
	e.addContent(getoutputFilePrefix());
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
private Element getoutputFilePrefix() {
	Element e = new Element(MBTags.outputFilePrefix);
	e.setText(outputPrefix);
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
		Integer step = defaultOutputTimeSpec.getKeepEvery();
		double startTime = 0.0; // hard code
		e1 = new Element(MBTags.startTime);
		e1.setText("0");
		e.addContent(e1);
		e1 = new Element(MBTags.step);
		e1.setText(step.toString());
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
	/*
	e1 = new Element(MBTags.traceFilename);
	e1.setAttribute("mode", "HARDCODED");
	e1.setText(outputPrefix + "trace.txt");
	e.addContent(e1);
	*/
	return e;
}
//------------------------------------------------------ debug
private Element getXMLDebug() {
	Element e = new Element(MBTags.matlabDebug);
	e.setAttribute("mode", "HARDCODED");
	e.setText("false");
	return e;
}

private Element getXMLTextReport() {
	if (solverSpec.isTextReport()) {
		Element e = new Element(MBTags.TEXT_REPORT);
		String filename = "MovingBoundary " + simulation.getSimulationID() + ".txt";
		Element fn = new Element(MBTags.outputFilePrefix);
		fn.setText(filename);
		Element w = new Element(MBTags.WIDTH);
		w.setText("6");
		Element p = new Element(MBTags.PRECISION);
		p.setText("6");

		markHardcoded(w);
		markHardcoded(p);

		e.addContent(fn);
		e.addContent(w);
		e.addContent(p);
		return e;
	}
	return null;

}

private void markHardcoded(Element e) {
	e.setAttribute("mode", "HARDCODED");
}


@SuppressWarnings("unused")
private class MBTags {
	private static final String MovingBoundarySetup		= "MovingBoundarySetup";

	private static final String problem					= "problem";
	private static final String noOperation				= "noOperation";
	private static final String circle					= "circle";
	private static final String originx					= "originx";
	private static final String originy					= "originy";
	private static final String radius					= "radius";
	private static final String velocityx				= "velocityx";
	private static final String theta					= "theta";
	private static final String thetaIncrement			= "thetaIncrement";

	private static final String specialFront				= "specialFront";
	private static final String expandingCircle			= "expandingCircle";
	private static final String radiusExpression			= "radiusExpression";

	private static final String xLimits					= "xLimits";
	private static final String yLimits					= "yLimits";
	private static final String low						= "low";
	private static final String high						= "high";
	private static final String numNodesX				= "numNodesX";
	private static final String numNodesY				= "numNodesY";
	private static final String frontToNodeRatio			= "frontToNodeRatio";
	private static final String maxTime					= "maxTime";
	private static final String timeStep					= "timeStep";
	private static final String outputTimeStep					= "outputTimeStep";
	private static final String diffusion		        = "diffusion";
	private static final String diffusionConstant		= "diffusionConstant";
	private static final String levelFunction			= "levelFunction";
	private static final String advectVelocityFunctionX	= "advectVelocityFunctionX";
	private static final String advectVelocityFunctionY	= "advectVelocityFunctionY";
	private static final String frontVelocityFunctionX	= "frontVelocityFunctionX";
	private static final String frontVelocityFunctionY	= "frontVelocityFunctionY";

	private static final String physiology				= "physiology";
	private static final String species					= "species";
	private static final String name						= "name";
	private static final String source					= "source";
	private static final String initial					= "initial";
	private static final String concentration			= "concentration";

	private static final String report					= "report";
	private static final String deleteExisting			= "deleteExisting";
//	private static final String outputFilename			= "outputFilename";
	private static final String outputFilePrefix	  = "outputFilePrefix";
	private static final String datasetName				= "datasetName";
	private static final String annotation				= "annotation";
	private static final String series					= "series";
	private static final String timeReport				= "timeReport";
	private static final String startTime				= "startTime";
	private static final String step						= "step";
	private static final String interval					= "interval";

	private static final String progress					= "progress";
	private static final String percent					= "percent";
	private static final String estimateProgress			= "estimateProgress";

	private static final String trace					= "trace";
	private static final String level					= "level";
	private static final String traceFilename			= "traceFilename";

	private static final String matlabDebug				= "matlabDebug";

	private static final String TEXT_REPORT 			= "textReport";
	private static final String WIDTH 					= "width";
	private static final String PRECISION 				= "precision";
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



