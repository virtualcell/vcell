package cbit.vcell.solvers.mb;

import org.vcell.vis.core.Vect3D;

/**
 * Vect3D that implements Comparable. NaN values
 * are normalized upon construction to ensure logical
 * consistency between, {@link Object#equals(Object)}, ==, and {@link Comparable#compareTo(Object)}
 */
public class Vect3Didx extends Vect3D implements Comparable<Vect3Didx> {

	int index;

	Vect3Didx(double x, double y, double z, int index){
		super(normalize(x),normalize(y),normalize(z));
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	/**
	 * normalize all NaN values
	 * @param d
	 * @return d or {@link Double#isNaN()}
	 */
	private static double normalize(double d) {
		if (!Double.isNaN(d)) {
			return d;
		}
		return Double.NaN;
	}

	/**
	 * order by x, then  y, then z
	 */
	@Override
	public int compareTo(Vect3Didx rhs) {
		int rval = Double.compare(x,rhs.x);
		if (rval != 0) {
			return rval;
		}
		rval = Double.compare(y,rhs.y);
		if (rval != 0) {
			return rval;
		}
		return Double.compare(z,rhs.z);
	}

	@Override
	public int hashCode() {
		return Double.hashCode(x) ^ Double.hashCode(y) ^ Double.hashCode(z);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return compareTo((Vect3Didx) obj)  == 0;
	}


}