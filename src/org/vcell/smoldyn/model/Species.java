package org.vcell.smoldyn.model;

import org.vcell.smoldyn.model.util.DiffusionDriftMatrix;
import org.vcell.smoldyn.simulation.SimulationUtilities;



/**
 * A species is a group of molecules which are treated as having a certain identity by Smoldyn, upon which the behavior of the molecule is
 * based.  A good way to think of a species is as a chemical compound.  A molecule may change state (be adsorbed or desorbed) without
 * changing its species.  Smoldyn determines molecular behavior on the basis of species.
 * 
 * 
 *  * A {@link Species} may move between several states defined by Smoldyn.  The states are:
 * solution, and four membrane-bound states, corresponding to transmembrane states
 * and sides of the membrane.  The diffusion properties of a Species depend on the
 * state it is in.  These properties can be specified with a simple, isotropic
 * diffusion coefficient, or an anisotropic {@link DiffusionDriftMatrix}, and optionally also
 * a DiffusionDriftMatrix to specify anisotropic drift.
 * 
 * Invariants:
 * 		name and name prefix are not null
 * 		all diffusion constants are greater than or equal to 0
 * 
 * @author mfenwick
 *
 */
public class Species {

	private final String name;
	private final Compartment compartment;
	private double solutiondiffusion = 0;
	private double updiffusion = 0;
	private double downdiffusion = 0;
	private double frontdiffusion = 0;
	private double backdiffusion = 0;
	
	
	/**
	 * Instantiates a new species with the given name and Compartment.  The compartment indicates the volume region to which the species
	 * is confined.  Presumably this is implemented (when converting to Smoldyn) by means of something like a namespace/prefix which 
	 * will probably correspond to the Compartment's name.
	 * 
	 * @param name String -- must not be null
	 * @param compartment Compartment -- not null
	 */
	public Species(String name, Compartment compartment) {
		SimulationUtilities.checkForNull("species name", name, compartment);
		this.name = name;
		this.compartment = compartment;
	}

	
	public String getName() {
		return this.name;
	}


	/**
	 * Sets the isotropic solution diffusion constant.
	 * 
	 * @param solutiondiffusion double -- not negative
	 */
	public void setSolutiondiffusion(double solutiondiffusion) {
		SimulationUtilities.checkForNonNegative("diffusion constant", solutiondiffusion);
		this.solutiondiffusion = solutiondiffusion;
	}


	/**
	 * Sets the isotropic membrane-up diffusion constant.
	 * 
	 * @param updiffusion double -- not negative
	 */
	public void setUpdiffusion(double updiffusion) {
		SimulationUtilities.checkForNonNegative("diffusion constant", updiffusion);
		this.updiffusion = updiffusion;
	}


	/**
	 * Sets the isotropic membrane-down diffusion constant.
	 * 
	 * @param downdiffusion double -- not negative
	 */
	public void setDowndiffusion(double downdiffusion) {
		SimulationUtilities.checkForNonNegative("diffusion constant", downdiffusion);
		this.downdiffusion = downdiffusion;
	}


	/**
	 * Sets the isotropic membrane-front diffusion constant.
	 * 
	 * @param frontdiffusion double -- not negative
	 */
	public void setFrontdiffusion(double frontdiffusion) {
		SimulationUtilities.checkForNonNegative("diffusion constant", frontdiffusion);
		this.frontdiffusion = frontdiffusion;
	}


	/**
	 * Sets the isotropic membrane-back diffusion constant.
	 * 
	 * @param backdiffusion double -- not negative
	 */
	public void setBackdiffusion(double backdiffusion) {
		SimulationUtilities.checkForNonNegative("diffusion constant", backdiffusion);
		this.backdiffusion = backdiffusion;
	}


	public Compartment getCompartment() {
		return this.compartment;
	}


	public double getSolutiondiffusion() {
		return solutiondiffusion;
	}


	public double getUpdiffusion() {
		return updiffusion;
	}


	public double getDowndiffusion() {
		return downdiffusion;
	}


	public double getFrontdiffusion() {
		return frontdiffusion;
	}


	public double getBackdiffusion() {
		return backdiffusion;
	}
	
	
	/**
	 * fsoln and bsoln are both solution states, they're just relative to a surface
	 * 	.... so, a molecule can not be in state 'fsoln', but a surface can say that
	 * 	a molecule is in 'fsoln'
	 * 
	 * @author mattf
	 *
	 */
	public static enum StateType {
		front,
		back,
		up,
		down,
		solution,
//		fsoln,
//		bsoln,
	}
}
