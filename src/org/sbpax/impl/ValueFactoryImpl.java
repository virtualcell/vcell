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

import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.XMLSchema;
import org.sbpax.util.StringUtil;

public class ValueFactoryImpl implements ValueFactory {

	protected final BNodeIDGenerator bNodeIDGenerator;
	
	public ValueFactoryImpl(BNodeIDGenerator bNodeIDGenerator) { this.bNodeIDGenerator = bNodeIDGenerator; }
	public ValueFactoryImpl() { this(new BNodeIDGeneratorImpl(StringUtil.createMnemonicRandomString(System.currentTimeMillis()))); }
	
	public BNode createBNode() { return new BNodeImpl(bNodeIDGenerator.getNextBNodeID()); }
	public BNode createBNode(String id) { return new BNodeImpl(id); }
	public Literal createLiteral(String label) { return new LiteralImpl(label); }
	public Literal createLiteral(boolean b) { return new LiteralImpl("" + b, XMLSchema.BOOLEAN); }
	public Literal createLiteral(byte b) { return new LiteralImpl("" + b, XMLSchema.BYTE); }
	public Literal createLiteral(short s) { return new LiteralImpl("" + s, XMLSchema.SHORT); }
	public Literal createLiteral(int i) { return new LiteralImpl("" + i, XMLSchema.INT); }
	public Literal createLiteral(long l) { return new LiteralImpl("" + l, XMLSchema.LONG); }
	public Literal createLiteral(float f) { return new LiteralImpl("" + f, XMLSchema.FLOAT); }
	public Literal createLiteral(double d) { return new LiteralImpl("" + d, XMLSchema.DOUBLE); }
	public Literal createLiteral(XMLGregorianCalendar calendar) { return new LiteralImpl(calendar.toXMLFormat(), XMLSchema.DATE); }
	public Literal createLiteral(String label, String language) { return new LiteralImpl(label, language); }
	public Literal createLiteral(String label, URI datatype) { return new LiteralImpl(label, datatype); }

	public Statement createStatement(Resource subject, URI predicate, Value object) {
		return new StatementImpl(subject, predicate, object);
	}

	public Statement createStatement(Resource subject, URI predicate, Value object, Resource context) {
		return new StatementImpl(subject, predicate, object);
	}

	public URI createURI(String uri) { return new URIImpl(uri); }
	public URI createURI(String nameSpace, String localName) { return new URIImpl(nameSpace + localName); }

}
