package cbit.vcell.solvers.mb;

/**
 * Continuous index of unique points.
 * Lowest index is 0; highest is {@link #size() - 1}
 * @author GWeatherby
 *
 */
public interface PointIndex {

	/**
	 * @return size (number of points)
	 */

	int size( );
	/**
	 * @throws IndexOutOfBoundsException
	 */
	Vect3Didx lookup(int index);


}
