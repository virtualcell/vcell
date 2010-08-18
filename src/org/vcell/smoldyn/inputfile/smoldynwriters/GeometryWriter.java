package org.vcell.smoldyn.inputfile.smoldynwriters;

import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.model.Boundaries;
import org.vcell.smoldyn.model.Geometryable;
import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.simulation.Simulation;


/**
 * @author mfenwick
 *
 */
public class GeometryWriter {
	
	private final Simulation simulation;
	private final Model model;
	private final PrintWriter writer;
	private final Geometryable geometry;
	
	
	public GeometryWriter(Simulation simulation, PrintWriter writer) {
		this.simulation = simulation;
		this.model = simulation.getModel();
		this.writer = writer;
		this.geometry = model.getGeometry();
	}

	public void write() {
		writer.println("# geometry");
		writer.println(SmoldynFileKeywords.Space.dim + " " + model.getDimensionality());
		writer.println(SmoldynFileKeywords.Surface.max_surface + " " + (geometry.getSurfaces().length + 10));
		writer.println(SmoldynFileKeywords.Compartment.max_compartment + " " + (geometry.getCompartments().length + 10));
		writer.println();
		writer.println();
		
		writeBoundaries();

		SurfaceWriter sw = new SurfaceWriter(simulation, writer);
		sw.write();
		
		CompartmentWriter cw = new CompartmentWriter(simulation, writer);
		cw.write();
	}

	private void writeBoundaries() {
		writer.println("# boundaries and bounding walls");
		final double [] widths = new double[3];
		int i = 0;
		Boundaries b = model.getGeometry().getBoundaries();
		writer.println(SmoldynFileKeywords.Space.boundaries + " " + i + " " + b.getXlow() + " " + b.getXhigh());
		widths[i++] = b.getXhigh() - b.getXlow();
		writer.println(SmoldynFileKeywords.Space.boundaries + " " + i + " " + b.getYlow() + " " + b.getYhigh());
		widths[i++] = b.getYhigh() - b.getYlow();
		writer.println(SmoldynFileKeywords.Space.boundaries + " " + i + " " + b.getZlow() + " " + b.getZhigh());
		widths[i++] = b.getZhigh() - b.getZlow();
		writer.println();		
	}
}
