package org.vcell.smoldyn.inputfile.smoldynwriters;


import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.model.SpeciesState;
import org.vcell.smoldyn.simulation.Simulation;
import org.vcell.smoldyn.simulationsettings.util.Color;
import org.vcell.smoldyn.simulationsettings.util.SpeciesStateGraphics;


/**
 * @author mfenwick
 *
 */
public class SpeciesStateWriter {
	
	private Simulation simulation;
	private Model model;
	private PrintWriter writer;
	
	
	public SpeciesStateWriter(Simulation simulation, PrintWriter writer) {
		this.simulation = simulation;
		this.model = simulation.getModel();
		this.writer = writer;
	}

	public void write() {
		writer.println("# speciesstate declarations");
		SpeciesState[] speciesstates = model.getSpeciesStates();
		for(SpeciesState ss : speciesstates) {
			this.writeGraphics(ss);
			this.writeDiffusion(ss);
		}
		writer.println();
		writer.println();
	}
	
	private void writeDiffusion(SpeciesState speciesstate) {
		writer.println(SmoldynFileKeywords.Molecule.difc + " " + speciesstate.getSpecies().getName() + "(" + 
				speciesstate.getState() + ") " + speciesstate.getDifc());
		
//		if (speciesstate.getDifm() != null) {
//			Utilities.writeSmoldynWarning("warning, writing of difm not yet supported (SpeciesStatesWriter)");// TODO
//		}
//		if (speciesstate.getDrift() != null) {
//			Utilities.writeSmoldynWarning("warning, writing of drift not yet supported (SpeciesStatesWriter)");// TODO		
//		}		
	}

	private void writeGraphics(SpeciesState speciesstate) {
		SpeciesStateGraphics graphics = simulation.getSpeciesStateGraphics(speciesstate);
		if (graphics != null) {
			Color c = graphics.getColor();
//			writer.println(SmoldynFileKeywords.Graphics.color + " " + speciesstate.getSpecies().getName() + "(" 
//					+ speciesstate.getState() + ") " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
			writer.println(SmoldynFileKeywords.Graphics.color + " " + speciesstate.getSpecies().getName() + "(all) " 
			+ c.getRed() + " " + c.getGreen() + " " + c.getBlue());
		}
	}
}
