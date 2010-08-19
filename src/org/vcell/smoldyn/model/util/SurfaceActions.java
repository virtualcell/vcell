package org.vcell.smoldyn.model.util;

import org.vcell.smoldyn.simulation.SimulationUtilities;


/**
 * Determines what happens to a molecule of Species (in solution) when it encounters
 * a Surface.  A SurfaceActions includes the face of the surface on which the action 
 * occurs, and a rate for each of reflection, transmission,
 * and absorption.
 * 
 * <ul>
 * <li>What must be specified:  absorption, reflection, and transmission rates for both the front and back faces</li>
 * <li>Absorption: a molecule solution encounters a surface and changes its state from solution to one of the
 * membrane-bound states</li>
 * <li>Transmission: a molecule in solution encounters a surface and passes through it to enter the solution on
 * the opposite side of the surface</li>
 * <li>Reflection: a molecule in solution encounters a surface and remains in solution on the same side as which
 * it started</li>
 * </ul>
 * 
 * Invariants:
 * 		all rates are non-negative
 * 
 * @author mfenwick
 *
 */
public class SurfaceActions {
	private double frontreflectionrate;
	private double frontabsorptionrate;
	private double fronttransmissionrate;
	private double backreflectionrate;
	private double backabsorptionrate;
	private double backtransmissionrate;
	//actions only happen to solution-phase molecules

	
	/**
	 * Initializes a SurfaceActions with identical behavior on both sides.
	 * 
	 * @param reflectionrate -- nonnegative double
	 * @param absorptionrate -- nonnegative double
	 * @param transmissionrate -- nonnegative double
	 */
	public SurfaceActions(double reflectionrate, double absorptionrate, double transmissionrate) {
		this(reflectionrate, absorptionrate, transmissionrate, reflectionrate, absorptionrate, transmissionrate);
	}
	

	/**
	 * Initializes a {@link SurfaceActions} with the specified behavior.
	 * 
	 * @param reflectfront -- nonnegative: probability that a solution molecule hitting the surface from the front is reflected
	 * @param absorbfront -- nonnegative: probability that a solution molecule hitting the surface from the front is absorbed
	 * @param transmitfront -- nonnegative: (....) is transmitted
	 * @param reflectback -- nonnegative: etc.
	 * @param absorbback -- nonnegative: etc.
	 * @param transmitback -- nonnegative: etc.
	 */
	public SurfaceActions(double reflectfront, double absorbfront, double transmitfront, double reflectback,
			double absorbback, double transmitback) {
		SimulationUtilities.checkForNonNegative("negative rates are not permitted for SurfaceActions", reflectfront, absorbfront,
				transmitfront, reflectback, absorbback, transmitback);
		this.frontreflectionrate = reflectfront;
		this.frontabsorptionrate = absorbfront;
		this.fronttransmissionrate = transmitfront;
		this.backreflectionrate = reflectback;
		this.backabsorptionrate = absorbback;
		this.backtransmissionrate = transmitback;
	}
	

	public double getFrontreflectionrate() {
		return frontreflectionrate;
	}

	public double getFrontabsorptionrate() {
		return frontabsorptionrate;
	}

	public double getFronttransmissionrate() {
		return fronttransmissionrate;
	}

	public double getBackreflectionrate() {
		return backreflectionrate;
	}

	public double getBackabsorptionrate() {
		return backabsorptionrate;
	}

	public double getBacktransmissionrate() {
		return backtransmissionrate;
	}

}
