package org.vcell.sybil.models.graphcomponents;

/*   SyCoResource  --- by Oliver Ruebenacker, UCHC --- March 2008 to November 2009
 *   A component of a graph, corresponding to a single Resource
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTag;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.rdf.schemas.BioPAX2;
import org.vcell.sybil.util.sets.SetOfNone;
import org.vcell.sybil.util.sets.SetOfOne;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class RDFGraphCompSingleThing implements RDFGraphCompThing {

	protected NamedThing thing;
	protected Resource resource;
	protected RDFGraphCompTag tag;
	protected Resource type;
	protected String name;
	protected String label;

	public RDFGraphCompSingleThing(NamedThing thing, RDFGraphCompTag tagNew) { 
		this.thing = thing;
		resource = thing.resource(); 
		tag = tagNew; 
		scanProperties();
	}

	public NamedThing thing() { return thing; }
	public RDFGraphCompTag tag() { return tag; }
	public Set<RDFGraphComponent> dependencies() { return new SetOfNone<RDFGraphComponent>(); }
	public Set<RDFGraphComponent> children() { return new SetOfNone<RDFGraphComponent>(); }
	public Set<Statement> statements() { return new SetOfNone<Statement>(); }
	public String name() { return name; }
	public String label() { return label; }
	public int numberOfResources() { return 1; }
	public Set<NamedThing> things() { return new SetOfOne<NamedThing>(thing); }
	
	public Resource type() { return type; }

	public boolean equals(Object object) {
		if(object instanceof RDFGraphCompThing) {
			return ((RDFGraphCompThing)object).thing().equals(this.thing());
		} else {
			return false;
		}
	}

	public int hashCode() { return thing().hashCode(); }

	protected String shortString(RDFNode node) {
		if(node instanceof Resource) { return ((Resource) node).getLocalName(); }
		else if(node instanceof Literal) { return ((Literal) node).getLexicalForm(); }
		else { return null; }
	}
	
	protected void scanProperties() {
		StmtIterator stmtIter = resource.listProperties();
		RDFNode nameNode = null;
		RDFNode shortNameNode = null;
		Set<RDFNode> synonymNodes = new HashSet<RDFNode>();
		while(stmtIter.hasNext()) {
			Statement statement = stmtIter.nextStatement();
			Property predicate = statement.getPredicate();
			RDFNode object = statement.getObject();
			if(predicate.equals(RDF.type)) { 
				if(object instanceof Resource) { type = (Resource) object; }
			} else if(predicate.equals(BioPAX2.NAME)) {
				nameNode = object;
			} else if(predicate.equals(BioPAX2.SHORT_NAME)) {
				shortNameNode = object;
			} else if(predicate.equals(BioPAX2.SYNONYMS)) {
				synonymNodes.add(object);
			}
		}
		name = shortString(nameNode);
		for(RDFNode synonymNode : synonymNodes) {
			if(name == null) { name = shortString(synonymNode); }
			else{ break; }
		}
		if(name == null) { name = resource.getLocalName(); }
		label = shortString(shortNameNode);
		if(label == null) { label = name; }
	}
	
}
