package cbit.vcell.graph;

import javax.swing.JPanel;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;

import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule;

@SuppressWarnings("serial")
public class LargeShapePanel extends JPanel implements ShapeModeInterface {
	
	private boolean showDifferencesOnly = false;
	private boolean bViewSingleRow = false;

	public static enum Highlight { on, off }
	public static enum WhatIsHighlighted { reactant, product }
	// the entity that is selected (for example a molecule, component, a state, a pattern...)
	// and will be painted as "highlighted"
	public ReactionRule rr = null;
	public WhatIsHighlighted whatIsHighlighted = WhatIsHighlighted.reactant;
	public RbmObservable o = null;
	public SpeciesPattern sp = null;
	public MolecularType mt = null;
	public MolecularTypePattern mtp = null;
	public MolecularComponent mc = null;
	public MolecularComponentPattern mcp = null;
	public ComponentStateDefinition csd = null;
	public ComponentStatePattern csp = null;
	// bonds are not being highlighted
	
	@Override
	public boolean isShowDifferencesOnly() {
		return showDifferencesOnly;
	}
	@Override
	public void setShowDifferencesOnly(boolean showDifferencesOnly) {
		this.showDifferencesOnly = showDifferencesOnly;
	}
	public boolean isViewSingleRow() {
		return bViewSingleRow;
	}
	public void setViewSingleRow(boolean bViewSingleRow) {
		this.bViewSingleRow = bViewSingleRow;
	}
	// ----------------------------------------------------------------------------------------------------
	
	public void resetSpeciesPattern() {
		sp = null;
		mtp = null;
		mcp = null;
		csp = null;
	}
	public void setHighlightedRecursively(MolecularType theirs, Highlight highlight) {
		if(highlight == Highlight.on) {
			mt = theirs;
		} else {
			mt = null;
			mc = null;
			csd = null;
		}
	}
	public void setHighlightedRecursively(RbmObservable theirs, Highlight highlight) {
		if(highlight == Highlight.on) {
			o = theirs;
		} else {
			sp = null;
			mtp = null;
			mcp = null;
			csp = null;
		}
	}

	public boolean isHighlighted(MolecularType candidate) {
		if(candidate == mt) {
			return true;
		}
		return false;
	}
	public boolean isHighlighted(MolecularTypePattern candidate) {
		if(candidate == mtp) {
			return true;
		}
		return false;
	}
	public boolean isHighlighted(MolecularComponent candidate) {
		if(candidate == mc) {
			return true;
		}
		return false;
	}
	public boolean isHighlighted(MolecularComponentPattern candidate) {
		if(candidate == mcp) {
			return true;
		}
		return false;
	}

	public boolean isHighlighted(ComponentStateDefinition candidate) {
		if(candidate == csd) {
			return true;
		}
		return false;
	}

	public boolean isHighlighted(ComponentStatePattern candidate) {
		if(candidate == csp) {
			return true;
		}
		return false;
	}
	public boolean isHighlighted(SpeciesPattern candidate) {
		if(candidate == sp) {
			return true;
		}
		return false;
	}
	public boolean isHighlighted(ReactionRule candidate) {
		if(candidate == rr) {
			return true;
		}
		return false;
	}

	

}
