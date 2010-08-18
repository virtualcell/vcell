package org.vcell.smoldyn.inputfile.smoldynwriters;


import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.model.Compartment;
import org.vcell.smoldyn.model.Geometryable;
import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.model.Surface;
import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.simulation.Simulation;


/**
 * @author mfenwick
 *
 */
public class CompartmentWriter {
	
	private Model model;
	private PrintWriter writer;
	private Geometryable geometry;
	
	
	public CompartmentWriter(Simulation simulation, PrintWriter writer) {
		this.model = simulation.getModel();
		this.writer = writer;
		this.geometry = model.getGeometry();
	}
	

	public void write() {
		writer.println("# compartments");
		Compartment [] compartments = geometry.getCompartments();
		for(Compartment compartment : compartments) {
			writer.println(SmoldynFileKeywords.Compartment.start_compartment + " " + compartment.getName());
			for(Surface surface : compartment.getSurfaces()) {
				writer.println(SmoldynFileKeywords.Compartment.surface + " " + surface.getName());
			}
			for(Point point : compartment.getPoints()) {
				writer.println(SmoldynFileKeywords.Compartment.point + " " + point.getX() + " " + point.getY() + " " + point.getZ());
			}
			//TODO figure out how to define inside/outside with points
			writer.println(SmoldynFileKeywords.Compartment.end_compartment);
			writer.println();
		}
		writer.println();
	}
	
}
