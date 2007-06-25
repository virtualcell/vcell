package cbit.vcell.xml;

import org.jdom.Element;
import cbit.sql.Cacheable;
import cbit.util.Compare;
import cbit.util.Matchable;
import cbit.util.xml.XmlUtil;

public class MIRIAMAnnotation implements Cacheable{
	
	private Element annotation;
	private Element userNotes;
	

	public MIRIAMAnnotation(){
	}

	public boolean compareEqual(Matchable obj) {
//		if(true){return true;}
		if(obj instanceof MIRIAMAnnotation){
//			String thisAnnot =
//				(annotation == null?null:XmlUtil.xmlToString(annotation,true));
//			String objAnnot =
//				(((MIRIAMAnnotation)obj).getAnnotation() == null?
//					null:XmlUtil.xmlToString(((MIRIAMAnnotation)obj).getAnnotation(),true));
//			boolean bEqual = 
//			Compare.isEqualOrNull(
//					thisAnnot,
//					objAnnot
//			);
//			if(!bEqual){
//				if(thisAnnot == null || objAnnot == null){
//					System.out.println("NULL not NULL failed");
//				}else{
//					for (int i = 0; i < Math.max(thisAnnot.length(),objAnnot.length()); i++) {
//						System.out.println(
//								i+" '"+
//								(i < thisAnnot.length()?thisAnnot.charAt(i):"") + "' '"+
//								(i < objAnnot.length()?objAnnot.charAt(i):"")+"'"
//						);
//					}
//				}
//			}
//			return bEqual;
			
			
			return
			Compare.isEqualOrNull(
					(annotation == null?null:XmlUtil.xmlToString(annotation,true)),
					(((MIRIAMAnnotation)obj).getAnnotation() == null?
							null:XmlUtil.xmlToString(((MIRIAMAnnotation)obj).getAnnotation(),true))
			)
			&&
			Compare.isEqualOrNull(
					(userNotes == null?null:XmlUtil.xmlToString(userNotes,true)),
					(((MIRIAMAnnotation)obj).getUserNotes() == null?
							null:XmlUtil.xmlToString(((MIRIAMAnnotation)obj).getUserNotes(),true))
			);


		}
		return false;
	}
	public Element getAnnotation(){
		return annotation;
	}
	
	public void setAnnotation(Element annotation){
		this.annotation = annotation;
	}
	
	public Element getUserNotes() {
		return userNotes;
	}

	public void setUserNotes(Element userNotes) {
		this.userNotes = userNotes;
	}

}
