package org.vcell.smoldyn.inputfile.smoldynwriters;

import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.model.Species;
import org.vcell.smoldyn.model.SurfaceMolecule;
import org.vcell.smoldyn.model.VolumeMolecule;
import org.vcell.smoldyn.model.Species.StateType;
import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.simulation.Simulation;



/**
 * @author mfenwick
 *
 */
public class MoleculeWriter {
	
	private final Model model;
	private final PrintWriter writer;
	private static final int MAXMOLECULESMULTIPLIER = 5;
	
	
	public MoleculeWriter(Simulation simulation, PrintWriter writer) {
		this.model = simulation.getModel();
		this.writer = writer;
	}

	public void write() {
		writer.println("# locations of molecules");
		writeMaxMolecules();
		writeVolumeMolecules();
		writeSurfaceMolecules();
		writer.println();
		writer.println();
	}
	
	/**
	 * example:
	 * 		max_mol 10000
	 */
	private void writeMaxMolecules() {
		int maxMolecules = 0;
		for(VolumeMolecule vm : model.getVolumeMolecules()) {
			maxMolecules += vm.getCount();
		}
		for(SurfaceMolecule sm : model.getSurfaceMolecules()) {
			maxMolecules += sm.getCount();
		}
		maxMolecules *= MAXMOLECULESMULTIPLIER;// TODO this is not good.....overflow possibility, large number possibility....
		writer.println(SmoldynFileKeywords.Molecule.max_mol + " " + maxMolecules);
	}

	/**
	 * example:
	 * 		randomly distributed:
	 * 			mol 13 speciesname u u u
	 * 		specific location:
	 * 			mol 1 speciesname 7.5 13 2
	 * 
	 * example (always randomly distributed):
	 * 		compartment_mol 3 speciesname compartmentname
	 */
	private void writeVolumeMolecules() {
		VolumeMolecule [] volumemolecules = model.getVolumeMolecules();
		for(VolumeMolecule volumemolecule : volumemolecules) {
			if(volumemolecule.getCompartment() == null) {
				writer.print(SmoldynFileKeywords.Molecule.mol + " " + volumemolecule.getCount() + " " + 
						volumemolecule.getSpecies().getName());
				Point point = volumemolecule.getPoint();
				Double [] coordinates = {point.getX(), point.getY(), point.getZ()};
				for(Double d : coordinates) {
					if(d == null) {
						writer.print(" u");
					} else {
						writer.print(" " + d);
					}
				}
			} else {
				writer.println(SmoldynFileKeywords.Molecule.compartment_mol + " " + volumemolecule.getCount() + " " + 
						volumemolecule.getSpecies().getName() + " " + volumemolecule.getCompartment().getName());
			}
		}
	}
	
	/**
	 * example with unspecified location (Smoldyn calculates random distribution):
	 * 		surface_mol 75 speciesname(up) surfacename triangle all
	 * example with specified location:
	 * 		surface_mol 75 speciesname(up) surfacename sphere panelname1 1 2.3 1.85
	 */
	private void writeSurfaceMolecules() {
		SurfaceMolecule [] surfacemolecules = model.getSurfaceMolecules();
		for(SurfaceMolecule surfacemolecule : surfacemolecules) {
			final Species species = surfacemolecule.getSpecies();
			final StateType state = surfacemolecule.getStateType();
			writer.println(SmoldynFileKeywords.Molecule.surface_mol + " " + surfacemolecule.getCount() + " " + 
					species.getName() + "(" +  state + ") " + surfacemolecule.getSurface().getName() + " all all");
		}
	}
}
