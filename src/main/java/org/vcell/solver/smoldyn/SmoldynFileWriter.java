/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.solver.smoldyn;

import java.awt.Color;
import java.beans.PropertyVetoException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.zip.DeflaterOutputStream;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.Logger;
import org.vcell.solver.smoldyn.SmoldynVCellMapper.SmoldynKeyword;
import org.vcell.util.BeanUtils;
import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.Hex;
import org.vcell.util.ISize;
import org.vcell.util.NullSessionLog;
import org.vcell.util.Origin;
import org.vcell.util.ProgrammingException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.VCAssert;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCPixelClass;
import cbit.plot.gui.Plot2DPanel;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.Node;
import cbit.vcell.geometry.surface.Polygon;
import cbit.vcell.geometry.surface.RayCaster;
import cbit.vcell.geometry.surface.Surface;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.Triangle;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.math.Action;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.InteractionRadius;
import cbit.vcell.math.JumpProcessRateDefinition;
import cbit.vcell.math.MacroscopicRateConstant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MembraneParticleVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.ParticleJumpProcess;
import cbit.vcell.math.ParticleProperties;
import cbit.vcell.math.ParticleProperties.ParticleInitialCondition;
import cbit.vcell.math.ParticleProperties.ParticleInitialConditionConcentration;
import cbit.vcell.math.ParticleProperties.ParticleInitialConditionCount;
import cbit.vcell.math.ParticleVariable;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VolumeParticleVariable;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.render.Vect3d;
import cbit.vcell.simdata.DataSet;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.DataProcessingInstructions;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SmoldynSimulationOptions;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.server.SolverFileWriter;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.FiniteVolumeFileWriter;
import cbit.vcell.solvers.MembraneElement;


/**
 * The function reads model information from simulation and
 * generates the stochastic input file for simulation engine.
 * Creation date: (6/22/2006 4:22:59 PM)
 * @author: Tracy LI
 */
public class SmoldynFileWriter extends SolverFileWriter
{
	/**
	 * allow easy on / off of disabling
	 */
	private static final String PANEL_TRIANGLE_NAME_PREFIX = "tri";
	private static final Color bg = new Color(0x0);
	private Color[] colors = null;

	private static class TrianglePanel {
		String name;
		Triangle triangle;
		private TrianglePanel(int triLocalIndex, int triGlobalIndex, int membraneIndex,  Triangle triangle) {
			super();
			this.name = PANEL_TRIANGLE_NAME_PREFIX + "_" + triLocalIndex + "_" + triGlobalIndex + (membraneIndex >= 0 ? "_" + membraneIndex : "");
			this.triangle = triangle;
		}
		@Override
		public String toString() {
			return "TrianglePanel [" + name + ' ' + triangle + "]";
		}
	}
	private static final int MAX_MOLECULE_LIMIT       = 1_000_000;
	private static final int MIN_MOLECULE_LIMIT 		  =    50_000;
	private static final int MOLECULE_MAX_COEFFICIENT =        10;
	private long randomSeed = 0; //value assigned in the constructor
	private RandomDataGenerator dist = new RandomDataGenerator();

	@SuppressWarnings("serial")
	class NotAConstantException extends Exception {
	}

	private File outputFile = null;
	private Simulation simulation = null;
	private MathDescription mathDesc = null;
	private SimulationSymbolTable simulationSymbolTable = null;
	private ArrayList<ParticleVariable> particleVariableList = null;
	private Geometry resampledGeometry = null;
	private boolean bHasNoSurface = false;
	private int dimension = 1;
	private Set<SubVolume> boundaryXSubVolumes = new HashSet<SubVolume>();
	private Set<SubVolume> boundaryYSubVolumes = new HashSet<SubVolume>();
	private Set<SubVolume> boundaryZSubVolumes = new HashSet<SubVolume>();
	private ArrayList<String> killMolCommands = new ArrayList<String>();
	private boolean bGraphicOpenGL = false;
	private HashMap<MembraneSubDomain, ArrayList<TrianglePanel> > membraneSubdomainTriangleMap = null;
	private ArrayList<ClosestTriangle> closestTriangles = null;
	enum VCellSmoldynKeyword {
		bounding_wall_surface_X,
		bounding_wall_surface_Y,
		bounding_wall_surface_Z,
		bounding_wall_compartment,

		vcellPrintProgress,
		vcellWriteOutput,
		vcellDataProcess,
//		vcellReact1KillMolecules,

		dimension,
		sampleSize,
		numMembraneElements,
		variable,
		membrane,
		volume,

		// high resolution volume samples
		Origin,
		Size,
		VolumeSamples,
		start_highResVolumeSamples,
		end_highResVolumeSamples,
		CompartmentHighResPixelMap,
	}

	private Map<Polygon, MembraneElement> polygonMembaneElementMap = null;
	private CartesianMesh cartesianMesh = null;
	private String baseFileName = null;
	private static final Logger lg = Logger.getLogger(SmoldynFileWriter.class);

public SmoldynFileWriter(PrintWriter pw, boolean bGraphic, String baseName, SimulationTask simTask, boolean bMessaging)
{
	super(pw, simTask, bMessaging);
	this.bGraphicOpenGL = bGraphic;
	baseFileName = baseName;
	this.outputFile = new File(baseFileName + SimDataConstants.SMOLDYN_OUTPUT_FILE_EXTENSION);

	//get user defined random seed. If it doesn't exist, we assign system time (in millisecond) to it.
	SmoldynSimulationOptions smoldynSimulationOptions = simTask.getSimulation().getSolverTaskDescription().getSmoldynSimulationOptions();
	if (smoldynSimulationOptions.getRandomSeed() != null) {
		this.randomSeed = smoldynSimulationOptions.getRandomSeed();
	} else {
		this.randomSeed = System.currentTimeMillis();
	}
	//We add jobindex to the random seed in case there is a parameter scan.
	randomSeed = randomSeed + simTask.getSimulationJob().getJobIndex();
	dist.reSeed(randomSeed);
}

private void writeMeshFile() throws SolverException {
	FileOutputStream fos = null;
	try {
		polygonMembaneElementMap = new HashMap<Polygon, MembraneElement>();
		cartesianMesh = CartesianMesh.createSimpleCartesianMesh(resampledGeometry, polygonMembaneElementMap);
		//Write Mesh file
		File meshFile = new File(baseFileName + SimDataConstants.MESHFILE_EXTENSION);
		fos = new FileOutputStream(meshFile);
		cartesianMesh.write(new PrintStream(fos));
	} catch (Exception e) {
		e.printStackTrace(System.out);
		throw new SolverException(e.getMessage());
	} finally{
		try {
			if(fos != null){
				fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

private void init() throws SolverException {
	simulation = simTask.getSimulation();
	mathDesc = simulation.getMathDescription();
	simulationSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();

	particleVariableList = new ArrayList<ParticleVariable>();
	Variable[] variables = simulationSymbolTable.getVariables();
	for (Variable variable : variables) {
		if (variable instanceof ParticleVariable) {
			if (variable.getDomain() == null) {
				throw new SolverException("Particle Variables are required to be defined in a subdomain using syntax Subdomain::Variable.");
			}
			particleVariableList.add((ParticleVariable)variable);
		}
	}

	// write geometry
	Geometry geometry = mathDesc.getGeometry();
	dimension = geometry.getDimension();
	try {
		// clone and resample geometry
		resampledGeometry = (Geometry) BeanUtils.cloneSerializable(geometry);
		GeometrySurfaceDescription geoSurfaceDesc = resampledGeometry.getGeometrySurfaceDescription();
		ISize newSize = simulation.getMeshSpecification().getSamplingSize();
		geoSurfaceDesc.setVolumeSampleSize(newSize);
		geoSurfaceDesc.updateAll();
		bHasNoSurface = geoSurfaceDesc.getSurfaceClasses() == null || geoSurfaceDesc.getSurfaceClasses().length == 0;
	} catch (Exception e) {
		e.printStackTrace();
		throw new SolverException(e.getMessage());
	}
	if (!bGraphicOpenGL) {
		writeMeshFile();
	}
	colors = Plot2DPanel.generateAutoColor(particleVariableList.size() + resampledGeometry.getGeometrySurfaceDescription().getSurfaceClasses().length, bg, new Integer(5));
}


@Override
public void write(String[] parameterNames) throws ExpressionException, MathException, SolverException, DataAccessException, IOException, ImageException, PropertyVetoException, GeometryException {
	init();
	setupMolecules();

	if (bUseMessaging) {
		writeJms(simulation);
	}
	writeRandomSeed();
	writeSpecies();
	writeDiffusions();
	writeGraphicsOpenGL();
	if(simulation.getSolverTaskDescription().getSmoldynSimulationOptions().isUseHighResolutionSample())
	{
		try{
			writeHighResVolumeSamples();
		}catch(Exception ex)
		{
			ex.printStackTrace(System.out);
			throw new SolverException(ex.getMessage() + "\n" +
		                              "Problem may be solved by disable \'fast mesh sampling\'. It may take much longer time to complete the simulation." + "\n" +
					                  "Select \'Edit Simulation\' -> \'Solver\' -> \'Advanced Solver Options\' -> uncheck \'fast mesh sampling\'.");
		}
	}
	writeSurfaces();
	writeCompartments();
	writeDrifts(); // needs to be after dim=3 in input file (after geometry is written).
	writeReactions();
	writeMolecules();
	writeSimulationTimes();
	writeRuntimeCommands();
	writeSimulationSettings();
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.end_file);
	//SimulationWriter.write(SimulationJobToSmoldyn.convertSimulationJob(simulationJob, outputFile), printWriter, simulationJob);
}

/**
 * lazy create {@link #closestTriangles}
 * @return new or existing
 */
private ArrayList<ClosestTriangle> getClosestTriangle( ) {
	if (closestTriangles  == null) {
		closestTriangles = new ArrayList<>();
	}
	return closestTriangles;
}

/**
 * scan subdomains looking for {@link ParticleInitialConditionCount} on @link {@link MembraneSubDomain}
 * records for later use
 * @throws ExpressionException
 * @throws MathException
 */
private void setupMolecules() throws ExpressionException, MathException{
	// write molecules
	for ( SubDomain sd : mathDesc.getSubDomainCollection()) {
		MembraneSubDomain msd = BeanUtils.downcast(MembraneSubDomain.class, sd);
		if (msd != null) {
			for (ParticleProperties particleProperties :msd.getParticleProperties() ) {
				ArrayList<ParticleInitialCondition> particleInitialConditions = particleProperties.getParticleInitialConditions();
				for (ParticleInitialCondition pic : particleInitialConditions) {
					ParticleInitialConditionCount picc = BeanUtils.downcast(ParticleInitialConditionCount.class, pic);
					if (picc != null && !picc.isUniform()) {
						Variable var = particleProperties.getVariable();
						try {
							Domain vd = var.getDomain();
							if (vd.getName().equals(msd.getName())) {
								getClosestTriangle().add(new ClosestTriangle(picc, msd, this));
							}
						} catch (NotAConstantException e) {
							throw new ExpressionException("Non-constant expression for initial position for " + var.getName());
						}
					}
				}
			}
		}
	}
}

private void writeHighResVolumeSamples() throws SolverException {
	try {
		printWriter.println("# HighResVolumeSamples");
		printWriter.println(VCellSmoldynKeyword.start_highResVolumeSamples);

		Origin origin = resampledGeometry.getOrigin();
		Extent extent = resampledGeometry.getExtent();
		int numSamples = 10000000;
		ISize sampleSize = GeometrySpec.calulateResetSamplingSize(3, extent, numSamples);
		VCImage vcImage = RayCaster.sampleGeometry(resampledGeometry, sampleSize, true);

		printWriter.println(VCellSmoldynKeyword.Origin + " " + origin.getX() + " " + origin.getY() + " " + origin.getZ());
		printWriter.println(VCellSmoldynKeyword.Size + " " + extent.getX() + " " + extent.getY() + " " + extent.getZ());
		printWriter.println(VCellSmoldynKeyword.CompartmentHighResPixelMap + " " + resampledGeometry.getGeometrySpec().getNumSubVolumes());
		VCPixelClass[] pixelclasses = vcImage.getPixelClasses();
		if(pixelclasses != null &&  resampledGeometry.getGeometrySpec().getSubVolumes()!= null
		  && pixelclasses.length != resampledGeometry.getGeometrySpec().getSubVolumes().length)
		{
			throw new SolverException("Fast mesh sampling failed. Found " + pixelclasses.length +" of " + resampledGeometry.getGeometrySpec().getSubVolumes().length + " volume domains.\n");
		}
		for (SubVolume subVolume : resampledGeometry.getGeometrySpec().getSubVolumes()) {
			for(VCPixelClass vcPixelClass : pixelclasses )
			{
				if(vcPixelClass.getPixel() == subVolume.getHandle())
				{
					printWriter.println(subVolume.getName() + " " + vcPixelClass.getPixel());
					break;
				}
			}
		}

		printWriter.println(VCellSmoldynKeyword.VolumeSamples + " " + sampleSize.getX() + " " + sampleSize.getY() + " " + sampleSize.getZ());

		if (vcImage != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DeflaterOutputStream dos = new DeflaterOutputStream(bos);
			byte[] pixels = vcImage.getPixels();
			dos.write(pixels, 0, pixels.length);
			dos.close();
			byte[] compressedPixels = bos.toByteArray();
			String compressedStr = Hex.toString(compressedPixels);
			int strchar=250;
			int length = compressedStr.length();
			for (int i = 0; i < Math.ceil(length * 1.0 / strchar); ++ i) {
				printWriter.println(compressedStr.substring(i * strchar, Math.min(length, (i+1) * strchar)));
			}
		}
		printWriter.println(VCellSmoldynKeyword.end_highResVolumeSamples);
		printWriter.println();
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
		throw new RuntimeException("Error writing High Resolution Volume Samples: " + ex.getMessage());
	}
}

private void writeRandomSeed() {
	printWriter.println("# Random Seed");
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.rand_seed + " " + randomSeed);
	printWriter.println();
}


private void writeSimulationSettings() {
	printWriter.println("# simulation settings");
	SmoldynSimulationOptions smoldynSimulationOptions = simulation.getSolverTaskDescription().getSmoldynSimulationOptions();
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.accuracy + " " + smoldynSimulationOptions.getAccuracy());
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.gauss_table_size + " " + smoldynSimulationOptions.getGaussianTableSize());

	printWriter.println();
}

private void writeGraphicsOpenGL() throws MathException {
	if (!bGraphicOpenGL) {
		return;
	}
	printWriter.println("# graphics command");
	//uncomment for debug
	//writeGraphicsLegend();
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.graphics + " " + SmoldynVCellMapper.SmoldynKeyword.opengl);

	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.frame_thickness + " 3");
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.frame_color + " 0.8 0.9 0.0");
//	printWriter.println(SmoldynKeyword.grid_thickness + " 1");
//	printWriter.println(SmoldynKeyword.grid_color + " 0 0 0");
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.text_display + " " + SmoldynVCellMapper.SmoldynKeyword.time);
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.text_color + " 1 1 1");
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.background_color + " " + bg.getRed()/255.0 + " " + bg.getGreen()/255.0 + " " + bg.getBlue()/255.0);
	for (int i = 0; i < particleVariableList.size(); i ++) {
		Color c = colors[i];
		String variableName = getVariableName(particleVariableList.get(i),null);
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.color + " " + variableName + "(" + SmoldynVCellMapper.SmoldynKeyword.all + ") " + c.getRed()/255.0 + " " + c.getGreen()/255.0 + " " + c.getBlue()/255.0);
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.display_size + " " + variableName  + "(" + SmoldynVCellMapper.SmoldynKeyword.all + ") 3");
	}
	printWriter.println();
}

