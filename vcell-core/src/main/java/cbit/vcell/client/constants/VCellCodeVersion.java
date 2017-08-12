package cbit.vcell.client.constants;



/**
 * model VCell release numbers
 * @author gweatherby
 */
public class VCellCodeVersion implements Comparable<VCellCodeVersion>{
	public static final int CURRENT_MAJOR = 6;
	public static final int CURRENT_MINOR = 1;
	
	public static final VCellCodeVersion CURRENT = new VCellCodeVersion(CURRENT_MAJOR,CURRENT_MINOR);
	
	final Integer major;
	final Integer minor;
	
	/**
	 * explicit set major minor
	 * @param major
	 * @param minor
	 */
	public VCellCodeVersion(int major, int minor) {
		super();
		this.major = major;
		this.minor = minor;
	}
	
	/***
	 * convenience method; equivalent to {@link #compare(int, int)}
	 * @param major
	 * @param minor
	 * @return {@link #compare(int, int)} value 
	 */
	public int compare(int major, int minor) {
		return compareTo(new VCellCodeVersion(major, minor));
	}

	/**
	 * compare with other, using {@link #major} first, then {@link #minor}
	 */
	@Override
	public int compareTo(VCellCodeVersion other) {
		int majorCompare = major.compareTo(other.major);
		if (majorCompare == 0) {
			return minor.compareTo(other.minor);
		}
		return majorCompare;
	}

	@Override
	public String toString() {
		return "VCellCodeVersion" + major + '.' + minor;
	}
}
