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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Stores information about a reaction step
 * @author mlevin
 *
 */
class ReactStep {
	
	private Set<String> reactantNames = new HashSet<String>();
	private Set<String> productNames = new HashSet<String>();
	private Element reactNode;
	private boolean oneWay;
	private Element redundant;
	
	/**
	 * @param reaction SBML document <reaction> node
	 * @return
	 */
	public static ReactStep makePotentiallyReversible(Element reaction) {
		if( 
				null == reaction ||
				! SbmlElements.React_tag.equals( reaction.getTagName() ) ||
				isReversible(reaction) 
				) {
			return null;
		}
		
		try {
			return new ReactStep(reaction);			
		} catch( Exception e) {
			return null;
		}
	}
	
	public static boolean isReversible(Element reaction) {
		Attr attr = reaction.getAttributeNode(SbmlElements.Rev_attrib);
		if( attr != null && "true".equals( attr.getValue() ) ) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * @param reaction SBML document <reaction> node
	 */
	public ReactStep(Element reaction) {
		oneWay = ! isReversible(reaction);
		reactNode = reaction;
		Element e;
		NodeList nl;
		
		//store Reactants
		nl = reaction.getElementsByTagName(SbmlElements.ListofReactants_tag);
		e = (Element)nl.item(0);
		nl = e.getElementsByTagName(SbmlElements.SpRef_tag);
		for( int i = 0, max = nl.getLength(); i < max; ++i ) {
			e = (Element)nl.item(i);
			Attr attr = e.getAttributeNode(SbmlElements.Species_attr);
			String specieId = attr.getValue();
			reactantNames.add(specieId);
		}
		
		//store Products
		nl = reaction.getElementsByTagName(SbmlElements.ListOfProds_tag);
		e = (Element)nl.item(0);
		nl = e.getElementsByTagName(SbmlElements.SpRef_tag);
		for( int i = 0, max = nl.getLength(); i < max; ++i ) {
			e = (Element)nl.item(i);
			Attr attr = e.getAttributeNode(SbmlElements.Species_attr);
			String specieId = attr.getValue();
			productNames.add(specieId);
		}
		
	}

	/** 
	 * @param rs
	 * @return true if rs is the reverse of this ReactionStep
	 */
	public boolean isAntiparallel(ReactStep rs) {
		return 
			 isOneWay() &&
			reactantNames.equals(rs.productNames) &&
			productNames.equals(rs.reactantNames);
		
	}
	
	public boolean isOneWay() {
		return oneWay;
	}
	
	/** Attempts to merge two reaction nodes into one reversible reaction
	 * if successful, store rs.dom in <code>redundant</code> in order to 
	 * be able to subsequently eliminate rs.dom from the original document
	 * @param rs
	 * @return true if successful
	 */
	public boolean merge(ReactStep rs, Document doc) {
		if( ! isAntiparallel(rs) ) return false;

		try {
			//fuse kinetic laws
			Element e;
			NodeList nl;
			nl = reactNode.getElementsByTagName(SbmlElements.KinLaw_tag);
			e = (Element)nl.item(0);	//kinetic law
			nl = e.getElementsByTagName(SbmlElements.Math_tag);
			Element math = (Element)nl.item(0);
			
			Element apply = doc.createElement(SbmlElements.Apply_tag);

			Element minus = doc.createElement(SbmlElements.Minus_tag);
			apply.appendChild(minus);
			
			//copy forward rate expressions
			nl = math.getChildNodes();
			for( int i = 0, max = nl.getLength(); i < max; ++i ) {
				Node n = nl.item(i);
				if( n.getNodeType() == Node.ELEMENT_NODE ) {
					n = n.cloneNode(true);
					apply.appendChild(n);
				}
			}

			//copy reverse rate expressions
			nl = rs.reactNode.getElementsByTagName(SbmlElements.KinLaw_tag);
			e = (Element)nl.item(0);	//kinetic law
			nl = e.getElementsByTagName(SbmlElements.Math_tag);
			Element mathTag2 = (Element)nl.item(0);
			nl = mathTag2.getChildNodes();
			for( int i = 0, max = nl.getLength(); i < max; ++i ) {
				Node n = nl.item(i);
				if( n.getNodeType() == Node.ELEMENT_NODE ) {
					n = n.cloneNode(true);
					apply.appendChild(n);
				}
			}

			nl = math.getElementsByTagName(SbmlElements.Apply_tag);
			Node applOld = nl.item(0);
			
			math.removeChild(applOld);
			math.appendChild(apply);
			
			reactNode.removeAttribute(SbmlElements.Rev_attrib);
			
			String comment = "merged with '" + rs.reactNode.getAttribute("id") + "'";
			reactNode.appendChild(doc.createComment(comment));

		} catch( Exception e) {
			e.printStackTrace();
			return false;
		}

		redundant = rs.reactNode;
		return true;
	}
	
	public void commitToDom() {
		if( null != redundant ) {
			Node parent = redundant.getParentNode();
			parent.removeChild(redundant);
		}
	}
	
	public String toString() {
		return reactantNames.toString() + " -> " + productNames.toString();
	}

	// convert a reaction kinetics from lumped to distributed (general) by multiplying reaction rate with compartment size
	public static ReactStep adjustReactionRate(Element r, Document doc) {
		ReactStep rs = new ReactStep(r);
		
		// get compartment in which reaction takes place
		String compartmentName = getReactionCompartment(rs, doc);
		
		// multiply reaction rate with this compartment name
		try {
			NodeList nl = rs.reactNode.getElementsByTagName(SbmlElements.KinLaw_tag);
			Element e = (Element)nl.item(0);	//kinetic law
			nl = e.getElementsByTagName(SbmlElements.Math_tag);
			Element math = (Element)nl.item(0);

			// create a new <apply> element
			Element apply = doc.createElement(SbmlElements.Apply_tag);
			// create a new 'times' element & add it to <apply> element
			Element times = doc.createElement(SbmlElements.Times_tag);
			apply.appendChild(times);
			
			// add node for 'compartment name' to 
			Element compIdNode = doc.createElement(SbmlElements.CiMath_tag);
			compIdNode.appendChild(doc.createTextNode(compartmentName));
			apply.appendChild(compIdNode);

			// take original <apply> node from <math> tag 
			nl = math.getElementsByTagName(SbmlElements.Apply_tag);
			Node origApplyNode = nl.item(0);
			// remove it from mathNode
			math.removeChild(origApplyNode);
			// add it to new <apply> node
			apply.appendChild(origApplyNode);
			// add new <apply> node to math node
			math.appendChild(apply);
			
			String comment = "Adjusted reaction kinetics of '" + rs.reactNode.getAttribute("id") + "' from lumped to distributed kinetics";
			rs.reactNode.appendChild(doc.createComment(comment));

		} catch( Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Unable to convert reaction kinetics from lumped to distributed.");
		}

		return rs;
	}

	// determines compartment in which reaction takes place.
	private static String getReactionCompartment(ReactStep rs, Document doc) {
		String rxnCompName = null;
	    HashSet<String> refSpeciesNameHash = new HashSet<String>(); 
	    getReferencedSpecies(rs, refSpeciesNameHash);
	    
	    java.util.Iterator<String> refSpIterator = refSpeciesNameHash.iterator();
	    HashSet<String> compartmentNamesHash = new HashSet<String>();
	    while (refSpIterator.hasNext()) {
	    	String spName = refSpIterator.next();
	    	String rxnCompartmentName = getSpeciesCompartmentName(spName, doc);
	    	if (rxnCompartmentName != null) {
	    		compartmentNamesHash.add(rxnCompartmentName);
	    	}
	    }
	    
	    if (compartmentNamesHash.size() == 1) {
	    	rxnCompName = compartmentNamesHash.iterator().next(); 
	    	return rxnCompName;
	    } else {
	    	// Check adjacency of compartments of reactants/products/modifiers
	    	if (compartmentNamesHash.size() > 3) {
	    		throw new RuntimeException("Cannot resolve location of reaction : " + rs.reactNode.getAttribute(SbmlElements.Id_attrib));
	    	}
	    	String[] compNames = compartmentNamesHash.toArray(new String[compartmentNamesHash.size()]);
	    	if (compNames.length == 2) {
				Element compartment1 = getCompartmentElement(compNames[0], doc);
				Element compartment2 = getCompartmentElement(compNames[1], doc);
				String comp1_id = compartment1.getAttribute(SbmlElements.Id_attrib);
				String comp1_outside = compartment1.getAttribute(SbmlElements.CompOutside_attrib);
				String comp1_spDimStr = compartment1.getAttribute(SbmlElements.CompSpatialDim_attrib);
				int comp1_spDim = comp1_spDimStr.equals("") ? 3 : Integer.parseInt(comp1_spDimStr);
				String comp2_id = compartment2.getAttribute(SbmlElements.Id_attrib);
				String comp2_outside = compartment2.getAttribute(SbmlElements.CompOutside_attrib);
				String comp2_spDimStr = compartment2.getAttribute(SbmlElements.CompSpatialDim_attrib);
				int comp2_spDim = comp2_spDimStr.equals("") ? 3 : Integer.parseInt(comp2_spDimStr);

				boolean bAdjacent = comp1_outside.equals(comp2_id) || comp2_outside.equals(comp1_id);
	    		if ((comp1_spDim == 2  && comp2_spDim == 3) && bAdjacent) {
	    			rxnCompName = comp1_id;
	    		} else if ((comp2_spDim == 2  && comp1_spDim == 3) && bAdjacent) {
	    			rxnCompName = comp2_id;
	    		} else if (comp1_spDim == 3  && comp2_spDim == 3) {
	    			Element outside1 = null;
	    			Element outside2 = null;
	    			if ((comp1_outside != null) && !(comp1_outside.equals("")) ) {
	    				outside1 = getCompartmentElement(comp1_outside, doc);
	    			} 
	    			if ((comp2_outside != null) && !(comp2_outside.equals("")) ) {
	    				outside2 = getCompartmentElement(comp2_outside, doc);
	    			}
	    			
	    			String outside1_outside = "";
	    			int outside1_spDim = 3;
	    			String outside2_outside = "";
	    			int outside2_spDim = 3;
	    			if (outside1 != null) {
		    			outside1_outside = outside1.getAttribute(SbmlElements.CompOutside_attrib);
						String sp_dim = outside1.getAttribute(SbmlElements.CompSpatialDim_attrib);
						if (sp_dim != null && !sp_dim.equals("")) {
							outside1_spDim = Integer.parseInt(sp_dim);
						}
	    			}
	    			if (outside2 != null) {
	    				outside2_outside = outside2.getAttribute(SbmlElements.CompOutside_attrib);
	    				String sp_dim = outside2.getAttribute(SbmlElements.CompSpatialDim_attrib);
						if (sp_dim != null && !sp_dim.equals("")) {
							outside2_spDim = Integer.parseInt(sp_dim);
						}
	    			}
 
					if ( (outside1 != null) && ((outside1_spDim == 2) && (comp2_id.equals(outside1_outside))) ) {
						rxnCompName = outside1.getAttribute(SbmlElements.Id_attrib);
					} else if ( (outside2 != null) && ((outside2_spDim == 2) && (comp1_id.equals(outside2_outside))) ) {
						rxnCompName = outside2.getAttribute(SbmlElements.Id_attrib);
					} else {

						System.out.println("Error!");
					}
	    		}
	    	} else if (compNames.length == 3) {
	    		int dim2 = 0;
	    		int dim3 = 0;
	    		int membraneIndx = -1;
	    		for (int i = 0; i < compNames.length; i++) {
	    			Element comp = getCompartmentElement(compNames[i], doc);
	    			if (Integer.parseInt(comp.getAttribute(SbmlElements.CompSpatialDim_attrib)) == 2) {
	    				dim2++;
	    				membraneIndx = i;
	    			} else if (Integer.parseInt(comp.getAttribute(SbmlElements.CompSpatialDim_attrib)) == 3) {
	    				dim3++;
	    			}
				}
	    		if (dim2 != 1 || dim3 != 2) {
	    	   		throw new RuntimeException("Cannot resolve location of reaction : " + rs.reactNode.getAttribute(SbmlElements.Id_attrib));
	    		}
	    		Element membraneComp = getCompartmentElement(compNames[membraneIndx], doc);
	    		Element volComp1 = getCompartmentElement(compNames[(membraneIndx+1)%3], doc);
	    		Element volComp2 = getCompartmentElement(compNames[(membraneIndx+2)%3], doc);
	    		String memComp_id = membraneComp.getAttribute(SbmlElements.Id_attrib); 
	    		String memComp_outside = membraneComp.getAttribute(SbmlElements.CompOutside_attrib); 
	    		String volComp1_id = volComp1.getAttribute(SbmlElements.Id_attrib);
	    		String volComp1_outside = volComp1.getAttribute(SbmlElements.CompOutside_attrib); 
	    		String volComp2_id = volComp2.getAttribute(SbmlElements.Id_attrib);
	    		String volComp2_outside = volComp2.getAttribute(SbmlElements.CompOutside_attrib); 
	    		if ( (volComp1_id.equals(memComp_outside) && memComp_id.equals(volComp2_outside)) ||  
	    			 (volComp2_id.equals(memComp_outside) && memComp_id.equals(volComp1_outside)) ) {
	    					rxnCompName = memComp_id;
	    		} 
	    	} 
	    	if (rxnCompName == null) {
	    		throw new RuntimeException("Cannot resolve location of reaction : " + rs.reactNode.getAttribute(SbmlElements.Id_attrib));
	    	} 
	    	return rxnCompName;
	    }
	}

	// get the compartment element for given compartment name 'cName'
	private static Element getCompartmentElement(String cName, Document doc) {
		NodeList listOfCompartments = doc.getElementsByTagName(SbmlElements.Compartment_tag);
		int nComps = listOfCompartments.getLength();
		for( int i = 0; i < nComps; ++i ) {
			Element c = (Element)listOfCompartments.item(i);
			String id = c.getAttribute(SbmlElements.Id_attrib);
			if (id.equals(cName)) {
				return c;
			}
		}
		return null;
	}

	// get the name of the compartment for given species 'spName'
	private static String getSpeciesCompartmentName(String spName, Document doc) {
		NodeList listOfSpecies = doc.getElementsByTagName(SbmlElements.Species_tag);
		int nSpecies = listOfSpecies.getLength();
		for( int i = 0; i < nSpecies; ++i ) {
			Element s = (Element)listOfSpecies.item(i);
			String name = s.getAttribute(SbmlElements.Id_attrib);
			if (name.equals(spName)) {
				String compartmentName = s.getAttribute(SbmlElements.Compart_attrib);
				return compartmentName;
			}
		}
		return null;
	}

	// get the species referenced in the reaction (reactants, products, rate law).
	private static void getReferencedSpecies(ReactStep rs, HashSet<String> refSpeciesNameHash) {
		// get all species referenced in listOfReactants
		Iterator<String> iter = rs.reactantNames.iterator();
		while (iter.hasNext()) {
			refSpeciesNameHash.add(iter.next());
		}
		// get all species referenced in listOfProducts
		iter = rs.productNames.iterator();
		while (iter.hasNext()) {
			refSpeciesNameHash.add(iter.next());
		}
		/*
		// get all species referenced in reaction rate law
		if (rs.reactNode.getKineticLaw() != null) {
			Expression rateExpression = getExpressionFromFormula(sbmlRxn.getKineticLaw().getMath());
			getReferencedSpeciesInExpr(rateExpression, refSpeciesNameHash);
		} 
		*/
	}
	
}
