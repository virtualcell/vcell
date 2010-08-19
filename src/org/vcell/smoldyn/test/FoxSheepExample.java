package org.vcell.smoldyn.test;


import org.vcell.smoldyn.model.Geometryable;
import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.model.Species;
import org.vcell.smoldyn.model.Surface;
import org.vcell.smoldyn.model.Model.Dimensionality;
import org.vcell.smoldyn.model.Species.StateType;
import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.model.util.Sphere;
import org.vcell.smoldyn.model.util.Point.PointFactory;
import org.vcell.smoldyn.model.util.SurfaceActions;
import org.vcell.smoldyn.simulation.Simulation;
import org.vcell.smoldyn.simulation.SmoldynException;
import org.vcell.smoldyn.simulationsettings.ControlEvent;
import org.vcell.smoldyn.simulationsettings.InternalSettings;
import org.vcell.smoldyn.simulationsettings.ObservationEvent;
import org.vcell.smoldyn.simulationsettings.SimulationSettings;
import org.vcell.smoldyn.simulationsettings.SmoldynTime;
import org.vcell.smoldyn.simulationsettings.util.Color;
import org.vcell.smoldyn.simulationsettings.util.EventTiming;
import org.vcell.smoldyn.simulationsettings.util.SimulationGraphics;
import org.vcell.smoldyn.simulationsettings.util.SpeciesGraphics;
import org.vcell.smoldyn.simulationsettings.util.SurfaceGraphics;
import org.vcell.smoldyn.simulationsettings.util.SimulationGraphics.GraphicsType;
import org.vcell.smoldyn.simulationsettings.util.SurfaceGraphics.Drawmode;


/**
 * The fox-sheep thing in a sphere!  Don't change this class!
 * 
 * @author mfenwick
 *
 */
public class FoxSheepExample {
	
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
	public FoxSheepExample() {
		this.model = new Model(Dimensionality.three, null);
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
		String [] speciesnames = {"spec1", "spec2", "spec3", "spec4"};
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
		model.addSurfaceActions("surface1", "spec1", new SurfaceActions(1f, 1f, 1f));
	}
	
	private void addModelCompartments() throws SmoldynException {
		Geometryable geometry = model.getGeometry();
		geometry.addCompartment("compartment1", new String [] {"surface1"}, new Point [] {});
	}

	private void addModelSpeciesStateDiffusion() throws SmoldynException {
//		int i = 0;
//		float j = 1;
//		StateType statetype = StateType.solution;
//		for(Species s : model.getSpecies()) {
//			String name = s.getName();
//			model.addSpeciesState(name, statetype, 1d);
//			SpeciesState speciesstate = model.getSpeciesState(name, statetype);
//			this.simulation.addSpeciesStateGraphics(speciesstate, new SpeciesGraphics(
//					new Color((float) (j - .3 * i), (float) (.3 * i), (float) (j - .3 * i)), 1));
//			i = i + 1;
//		}
	}
	
	private void addModelVolumeReactions() throws SmoldynException {
//		model.addVolumeReaction("volumereaction1", null, null, "spec1", "spec1", "spec1", .007f);
//		model.addVolumeReaction("volumereaction2", null, "spec1", "spec3", "spec3", "spec3", 20f);
//		model.addVolumeReaction("volumereaction3", null, "spec3", null, null, null, .005f);
//		model.addVolumeReaction("volumereaction3", null, "spec3", "spec3", "spec1", "spec4", .0001f, false);
	}
	
	private void addModelSurfaceReactions() {
		//TODO
	}
	
	private void addModelVolumeMolecules() throws SmoldynException {
		int number = 100;
		PointFactory pf = model.getPointFactory();
		model.addVolumeMolecule("compartment1", "spec1", pf.getNewPoint(50d, 50d, 50d), number);
		model.addVolumeMolecule(null, "spec3", pf.getNewPoint(30d, 30d, 30d), number);
	}
	
	private void addModelSurfaceMolecules() {
		
	}
	
	private void addModelManipulationEvents() {
		
	}
	
	
	
	private void addSimTime() {
		simulationsettings.setSmoldyntime(new SmoldynTime(0f, 1000000f, .1f));
	}
	
	private void addSimFilehandles() throws SmoldynException {
		String [] paths = {"filehandle.txt", "savesim.txt"};
		for(String string : paths) {
			simulationsettings.addFilehandle(string);
		}
	}
	
	private void addSimObservationEvents() throws SmoldynException {
		simulationsettings.addObservationEvent(new EventTiming(0., 100000., 25.), 
				ObservationEvent.EventType.TOTAL_MOLECULES_BY_TYPE, "filehandle.txt");
		simulationsettings.addObservationEvent(new EventTiming(0., 50000., 5000.), 
				ObservationEvent.EventType.SAVE_SIMULATION_STATE, "savesim.txt");
	}
	
	private void addSimControlEvents() {
		simulationsettings.addControlEvent(new ControlEvent(new EventTiming(0., 0., 0.), ControlEvent.EventType.pause));
	}
	
	private void addSimGraphics() {
		SimulationGraphics simulationgraphics = new SimulationGraphics(10, GraphicsType.opengl_good);
		simulationsettings.setSimulationGraphics(simulationgraphics);
	}
	
	private void addSimInternalSettings() {
		InternalSettings internalsettings = new InternalSettings(3);
		simulationsettings.setInternalSettings(internalsettings);
	}
}
