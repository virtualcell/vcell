package cbit.vcell.graph.gui;

import javax.swing.JPanel;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;

import cbit.vcell.graph.LargeShapeCanvas;
import cbit.vcell.graph.ShapeModeInterface;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule;

@SuppressWarnings("serial")
public abstract class LargeShapePanel extends JPanel implements LargeShapeCanvas, ShapeModeInterface {

    private boolean showDifferencesOnly = false;
    private boolean bShowMoleculeColor = false;
    private boolean bShowNonTrivialOnly = false;

    // zooming the shape, 0 means normal size, a negative number means smaller shape
    private static final int SmallestZoomFactor = -7;            // -7 is the smallest where the shapes scale decently well
    private static final int DefaultZoomFactor = 0;
    private static final int LargestZoomFactor = 0;
    private int zoomFactor = DefaultZoomFactor;

    // by default the shapes are editable and their border and text is black / gray, aso
    // otherwise they are a shade of brown, very much alike the DefaultScrollTableCellRenderer.uneditableForeground
    private boolean editable = true;

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
    public void setSpeciesPattern(SpeciesPattern sp) {
        this.sp = sp;
    }

    @Override
    public WhatIsHighlighted getWhatIsHighlighted() {
        return this.whatIsHighlighted;
    }

    @Override
    public void setWhatIsHighlighted(WhatIsHighlighted whatIsHighlighted) {
        this.whatIsHighlighted = whatIsHighlighted;
    }

    @Override
    public void setSelectedReactionRule(ReactionRule rr) {
        this.rr = rr;
    }

    @Override
    public void setMolecularTypePattern(MolecularTypePattern mtp) {
        this.mtp = mtp;
    }

    @Override
    public void setMolecularType(MolecularType mt) {
        this.mt = mt;
    }

    @Override
    public void setComponentStatePattern(ComponentStatePattern csp) {
        this.csp = csp;
    }

    @Override
    public void setComponentStateDefinition(ComponentStateDefinition csd) {
        this.csd = csd;
    }

    @Override
    public void setMolecularComponentPattern(MolecularComponentPattern mcp) {
        this.mcp = mcp;
    }

    @Override
    public void setMolecularComponent(MolecularComponent mc) {
        this.mc = mc;
    }

    @Override
    public void setShowDifferencesOnly(boolean showDifferencesOnly) {
        this.showDifferencesOnly = showDifferencesOnly;
    }

    @Override
    public boolean isShowDifferencesOnly() {
        return this.showDifferencesOnly;
    }

    @Override
    public void setShowMoleculeColor(boolean bShowMoleculeColor) {
        this.bShowMoleculeColor = bShowMoleculeColor;
    }

    @Override
    public boolean isShowMoleculeColor() {
        return this.bShowMoleculeColor;
    }

    @Override
    public void setShowNonTrivialOnly(boolean bShowNonTrivialOnly) {
        this.bShowNonTrivialOnly = bShowNonTrivialOnly;
    }

    @Override
    public boolean isShowNonTrivialOnly() {
        return this.bShowNonTrivialOnly;
    }

    public boolean zoomLarger() {    // returns false when upper limit was reached
        this.zoomFactor++;
        if (this.zoomFactor >= LargestZoomFactor) {
            this.zoomFactor = LargestZoomFactor;
            System.out.println("MAX. Factor is " + this.zoomFactor);
            return false;
        } else {
            System.out.println("Up. Factor is " + this.zoomFactor);
            return true;
        }
    }

    public void setZoomFactor(int newZoomFactor) {
        if (newZoomFactor > LargestZoomFactor || newZoomFactor < SmallestZoomFactor) {
            throw new RuntimeException("Large Shape Panel zoom factor is out of bounds");
        }
        this.zoomFactor = newZoomFactor;
    }

    public boolean zoomSmaller() {    // returns false when lower limit was reached
        this.zoomFactor--;
        if (this.zoomFactor <= SmallestZoomFactor) {
            this.zoomFactor = SmallestZoomFactor;
            System.out.println("MIN. Factor is " + this.zoomFactor);
            return false;
        } else {
            System.out.println("Down. Factor is " + this.zoomFactor);
            return true;
        }
    }

    public boolean isLargestZoomFactor() {
        return this.zoomFactor >= LargestZoomFactor;
    }

    public boolean isSmallestZoomFactor() {
        return this.zoomFactor <= SmallestZoomFactor;
    }

    public int getZoomFactor() {
        return this.zoomFactor;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return this.editable;
    }

    // ----------------------------------------------------------------------------------------------------

    public void resetSpeciesPattern() {
        this.sp = null;
        this.mtp = null;
        this.mcp = null;
        this.csp = null;
    }

    public void setHighlightedRecursively(MolecularType theirs, Highlight highlight) {
        if (highlight == Highlight.on) {
            this.mt = theirs;
        } else {
            this.mt = null;
            this.mc = null;
            this.csd = null;
        }
    }

    public void setHighlightedRecursively(RbmObservable theirs, Highlight highlight) {
        if (highlight == Highlight.on) {
            this.o = theirs;
        } else {
            this.sp = null;
            this.mtp = null;
            this.mcp = null;
            this.csp = null;
        }
    }

    public boolean isHighlighted(MolecularType candidate) {
        return candidate == this.mt;
    }

    public boolean isHighlighted(MolecularTypePattern candidate) {
        return candidate == this.mtp;
    }

    public boolean isHighlighted(MolecularComponent candidate) {
        return candidate == this.mc;
    }

    public boolean isHighlighted(MolecularComponentPattern candidate) {
        return candidate == this.mcp;
    }

    public boolean isHighlighted(ComponentStateDefinition candidate) {
        return candidate == this.csd;
    }

    public boolean isHighlighted(ComponentStatePattern candidate) {
        return candidate == this.csp;
    }

    public boolean isHighlighted(SpeciesPattern candidate) {
        return candidate == this.sp;
    }

    public boolean isHighlighted(ReactionRule candidate) {
        return candidate == this.rr;
    }

}
