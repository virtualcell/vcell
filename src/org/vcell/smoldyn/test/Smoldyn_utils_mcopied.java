package org.vcell.smoldyn.test;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import cbit.plot.Plot2DPanel;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.RegionImage;
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
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Feature;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;


/**
 * 
 *
 */
public class Smoldyn_utils_mcopied {
		
	/**
	 * @param simContext
	 * @param pw
	 * @throws ExpressionException
	 */
	public static void writeModelFile(SimulationContext simContext, PrintWriter pw) throws ExpressionException {
		writeSimGraphics(simContext, pw);
		writeSpeciesNames(simContext.getReactionContext().getSpeciesContextSpecs(), pw);
		writeGeometry(simContext, pw);
		writeMolecules(pw);
		writeSpeciesStates(simContext, pw);
		writeReactions(simContext, pw);
		writeTime(pw);
		writeEndfile(pw);
	}
	
	/**
	 * Sets up the conditions for turning a VCell {@link SimulationContext} into a Smoldyn config file:  locates Smoldyn, locates a 
	 * VCell model, makes a {@link PrintWriter}, and starts it up.
	 * 
	 * @param simContext
	 * @throws IOException
	 * @throws ExpressionException
	 */
	public static void doVCellToSmoldynTest(SimulationContext simContext) throws IOException, ExpressionException {
		File smoldynInstallDir = new File("./Smoldyn");
		File smoldynExecutable = new File(smoldynInstallDir,"smoldyn.exe");
		File modelDir = new File(smoldynInstallDir,"vcell");
		if (!modelDir.exists()){
			modelDir.mkdir();
		}
		File modelFile = new File(modelDir,"model.txt");
		
		FileWriter modelWriter = new FileWriter(modelFile);
		PrintWriter modelPW = new PrintWriter(modelWriter);
		
		System.out.println("writing model file '"+modelFile.getAbsolutePath()+"' ... ");
		writeModelFile(simContext,modelPW);
		System.out.println("wrote model file '"+modelFile.getAbsolutePath()+"'");
		modelPW.close();
		
		ArrayList<String> commandList = new ArrayList<String>();
		commandList.add("cmd.exe");
		commandList.add("/K");
		commandList.add(smoldynExecutable.getAbsolutePath());
		commandList.add(modelFile.getAbsolutePath());
		System.out.println("invoking command: "+commandList.toString());
		ProcessBuilder pb = new ProcessBuilder(commandList);
		@SuppressWarnings("unused")
		Process process = pb.start();
//		try {
//			process.waitFor();
//		}catch (InterruptedException e){
//			e.printStackTrace(System.out);
//		}
	}
	
	private static void writeSpeciesNames(SpeciesContextSpec [] speciesContextSpecs, PrintWriter pw) {
		pw.println("max_species "+speciesContextSpecs.length);
		for (int i = 0; i < speciesContextSpecs.length; i++) {
			pw.print("species "+speciesContextSpecs[i].getSpeciesContext().getName());
		}
		pw.println();
	}


	private static void writeGeometry(SimulationContext simContext, PrintWriter pw) {

		Geometry geometry = simContext.getGeometry();
		writeBoundaries(geometry, pw);

		/**
		 * Getting the compartments and surfaces out of the geometry
		 */
		GeometricRegion[] geometricRegions = geometry.getGeometrySurfaceDescription().getGeometricRegions();
		ArrayList<SurfaceGeometricRegion> surfaceRegions = new ArrayList<SurfaceGeometricRegion>();
		ArrayList<VolumeGeometricRegion> volumeRegions = new ArrayList<VolumeGeometricRegion>();
		for (int i = 0; i < geometricRegions.length; i++) {
			if (geometricRegions[i] instanceof SurfaceGeometricRegion){
				surfaceRegions.add((SurfaceGeometricRegion)geometricRegions[i]);
			}else if (geometricRegions[i] instanceof VolumeGeometricRegion){
				volumeRegions.add((VolumeGeometricRegion)geometricRegions[i]);
			}
		}		
		writeSurfaces(surfaceRegions, pw, geometry);
		writeCompartments(volumeRegions, pw, geometry);
	}


	private static void writeEndfile(PrintWriter pw) {
		pw.println("end_file");
	}


	private static void writeMolecules(PrintWriter pw) {
		pw.println("max_mol 80000");
		pw.println("molperbox 2");
		pw.println("rand_seed 0");
		pw.println();
	}


