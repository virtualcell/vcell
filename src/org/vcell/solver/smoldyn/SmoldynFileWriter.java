package org.vcell.solver.smoldyn;


import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.math.random.RandomDataImpl;
import org.vcell.util.BeanUtils;
import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.NullSessionLog;
import org.vcell.util.Origin;
import org.vcell.util.PropertyLoader;

import cbit.plot.Plot2DPanel;
import cbit.vcell.client.desktop.biomodel.VCellErrorMessages;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.Node;
import cbit.vcell.geometry.surface.Polygon;
import cbit.vcell.geometry.surface.StlExporter;
import cbit.vcell.geometry.surface.Surface;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.Triangle;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.math.Action;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.MacroscopicRateConstant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.ParticleJumpProcess;
import cbit.vcell.math.ParticleProbabilityRate;
import cbit.vcell.math.ParticleProperties;
import cbit.vcell.math.ParticleProperties.ParticleInitialCondition;
import cbit.vcell.math.ParticleProperties.ParticleInitialConditionConcentration;
import cbit.vcell.math.ParticleProperties.ParticleInitialConditionCount;
import cbit.vcell.math.ParticleVariable;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.messaging.JmsUtils;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.render.Vect3d;
import cbit.vcell.simdata.DataSet;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.DataProcessingInstructions;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverFileWriter;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solvers.CartesianMesh;


/**
 * The function reads model information from simulation and
 * generates the stochastic input file for simulation engine.
 * Creation date: (6/22/2006 4:22:59 PM)
 * @author: Tracy LI
 */
public class SmoldynFileWriter extends SolverFileWriter 
{
	private static final String PANEL_TRIANGLE_NAME_PREFIX = "triangle";

	private static class TrianglePanel {
		String name;
		Triangle triangle;		
		private TrianglePanel(String name, Triangle triangle) {
			super();
			this.name = name;
			this.triangle = triangle;
		}
	}
	private static final int MAX_MOL_LIMIT = 1000000;

	@SuppressWarnings("serial")
	private class NotAConstantException extends Exception {		
	}
	
	private File outputFile = null;
	private Simulation simulation = null;
	private MathDescription mathDesc = null;
	private SimulationSymbolTable simulationSymbolTable = null;
	ArrayList<ParticleVariable> particleVariableList = null;
	private Geometry resampledGeometry = null;
	private boolean bHasNoSurface = false;
	private int dimension = 1;
	private Set<SubVolume> boundaryXSubVolumes = new HashSet<SubVolume>();
	private Set<SubVolume> boundaryYSubVolumes = new HashSet<SubVolume>();
	private Set<SubVolume> boundaryZSubVolumes = new HashSet<SubVolume>();
	private boolean bGraphicOpenGL = false;
	private HashMap<MembraneSubDomain, ArrayList<TrianglePanel> > membraneSubdomainTriangleMap = null;
	
	enum SmoldynKeyword {
		species,
		difc,
		
		graphics,
		opengl,
		color,
		
		time_start,
		time_stop,
		time_step,
		
		dim,
		max_compartment,
		max_surface,
		boundaries,
		low_wall,
		high_wall,
		r,
		a,
		p,
		
		start_surface,
		end_surface,
		action,
		front,
		back,
		both,
		all,
		reflect,
		absorb,
		max_panels,
		panel,
		rect,
		tri,
		
		start_compartment,
		end_compartment,
		surface,
		point,
		
		reaction,
		reaction_cmpt,
		reaction_surface,
		
		max_mol,
		compartment_mol,
		surface_mol,
		mol,
	
//		the possible molecular states
		up,
		down,
//		front,
//		back,
		fsoln,
		bsoln,
//		all,
				
		output_files,
		
		cmd,
		B,
		n,
//		one line of display is printed to the listed file, giving the time and the number 
//		of molecules for each molecular species. Molecule states are ignored. 
//		The ordering used is the same as was given in the species command.
		molcount,
//		This prints out the identity, state, and location of every molecule in the
//		system to the listed file name, using a separate line of text for each
//		molecule.		
		listmols,
//		This is very similar to listmols but has a slightly different output format.
//		Each line of text is preceded by the “time counter”, which is an integer
//		that starts at 1 and is incremented each time the routine is called. Also, the
//		names and states of molecules are not printed, but instead the identity and
//		state numbers are printed.
		listmols2,
		killmoloutsidesystem,
		warnescapee,
		output_file_number,		
		incrementfile,
		
		accuracy,
		boxsize,
		gauss_table_size,
		rand_seed,
		
		end_file,
	}

	enum VCellSmoldynKeyword {
		bounding_wall_surface_X,
		bounding_wall_surface_Y,
		bounding_wall_surface_Z,
		bounding_wall_compartment,
		
