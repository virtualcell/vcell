package org.vcell.smoldyn.converter;


import java.util.ArrayList;
import java.util.Enumeration;

import org.vcell.smoldyn.model.Boundaries;
import org.vcell.smoldyn.model.Geometryable;
import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.model.Model.Dimensionality;
import org.vcell.smoldyn.model.SpeciesState.StateType;
import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.model.util.Triangle;
import org.vcell.smoldyn.model.util.Point.PointFactory;
import org.vcell.smoldyn.simulation.Simulation;
import org.vcell.smoldyn.simulationsettings.SimulationSettings;
import org.vcell.smoldyn.test.SmoldynWriteUtilities;
import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.Origin;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.SubVolume;
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
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.MacroscopicRateConstant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathDescriptionTest;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.ParticleJumpProcess;
import cbit.vcell.math.ParticleProbabilityRate;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;



/**
 * A throw-away test class for Matt's use
 * 
 * @author mfenwick
 *
 */
public class MathDToSmoldyn {

	private MathDescription mathd;
	private Simulation simulation;
	private Model smoldynmodel;
	
	
	public MathDToSmoldyn(MathDescription mathd) throws Exception {
		this.mathd = mathd;
		this.smoldynmodel = new Model(Dimensionality.three, 
				new org.vcell.smoldyn.model.Geometry(getBoundariesFromVCell(mathd.getGeometry())));
		SimulationSettings simulationsettings = new SimulationSettings();
		this.simulation = new Simulation(smoldynmodel, simulationsettings);
		this.convert();
	}
	
	
	public static void main(String [] args) {
		MathDToSmoldyn converter;
		try {
			converter = new MathDToSmoldyn(MathDescriptionTest.getParticleExample());
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		SmoldynWriteUtilities.findFilesAndPrintConfigFile("baseorsomething", "someprefix", converter.getSimulation());
	}
	
	
	
	private static Boundaries getBoundariesFromVCell(Geometry vcellgeometry) {
		Extent extent = vcellgeometry.getExtent();
		Origin origin = vcellgeometry.getOrigin();
		Boundaries boundaries = new Boundaries(origin.getX(), extent.getX() + origin.getX(), origin.getY(), extent.getY() + origin.getY(),
				origin.getZ(), extent.getZ() + origin.getZ());
		return boundaries;
	}
	
	
	public static void convertMathdtoSmoldyn(MathDescription mathd) throws Exception {
		MathDToSmoldyn tester;
		tester = new MathDToSmoldyn(mathd);
		SmoldynWriteUtilities.findFilesAndPrintConfigFile("convert1", "converttest\\", tester.getSimulation());
	}
	
	public static Model convertMathDescriptionToSmoldynModel(MathDescription mathd) throws Exception {
		MathDToSmoldyn tester = new MathDToSmoldyn(mathd);
		return tester.smoldynmodel;
	}
	
	
	public Simulation getSimulation() {
		return this.simulation;
	}
	
	
	private void convert() {
		this.setSmoldynGeometry();
		this.setSpecies();
		this.setSpeciesStates();
		this.setParticles();
		this.setReactions();
	}
	
	
	private void setSmoldynGeometry() {
		Geometry vcellgeometry = mathd.getGeometry();
		Geometryable smoldyngeometry = smoldynmodel.getGeometry();

		GeometricRegion[] geometricRegions = vcellgeometry.getGeometrySurfaceDescription().getGeometricRegions();
		ArrayList<SurfaceGeometricRegion> surfaceRegions = new ArrayList<SurfaceGeometricRegion>();
		ArrayList<VolumeGeometricRegion> volumeRegions = new ArrayList<VolumeGeometricRegion>();
		for (int i = 0; i < geometricRegions.length; i++) {
			if (geometricRegions[i] instanceof SurfaceGeometricRegion){
				surfaceRegions.add((SurfaceGeometricRegion)geometricRegions[i]);
			}else if (geometricRegions[i] instanceof VolumeGeometricRegion){
				volumeRegions.add((VolumeGeometricRegion)geometricRegions[i]);
			} else {
				printWarning("huh?");
			}
		}
		
		for(SurfaceGeometricRegion sgr : surfaceRegions.toArray(new SurfaceGeometricRegion [surfaceRegions.size()])) {
			addSmoldynSurface(sgr, vcellgeometry, smoldyngeometry);			
		}
		for(VolumeGeometricRegion vgr : volumeRegions.toArray(new VolumeGeometricRegion [volumeRegions.size()])) {
			addSmoldynCompartment(vgr, vcellgeometry, smoldyngeometry);			
		}
	}

	private void addSmoldynSurface(GeometricRegion gr, Geometry vcellgeometry, Geometryable smoldyngeometry) {
		GeometrySurfaceDescription geoSurfaceDescription = vcellgeometry.getGeometrySurfaceDescription();
		smoldyngeometry.addSurface(gr.getName());
		VolumeGeometricRegion insideVolRegion = (VolumeGeometricRegion) gr.getAdjacentGeometricRegions()[0];
		org.vcell.smoldyn.model.Surface smoldynsurface = smoldyngeometry.getSurface(gr.getName());
		addPanels(smoldynsurface, insideVolRegion.getRegionID(), geoSurfaceDescription);
		printWarning("added surface of name: " + gr.getName());
	}
	
	private void addPanels(org.vcell.smoldyn.model.Surface smoldynsurface, Integer volRegionID, 
			GeometrySurfaceDescription geometrySurfaceDescription){
		SurfaceCollection surfaceCollection = geometrySurfaceDescription.getSurfaceCollection();
		for(int j = 0; j < surfaceCollection.getSurfaceCount(); j++) {
			Surface surface = surfaceCollection.getSurfaces(j);
			for(int k = 0; k < surface.getPolygonCount(); k++) {
				Polygon polygon = surface.getPolygons(k);
				Node [] nodes = polygon.getNodes();
				Boolean interior = null;
				if(surface.getInteriorRegionIndex() == volRegionID) {
					interior = true;
				} else if(surface.getExteriorRegionIndex() == volRegionID) {
					interior = false;
				}
				if (interior == true) {
					addTriangle(smoldynsurface, new Node [] {nodes[0], nodes[1], nodes[2]}, interior);
					if(nodes.length == 4) {
						addTriangle(smoldynsurface, new Node [] {nodes[0], nodes[2], nodes[3]}, interior);
					}
				} else if (interior == false) {
					addTriangle(smoldynsurface, new Node [] {nodes[2], nodes[1], nodes[0]}, interior);
					if(nodes.length == 4) {
						addTriangle(smoldynsurface, new Node [] {nodes[3], nodes[2], nodes[0]}, interior);
					}
				} else {//interior is null
					throwRuntimeException("neither interior nor exterior???");						
				}
			}
		}
	}
	
	private void addTriangle(org.vcell.smoldyn.model.Surface smoldynsurface, Node [] nodes, Boolean interior) {
		Point [] points = new Point [3];
		PointFactory pf = this.smoldynmodel.getPointFactory();
		points[0] = pf.getNewPoint(nodes[0].getX(), nodes[0].getY(), nodes[0].getZ());
		points[1] = pf.getNewPoint(nodes[1].getX(), nodes[1].getY(), nodes[1].getZ());
		points[2] = pf.getNewPoint(nodes[2].getX(), nodes[2].getY(), nodes[2].getZ());
		smoldynsurface.addPanel(new Triangle(null, points[0], points[1], points[2]));
	}
	
	private void addSmoldynCompartment(VolumeGeometricRegion volumegr, Geometry vcellgeometry, Geometryable smoldyngeometry) {
		GeometrySurfaceDescription geoSurfaceDescription = vcellgeometry.getGeometrySurfaceDescription();
		SubVolume subVolume = (SubVolume)geoSurfaceDescription.getGeometryClass(volumegr);
		GeometricRegion[] adjacentRegions = volumegr.getAdjacentGeometricRegions();
		ArrayList<String> boundingsurfacenames = new ArrayList<String>();
		for (int j = 0; j < adjacentRegions.length; j++) {
			if (adjacentRegions[j] instanceof SurfaceGeometricRegion){
				boundingsurfacenames.add(adjacentRegions[j].getName()/*surfaceClass.getName()*/);
			}
		}
		Coordinate coord = getAnyCoordinate(vcellgeometry, volumegr);
		smoldyngeometry.addCompartment(subVolume.getName(), boundingsurfacenames.toArray(new String [boundingsurfacenames.size()]), 
				new Point [] {convertCoordinateToPoint(coord)});
	}	
	
	private Point convertCoordinateToPoint(Coordinate coordinate) {
		PointFactory pf = this.smoldynmodel.getPointFactory();
		Point point = pf.getNewPoint(coordinate.getX(), coordinate.getY(), coordinate.getZ());
		return point;
	}
	
	private Coordinate getAnyCoordinate(Geometry geometry, GeometricRegion region){
		RegionImage regionImage = geometry.getGeometrySurfaceDescription().getRegionImage();
		int numX = regionImage.getNumX();
		int numY = regionImage.getNumY();
		int numZ = regionImage.getNumZ();
		double totalX = 0;
		double totalY = 0;
		double totalZ = 0;
		Origin origin = geometry.getOrigin();
		Extent extent = geometry.getExtent();
		int count = 0;
		GeometricRegion[] allRegions = geometry.getGeometrySurfaceDescription().getGeometricRegions();
		for (int indexX = 0; indexX < numX; indexX++){
			for (int indexY = 0; indexY < numY; indexY++){
				for (int indexZ = 0; indexZ < numZ; indexZ++){
					int offset = indexX + (numX)*indexY + numX*numY*indexZ;
					RegionInfo regionInfo = regionImage.getRegionInfoFromOffset(offset);
					if (allRegions[regionInfo.getRegionIndex()].equals(region)){
						switch (geometry.getDimension()){
						case 2: {
							totalX += origin.getX()+(indexX*extent.getX()/(numX-1));
							totalY += origin.getY()+(indexY*extent.getY()/(numY-1));
							totalZ += origin.getZ()+extent.getZ()/2;
							break;
						}
						case 3: {
							totalX += origin.getX()+(indexX*extent.getX()/(numX-1));
							totalY += origin.getY()+(indexY*extent.getY()/(numY-1));
							totalZ += origin.getZ()+(indexZ*extent.getZ()/(numZ-1));
							break;
						}
						default:
							throw new RuntimeException("unsupported geometry dimension");
						}
						count++;
					}
				}
			}
		}
		if (count==0){
			throw new RuntimeException("cant find region "+region.getName()+" in geometry");
		}
		return new Coordinate(totalX/count, totalY/count, totalZ/count);
	}
	
	private void setSpecies() {
		Enumeration<Variable> variables = mathd.getVariables();
		while(variables.hasMoreElements()) {
			smoldynmodel.addSpecies(variables.nextElement().getName());
		}
	}
	
	private void setSpeciesStates() {
		// can't write speciesstates as these come from a SimulationContext and I only have a MathDescription
	}
	
	private void setParticles() {
		
	}
	
	private void setReactions() {
		Enumeration<SubDomain> subdomains = mathd.getSubDomains();
		while(subdomains.hasMoreElements()) {
			SubDomain subdomain = subdomains.nextElement();
			for(ParticleJumpProcess pjp : subdomain.getParticleJumpProcesses().toArray(
					new ParticleJumpProcess [subdomain.getParticleJumpProcesses().size()])) {
				String name = pjp.getName();
				String [] reactants = new String [2];
				String [] products = new String [2];
				int reactindex = 0, productindex = 0;
				for(Action a : pjp.getActions().toArray(new Action [pjp.getActions().size()])) {
					printDebuggingStatement("reaction action: " + a.getOperation());
					printDebuggingStatement("reaction species: " + a.getVar().getName());
					if(a.getOperation() == Action.ACTION_CREATE) {
						reactants[reactindex++] = a.getVar().getName();
					} else if(a.getOperation() == Action.ACTION_DESTROY) {
						products[productindex++] = a.getVar().getName();
					} else {
						printWarning("skipping action due to problem (unexpected operation): " + a.getOperation());
					}
				}
				double rateconstant = 0;
				try {
					ParticleProbabilityRate ppr = pjp.getParticleProbabilityRate();
					if(ppr instanceof MacroscopicRateConstant) {
						rateconstant = ((MacroscopicRateConstant) ppr).getExpression().evaluateConstant();
					} else {
						throw new RuntimeException("particle probability rate not supported");
					}
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				if(subdomain instanceof CompartmentSubDomain) {
					String compartname = subdomain.getName();
					smoldynmodel.addVolumeReaction(name, compartname, reactants[0], reactants[1], products[0], products[1], 
							(float) rateconstant);
				} else if (subdomain instanceof MembraneSubDomain){
					String surfacename = subdomain.getName();
					smoldynmodel.addSurfaceReaction(name, surfacename, reactants[0], StateType.back, reactants[1], StateType.back, 
							products[0], StateType.back, products[1], StateType.back, rateconstant);
				} else {
					printWarning("I have something weird, it's a " + subdomain.getClass());
				}
			}
		}
	}
	
	private static void throwRuntimeException(String message) {
		throw new RuntimeException(message);
	}
	
	private static void printWarning(String message) {
		System.err.println("converttest warning: " + message);
	}
	
	private static void printDebuggingStatement(String message) {
		System.out.println("converttest debugging statement: " + message);
	}
}

