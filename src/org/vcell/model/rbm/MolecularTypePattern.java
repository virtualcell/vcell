package org.vcell.model.rbm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vcell.model.rbm.MolecularComponentPattern.BondType;

public class MolecularTypePattern extends RbmElement implements PropertyChangeListener {
	public static final String PROPERTY_NAME_COMPONENT_PATTERN_LIST = "componentPatternList";
	private MolecularType molecularType;
	private List<MolecularComponentPattern> componentPatternList = new ArrayList<MolecularComponentPattern>();
	private int index = 0; // purely for displaying purpose, since molecule can bind to itself

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
		throw new RuntimeException("All components are added in the constructor, so here it can never be null");
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
			if (mcp.getBondType() == BondType.Possible && mcp.getComponentState() == null) {
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
}
