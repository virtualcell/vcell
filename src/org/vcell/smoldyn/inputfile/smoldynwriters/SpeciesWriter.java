package org.vcell.smoldyn.inputfile.smoldynwriters;

import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.inputfile.SmoldynFileKeywordsDeprecated;
import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.model.Species;
import org.vcell.smoldyn.model.Species.StateType;
import org.vcell.smoldyn.simulation.Simulation;
import org.vcell.smoldyn.simulationsettings.util.Color;
import org.vcell.smoldyn.simulationsettings.util.SpeciesGraphics;


/**
 * @author mfenwick
 *
 */
public class SpeciesWriter {

	private Simulation simulation;
	private Model model;
	private PrintWriter writer;
	
	
	public SpeciesWriter(Simulation simulation, PrintWriter writer) {
		this.simulation = simulation;
		this.model = simulation.getModel();
		this.writer = writer;
	}
	
	public void write() {
		Species [] species = model.getSpecies();
		writer.println("# species declarations");
		if(species.length > 0) {
			writer.print(SmoldynFileKeywords.Molecule.species);
		} else {
			writer.print(SmoldynFileKeywordsDeprecated.Molecule.max_species + " " + 1);
		}
		for(Species s : species) {
			writer.print(" " + s.getName());
		}
		writer.println();
		writer.println();
		writer.println("# diffusion properties");
		for(Species s : species) {
			writeDiffusion(s);
			writeGraphics(s);
		}
		writer.println();
		writer.println();
		writer.println();
	}	
	
	
	private void writeDiffusion(Species species) {
		writer.println(SmoldynFileKeywords.Molecule.difc + " " + species.getName() + "(" + 
				StateType.solution + ") " + species.getSolutiondiffusion());
		writer.println(SmoldynFileKeywords.Molecule.difc + " " + species.getName() + "(" + 
				StateType.up + ") " + species.getUpdiffusion());
		writer.println(SmoldynFileKeywords.Molecule.difc + " " + species.getName() + "(" + 
				StateType.down + ") " + species.getDowndiffusion());
		writer.println(SmoldynFileKeywords.Molecule.difc + " " + species.getName() + "(" + 
				StateType.front + ") " + species.getFrontdiffusion());
		writer.println(SmoldynFileKeywords.Molecule.difc + " " + species.getName() + "(" + 
				StateType.back + ") " + species.getBackdiffusion());	
	}

	private void writeGraphics(Species species) {
		SpeciesGraphics graphics = simulation.getSpeciesGraphics(species);
		if (graphics != null) {
			Color c = graphics.getSolutioncolor();
			//TODO this actually ignores the colors for states that aren't solution....just because it's simple now
			writer.println(SmoldynFileKeywords.Graphics.color + " " + species.getName() + "(all) " 
					+ c.getRed() + " " + c.getGreen() + " " + c.getBlue());
		}
	}
}
