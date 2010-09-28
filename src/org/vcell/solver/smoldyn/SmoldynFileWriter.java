package org.vcell.solver.smoldyn;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vcell.util.BeanUtils;
import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.Node;
import cbit.vcell.geometry.surface.Polygon;
import cbit.vcell.geometry.surface.Surface;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
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
import cbit.vcell.math.ParticleVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.ParticleProperties.ParticleInitialCondition;
import cbit.vcell.messaging.JmsUtils;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
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


/**
 * The function reads model information from simulation and
 * generates the stochastic input file for simulation engine.
 * Creation date: (6/22/2006 4:22:59 PM)
 * @author: Tracy LI
 */
public class SmoldynFileWriter extends SolverFileWriter 
{

	private class NotAConstantException extends Exception {		
	}
	
	private File outputFile = null;
	private Simulation simulation = null;
	private MathDescription mathDesc = null;
	private SimulationSymbolTable simulationSymbolTable = null;
	ArrayList<ParticleVariable> particleVariableList = null;
	private Geometry resampledGeometry = null;
	private int dimension = 1;
	private Set<SubVolume> boundaryXSubVolumes = new HashSet<SubVolume>();
	private Set<SubVolume> boundaryYSubVolumes = new HashSet<SubVolume>();
	private Set<SubVolume> boundaryZSubVolumes = new HashSet<SubVolume>();
	
	enum SmoldynKeyword {
		species,
		difc,
		
		time_start,
		time_stop,
		time_step,
		
		dim,
		max_compartment,
		max_surface,
		boundaries,
		
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
		mol,
				
		output_files,
		
		cmd,
		n,
	
		accuracy,
		boxsize,
		gauss_table_size,
	}

	enum VCellSmoldynKeyword {
		bounding_wall_surface_X,
		bounding_wall_surface_Y,
		bounding_wall_surface_Z,
		bounding_wall_compartment,
		
		vcellPrintProgress,
		vcellWriteOutput,
	}
	
/**
 * StochFileWriter constructor comment.
 */
public SmoldynFileWriter(PrintWriter pw, File outputFile, SimulationJob arg_simulationJob, boolean bMessaging) 
{
	super(pw, arg_simulationJob, bMessaging);
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
	} catch (Exception e) {
		e.printStackTrace();
		throw new SolverException(e.getMessage());
	}
}


@Override
public void write(String[] parameterNames) throws ExpressionException, MathException, SolverException {	
	init();
	
	if (!simulation.getMeshSpecification().isAspectRatioOK()) {
		throw new SolverException(SolverDescription.Smoldyn.getDisplayLabel() + " expect uniform box sizes");
	}
	
	writeJms(simulation);	
	writeSpecies();	
	writeDiffusions();	
	writeSurfacesAndCompartments();	
	writeReactions();
	writeMolecules();	
	writeSimulationTimes();
	writeRuntimeCommands();
	writeSimulationSettings();
	//SimulationWriter.write(SimulationJobToSmoldyn.convertSimulationJob(simulationJob, outputFile), printWriter, simulationJob);
}

private void writeSimulationSettings() {
	printWriter.println("# simulation settings");
	SmoldynSimulationOptions smoldynSimulationOptions = simulation.getSolverTaskDescription().getSmoldynSimulationOptions();
	printWriter.println(SmoldynKeyword.accuracy + " " + smoldynSimulationOptions.getAccuracy());
	printWriter.println(SmoldynKeyword.boxsize + " " + simulation.getMeshSpecification().getDx());
	printWriter.println(SmoldynKeyword.gauss_table_size + " " + smoldynSimulationOptions.getGaussianTableSize());
	
}

