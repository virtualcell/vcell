package org.vcell.cellml;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.parser.MathMLTags;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ContentFilter;
import org.jdom.filter.ElementFilter;
import org.vcell.util.TokenMangler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
/**
 * Utility translation class, mainly trims the source XML document to the required elements that are of interest in the
 translation.
 * Creation date: (8/12/2003 10:34:56 AM)
 * @author: Rashad Badrawi
 */
public class TransFilter {

	public static final String QUANCELLVC_MANGLE = "1";
	public static final String QUALCELLVC_MANGLE = "2";
	public static final String VCQUALCELL_MANGLE = "3";
	public static final String VCQUANCELL_MANGLE = "4";
	private String [] elements;
	private String [] atts;
	private boolean mangle;
	private String mangleType;
	private Hashtable hash;
	private ArrayList ignoredAtts;
	private ArrayList ignoredElements;
	//a temporary variable for reaction names longer than 30 characters. 
	private int cnt;

//I don't know if it will be needed. 
	private TransFilter(String [] elements) {

		this(elements, null, null);
	}


	public TransFilter(String [] elements, String [] atts) {

		this(elements, atts, null);
	}


	public TransFilter(String [] elements, String [] atts, String mangleType) {

		if (mangleType == null) {
			mangle = false;
		} else {
			mangle = true;
			this.mangleType = mangleType;
		}
		if (elements != null) {
			Arrays.sort(elements);
			this.elements = elements;
		}
		if (atts != null) {
			Arrays.sort(atts);
			this.atts = atts;
		} 
		hash = new Hashtable();
		ignoredAtts = new ArrayList();
		ignoredElements = new ArrayList();
	}


	public void filter(Element e) {

		filterTree(e);
		if (mangle)
			mangle(e);
	}


/**
 @deprecated - need to revisit CellML
**/
private void filterTree(Element e) {

	ArrayList alist = new ArrayList(e.getAttributes());
	for (int i = 0; i < alist.size(); i++) {
		Attribute att = (Attribute)alist.get(i);
		if (!matchesAttribute(att)) {
			if (!ignoredAtts.contains(e.getName() + "." + att.getName())) {
				ignoredAtts.add(e.getName() + "." + att.getName());
			}
			e.removeAttribute(att);
			//System.out.println("\tRemoved att: " + att.getName());
		} else {
			if (mangle) {
				mangle(att);
			}
		}
	}
	//skip contents of math/annotation elements in CELLML.
	ArrayList elist = new ArrayList(e.getChildren());		
	for (int j = 0; j < elist.size(); j++) { 
		Element child = (Element)elist.get(j);
		//also works for the CellML mathML.
		/*if (child.getName().equals(CELLMLTags.MATH) || child.getName().equals(CELLMLTags.ANNOTATION) ||
			child.getName().equals(SBMLTags.NOTES) || child.getName().equals(XMLTags.AnnotationTag))             */
		if (child.getName().equals(XMLTags.AnnotationTag)) {
			continue;
		}
		if (matchesElement(child)) {
			filterTree(child);
		} else { 
			//System.out.println("Removed el.: " + child.getName());
			if (!ignoredElements.contains(child.getName())) {
				ignoredElements.add(child.getName());
			}
			e.removeContent(child);
		}
	}
}


	protected String [] getIgnoredAtts() {

		return (String [])ignoredAtts.toArray(new String[ignoredAtts.size()]);
	}


	protected String [] getIgnoredElements() {

		return (String [])ignoredElements.toArray(new String[ignoredElements.size()]);		
	}


	 protected static boolean isFloat(String value) {

        boolean bool = true;
        StringBuffer temp = new StringBuffer(value.trim());
        for (int i = 0; i < temp.length(); i++) {
            if (temp.charAt(i) == ' ')
                temp = temp.deleteCharAt(i);
        }
        try {
            Float f = new Float(temp.toString());
            // a hack for formats like 1f or 2D
            int len = temp.length();
            if (Character.isLetter(temp.charAt(len - 1)))
            	bool = false;
        } catch (NumberFormatException e) {
            //System.out.println("exception: Not a number" + temp);
            //needed because of purely numeric expressions
        	for (int i = 0; i < temp.length(); i++) {
        		if (Character.isLetter(temp.charAt(i))) {
            		bool = false;
					break;
            	}
        	}
        }
        
       	return bool;
    }


//currently limited to mangling VCSB, and SBVC
//dropped from original List for VCSB:  XMLTags.StoichiometryAttrTag,XMLTags.KineticsTypeAttrTag, 
//XMLTags.FluxCarrierValenceAttrTag, XMLTags.FluxOptionAttrTag, XMLTags.ForceConstantAttrTag, XMLTags.HasOverrideAttrTag,
//XMLTags.DimensionAttrTag
	private void mangle(Attribute att) {

		if (mangleType.equals(QUANCELLVC_MANGLE) || mangleType.equals(QUALCELLVC_MANGLE))
			mangleCELLVC(att);
		else if (mangleType.equals(VCQUANCELL_MANGLE) || mangleType.equals(VCQUALCELL_MANGLE))
			mangleVCCELL(att);
		else
			return;
	}


