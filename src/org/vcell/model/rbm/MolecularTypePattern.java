package org.vcell.model.rbm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Matchable;

import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;

public class MolecularTypePattern extends RbmElementAbstract implements Matchable, PropertyChangeListener, IssueSource, Displayable {
	public static final String PROPERTY_NAME_COMPONENT_PATTERN_LIST = "componentPatternList";
	
	private MolecularType molecularType;
	private List<MolecularComponentPattern> componentPatternList = new ArrayList<MolecularComponentPattern>();
	private int index = 0; // purely for displaying purpose, since molecule can bind to itself
	private Map<String,ArrayList<MolecularComponent>> processedMolecularComponentsMultiMap = new HashMap<String,ArrayList<MolecularComponent>>();
	 
	public MolecularTypePattern(MolecularType molecularType) {
		// TODO: this works as long as there can only be one component pattern in the molecular type pattern for each component in the molecular type
		// TODO: this will have to be redesigned once we accept multiple component patterns with the same name
		this(molecularType, true);
	}
	public MolecularTypePattern(MolecularType molecularType, boolean insertComponentsAutomatically) {
		this.molecularType = molecularType;
		// TODO: this works as long as there can only be one component pattern in the molecular type pattern for each component in the molecular type
		// TODO: this will have to be redesigned once we accept multiple component patterns with the same name
		if(insertComponentsAutomatically) {
			for (MolecularComponent mc : this.molecularType.getComponentList()) {
				componentPatternList.add(new MolecularComponentPattern(mc));
			}
		}
	}
	public boolean hasMolecularComponentPattern(MolecularComponent mc) {
		for (MolecularComponentPattern mcp : componentPatternList) {
			if (mcp.getMolecularComponent() == mc) {
				return true;
			}
		}
		return false;
	}
	public MolecularComponentPattern getMolecularComponentPattern(MolecularComponent mc) {
		for (MolecularComponentPattern mcp : componentPatternList) {
			if (mcp.getMolecularComponent() == mc) {
				return mcp;
			}
		}
		MolecularComponentPattern mcp = new MolecularComponentPattern(mc);
		componentPatternList.add(mcp);
		return mcp;
		//throw new RuntimeException("All components are added in the constructor, so here it can never be null");
	}
	
	public void removeMolecularComponentPattern(MolecularComponentPattern molecularComponentPattern) {
		List<MolecularComponentPattern> newValue = new ArrayList<MolecularComponentPattern>(componentPatternList);
		newValue.remove(molecularComponentPattern);
		setComponentPatterns(newValue);
	}
	
	boolean isFullyDefined(){
		for (MolecularComponentPattern patterns : componentPatternList){
			if (!patterns.isFullyDefined()){
				return false;
			}
		}
		return true;
	}

	public final MolecularType getMolecularType() {
		return molecularType;
	}

	public final List<MolecularComponentPattern> getComponentPatternList() {
		return componentPatternList;
	}

	public final void setComponentPatterns(List<MolecularComponentPattern> newValue) {
		List<MolecularComponentPattern> oldValue = componentPatternList;
		if (oldValue != null) {
			for(MolecularComponentPattern mcp : oldValue) {
				mcp.removePropertyChangeListener(this);
			}
		}
		this.componentPatternList = newValue;
		if (newValue != null) {
			for(MolecularComponentPattern mcp : newValue) {
				mcp.addPropertyChangeListener(this);
			}
		}
		firePropertyChange(PROPERTY_NAME_COMPONENT_PATTERN_LIST, oldValue, newValue);
	}

	@Override
	public String toString() {
		return molecularType.getName() + "(" + index + ")";
	}
	
