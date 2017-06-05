package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Graphics;

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

	boolean isEditable();

	boolean isHighlighted(ComponentStatePattern csp);

	boolean isShowNonTrivialOnly();

	boolean isHighlighted(ComponentStateDefinition csd);

	boolean isHighlighted(MolecularComponentPattern mcp);

	boolean isHighlighted(MolecularComponent mc);

	void setComponentStatePattern(ComponentStatePattern csp);

	void setComponentStateDefinition(ComponentStateDefinition csd);

	void setMolecularComponentPattern(MolecularComponentPattern mcp);

	void setMolecularComponent(MolecularComponent mc);
	
	DisplayMode getDisplayMode();

	boolean isShowDifferencesOnly();
	
	RuleAnalysisChanged hasStateChanged(String reactionRuleName, MolecularComponentPattern molecularComponentPattern);

	RuleAnalysisChanged hasStateChanged(MolecularComponentPattern molecularComponentPattern);
	
	RuleAnalysisChanged hasBondChanged(String reactionRuleName, MolecularComponentPattern molecularComponentPattern);
	
	RuleAnalysisChanged hasBondChanged(MolecularComponentPattern molecularComponentPattern);

	RuleAnalysisChanged hasNoMatch(String reactionRuleName, MolecularTypePattern mtp);
	
	RuleAnalysisChanged hasNoMatch(MolecularTypePattern molecularTypePattern);

	boolean isViewSingleRow();

	boolean isShowMoleculeColor();

	void setMolecularTypePattern(MolecularTypePattern mtp);

	void setMolecularType(MolecularType mt);

	boolean isHighlighted(MolecularTypePattern mtp);

	boolean isHighlighted(MolecularType mt);

	void resetSpeciesPattern();

	boolean isHighlighted(SpeciesPattern sp);

	void setSpeciesPattern(SpeciesPattern sp);

	RuleParticipantSignature getSignature();

	GroupingCriteria getCriteria();

	Color getBackground();

	boolean isHighlighted(ReactionRule rr);

	WhatIsHighlighted getWhatIsHighlighted();

	void setWhatIsHighlighted(WhatIsHighlighted whatIsHighlighted);

	void setReactionRule(ReactionRule rr);

}
