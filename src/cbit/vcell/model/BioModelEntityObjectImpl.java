package cbit.vcell.model;

public class BioModelEntityObjectImpl implements BioModelEntityObject{
	
	public String getTypeLabel(){
		String typeName = getClass().getName();
		typeName = typeName.replace(getClass().getPackage().getName(),"");
		typeName = typeName.replace(".","");
		return typeName;
	}

}
