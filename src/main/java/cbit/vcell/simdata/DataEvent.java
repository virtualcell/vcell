/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;
import org.vcell.util.document.VCDataIdentifier;
/**
 * Insert the type's description here.
 * Creation date: (3/4/2004 12:13:57 PM)
 * @author: Fei Gao
 */
public class DataEvent extends java.util.EventObject {
	private VCDataIdentifier vcDataIdentifier = null;

/**
 * DataEvent constructor comment.
 * @param source java.lang.Object
 */
public DataEvent(Object source, VCDataIdentifier vcDataIdentifier) {
	super(source);
	setVcDataIdentifier(vcDataIdentifier);
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 2:16:21 PM)
 * @return cbit.vcell.server.VCDataIdentifier
 */
public org.vcell.util.document.VCDataIdentifier getVcDataIdentifier() {
	return vcDataIdentifier;
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 2:16:21 PM)
 * @param newVcDataIdentifier cbit.vcell.server.VCDataIdentifier
 */
private void setVcDataIdentifier(org.vcell.util.document.VCDataIdentifier newVcDataIdentifier) {
	vcDataIdentifier = newVcDataIdentifier;
}
}
