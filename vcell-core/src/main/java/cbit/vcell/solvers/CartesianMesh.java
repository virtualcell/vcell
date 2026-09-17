/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solvers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.*;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.Polygon;
import cbit.vcell.geometry.surface.Quadrilateral;
import cbit.vcell.geometry.surface.Surface;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathFormatException;
import cbit.vcell.math.VCML;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solvers.MeshRegionInfo.MembraneRegionMapVolumeRegion;
import cbit.vcell.solvers.MeshRegionInfo.VolumeRegionMapSubvolume;


public class CartesianMesh implements Serializable, Matchable {
	private final static Logger lg = LogManager.getLogger(CartesianMesh.class);

	private static final long serialVersionUID = 6758903098098649389L;

	protected static class MembraneMeshMetrics {
		public short[] regionIndexes;
		public float[] areas;
		public float[][] normals;
		public float[][] centroids;
	}

	//
	protected byte[] compressedBytes = null;

	protected transient SubdomainInfo subdomainInfo = null;
	protected transient MembraneElement[] membraneElements = null;
	protected transient ContourElement[] contourElements = null;
	protected transient MeshRegionInfo meshRegionInfo = null;
	protected transient ISize size = null;
	protected transient Origin origin = new Origin(0, 0, 0);
	protected transient Extent extent = new Extent(10, 10, 10);
	protected transient String version = VERSION_1_0;

	public final static String VERSION_1_0 = "1.0";        // origin, extent, sizeXYZ,
	// membrane (membraneIndex, insideVolumeIndex, outsideVolumeIndex)

	public final static String VERSION_1_1 = "1.1";        // added: membrane connectivity 8/30/2000
	public final static String VERSION_1_2 = "1.2";        // added: Regions 07/02/2001


	public class UCDInfo {
		private Coordinate[][][] ucdGridNodes;
		private final int[][] ucdMembraneQuads;
		private Vector<Coordinate> reducedUCDGridNodesV;

		private static final String UCD_LINE_CELL_TYPE = "line"; //line (line, 1 edges, 2 corners)
		private static final String UCD_QUAD_CELL_TYPE = "quad"; //quadrilateral (box, 4 edges, 4 corners)
		private static final String UCD_HEX_CELL_TYPE = "hex"; // hexahedron (cube, 12 edges, 6 faces, 8 corners)

		public UCDInfo() {
			this.ucdGridNodes = this.calcUCDGridNodes();
			this.ucdMembraneQuads = this.calcUCDMembraneQuads();
		}

		private UCDInfo(Vector<Coordinate> reducedUCDGridNodesV, int[][] ucdMembraneQuads) {
			this.reducedUCDGridNodesV = reducedUCDGridNodesV;
			this.ucdMembraneQuads = ucdMembraneQuads;
		}

		public UCDInfo removeNonMembraneGridNodes() {
			int maxGridNodes =
					this.calcNodeCount(Coordinate.Axis.X) *
							this.calcNodeCount(Coordinate.Axis.Y) *
							this.calcNodeCount(Coordinate.Axis.Z);
			int[] mapOrigNodeIndexToNewNodeIndex = new int[maxGridNodes];
			Arrays.fill(mapOrigNodeIndexToNewNodeIndex, -1);
			Vector<Coordinate> newGridNodes = new Vector<Coordinate>();
			int[][] newMembraneQuads = new int[this.getUCDMembraneQuads().length][4];
			final int xNodeCount = this.calcNodeCount(Coordinate.Axis.X);
			final int yNodeCount = this.calcNodeCount(Coordinate.Axis.Y);
			final int xyNodeCount = xNodeCount * yNodeCount;
			for (int i = 0; i < this.getUCDMembraneQuads().length; i++) {
				for (int j = 0; j < this.getUCDMembraneQuads()[i].length; j++) {
					int gridNodeIndex = this.getUCDMembraneQuads()[i][j];
					if (mapOrigNodeIndexToNewNodeIndex[gridNodeIndex] == -1) {
						mapOrigNodeIndexToNewNodeIndex[gridNodeIndex] = newGridNodes.size();
						int origGridX = gridNodeIndex % xNodeCount;
						int origGridY = (gridNodeIndex % xyNodeCount) / xNodeCount;
						int origGridZ = gridNodeIndex / xyNodeCount;
						newGridNodes.add(this.getUCDGridNodes()[origGridZ][origGridY][origGridX]);
					}
					newMembraneQuads[i][j] = mapOrigNodeIndexToNewNodeIndex[gridNodeIndex];
				}
			}
			return new UCDInfo(newGridNodes, newMembraneQuads);
		}

		public String getHeaderString(int numCellVolumeData, int numCellMembraneData) {
			//Nodes of Hexahedral mesh volume elements (must be calculated using mesh info)
			int numNodeData = 0;

			int numCells =
					(numCellVolumeData == 0 ? 0 : CartesianMesh.this.getNumVolumeElements()) +
							(numCellMembraneData == 0 ? 0 : CartesianMesh.this.getNumMembraneElements());

			int numCellData = numCellVolumeData + numCellMembraneData;

			int numModelData = 0;//never used, always set to 0

			//UCD Header
			return this.getNumPointsXYZ() + " " + numCells + " " + numNodeData + " " + numCellData + " " + numModelData + "\n";

		}

		public String getMeshGridNodesString(boolean bVTK) {
			//UCD Node Coordinates (used for Volume cells and unsmoothed Membrane cells)
			StringBuffer volGridNodeSB = new StringBuffer();
			int nodeCountStart = 0;
			if (this.ucdGridNodes != null) {//Full Grid Nodes
				for (int z = 0; z < this.getNumVolumeNodesZ(); z++) {
					for (int y = 0; y < this.getNumVolumeNodesY(); y++) {
						for (int x = 0; x < this.getNumVolumeNodesX(); x++) {
							volGridNodeSB.append(
									(bVTK ? "" : nodeCountStart + " ") +
											this.ucdGridNodes[z][y][x].x() + " " +
											this.ucdGridNodes[z][y][x].y() + " " +
											this.ucdGridNodes[z][y][x].z() +
											"\n");
							nodeCountStart++;
						}
					}
				}
			} else {//Reduced Grid Nodes (Only include thos needed for membranes)
				for (int i = 0; i < this.reducedUCDGridNodesV.size(); i++) {
					volGridNodeSB.append(
							(bVTK ? "" : nodeCountStart + " ") +
									this.reducedUCDGridNodesV.get(i).x() + " " +
									this.reducedUCDGridNodesV.get(i).y() + " " +
									this.reducedUCDGridNodesV.get(i).z() +
									"\n");
					nodeCountStart++;
				}
			}
			return volGridNodeSB.toString();
		}

		public String getVolumeCellsString(boolean bVTK) {
			StringBuffer volCellsSB = new StringBuffer();
			//UCD Cells (Volume)
			int volCellID = 0;
			for (int z = 0; z < (this.getNumVolumeNodesZ() == 1 ? 1 : this.getNumVolumeNodesZ() - 1); z++) {
				int hexCornerZ = z * this.getNumVolumeNodesXY();
				for (int y = 0; y < (this.getNumVolumeNodesY() == 1 ? 1 : this.getNumVolumeNodesY() - 1); y++) {
					int hexCornerY = hexCornerZ + (y * this.getNumVolumeNodesX());
					for (int x = 0; x < (this.getNumVolumeNodesX() - 1); x++) {
						int hexCornerX = hexCornerY + x;
						volCellsSB.append(
								(bVTK ? (this.getNumVolumeNodesZ() == 1 ? "4" : "8") : volCellID + " " + CartesianMesh.this.getVolumeRegionIndex(CartesianMesh.this.getVolumeIndex(x, y, z)) + " " + (this.getNumVolumeNodesZ() == 1 ? UCD_QUAD_CELL_TYPE : UCD_HEX_CELL_TYPE)) + " " +
										(hexCornerX) + " " +
										(hexCornerX + 1) + " " +
										(hexCornerX + 1 + this.getNumVolumeNodesX()) + " " +
										(hexCornerX + this.getNumVolumeNodesX()));
						if (this.getNumVolumeNodesZ() > 1) {
							volCellsSB.append(" " +
									(hexCornerX + this.getNumVolumeNodesXY()) + " " +
									(hexCornerX + 1 + this.getNumVolumeNodesXY()) + " " +
									(hexCornerX + 1 + this.getNumVolumeNodesX() + this.getNumVolumeNodesXY()) + " " +
									(hexCornerX + this.getNumVolumeNodesX() + this.getNumVolumeNodesXY()));
						}
						volCellsSB.append("\n");
						volCellID++;
					}
				}
			}
			return volCellsSB.toString();
		}

		public String getMembraneCellsString(int cellIDStart, boolean bVTK) {
			StringBuffer membrCellsString = new StringBuffer();
			int MembCellID = 0;
			for (int i = 0; i < this.ucdMembraneQuads.length; i++) {
				membrCellsString.append(
						(bVTK ? (this.getNumVolumeNodesZ() == 1 ? "2" : "4") : (MembCellID + cellIDStart) + " " + (CartesianMesh.this.getMembraneRegionIndex(i) + 1) + " " + (this.getNumVolumeNodesZ() == 1 ? UCD_LINE_CELL_TYPE : UCD_QUAD_CELL_TYPE)) + " " +
								this.ucdMembraneQuads[i][0] + " " + this.ucdMembraneQuads[i][1]);
				if (this.getNumVolumeNodesZ() > 1) {
					membrCellsString.append(" " + this.ucdMembraneQuads[i][2] + " " + this.ucdMembraneQuads[i][3]);
				}
				membrCellsString.append("\n");
				MembCellID++;
			}
			return membrCellsString.toString();
		}

