package cbit.vcell.resource;


/**
 * cygwin library packaged with BioNetGen (run_network) 
 * @author gweatherby
 *
 */
public class BioNetGenCygwinDLL extends CygwinDLL {

	@Override
	public String version() throws UnsupportedOperationException {
		return "cygwin1.bionetgen"; 
	}


}
