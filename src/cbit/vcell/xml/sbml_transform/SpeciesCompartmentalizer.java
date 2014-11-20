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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vcell.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Assignes compartment to each specie based on specie's name and using 
 * user-supplied regular expression match patterns
 * @author mlevin
 *
 */
class SpeciesCompartmentalizer extends ASbmlTransformer {
	public static final String Name = "compartmentalizeSpecies";

	/** map old specie name pattern to compartment ids 
	 */
	private List<Pair<Pattern, String> > compartmentNamePattern = null;
	private HashMap<String, Integer> compDimensionMap = null;
	
	/** map each compartment id to its enclosing compartment id, the outmost 
	 * compartment shall be paired with "";
	 */
	private Map<String, String> compIds = null;
	
	public String getName() {return Name;}
	public int countParameters() {	return 4;}
	
	/** Adds a rule for re-assigning species to different compartments based on 
	 * specie name
	 * @param str array of three strings is expected
	 * first is regex name pattern; if specie name matches this pattern the specie will be 
	 * moved to the specified compartment;
	 * second is compartment id
	 * third is id of the enclosing compartment, shall be "" for one 
	 * outmost compartment 
	 */
	public void addTransformation(String[] parameters, String comment) {
		if( compartmentNamePattern == null ) {
			compartmentNamePattern = new ArrayList<Pair<Pattern, String> >();
			compIds = new HashMap<String, String>();
			compDimensionMap = new HashMap<String, Integer>();
		}
		
		super.storeTransformationInfo(parameters, comment);
		String pattern = parameters[0];
		String dim = parameters[1];
		String compartment = parameters[2];
		String enclosed = parameters[3];
		
		Pattern p = Pattern.compile(pattern);
		compartmentNamePattern.add(new Pair<Pattern, String>(p, compartment) );
		
		String enclStored = compIds.get(compartment);
		
		if( null == compartment || compartment.length() == 0 )
			throw new SbmlTransformException("empty compartment name");

		// store the compartment - spDim values in hash
		compDimensionMap.put(compartment, Integer.valueOf(dim));
		
		if( null == enclStored || //compartment is not defined or
			enclStored.equals(enclosed)) //defined again inside same enclosure
		{
			compIds.put(compartment, enclosed);
		} else {
			StringBuffer buff = new StringBuffer();
			buff.append("compartment \"" + compartment + "\" defined twice: ");
			if( enclStored.length() == 0 ) buff.append("top level");
			else buff.append("inside \"" + enclStored + "\"");
			buff.append(" and ");
			if( enclosed.length() == 0 ) buff.append("top level");
			else buff.append("inside \"" + enclosed + "\"");
			throw new SbmlTransformException(buff.toString());
		}
	}
	
	private void ensureSingleTopLevel() {

		//put all enclosures as keys
		java.util.Set<String> enclSet = new java.util.HashSet<String>( compIds.values() );
		for( Iterator<String> iter = enclSet.iterator(); iter.hasNext(); ) {
			String encl = iter.next();
			if( encl.length() > 0 && ! compIds.containsKey(encl) ) 
				compIds.put(encl, "");
		}

		String topLevel = null;
		//check for multiple top-levels		
		for (Iterator<Map.Entry<String, String>> iter = compIds.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<String, String> e = (Map.Entry<String, String>) iter.next();
			String compId = e.getKey();
			String encl = e.getValue();

			if( encl.length() == 0 ) {
				if( null != topLevel ) {
					String msg = "compartments \"" + topLevel + "\" and \"" + compId + 
					"\" are both top-level";
					throw new Exceptn(msg);
				}
				topLevel = compId;
			}
		}
	}
	