		vcellPrintProgress,
		vcellWriteOutput,
		vcellDataProcess,
	}
	
/**
 * StochFileWriter constructor comment.
 */
public SmoldynFileWriter(PrintWriter pw, File outputFile, SimulationJob arg_simulationJob, boolean bMessaging) 
{
	this(pw, false, outputFile, arg_simulationJob, bMessaging);
}

public SmoldynFileWriter(PrintWriter pw, boolean bGraphic, File outputFile, SimulationJob arg_simulationJob, boolean bMessaging) 
{
	super(pw, arg_simulationJob, bMessaging);
	bGraphicOpenGL = bGraphic;
	this.outputFile = outputFile;
}

private void init() throws SolverException {
	simulation = simulationJob.getSimulation();
	mathDesc = simulation.getMathDescription();
	simulationSymbolTable = simulationJob.getSimulationSymbolTable();

	particleVariableList = new ArrayList<ParticleVariable>();
	Variable[] variables = simulationSymbolTable.getVariables();
	for (Variable variable : variables) {
		if (variable instanceof ParticleVariable) {
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
}


@Override
public void write(String[] parameterNames) throws ExpressionException, MathException, SolverException, DataAccessException, IOException {	
	init();
	
	if (bUseMessaging) {
		writeJms(simulation);
	}
	writeSpecies();	
	writeDiffusions();
	writeGraphicsOpenGL();
	writeSurfacesAndCompartments();	
	writeReactions();
	writeMolecules();	
	writeSimulationTimes();
	writeRuntimeCommands();
	writeSimulationSettings();
	printWriter.println(SmoldynKeyword.end_file);
	//SimulationWriter.write(SimulationJobToSmoldyn.convertSimulationJob(simulationJob, outputFile), printWriter, simulationJob);
}

private void writeSimulationSettings() {
	printWriter.println("# simulation settings");
	SmoldynSimulationOptions smoldynSimulationOptions = simulation.getSolverTaskDescription().getSmoldynSimulationOptions();
	printWriter.println(SmoldynKeyword.accuracy + " " + smoldynSimulationOptions.getAccuracy());
	printWriter.println(SmoldynKeyword.gauss_table_size + " " + smoldynSimulationOptions.getGaussianTableSize());
	if (smoldynSimulationOptions.getRandomSeed() != null) {
		printWriter.println(SmoldynKeyword.rand_seed + " " + smoldynSimulationOptions.getRandomSeed());
	}
	printWriter.println();
}

private void writeGraphicsOpenGL() throws MathException {
	if (!bGraphicOpenGL) {
		return;
	}
	printWriter.println("# runtime command");	
	printWriter.println(SmoldynKeyword.graphics + " " + SmoldynKeyword.opengl);
	Color[] colors = Plot2DPanel.generateAutoColor(particleVariableList.size(), Color.white, new Integer(5));
	for (int i = 0; i < particleVariableList.size(); i ++) {
		Color c = colors[i];
		printWriter.println(SmoldynKeyword.color + " " + getVariableName(particleVariableList.get(i),null) + " " + c.getRed()/255.0 + " " + c.getGreen()/255.0 + " " + c.getBlue()/255.0);
	}
	printWriter.println();
}

private void writeRuntimeCommands() throws SolverException, DivideByZeroException, DataAccessException, IOException, MathException, ExpressionException {
	printWriter.println("# runtime command");
	printWriter.println(SmoldynKeyword.cmd + " " + SmoldynKeyword.n + " 1 " + VCellSmoldynKeyword.vcellPrintProgress);
	if (outputFile != null) {
		OutputTimeSpec ots = simulation.getSolverTaskDescription().getOutputTimeSpec();
		if (ots.isUniform()) {
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
			
			printWriter.println(SmoldynKeyword.output_files + " " + outputFile.getName());
	//		printWriter.println(SmoldynKeyword.cmd + " " + SmoldynKeyword.n + " 1 " + SmoldynKeyword.warnescapee + " " + SmoldynKeyword.all + " " + outputFile.getName());
	//		printWriter.println(SmoldynKeyword.cmd + " " + SmoldynKeyword.n + " 1 " + SmoldynKeyword.killmoloutsidesystem + " " + SmoldynKeyword.all);
			ISize sampleSize = simulation.getMeshSpecification().getSamplingSize();
			TimeStep timeStep = simulation.getSolverTaskDescription().getTimeStep();
			int n = (int)Math.round(((UniformOutputTimeSpec)ots).getOutputTimeStep()/timeStep.getDefaultTimeStep());
			printWriter.println(SmoldynKeyword.cmd + " " + SmoldynKeyword.n + " " + n + " " + SmoldynKeyword.incrementfile + " " + outputFile.getName());
			printWriter.println(SmoldynKeyword.cmd + " " + SmoldynKeyword.n + " " + n + " " + SmoldynKeyword.listmols + " " + outputFile.getName());
	
			// DON'T CHANGE THE ORDER HERE.
			// DataProcess must be before vcellWriteOutput
			if (simulation.getDataProcessingInstructions() != null) {
				writeDataProcessor();
			}		
			printWriter.print(SmoldynKeyword.cmd + " " + SmoldynKeyword.n + " " + n + " " + VCellSmoldynKeyword.vcellWriteOutput + " " + sampleSize.getX());
			if (dimension > 1) {
				printWriter.print(" " + sampleSize.getY());
				if (dimension > 2) {
					printWriter.print(" " + sampleSize.getZ());			
				}
			}
			printWriter.print(" " + volumeRegionList.size());
			printWriter.println();
		} else {
			throw new SolverException(SolverDescription.Smoldyn.getDisplayLabel() + " only supports uniform output.");
		}
	}
	printWriter.println();		
}

private void writeDataProcessor() throws DataAccessException, IOException, MathException, DivideByZeroException, ExpressionException {
	Simulation simulation = simulationJob.getSimulation();
	DataProcessingInstructions dpi = simulation.getDataProcessingInstructions();
	if (dpi == null) {
		return;
	}
	
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
	String filename = SimulationJob.createSimulationJobID(Simulation.createSimulationID(simulation.getKey()), simulationJob.getJobIndex()) + FieldDataIdentifierSpec.getDefaultFieldDataFileNameForSimulation(fdis.getFieldFuncArgs());
	
	File fdatFile = new File(userDirectory, filename);
	
	
	DataSet.writeNew(fdatFile,
			new String[] {fdis.getFieldFuncArgs().getVariableName()},
			new VariableType[]{simDataBlock.getVariableType()},
			new ISize(origMesh.getSizeX(),origMesh.getSizeY(),origMesh.getSizeZ()),
			new double[][]{origData});
	printWriter.println(SmoldynKeyword.cmd + " " + SmoldynKeyword.B + " " + VCellSmoldynKeyword.vcellDataProcess + " begin " + dpi.getScriptName());
	StringTokenizer st = new StringTokenizer(dpi.getScriptInput(), "\n\r");
	while (st.hasMoreTokens()) {
		String str = st.nextToken();
		if (str.trim().length() > 0) {
			printWriter.println(SmoldynKeyword.cmd + " " + SmoldynKeyword.B + " " + VCellSmoldynKeyword.vcellDataProcess + " " + str);
		}
	}
	printWriter.println(SmoldynKeyword.cmd + " " + SmoldynKeyword.B + " " + VCellSmoldynKeyword.vcellDataProcess + " SampleImageFile " + fdis.getFieldFuncArgs().getVariableName() + " " + fdis.getFieldFuncArgs().getTime().infix() + " " + fdatFile);
	printWriter.println(SmoldynKeyword.cmd + " " + SmoldynKeyword.B + " " + VCellSmoldynKeyword.vcellDataProcess + " end");	
}

private void writeReactions() throws ExpressionException, MathException {
	printWriter.println("# reaction in compartments");
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
			double macroscopicRateConstant = 0;
			ParticleProbabilityRate ppr = pjp.getParticleProbabilityRate();
			if(ppr instanceof MacroscopicRateConstant) {
				try {
					macroscopicRateConstant = subsituteFlatten(((MacroscopicRateConstant) ppr).getExpression());
				} catch (NotAConstantException ex) {
					throw new ExpressionException("reacion rate for jump process " + pjp.getName() + " is not a constant. Constants are required for all reaction rates");
				}
			} else {
				new RuntimeException("particle probability rate not supported");
			}
			
			if (macroscopicRateConstant == 0) {
				continue;
			}
			
			if(subdomain instanceof CompartmentSubDomain) {
				printWriter.print(SmoldynKeyword.reaction_cmpt + " " + subdomain.getName() + " " + pjp.getName() + " ");
			} else if (subdomain instanceof MembraneSubDomain){
				printWriter.print(SmoldynKeyword.reaction + " " + pjp.getName() + " ");
			}
			// reactants
			if (reactants.size() == 0) {
				printWriter.print(0);
			} else {
				// find state for each molecule ... (up) for membrane, (fsoln) for front soluble, (bsoln) for back soluble
				printWriter.print(getVariableName(reactants.get(0),subdomain));
				for (int i = 1; i < reactants.size(); i ++) {
					printWriter.print(" + " + getVariableName(reactants.get(i),subdomain));
				}
			}
			printWriter.print(" -> ");
			// products
			if (products.size() == 0) {
				printWriter.print(0);
			} else {
				printWriter.print(getVariableName(products.get(0),subdomain));
				for (int i = 1; i < products.size(); i ++) {
					printWriter.print(" + " + getVariableName(products.get(i),subdomain));
				}
			}
			if (reactants.size() == 2) {
				macroscopicRateConstant *= ReservedSymbol.KMOLE.getExpression().evaluateConstant();
			}
			printWriter.println(" " + macroscopicRateConstant);
		}
	}
	printWriter.println();
}

private String getVariableName(Variable var, SubDomain subdomain) throws MathException {
	if (subdomain instanceof MembraneSubDomain){
		MembraneSubDomain membrane = (MembraneSubDomain)subdomain;
		if (var.getDomain().getName().equals(membrane.getName())){
			return var.getName()+"("+SmoldynKeyword.up+")";
		}else if (membrane.getInsideCompartment().getName().equals(var.getDomain().getName())){
			return var.getName()+"("+SmoldynKeyword.bsoln+")";
		}else if (membrane.getOutsideCompartment().getName().equals(var.getDomain().getName())){
			return var.getName()+"("+SmoldynKeyword.fsoln+")";
		}else{
			throw new MathException("variable "+var.getQualifiedName()+" cannot be in a reaction on non-adjacent membrane "+subdomain.getName());
		}
	}else{
		return var.getName();
	} 
}

private double writeInitialConcentration(ParticleInitialConditionConcentration initialConcentration, SubDomain subDomain, Variable variable, String variableName, StringBuilder sb) throws ExpressionException, MathException {	
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
	
	RandomDataImpl dist = new RandomDataImpl();
	Integer randomSeed = simulation.getSolverTaskDescription().getSmoldynSimulationOptions().getRandomSeed();
	if (randomSeed != null) {
		dist.reSeed(randomSeed);
	}
	double totalCount = 0;
	if (subDomain instanceof CompartmentSubDomain) {		
		MeshSpecification meshSpecification = simulation.getMeshSpecification();
		ISize sampleSize = meshSpecification.getSamplingSize();
		int numX = sampleSize.getX();
		int numY = dimension < 2 ? 1 : sampleSize.getY();
		int numZ = dimension < 3 ? 1 : sampleSize.getZ();
		double dx = meshSpecification.getDx();
		double dy = meshSpecification.getDy();
		double dz = meshSpecification.getDz();
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
					sb.append(SmoldynKeyword.mol + " " + count + " " + variableName + " " + (float)lox + "-" + (float)hix);
					if (dimension > 1) {
						sb.append(" " + loy + "-" + hiy);
					
						if (dimension > 2) {
							sb.append(" " + loz + "-" + hiz);
						}
					}
					sb.append("\n");				
				}
			}
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
			sb.append(SmoldynKeyword.surface_mol + " " + count + " " + variableName + " " + subDomain.getName() + " " 
					+ SmoldynKeyword.tri + " " + trianglePanel.name + "\n");
		}
	}

	return totalCount;
}