		public void writeCellData(boolean bFill,
		                          String[] volumeVariableNames, String[] volumeVariableUnits, double[][] volumeData,
		                          String[] membraneVariableNames, String[] membraneVariableUnits, double[][] membraneData,
		                          java.io.Writer writer) throws Exception {

			if (volumeData != null && volumeData.length == 0) {
				volumeData = null;
			}
			if (membraneData != null && membraneData.length == 0) {
				membraneData = null;
			}
			if (volumeData != null && volumeData[0].length != CartesianMesh.this.getNumVolumeElements()) {
				throw new Exception("Volume data length does not match mesh info.");
			}
			if (membraneData != null && membraneData[0].length != CartesianMesh.this.getNumMembraneElements()) {
				throw new Exception("Membrane data length does not match mesh info.");
			}

			int numCellData = (volumeData != null ? volumeData.length : 0) + (membraneData != null ? membraneData.length : 0);

			String cellDataS = numCellData + "";
			for (int i = 0; i < numCellData; i++) {
				cellDataS = cellDataS + " 1";
			}
			writer.write(cellDataS + "\n");
			for (int i = 0; volumeVariableNames != null && i < volumeVariableNames.length; i++) {
				writer.write(volumeVariableNames[i] + "," + volumeVariableUnits[i] + "\n");
			}
			for (int i = 0; membraneVariableNames != null && i < membraneVariableNames.length; i++) {
				writer.write(membraneVariableNames[i] + "," + membraneVariableUnits[i] + "\n");
			}

			String membDataFiller = "";
			for (int i = 0; membraneData != null && i < membraneData.length; i++) {
				membDataFiller += " 0";
			}
			String volDataFiller = "";
			for (int i = 0; volumeData != null && i < volumeData.length; i++) {
				volDataFiller += "0 ";
			}

			if (volumeData != null || bFill) {
				for (int i = 0; i < CartesianMesh.this.getNumVolumeElements(); i++) {
					writer.write(i + " ");
					if (volumeData != null) {
						for (int j = 0; j < volumeData.length; j++) {
							writer.write((j != 0 ? " " : "") + volumeData[j][i]);
						}
					} else if (bFill) {
						writer.write(volDataFiller);
					}
					writer.write(membDataFiller + "\n");
				}
			}

			if (membraneData != null || bFill) {
				for (int i = 0; i < CartesianMesh.this.getNumMembraneElements(); i++) {
					writer.write(((bFill ? this.getNumVolumeCells() : 0) + i) + " ");
					writer.write(volDataFiller);
					if (membraneData != null) {
						for (int j = 0; j < membraneData.length; j++) {
							writer.write((j != 0 ? " " : "") + membraneData[j][i]);
						}
					} else if (bFill) {
						writer.write(membDataFiller);
					}
					writer.write("\n");
				}
			}

		}

		public Coordinate[][][] getUCDGridNodes() {
			return this.ucdGridNodes;
		}

		public int[][] getUCDMembraneQuads() {
			return this.ucdMembraneQuads;
		}

		public int getNumVolumeNodesX() {
			return this.calcNodeCount(Coordinate.Axis.X);
		}

		public int getNumVolumeNodesY() {
			return this.calcNodeCount(Coordinate.Axis.Y);
		}

		public int getNumVolumeNodesZ() {
			return this.calcNodeCount(Coordinate.Axis.Z);
		}

		public int getNumPointsXYZ() {
			if (this.ucdGridNodes != null) {
				return
						this.getNumVolumeNodesX() *
								this.getNumVolumeNodesY() *
								this.getNumVolumeNodesZ();
			}
			return this.reducedUCDGridNodesV.size();
		}

		public int getNumVolumeNodesXY() {
			return
					this.getNumVolumeNodesX() *
							this.getNumVolumeNodesY();
		}

		public int getNumVolumeCells() {
			return CartesianMesh.this.getNumVolumeElements();
		}

		public int getNumMembraneCells() {
			return CartesianMesh.this.getNumMembraneElements();
		}

		private int calcNodeCount(Coordinate.Axis axis) {
			if (axis == Coordinate.Axis.X) {
				return 1 + CartesianMesh.this.getSizeX();
			} else if (axis == Coordinate.Axis.Y) {
				return 1 + (CartesianMesh.this.getGeometryDimension() > 1 ? CartesianMesh.this.getSizeY() : 0);
			} else if (axis == Coordinate.Axis.Z) {
				return 1 + (CartesianMesh.this.getGeometryDimension() > 2 ? CartesianMesh.this.getSizeZ() : 0);
			}
			throw new RuntimeException("Axis value " + axis + " unknown");
		}

		private Coordinate[][][] calcUCDGridNodes() {
			Coordinate[][][] ucdMeshNodes =
					new Coordinate[this.calcNodeCount(Coordinate.Axis.Z)]
							[this.calcNodeCount(Coordinate.Axis.Y)]
							[this.calcNodeCount(Coordinate.Axis.X)];
			double dx = CartesianMesh.this.getExtent().getX() / (CartesianMesh.this.getSizeX() - 1);
			double dy = (CartesianMesh.this.getGeometryDimension() > 1 ? CartesianMesh.this.getExtent().getY() / (CartesianMesh.this.getSizeY() - 1) : 0);
			double dz = (CartesianMesh.this.getGeometryDimension() > 2 ? CartesianMesh.this.getExtent().getZ() / (CartesianMesh.this.getSizeZ() - 1) : 0);
			CoordinateIndex coordIndex = new CoordinateIndex(0, 0, 0);
			for (int z = 0; z < CartesianMesh.this.getSizeZ(); z++) {
				coordIndex.z = z;
				boolean bZEnd = (z + 1 == CartesianMesh.this.getSizeZ()) && (CartesianMesh.this.getSizeZ() > 1);
				for (int y = 0; y < CartesianMesh.this.getSizeY(); y++) {
					coordIndex.y = y;
					boolean bYEnd = y + 1 == CartesianMesh.this.getSizeY();
					for (int x = 0; x < CartesianMesh.this.getSizeX(); x++) {
						coordIndex.x = x;
						boolean bXEnd = x + 1 == CartesianMesh.this.getSizeX();

						//Get mesh volume element center coordinate.
						Coordinate volCenterCoord = CartesianMesh.this.getCoordinate(coordIndex);

						//convert volume center coord to UCD grid "cell" node
						double nodeX = volCenterCoord.x() - (x == 0 ? 0 : dx / 2);
						double nodeY = volCenterCoord.y() - (y == 0 ? 0 : dy / 2);
						double nodeZ = volCenterCoord.z() - (z == 0 ? 0 : dz / 2);
						ucdMeshNodes[z][y][x] = new Coordinate(nodeX, nodeY, nodeZ);

						//Add "end" nodes when necessary
						if (bXEnd) {
							ucdMeshNodes[z][y][x + 1] = new Coordinate(volCenterCoord.x(), nodeY, nodeZ);
						}
						if (bYEnd) {
							ucdMeshNodes[z][y + 1][x] = new Coordinate(nodeX, volCenterCoord.y(), nodeZ);
						}
						if (bZEnd) {
							ucdMeshNodes[z + 1][y][x] = new Coordinate(nodeX, nodeY, volCenterCoord.z());
						}

						if (bXEnd && bYEnd) {
							ucdMeshNodes[z][y + 1][x + 1] = new Coordinate(volCenterCoord.x(), volCenterCoord.y(), nodeZ);
						}
						if (bYEnd && bZEnd) {
							ucdMeshNodes[z + 1][y + 1][x] = new Coordinate(nodeX, volCenterCoord.y(), volCenterCoord.z());
						}
						if (bZEnd && bXEnd) {
							ucdMeshNodes[z + 1][y][x + 1] = new Coordinate(volCenterCoord.x(), nodeY, volCenterCoord.z());
						}

						if (bXEnd && bYEnd && bZEnd) {
							ucdMeshNodes[z + 1][y + 1][x + 1] = new Coordinate(volCenterCoord.x(), volCenterCoord.y(), volCenterCoord.z());
						}
					}
				}
			}

			return ucdMeshNodes;
		}

		private int[][] calcUCDMembraneQuads() {
			int ZOFFSET = (CartesianMesh.this.getSizeZ() == 1 ? 0 : 1);
			int[][] membrQuadNodes = new int[CartesianMesh.this.getMembraneElements().length][4];
			final int xNodeCount = this.calcNodeCount(Coordinate.Axis.X);
			final int yNodeCount = this.calcNodeCount(Coordinate.Axis.Y);
			final int xyNodeCount = xNodeCount * yNodeCount;
			for (int i = 0; i < CartesianMesh.this.getMembraneElements().length; i++) {
				CoordinateIndex coordIndex0 = CartesianMesh.this.getCoordinateIndexFromVolumeIndex(CartesianMesh.this.getMembraneElements()[i].getInsideVolumeIndex());
				CoordinateIndex coordIndex1 = CartesianMesh.this.getCoordinateIndexFromVolumeIndex(CartesianMesh.this.getMembraneElements()[i].getOutsideVolumeIndex());
				if (coordIndex0.y == coordIndex1.y && coordIndex0.z == coordIndex1.z) {//perpendicular x-axis
					int xBase = Math.max(coordIndex0.x, coordIndex1.x);
					membrQuadNodes[i][0] = xBase + (coordIndex0.y * xNodeCount) + (coordIndex0.z * xyNodeCount);
					membrQuadNodes[i][1] = xBase + ((coordIndex0.y + 1) * xNodeCount) + (coordIndex0.z * xyNodeCount);
					membrQuadNodes[i][2] = xBase + ((coordIndex0.y + 1) * xNodeCount) + ((coordIndex0.z + ZOFFSET) * xyNodeCount);
					membrQuadNodes[i][3] = xBase + ((coordIndex0.y) * xNodeCount) + ((coordIndex0.z + ZOFFSET) * xyNodeCount);

				} else if (coordIndex0.x == coordIndex1.x && coordIndex0.z == coordIndex1.z) {//perpendicular y-axis
					int yBase = Math.max(coordIndex0.y, coordIndex1.y);
					membrQuadNodes[i][0] = coordIndex0.x + (yBase * xNodeCount) + (coordIndex0.z * xyNodeCount);
					membrQuadNodes[i][1] = coordIndex0.x + 1 + (yBase * xNodeCount) + (coordIndex0.z * xyNodeCount);
					membrQuadNodes[i][2] = coordIndex0.x + 1 + (yBase * xNodeCount) + ((coordIndex0.z + ZOFFSET) * xyNodeCount);
					membrQuadNodes[i][3] = coordIndex0.x + (yBase * xNodeCount) + ((coordIndex0.z + ZOFFSET) * xyNodeCount);

				} else if (coordIndex0.x == coordIndex1.x && coordIndex0.y == coordIndex1.y) {//perpendicular z-axis
					int zBase = Math.max(coordIndex0.z, coordIndex1.z);
					membrQuadNodes[i][0] = coordIndex0.x + (coordIndex0.y * xNodeCount) + (zBase * xyNodeCount);
					membrQuadNodes[i][1] = coordIndex0.x + ((coordIndex0.y + 1) * xNodeCount) + (zBase * xyNodeCount);
					membrQuadNodes[i][2] = coordIndex0.x + 1 + ((coordIndex0.y + 1) * xNodeCount) + (zBase * xyNodeCount);
					membrQuadNodes[i][3] = coordIndex0.x + 1 + ((coordIndex0.y) * xNodeCount) + (zBase * xyNodeCount);

				} else {
					throw new RuntimeException("No boundary found for MembraneElement " + i + " in=" + coordIndex0 + " out=" + coordIndex1);
				}
			}
			return membrQuadNodes;
		}

	}

	protected CartesianMesh() {
	}

