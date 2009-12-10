package org.vcell.sybil.models.tree.pckeyword;

/*   HitWrapper  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Wrapper for a tree node with a (Pathway Commons) Hit
 */

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.vcell.sybil.models.tree.NodeDataWrapper;
import org.vcell.sybil.util.http.pathwaycommons.search.DataSource;
import org.vcell.sybil.util.http.pathwaycommons.search.Hit;
import org.vcell.sybil.util.http.pathwaycommons.search.Organism;
import org.vcell.sybil.util.text.StringUtil;

public class HitWrapper extends NodeDataWrapper<Hit> {

	public HitWrapper(Hit hit) {
		super(hit);
		append(hit.names(), "name");
		append(hit.synonyms(), "synonym");
		append(hit.descriptions(), "description");
		append(hit.excerpts(), "excerpt");
		DataSource dataSource = hit.dataSource();
		if(dataSource != null) {
			append("Data source: " + dataSource.name() + " (" + dataSource.primaryId() + ")");			
		}
		Organism organism = hit.organism();
		if(organism != null) {
			append("Organism: (" + organism.speciesName() + ", " + organism.commonName() + ", " +
					organism.ncbiOrganismId() + ")");			
		}
		append(new XRefListWrapper(hit.xRefs()));
		append(new PathwayListWrapper(hit.pathways()));
	}

	public Hit data() { return (Hit) super.data(); }
	public Hit hit() { return (Hit) super.data(); }
	
	protected void addIfNotEmpty(List<String> list, String string) {
		if(StringUtil.notEmpty(string)) { list.add(string); }
	}
	
	public String toString() {
		Iterator<String> nameIter = hit().names().iterator();
		String name = "no name";
		if(nameIter.hasNext()) { 
			name = nameIter.next(); 
		} else {
			Iterator<String> synonymIter = hit().synonyms().iterator();
			if(synonymIter.hasNext()) { name = synonymIter.next(); }
		}
		List<String> info = new Vector<String>();
		addIfNotEmpty(info, hit().primaryID());
		addIfNotEmpty(info, hit().entityType());
		Organism organism = hit().organism();
		if(organism != null) { addIfNotEmpty(info, organism.speciesName()); }
		return name + " (" + StringUtil.concat(info, ", ") + ")";
	}
	
}
