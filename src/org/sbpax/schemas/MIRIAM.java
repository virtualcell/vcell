/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.schemas;

/*   MIRIAM  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   The MIRIAM qualifiers
 */

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.sbpax.impl.HashGraph;

public class MIRIAM {
	
	public static Graph schema = new HashGraph();

	public static URI createProperty(Set<URI> all, String ns, String name) {
		URI property = schema.getValueFactory().createURI(ns + name);
		all.add(property);
		return property;
	}
	
	public static class ModelProperties {
		public static final String ns = "http://biomodels.net/model-qualifiers/";
		public static final Set<URI> all = new HashSet<URI>();

		protected static URI createProperty(String name) { 
			return MIRIAM.createProperty(all, ns, name);
		}

		public static final URI is = createProperty("is");
		public static final URI isDerivedFrom = createProperty("isDerivedFrom");
		public static final URI isDescribedBy = createProperty("isDescribedBy");	
	}

	public static class BioProperties {
		public static final String ns = "http://biomodels.net/biology-qualifiers/";
		public static final Set<URI> all = new HashSet<URI>();
		
		protected static URI createProperty(String name) { 
			return MIRIAM.createProperty(all, ns, name);
		}

		public static final URI encodes = createProperty("encodes");
		public static final URI hasPart = createProperty("hasPast");
		public static final URI hasProperty = createProperty("hasProperty");
		public static final URI hasVersion = createProperty("hasVersion");
		public static final URI is = createProperty("is");
		public static final URI isDescribedBy = createProperty("isDescribedBy");	
		public static final URI isEncodedBy = createProperty("isEncodedBy");
		public static final URI isHomologTo = createProperty("isHomologTo");
		public static final URI isPartOf = createProperty("isPartOf");
		public static final URI isPropertyOf = createProperty("isPropertyOf");
		public static final URI isVersionOf = createProperty("isVersionOf");
		public static final URI occursIn = createProperty("occursIn");
	}

}
