package org.vcell.smoldyn.simulation;


import java.util.HashMap;

import org.vcell.smoldyn.model.Geometryable;
import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.model.Species;
import org.vcell.smoldyn.model.Surface;
import org.vcell.smoldyn.simulationsettings.SimulationSettings;
import org.vcell.smoldyn.simulationsettings.util.SpeciesGraphics;
import org.vcell.smoldyn.simulationsettings.util.SurfaceGraphics;


/**
 * The general mapping of a Smoldyn simulation.  A simulation is broken down into
 * a {@link Model} and a {@link SimulationSettings}.  The Model has things such as Molecules,
 * {@link Geometryable}, and Reactions, while the SimulationSettings has things such as events,
 * display information, and timing settings.  Some information is split across
 * the Model and the SimulationSettings, which belongs together according to 
 * Smoldyn.  One example is display information for surfaces.  The Simulation
 * maintains a HashMap, mapping the Surface, owned by the Model, to a SurfaceGraphics, 
 * which is owned by the SimulationSettings.  This keeps the Model ignorant of 
 * irrelevant information (display information), while maintaining relationships
 * important to Smoldyn.
 * 
 * <p>
 * General Smoldyn simulation information:
 * </p>
 * <table id="SmoldynTime" border="2">
 * <tr><th>step</th><th>description</th><th>details</th></tr>
 * <tr><td>0</td><td>time = t</td><td>&nbsp;</td></tr>
 * <tr><td>1</td><td>observe and manipulate system</td><td>&nbsp;</td></tr>
 * <tr><td>2</td><td>graphics are drawn</td><td>&nbsp;</td></tr>
 * <tr><td>3</td><td>molecules diffuse</td><td>The evolution over a finite time step starts by diffusing all mobile molecules. </td></tr>
 * <tr><td>4</td><td>desorption and surface-state transitions</td><td>&nbsp;</td></tr>
 * <tr><td>5</td><td>surface or boundary interactions</td><td>These are reflected, transmitted, absorbed, or transported as needed.</td></tr>
 * <tr><td>6</td><td>0th order reactions</td><td>&nbsp;</td></tr>
 * <tr><td>7</td><td>1st order reactions</td><td>Reactants are removed from the system as soon as they react and products are not added into 
 * the system until all reactions have been completed. </td></tr>
 * <tr><td>8</td><td>2nd order reactions</td><td>This prevents reactants from reacting twice during a time step and it prevents 
 * products from one reaction from reacting again during the same time step.</td></tr>
 * <tr><td>9</td><td>reaction products are added to system</td><td>&nbsp;</td></tr>
 * <tr><td>10</td><td>surface interactions of reaction products</td><td>As it is possible for reactions to produce molecules that are 
 * across internal surfaces or outside the system walls, those products are then reflected back into the system.</td></tr>
 * <tr><td>11</td><td>t += (delta)t; goto 0</td><td>system has fully evolved by one time step</td></tr>
 * </table>
 * </p>
 * 
 * @author mfenwick
 *
 */
public class Simulation {

	private Model model;
	private SimulationSettings simulationsettings;
	private HashMap<Surface, SurfaceGraphics> surfacegraphics = new HashMap<Surface, SurfaceGraphics>();
	private HashMap<Species, SpeciesGraphics> speciesgraphics = new HashMap<Species, SpeciesGraphics>();
	
	
	/**
	 * Instantiates a new Simulation, composed of a Model and a SimulationSettings.
	 * A Simulation corresponds to a Smoldyn configuration file.
	 * 
	 * @param model
	 * @param simulationsettings
	 */
	public Simulation(Model model, SimulationSettings simulationsettings) {
		this.model = model;
		this.simulationsettings = simulationsettings;
	}
	
	
	/**
	 * Return the SurfaceGraphics associated with a Surface.
	 * @param surface
	 * @return SurfaceGraphics
	 */
	public SurfaceGraphics getSurfaceGraphics(Surface surface) {
		return this.surfacegraphics.get(surface);
	}
	
	/**
	 * Register display settings for a surface with the Simulation.  A Model has Surfaces,
	 * but these Surfaces do not know anything about their display settings.  To give a 
	 * Surface display settings, a Simulation keeps a hash mapping:  the keys are Surfaces,
	 * and the values are SurfaceGraphics.  The SurfaceGraphics instance is also registered
	 * with the SimulationSettings.
	 * @param surface
	 * @param surfacegraphics
	 */
	public void addSurfaceGraphics(Surface surface, SurfaceGraphics surfacegraphics) {
		this.simulationsettings.addSurfaceGraphics(surfacegraphics);
		this.surfacegraphics.put(surface, surfacegraphics);
	}
	
	
	public SpeciesGraphics getSpeciesGraphics(Species species) {
		return this.speciesgraphics.get(species);
	}
	
	public void addSpeciesGraphics(Species species, SpeciesGraphics speciesgraphics) {
		this.simulationsettings.addSpeciesGraphics(speciesgraphics);
		this.speciesgraphics.put(species, speciesgraphics);
	}
	

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	
	public SimulationSettings getSimulationSettings() {
		return simulationsettings;
	}
	
	public void setSimulationSettings(SimulationSettings simulation) {
		this.simulationsettings = simulation;
	}

}
