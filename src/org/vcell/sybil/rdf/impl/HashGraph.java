package org.vcell.sybil.rdf.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.vcell.sybil.rdf.StatementComparator;

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
			return statements.equals(((HashGraph) o).statements);
		}
		return false;
	}
	
	public int hashCode() { return statements.hashCode(); }

}
