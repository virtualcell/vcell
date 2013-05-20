package org.vcell.model.rbm;

import java.beans.PropertyVetoException;

import org.vcell.util.document.PropertyConstants;

public class ComponentState extends RbmElement {
	private String name;   // e.g. Phosphorated, ...
	private boolean bAny;
	
	public ComponentState(String name) {
		this.name = name;
	}
	
	public boolean isAny(){
		return bAny;
	}

	public final String getName() {
		return name;
	}
	
	public void setName(String newValue) throws PropertyVetoException {
		String oldValue = name;
		fireVetoableChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, newValue);
		name = newValue;
		firePropertyChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, newValue);
	}

	@Override
	public String toString() {
		return getName();
	}
	
}
