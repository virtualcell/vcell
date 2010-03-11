package org.vcell.sybil.rdf.schemas;

/*   MIRIAM  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   The MIRIAM qualifiers
 */

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

public class MIRIAM {
	
	public static OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

	public static Property createProperty(Set<Property> all, String ns, String name) {
		Property property = schema.createProperty(ns + name);
		all.add(property);
		return property;
	}
	
	public static class ModelProperties {
		public static final String ns = "http://biomodels.net/model-qualifiers/";
		public static final Set<Property> all = new HashSet<Property>();

		protected static Property createProperty(String name) { 
			return MIRIAM.createProperty(all, ns, name);
		}

		public static final Property is = createProperty("is");
		public static final Property isDerivedFrom = createProperty("isDerivedFrom");
		public static final Property isDescribedBy = createProperty("isDescribedBy");	
	}

	public static class BioProperties {
		public static final String ns = "http://biomodels.net/biology-qualifiers/";
		public static final Set<Property> all = new HashSet<Property>();
		
		protected static Property createProperty(String name) { 
			return MIRIAM.createProperty(all, ns, name);
		}

		public static final Property encodes = createProperty("encodes");
		public static final Property hasPart = createProperty("hasPast");
		public static final Property hasProperty = createProperty("hasProperty");
		public static final Property hasVersion = createProperty("hasVersion");
		public static final Property is = createProperty("is");
		public static final Property isDescribedBy = createProperty("isDescribedBy");	
		public static final Property isEncodedBy = createProperty("isEncodedBy");
		public static final Property isHomologTo = createProperty("isHomologTo");
		public static final Property isPartOf = createProperty("isPartOf");
		public static final Property isPropertyOf = createProperty("isPropertyOf");
		public static final Property isVersionOf = createProperty("isVersionOf");
		public static final Property occursIn = createProperty("occursIn");
	}

}
