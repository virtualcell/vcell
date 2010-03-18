package org.vcell.sybil.models.graph;

/*   SybilGraphFactory  --- by Oliver Ruebenacker, UCHC --- April 2007 to March 2010
 *   Creates a graph from a Jena model
 */

import org.vcell.sybil.models.graph.SybilGraphFactory;
import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTagCreator;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.SBWrapper;
import org.vcell.sybil.models.views.SBView;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class SybilGraphFactory {

	public static <S extends UIShape<S>, G extends UIGraph<S, G>> 
	void createGraph(UIGraph<S, G> graph, SBView view) throws InterruptedException {
		SBBox box = view.box();
		Model model = box.data();
		graph.startNewGraph();
		StmtIterator stmtIter = model.listStatements();
		long progress = 0;
		RDFGraphCompTagCreator<S, G> tag = new RDFGraphCompTagCreator<S, G>(model, graph, 
				new GraphCreationMethod("Sybil Graph Factory"));
		while(stmtIter.hasNext()) {
			Statement statement = stmtIter.nextStatement();
			RDFNode theObject = statement.getObject();
			if(theObject.isResource()) {
				Resource subject = statement.getSubject();
				Resource object = (Resource) theObject;
				graph.addEdge(box, new SBWrapper(box, subject), new SBWrapper(box, object), statement, tag);									
			}
			++progress;
		}
	}

}