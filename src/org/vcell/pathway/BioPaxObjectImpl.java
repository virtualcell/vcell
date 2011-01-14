package org.vcell.pathway;

import java.util.List;

import org.vcell.pathway.persistence.PathwayReader.RdfObjectProxy;

import sun.security.action.GetLongAction;


public abstract class BioPaxObjectImpl implements BioPaxObject {
	public final static String spaces = "                                                                                        ";
	private String ID;
	private String resource;
	private String comment;
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setID(String value) {
		if (value.equals("#CPATH-LOCAL-12758385")){
			System.out.println("found it");
		}
		this.ID = value;
	}

	public void setResource(String value) {
		if (value.equals("#CPATH-LOCAL-12758385")){
			System.out.println("found it");
		}
		this.resource = value;
	}

	public String getID() {
		return ID;
	}

	public String getResource() {
		return resource;
	}
	
	public String getTypeLabel(){
		String typeName = getClass().getName();
		typeName = typeName.replace(getClass().getPackage().getName(),"");
		typeName = typeName.replace(".","");
		return typeName;
	}
	
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		// default implementation ... do nothing
	}

	public String toString(){
		String suffix = "";
		if (this instanceof Entity){
			Entity entity = (Entity)this;
			if (entity.getName().size()>0){
				suffix = suffix + " : \""+entity.getName().get(0)+"\"";
			}
		}
		if (ID!=null){
			suffix = suffix + "  ID='"+ID+"'";
		}
		if (resource!=null){
			suffix = suffix + "  resource='"+resource+"'";
		}
		if (suffix.length()>0){
			suffix = suffix + " ";
		}
		return getTypeLabel()+suffix;
	}
	
	public final String getPad(int level){
		if (level==0){
			return "";
		}
		if (level>10){
			throw new RuntimeException("unchecked recursion in pathway.show()");
		}
		return spaces.substring(0,3*level);
	}
	
	public final void show(StringBuffer sb){
		sb.append(getPad(0)+toString()+"\n");
		showChildren(sb,1);
	}

	public void showChildren(StringBuffer sb, int level) {
		printString(sb,"comment",comment,level);
	}
	
	public void printString(StringBuffer sb, String name, String value, int level){
		if (name!=null && value!=null){
			sb.append(getPad(level)+name+" = "+value+"\n");
		}
	}

	public void printStrings(StringBuffer sb, String name, List<String> values, int level){
		if (name!=null && values!=null){
			for (String v : values){
				sb.append(getPad(level)+name+" = "+v+"\n");
			}
		}
	}

	public void printDoubles(StringBuffer sb, String name, List<Double> values, int level){
		if (name!=null && values!=null){
			for (Double v : values){
				sb.append(getPad(level)+name+" = "+v+"\n");
			}
		}
	}

	public void printObjects(StringBuffer sb,String name, List<? extends BioPaxObject> values, int level){
		if (name!=null && values!=null){
			for (BioPaxObject v : values){
				sb.append(getPad(level)+name+" = "+v.toString()+"\n");
				v.showChildren(sb,level+1);
			}
		}
	}

	public void printObject(StringBuffer sb, String name, BioPaxObject value, int level){
		if (name!=null && value!=null){
			sb.append(getPad(level)+name+" = "+value.toString()+"\n");
			value.showChildren(sb,level+1);
		}
	}

	public void printBoolean(StringBuffer sb, String name, Boolean value, int level){
		if (name!=null && value!=null){
			sb.append(getPad(level)+name+" = "+value.toString()+"\n");
			showChildren(sb,level+1);
		}
	}
	
	public void printDouble(StringBuffer sb, String name, Double value, int level){
		if (name!=null && value!=null){
			sb.append(getPad(level)+name+" = "+value.toString()+"\n");
			showChildren(sb,level+1);
		}
	}
	
	public void printInteger(StringBuffer sb, String name, Integer value, int level){
		if (name!=null && value!=null){
			sb.append(getPad(level)+name+" = "+value.toString()+"\n");
			showChildren(sb,level+1);
		}
	}
	
}
