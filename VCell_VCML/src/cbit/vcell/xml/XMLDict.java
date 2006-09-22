package cbit.vcell.xml;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
/**
 * A container pattern class, wraps around a Hashtable, used by the XMLReader. The format of the keys is:
 x_path_of_element + ":" + java_class_name + ":" + identifying_attribute (varies)
 The value is the instance of that object.
 The x path embeds the elements as well as their 'name' attribute value. Luckily, all of the relevant elements have
 this attribute: CompartmentSubDomain, Feature, FilamentVariable, FilamentRegionVariable, FluxReaction, SimpleReaction,
 MembraneVariable, MembraneRegionVariable, Species, SpeciesContext, SubVolume, VolumeVariable, VolumeRegionVariable, 
 
 * Creation date: (3/16/2004 1:02:18 PM)
 * @author: Rashad Badrawi
 */
 
public class XMLDict {

	private static final String XPATH_SEP = "/";
    private static Hashtable reHashBio;
    private static Hashtable reHashMath;
    private Hashtable hash;
        
    static {
		reHashBio = new Hashtable(15);
		reHashBio.put(XMLTags.SpeciesTag, XMLTags.ModelTag + XPATH_SEP + XMLTags.SpeciesTag);
		reHashBio.put(XMLTags.SpeciesContextTag, XMLTags.BioModelTag + XPATH_SEP + XMLTags.ModelTag + XPATH_SEP + XMLTags.SpeciesContextTag);
		reHashBio.put(XMLTags.FeatureTag, XMLTags.BioModelTag + XPATH_SEP + XMLTags.ModelTag + XPATH_SEP + XMLTags.FeatureTag);
		reHashBio.put(XMLTags.MembraneTag, XMLTags.BioModelTag + XPATH_SEP + XMLTags.ModelTag + XPATH_SEP + XMLTags.MembraneTag);
		reHashBio.put(XMLTags.FluxStepTag, XMLTags.BioModelTag + XPATH_SEP + XMLTags.ModelTag + XPATH_SEP + XMLTags.FluxStepTag);
	    reHashBio.put(XMLTags.SimpleReactionTag, XMLTags.BioModelTag + XPATH_SEP + XMLTags.ModelTag + XPATH_SEP + XMLTags.SimpleReactionTag); 
		reHashBio.put(XMLTags.SubVolumeTag, XMLTags.SimulationSpecTag + XPATH_SEP + XMLTags.GeometryTag + XPATH_SEP + XMLTags.SubVolumeTag);	  
	    reHashBio.put(XMLTags.CompartmentSubDomainTag, XMLTags.SimulationSpecTag + XPATH_SEP + XMLTags.MathDescriptionTag + XPATH_SEP + XMLTags.CompartmentSubDomainTag);
	    reHashBio.put(XMLTags.VolumeVariableTag, XMLTags.SimulationSpecTag + XPATH_SEP + XMLTags.MathDescriptionTag + XPATH_SEP + XMLTags.VolumeVariableTag);
		reHashBio.put(XMLTags.MembraneVariableTag, XMLTags.SimulationSpecTag + XPATH_SEP + XMLTags.MathDescriptionTag + XPATH_SEP + XMLTags.MembraneVariableTag);
		reHashBio.put(XMLTags.FilamentVariableTag, XMLTags.SimulationSpecTag + XPATH_SEP + XMLTags.MathDescriptionTag + XPATH_SEP + XMLTags.FilamentVariableTag);
		reHashBio.put(XMLTags.VolumeRegionVariableTag, XMLTags.SimulationSpecTag + XPATH_SEP + XMLTags.MathDescriptionTag + XPATH_SEP + XMLTags.VolumeRegionVariableTag);
	    reHashBio.put(XMLTags.MembraneRegionVariableTag, XMLTags.SimulationSpecTag + XPATH_SEP + XMLTags.MathDescriptionTag + XPATH_SEP + XMLTags.MembraneRegionVariableTag);
		reHashBio.put(XMLTags.FilamentRegionVariableTag, XMLTags.SimulationSpecTag + XPATH_SEP + XMLTags.MathDescriptionTag + XPATH_SEP + XMLTags.FilamentRegionVariableTag);
		reHashMath = new Hashtable(10);
		reHashMath.put(XMLTags.SubVolumeTag, XMLTags.MathModelTag + XPATH_SEP + XMLTags.GeometryTag + XPATH_SEP + XMLTags.SubVolumeTag);	  
	    reHashMath.put(XMLTags.CompartmentSubDomainTag, XMLTags.MathModelTag + XPATH_SEP + XMLTags.MathDescriptionTag + XPATH_SEP + XMLTags.CompartmentSubDomainTag);
	    reHashMath.put(XMLTags.VolumeVariableTag, XMLTags.MathModelTag + XPATH_SEP + XMLTags.MathDescriptionTag + XPATH_SEP + XMLTags.VolumeVariableTag);
		reHashMath.put(XMLTags.MembraneVariableTag, XMLTags.MathModelTag + XPATH_SEP + XMLTags.MathDescriptionTag + XPATH_SEP + XMLTags.MembraneVariableTag);
		reHashMath.put(XMLTags.FilamentVariableTag, XMLTags.MathModelTag + XPATH_SEP + XMLTags.MathDescriptionTag + XPATH_SEP + XMLTags.FilamentVariableTag);
		reHashMath.put(XMLTags.VolumeRegionVariableTag, XMLTags.MathModelTag + XPATH_SEP + XMLTags.MathDescriptionTag + XPATH_SEP + XMLTags.VolumeRegionVariableTag);
	    reHashMath.put(XMLTags.MembraneRegionVariableTag, XMLTags.MathModelTag + XPATH_SEP + XMLTags.MathDescriptionTag + XPATH_SEP + XMLTags.MembraneRegionVariableTag);
		reHashMath.put(XMLTags.FilamentRegionVariableTag, XMLTags.MathModelTag + XPATH_SEP + XMLTags.MathDescriptionTag + XPATH_SEP + XMLTags.FilamentRegionVariableTag);
   }

public XMLDict() {
	super();
	hash = new Hashtable();
}


