package org.vcell.vis.vismesh;

import java.util.ArrayList;
import java.util.List;

import org.vcell.vis.core.Vect3D;

public class VisMesh {
	private final int dimension;
	private final Vect3D origin;
	private final Vect3D extent;
	private final ArrayList<VisPoint> points = new ArrayList<VisPoint>();
	private final ArrayList<VisPolygon> polygons = new ArrayList<VisPolygon>();
	private final ArrayList<VisPolyhedron> polyhedra = new ArrayList<VisPolyhedron>();
	private final ArrayList<VisSurfaceTriangle> surfaceTriangles = new ArrayList<VisSurfaceTriangle>();
	private final ArrayList<VisLine> lines = new ArrayList<VisLine>();
	private final List<VisPoint> surfacePoints = new ArrayList<VisPoint>();
	
	public VisMesh(int dimension, Vect3D origin, Vect3D extent) {
		this.dimension = dimension;
		this.origin = origin;
		this.extent = extent;
	}

	public int getDimension() {
		return dimension;
	}

	public Vect3D getOrigin() {
		return origin;
	}

	public Vect3D getExtent() {
		return extent;
	}

	public List<VisPoint> getPoints() {
		return points;
	}

	public List<VisPolygon> getPolygons() {
		return polygons;
	}

	public List<VisPolyhedron> getPolyhedra() {
		return polyhedra;
	}

	public List<VisSurfaceTriangle> getSurfaceTriangles() {
		return surfaceTriangles;
	}

	public List<VisLine> getLines() {
		return lines;
	}

	public void addPolygon(VisPolygon polygon) {
		polygons.add(polygon);
	}

	public void addPolyhedron(VisPolyhedron polyhedron) {
		polyhedra.add(polyhedron);
	}

	public void addSurfaceTriangle(VisSurfaceTriangle surfaceTriangle) {
		surfaceTriangles.add(surfaceTriangle);
	}

	public void addLine(VisLine newVisLine) {
		lines.add(newVisLine);
	}

	public void addPoint(VisPoint newVisPoint) {
		points.add(newVisPoint);
	}
	
	public void addSurfacePoint(VisPoint newVisPoint) {
		surfacePoints.add(newVisPoint);
	}

	public List<VisPoint> getSurfacePoints() {
		return surfacePoints;
	}

}
