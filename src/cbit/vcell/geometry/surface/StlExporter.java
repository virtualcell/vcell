/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.surface;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import cbit.vcell.client.task.ExportDocument;
import cbit.vcell.render.Vect3d;

/**
 * Insert the type's description here.
 * Creation date: (7/19/2004 10:52:10 AM)
 * @author: Jim Schaff
 */
public class StlExporter {
	/**
	 * Insert the method's description here.
	 * Creation date: (7/19/2004 10:54:30 AM)
	 * @param geometrySurfaceDescription cbit.vcell.geometry.surface.GeometrySurfaceDescription
	 */
	public static void writeASCIIStl(GeometrySurfaceDescription geometrySurfaceDescription, java.io.Writer writer) throws java.io.IOException {

		GeometricRegion regions[] = geometrySurfaceDescription.getGeometricRegions();
		SurfaceCollection surfaceCollection = geometrySurfaceDescription.getSurfaceCollection();
		//
		// vertices should be all in positive quadrant (no negative coordinates) so have to add offset based on origin
		//
		double ox = geometrySurfaceDescription.getGeometry().getOrigin().getX();
		double oy = geometrySurfaceDescription.getGeometry().getOrigin().getY();
		double oz = geometrySurfaceDescription.getGeometry().getOrigin().getZ();
		
		//
		// for each volume region, collect surfaces and make "solid"
		//
		if (regions==null){
			throw new RuntimeException("Geometric Regions not defined");
		}
		if (surfaceCollection==null){
			throw new RuntimeException("Surfaces not defined");
		}
		writer.write("solid vcellexport\n");
		for (int i = 0; i < regions.length; i++){
			if (regions[i] instanceof VolumeGeometricRegion){
				VolumeGeometricRegion volRegion = (VolumeGeometricRegion)regions[i];
				//
				// find surfaces that border this region (and invert normals if necessary)
				//
				cbit.vcell.render.Vect3d unitNormal = new cbit.vcell.render.Vect3d();
				for (int j = 0; j < surfaceCollection.getSurfaceCount(); j++){
					Surface surface = surfaceCollection.getSurfaces(j);
					if (surface.getInteriorRegionIndex() == volRegion.getRegionID()){
						//
						// need Counter Clockwise for "out" with respect to this volume region, (Already OK)
						//
						for (int k = 0; k < surface.getPolygonCount(); k++){
							Polygon polygon = surface.getPolygons(k);
							if (polygon.getNodeCount()==3){
								polygon.getUnitNormal(unitNormal);
								writer.write("facet normal "+unitNormal.getX()+" "+unitNormal.getY()+" "+unitNormal.getZ()+"\n");
								writer.write("  outer loop\n");
								writer.write("    vertex "+(polygon.getNodes(0).getX()-ox)+" "+(polygon.getNodes(0).getY()-oy)+" "+(polygon.getNodes(0).getZ()-oz)+"\n");
								writer.write("    vertex "+(polygon.getNodes(1).getX()-ox)+" "+(polygon.getNodes(1).getY()-oy)+" "+(polygon.getNodes(1).getZ()-oz)+"\n");
								writer.write("    vertex "+(polygon.getNodes(2).getX()-ox)+" "+(polygon.getNodes(2).getY()-oy)+" "+(polygon.getNodes(2).getZ()-oz)+"\n");
								writer.write("  endloop\n");
								writer.write("endfacet\n");
							}else if (polygon.getNodeCount()==4){
								Polygon triangle = new Triangle(polygon.getNodes(0),polygon.getNodes(1),polygon.getNodes(2));
								triangle.getUnitNormal(unitNormal);
								writer.write("facet normal "+unitNormal.getX()+" "+unitNormal.getY()+" "+unitNormal.getZ()+"\n");
								writer.write("  outer loop\n");
								writer.write("    vertex "+(triangle.getNodes(0).getX()-ox)+" "+(triangle.getNodes(0).getY()-oy)+" "+(triangle.getNodes(0).getZ()-oz)+"\n");
								writer.write("    vertex "+(triangle.getNodes(1).getX()-ox)+" "+(triangle.getNodes(1).getY()-oy)+" "+(triangle.getNodes(1).getZ()-oz)+"\n");
								writer.write("    vertex "+(triangle.getNodes(2).getX()-ox)+" "+(triangle.getNodes(2).getY()-oy)+" "+(triangle.getNodes(2).getZ()-oz)+"\n");
								writer.write("  endloop\n");
								writer.write("endfacet\n");
								triangle = new Triangle(polygon.getNodes(0),polygon.getNodes(2),polygon.getNodes(3));
								triangle.getUnitNormal(unitNormal);
								writer.write("facet normal "+unitNormal.getX()+" "+unitNormal.getY()+" "+unitNormal.getZ()+"\n");
								writer.write("  outer loop\n");
								writer.write("    vertex "+(triangle.getNodes(0).getX()-ox)+" "+(triangle.getNodes(0).getY()-oy)+" "+(triangle.getNodes(0).getZ()-oz)+"\n");
								writer.write("    vertex "+(triangle.getNodes(1).getX()-ox)+" "+(triangle.getNodes(1).getY()-oy)+" "+(triangle.getNodes(1).getZ()-oz)+"\n");
								writer.write("    vertex "+(triangle.getNodes(2).getX()-ox)+" "+(triangle.getNodes(2).getY()-oy)+" "+(triangle.getNodes(2).getZ()-oz)+"\n");
								writer.write("  endloop\n");
								writer.write("endfacet\n");
							}
						}
					} else if (surface.getExteriorRegionIndex() == volRegion.getRegionID()){
						//
						// need Counter Clockwise for "out" with respect to this volume region, (MUST FLIP NORMAL AND RE-ORDER VERTICES)
						//
						for (int k = 0; k < surface.getPolygonCount(); k++){
							Polygon polygon = surface.getPolygons(k);
							if (polygon.getNodeCount()==3){
								polygon.getUnitNormal(unitNormal);
								writer.write("facet normal "+(-unitNormal.getX())+" "+(-unitNormal.getY())+" "+(-unitNormal.getZ())+"\n");
								writer.write("  outer loop\n");
								writer.write("    vertex "+(polygon.getNodes(2).getX()-ox)+" "+(polygon.getNodes(2).getY()-oy)+" "+(polygon.getNodes(2).getZ()-oz)+"\n");
								writer.write("    vertex "+(polygon.getNodes(1).getX()-ox)+" "+(polygon.getNodes(1).getY()-oy)+" "+(polygon.getNodes(1).getZ()-oz)+"\n");
								writer.write("    vertex "+(polygon.getNodes(0).getX()-ox)+" "+(polygon.getNodes(0).getY()-oy)+" "+(polygon.getNodes(0).getZ()-oz)+"\n");
								writer.write("  endloop\n");
								writer.write("endfacet\n");
							}else if (polygon.getNodeCount()==4){
								Polygon triangle = new Triangle(polygon.getNodes(0),polygon.getNodes(1),polygon.getNodes(2));
								triangle.getUnitNormal(unitNormal);
								writer.write("facet normal "+(-unitNormal.getX())+" "+(-unitNormal.getY())+" "+(-unitNormal.getZ())+"\n");
								writer.write("  outer loop\n");
								writer.write("    vertex "+(triangle.getNodes(2).getX()-ox)+" "+(triangle.getNodes(2).getY()-oy)+" "+(triangle.getNodes(2).getZ()-oz)+"\n");
								writer.write("    vertex "+(triangle.getNodes(1).getX()-ox)+" "+(triangle.getNodes(1).getY()-oy)+" "+(triangle.getNodes(1).getZ()-oz)+"\n");
								writer.write("    vertex "+(triangle.getNodes(0).getX()-ox)+" "+(triangle.getNodes(0).getY()-oy)+" "+(triangle.getNodes(0).getZ()-oz)+"\n");
								writer.write("  endloop\n");
								writer.write("endfacet\n");
								triangle = new Triangle(polygon.getNodes(0),polygon.getNodes(2),polygon.getNodes(3));
								triangle.getUnitNormal(unitNormal);
								writer.write("facet normal "+(-unitNormal.getX())+" "+(-unitNormal.getY())+" "+(-unitNormal.getZ())+"\n");
								writer.write("  outer loop\n");
								writer.write("    vertex "+(triangle.getNodes(2).getX()-ox)+" "+(triangle.getNodes(2).getY()-oy)+" "+(triangle.getNodes(2).getZ()-oz)+"\n");
								writer.write("    vertex "+(triangle.getNodes(1).getX()-ox)+" "+(triangle.getNodes(1).getY()-oy)+" "+(triangle.getNodes(1).getZ()-oz)+"\n");
								writer.write("    vertex "+(triangle.getNodes(0).getX()-ox)+" "+(triangle.getNodes(0).getY()-oy)+" "+(triangle.getNodes(0).getZ()-oz)+"\n");
								writer.write("  endloop\n");
								writer.write("endfacet\n");
							}
						}
					}
				}
			}
		}
		writer.write("endsolid vcellexport\n");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (7/19/2004 10:54:30 AM)
	 * @param geometrySurfaceDescription cbit.vcell.geometry.surface.GeometrySurfaceDescription
	 */
	public static void writeBinaryStl(GeometrySurfaceDescription geometrySurfaceDescription,RandomAccessFile raf) throws java.io.IOException {

		GeometricRegion regions[] = geometrySurfaceDescription.getGeometricRegions();
		SurfaceCollection surfaceCollection = geometrySurfaceDescription.getSurfaceCollection();
		//
		// vertices should be all in positive quadrant (no negative coordinates) so have to add offset based on origin
		//
		double ox = geometrySurfaceDescription.getGeometry().getOrigin().getX();
		double oy = geometrySurfaceDescription.getGeometry().getOrigin().getY();
		double oz = geometrySurfaceDescription.getGeometry().getOrigin().getZ();
		
		//
		// for each volume region, collect surfaces and make "solid"
		//
		if (regions==null){
			throw new RuntimeException("Geometric Regions not defined");
		}
		if (surfaceCollection==null){
			throw new RuntimeException("Surfaces not defined");
		}
		int triangleCount = 0;
		for (int i=0;i<surfaceCollection.getSurfaceCount();i++){
			Surface surface = surfaceCollection.getSurfaces(i);
			triangleCount += surface.getPolygonCount()*2;
		}
		int bytesPerTriangle = 4*3 + 4*3+4*3+4*3 + 2;
		int fileLength = 80 + 4 + bytesPerTriangle*triangleCount;

		ByteBuffer buf = raf.getChannel().map(MapMode.READ_WRITE, 0L, fileLength);
		buf.order(ByteOrder.LITTLE_ENDIAN); // stl should be little endian (ref: "http://en.wikipedia.org/wiki/STL_(file_format)" )

		// write 80 byte ASCII header (no text for now).
		buf.put(new byte[80]);
		
		// 
		// write number of triangles (num facets)
		//
		buf.putInt(triangleCount);
		
		Triangle[] triangles = new Triangle[2];
		Vect3d[] unitNormals = new Vect3d[] {  new Vect3d(), new Vect3d()  };
		
		for (int j = 0; j < surfaceCollection.getSurfaceCount(); j++){
			Surface surface = surfaceCollection.getSurfaces(j);
			for (int k = 0; k < surface.getPolygonCount(); k++){
				Polygon polygon = surface.getPolygons(k);
				Vect3d unitNormal = new Vect3d();
				if (polygon.getNodeCount()!=4){
					throw new RuntimeException("expecting quad mesh elements for STL export");
				}
				triangles[0] = new Triangle(polygon.getNodes()[0],polygon.getNodes()[1],polygon.getNodes()[2]);
				triangles[1] = new Triangle(polygon.getNodes()[0],polygon.getNodes()[2],polygon.getNodes()[3]);
				triangles[0].getUnitNormal(unitNormals[0]);
				triangles[1].getUnitNormal(unitNormals[1]);
				
				for (int t = 0; t < 2; t++){
					buf.putFloat((float)unitNormals[t].getX());
					buf.putFloat((float)unitNormals[t].getY());
					buf.putFloat((float)unitNormals[t].getZ());
					
					Node n0 = triangles[t].getNodes()[0];
					buf.putFloat((float)n0.getX());
					buf.putFloat((float)n0.getY());
					buf.putFloat((float)n0.getZ());
					
					Node n1 = triangles[t].getNodes()[1];
					buf.putFloat((float)n1.getX());
					buf.putFloat((float)n1.getY());
					buf.putFloat((float)n1.getZ());
					
					Node n2 = triangles[t].getNodes()[2];
					buf.putFloat((float)n2.getX());
					buf.putFloat((float)n2.getY());
					buf.putFloat((float)n2.getZ());
					
					buf.putShort((short)j); // store the surface index in the "attribute byte count".
				}
			}
		}
	}
	
}