	private static void writeSpeciesStates(SimulationContext simContext, PrintWriter pw) throws ExpressionException {
		Geometry geometry = simContext.getGeometry();
		SpeciesContextSpec[] speciesContextSpecs = simContext.getReactionContext().getSpeciesContextSpecs();
		Color[] autoContrastColors = Plot2DPanel.generateAutoColor(speciesContextSpecs.length, new Color(0.0f,0.0f,0.0f), 0);
		for (int i = 0; i < speciesContextSpecs.length; i++) {
			SpeciesContextSpec scs = speciesContextSpecs[i];
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(scs.getSpeciesContext().getStructure());
			String name = scs.getSpeciesContext().getName();
			pw.println("difc "+name+" "+scs.getDiffusionParameter().getExpression().infix());
			Double velX = null;
			Double velY = null;
			Double velZ = null;
			if (scs.getVelocityXParameter()!=null && scs.getVelocityXParameter().getExpression()!=null){
				velX = scs.getVelocityXParameter().getConstantValue();
			}
			if (scs.getVelocityYParameter()!=null && scs.getVelocityYParameter().getExpression()!=null){
				velY = scs.getVelocityYParameter().getConstantValue();
			}
			if (scs.getVelocityZParameter()!=null && scs.getVelocityZParameter().getExpression()!=null){
				velZ = scs.getVelocityZParameter().getConstantValue();
			}
			if (velX!=null && velY!=null && velZ!=null){
				pw.println("drift "+velX+" "+velY+" "+velZ);
			}
			float[] color = autoContrastColors[i].getRGBColorComponents(new float[3]);
			pw.println("color "+name+" "+color[0]+" "+color[1]+" "+color[2]);
			pw.println("display_size "+name+" 5");

			double concentration = scs.getInitialConcentrationParameter().getExpression().evaluateConstant();
			VCUnitDefinition concUnits = scs.getInitialConcentrationParameter().getUnitDefinition();
			GeometrySurfaceDescription geoSurfaceDescription = geometry.getGeometrySurfaceDescription();
			GeometricRegion[] regions = geoSurfaceDescription.getGeometricRegions(sm.getGeometryClass());
			if (regions.length!=1){
//				throw new RuntimeException("expected exactly one region for geometryClass "+sm.getGeometryClass().toString());
				System.err.println("something weird happened");
			}
			double size = regions[0].getSize();
			Integer numMolecules = null;
			if (concUnits.equals(VCUnitDefinition.UNIT_uM)){
				numMolecules = (int)Math.floor(concentration*602*size);
			}else if (concUnits.equals(VCUnitDefinition.UNIT_molecules_per_um2)){
				numMolecules = (int)Math.floor(concentration*size);
			}else{
				throw new RuntimeException("unexpected units for initial concentration '"+concUnits.getSymbol());
			}
			if (numMolecules>0){
				if (scs.getSpeciesContext().getStructure() instanceof Feature){
					//Coordinate coord = getAnyCoordinate(geometry, regions[0]);
					//pw.println("mol "+numMolecules+" "+name+" "+coord.getX()+" "+coord.getY()+" "+coord.getZ());
					pw.println("compartment_mol "+numMolecules+" "+name+" "+sm.getGeometryClass().getName());
				}else if (scs.getSpeciesContext().getStructure() instanceof Membrane){
					pw.println("surface_mol "+numMolecules+" "+name+" "+sm.getGeometryClass().getName());
				}
			}
		}
		pw.println();
	}


