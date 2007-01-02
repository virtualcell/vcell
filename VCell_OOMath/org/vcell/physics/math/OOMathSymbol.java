package org.vcell.physics.math;

import jscl.plugin.Variable;
import jscl.plugin.ParseException;

import org.vcell.physics.component.PhysicalSymbol;

public class OOMathSymbol {
	private PhysicalSymbol physicalSymbol = null;
	private String name = null;
	private Variable jsclVariable = null;
	
	public OOMathSymbol(String argSymbolName, PhysicalSymbol argPhysicalSymbol) throws ParseException {
		physicalSymbol = argPhysicalSymbol;
		name = argSymbolName;
		jsclVariable = Variable.valueOf(argSymbolName);
	}
	public Variable getJsclVariable() {
		return jsclVariable;
	}
//	public void setJsclVariable(Variable jsclVariable) {
//		this.jsclVariable = jsclVariable;
//	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String toString(){
		return super.toString()+" "+getName()+" jscl="+jsclVariable;
	}
	public PhysicalSymbol getPhysicalSymbol() {
		return physicalSymbol;
	}
	public void setPhysicalSymbol(PhysicalSymbol argPhysicalSymbol) {
		this.physicalSymbol = argPhysicalSymbol;
	}
	public boolean equals(Object object){
		if (object instanceof OOMathSymbol){
			OOMathSymbol other = (OOMathSymbol)object;
			if (!other.name.equals(name)){
				return false;
			}
			if (other.jsclVariable!=null && jsclVariable!=null){
				if (!other.jsclVariable.equals(jsclVariable)){
					return false;
				}
			}else if (other.jsclVariable!=null || jsclVariable!=null){
				return false;
			}
			return true;
		}
		return false;
	}
	public int hashCode(){
		return name.hashCode();
	}
}
