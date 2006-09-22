package cbit.vcell.vcml.compare;
import java.io.PrintStream;

import org.jdom.Element;

import cbit.util.Matchable;
import cbit.util.xml.XmlParseException;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.vcml.VCellXMLComparePolicy;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.Xmlproducer;
import cbit.vcell.xml.merge.XmlComparator;


/**
This utility class encapsulates the functionality of comparing VCML documents. Contrary to what the class name might indicate,
this class does not extend java.util.Comparator

 * Creation date: (9/21/2004 4:04:50 PM)
 * @author: Rashad Badrawi
 */
public class VCMLComparator {

	private static boolean VERBOSE_MODE = true;	                //for now...
	
	public static PrintStream ps;
//	static Hashtable map;

	//can also be loaded from a property file. Fills a hashtable of all the VCML elements whose 'primary key' is not 'Name'
	//but some other attribute.
	static {
		//
		// moved to VCMLXmlComparePolicy
		//
//		map = new Hashtable();
//		map.put(XMLTags.ReactantTag, XMLTags.SpeciesContextRefAttrTag);
//		map.put(XMLTags.ProductTag, XMLTags.SpeciesContextRefAttrTag);
//		map.put(XMLTags.CatalystTag, XMLTags.SpeciesContextRefAttrTag);
//		map.put(XMLTags.SpeciesContextShapeTag, XMLTags.SpeciesContextRefAttrTag);
//		map.put(XMLTags.SimpleReactionShapeTag, XMLTags.SimpleReactionRefAttrTag);
//		map.put(XMLTags.FluxReactionShapeTag, XMLTags.FluxReactionRefAttrTag);
//		map.put(XMLTags.FeatureMappingTag, XMLTags.FeatureAttrTag);
//		map.put(XMLTags.MembraneMappingTag, XMLTags.MembraneAttrTag);
//		map.put(XMLTags.SpeciesContextSpecTag, XMLTags.SpeciesContextRefAttrTag);
//		map.put(XMLTags.ReactionSpecTag, XMLTags.ReactionStepRefAttrTag);
//		map.put(XMLTags.BoundaryTypeTag, XMLTags.BoundaryAttrTag);
//		// TODO fix document comparison for plugin-defined XML formats
////		map.put(ParameterEstimationTaskXMLPersistence.ParameterMappingSpecTag, ParameterEstimationTaskXMLPersistence.ParameterReferenceAttribute);
////		map.put(ParameterEstimationTaskXMLPersistence.ReferenceDataMappingSpecTag, ParameterEstimationTaskXMLPersistence.ReferenceDataColumnNameAttribute);
////		map.put(ParameterEstimationTaskXMLPersistence.DataRowTag, "TEXT");
//		
//		map.put(XMLTags.FastInvariantTag, "TEXT");
//		map.put(XMLTags.FastRateTag, "TEXT");
//		map.put(XMLTags.NameTag, "TEXT");
//		//a hack, for compound 'primary key'
//		map.put(XMLTags.MembraneSubDomainTag, XMLTags.InsideCompartmentTag + "&" + XMLTags.OutsideCompartmentTag);
//		map.put(XMLTags.CoordinateTag, XMLTags.XAttrTag + "&" + XMLTags.YAttrTag + "&" + XMLTags.ZAttrTag);
//		map.put(XMLTags.VelocityTag, XMLTags.XAttrTag + "&" + XMLTags.YAttrTag + "&" + XMLTags.ZAttrTag);
//		map.put(XMLTags.SurfaceDescriptionTag, XMLTags.CutoffFrequencyAttrTag + "&" + XMLTags.NumSamplesXAttrTag +
//			   "&" + XMLTags.NumSamplesYAttrTag + "&" + XMLTags.NumSamplesZAttrTag);     //?

		ps = System.out;
	}
	
	public static boolean compareEquals(String xmlStr1, String xmlStr2) throws XmlParseException {

		if (xmlStr1 == null || xmlStr1.length() == 0 ||
			xmlStr2 == null || xmlStr2.length() == 0) {
			throw new XmlParseException("Invalid values for the xml strings.");
		}
		XmlComparator xmlComparator = new XmlComparator(new VCellXMLComparePolicy(true));
		return xmlComparator.compareXML(xmlStr1, xmlStr2, false);
	}


	public static boolean compareMatchables(Matchable m1, Matchable m2, String type) {

		Element source = null, target = null; 
		try { 
			Xmlproducer producer = new Xmlproducer(true);
			if (type.equals(XMLTags.BioModelTag)) {
				source = producer.getXML((BioModel)m1);
				target = producer.getXML((BioModel)m2);
			} else if (type.equals(XMLTags.MathModelTag)) {
				source = producer.getXML((MathModel)m1);
				target = producer.getXML((MathModel)m2);
			} else {
				throw new IllegalArgumentException("Accepted matchable types are biomodel and mathmodel");
			}
			String sourceXMLStr = XmlUtil.xmlToString(source);
			String targetXMLStr = XmlUtil.xmlToString(target);
			
			XmlComparator xmlComparator = new XmlComparator(new VCellXMLComparePolicy(true));
			
			boolean result = xmlComparator.compareXML(sourceXMLStr, targetXMLStr, true);
			if (!result && VERBOSE_MODE) {
				ps.println(sourceXMLStr);
				ps.println(targetXMLStr);
			}
			return result;
		} catch (Exception e) {         					//ExpressionException, XmlParseException 
			e.printStackTrace();
			return false;
		}
	}
}