//uncomment for debug
/*private void writeGraphicsLegend() throws MathException{
	try {
		java.awt.image.BufferedImage cmapImage = new java.awt.image.BufferedImage(200, particleVariableList.size()*30,java.awt.image.BufferedImage.TYPE_INT_RGB);
		Graphics g = cmapImage.getGraphics();
		for (int i = 0; i < particleVariableList.size(); i ++) {
			Color c = colors[i];
			System.out.println("color for legend: " + "red--"+ c.getRed() + "  green--" + c.getGreen() + "  blue--" + c.getBlue());
			String variableName = getVariableName(particleVariableList.get(i),null);
			g.setColor(c);
			g.drawString(variableName, 5, 30*i + 20);
			g.fillRect(105, 30*i + 10, 20, 10);
		}
		g.dispose();
		File tmpFile = File.createTempFile("legend", ".jpg");

		FileOutputStream fios = null;
		try {
			printWriter.println("# legend file: " + tmpFile.getAbsolutePath());
			fios = new FileOutputStream(tmpFile);
			ImageIO.write(cmapImage,"jpg",fios);
		}  finally {
			if(fios != null) {fios.close();}
		}
	} catch (Exception e) {
		e.printStackTrace();
		throw new MathException(e.getMessage());
	}
}*/

private void writeRuntimeCommands() throws SolverException, DivideByZeroException, DataAccessException, IOException, MathException, ExpressionException {
	printWriter.println("# " + SmoldynVCellMapper.SmoldynKeyword.killmolincmpt + " runtime command to kill molecules misplaced during initial condtions");

	for (ParticleVariable pv : particleVariableList) {
		CompartmentSubDomain varDomain = mathDesc.getCompartmentSubDomain(pv.getDomain().getName());
		if (varDomain == null) {
			continue;
		}
		boolean bkillMol = false;
		ArrayList<ParticleInitialCondition> iniConditionList = varDomain.getParticleProperties(pv).getParticleInitialConditions();
		for(ParticleInitialCondition iniCon:iniConditionList)
		{
			if(iniCon instanceof ParticleInitialConditionConcentration)
			{
				try{
					subsituteFlattenToConstant(((ParticleInitialConditionConcentration) iniCon).getDistribution());
				}
				catch(Exception e)//can not be evaluated to a constant
				{
					bkillMol = true;
					break;
				}
			}
		}
		if(bkillMol)
		{
			Enumeration<SubDomain> subDomainEnumeration = mathDesc.getSubDomains();
			while (subDomainEnumeration.hasMoreElements()) {
				SubDomain subDomain = subDomainEnumeration.nextElement();
				if (subDomain instanceof CompartmentSubDomain && varDomain != subDomain) {
					printWriter.println(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.B + " " + SmoldynVCellMapper.SmoldynKeyword.killmolincmpt + " " + pv.getName() + "(" + SmoldynVCellMapper.SmoldynKeyword.all + ") " + subDomain.getName());
				}
			}
		}
	}
	printWriter.println();

	//write command to kill molecules on membrane for adsortption to nothing
	printWriter.println("# kill membrane molecues that are absorbed (to nothing)");
	for(String killMolCmd : killMolCommands)
	{
		printWriter.println(killMolCmd);
	}
	printWriter.println();

	printWriter.println("# runtime command");
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.E + " " + VCellSmoldynKeyword.vcellPrintProgress);
	if (outputFile != null && cartesianMesh != null) {
		OutputTimeSpec ots = simulation.getSolverTaskDescription().getOutputTimeSpec();
		if (ots.isUniform()) {
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.output_files + " " + outputFile.getName());
			ISize sampleSize = simulation.getMeshSpecification().getSamplingSize();
			TimeStep timeStep = simulation.getSolverTaskDescription().getTimeStep();
			int n = (int)Math.round(((UniformOutputTimeSpec)ots).getOutputTimeStep()/timeStep.getDefaultTimeStep());
			if(simulation.getSolverTaskDescription().getSmoldynSimulationOptions().isSaveParticleLocations()){
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.N + " " + n + " " + SmoldynVCellMapper.SmoldynKeyword.incrementfile + " " + outputFile.getName());
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.N + " " + n + " " + SmoldynVCellMapper.SmoldynKeyword.listmols + " " + outputFile.getName());
			}

			// DON'T CHANGE THE ORDER HERE.
			// DataProcess must be before vcellWriteOutput
			writeDataProcessor();

			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.N + " " + n + " " + VCellSmoldynKeyword.vcellWriteOutput + " begin");
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.N + " " + n + " " + VCellSmoldynKeyword.vcellWriteOutput + " " + VCellSmoldynKeyword.dimension + " " + dimension);
			printWriter.print(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.N + " " + n + " " + VCellSmoldynKeyword.vcellWriteOutput + " " + VCellSmoldynKeyword.sampleSize + " " + sampleSize.getX());
			if (dimension > 1) {
				printWriter.print(" " + sampleSize.getY());
				if (dimension > 2) {
					printWriter.print(" " + sampleSize.getZ());
				}
			}
			printWriter.println();
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.N + " " + n + " " + VCellSmoldynKeyword.vcellWriteOutput + " " + VCellSmoldynKeyword.numMembraneElements + " " + cartesianMesh.getNumMembraneElements());
			for (ParticleVariable pv : particleVariableList) {
				String type = pv instanceof MembraneParticleVariable ? VCellSmoldynKeyword.membrane.name() : VCellSmoldynKeyword.volume.name();
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.N + " " + n + " " + VCellSmoldynKeyword.vcellWriteOutput + " " + VCellSmoldynKeyword.variable + " " + pv.getName() + " " + type + " " + pv.getDomain().getName());
			}
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.N + " " + n + " " + VCellSmoldynKeyword.vcellWriteOutput + " end");
		} else {
			throw new SolverException(SolverDescription.Smoldyn.getDisplayLabel() + " only supports uniform output.");
		}
	}
	printWriter.println();
}

private void writeDataProcessor() throws DataAccessException, IOException, MathException, DivideByZeroException, ExpressionException {
	Simulation simulation = simTask.getSimulation();
	DataProcessingInstructions dpi = simulation.getDataProcessingInstructions();
	if (dpi == null) {
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.B + " " + VCellSmoldynKeyword.vcellDataProcess + " begin " + DataProcessingInstructions.ROI_TIME_SERIES);
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.B + " " + VCellSmoldynKeyword.vcellDataProcess + " end");
	} else {
		FieldDataIdentifierSpec fdis = dpi.getSampleImageFieldData(simulation.getVersion().getOwner());
		if (fdis == null) {
			throw new DataAccessException("Can't find sample image in data processing instructions");
		}
		File userDirectory = outputFile.getParentFile();

		String secondarySimDataDir = PropertyLoader.getProperty(PropertyLoader.secondarySimDataDirProperty, null);
		DataSetControllerImpl dsci = new DataSetControllerImpl(new NullSessionLog(),null,userDirectory.getParentFile(),secondarySimDataDir == null ? null : new File(secondarySimDataDir));
		CartesianMesh origMesh = dsci.getMesh(fdis.getExternalDataIdentifier());
		SimDataBlock simDataBlock = dsci.getSimDataBlock(null,fdis.getExternalDataIdentifier(), fdis.getFieldFuncArgs().getVariableName(), fdis.getFieldFuncArgs().getTime().evaluateConstant());
		VariableType varType = fdis.getFieldFuncArgs().getVariableType();
		VariableType dataVarType = simDataBlock.getVariableType();
		if (!varType.equals(VariableType.UNKNOWN) && !varType.equals(dataVarType)) {
			throw new IllegalArgumentException("field function variable type (" + varType.getTypeName() + ") doesn't match real variable type (" + dataVarType.getTypeName() + ")");
		}
		double[] origData = simDataBlock.getData();
		String filename = SimulationJob.createSimulationJobID(Simulation.createSimulationID(simulation.getKey()), simTask.getSimulationJob().getJobIndex()) + SimulationData.getDefaultFieldDataFileNameForSimulation(fdis.getFieldFuncArgs());

		File fdatFile = new File(userDirectory, filename);


		DataSet.writeNew(fdatFile,
				new String[] {fdis.getFieldFuncArgs().getVariableName()},
				new VariableType[]{simDataBlock.getVariableType()},
				new ISize(origMesh.getSizeX(),origMesh.getSizeY(),origMesh.getSizeZ()),
				new double[][]{origData});
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.B + " " + VCellSmoldynKeyword.vcellDataProcess + " begin " + dpi.getScriptName());
		StringTokenizer st = new StringTokenizer(dpi.getScriptInput(), "\n\r");
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			if (str.trim().length() > 0) {
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.B + " " + VCellSmoldynKeyword.vcellDataProcess + " " + str);
			}
		}
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.B + " " + VCellSmoldynKeyword.vcellDataProcess + " SampleImageFile " + fdis.getFieldFuncArgs().getVariableName() + " " + fdis.getFieldFuncArgs().getTime().infix() + " " + fdatFile);
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.cmd + " " + SmoldynVCellMapper.SmoldynKeyword.B + " " + VCellSmoldynKeyword.vcellDataProcess + " end");
	}
}