   private Object get(Object key) {

	   return hash.get(key);
   }


   public Object get(Element e, String key) {

	   if (e == null)
			return null;
			
	   String path = getPathToRoot(e);

	   //System.out.println(path + "          " + key); 
	   return hash.get(path + ":" + key);
   }


   private static Element getMatchingElement(Element root, String reName, String attName, String attValue) {

	   String path;
	   //accomodate math models.
	   if (!root.getName().equals(XMLTags.MathModelTag)) {
	       path = (String)reHashBio.get(reName);
	   } else {
		   path = (String)reHashMath.get(reName);
	   }
	   if (path == null) {
		    System.err.println("The xpath of the element to resolve is not available: " + reName);
			return getMatchingElementGeneric(root, reName, attName, attValue);
	   } 
	   java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(path, XMLDict.XPATH_SEP);
	   //System.out.println(root.getName() + " " + reName + " " + attName + " " + attValue + " " + path + " " + tokenizer.countTokens());
	   if (tokenizer.countTokens() < 2) {                      	//the element to resolve is the root. Should not happen.
		   System.err.println("No tokens found for element: " + reName);
		   return null;
	   }   
	   while (tokenizer.hasMoreTokens()) {             //start at the parent of interest, which can be a shorter path than the one saved.
		   String curToken = tokenizer.nextToken();
			if (root.getName().equals(curToken)) {
				break;
			}
	   }
	   for (int i = 0; i < tokenizer.countTokens() - 1; i++) {            //break when reaching the direct parent.
			String elementName = tokenizer.nextToken();
			//System.out.println("Current Token: " + elementName);
			root = root.getChild(elementName);
	   }
	   String resElementName = tokenizer.nextToken();
	   //System.out.println(resElementName);
	   Iterator iterator = root.getChildren(resElementName).iterator();              //last 2 tokens (direct parent, and element to resolve)
	   Element temp = null;
	   while (iterator.hasNext()) {
	   		temp = (Element)iterator.next();
	   		if (attValue.equals(temp.getAttributeValue(attName))) {
				break;
	   		} else {
				temp = null;
	   		}
	   }

	   return temp;
   }


