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

/*   PathwayListWrapper  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Wrapper for a tree node with a (Pathway Commons) Pathways list
 */

import java.util.List;
import org.vcell.sybil.models.tree.NodeDataWrapper;
import org.vcell.sybil.util.http.pathwaycommons.search.Pathway;
import org.vcell.sybil.util.text.NumberText;

public class PathwayListWrapper extends NodeDataWrapper<List<Pathway>> {

	public PathwayListWrapper(List<Pathway> pathways) {
		super(pathways);
		for(Pathway pathway : pathways) { append(new PathwayWrapper(pathway)); }
	}

	@Override
	public List<Pathway> data() { return super.data(); }
	public List<Pathway> pathways() { return super.data(); }
	
	@Override
	public String toString() {
		return NumberText.soMany(pathways().size(), "pathway");
	}
	
}
