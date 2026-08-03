package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;

import cbit.vcell.graph.ReactionCartoon.RuleAnalysisChanged;
import cbit.vcell.model.GroupingCriteria;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.RuleParticipantSignature;

public interface LargeShapeCanvas {

	public static final Color uneditableShape = new Color(0x9F4F07);
	public static enum Highlight { on, off }
	public static enum WhatIsHighlighted { reactant, product }
	public static enum DisplayMode { participantSignatures, rules, other }; 
	public static final int SmallestZoomFactorWithText = -5;	// -5 is the smallest where we show text

	
	int getZoomFactor();
	Graphics getGraphics();

	/** Off-screen surface backing {@link #measuringGraphics}; never painted to the display. */
	BufferedImage TEXT_MEASURING_BUFFER = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

	/**
	 * A {@link Graphics} usable for measuring text, <b>never null</b>.
	 *
	 * <p>Every implementation of {@link #getGraphics()} ends at
	 * {@link Component#getGraphics()}, which returns {@code null} while the component
	 * is not displayable — for instance while shapes are being laid out before their
	 * panel is part of a visible window. Shape construction measures strings at exactly
	 * that point, so using the result directly crashed the client with an NPE
	 * (issue #1749).
	 *
	 * <p>When there is no real graphics context this falls back to an off-screen
	 * buffer, carrying over the canvas's own font so the measurements still match what
	 * will eventually be painted. Callers get sensible sizes instead of an exception,
	 * and are re-laid out normally once the panel is realized.
	 */
	static Graphics measuringGraphics(LargeShapeCanvas canvas) {
		if (canvas != null) {
			Graphics gc = canvas.getGraphics();
			if (gc != null) {
				return gc;
			}
		}
		Graphics fallback = TEXT_MEASURING_BUFFER.getGraphics();
		if (canvas instanceof Component) {
			Font font = ((Component) canvas).getFont();
			if (font != null) {
				fallback.setFont(font);
			}
		}
		return fallback;
	}
	DisplayMode getDisplayMode();
	RuleParticipantSignature getSignature();
	GroupingCriteria getCriteria();
	Color getBackground();

	boolean isEditable();
	boolean isShowNonTrivialOnly();
	boolean isShowDifferencesOnly();
	boolean isViewSingleRow();
	boolean isShowMoleculeColor();

	boolean isHighlighted(ComponentStatePattern csp);
	boolean isHighlighted(ComponentStateDefinition csd);
	boolean isHighlighted(MolecularComponentPattern mcp);
	boolean isHighlighted(MolecularComponent mc);
	boolean isHighlighted(MolecularTypePattern mtp);
	boolean isHighlighted(MolecularType mt);
	boolean isHighlighted(SpeciesPattern sp);
	boolean isHighlighted(ReactionRule rr);

	void setSelectedReactionRule(ReactionRule rr);
	void setComponentStatePattern(ComponentStatePattern csp);
	void setComponentStateDefinition(ComponentStateDefinition csd);
	void setMolecularComponentPattern(MolecularComponentPattern mcp);
	void setMolecularComponent(MolecularComponent mc);
	void setMolecularTypePattern(MolecularTypePattern mtp);
	void setMolecularType(MolecularType mt);
	void setSpeciesPattern(SpeciesPattern sp);
	void resetSpeciesPattern();

	WhatIsHighlighted getWhatIsHighlighted();
	void setWhatIsHighlighted(WhatIsHighlighted whatIsHighlighted);

	RuleAnalysisChanged hasStateChanged(String reactionRuleName, MolecularComponentPattern molecularComponentPattern);
	RuleAnalysisChanged hasStateChanged(MolecularComponentPattern molecularComponentPattern);
	RuleAnalysisChanged hasBondChanged(String reactionRuleName, MolecularComponentPattern molecularComponentPattern);
	RuleAnalysisChanged hasBondChanged(MolecularComponentPattern molecularComponentPattern);
	RuleAnalysisChanged hasNoMatch(String reactionRuleName, MolecularTypePattern mtp);
	RuleAnalysisChanged hasNoMatch(MolecularTypePattern molecularTypePattern);

}