private void writeRuntimeCommands() throws SolverException {
	OutputTimeSpec ots = simulation.getSolverTaskDescription().getOutputTimeSpec();
	if (ots.isUniform()) {
		printWriter.println("# runtime command");	
		printWriter.println(SmoldynKeyword.output_files + " " + outputFile.getName());
		printWriter.println(SmoldynKeyword.cmd + " " + SmoldynKeyword.n + " 1 " + VCellSmoldynKeyword.vcellPrintProgress);
		ISize sampleSize = simulation.getMeshSpecification().getSamplingSize();
		TimeStep timeStep = simulation.getSolverTaskDescription().getTimeStep();
		int n = (int)Math.round(((UniformOutputTimeSpec)ots).getOutputTimeStep()/timeStep.getDefaultTimeStep());
		printWriter.print(SmoldynKeyword.cmd + " " + SmoldynKeyword.n + " " + n + " " + VCellSmoldynKeyword.vcellWriteOutput + " " + sampleSize.getX());
		if (dimension > 1) {
			printWriter.print(" " + sampleSize.getY());
			if (dimension > 2) {
				printWriter.print(" " + sampleSize.getZ());			
			}
		}
		
		printWriter.println();
		printWriter.println();
	} else {
		throw new SolverException(SolverDescription.Smoldyn.getDisplayLabel() + " only supports uniform output.");
	}
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
			double rateConstant = 0;
			ParticleProbabilityRate ppr = pjp.getParticleProbabilityRate();
			if(ppr instanceof MacroscopicRateConstant) {
				try {
					rateConstant = subsituteFlatten(((MacroscopicRateConstant) ppr).getExpression());
				} catch (NotAConstantException ex) {
					throw new ExpressionException("reacion rate for jump process " + pjp.getName() + " is not a constant. Constants are required for all reaction rates");
				}
			} else {
				new RuntimeException("particle probability rate not supported");
			}
			
			if(subdomain instanceof CompartmentSubDomain) {
				printWriter.print(SmoldynKeyword.reaction_cmpt);
			} else if (subdomain instanceof MembraneSubDomain){
				printWriter.print(SmoldynKeyword.reaction_surface);
			}
			printWriter.print(" " + subdomain.getName() + " " + pjp.getName() + " ");
			// reactants
			if (reactants.size() == 0) {
				printWriter.print(0);
			} else {
				printWriter.print(getVariableName(reactants.get(0)));
				for (int i = 1; i < reactants.size(); i ++) {
					printWriter.print(" + " + getVariableName(reactants.get(i)));
				}
			}
			printWriter.print(" -> ");
			// products
			if (products.size() == 0) {
				printWriter.print(0);
			} else {
				printWriter.print(getVariableName(products.get(0)));
				for (int i = 1; i < products.size(); i ++) {
					printWriter.print(" + " + getVariableName(products.get(i)));
				}
			}
			printWriter.println(" " + rateConstant);
		}
	}
	printWriter.println();
}

private String getVariableName(Variable var) {
	return var.getName();
}

