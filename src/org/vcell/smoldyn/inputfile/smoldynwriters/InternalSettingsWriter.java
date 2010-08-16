package org.vcell.smoldyn.inputfile.smoldynwriters;

import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.simulation.Simulation;
import org.vcell.smoldyn.simulationsettings.InternalSettings;
import org.vcell.smoldyn.simulationsettings.SimulationSettings;



/**
 * @author mfenwick
 *
 */
public class InternalSettingsWriter {
	
	private SimulationSettings simulationsettings;
	private PrintWriter writer;
	
	
	public InternalSettingsWriter(Simulation simulation, PrintWriter writer) {
		this.simulationsettings = simulation.getSimulationSettings();
		this.writer = writer;
	}

	
	public void write() {
		InternalSettings internalsettings = simulationsettings.getInternalSettings();
		writer.println("# simulation settings");
		if(internalsettings != null) {
			if(internalsettings.getRandomseed() != null) {
				writer.println(SmoldynFileKeywords.SimulationSetting.rand_seed + " " + internalsettings.getRandomseed());
			}
			writer.println(SmoldynFileKeywords.SimulationSetting.accuracy + " " + internalsettings.getAccuracy());
			writer.println(SmoldynFileKeywords.SimulationSetting.boxsize + " " + internalsettings.getBoxwidth());
			writer.println(SmoldynFileKeywords.SimulationSetting.gauss_table_size + " " + internalsettings.getGausstablesize());
//			writer.println(SmoldynFileKeywords.SimulationSetting.epsilon + " " + internalsettings.getEpsilon());
//			writer.println(SmoldynFileKeywords.SimulationSetting.neighbor_dist + " " + internalsettings.getNeighbordist());
		} else {
			Utilities.writeSmoldynWarning("internalsettings is null");
		}
		writer.println();
		writer.println();
	}
}
