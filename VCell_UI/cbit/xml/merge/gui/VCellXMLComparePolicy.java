package cbit.xml.merge.gui;

import java.util.HashMap;
import java.util.HashSet;

import cbit.vcell.xml.XMLTags;

public class VCellXMLComparePolicy implements XmlComparePolicy {
/**
 * elementMap holds element,attribute pairs where the attribute is considered the "primary key" for comparing elements
 */
	private HashMap<String, String> elementMap = null;
	private boolean bIgnoreVersions;
	
	
	public VCellXMLComparePolicy(boolean ignoreVersions){
		
		this.bIgnoreVersions = ignoreVersions;
		
		elementMap = new HashMap<String,String>();
		
		elementMap.put("Constant","Name");
		elementMap.put("VolumeVariable","Name");
		elementMap.put("Feature","Name");
		elementMap.put("OdeEquation","Name");
		elementMap.put("Version","Name");
		elementMap.put("Compound","Name");
		elementMap.put("Membrane","Name");
		elementMap.put("LocalizedCompound","Name");
		elementMap.put("SimulationSpec","Name");
		elementMap.put("Diagram","Structure");
		elementMap.put("BoundaryType","Boundary");
		
	}

	public String getKeyAttributeName(String elementName) {
		return elementMap.get(elementName);
	}

	public boolean ignoreElement(String elementName) {
		if (bIgnoreVersions){
			if (XMLTags.VersionTag.equals(elementName)){
				return true;
			}
		}
		return false;
	}

	public boolean ignoreAttribute(String elementName, String attributeName) {
		if (bIgnoreVersions){
			if (XMLTags.KeyValueAttrTag.equals(attributeName)){
				return true;
			}
		}
		return false;
	}
	
}