private void writeMolecules() throws ExpressionException, MathException {
	// write molecules
	StringBuffer sb = new StringBuffer();
	int max_mol = 0;
	Enumeration<SubDomain> subDomainEnumeration = mathDesc.getSubDomains();
	while (subDomainEnumeration.hasMoreElements()) {
		SubDomain subDomain = subDomainEnumeration.nextElement();
		
		if (!(subDomain instanceof CompartmentSubDomain)) {
			continue;
		}
		
		for (ParticleVariable particleVariable : particleVariableList) {
			ParticleProperties particleProperties = subDomain.getParticleProperties(particleVariable);			
			String variableName = getVariableName(particleVariable);
			
			if (particleProperties == null) {
				sb.append(SmoldynKeyword.compartment_mol + " 0 " + variableName + " " + subDomain.getName() + "\n");
				continue;
			}
			ArrayList<ParticleInitialCondition> particleInitialConditions = particleProperties.getParticleInitialConditions();
			for (ParticleInitialCondition pic : particleInitialConditions) {
				int count = 0;
				try {
					count = (int)subsituteFlatten(pic.getCount());
				} catch (NotAConstantException ex) {
					throw new ExpressionException("initial count for variable " + variableName + " is not a constant. Constants are required for all intial counts");
				}
				max_mol += count;
				if (pic.isUniform()) {
					// here count has to split between all compartments
					sb.append(SmoldynKeyword.compartment_mol + " " + count + " " + variableName + " " + subDomain.getName() + "\n");
				} else {
					sb.append(SmoldynKeyword.mol + " " + count + " " + variableName);
					try {
						if (pic.isXUniform()) {
							sb.append(" " + pic.getLocationX().infix());					
						} else {
							double locX = subsituteFlatten(pic.getLocationX());
							sb.append(" " + locX);
						}
						if (dimension > 1) {
							if (pic.isYUniform()) {
								sb.append(" " + pic.getLocationY().infix());					
							} else {
								double locY = subsituteFlatten(pic.getLocationY());
								sb.append(" " + locY);
							}
							if (dimension > 2) {
								if (pic.isZUniform()) {
									sb.append(" " + pic.getLocationZ().infix());					
								} else {
									double locZ = subsituteFlatten(pic.getLocationZ());
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
		}		
	}
	printWriter.println("# molecules");	
	printWriter.println(SmoldynKeyword.max_mol + " " + max_mol * 3);
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

private class RowSegment {
	int starti;
	int endi;
	
	public RowSegment(int starti, int endi) {
		super();
		this.starti = starti;
		this.endi = endi;
	}
	@Override
	public String toString() {	
		return "(" + starti + ", " + endi + ")";
	}
}

private void writeSurfacesAndCompartments() throws SolverException {
//	PrintWriter tmppw = null;  
//	try {
//		tmppw = new PrintWriter("D:\\smoldyn-2.15\\surfacepoints.m");
//	} catch (FileNotFoundException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
	
	GeometrySurfaceDescription geometrySurfaceDescription = resampledGeometry.getGeometrySurfaceDescription();	
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
	printWriter.println(SmoldynKeyword.max_compartment + " " + (subVolumes.length + 1));
	printWriter.println(SmoldynKeyword.max_surface + " " + (surfaceClasses.length + dimension)); // plus the surface which are bounding walls
	printWriter.println();

	// write boundaries and wall surfaces
	writeWallSurfaces();
	
	// write surfaces
	printWriter.println("# surfaces");
	for (int sci = 0; sci < surfaceClasses.length; sci ++) {
		SurfaceClass surfaceClass = surfaceClasses[sci];
		GeometricRegion[] geometricRegions = geometrySurfaceDescription.getGeometricRegions(surfaceClass);	
		ArrayList<Node[]> triList = new ArrayList<Node[]>();	
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
//						System.out.println();
//						for (Node node : nodes) {
//							System.out.println(node);
//						}
						if (surface.getInteriorRegionIndex() == volRegionID) { // interior							
							Node[] threeCorners = new Node[3];
							threeCorners[0] = nodes[0];
							threeCorners[1] = nodes[1];
							threeCorners[2] = nodes[2];
							triList.add(threeCorners);
							if(nodes.length == 4 && dimension > 2) {
								threeCorners = new Node[3];
								threeCorners[0] = nodes[0];
								threeCorners[1] = nodes[2];
								threeCorners[2] = nodes[3];
								triList.add(threeCorners);
							}
						} else {
							Node[] threeCorners = new Node[3];
							threeCorners[0] = nodes[2];
							threeCorners[1] = nodes[1];
							threeCorners[2] = nodes[0];
							triList.add(threeCorners);
							if(nodes.length == 4 && dimension > 2) {
								threeCorners = new Node[3];
								threeCorners[0] = nodes[3];
								threeCorners[1] = nodes[2];
								threeCorners[2] = nodes[0];
								triList.add(threeCorners);
							}
						}
					}
				}
			}
		}
		
		printWriter.println(SmoldynKeyword.start_surface + " " + surfaceClass.getName());
		printWriter.println(SmoldynKeyword.max_panels + " " + SmoldynKeyword.tri + " " + triList.size());
		
//		tmppw.println("verts" + sci + "=[");
		for (Node[] threeCorners : triList) {
			printWriter.print(SmoldynKeyword.panel + " " + SmoldynKeyword.tri);
			switch (dimension) {
			case 1:
				printWriter.print(" " + threeCorners[0].getX());
				break;
			case 2:
				printWriter.print(" " + threeCorners[0].getX() + " " + threeCorners[0].getY());
//				tmppw.print(" " + threeCorners[0].getX() + " " + threeCorners[0].getY());

				if (threeCorners[0].getX() == threeCorners[1].getX() && threeCorners[0].getY() == threeCorners[1].getY()) {
					printWriter.print(" " + threeCorners[2].getX() + " " + threeCorners[2].getY());
//					tmppw.print(" " + threeCorners[2].getX() + " " + threeCorners[2].getY());
				} else {
					printWriter.print(" " + threeCorners[1].getX() + " " + threeCorners[1].getY());
//					tmppw.print(" " + threeCorners[1].getX() + " " + threeCorners[1].getY());
				}
				break;
			case 3:
				for (Node node : threeCorners) {
					printWriter.print(" " + node.getX() + " " + node.getY() + " " + node.getZ());
				}
				break;
			}
		
			printWriter.println();
//			tmppw.println();
		}
		printWriter.println(SmoldynKeyword.end_surface);
		printWriter.println();
//		tmppw.println("];");
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
	for (int svi = 0; svi < subVolumes.length; svi ++) { 		
		SubVolume sv = subVolumes[svi];
		printWriter.println(SmoldynKeyword.start_compartment + " " + sv.getName());		
		for (SurfaceClass sc : surfaceClasses) {
			if (sc.getAdjacentSubvolumes().contains(sv)) {
				printWriter.println(SmoldynKeyword.surface + " " + sc.getName());
			}
		}
		if (boundaryXSubVolumes.contains(sv)) {
			printWriter.println(SmoldynKeyword.surface + " " + VCellSmoldynKeyword.bounding_wall_surface_X);
		}
		if (dimension > 1) {
			if (boundaryYSubVolumes.contains(sv)) {			
				printWriter.println(SmoldynKeyword.surface + " " + VCellSmoldynKeyword.bounding_wall_surface_Y);
			}
			if (dimension > 2) {
				if (boundaryZSubVolumes.contains(sv)) {
					printWriter.println(SmoldynKeyword.surface + " " + VCellSmoldynKeyword.bounding_wall_surface_Z);
				}				
			}
		}
//		tmppw.println("points" + svi + "=[");
		
		// gather all the points in all the regions
		GeometricRegion[] geometricRegions = geometrySurfaceDescription.getGeometricRegions(sv);
		RegionInfo[] regionInfos = geometrySurfaceDescription.getRegionImage().getRegionInfos();
		ArrayList<RowSegment> rowSegmentList = new ArrayList<RowSegment>(); 
		ArrayList<RowSegment> lastRowSegmentList = new ArrayList<RowSegment>(); 
		for (GeometricRegion gr : geometricRegions) {			
			VolumeGeometricRegion vgr = (VolumeGeometricRegion)gr;
			for (RegionInfo ri : regionInfos) {
				if (ri.getRegionIndex() != vgr.getRegionID()) {
					continue;
				}
				int volIndex = 0;
				for (int k = 0; k < numZ; k ++){
					for (int j = 0; j < numY; j ++){
						int starti = -1;
						int endi = 0;
						lastRowSegmentList.clear();
						lastRowSegmentList.addAll(rowSegmentList);
						rowSegmentList.clear();
						for (int i = 0; i < numX; i ++, volIndex ++){
							boolean bInRegion = false;
							if (ri.isIndexInRegion(volIndex)) {
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
									if (neighbors[n] == -1 || !ri.isIndexInRegion(neighbors[n])) {
										bOnBoundary = true;
										break;
									}
								}								
								
								if (!bOnBoundary) {
									rowSegmentList.add(new RowSegment(starti, endi));
									boolean bPrint = true;
									// check if this row segment is contained in any segment in last row.
									for (RowSegment rs : lastRowSegmentList) {
										if (rs.starti <= starti && rs.endi >= endi && ((rs.endi - rs.starti + 1) / (endi - starti + 1) < 2) ) {
											bPrint = false;
											break;
										}
									}
									if (bPrint) {
										double coordX = origin.getX() + dx * midi;
										printWriter.print(SmoldynKeyword.point + " " + coordX);
//										tmppw.print(coordX);
										if (dimension > 1) {
											double coordY = origin.getY() + dy * j;
											printWriter.print(" " + coordY);
//											tmppw.print(" " + coordY);										
											if (dimension > 2) {
												double coordZ = origin.getZ() + dz * k;
												printWriter.print(" " + coordZ);
//												tmppw.print(" " + coordZ);
											}
										}
										printWriter.println();
//										tmppw.println();
									}
								}
								starti = -1;
							}							
						} // end i
					} // end j
				} // end k
			} // end for (RegionInfo
//			tmppw.println("];");
		} // end for (GeometricRegion
		
		printWriter.println(SmoldynKeyword.end_compartment);
		printWriter.println();
	}
//	tmppw.close();
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
			String variableName = getVariableName(pp.getVariable());
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




private void writeSpecies() {
	// write species
	printWriter.println("# species declarations");
	printWriter.print(SmoldynKeyword.species);
	for (ParticleVariable pv : particleVariableList) {
		printWriter.print(" " + getVariableName(pv));
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