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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.vcell.util.document.Identifiable;

import cbit.vcell.biomodel.meta.IdentifiableProvider;
import cbit.vcell.biomodel.meta.VCID;
import cbit.vcell.xml.XMLTags;

/*   Registry  --- May 2009
 *   Maintains resources and their relationships to objects
 *   @author: Oliver Ruebenacker
 */

@SuppressWarnings("serial")
public class Registry implements Serializable {

	public class Entry implements Serializable {
		private final Identifiable identifiable;
		private Resource resource = null;
		
		public Entry(Identifiable identifiable) {
			this.identifiable = identifiable;
		}
		
		public Registry getRegistry() { return Registry.this; }
		public Identifiable getIdentifiable() { return identifiable; }
		public Resource getResource() { return resource; }
		
		private void setResource(Resource resource) {
			if (this.resource!=null){
				resourceToEntry.remove(resource);
				uriToEntry.remove(resource.stringValue());
			}
			this.resource = resource;
			if (resource!=null){
				resourceToEntry.put(resource, this);
				uriToEntry.put(resource.stringValue(), this);
			}
		}
		
		public URI setURI(Graph graph, String uriString) {
			if (uriString.contains("#")){
				throw new RuntimeException("unexpected character, '#' in uri");
			}
			URI uri = graph.getValueFactory().createURI(uriString);
			setResource(uri);
			return uri;
		}
		
		@Override
		public String toString(){
			return getIdentifiable().toString()+" <==> " + getResource();
		}
	}
	
	protected ArrayList<Entry> entryList = new ArrayList<Entry>(); // here for ease of debugging
	protected Map<Identifiable, Entry> identifiableToEntry = 
		Collections.synchronizedMap(new IdentityHashMap<Identifiable, Entry>());
	protected Hashtable<Resource, Entry> resourceToEntry = new Hashtable<Resource, Entry>();
	protected Hashtable<String, Entry> uriToEntry = new Hashtable<String, Entry>();
	protected IdentifiableProvider identifiableProvider = null;
	
	public Registry(IdentifiableProvider identifiableProvider) {
		this.identifiableProvider = identifiableProvider;
	}
	
	public String generateFreeURI(Identifiable identifiable) {
		while (true){
			String uri = XMLTags.VCML_NS + "/" + identifiable.getClass().getName() + "/" + (Math.abs((new Random()).nextInt()));
			if (uriToEntry.get(uri)==null){
				return uri;
			}
		}
	}
	
	public Entry newEntry(Identifiable identifiable, Resource resource) {
		if (identifiable==null){
			throw new RuntimeException("cannot create a new metadata entry with a null identifiable object");
		}
		if (resource==null){
			throw new RuntimeException("cannot create a new metadata entry with a null resource");
		}
		if (resource.stringValue().contains("#")){
			throw new RuntimeException("unexpected character, '#' in uri");
		}
		if (identifiableToEntry.get(identifiable)!=null){
			throw new RuntimeException("entry for '"+identifiable+"' already exists");
		}
		if (resourceToEntry.get(resource)!=null){
			throw new RuntimeException("entry for resource '" + resource + "' already exists");
		}
		if (uriToEntry.get(resource.stringValue())!=null){
			throw new RuntimeException("entry for uri '"+resource.stringValue()+"' already exists");
		}
		Entry newEntry = new Entry(identifiable);
		identifiableToEntry.put(identifiable,newEntry);
		entryList.add(newEntry);
		newEntry.setResource(resource);
		return newEntry;
	}
	
	public Entry newEntry(Graph graph, Identifiable identifiable, String uri) {
		if (identifiable==null){
			throw new RuntimeException("cannot create a new metadata entry with a null identifiable object");
		}
		if (uri==null){
			throw new RuntimeException("cannot create a new metadata entry with a null URI");
		}
		if (identifiableToEntry.get(identifiable)!=null){
			throw new RuntimeException("entry for '"+identifiable+"' already exists");
		}
		if (uriToEntry.get(uri)!=null){
			throw new RuntimeException("entry for uri '"+uri+"' already exists");
		}
		Entry newEntry = new Entry(identifiable);
		identifiableToEntry.put(identifiable,newEntry);
		entryList.add(newEntry);
		newEntry.setURI(graph, uri);
		return newEntry;
	}
	
	public Entry getEntry(Identifiable object) { 
		Entry entry = identifiableToEntry.get(object); 
		if(entry == null) { 
			entry = new Entry(object);
			identifiableToEntry.put(object, entry);
			entryList.add(entry);
		}
		return entry;
	}	
	
	public Entry getEntry(Resource resource) {
		return resourceToEntry.get(resource);
	}
	
	public Set<Resource> getResources(){
		return resourceToEntry.keySet();
	}
	
	public Set<Entry> getAllEntries() {
		HashSet<Entry> entries = new HashSet<Entry>();
		entries.addAll(identifiableToEntry.values());
		return entries;		
	}
	
	public boolean compareEquals(Registry other) {
		Set<Resource> resSet = resourceToEntry.keySet();
		Set<Resource> otherResSet = other.resourceToEntry.keySet();
		if (!resSet.equals(otherResSet)) {
			return false;
		}
		for (Resource res : resSet) {
			Entry oe = resourceToEntry.get(res);
			Entry otherOe = other.resourceToEntry.get(res);
			final VCID vcid = identifiableProvider.getVCID(oe.getIdentifiable());
			final VCID otherVcid = other.identifiableProvider.getVCID(otherOe.getIdentifiable());
			if (!vcid.toASCIIString().equals(otherVcid.toASCIIString())) {
				return false;
			}
		}
		return true;
	}
}
