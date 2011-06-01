/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.tree.pckeyword;

/*   PathwayWrapper  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Wrapper for a tree node with a (Pathway Commons) Pathway
 */

import org.vcell.sybil.models.tree.NodeDataWrapper;
import org.vcell.sybil.util.http.pathwaycommons.search.DataSource;
import org.vcell.sybil.util.http.pathwaycommons.search.Pathway;

public class PathwayWrapper extends NodeDataWrapper<Pathway> {

	public PathwayWrapper(Pathway pathways) {
		super(pathways);
	}

	public Pathway data() { return (Pathway) super.data(); }
	public Pathway pathway() { return (Pathway) super.data(); }
	
	public String toString() {
		String primaryId = pathway().primaryId();
		DataSource dataSource = pathway().dataSource();
		String dataSourceText = "";
		if(dataSource != null) {
			dataSourceText = ", " + dataSource.name();
		}
		return pathway().name() + " (" + primaryId + dataSourceText + ")";
	}
	
}
