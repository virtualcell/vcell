package org.vcell.sybil.rdf.impl;

import java.util.Iterator;

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

public class TestHashGraph {

	public static void main(String[] args) {
		Graph graph = new HashGraph();
		// TODO Auto-generated method stub
		URI a = graph.getValueFactory().createURI("ex:a");
		URI b = graph.getValueFactory().createURI("ex:b");
		URI c = graph.getValueFactory().createURI("ex:c");
		URI d = graph.getValueFactory().createURI("ex:d");
		URI e = graph.getValueFactory().createURI("ex:e");
		URI f = graph.getValueFactory().createURI("ex:f");
		URI g = graph.getValueFactory().createURI("ex:g");
		graph.add(a, b, c);
		graph.add(a, b, d);
		graph.add(a, b, e);
		graph.add(a, b, f);
		graph.add(a, b, g);
		graph.add(a, c, d);
		graph.add(a, c, e);
		graph.add(a, c, f);
		graph.add(a, c, g);
		graph.add(a, d, e);
		graph.add(a, d, f);
		graph.add(a, d, g);
		graph.add(a, e, f);
		graph.add(a, e, g);
		graph.add(b, c, d);
		graph.add(b, c, e);
		graph.add(b, c, f);
		graph.add(b, c, g);
		graph.add(b, d, e);
		graph.add(b, d, f);
		graph.add(b, d, g);
		graph.add(b, e, f);
		graph.add(b, e, g);
		graph.add(b, f, g);
		graph.add(c, d, e);
		graph.add(c, d, f);
		graph.add(c, d, g);
		graph.add(c, e, f);
		graph.add(c, e, g);
		graph.add(c, f, g);
		graph.add(d, e, f);
		graph.add(d, e, g);
		graph.add(e, f, g);
		Iterator<Statement> iterator = graph.match(a, null, null);
		int count = 0;
		while(iterator.hasNext()) {
			Statement statement = iterator.next();
			iterator.remove();
			++count;
			System.out.println(count + " : " + statement);
		}
		for(Statement statement : graph) {
			System.out.println(statement);
		}
	}

}