private void writeReactions() throws ExpressionException, MathException {
	printWriter.println("# reactions");
	Enumeration<SubDomain> subdomains = mathDesc.getSubDomains();
	while(subdomains.hasMoreElements()) {
		SubDomain subdomain = subdomains.nextElement();
		for(ParticleJumpProcess pjp : subdomain.getParticleJumpProcesses()) {
			ArrayList<Variable> reactants = new ArrayList<Variable>();
			ArrayList<Variable> products = new ArrayList<Variable>();
			for(Action a : pjp.getActions().toArray(new Action [pjp.getActions().size()])) {
				if(a.getOperation().equals(Action.ACTION_CREATE)) {
					products.add(a.getVar());
				} else if(a.getOperation().equals(Action.ACTION_DESTROY)) {
					reactants.add(a.getVar());
				}
			}
			Expression rateDefinition = null;
			JumpProcessRateDefinition jprd = pjp.getParticleRateDefinition();

			if(jprd instanceof MacroscopicRateConstant) {
				rateDefinition = subsituteFlatten(((MacroscopicRateConstant) jprd).getExpression());
			} else if(jprd instanceof InteractionRadius) {
				rateDefinition = subsituteFlatten(((InteractionRadius) jprd).getExpression());
			} else {
				new RuntimeException("The jump process rate definition is not supported");
			}


			if (rateDefinition.isZero()) {
				continue;
			}
			if (mathDesc.isSpatialHybrid()) {
				String symbols[] = rateDefinition.getSymbols();
				if(symbols != null)
				{
					if(subdomain instanceof MembraneSubDomain)
					{
						rateDefinition = new Expression(FiniteVolumeFileWriter.replaceVolumeVariable(getSimulationTask(), (MembraneSubDomain)subdomain, rateDefinition));
					}
				}
			}else{
				try {
					rateDefinition.evaluateConstant();
				} catch (ExpressionException ex) {
					throw new ExpressionException("reaction rate for jump process " + pjp.getName() + " is not a constant. Constants are required for all reaction rates.");
				}
			}
			//if the reaction rate is not 0, means we are going to run the simulations
			//Smoldyn takes maximum 2nd order reaction.
			if (reactants.size() > 2)
			{
				throw new MathException("VCell spatial stochastic models support up to 2nd order reactions. \n" + "The reaction:" + pjp.getName() + " has more than 2 reactants.");
			}
			if (products.size() > 2)
			{
				throw new MathException("VCell spatial stochastic models support up to 2nd order reactions. \n" + "The reaction:" + pjp.getName() + " has more than 2 products.");
			}

			String rateDefinitionStr = simulation.getMathDescription().isSpatialHybrid() ? rateDefinition.infix() + ";" : rateDefinition.evaluateConstant() + "";
			if(subdomain instanceof CompartmentSubDomain)
			{
				//0th order reaction, product limited to one and we'll let the reaction know where it happens
				if(reactants.size() == 0 && products.size() == 1)
				{
					printWriter.print(SmoldynVCellMapper.SmoldynKeyword.reaction_cmpt + " " + subdomain.getName() + " " + pjp.getName() + " ");
				}
				else{
					printWriter.print(SmoldynVCellMapper.SmoldynKeyword.reaction + " "/* + subdomain.getName() + " "*/ + pjp.getName() + " ");
				}
				writeReactionCommand(reactants, products, subdomain, rateDefinitionStr);
			} else if (subdomain instanceof MembraneSubDomain){
				//0th order reaction, product limited to one and it can be on mem or in vol
				if(reactants.size() == 0 && products.size() == 1)
				{
					printWriter.print(SmoldynVCellMapper.SmoldynKeyword.reaction_surface + " " + subdomain.getName() + " " + pjp.getName() + " ");
					writeReactionCommand(reactants, products, subdomain, rateDefinitionStr);
				}
				//consuming of a species to nothing, limited to one reactant
				else if(reactants.size() == 1 && products.size() == 0)
				{
					if(getMembraneVariableCount(reactants) == 1)//consuming a mem species in mem reaction
					{
						printWriter.print(SmoldynVCellMapper.SmoldynKeyword.reaction_surface + " " + subdomain.getName() + " " + pjp.getName() + " ");
						writeReactionCommand(reactants, products, subdomain, rateDefinitionStr);
					}
					//consuming a vol spcies in mem reaction
					//it equals to adsorption, species A from vol adsorbed to mem as again species A, and then we kill the speceis A on mem.
					else if(getVolumeVariableCount(reactants) == 1)
					{
						writeRateTransitionCommand(reactants, products, subdomain, rateDefinitionStr);
						String speciesName = reactants.get(0).getName();
						String killMolCmd = "cmd " + SmoldynVCellMapper.SmoldynKeyword.E + " " + SmoldynVCellMapper.SmoldynKeyword.killmol + " " + speciesName + "(up)";
						killMolCommands.add(killMolCmd);
					}
				}
				//
				// Use rate command for any membrane reactions with 1 reactant and 1 product
				else if ((reactants.size() == 1) && (products.size() == 1))
				{
					//Membrane reaction (1 react to 1 product).
					if(getMembraneVariableCount(products) == 1 && getMembraneVariableCount(reactants) == 1)
					{
						printWriter.print(SmoldynVCellMapper.SmoldynKeyword.reaction_surface + " " + subdomain.getName() + " " + pjp.getName() + " ");
						writeReactionCommand(reactants, products, subdomain, rateDefinitionStr);
					}
					else//Other single molecular reactions
					{
						writeRateTransitionCommand(reactants, products, subdomain, rateDefinitionStr);
					}
				}
				else //membrane reactions which are not one to one, or 0th order, or consuming species
				{
					if((getMembraneVariableCount(reactants) == 1)) // membrane reaction has one membrane bound reactant
					{
						printWriter.print(SmoldynVCellMapper.SmoldynKeyword.reaction_surface + " " + subdomain.getName() + " " + pjp.getName() + " ");
						writeReactionCommand(reactants, products, subdomain, rateDefinitionStr);
					}
					else if(getMembraneVariableCount(reactants) == 2)  // bimolecular membrane reaction
					{
						if(jprd instanceof InteractionRadius)
						{
							printWriter.print(SmoldynVCellMapper.SmoldynKeyword.reaction_surface + " " + subdomain.getName() + " " + pjp.getName() + " ");
							writeReactionByInteractionRadius(reactants, products, subdomain, rateDefinitionStr, pjp.getName());
						}
						else
						{
							// throw new MathException("Error with reaction: " + pjp.getName() + ".\nVCell Spatial stochastic modeling requires macroscopic or microscopic kinetics for bimolecular membrane reactions.");
							printWriter.print(SmoldynVCellMapper.SmoldynKeyword.reaction_surface + " " + subdomain.getName() + " " + pjp.getName() + " ");
							writeReactionCommand(reactants, products, subdomain, rateDefinitionStr);
						}
					}
					else if(getMembraneVariableCount(reactants) == 0)
					{
						throw new MathException("Error with reaction: " + pjp.getName() + ".\nIn VCell spatial stochastic modeling, the membrane reaction requires at least one membrane bound reactant.");
					}
				}
			}
		}
	}
	printWriter.println();

}

private void writeReactionCommand(List<Variable> reacts, List<Variable> prods, SubDomain subdomain, String rateConstantStr) throws MathException, DivideByZeroException, ExpressionException
{
	if (reacts.size() == 0) {
		printWriter.print(0);
	} else {
		// find state for each molecule ... (up) for membrane, (fsoln) for front soluble, (bsoln) for back soluble
		printWriter.print(getVariableName(reacts.get(0),subdomain));
		for (int i = 1; i < reacts.size(); i ++) {
			printWriter.print(" + " + getVariableName(reacts.get(i),subdomain));
		}
	}
	printWriter.print(" -> ");
	// products
	if (prods.size() == 0) {
		printWriter.print(0);
	} else {
		printWriter.print(getVariableName(prods.get(0),subdomain));
		for (int i = 1; i < prods.size(); i ++) {
			printWriter.print(" + " + getVariableName(prods.get(i),subdomain));
		}
	}

	printWriter.println(" " + rateConstantStr); //rateConstantStr don't need a ";" behind, since it is the last parameter
}


private void writeReactionByInteractionRadius(List<Variable> reacts, List<Variable> prods, SubDomain subdomain, String interactionRadius, String reactionName) throws MathException
{
	if (reacts.size() == 0) {
		printWriter.print(0);
	} else {
		// find state for each molecule ... (up) for membrane, (fsoln) for front soluble, (bsoln) for back soluble
		printWriter.print(getVariableName(reacts.get(0),subdomain));
		for (int i = 1; i < reacts.size(); i ++) {
			printWriter.print(" + " + getVariableName(reacts.get(i),subdomain));
		}
	}
	printWriter.print(" -> ");
	// products
	if (prods.size() == 0) {
		printWriter.print(0);
	} else {
		printWriter.print(getVariableName(prods.get(0),subdomain));
		for (int i = 1; i < prods.size(); i ++) {
			printWriter.print(" + " + getVariableName(prods.get(i),subdomain));
		}
	}
	//not rate constant is printed. go to next line.
	printWriter.println();
	//print binding radius to override smoldyn auto-calculated radius.
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.binding_radius + " " + reactionName + " " + interactionRadius);
}

//used to write molecule transition rate command when it interacts with surface, the possible states can be
//reflection, transmission, adsorption, desorption
private void writeRateTransitionCommand(List<Variable> reacts, List<Variable> prods, SubDomain subdomain, String rateConstantStr) throws MathException
{
	printWriter.print(SmoldynVCellMapper.SmoldynKeyword.surface + " " + subdomain.getName() + " " + SmoldynVCellMapper.SmoldynKeyword.rate + " ");

	//Transmission. Membrane reaction/flux with species in inside and outside membrane solutions
	//e.g. "surface c_n_membrane rate s7_c fsoln bsoln 0.830564784 s8_n", "surface c_n_membrane rate s8_n bsoln fsoln 0.415282392 s7_c",
	if((getVolumeVariableCount(prods) == 1) && (getVolumeVariableCount(reacts) == 1))
	{

		printWriter.print(reacts.get(0).getName() + " ");
		if(getVariableName(reacts.get(0),subdomain).indexOf(SmoldynVCellMapper.SmoldynKeyword.fsoln.name()) > -1)
		{
			printWriter.print(SmoldynVCellMapper.SmoldynKeyword.fsoln + " " + SmoldynVCellMapper.SmoldynKeyword.bsoln + " ");
		}
		else if(getVariableName(reacts.get(0),subdomain).indexOf(SmoldynVCellMapper.SmoldynKeyword.bsoln.name()) > -1)
		{
			printWriter.print(SmoldynVCellMapper.SmoldynKeyword.bsoln + " " + SmoldynVCellMapper.SmoldynKeyword.fsoln + " ");
		}
		printWriter.print(rateConstantStr + " ");
		printWriter.println(prods.get(0).getName());
	}
	//Desorption. Membrane reaction with reactants on membrane and products in either inside or outside membrane solution, 0th order desorption doesn't work in the way
	//e.g. "surface c_n_membrane rate B2 front fsoln 4.22 C2"
	else if(getVolumeVariableCount(prods) == 1 && getMembraneVariableCount(reacts) == 1)
	{
		printWriter.print(reacts.get(0).getName() + " ");
		if(getVariableName(prods.get(0),subdomain).indexOf(SmoldynVCellMapper.SmoldynKeyword.fsoln.name()) > -1)
		{
			printWriter.print(SmoldynVCellMapper.SmoldynKeyword.up + " " + SmoldynVCellMapper.SmoldynKeyword.fsoln + " ");
		}
		else if(getVariableName(prods.get(0),subdomain).indexOf(SmoldynVCellMapper.SmoldynKeyword.bsoln.name()) > -1)
		{
			printWriter.print(SmoldynVCellMapper.SmoldynKeyword.up + " " + SmoldynVCellMapper.SmoldynKeyword.bsoln + " ");
		}
		printWriter.print(rateConstantStr + " ");
		printWriter.println(prods.get(0).getName());
	}
	//Adsorption. Membrane reaction with reactants in either inside or outside membrane solution and products are adsorbed on membrane
	//e.g. "surface c_n_membrane rate B2 fsoln front 4.22 C2"
	else if((getVolumeVariableCount(reacts) == 1) && ((getMembraneVariableCount(prods) == 1) || (prods.size() == 0)))
	{
		printWriter.print(reacts.get(0).getName() + " ");
		if(getVariableName(reacts.get(0),subdomain).indexOf(SmoldynVCellMapper.SmoldynKeyword.fsoln.name()) > -1)
		{
			printWriter.print(SmoldynVCellMapper.SmoldynKeyword.fsoln + " " + SmoldynVCellMapper.SmoldynKeyword.up + " ");
		}
		else if(getVariableName(reacts.get(0),subdomain).indexOf(SmoldynVCellMapper.SmoldynKeyword.bsoln.name()) > -1)
		{
			printWriter.print(SmoldynVCellMapper.SmoldynKeyword.bsoln + " " + SmoldynVCellMapper.SmoldynKeyword.up + " ");
		}
		printWriter.print(rateConstantStr + " ");
		if(prods.size() == 1)
		{
			printWriter.println(prods.get(0).getName());
		}
		else
		{
			printWriter.println(reacts.get(0).getName());
		}
	}
}

