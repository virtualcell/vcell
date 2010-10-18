package org.vcell.sybil.models.sbbox.imp;

/*   RDFTypeImp  --- by Oliver Ruebenacker, UCHC --- July to December 2009
 *   A view of a resource representing an OWL class
 */

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.MutableRDFType;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.models.sbbox.SBBox.RDFType;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

@SuppressWarnings("serial")
public class RDFTypeImp extends SBWrapper implements SBBox.MutableRDFType, Serializable {

	public RDFTypeImp(SBBox box, Resource resource) { super(box, resource); }

	public Set<RDFType> directSuperTypes() {
		Set<RDFType> superTypes = new HashSet<RDFType>();
		StmtIterator superIter = 
			box().getRdf().listStatements(resource(), RDFS.subClassOf, (RDFNode) null);
		while(superIter.hasNext()) {
			Statement statement = superIter.nextStatement();
			RDFNode superTypeNode = statement.getObject();
			if(superTypeNode instanceof Resource) {
				superTypes.add(new RDFTypeImp(box(), (Resource) superTypeNode));				
			}
		}
		return superTypes;
	}

	public MutableRDFType addDirectSuperType(RDFType superType) {
		box().getRdf().add(resource(), RDFS.subClassOf, superType.resource());
		return this;
	}

	public MutableRDFType removeDirectSuperType(RDFType superType) {
		box().getRdf().remove(resource(), RDFS.subClassOf, superType.resource());
		return this;
	}

	public MutableRDFType removeAllDirectSuperTypes() {
		StmtIterator stmtIter = 
			box().getRdf().listStatements(resource(), RDFS.subClassOf, (RDFNode) null);
		Set<RDFNode> superTypeNodes = new HashSet<RDFNode>();
		while(stmtIter.hasNext()) {
			Statement statement = stmtIter.nextStatement();
			superTypeNodes.add(statement.getObject());
		}
		for(RDFNode superTypeNode : superTypeNodes) {
			box().getRdf().remove(resource(), RDFS.subClassOf, superTypeNode);
		}
		return this;
	}

	public Set<RDFType> directSubTypes() {
		Set<RDFType> subTypes = new HashSet<RDFType>();
		StmtIterator subIter = 
			box().getRdf().listStatements(null, RDFS.subClassOf, resource());
		while(subIter.hasNext()) {
			Statement statement = subIter.nextStatement();
			Resource superTypeNode = statement.getSubject();
			subTypes.add(new RDFTypeImp(box(), (Resource) superTypeNode));				
		}
		return subTypes;
	}

	public MutableRDFType addDirectSubType(RDFType subType) {
		box().getRdf().add(subType.resource(), RDFS.subClassOf, resource());
		return this;
	}

	public MutableRDFType removeDirectSubType(RDFType subType) {
		box().getRdf().remove(subType.resource(), RDFS.subClassOf, resource());
		return this;
	}

	public MutableRDFType removeAllDirectSubTypes() {
		StmtIterator stmtIter = 
			box().getRdf().listStatements(null, RDFS.subClassOf, resource());
		Set<Resource> subTypeNodes = new HashSet<Resource>();
		while(stmtIter.hasNext()) {
			Statement statement = stmtIter.nextStatement();
			subTypeNodes.add(statement.getSubject());
		}
		for(Resource subTypeNode : subTypeNodes) {
			box().getRdf().remove(subTypeNode, RDFS.subClassOf, resource());
		}
		return this;
	}

	public Set<RDFType> allSubTypes() {
		Set<RDFType> subTypes = new HashSet<RDFType>();
		Set<RDFType> subTypesNew = directSubTypes();
		while(!subTypesNew.isEmpty()) {
			Set<RDFType> subTypesNewNew = new HashSet<RDFType>();
			for(RDFType subType : subTypesNew) {
				if(!subTypes.contains(subType)) {
					subTypes.add(subType);
					subTypesNewNew.addAll(subType.directSubTypes());
				}
			}
			subTypesNew = subTypesNewNew;
		}
		return subTypes;
	}

	public Set<RDFType> allSuperTypes() {
		Set<RDFType> superTypes = new HashSet<RDFType>();
		Set<RDFType> superTypesNew = directSuperTypes();
		while(!superTypesNew.isEmpty()) {
			Set<RDFType> superTypesNewNew = new HashSet<RDFType>();
			for(RDFType superType : superTypesNew) {
				if(!superTypes.contains(superType)) {
					superTypes.add(superType);
					superTypesNewNew.addAll(superType.directSubTypes());
				}
			}
			superTypesNew = superTypesNewNew;
		}
		return superTypes;
	}
	
	public boolean isSBType() { return SBPAX.schema.contains(resource(), RDF.type, OWL.Class); }

	public boolean isTypeOf(NamedThing thing) {
		return box().getRdf().contains(thing.resource(), RDF.type, resource());
	}
	
	public RDFType getSBSubTypeFor(NamedThing thing) {
		RDFType type = this;
		Set<RDFType> subTypes = type.directSubTypes();
		Set<RDFType> subTypesNew = new HashSet<RDFType>();
		for(RDFType subType : subTypes) {
			if(subType.isTypeOf(thing) && subType.isSBType()) { 
				type = subType; 
				subTypesNew.addAll(subType.directSubTypes());
			}
		}
		while(!subTypesNew.isEmpty()) {
			Set<RDFType> subTypesNewNew = new HashSet<RDFType>();
			for(RDFType subTypeNew : subTypesNew) {
				if(!subTypes.contains(subTypeNew)) {
					if(subTypeNew.isTypeOf(thing) && subTypeNew.isSBType()) { 
						type = subTypeNew; 
						subTypesNewNew.addAll(subTypeNew.directSubTypes());
					}
				}
			}
			subTypes.addAll(subTypesNew);
			subTypesNew = subTypesNewNew;
		}
		return type;
	}
	
}