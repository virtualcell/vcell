package cbit.vcell.graph;

import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RuleAnalysis;
import org.vcell.model.rbm.RuleAnalysisReport;
import org.vcell.model.rbm.RuleAnalysis.MolecularComponentEntry;
import org.vcell.model.rbm.RuleAnalysis.MolecularTypeEntry;

import cbit.vcell.model.ModelRuleFactory;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ModelRuleFactory.ModelRuleEntry;
import cbit.vcell.model.ModelRuleFactory.ReactionRuleDirection;

@SuppressWarnings("serial")
public class RulesShapePanel extends LargeShapePanel {

	private ReactionRule reactionRule = null;

	//
	// for painting changes we use the RuleAnalysis
	//
	private RuleAnalysisReport report = null;
	private ModelRuleEntry modelRuleEntry = null;
	private boolean bRuleAnalysisFailed = false;

	private boolean bViewSingleRow = false;
	boolean bShowMoleculeColor = false;
	boolean bShowNonTrivialOnly = false;

	public boolean isViewSingleRow() {
		return bViewSingleRow;
	}
	public void setViewSingleRow(boolean bViewSingleRow) {
		this.bViewSingleRow = bViewSingleRow;
	}
	public void setShowMoleculeColor(boolean bShowMoleculeColor) {
		this.bShowMoleculeColor = bShowMoleculeColor;
	}
	public boolean isShowMoleculeColor() {
		return bShowMoleculeColor;
	}

	public void setShowNonTrivialOnly(boolean bShowNonTrivialOnly) {
		this.bShowNonTrivialOnly = bShowNonTrivialOnly;
	}
	public boolean isShowNonTrivialOnly() {
		return bShowNonTrivialOnly;
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
		if (isShowDifferencesOnly()){
			refreshRuleAnalysis();
		}
		super.repaint();
	}
	
	public void setReactionRule(ReactionRule reactionRule) {
		this.reactionRule = reactionRule;
		repaint();
	}
	
}
