package org.vcell.sybil.util.http.uniprot;

/*   UniProtExtractor  --- by Oliver Ruebenacker, UCHC --- January 2010
 *   Some information about UniProt useful for web requests.
 *   Note the complexity of the RDF obtained from UniProt:
 *   some URIs begin with "www", others with "purl", some contain
 *   the suffix ".rdf", others do not.
 */

import org.vcell.sybil.util.http.uniprot.box.UniProtBox;
import org.vcell.sybil.util.http.uniprot.box.imp.UniProtBoxImp;
import org.vcell.sybil.util.text.StringUtil;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class UniProtExtractor {

	protected Model model;
	
	public UniProtExtractor(Model model) { this.model = model; }

	public Model model() { return model; }
	
	public Resource entry(String id) { return model.createResource(UniProtConstants.uri(id)); }
	public String id(Resource entry) { return entry.getLocalName(); }	
	
	public UniProtBox extractBox() {
		UniProtBoxImp box = new UniProtBoxImp();
		
		StmtIterator stmtIter1 = model.listStatements(null, UniProtConstants.replaces, (RDFNode) null);
		while(stmtIter1.hasNext()) {
			Statement statement = stmtIter1.nextStatement();
			RDFNode object = statement.getObject();
			if(object instanceof Resource) {
				String idSubject = id(statement.getSubject());
				String idObject = id((Resource) statement.getObject());
				if(StringUtil.notEmpty(idSubject) && StringUtil.notEmpty(idObject)) {
					UniProtBox.Entry entrySubject = box.entry(idSubject);
					UniProtBox.Entry entryObject = box.entry(idObject);
					box.setReplaces(entrySubject, entryObject);
				}
			}
		}
		StmtIterator stmtIter2 = model.listStatements(null, UniProtConstants.replacedBy, (RDFNode) null);
		while(stmtIter2.hasNext()) {
			Statement statement = stmtIter2.nextStatement();
			RDFNode object = statement.getObject();
			if(object instanceof Resource) {
				String idSubject = id(statement.getSubject());
				String idObject = id((Resource) statement.getObject());
				if(StringUtil.notEmpty(idSubject) && StringUtil.notEmpty(idObject)) {
					UniProtBox.Entry entrySubject = box.entry(idSubject);
					UniProtBox.Entry entryObject = box.entry(idObject);
					box.setReplaces(entryObject, entrySubject);
				}
			}
		} 
		
		StmtIterator stmtIter3 = model.listStatements(null, UniProtConstants.recommendedName, (RDFNode) null);
		if (stmtIter3.hasNext()) {
			while(stmtIter3.hasNext()) {
				Statement statement = stmtIter3.nextStatement();
				Resource entryNode = statement.getSubject();
				RDFNode nameNode = statement.getObject();
				if(nameNode instanceof Resource) {
					Resource nameResource = (Resource) nameNode;
					StmtIterator stmtIter4 = 
						model.listStatements(nameResource, UniProtConstants.fullName, (RDFNode) null);
						// System.out.println("hello world");
					while(stmtIter4.hasNext()) {
						Statement statement2 = stmtIter4.nextStatement();
						RDFNode objectNode = statement2.getObject();
						// System.out.println("hello moon");
						if(objectNode instanceof Literal) {
							String name = ((Literal) objectNode).getLexicalForm();
							UniProtBox.Entry entry = box.entry(UniProtConstants.idFromResource(entryNode));
							System.out.println("UniProtExtractor: name: " + name + "\tid: " + entry.id());
							entry.setRecommendedName(name);
						}
					}
				}
			}
		} else { 
			// sometimes, recommended name is not present for an uniprot id : check the full name
			stmtIter3 = model.listStatements(null, UniProtConstants.fullName, (RDFNode) null);
			while(stmtIter3.hasNext()) {
				Statement statement2 = stmtIter3.nextStatement();
				RDFNode objectNode = statement2.getObject();
				Resource entryNode = statement2.getSubject();
				if(objectNode instanceof Literal) {
					String name = ((Literal) objectNode).getLexicalForm();
					UniProtBox.Entry entry = box.entry(UniProtConstants.idFromResource(entryNode));
					entry.setRecommendedName(name);
					System.out.println("UniProtExtractor: name: " + name + "\tid: " + entry.id());
				}
			}
		}
		
		return box;
	}
	
	public static UniProtBox extractBox(Model model) { return new UniProtExtractor(model).extractBox(); }
	
}
