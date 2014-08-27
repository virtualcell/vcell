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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.sbpax.util.StatementComparator;

/* 
 *   A simple implementation of Sesame's RDF Graph without support for contexts.
 *   This is not a list like Sesame's default implementation, but a set.
 *   This makes is faster to query the presence of a statement and prevents
 *   a statement from being contained multiple times.
 *   The disadvantage is that the order in which statements were added is not preserved.
 */

@SuppressWarnings("serial")
public class HashGraph implements Graph {
	
	protected Set<Statement> statements = new TreeSet<Statement>(new StatementComparator());

	public boolean add(Statement e) { return statements.add(e); }
	public boolean addAll(Collection<? extends Statement> c) { return statements.addAll(c); }
	public void clear() { statements.clear(); }
	public boolean contains(Object o) { return statements.contains(o); }
	public boolean containsAll(Collection<?> c) { return statements.containsAll(c); }
	public boolean isEmpty() { return statements.isEmpty(); }
	public Iterator<Statement> iterator() { return statements.iterator(); }
	public boolean remove(Object o) { return statements.remove(o); }
	public boolean removeAll(Collection<?> c) { return statements.removeAll(c); }
	public boolean retainAll(Collection<?> c) { return statements.retainAll(c); }
	public int size() { return statements.size(); }
	public Object[] toArray() { return statements.toArray(); }
	public <T> T[] toArray(T[] a) { return statements.toArray(a); }

	public boolean add(Resource arg0, URI arg1, Value arg2, Resource... arg3) {
		return add(getValueFactory().createStatement(arg0, arg1, arg2));
	}

	public ValueFactory getValueFactory() { return ValueFactoryImpl.getInstance(); }

	public Iterator<Statement> match(Resource subject, URI predicate, Value object,
			Resource... contexts) {
		return new RDFGraphMatchIterator(this, subject, predicate, object);
	}
	
	public boolean equals(Object o) {
		if(o instanceof HashGraph) {
			if(statements.size() != ((HashGraph) o).statements.size()){
				return false;
			}
			Iterator<Statement> stiter1 = statements.iterator();
			Iterator<Statement> stiter2 = ((HashGraph) o).statements.iterator();
			HashMap<String, Statement[]> stmtObjRefHash = new HashMap<String, Statement[]>();
			ArrayList<String> postLookupArr = new ArrayList<String>();
			while (stiter1.hasNext()){
				Statement st1 = stiter1.next();
				Statement st2 = stiter2.next();
				
				if(!compareStatementsExcludeBNode(st1, st2)){
					return false;
				}
				
				if(st1.getObject() instanceof BNodeImpl && st2.getObject() instanceof BNodeImpl){
					postLookupArr.add(makeStatementKey((BNodeImpl)st1.getObject(), (BNodeImpl)st2.getObject()));
				}else if(st1.getSubject() instanceof BNodeImpl && st2.getSubject() instanceof BNodeImpl){
					stmtObjRefHash.put(makeStatementKey((BNodeImpl)st1.getSubject(), (BNodeImpl)st2.getSubject()), new Statement[] {st1,st2});
				}
				
//				System.out.println(st1.getSubject().getClass().getName()+" "+st1.getPredicate().getClass().getName()+" "+st1.getObject().getClass().getName()+" "+"\n"+ st2.getSubject().getClass().getName()+" "+st2.getPredicate().getClass().getName()+" "+st2.getObject().getClass().getName());
//				System.out.println(st1.getSubject()+" "+st1.getPredicate()+" "+st1.getObject()+" "+"\n"+ st2.getSubject()+" "+st2.getPredicate()+" "+st2.getObject());
//				System.out.println();
			}
			
			for(String postLookup:postLookupArr){
				Statement[] checkStatements = stmtObjRefHash.get(postLookup);
				if(checkStatements == null){
					return false;
				}
				if(!compareStatementsExcludeBNode(checkStatements[0], checkStatements[1])){
					return false;
				}
			}

			return true;
		}
		return false;
	}
	
	public int hashCode() { return statements.hashCode(); }
	
	private String makeStatementKey(BNodeImpl bnode1,BNodeImpl bnode2){
		if(bnode1.getID().compareTo(bnode2.getID()) > 0){
			return bnode1.getID()+"_"+bnode2.getID();
		}else{
			return bnode2.getID()+"_"+bnode1.getID();
		}
	}
	private boolean compareStatementsExcludeBNode(Statement st1,Statement st2){
			if(!(st1.getObject().getClass() == st2.getObject().getClass() &&
				st1.getPredicate().getClass() == st2.getPredicate().getClass() &&
				st1.getSubject().getClass() == st2.getSubject().getClass())){
				return false;
			}
			
			boolean bObjectIsBNode = st1.getObject() instanceof BNodeImpl && st2.getObject() instanceof BNodeImpl;
			boolean bSubjectIsBNode = st1.getSubject() instanceof BNodeImpl && st2.getSubject() instanceof BNodeImpl;
			boolean bPredicateIsBNode = st1.getPredicate() instanceof BNodeImpl && st2.getPredicate() instanceof BNodeImpl;
			
			if(bObjectIsBNode && bSubjectIsBNode && bPredicateIsBNode){
				throw new RuntimeException("Unexpected Statement with all elements BNode");
			}
			if(bSubjectIsBNode && bObjectIsBNode){
				return st1.getPredicate().equals(st2.getPredicate());
			}else if(bObjectIsBNode){
				return st1.getPredicate().equals(st2.getPredicate()) && st1.getSubject().equals(st2.getSubject());
			}else if(bSubjectIsBNode){
				return st1.getPredicate().equals(st2.getPredicate()) && st1.getObject().equals(st2.getObject());
			}else{
				return st1.getSubject().equals(st2.getSubject()) && st1.getPredicate().equals(st2.getPredicate()) && st1.getObject().equals(st2.getObject());
			}
	}

}
