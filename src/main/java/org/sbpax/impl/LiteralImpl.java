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

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;

@SuppressWarnings("serial")
public class LiteralImpl implements Literal {

	protected final String label;
	protected final String language;
	protected final URI datatype;
	
	protected LiteralImpl(String label, String language, URI datatype) {
		this.label = label;
		this.language = language;
		this.datatype = datatype;
	}
	
	public LiteralImpl(String label) { this(label, null, null); }
	public LiteralImpl(String label, String language) { this(label, language, null); }
	public LiteralImpl(String label, URI datatype) { this(label, null, datatype); }	
	
	public String getLabel() { return label; }
	public String getLanguage() { return language; }
	public URI getDatatype() { return datatype; }

	public String stringValue() { return label; }
	public boolean booleanValue() { return Boolean.parseBoolean(label); }
	public byte byteValue() { return Byte.parseByte(label); }

	public XMLGregorianCalendar calendarValue() {
		XMLGregorianCalendar xmlGregorianCalendar = null;
		try {
			xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(label);
		} catch (DatatypeConfigurationException e) { }
		return xmlGregorianCalendar;
	}

	public BigDecimal decimalValue() { return new BigDecimal(label); }
	public double doubleValue() { return Double.parseDouble(label); }
	public float floatValue() { return Float.parseFloat(label); }
	public int intValue() { return Integer.parseInt(label); }
	public BigInteger integerValue() { return new BigInteger(label); }
	public long longValue() { return Long.parseLong(label); }
	public short shortValue() { return Short.parseShort(label); }
	public String toString() { return label; }
	
}
