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
	
	/** map each compartment id to its enclosing compartment id, the outmost 
	 * compartment shall be paired with "";
	 */
	private Map<String, String> compIds = null;
	
	/**
	 * @return a list of string triplets.  The first string is a regular expression match
	 * pattern, the second string is the name of destination compartment, the third is the name 
	 * of enclosing compartment.  Species with names that match the pattern can 
	 * be moved to the destination compartment that is located inside enclosing 
	 * compartment.  One destination compartment should be the top-level (outmost)
	 * its enclosing compartment should be null.
	 */
	public static List<String[]> getDefaultCompartmentPattern() {
		List<String[] > list = new ArrayList<String[] >();
		
		list.add(new String[] {"loc\\([^\\)]*?l~N", "Nucleus", "Cytoplasm"});
		list.add(new String[] {"loc\\([^\\)]*?l~C", "Cytoplasm", "environment"});
		
		return list;
	}
	
	public String getName() {return Name;}
	protected int countParameters() {	return 3;}
	
	/** Adds a rule for re-assigning species to different compartments based on 
	 * specie name
	 * @param str array of three strings is expected
	 * first is regex name pattern; if specie name matches this pattern the specie will be 
	 * moved to the specified compartment;
	 * second is compartment id
	 * third is id of the enclosing compartment, shall be "" for one 
	 * outmost compartment 
	 */
	public void addTransformation(String[] str) {
		super.addTransformation(str);
		if( compartmentNamePattern == null ) {
			compartmentNamePattern = new ArrayList<Pair<Pattern, String> >();
			compIds = new HashMap<String, String>();
		}
		
		String pattern = str[0];
		String compartment = str[1];
		String enclosed = str[2];
		
		Pattern p = Pattern.compile(pattern);
		compartmentNamePattern.add(new Pair<Pattern, String>(p, compartment) );
		
		compIds.put(compartment, enclosed);
	}
	
	private void ensureSingleTopLevel() {
		String topLevel = null;
		
		for (Iterator<Map.Entry<String, String>> iter = compIds.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<String, String> e = (Map.Entry<String, String>) iter.next();
			String compId = e.getKey();
			String encl = e.getValue();
			
			if( null == encl || encl.length() == 0 ) {
				if( null != topLevel ) {
					String msg = "multiple top-level compartments found: '" + topLevel + 
					"' and '" + compId + "'";
					throw new Exceptn(msg);
				}
				topLevel = compId;
			} else if( ! compIds.containsKey(encl) ) {
					compIds.put(encl, "");
			}
		}
	}
	
	private void replaceCompartmentList(Document doc) {
		Element listNew = doc.createElement(SbmlElements.ListOfCompartm_tag);
		
		for (Iterator<Map.Entry<String, String>> iter = compIds.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<String, String> e = (Map.Entry<String, String>) iter.next();
			String compId = e.getKey();
			String encl = e.getValue();
			
			Element c = doc.createElement(SbmlElements.Compartment_tag);
			c.setAttribute(SbmlElements.Id_attrib, compId);
			c.setAttribute(SbmlElements.Name_attrib, compId);
			c.setAttribute(SbmlElements.Size_attrib, "1.0");
			c.setAttribute(SbmlElements.Units_attrib, SbmlElements.Litre_val);
			
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

		ensureSingleTopLevel();

		try {
			replaceCompartmentList(doc);
		} catch(Exception e) {
			String msg = "error creating compartment list";
			throw new Exceptn(msg, e.getCause());
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
			throw new Exceptn(msg, e.getCause());
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



	public void setDefaultTransformations() {
		List<String[]> trList = getDefaultCompartmentPattern();
		for( int i = 0, max = trList.size(); i < max; ++i ) {
			String[] tr = trList.get(i);
			addTransformation(tr);
		}
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
