package cbit.vcell.resource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * cygwin library local VCell numerics compiled against  
 * @author gweatherby
 *
 */
public class VCellCygwinDLL extends CygwinDLL {
	
	/**
	 * associated library names
	 */
	protected static final String[] LIB_64_NAMES = { "cyggcc_s-seh-1.dll","cygstdc++-6.dll","cyggfortran-3.dll", "cygquadmath-0.dll"};
	protected static final String[] LIB_32_NAMES = { "cyggcc_s-1.dll","cygstdc++-6.dll"};


	@Override
	public String version() throws UnsupportedOperationException {
		return "cygwin1.vcell"; 
	}


	@Override
	protected Collection<String> get32bit() {
		return Collections.unmodifiableCollection(Arrays.asList(LIB_32_NAMES));
	}


	@Override
	protected Collection<String> get64bit() {
		return Collections.unmodifiableCollection(Arrays.asList(LIB_64_NAMES));
	}

}
