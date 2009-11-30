package cbit.vcell.biomodel.meta;

import org.jdom.Element;

public class NonRDFAnnotation {
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
}