   private static Element getMatchingElementGeneric(Element root, String reName, String attName, String attValue) {

	   Element curElement = null;
	   
   	   Iterator i = root.getChildren().iterator();
   	   while (i.hasNext()) {
	   	   curElement = (Element)i.next();
	   	   if (curElement.getName().equals(reName)) {
		   		String tempAttVal = curElement.getAttributeValue(attName);
		   		if (attValue.equals(tempAttVal)) {
					break;
		   		}
	   	   }
	   	    return getMatchingElementGeneric(curElement, reName, attName, attValue);
   	   }
   	   
   	   return curElement;
   }


   //a utility method
   private static Element getParent(Element e, int hier) {

	   for (int i = 0; i < hier; i++)
			e = e.getParent();
			
		return e; 
   }


   //utility method, returns empty string for root. 
   private String getPathToRoot(Element e) {

	   StringBuffer buf = new StringBuffer();
	   Element temp = e.getParent();
	   while (temp != null) {
		   buf.insert(0, temp.getName() + "[@Name='" +  temp.getAttributeValue(XMLTags.NameTag) + "']" + "/");
	       temp = temp.getParent();
	   }
	   buf.append(e.getName() + "[@Name='" + e.getAttributeValue(XMLTags.NameTag) + "']");
	   
	   return buf.toString();
   }


//can also be placed in XmlReader.
	protected static Element getResolvedElement(Element e, String reName, String attName, String attValue) {

		if (e == null)
			return null;
		Element root;
		String name = e.getName();
		if (name.equals(XMLTags.OdeEquationTag) ||
			name.equals(XMLTags.MembraneRegionEquationTag) || name.equals(XMLTags.PdeEquationTag)) {
			root = getParent(e, 3);
		} else if (name.equals(XMLTags.CatalystTag) || name.equals(XMLTags.FilamentSubDomainTag) ||
		           name.equals(XMLTags.ReactantTag) ||
		           name.equals(XMLTags.ProductTag) || name.equals(XMLTags.JumpConditionTag) ||
		           (name.equals(XMLTags.FeatureMappingTag) && reName.equals(XMLTags.SubVolumeTag))) {
			root = getParent(e, 2);
		} else if (name.equals(XMLTags.DiagramTag) || name.equals(XMLTags.FluxStepTag) ||
				   name.equals(XMLTags.MembraneTag) || name.equals(XMLTags.SimpleReactionTag) ||
				   name.equals(XMLTags.SpeciesContextTag) || name.equals(XMLTags.MembraneSubDomainTag)){
			root = getParent(e, 1);
		} else {               //XMLTags.ReactionSpecTag, XMLTags.SpeciesContextRefTag, XMLTags.FeatureMappingTag
			                   //XMLTags.MembraneMappingTag, XMLTags.VolumeRegion
			root = e.getDocument().getRootElement();
		}
		Element temp = getMatchingElement(root, reName, attName, attValue);
		
		return temp;
   }


   private Enumeration keys() {

	   return hash.keys();
   }


   //tests the x-path functionality
   public static void main (String args []) throws Exception {

	   if (args.length < 1)
	   		System.out.println("Test Usage: XMLDict test_BioModel_File");
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new FileReader(args[0]));	
	   	Element root = doc.getRootElement();
	   	//not the actual usage, but works for testing
		XMLDict dict = new XMLDict();
		//add just one round
	   	dict.put(root, root.getClass().getName() + ":" + root.getAttributeValue(XMLTags.NameTag), root);
	   	Iterator i = root.getChildren().iterator();
		while (i.hasNext()) {
			Element temp = (Element)i.next();
			dict.put(temp, temp.getClass().getName() + ":" + temp.getAttributeValue(XMLTags.NameTag), temp);
	   	}
		Enumeration e = dict.keys();
		while (e.hasMoreElements()) {
			String key = (String)e.nextElement();
			Object value = dict.get(key);
			System.out.println("key: " + key + " value: " + value);
		}
		i = root.getChildren().iterator();
		while (i.hasNext()) {
			Element temp = (Element)i.next();
			Element value = (Element)dict.get(temp, temp.getClass().getName() + ":" + temp.getAttributeValue(XMLTags.NameTag));
			System.out.println(value.getName() + " " + value.getAttributeValue(XMLTags.NameTag) + " " +
					           temp.getName()  + " " + temp.getAttributeValue(XMLTags.NameTag));
		}
   }


   public void put(Element e, String key, Object value) {

	   if (e == null)
	   		return;
	   		
	   String path = getPathToRoot(e);
	   hash.put(path + ":" + key, value);
   }
}