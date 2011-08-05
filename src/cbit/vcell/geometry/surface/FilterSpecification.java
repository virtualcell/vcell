/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.surface;

/**
 * Insert the type's description here.
 * Creation date: (5/6/2004 10:53:03 AM)
 * @author: Jim Schaff
 */
public class FilterSpecification {
	public final double Kpb;
	public final double Ksb;
	public final double Dpb;
	public final double Dsb;
/**
 * FilterSpecification constructor comment.
 */
public FilterSpecification(double argKpb, double argKsb, double argDpb, double argDsb) {
	if (0 > argKpb || argKpb > 2){
		throw new IllegalArgumentException("passband frequency must be between 0 and 2");
	}else if (argKpb > argKsb){
		throw new IllegalArgumentException("passband frequency must be less than stopband frequency");
	}else if (argKsb > 2) {
		throw new IllegalArgumentException("stopband frequency must be less than 2");
	}
	// Make sure 0 < passBandRipple
	if (0 >= argDpb) {
		throw new IllegalArgumentException("passband ripple must be greater than 0");
	}
	// Make sure 0 < stopBandRipple < 1
	if (0 >= argDsb || argDsb > 1) {
		throw new IllegalArgumentException("stopband ripple must be between 0 and 1");
	}
	Kpb = argKpb;
	Ksb = argKsb;
	Dpb = argDpb;
	Dsb = argDsb;
}
/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 11:02:35 AM)
 * @return double
 */
public final double getDpb() {
	return Dpb;
}
/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 11:02:35 AM)
 * @return double
 */
public final double getDsb() {
	return Dsb;
}
/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 11:02:35 AM)
 * @return double
 */
public final double getKpb() {
	return Kpb;
}
/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 11:02:35 AM)
 * @return double
 */
public final double getKsb() {
	return Ksb;
}
}
