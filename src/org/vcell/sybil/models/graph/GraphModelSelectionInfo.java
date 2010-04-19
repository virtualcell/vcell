package org.vcell.sybil.models.graph;

/*   GraphModelSelectionsInfo  --- by Oliver Ruebenacker, UCHC --- January 2009 to March 2010
 *   Extracts resources and statements form selected graph components and
 *   populates a relation map with these and relates resources and statements
 */

 import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompThing;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompRelation;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.rdf.TokenCategory;
import org.vcell.sybil.util.collections.RelationHashMap;
import org.vcell.sybil.util.collections.RelationMap;
import org.vcell.sybil.util.keys.KeyOfTwo;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class GraphModelSelectionInfo implements GraphModel.Listener {

	public static interface Listener { 
		public void dataChanged(); 
		public void selectionChanged(Resource resource);
	}
	
	public class Token extends KeyOfTwo<Resource, TokenCategory> {
		public Token(Resource resource, TokenCategory cat) { super(resource, cat); }
		public Resource resource() { return a(); }
		public TokenCategory category() { return b(); }
	}

	// TODO switch from resources to things
	// TODO switch from statements to sth else
	
	protected GraphModel model;
	protected Set<Resource> resources = new HashSet<Resource>();
	protected RelationMap<Token, Statement> relationMap = new RelationHashMap<Token, Statement>();
	protected Resource selectedResource;
	protected Set<Listener> listeners = new HashSet<Listener>();
	
	public GraphModelSelectionInfo(GraphModel model) { 
		this.model = model;
		this.model.listeners().add(this);
	}
	
	public Set<Listener> listeners() { return listeners; }
	public GraphModel model() { return model; }
	
	public Resource selectedResource() { return selectedResource; }
	
	public void setSelectedResource(Resource resource) {
		selectedResource = resource;
		for(Listener listener : listeners) { listener.selectionChanged(resource); }
	}

	public void updateView() { update(); }
	
	protected RelationMap<Token, Statement> relationMap() { return relationMap; }
	
	public void clear() {
		resources.clear();
		relationMap.clear();
	}

	protected void add(Resource resource, TokenCategory cat, Statement statement) {
		resources.add(resource);
		relationMap.add(new Token(resource, cat), statement);
	}
	
	public Set<Resource> resources() { return resources; }
	
	public Set<Statement> statements(Resource resource, TokenCategory cat) {
		return relationMap.getBSet(new Token(resource, cat));
	}
	
	public Set<Statement> selectedStatements(TokenCategory cat) { return statements(selectedResource, cat); }
	
	public void update() {
		Set<Resource> resources = new HashSet<Resource>();
		Set<Statement> statements = new HashSet<Statement>();
		for(RDFGraphComponent comp : model().selectedComps()) {
			for(NamedThing thing : comp.things()) {
				Resource resource = thing.resource();
				if(comp instanceof RDFGraphCompThing && 
						(model().box().factories().interactionFactory().test(resource) 
						|| model().box().factories().substanceFactory().test(resource))) {
					resources.add(resource);					
				}
				if(comp instanceof RDFGraphCompRelation && 
						model().box().factories().participantFactory().test(resource)) {
					resources.add(resource);					
				}
			}
			statements.addAll(comp.statements());
		}
		clear();
		for(Resource resource : resources) {
			//StmtIterator stmtIter = resource.listProperties();
			StmtIterator stmtIter = 
				model().box().getData().listStatements(resource, null, (RDFNode) null) ;
			while(stmtIter.hasNext()) { add(resource, TokenCategory.SUBJECT, stmtIter.nextStatement()); }
		}
		for(Statement statement : statements) {
			Resource subject = statement.getSubject();
			if(resources.contains(subject)) { add(subject, TokenCategory.SUBJECT, statement); }
			//RDFNode object = statement.getObject();
			//if(object instanceof Resource) { add((Resource) object, TokenCat.OBJECT, statement); }
		}
		for(Listener listener : listeners) { listener.dataChanged(); }
	}

	public void startNewGraph() { clear(); }

}
