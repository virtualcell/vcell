package org.vcell.model.rbm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.PropertyConstants;

import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.model.BioModelEntityObject;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class MolecularType extends RbmElementAbstract implements BioModelEntityObject, Matchable, VetoableChangeListener, 
	IssueSource, Displayable
{
	public static final String PROPERTY_NAME_COMPONENT_LIST = "componentList";
	public static final String PROPERTY_NAME_ANNOTATION = "annotation";
	
	private String name;	
	private List<MolecularComponent> componentList = new ArrayList<>();
	private transient Model model = null;
	
	private Set<Structure> anchorSet = new HashSet<>();	// list of structures where the molecule are allowed to exist (Feature or Membrane) 
	private boolean bAnchorAll = true;					// if true ignore the anchorSet content (by default any compartment is allowed)

	
	public MolecularType(String name, Model model) {
		this.name = name;
		this.model = model;
	}
	public MolecularType(MolecularType mt, Model model) {
		// TODO: anchors not done yet, solve structures mapping first
		// deep copy constructor
		this.name = new String(mt.getName());
		this.model = model;
		for(MolecularComponent mc : mt.getComponentList()) {
			this.componentList.add(new MolecularComponent(mc));
		}
	}
	
	public void setComment(String comment) {
		if(comment == null) {
			return;
		}
		try {	// set text from annotationTextField in free text annotation for species in vcMetaData (from model)
			if(model != null && model.getVcMetaData() != null) {
				VCMetaData vcMetaData = model.getVcMetaData();
				String oldValue = vcMetaData.getFreeTextAnnotation(this);
				if(!Compare.isEqualOrNull(oldValue, comment)){
					vcMetaData.setFreeTextAnnotation(this, comment);
					firePropertyChange(PROPERTY_NAME_ANNOTATION, oldValue, comment);
				}
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
		}
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
	
	// returns an array containing all the components of the same name
	// useless for now since we right now don't allow more than 1 component with the same name
	// TODO: make it into an array list, not array
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
	
	public Set<Structure> getAnchors() {
		if(anchorSet == null) {
			anchorSet = new HashSet<>();
		}
//		for(Iterator<Structure> it = anchorSet.iterator(); it.hasNext(); ) {
//			Structure element = it.next();
//			if (model.getStructure(element.getName()) == null) {
//				it.remove();
//			}
//		}		
		return anchorSet;
	}
	public boolean addAnchor(Structure s) {
		return getAnchors().add(s);
	}
	public boolean removeAnchor(Structure s) {
		return getAnchors().remove(s);
	}

	public boolean isAnchorAll() {
		return bAnchorAll;
	}
	public void setAnchorAll(boolean bAnchorAll) {
		this.bAnchorAll = bAnchorAll;
	}

	public Model getModel() {
		return model;
	}
	public void setModel(Model model){
		this.model = model;
	}

	public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
		if (evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_NAME)) {
			if (evt.getSource() instanceof MolecularComponent) {
				String newName = (String) evt.getNewValue();
				for (MolecularComponent mc : componentList) {
					if (mc != evt.getSource()) {
						if (mc.getName().equals(newName)) {
							throw new PropertyVetoException(MolecularComponent.typeName + " '" + newName + "' already exists in " + typeName + " '" + getDisplayName() + "'!", evt);
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
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Name of " + getDisplayType() + " is null", Issue.Severity.ERROR));
		} else if(name.equals("")) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Name of " + getDisplayType() + " is empty", Issue.Severity.ERROR));
		} else if(!name.equals(TokenMangler.fixTokenStrict(name))) {
			String msg = "Name of " + getDisplayType() + " is invalid";
			String tip = "Valid names must start with a letter or underscore and contain only letters, numbers and the '_' character (underscore)";
			issueList.add(new Issue(this, this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.ERROR));
		}
		if(componentList == null) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, getDisplayType() + " '" + getDisplayName() + MolecularComponent.typeName + "' List is null", Issue.Severity.ERROR));
		} else if(componentList.isEmpty()) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, getDisplayType() + " '" + getDisplayName() + MolecularComponent.typeName + "' List is empty", Issue.Severity.INFO));
		} else {
			for (MolecularComponent mc : componentList) {
				MolecularComponent[] mcList = getMolecularComponents(mc.getName());
				if(mcList.length > 1) {
					String msg = "Duplicate " + mc.getDisplayType() + " '" + mc.getDisplayName() + "' in the definition of the " + MolecularType.typeName + ".";
					issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
				}
				String mcName = mc.getName();
				if(!mcName.equals(TokenMangler.fixTokenStrict(mcName))) {
					String msg = "Name of " + MolecularComponent.typeName + " " + mcName + " is invalid";
					String tip = "Valid names must start with a letter or underscore and contain only letters, numbers and the '_' character (underscore)";
					issueList.add(new Issue(this, mc, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.ERROR));
				}
				for(ComponentStateDefinition csd : mc.getComponentStateDefinitions()) {
					String csdName = csd.getName();
					if(!csdName.equals(TokenMangler.fixTokenStrict(csdName))) {
						String msg = "Name of " + ComponentStateDefinition.typeName + " " + csdName + " is invalid";
						String tip = "Valid names must start with a letter or underscore and contain only letters, numbers and the '_' character (underscore)";
						issueList.add(new Issue(this, csd, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.ERROR));
					}
				}
			}
			for (MolecularComponent entity : componentList) {
				entity.gatherIssues(issueContext, issueList);
			}
		}
		if(!isAnchorAll() && getAnchors().isEmpty()) {
			String msg = getDisplayType() + " " + getDisplayName() + " must be anchored to at least one Structure.";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));

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

	@Override
	public String getTypeLabel() {
		return typeName;
	}

	@Override
	public Structure getStructure() {
		throw new UnsupportedOperationException("getStructure() not available for Molecular Type Objects");
	}


}
