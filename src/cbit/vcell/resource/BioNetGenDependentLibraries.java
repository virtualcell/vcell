package cbit.vcell.resource;


/**
 * cygwin library packaged with BioNetGen (run_network) 
 * @author gweatherby
 *
 */
public class BioNetGenDependentLibraries extends CygwinDLLorMacosDylib {

	@Override
	public String cygwinSourceFilename() throws UnsupportedOperationException {
		return "cygwin1.bionetgen"; 
	}


}
