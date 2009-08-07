/**
 * 
 */
package cbit.vcell.xml;

import java.util.Vector;

import org.jdom.Element;

public abstract class DescriptiveHeirarchy{
		private Vector<Element> heirarchy = new Vector<Element>();
		private Element listElement;
		private Element typeElement;
		
		private Element getCreatorElement(){
			for (int i = 0; i < heirarchy.size(); i++) {
				if(	heirarchy.get(i).getNamespaceURI().equals(XMLTags.DUBCORE_NAMESPACE_URI) &&
					heirarchy.get(i).getName().equals("creator")){
					return heirarchy.get(i);
				}
			}
			return null;
		}
//		private Element getRDFItemElement(){
//			for (int i = 0; i < heirarchy.size(); i++) {
//				if(	heirarchy.get(i).getNamespaceURI().equals(XMLTags.RDF_NAMESPACE_URI) &&
//					heirarchy.get(i).getName().equals("li")){
//					return heirarchy.get(i);
//				}
//			}
//			return null;
//			
//		}
		public Vector<Element> getHeirarchy(){
			return  heirarchy;
		}
		public boolean isCreatorChild(){
			if(heirarchy == null){
				return false;
			}
			
			return(getCreatorElement() != null);
		}
		public boolean isSameCreator(DescriptiveHeirarchy descrHeirarchy){
			return
				this.listElement != null &&
				descrHeirarchy.listElement != null &&
				this.listElement == descrHeirarchy.listElement;
//			Element thisCreator = getCreatorElement();
//			Element argCreator = descrHeirarchy.getCreatorElement();
//			if(thisCreator != null && argCreator != null && thisCreator == argCreator){
//				Element thisItem = getRDFItemElement();
//				Element argItem = descrHeirarchy.getRDFItemElement();
//				if(thisItem != null && argItem != null && thisItem == argItem){
//					return true;
//				}
//			}
//			return false;
		}
		public void setListElement(Element listElement){
			this.listElement = listElement;
		}
		public void setTypeElement(Element typeElement){
			this.typeElement = typeElement;
		}
		public Element getTypeElement(){
			return typeElement;
		}
		public Element getListElement(){
			return listElement;
		}
	}