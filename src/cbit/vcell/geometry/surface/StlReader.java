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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.vcell.util.CommentStringTokenizer;

import cbit.util.xml.XmlUtil;

public class StlReader {
	
	private static class NodeKey {
		public Node node;
		public int hashcode;
		
		public NodeKey(Node node){
			this.node = node;
			this.hashcode = Double.valueOf(node.getX()).hashCode() 
					+ Double.valueOf(node.getY()).hashCode() 
					+ Double.valueOf(node.getZ()).hashCode();
		}
				
		@Override
		public boolean equals(Object obj){
			if (obj instanceof NodeKey){
				NodeKey other = (NodeKey)obj;
				if (node.getX()==other.node.getX() && 
					node.getY()==other.node.getY() && 
					node.getZ()==other.node.getZ()){
					
					return true;
				}
			}
			return false;
		}
		
		@Override
		public int hashCode(){
			return hashcode; 
		}
	}
	
	
	public static SurfaceCollection readStl(File file) throws IOException{
		SurfaceCollection surfaceCollection = new SurfaceCollection();

		// determine if it is ASCII or binary
		FileChannel channel = new RandomAccessFile(file, "r").getChannel();
		ByteBuffer buf = channel.map(MapMode.READ_ONLY, 0L, file.length());
		buf.order(ByteOrder.LITTLE_ENDIAN); // stl should be little endian (ref: "http://en.wikipedia.org/wiki/STL_(file_format)" )
		try {
			ArrayList<Triangle> triangles = new ArrayList<Triangle>();
			ArrayList<Short> attributeByteCounts = new ArrayList<Short>();
			HashSet<Short> uniqueAttributeByteCounts = new HashSet<Short>();
			
			// read header
			byte[] header = new byte[80];
			buf.get(header);
			
			byte ascii_s = 115;
			byte ascii_o = 111;
			byte ascii_l = 108;
			byte ascii_i = 105;
			byte ascii_d = 100;
			
			if (header[0]==ascii_s && 
				header[1]==ascii_o &&
				header[2]==ascii_l &&
				header[3]==ascii_i &&
				header[4]==ascii_d){
				
				channel.close();
				
				return readASCIIStl(file);
			}
			
			// continue to read binary file
			
			HashMap<NodeKey,Node> nodeMap = new HashMap<NodeKey,Node>();
            int numFacets = buf.getInt();

			for (int i=0;i<numFacets;i++){
				float nx = buf.getFloat();
				float ny = buf.getFloat();
				float nz = buf.getFloat();

				float v1x = buf.getFloat();
				float v1y = buf.getFloat();
				float v1z = buf.getFloat();

				float v2x = buf.getFloat();
				float v2y = buf.getFloat();
				float v2z = buf.getFloat();

				float v3x = buf.getFloat();
				float v3y = buf.getFloat();
				float v3z = buf.getFloat();
				
				short attributeByteCount = buf.getShort();
				
				Node n1 = new Node(v1x,v1y,v1z);
				Node n2 = new Node(v2x,v2y,v2z);
				Node n3 = new Node(v3x,v3y,v3z);

				//
				// merge nodes with identical coordinates.
				//
				NodeKey key1 = new NodeKey(n1);
				NodeKey key2 = new NodeKey(n2);
				NodeKey key3 = new NodeKey(n3);
				
				Node node1 = nodeMap.get(key1);
				if (node1==null){
					node1 = n1;
					nodeMap.put(key1, n1);
				}
				
				Node node2 = nodeMap.get(key2);
				if (node2==null){
					node2 = n2;
					nodeMap.put(key2, n2);
				}
				
				Node node3 = nodeMap.get(key3);
				if (node3==null){
					node3 = n3;
					nodeMap.put(key3, n3);
				}
				
				Triangle triangle = new Triangle(node1,node2,node3);
				
				triangles.add(triangle);
				attributeByteCounts.add(attributeByteCount);
				uniqueAttributeByteCounts.add(attributeByteCount);
			}
			//
			// reconcile triangles to surfaces
			//
			// store triangle in surface indexed by "attributeByteCount" (e.g. color)
			//
			// if there are too many unique "colors" ... (>10) then just treat as one surface
			//
			if (uniqueAttributeByteCounts.size()<10){
				HashMap<Short,OrigSurface> surfaceMap = new HashMap<Short, OrigSurface>();
				for (int i=0;i<triangles.size();i++){
					OrigSurface surface = surfaceMap.get(attributeByteCounts.get(i));
					if (surface == null){
						int inMask = 2*surfaceCollection.getSurfaceCount();
						int outMask = 2*surfaceCollection.getSurfaceCount()+1;
						surface = new OrigSurface(inMask,outMask);
						surface.setExteriorMask(outMask);
						surface.setInteriorMask(inMask);
						surfaceMap.put(attributeByteCounts.get(i),surface);
						surfaceCollection.addSurface(surface);
					}
					surface.addPolygon(triangles.get(i));
				}
			}else{
				OrigSurface surface = new OrigSurface(1,2);
				for (Triangle tri : triangles){
					surface.addPolygon(tri);
				}
				surfaceCollection.addSurface(surface);
			}
			
			//
			// set all nodes to surface collection (global list across all surfaces).
			//
			surfaceCollection.setNodes(nodeMap.values().toArray(new Node[nodeMap.size()]));
			System.out.println("StlReader.readBinaryStl() - numTriangles = "+numFacets
					+", numNodes="+surfaceCollection.getNodeCount()
					+", numSurfaces="+surfaceCollection.getSurfaceCount()
					+", numColors="+uniqueAttributeByteCounts.size());
		}finally{
			channel.close();
		}	
		
		return surfaceCollection;
	}

