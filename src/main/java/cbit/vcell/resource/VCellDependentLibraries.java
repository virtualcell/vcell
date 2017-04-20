package cbit.vcell.resource;


/**
 * cygwin library local VCell numerics compiled against  
 * @author gweatherby
 *
 */
public class VCellDependentLibraries extends CygwinDLLorMacosDylib {
	
	@Override
	public String cygwinSourceFilename() throws UnsupportedOperationException {
		return "cygwin1.vcell"; 
	}

}
