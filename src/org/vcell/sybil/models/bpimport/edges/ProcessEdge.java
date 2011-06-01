/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.bpimport.edges;

/*   ProcessEdge  --- by Oliver Ruebenacker, UCHC --- July 2008 to September 2009
 *   A set representing an SBPAX reaction participation edge
 */

import org.vcell.sybil.models.sbbox.SBBox;

public interface ProcessEdge {
	
	public SBBox.Process process();
	public SBBox.Participant participant();
	public SBBox.Species species();
	public SBBox.Substance entity();
	public SBBox.RDFType entityType();
	public SBBox.Substance substance();
	public SBBox.Location location();
	public SBBox.Stoichiometry stoichiometry();
	public float stoichiometricCoeff();

}
