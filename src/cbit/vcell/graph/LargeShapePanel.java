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
	
	// zooming the shape, 0 means normal size, a negative number means smaller shape
	private static final int SmallestZoomFactor = -7;			// -7 is the smallest where the shapes scale decently well
	public static final int SmallestZoomFactorWithText = -5;	// -5 is the smallest where we show text
	private static final int DefaultZoomFactor = 0;
	private static final int LargestZoomFactor = 0;
	private int zoomFactor = DefaultZoomFactor;	

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

	public boolean zoomLarger() {	// returns false when upper limit was reached
		zoomFactor++;
		if(zoomFactor >= LargestZoomFactor) {
			zoomFactor = LargestZoomFactor;
			System.out.println("MAX. Factor is " + zoomFactor);
			return false;
		} else {
			System.out.println("Up. Factor is " + zoomFactor);
			return true;
		}
	}
	public void setZoomFactor(int newZoomFactor) {
		if(newZoomFactor > LargestZoomFactor || newZoomFactor < SmallestZoomFactor) {
			throw new RuntimeException("Large Shape Panel zoom factor is out of bounds");
		}
		zoomFactor = newZoomFactor;
	}
	public boolean zoomSmaller() {	// returns false when lower limit was reached
		zoomFactor--;
		if(zoomFactor <= SmallestZoomFactor) {
			zoomFactor = SmallestZoomFactor;
			System.out.println("MIN. Factor is " + zoomFactor);
			return false;
		} else {
			System.out.println("Down. Factor is " + zoomFactor);
			return true;
		}
	}
	public boolean isLargestZoomFactor() {
		return zoomFactor >= LargestZoomFactor ? true : false;
	}
	public boolean isSmallestZoomFactor() {
		return zoomFactor <= SmallestZoomFactor ? true : false;
	}
	public int getZoomFactor() {
		return zoomFactor;
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