private int  getMembraneVariableCount(List<Variable> variables)
{
	int count = 0;
	for(Variable var : variables)
	{
		if(var instanceof MembraneParticleVariable)
		{
			count++;
		}
	}
	return count;
}

private int getVolumeVariableCount(List<Variable> variables)
{
	int count = 0;
	for(Variable var : variables)
	{
		if(var instanceof VolumeParticleVariable)
		{
			count++;
		}
	}
	return count;
}

private String getVariableName(Variable var, SubDomain subdomain) throws MathException {
	String name = var.getName( );
	if (subdomain instanceof MembraneSubDomain){
		MembraneSubDomain membrane = (MembraneSubDomain)subdomain;
		if (var.getDomain().getName().equals(membrane.getName())){
			return SmoldynVCellMapper.vcellToSmoldyn(name, SmoldynVCellMapper.MAP_PARTICLE_TO_MEMBRANE);
		}else if (membrane.getInsideCompartment().getName().equals(var.getDomain().getName())){
			return SmoldynVCellMapper.vcellToSmoldyn(name, SmoldynKeyword.bsoln);
		}else if (membrane.getOutsideCompartment().getName().equals(var.getDomain().getName())){
			return SmoldynVCellMapper.vcellToSmoldyn(name, SmoldynKeyword.fsoln);
		}else{
			throw new MathException("variable "+var.getQualifiedName()+" cannot be in a reaction on non-adjacent membrane "+subdomain.getName());
		}
	}else{
		return name;
	}
}

private int writeInitialConcentration(ParticleInitialConditionConcentration initialConcentration, SubDomain subDomain, Variable variable, String variableName, StringBuilder sb) throws ExpressionException, MathException {
	SimpleSymbolTable simpleSymbolTable = new SimpleSymbolTable(new String[]{ReservedVariable.X.getName(), ReservedVariable.Y.getName(), ReservedVariable.Z.getName()});
	Expression disExpression = new Expression(initialConcentration.getDistribution());
	disExpression.bindExpression(simulationSymbolTable);
	disExpression = simulationSymbolTable.substituteFunctions(disExpression).flatten();
	disExpression.bindExpression(simpleSymbolTable);
	double values[] = new double[3];
	if (dimension == 1) {
		if (disExpression.getSymbolBinding(ReservedVariable.Y.getName()) != null
				|| disExpression.getSymbolBinding(ReservedVariable.Z.getName()) != null) {
			throw new MathException(VCellErrorMessages.getSmoldynWrongCoordinates("'y' or 'z'", dimension, variable, disExpression));
		}
	} else if (dimension == 2) {
		if (disExpression.getSymbolBinding(ReservedVariable.Z.getName()) != null) {
			throw new MathException(VCellErrorMessages.getSmoldynWrongCoordinates("'z'", dimension, variable, disExpression));
		}
	}

	int totalCount = 0;
	StringBuilder localsb = new StringBuilder();
	if (subDomain instanceof CompartmentSubDomain) {
		MeshSpecification meshSpecification = simulation.getMeshSpecification();
		ISize sampleSize = meshSpecification.getSamplingSize();
		int numX = sampleSize.getX();
		int numY = dimension < 2 ? 1 : sampleSize.getY();
		int numZ = dimension < 3 ? 1 : sampleSize.getZ();
		boolean bCellCentered = simulation.hasCellCenteredMesh();
		double dx = meshSpecification.getDx(bCellCentered);
		double dy = meshSpecification.getDy(bCellCentered);
		double dz = meshSpecification.getDz(bCellCentered);
		Origin origin = resampledGeometry.getGeometrySpec().getOrigin();
		double ox = origin.getX();
		double oy = origin.getY();
		double oz = origin.getZ();
		Extent extent = resampledGeometry.getExtent();
		double ex = extent.getX();
		double ey = extent.getY();
		double ez = extent.getZ();

		int offset = 0;
		for (int k = 0; k < numZ; k ++) {
			double centerz = oz + k * dz;
			double loz = Math.max(oz, centerz - dz/2);
			double hiz = Math.min(oz + ez, centerz + dz/2);
			double lz = hiz - loz;
			values[2] = centerz;
			for (int j = 0; j < numY; j ++) {
				double centery = oy + j * dy;
				double loy = Math.max(oy, centery - dy/2);
				double hiy = Math.min(oy + ey, centery + dy/2);
				values[1] = centery;
				double ly = hiy - loy;
				for (int i = 0; i < numX; i ++) {
					int regionIndex = resampledGeometry.getGeometrySurfaceDescription().getRegionImage().getRegionInfoFromOffset(offset).getRegionIndex();
					offset ++;
					GeometricRegion region = resampledGeometry.getGeometrySurfaceDescription().getGeometricRegions(regionIndex);
					if (region instanceof VolumeGeometricRegion) {
						if (!((VolumeGeometricRegion) region).getSubVolume().getName().equals(subDomain.getName())) {
							continue;
						}
					}
					double centerx = ox + i * dx;
					double lox = Math.max(ox, centerx - dx/2);
					double hix = Math.min(ox + ex, centerx + dx/2);
					double lx = hix - lox;
					values[0] = centerx;

					double volume = lx;
					if (dimension > 1) {
						volume *= ly;
						if (dimension > 2) {
							volume *= lz;
						}
					}
					double expectedCount = disExpression.evaluateVector(values) * volume;
					if (expectedCount <= 0) {
						continue;
					}
					long count = dist.nextPoisson(expectedCount);
					if (count <= 0) {
						continue;
					}
					totalCount += count;
					localsb.append(SmoldynVCellMapper.SmoldynKeyword.mol + " " + count + " " + variableName + " " + (float)lox + "-" + (float)hix);
					if (lg.isTraceEnabled()) {
						lg.trace("Component subdomain " + variableName + " count " + count);
					}
					if (dimension > 1) {
						localsb.append(" " + loy + "-" + hiy);

						if (dimension > 2) {
							localsb.append(" " + loz + "-" + hiz);
						}
					}
					localsb.append("\n");
				}
			}
		}

		//decide what to append to the string buffer, if concentration can be evaluated to a constant, we append the uniform molecule count.
		//otherwise we append the distributed molecules in different small boxes
		try{
			subsituteFlattenToConstant(disExpression);
			sb.append(SmoldynVCellMapper.SmoldynKeyword.compartment_mol);
			sb.append(" " + totalCount + " " + variableName + " " + subDomain.getName() + "\n");
		}
		catch(Exception e)//can not be evaluated to a constant
		{
			sb.append(localsb);
		}

	} else if (subDomain instanceof MembraneSubDomain) {
		ArrayList<TrianglePanel> trianglePanelList = membraneSubdomainTriangleMap.get(subDomain);
		for (TrianglePanel trianglePanel : trianglePanelList) {
			Triangle triangle = trianglePanel.triangle;
			switch (dimension) {
			case 1:
				values[0] = triangle.getNodes(0).getX();
				break;
			case 2: {
				double centroidX = triangle.getNodes(0).getX();
				double centroidY = triangle.getNodes(0).getY();

				if (triangle.getNodes(0).getX() == triangle.getNodes(1).getX() && triangle.getNodes(0).getY() == triangle.getNodes(1).getY()) {
					centroidX += triangle.getNodes(2).getX();
					centroidY += triangle.getNodes(2).getY();
				} else {
					centroidX += triangle.getNodes(1).getX();
					centroidY += triangle.getNodes(1).getY();
				}
				values[0] = centroidX / 2;
				values[1] = centroidY / 2;
				break;
			}
			case 3: {
				double centroidX = triangle.getNodes(0).getX() + triangle.getNodes(1).getX() + triangle.getNodes(2).getX();
				double centroidY = triangle.getNodes(0).getY() + triangle.getNodes(1).getY() + triangle.getNodes(2).getY();
				double centroidZ = triangle.getNodes(0).getZ() + triangle.getNodes(1).getZ() + triangle.getNodes(2).getZ();
				values[0] = centroidX / 3;
				values[1] = centroidY / 3;
				values[2] = centroidZ / 3;
				break;
			}
			}
			double expectedCount = disExpression.evaluateVector(values) * triangle.getArea();
			if (expectedCount <= 0) {
				continue;
			}
			long count = dist.nextPoisson(expectedCount);
			if (count <= 0) {
				continue;
			}
			totalCount += count;
			if (lg.isTraceEnabled()) {
				lg.trace("Membrane subdomain " + subDomain.getName( ) + ' ' + variableName + " count " + count);
			}
			localsb.append(SmoldynVCellMapper.SmoldynKeyword.surface_mol + " " + count + " " + variableName + " " + subDomain.getName() + " "
					+ SmoldynVCellMapper.SmoldynKeyword.tri + " " + trianglePanel.name + "\n");
		}

		//decide what to append to the string buffer, if concentration can be evaluated to a constant, we append the uniform molecule count.
		//otherwise we append the distributed molecules in different small boxes
		try{
			subsituteFlattenToConstant(disExpression);
			sb.append(SmoldynVCellMapper.SmoldynKeyword.surface_mol);
			sb.append(" " + totalCount + " " + variableName + " " + subDomain.getName() + " " + SmoldynVCellMapper.SmoldynKeyword.all + " " + SmoldynVCellMapper.SmoldynKeyword.all + "\n");
		}
		catch(Exception e)//can not be evaluated to a constant
		{
			sb.append(localsb);
		}

	}

	if (lg.isDebugEnabled( )) {
		lg.debug("Subdomain " + subDomain.getName( ) + ' ' + variableName + " total count " + totalCount);
	}
	return totalCount;
}


