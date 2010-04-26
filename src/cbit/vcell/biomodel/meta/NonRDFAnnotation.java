package cbit.vcell.biomodel.meta;

import org.jdom.Element;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.util.xml.XmlUtil;

public class NonRDFAnnotation implements Matchable {
	private Element xhtmlNotes;
	private Element[] xmlAnnotations;
	private String freeTextAnnotation;
	
	public Element getXhtmlNotes() {
		return xhtmlNotes;
	}
	public void setXhtmlNotes(Element xhtmlNotes) {
		this.xhtmlNotes = xhtmlNotes;
	}
	public Element[] getXmlAnnotations() {
		return xmlAnnotations;
	}
	public void setXmlAnnotations(Element[] xmlAnnotations) {
		this.xmlAnnotations = xmlAnnotations;
	}
	public String getFreeTextAnnotation() {
		return freeTextAnnotation;
	}
	public void setFreeTextAnnotation(String freeTextAnnotation) {
		this.freeTextAnnotation = freeTextAnnotation;
	}
	public boolean isEmpty(){
		if (xhtmlNotes!=null){
			return false;
		}
		if (xmlAnnotations!=null && xmlAnnotations.length>0){
			return false;
		}
		if (freeTextAnnotation!=null && freeTextAnnotation.length()>0){
			return false;
		}
		return true;
	}
	public boolean compareEqual(Matchable obj) {
		if (!(obj instanceof NonRDFAnnotation)) {
			return false;
		}
		NonRDFAnnotation nonRDFAnnotation = (NonRDFAnnotation)obj;
		
		String annot1 = freeTextAnnotation == null || freeTextAnnotation.length() == 0 ? null : freeTextAnnotation;
		String annot2 = nonRDFAnnotation.freeTextAnnotation == null || nonRDFAnnotation.freeTextAnnotation.length() == 0 ? null : nonRDFAnnotation.freeTextAnnotation;
		if (!Compare.isEqualOrNull(annot1, annot2)) {
			return false;
		}
		if (xhtmlNotes != null || nonRDFAnnotation.xhtmlNotes != null) {
			if (xhtmlNotes == null || nonRDFAnnotation.xhtmlNotes == null) {
				return false;
			}			
			if (!Compare.isEqualOrNull(XmlUtil.xmlToString(xhtmlNotes), XmlUtil.xmlToString(nonRDFAnnotation.xhtmlNotes))) {
				return false;
			}			
		} 
		if (xmlAnnotations != null || nonRDFAnnotation.xmlAnnotations != null) {
			if (xmlAnnotations == null || nonRDFAnnotation.xmlAnnotations == null) {
				return false;
			}
			if (xmlAnnotations.length != nonRDFAnnotation.xmlAnnotations.length) {
				return false;
			}
			if (!Compare.isEqualOrNull(XmlUtil.xmlToString(xhtmlNotes), XmlUtil.xmlToString(nonRDFAnnotation.xhtmlNotes))) {
				return false;
			}
			for (int i = 0; i < xmlAnnotations.length; i ++) {
				Element element1 = xmlAnnotations[i];
				Element element2 = null;
				for (Element e : nonRDFAnnotation.xmlAnnotations) {
					if (element1.getName().equals(e.getName())) {
						if (element1.getNamespace() != null && element1.getNamespace().equals(e.getNamespace())
							|| element1.getNamespace() == null && e.getNamespace() == null) {					
							element2 = e;
						}
					}
				}
				if (element2 == null) {
					return false;
				}				
				if (!Compare.isEqualOrNull(XmlUtil.xmlToString(element1), XmlUtil.xmlToString(element2))) {
					return false;
				}
			}
		} 
		return true;
	}
}
