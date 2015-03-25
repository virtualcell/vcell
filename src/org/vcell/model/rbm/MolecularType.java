package org.vcell.model.rbm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;
import org.vcell.util.document.PropertyConstants;

public class MolecularType extends RbmElementAbstract implements Matchable, VetoableChangeListener, 
	IssueSource, Displayable
{
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
			name = MolecularComponent.typeName + count;	
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
	
		public MolecularComponent[] getMolecularComponents(String componentName) {
		ArrayList<MolecularComponent> molecularComponents = new ArrayList<MolecularComponent>();
		for (MolecularComponent mc : componentList)  {
			if (mc.getName().equals(componentName)) {
				molecularComponents.add(mc);
			}
		}
		return molecularComponents.toArray(new MolecularComponent[molecularComponents.size()]);
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
							throw new PropertyVetoException(MolecularComponent.typeName + " '" + newName + "' already exists in " + getDisplayType() + " '" + getDisplayName() + "'!", evt);
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
	
	@Override
	public boolean compareEqual(Matchable aThat) {
		if (this == aThat) {
			return true;
		}
		if (!(aThat instanceof MolecularType)) {
			return false;
		}
		MolecularType that = (MolecularType)aThat;
		
		if (!Compare.isEqual(name, that.name)) {
			return false;
		}
		if (!Compare.isEqual(componentList, that.componentList)){
			return false;
		}
		return true;
	}

	
	@Override
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		issueContext = issueContext.newChildContext(ContextType.MolecularType, this);
		if(name == null) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Name of " + getDisplayType() + " is null", Issue.SEVERITY_ERROR));
		} else if(name.equals("")) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Name of " + getDisplayType() + " is empty", Issue.SEVERITY_WARNING));
		}
		if(componentList == null) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, getDisplayType() + " '" + getDisplayName() + MolecularComponent.typeName + "' List is null", Issue.SEVERITY_ERROR));
		} else if(componentList.isEmpty()) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, getDisplayType() + " '" + getDisplayName() + MolecularComponent.typeName + "' List is empty", Issue.SEVERITY_INFO));
		} else {
			for (MolecularComponent mc : componentList) {
				MolecularComponent[] mcList = getMolecularComponents(mc.getName());
				if(mcList.length > 1) {
					String msg = "Duplicate " + mc.getDisplayType() + " '" + mc.getDisplayName() + "' in the definition of the " + MolecularType.typeName + ".";
					issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_ERROR));
				}
			}
			
			for (MolecularComponent entity : componentList) {
				entity.gatherIssues(issueContext, issueList);
			}
		}
		
	}
	
	public static final String typeName = "Molecule";
	@Override
	public final String getDisplayName() {
		return getName();
	}
	@Override
	public final String getDisplayType() {
		return typeName;
	}

}
