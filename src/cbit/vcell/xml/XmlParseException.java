/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.xml;

/**
 * Insert the type's description here.
 * Creation date: (4/2/2001 5:38:04 PM)
 * @author: Daniel Lucio
 */
public class XmlParseException extends Exception {
/**
 * Default XmlParseException constructor.
 */
public XmlParseException() {
	super();
}
/**
 * XmlParseException constructor .
 * @param s java.lang.String
 */
public XmlParseException(String s) {
	super(s);
}

public XmlParseException(Throwable e) {
	super(e);
}

public XmlParseException(String s, Throwable e) {
	super(s, e);
}
}
