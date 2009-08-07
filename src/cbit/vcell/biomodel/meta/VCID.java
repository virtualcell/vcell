package cbit.vcell.biomodel.meta;

import java.util.StringTokenizer;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.Structure;

public class VCID {
	private String id;
	private String className;
	private String localName;
	
	@SuppressWarnings("serial")
	public static class InvalidVCIDException extends Exception {
		public InvalidVCIDException(String message){
			super(message);
		}
	};
	
	private VCID(String id){
		this.id = id;
	}
	
	public static VCID fromString(String id) throws InvalidVCIDException {
		VCID vcid = new VCID(id);
		vcid.parse(id);
		return vcid;
	}
	
	public String toASCIIString(){
		return id;
	}
	
	private void parse(String id) throws InvalidVCIDException {
		// parse class
		StringTokenizer token = new StringTokenizer(id,"()/",true);
		String class_Name = token.nextToken();
		String beginParentheses = token.nextToken();
		String name = token.nextToken();
		String endParentheses = token.nextToken();
		if (!(beginParentheses.equals("(") && endParentheses.equals(")"))){
			throw new InvalidVCIDException("illegal syntax");
		}
		this.localName = name;
		if (!class_Name.equals("Species") &&
			!class_Name.equals("Structure") &&
			!class_Name.equals("ReactionStep") &&
			!class_Name.equals("BioModel")){
			throw new InvalidVCIDException("Invalid VCID: unable to parse class");
		}
		this.className = class_Name;
	}

	public String getClassName(){
		return className;
	}
	
	public String getLocalName(){
		return localName;
	}
	
	public static Identifiable getIdentifiableObject(BioModel bioModel, VCID vcid) {
		if (vcid.getClassName().equals("Species")){
			String localName = vcid.getLocalName();
			return bioModel.getModel().getSpecies(localName);
		}
		if (vcid.getClassName().equals("Structure")){
			String localName = vcid.getLocalName();
			return bioModel.getModel().getStructure(localName);
		}
		if (vcid.getClassName().equals("ReactionStep")){
			String localName = vcid.getLocalName();
			return bioModel.getModel().getReactionStep(localName);
		}
		if (vcid.getClassName().equals("BioModel")){
			return bioModel;
		}
		return null;
	}

	public static VCID getVCID(BioModel bioModel, Identifiable identifiable) {
		String localName;
		String className;
		if (identifiable instanceof Species){
			localName = ((Species)identifiable).getCommonName();
			className = "Species";
		}else if (identifiable instanceof Structure){
			localName = ((Structure)identifiable).getName();
			className = "Structure";
		}else if (identifiable instanceof ReactionStep){
			localName = ((ReactionStep)identifiable).getName();
			className = "ReactionStep";
		}else if (identifiable instanceof BioModel){
			localName = ((BioModel)identifiable).getName();
			className = "BioModel";
		}else{
			throw new RuntimeException("unsupported Identifiable class");
		}
		VCID vcid = new VCID(className+"("+localName+")");
		vcid.className = className;
		vcid.localName = localName;

		return vcid;
	}

	public int hashCode(){
		return id.hashCode();
	}
	
	public boolean equals(Object obj){
		if (obj instanceof VCID){
			VCID vcid = (VCID)obj;
			if (vcid.id.equals(id)){
				return true;
			}
		}
		return false;
	}

}
