/**
 * 
 */
package cbit.vcell.xml.sbml_transform;

import java.util.HashSet;
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
	
	private Set<String> rNames = new HashSet<String>();
	private Set<String> pNames = new HashSet<String>();
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
			rNames.add(specieId);
		}
		
		//store Products
		nl = reaction.getElementsByTagName(SbmlElements.ListOfProds_tag);
		e = (Element)nl.item(0);
		nl = e.getElementsByTagName(SbmlElements.SpRef_tag);
		for( int i = 0, max = nl.getLength(); i < max; ++i ) {
			e = (Element)nl.item(i);
			Attr attr = e.getAttributeNode(SbmlElements.Species_attr);
			String specieId = attr.getValue();
			pNames.add(specieId);
		}
		
	}
	
	/** 
	 * @param rs
	 * @return true if rs is the reverse of this ReactionStep
	 */
	public boolean isAntiparallel(ReactStep rs) {
		return 
			 isOneWay() &&
			rNames.equals(rs.pNames) &&
			pNames.equals(rs.rNames);
		
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
		return rNames.toString() + " -> " + pNames.toString();
	}
	
}
