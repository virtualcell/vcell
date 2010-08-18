package org.vcell.smoldyn.test;

import java.util.Random;
import org.vcell.smoldyn.model.Boundaries;
import org.vcell.smoldyn.model.Geometry;
import org.vcell.smoldyn.model.Geometryable;
import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.model.Species;
import org.vcell.smoldyn.model.SpeciesState;
import org.vcell.smoldyn.model.Surface;
import org.vcell.smoldyn.model.Model.Dimensionality;
import org.vcell.smoldyn.model.SpeciesState.StateType;
import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.model.util.Sphere;
import org.vcell.smoldyn.model.util.Point.PointFactory;
import org.vcell.smoldyn.simulation.Simulation;
import org.vcell.smoldyn.simulation.SmoldynException;
import org.vcell.smoldyn.simulationsettings.ControlEvent;
import org.vcell.smoldyn.simulationsettings.InternalSettings;
import org.vcell.smoldyn.simulationsettings.SimulationSettings;
import org.vcell.smoldyn.simulationsettings.SmoldynTime;
import org.vcell.smoldyn.simulationsettings.ObservationEvent.EventType;
import org.vcell.smoldyn.simulationsettings.util.Color;
import org.vcell.smoldyn.simulationsettings.util.EventTiming;
import org.vcell.smoldyn.simulationsettings.util.SimulationGraphics;
import org.vcell.smoldyn.simulationsettings.util.SpeciesStateGraphics;
import org.vcell.smoldyn.simulationsettings.util.SurfaceGraphics;
import org.vcell.smoldyn.simulationsettings.util.SimulationGraphics.GraphicsType;
import org.vcell.smoldyn.simulationsettings.util.SurfaceGraphics.Drawmode;



/**
 * An example showing how to produce a Simulation and fill it in with information. 
 * The simulation may then be sent to Smoldyn for execution by printing it to a 
 * configuration file.
 * @author mfenwick
 * 
 */
public class SimulationExample {
	
