package org.vcell.model.rbm;

import org.vcell.model.rbm.SpeciesPattern.Bond;


@SuppressWarnings("serial")
public class MolecularComponentPattern extends RbmElement {
	public static final String PROPERTY_NAME_COMPONENT_STATE = "componentState";
	public static final String PROPERTY_NAME_BOND_TYPE = "bondType";
	public static final String PROPERTY_NAME_BOND_ID = "bondId";
	public static final String PROPERTY_NAME_VISIBLE = "bondId";
	private MolecularComponent molecularComponent;
	private ComponentState componentState;
	private boolean bVisible = false;
		
	private Bond bond = null;
	
	public enum BondType {
		Specified(""), // numbers
		Exists("+"),    // "+"
		Possible("?"),  // "?"
		None("-");  	   //
		
		public String symbol;
		BondType(String s) {
			this.symbol = s;
		}		
	}
	
	private int bondId = -1;                // used in BNGL for mapping notation (e.g. 1, 2, 3)
	private BondType bondType = BondType.Possible;
	
	public MolecularComponentPattern(MolecularComponent molecularComponent) {
		super();
		this.molecularComponent = molecularComponent;
	}
	
	public  boolean isFullyDefined(){
		return !componentState.isAny();
	}

	public final MolecularComponent getMolecularComponent() {
		return molecularComponent;
	}

	public final ComponentState getComponentState() {
		return componentState;
	}

	public final void setComponentState(ComponentState newValue) {
		if(componentState == newValue) {
			return;
		}
		ComponentState oldValue = componentState;
		this.componentState = newValue;
		firePropertyChange(PROPERTY_NAME_COMPONENT_STATE, oldValue, newValue);
		setVisible(!isImplied());
	}

	public final BondType getBondType() {
		return bondType;
	}

	public final int getBondId() {
		return bondId;
	}
	
	public final void setBondType(BondType newValue) {
		BondType oldValue = bondType;
		this.bondType = newValue;		
		firePropertyChange(PROPERTY_NAME_BOND_TYPE, oldValue, newValue);
		setVisible(!isImplied());
	}

	public final void setBondId(int newValue) {
		this.bondId = newValue;
		setBondType(BondType.Specified);
		setVisible(!isImplied());
	}
	
	public final Bond getBond() {
		return bond;
	}

	public final void setBond(Bond bond) {
		this.bond = bond;
	}

	@Override
	public String toString() {
		return RbmUtils.toBnglString(this);
	}
	
	public boolean isImplied() {
		return componentState == null && bondType == BondType.Possible;
	}

	public final boolean isbVisible() {
		return bVisible;
	}

	public final void setVisible(boolean newValue) {
		boolean oldValue = bVisible;
		this.bVisible = newValue;
		firePropertyChange(PROPERTY_NAME_VISIBLE, oldValue, newValue);
	}
	
	
}
