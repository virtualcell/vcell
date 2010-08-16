package org.vcell.smoldyn.test;
//package org.vcell.smoldyn.vcellstuff;
//
//
//import java.awt.Color;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//
//import org.vcell.util.Coordinate;
//import org.vcell.util.Extent;
//import org.vcell.util.ISize;
//import org.vcell.util.Origin;
//
//import cbit.plot.Plot2DPanel;
//import cbit.vcell.biomodel.BioModel;
//import cbit.vcell.geometry.Geometry;
//import cbit.vcell.geometry.RegionImage;
//import cbit.vcell.geometry.SubVolume;
//import cbit.vcell.geometry.SurfaceClass;
//import cbit.vcell.geometry.RegionImage.RegionInfo;
//import cbit.vcell.geometry.surface.GeometricRegion;
//import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
//import cbit.vcell.geometry.surface.Node;
//import cbit.vcell.geometry.surface.Polygon;
//import cbit.vcell.geometry.surface.Surface;
//import cbit.vcell.geometry.surface.SurfaceCollection;
//import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
//import cbit.vcell.geometry.surface.VolumeGeometricRegion;
//import cbit.vcell.mapping.ReactionSpec;
//import cbit.vcell.mapping.SimulationContext;
//import cbit.vcell.mapping.SpeciesContextSpec;
//import cbit.vcell.mapping.StructureMapping;
//import cbit.vcell.model.MassActionKinetics;
//import cbit.vcell.model.Product;
//import cbit.vcell.model.Reactant;
//import cbit.vcell.model.ReactionParticipant;
//import cbit.vcell.model.ReactionStep;
//import cbit.vcell.parser.ExpressionException;
//import cbit.vcell.units.VCUnitDefinition;
//import cbit.vcell.xml.XMLSource;
//import cbit.vcell.xml.XmlHelper;
//
//
//public class SmoldynUtils2 {
//	
//	public void test(SimulationContext simContext) throws IOException, ExpressionException {
//		File smoldynInstallDir = new File("./Smoldyn");
//		File smoldynExecutable = new File(smoldynInstallDir,"smoldyn.exe");
//		File modelDir = new File(smoldynInstallDir,"vcell");
//		if (!modelDir.exists()){
//			modelDir.mkdir();
//		}
//		File modelFile = new File(modelDir,"model2.txt");
//		
//		FileWriter modelWriter = new FileWriter(modelFile);
//		PrintWriter modelPW = new PrintWriter(modelWriter);
//		System.out.println("writing model file '"+modelFile.getAbsolutePath()+"' ... ");
//		Model smoldynModel = getModel(simContext);
//		writeModel(smoldynModel,modelPW);
//		System.out.println("wrote model file '"+modelFile.getAbsolutePath()+"'");
//		modelPW.close();
//		ArrayList<String> commandList = new ArrayList<String>();
//		commandList.add("cmd.exe");
//		commandList.add("/K");
//		commandList.add(smoldynExecutable.getAbsolutePath());
//		commandList.add(modelFile.getAbsolutePath());
//		System.out.println("invoking command: "+commandList.toString());
//		ProcessBuilder pb = new ProcessBuilder(commandList);
//		@SuppressWarnings("unused")
//		Process process = pb.start();
////		try {
////			process.waitFor();
////		}catch (InterruptedException e){
////			e.printStackTrace(System.out);
////		}
//	}
//	private void writeModel(Model model, PrintWriter pw){
//		pw.println("# model "+model.name);
//		pw.println();
//		pw.println("graphics opengl");
//		pw.println("graphic_iter 1");
//		pw.println("graphic_delay 100");
//		pw.println();
//		pw.println("dim "+model.geometry.getDim());
//
//		pw.println();
//		pw.println("boundaries 0 "+model.geometry.getMinX()+" "+model.geometry.getMaxX());
//		pw.println("boundaries 1 "+model.geometry.getMinY()+" "+model.geometry.getMaxY());
//		pw.println("boundaries 2 "+model.geometry.getMinZ()+" "+model.geometry.getMaxZ());
//
//		pw.println("max_species "+model.speciesList.size());
//		pw.println("max_surface "+(model.geometry.surfaceList.size()+1));
//		pw.println("max_compartment "+(model.geometry.compartmentList.size()+1));
//		for (int i = 0; i < model.geometry.domainTypeList.size(); i++) {
//			DomainType domainType = model.geometry.domainTypeList.get(i);
//			if (domainType.getDimension()!=2){
//				continue;
//			}
//			org.vcell.smoldyn.vcellstuff.Surface[] surfacesFromSameDomain = model.geometry.getSurfaces(domainType);
//			int numPanelsForDomain = 0;
//			for (int j = 0; j < surfacesFromSameDomain.length; j++) {
//				numPanelsForDomain += surfacesFromSameDomain[j].getPanels().size();
//			}
//			for (int j = 0; j < surfacesFromSameDomain.length; j++) {
//				pw.println("start_surface "+domainType.getName()+" # region "+surfacesFromSameDomain[j].getName());
//				pw.println("action both all reflect");
//				pw.println("polygon front edge"); //face
//				pw.println("polygon back edge");
//				if (j==0){
//					pw.println("max_panels tri "+numPanelsForDomain);
//				}
//				List<Panel> panels = surfacesFromSameDomain[j].getPanels();
//				for (int k = 0; k < panels.size(); k++) {
//					if (panels.get(k) instanceof Triangle){
//						Triangle triangle = (Triangle)panels.get(k);
//						pw.print("panel tri");
//						pw.print(" "+triangle.getPoint1().getX()+" "+triangle.getPoint1().getY()+" "+triangle.getPoint1().getZ());
//						pw.print(" "+triangle.getPoint2().getX()+" "+triangle.getPoint2().getY()+" "+triangle.getPoint2().getZ());
//						pw.print(" "+triangle.getPoint3().getX()+" "+triangle.getPoint3().getY()+" "+triangle.getPoint3().getZ());
//						pw.println();
//					}
//				}
//				pw.println("end_surface");
//			}
//		}
//		for (int i = 0; i < model.geometry.domainTypeList.size(); i++) {
//			DomainType domainType = model.geometry.domainTypeList.get(i);
//			if (domainType.getDimension()!=3){
//				continue;
//			}
//			Compartment[] compartmentsFromSameDomain = model.geometry.getCompartments(domainType);
//			for (int j = 0; j < compartmentsFromSameDomain.length; j++) {
//				pw.println("start_compartment "+domainType.getName()+" # region "+compartmentsFromSameDomain[j].getName());
//				if (j==0){
//					HashSet<String> referencedSurfaceDomainNames = new HashSet<String>();
//					for (int k = 0; k < compartmentsFromSameDomain[j].getSurfaces().size(); k++) {
//						String referencedSurfaceDomainName = compartmentsFromSameDomain[j].getSurfaces().get(k).getDomainType().getName();
//						if (!referencedSurfaceDomainNames.contains(referencedSurfaceDomainName)){
//							pw.println("surface "+referencedSurfaceDomainName);
//							referencedSurfaceDomainNames.add(referencedSurfaceDomainName);
//						}
//					}
//				}
//				for (int k = 0; k < compartmentsFromSameDomain[j].getPoints().size(); k++) {
//					Coordinate coord = compartmentsFromSameDomain[j].getPoints().get(k);
//					pw.println("point "+coord.getX()+" "+coord.getY()+" "+coord.getZ()+"  # center point");
//				}
//				pw.println("end_compartment");
//			}		
//		}
//		
//		int totalNumMolecules = 0;
//		for (int i = 0; i < model.speciesList.size(); i++) {
//			Species species = model.speciesList.get(i);
//			pw.println("species "+species.getName());
//			totalNumMolecules += species.getNumMolecules();
//		}
//		pw.println();
//		
//		pw.println("max_mol "+(totalNumMolecules*10/9));
//		pw.println("molperbox 2");
//		pw.println("rand_seed 0");
//		pw.println("");
//		for (int i = 0; i < model.speciesList.size(); i++) {
//			Species species = model.speciesList.get(i);
//			if (species.getDifc()!=null){
//				pw.println("difc "+species.getName()+" "+species.getDifc());
//			}
//			if (species.getDriftX()!=null || species.getDriftY()!=null || species.getDriftZ()!=null){
//				switch (model.geometry.getDim()){
//				case 1:{
//					pw.println("drift "+((species.getDriftX()!=null)?(species.getDriftX()):(0.0)));
//					break;
//				}
//				case 2:{
//					pw.println("drift "+((species.getDriftX()!=null)?(species.getDriftX()):(0.0))+
//									" "+((species.getDriftY()!=null)?(species.getDriftY()):(0.0)));
//					break;
//				}
//				case 3:{
//					pw.println("drift "+((species.getDriftX()!=null)?(species.getDriftX()):(0.0))+
//									" "+((species.getDriftY()!=null)?(species.getDriftY()):(0.0))+
//									" "+((species.getDriftZ()!=null)?(species.getDriftZ()):(0.0)));
//					break;
//				}
//				}
//			}
//			float[] color = species.getColor().getRGBColorComponents(new float[3]);
//			pw.println("color "+species.getName()+" "+color[0]+" "+color[1]+" "+color[2]);
//			pw.println("display_size "+species.getName()+" 5");
//
//			if (species.getNumMolecules() != null && species.getNumMolecules()>0){
//				if (species.getDomains()[0] instanceof Compartment){
//					pw.println("compartment_mol "+species.getNumMolecules()+" "+species.getName()+" "+species.getDomains()[0].getDomainType().getName());
//				}else if (species.getDomains()[0] instanceof org.vcell.smoldyn.vcellstuff.Surface){
//					pw.println("surface_mol "+species.getNumMolecules()+" "+species.getName()+" "+species.getDomains()[0].getDomainType().getName());
//				}else{
//					throw new RuntimeException("unknown compartment type "+species.getDomains()[0]+" for species "+species.getName());
//				}
//			}
//		}
//		pw.println();
//		for (int i = 0; i < model.reactionList.size(); i++) {
//			Reaction reaction = model.reactionList.get(i);
//			pw.print("reaction "+reaction.getName());
//			int count = 0;
//			for (int j = 0; j < reaction.getReactants().size(); j++) {
//				org.vcell.smoldyn.vcellstuff.ReactionParticipant reactant = reaction.getReactants().get(j);
//				for (int k=0;k<reactant.getStochiometry();k++){
//					if (count++ > 0){
//						pw.print(" +");
//					}
//					pw.print(" "+reactant.getSpecies().getName());
//				}
//			}
//			pw.print(" -> ");
//			count = 0;
//			for (int j = 0; j < reaction.getProducts().size(); j++) {
//				org.vcell.smoldyn.vcellstuff.ReactionParticipant product = reaction.getProducts().get(j);
//				for (int k=0;k<product.getStochiometry();k++){
//					if (count++ > 0){
//						pw.print(" +");
//					}
//					pw.print(" "+product.getSpecies().getName());
//				}
//			}
//			pw.println(" "+reaction.getRate());
//		}
//
//		pw.println();
//		// time
//		pw.println();
//		pw.println("time_start 0");
//		pw.println("time_stop 200");
//		pw.println("time_step 0.01");
//
//		pw.println("end_file");
//
//	}
//	
//	private Model getModel(SimulationContext simContext) throws ExpressionException {
//		Model model = new Model();
//		Geometry geometry = simContext.getGeometry();
//		model.geometry.setDim(geometry.getDimension());
//		
//		GeometrySurfaceDescription geoSurfaceDescription = geometry.getGeometrySurfaceDescription();
//		Origin origin = geometry.getOrigin();
//		Extent extent = geometry.getExtent();
//		model.geometry.setMinX(origin.getX());
//		model.geometry.setMaxX(origin.getX()+extent.getX());
//		model.geometry.setMinY(origin.getY());
//		model.geometry.setMaxY(origin.getY()+extent.getY());
//		model.geometry.setMinZ(origin.getZ());
//		model.geometry.setMaxZ(origin.getZ()+extent.getZ());
//		
//		GeometricRegion[] geometricRegions = geometry.getGeometrySurfaceDescription().getGeometricRegions();
//		ArrayList<SurfaceGeometricRegion> surfaceRegions = new ArrayList<SurfaceGeometricRegion>();
//		ArrayList<VolumeGeometricRegion> volumeRegions = new ArrayList<VolumeGeometricRegion>();
//		for (int i = 0; i < geometricRegions.length; i++) {
//			if (geometricRegions[i] instanceof SurfaceGeometricRegion){
//				surfaceRegions.add((SurfaceGeometricRegion)geometricRegions[i]);
//			}else if (geometricRegions[i] instanceof VolumeGeometricRegion){
//				volumeRegions.add((VolumeGeometricRegion)geometricRegions[i]);
//			}
//		}
//		SpeciesContextSpec[] speciesContextSpecs = simContext.getReactionContext().getSpeciesContextSpecs();
//		for (int i = 0; i < surfaceRegions.size(); i++) {
//			org.vcell.smoldyn.vcellstuff.Surface surface = getSurface(surfaceRegions.get(i),geoSurfaceDescription);
//			SurfaceClass surfaceClass = (SurfaceClass)geoSurfaceDescription.getGeometryClass(surfaceRegions.get(i));
//			DomainType surfaceDomainType = model.geometry.getDomainType(surfaceClass.getName());
//			if (surfaceDomainType==null){
//				surfaceDomainType = new DomainType();
//				surfaceDomainType.setName(surfaceClass.getName());
//				surfaceDomainType.setDimension(2);
//				model.geometry.domainTypeList.add(surfaceDomainType);
//			}
//			surface.setDomainType(surfaceDomainType);
//			surface.setName(surfaceRegions.get(i).getName());
//			model.geometry.surfaceList.add(surface);
//		}		
//		for (int i = 0; i < volumeRegions.size(); i++) {
//			Compartment compartment = new Compartment();
//			SubVolume subVolume = (SubVolume)geoSurfaceDescription.getGeometryClass(volumeRegions.get(i));
//			DomainType volumeDomainType = model.geometry.getDomainType(subVolume.getName());
//			if (volumeDomainType==null){
//				volumeDomainType = new DomainType();
//				volumeDomainType.setName(subVolume.getName());
//				volumeDomainType.setDimension(3);
//				model.geometry.domainTypeList.add(volumeDomainType);
//			}
//			compartment.setDomainType(volumeDomainType);
//			compartment.setName(volumeRegions.get(i).getName());
//			GeometricRegion[] adjacentRegions = volumeRegions.get(i).getAdjacentGeometricRegions();
//			for (int j = 0; j < adjacentRegions.length; j++) {
//				if (adjacentRegions[j] instanceof SurfaceGeometricRegion){
//					SurfaceGeometricRegion surfaceRegion = (SurfaceGeometricRegion)adjacentRegions[j];
//					//SurfaceClass surfaceClass = (SurfaceClass)geoSurfaceDescription.getGeometryClass(adjacentRegions[j]);
//					org.vcell.smoldyn.vcellstuff.Surface surface = model.geometry.getSurface(surfaceRegion.getName());
//					compartment.getSurfaces().add(surface);
//				}
//			}
//			Coordinate coord = getAnyCoordinate(geometry, volumeRegions.get(i));
//			compartment.getPoints().add(coord);
//			model.geometry.compartmentList.add(compartment);
//		}		
//		
//		int colorSeed = 0;
//		Color backgroundColor = new Color(0.0f,0.0f,0.0f);
//		Color[] autoContrastColors = Plot2DPanel.generateAutoColor(speciesContextSpecs.length,backgroundColor,colorSeed);
//			
//		for (int i = 0; i < speciesContextSpecs.length; i++) {
//			Species species = new Species();
//			SpeciesContextSpec scs = speciesContextSpecs[i];
//			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(scs.getSpeciesContext().getStructure());
//			String name = scs.getSpeciesContext().getName();
//			species.setName(name);
//			if (scs.getDiffusionParameter()!=null && scs.getDiffusionParameter().getExpression()!=null){
//				species.setDifc(scs.getDiffusionParameter().getConstantValue());
//			}
//			if (scs.getVelocityXParameter()!=null && scs.getVelocityXParameter().getExpression()!=null){
//				species.setDriftX(scs.getVelocityXParameter().getConstantValue());
//			}
//			if (scs.getVelocityYParameter()!=null && scs.getVelocityYParameter().getExpression()!=null){
//				species.setDriftY(scs.getVelocityYParameter().getConstantValue());
//			}
//			if (scs.getVelocityZParameter()!=null && scs.getVelocityZParameter().getExpression()!=null){
//				species.setDriftZ(scs.getVelocityZParameter().getConstantValue());
//			}
//			species.setColor(autoContrastColors[i]);
//			species.setDisplaySize(5);
//			species.setDomains(model.geometry.getDomains(model.geometry.getDomainType(sm.getGeometryClass().getName())));
//			
//			double concentration = scs.getInitialConcentrationParameter().getExpression().evaluateConstant();
//			VCUnitDefinition concUnits = scs.getInitialConcentrationParameter().getUnitDefinition();
//			GeometricRegion[] regions = geoSurfaceDescription.getGeometricRegions(sm.getGeometryClass());
////			if (regions.length!=1){
////				throw new RuntimeException("expected exactly one region for geometryClass "+sm.getGeometryClass().toString());
////			}
//			double size = 0.0;
//			for (int j = 0; j < regions.length; j++) {
//				size += regions[j].getSize();
//			}
//			if (concUnits.equals(VCUnitDefinition.UNIT_uM)){
//				species.setNumMolecules((int)Math.floor(concentration*602*size));
//			}else if (concUnits.equals(VCUnitDefinition.UNIT_molecules_per_um2)){
//				species.setNumMolecules((int)Math.floor(concentration*size));
//			}else{
//				throw new RuntimeException("unexpected units for initial concentration '"+concUnits.getSymbol());
//			}
//			model.speciesList.add(species);
//		}
//		
//		ReactionSpec[] reactionSpecs = simContext.getReactionContext().getReactionSpecs();
//		for (int i = 0; i < reactionSpecs.length; i++) {
//			ReactionSpec reactionSpec = reactionSpecs[i];
//			if (reactionSpec.isExcluded()){
//				continue;
//			}
//			if (reactionSpec.isFast()){
//				throw new RuntimeException("fast spatial stochastic reactions not yet implemented");
//			}
//			
//			ReactionStep reactionStep = reactionSpec.getReactionStep();
//			MassActionKinetics maKinetics = (MassActionKinetics)reactionStep.getKinetics();
//			ReactionParticipant[] rps = reactionStep.getReactionParticipants();
//
//			
//			Double forwardRate = maKinetics.getForwardRateParameter().getConstantValue();
//			if (forwardRate!=null && forwardRate!=0.0){
//				Reaction reaction = new Reaction();
//				reaction.setName(reactionStep.getName()+"_forward");
//				reaction.setRate(forwardRate);
//				
//				for (int j = 0; j < rps.length; j++) {
//					org.vcell.smoldyn.vcellstuff.ReactionParticipant rp = new org.vcell.smoldyn.vcellstuff.ReactionParticipant();
//					rp.setSpecies(model.getSpecies(rps[j].getSpeciesContext().getName()));
//					rp.setStochiometry(rps[j].getStoichiometry());
//					if (rps[j] instanceof Reactant){
//						reaction.getReactants().add(rp);
//					}else if (rps[j] instanceof Product){
//						reaction.getProducts().add(rp);
//					}
//				}
//				model.reactionList.add(reaction);
//			}
//			
//			Double reverseRate = maKinetics.getReverseRateParameter().getConstantValue();
//			if (reverseRate!=null && reverseRate!=0.0){
//				Reaction reaction = new Reaction();
//				reaction.setName(reactionStep.getName()+"_reverse");
//				reaction.setRate(reverseRate);
//				
//				for (int j = 0; j < rps.length; j++) {
//					org.vcell.smoldyn.vcellstuff.ReactionParticipant rp = new org.vcell.smoldyn.vcellstuff.ReactionParticipant();
//					rp.setSpecies(model.getSpecies(rps[j].getSpeciesContext().getName()));
//					rp.setStochiometry(rps[j].getStoichiometry());
//					if (rps[j] instanceof Reactant){
//						reaction.getProducts().add(rp);
//					}else if (rps[j] instanceof Product){
//						reaction.getReactants().add(rp);
//					}
//				}
//				model.reactionList.add(reaction);
//			}
//		}
//		return model;
//	}
//	
//	public org.vcell.smoldyn.vcellstuff.Surface getSurface(SurfaceGeometricRegion surfaceRegion, GeometrySurfaceDescription geoSurfaceDescription){
//		org.vcell.smoldyn.vcellstuff.Surface smoldynSurface = new org.vcell.smoldyn.vcellstuff.Surface();
//		SurfaceCollection surfaceCollection = geoSurfaceDescription.getSurfaceCollection();
//		VolumeGeometricRegion interiorRegion = (VolumeGeometricRegion)surfaceRegion.getAdjacentGeometricRegions()[0];
//		VolumeGeometricRegion exteriorRegion = (VolumeGeometricRegion)surfaceRegion.getAdjacentGeometricRegions()[1];
//		
//		//
//		// find surfaces that border this region (and invert normals if necessary)
//		//
//		for (int j = 0; j < surfaceCollection.getSurfaceCount(); j++){
//			Surface surface = surfaceCollection.getSurfaces(j);
//			if ((surface.getInteriorRegionIndex() == interiorRegion.getRegionID()) && 
//				(surface.getExteriorRegionIndex() == exteriorRegion.getRegionID())){
//				//
//				// need Counter Clockwise for "out" with respect to this volume region, (Already OK)
//				//
//				for (int k = 0; k < surface.getPolygonCount(); k++){
//					Polygon polygon = surface.getPolygons(k);
//					Node[] nodes = polygon.getNodes();
//					if (polygon.getNodeCount()==3){
//						Triangle triangle = new Triangle();
//						triangle.setPoint1(new Coordinate(nodes[0].getX(),nodes[0].getY(),nodes[0].getZ()));
//						triangle.setPoint2(new Coordinate(nodes[1].getX(),nodes[1].getY(),nodes[1].getZ()));
//						triangle.setPoint3(new Coordinate(nodes[2].getX(),nodes[2].getY(),nodes[2].getZ()));
//						smoldynSurface.getPanels().add(triangle);
//					}else if (polygon.getNodeCount()==4){
//						Triangle triangle = new Triangle();
//						triangle.setPoint1(new Coordinate(nodes[0].getX(),nodes[0].getY(),nodes[0].getZ()));
//						triangle.setPoint2(new Coordinate(nodes[1].getX(),nodes[1].getY(),nodes[1].getZ()));
//						triangle.setPoint3(new Coordinate(nodes[2].getX(),nodes[2].getY(),nodes[2].getZ()));
//						smoldynSurface.getPanels().add(triangle);
//						triangle = new Triangle();
//						triangle.setPoint1(new Coordinate(nodes[0].getX(),nodes[0].getY(),nodes[0].getZ()));
//						triangle.setPoint2(new Coordinate(nodes[2].getX(),nodes[2].getY(),nodes[2].getZ()));
//						triangle.setPoint3(new Coordinate(nodes[3].getX(),nodes[3].getY(),nodes[3].getZ()));
//						smoldynSurface.getPanels().add(triangle);
//					}
//				}
//			} else if ((surface.getInteriorRegionIndex() == exteriorRegion.getRegionID()) &&
//					   (surface.getExteriorRegionIndex() == interiorRegion.getRegionID())){
//				//
//				// need Counter Clockwise for "out" with respect to this volume region, (MUST FLIP NORMAL AND RE-ORDER VERTICES)
//				//
//				for (int k = 0; k < surface.getPolygonCount(); k++){
//					Polygon polygon = surface.getPolygons(k);
//					Node[] nodes = polygon.getNodes();
//					if (polygon.getNodeCount()==3){
//						Triangle triangle = new Triangle();
//						triangle.setPoint1(new Coordinate(nodes[2].getX(),nodes[2].getY(),nodes[2].getZ()));
//						triangle.setPoint2(new Coordinate(nodes[1].getX(),nodes[1].getY(),nodes[1].getZ()));
//						triangle.setPoint3(new Coordinate(nodes[0].getX(),nodes[0].getY(),nodes[0].getZ()));
//						smoldynSurface.getPanels().add(triangle);
//					}else if (polygon.getNodeCount()==4){
//						Triangle triangle = new Triangle();
//						triangle.setPoint1(new Coordinate(nodes[2].getX(),nodes[2].getY(),nodes[2].getZ()));
//						triangle.setPoint2(new Coordinate(nodes[1].getX(),nodes[1].getY(),nodes[1].getZ()));
//						triangle.setPoint3(new Coordinate(nodes[0].getX(),nodes[0].getY(),nodes[0].getZ()));
//						smoldynSurface.getPanels().add(triangle);
//						triangle = new Triangle();
//						triangle.setPoint1(new Coordinate(nodes[3].getX(),nodes[3].getY(),nodes[3].getZ()));
//						triangle.setPoint2(new Coordinate(nodes[2].getX(),nodes[2].getY(),nodes[2].getZ()));
//						triangle.setPoint3(new Coordinate(nodes[0].getX(),nodes[0].getY(),nodes[0].getZ()));
//						smoldynSurface.getPanels().add(triangle);
//					}
//				}
//			}
//		}
//		return smoldynSurface;
//	}
//
//	public static Coordinate getAnyCoordinate(Geometry geometry, GeometricRegion region){
//		RegionImage regionImage = geometry.getGeometrySurfaceDescription().getRegionImage();
//		int numX = regionImage.getNumX();
//		int numY = regionImage.getNumY();
//		int numZ = regionImage.getNumZ();
//		double totalX = 0;
//		double totalY = 0;
//		double totalZ = 0;
//		Origin origin = geometry.getOrigin();
//		Extent extent = geometry.getExtent();
//		int count = 0;
//		GeometricRegion[] allRegions = geometry.getGeometrySurfaceDescription().getGeometricRegions();
//		for (int indexX = 0; indexX < numX; indexX++){
//			for (int indexY = 0; indexY < numY; indexY++){
//				for (int indexZ = 0; indexZ < numZ; indexZ++){
//					int offset = indexX + (numX)*indexY + numX*numY*indexZ;
//					RegionInfo regionInfo = regionImage.getRegionInfoFromOffset(offset);
//					if (allRegions[regionInfo.getRegionIndex()].equals(region)){
//						switch (geometry.getDimension()){
//						case 2: {
//							totalX += origin.getX()+(indexX*extent.getX()/(numX-1));
//							totalY += origin.getY()+(indexY*extent.getY()/(numY-1));
//							totalZ += origin.getZ()+extent.getZ()/2;
//							break;
//						}
//						case 3: {
//							totalX += origin.getX()+(indexX*extent.getX()/(numX-1));
//							totalY += origin.getY()+(indexY*extent.getY()/(numY-1));
//							totalZ += origin.getZ()+(indexZ*extent.getZ()/(numZ-1));
//							break;
//						}
//						default:
//							throw new RuntimeException("unsupported geometry dimension");
//						}
//						count++;
//					}
//				}
//			}
//		}
//		if (count==0){
//			throw new RuntimeException("cant find region "+region.getName()+" in geometry");
//		}
//		return new Coordinate(totalX/count, totalY/count, totalZ/count);
//	}
//	
//	public final static void main(String args[]){
//		try {
//			if (args.length!=1){
//				System.out.println("usage: SmoldynUtils vcmlFilename");
//				throw new RuntimeException("improper commandline arguments");
//			}
//			BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(new File(args[0])));
//			SimulationContext simContext = bioModel.getSimulationContexts()[0];
//			System.out.println(simContext.getGeometry().getGeometrySurfaceDescription().getVolumeSampleSize());
//			simContext.getGeometry().getGeometrySurfaceDescription().setFilterCutoffFrequency(0.55); // default is 0.3 (less is more)
//			simContext.getGeometry().getGeometrySurfaceDescription().setVolumeSampleSize(new ISize(50,50,30));
//			simContext.getGeometry().getGeometrySurfaceDescription().updateAll();
//
//			SmoldynUtils2 utils = new SmoldynUtils2();
//			utils.test(simContext);
//		} catch (Exception e) {
//			e.printStackTrace(System.out);
//		} finally {
//			System.exit(0);
//		}
//	}
//
//}
