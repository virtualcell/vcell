package org.vcell.model.rbm;

import java.util.List;

import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;


@SuppressWarnings("serial")
public class MolecularComponentPattern extends RbmElementAbstract implements Matchable, IssueSource, Displayable
{
	public static final String PROPERTY_NAME_COMPONENT_STATE = "componentStatePattern";
	public static final String PROPERTY_NAME_BOND_TYPE = "bondType";
	public static final String PROPERTY_NAME_BOND_ID = "bondId";
	public static final String PROPERTY_NAME_VISIBLE = "bondId";
	
	private MolecularComponent molecularComponent;
	private ComponentStatePattern componentStatePattern;
	private boolean bVisible = false;
	private Bond bond = null;
	private int bondId = -1;                // used in BNGL for mapping notation (e.g. 1, 2, 3)
	private BondType bondType = BondType.Possible;
	private transient boolean bHighlighted = false;
	
	public enum BondType {
		Specified(""), // numbers
		Exists("+"),    // "+"
		Possible("?"),  // "?"
		None("-");  	   //
		
		public String symbol;
		BondType(String s) {
			this.symbol = s;
		}
		
		public static BondType fromSymbol(String symbol) throws NumberFormatException {
			for (BondType bondType : values()){
				if (bondType.symbol.equals(symbol)){
					return bondType;
				}
			}
			int bondInt = Integer.parseInt(symbol);		// not used, just for debugging
			return Specified;
		}
	}
	
	
//	public String getId() {
//		System.err.println("MolecularComponentPattern id generated badly");
//		return "MolecularComponentPattern_"+hashCode();
//	}

	public MolecularComponentPattern(MolecularComponent molecularComponent) {
		super();
		this.molecularComponent = molecularComponent;
		if(!molecularComponent.getComponentStateDefinitions().isEmpty()) {
			componentStatePattern = new ComponentStatePattern();
		}
	}
	// !!! copy constructor - do never call this standalone because the bonds will be probably pointing wrong
	// normally this should only be called from within the copy constructor of a species pattern
	public MolecularComponentPattern(MolecularType thisMt, MolecularComponentPattern that) {
		super();
		String thatMcName = that.getMolecularComponent().getName();
		MolecularComponent thisMc = thisMt.getMolecularComponent(thatMcName);
		this.molecularComponent = thisMc;
		if(that.getMolecularComponent().getComponentStateDefinitions().isEmpty()) {
			this.componentStatePattern = null;		// if the component has no states then the component pattern must be null
		} else {
			this.componentStatePattern = new ComponentStatePattern(that.getComponentStatePattern());	// we clone the component state pattern
		}
		this.bVisible = that.isbVisible();
		// we can't initialize the bond here properly, we are in the middle of cloning and the 
		// corresponding mtp and mcp may not be created yet
		this.bondId = that.getBondId();
		this.bondType = that.getBondType();
		this.bond = null;
	}

	public  boolean isFullyDefined(){
		return !componentStatePattern.isAny();
	}

	public final MolecularComponent getMolecularComponent() {
		return molecularComponent;
	}

	public final ComponentStatePattern getComponentStatePattern() {
		return componentStatePattern;
	}

	public final void setComponentStatePattern(ComponentStatePattern newValue) {
		if(componentStatePattern == newValue) {
			return;
		}
		ComponentStatePattern oldValue = componentStatePattern;
		this.componentStatePattern = newValue;
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
		final boolean a = componentStatePattern == null || componentStatePattern.isAny();
		final boolean b = bondType == BondType.Possible;
		return a && b;
	}
	
	public boolean isAmbiguousState() {
		final boolean a = !molecularComponent.getComponentStateDefinitions().isEmpty() && componentStatePattern.isAny();
		return a;
	}
	public boolean isAmbiguousBond() {
		final boolean a = bondType == BondType.Exists;
		final boolean b = bondType == BondType.Possible;
		return a || b;
	}

	public final boolean isbVisible() {
		return bVisible;
	}

	public final void setVisible(boolean newValue) {
		boolean oldValue = bVisible;
		this.bVisible = newValue;
		firePropertyChange(PROPERTY_NAME_VISIBLE, oldValue, newValue);
	}
	
	@Override
	public boolean compareEqual(Matchable aThat) {
		if (this == aThat) {
			return true;
		}
		if (!(aThat instanceof MolecularComponentPattern)) {
			return false;
		}
		MolecularComponentPattern that = (MolecularComponentPattern)aThat;
		
		if (!Compare.isEqual(molecularComponent, that.molecularComponent)) {
			return false;
		}
		if (!Compare.isEqual(componentStatePattern, that.componentStatePattern)) {
			return false;
		}
		if(!(bVisible == that.bVisible)) {
			return false;
		}
		// we don't compare the bonds since they can be recomputed at any time from the molecular component pattern
//		if (!(Compare.isEqualOrNull(bond,that.bond))) {
//			return false;
//		}
		if (!Compare.isEqual(bondType, that.bondType)) {
			return false;
		}
		return true;
	}

	@Override
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		if(molecularComponent == null) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, MolecularComponent.typeName + " of " + typeName + "  '" + toString() + "' is null", Issue.SEVERITY_INFO));
		} else {
			//molecularComponent.gatherIssues(issueContext, issueList);	// we call this somewhere else already
		}
		if(componentStatePattern == null) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "State of " + typeName + "  '" + toString() + "' is null", Issue.SEVERITY_INFO));
		} else {
			//componentStatePattern.gatherIssues(issueContext, issueList);		// we call this somewhere else already
		}
	}

	public boolean isHighlighted() {
		return bHighlighted;
	}
	public void setHighlighted(boolean isHighlighted) {
		this.bHighlighted = isHighlighted;
	}

	public static final String typeName = "Site";		// normally would be site pattern but we try to simplify and get rid of the "pattern" part
	@Override
	public String getDisplayName() {
		String name = toString();
		if(name == null) {
			return "";
		} else {
			return name;
		}
	}
	@Override
	public String getDisplayType() {
		return typeName;
	}
	
}
