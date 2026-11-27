package org.vcell.vis.vtk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.vis.vismesh.thrift.ChomboIndexData;
import org.vcell.vis.vismesh.thrift.ChomboSurfaceIndex;
import org.vcell.vis.vismesh.thrift.ChomboVolumeIndex;
import org.vcell.vis.vismesh.thrift.MovingBoundaryIndexData;
import org.vcell.vis.vismesh.thrift.MovingBoundarySurfaceIndex;
import org.vcell.vis.vismesh.thrift.MovingBoundaryVolumeIndex;
import org.vcell.vis.vismesh.thrift.VisIrregularPolyhedron;
import org.vcell.vis.vismesh.thrift.VisLine;
import org.vcell.vis.vismesh.thrift.VisMesh;
import org.vcell.vis.vismesh.thrift.VisPolygon;
import org.vcell.vis.vismesh.thrift.VisSurfaceTriangle;
import org.vcell.vis.vismesh.thrift.VisTetrahedron;
import org.vcell.vis.vismesh.thrift.VisVoxel;

public abstract class VtkService {
	public static VtkService vtkService = null;
	protected static final Logger lg = LogManager.getLogger(VtkService.class);

	public static VtkService getInstance(){
		//return new VtkGridUtils();
		return new VtkServicePython();
	}

	/**
	 * Writes a Chombo membrane's grid and its {@link ChomboIndexData} in pure Java, for every service (the desktop
	 * client has no Python VTK service, and the data server is not given one): the grid is the mesh's surface
	 * points and its lines (2D) or surface triangles (3D), written by {@link VtuWriter#writeSurfaceGrid}.
	 * <p>
	 * Equivalent to the Python service's {@code writeChomboMembraneVtkGridAndIndexData}: the same points and cells
	 * in the same order, and one surface index per cell, in cell order — the membrane element whose value that
	 * cell shows.
	 */
	public void writeChomboMembraneVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException {
		if (!domainName.toUpperCase(Locale.ROOT).endsWith("MEMBRANE")) {
			throw new IllegalArgumentException("expecting a membrane domain name (ending with membrane), found " + domainName);
		}
		VtuWriter.writeSurfaceGrid(visMesh, vtkFile);
		ChomboIndexData indexData = new ChomboIndexData();
		indexData.setDomainName(domainName);
		indexData.setChomboSurfaceIndices(new ArrayList<ChomboSurfaceIndex>());
		if (visMesh.getDimension() == 3) {
			if (visMesh.getSurfaceTriangles() != null) {
				for (VisSurfaceTriangle triangle : visMesh.getSurfaceTriangles()) {
					indexData.addToChomboSurfaceIndices(triangle.getChomboSurfaceIndex());
				}
			}
		} else if (visMesh.getDimension() == 2) {
			if (visMesh.getVisLines() != null) {
				for (VisLine visLine : visMesh.getVisLines()) {
					indexData.addToChomboSurfaceIndices(visLine.getChomboSurfaceIndex());
				}
			}
		}
		if (indexData.getChomboSurfaceIndicesSize() == 0) {
			lg.warn("Chombo membrane " + domainName + " has no cells: writing an empty index for " + indexFile);
		}
		VisMeshUtils.writeChomboIndexData(indexFile, indexData);
	}

