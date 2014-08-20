package cbit.vcell.math;

/**
 * {@link Commented} which produces VCML
 * @author gweatherby
 *
 */
public interface VCMLProvider extends Commented {
	/**
	 * @return VCML representation of object
	 * @throws MathException 
	 */
	public String getVCML( ) throws MathException;
}
