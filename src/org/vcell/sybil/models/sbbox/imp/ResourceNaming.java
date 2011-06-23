package org.vcell.sybil.models.sbbox.imp;

/*   ResourceNaming  --- by Oliver Ruebenacker, UCHC --- September to November 2009
 *   Generate a name for an SBView
 */

import java.util.Iterator;

import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.vcell.sybil.util.keys.KeyOfOne;

public interface ResourceNaming {

	public String createName(Resource resource, Graph model);

	public static class FixedName extends KeyOfOne<String> implements ResourceNaming {
		public FixedName(String name) { super(name); }
		public String name() { return a(); }
		public String createName(Resource resource, Graph model) { return name(); }
	}
	
	public static class StringProperty extends KeyOfOne<URI> implements ResourceNaming {

		public StringProperty(URI property) { super(property); }
		public URI property() { return a(); }

		public String createName(Resource resource, Graph model) {
			String string = null;
			Iterator<Statement> stmtIter = model.match(resource, property(), null);
			while(stmtIter.hasNext()) {
				Value nodeName = stmtIter.next().getObject();
				if(nodeName instanceof Literal) {
					String nameNew = ((Literal) nodeName).stringValue();
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
		public String createName(Resource resource, Graph model) {
			return resource instanceof URI ? ((URI) resource).getLocalName() : null;
		}
	}
	
}