private int writeInitialCount(ParticleInitialConditionCount initialCount, SubDomain subDomain, String variableName, StringBuilder sb) throws ExpressionException, MathException {
	int count = 0;
	try {
		count = (int) subsituteFlattenToConstant(initialCount.getCount());
	} catch (NotAConstantException ex) {
		String errMsg = "\n" +
						"Initial count for variable " + variableName + " is not a constant. Spatial stochastic simulation requires constant value for initial count.\n" +
						"If you want to set variable initial condition as a function of time or space, please select application ->specifications ->species and choose initial condition to 'Concentration'.";
		throw new ExpressionException(errMsg);
	}
	if (count > 0) {
		final boolean isCompartment = subDomain instanceof CompartmentSubDomain;
		if (initialCount.isUniform()) {
			// here count has to split between all compartments
			if (isCompartment) {
				sb.append(SmoldynVCellMapper.SmoldynKeyword.compartment_mol);
				sb.append(" " + count + " " + variableName + " " + subDomain.getName() + "\n");
			} else if (subDomain instanceof MembraneSubDomain) {
				sb.append(SmoldynVCellMapper.SmoldynKeyword.surface_mol);
				sb.append(" " + count + " " + variableName + " " + subDomain.getName() + " " + SmoldynVCellMapper.SmoldynKeyword.all + " " + SmoldynVCellMapper.SmoldynKeyword.all + "\n");
			}
		} else {
			if (isCompartment) {
				sb.append(SmoldynVCellMapper.SmoldynKeyword.mol + " " + count + " " + variableName);
				if (lg.isTraceEnabled()) {
					lg.trace("initial count for compartment " + subDomain.getName() + ' ' + variableName  + " is " + count);
				}
				try {
					if (initialCount.isXUniform()) {
						sb.append(" " + initialCount.getLocationX().infix());
					} else {
						double locX = subsituteFlattenToConstant(initialCount.getLocationX());
						sb.append(" " + locX);
					}
					if (dimension > 1) {
						if (initialCount.isYUniform()) {
							sb.append(" " + initialCount.getLocationY().infix());
						} else {
							double locY = subsituteFlattenToConstant(initialCount.getLocationY());
							sb.append(" " + locY);
						}
						if (dimension > 2) {
							if (initialCount.isZUniform()) {
								sb.append(" " + initialCount.getLocationZ().infix());
							} else {
								double locZ = subsituteFlattenToConstant(initialCount.getLocationZ());
								sb.append(" " + locZ);
							}
						}
					}
				} catch (NotAConstantException ex) {
					throw new ExpressionException("location for variable " + variableName + " is not a constant. Constants are required for all locations");
				}

				sb.append('\n');
			}
			else if (subDomain instanceof MembraneSubDomain) {
				//closestTriangles should have been allocated in setupMolecules if this condition exists
				for (ClosestTriangle ct : closestTriangles) {
					if (ct.picc == initialCount) {
						VCAssert.assertTrue(ct.membrane == subDomain, "wrong subdomain");
						final char space = ' ';
						sb.append(SmoldynVCellMapper.SmoldynKeyword.surface_mol);
						sb.append(space);
						sb.append(count);
						sb.append(space);
						sb.append(variableName);
						sb.append(space);
						sb.append(subDomain.getName());
						sb.append(" tri "); //pshape, always triangle for us
						sb.append(ct.triPanel.name);
						sb.append(space);
						sb.append(ct.node.getX());
						if (dimension > 1) {
							sb.append(space);
							sb.append(ct.node.getY());
						}
						if (dimension > 2) {
							sb.append(space);
							sb.append(ct.node.getZ());
						}
						sb.append('\n');
						if (lg.isTraceEnabled()) {
							lg.trace("initial count for " + subDomain.getName() + ' ' + variableName  + " is " + count);
						}
						return count;
					}
				}
				throw new ProgrammingException("unable to find " + variableName + " in closest triangles" );
			}
		}
	}
	return count;
}

private void writeMolecules() throws ExpressionException, MathException {
	// write molecules
	StringBuilder sb = new StringBuilder();
	int max_mol = 0;
	Enumeration<SubDomain> subDomainEnumeration = mathDesc.getSubDomains();
	while (subDomainEnumeration.hasMoreElements()) {
		SubDomain subDomain = subDomainEnumeration.nextElement();

		for (ParticleProperties particleProperties : subDomain.getParticleProperties()) {
			ArrayList<ParticleInitialCondition> particleInitialConditions = particleProperties.getParticleInitialConditions();
			String variableName = getVariableName(particleProperties.getVariable(),subDomain);
			for (ParticleInitialCondition pic : particleInitialConditions) {
				if (pic instanceof ParticleInitialConditionCount) {
					max_mol += writeInitialCount((ParticleInitialConditionCount)pic, subDomain, variableName, sb);
				} else if (pic instanceof ParticleInitialConditionConcentration) {
					max_mol += writeInitialConcentration((ParticleInitialConditionConcentration)pic, subDomain, particleProperties.getVariable(), variableName, sb);
				}
			}
			if (lg.isDebugEnabled( )) {
				lg.debug("subdomain " + subDomain.getName() + ' ' + variableName + " processed, maximum mol estimate now " + max_mol);
			}
		}
	}

	if (max_mol > MAX_MOLECULE_LIMIT) {
		throw new MathException(VCellErrorMessages.getSmoldynMaxMolReachedErrorMessage((long)max_mol, MAX_MOLECULE_LIMIT));
	}

	int max_adjusted = max_mol * MOLECULE_MAX_COEFFICIENT;
	if (max_adjusted < MIN_MOLECULE_LIMIT) {
		if (lg.isInfoEnabled()) {
			lg.info("adjusting computed max " + max_adjusted + " to minimum " + MIN_MOLECULE_LIMIT);
		}
		max_adjusted = MIN_MOLECULE_LIMIT;
	}
	if (max_adjusted > MAX_MOLECULE_LIMIT) {
		if (lg.isInfoEnabled()) {
			lg.info("adjusting computed max " + max_adjusted + " to maximum " + MAX_MOLECULE_LIMIT); 
		}
		max_adjusted = MAX_MOLECULE_LIMIT;
	}
	printWriter.println("# molecules");
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.max_mol + " " + max_adjusted); 
	printWriter.println(sb);
}

private void writeSimulationTimes() {
	// write simulation times
	TimeBounds timeBounds = simulation.getSolverTaskDescription().getTimeBounds();
	TimeStep timeStep = simulation.getSolverTaskDescription().getTimeStep();
	printWriter.println("# simulation times");
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.time_start + " " + timeBounds.getStartingTime());
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.time_stop + " " + timeBounds.getEndingTime());
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.time_step + " " + timeStep.getDefaultTimeStep());
	printWriter.println();
}

private class SelectPoint {
	int j, k;
	int starti, endi;

	public SelectPoint(int starti, int endi, int j, int k) {
		super();
		this.starti = starti;
		this.endi = endi;
		this.j = j;
		this.k = k;
	}
	@Override
	public String toString() {
		return "([" + starti + ", " + endi + "], " + j + ", " + k + ")";
	}

	public int length() {
		return endi - starti + 1;
	}

	public boolean contains(SelectPoint point) {
		return point.starti > starti && point.endi <= endi || point.starti >= starti && point.endi < endi;
	}

	public boolean same(SelectPoint point) {
		return point.starti == starti && point.endi == endi || point.starti == starti && point.endi == endi;
	}
}

