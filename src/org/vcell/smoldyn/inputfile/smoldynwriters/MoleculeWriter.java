package org.vcell.smoldyn.inputfile.smoldynwriters;

import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.model.SpeciesState;
import org.vcell.smoldyn.model.SurfaceMolecule;
import org.vcell.smoldyn.model.VolumeMolecule;
import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.simulation.Simulation;



/**
 * @author mfenwick
 *
 */
public class MoleculeWriter {
	
	private Model model;
	private PrintWriter writer;
	private static final int MAXMOLECULESMULTIPLIER = 5;
	
	
	public MoleculeWriter(Simulation simulation, PrintWriter writer) {
		this.model = simulation.getModel();
		this.writer = writer;
	}

	public void write() {
		writer.println("# locations of molecules");
		int maxMolecules = 0;
		for(VolumeMolecule vm : model.getVolumeMolecules()) {
			maxMolecules += vm.getCount();
		}
		for(SurfaceMolecule sm : model.getSurfaceMolecules()) {
			maxMolecules += sm.getCount();
		}
		maxMolecules *= MAXMOLECULESMULTIPLIER;// TODO this is not good.....overflow possibility, large number possibility....
		writer.println(SmoldynFileKeywords.Molecule.max_mol + " " + maxMolecules);
		writeVolumeMolecules();
		writeSurfaceMolecules();
		writer.println();
		writer.println();
	}
	
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
	
	private void writeSurfaceMolecules() {
		SurfaceMolecule [] surfacemolecules = model.getSurfaceMolecules();
		for(SurfaceMolecule surfacemolecule : surfacemolecules) {
			SpeciesState ssd = surfacemolecule.getSpeciesStateDiffusion();
			writer.println(SmoldynFileKeywords.Molecule.surface_mol + " " + surfacemolecule.getCount() + " " + 
					ssd.getSpecies().getName() + "(" +  ssd.getState() + ") " + surfacemolecule.getSurface().getName() + " all all");
		}
	}
}
