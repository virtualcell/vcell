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

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Resource;

public class RDFObjectCompositePool<T, T1 extends T, T2 extends T> implements RDFObjectPool<T> {

	protected final RDFObjectPool<T1> pool1;
	protected final RDFObjectPool<T2> pool2;
	protected final Set<Resource> supportedTypes = new HashSet<Resource>();
	protected final Class<T> createdClass;
	
	public RDFObjectCompositePool(RDFObjectPool<T1> pool1, RDFObjectPool<T2> pool2, Class<T> 
	createdClass) {
		this.pool1 = pool1;
		this.pool2 = pool2;
		this.createdClass = createdClass;
		supportedTypes.addAll(pool1.getSupportedTypes());
		supportedTypes.addAll(pool2.getSupportedTypes());
	}
	
	public Set<Resource> getSupportedTypes() { return supportedTypes; }
	public Class<T> getCreatedClass() { return createdClass; }

	public boolean canSuggestType(T object) {
		Class<T1> createdClass1 = pool1.getCreatedClass();
		if(createdClass1.isInstance(object)) {
			T1 object1 = createdClass1.cast(object);
			if(pool1.canSuggestType(object1)) { return true; }
		}
		Class<T2> createdClass2 = pool2.getCreatedClass();
		if(createdClass2.isInstance(object)) {
			T2 object2 = createdClass2.cast(object);
			if(pool2.canSuggestType(object2)) { return true; }
		}
		return false;
	}

	public Resource getSuggestedType(T object) throws UnsupportedRDFTypeException {
		Class<T1> createdClass1 = pool1.getCreatedClass();
		if(createdClass1.isInstance(object)) {
			T1 object1 = createdClass1.cast(object);
			if(pool1.canSuggestType(object1)) { return pool1.getSuggestedType(object1); }
		}
		Class<T2> createdClass2 = pool2.getCreatedClass();
		if(createdClass2.isInstance(object)) {
			T2 object2 = createdClass2.cast(object);
			if(pool2.canSuggestType(object2)) { return pool2.getSuggestedType(object2); }
		}
		throw new UnsupportedRDFTypeException("Can not suggest a type for this object.");
	}

	public T createObject(Resource resource, Resource type)
	throws UnsupportedRDFTypeException {
		if(pool1.getSupportedTypes().contains(type)) {
			return pool1.createObject(resource, type);
		} else if(pool2.getSupportedTypes().contains(type)) {
			return pool2.createObject(resource, type);
		}
		throw new UnsupportedRDFTypeException("Can not create an object for this type.");
	}

	public T createObject(Resource resource, Set<Resource> types)
	throws UnsupportedRDFTypeException {
		for(Resource type : types) {
			if(pool1.getSupportedTypes().contains(type)) {
				return pool1.createObject(resource, types);
			} else if(pool2.getSupportedTypes().contains(type)) {
				return pool2.createObject(resource, types);
			}
		}
		throw new UnsupportedRDFTypeException("Can not create an object for this type.");
	}

	public T getObject(Resource resource) {
		T object = pool1.getObject(resource);
		if(object == null) { object = pool2.getObject(resource); }
		return object;
	}
	
	public Set<T> getObjects() {
		HashSet<T> objects = new HashSet<T>();
		objects.addAll(pool1.getObjects());
		objects.addAll(pool2.getObjects());
		return objects;
	}
	
	public Set<T> getObjects(Set<Resource> resources) {
		HashSet<T> objects = new HashSet<T>();
		objects.addAll(pool1.getObjects(resources));
		objects.addAll(pool2.getObjects(resources));
		return objects;
	}

	public T getOrCreateObject(Resource resource, Resource type)
	throws UnsupportedRDFTypeException {
		T object = getObject(resource);
		if(object == null) { object = createObject(resource, type); }
		return object;
	}

	public T getOrCreateObject(Resource resource, Set<Resource> types)
	throws UnsupportedRDFTypeException {
		T object = getObject(resource);
		if(object == null) { object = createObject(resource, types); }
		return object;
	}

	public Set<Resource> getResources(T object) {
		HashSet<Resource> resources = new HashSet<Resource>();
		Class<T1> createdClass1 = pool1.getCreatedClass();
		if(createdClass1.isInstance(object)) {
			T1 object1 = createdClass1.cast(object);
			resources.addAll(pool1.getResources(object1));
		}
		Class<T2> createdClass2 = pool2.getCreatedClass();
		if(createdClass2.isInstance(object)) {
			T2 object2 = createdClass2.cast(object);
			resources.addAll(pool2.getResources(object2));
		}
		return resources;
	}

	public boolean containsResource(Resource resource) {
		return pool1.containsResource(resource) || pool2.containsResource(resource);
	}

	public boolean containsObject(T object) {
		Class<T1> createdClass1 = pool1.getCreatedClass();
		if(createdClass1.isInstance(object)) {
			T1 object1 = createdClass1.cast(object);
			if(pool1.containsObject(object1)) { return true; }
		}
		Class<T2> createdClass2 = pool2.getCreatedClass();
		if(createdClass2.isInstance(object)) {
			T2 object2 = createdClass2.cast(object);
			if(pool2.containsObject(object2)) { return true; }
		}
		return false;
	}
	
	public void setPrimaryResource(T object, Resource resource) {
		Class<T1> createdClass1 = pool1.getCreatedClass();
		if(createdClass1.isInstance(object)) {
			T1 object1 = createdClass1.cast(object);
			if(pool1.containsObject(object1)) { pool1.setPrimaryResource(object1, resource); }
		}
		Class<T2> createdClass2 = pool2.getCreatedClass();
		if(createdClass2.isInstance(object)) {
			T2 object2 = createdClass2.cast(object);
			if(pool2.containsObject(object2)) { pool2.setPrimaryResource(object2, resource); }
		}
	}

	public void unsetPrimaryResource(T object) {
		Class<T1> createdClass1 = pool1.getCreatedClass();
		if(createdClass1.isInstance(object)) {
			T1 object1 = createdClass1.cast(object);
			pool1.unsetPrimaryResource(object1);
		}
		Class<T2> createdClass2 = pool2.getCreatedClass();
		if(createdClass2.isInstance(object)) {
			T2 object2 = createdClass2.cast(object);
			pool2.unsetPrimaryResource(object2);
		}
	}

	public Resource getPrimaryResource(T object) {
		Class<T1> createdClass1 = pool1.getCreatedClass();
		if(createdClass1.isInstance(object)) {
			T1 object1 = createdClass1.cast(object);
			Resource primaryResource = pool1.getPrimaryResource(object1);
			if(primaryResource != null) { return primaryResource; }
		}
		Class<T2> createdClass2 = pool2.getCreatedClass();
		if(createdClass2.isInstance(object)) {
			T2 object2 = createdClass2.cast(object);
			Resource primaryResource = pool2.getPrimaryResource(object2);
			if(primaryResource != null) { return primaryResource; }
		}
		return null;
	}

	public void remove(Resource resource) {
		pool1.remove(resource);
		pool2.remove(resource);
	}

	public void remove(T object) {
		Class<T1> createdClass1 = pool1.getCreatedClass();
		if(createdClass1.isInstance(object)) {
			T1 object1 = createdClass1.cast(object);
			pool1.remove(object1);
		}
		Class<T2> createdClass2 = pool2.getCreatedClass();
		if(createdClass2.isInstance(object)) {
			T2 object2 = createdClass2.cast(object);
			pool2.remove(object2);
		}
	}

}
