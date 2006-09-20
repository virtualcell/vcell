package cbit.vcell.vcml;

import java.util.HashMap;

import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.merge.XmlComparePolicy;

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
		
		// from VCMLComparator
		elementMap.put(XMLTags.ReactantTag, XMLTags.SpeciesContextRefAttrTag);
		elementMap.put(XMLTags.ProductTag, XMLTags.SpeciesContextRefAttrTag);
		elementMap.put(XMLTags.CatalystTag, XMLTags.SpeciesContextRefAttrTag);
		elementMap.put(XMLTags.SpeciesContextShapeTag, XMLTags.SpeciesContextRefAttrTag);
		elementMap.put(XMLTags.SimpleReactionShapeTag, XMLTags.SimpleReactionRefAttrTag);
		elementMap.put(XMLTags.FluxReactionShapeTag, XMLTags.FluxReactionRefAttrTag);
		elementMap.put(XMLTags.FeatureMappingTag, XMLTags.FeatureAttrTag);
		elementMap.put(XMLTags.MembraneMappingTag, XMLTags.MembraneAttrTag);
		elementMap.put(XMLTags.SpeciesContextSpecTag, XMLTags.SpeciesContextRefAttrTag);
		elementMap.put(XMLTags.ReactionSpecTag, XMLTags.ReactionStepRefAttrTag);
		elementMap.put(XMLTags.BoundaryTypeTag, XMLTags.BoundaryAttrTag);
		//		// TODO fix document comparison for plugin-defined XML formats
		//elementMap.put(ParameterEstimationTaskXMLPersistence.ParameterMappingSpecTag, ParameterEstimationTaskXMLPersistence.ParameterReferenceAttribute);
		//elementMap.put(ParameterEstimationTaskXMLPersistence.ReferenceDataMappingSpecTag, ParameterEstimationTaskXMLPersistence.ReferenceDataColumnNameAttribute);
		//elementMap.put(ParameterEstimationTaskXMLPersistence.DataRowTag, "TEXT");
		//		
		elementMap.put(XMLTags.FastInvariantTag, "TEXT");
		elementMap.put(XMLTags.FastRateTag, "TEXT");
		elementMap.put(XMLTags.NameTag, "TEXT");
		//		//a hack, for compound 'primary key'
		elementMap.put(XMLTags.MembraneSubDomainTag, XMLTags.InsideCompartmentTag + "&" + XMLTags.OutsideCompartmentTag);
		elementMap.put(XMLTags.CoordinateTag, XMLTags.XAttrTag + "&" + XMLTags.YAttrTag + "&" + XMLTags.ZAttrTag);
		elementMap.put(XMLTags.VelocityTag, XMLTags.XAttrTag + "&" + XMLTags.YAttrTag + "&" + XMLTags.ZAttrTag);
		elementMap.put(XMLTags.SurfaceDescriptionTag, XMLTags.CutoffFrequencyAttrTag + "&" + XMLTags.NumSamplesXAttrTag + "&" + XMLTags.NumSamplesYAttrTag + "&" + XMLTags.NumSamplesZAttrTag);     //?
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

	public String getDefaultKeyAttributeName() {
		return XMLTags.NameAttrTag;
	}

	public boolean skipOrdering(String elementName) {
		return XMLTags.CoordinateTag.equals(elementName);
	}
	
}
