package org.vcell.smoldyn.inputfile.smoldynwriters;

import java.io.PrintWriter;

import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.simulation.Simulation;
import org.vcell.smoldyn.simulationsettings.SimulationSettings;


/**
 * Main starting point for writing a simulation to a Smoldyn configuration file.
 * Generally, the model information is written first, and the simulation settings
 * are written towards the end; however, this is not absolute nor unchanging.  
 * The order of writing is very important, but the rules governing the ordering
 * are not very clear, so it is possible (likely?) that the ordering will need to
 * be changed in the future in order to make more complicated simulations parseable
 * by Smoldyn.
 * The file is written by a set of classes which correspond to the data members
 * of a {@link Model} and a {@link SimulationSettings}.  Each class writes data that mostly pertains
 * to either a Model or to a SimulationSettings.  However, each constuctor is passed
 * a PrintWriter and a Simulation.  Since a {@link Simulation} has both a {@link Model} and a 
 * SimulationSettings, the constructors for Model-related writers are able to access
 * the SimulationSettings if they need to, and vice versa.  Most do not, but two 
 * exceptions are the {@link GeometryWriter} and the {@link SpeciesStateWriter}, which use the
 * Simulation reference to access display data held by the SimulationSettings.
 * Constructors that do not currently need the Simulation reference receive it anyway,
 * both to maintain a consistent, if arbitrary, standard, and to provide for the future,
 * in case they ever need to access information from the "other side".
 * 
 * Configuration file writing was previously implemented by a group of static classes
 * and static methods.  This became very complicated, because the PrintWriter, Model,
 * Simulation, and so on, had to be passed to each method.  This became a nightmare
 * to keep track of.  Instantiation of a class allows it to hold on to a pointer to
 * the PrintWriter and so on, so that those do not need to be passed to each method.
 * 
 * All of the writers *SHOULD* (but may not, as this was conceived later) print a warning
 * to stderr if attempting to write a feature that is either not well-tested or not yet
 * implemented, and may optionally cause a {@link RuntimeException} to be thrown.
 * 
 * @author mfenwick
 * 
 */
public class SimulationWriter {

	/**
	 * Translates a simulation into a Smoldyn configuration file.
	 *
	 * @param simulation
	 * @param writer
	 */
	public static void write(Simulation simulation, PrintWriter writer) {

		//writers related to Model (although a couple write some display stuff from SimulationSettings)
		SpeciesWriter specieswriter = new SpeciesWriter(simulation, writer);
		specieswriter.write();
		
		GeometryWriter gw = new GeometryWriter(simulation, writer);
		gw.write();//writes dim, compartments, surfaces
		
		BoundaryWriter bw = new BoundaryWriter(simulation, writer);
		bw.write();
		
		SpeciesStateWriter speciesstatewriter = new SpeciesStateWriter(simulation, writer);
		speciesstatewriter.write();
		
		ReactionWriter rw = new ReactionWriter(simulation, writer);
		rw.write();
		
		MoleculeWriter mw = new MoleculeWriter(simulation, writer);
		mw.write();
		
		ManipulationEventWriter mew = new ManipulationEventWriter(simulation, writer);
		mew.write();
		
		//writers related to SimulationSettings		
		SmoldynTimeWriter stw = new SmoldynTimeWriter(simulation, writer);
		stw.write();

		FilehandlesWriter fhw = new FilehandlesWriter(simulation, writer);
		fhw.write();
		
		ObservationEventWriter oew = new ObservationEventWriter(simulation, writer);
		oew.write();
		
		ControlEventWriter cew = new ControlEventWriter(simulation, writer);
		cew.write();
		
		SimulationGraphicsWriter sgw = new SimulationGraphicsWriter(simulation, writer);
		sgw.write();
		
		InternalSettingsWriter isw = new InternalSettingsWriter(simulation, writer);
		isw.write();
		
	}
}
