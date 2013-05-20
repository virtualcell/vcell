package org.vcell.model.rbm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.document.PropertyConstants;

public class MolecularType extends RbmElement implements VetoableChangeListener {
	public static final String PROPERTY_NAME_COMPONENT_LIST = "componentList";
	private String name;	
	
	private List<MolecularComponent> componentList = new ArrayList<MolecularComponent>();
	public MolecularType(String name) {
		this.name = name;
	}
	
	public void addMolecularComponent(MolecularComponent molecularComponent) {
		if (!componentList.contains(molecularComponent)) {
			List<MolecularComponent> newValue = new ArrayList<MolecularComponent>(componentList);
			newValue.add(molecularComponent);
			setComponentList(newValue);
		}
	}
	
	public MolecularComponent createMolecularComponent() {
		int count=0;
		String name = null;
		while (true) {
			name = "component" + count;	
			if (getMolecularComponent(name) == null) {
				break;
			}	
			count++;
		}
		return new MolecularComponent(name);
	}
	
	public void removeMolecularComponent(MolecularComponent molecularComponent) {
		if (componentList.contains(molecularComponent)) {
			List<MolecularComponent> newValue = new ArrayList<MolecularComponent>(componentList);
			newValue.remove(molecularComponent);
			setComponentList(newValue);
		}
	}

	public MolecularComponent getMolecularComponent(String componentName) {
		for (MolecularComponent mc : componentList)  {
			if (mc.getName().equals(componentName)) {
				return mc;
			}
		}
		return null;
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

	public final List<MolecularComponent> getComponentList() {
		return componentList;
	}

	public final void setComponentList(List<MolecularComponent> newValue) {
		List<MolecularComponent> oldValue = componentList;
		if (oldValue != null) {
			for (MolecularComponent mc : oldValue) {
				mc.removeVetoableChangeListener(this);
			}
		}
		componentList = newValue;
		if (newValue != null) {
			for (int i = 0; i < componentList.size(); ++ i) {
				MolecularComponent molecularComponent = componentList.get(i);
				molecularComponent.addVetoableChangeListener(this);
				molecularComponent.setIndex(i+1);
			}
		}
		firePropertyChange(PROPERTY_NAME_COMPONENT_LIST, oldValue, newValue);
	}

	public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
		if (evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_NAME)) {
			if (evt.getSource() instanceof MolecularComponent) {
				String newName = (String) evt.getNewValue();
				for (MolecularComponent mc : componentList) {
					if (mc != evt.getSource()) {
						if (mc.getName().equals(newName)) {
							throw new PropertyVetoException("Molecular Component '" + newName + "' already exists in Molecular Type '" + getName() + "'!", evt);
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
}
