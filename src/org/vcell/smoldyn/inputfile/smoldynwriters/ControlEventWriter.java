package org.vcell.smoldyn.inputfile.smoldynwriters;


import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.simulation.Simulation;
import org.vcell.smoldyn.simulationsettings.ControlEvent;
import org.vcell.smoldyn.simulationsettings.SimulationSettings;
import org.vcell.smoldyn.simulationsettings.ControlEvent.EventType;
import org.vcell.smoldyn.simulationsettings.util.EventTiming;


/**
 * @author mfenwick
 *
 */
public class ControlEventWriter {
	
	private SimulationSettings simulationsettings;
	private PrintWriter writer;
	private static double tolerance = .000000001;
	
	
	public ControlEventWriter(Simulation simulation, PrintWriter writer) {
		this.simulationsettings = simulation.getSimulationSettings();
		this.writer = writer;
	}


	public void write() {
		writer.println("# control events");
		ControlEvent [] events = simulationsettings.getControlEvents();
		for(ControlEvent controlevent : events) {
			this.determineCommandType(controlevent);
		}
		writer.println();
		writer.println();
	}

	private void determineCommandType(ControlEvent controlevent) {
		EventType type = controlevent.getEventType();
		if(type == EventType.pause) {
			writeCommandTime(new EventTiming(2d, 2d, 0d));//new eventtiming of before simulation starts
			writer.println(SmoldynFileKeywords.SimulationControl.pause);
		} else {
			throw new RuntimeException("printing for event type " + type + " is currently unsupported");
		}
	}
	
	private void writeCommandTime(EventTiming eventtiming) {
		double start = eventtiming.getTimestart();
		double stop = eventtiming.getTimestop();
		double step = eventtiming.getTimestep();
		String timing = SmoldynFileKeywords.Runtime.cmd.toString() + " ";
		if ((start - 0 < tolerance) && (stop - 0 < tolerance)) {
			timing = timing + SmoldynFileKeywords.Runtime.b;
		} else if (stop - start < tolerance) {
			timing = timing + "@ " + start;
		} else if (stop > start) {
			timing = timing + SmoldynFileKeywords.Runtime.i + " " + start + " " + stop + " " + step;
		} 
//			else if () {
//				//a
//			} 
//			else if () {
//				//x
//			} 
		else {// unsupported -> throw exception!
			Utilities.throwUnexpectedException("unsupported command timing caused by odd timing specification");
		}
		writer.print(timing + " ");
	}
}