	private static void writeReactions(SimulationContext simContext, PrintWriter pw) {
		ReactionSpec[] reactionSpecs = simContext.getReactionContext().getReactionSpecs();
		for (int i = 0; i < reactionSpecs.length; i++) {
			ReactionSpec reactionSpec = reactionSpecs[i];
			if (reactionSpec.isExcluded()){
				continue;
			}
			if (reactionSpec.isFast()){
				throw new RuntimeException("fast spatial stochastic reactions not yet implemented");
			}
			
			ReactionStep reactionStep = reactionSpec.getReactionStep();
			MassActionKinetics maKinetics = (MassActionKinetics)reactionStep.getKinetics();
			ReactionParticipant[] rps = reactionStep.getReactionParticipants();

			pw.print("reaction "+reactionStep.getName()+"_forward");
			int count = 0;
			for (int j = 0; j < rps.length; j++) {
				if (rps[j] instanceof Reactant){
					if (count++ > 0){
						pw.print(" +");
					}
					pw.print(" "+rps[j].getSpeciesContext().getName());
				}
			}
			pw.print(" -> ");
			count = 0;
			for (int j = 0; j < rps.length; j++) {
				if (rps[j] instanceof Product){
					if (count++ > 0){
						pw.print(" +");
					}
					pw.print(" "+rps[j].getSpeciesContext().getName());
				}
			}
			pw.println(" "+maKinetics.getForwardRateParameter().getExpression().infix());
			
			pw.print("reaction "+reactionStep.getName()+"_reverse");
			count = 0;
			for (int j = 0; j < rps.length; j++) {
				if (rps[j] instanceof Product){
					if (count++ > 0){
						pw.print(" +");
					}
					pw.print(" "+rps[j].getSpeciesContext().getName());
				}
			}
			pw.print(" -> ");
			count = 0;
			for (int j = 0; j < rps.length; j++) {
				if (rps[j] instanceof Reactant){
					if (count++ > 0){
						pw.print(" +");
					}
					pw.print(" "+rps[j].getSpeciesContext().getName());
				}
			}
			pw.println(" "+maKinetics.getReverseRateParameter().getExpression().infix());
		}
		pw.println();
	}


	private static void writeTime(PrintWriter pw) {
		pw.println();
		pw.println("time_start 0");
		pw.println("time_stop 200");
		pw.println("time_step 0.01");
		pw.println();
	}


	private static void writeBoundaries(Geometry geometry, PrintWriter pw) {
		pw.println("dim "+geometry.getDimension());
		Origin origin = geometry.getOrigin();
		Extent extent = geometry.getExtent();
		pw.println("boundaries 0 "+origin.getX()+" "+(origin.getX()+extent.getX()));
		pw.println("boundaries 1 "+origin.getY()+" "+(origin.getY()+extent.getY()));
		pw.println("boundaries 2 "+origin.getZ()+" "+(origin.getZ()+extent.getZ()));
		pw.println();
	}


	private static void writeSimGraphics(SimulationContext simContext, PrintWriter pw) {
		/**
		 * Printing out graphics and dimensions
		 */
		pw.println("# vcell model for simulationContext "+simContext.getName());
		pw.println("graphics opengl");
		pw.println("graphic_iter 1");
		pw.println("graphic_delay 100");
		pw.println();
	}
	
	private static void writeSurfaces(ArrayList<SurfaceGeometricRegion> surfaceRegions, PrintWriter pw,	Geometry geometry) {
		/**
		 * for each surface, print out its stuff
		 */
		GeometrySurfaceDescription geoSurfaceDescription = geometry.getGeometrySurfaceDescription();
		pw.println("max_surface "+(surfaceRegions.size()+1)); // add one in case it is zero.
		for (int i = 0; i < surfaceRegions.size(); i++) {
			SurfaceClass surfaceClass = (SurfaceClass)geoSurfaceDescription.getGeometryClass(surfaceRegions.get(i));
			pw.println("start_surface "+surfaceClass.getName());
			pw.println("action both all reflect");
			pw.println("polygon front edge"); //face
			pw.println("polygon back edge");
			VolumeGeometricRegion insideVolRegion = (VolumeGeometricRegion)surfaceRegions.get(i).getAdjacentGeometricRegions()[0];
			printTriangulatedPanel(pw, insideVolRegion, geoSurfaceDescription);
			pw.println("end_surface");
		}
		pw.println();
	}
	
	private static void writeCompartments(ArrayList<VolumeGeometricRegion> volumeRegions, PrintWriter pw, Geometry geometry) {
		/**
		 * for each compartment, print out its stuff
		 */
		GeometrySurfaceDescription geoSurfaceDescription = geometry.getGeometrySurfaceDescription();
		pw.println("max_compartment "+(volumeRegions.size()+1)); // add one in case it is zero.
		for (int i = 0; i < volumeRegions.size(); i++) {
			SubVolume subVolume = (SubVolume)geoSurfaceDescription.getGeometryClass(volumeRegions.get(i));
			pw.println("start_compartment "+subVolume.getName());
			GeometricRegion[] adjacentRegions = volumeRegions.get(i).getAdjacentGeometricRegions();
			for (int j = 0; j < adjacentRegions.length; j++) {
				if (adjacentRegions[j] instanceof SurfaceGeometricRegion){
					SurfaceClass surfaceClass = (SurfaceClass)geoSurfaceDescription.getGeometryClass(adjacentRegions[j]);
					pw.println("surface "+surfaceClass.getName());
				}
			}
			Coordinate coord = getAnyCoordinate(geometry, volumeRegions.get(i));
			pw.println("point "+coord.getX()+" "+coord.getY()+" "+coord.getZ()+"  # center point");
			pw.println("end_compartment");
		}
		pw.println();
	}
	
	
	
