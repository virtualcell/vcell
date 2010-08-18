package org.vcell.smoldyn.simulationsettings;

import org.vcell.smoldyn.simulation.SimulationUtilities;


/**
 * <p>
 * <h3>@author mfenwick</h3>
 * This class contains the general internal settings used by Smoldyn to control
 * such things as accuracy and random number generation.  Smoldyn uses default
 * values for any of these properties which are not specified in a configuration
 * file, so it is permissible to set any or all of these to null, if the methods
 * responsible for printing understand null to mean "don't write anything for this".
 * </p>
 * 
 * <p>
 * <table border="2">
 * <tr><td><h2>Data members:</h2></td><td><i>Smoldyn's default values seem to work fine (for now)</i></td></tr>
 * <tr><td><b>randomseed</b></td><td>the seed for Smoldyn's random number generator.  IMPORTANT:  starting identical simulations with 
 * identical randomseed's will give identical results, whereas changing the randomseed should give different results.  
 * <b>DEFAULT: null</b></td></tr>
 * <tr><td><b>accuracy</b></td><td>the accuracy statement sets which neighboring boxes are checked for potential bimolecular reactions. 
 * Consider the reaction A + B -> C and suppose that A and B are within a binding radius of each other. This reaction will always be 
 * performed if A and B are in the same virtual box. If accuracy is set to at least 3, then it will also occur if A and B are in 
 * nearest-neighbor virtual boxes. If it is at least 7, then the reaction will happen if they are in nearest-neighbor boxes that are 
 * separated by periodic boundary conditions. And if it is 9 or 10, then all edge and corner boxes are checked for reactions, which 
 * means that no potential reactions are overlooked.  <b>DEFAULT: 10</b></td></tr>
 * <tr><td><b>molperbox</b></td><td> Smoldyn sets up boxes of a certain size internally.  Specifying this sets the average number of
 * molecules per box.  NOT USED.    <b>DEFAULT:  irrelevant</b></td></tr>
 * <tr><td><b>boxsize</b></td><td>The direct way of specifying virtual box size.  For more information, consult Smoldyn manual.
 * <b>DEFAULT:  1</td></tr>
 * <tr><td><b>gausstablesize</b></td><td>The size of the table of random numbers that Smoldyn pre-generates. <b>DEFAULT:  4096</b></td></tr>
 * <tr><td><b>epsilon</b></td><td>Something about how close a molecule must be to a Surface. Molecules that are bound to a surface are 
 * given locations that are extremely close to that surface. However, this position does not need to be exactly at the surface, and in 
 * fact it usually cannot be exactly at the surface due to round-off error. The tolerance for how far a surface-bound molecule is allowed 
 * to be away from the surface can be set with the epsilon statement.  NOT USED    <b>DEFAULT: ????</b></td></tr>
 * <tr><td><b>neighbordist</b></td><td>Used for surface molecules diffusing between panels. When a surface-bound molecule diffuses off of 
 * one surface panel, it can sometimes diffuse onto the neighboring surface tile. It does so only if the neighboring panel is declared to 
 * be a neighbor, as described above in the surfaces section, and also the neighbor is within a distance that is set with the neighbor_dist 
 * statement.  NOT USED    <b>DEFAULT: this distance is set to 3 times the longest surface-bound molecule rms step length</b></td></tr>
 * </table>
 * </p>
 * 
 * <span>
 * <h3>Addtional virtual box information</h3>
 * <p>
 * The simulation volume is partitioned into an array of virtual boxes, each of which is the same size and shape. In addition, each box 
 * that is on the edge of the simulation volume actually extends out to infinity in that direction, such that every location in space, 
 * whether in the simulation volume or not, is in some virtual box. These boxes do not affect the performance of the simulation, except 
 * for allowing computational efficiencies that speed it up.</p>
 * <p>
 * Box sizes that are too large will cause slow simulations, but no errors. Warnings that say that there are a lot of molecules or surface 
 * panels in a box are suggestions that smaller boxes may make the simulation run faster, but do not need to be heeded. Box sizes that are 
 * too small may cause errors. Several warnings can be generated for this, including that the diffusive step lengths are larger than the 
 * box size, etc. However, the only warning that really matters is if box sizes are smaller than the largest bimolecular reaction binding 
 * radius. If this happens, some bimolecular reactions are likely to be ignored, which will lead to a too slow reaction rate. If simulation 
 * speed is important, it is a good idea to run a few trial simulations with different box sizes to see which one leads to the fastest 
 * simulations.
 * </p>
 * </span>
 * 
 * Invariants:
 * 		accuracy is non-negative
 * 		gaussian table size and boxwidth are positive
 * 		randomseed is greater than or equal to -1
 * 
 * Value interpretations:
 * 		randomseed >= 0: 
 * 			pass the value of randomseed to Smoldyn
 * 		randomseed == -1:
 * 			pass nothing to Smoldyn for randomseed (allowing Smoldyn to decide what the random number seed should be)
 */
public class InternalSettings{

	private final int randomseed;
	private final double accuracy;
	private final double boxwidth;
	private final int gausstablesize;
//	private final double epsilon = 2.22045e-014;
//	private final double neighbordist;//defaults to three times the maximum rms step length of surface bound molecules
	
	/**
	 * Sets everything but the random number seed to the default value.
	 * 
	 * @param randomseed 
	 */
	public InternalSettings(int randomseed){
		this(randomseed, 10, 1, 4096);
	}
	
	
	/**
	 * Sets the values of internal settings used by Smoldyn.
	 * 
	 * @param randomseed int
	 * @param accuracy double -- between 0 and 10 (inclusive)
	 * @param boxwidth double -- positive
	 * @param gausstablesize positive, power of 2
	 * 
	 */
	public InternalSettings(int randomseed, double accuracy, double boxwidth, int gausstablesize) {
		SimulationUtilities.assertIsTrue("accuracy", accuracy >= 0 && accuracy <= 10);
		SimulationUtilities.checkForPositive("boxsize or gaussian table size", boxwidth, gausstablesize);
		this.randomseed = randomseed;
		this.accuracy = accuracy;
		this.boxwidth = boxwidth;
		this.gausstablesize = gausstablesize;
	}
	

	public Integer getRandomseed() {
		return randomseed;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public double getBoxwidth() {
		return boxwidth;
	}

	public int getGausstablesize() {
		return gausstablesize;
	}
}
