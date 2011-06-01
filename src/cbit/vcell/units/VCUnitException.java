/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
