package cbit.vcell.resource;


/**
 * cygwin library local VCell numerics compiled against  
 * @author gweatherby
 *
 */
public class VCellCygwinDLL extends CygwinDLL {
	
	@Override
	public String version() throws UnsupportedOperationException {
		return "cygwin1.vcell"; 
	}

}