private void writeSurfaces() throws SolverException, ImageException, PropertyVetoException, GeometryException, ExpressionException {

	GeometrySurfaceDescription geometrySurfaceDescription = resampledGeometry.getGeometrySurfaceDescription();

	SurfaceClass[] surfaceClasses = geometrySurfaceDescription.getSurfaceClasses();
	GeometrySpec geometrySpec = resampledGeometry.getGeometrySpec();
	SubVolume[] surfaceGeometrySubVolumes = geometrySpec.getSubVolumes();

	GeometricRegion[] AllGeometricRegions = resampledGeometry.getGeometrySurfaceDescription().getGeometricRegions();
	ArrayList<SurfaceGeometricRegion> surfaceRegionList = new ArrayList<SurfaceGeometricRegion>();
	ArrayList<VolumeGeometricRegion> volumeRegionList = new ArrayList<VolumeGeometricRegion>();
	for (GeometricRegion geometricRegion : AllGeometricRegions) {
		if (geometricRegion instanceof SurfaceGeometricRegion){
			surfaceRegionList.add((SurfaceGeometricRegion)geometricRegion);
		} else if (geometricRegion instanceof VolumeGeometricRegion){
			volumeRegionList.add((VolumeGeometricRegion)geometricRegion);
		} else {
			throw new SolverException("unsupported geometric region type " + geometricRegion.getClass());
		}
	}
	printWriter.println("# geometry");
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.dim + " " + dimension);
	if (bHasNoSurface) {
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.max_compartment + " " + surfaceGeometrySubVolumes.length);
	} else {
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.max_compartment + " " + (surfaceGeometrySubVolumes.length + 1));
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.max_surface + " " + (surfaceClasses.length + dimension)); // plus the surface which are bounding walls
	}
	printWriter.println();

	// write boundaries and wall surfaces
	writeWallSurfaces();

	// membrane subdomain inside and outside determine the surface normals for Smoldyn
	// VCell surfaces normals already point to exterior geometric region (right hand rule with quads).
	// If the vcell surface normals point to inside compartment subdomain, we flip the vcell surface normals when writing to smoldyn panels.
	// bsoln maps to inside compartment subdomain.
	// fsoln maps to outside compartment subdomain.
	//
	// for 2D ... smoldyn normal convension is (V1-V0).cross.([0 0 1]) points to the outside compartment subdomain.
	// for 3D ... smoldyn normal convension is triangle right-hand-rule normal points to the outside compartment subdomain.
	if (!bHasNoSurface) {
		membraneSubdomainTriangleMap = new HashMap<MembraneSubDomain, ArrayList<TrianglePanel> >();
		// write surfaces
		printWriter.println("# surfaces");
		int triangleGlobalCount = 0;
		int membraneIndex = -1;
		SurfaceCollection surfaceCollection = geometrySurfaceDescription.getSurfaceCollection();

		//pre-allocate collections used repeatedly in following loops; clear before reusing
		HashMap<Node, Set<String>> nodeTriMap = new HashMap<>();
		ArrayList<TrianglePanel> triList = new ArrayList<TrianglePanel>();
		//use a sorted set to ensure neighbors written out is same order for reproducibility
		SortedSet<String> neighborsForCurrentNode = new TreeSet<String>();

		for (int sci = 0; sci < surfaceClasses.length; sci ++) {
			nodeTriMap.clear();
			triList.clear();

			int triLocalCount = 0;
			SurfaceClass surfaceClass = surfaceClasses[sci];
			GeometricRegion[] geometricRegions = geometrySurfaceDescription.getGeometricRegions(surfaceClass);
			for (GeometricRegion gr : geometricRegions) {
				SurfaceGeometricRegion sgr = (SurfaceGeometricRegion)gr;
				VolumeGeometricRegion volRegion0 = (VolumeGeometricRegion)sgr.getAdjacentGeometricRegions()[0];
				VolumeGeometricRegion volRegion1 = (VolumeGeometricRegion)sgr.getAdjacentGeometricRegions()[1];
				SubVolume subVolume0 = volRegion0.getSubVolume();
				SubVolume subVolume1 = volRegion1.getSubVolume();
				CompartmentSubDomain compart0 = mathDesc.getCompartmentSubDomain(subVolume0.getName());
				CompartmentSubDomain compart1 = mathDesc.getCompartmentSubDomain(subVolume1.getName());
				MembraneSubDomain membraneSubDomain = mathDesc.getMembraneSubDomain(compart0, compart1);
				if (membraneSubDomain == null) {
					throw new SolverException(VCellErrorMessages.getSmoldynUnexpectedSurface(compart0, compart1));
				}
				int exteriorRegionID = volRegion0.getRegionID();
				int interiorRegionID = volRegion1.getRegionID();
				if (membraneSubDomain.getInsideCompartment() == compart0) {
					exteriorRegionID = volRegion1.getRegionID();
					interiorRegionID = volRegion0.getRegionID();
				}
				for(int j = 0; j < surfaceCollection.getSurfaceCount(); j++) {
					Surface surface = surfaceCollection.getSurfaces(j);
					if ((surface.getInteriorRegionIndex() == exteriorRegionID && surface.getExteriorRegionIndex() == interiorRegionID) ||
						(surface.getInteriorRegionIndex() == interiorRegionID && surface.getExteriorRegionIndex() == exteriorRegionID)) { // my triangles
//						for(int k = 0; k < surface.getPolygonCount(); k++) {
//							Polygon polygon = surface.getPolygons(k);
						for (Polygon polygon: surface) {
							if (polygonMembaneElementMap != null) {
								membraneIndex = polygonMembaneElementMap.get(polygon).getMembraneIndex();
							}
							Node[] nodes = polygon.getNodes();
							if (dimension == 2){
								// ignore z
								Vect3d unitNormal = new Vect3d();
								polygon.getUnitNormal(unitNormal);
								unitNormal.set(unitNormal.getX(), unitNormal.getY(), 0);
								int point0 = 0;
								Vect3d v0 = new Vect3d(nodes[point0].getX(),nodes[point0].getY(),0);
								int point1 = 1;
								Vect3d v1 = null;
								for (point1 = 1; point1 < nodes.length; point1 ++) {
									if (v0.getX() != nodes[point1].getX() || v0.getY() != nodes[point1].getY()) {
										v1 = new Vect3d(nodes[point1].getX(),nodes[point1].getY(),0);
										break;
									}
								}
								if (v1 == null) {
									throw new RuntimeException("failed to generate surface");
								}
								Vect3d v01 = Vect3d.sub(v1, v0);
								Vect3d unit01n = v01.cross(unitNormal);
								unit01n.unit();
								if (Math.abs(unit01n.getZ()-1.0) < 1e-6){
									// v0 to v1 opposes vcell surface normal. it's already flipped.
									Triangle triangle;
									if (surface.getInteriorRegionIndex() == interiorRegionID) {
										// we have to flipped it back
										triangle = new Triangle(nodes[point1], nodes[point0], null);
									} else {
										triangle = new Triangle(nodes[point0], nodes[point1], null);
									}
									triList.add(new TrianglePanel(triLocalCount ++, triangleGlobalCount ++, membraneIndex, triangle));
								}else if (Math.abs(unit01n.getZ()+1.0) < 1e-6){
									// v0 to v1 is in direction of vcell surface normal.
									Triangle triangle;
									if (surface.getInteriorRegionIndex() == interiorRegionID) {
										triangle = new Triangle(nodes[point0], nodes[point1], null);
									} else {
										triangle = new Triangle(nodes[point1], nodes[point0], null);
									}
									triList.add(new TrianglePanel(triLocalCount ++, triangleGlobalCount ++, membraneIndex, triangle));
								}else {
									throw new RuntimeException("failed to generate surface");
								}
							} else if (dimension == 3) {
								Triangle triangle1;
								Triangle triangle2;
								if (surface.getInteriorRegionIndex() == interiorRegionID) { // interior
									triangle1 = new Triangle(nodes[0], nodes[1], nodes[2]);
									triangle2 = new Triangle(nodes[0], nodes[2], nodes[3]);
								}else{
									triangle1 = new Triangle(nodes[2], nodes[1], nodes[0]);
									triangle2 = new Triangle(nodes[3], nodes[2], nodes[0]);
								}
								triList.add(new TrianglePanel(triLocalCount ++, triangleGlobalCount ++, membraneIndex, triangle1));
								triList.add(new TrianglePanel(triLocalCount ++, triangleGlobalCount ++, membraneIndex, triangle2));
							}
						}
					}
				}
			}
			//add triangles to node hash
			for(TrianglePanel triPanel : triList)
			{
				for(Node node : triPanel.triangle.getNodes())
				{
					if(node == null)
					{
						continue;
					}
					Set<String> triNameSet = nodeTriMap.get(node);
					if(triNameSet == null)
					{
						triNameSet = new HashSet<String>();
						nodeTriMap.put(node, triNameSet);
					}
					triNameSet.add(triPanel.name);
				}
			}


			SubVolume[] adjacentSubvolums = surfaceClass.getAdjacentSubvolumes().toArray(new SubVolume[0]);
			CompartmentSubDomain csd0 = simulation.getMathDescription().getCompartmentSubDomain(adjacentSubvolums[0].getName());
			CompartmentSubDomain csd1 = simulation.getMathDescription().getCompartmentSubDomain(adjacentSubvolums[1].getName());
			MembraneSubDomain membraneSubDomain = simulation.getMathDescription().getMembraneSubDomain(csd0, csd1);
			membraneSubdomainTriangleMap.put(membraneSubDomain, triList);
			final boolean initialMoleculesOnMembrane = (closestTriangles != null );
			if (initialMoleculesOnMembrane) {
				findClosestTriangles(membraneSubDomain, triList);

			}

			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.start_surface + " " + surfaceClass.getName());
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.action + " " + SmoldynVCellMapper.SmoldynKeyword.all + "(" + SmoldynVCellMapper.SmoldynKeyword.all + ") " + SmoldynVCellMapper.SmoldynKeyword.both + " " + SmoldynVCellMapper.SmoldynKeyword.reflect);
//			printWriter.println(SmoldynKeyword.action + " " + SmoldynKeyword.all + "(" + SmoldynKeyword.up + ") " + SmoldynKeyword.both + " " + SmoldynKeyword.reflect);
			Color c = colors[sci+particleVariableList.size()]; //get color after species
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.color + " " +  SmoldynVCellMapper.SmoldynKeyword.both + " " + c.getRed()/255.0 + " " + c.getGreen()/255.0 + " " + c.getBlue()/255.0 + " 0.1");
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.polygon + " " + SmoldynVCellMapper.SmoldynKeyword.front + " " + SmoldynVCellMapper.SmoldynKeyword.edge);
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.polygon + " " + SmoldynVCellMapper.SmoldynKeyword.back + " " + SmoldynVCellMapper.SmoldynKeyword.edge);
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.max_panels + " " + SmoldynVCellMapper.SmoldynKeyword.tri + " " + triList.size());

			for (TrianglePanel trianglePanel : triList) {
				Triangle triangle = trianglePanel.triangle;
				printWriter.print(SmoldynVCellMapper.SmoldynKeyword.panel + " " + SmoldynVCellMapper.SmoldynKeyword.tri);
				switch (dimension) {
				case 1:
					printWriter.print(" " + triangle.getNodes(0).getX());
					break;
				case 2:
					printWriter.print(" " + triangle.getNodes(0).getX() + " " + triangle.getNodes(0).getY());

					printWriter.print(" " + triangle.getNodes(1).getX() + " " + triangle.getNodes(1).getY());
					break;
				case 3:
					for (Node node : triangle.getNodes()) {
						printWriter.print(" " + node.getX() + " " + node.getY() + " " + node.getZ());
					}
					break;
				}

				printWriter.println(" " + trianglePanel.name);
			}

			for(TrianglePanel triPanel : triList)
			{
				neighborsForCurrentNode.clear();
				for(Node node : triPanel.triangle.getNodes())
				{
					if(node == null)
					{
						continue;
					}
					neighborsForCurrentNode.addAll(nodeTriMap.get(node));
				}
				neighborsForCurrentNode.remove(triPanel.name);
				//printWriter.print(SmoldynKeyword.neighbors + " " +triPanel.name);
				int maxNeighborCount = 4; //to allow smoldyn read line length as 256, chop the neighbors to multiple lines
//
				int count = 0;
				for(String neigh:neighborsForCurrentNode)
				{
					if(count%maxNeighborCount == 0)
					{
						printWriter.println();
						printWriter.print(SmoldynVCellMapper.SmoldynKeyword.neighbors + " " + triPanel.name);
					}
					printWriter.print(" "+ neigh);
					count++;
				}

			}
			printWriter.println();
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.end_surface);
			printWriter.println();
		}

		// write compartment
//		printWriter.println("# bounding wall compartment");
//		printWriter.println(SmoldynKeyword.start_compartment + " " + VCellSmoldynKeyword.bounding_wall_compartment);
//		printWriter.println(SmoldynKeyword.surface + " " + VCellSmoldynKeyword.bounding_wall_surface_X);
//		if (dimension > 1) {
//			printWriter.println(SmoldynKeyword.surface + " " + VCellSmoldynKeyword.bounding_wall_surface_Y);
//			if (dimension > 2) {
//				printWriter.println(SmoldynKeyword.surface + " " + VCellSmoldynKeyword.bounding_wall_surface_Z);
//			}
//		}
//		printWriter.println(SmoldynKeyword.end_compartment);
//		printWriter.println();
	}

}


private void writeCompartments() throws ImageException, PropertyVetoException, GeometryException, ExpressionException {
	MeshSpecification meshSpecification = simulation.getMeshSpecification();
	ISize sampleSize = meshSpecification.getSamplingSize();
	int numX = sampleSize.getX();
	int numY = dimension < 2 ? 1 : sampleSize.getY();
	int numZ = dimension < 3 ? 1 : sampleSize.getZ();
	int numXY = numX * numY;
	boolean bCellCentered = simulation.hasCellCenteredMesh();
	double dx = meshSpecification.getDx(bCellCentered);
	double dy = meshSpecification.getDy(bCellCentered);
	double dz = meshSpecification.getDz(bCellCentered);
	Origin origin = resampledGeometry.getGeometrySpec().getOrigin();

	printWriter.println("# compartments");
	resampledGeometry.precomputeAll(new GeometryThumbnailImageFactoryAWT());
	for (SubVolume subVolume : resampledGeometry.getGeometrySpec().getSubVolumes()) {
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.start_compartment + " " + subVolume.getName());
		for (SurfaceClass sc : resampledGeometry.getGeometrySurfaceDescription().getSurfaceClasses()) {
			if (sc.getAdjacentSubvolumes().contains(subVolume)) {
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.surface + " " + sc.getName());
			}
		}
		if (boundaryXSubVolumes.contains(subVolume)) {
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.surface + " " + VCellSmoldynKeyword.bounding_wall_surface_X);
		}
		if (dimension > 1) {
			if (boundaryYSubVolumes.contains(subVolume)) {
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.surface + " " + VCellSmoldynKeyword.bounding_wall_surface_Y);
			}
			if (dimension > 2) {
				if (boundaryZSubVolumes.contains(subVolume)) {
					printWriter.println(SmoldynVCellMapper.SmoldynKeyword.surface + " " + VCellSmoldynKeyword.bounding_wall_surface_Z);
				}
			}
		}

//		if (DEBUG) {
//			tmppw.println("points" + pointsCount + "=[");
//			pointsCount ++;
//		}

		// gather all the points in all the regions
		Geometry interiorPointGeometry = RayCaster.resampleGeometry(new GeometryThumbnailImageFactoryAWT(), resampledGeometry, resampledGeometry.getGeometrySurfaceDescription().getVolumeSampleSize());
		SubVolume interiorPointSubVolume = interiorPointGeometry.getGeometrySpec().getSubVolume(subVolume.getName());
		GeometricRegion[] geometricRegions = interiorPointGeometry.getGeometrySurfaceDescription().getGeometricRegions(interiorPointSubVolume);
		RegionInfo[] regionInfos = interiorPointGeometry.getGeometrySurfaceDescription().getRegionImage().getRegionInfos();
		for (GeometricRegion geometricRegion : geometricRegions) {
			VolumeGeometricRegion volumeGeometricRegion = (VolumeGeometricRegion)geometricRegion;

			ArrayList<SelectPoint> selectPointList = new ArrayList<SelectPoint>();
			for (RegionInfo regionInfo : regionInfos) {
				if (regionInfo.getRegionIndex() != volumeGeometricRegion.getRegionID()) {
					continue;
				}
				int volIndex = 0;
				for (int k = 0; k < numZ; k ++){
					for (int j = 0; j < numY; j ++){
						int starti = -1;
						int endi = 0;
						for (int i = 0; i < numX; i ++, volIndex ++){
							boolean bInRegion = false;
							if (regionInfo.isIndexInRegion(volIndex)) {
								bInRegion = true;
								if (starti == -1) {
									starti = i;
									endi = i;
								} else {
									endi ++;
								}
							}

							if ((!bInRegion || i == numX - 1) && starti != -1) {
								int midi = (endi + starti) / 2;
								int midVolIndex = k * numXY + j * numX + midi;
								boolean bOnBoundary = false;
								int neighbors[] = {
										j == 0 ? -1 : midVolIndex - numX,
										j == numY - 1 ? -1 : midVolIndex + numX,
										k == 0 ? -1 : midVolIndex - numXY,
										k == numZ - 1 ? -1 : midVolIndex + numXY,
								};
								for (int n = 0; n < 2 * (dimension - 1); n ++) {
									if (neighbors[n] == -1 || !regionInfo.isIndexInRegion(neighbors[n])) {
										bOnBoundary = true;
										break;
									}
								}

								if (!bOnBoundary) {
									selectPointList.add(new SelectPoint(starti, endi, j, k));
								}
								starti = -1;
							}
						} // end i
					} // end j
				} // end k
			} // end for (RegionInfo

			for (int m = 0; m < selectPointList.size(); m ++) {
				SelectPoint thisPoint = selectPointList.get(m);
				boolean bPrint = true;
				for (int n = 0; n < selectPointList.size(); n ++) {
					if (n == m) {
						continue;
					}
					SelectPoint point = selectPointList.get(n);
					if (thisPoint.k == point.k && Math.abs(thisPoint.j - point.j) == 1
							|| thisPoint.j == point.j && Math.abs(thisPoint.k - point.k) == 1) {
						if (point.same(thisPoint) && m < n) { // found same one later, print the later one
							bPrint = false;
							break;
						}
						if (point.contains(thisPoint) && point.length() < 2 * thisPoint.length()) { // found a longer one, but not too much longer
							bPrint = false;
							break;
						}
					}
				}
				if (bPrint) {
					int midi = (thisPoint.starti + thisPoint.endi) / 2;
					double coordX = origin.getX() + dx * midi;
					printWriter.print(SmoldynVCellMapper.SmoldynKeyword.point + " " + coordX);
					if (dimension > 1) {
						double coordY = origin.getY() + dy * thisPoint.j;
						printWriter.print(" " + coordY);
						if (dimension > 2) {
							double coordZ = origin.getZ() + dz * thisPoint.k;
							printWriter.print(" " + coordZ);
						}
					}
					printWriter.println();
				}
			}
		} // end for (GeometricRegion

		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.end_compartment);
		printWriter.println();
	} // end for (SubVolume
}

