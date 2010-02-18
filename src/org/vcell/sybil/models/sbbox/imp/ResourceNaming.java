package org.vcell.sybil.models.sbbox.imp;

/*   ResourceNaming  --- by Oliver Ruebenacker, UCHC --- September to November 2009
 *   Generate a name for an SBView
 */

import org.vcell.sybil.util.keys.KeyOfOne;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public interface ResourceNaming {

	public String createName(Resource resource, Model model);

	public static class FixedName extends KeyOfOne<String> implements ResourceNaming {
		public FixedName(String name) { super(name); }
		public String name() { return a(); }
		public String createName(Resource resource, Model model) { return name(); }
	}
	
	public static class StringProperty extends KeyOfOne<Property> implements ResourceNaming {

		public StringProperty(Property property) { super(property); }
		public Property property() { return a(); }

		public String createName(Resource resource, Model model) {
			String string = null;
			StmtIterator stmtIter = model.listStatements(resource, property(), (RDFNode) null);
			while(stmtIter.hasNext()) {
				RDFNode nodeName = stmtIter.nextStatement().getObject();
				if(nodeName instanceof Literal) {
					String nameNew = ((Literal) nodeName).getLexicalForm();
					if(nameNew != null && nameNew.length() > 0) {
						string = nameNew;
						break;
					}
				}
			}
			return string;
		}
		
	}
	
	public static class LocalName implements ResourceNaming {
		public String createName(Resource resource, Model model) {
			return resource.isURIResource() ? resource.getLocalName() : null;
		}
	}
	
}
