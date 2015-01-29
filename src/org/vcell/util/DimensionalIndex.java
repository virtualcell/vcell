package org.vcell.util;

/**
 * encapsulate converting 3 dimension data into single access index
 */
@SuppressWarnings("serial")
public class DimensionalIndex extends ISize {
	/**
	 * product of x and y dimensions
	 */
	final int xy;

	public DimensionalIndex(int newX, int newY, int newZ) {
		super(newX, newY, newZ);
		xy = getX() *getY( );
	}

	public DimensionalIndex(String newX, String newY, String newZ) {
		super(newX, newY, newZ);
		xy = getX() *getY( );
	}
	/**
	 * create from existing {@link ISize}
	 * @param source
	 */
	public DimensionalIndex(ISize source) {
		this(source.getX(),source.getY(),source.getZ());
	}
	
	/**
	 * calculate index for specified values
	 * @param xValue
	 * @param yValue
	 * @param zValue
	 * @return unique index based on size, between 0 and {@link #getXYZ()}
	 * @throws RuntimeException if any index out of range
	 */
	public int rollup(int xValue, int  yValue, int zValue) {
		check(xValue,super.getX(),'x');
		check(yValue,super.getY(),'y');
		check(zValue,super.getZ(),'z');
		return xValue + yValue * getX( ) + zValue * xy; 
	}
	
	/**
	 * check value between zero and limit 
	 * @param input
	 * @param limit
	 * @param dim e.g. 'x'
	 * @throws RuntimeException if out of bounds
	 */
	private void check(int input, int limit, char dim) {
		if ((input < 0) || input >= limit) {
			throw new RuntimeException("invalid index " + input + " for dimension "  + dim 
					+ ", must be between 0 and " + limit);
		}
		
	}
	
	
	
	
	

}