private void writeWallSurfaces() throws SolverException {
	GeometrySurfaceDescription geometrySurfaceDescription = resampledGeometry.getGeometrySurfaceDescription();
	GeometrySpec geometrySpec = resampledGeometry.getGeometrySpec();
	SubVolume[] subVolumes = geometrySpec.getSubVolumes();

	printWriter.println("# boundaries");
	Origin origin = geometrySpec.getOrigin();
	Extent extent = geometrySpec.getExtent();
	Coordinate lowWall = new Coordinate(origin.getX(), origin.getY(), origin.getZ());
	Coordinate highWall = new Coordinate(origin.getX() + extent.getX(), origin.getY() + extent.getY(), origin.getZ() + extent.getZ());
//	These boundaries of the entire system are different from surfaces, which are
//	described below. However, they have enough in common that Smoldyn does not work
//	well with both at once. Thus, if any surfaces are used, the system boundaries will always
//	behave as though the types are transparent, whether they are defined that way or not.
//	Thus, if there are surfaces, it is usually best to use the boundaries statement without a
//	type parameter, which will lead to the default transparent type. To account for the
//	transparent boundaries, an outside surface may be needed that keeps molecules within the
//	system. The one exception to these suggestions arises for systems with both surfaces and
//	periodic boundary conditions. To accomplish this with the maximum accuracy, set the
//	boundary types to periodic (although they will behave as though they are transparent) and
//	create jump type surfaces, described below, at each outside edge that send molecules to
//	the far sides. The reason for specifying that the boundaries are periodic is that they will
//	then allow bimolecular reactions that occur with one molecule on each side of the system.
//	This will probably yield a negligible improvement in results, but nevertheless removes a
//	potential artifact.
	if (bHasNoSurface) {
		SubDomain subDomain0 = mathDesc.getSubDomains().nextElement();
		CompartmentSubDomain compartSubDomain0 = null;
		compartSubDomain0 = (CompartmentSubDomain)subDomain0;
		// x
		if (compartSubDomain0.getBoundaryConditionXm().isPERIODIC()) {
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.boundaries + " 0 " + lowWall.getX() + " " + highWall.getX() + " " + SmoldynVCellMapper.SmoldynKeyword.p);
		} else {
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.low_wall + " 0 " + lowWall.getX() + " " + (compartSubDomain0.getBoundaryConditionXm().isNEUMANN() ? SmoldynVCellMapper.SmoldynKeyword.r : SmoldynVCellMapper.SmoldynKeyword.a));
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.high_wall + " 0 " + highWall.getX() + " " + (compartSubDomain0.getBoundaryConditionXp().isNEUMANN() ? SmoldynVCellMapper.SmoldynKeyword.r : SmoldynVCellMapper.SmoldynKeyword.a));
		}
		if (dimension > 1) {
			// y
			if (compartSubDomain0.getBoundaryConditionYm().isPERIODIC()) {
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.boundaries + " 1 " + lowWall.getY() + " " + highWall.getY() + " " + SmoldynVCellMapper.SmoldynKeyword.p);
			} else {
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.low_wall + " 1 " + lowWall.getY() + " " + (compartSubDomain0.getBoundaryConditionYm().isNEUMANN() ? SmoldynVCellMapper.SmoldynKeyword.r : SmoldynVCellMapper.SmoldynKeyword.a));
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.high_wall + " 1 " + highWall.getY() + " " + (compartSubDomain0.getBoundaryConditionYp().isNEUMANN() ? SmoldynVCellMapper.SmoldynKeyword.r : SmoldynVCellMapper.SmoldynKeyword.a));
			}

			if (dimension > 2) {
				// z
				if (compartSubDomain0.getBoundaryConditionZm().isPERIODIC()) {
					printWriter.println(SmoldynVCellMapper.SmoldynKeyword.boundaries + " 2 " + lowWall.getZ() + " " + highWall.getZ() + " " + SmoldynVCellMapper.SmoldynKeyword.p);
				} else {
					printWriter.println(SmoldynVCellMapper.SmoldynKeyword.low_wall + " 2 " + lowWall.getZ() + " " + (compartSubDomain0.getBoundaryConditionZm().isNEUMANN() ? SmoldynVCellMapper.SmoldynKeyword.r : SmoldynVCellMapper.SmoldynKeyword.a));
					printWriter.println(SmoldynVCellMapper.SmoldynKeyword.high_wall + " 2 " + highWall.getZ() + " " + (compartSubDomain0.getBoundaryConditionZp().isNEUMANN() ? SmoldynVCellMapper.SmoldynKeyword.r : SmoldynVCellMapper.SmoldynKeyword.a));
				}
			}
		}
		printWriter.println();
	} else {
		// x
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.boundaries + " 0 " + lowWall.getX() + " " + highWall.getX());
		if (dimension > 1) {
			// y
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.boundaries + " 1 " + lowWall.getY() + " " + highWall.getY());
			if (dimension > 2) {
				// z
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.boundaries + " 2 " + lowWall.getZ() + " " + highWall.getZ());
			}
		}
		printWriter.println();

		// bounding walls as surfaces

		// have to find boundary condition type
		ISize sampleSize = simulation.getMeshSpecification().getSamplingSize();
		int numX = sampleSize.getX();
		int numY = dimension < 2 ? 1 : sampleSize.getY();
		int numZ = dimension < 3 ? 1 : sampleSize.getZ();

		if (dimension > 2) {
			int[] k_wall = new int[] {0, numZ - 1};
			for (int k = 0; k < k_wall.length; k ++) {
				for (int j = 0; j < numY; j ++) {
					for (int i = 0; i < numX; i ++) {
						int volIndex = k_wall[k] * numX * numY + j * numX + i;

						for (SubVolume sv : subVolumes) {
							// gather all the points in all the regions
							GeometricRegion[] geometricRegions = geometrySurfaceDescription.getGeometricRegions(sv);
							RegionInfo[] regionInfos = geometrySurfaceDescription.getRegionImage().getRegionInfos();
							for (GeometricRegion gr : geometricRegions) {
								VolumeGeometricRegion vgr = (VolumeGeometricRegion)gr;
								for (RegionInfo ri : regionInfos) {
									if (ri.getRegionIndex() == vgr.getRegionID() && ri.isIndexInRegion(volIndex)) {
										boundaryZSubVolumes.add(sv);
									}
								}
							}
						}
					}
				}
			}
		}

		if (dimension > 1) {
			int[] j_wall = new int[] {0, numY - 1};
			for (int k = 0; k < numZ; k ++) {
				for (int j = 0; j < j_wall.length; j ++) {
					for (int i = 0; i < numX; i ++) {
						int volIndex = k * numX * numY + j_wall[j] * numX + i;

						for (SubVolume sv : subVolumes) {
							// gather all the points in all the regions
							GeometricRegion[] geometricRegions = geometrySurfaceDescription.getGeometricRegions(sv);
							RegionInfo[] regionInfos = geometrySurfaceDescription.getRegionImage().getRegionInfos();
							for (GeometricRegion gr : geometricRegions) {
								VolumeGeometricRegion vgr = (VolumeGeometricRegion)gr;
								for (RegionInfo ri : regionInfos) {
									if (ri.getRegionIndex() == vgr.getRegionID() && ri.isIndexInRegion(volIndex)) {
										boundaryYSubVolumes.add(sv);
									}
								}
							}
						}
					}
				}
			}
		}

		int[] i_wall = new int[] {0, numX - 1};
		for (int k = 0; k < numZ; k ++) {
			for (int j = 0; j < numY; j ++) {
				for (int i = 0; i < i_wall.length; i ++) {
					int volIndex = k * numX * numY + j * numX + i_wall[i];

					for (SubVolume sv : subVolumes) {
						// gather all the points in all the regions
						GeometricRegion[] geometricRegions = geometrySurfaceDescription.getGeometricRegions(sv);
						RegionInfo[] regionInfos = geometrySurfaceDescription.getRegionImage().getRegionInfos();
						for (GeometricRegion gr : geometricRegions) {
							VolumeGeometricRegion vgr = (VolumeGeometricRegion)gr;
							for (RegionInfo ri : regionInfos) {
								if (ri.getRegionIndex() == vgr.getRegionID() && ri.isIndexInRegion(volIndex)) {
									boundaryXSubVolumes.add(sv);
								}
							}
						}
					}
				}
			}
		}

		Set<SubVolume> boundarySubVolumes = new HashSet<SubVolume>();
		boundarySubVolumes.addAll(boundaryXSubVolumes);
		boundarySubVolumes.addAll(boundaryYSubVolumes);
		boundarySubVolumes.addAll(boundaryZSubVolumes);
		BoundaryConditionType[] computedBct = new BoundaryConditionType[dimension * 2];
		String[] smoldynBct = new String[dimension * 2];
		String[] wallNames = new String[] {"Xm", "Xp", "Ym", "Yp", "Zm", "Zp"};
		if (boundarySubVolumes.size() >= 1) {
			for (SubVolume sv : boundarySubVolumes) {
				CompartmentSubDomain csd = (CompartmentSubDomain)mathDesc.getSubDomain(sv.getName());
				BoundaryConditionType bct[] = new BoundaryConditionType[] {
					csd.getBoundaryConditionXm(),
					csd.getBoundaryConditionXp(),
					csd.getBoundaryConditionYm(),
					csd.getBoundaryConditionYp(),
					csd.getBoundaryConditionZm(),
					csd.getBoundaryConditionZp(),
				};

				if (computedBct[0] == null) {
					System.arraycopy(bct, 0, computedBct, 0, dimension * 2);
					for (int i = 0; i < dimension * 2; i ++) {
						if (computedBct[i].isPERIODIC()) {
							throw new SolverException("Models with both surfaces and periodic boundary conditions are not supported yet.");
						}
						smoldynBct[i] = computedBct[i].isDIRICHLET() ? SmoldynVCellMapper.SmoldynKeyword.absorb.name() : SmoldynVCellMapper.SmoldynKeyword.reflect.name();
					}
				} else {
					for (int i = 0; i < dimension * 2; i ++) {
						if (!computedBct[i].compareEqual(bct[i])) {
							throw new SolverException(wallNames[i] + " wall has different boundary conditions");
						}
					}
				}
			}
		}

		printWriter.println("# bounding wall surface");
		// X walls
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.start_surface + " " + VCellSmoldynKeyword.bounding_wall_surface_X);
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.action + " " + SmoldynVCellMapper.SmoldynKeyword.all + "(" + SmoldynVCellMapper.SmoldynKeyword.up + ") " + SmoldynVCellMapper.SmoldynKeyword.both + " " + SmoldynVCellMapper.SmoldynKeyword.reflect);
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.action + " " + SmoldynVCellMapper.SmoldynKeyword.all + " " + SmoldynVCellMapper.SmoldynKeyword.front + " " + smoldynBct[0]);
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.action + " " + SmoldynVCellMapper.SmoldynKeyword.all + " " + SmoldynVCellMapper.SmoldynKeyword.back + " " + smoldynBct[1]);
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.color + " " + SmoldynVCellMapper.SmoldynKeyword.both + " 1 1 1");
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.polygon + " " + SmoldynVCellMapper.SmoldynKeyword.both + " " + SmoldynVCellMapper.SmoldynKeyword.edge);
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.max_panels + " " + SmoldynVCellMapper.SmoldynKeyword.rect + " 2");
		// yz walls
		switch (dimension) {
		case 1:
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.panel + " " + SmoldynVCellMapper.SmoldynKeyword.rect + " +0 " + lowWall.getX());
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.panel + " " + SmoldynVCellMapper.SmoldynKeyword.rect + " -0 " + highWall.getX());
			break;
		case 2:
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.panel + " " + SmoldynVCellMapper.SmoldynKeyword.rect + " +0 " + lowWall.getX() + " " + lowWall.getY() + " " + extent.getY());
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.panel + " " + SmoldynVCellMapper.SmoldynKeyword.rect + " -0 " + highWall.getX() + " " + lowWall.getY() + " " + extent.getY());
			break;
		case 3:
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.panel + " " + SmoldynVCellMapper.SmoldynKeyword.rect + " +0 " + lowWall.getX() + " " + lowWall.getY() + " " + lowWall.getZ() + " " + extent.getY() + " " + extent.getZ());
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.panel + " " + SmoldynVCellMapper.SmoldynKeyword.rect + " -0 " + highWall.getX() + " " + lowWall.getY() + " " + lowWall.getZ() + " " + extent.getY() + " " + extent.getZ());
			break;
		}
		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.end_surface);
		printWriter.println();

		if (dimension > 1) {
			// Y walls
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.start_surface + " " + VCellSmoldynKeyword.bounding_wall_surface_Y);
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.action + " " + SmoldynVCellMapper.SmoldynKeyword.all + "(" + SmoldynVCellMapper.SmoldynKeyword.up + ") " + SmoldynVCellMapper.SmoldynKeyword.both + " " + SmoldynVCellMapper.SmoldynKeyword.reflect);
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.action + " " + SmoldynVCellMapper.SmoldynKeyword.all + " " + SmoldynVCellMapper.SmoldynKeyword.front + " " + smoldynBct[2]);
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.action + " " + SmoldynVCellMapper.SmoldynKeyword.all + " " + SmoldynVCellMapper.SmoldynKeyword.back + " " + smoldynBct[3]);
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.color + " " + SmoldynVCellMapper.SmoldynKeyword.both + " 1 1 1");
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.polygon + " " + SmoldynVCellMapper.SmoldynKeyword.both + " " + SmoldynVCellMapper.SmoldynKeyword.edge);
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.max_panels + " " + SmoldynVCellMapper.SmoldynKeyword.rect + " 2");
			// xz walls
			switch (dimension) {
			case 2:
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.panel + " " + SmoldynVCellMapper.SmoldynKeyword.rect + " +1 " + lowWall.getX() + " " + lowWall.getY() + " " + extent.getX());
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.panel + " " + SmoldynVCellMapper.SmoldynKeyword.rect + " -1 " + lowWall.getX() + " " + highWall.getY() + " " + extent.getX());
				break;
			case 3:
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.panel + " " + SmoldynVCellMapper.SmoldynKeyword.rect + " +1 " + lowWall.getX() + " " + lowWall.getY() + " " + lowWall.getZ() + " " + extent.getX() + " " + extent.getZ());
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.panel + " " + SmoldynVCellMapper.SmoldynKeyword.rect + " -1 " + lowWall.getX() + " " + highWall.getY() + " " + lowWall.getZ() + " " + extent.getX() + " " + extent.getZ());
				break;
			}
			printWriter.println(SmoldynVCellMapper.SmoldynKeyword.end_surface);
			printWriter.println();

			if (dimension > 2) {
				// Z walls
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.start_surface + " " + VCellSmoldynKeyword.bounding_wall_surface_Z);
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.action + " " + SmoldynVCellMapper.SmoldynKeyword.all + "(" + SmoldynVCellMapper.SmoldynKeyword.up + ") " + SmoldynVCellMapper.SmoldynKeyword.both + " " + SmoldynVCellMapper.SmoldynKeyword.reflect);
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.action + " " + SmoldynVCellMapper.SmoldynKeyword.all + " " + SmoldynVCellMapper.SmoldynKeyword.front + " " + smoldynBct[4]);
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.action + " " + SmoldynVCellMapper.SmoldynKeyword.all + " " + SmoldynVCellMapper.SmoldynKeyword.back + " " + smoldynBct[5]);
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.color + " " + SmoldynVCellMapper.SmoldynKeyword.both + " 1 1 1");
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.polygon + " " + SmoldynVCellMapper.SmoldynKeyword.both + " " + SmoldynVCellMapper.SmoldynKeyword.edge);
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.max_panels + " " + SmoldynVCellMapper.SmoldynKeyword.rect + " 2");
				// xy walls
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.panel + " " + SmoldynVCellMapper.SmoldynKeyword.rect + " +2 " + lowWall.getX() + " " + lowWall.getY() + " " + lowWall.getZ() + " " + extent.getX() + " " + extent.getY());
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.panel + " " + SmoldynVCellMapper.SmoldynKeyword.rect + " -2 " + lowWall.getX() + " " + lowWall.getY() + " " + highWall.getZ() + " " + extent.getX() + " " + extent.getY());
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.end_surface);
				printWriter.println();
			}
		}
	}
}