	private void checkIgnorablePatterns() {
		List<MolecularComponentPattern> newValue = new ArrayList<MolecularComponentPattern>(componentPatternList);
		Iterator<MolecularComponentPattern> iter = newValue.iterator();
		while (iter.hasNext()) {
			MolecularComponentPattern mcp = iter.next();
			if (mcp.getBondType() == BondType.Possible && mcp.getComponentStatePattern() == null) {
				iter.remove();
			}
		}
		setComponentPatterns(newValue);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof MolecularComponentPattern) {
			checkIgnorablePatterns();
		}		
	}

	public final int getIndex() {
		return index;
	}

	public final void setIndex(int index) {
		this.index = index;
	}

	public MolecularComponent getFirstUnprocessedMolecularComponent(String name, MolecularComponent[] molecularComponents) {

		if(molecularComponents.length == 0) {
			return null;
		}
		ArrayList<MolecularComponent> processedMolecularComponents = processedMolecularComponentsMultiMap.get(name);
		if(processedMolecularComponents == null) {
			processedMolecularComponents = new ArrayList<MolecularComponent>();
		}
		for (MolecularComponent mc : molecularComponents) {
			if(!processedMolecularComponents.contains(mc)) {
				processedMolecularComponents.add(mc);
				processedMolecularComponentsMultiMap.put(name, processedMolecularComponents);
				return mc;
			}
		}
		return null;
	}
	
	public void ClearProcessedMolecularComponentsMultiMap() {
		processedMolecularComponentsMultiMap.clear();
	}
	
	@Override
	public boolean compareEqual(Matchable aThat) {
		if (this == aThat) {
			return true;
		}
		if (!(aThat instanceof MolecularTypePattern)) {
			return false;
		}
		MolecularTypePattern that = (MolecularTypePattern)aThat;

		if (!Compare.isEqual(molecularType, that.molecularType)){
			return false;
		}
		if (!Compare.isEqual(componentPatternList, that.componentPatternList)){
			return false;
		}
		return true;
	}

	@Override
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		if(componentPatternList == null) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, typeName + " '" + getDisplayName() + "' " + MolecularComponentPattern.typeName + " List is null", Issue.SEVERITY_ERROR));
		} else if(componentPatternList.isEmpty()) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, typeName + " '" + getDisplayName() + "' " + MolecularComponentPattern.typeName + " List is empty", Issue.SEVERITY_INFO));
		} else {
//			if(!isFullyDefined()) {
//				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, typeName + " '" + toString() + "' " + MolecularComponentPattern.typeName + " List is not fully defined", Issue.SEVERITY_WARNING));
//			}
			for (MolecularComponentPattern entity : componentPatternList) {
				entity.gatherIssues(issueContext, issueList);
			}
			//
			//	Big sanity check: take each molecule type and make sure it's present in the rbmModelContainer
			//
			MolecularType mt = getMolecularType();
			IssueSource m = issueContext.getContextObject(IssueContext.ContextType.Model);
			if(!(m instanceof Model)) {
				return;
			}
			IssueContext.ContextType ownerContextType = issueContext.getContextType();
			IssueSource owner = issueContext.getContextObject(ownerContextType);
			RbmModelContainer c = ((Model)m).getRbmModelContainer();
			if(!c.getMolecularTypeList().contains(mt)) {
				issueList.add(new Issue(owner, issueContext, IssueCategory.Identifiers, MolecularType.typeName + " '" + mt.getName() + "' missing from the " + mt.getDisplayType() + " table.", Issue.SEVERITY_ERROR));
			}
			for(MolecularComponentPattern mcp : getComponentPatternList()) {
				MolecularComponent mc = mcp.getMolecularComponent();
				if(!mt.getComponentList().contains(mc)) {
					issueList.add(new Issue(owner, issueContext, IssueCategory.Identifiers, MolecularComponent.typeName + " '" + mc.toString() + "' missing from the " + mt.getDisplayType() + " definition.", Issue.SEVERITY_ERROR));
				}
				ComponentStatePattern csp = mcp.getComponentStatePattern();
				if(csp != null && !csp.isAny()) {
					ComponentStateDefinition cs = csp.getComponentStateDefinition();
					if(cs == null) {
						issueList.add(new Issue(owner, issueContext, IssueCategory.Identifiers, "State '" + mc.toString() + "' missing from the " + ComponentStatePattern.typeName + ".", Issue.SEVERITY_ERROR));
					}
					if(!mc.getComponentStateDefinitions().contains(cs)) {
						issueList.add(new Issue(owner, issueContext, IssueCategory.Identifiers, "State '" + mc.toString() + "' missing from the " + mt.getDisplayType() + " definition.", Issue.SEVERITY_ERROR));
					}
				}
			}
			for(MolecularComponent mc : mt.getComponentList()) {
				MolecularComponentPattern mcp = getMolecularComponentPattern(mc);
				if(mcp == null) {
					issueList.add(new Issue(owner, issueContext, IssueCategory.Identifiers, MolecularComponentPattern.typeName + " missing for the " + mc.getDisplayType() + " '" + mc.getDisplayName(), Issue.SEVERITY_ERROR));
				}
			}
		}			
	}

	public static final String typeName = "Molecular Type Pattern";
	@Override
	public String getDisplayName() {
		MolecularType mt = getMolecularType();
		if(mt == null) {
			return "";
		}
		return mt.getDisplayName() + "(" + index + ")";
	}
	@Override
	public String getDisplayType() {
		return typeName;
	}


}
