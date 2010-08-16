package org.vcell.smoldyn.inputfile.smoldynwriters;

import java.io.PrintWriter;

//import org.vcell.smoldyn.matts_project.model.ManipulationEvent;
//import org.vcell.smoldyn.matts_project.model.Model;
import org.vcell.smoldyn.simulation.Simulation;


/**
 * @author mfenwick
 *
 */
public class ManipulationEventWriter {
	
//	private Model model;
	private PrintWriter writer;
	
	
	public ManipulationEventWriter(Simulation simulation, PrintWriter writer) {
//		this.model = simulation.getModel();
		this.writer = writer;
	}

	

	public void write() {
		// TODO Auto-generated method stub
//		ManipulationEvent [] manipulationevents = model.getManipulationEvents();
		writer.println("# manipulation events");
		Utilities.writeUnimplementedWarning("manipulation events", false);
		writer.println();
		writer.println();
	}

}