	//This method is needed, in case an identifier in a formula was mangled, Annotations and Initial also have text, 
	//but are not mangled (in case of SBML)
	private void mangle(Element e) {

    	if (mangleType.equals(QUANCELLVC_MANGLE) || mangleType.equals(QUALCELLVC_MANGLE))
			mangleCELLVC(e);
		else if (mangleType.equals(VCQUANCELL_MANGLE) || mangleType.equals(VCQUALCELL_MANGLE))
			mangleVCCELL(e);
		else
			return;	
	}


	private void mangleCELLVC(Attribute att) {

		String oldValue = att.getValue().trim();
		String newValue;
		
		if (isFloat(oldValue))
			return;
		
		if (Character.isDigit(oldValue.charAt(0))) {
			newValue = "_" + oldValue;
			att.setValue(newValue);
			hash.put(oldValue, newValue);
		} else if (oldValue.equals("x") || oldValue.equals("y") || oldValue.equals("z")) {
			newValue = "_" + oldValue;
			att.setValue(newValue);
			hash.put(oldValue, newValue);
		}
	}


	//mainly mangles the ci element.
	private void mangleCELLVC(Element e) {

    	JDOMTreeWalker walker;
    	Element temp, math;
    	String key, value;
    	Iterator i = hash.keySet().iterator();
    	while (i.hasNext()) {
	    	key = (String)i.next();
	    	value = (String)hash.get(key);
    		walker = new JDOMTreeWalker(e, new ElementFilter(MathMLTags.IDENTIFIER, Namespace.getNamespace(Translator.MATHML_NS)));
    		while (walker.hasNext()) {
				temp = (Element)walker.next();
	      		//System.out.println(key + " " + value + " " + temp.getName() + " " + temp.getTextTrim());
      			if (temp.getTextTrim().equals(key))     
					temp.setText(value);
			}
    	}
	}


	private void mangleVCCELL(Attribute att) {

		//do not mangle unit symbols, since they will be not be translated. Also, allow spaces in param roles. 
		if (att.getName().equals(XMLTags.VCUnitDefinitionAttrTag) || att.getName().equals(XMLTags.ParamRoleAttrTag)) {
			return;
		}
		String value = att.getValue();
		if (value.length() == 0)
			return;
		if (isFloat(value))
			return;
		TokenMangler tm = new TokenMangler();
		String nvalue = tm.mangleToSName(value);
		if (nvalue.equals(value))
			return;
		else {
			att.setValue(nvalue);
			hash.put(value, nvalue);
			//System.out.println("TransFilter mangle:" + att.getName() + " " + value + " " + nvalue);
		}
	}


	private void mangleVCCELL(Element e) {

		String mangleE[] = { XMLTags.ParameterTag, XMLTags.RateTag, XMLTags.InitialTag, XMLTags.VolumeVariableTag, 
							 XMLTags.ConstantTag, XMLTags.FunctionTag};
    	JDOMTreeWalker walker;
    	Element temp;
    	String key, value;
    	Iterator i = hash.keySet().iterator();
    	while (i.hasNext()) {
	    	key = (String)i.next();
	    	value = (String)hash.get(key);
    		for (int j = 0; j < mangleE.length; j++) {
    			walker = new JDOMTreeWalker(e, new ElementFilter(mangleE[j]));
    			while (walker.hasNext()) {
					temp = (Element)walker.next();
					temp.setText(replaceString(temp.getText(), key, value));
    			}
    		}
    	}
	}


	private boolean matchesAttribute(org.jdom.Attribute a) {

		if (atts == null)
			return true;
			
		String aName = a.getName();
		
		return Arrays.binarySearch(atts, aName) >= 0;
	}


	private boolean matchesElement(Element e) {

		if (elements == null)
			return true;
			
		String eName = e.getName();

		return Arrays.binarySearch(elements, eName) >= 0 ;
	}


	//similar to the jdk1.4 String.replaceAll()
	protected static String replaceString(String value, String oldName, String newName) {

		StringBuffer buf; 
		int index = 0; 

		if (isFloat(value) || oldName.equals(newName))
			return value;
		//keep starting from index to avoid infinite loops if there is a problem
		//System.out.println("Before:" + value + " " + oldName + " " + newName);
		while ((index = value.indexOf(oldName, index)) != -1) {
			if ( (index == 0 || !Character.isJavaIdentifierPart(value.charAt(index - 1))) &&
				 ((index + oldName.length() == value.length()) ||
				   !Character.isJavaIdentifierPart(value.charAt(index + oldName.length()))) ){
				buf = new StringBuffer(value);
				buf.replace(index, index + oldName.length(), newName);
				value = buf.toString();
				//System.out.println(value);
				 index += newName.length() - oldName.length();        //new name is longer
			} else {
				 index += oldName.length();   
			}
		}
		//System.out.println("After:" + value);
		
		return value;
	}
}