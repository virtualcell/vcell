package cbit.vcell.graph;

import javax.swing.JPanel;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RuleAnalysis;
import org.vcell.model.rbm.RuleAnalysis.MolecularComponentEntry;
import org.vcell.model.rbm.RuleAnalysis.MolecularTypeEntry;
import org.vcell.model.rbm.RuleAnalysisReport;
import org.vcell.model.rbm.SpeciesPattern;

import cbit.vcell.model.ModelRuleFactory;
import cbit.vcell.model.ModelRuleFactory.ModelRuleEntry;
import cbit.vcell.model.ModelRuleFactory.ReactionRuleDirection;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule;

@SuppressWarnings("serial")
public class LargeShapePanel extends JPanel {
	private ReactionRule reactionRule = null;
	//
	// for painting changes we use the RuleAnalysis
	//
	private RuleAnalysisReport report = null;
	private ModelRuleEntry modelRuleEntry = null;
	private boolean bRuleAnalysisFailed = false;
	
	private boolean showDifferencesOnly = false;
	public boolean isShowDifferencesOnly() {
		return showDifferencesOnly;
	}
	public void setShowDifferencesOnly(boolean showDifferencesOnly) {
		this.showDifferencesOnly = showDifferencesOnly;
	}

	public static enum Highlight { on, off }
	public static enum WhatIsHighlighted { reactant, product }
	
	// here we store the entity that needs to be displayed highlighted (selected object and maybe its container)
	
	// the entity that is selected (for example a molecule, component, a state, a pattern...)
	// that and will be painted as "highlighted"
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
	enum RuleAnalysisChanged {
		CHANGED,
		UNCHANGED,
		ANALYSISFAILED
	}
	public RuleAnalysisChanged hasBondChanged(MolecularComponentPattern molecularComponentPattern){
		if (reactionRule == null){
			return RuleAnalysisChanged.ANALYSISFAILED;
		}
		if (modelRuleEntry == null || report == null){
			refreshRuleAnalysis();
		}
		if (!bRuleAnalysisFailed && report != null){
			MolecularComponentEntry molecularComponentEntry = modelRuleEntry.findMolecularComponentEntry(molecularComponentPattern);
			if (report.hasBondChanged(molecularComponentEntry)){
				return RuleAnalysisChanged.CHANGED;
			}else{
				return RuleAnalysisChanged.UNCHANGED;
			}
		}else{
			return RuleAnalysisChanged.ANALYSISFAILED;
		}
	}
	
	public RuleAnalysisChanged hasStateChanged(MolecularComponentPattern molecularComponentPattern){
		if (reactionRule == null){
			return RuleAnalysisChanged.ANALYSISFAILED;
		}
		if (modelRuleEntry == null || report == null){
			refreshRuleAnalysis();
		}
		if (!bRuleAnalysisFailed && report != null){
			MolecularComponentEntry molecularComponentEntry = modelRuleEntry.findMolecularComponentEntry(molecularComponentPattern);
			if (report.hasStateChanged(molecularComponentEntry)){
				return RuleAnalysisChanged.CHANGED;
			}else{
				return RuleAnalysisChanged.UNCHANGED;
			}
		}else{
			return RuleAnalysisChanged.ANALYSISFAILED;
		}
	}
	
	public RuleAnalysisChanged hasNoMatch(MolecularTypePattern molecularTypePattern){
		if (reactionRule == null){
			return RuleAnalysisChanged.ANALYSISFAILED;
		}
		if (modelRuleEntry == null || report == null){
			refreshRuleAnalysis();
		}
		if (!bRuleAnalysisFailed && report != null){
			MolecularTypeEntry molecularTypeEntry = modelRuleEntry.findMolecularTypeEntry(molecularTypePattern);
			if (report.hasNoMatch(molecularTypeEntry)){
				return RuleAnalysisChanged.CHANGED;
			}else{
				return RuleAnalysisChanged.UNCHANGED;
			}
		}else{
			return RuleAnalysisChanged.ANALYSISFAILED;
		}
	}
	
	private void refreshRuleAnalysis(){
		if (reactionRule==null){
			bRuleAnalysisFailed = true;
			return;
		}
		ModelRuleFactory factory = new ModelRuleFactory();
		this.modelRuleEntry = factory.createRuleEntry(reactionRule, 0, ReactionRuleDirection.forward);
		try {
			this.report = RuleAnalysis.analyze(modelRuleEntry, false);
			this.bRuleAnalysisFailed = false;
		}catch (Exception e){
			e.printStackTrace();
			System.err.println("exception computing RuleAnalysis report: "+e.getMessage());
			bRuleAnalysisFailed = true;
		}
	}
	
	@Override
	public void repaint() {
//		System.out.println("repaint");
		if (showDifferencesOnly){
			refreshRuleAnalysis();
		}
		super.repaint();
	}
	
	public void setReactionRule(ReactionRule reactionRule) {
		this.reactionRule = reactionRule;
		repaint();
	}

}
