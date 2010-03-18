package org.vcell.sybil.models.graphcomponents.tag;

/*   SyCoTagGraphCreator  --- by Oliver Ruebenacker, UCHC --- April to October 2007
 *   Creates a graph from a Jena model
 */

import org.vcell.sybil.models.graph.GraphCreationMethod;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

import com.hp.hpl.jena.rdf.model.Model;

public class RDFGraphCompTagCreator<S extends UIShape<S>, G extends UIGraph<S, G>> 
implements RDFGraphCompTag {

	protected Model model;
	protected UIGraph<S, G> graph;
	protected GraphCreationMethod method;
	
	public RDFGraphCompTagCreator(Model model, UIGraph<S, G> graph, GraphCreationMethod method) {
		this.model = model;
		this.graph = graph;
		this.method = method;
	}
	
	public Model model() { return model; }
	public UIGraph<S, G> graph() { return graph; }
	public GraphCreationMethod method() { return method; }
}
