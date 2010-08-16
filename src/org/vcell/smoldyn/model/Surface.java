package org.vcell.smoldyn.model;


import org.vcell.smoldyn.model.util.Panel;
import org.vcell.smoldyn.simulation.SimulationUtilities;

import java.util.HashSet;



/**
 * A surface is the Smoldyn equivalent of something like a membrane.  However, in Smoldyn,
 * a surface does not have to physically interact with anything in any way (although it
 * may).
 * A surface has a name, and is composed of panels.
 * 
 * 
 * <p>
 * <h2>Smoldyn Surfaces</h2>
 * <ul>
 *  <li>surface basics
 *   <ul>
 *    <li>Smoldyn supports disjoint surfaces</li>
 *    <li>each Smoldyn surface is comprised of many panels which have simple geometries</li>
 *    <li>each surface includes a set of rules that dictate how molecules interact with it</li>
 *    <li>user may specify behavior of molecules that diffuse into it from solution as well as bound molecules</li>
 *    <li>all panels on a given surface interact identically</li>
 *    <li>surfaces have a front and a back face, which may interact differently from each other (but identically with respect
 *    to panel)</li>
 *   </ul>
 *  </li>
 *  <li>surface interactions
 *   <ul>
 *    <li>actions set the rules that molecules follow when they interact with a surface</li>
 *    <li>there are three basic actions: reflection, transmission, and absorption</li>
 *    <li>“jumping” is also possible, such that a molecule can be magically transported to a remote destination</li>
 *    <li>it is also possible to cause a molecule to change species when it interacts with a surface</li>
 *   </ul>
 *  </li>
 *  <li>panels
 *   <ul>
 *    <li>basic panel shapes: triangle, rectangle, sphere, hemisphere, disk, cylinder</li>
 *    <li>panels have names</li>
 *    <li>a default name is assigned if left unspecified</li>
 *    <li>the names are used for jump surfaces and diffusion of surface-bound molecules</li>
 *    <li>for a surface to work in a consistent manner, it is worth making sure that all panel front sides face the same way
 *    <li>QUESTION:  how is this done?</li>
 *    <li>As far as the simulation is concerned, a panel is mathematically exact</li>
 *    <li>If multiple panels are exactly coincident, then only the one that is defined last in the configuration file is in effect</li>
 *   </ul>
 *  </li>
 *  <li>Membrane-bound molecules
 *   <ul>
 *    <li>surface-bound molecules can be on the front or back, or trans-membrane (either “up” or “down”)</li>
 *    <li>also, surfaces see solution molecules as either “front solution” or “back solution”</li>
 *    <li>a rate statement is used for transition rates for surface-bound molecules: specifying the rate at which a solution-state 
 *    molecular species is adsorbed onto a surface, or for the release rate, from surface to solution</li>
 *    <li>Surface-bound molecules have a diffusion coefficient which allows them to diffuse within the plane of the surface</li>
 *    <li>QUESTION:  what does that mean with respect to non-planar surfaces?</li>
 *    <li>diffusion between panels must be explicity specified</li>
 *    <li>molecules can only diffuse off of edges of panels</li>
 *   </ul>
 *  </li>
 *  <li>collisions with surfaces
 *   <ul>
 *    <li>indirect collisions: when molecule starts and ends time-step on same side of surface</li>
 *    <li>direct collisions: when molecules starts and ends time-step on opposite sides of surface</li>
 *    <li>after a direct collision, if the action is reflect the molecule is put back on the same side of the surface as it started</li>
 *    <li>Smoldyn ignores indirect collisions</li>
 *   </ul>
 *  </li>
 * </ul>
 * 
 * Invariants:
 * 		name is not null
 * 		name may not be changed
 * 		a panel is contained at most once
 *  
 * @author mfenwick
 *
 */
public class Surface {
	private final String name;
	private final HashSet<Panel> panels = new HashSet<Panel>();
	

	/**
	 * Instantiates a new Surface with the specified name, which must be non-null.
	 * 
	 * @param name
	 * @throws NullPointerException if name is null
	 * 
	 */
	public Surface(String name) {
		SimulationUtilities.checkForNull("surface name", name);
		this.name = name;
	}

	
	public String getName() {
		return name;
	}
	
	/**
	 * Adds a panel to the current surface.  A panel is an interface for the six different shape types that 
	 * Smoldyn supports.
	 * @param panel Panel
	 */
	public void addPanel(Panel panel) {
		SimulationUtilities.checkForNull("panel", panel);
		panel.setSurface(this);
		this.panels.add(panel);
	}

	public Panel [] getPanels() {
		return panels.toArray(new Panel [panels.size()]);
	}
	
}
