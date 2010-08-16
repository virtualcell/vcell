package org.vcell.smoldyn.inputfile.smoldynwriters;

import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.simulation.Simulation;
import org.vcell.smoldyn.simulationsettings.ObservationEvent;
import org.vcell.smoldyn.simulationsettings.SimulationSettings;
import org.vcell.smoldyn.simulationsettings.VCellObservationEvent;
import org.vcell.smoldyn.simulationsettings.ObservationEvent.EventType;
import org.vcell.smoldyn.simulationsettings.VCellObservationEvent.VCellEventType;
import org.vcell.smoldyn.simulationsettings.util.EventTiming;
import org.vcell.smoldyn.simulationsettings.util.Filehandle;



/**
 * @author mfenwick
 *
 */
public class ObservationEventWriter {
	
	private SimulationSettings simulationsettings;
	private PrintWriter writer;
	private static double tolerance = .000000001;
	
	
	public ObservationEventWriter(Simulation simulation, PrintWriter writer) {
		this.simulationsettings = simulation.getSimulationSettings();
		this.writer = writer;
	}
	
	
	public void write() {
		writer.println("# observation events");
		ObservationEvent [] events = simulationsettings.getObservationEvents();
		for(ObservationEvent observationevent : events) {
			this.determineCommandType(observationevent);
		}
		writer.println();
		writer.println();
		
		writer.println("# vcell observation events");
		VCellObservationEvent [] vcellevents = simulationsettings.getVCellObservationEvents();
		for(VCellObservationEvent vcellevent : vcellevents) {
			this.determineCommandType(vcellevent);
		}
		writer.println();
		writer.println();
	}
	
	private void determineCommandType(ObservationEvent event) {
		EventType type = event.getEventType();
		if(type == EventType.TOTAL_MOLECULES_BY_TYPE) {
			writeTotalByType(event);
		} else if(type == EventType.SAVE_SIMULATION_STATE) {
			writeCommandTime(event.getEventTiming());
			writeSaveSim(event.getFilehandle());
		} else if (type == EventType.LIST_ALL_MOLECULES) {
			writeCommandTime(event.getEventTiming());
			writeEcho(event.getFilehandle(), "\\nSmoldyn molecules at time (time unknown):\\n");
			writeCommandTime(event.getEventTiming());
			writeAllMolecules(event.getFilehandle());
		}else {
			throw new RuntimeException("printing for event type " + type + " is currently unsupported");
		}
	}
	
	
	private void determineCommandType(VCellObservationEvent vcellevent) {
		VCellEventType type = vcellevent.getEventType();
		if (type == VCellEventType.PRINT_PROGRESS) {
			writeCommandTime(vcellevent.getEventTiming());
			writePrintProgress();
		} else if (type == VCellEventType.WRITE_OUTPUT) {
			writeCommandTime(vcellevent.getEventTiming());
			writeWriteOutput(simulationsettings.getInternalSettings().getBoxwidth());
		} 
	}


	private void writeCommandTime(EventTiming eventtiming) {
		Double start = eventtiming.getTimestart();
		Double stop = eventtiming.getTimestop();
		Double step = eventtiming.getTimestep();
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
			throw new RuntimeException("unsupported command type caused by odd event specification (timing components unrecognized)");
		}
		writer.print(timing + " ");
	}
	
	
	
	private void writeTotalByType(ObservationEvent event) {
		writeCommandTime(new EventTiming(0., 0., 0.));//new eventtiming of before simulation starts
		writeMolcountheader(event.getFilehandle());
		writeCommandTime(event.getEventTiming());
		writeMolcount(event.getFilehandle());
	}
	
	
	private void writeWriteOutput(final double boxwidth) {
		writer.print(SmoldynFileKeywords.SimulationControl.writeOutput);
		final int dim = 2;//TODO this shouldn't be hard-coded, but where should it come from?
		Utilities.writeSmoldynWarning("ObservationEventWriter assuming VCell simulation has only 2 dimensions");
		int [] boxes = this.simulationsettings.getBoxes();
		for(int i = 0; i < dim; i++) {
			writer.print(" " + boxes[i]);
		}
		writer.println();
	}
		
	private void writePrintProgress() {
		writer.println(SmoldynFileKeywords.SimulationControl.printProgress);
	}
	
	
	private void writeMolcountheader(Filehandle filehandle) {
		writer.println(SmoldynFileKeywords.SimulationControl.molcountheader + " " + filehandle.getPath());
	}
	
	private void writeMolcount(Filehandle filehandle) {
		writer.println(SmoldynFileKeywords.SimulationControl.molcount + " " + filehandle.getPath());
	}

	private void writeSaveSim(Filehandle filehandle) {
		writer.println(SmoldynFileKeywords.SimulationControl.savesim + " " + filehandle.getPath());
	}
	
	private void writeAllMolecules(Filehandle filehandle) {
		writer.println(SmoldynFileKeywords.SimulationControl.listmols + " " + filehandle.getPath());
	}
	
	private void writeEcho(Filehandle filehandle, String message) {
		writer.println(SmoldynFileKeywords.SimulationControl.echo + " " + filehandle.getPath() + " \"" + message + "\"");
	}
}