private double writeInitialCount(ParticleInitialConditionCount initialCount, SubDomain subDomain, String variableName, StringBuilder sb) throws ExpressionException, MathException {
	double count = 0;
	try {
		count = subsituteFlatten(initialCount.getCount());
	} catch (NotAConstantException ex) {
		throw new ExpressionException("initial count for variable " + variableName + " is not a constant. Constants are required for all intial counts");
	}
	if (count > 0) {
		int intcount = (int)count;
		if (initialCount.isUniform()) {
			// here count has to split between all compartments
			if (subDomain instanceof CompartmentSubDomain) {
				sb.append(SmoldynKeyword.compartment_mol);
				sb.append(" " + intcount + " " + variableName + " " + subDomain.getName() + "\n");
			} else if (subDomain instanceof MembraneSubDomain) {
				sb.append(SmoldynKeyword.surface_mol); 
				sb.append(" " + intcount + " " + variableName + " " + subDomain.getName() + " " + SmoldynKeyword.all + " " + SmoldynKeyword.all + "\n");
			}
		} else {
			sb.append(SmoldynKeyword.mol + " " + intcount + " " + variableName);
			try {
				if (initialCount.isXUniform()) {
					sb.append(" " + initialCount.getLocationX().infix());					
				} else {
					double locX = subsituteFlatten(initialCount.getLocationX());
					sb.append(" " + locX);
				}
				if (dimension > 1) {
					if (initialCount.isYUniform()) {
						sb.append(" " + initialCount.getLocationY().infix());					
					} else {
						double locY = subsituteFlatten(initialCount.getLocationY());
						sb.append(" " + locY);
					}
					if (dimension > 2) {
						if (initialCount.isZUniform()) {
							sb.append(" " + initialCount.getLocationZ().infix());					
						} else {
							double locZ = subsituteFlatten(initialCount.getLocationZ());
							sb.append(" " + locZ);
						}
					}
				}
			} catch (NotAConstantException ex) {
				throw new ExpressionException("location for variable " + variableName + " is not a constant. Constants are required for all locations");
			}
			sb.append("\n");
		}
	}
	return count;
}

