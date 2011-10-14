/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

/**
 * 
 */
package cbit.vcell.xml.sbml_transform;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

/** Accumulates transformations planned for SBML document and performs them
 * @author mlevin
 *
 */
public class SbmlTransformer {
	
	private Document doc;
	
	private Map<String, ISbmlTransformer> transMap = new HashMap<String, ISbmlTransformer>();
	
	
//	private List<ISbmlTransformer> transforms = new ArrayList<ISbmlTransformer>();
	
	public SbmlTransformer(CharSequence sbml) {
		if( null == sbml || sbml.length() == 0 ) {
			String msg = "no model to import";
			throw new SbmlTransformException(msg);
		}

		doc = XmlTools.parseDom(sbml);
	}
	
	public SbmlTransformer(InputStream sbml) {
		doc = XmlTools.parseDom(sbml);
	}
	
	
	public void writeXml(OutputStream os) {
		XmlTools.serialize(doc, os);
	}
	
	public String getXmlString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		writeXml(baos);
		String str = baos.toString();
		return str;
	}
	
	public void addTransformation(List<String> strList) {
		String command = strList.remove(0);
		ISbmlTransformer tr = transMap.get(command);
		if( null == tr ) {
			tr = addNewTransformType(command);
		}
		
		String[] args = new String[strList.size()];
		args = strList.toArray(args);
		
		try {
		tr.addTransformation(args, "");
		} catch(Exception e) {
			String msg = "error in command \"" + command + "\"";
			throw new SbmlTransformException(msg, e);
		}
	}
	
	private ISbmlTransformer addNewTransformType(String name) {
		ISbmlTransformer tr = null;
		if( SpeciesRenamer.Name.equals(name) ) {
			tr = new SpeciesRenamer();
		} 
		else if( ReactionCollapser.Name.equals(name) ) {
				tr = new ReactionCollapser();
		} 
		else if( UnitTransformer.Name.equals(name) ) {
			tr = new UnitTransformer();
		} 
		else if( SpeciesCompartmentalizer.Name.equals(name) ) {
			tr = new SpeciesCompartmentalizer();
		} else {
			String msg = "unknown processing command '" + name + "'";
			throw new SbmlTransformException(msg);
		}
		
		transMap.put(name, tr);
		return tr;
	}


	/**
	 * 
	 */
	public void transform() {
		for( Iterator<ISbmlTransformer> iter = transMap.values().iterator(); iter.hasNext(); ) {
			ISbmlTransformer tr = iter.next();
			try {
			tr.transform(doc);
			} catch(Exception e) {
				String msg = "error performing transformation \"" + tr.getName() + "\"";
				throw new SbmlTransformException(msg, e);
			}
		}
	}

	
}
