package org.vcell.solver.smoldyn;

import java.awt.Color;
import java.beans.PropertyVetoException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.output.NullWriter;
import org.vcell.solver.smoldyn.SmoldynFileWriter.NotAConstantException;
import org.vcell.util.BeanUtils;
import org.vcell.util.ISize;

import cbit.image.ImageException;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.Node;
import cbit.vcell.geometry.surface.Polygon;
import cbit.vcell.geometry.surface.Surface;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.Triangle;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.ParticleProperties.ParticleInitialConditionCount;
import cbit.vcell.math.ParticleVariable;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.render.Vect3d;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.server.SolverFileWriter;
import cbit.vcell.solvers.MembraneElement;

public class SmoldynSurfaceTessellator extends SolverFileWriter {

	private static final String PANEL_TRIANGLE_NAME_PREFIX = "tri";
	protected Color[] colors = null;

	protected static class TrianglePanel {
			public final String name;
			public final Triangle triangle;
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

	protected final Simulation simulation;
	protected final MathDescription mathDesc;
	protected Geometry resampledGeometry = null;
	protected boolean bHasNoSurface = false;
	protected int dimension = 1;
	protected HashMap<MembraneSubDomain, ArrayList<TrianglePanel> > membraneSubdomainTriangleMap = null;
	protected ArrayList<ParticleVariable> particleVariableList = null;
	protected ArrayList<ClosestTriangle> closestTriangles = null;
	protected Map<Polygon, MembraneElement> polygonMembaneElementMap = null;

	protected enum VCellSmoldynKeyword {
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

	/**
	 * find and stores information about which {@link TrianglePanel} is closest to a
	 * {@link ParticleInitialConditionCount}
	 */
	protected static class ClosestTriangle {
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

	public SmoldynSurfaceTessellator(PrintWriter pw, SimulationTask simTask, boolean messaging) {
		super(pw, simTask, messaging);
		simulation = simTask.getSimulation();
		mathDesc = simulation.getMathDescription();
	}

	public SmoldynSurfaceTessellator(Simulation sim) {
		super(new PrintWriter(new NullWriter( )),null,false);
			simulation = sim;
			mathDesc = simulation.getMathDescription();
		try {
			Geometry geo = mathDesc.getGeometry();
			cloneAndResample(geo);
			dimension = geo.getDimension();
			writeSurfaces( );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param name not null
	 * @return tessellation of membrane, or empty list if no such element
	 */
	public List<Triangle> getTessellation(String name) {
		Objects.requireNonNull(name);
		Optional<MembraneSubDomain> r = membraneSubdomainTriangleMap.keySet().stream().filter( (m) -> name.equals(m.getName())).findAny();
		if (r.isPresent()) {
			ArrayList<TrianglePanel> tpl = membraneSubdomainTriangleMap.get(r.get( ));
			if (tpl != null) {
				ArrayList<Triangle> rval = new ArrayList<>(tpl.size());
				for (TrianglePanel tp: tpl) {
					rval.add(tp.triangle);
				}
				return rval;
			}
		}
		return Collections.emptyList();
	}

	@Override
	public void write(String[] parameterNames) throws Exception {
		throw new UnsupportedOperationException("Use SmoldynFileWriter" );
	}

	protected void cloneAndResample(Geometry geometry) throws Exception {
	// clone and resample geometry
			resampledGeometry = (Geometry) BeanUtils.cloneSerializable(geometry);
			GeometrySurfaceDescription geoSurfaceDesc = resampledGeometry.getGeometrySurfaceDescription();
			ISize newSize = simulation.getMeshSpecification().getSamplingSize();
			geoSurfaceDesc.setVolumeSampleSize(newSize);
			geoSurfaceDesc.updateAll();
			bHasNoSurface = geoSurfaceDesc.getSurfaceClasses() == null || geoSurfaceDesc.getSurfaceClasses().length == 0;
	}

	protected void writeSurfaces()
			throws SolverException, ImageException, PropertyVetoException, GeometryException, ExpressionException {

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
						Color c = colorForSurface(sci);
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
				}

			}

	/**
	 * placeholder for {@link SmoldynFileWriter }
	 * @throws SolverException
	 */
	protected void writeWallSurfaces() throws SolverException { }

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
	 * @param sci index of surface ?
	 * @return appropriate color or default
	 */
	Color colorForSurface(int sci) {
		return Color.BLACK;
	}



}
