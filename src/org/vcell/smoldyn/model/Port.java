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

	private String name;
	Surface surface;
	FaceType face;
	
	
	/**
	 * @param name
	 * @param surface
	 * @param face
	 * @throws RuntimeException since Ports are not understood
	 */
	public Port(String name, Surface surface, FaceType face) {
		SimulationUtilities.throwRuntimeException("ports are not understood and can not be safely used");
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

