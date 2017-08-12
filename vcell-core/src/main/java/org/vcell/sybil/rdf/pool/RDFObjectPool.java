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

import java.util.Collection;
import java.util.Set;

import org.openrdf.model.Resource;

public interface RDFObjectPool<T> {
	
	public Set<Resource> getSupportedTypes();
	public Class<T> getCreatedClass();
	public boolean canSuggestType(T object);
	public Resource getSuggestedType(T object) throws UnsupportedRDFTypeException;	
	public T createObject(Resource resource, Resource type)
	throws UnsupportedRDFTypeException;
	public T createObject(Resource resource, Set<Resource> types) 
	throws UnsupportedRDFTypeException;
	public T getObject(Resource resource);
	public Collection<T> getObjects();
	public Collection<T> getObjects(Set<Resource> resources);
	public T getOrCreateObject(Resource resource, Resource type)
	throws UnsupportedRDFTypeException;
	public T getOrCreateObject(Resource resource, Set<Resource> types)
	throws UnsupportedRDFTypeException;
	public Set<Resource> getResources(T object);
	public boolean containsResource(Resource resource);
	public boolean containsObject(T object);
	public void setPrimaryResource(T object, Resource resource);
	public void unsetPrimaryResource(T object);
	public Resource getPrimaryResource(T object);
	public void remove(Resource resource);
	public void remove(T object);
	
}