	public double calculateMeshElementVolumeFromVolumeIndex(int volumeIndex) {

		CoordinateIndex ci = this.getCoordinateIndexFromVolumeIndex(volumeIndex);
		double fxMESize = (ci.x == 0 || ci.x == (this.getSizeX() - 1) ? .5 : 1);
		double fyMESize = (this.getGeometryDimension() >= 2 ? (ci.y == 0 || ci.y == (this.getSizeY() - 1) ? .5 : 1) : 0);
		double fzMESize = (this.getGeometryDimension() == 3 ? (ci.z == 0 || ci.z == (this.getSizeZ() - 1) ? .5 : 1) : 0);

		return (fxMESize != 0 ? fxMESize * (this.getExtent().getX() / (this.getSizeX() - 1)) : 1) *
				(fyMESize != 0 ? fyMESize * (this.getExtent().getY() / (this.getSizeY() - 1)) : 1) *
				(fzMESize != 0 ? fzMESize * (this.getExtent().getZ() / (this.getSizeZ() - 1)) : 1);

	}


	public boolean compareEqual(Matchable object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		CartesianMesh mesh = null;
		if (!(object instanceof CartesianMesh)) {
			return false;
		} else {
			mesh = (CartesianMesh) object;
		}

		if (!Compare.isEqualOrNullStrict(this.membraneElements, mesh.membraneElements)) {
			return false;
		}
		if (!Compare.isEqualOrNullStrict(this.contourElements, mesh.contourElements)) {
			return false;
		}
		if (!this.size.compareEqual(mesh.size)) {
			return false;
		}
	/*
	if (sizeX!=mesh.sizeX || sizeY!=mesh.sizeY || sizeZ!=mesh.sizeZ){
		return false;
	}
	*/
		return this.origin.compareEqual(mesh.origin);
	}


	public static boolean compareMesh(CartesianMesh mesh1, CartesianMesh mesh2, PrintWriter pw) {
		try {
			if (!CartesianMesh.isSpatialDomainSame(mesh1, mesh2)) {
				return false;
			}
			if (!Compare.isEqualOrNullStrict(mesh1.getContourElements(), mesh2.getContourElements())) {
				pw.println("ContourElemment is different!");
				System.out.println("ContourElemment is different!");
				return false;
			}

			MeshRegionInfo meshRegionInfo1 = mesh1.getMeshRegionInfo();
			MeshRegionInfo meshRegionInfo2 = mesh2.getMeshRegionInfo();


			// Compare VolumeRegionMapSubVolume
			if (meshRegionInfo1.getNumVolumeRegions() != meshRegionInfo2.getNumVolumeRegions()) {
				pw.println("# of volume regions is different!");
				return false;
			}
			Vector<MeshRegionInfo.VolumeRegionMapSubvolume> vrmsv1 = meshRegionInfo1.getVolumeRegionMapSubvolume();
			Vector<MeshRegionInfo.VolumeRegionMapSubvolume> vrmsv2 = meshRegionInfo2.getVolumeRegionMapSubvolume();

			double precision = 1.1e-15;
			double MAX_REL_ERROR = 1.1e-15;

			for (int i = 0; i < meshRegionInfo1.getNumVolumeRegions(); i++) {
				MeshRegionInfo.VolumeRegionMapSubvolume region1 = vrmsv1.elementAt(i);
				MeshRegionInfo.VolumeRegionMapSubvolume region2 = vrmsv2.elementAt(i);
				double region1Volume = region1.volumeRegionVolume;
				double region2Volume = region2.volumeRegionVolume;

				if (region1.volumeRegionID != region2.volumeRegionID
						|| region1.subvolumeID != region2.subvolumeID
						|| (Math.abs(region1Volume - region2Volume) > precision && Math.abs(region1Volume - region2Volume) / region1Volume > MAX_REL_ERROR)) {
					System.out.println("VolumeRegionMapSubVolume is different!");
					pw.println("VolumeRegionMapSubVolume is different!");
					return false;
				}
			}

			// Compare MembraneRegionsMapVolumeRegion
			if (meshRegionInfo1.getNumMembraneRegions() != meshRegionInfo2.getNumMembraneRegions()) {
				System.out.println("# of MembraneRegions is different");
				pw.println("# of MembraneRegions is different");
				return false;
			}

			Vector<MeshRegionInfo.MembraneRegionMapVolumeRegion> mrmsv1 = meshRegionInfo1.getMembraneRegionMapVolumeRegion();
			Vector<MeshRegionInfo.MembraneRegionMapVolumeRegion> mrmsv2 = meshRegionInfo2.getMembraneRegionMapVolumeRegion();

			for (int i = 0; i < meshRegionInfo2.getNumMembraneRegions(); i++) {
				MeshRegionInfo.MembraneRegionMapVolumeRegion region1 = mrmsv1.elementAt(i);
				MeshRegionInfo.MembraneRegionMapVolumeRegion region2 = mrmsv2.elementAt(i);

				double region1Surface = region1.membraneRegionSurface;
				double region2Surface = region2.membraneRegionSurface;

				// compare surface area, ignoring the index
				if (Math.abs(region1Surface - region2Surface) / region1Surface > MAX_REL_ERROR && Math.abs(region1Surface - region2Surface) > precision) {
					System.out.println("MembraneRegionsMapVolumeRegion is different!");
					pw.println("MembraneRegionsMapVolumeRegion is different!");
					return false;
				}
			}

			// Compare VolumeElementsMapVolumeRegion
			if (mesh1.getNumVolumeElements() != mesh2.getNumVolumeElements()) {
				return false;
			}
			for (int i = 0; i < mesh1.getNumVolumeElements(); i++) {
				if (mesh1.getVolumeRegionIndex(i) != mesh2.getVolumeRegionIndex(i)) {
					System.out.println("VolumeElementsMapVolumeRegion is different at " + i);
					pw.println("VolumeElementsMapVolumeRegion is different at " + i);
					return false;
				}
			}

			// Compare Membrane Elements
			if (mesh1.getNumMembraneElements() != mesh2.getNumMembraneElements()) {
				return false;
			}
			if (mesh1.getNumMembraneElements() == 0) {
				return true;
			}
			MembraneElement[] memElements1 = mesh1.getMembraneElements();
			MembraneElement[] memElements2 = mesh2.getMembraneElements();

			for (int i = 0; i < mesh1.getNumMembraneElements(); i++) {

				// index
				if (memElements1[i].getMembraneIndex() != memElements2[i].getMembraneIndex()) {
					return false;
				}

				// Inside
				if (memElements1[i].getInsideVolumeIndex() != memElements2[i].getInsideVolumeIndex()) {
					return false;
				}

				// Outside
				if (memElements1[i].getOutsideVolumeIndex() != memElements2[i].getOutsideVolumeIndex()) {
					return false;
				}

				// Neighbors
				int[] neighborIndexes1 = memElements1[i].getMembraneNeighborIndexes();
				int[] neighborIndexes2 = memElements2[i].getMembraneNeighborIndexes();
				if ((neighborIndexes1 != null && neighborIndexes2 == null) ||
						(neighborIndexes1 == null && neighborIndexes2 != null)) {
					return false;
				}
				if (neighborIndexes1 != null && neighborIndexes2 != null) {
					if (neighborIndexes1.length != neighborIndexes2.length) {
						return false;
					}
					for (int k = 0; k < neighborIndexes1.length; k++) {
						if (neighborIndexes1[k] != neighborIndexes2[k]) {
							return false;
						}
					}
				}
				// Membrane Region ID
				if (mesh1.getMembraneRegionIndex(i) != mesh2.getMembraneRegionIndex(i)) {
					return false;
				}

				// X, Y, Z
				if (memElements1[i].centroidX != memElements2[i].centroidX
						|| memElements1[i].centroidY != memElements2[i].centroidY
						|| memElements1[i].centroidZ != memElements2[i].centroidZ) {
					return false;
				}
				// area
				if (Math.abs(memElements1[i].area - memElements2[i].area) > precision) {
					return false;
				}
				// normal
				if (Math.abs(memElements1[i].normalX - memElements2[i].normalX) > precision
						|| Math.abs(memElements1[i].normalY - memElements2[i].normalY) > precision
						|| Math.abs(memElements1[i].normalZ - memElements2[i].normalZ) > precision) {
					return false;
				}
			}

		} catch (Exception ex) {
			lg.error(ex.getMessage(), ex);
		}
		return true;
	}


	public static double coordComponentFromSinglePlanePolicy(Origin argOrigin, Extent argExtent, Coordinate.Axis argAxisFlag) {
		return Coordinate.coordComponentFromSinglePlanePolicy(argOrigin, argExtent, argAxisFlag);
	}


	public static CartesianMesh createSimpleCartesianMesh(Origin orig, Extent extent, ISize size, RegionImage regionImage) throws IOException {
		return createSimpleCartesianMesh(orig, extent, size, regionImage, false);
	}

	public static CartesianMesh createSimpleCartesianMesh(Origin orig, Extent extent, ISize size, RegionImage regionImage, boolean bCreateSubvolumeMap) throws IOException {
		CartesianMesh mesh = new CartesianMesh();
		mesh.setOrigin(orig);
		mesh.setExtent(extent);
		mesh.setSize(size.getX(), size.getY(), size.getZ());

		mesh.meshRegionInfo = new MeshRegionInfo();
		byte[] compressRegionBytes = CompressionUtils.compress(regionImage.getShortEncodedRegionIndexImage());
		mesh.meshRegionInfo.setCompressedVolumeElementMapVolumeRegion(compressRegionBytes, mesh.getNumVolumeElements());
		if (bCreateSubvolumeMap) {
			for (int i = 0; i < regionImage.getNumRegions(); i++) {
				mesh.meshRegionInfo.mapVolumeRegionToSubvolume(i, 0, 1, "region" + i);
			}
		}
		return mesh;
	}

	public static CartesianMesh createSimpleCartesianMesh(Geometry geometry) throws IOException, MathFormatException {
		return createSimpleCartesianMesh(geometry, null);
	}

