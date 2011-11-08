/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf.pool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Resource;

public abstract class RDFObjectSimplePool<T> implements RDFObjectPool<T> {
	
	protected final Map<Resource, T> resource2object = new HashMap<Resource, T>();
	protected final Map<T, Set<Resource>> object2resources = new HashMap<T, Set<Resource>>();
	protected final Map<T, Resource> object2primaryResource = new HashMap<T, Resource>();
	
	protected abstract T createObjectOnly(Resource resource, Resource type)
	throws UnsupportedRDFTypeException;
	protected abstract T createObjectOnly(Resource resource, Set<Resource> types) 
	throws UnsupportedRDFTypeException;
	public abstract Set<Resource> getSupportedTypes();
	public abstract Class<T> getCreatedClass();
	public abstract boolean canSuggestType(T object);
	public abstract Resource getSuggestedType(T object) throws UnsupportedRDFTypeException;
	
	public void link(Resource resource, T object) {
		resource2object.put(resource, object);
		Set<Resource> resources = object2resources.get(object);
		if(resources == null) { 
			resources = new HashSet<Resource>();
			object2resources.put(object, resources);
		}
		if(!object2primaryResource.containsKey(object)) { object2primaryResource.put(object, resource); }
		resources.add(resource);		
	}
	
	public void unlink(Resource resource, T object) {
		T object2 = resource2object.get(resource);
		if(object2 != null && !object2.equals(object)) { resource2object.remove(resource); }
		Set<Resource> resources2 = object2resources.get(object);
		if(resources2 != null) { resources2.remove(resource); }
		Resource primaryResource2 = object2primaryResource.get(object);
		if(primaryResource2 != null && primaryResource2.equals(resource)) { object2resources.remove(object); }
	}
	
	public T createObject(Resource resource, Resource type) throws UnsupportedRDFTypeException {
		T oldObject = resource2object.get(resource);
		if(oldObject != null) { unlink(resource, oldObject); }
		T object = createObjectOnly(resource, type);
		link(resource, object);
		return object;
	}
	
	public T createObject(Resource resource, Set<Resource> types) throws UnsupportedRDFTypeException {
		T oldObject = resource2object.get(resource);
		if(oldObject != null) { unlink(resource, oldObject); }
		T object = createObjectOnly(resource, types);
		link(resource, object);
		return object;
	}
	
	public T getObject(Resource resource) {
		return resource2object.get(resource);
	}
	
	public Set<T> getObjects() { return object2resources.keySet(); }

	public Set<T> getObjects(Set<Resource> resources) { 
		HashSet<T> objects = new HashSet<T>();
		for(Resource resource : resources) {
			T object = resource2object.get(resource);
			if(object != null) { objects.add(object); }
		}
		return objects; 
	}
	
	public T getOrCreateObject(Resource resource, Resource type) throws UnsupportedRDFTypeException {
		T object = getObject(resource);
		if(object == null) { object = createObject(resource, type); }
		return object;
	}
	
	public T getOrCreateObject(Resource resource, Set<Resource> types) throws UnsupportedRDFTypeException {
		T object = getObject(resource);
		if(object == null) { object = createObject(resource, types); }
		return object;
	}
	
	public Set<Resource> getResources(T object) {
		Set<Resource> resources = object2resources.get(object);
		if(resources == null) {
			resources = new HashSet<Resource>();
			object2resources.put(object, resources);
		}
		return resources;
	}
	
	public boolean containsResource(Resource resource) { 
		return resource2object.containsKey(resource); 
	}
	
	public boolean containsObject(T object) { 
		return object2resources.containsKey(object); 
	}
	
	public void setPrimaryResource(T object, Resource resource) { 
		if(containsObject(object)) {
			object2primaryResource.put(object, resource); 			
		}
	}
	
	public void unsetPrimaryResource(T object) { object2primaryResource.remove(object); }
	public Resource getPrimaryResource(T object) { return object2primaryResource.get(object); }
	
	public void remove(Resource resource) {
		T object = resource2object.get(resource);
		if(object != null) { unlink(resource, object); }
	}
	
	public void remove(T object) {
		Set<Resource> resourcesToDelete = new HashSet<Resource>();
		Set<Resource> resources = object2resources.get(object);
		if(resources != null) { 
			object2resources.remove(object);
			resourcesToDelete.addAll(resources); 
		}
		Resource primaryResource = object2primaryResource.get(object);
		if(primaryResource != null) {
			object2primaryResource.remove(object);
			resourcesToDelete.add(primaryResource);
		}
		for(Resource resourceToDelete : resourcesToDelete) {
			resource2object.remove(resourceToDelete);
		}
	}
	
}
