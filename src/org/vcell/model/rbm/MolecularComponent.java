package org.vcell.model.rbm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.Pair;
import org.vcell.util.Issue;
import org.vcell.util.TokenMangler;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;
import org.vcell.util.document.PropertyConstants;

public class MolecularComponent extends RbmElementAbstract implements Matchable, VetoableChangeListener, 
	IssueSource, Displayable
{
	public static final String PROPERTY_NAME_COMPONENT_STATE_DEFINITIONS = "componentStateDefinitions";
	
	private String name;                                  // binding site or phosphosite, motif, extracdomain (e.g. tyrosine 77) 
	private List<ComponentStateDefinition> componentStateDefinitions = new ArrayList<ComponentStateDefinition>();    // allowable states (e.g. Phosphorylated, Unphosphorylated)
	private int index = 0; 

	public MolecularComponent(String name) {
		super();
		this.name = name;
	}
	public MolecularComponent(MolecularComponent mc) {
		// deep copy constructor
		super();
		this.name = new String(mc.getName());
		this.index = mc.getIndex();
		for(ComponentStateDefinition csd : mc.getComponentStateDefinitions()) {
			this.componentStateDefinitions.add(new ComponentStateDefinition(csd));
		}
	}
	public final String getName() {
		return name;
	}
	public void addComponentStateDefinition(ComponentStateDefinition componentStateDefinition) {
		if (!componentStateDefinitions.contains(componentStateDefinition)) {
			List<ComponentStateDefinition> newValue = new ArrayList<ComponentStateDefinition>(componentStateDefinitions);
			newValue.add(componentStateDefinition);
			setComponentStateDefinitions(newValue);
		}
	}
	
	public void deleteComponentStateDefinition(ComponentStateDefinition componentStateDefinition) {
		if (componentStateDefinitions.contains(componentStateDefinition)) {
			List<ComponentStateDefinition> newValue = new ArrayList<ComponentStateDefinition>(componentStateDefinitions);
			newValue.remove(componentStateDefinition);
			setComponentStateDefinitions(newValue);
		}
	}
	
	public ComponentStateDefinition createComponentStateDefinition() {
		int count=0;
		String name = null;
		while (true) {
			name = "state" + count;	
			if (getComponentStateDefinition(name) == null) {
				break;
			}	
			count++;
		}
		return new ComponentStateDefinition(name);
	}
	
	public ComponentStateDefinition getComponentStateDefinition(String stateName) {
		for (ComponentStateDefinition cs : componentStateDefinitions)  {
			if (cs.getName().equals(stateName)) {
				return cs;
			}
		}
		return null;
	}
	
	public final List<ComponentStateDefinition> getComponentStateDefinitions() {
		return componentStateDefinitions;
	}
	public void setName(String newValue) throws PropertyVetoException {
		String oldValue = name;
		fireVetoableChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, newValue);
		name = newValue;
		firePropertyChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, newValue);
	}
	
	public final void setComponentStateDefinitions(List<ComponentStateDefinition> newValue) {
		List<ComponentStateDefinition> oldValue = componentStateDefinitions;
		if (oldValue != null) {
			for (ComponentStateDefinition cs : oldValue) {
				cs.removeVetoableChangeListener(this);
			}
		}
		componentStateDefinitions = newValue;
		if (newValue != null) {
			for (ComponentStateDefinition cs : newValue) {
				cs.addVetoableChangeListener(this);
			}
		}
		firePropertyChange(PROPERTY_NAME_COMPONENT_STATE_DEFINITIONS, oldValue, newValue);
	}
	public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
		if (evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_NAME)) {
			if (evt.getSource() instanceof ComponentStateDefinition) {
				String newName = (String) evt.getNewValue();
				for (ComponentStateDefinition cs : componentStateDefinitions) {
					if (cs != evt.getSource()) {
						if (cs.getName().equals(newName)) {
							throw new PropertyVetoException("State '" + newName + "' already exists in " + typeName + " '" + getDisplayName() + "'!", evt);
						}
					}
				}
				if (newName==null){
					throw new PropertyVetoException(ComponentStateDefinition.typeName + " name is null.", evt);
				}
				if (newName.length()<1){
					throw new PropertyVetoException(ComponentStateDefinition.typeName + " name is empty (zero length).", evt);
				}
				if (!newName.equals(TokenMangler.fixTokenStrict(newName))){
					throw new PropertyVetoException(ComponentStateDefinition.typeName + " '" + newName + "' not legal identifier, try '" + TokenMangler.fixTokenStrict(newName) + "'.", evt);
				}
			}
		}			
	}
	
	@Override
	public boolean compareEqual(Matchable aThat) {
		if (this == aThat) {
			return true;
		}
		if (!(aThat instanceof MolecularComponent)) {
			return false;
		}
		MolecularComponent that = (MolecularComponent)aThat;

		if (!Compare.isEqual(name, that.name)) {
			return false;
		}
		if (!Compare.isEqual(index, that.index)) {
			return false;
		}
		if (!Compare.isEqual(componentStateDefinitions, that.componentStateDefinitions)){
			return false;
		}
		return true;
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
	public String getId() {
		System.err.println("getId() not correct for MolecularComponent");
		return "MolecularComponent_"+hashCode();
	}
	
	public String dependenciesToHtml(Map<String, Pair<Displayable, SpeciesPattern>> usedHere) {
		String errMsg = typeName + " '<b>" + getDisplayName() + "'</b> is already being used by:<br>";
		final int MaxListSize = 7;
		int count = 0;
		for(String key : usedHere.keySet()) {
			System.out.println(key);
			if(count >= MaxListSize) {
				errMsg += "<br> ... and more.";
				break;
			}
			Pair<Displayable, SpeciesPattern> o = usedHere.get(key);
			Displayable e = o.one;
			SpeciesPattern sp = o.two;
			errMsg += "<br> - " + e.getDisplayType().toLowerCase() + " <b>" + e.getDisplayName() + "</b>";
			errMsg += ", " + sp.getDisplayType().toLowerCase() + " " + " <b>" + sp.getDisplayName() + "</b>";
			count++;
		}
		return errMsg;
	}

	@Override
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		if(name == null) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Name of " + typeName + " is null", Issue.SEVERITY_ERROR));
		} else if(name.equals("")) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Name of " + typeName + " is empty", Issue.SEVERITY_WARNING));
		} else if(componentStateDefinitions == null) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, typeName + " '" + getDisplayName() + "' State List is null", Issue.SEVERITY_ERROR));
		} else if(componentStateDefinitions.isEmpty()) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, typeName + " '" + getDisplayName() + "' State List is empty", Issue.SEVERITY_INFO));
		} else {
			for (ComponentStateDefinition entity : componentStateDefinitions) {
				entity.gatherIssues(issueContext, issueList);
			}
		}
	}

	public static final String typeName = "Site";
	@Override
	public final String getDisplayName() {
		return getName();
	}
	@Override
	public final String getDisplayType() {
		return typeName;
	}

}
