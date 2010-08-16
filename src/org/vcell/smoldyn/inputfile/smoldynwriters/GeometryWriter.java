package org.vcell.smoldyn.inputfile.smoldynwriters;

import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.model.Geometryable;
import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.simulation.Simulation;


/**
 * @author mfenwick
 *
 */
public class GeometryWriter {
	
	private Simulation simulation;
	private Model model;
	private PrintWriter writer;
	private Geometryable geometry;
	
	
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

		SurfaceWriter sw = new SurfaceWriter(simulation, writer);
		sw.write();
		
		CompartmentWriter cw = new CompartmentWriter(simulation, writer);
		cw.write();
	}
}
