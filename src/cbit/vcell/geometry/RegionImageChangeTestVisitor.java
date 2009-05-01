package cbit.vcell.geometry;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import org.vcell.util.Coordinate;
import org.vcell.util.Matchable;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;

import com.sun.java_cup.internal.lexer;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.GeometrySurfaceUtils;
import cbit.vcell.geometry.surface.Node;
import cbit.vcell.geometry.surface.OrigSurface;
import cbit.vcell.geometry.surface.Polygon;
import cbit.vcell.geometry.surface.Quadrilateral;
import cbit.vcell.geometry.surface.Surface;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.TaubinSmoothing;
import cbit.vcell.geometry.surface.TaubinSmoothingSpecification;
import cbit.vcell.geometry.surface.TaubinSmoothingWrong;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;
import cbit.vcell.parser.ExpressionException;

public class RegionImageChangeTestVisitor implements VCDatabaseVisitor {

	public boolean filterGeometry(GeometryInfo geometryInfo) {
		return (geometryInfo.getDimension()>0);
	}
private void test2(Geometry geometry, PrintStream logFilePrintStream){
	try{
		java.io.File meshMetricsFile0 = new java.io.File("\\\\cfs01\\RAID\\vcell\\users\\frm\\SimID_23521631_0_.meshmetrics");
		java.io.File meshFile0 = new java.io.File("\\\\cfs01\\RAID\\vcell\\users\\frm\\SimID_23521631_0_.mesh");
		java.io.File meshMetricsFile1 = new java.io.File("\\\\cfs01\\RAID\\vcell\\users\\frm\\SimID_23527605_0_.meshmetrics");
		java.io.File meshFile1 = new java.io.File("\\\\cfs01\\RAID\\vcell\\users\\frm\\SimID_23527605_0_.mesh");
		cbit.vcell.solvers.CartesianMesh mesh0 = cbit.vcell.geometry.surface.GeometryRegressionTest.loadMesh(meshFile0,meshMetricsFile0);
		cbit.vcell.solvers.CartesianMesh mesh1 = cbit.vcell.geometry.surface.GeometryRegressionTest.loadMesh(meshFile1,meshMetricsFile1);
		System.out.println("load success. x="+mesh0.getSizeX()+" y="+mesh0.getSizeY()+ "z="+mesh0.getSizeZ());
		cbit.vcell.solvers.MembraneElement[] membArr0 = mesh0.getMembraneElements();
		cbit.vcell.solvers.MembraneElement[] membArr1 = mesh1.getMembraneElements();
		double[] area0 = new double[mesh0.getNumMembraneRegions()];
		double[] area1 = new double[mesh1.getNumMembraneRegions()];
		System.out.println("area0 len="+area0.length+" area1 len="+area1.length);
		for(int i=0;i<membArr0.length;i+= 1){
			area0[mesh0.getMembraneRegionIndex(membArr0[i].getMembraneIndex())]+= membArr0[i].getArea();
			area1[mesh1.getMembraneRegionIndex(membArr1[i].getMembraneIndex())]+= membArr1[i].getArea();
		}
		System.out.println("Sum completed");
		for(int i=0;i<area0.length;i+= 1){
			System.out.println("area0="+area0[i]+" area1="+area1[i]);
		}
		java.util.Arrays.sort(membArr0, new java.util.Comparator<cbit.vcell.solvers.MembraneElement> (){
			public int compare(cbit.vcell.solvers.MembraneElement p1, cbit.vcell.solvers.MembraneElement p2) {
				org.vcell.util.Coordinate o1 = p1.getCentroid();
				org.vcell.util.Coordinate o2 = p2.getCentroid();
				double xdiff = o1.getX()-o2.getX();
				double xmin = Math.min(Math.abs(o1.getX()),Math.abs(o2.getX()));
				double xlimit = (1e-12*(xmin>= 1.0?(Math.pow(10,(int)Math.log10(xmin)+1)):1));
				double ydiff = o1.getY()-o2.getY();
				double ymin = Math.min(Math.abs(o1.getY()),Math.abs(o2.getY()));
				double ylimit = (1e-12*(ymin>= 1.0?(Math.pow(10,(int)Math.log10(ymin)+1)):1));
				double zdiff = o1.getZ()-o2.getZ();
				double zmin = Math.min(Math.abs(o1.getZ()),Math.abs(o2.getZ()));
				double zlimit = (1e-12*(zmin>= 1.0?(Math.pow(10,(int)Math.log10(zmin)+1)):1));
				if(Math.abs(zdiff) < zlimit){
					if(Math.abs(ydiff) < ylimit){
						return (int)Math.signum((Math.abs(xdiff)<xlimit?0:xdiff));
					}
					return (int)Math.signum((Math.abs(ydiff)<ylimit?0:ydiff));
				}
				return (int)Math.signum((Math.abs(zdiff)<zlimit?0:zdiff));
			}});
		java.util.Arrays.sort(membArr1, new java.util.Comparator<cbit.vcell.solvers.MembraneElement> (){
			public int compare(cbit.vcell.solvers.MembraneElement p1, cbit.vcell.solvers.MembraneElement p2) {
				org.vcell.util.Coordinate o1 = p1.getCentroid();
				org.vcell.util.Coordinate o2 = p2.getCentroid();
				double xdiff = o1.getX()-o2.getX();
				double xmin = Math.min(Math.abs(o1.getX()),Math.abs(o2.getX()));
				double xlimit = (1e-12*(xmin>= 1.0?(Math.pow(10,(int)Math.log10(xmin)+1)):1));
				double ydiff = o1.getY()-o2.getY();
				double ymin = Math.min(Math.abs(o1.getY()),Math.abs(o2.getY()));
				double ylimit = (1e-12*(ymin>= 1.0?(Math.pow(10,(int)Math.log10(ymin)+1)):1));
				double zdiff = o1.getZ()-o2.getZ();
				double zmin = Math.min(Math.abs(o1.getZ()),Math.abs(o2.getZ()));
				double zlimit = (1e-12*(zmin>= 1.0?(Math.pow(10,(int)Math.log10(zmin)+1)):1));
				if(Math.abs(zdiff) < zlimit){
					if(Math.abs(ydiff) < ylimit){
						return (int)Math.signum((Math.abs(xdiff)<xlimit?0:xdiff));
					}
					return (int)Math.signum((Math.abs(ydiff)<ylimit?0:ydiff));
				}
				return (int)Math.signum((Math.abs(zdiff)<zlimit?0:zdiff));
			}});
		double SMALL_DIST = 1e-9;
		for (int i = 0; i < membArr0.length; i+= 1) {
			org.vcell.util.Coordinate centroid0 = membArr0[i].getCentroid();
			org.vcell.util.Coordinate centroid1 = membArr1[i].getCentroid();
			double dist = Math.sqrt(Math.pow(centroid0.getX()-centroid1.getX(), 2)+
							Math.pow(centroid0.getY()-centroid1.getY(), 2)+
					Math.pow(centroid0.getZ()-centroid1.getZ(), 2));
				
			//System.out.println(centroid0.getX());
			if(dist>SMALL_DIST){
				System.out.println("  ** SURF POLYGON CENTROID VAL DIFF dist="+dist);
				return;
			}
			cbit.vcell.render.Vect3d n0 = membArr0[i].getNormal();
			cbit.vcell.render.Vect3d n1 = membArr1[i].getNormal();
			//System.out.println(n0);
			dist = Math.sqrt(Math.pow(n0.getX()-n1.getX(), 2)+
					Math.pow(n0.getY()-n1.getY(), 2)+
				Math.pow(n0.getZ()-n1.getZ(), 2));
			if(dist>SMALL_DIST){
				System.out.println("  ** SURF POLYGON NORMAL VAL DIFF dist="+dist);
				return;
			}
		}
		for (int i = 0; i < membArr0.length; i+= 1) {
			double polyArea0 = membArr0[i].getArea();
			double polyArea1 = membArr1[i].getArea();
			double dist = Math.abs(polyArea0-polyArea1);
				
			//System.out.println(centroid0.getX());
			if(dist>SMALL_DIST){
				System.out.println("  ** SURF POLYGON AREA VAL DIFF dist="+dist);
				System.out.println("memb0 indx="+membArr0[i].getMembraneIndex()+" memb1 indx="+membArr1[i].getMembraneIndex());
				System.out.println("memb0 VI="+membArr0[i].getInsideVolumeIndex()+"memb0 VO="+membArr0[i].getOutsideVolumeIndex());
				System.out.println("memb1 VI="+membArr1[i].getInsideVolumeIndex()+"memb1 VO="+membArr1[i].getOutsideVolumeIndex());
				System.out.println("centroid0="+membArr0[i].getCentroid()+"    centroid1="+membArr1[i].getCentroid());
				System.out.println("area0="+polyArea0+"   area1="+polyArea1);
				int k=membArr0[i].getInsideVolumeIndex();
				int z = k/(mesh0.getSizeX()*mesh0.getSizeY());
				int y = (k-z*(mesh0.getSizeX()*mesh0.getSizeY()))/mesh0.getSizeX();
				int x = (k-z*(mesh0.getSizeX()*mesh0.getSizeY())) - (y*mesh0.getSizeX());
				System.out.println("VInsize="+k+" z="+z+" y="+y+" x="+x);
				for (int j = 0; j < membArr0[i].getMembraneNeighborIndexes().length; j++) {
					System.out.println("M="+membArr0[i].getMembraneIndex()+
							" n="+j+
							" vi="+membArr0[membArr0[i].getMembraneNeighborIndexes()[j]].getInsideVolumeIndex()+
							" vo="+membArr0[membArr0[i].getMembraneNeighborIndexes()[j]].getOutsideVolumeIndex());
				}
				for (int j = 0; j < membArr1[i].getMembraneNeighborIndexes().length; j++) {
					System.out.println("M="+membArr1[i].getMembraneIndex()+
							" n="+j+
							" vi="+membArr1[membArr0[i].getMembraneNeighborIndexes()[j]].getInsideVolumeIndex()+
							" vo="+membArr1[membArr0[i].getMembraneNeighborIndexes()[j]].getOutsideVolumeIndex());
				}
				System.out.println();
//				return;
			}
		}

		System.out.println("compare success.");
		}catch(Exception e){
			System.out.println("failed.  Error="+e.getMessage());
		}

}
	public void visitGeometry(Geometry geometry, PrintStream logFilePrintStream){
		
		test2(geometry,logFilePrintStream);
		if(true){
			System.exit(0);
		}
		try{
			if(!geometry.getName().equals("AliciaSpacial") || !geometry.getVersion().getOwner().getName().equals("les")){
//			if(geometry.getDimension() == 0){
				return;
			}
			
			logFilePrintStream.println(geometry.getName()+" dim="+geometry.getDimension());
			logFilePrintStream.println("     "+geometry.getVersion().getDate());
			logFilePrintStream.println("     "+geometry.getVersion().getOwner().getName());
			long startTime = System.currentTimeMillis();
			//Original
			GeometrySurfaceDescription geoSurfaceDescription = new GeometrySurfaceDescription(geometry);
//			geoSurfaceDescription.setFilterCutoffFrequency(1.0);
			geoSurfaceDescription.updateAll();
			logFilePrintStream.println("     origTime="+((System.currentTimeMillis()-startTime)/1000.0));
			startTime = System.currentTimeMillis();
			
			//New
			long localStartTime = System.currentTimeMillis();
			GeometrySurfaceDescription newGeoSurfaceDescription = new GeometrySurfaceDescription(geometry);
			cbit.vcell.geometry.GeometrySpec geometrySpec = geometry.getGeometrySpec();
			logFilePrintStream.println("  new GeoSurfaceDescription time="+((System.currentTimeMillis()-localStartTime)/1000.0));
			localStartTime = System.currentTimeMillis();
			cbit.image.VCImage newImage = geometrySpec.createSampledImage(newGeoSurfaceDescription.getVolumeSampleSize());
			logFilePrintStream.println("  new sample time="+((System.currentTimeMillis()-localStartTime)/1000.0));
			localStartTime = System.currentTimeMillis();
			cbit.vcell.geometry.RegionImage newRegionImage =
				new cbit.vcell.geometry.RegionImage(
						newImage,geometry.getDimension(),
						geometry.getExtent(),geometry.getOrigin(),
						newGeoSurfaceDescription.getFilterCutoffFrequency().doubleValue());
			
			logFilePrintStream.println("  new region image time="+((System.currentTimeMillis()-localStartTime)/1000.0));
			localStartTime = System.currentTimeMillis();
//			if (newGeoSurfaceDescription.getFilterCutoffFrequency().doubleValue()<0.6){
//				TaubinSmoothing taubinSmoothing = new TaubinSmoothingWrong();
//				TaubinSmoothingSpecification taubinSpec = TaubinSmoothingSpecification.getInstance(newGeoSurfaceDescription.getFilterCutoffFrequency().doubleValue());
//				taubinSmoothing.smooth(newRegionImage.getSurfacecollection(),taubinSpec);
//			}			
//			logFilePrintStream.println("  new taubin time="+((System.currentTimeMillis()-localStartTime)/1000.0));
//			localStartTime = System.currentTimeMillis();
			GeometricRegion[] newGeometricRegionArr =
				GeometrySurfaceUtils.getUpdatedGeometricRegions(
					newGeoSurfaceDescription,newRegionImage,newRegionImage.getSurfacecollection());

			logFilePrintStream.println("  new geom region time="+((System.currentTimeMillis()-localStartTime)/1000.0));
			localStartTime = System.currentTimeMillis();
			logFilePrintStream.println("     newTime="+((System.currentTimeMillis()-startTime)/1000.0));
			startTime = System.currentTimeMillis();

			//Compare Data Structures
			RegionImage origRegionImage = geoSurfaceDescription.getRegionImage();
			RegionInfo[] origRegionInfoArr = origRegionImage.getRegionInfos();
			RegionInfo[] newRegionInfoArr = newRegionImage.getRegionInfos();
			if(origRegionImage.getNumX() != newRegionImage.getNumX() ||
					origRegionImage.getNumY() != newRegionImage.getNumY() ||
					origRegionImage.getNumZ() != newRegionImage.getNumZ()){
				logFilePrintStream.println("  ** REG IMAGE XYZ SIZE DIFF");
				return;
			}
			//Same RegionInfo count
			if(origRegionInfoArr.length != newRegionInfoArr.length){
//				//Special
//				int imageSize = newRegionImage.getNumX()*newRegionImage.getNumY()*newRegionImage.getNumZ();
//				for (int k = 0; k < imageSize; k++) {
//					if(origRegionInfoArr[0].isIndexInRegion(k) != newRegionInfoArr[0].isIndexInRegion(k)){
//						int z = k/(newRegionImage.getNumX()*newRegionImage.getNumY());
//						int y = (k-z*(newRegionImage.getNumX()*newRegionImage.getNumY()))/newRegionImage.getNumX();
//						int x = (k-z*(newRegionImage.getNumX()*newRegionImage.getNumY())) - (y*newRegionImage.getNumX());
//						System.out.println(
//							"img_index="+k+" z="+z+" y="+y+" x="+x+
//							" orig="+origRegionInfoArr[0].isIndexInRegion(k)+
//							" orig_pix="+origRegionInfoArr[0].getPixelValue()+
//							" orig_reg="+origRegionInfoArr[0].getRegionIndex()+
//							" new="+newRegionInfoArr[0].isIndexInRegion(k)+
//							" new_pix="+newRegionInfoArr[0].getPixelValue()+
//							" new_reg="+newRegionInfoArr[0].getRegionIndex()
//						);
//						logFilePrintStream.println("  ** REGINFO MASK DIFF");
//						return;								
//					}
//				}
//				//Special End
				logFilePrintStream.println("  ** REGINFO NUM REGIONS DIFF");
				return;
			}
			//Same RegionInfo values

			for (int i = 0; i < origRegionInfoArr.length; i++) {
				boolean bFound = false;
				for (int j = 0; j < newRegionInfoArr.length; j++) {
					if(newRegionInfoArr[j].getRegionIndex() == origRegionInfoArr[i].getRegionIndex()){
						if(newRegionInfoArr[j].getNumPixels() != origRegionInfoArr[i].getNumPixels()){
							logFilePrintStream.println("  ** REGINFO NUM PIXELS DIFF");
							return;								
						}
						if(newRegionInfoArr[j].getPixelValue() != origRegionInfoArr[i].getPixelValue()){
							logFilePrintStream.println("  ** REGINFO PIXEL VALS DIFF");
							return;								
						}
						int imageSize = newRegionImage.getNumX()*newRegionImage.getNumY()*newRegionImage.getNumZ();
						for (int k = 0; k < imageSize; k++) {
							if(origRegionInfoArr[i].isIndexInRegion(k) != newRegionInfoArr[j].isIndexInRegion(k)){
								logFilePrintStream.println("  ** REGINFO MASK DIFF");
								return;								
							}
						}
//						if(!newRegionInfoArr[j].mask.equals(origRegionInfoArr[i].mask)){
//							throw new ImageException("Mask not match in regions");
//						}
						bFound = true;
						break;
					}
				}
				if(!bFound){
					logFilePrintStream.println("  ** REGINFO INDEX DIFF");
					return;								
				}
			}

			//Same num surfaces
			SurfaceCollection origSurfColl = geoSurfaceDescription.getSurfaceCollection();
			SurfaceCollection newSurfColl = newRegionImage.getSurfacecollection();
			if(origSurfColl.getSurfaceCount() != newSurfColl.getSurfaceCount()){
				logFilePrintStream.println("  ** REG NUM SURF DIFF");
				return;
			}

			//Same RgionImage Surface global node count
			Node[] origNodeArr = origSurfColl.getNodes().clone();
			Node[] newNodeArr = newSurfColl.getNodes().clone();
			if(origNodeArr.length != newNodeArr.length){
				logFilePrintStream.println("  ** REG SURF NODE COUNT DIFF");
				return;
			}
			//Sort Nodes to allow comparison between orig and new
//			final double SORT_SMALL_DIFF = 1e-12;
			Arrays.sort(origNodeArr, new Comparator<Node> (){
				public int compare(Node o1, Node o2) {
					double xdiff = o1.getX()-o2.getX();
					double xmin = Math.min(Math.abs(o1.getX()),Math.abs(o2.getX()));
					double xlimit = (1e-12*(xmin>= 1.0?(Math.pow(10,(int)Math.log10(xmin)+1)):1));
					double ydiff = o1.getY()-o2.getY();
					double ymin = Math.min(Math.abs(o1.getY()),Math.abs(o2.getY()));
					double ylimit = (1e-12*(ymin>= 1.0?(Math.pow(10,(int)Math.log10(ymin)+1)):1));
					double zdiff = o1.getZ()-o2.getZ();
					double zmin = Math.min(Math.abs(o1.getZ()),Math.abs(o2.getZ()));
					double zlimit = (1e-12*(zmin>= 1.0?(Math.pow(10,(int)Math.log10(zmin)+1)):1));
					if(Math.abs(zdiff) < zlimit){
						if(Math.abs(ydiff) < ylimit){
							return (int)Math.signum((Math.abs(xdiff)<xlimit?0:xdiff));
						}
						return (int)Math.signum((Math.abs(ydiff)<ylimit?0:ydiff));
					}
					return (int)Math.signum((Math.abs(zdiff)<zlimit?0:zdiff));
//					if(o1.getZ() == o2.getZ()){
//						if(o1.getY() == o2.getY()){
//							return (int)Math.signum(o1.getX()-o2.getX());
//						}
//						return (int)Math.signum(o1.getY()-o2.getY());
//					}
//					return (int)Math.signum(o1.getZ()-o2.getZ());
				}});
			Arrays.sort(newNodeArr, new Comparator<Node> (){
				public int compare(Node o1, Node o2) {
					double xdiff = o1.getX()-o2.getX();
					double xmin = Math.min(Math.abs(o1.getX()),Math.abs(o2.getX()));
					double xlimit = (1e-12*(xmin>= 1.0?(Math.pow(10,(int)Math.log10(xmin)+1)):1));
					double ydiff = o1.getY()-o2.getY();
					double ymin = Math.min(Math.abs(o1.getY()),Math.abs(o2.getY()));
					double ylimit = (1e-12*(ymin>= 1.0?(Math.pow(10,(int)Math.log10(ymin)+1)):1));
					double zdiff = o1.getZ()-o2.getZ();
					double zmin = Math.min(Math.abs(o1.getZ()),Math.abs(o2.getZ()));
					double zlimit = (1e-12*(zmin>= 1.0?(Math.pow(10,(int)Math.log10(zmin)+1)):1));
					if(Math.abs(zdiff) < zlimit){
						if(Math.abs(ydiff) < ylimit){
							return (int)Math.signum((Math.abs(xdiff)<xlimit?0:xdiff));
						}
						return (int)Math.signum((Math.abs(ydiff)<ylimit?0:ydiff));
					}
					return (int)Math.signum((Math.abs(zdiff)<zlimit?0:zdiff));
					
//					double xdiff = o1.getX()-o2.getX();
//					double ydiff = o1.getY()-o2.getY();
//					double zdiff = o1.getZ()-o2.getZ();
//					if(Math.abs(zdiff) < SORT_SMALL_DIFF){
//						if(Math.abs(ydiff) < SORT_SMALL_DIFF){
//							return (int)Math.signum((Math.abs(xdiff)<SORT_SMALL_DIFF?0:xdiff));
//						}
//						return (int)Math.signum((Math.abs(ydiff)<SORT_SMALL_DIFF?0:ydiff));
//					}
//					return (int)Math.signum((Math.abs(zdiff)<SORT_SMALL_DIFF?0:zdiff));
					
					
//					if(o1.getZ() == o2.getZ()){
//						if(o1.getY() == o2.getY()){
//							return (int)Math.signum(o1.getX()-o2.getX());
//						}
//						return (int)Math.signum(o1.getY()-o2.getY());
//					}
//					return (int)Math.signum(o1.getZ()-o2.getZ());
				}});
			
//			for (int i = 0; i < newNodeArr.length; i+= 1) {
//				if(newNodeArr[i].getX() > 7.23 && newNodeArr[i].getX() < 7.24){
//					logFilePrintStream.println("SI="+i+" GI="+newNodeArr[i].getGlobalIndex()+" N "+(newNodeArr[i].getX())+" "+(newNodeArr[i].getY())+" "+(newNodeArr[i].getZ()));
//				}
//			}
			
//			//Map Global index to sort position
//			int[] origMapGlobalIndexToSortIndex = new int[origNodeArr.length];
//			int[] newMapGlobalIndexToSortIndex = new int[newNodeArr.length];
//			for (int i = 0; i < newNodeArr.length; i+= 1) {
//				origMapGlobalIndexToSortIndex[origNodeArr[i].getGlobalIndex()] = i;
//				newMapGlobalIndexToSortIndex[newNodeArr[i].getGlobalIndex()] = i;
//			}
//			//Same polygon nodes in same order
//			//[orig/new][surface][polygons][nodesGI]
//			int[][][][] polyNodesGlobalIndex = new int[2][][][];
//			for (int i = 0; i < 2; i++) {
//				int surfCount = (i==0?origSurfColl.getSurfaceCount()):newSurfColl.getSurfaceCount());
//				polyNodesGlobalIndex[i] = new int[surfCount][][];
//				for (int j = 0; j < surfCount; j++) {
//					Surface surface = (i==0?origSurfColl.getSurfaces(j):newSurfColl.getSurfaces(j));
//					polyNodesGlobalIndex[i][j] = new int[surface.getPolygonCount()][4];
//					for (int k = 0; k < surface.getPolygonCount(); k++) {
//						for (int index = 0; index < 4; index++) {
//							polyNodesGlobalIndex[i][j][k][index] = surface.getPolygons(k).getNodes(index).getGlobalIndex();							
//						}
//					}
//				}
//			}
			
			//Same RgionImage Surface global node values (position)
			final double SMALL_DIST = 1e-11;
			for (int i = 0; i < newNodeArr.length; i+= 1) {
				double dist = Math.sqrt(Math.pow(origNodeArr[i].getX()-newNodeArr[i].getX(), 2)+
								Math.pow(origNodeArr[i].getY()-newNodeArr[i].getY(), 2)+
						Math.pow(origNodeArr[i].getZ()-newNodeArr[i].getZ(), 2));
				
//				logFilePrintStream.println(i+" "+(origNodeArr[i].getX()-newNodeArr[i].getX())+" "+
//						(origNodeArr[i].getY()-newNodeArr[i].getY())+" "+
//						(origNodeArr[i].getZ()-newNodeArr[i].getZ())+" -- "+
//						dist);
				
				if(dist>SMALL_DIST){
//					printNode(origSurfColl,newSurfColl,origNodeArr[i],newNodeArr[i],logFilePrintStream);
//					for (int j = -4; j < 5; j++) {
//						logFilePrintStream.println((i+j)+" O "+(origNodeArr[i+j].getX())+" "+(origNodeArr[i+j].getY())+" "+(origNodeArr[i+j].getZ()));						
//						logFilePrintStream.println((i+j)+" N "+(newNodeArr[i+j].getX())+" "+(newNodeArr[i+j].getY())+" "+(newNodeArr[i+j].getZ()));						
//					}
					logFilePrintStream.println("  ** REG SURF NODE VAL DIFF dist="+dist);
					return;
				}
			}
			
			
			
			
			//
			//Sort polygons by centroid and compare equal
			//
			Vector<Polygon> newPolygonV = new Vector<Polygon>();
			Vector<Polygon> origPolygonV = new Vector<Polygon>();
			for (int i = 0; i < newSurfColl.getSurfaceCount(); i++) {
				Surface newSurface = newSurfColl.getSurfaces(i);
				Surface origSurface = origSurfColl.getSurfaces(i);
				for (int j = 0; j < newSurface.getPolygonCount(); j++) {
					newPolygonV.add(newSurface.getPolygons(j));
					origPolygonV.add(origSurface.getPolygons(j));
				}
			}
			Polygon[] newPolygonArr = new Polygon[newPolygonV.size()];
			Polygon[] origPolygonArr = new Polygon[origPolygonV.size()];
			
			newPolygonV.copyInto(newPolygonArr);
			origPolygonV.copyInto(origPolygonArr);
			
			Arrays.sort(newPolygonArr, new Comparator<Polygon> (){
				public int compare(Polygon p1, Polygon p2) {
					Coordinate o1 = ((Quadrilateral)p1).calculateCentroid();
					Coordinate o2 = ((Quadrilateral)p2).calculateCentroid();
					double xdiff = o1.getX()-o2.getX();
					double xmin = Math.min(Math.abs(o1.getX()),Math.abs(o2.getX()));
					double xlimit = (1e-12*(xmin>= 1.0?(Math.pow(10,(int)Math.log10(xmin)+1)):1));
					double ydiff = o1.getY()-o2.getY();
					double ymin = Math.min(Math.abs(o1.getY()),Math.abs(o2.getY()));
					double ylimit = (1e-12*(ymin>= 1.0?(Math.pow(10,(int)Math.log10(ymin)+1)):1));
					double zdiff = o1.getZ()-o2.getZ();
					double zmin = Math.min(Math.abs(o1.getZ()),Math.abs(o2.getZ()));
					double zlimit = (1e-12*(zmin>= 1.0?(Math.pow(10,(int)Math.log10(zmin)+1)):1));
					if(Math.abs(zdiff) < zlimit){
						if(Math.abs(ydiff) < ylimit){
							return (int)Math.signum((Math.abs(xdiff)<xlimit?0:xdiff));
						}
						return (int)Math.signum((Math.abs(ydiff)<ylimit?0:ydiff));
					}
					return (int)Math.signum((Math.abs(zdiff)<zlimit?0:zdiff));
				}});
			Arrays.sort(origPolygonArr, new Comparator<Polygon> (){
				public int compare(Polygon p1, Polygon p2) {
					Coordinate o1 = ((Quadrilateral)p1).calculateCentroid();
					Coordinate o2 = ((Quadrilateral)p2).calculateCentroid();
					double xdiff = o1.getX()-o2.getX();
					double xmin = Math.min(Math.abs(o1.getX()),Math.abs(o2.getX()));
					double xlimit = (1e-12*(xmin>= 1.0?(Math.pow(10,(int)Math.log10(xmin)+1)):1));
					double ydiff = o1.getY()-o2.getY();
					double ymin = Math.min(Math.abs(o1.getY()),Math.abs(o2.getY()));
					double ylimit = (1e-12*(ymin>= 1.0?(Math.pow(10,(int)Math.log10(ymin)+1)):1));
					double zdiff = o1.getZ()-o2.getZ();
					double zmin = Math.min(Math.abs(o1.getZ()),Math.abs(o2.getZ()));
					double zlimit = (1e-12*(zmin>= 1.0?(Math.pow(10,(int)Math.log10(zmin)+1)):1));
					if(Math.abs(zdiff) < zlimit){
						if(Math.abs(ydiff) < ylimit){
							return (int)Math.signum((Math.abs(xdiff)<xlimit?0:xdiff));
						}
						return (int)Math.signum((Math.abs(ydiff)<ylimit?0:ydiff));
					}
					return (int)Math.signum((Math.abs(zdiff)<zlimit?0:zdiff));
				}});
			//Same Surface polygon centroid values (position)
			for (int i = 0; i < newPolygonArr.length; i+= 1) {
				Coordinate newCentroid = ((Quadrilateral)newPolygonArr[i]).calculateCentroid();
				Coordinate origCentroid = ((Quadrilateral)origPolygonArr[i]).calculateCentroid();
				double dist = Math.sqrt(Math.pow(origCentroid.getX()-newCentroid.getX(), 2)+
								Math.pow(origCentroid.getY()-newCentroid.getY(), 2)+
						Math.pow(origCentroid.getZ()-newCentroid.getZ(), 2));
				
//				logFilePrintStream.println(i+" "+(origNodeArr[i].getX()-newNodeArr[i].getX())+" "+
//						(origNodeArr[i].getY()-newNodeArr[i].getY())+" "+
//						(origNodeArr[i].getZ()-newNodeArr[i].getZ())+" -- "+
//						dist);
				
				if(dist>SMALL_DIST){
//					printNode(origSurfColl,newSurfColl,origNodeArr[i],newNodeArr[i],logFilePrintStream);
//					for (int j = -4; j < 5; j++) {
//						logFilePrintStream.println((i+j)+" O "+(origNodeArr[i+j].getX())+" "+(origNodeArr[i+j].getY())+" "+(origNodeArr[i+j].getZ()));						
//						logFilePrintStream.println((i+j)+" N "+(newNodeArr[i+j].getX())+" "+(newNodeArr[i+j].getY())+" "+(newNodeArr[i+j].getZ()));						
//					}
					logFilePrintStream.println("  ** SURF POLYGON VAL DIFF dist="+dist);
					return;
				}
			}
	
			
			
			
			
			
			
			
			
			//Same GeometricsRegion count
			int volGeomRegCount = 0;
			int surfGeomRegCount = 0;
			GeometricRegion[] origGeometricRegionArr = geoSurfaceDescription.getGeometricRegions();
			GeometricRegion[] newGeometricsRegionArr =
				GeometrySurfaceUtils.getUpdatedGeometricRegions(
					newGeoSurfaceDescription, newRegionImage, newRegionImage.getSurfacecollection());
			if(origGeometricRegionArr.length !=  newGeometricsRegionArr.length){
				logFilePrintStream.println("  ** GEOMETRIC REG COUNT DIFF");
				return;				
			}
			boolean[] origBMatchedArr = new boolean[origGeometricRegionArr.length];
			boolean[] newBMatchedArr = new boolean[newGeometricRegionArr.length];
			for (int i = 0; i < origGeometricRegionArr.length; i++) {
				volGeomRegCount+= (origGeometricRegionArr[i] instanceof VolumeGeometricRegion?1:0);
				surfGeomRegCount+= (origGeometricRegionArr[i] instanceof SurfaceGeometricRegion?1:0);
				for (int j = 0; j < newGeometricsRegionArr.length; j++) {
					if(!newBMatchedArr[j] && 
							((Matchable)origGeometricRegionArr[i]).compareEqual(((Matchable)newGeometricsRegionArr[j]))){
						origBMatchedArr[i] = true;
						newBMatchedArr[j] = true;
						break;
					}
				}
				if(!origBMatchedArr[i]){
					logFilePrintStream.println("  ** GEOMETRIC REG NOT MATCH");
					return;									
				}
			}
			logFilePrintStream.println("     size="+newImage.getNumXYZ()+" volGR="+volGeomRegCount+" surfGR="+surfGeomRegCount);
			logFilePrintStream.println("     PASSED TEST");
		}catch(Exception e){
			logFilePrintStream.println("     -----ERROR "+e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private void printNode(SurfaceCollection origSurfColl,SurfaceCollection newSurfcoll,Node origNode,Node newNode,PrintStream logFilePrintStream){
		for (int c = 0; c < 2; c++) {
			SurfaceCollection surfColl = (c==0?origSurfColl:newSurfcoll);
			for (int i = 0; i < surfColl.getSurfaceCount(); i++) {
				Surface surface = surfColl.getSurfaces(i);
				for (int j = 0; j < surface.getPolygonCount(); j++) {
					Polygon poly = surface.getPolygons(j);
					for (int k = 0; k < poly.getNodeCount(); k++) {
						if(poly.getNodes(k) == (c==0?origNode:newNode)){
							logFilePrintStream.println(
								" surf="+(c==0?"orig "+i:"new "+i)+" poly="+j+" nodeGI="+poly.getNodes(k).getGlobalIndex()+
								" extReg="+surface.getExteriorRegionIndex()+
								" intRegion="+surface.getInteriorRegionIndex()+
								" X="+poly.getNodes(k).getX()+
								" Y="+poly.getNodes(k).getY()+
								" Z="+poly.getNodes(k).getZ());
						}
					}
				}
			}
		}
	}
	/**
	 * @param args
	 * for usage, please see command-line argument instructions in VCDatabaseScanner
	 */
	public static void main(String[] args) {
		RegionImageChangeTestVisitor visitor = new RegionImageChangeTestVisitor();
		boolean bAbortOnDataAccessException = false;
		try{
			VCDatabaseScanner.scanGeometries(args, visitor, bAbortOnDataAccessException);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
	}

	//
	// not used for geometries
	//
	public boolean filterBioModel(BioModelInfo bioModelInfo) {
		return false;
	}
	//
	// not used for geometries
	//
	public void visitBioModel(BioModel bioModel, PrintStream logFilePrintStream) {
	}

	//
	// not used for geometries
	//
	public boolean filterMathModel(MathModelInfo mathModelInfo) {
		return false;
	}
	//
	// not used for geometries
	//
	public void visitMathModel(MathModel mathModel, PrintStream logFilePrintStream) {
	}


}
