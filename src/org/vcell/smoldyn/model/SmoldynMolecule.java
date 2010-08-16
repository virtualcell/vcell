package org.vcell.smoldyn.model;

import org.vcell.smoldyn.model.util.Point;

/**
 * <p>
 * Smoldyn's conception of a molecule.
 * 
 * <ul id="SmoldynMolecules">
 * <li>in Smoldyn, each individual molecule is represented as a separate point-like particle</li>
 * <li>these particles have no volume, so they do not collide with each other when diffusing</li>
 * <li>orientations and momenta are ignored in Smoldyn</li>
 * <li>each molecule has a molecular species</li>
 * <li>each molecule is allowed to exist in any of 5 states: </li>
 * <ol id="SmoldynMolecularStates">
 * <li>not bound to any surface (called solution state)</li> 
 * <li>bound to the front of a surface</li>
 * <li>bound to the back of a surface</li>
 * <li>bound across a surface in the “up” direction </li>
 * <li>bound across a surface in the “down” direction</li>
 * </ol>
 * <li>molecules may be added to surfaces or to compartments, HOWEVER Smoldyn only sees the initial position, meaning that there is no
 * explicit way to make a molecule "belong" to a surface or compartment (a zero diffusion coefficient would implicitly do that)</li>
 * <li>molecules may be placed 'randomly' (meaning uniformly according to the random number generator) within a region</li>
 * </ul>
 * <p>
 * 
 * <p>
 * Smoldyn's diffusion
 * <ul id="SmoldynDiffusion">
 * <li>molecules in Smoldyn diffuse according to the diffusion characteristics that are entered for the appropriate species and state</li>
 * <ul>
 * <li>both isotropic and anisotropic diffusion, as well as anisotropic drift, are supported</li>
 * </ul>
 * </ul>
 * </p>
 * 
 * @author mfenwick
 *
 */
public interface SmoldynMolecule {

	public Point getPoint();
	
	public int getCount();
}
