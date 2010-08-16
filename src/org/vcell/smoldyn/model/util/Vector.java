package org.vcell.smoldyn.model.util;



/**
 * @author mfenwick
 *
 */
public class Vector {

	private float [] components;
	
	
	/**
	 * @param components
	 */
	public Vector(float [] components) {
		this.components = components;
	}
	
	
	public float [] getComponents() {
		return this.components;
	}
}
