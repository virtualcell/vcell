package cbit.vcell.opt;

import org.vcell.util.Matchable;

public interface Weights extends Matchable, Cloneable {
	/**
	 * returns number of total weights
	 */
	int getNumWeights();
	public Weights clone();
}