	public static CartesianMesh createSimpleCartesianMesh(Geometry geometry, Map<Polygon, MembraneElement> polygonMembaneElementMap) throws IOException, MathFormatException {
		GeometrySurfaceDescription geometrySurfaceDescription = geometry.getGeometrySurfaceDescription();
		RegionImage regionImage = geometrySurfaceDescription.getRegionImage();
		ISize iSize = new ISize(regionImage.getNumX(), regionImage.getNumY(), regionImage.getNumZ());
		CartesianMesh mesh = createSimpleCartesianMesh(geometry.getOrigin(), geometry.getExtent(), iSize, regionImage);

		GeometricRegion[] geometricRegions = geometrySurfaceDescription.getGeometricRegions();
		if (geometricRegions != null) {
			int memRegionCount = 0;
			for (int i = 0; i < geometricRegions.length; i++) {
				if (geometricRegions[i] instanceof VolumeGeometricRegion vgr) {
					mesh.meshRegionInfo.mapVolumeRegionToSubvolume(vgr.getRegionID(), vgr.getSubVolume().getHandle(), vgr.getSize(), vgr.getName());
				} else if (geometricRegions[i] instanceof SurfaceGeometricRegion sgr) {
					GeometricRegion[] neighbors = sgr.getAdjacentGeometricRegions();
					VolumeGeometricRegion insideRegion = (VolumeGeometricRegion) neighbors[0];
					VolumeGeometricRegion outsideRegion = (VolumeGeometricRegion) neighbors[1];
					mesh.meshRegionInfo.mapMembraneRegionToVolumeRegion(memRegionCount, insideRegion.getRegionID(), outsideRegion.getRegionID(), sgr.getSize());
					memRegionCount++;
				}
			}
		}
		SurfaceCollection surfaceCollection = geometrySurfaceDescription.getSurfaceCollection();
		if (surfaceCollection != null) {
			int numMembraneElement = surfaceCollection.getTotalPolygonCount();
			mesh.membraneElements = new MembraneElement[numMembraneElement];
			boolean bMembraneEdgeNeighborsAvailable =
					surfaceCollection.getMembraneEdgeNeighbors() != null &&
							surfaceCollection.getMembraneEdgeNeighbors().length == surfaceCollection.getSurfaceCount();

			int[] membraneElementMapMembraneRegion = new int[numMembraneElement];
			mesh.meshRegionInfo.mapMembraneElementsToMembraneRegions(membraneElementMapMembraneRegion);
			int memCount = 0;
			int[] membraneNeighbors = new int[]{0, 0, 0, 0};//original values when no membraneedgeneighbors
			for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++) {
				Surface surface = surfaceCollection.getSurfaces(i);
				bMembraneEdgeNeighborsAvailable = bMembraneEdgeNeighborsAvailable && surfaceCollection.getMembraneEdgeNeighbors()[i].length == surface.getPolygonCount();
				for (int j = 0; j < surface.getPolygonCount(); j++) {
					if (bMembraneEdgeNeighborsAvailable) {
						membraneNeighbors = new int[MembraneElement.MAX_POSSIBLE_NEIGHBORS];
						Arrays.fill(membraneNeighbors, MembraneElement.NEIGHBOR_UNDEFINED);
						for (int k = 0; k < surfaceCollection.getMembraneEdgeNeighbors()[i][j].size(); k++) {
							membraneNeighbors[k] = surfaceCollection.getMembraneEdgeNeighbors()[i][j].get(k).getMasterPolygonIndex();
						}
					}
					Quadrilateral polygon = (Quadrilateral) surface.getPolygons(j);
					int volNeighbor1Region = regionImage.getRegionInfoFromOffset(polygon.getVolIndexNeighbor1()).getRegionIndex();
					int volNeighbor2Region = regionImage.getRegionInfoFromOffset(polygon.getVolIndexNeighbor2()).getRegionIndex();

					HashMap<Integer, int[]> map = mesh.getMembraneRegionMapSubvolumesInOut();
					Set<Entry<Integer, int[]>> entries = map.entrySet();
					for (Entry<Integer, int[]> entry : entries) {
						int[] volNeighbors = entry.getValue();
						if (volNeighbors[0] == volNeighbor1Region && volNeighbors[1] == volNeighbor2Region
								|| volNeighbors[1] == volNeighbor1Region && volNeighbors[0] == volNeighbor2Region) {
							membraneElementMapMembraneRegion[memCount] = entry.getKey();
							break;
						}
					}
					mesh.membraneElements[memCount] = new MembraneElement(memCount, polygon.getVolIndexNeighbor1(), polygon.getVolIndexNeighbor2(),
							membraneNeighbors[0], membraneNeighbors[1], membraneNeighbors[2], membraneNeighbors[3], MembraneElement.AREA_UNDEFINED, 0, 0, 0, 0, 0, 0);
					if (polygonMembaneElementMap != null) {
						polygonMembaneElementMap.put(polygon, mesh.membraneElements[memCount]);
					}
					memCount++;
				}
			}
		}

