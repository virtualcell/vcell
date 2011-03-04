package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class BioSource extends BioPaxObjectImpl implements UtilityClass {
	
	private CellVocabulary cellType;
	private ArrayList<String> name = new ArrayList<String>();
	private TissueVocabulary tissue;
	private ArrayList<Xref> xRef = new ArrayList<Xref>();
	
	public ArrayList<Xref> getxRef() {
		return xRef;
	}
	public void setxRef(ArrayList<Xref> xRef) {
		this.xRef = xRef;
	}
	public CellVocabulary getCellType() {
		return cellType;
	}
	public ArrayList<String> getName() {
		return name;
	}
	public TissueVocabulary getTissue() {
		return tissue;
	}
	public void setCellType(CellVocabulary cellType) {
		this.cellType = cellType;
	}
	public void setName(ArrayList<String> name) {
		this.name = name;
	}
	public void setTissue(TissueVocabulary tissue) {
		this.tissue = tissue;
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		if(cellType == objectProxy) {
			cellType = (CellVocabulary) concreteObject;
		}
		if(tissue == objectProxy) {
			tissue = (TissueVocabulary) concreteObject;
		}
		for (int i=0; i<xRef.size(); i++) {
			Xref thing = xRef.get(i);
			if(thing == objectProxy) {
				xRef.set(i, (Xref)concreteObject);
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printObject(sb,"cellType",cellType,level);
		printObject(sb,"tissue",tissue,level);
		printStrings(sb,"name",name,level);
		printObjects(sb,"xRef",xRef,level);
	}
}
