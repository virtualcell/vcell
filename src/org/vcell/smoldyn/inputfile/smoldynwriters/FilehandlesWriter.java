package org.vcell.smoldyn.inputfile.smoldynwriters;


import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.simulation.Simulation;
import org.vcell.smoldyn.simulationsettings.SimulationSettings;
import org.vcell.smoldyn.simulationsettings.util.Filehandle;


/**
 * @author mfenwick
 *
 */
public class FilehandlesWriter {
	
	private SimulationSettings simulationsettings;
	private PrintWriter writer;
	
	
	public FilehandlesWriter(Simulation simulation, PrintWriter writer) {
		this.simulationsettings = simulation.getSimulationSettings();
		this.writer = writer;
	}

	
	public void write() {
		writer.println("# filehandles");
		Filehandle [] filehandles = simulationsettings.getFilehandles();
		//output_root
		if(filehandles.length > 0) {
			writer.print(SmoldynFileKeywords.RuntimeCommand.output_files);
			for(Filehandle f : filehandles) {
				writer.print(" " + f.getPath());
			}
			writer.println();
		}
		writer.println();
		writer.println();
	}
	
}
