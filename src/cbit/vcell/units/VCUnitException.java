package cbit.vcell.units;
/**
 Generic wrapper around all the exceptions thrown by the ucar.units package. It extends RuntimeException for convenience.
 
 * Creation date: (3/3/2004 12:28:32 PM)
 * @author: Rashad Badrawi
 */
public class VCUnitException extends RuntimeException {

public VCUnitException() {
	super();
}


public VCUnitException(String s) {
	super(s);
}
}