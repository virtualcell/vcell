/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.miriam;

/*   RefGroup  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   A group of MIRIAM references (data type plus value, e.g. UniProt P00533)
 */

import java.util.Set;

import org.vcell.sybil.rdf.RDFBag;

public interface RefGroup extends RDFBag {
	public RefGroup add(MIRIAMRef ref);
	public RefGroup remove(MIRIAMRef ref);
	public boolean contains(MIRIAMRef ref);
	public Set<MIRIAMRef> refs();
	public RefGroup removeAll();
	public void delete();
}
