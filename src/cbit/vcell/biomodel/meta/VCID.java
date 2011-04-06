package cbit.vcell.biomodel.meta;

import java.util.StringTokenizer;

import org.vcell.util.TokenMangler;


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
		StringTokenizer token = new StringTokenizer(id,"()",true);
		String class_Name = token.nextToken();
		String beginParentheses = token.nextToken();
		String name = token.nextToken();
		name = TokenMangler.unmangleVCId(name);
		String endParentheses = token.nextToken();
		if (!(beginParentheses.equals("(") && endParentheses.equals(")"))){
			throw new InvalidVCIDException("illegal syntax");
		}
		this.localName = name;
		if (class_Name.length()<1){
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
	
	@Override
	public int hashCode(){
		return id.hashCode();
	}
	
	@Override
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