double subsituteFlattenToConstant(Expression exp) throws MathException, NotAConstantException, ExpressionException {
	Expression newExp = subsituteFlatten(exp);
	try {
		return newExp.evaluateConstant();
	} catch (ExpressionException ex) {
		throw new NotAConstantException();
	}
}

private Expression subsituteFlatten(Expression exp) throws MathException, ExpressionException {
	Expression newExp = new Expression(exp);
	newExp.bindExpression(simulationSymbolTable);
	newExp = simulationSymbolTable.substituteFunctions(newExp).flatten();
	return newExp;
}

private void writeDiffusions() throws ExpressionBindingException,
		ExpressionException, MathException {
	// writer diffusion properties
	printWriter.println("# diffusion properties");
	Enumeration<SubDomain> subDomainEnumeration = mathDesc.getSubDomains();
	while (subDomainEnumeration.hasMoreElements()) {
		SubDomain subDomain = subDomainEnumeration.nextElement();
		List<ParticleProperties> particlePropertiesList = subDomain.getParticleProperties();
		for (ParticleProperties pp : particlePropertiesList) {
			String variableName = null;
			if(subDomain instanceof MembraneSubDomain)
			{
				variableName = getVariableName(pp.getVariable(), subDomain);
			}
			else
			{
				variableName = getVariableName(pp.getVariable(), null);
			}
			try {
				double diffConstant = subsituteFlattenToConstant(pp.getDiffusion());
				printWriter.println(SmoldynVCellMapper.SmoldynKeyword.difc + " " + variableName + " " + diffConstant);
			} catch (NotAConstantException ex) {
				throw new ExpressionException("diffusion coefficient for variable " + variableName + " is not a constant. Constants are required for all diffusion coefficients");
			}
		}
	}
	printWriter.println();
}

private void writeDrifts() throws ExpressionBindingException,
ExpressionException, MathException {
// writer diffusion properties
printWriter.println("# drift properties");
Enumeration<SubDomain> subDomainEnumeration = mathDesc.getSubDomains();
while (subDomainEnumeration.hasMoreElements()) {
SubDomain subDomain = subDomainEnumeration.nextElement();
List<ParticleProperties> particlePropertiesList = subDomain.getParticleProperties();
for (ParticleProperties pp : particlePropertiesList) {
	String variableName = null;
	if(subDomain instanceof MembraneSubDomain)
	{
		variableName = getVariableName(pp.getVariable(), subDomain);
	}
	else
	{
		variableName = getVariableName(pp.getVariable(), null);
	}
	try {
		double driftX = 0.0;
		if (pp.getDriftX()!=null){
			driftX = subsituteFlattenToConstant(pp.getDriftX());
		}

		double driftY = 0.0;
		if (pp.getDriftY()!=null){
			driftY = subsituteFlattenToConstant(pp.getDriftY());
		}

		double driftZ = 0.0;
		if (pp.getDriftZ()!=null){
			driftZ = subsituteFlattenToConstant(pp.getDriftZ());
		}

		printWriter.println(SmoldynVCellMapper.SmoldynKeyword.drift + " " + variableName + " " + driftX + " " + driftY + " " + driftZ);
	} catch (NotAConstantException ex) {
		throw new ExpressionException("diffusion coefficient for variable " + variableName + " is not a constant. Constants are required for all diffusion coefficients");
	}
}
}
printWriter.println();
}

private void writeSpecies() throws MathException {
	// write species
	printWriter.println("# species declarations");
	printWriter.println(SmoldynVCellMapper.SmoldynKeyword.max_species + " " + (particleVariableList.size() + 1));
	printWriter.print(SmoldynVCellMapper.SmoldynKeyword.species);
	for (ParticleVariable pv : particleVariableList) {
		printWriter.print(" " + getVariableName(pv,null));
	}
	printWriter.println();
	printWriter.println();
}

private void writeJms(Simulation simulation) {
	if (simTask != null) {
		printWriter.println("# JMS_Paramters");
		printWriter.println("start_jms");
		printWriter.println(PropertyLoader.getRequiredProperty(PropertyLoader.jmsProvider) + " " + PropertyLoader.getRequiredProperty(PropertyLoader.jmsURL)
			+ " " + PropertyLoader.getRequiredProperty(PropertyLoader.jmsUser) + " " + PropertyLoader.getRequiredProperty(PropertyLoader.jmsPassword)
			+ " " + VCellQueue.WorkerEventQueue.getName()
			+ " " + VCellTopic.ServiceControlTopic.getName()
			+ " " + simulation.getVersion().getOwner().getName()
			+ " " + simulation.getVersion().getVersionKey()
			+ " " + simTask.getSimulationJob().getJobIndex());
		printWriter.println("end_jms");
		printWriter.println();
	}
}

/**
 * find closest triangles for membrane initial condition
 * @param membraneSubDomain not null
 * @param triList panels to evaluated not null
 */
private void findClosestTriangles(MembraneSubDomain membraneSubDomain, ArrayList<TrianglePanel> triList) {
	Objects.requireNonNull(membraneSubDomain);
	for ( ClosestTriangle ct : closestTriangles) {
		if (ct.membrane == membraneSubDomain) {
			for (TrianglePanel tp : triList) {
				ct.evaluate(tp);
			}
		}
	}
}
/**
 * find and stores information about which {@link TrianglePanel} is closest to a
 * {@link ParticleInitialConditionCount}
 */
private static class ClosestTriangle {
	final ParticleInitialConditionCount picc;
	final MembraneSubDomain membrane;
	final Node node;
	double currentNodeDistanceSquared;
	double currentTriangleDistanceSquared;
	TrianglePanel triPanel;

	/**
	 * @param picc not null
	 * @param msb not null
	 * @param sfw not null
	 * @throws MathException
	 * @throws NotAConstantException
	 * @throws ExpressionException
	 */
	ClosestTriangle(ParticleInitialConditionCount picc, MembraneSubDomain msb, SmoldynFileWriter sfw) throws MathException, NotAConstantException, ExpressionException {
		if (picc.isXUniform() || picc.isYUniform() || picc.isZUniform()) {
			throw new ExpressionException("Uniform specifier " + ParticleInitialConditionCount.UNIFORM + " for membrane initial condition not supported");
		}
		this.picc = picc;
		this.membrane = msb;
		double x = sfw.subsituteFlattenToConstant(picc.getLocationX());
		double y = sfw.subsituteFlattenToConstant(picc.getLocationY());
		double z = sfw.subsituteFlattenToConstant(picc.getLocationZ());
		node = new Node(x,y,z);
		triPanel = null;
		currentNodeDistanceSquared = Double.MAX_VALUE;
	}

	/**
	 * see if this panel is closer than ones previously evaluated
	 * @param tp no null
	 */
	void evaluate(TrianglePanel tp) {
		for (Node n : tp.triangle.getNodes()) {
			double ds = node.distanceSquared(n);
			if (ds < currentNodeDistanceSquared) {
				currentNodeDistanceSquared = ds;
				currentTriangleDistanceSquared = tp.triangle.totalDistanceSquared(node);
				triPanel = tp;
			}
			else if (ds == currentNodeDistanceSquared ){
				double tds = tp.triangle.totalDistanceSquared(node);
				if (tds < currentTriangleDistanceSquared) {
					currentTriangleDistanceSquared = tds;
					triPanel = tp;
				}
			}
		}
	}
}



}
