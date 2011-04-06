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
