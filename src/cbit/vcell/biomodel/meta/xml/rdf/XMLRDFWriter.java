/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.biomodel.meta.xml.rdf;

import org.jdom.Element;
import org.vcell.sybil.models.annotate.Model2JDOM;

import com.hp.hpl.jena.rdf.model.Model;


/**
 * Turns a Jena Model into a JDOM Element
 * @author ruebenacker
 *
 */

public class XMLRDFWriter extends XMLRDF {
	
	public static Element createElement(Model rdf, String baseURI) {
		Model2JDOM model2jdom = new Model2JDOM();
		model2jdom.addModel(rdf, baseURI);
		return model2jdom.root();
	}
	
}