		return mesh;
	}

	public static boolean isSpatialDomainSame(CartesianMesh mesh1, CartesianMesh mesh2) {
		if (mesh1.getGeometryDimension() < 1 || mesh1.getGeometryDimension() > 3 ||
				mesh2.getGeometryDimension() < 1 || mesh2.getGeometryDimension() > 3) {
			throw new IllegalArgumentException("CartesianMesh.isSpatialDomain:  expecting dimension 1, 2 or 3");
		}
		if (mesh1.getGeometryDimension() != mesh2.getGeometryDimension()) {
			return false;
		}
		if (mesh1.getGeometryDimension() >= 1) {
			if ((mesh1.getSizeX() != mesh2.getSizeX()) ||
					(mesh1.getOrigin().getX() != mesh2.getOrigin().getX()) ||
					(mesh1.getExtent().getX() != mesh2.getExtent().getX())) {
				return false;
			}
		}
		if (mesh1.getGeometryDimension() >= 2) {
			if ((mesh1.getSizeY() != mesh2.getSizeY()) ||
					(mesh1.getOrigin().getY() != mesh2.getOrigin().getY()) ||
					(mesh1.getExtent().getY() != mesh2.getExtent().getY())) {
				return false;
			}
		}
		if (mesh1.getGeometryDimension() == 3) {
			return (mesh1.getSizeZ() == mesh2.getSizeZ()) &&
					(mesh1.getOrigin().getZ() == mesh2.getOrigin().getZ()) &&
					(mesh1.getExtent().getZ() == mesh2.getExtent().getZ());
		}

		return true;
	}

	public ContourElement[] getContourElements() {
		if (this.contourElements == null) {
			this.inflate();
		}
		return this.contourElements;
	}


	public int getContourRegionIndex(int contourIndex) {
		throw new RuntimeException("CartesianMesh.getContourRegionIndex() not yet implemented");
		//ContourElement contourElement = getContourElements()[contourIndex];
		//return contourElement.getRegionIndex();
	}


	public UCDInfo getUCDInfo() {
		return new UCDInfo();
	}

	public Coordinate getCoordinate(CoordinateIndex coordIndex) {
		//
		//
		// calculate coordinates based on element coordinates
		//
		//     1--------2--------3-------4--------N
		//
		// so spacing is divided into (N-1) regions.
		//
		//
		// if N is 1, then take middle of that dimension
		//
		//
		double x = Coordinate.coordComponentFromSinglePlanePolicy(this.origin, this.extent, Coordinate.Axis.X);//extent.getX()/2.0 + origin.getX();
		if (this.getSizeX() > 1) {
			x = (((double) coordIndex.x) / (this.getSizeX() - 1)) * this.extent.getX() + this.origin.getX();
		}
		double y = Coordinate.coordComponentFromSinglePlanePolicy(this.origin, this.extent, Coordinate.Axis.Y);//extent.getY()/2.0 + origin.getY();
		if (this.getSizeY() > 1) {
			y = (((double) coordIndex.y) / (this.getSizeY() - 1)) * this.extent.getY() + this.origin.getY();
		}
		double z = Coordinate.coordComponentFromSinglePlanePolicy(this.origin, this.extent, Coordinate.Axis.Z);//extent.getZ()/2.0 + origin.getZ();
		if (this.getSizeZ() > 1) {
			z = (((double) coordIndex.z) / (this.getSizeZ() - 1)) * this.extent.getZ() + this.origin.getZ();
		}
		return (new Coordinate(x, y, z));
	}


	public Coordinate getCoordinateFromContourIndex(int contourIndex) {
		ContourElement ce = this.getContourElements()[contourIndex];
		Coordinate begCoord = ce.getBeginCoordinate();
		Coordinate endCoord = ce.getEndCoordinate();
		return (new Coordinate(
				0.5 * (begCoord.x() + endCoord.x()),
				0.5 * (begCoord.y() + endCoord.y()),
				0.5 * (begCoord.z() + endCoord.z())));
	}


	public Coordinate getCoordinateFromMembraneIndex(int membraneIndex) {
		MembraneElement me = this.getMembraneElements()[membraneIndex];
		Coordinate inCoord = this.getCoordinateFromVolumeIndex(me.getInsideVolumeIndex());
		Coordinate outCoord = this.getCoordinateFromVolumeIndex(me.getOutsideVolumeIndex());
		return (new Coordinate(
				0.5 * (inCoord.x() + outCoord.x()),
				0.5 * (inCoord.y() + outCoord.y()),
				0.5 * (inCoord.z() + outCoord.z())));
	}


	public Coordinate getCoordinateFromVolumeIndex(int volumeIndex) {
		return this.getCoordinate(this.getCoordinateIndexFromVolumeIndex(volumeIndex));
	}

	public CoordinateIndex getCoordinateIndexFromFractionalIndex(Coordinate fractionalIndex) {
		return new CoordinateIndex((int) Math.round(fractionalIndex.x()),
				(int) Math.round(fractionalIndex.y()),
				(int) Math.round(fractionalIndex.z()));
	}

	public CoordinateIndex getCoordinateIndexFromVolumeIndex(int volIndex) {
		int volZ = volIndex / (this.getSizeX() * this.getSizeY());
		volIndex -= volZ * (this.getSizeX() * this.getSizeY());
		int volY = volIndex / this.getSizeX();
		volIndex -= volY * this.getSizeX();
		int volX = volIndex;
		return new CoordinateIndex(volX, volY, volZ);
	}

	public int getDataLength(VariableType pdeVariableType) {
		int num = 0;
		if (pdeVariableType.equals(VariableType.VOLUME) || pdeVariableType.equals(VariableType.POSTPROCESSING)) {
			num = this.getSizeX() * this.getSizeY() * this.getSizeZ();
		} else if (pdeVariableType.equals(VariableType.MEMBRANE)) {
			num = (this.membraneElements == null ? 0 : this.membraneElements.length);
		} else if (pdeVariableType.equals(VariableType.CONTOUR)) {
			throw new RuntimeException("CartesianMesh.getDataLength(" + pdeVariableType + ") not yet implemented");
		} else if (pdeVariableType.equals(VariableType.VOLUME_REGION)) {
			num = this.getNumVolumeRegions();
		} else if (pdeVariableType.equals(VariableType.MEMBRANE_REGION)) {
			num = this.getNumMembraneRegions();
		} else if (pdeVariableType.equals(VariableType.CONTOUR_REGION)) {
			throw new RuntimeException("CartesianMesh.getDataLength(" + pdeVariableType + ") not yet implemented");
		}
		return num;
	}


	public Extent getExtent() {
		if (this.extent == null) {
			this.inflate();
		}
		return this.extent;
	}

	public Coordinate getFractionalCoordinateIndex(Coordinate worldCoord) {
		//
		//
		// calculate coordinates based on element coordinates
		//
		//     1--------2--------3-------4--------N
		//
		// so spacing is divided into (N-1) regions.
		//
		//
		// if N is 1, then take middle of that dimension
		//
		//
		double fractX;
		double fractY;
		double fractZ;

		if (this.getSizeX() > 1) {
			fractX = (worldCoord.x() - this.origin.getX()) / this.extent.getX() * (this.getSizeX() - 1);
		} else {
			fractX = 0;
		}

		if (this.getSizeY() > 1) {
			fractY = (worldCoord.y() - this.origin.getY()) / this.extent.getY() * (this.getSizeY() - 1);
		} else {
			fractY = 0;
		}

		if (this.getSizeZ() > 1) {
			fractZ = (worldCoord.z() - this.origin.getZ()) / this.extent.getZ() * (this.getSizeZ() - 1);
		} else {
			fractZ = 0;
		}

		return new Coordinate(fractX, fractY, fractZ);
	}


	public int getGeometryDimension() {
		// Get dimension of geometry using mesh variables to use appropriate resampling algorithm.
		int dimension = 0;

		if ((this.getSizeX() > 1) && (this.getSizeY() > 1) && (this.getSizeZ() > 1)) {
			dimension = 3;
		} else if (((this.getSizeX() > 1) && (this.getSizeY() > 1) && (this.getSizeZ() == 1)) ||
				((this.getSizeX() > 1) && (this.getSizeY() == 1) && (this.getSizeZ() > 1)) ||
				((this.getSizeX() == 1) && (this.getSizeY() > 1) && (this.getSizeZ() > 1))) {
			dimension = 2;
		} else if (((this.getSizeX() > 1) && (this.getSizeY() == 1) && (this.getSizeZ() == 1)) ||
				((this.getSizeX() == 1) && (this.getSizeY() > 1) && (this.getSizeZ() == 1)) ||
				((this.getSizeX() == 1) && (this.getSizeY() == 1) && (this.getSizeZ() > 1))) {
			dimension = 1;
		}

		return dimension;
	}


	public MembraneElement[] getMembraneElements() {
		if (this.membraneElements == null) {
			this.inflate();
		}
		return this.membraneElements;
	}


	public int[] getMembraneIndexMapping(CartesianMesh referenceMesh) {
		//
		// this method gets a mapping between the membrane element indexes of this mesh and the reference mesh.
		// the assumption is that the volume indices don't require any mappings, but the surfaces may have their normals flipped.
		//
		//
		//   e.g.  this allows comparing membrane values as follows:
		//
		//       int[] mapping = this.getMembraneIndexMapping(refMesh);
		//       double difference = refData[i] - data[mapping[i]];
		//
		if (this.getNumMembraneElements() != referenceMesh.getNumMembraneElements()) {
			throw new RuntimeException("this mesh and reference mesh have different number of membrane elements '" + this.getNumMembraneElements() + " and " + referenceMesh.getNumMembraneElements());
		}
		if (this.getNumMembraneElements() == 0) {
			return null;
		}
		MembraneElement[] memElements = this.getMembraneElements();
		MembraneElement[] refMemElements = referenceMesh.getMembraneElements();
		int[] mapping = new int[refMemElements.length];
		for (int i = 0; i < refMemElements.length; i++) {
			int refInsideIndex = refMemElements[i].getInsideVolumeIndex();
			int refOutsideIndex = refMemElements[i].getOutsideVolumeIndex();
			int correspondingMemIndex = -1;
			for (int j = 0; j < memElements.length; j++) {
				int insideIndex = memElements[j].getInsideVolumeIndex();
				int outsideIndex = memElements[j].getOutsideVolumeIndex();
				//
				// just in case inside/outside are flipped we will check both ways.
				//
				if ((insideIndex == refInsideIndex && outsideIndex == refOutsideIndex) || (insideIndex == refOutsideIndex && outsideIndex == refInsideIndex)) {
					correspondingMemIndex = j;
					break;
				}
			}
			if (correspondingMemIndex == -1) {
				throw new RuntimeException("couldn't find corresponding membrane element in reference mesh for index = " + i);
			}
			mapping[i] = correspondingMemIndex;
		}

		return mapping;
	}

	public int getMembraneRegionIndex(int membraneIndex) {
		return this.getMeshRegionInfo().getMembraneRegionForMembraneElement(membraneIndex);
	}


	private MeshRegionInfo getMeshRegionInfo() {
		//
		// mesh region info is a very low-level object, it should be encapsulated.
		//
		if (this.meshRegionInfo == null) {
			this.inflate();
		}
		return this.meshRegionInfo;
	}

	public BitSet getVolumeROIFromVolumeRegionID(int volumeRegionID) {
		return this.getMeshRegionInfo().getVolumeROIFromVolumeRegionID(volumeRegionID);

	}

	public BitSet getMembraneROIFromMembraneRegionID(int membraneRegionID) {
		return this.getMeshRegionInfo().getMembraneROIFromMembraneRegionID(membraneRegionID);
	}

	public HashMap<Integer, Integer> getVolumeRegionMapSubvolume() {
		Vector<MeshRegionInfo.VolumeRegionMapSubvolume> volRegionMapSubVolV = this.getMeshRegionInfo().getVolumeRegionMapSubvolume();
		HashMap<Integer, Integer> volregMapSubVHashMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < volRegionMapSubVolV.size(); i++) {
			volregMapSubVHashMap.put(volRegionMapSubVolV.elementAt(i).volumeRegionID, volRegionMapSubVolV.elementAt(i).subvolumeID);
		}
		return volregMapSubVHashMap;
	}

	public HashMap<Integer, int[]> getMembraneRegionMapSubvolumesInOut() {
		Vector<MeshRegionInfo.MembraneRegionMapVolumeRegion> membraneRegionMapVolumeRegionV = this.getMeshRegionInfo().getMembraneRegionMapVolumeRegion();
		HashMap<Integer, int[]> membrRegMapVolRegionsHashMap = new HashMap<Integer, int[]>();
		for (int i = 0; i < membraneRegionMapVolumeRegionV.size(); i++) {
			membrRegMapVolRegionsHashMap.put(
					membraneRegionMapVolumeRegionV.elementAt(i).membraneRegionID,
					new int[]{
							this.getMeshRegionInfo().getSubVolumeIDfromVolRegion(membraneRegionMapVolumeRegionV.elementAt(i).volumeRegionInsideID),
							this.getMeshRegionInfo().getSubVolumeIDfromVolRegion(membraneRegionMapVolumeRegionV.elementAt(i).volumeRegionOutsideID)}
			);
		}
		return membrRegMapVolRegionsHashMap;
	}

	public int getNumMembraneElements() {
		MembraneElement[] memElement = this.getMembraneElements();
		if (memElement != null) {
			return memElement.length;
		} else {
			return 0;
		}
	}

	public int getNumMembraneRegions() {
		MeshRegionInfo mri = this.getMeshRegionInfo();
		if (mri == null) {
			return 0;
		}
		return mri.getNumMembraneRegions();
	}

	public int getNumVolumeElements() {
		return this.size.getX() * this.size.getY() * this.size.getZ();
	}


	public int getNumVolumeRegions() {
		MeshRegionInfo mri = this.getMeshRegionInfo();
		if (mri == null) {
			return 0;
		}
		return mri.getNumVolumeRegions();
	}


	public Origin getOrigin() {
		if (this.origin == null) {
			this.inflate();
		}
		return this.origin;
	}

	public ISize getISize() {
		if (this.size == null) {
			this.inflate();
		}
		return this.size;
	}

	public double getRegionMembraneSurfaceAreaFromMembraneIndex(int membraneIndex) {
		return this.getMeshRegionInfo().getRegionMembraneSurfaceAreaFromMembraneIndex(membraneIndex);
	}


	public int getSizeX() {
		if (this.size == null) {
			this.inflate();
		}
		return this.size.getX();
	}


	public int getSizeY() {
		if (this.size == null) {
			this.inflate();
		}
		return this.size.getY();
	}


	public int getSizeZ() {
		if (this.size == null) {
			this.inflate();
		}

		return this.size.getZ();
	}


	public int getSubVolumeFromVolumeIndex(int volIndex) {
		int regionIndex = this.getVolumeRegionIndex(volIndex);
		return this.meshRegionInfo.getSubVolumeIDfromVolRegion(regionIndex);
	}

	public boolean hasSubvolumeInfo() {
		return this.getMeshRegionInfo() != null && this.getMeshRegionInfo().getVolumeRegionMapSubvolume() != null && this.getMeshRegionInfo().getVolumeRegionMapSubvolume().size() > 0;
	}

	private int getVolumeIndex(int coordX, int coordY, int coordZ) {
		return coordX + this.getSizeX() * (coordY + this.getSizeY() * coordZ);
	}

	public int getVolumeIndex(CoordinateIndex coordIndex) {
		return this.getVolumeIndex(coordIndex.x, coordIndex.y, coordIndex.z);
		//return coordIndex.x + getSizeX()*(coordIndex.y + getSizeY()*coordIndex.z);
	}

	public int getVolumeRegionIndex(int volumeIndex) {
		return this.getMeshRegionInfo().getVolumeElementMapVolumeRegion(volumeIndex);
	}

	public String getCompartmentSubdomainNamefromVolIndex(int volIndex) {
		return this.getMeshRegionInfo().getSubdomainNamefromVolIndex(volIndex);
	}

	public String getMembraneSubdomainNamefromMemIndex(int memIndex) throws MathException {
		int memRegIndex = this.getMembraneRegionIndex(memIndex);
		HashMap<Integer, int[]> memRegSubVolumeInOut = this.getMembraneRegionMapSubvolumesInOut();
		int[] subVolumeInOut = memRegSubVolumeInOut.get(memRegIndex);
		return this.subdomainInfo.getMembraneSubdomainName(subVolumeInOut[0], subVolumeInOut[1]);
	}

	public int getVolumeRegionIndex(CoordinateIndex coordIndex) {
		int volumeIndex = this.getVolumeIndex(coordIndex);
		return this.getMeshRegionInfo().getVolumeElementMapVolumeRegion(volumeIndex);
	}


	public int[] getVolumeSliceIndices(Coordinate.Axis normalAxis, int sliceNumber) {

		Coordinate normalAxisDimension = Coordinate.convertAxisFromStandardXYZToNormal(this.getSizeX(), this.getSizeY(), this.getSizeZ(), normalAxis);
		int[] indices = new int[(int) normalAxisDimension.x() * (int) normalAxisDimension.y()];
		int counter = 0;
		CoordinateIndex coordIndex = new CoordinateIndex();
		for (int y = 0; y < normalAxisDimension.y(); y += 1) {
			for (int x = 0; x < normalAxisDimension.x(); x += 1) {
				//
				// indexing through "normal" slice, converting each X,Y pair into equivalent
				// (X,Y,Z) in mesh coordinates.
				//
				coordIndex.x = x;
				coordIndex.y = y;
				coordIndex.z = sliceNumber;
				Coordinate.convertCoordinateIndexFromNormalToStandardXYZ(coordIndex, normalAxis);
				//
				// store mesh coordinates in "normal" slice sample array.
				//
				indices[counter] = this.getVolumeIndex(coordIndex.x, coordIndex.y, coordIndex.z);
				counter += 1;
			}
		}
		return indices;
	}

	public boolean hasRegionInfo() {
		return (this.meshRegionInfo != null);
	}


	protected void inflate() {
		if (this.compressedBytes == null) {
			return;
		}

		try {
			//Object objArray[] =  { version, size, origin, extent, meshRegionInfo, membraneElements, contourElements};
			Object[] objArray = (Object[]) CompressionUtils.fromCompressedSerialized(this.compressedBytes);
			this.version = (String) objArray[0];
			this.size = (ISize) objArray[1];
			this.origin = (Origin) objArray[2];
			this.extent = (Extent) objArray[3];
			this.meshRegionInfo = (MeshRegionInfo) objArray[4];
			this.membraneElements = (MembraneElement[]) objArray[5];
			this.contourElements = (ContourElement[]) objArray[6];
			this.subdomainInfo = (SubdomainInfo) objArray[7];

			this.compressedBytes = null;

		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}


	private boolean isFriendshipMutual(MembraneElement neighbor1, MembraneElement neighbor2) {
		//
		if (!this.isInList(neighbor1.getMembraneIndex(), neighbor2.getMembraneNeighborIndexes())) {
			return false;
		}
		return this.isInList(neighbor2.getMembraneIndex(), neighbor1.getMembraneNeighborIndexes());
	}


	private boolean isInList(int checkMe, int[] checkList) {
		boolean bInList = false;
		for (int i = 0; i < checkList.length; i += 1) {
			if (checkMe == checkList[i]) {
				bInList = true;
				break;
			}
		}
		return bInList;
	}


	public boolean isMembraneConnectivityOK() {
		for (int c = 0; c < this.membraneElements.length; c += 1) {
			//Get the next membraneElement in the list
			MembraneElement currentMembraneElement = this.membraneElements[c];
			int[] membraneNeighborIndexes =
					currentMembraneElement.getMembraneNeighborIndexes();
			//Search the neighbors the the currentMembraneElement
			for (int mnic = 0; mnic < membraneNeighborIndexes.length; mnic += 1) {
				//Get the connectivity neighbors the of the next neighbor in the list
				if (!this.isFriendshipMutual(currentMembraneElement, this.membraneElements[membraneNeighborIndexes[mnic]])) {
					return false;
				}
			}
		}
		return true;
	}

	private void read(CommentStringTokenizer tokens, MembraneMeshMetrics membraneMeshMetrics) throws MathException {
		//
		// clear previous contents
		//
		this.membraneElements = null;

		//
		// read new stuff
		//
		String token = null;
		token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.Version)) {
			//
			// read version number
			//
			token = tokens.nextToken();
			this.version = token;
			token = tokens.nextToken();
		}
		if (token.equalsIgnoreCase(VCML.CartesianMesh)) {
			token = tokens.nextToken();
		} else {
			throw new MathFormatException("unexpected token " + token + " expecting " + VCML.CartesianMesh);
		}
		//
		// only Version 1.1 and later supports membrane connectivity  (as of 8/30/2000)
		//
		boolean bConnectivity = this.version.equals(VERSION_1_1) || this.version.equals(VERSION_1_2);
		//
		// only Version 1.2 and later supports Regions
		//
		boolean bRegions = false;
		if (this.version.equals(VERSION_1_2)) {
			bRegions = true;
			this.meshRegionInfo = new MeshRegionInfo();
		}

		if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
			throw new MathFormatException("unexpected token " + token + " expecting " + VCML.BeginBlock);
		}
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				break;
			}
			if (token.equalsIgnoreCase(VCML.Size)) {
				int sx, sy, sz;
				try {
					token = tokens.nextToken();
					sx = Integer.valueOf(token).intValue();
					token = tokens.nextToken();
					sy = Integer.valueOf(token).intValue();
					token = tokens.nextToken();
					sz = Integer.valueOf(token).intValue();
				} catch (NumberFormatException e) {
					throw new MathFormatException("expected:  " + VCML.Size + " # # #");
				}
				this.setSize(sx, sy, sz);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.Extent)) {
				double ex, ey, ez;
				try {
					token = tokens.nextToken();
					ex = Double.valueOf(token).doubleValue();
					token = tokens.nextToken();
					ey = Double.valueOf(token).doubleValue();
					token = tokens.nextToken();
					ez = Double.valueOf(token).doubleValue();
				} catch (NumberFormatException e) {
					throw new MathFormatException("expected:  " + VCML.Extent + " # # #");
				}
				this.setExtent(new Extent(ex, ey, ez));
				continue;
			}
			if (token.equalsIgnoreCase(VCML.Origin)) {
				double ox, oy, oz;
				try {
					token = tokens.nextToken();
					ox = Double.valueOf(token).doubleValue();
					token = tokens.nextToken();
					oy = Double.valueOf(token).doubleValue();
					token = tokens.nextToken();
					oz = Double.valueOf(token).doubleValue();
				} catch (NumberFormatException e) {
					throw new MathFormatException("expected:  " + VCML.Origin + " # # #");
				}
				this.setOrigin(new Origin(ox, oy, oz));
				continue;
			}
			//
			//
			//
			if (token.equalsIgnoreCase(VCML.VolumeRegionsMapSubvolume)) {
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
					throw new MathFormatException("unexpected token " + token + " expecting " + VCML.BeginBlock);
				}
				token = tokens.nextToken();
				int numVolumeRegions = 0;
				try {
					numVolumeRegions = Integer.valueOf(token).intValue();
				} catch (NumberFormatException e) {
					throw new MathFormatException("unexpected token " + token + " expecting the VolumeRegionsMapSubvolume list length");
				}
				int checkCount = 0;
				while (tokens.hasMoreTokens()) {
					token = tokens.nextToken();
					if (token.equalsIgnoreCase(VCML.EndBlock)) {
						break;
					}
					try {
						int volRegionID = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						int subvolumeID = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						double volume = Double.valueOf(token).doubleValue();

						String subdomainName = null;
						if (this.subdomainInfo != null) {
							subdomainName = this.subdomainInfo.getCompartmentSubdomainName(subvolumeID);
						}
						this.meshRegionInfo.mapVolumeRegionToSubvolume(volRegionID, subvolumeID, volume, subdomainName);
					} catch (NumberFormatException e) {
						throw new MathFormatException("expected:  # # #");
					}
					checkCount += 1;
				}
				if (checkCount != numVolumeRegions) {
					throw new MathFormatException("CartesianMesh.read->VolumeRegionsMapSubvolume: read " + checkCount + " VolRegions but was expecting " + numVolumeRegions);
				}
				continue;
			}
			if (token.equalsIgnoreCase(VCML.MembraneRegionsMapVolumeRegion)) {
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
					throw new MathFormatException("unexpected token " + token + " expecting " + VCML.BeginBlock);
				}
				token = tokens.nextToken();
				int numMembraneRegions = 0;
				try {
					numMembraneRegions = Integer.valueOf(token).intValue();
				} catch (NumberFormatException e) {
					throw new MathFormatException("unexpected token " + token + " expecting the MembraneRegionsMapVolumeRegion list length");
				}
				int checkCount = 0;
				while (tokens.hasMoreTokens()) {
					token = tokens.nextToken();
					if (token.equalsIgnoreCase(VCML.EndBlock)) {
						break;
					}
					try {
						int memRegionID = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						int volRegionIn = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						int volRegionOut = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						double surface = Double.valueOf(token).doubleValue();
						this.meshRegionInfo.mapMembraneRegionToVolumeRegion(memRegionID, volRegionIn, volRegionOut, surface);
					} catch (NumberFormatException e) {
						throw new MathFormatException("expected:  # # #");
					}
					checkCount += 1;
				}
				if (checkCount != numMembraneRegions) {
					throw new MathFormatException("CartesianMesh.read->MembraneRegionsMapVolumeRegion: read " + checkCount + " MembraneRegions but was expecting " + numMembraneRegions);
				}
				continue;
			}
			if (token.equalsIgnoreCase(VCML.VolumeElementsMapVolumeRegion)) {
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
					throw new MathFormatException("unexpected token " + token + " expecting " + VCML.BeginBlock);
				}
				token = tokens.nextToken();
				int numVolumeElements = 0;
				try {
					numVolumeElements = Integer.valueOf(token).intValue();
				} catch (NumberFormatException e) {
					throw new MathFormatException("unexpected token " + token + " expecting the VolumeElementsMapVolumeRegion list length");
				}
				token = tokens.nextToken();
				boolean bCompressed = token.equalsIgnoreCase("Compressed");
				if (!bCompressed) {
					if (!token.equalsIgnoreCase("UnCompressed")) {
						throw new MathFormatException("unexpected token " + token + " expecting Compress or UnCompress");
					}
				}
				byte[] volumeElementMap = new byte[numVolumeElements];
				int checkCount = 0;
				if (bCompressed) {
					//Get HEX encoded bytes of the compressed VolumeElements-RegionID Map
					StringBuffer hexOfCompressed = new StringBuffer();
					while (tokens.hasMoreTokens()) {
						token = tokens.nextToken();
						if (token.equalsIgnoreCase(VCML.EndBlock)) {
							break;
						}
						hexOfCompressed.append(token);
					}
					//Un-HEX the compressed data
					byte[] compressedData = Hex.toBytes(hexOfCompressed.toString());
					try {
						this.meshRegionInfo.setCompressedVolumeElementMapVolumeRegion(compressedData, numVolumeElements);
					} catch (IOException e) {
						throw new MathFormatException("CartesianMesh.read->VolumeElementsMapVolumeRegion " + e);
					}
					checkCount = this.meshRegionInfo.getUncompressedVolumeElementMapVolumeRegionLength();
				} else {
					while (tokens.hasMoreTokens()) {
						token = tokens.nextToken();
						if (token.equalsIgnoreCase(VCML.EndBlock)) {
							break;
						}
						try {
							int volumeRegionID = Integer.valueOf(token).intValue();
							volumeElementMap[checkCount] = (byte) volumeRegionID;
						} catch (NumberFormatException e) {
							throw new MathFormatException("expected:  # # #");
						}
						checkCount += 1;
					}
				}
				if (checkCount != numVolumeElements && checkCount != 2 * numVolumeElements) {
					throw new MathFormatException("CartesianMesh.read->VolumeElementsMapVolumeRegion: read " + checkCount + " VolumeElements but was expecting " + numVolumeElements);
				}
				continue;
			}
			//
			//
			//
			HashMap<Integer, Integer> volumeRegionMapSubvolume = this.getVolumeRegionMapSubvolume();
			if (token.equalsIgnoreCase(VCML.MembraneElements)) {
				//
				// read '{'
				//
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
					throw new MathFormatException("unexpected token " + token + " expecting " + VCML.BeginBlock);
				}
				token = tokens.nextToken();
				int numMemElements = 0;
				try {
					numMemElements = Integer.valueOf(token).intValue();
				} catch (NumberFormatException e) {
					throw new MathFormatException("unexpected token " + token + " expecting the membraneElement list length");
				}
				//
				// read list of the following format:
				//
				//		memIndex insideVolIndex outsideVolIndex
				//
				this.membraneElements = new MembraneElement[numMemElements];
				int index = 0;
				int[] membraneElementMapMembraneRegion = null;
				if (bRegions) {
					membraneElementMapMembraneRegion = new int[numMemElements];
					this.meshRegionInfo.mapMembraneElementsToMembraneRegions(membraneElementMapMembraneRegion);
				}
				//
				// loop until read a "}"
				//
				while (tokens.hasMoreTokens()) {
					token = tokens.nextToken();
					if (token.equalsIgnoreCase(VCML.EndBlock)) {
						break;
					}
					int memIndex = -1;
					int insideIndex = -1;
					int outsideIndex = -1;
					try {
						//
						// read first three tokens of a membrane element
						//
						//     membraneIndex   insideIndex    outsideIndex
						//
						memIndex = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						insideIndex = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						outsideIndex = Integer.valueOf(token).intValue();

						if (this.subdomainInfo != null) {
							int insideRegionIndex = this.getVolumeRegionIndex(insideIndex);
							int outsideRegionIndex = this.getVolumeRegionIndex(outsideIndex);
							int insideSubVolumeHandle = volumeRegionMapSubvolume.get(insideRegionIndex);
							int outsideSubVolumeHandle = volumeRegionMapSubvolume.get(outsideRegionIndex);
							int realInsideSubVolumeHandle = this.subdomainInfo.getInside(insideSubVolumeHandle, outsideSubVolumeHandle);

							if (realInsideSubVolumeHandle != insideSubVolumeHandle) {
								int temp = insideIndex;
								insideIndex = outsideIndex;
								outsideIndex = temp;
							}
						}
					} catch (NumberFormatException e) {
						throw new MathFormatException("expected:  # # #");
					}

					MembraneElement me = null;
					//
					// grab connectivity if enabled (additional four tokens)
					//
					//     memNeighbor1  memNeighbor2  memNeighbor3  memNeighbor4
					//
					//	where memNeighborX = -1 for missing connections.
					//
					if (bConnectivity) {
						try {
							token = tokens.nextToken();
							int neighbor1 = Integer.valueOf(token).intValue();
							token = tokens.nextToken();
							int neighbor2 = Integer.valueOf(token).intValue();
							token = tokens.nextToken();
							int neighbor3 = Integer.valueOf(token).intValue();
							token = tokens.nextToken();
							int neighbor4 = Integer.valueOf(token).intValue();
							//
							if (bRegions) {
								token = tokens.nextToken();
								int regionID = Integer.valueOf(token).intValue();
								membraneElementMapMembraneRegion[memIndex] = regionID;
							}
							if (membraneMeshMetrics == null) {
								me = new MembraneElement(memIndex, insideIndex, outsideIndex,
										neighbor1, neighbor2, neighbor3, neighbor4, MembraneElement.AREA_UNDEFINED, 0, 0, 0, 0, 0, 0);
							} else {
								me = new MembraneElement(memIndex, insideIndex, outsideIndex,
										neighbor1, neighbor2, neighbor3, neighbor4,
										membraneMeshMetrics.areas[memIndex],
										membraneMeshMetrics.normals[memIndex][0],
										membraneMeshMetrics.normals[memIndex][1],
										membraneMeshMetrics.normals[memIndex][2],
										membraneMeshMetrics.centroids[memIndex][0],
										membraneMeshMetrics.centroids[memIndex][1],
										membraneMeshMetrics.centroids[memIndex][2]);
							}
						} catch (NumberFormatException e) {
							throw new MathFormatException("expected:  # # # # # # #");
						}
					} else {
						me = new MembraneElement(memIndex, insideIndex, outsideIndex);
					}

					this.membraneElements[index] = me;
					index++;
				}
				continue;
			}
			if (token.equalsIgnoreCase(VCML.ContourElements)) {
				//
				// read '{'
				//
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
					throw new MathFormatException("unexpected token " + token + " expecting " + VCML.BeginBlock);
				}
				token = tokens.nextToken();
				int numContourElements = 0;
				try {
					numContourElements = Integer.valueOf(token).intValue();
				} catch (NumberFormatException e) {
					throw new MathFormatException("unexpected token " + token + " expecting the contourElement list length");
				}
				//
				// read list of the following format:
				//
				//		contourIndex volumeIndex beginCoord endCoord prevIndex nextIndex
				//
				this.contourElements = new ContourElement[numContourElements];
				int index = 0;
				//
				// loop until read a "}"
				//
				while (tokens.hasMoreTokens()) {
					token = tokens.nextToken();
					if (token.equalsIgnoreCase(VCML.EndBlock)) {
						break;
					}
					ContourElement ce = null;
					try {
						//
						// read first two tokens of a contour element
						//
						//     contourIndex volumeIndex
						//
						int contourIndex = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						int volumeIndex = Integer.valueOf(token).intValue();
						token = tokens.nextToken();

						//
						// read beginCoord endCoord
						//
						double beginX = Double.valueOf(token).doubleValue();
						token = tokens.nextToken();
						double beginY = Double.valueOf(token).doubleValue();
						token = tokens.nextToken();
						double beginZ = Double.valueOf(token).doubleValue();
						token = tokens.nextToken();

						double endX = Double.valueOf(token).doubleValue();
						token = tokens.nextToken();
						double endY = Double.valueOf(token).doubleValue();
						token = tokens.nextToken();
						double endZ = Double.valueOf(token).doubleValue();
						token = tokens.nextToken();

						Coordinate begin = new Coordinate(beginX, beginY, beginZ);
						Coordinate end = new Coordinate(endX, endY, endZ);

						//
						// read last two tokens of a contour element
						//
						//     prevContourIndex nextContourIndex
						//
						int prevContourIndex = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						int nextContourIndex = Integer.valueOf(token).intValue();

						ce = new ContourElement(contourIndex, volumeIndex, begin, end, prevContourIndex, nextContourIndex);

					} catch (NumberFormatException e) {
						throw new MathFormatException("expected:  %d %d   %f %f %f    %f %f %f   %d %d");
					}

					this.contourElements[index] = ce;

					index++;
				}
				continue;
			}
			throw new MathFormatException("unexpected identifier " + token);
		}
		switch (this.getGeometryDimension()) {
			case 1: {
				if (this.extent.getY() != 1 || this.extent.getZ() != 1) {
					System.out.println("Extent " + this.extent.toString() + " for a 1-D mesh truncated to 1 for y and z");
					this.setExtent(new Extent(this.extent.getX(), 1.0, 1.0));
				}
				break;
			}
			case 2: {
				if (this.extent.getZ() != 1) {
					System.out.println("Extent " + this.extent.toString() + " for a 2-D mesh truncated to 1 for z");
					this.setExtent(new Extent(this.extent.getX(), this.extent.getY(), 1.0));
				}
				break;
			}
		}
	}

	public MembraneMeshMetrics readMembraneMeshMetrics(CommentStringTokenizer tokens) throws MathException {

		MembraneMeshMetrics membraneMeshMetrics = new MembraneMeshMetrics();

		String token = null;
		token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.MembraneElements)) {
			token = tokens.nextToken();
		} else {
			throw new MathFormatException("unexpected token " + token + " expecting " + VCML.CartesianMesh);
		}
		if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
			throw new MathFormatException("unexpected token " + token + " expecting " + VCML.BeginBlock);
		}

		token = tokens.nextToken();
		int numMembraneElements = 0;
		try {
			numMembraneElements = Integer.valueOf(token).intValue();
		} catch (NumberFormatException e) {
			throw new MathFormatException("unexpected token " + token + " expecting MembraneElement count");
		}

		short[] regionIndex = new short[numMembraneElements];
		float[] areas = new float[numMembraneElements];
		float[][] normals = new float[numMembraneElements][3];
		float[][] centroids = new float[numMembraneElements][3];

		if (
				!(token = tokens.nextToken()).equals("Index") ||
						!(token = tokens.nextToken()).equals("RegionIndex") ||
						!(token = tokens.nextToken()).equals("X") ||
						!(token = tokens.nextToken()).equals("Y") ||
						!(token = tokens.nextToken()).equals("Z") ||
						!(token = tokens.nextToken()).equals("Area") ||
						!(token = tokens.nextToken()).equals("Nx") ||
						!(token = tokens.nextToken()).equals("Ny") ||
						!(token = tokens.nextToken()).equals("Nz")
		) {
			throw new MathFormatException("unexpected MeshMetrics column description = " + token);
		}
		int counter = 0;
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				break;
			}
			if (counter >= numMembraneElements) {
				throw new MathFormatException("Error parsing MembraneMeshMetrics values index=" + counter + ".  Expecting only " + numMembraneElements + " MembraneElements");
			}
			try {
				int index = Integer.valueOf(token).intValue();
				if (index != counter) {
					throw new MathFormatException("unexpected token " + token + " expecting " + counter);
				}
				regionIndex[counter] = Short.parseShort(tokens.nextToken());
				//centroids
				centroids[counter][0] = Float.parseFloat(tokens.nextToken());
				centroids[counter][1] = Float.parseFloat(tokens.nextToken());
				centroids[counter][2] = Float.parseFloat(tokens.nextToken());
				//area
				areas[counter] = Float.parseFloat(tokens.nextToken());
				//normals
				normals[counter][0] = Float.parseFloat(tokens.nextToken());
				normals[counter][1] = Float.parseFloat(tokens.nextToken());
				normals[counter][2] = Float.parseFloat(tokens.nextToken());

			} catch (NumberFormatException e) {
				throw new MathFormatException("Error parsing MembraneMeshMetrics values index=" + counter + "  " + e.getMessage());
			}
			counter += 1;
		}

		membraneMeshMetrics.regionIndexes = regionIndex;
		membraneMeshMetrics.areas = areas;
		membraneMeshMetrics.normals = normals;
		membraneMeshMetrics.centroids = centroids;

		return membraneMeshMetrics;
	}


	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		int compressedSize = s.readInt();
		this.compressedBytes = new byte[compressedSize];
		s.readFully(this.compressedBytes, 0, compressedSize);
	}


	protected void setExtent(Extent argExtent) {
		this.extent = argExtent;
	}


	protected void setOrigin(Origin origin) {
		this.origin = origin;
	}


	protected void setSize(int x, int y, int z) {
		this.size = new ISize(x, y, z);
	}

	protected Object[] getOutputFields() throws IOException {
		return new Object[]{this.version, this.size, this.origin, this.extent, this.meshRegionInfo, this.membraneElements, this.contourElements, this.subdomainInfo};
	}

	private void writeObject(ObjectOutputStream s) throws IOException {
		if (this.compressedBytes == null) {
			this.compressedBytes = CompressionUtils.toCompressedSerialized(this.getOutputFields());
		}
		s.writeInt(this.compressedBytes.length);
		s.write(this.compressedBytes);
	}

	public void write(PrintStream out) {
		//
		// 'Version 1.1' added membrane connectivity
		// 'Version 1.2' added regions
		//
		out.println("Version 1.2");
		out.println("CartesianMesh {");//Begin CartesianMesh
		//
		this.writeCartesianMeshHeader(out);
		out.println();
		this.writeVolumeRegionsMapSubvolume(out);
		out.println();
		this.writeMembraneRegionMapVolumeRegion(out);
		out.println();
		this.writeVolumeElementsMapVolumeRegion(out);
		out.println();
		this.writeMembraneElements_Connectivity_Region(out);
//	writeContourElements(out);
		//
		out.println("}");//End CartesianMesh
	}

	private void writeVolumeRegionsMapSubvolume(PrintStream out) {
		int numVolumeRegions = this.getMeshRegionInfo().getNumVolumeRegions();
		out.println("\tVolumeRegionsMapSubvolume {");
		out.println("\t" + numVolumeRegions);
		out.println("\t// VolRegID   SubvolID     Volume");
		for (int c = 0; c < numVolumeRegions; c += 1) {
			VolumeRegionMapSubvolume volRegionMapSubvolume = this.getMeshRegionInfo().getVolumeRegionMapSubvolume().get(c);
			out.println("\t" + volRegionMapSubvolume.volumeRegionID + " " +
					volRegionMapSubvolume.subvolumeID + " " +
					volRegionMapSubvolume.volumeRegionVolume);
		}
		out.println("\t}");
	}

	private void writeVolumeElementsMapVolumeRegion(PrintStream out) {
		int numVolumeElements = this.getNumVolumeElements();
		byte[] compressedBytes = this.meshRegionInfo.getCompressedVolumeElementMapVolumeRegion();
		out.println("\tVolumeElementsMapVolumeRegion {");
		out.print("\t" + numVolumeElements + " Compressed");
		byte[] tempBuffer = new byte[1];
		for (int c = 0; c < compressedBytes.length; c += 1) {
			if (c % 40 == 0) {
				out.print("\n\t");
			}
			tempBuffer[0] = compressedBytes[c];
			out.print(Hex.toString(tempBuffer));
		}
		out.println("\n\t}");
	}

	private void writeMembraneRegionMapVolumeRegion(PrintStream out) {
		int numMembraneRegions = this.getNumMembraneRegions();
		out.println("\tMembraneRegionsMapVolumeRegion {");
		out.println("\t" + numMembraneRegions);
		out.println("\t//MemRegID  VolReg0  VolReg1  Surface");
//	if (numMembraneRegions>0){
//		throw new RuntimeException("membrane regions not supported for write()");
//	}
		for (int c = 0; c < numMembraneRegions; c += 1) {
			MembraneRegionMapVolumeRegion membraneRegion = this.getMeshRegionInfo().getMembraneRegionMapVolumeRegion().get(c);
			int vrInside = membraneRegion.volumeRegionInsideID;
			int vrOutside = membraneRegion.volumeRegionOutsideID;
			out.println("\t" + c + " " + vrInside + " " + vrOutside + " " + membraneRegion.membraneRegionSurface);
		}
		out.println("\t}");
	}

	private void writeCartesianMeshHeader(PrintStream out) {
		out.println("\t//     X       Y       Z");
		out.println("\tSize   " + this.getSizeX() + "   " + this.getSizeY() + "    " + this.getSizeZ());
		out.println("\tExtent " + this.getExtent().getX() + "   " + this.getExtent().getY() + "   " + this.getExtent().getZ());
		out.println("\tOrigin " + this.getOrigin().getX() + "   " + this.getOrigin().getY() + "   " + this.getOrigin().getZ());
	}

