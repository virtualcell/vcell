package org.vcell.smoldyn.model;

import org.vcell.smoldyn.model.util.FaceType;
import org.vcell.smoldyn.simulation.SimulationUtilities;


/**
 * Ports are used by Smoldyn to interact with other simulators.  Molecules
 * can be sent from Smoldyn simulations to another simulator using a port.
 * WARNING:  it is not currently understood how Ports work.
 * 
 * @author mfenwick
 *
 */
public class Port {

	private final String name;
	private final Surface surface;
	private final FaceType face;
	
	
	/**
	 * @param name
	 * @param surface
	 * @param face
	 */
	public Port(String name, Surface surface, FaceType face) {
		SimulationUtilities.throwIllegalArgumentException("ports are not understood and can not be safely used");
		this.name = name;
		this.surface = surface;
		this.face = face;
	}
	
	
	public String getName() {
		return name;
	}
	
	public Surface getSurface() {
		return surface;
	}
	
	public FaceType getFace() {
		return face;
	}
}