	/**
	 * @param pw
	 * @param volRegion
	 * @param geometrySurfaceDescription
	 */
	public static void printTriangulatedPanel(PrintWriter pw, VolumeGeometricRegion volRegion, GeometrySurfaceDescription geometrySurfaceDescription){
		SurfaceCollection surfaceCollection = geometrySurfaceDescription.getSurfaceCollection();
		
		int polygonCount = 0;
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		//
		// find surfaces that border this region (and invert normals if necessary)
		//
		Surface surface;
		Polygon polygon;
		Node [] nodes;
		for (int j = 0; j < surfaceCollection.getSurfaceCount(); j++){
			surface = surfaceCollection.getSurfaces(j);
			if (surface.getInteriorRegionIndex() == volRegion.getRegionID()){
				//
				// need Counter Clockwise for "out" with respect to this volume region, (Already OK)
				//
				for (int k = 0; k < surface.getPolygonCount(); k++){
					polygon = surface.getPolygons(k);
					nodes = polygon.getNodes();
					if (polygon.getNodeCount()==3){
						print_triangle(writer, nodes[0], nodes[1], nodes[2]);
						polygonCount++;
					} else if ((polygon.getNodeCount()==4)) {
						print_triangle(writer, nodes[0], nodes[1], nodes[2]);
						polygonCount++;
						print_triangle(writer, nodes[0], nodes[2], nodes[3]);
						polygonCount++;
					}
				}
			} else if (surface.getExteriorRegionIndex() == volRegion.getRegionID()){
				//
				// need Counter Clockwise for "out" with respect to this volume region, (MUST FLIP NORMAL AND RE-ORDER VERTICES)
				//
				for (int k = 0; k < surface.getPolygonCount(); k++){
					polygon = surface.getPolygons(k);
					nodes = polygon.getNodes();
					if (polygon.getNodeCount()==3){
						print_triangle(writer, nodes[2], nodes[1], nodes[0]);
						polygonCount++;
					} else if (polygon.getNodeCount()==4){
						print_triangle(writer, nodes[2], nodes[1], nodes[0]);
						polygonCount++;
						print_triangle(writer, nodes[3], nodes[2], nodes[0]);
						polygonCount++;
					}
				}
			}
		}
		pw.println("max_panels tri "+polygonCount);
		writer.flush();
		pw.write(stringWriter.getBuffer().toString());
	}
	
	/**
	 * @param writer
	 * @param node1
	 * @param node2
	 * @param node3
	 */
	private static void print_triangle(PrintWriter writer, Node node1, Node node2, Node node3) {
		writer.print("panel tri");
		writer.print(" "+node1.getX()+" "+node1.getY()+" "+node1.getZ());
		writer.print(" "+node2.getX()+" "+node2.getY()+" "+node2.getZ());
		writer.print(" "+node3.getX()+" "+node3.getY()+" "+node3.getZ());
		writer.println();
	}
	
	/**
	 * @param geometry
	 * @param region
	 * @return
	 */
	public static Coordinate getAnyCoordinate(Geometry geometry, GeometricRegion region){
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
	
	/**
	 * @param args
	 */
	public final static void main(String args[]){
		try {
			if (args.length!=1){
				System.out.println("usage: SmoldynUtils vcmlFilename");
				throw new RuntimeException("improper commandline arguments");
			}
			BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(new File(args[0])));
			SimulationContext simContext = bioModel.getSimulationContexts()[0];
			simContext.getGeometry().getGeometrySurfaceDescription().setVolumeSampleSize(new ISize(20,20,20));
			simContext.getGeometry().getGeometrySurfaceDescription().updateAll();
			
			doVCellToSmoldynTest(simContext);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

}