	private void replaceCompartmentList(Document doc) {
		Element listNew = doc.createElement(SbmlElements.ListOfCompartm_tag);
		
		for (Iterator<Map.Entry<String, String>> iter = compIds.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<String, String> e = (Map.Entry<String, String>) iter.next();
			String compId = e.getKey();
			String encl = e.getValue();
			
			// get sp dim from compDimsensionHashMap
			Integer dimInt = compDimensionMap.get(compId);
			int spDim = dimInt.intValue();
			
			Element c = doc.createElement(SbmlElements.Compartment_tag);
			c.setAttribute(SbmlElements.Id_attrib, compId);
			c.setAttribute(SbmlElements.Name_attrib, compId);
			c.setAttribute(SbmlElements.CompSpatialDim_attrib, Integer.toString(spDim));
			c.setAttribute(SbmlElements.Size_attrib, "1.0");
			if (spDim == 3) {
				c.setAttribute(SbmlElements.Units_attrib, SbmlElements.Litre_val);
			} else if (spDim == 2) {
				c.setAttribute(SbmlElements.Units_attrib, SbmlElements.Um2_val);
			} else {
				throw new RuntimeException("Unknown spatial dimension for compartment - VCell only allows compartments of dimension 2 or 3");
			}
			
			if( null != encl && encl.length() > 0 ) {
				c.setAttribute(SbmlElements.CompOutside_attrib, encl);
			}
			listNew.appendChild(c);
		}
		NodeList nl = doc.getElementsByTagName(SbmlElements.ListOfCompartm_tag);
		Element listOld = (Element) nl.item(0);
		Node p = listOld.getParentNode();
		p.replaceChild(listNew, listOld);
	}
		
	private String getCompartmentId(String name) {
		for( int i = 0, max = compartmentNamePattern.size(); i < max; ++i ) {
			Pair<Pattern, String> pair = compartmentNamePattern.get(i);
			Matcher matcher = pair.one.matcher(name);
			if( matcher.find() ) {
				return pair.two;
			}
		}
		//no compartment pattern was found - set default
		return compartmentNamePattern.get(0).two;
	}
	
	public void transform(Document doc) {
		if( null== compartmentNamePattern ) return;


		try {
			ensureSingleTopLevel();
			replaceCompartmentList(doc);
		} catch(Exception e) {
			String msg = "error creating compartment list";
			throw new Exceptn(msg, e);
		}

		try {
			//change specie compartments
			NodeList nl = doc.getElementsByTagName(SbmlElements.Species_tag);
			for( int i = 0, max = nl.getLength(); i < max; ++i ) {
				Element e = (Element) nl.item(i);
				String name = e.getAttribute(SbmlElements.Name_attrib);
				String compartment = getCompartmentId(name);
				e.setAttribute(SbmlElements.Compart_attrib, compartment);
			}
			doc.normalize();
		} catch(Exception e) {
			String msg = "error changing specie compartments";
			throw new Exceptn(msg, e);
		}
	}


	public int countTransformations() {
		return compartmentNamePattern.size();
	}



	public String[] getTransformation(int i) {
		if( i >= 0 || i < compartmentNamePattern.size() ) {
			Pair<Pattern, String> p = compartmentNamePattern.get(i);
			String encl = compIds.get(p.two);
			return new String[] {p.one.pattern(), p.two, encl};
		}
		return new String[] {"","", ""};
	}



	public void removeTransformation(int i) {
		Pair<Pattern, String> p = compartmentNamePattern.remove(i);

		String compId = p.two;
		if( compId.length() == 0 ) return;

		//do NOT remove compartment if mentioned in another pattern
		for( int j = 0, max = compartmentNamePattern.size(); j < max; ++j ) {
			if( compartmentNamePattern.get(j).two.equals(compId) ) return;
		}

		//do NOT remove compartment if mentioned as an enclosure of another 
		//compartment
		for( 
				Iterator<Map.Entry<String, String>> iter = compIds.entrySet().iterator(); 
				iter.hasNext(); ) {
			Map.Entry<String, String> e = iter.next();
			if( e.getValue().equals(compId)) return;
		}
		compIds.remove(compId);
	}
	
	private static class Exceptn extends SbmlTransformException {
		private static final long serialVersionUID = -3943387264627605457L;
		private static final String messageDefault = "error asigning compartments";
		
		public Exceptn() {
			super(messageDefault);
		}

		public Exceptn(String message, Throwable cause) {
			super(message, cause);
		}

		public Exceptn(String message) {
			super(message);
		}

		public Exceptn(Throwable cause) {
			super(messageDefault, cause);
		}
		
		
	}

}
