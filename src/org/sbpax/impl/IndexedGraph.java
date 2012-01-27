/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.sbpax.util.StatementComparator;
import org.sbpax.util.iterators.IterOfNone;
import org.sbpax.util.iterators.IterOfOne;
import org.vcell.sybil.util.lists.ListOfTwo;

/* 
 *   A fully indexed implementation of Sesame's RDF Graph without support for contexts.
 *   This graph contains maps from all values and value pairs to sets of
 *   statements matching these, which makes match() as fast as possible.
 *   This is not a list like Sesame's default implementation, but a set.
 *   This makes is faster to query the presence of a statement and prevents
 *   a statement from being contained multiple times.
 *   The disadvantage is that the order in which statements were added is not preserved.
 */

@SuppressWarnings("serial")
public class IndexedGraph implements Graph {
	
	protected class IndexedGraphIterator implements Iterator<Statement> {
		
		protected final IndexedGraph graph;
		protected final Iterator<Statement> iterator;
		protected Statement current;
		
		public IndexedGraphIterator(IndexedGraph graph, 
				Iterator<Statement> iterator) {
			this.graph = graph;
			this.iterator = iterator;
		}

		public boolean hasNext() { return iterator.hasNext(); }
		
		public Statement next() { 
			current = iterator.next();
			return current; 
		}

		public void remove() {
			iterator.remove();
			graph.removeFromAllMaps(current);
			if(graph.contains(current)) { graph.remove(current); }
		}
		
	}
	
	public static final ValueFactory VALUE_FACTORY = new ValueFactoryImpl();
	
	protected Set<Statement> statements = new TreeSet<Statement>(new StatementComparator());

	protected Map<Value, Set<Statement>> sMap = new HashMap<Value, Set<Statement>>();
	protected Map<Value, Set<Statement>> pMap = new HashMap<Value, Set<Statement>>();
	protected Map<Value, Set<Statement>> oMap = new HashMap<Value, Set<Statement>>();
	protected Map<ListOfTwo<Value>, Set<Statement>> spMap = 
		new HashMap<ListOfTwo<Value>, Set<Statement>>();
	protected Map<ListOfTwo<Value>, Set<Statement>> soMap = 
		new HashMap<ListOfTwo<Value>, Set<Statement>>();
	protected Map<ListOfTwo<Value>, Set<Statement>> poMap = 
		new HashMap<ListOfTwo<Value>, Set<Statement>>();
	
	protected <K> void addToMap(Map<K, Set<Statement>> map, K key, Statement statement) {
		Set<Statement> set = map.get(key);
		if(set == null) {
			set = new HashSet<Statement>();
			map.put(key, set);
		}
		set.add(statement);
	}
	
	protected void addToAllMaps(Statement statement) {
		Resource subject = statement.getSubject();
		URI predicate = statement.getPredicate();
		Value object = statement.getObject();
		addToMap(sMap, subject, statement);
		addToMap(pMap, predicate, statement);
		addToMap(oMap, object, statement);
		addToMap(spMap, new ListOfTwo<Value>(subject, predicate), statement);
		addToMap(soMap, new ListOfTwo<Value>(subject, object), statement);
		addToMap(poMap, new ListOfTwo<Value>(predicate, object), statement);		
	}
	
	public boolean add(Statement statement) { 
		boolean hasChanged = statements.add(statement);
		if(hasChanged) { addToAllMaps(statement); }
		return hasChanged; 
	}
	
	public boolean addAll(Collection<? extends Statement> collection) { 
		boolean hasChanged = false;
		for(Statement statement : collection) {
			hasChanged = hasChanged || add(statement);
		}
		return hasChanged;
	}
	
	public void clear() { 
		statements.clear(); 
		sMap.clear();
		pMap.clear();
		oMap.clear();
		spMap.clear();
		soMap.clear();
		poMap.clear();
	}
	
	public boolean contains(Object o) { return statements.contains(o); }
	public boolean containsAll(Collection<?> c) { return statements.containsAll(c); }
	public boolean isEmpty() { return statements.isEmpty(); }
	
	public Iterator<Statement> iterator() { 
		return new IndexedGraphIterator(this, statements.iterator()); 
	}
	