	/**
	 * Writes a Chombo volume domain's grid and its {@link ChomboIndexData} in pure Java, for every service (see
	 * {@link #writeChomboMembraneVtkGridAndIndexData}): the grid is the mesh's points and cells — polygons in 2D;
	 * whole voxels and the irregular polyhedra Chombo cuts at the embedded boundary in 3D — written by
	 * {@link VtuWriter#writeVolumeGrid}.
	 * <p>
	 * Equivalent to the Python service's {@code writeChomboVolumeVtkGridAndIndexData}: the same points and cells in
	 * the same order, and one volume index per cell in that order (2D: the polygons; 3D: the voxels, then the
	 * tetrahedra, then the polyhedra), naming the Chombo cell whose value it shows.
	 */
	public void writeChomboVolumeVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException {
		VtuWriter.writeVolumeGrid(visMesh, vtkFile);
		ChomboIndexData indexData = new ChomboIndexData();
		indexData.setDomainName(domainName);
		indexData.setChomboVolumeIndices(new ArrayList<ChomboVolumeIndex>());
		if (visMesh.getDimension() == 2) {
			if (visMesh.getPolygons() != null) {
				for (VisPolygon polygon : visMesh.getPolygons()) {
					indexData.addToChomboVolumeIndices(polygon.getChomboVolumeIndex());
				}
			}
		} else if (visMesh.getDimension() == 3) {
			// the order the grid's cells are written in: voxels, then tetrahedra, then polyhedra
			if (visMesh.getVisVoxels() != null) {
				for (VisVoxel voxel : visMesh.getVisVoxels()) {
					indexData.addToChomboVolumeIndices(voxel.getChomboVolumeIndex());
				}
			}
			if (visMesh.getTetrahedra() != null) {
				for (VisTetrahedron tet : visMesh.getTetrahedra()) {
					indexData.addToChomboVolumeIndices(tet.getChomboVolumeIndex());
				}
			}
			if (visMesh.getIrregularPolyhedra() != null) {
				for (VisIrregularPolyhedron polyhedron : visMesh.getIrregularPolyhedra()) {
					indexData.addToChomboVolumeIndices(polyhedron.getChomboVolumeIndex());
				}
			}
		}
		if (indexData.getChomboVolumeIndicesSize() == 0) {
			lg.warn("Chombo domain " + domainName + " has no cells: writing an empty index for " + indexFile);
		}
		VisMeshUtils.writeChomboIndexData(indexFile, indexData);
	}

	public abstract void writeFiniteVolumeSmoothedVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException, InterruptedException;
	
	/**
	 * Writes a MovingBoundary domain's grid and its {@link MovingBoundaryIndexData} in pure Java, for every
	 * service: the grid is just the mesh's points and polygons ({@link VtuWriter}), with nothing for VTK itself to
	 * compute, so there is no reason to start Python for it — and the desktop client, which runs MovingBoundary
	 * locally and opens its results in the field viewer, has no Python VTK service to start.
	 * <p>
	 * Equivalent to the Python service's {@code writeMovingBoundaryVolumeVtkGridAndIndexData}: the same points and
	 * cells in the same order, and one volume index per polygon, in polygon order ({@code timeIndex} is 0 there
	 * too: the file name, not the record, carries the time).
	 */
	public void writeMovingBoundaryVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException {
		if (visMesh.getDimension() != 2) {
			throw new UnsupportedOperationException("MovingBoundary meshes are 2D, found dimension " + visMesh.getDimension());
		}
		VtuWriter.writeVolumeGrid(visMesh, vtkFile);
		MovingBoundaryIndexData indexData = new MovingBoundaryIndexData();
		indexData.setDomainName(domainName);
		indexData.setTimeIndex(0);
		indexData.setMovingBoundaryVolumeIndices(new ArrayList<MovingBoundaryVolumeIndex>());
		if (visMesh.getPolygons() != null) {
			for (VisPolygon polygon : visMesh.getPolygons()) {
				indexData.addToMovingBoundaryVolumeIndices(polygon.getMovingBoundaryVolumeIndex());
			}
		}
		if (visMesh.getVisLines() != null) {
			indexData.setMovingBoundarySurfaceIndices(new ArrayList<MovingBoundarySurfaceIndex>());
			for (VisLine visLine : visMesh.getVisLines()) {
				indexData.addToMovingBoundarySurfaceIndices(visLine.getMovingBoundarySurfaceIndex());
			}
		}
		if (indexData.getMovingBoundaryVolumeIndicesSize() == 0 && indexData.getMovingBoundarySurfaceIndicesSize() == 0) {
			lg.warn("MovingBoundary domain " + domainName + " has no cells: writing an empty index for " + indexFile);
		}
		VisMeshUtils.writeMovingBoundaryIndexData(indexFile, indexData);
	}
	
	public abstract void writeComsolVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException, InterruptedException;
}
