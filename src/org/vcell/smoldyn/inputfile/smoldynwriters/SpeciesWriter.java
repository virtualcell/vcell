package org.vcell.smoldyn.inputfile.smoldynwriters;

import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.inputfile.SmoldynFileKeywordsDeprecated;
import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.model.Species;
import org.vcell.smoldyn.simulation.Simulation;


/**
 * @author mfenwick
 *
 */
public class SpeciesWriter {

	private Model model;
	private PrintWriter writer;
	
	
	public SpeciesWriter(Simulation simulation, PrintWriter writer) {
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
		writer.println();
	}
}
