package org.vcell.sybil.models.graph;

/*   GraphCreationMethod  --- by Oliver Ruebenacker, UCHC --- April to October 2007
 *   A label to describe the method used to turn an RDF (Jena) model into a graph.
 */

public class GraphCreationMethod {

	protected String name;
	
	public GraphCreationMethod(String name) { this.name = name; }
	public String name() { return name; }
	
}
