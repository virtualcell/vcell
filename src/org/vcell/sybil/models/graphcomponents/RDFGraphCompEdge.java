/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.graphcomponents;

/*   SyCoStatementLike  --- by Oliver Ruebenacker, UCHC --- December 2007 to April 2008
 *   An edge of a graph
 */

public interface RDFGraphCompEdge extends RDFGraphCompRelation {

	public RDFGraphComponent startComp();
	public RDFGraphComponent endComp();
	public RDFGraphCompRelation edgeComp();
	
}