	private Simulation simulation;
	private SimulationSettings simulationsettings;
	private Model model;
	
	
	/**
	 * Initializes a new 3-d Model, and a SimulationSettings, then hands both of 
	 * these to a new instance of a Simulation.  Pointers are kept to all three of
	 * these in order to eliminate the need to pass the pointers around to all of 
	 * the submethods which are responsible for filling it out with information.
	 * @param none
	 */
	public SimulationExample() {
		this.model = new Model(Dimensionality.three, new Geometry(new Boundaries(0, 200, 0, 100, 0, 100)));
		this.simulationsettings = new SimulationSettings();
		this.simulation = new Simulation(this.model, this.simulationsettings);
	}
	
	
	/**
	 * Gets an example Simulation, which may be translated into a Smoldyn configuration
	 * file and executed.
	 * 
	 * @param none
	 * @return a filled-out Simulation
	 */
	public Simulation getExample() {
		try {
			this.addModelSpecies();
			this.addModelSurfaces();
			this.addModelSurfaceActions();
			this.addModelCompartments();
			this.addModelSpeciesStateDiffusion();
			this.addModelVolumeReactions();
			this.addModelSurfaceReactions();
			this.addModelVolumeMolecules();
			this.addModelSurfaceMolecules();
			this.addModelManipulationEvents();
			
			this.addSimTime();
			this.addSimFilehandles();
			this.addSimObservationEvents();
			this.addSimControlEvents();
			this.addSimGraphics();
			this.addSimInternalSettings();
		} catch (SmoldynException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return simulation;
	}

	
	private void addModelSpecies() throws SmoldynException {
		String [] speciesnames = {"spec1", "spec2", "spec3", "spec4", "spec5"};
		for(String x : speciesnames) {
			model.addSpecies(x);
		}
	}
	
	private void addModelSurfaces() throws SmoldynException {
		Geometryable geometry = model.getGeometry();
		geometry.addSurface("surface1");
		Surface surface = geometry.getSurface("surface1");
		PointFactory pf = model.getPointFactory();
		surface.addPanel(new Sphere("panel1", 35f, pf.getNewPoint(50d, 50d, 50d)));
		surface.addPanel(new Sphere("panel2", 20f, pf.getNewPoint(150d, 50d, 50d)));
		this.simulation.addSurfaceGraphics(surface, new SurfaceGraphics(new Color(.9f, .9f, .9f), Drawmode.edge, 28, 24));
	}
	
	private void addModelSurfaceActions() throws SmoldynException {
		model.addSurfaceActions("surface1", "spec1", 1d, 1d, 1d);
		model.addSurfaceActions("surface1", "spec5", .3d, .5d, .6d);
	}
	
	private void addModelCompartments() throws SmoldynException {
		Geometryable geometry = model.getGeometry();
		geometry.addCompartment("compartment1", new String [] {"surface1"}, new Point [] {new Point(50d, 50d, 50d)});
	}

	private void addModelSpeciesStateDiffusion() throws SmoldynException {
		int i = 0;
		float j = 1;
		StateType statetype = StateType.solution;
		Species [] modelspecies = model.getSpecies();
		for(Species s : modelspecies) {
			String name = s.getName();
			model.addSpeciesState(name, statetype, 1d);
			SpeciesState speciesstate = model.getSpeciesState(name, statetype);
			this.simulation.addSpeciesStateGraphics(speciesstate, new SpeciesStateGraphics(
					new Color((float) (j - .2 * i), (float) (.2 * i), (float) (j - .2 * i)), 1));
			i = i + 1;
		}
		//further testing
		Species tempspecies = modelspecies[0];
		model.addSpeciesState(tempspecies.getName(), StateType.back, 2d);
		model.addSpeciesState(tempspecies.getName(), StateType.front, 3d);
		model.addSpeciesState(tempspecies.getName(), StateType.down, 4d);
		model.addSpeciesState("spec1", StateType.up, .5d);
		model.addSpeciesState("spec3", StateType.down, .2d);
		model.addSpeciesState("spec4", StateType.up, .3d);
		model.addSpeciesState("spec2", StateType.down, .8d);
	}
	
	private void addModelVolumeReactions() throws SmoldynException {
		model.addVolumeReaction("volumereaction1", Geometry.SIMULATIONENCLOSINGCOMPARTMENT, null, "spec1", "spec1", "spec1", .007f);
		model.addVolumeReaction("volumereaction2", Geometry.SIMULATIONENCLOSINGCOMPARTMENT, "spec1", "spec3", "spec3", "spec3", 20f);
		model.addVolumeReaction("volumereaction3", Geometry.SIMULATIONENCLOSINGCOMPARTMENT, "spec3", null, null, null, .005f);
//		model.addVolumeReaction("volumereaction3", null, "spec3", "spec3", "spec1", "spec4", .0001f, false);
	}
	
	private void addModelSurfaceReactions() throws SmoldynException {
		model.addSurfaceReaction("surfacereaction1", "surface1", "spec4", StateType.up, "spec4", StateType.up, 
				"spec2", StateType.down, null, StateType.down, .5f);
		model.addSurfaceReaction("surfacereaction2", "surface1", "spec2", StateType.down, null, null, "spec4", StateType.up, 
				"spec4", StateType.up, .5f);
	}
	
	private void addModelVolumeMolecules() throws SmoldynException {
		int number = 100;
		PointFactory pf = model.getPointFactory();
		model.addVolumeMolecule("compartment1", "spec1", pf.getNewPoint(50d, 50d, 50d), number);
		model.addVolumeMolecule(Geometry.SIMULATIONENCLOSINGCOMPARTMENT, "spec3", pf.getNewPoint(40d, 40d, 40d), number);
	}
	
	private void addModelSurfaceMolecules() throws SmoldynException {
		final int numnum = 150;
		model.addSurfaceMolecule("surface1", "spec4", StateType.up, null, numnum);
	}
	
	private void addModelManipulationEvents() {
		
	}
	
	
	
	private void addSimTime() {
		simulationsettings.setSmoldyntime(new SmoldynTime(0f, 1000000f, .1f));
	}
	
	private void addSimFilehandles() throws SmoldynException {
		String [] paths = {"filehandle.txt", "savesim.txt", "molecularlocations.txt"};
		for(String string : paths) {
			simulationsettings.addFilehandle(string);
		}
	}
	
	private void addSimObservationEvents() throws SmoldynException {
		simulationsettings.addObservationEvent(new EventTiming(0., 100000., 25.), EventType.TOTAL_MOLECULES_BY_TYPE, "filehandle.txt");
		simulationsettings.addObservationEvent(new EventTiming(0., 50000., 5000.), EventType.SAVE_SIMULATION_STATE, "savesim.txt");
		simulationsettings.addObservationEvent(new EventTiming(0., 1000., 50.), EventType.LIST_ALL_MOLECULES, "molecularlocations.txt");
	}
	
	private void addSimControlEvents() {
		simulationsettings.addControlEvent(new ControlEvent(new EventTiming(0., 0., 0.), ControlEvent.EventType.pause));
	}
	
	private void addSimGraphics() {
		SimulationGraphics simulationgraphics = new SimulationGraphics(10, GraphicsType.opengl_good);
		simulationsettings.setSimulationGraphics(simulationgraphics);
	}
	
	private void addSimInternalSettings() {
		Random random = new Random();
		InternalSettings internalsettings = new InternalSettings(Math.abs(random.nextInt() % 1000000));
		simulationsettings.setInternalSettings(internalsettings);
	}
}
