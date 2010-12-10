package org.vcell.sybil.models.io.selection;

/*   ModelSelectorSimple  --- by Oliver Ruebenacker, UCHC --- November 2009
 *   Extract statements of a model based on selected resources
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.rdf.schemas.BioPAX2;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ModelSelectorSimple implements ModelSelector {

	public Model createSelection(Model model, Set<Resource> selectedResources) {
		Model modelSelection = ModelFactory.createDefaultModel();
		Set<Resource> entitiesNew = new HashSet<Resource>();
		entitiesNew.addAll(selectedResources);
		long nComplexes = 0;
		long nComplexesOld = 0;
		do {
			Set<Resource> complexes = new HashSet<Resource>();
			for(Resource entity : entitiesNew) {
				ResIterator partIter = model.listResourcesWithProperty(BioPAX2.PHYSICAL_ENTITY, entity);
				while(partIter.hasNext()) {
					Resource part = partIter.nextResource();
					BioPAX2.schema.listResourcesWithProperty(RDFS.subPropertyOf, BioPAX2.PARTICIPANTS);
					ResIterator interIter = model.listResourcesWithProperty(BioPAX2.COMPONENTS, part);
					while(interIter.hasNext()) {
						complexes.add(interIter.nextResource());
					}					
				}
			}
			entitiesNew.addAll(complexes);
			nComplexesOld = nComplexes;
			nComplexes = complexes.size();
		} while(nComplexes > nComplexesOld);
		Set<Resource> conversions = new HashSet<Resource>();
		for(Resource entity : entitiesNew) {
			ResIterator partIter = model.listResourcesWithProperty(BioPAX2.PHYSICAL_ENTITY, entity);
			while(partIter.hasNext()) {
				Resource part = partIter.nextResource();
				ResIterator partPropIter = 
					BioPAX2.schema.listResourcesWithProperty(RDFS.subPropertyOf, BioPAX2.PARTICIPANTS);
				while(partPropIter.hasNext()) {
					Resource partPropNode = partPropIter.nextResource();
					Property partProp = model.createProperty(partPropNode.getURI());
					ResIterator interIter = model.listResourcesWithProperty(partProp, part);
					while(interIter.hasNext()) {
						conversions.add(interIter.nextResource());
					}					
				}
			}
		}
		entitiesNew.addAll(conversions);
		Set<Resource> controlls = new HashSet<Resource>();
		for(Resource entity : entitiesNew) {
			ResIterator contIter = model.listResourcesWithProperty(BioPAX2.CONTROLLED, entity);
			while(contIter.hasNext()) {
				controlls.add(contIter.nextResource());				
			}
		}
		entitiesNew.addAll(controlls);
		Set<Resource> entities = new HashSet<Resource>();
		while(!entitiesNew.isEmpty()) {
			Set<Resource> entitiesNewNew = new HashSet<Resource>();
			for(Resource entity : entitiesNew) {
				modelSelection.add(model.listStatements(entity, RDF.type, (RDFNode) null));
				StmtIterator stmtIter = model.listStatements(entity, null, (RDFNode) null);
				while(stmtIter.hasNext()) {
					Statement stmt = stmtIter.nextStatement();
					modelSelection.add(stmt);
					RDFNode objectNode = stmt.getObject();
					if(objectNode instanceof Resource) {
						Resource object = (Resource) objectNode;
						if(!entities.contains(object)) {
							entitiesNewNew.add(object);
						}
					}
				}
			}
			entities.addAll(entitiesNew);
			entitiesNew = entitiesNewNew;
		}
		StmtIterator stmtIter = model.listStatements();
		while(stmtIter.hasNext()) {
			Statement statement = stmtIter.nextStatement();
			if(entities.contains(statement.getSubject())) {
				modelSelection.add(statement);
			}
		}
		return modelSelection;
	}
	
}