//boolean CartesianMesh::writeMeshMetrics(OutputStream out)
//{
//	out.println("MembraneElements {\n");
//	out.println("%d\n",(int)getNumMembraneElements());
//	out.println("%5s %11s %17s %25s %25s %25s %25s %25s %25s\n","Index","RegionIndex","X","Y","Z","Area","Nx","Ny","Nz");
//	for (int i=0;i<getNumMembraneElements();i++){
//		MembraneElement *memEl = pMembraneElement + i;
//		WorldCoord wc = getMembraneWorldCoord(i);
//		out.println("%-5ld %11ld %25.17lg %25.17lg %25.17lg %25.17lg %25.17lg %25.17lg %25.17lg\n",
//			memEl->index,
//			memEl->region->getId(),
//			wc.x,
//			wc.y,
//			wc.z,
//			memEl->area,
//			memEl->unitNormal.x,
//			memEl->unitNormal.y,
//			memEl->unitNormal.z);
//	}
//	out.println("}");
//	return TRUE;
//}

	private void writeMembraneElements_Connectivity_Region(PrintStream out) {
		out.println("\tMembraneElements {");
		out.println("\t" + this.getNumMembraneElements());
		out.println("\t//Indx  VIn  VOut  Conn0  Conn1  Conn2  Conn3  MemRegID");
		MembraneElement[] memEl = this.getMembraneElements();
		for (int i = 0; this.membraneElements != null && i < this.membraneElements.length; i++) {
			out.println("\t" + i + " " + memEl[i].getInsideVolumeIndex() + " " + memEl[i].getOutsideVolumeIndex() + " ");
			for (int j = 0; j < MembraneElement.MAX_POSSIBLE_NEIGHBORS; j++) {
				out.println((j < memEl[i].getMembraneNeighborIndexes().length ? memEl[i].getMembraneNeighborIndexes()[j] : MembraneElement.NEIGHBOR_UNDEFINED) + " ");
			}
			out.println(this.meshRegionInfo.getMembraneRegionForMembraneElement(i));
		}
		out.println("\t}");
	}

