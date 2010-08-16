package org.vcell.smoldyn.inputfile.smoldynwriters;


import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.simulation.Simulation;
import org.vcell.smoldyn.simulationsettings.SimulationSettings;
import org.vcell.smoldyn.simulationsettings.SmoldynTime;


/**
 * @author mfenwick
 *
 */
public class SmoldynTimeWriter {
	
	private SimulationSettings simulationsettings;
	private PrintWriter writer;
	
	
	public SmoldynTimeWriter(Simulation simulation, PrintWriter writer) {
		this.simulationsettings = simulation.getSimulationSettings();
		this.writer = writer;
	}

	public void write() {
		writer.println("# time declarations");
		SmoldynTime time = simulationsettings.getSmoldyntime();
		if(time != null) {
			writer.println(SmoldynFileKeywords.Time.time_start + " " + time.getStarttime());
			writer.println(SmoldynFileKeywords.Time.time_stop + " " + time.getStoptime());
			writer.println(SmoldynFileKeywords.Time.time_step + " " + time.getSteptime());
		} else {
			Utilities.writeSmoldynWarning("SmoldynTime is null");
		}
		writer.println();
		writer.println();
	}
}
