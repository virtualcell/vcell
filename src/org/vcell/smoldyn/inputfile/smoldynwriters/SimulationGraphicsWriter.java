package org.vcell.smoldyn.inputfile.smoldynwriters;

import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.simulation.Simulation;
import org.vcell.smoldyn.simulationsettings.SimulationSettings;
import org.vcell.smoldyn.simulationsettings.util.SimulationGraphics;



/**
 * @author mfenwick
 *
 */
public class SimulationGraphicsWriter {
	
	private SimulationSettings simulationsettings;
	private PrintWriter writer;
	
	
	public SimulationGraphicsWriter(Simulation simulation, PrintWriter writer) {
		this.simulationsettings = simulation.getSimulationSettings();
		this.writer = writer;
	}

	public void write() {
		writer.println("# graphics declarations");
		SimulationGraphics graphics = simulationsettings.getSimulationGraphics();
		if(graphics != null) {
			writer.println(SmoldynFileKeywords.Graphics.graphics + " " + graphics.getGraphicstype());
			writer.println(SmoldynFileKeywords.Graphics.graphic_iter + " " + graphics.getGraphic_iter());
		} else {
			Utilities.writeSmoldynWarning("SimulationGraphics is null");
		}
		writer.println();
		writer.println();
	}
}