//private void writeContourElements(OutputStream out)
//{
//	int i;
//	//
//	// write out contour elements (if present)
//	//
//	if (getNumContourElements()>0){
//		out.println("    ContourElements {\n");
//		out.println("           %d\n",(int)getNumContourElements());
//		//
//		// index volumeIndex begin.x begin.y begin.z  end.x, end.y end.z neighbor(prev) neighbor(next)
//		//
//		for (i=0;i<getNumContourElements();i++){
//			ContourElement *cEl = getContourElement(i);
//			int neighborPrev = -1;
//			int neighborNext = -1;
//			if (cEl->getBorder() == CONTOUR_BEGIN){
//				neighborPrev = -1;
//				neighborNext = i+1;
//			}else if (cEl->getBorder() == CONTOUR_END){
//				neighborPrev = i-1;
//				neighborNext = -1;
//			}else if (cEl->getBorder() == CONTOUR_INTERIOR){
//				neighborPrev = i-1;
//				neighborNext = i+1;
//			}else{
//				throw new Exception("Error writing contour mesh, contour element(%ld) has an illegal ContourBorder type = %d\n",i,cEl->getBorder());
//			}
//			out.println("           %ld %ld %lg %lg %lg %lg %lg %lg %ld %ld\n",cEl->getElementIndex(),cEl->getVolumeIndex(),
//						cEl->getBegin().x,cEl->getBegin().y,cEl->getBegin().z,
//						cEl->getEnd().x,  cEl->getEnd().y,  cEl->getEnd().z,
//						neighborPrev, neighborNext);
//		}
//		out.println("    }\n");  // end ContourElements
//	}
//}

	public static CartesianMesh readFromFiles(File meshFile, File meshmetricsFile, File subdomainFile) throws IOException, MathException {
		//
		// read meshFile and parse into 'mesh' object
		//
		BufferedReader meshReader = null;
		BufferedReader meshMetricsReader = null;
		try {
			meshReader = new BufferedReader(new FileReader(meshFile));
			CommentStringTokenizer meshST = new CommentStringTokenizer(meshReader);

			CommentStringTokenizer membraneMeshMetricsST = null;
			if (meshmetricsFile != null) {
				meshMetricsReader = new BufferedReader(new FileReader(meshmetricsFile));
				membraneMeshMetricsST = new CommentStringTokenizer(meshMetricsReader);
			}

			CartesianMesh mesh = new CartesianMesh();
			MembraneMeshMetrics membraneMeshMetrics = null;
			if (membraneMeshMetricsST != null) {
				membraneMeshMetrics = mesh.readMembraneMeshMetrics(membraneMeshMetricsST);
			}
			if (subdomainFile != null) {
				mesh.subdomainInfo = SubdomainInfo.read(subdomainFile);
			}
			mesh.read(meshST, membraneMeshMetrics);
			return mesh;
		} finally {
			if (meshReader != null) {
				try {
					meshReader.close();
				} catch (Exception e) {
					lg.error(e.getMessage(), e);
				}
			}
			if (meshMetricsReader != null) {
				try {
					meshMetricsReader.close();
				} catch (Exception e) {
					lg.error(e.getMessage(), e);
				}
			}
		}
	}

	public static void test() {
		try {
			CartesianMesh mesh = null;

			File meshFile = new File("users\\fgao\\SimID_28389873_0_.mesh");
			File membraneMeshMetricsFile = new File("users\\fgao\\SimID_28389873_0_" + SimDataConstants.MESHMETRICSFILE_EXTENSION);
			// read meshFile,MembraneMeshMetrics and parse into 'mesh' object
			//
			mesh = CartesianMesh.readFromFiles(meshFile, membraneMeshMetricsFile, null);
			System.out.println("-------------------- generated file ----------------");
			mesh.write(System.out);
		} catch (Exception e) {
			lg.error(e.getMessage(), e);
		}
	}

	public boolean isChomboMesh() {
		return false;
	}
}
