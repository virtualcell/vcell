package org.vcell.model.rbm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.document.PropertyConstants;

public class MolecularComponent extends RbmElement implements VetoableChangeListener {
	public static final String PROPERTY_NAME_COMPONENT_STATES = "componentStates";
	private String name;                                  // binding site or phosphosite, motif, extracdomain (e.g. tyrosine 77) 
	private List<ComponentState> componentStates = new ArrayList<ComponentState>();    // allowable states (e.g. Phosphorylated, Unphosphorylated)
	private int index = 0; 
	
	public MolecularComponent(String name) {
		super();
		this.name = name;
	}
	public final String getName() {
		return name;
	}
	public void addComponentState(ComponentState componentState) {
		if (!componentStates.contains(componentState)) {
			List<ComponentState> newValue = new ArrayList<ComponentState>(componentStates);
			newValue.add(componentState);
			setComponentStates(newValue);
		}
	}
	
	public void deleteComponentState(ComponentState componentState) {
		if (componentStates.contains(componentState)) {
			List<ComponentState> newValue = new ArrayList<ComponentState>(componentStates);
			newValue.remove(componentState);
			setComponentStates(newValue);
		}
	}
	
	public ComponentState createComponentState() {
		int count=0;
		String name = null;
		while (true) {
			name = "state" + count;	
			if (getComponentState(name) == null) {
				break;
			}	
			count++;
		}
		return new ComponentState(name);
	}
	
	public ComponentState getComponentState(String componentName) {
		for (ComponentState cs : componentStates)  {
			if (cs.getName().equals(componentName)) {
				return cs;
			}
		}
		return null;
	}
	
	public final List<ComponentState> getComponentStates() {
		return componentStates;
	}
	public void setName(String newValue) throws PropertyVetoException {
		String oldValue = name;
		fireVetoableChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, newValue);
		name = newValue;
		firePropertyChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, newValue);
	}
	
	public final void setComponentStates(List<ComponentState> newValue) {
		List<ComponentState> oldValue = componentStates;
		if (oldValue != null) {
			for (ComponentState cs : oldValue) {
				cs.removeVetoableChangeListener(this);
			}
		}
		componentStates = newValue;
		if (newValue != null) {
			for (ComponentState cs : newValue) {
				cs.addVetoableChangeListener(this);
			}
		}
		firePropertyChange(PROPERTY_NAME_COMPONENT_STATES, oldValue, newValue);
	}
	public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
		if (evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_NAME)) {
			if (evt.getSource() instanceof ComponentState) {
				String newName = (String) evt.getNewValue();
				for (ComponentState cs : componentStates) {
					if (cs != evt.getSource()) {
						if (cs.getName().equals(newName)) {
							throw new PropertyVetoException("Component State '" + newName + "' already exists in Molecular Component '" + getName() + "'!", evt);
						}
					}
				}
			}
		}			
	}
	
	@Override
	public String toString() {
		return getName();
	}
	public final int getIndex() {
		return index;
	}
	public final void setIndex(int index) {
		this.index = index;
	}
}
