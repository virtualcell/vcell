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
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Matchable;

import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;

public class MolecularTypePattern extends RbmElementAbstract implements Matchable, PropertyChangeListener, IssueSource {
	public static final String PROPERTY_NAME_COMPONENT_PATTERN_LIST = "componentPatternList";
	
	private MolecularType molecularType;
	private List<MolecularComponentPattern> componentPatternList = new ArrayList<MolecularComponentPattern>();
	private int index = 0; // purely for displaying purpose, since molecule can bind to itself
	private Map<String,ArrayList<MolecularComponent>> processedMolecularComponentsMultiMap = new HashMap<String,ArrayList<MolecularComponent>>();
	 
	public MolecularTypePattern(MolecularType molecularType) {
		this.molecularType = molecularType;
		for (MolecularComponent mc : this.molecularType.getComponentList()) {
			componentPatternList.add(new MolecularComponentPattern(mc));
		}
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
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Molecular Type Pattern '" + toString() + "' Component Pattern List is null", Issue.SEVERITY_ERROR));
		} else if(componentPatternList.isEmpty()) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Molecular Type Pattern '" + toString() + "' Component Pattern List is empty", Issue.SEVERITY_INFO));
		} else {
//			if(!isFullyDefined()) {
//				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Molecular Type Pattern '" + toString() + "' Component Pattern List is not fully defined", Issue.SEVERITY_WARNING));
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
				issueList.add(new Issue(owner, issueContext, IssueCategory.Identifiers, "Molecular Type '" + mt.getName() + "' missing from the SpeciesTypes table.", Issue.SEVERITY_ERROR));
			}
			for(MolecularComponentPattern mcp : getComponentPatternList()) {
				MolecularComponent mc = mcp.getMolecularComponent();
				if(!mt.getComponentList().contains(mc)) {
					issueList.add(new Issue(owner, issueContext, IssueCategory.Identifiers, "Molecular Component '" + mc.toString() + "' missing from the SpeciesType definition.", Issue.SEVERITY_ERROR));
				}
				ComponentStatePattern csp = mcp.getComponentStatePattern();
				if(csp != null && !csp.isAny()) {
					ComponentStateDefinition cs = csp.getComponentStateDefinition();
					if(cs == null) {
						issueList.add(new Issue(owner, issueContext, IssueCategory.Identifiers, "State '" + mc.toString() + "' missing from the ComponentStatePattern.", Issue.SEVERITY_ERROR));
					}
					if(!mc.getComponentStateDefinitions().contains(cs)) {
						issueList.add(new Issue(owner, issueContext, IssueCategory.Identifiers, "State '" + mc.toString() + "' missing from the SpeciesType definition.", Issue.SEVERITY_ERROR));
					}
				}
			}
			
		}			
	}

//	public String getId() {
//		System.err.println("MolecularTypePattern id generated badly");
//		return "MolecularTypePattern_" + hashCode();
//	}
	
}