	public static SurfaceCollection readASCIIStl(File file) throws IOException{
		String fileContents = XmlUtil.getXMLString(file.getAbsolutePath()).trim();
		fileContents = fileContents.replaceFirst("^solid.*", "solid name");//get rid of description text
		fileContents = fileContents.replaceFirst("endsolid.*", "endsolid name");//get rid of description text
		CommentStringTokenizer tokens = new CommentStringTokenizer(fileContents);
		String token = tokens.nextToken();
		if (!token.equalsIgnoreCase("solid")){
			throw new RuntimeException("file not an ASCII STL file, didn't find 'solid'");
		}
		String name = tokens.nextToken();
		
		int inMask = 1;
		int outMask = 2;
		OrigSurface surface = new OrigSurface(inMask,outMask);
		surface.setExteriorMask(outMask);
		surface.setInteriorMask(inMask);
		SurfaceCollection surfaceCollection = new SurfaceCollection();
		surfaceCollection.addSurface(surface);
		HashMap<NodeKey,Node> nodeMap = new HashMap<NodeKey,Node>();
        
		while (tokens.hasMoreTokens()){
			token = tokens.nextToken();
			if (token.equalsIgnoreCase("facet")){
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase("normal")){
					throw new RuntimeException("unexpected token '"+token+"' while reading ASCII STL file at line "+tokens.lineIndex()+", column "+tokens.columnIndex());
				}
				float nx = Float.parseFloat(tokens.nextToken());
				float ny = Float.parseFloat(tokens.nextToken());
				float nz = Float.parseFloat(tokens.nextToken());
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase("outer")){
					throw new RuntimeException("unexpected token '"+token+"' while reading ASCII STL file at line "+tokens.lineIndex()+", column "+tokens.columnIndex());
				}
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase("loop")){
					throw new RuntimeException("unexpected token '"+token+"' while reading ASCII STL file at line "+tokens.lineIndex()+", column "+tokens.columnIndex());
				}
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase("vertex")){
					throw new RuntimeException("unexpected token '"+token+"' while reading ASCII STL file at line "+tokens.lineIndex()+", column "+tokens.columnIndex());
				}
				float v1x = Float.parseFloat(tokens.nextToken());
				float v1y = Float.parseFloat(tokens.nextToken());
				float v1z = Float.parseFloat(tokens.nextToken());
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase("vertex")){
					throw new RuntimeException("unexpected token '"+token+"' while reading ASCII STL file at line "+tokens.lineIndex()+", column "+tokens.columnIndex());
				}
				float v2x = Float.parseFloat(tokens.nextToken());
				float v2y = Float.parseFloat(tokens.nextToken());
				float v2z = Float.parseFloat(tokens.nextToken());
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase("vertex")){
					throw new RuntimeException("unexpected token '"+token+"' while reading ASCII STL file at line "+tokens.lineIndex()+", column "+tokens.columnIndex());
				}
				float v3x = Float.parseFloat(tokens.nextToken());
				float v3y = Float.parseFloat(tokens.nextToken());
				float v3z = Float.parseFloat(tokens.nextToken());
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase("endloop")){
					throw new RuntimeException("unexpected token '"+token+"' while reading ASCII STL file at line "+tokens.lineIndex()+", column "+tokens.columnIndex());
				}
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase("endfacet")){
					throw new RuntimeException("unexpected token '"+token+"' while reading ASCII STL file at line "+tokens.lineIndex()+", column "+tokens.columnIndex());
				}
			
				Node n1 = new Node(v1x,v1y,v1z);
				Node n2 = new Node(v2x,v2y,v2z);
				Node n3 = new Node(v3x,v3y,v3z);

				//
				// merge nodes with identical coordinates.
				//
				NodeKey key1 = new NodeKey(n1);
				NodeKey key2 = new NodeKey(n2);
				NodeKey key3 = new NodeKey(n3);
				
				Node node1 = nodeMap.get(key1);
				if (node1==null){
					node1 = n1;
					nodeMap.put(key1, n1);
				}
				
				Node node2 = nodeMap.get(key2);
				if (node2==null){
					node2 = n2;
					nodeMap.put(key2, n2);
				}
				
				Node node3 = nodeMap.get(key3);
				if (node3==null){
					node3 = n3;
					nodeMap.put(key3, n3);
				}
				
				Triangle triangle = new Triangle(node1,node2,node3);
				
				surface.addPolygon(triangle);
			
			}else if (token.equalsIgnoreCase("endsolid")){
				String end_name = tokens.nextToken();
				// should match name
			}else{
				throw new RuntimeException("unexpected token '"+token+"' while reading ASCII STL file at line "+tokens.lineIndex()+", column "+tokens.columnIndex());
			}
		}
		
		//
		// set all nodes to surface collection (global list across all surfaces).
		//
		surfaceCollection.setNodes(nodeMap.values().toArray(new Node[nodeMap.size()]));
		System.out.println("StlReader.readBinaryStl() - numTriangles = "+surface.getPolygonCount()
				+", numNodes="+surfaceCollection.getNodeCount()
				+", numSurfaces="+surfaceCollection.getSurfaceCount());
		
		return surfaceCollection;
	}

}
