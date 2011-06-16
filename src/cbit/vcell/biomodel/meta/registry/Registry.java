/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.biomodel.meta.registry;

/**
 * maps between resources, identifiables, uris and meta ids.
 * @author ruebenacker
 *
 */

import java.util.Set;

import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import cbit.vcell.biomodel.meta.Identifiable;
import cbit.vcell.biomodel.meta.registry.OpenRegistry.OpenEntry;

import com.hp.hpl.jena.rdf.model.Resource;

public interface Registry {
	
	public static interface Entry {
		public Identifiable getIdentifiable();
		public NamedThing getNamedThing();
	}
	
	public static interface IdentifiableURIGenerator {
		public String generateURI(Identifiable identifiable);
	}
	
	public static interface IdentifiableSBThingFactory {
		public NamedThing createThingWithURI(Identifiable identifiable, String uri);
		public NamedThing createThing(Identifiable identifiable, Resource resource);
		public NamedThing createThingAnonymous(Identifiable identifiable);
	}
	
	public OpenEntry newEntry(Identifiable identifiable, NamedThing sbThing);
	public OpenEntry newEntry(Identifiable identifiable, String uri);	

	
	public Registry.Entry getEntry(Identifiable identifiable);
	public Registry.Entry getEntry(Resource resource);
	
	public Set<Entry> getAllEntries();
	
}