	protected <K> void removeFromMap(Map<K, Set<Statement>> map, K key, Statement statement) {
		Set<Statement> set = map.get(key);
		if(set != null) {
			set.remove(statement);
			if(set.isEmpty()) {
				map.remove(key);
			}
		}
	}
	
	protected void removeFromAllMaps(Statement statement) {
		Resource subject = statement.getSubject();
		URI predicate = statement.getPredicate();
		Value object = statement.getObject();
		removeFromMap(sMap, subject, statement);
		removeFromMap(pMap, predicate, statement);
		removeFromMap(oMap, object, statement);
		removeFromMap(spMap, new ListOfTwo<Value>(subject, predicate), statement);
		removeFromMap(soMap, new ListOfTwo<Value>(subject, object), statement);
		removeFromMap(poMap, new ListOfTwo<Value>(predicate, object), statement);		
	}
	
	public boolean remove(Object object) { 
		boolean hasChanged = statements.remove(object);
		if(hasChanged) { removeFromAllMaps((Statement) object); }
		return hasChanged; 
	}

	public boolean removeAll(Collection<?> collection) { 
		boolean hasChanged = false;
		for(Object object : collection) {
			hasChanged = hasChanged || remove(object);
		}
		return hasChanged;
	}
	
	public boolean retainAll(Collection<?> collection) { 
		boolean hasChanged = false;
		Iterator<Statement> iterator = statements.iterator();
		while(iterator.hasNext()) {
			Statement statement = iterator.next();
			if(!collection.contains(statement)) {
				iterator.remove();
				removeFromAllMaps(statement);
				hasChanged = true;
			}
		}
		return hasChanged; 
	}
	
	public int size() { return statements.size(); }
	public Object[] toArray() { return statements.toArray(); }
	public <T> T[] toArray(T[] a) { return statements.toArray(a); }

	public boolean add(Resource arg0, URI arg1, Value arg2, Resource... arg3) {
		return add(getValueFactory().createStatement(arg0, arg1, arg2));
	}

	public ValueFactory getValueFactory() { return VALUE_FACTORY; }

	public Iterator<Statement> match(Resource subject, URI predicate, Value object,
			Resource... contexts) {
		Iterator<Statement> iterator = new RDFGraphMatchIterator(this, subject, predicate, object);
		if(subject != null) {
			if(predicate != null) {
				if(object != null) {
					Statement statement = getValueFactory().createStatement(subject, predicate, object);
					if(statements.contains(statement)) {
						iterator = new IndexedGraphIterator(this, new IterOfOne<Statement>(statement));
					} else {
						iterator = new IterOfNone<Statement>();				
					}
				} else {
					Set<Statement> matches = spMap.get(new ListOfTwo<Value>(subject, predicate));
					iterator = matches != null ? new IndexedGraphIterator(this, matches.iterator()) : new IterOfNone<Statement>();					
				}
			} else {
				if(object != null) {
					Set<Statement> matches = soMap.get(new ListOfTwo<Value>(subject, object));
					iterator = matches != null ?  new IndexedGraphIterator(this, matches.iterator()) : new IterOfNone<Statement>();					
				} else {
					Set<Statement> matches = sMap.get(subject);
					iterator = matches != null ? new IndexedGraphIterator(this, matches.iterator()) : new IterOfNone<Statement>();
				}
			}
		} else {
			if(predicate != null) {
				if(object != null) {
					Set<Statement> matches = poMap.get(new ListOfTwo<Value>(predicate, object));
					iterator = matches != null ? new IndexedGraphIterator(this, matches.iterator()) : new IterOfNone<Statement>();					
				} else {
					Set<Statement> matches = pMap.get(predicate);
					iterator = matches != null ? new IndexedGraphIterator(this, matches.iterator()) : new IterOfNone<Statement>();
				}
			} else {
				if(object != null) {
					Set<Statement> matches = oMap.get(object);
					iterator = matches != null ? new IndexedGraphIterator(this, matches.iterator()) : new IterOfNone<Statement>();
				} else {
					iterator = new IndexedGraphIterator(this, statements.iterator());
				}
			}
			
		}
		return iterator;
	}
	
	public boolean equals(Object o) {
		if(o instanceof IndexedGraph) {
			return statements.equals(((IndexedGraph) o).statements);
		}
		return false;
	}
	
	public int hashCode() { return statements.hashCode(); }

}