private void writeMolecules() throws ExpressionException, MathException {
	// write molecules
	StringBuilder sb = new StringBuilder();
	double max_mol = 0;
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
		}		
	}
	max_mol = Math.max(50000, max_mol * 10);
	if (max_mol > MAX_MOL_LIMIT) {
		throw new MathException(VCellErrorMessages.getSmoldynMaxMolReachedErrorMessage((long)max_mol, MAX_MOL_LIMIT));
	}
	printWriter.println("# molecules");	
	printWriter.println(SmoldynKeyword.max_mol + " " + (long)max_mol);
	printWriter.println(sb);
}

private void writeSimulationTimes() {
	// write simulation times
	TimeBounds timeBounds = simulation.getSolverTaskDescription().getTimeBounds();
	TimeStep timeStep = simulation.getSolverTaskDescription().getTimeStep();
	printWriter.println("# simulation times");	
	printWriter.println(SmoldynKeyword.time_start + " " + timeBounds.getStartingTime());
	printWriter.println(SmoldynKeyword.time_stop + " " + timeBounds.getEndingTime());
	printWriter.println(SmoldynKeyword.time_step + " " + timeStep.getDefaultTimeStep());
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

private void writeSurfacesAndCompartments() throws SolverException {
	boolean DEBUG = false;
	
	PrintWriter tmppw = null;  
	try {
		if (DEBUG) tmppw = new PrintWriter("D:\\smoldyn-2.15\\surfacepoints.m");
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	
	GeometrySurfaceDescription geometrySurfaceDescription = resampledGeometry.getGeometrySurfaceDescription();	
	
	if (DEBUG) {
		PrintWriter stlpw = null;  
		try {
			stlpw = new PrintWriter("D:\\smoldyn-2.15\\surface.stl");
			StlExporter.writeStl(geometrySurfaceDescription, stlpw);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stlpw != null) stlpw.close();
		}
	}
	
	SurfaceClass[] surfaceClasses = geometrySurfaceDescription.getSurfaceClasses();
	GeometrySpec geometrySpec = resampledGeometry.getGeometrySpec();
	SubVolume[] subVolumes = geometrySpec.getSubVolumes();
	
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
	printWriter.println(SmoldynKeyword.dim + " " + dimension);
	if (bHasNoSurface) {
		printWriter.println(SmoldynKeyword.max_compartment + " " + subVolumes.length);
		printWriter.println(SmoldynKeyword.max_surface + " 0 ");
	} else {
		printWriter.println(SmoldynKeyword.max_compartment + " " + (subVolumes.length + 1));
		printWriter.println(SmoldynKeyword.max_surface + " " + (surfaceClasses.length + dimension)); // plus the surface which are bounding walls
	}
	printWriter.println();

	// write boundaries and wall surfaces
	writeWallSurfaces();
	
	if (!bHasNoSurface) {
		membraneSubdomainTriangleMap = new HashMap<MembraneSubDomain, ArrayList<TrianglePanel> >();
		// write surfaces
		printWriter.println("# surfaces");
		for (int sci = 0; sci < surfaceClasses.length; sci ++) {
			SurfaceClass surfaceClass = surfaceClasses[sci];			
			GeometricRegion[] geometricRegions = geometrySurfaceDescription.getGeometricRegions(surfaceClass);
			ArrayList<TrianglePanel> triList = new ArrayList<TrianglePanel>();
			for (GeometricRegion gr : geometricRegions) {
				SurfaceGeometricRegion sgr = (SurfaceGeometricRegion)gr;
				VolumeGeometricRegion volRegion1 = (VolumeGeometricRegion)sgr.getAdjacentGeometricRegions()[0];
				int volRegionID = volRegion1.getRegionID();
				SurfaceCollection surfaceCollection = geometrySurfaceDescription.getSurfaceCollection();
				for(int j = 0; j < surfaceCollection.getSurfaceCount(); j++) {
					Surface surface = surfaceCollection.getSurfaces(j);
					if (surface.getInteriorRegionIndex() == volRegionID || surface.getExteriorRegionIndex() == volRegionID) { // my triangles
						for(int k = 0; k < surface.getPolygonCount(); k++) {
							Polygon polygon = surface.getPolygons(k);
							Node[] nodes = polygon.getNodes();
							if (surface.getExteriorRegionIndex() == volRegionID) { // interior	
								if (dimension == 2){
									// ignore z
									Vect3d unitNormal = new Vect3d();
									polygon.getUnitNormal(unitNormal);
									unitNormal.set(unitNormal.getX(), unitNormal.getY(), 0);
									Vect3d v0 = new Vect3d(nodes[0].getX(),nodes[0].getY(),0);
									Vect3d v1 = new Vect3d(nodes[1].getX(),nodes[1].getY(),0);
									Vect3d v2 = new Vect3d(nodes[2].getX(),nodes[2].getY(),0);
									Vect3d v01 = Vect3d.sub(v1, v0);
									Vect3d unit01n = v01.cross(unitNormal);
									unit01n.unit();
									if (Math.abs(unit01n.dot(new Vect3d(0,0,1))-1.0) < 1e-6){
										// first two indices are ok
										triList.add(new TrianglePanel(PANEL_TRIANGLE_NAME_PREFIX + triList.size(), new Triangle(nodes[0], nodes[1], nodes[2])));
									}else if ((unit01n.dot(new Vect3d(0,0,1))+1.0) < 1e-6){
										// first two are opposite
										triList.add(new TrianglePanel(PANEL_TRIANGLE_NAME_PREFIX + triList.size(), new Triangle(nodes[1], nodes[2], nodes[0])));
									}else if (unit01n.dot(new Vect3d(0,0,1)) < 1e-6){
										// same plane ... do we use 0 and 2 or 2 and 0?
										Vect3d v02 = Vect3d.sub(v2, v0);
										Vect3d unit02n = v02.cross(unitNormal);
										unit02n.unit();
										if (Math.abs(unit02n.dot(new Vect3d(0,0,1))-1.0) < 1e-6){
											// first two indices are ok
											triList.add(new TrianglePanel(PANEL_TRIANGLE_NAME_PREFIX + triList.size(), new Triangle(nodes[0], nodes[2], nodes[1])));
										}else if ((unit02n.dot(new Vect3d(0,0,1))+1.0) < 1e-6){
											// first two are opposite
											triList.add(new TrianglePanel(PANEL_TRIANGLE_NAME_PREFIX + triList.size(), new Triangle(nodes[2], nodes[0], nodes[1])));
										}else{
											throw new RuntimeException("failed to generate surface");
										}
									}
								}else{
									triList.add(new TrianglePanel(PANEL_TRIANGLE_NAME_PREFIX + triList.size(), new Triangle(nodes[0], nodes[1], nodes[2])));
									triList.add(new TrianglePanel(PANEL_TRIANGLE_NAME_PREFIX + triList.size(), new Triangle(nodes[0], nodes[2], nodes[3])));
								}
							} else {
								if (dimension == 2){
									// ignore z
									Vect3d unitNormal = new Vect3d();
									polygon.getUnitNormal(unitNormal);
									unitNormal.set(unitNormal.getX(), unitNormal.getY(), 0);
									Vect3d v0 = new Vect3d(nodes[2].getX(),nodes[2].getY(),0);
									Vect3d v1 = new Vect3d(nodes[1].getX(),nodes[1].getY(),0);
									Vect3d v2 = new Vect3d(nodes[0].getX(),nodes[0].getY(),0);
									Vect3d v01 = Vect3d.sub(v1, v0);
									Vect3d unit01n = v01.cross(unitNormal);
									unit01n.unit();
									if (Math.abs(unit01n.getZ()-1.0) < 1e-6){
										// first two indices are ok
										triList.add(new TrianglePanel(PANEL_TRIANGLE_NAME_PREFIX + triList.size(), new Triangle(nodes[0], nodes[1], nodes[2])));
									}else if ((unit01n.dot(new Vect3d(0,0,1))+1.0) < 1e-6){
										// first two are opposite
										triList.add(new TrianglePanel(PANEL_TRIANGLE_NAME_PREFIX + triList.size(), new Triangle(nodes[1], nodes[2], nodes[0])));
									}else if (unit01n.dot(new Vect3d(0,0,1)) < 1e-6){
										// same plane ... do we use 0 and 2 or 2 and 0?
										Vect3d v02 = Vect3d.sub(v2, v0);
										Vect3d unit02n = v02.cross(unitNormal);
										unit02n.unit();
										if (Math.abs(unit02n.getZ()-1.0) < 1e-6){
											// first two indices are ok
											triList.add(new TrianglePanel(PANEL_TRIANGLE_NAME_PREFIX + triList.size(), new Triangle(nodes[0], nodes[2], nodes[1])));
										}else if ((unit02n.dot(new Vect3d(0,0,1))+1.0) < 1e-6){
											// first two are opposite
											triList.add(new TrianglePanel(PANEL_TRIANGLE_NAME_PREFIX + triList.size(), new Triangle(nodes[2], nodes[0], nodes[1])));
										}else{
											throw new RuntimeException("failed to generate surface");
										}
									}
								}else{
									triList.add(new TrianglePanel(PANEL_TRIANGLE_NAME_PREFIX + triList.size(), new Triangle(nodes[0], nodes[1], nodes[2])));
									triList.add(new TrianglePanel(PANEL_TRIANGLE_NAME_PREFIX + triList.size(), new Triangle(nodes[0], nodes[2], nodes[3])));
								}
							}
						}
					}
				}
			}
			SubVolume[] adjacentSubvolums = surfaceClass.getAdjacentSubvolumes().toArray(new SubVolume[0]);
			CompartmentSubDomain csd0 = simulation.getMathDescription().getCompartmentSubDomain(adjacentSubvolums[0].getName());
			CompartmentSubDomain csd1 = simulation.getMathDescription().getCompartmentSubDomain(adjacentSubvolums[1].getName());
			MembraneSubDomain membraneSubDomain = simulation.getMathDescription().getMembraneSubDomain(csd0, csd1);
			membraneSubdomainTriangleMap.put(membraneSubDomain, triList);
			
			printWriter.println(SmoldynKeyword.start_surface + " " + surfaceClass.getName());
			printWriter.println(SmoldynKeyword.max_panels + " " + SmoldynKeyword.tri + " " + triList.size());			
			
			if (DEBUG) tmppw.println("verts" + sci + "=[");
			for (TrianglePanel trianglePanel : triList) {
				Triangle triangle = trianglePanel.triangle;
				printWriter.print(SmoldynKeyword.panel + " " + SmoldynKeyword.tri);
				switch (dimension) {
				case 1:
					printWriter.print(" " + triangle.getNodes(0).getX());
					break;
				case 2:
					printWriter.print(" " + triangle.getNodes(0).getX() + " " + triangle.getNodes(0).getY());
					if (DEBUG) tmppw.print(" " + triangle.getNodes(0).getX() + " " + triangle.getNodes(0).getY());
	
					if (triangle.getNodes(0).getX() == triangle.getNodes(1).getX() && triangle.getNodes(0).getY() == triangle.getNodes(1).getY()) {
						printWriter.print(" " + triangle.getNodes(2).getX() + " " + triangle.getNodes(2).getY());
						if (DEBUG) tmppw.print(" " + triangle.getNodes(2).getX() + " " + triangle.getNodes(2).getY());
					} else {
						printWriter.print(" " + triangle.getNodes(1).getX() + " " + triangle.getNodes(1).getY());
						if (DEBUG) tmppw.print(" " + triangle.getNodes(1).getX() + " " + triangle.getNodes(1).getY());
					}
					break;
				case 3:
					for (Node node : triangle.getNodes()) {
						printWriter.print(" " + node.getX() + " " + node.getY() + " " + node.getZ());
						if (DEBUG) tmppw.print(" " + node.getX() + " " + node.getY() + " " + node.getZ());
					}
					break;
				}
			
				printWriter.println(" " + trianglePanel.name);
				if (DEBUG) tmppw.println();
			}
			printWriter.println(SmoldynKeyword.end_surface);
			printWriter.println();
			if (DEBUG) tmppw.println("];");
		}
		
		// write compartment
		printWriter.println("# bounding wall compartment");
		printWriter.println(SmoldynKeyword.start_compartment + " " + VCellSmoldynKeyword.bounding_wall_compartment);
		printWriter.println(SmoldynKeyword.surface + " " + VCellSmoldynKeyword.bounding_wall_surface_X);
		if (dimension > 1) {
			printWriter.println(SmoldynKeyword.surface + " " + VCellSmoldynKeyword.bounding_wall_surface_Y);
			if (dimension > 2) {
				printWriter.println(SmoldynKeyword.surface + " " + VCellSmoldynKeyword.bounding_wall_surface_Z);
			}
		}
		printWriter.println(SmoldynKeyword.end_compartment);
		printWriter.println();
	}
	
	MeshSpecification meshSpecification = simulation.getMeshSpecification();
	ISize sampleSize = meshSpecification.getSamplingSize();
	int numX = sampleSize.getX();
	int numY = dimension < 2 ? 1 : sampleSize.getY();
	int numZ = dimension < 3 ? 1 : sampleSize.getZ();
	int numXY = numX * numY;
	double dx = meshSpecification.getDx();
	double dy = meshSpecification.getDy();
	double dz = meshSpecification.getDz();
	Origin origin = geometrySpec.getOrigin();

	printWriter.println("# compartments");
	int pointsCount = 0;
	for (SubVolume subVolume : subVolumes) {		
		printWriter.println(SmoldynKeyword.start_compartment + " " + subVolume.getName());		
		for (SurfaceClass sc : surfaceClasses) {
			if (sc.getAdjacentSubvolumes().contains(subVolume)) {
				printWriter.println(SmoldynKeyword.surface + " " + sc.getName());
			}
		}
		if (boundaryXSubVolumes.contains(subVolume)) {
			printWriter.println(SmoldynKeyword.surface + " " + VCellSmoldynKeyword.bounding_wall_surface_X);
		}
		if (dimension > 1) {
			if (boundaryYSubVolumes.contains(subVolume)) {			
				printWriter.println(SmoldynKeyword.surface + " " + VCellSmoldynKeyword.bounding_wall_surface_Y);
			}
			if (dimension > 2) {
				if (boundaryZSubVolumes.contains(subVolume)) {
					printWriter.println(SmoldynKeyword.surface + " " + VCellSmoldynKeyword.bounding_wall_surface_Z);
				}				
			}
		}
		
		if (DEBUG) {
			tmppw.println("points" + pointsCount + "=[");
			pointsCount ++;
		}
		
		// gather all the points in all the regions
		GeometricRegion[] geometricRegions = geometrySurfaceDescription.getGeometricRegions(subVolume);
		RegionInfo[] regionInfos = geometrySurfaceDescription.getRegionImage().getRegionInfos();
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
					printWriter.print(SmoldynKeyword.point + " " + coordX);
					if (DEBUG) tmppw.print(coordX);
					if (dimension > 1) {
						double coordY = origin.getY() + dy * thisPoint.j;
						printWriter.print(" " + coordY);
						if (DEBUG) tmppw.print(" " + coordY);										
						if (dimension > 2) {
							double coordZ = origin.getZ() + dz * thisPoint.k;
							printWriter.print(" " + coordZ);
							if (DEBUG) tmppw.print(" " + coordZ);
						}
					}
					printWriter.println();
					if (DEBUG) tmppw.println();					
				}
			}
		} // end for (GeometricRegion
		if (DEBUG) tmppw.println("];");
		
		printWriter.println(SmoldynKeyword.end_compartment);
		printWriter.println();
	} // end for (SubVolume
	if (DEBUG) tmppw.close();
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
			printWriter.println(SmoldynKeyword.boundaries + " 0 " + lowWall.getX() + " " + highWall.getX() + " " + SmoldynKeyword.p);
		} else {
			printWriter.println(SmoldynKeyword.low_wall + " 0 " + lowWall.getX() + " " + (compartSubDomain0.getBoundaryConditionXm().isNEUMANN() ? SmoldynKeyword.r : SmoldynKeyword.a));
			printWriter.println(SmoldynKeyword.high_wall + " 0 " + highWall.getX() + " " + (compartSubDomain0.getBoundaryConditionXp().isNEUMANN() ? SmoldynKeyword.r : SmoldynKeyword.a));
		}
		if (dimension > 1) {
			// y
			if (compartSubDomain0.getBoundaryConditionYm().isPERIODIC()) {
				printWriter.println(SmoldynKeyword.boundaries + " 1 " + lowWall.getY() + " " + highWall.getY() + " " + SmoldynKeyword.p);
			} else {
				printWriter.println(SmoldynKeyword.low_wall + " 1 " + lowWall.getY() + " " + (compartSubDomain0.getBoundaryConditionYm().isNEUMANN() ? SmoldynKeyword.r : SmoldynKeyword.a));
				printWriter.println(SmoldynKeyword.high_wall + " 1 " + highWall.getY() + " " + (compartSubDomain0.getBoundaryConditionYp().isNEUMANN() ? SmoldynKeyword.r : SmoldynKeyword.a));
			}
			
			if (dimension > 2) {
				// z
				if (compartSubDomain0.getBoundaryConditionZm().isPERIODIC()) {
					printWriter.println(SmoldynKeyword.boundaries + " 2 " + lowWall.getZ() + " " + highWall.getZ() + " " + SmoldynKeyword.p);
				} else {
					printWriter.println(SmoldynKeyword.low_wall + " 2 " + lowWall.getZ() + " " + (compartSubDomain0.getBoundaryConditionZm().isNEUMANN() ? SmoldynKeyword.r : SmoldynKeyword.a));
					printWriter.println(SmoldynKeyword.high_wall + " 2 " + highWall.getZ() + " " + (compartSubDomain0.getBoundaryConditionZp().isNEUMANN() ? SmoldynKeyword.r : SmoldynKeyword.a));
				}				
			}
		}
		printWriter.println();
	} else {	
		// x 
		printWriter.println(SmoldynKeyword.boundaries + " 0 " + lowWall.getX() + " " + highWall.getX());
		if (dimension > 1) {
			// y	
			printWriter.println(SmoldynKeyword.boundaries + " 1 " + lowWall.getY() + " " + highWall.getY());
			if (dimension > 2) {
				// z
				printWriter.println(SmoldynKeyword.boundaries + " 2 " + lowWall.getZ() + " " + highWall.getZ());
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
						int volIndex = k_wall[k] * numX * numY + j * numY + i;
						
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
						int volIndex = k * numX * numY + j_wall[j] * numY + i;
						
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
					int volIndex = k * numX * numY + j * numY + i_wall[i];
					
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
						smoldynBct[i] = computedBct[i].isDIRICHLET() ? SmoldynKeyword.absorb.name() : SmoldynKeyword.reflect.name();
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
		printWriter.println(SmoldynKeyword.start_surface + " " + VCellSmoldynKeyword.bounding_wall_surface_X);
		printWriter.println(SmoldynKeyword.action + " " + SmoldynKeyword.front + " " + SmoldynKeyword.all + " " + smoldynBct[0]);
		printWriter.println(SmoldynKeyword.action + " " + SmoldynKeyword.back + " " + SmoldynKeyword.all + " " + smoldynBct[1]);
		printWriter.println(SmoldynKeyword.max_panels + " " + SmoldynKeyword.rect + " 2");
		// yz walls
		switch (dimension) {
		case 1:
			printWriter.println(SmoldynKeyword.panel + " " + SmoldynKeyword.rect + " +0 " + lowWall.getX());
			printWriter.println(SmoldynKeyword.panel + " " + SmoldynKeyword.rect + " -0 " + highWall.getX());
			break;
		case 2:
			printWriter.println(SmoldynKeyword.panel + " " + SmoldynKeyword.rect + " +0 " + lowWall.getX() + " " + lowWall.getY() + " " + extent.getY());
			printWriter.println(SmoldynKeyword.panel + " " + SmoldynKeyword.rect + " -0 " + highWall.getX() + " " + lowWall.getY() + " " + extent.getY());
			break;
		case 3:
			printWriter.println(SmoldynKeyword.panel + " " + SmoldynKeyword.rect + " +0 " + lowWall.getX() + " " + lowWall.getY() + " " + lowWall.getZ() + " " + extent.getY() + " " + extent.getZ());
			printWriter.println(SmoldynKeyword.panel + " " + SmoldynKeyword.rect + " -0 " + highWall.getX() + " " + lowWall.getY() + " " + lowWall.getZ() + " " + extent.getY() + " " + extent.getZ());
			break;
		}
		printWriter.println(SmoldynKeyword.end_surface);
		printWriter.println();
		
		if (dimension > 1) {
			// Y walls
			printWriter.println(SmoldynKeyword.start_surface + " " + VCellSmoldynKeyword.bounding_wall_surface_Y);
			printWriter.println(SmoldynKeyword.action + " " + SmoldynKeyword.front + " " + SmoldynKeyword.all + " " + smoldynBct[2]);
			printWriter.println(SmoldynKeyword.action + " " + SmoldynKeyword.back + " " + SmoldynKeyword.all + " " + smoldynBct[3]);
			printWriter.println(SmoldynKeyword.max_panels + " " + SmoldynKeyword.rect + " 2");
			// xz walls
			switch (dimension) {
			case 2:
				printWriter.println(SmoldynKeyword.panel + " " + SmoldynKeyword.rect + " +1 " + lowWall.getX() + " " + lowWall.getY() + " " + extent.getX());
				printWriter.println(SmoldynKeyword.panel + " " + SmoldynKeyword.rect + " -1 " + lowWall.getX() + " " + highWall.getY() + " " + extent.getX());		
				break;
			case 3:
				printWriter.println(SmoldynKeyword.panel + " " + SmoldynKeyword.rect + " +1 " + lowWall.getX() + " " + lowWall.getY() + " " + lowWall.getZ() + " " + extent.getX() + " " + extent.getZ());
				printWriter.println(SmoldynKeyword.panel + " " + SmoldynKeyword.rect + " -1 " + lowWall.getX() + " " + highWall.getY() + " " + lowWall.getZ() + " " + extent.getX() + " " + extent.getZ());		
				break;
			}
			printWriter.println(SmoldynKeyword.end_surface);
			printWriter.println();
			
			if (dimension > 2) {
				// Z walls
				printWriter.println(SmoldynKeyword.start_surface + " " + VCellSmoldynKeyword.bounding_wall_surface_Z);
				printWriter.println(SmoldynKeyword.action + " " + SmoldynKeyword.front + " " + SmoldynKeyword.all + " " + smoldynBct[4]);
				printWriter.println(SmoldynKeyword.action + " " + SmoldynKeyword.back + " " + SmoldynKeyword.all + " " + smoldynBct[5]);
				printWriter.println(SmoldynKeyword.max_panels + " " + SmoldynKeyword.rect + " 2");
				// xy walls
				printWriter.println(SmoldynKeyword.panel + " " + SmoldynKeyword.rect + " +2 " + lowWall.getX() + " " + lowWall.getY() + " " + lowWall.getZ() + " " + extent.getX() + " " + extent.getY());
				printWriter.println(SmoldynKeyword.panel + " " + SmoldynKeyword.rect + " -2 " + lowWall.getX() + " " + lowWall.getY() + " " + highWall.getZ() + " " + extent.getX() + " " + extent.getY());			
				printWriter.println(SmoldynKeyword.end_surface);
				printWriter.println();
			}
		}
	}
}
private double subsituteFlatten(Expression exp) throws MathException, NotAConstantException, ExpressionException {
	Expression newExp = new Expression(exp);
	newExp.bindExpression(simulationSymbolTable);
	newExp = simulationSymbolTable.substituteFunctions(newExp).flatten();
	try {
		return newExp.evaluateConstant();
	} catch (ExpressionException ex) {
		throw new NotAConstantException();
	}
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
			String variableName = getVariableName(pp.getVariable(),null);
			try {
				double diffConstant = subsituteFlatten(pp.getDiffusion());
				printWriter.println(SmoldynKeyword.difc + " " + variableName + " " + diffConstant);
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
	printWriter.print(SmoldynKeyword.species);
	for (ParticleVariable pv : particleVariableList) {
		printWriter.print(" " + getVariableName(pv,null));
	}
	printWriter.println();
	printWriter.println();
}

private void writeJms(Simulation simulation) {
	if (simulationJob  != null) {
		printWriter.println("# JMS_Paramters");
		printWriter.println("start_jms"); 
		printWriter.println(JmsUtils.getJmsProvider() + " " + JmsUtils.getJmsUrl()
			+ " " + JmsUtils.getJmsUserID() + " " + JmsUtils.getJmsPassword()
			+ " " + JmsUtils.getQueueWorkerEvent()  
			+ " " + JmsUtils.getTopicServiceControl()
			+ " " + simulation.getVersion().getOwner().getName()
			+ " " + simulation.getVersion().getVersionKey()
			+ " " + simulationJob.getJobIndex());
		printWriter.println("end_jms");
		printWriter.println();
	}
}

}