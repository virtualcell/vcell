package cbit.vcell.graph.gui;

import java.util.LinkedHashMap;
import java.util.Map;

import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RuleAnalysis;
import org.vcell.model.rbm.RuleAnalysis.MolecularComponentEntry;
import org.vcell.model.rbm.RuleAnalysis.MolecularTypeEntry;
import org.vcell.model.rbm.RuleAnalysisReport;

import cbit.vcell.graph.ReactionCartoon.RuleAnalysisChanged;
import cbit.vcell.graph.ShapeModeInterface;
import cbit.vcell.model.GroupingCriteria;
import cbit.vcell.model.ModelRuleFactory;
import cbit.vcell.model.ModelRuleFactory.ModelRuleEntry;
import cbit.vcell.model.ModelRuleFactory.ReactionRuleDirection;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.RuleParticipantSignature;

@SuppressWarnings("serial")
public class ParticipantSignatureShapePanel extends LargeShapePanel implements ShapeModeInterface {
	
	GroupingCriteria crit = GroupingCriteria.full;
	RuleParticipantSignature signature = null;
	
	public void setCriteria(GroupingCriteria crit) {
		this.crit = crit;
	}
	public GroupingCriteria getCriteria() {
		return this.crit;
	}
	public void setSignature(RuleParticipantSignature signature) {
		this.signature = signature;
	}
	public RuleParticipantSignature getSignature() {
		return this.signature;
	}
	
	// ================================================================================================================
	
	private Map<String, ReactionRule> reactionRuleMap = null;
	
	private Map<String, RuleAnalysisReport> reportMap = new LinkedHashMap<>();
	private Map<String, ModelRuleEntry> modelRuleEntryMap = new LinkedHashMap<>();
	private Map<String, Boolean> bRuleAnalysisFailedMap = new LinkedHashMap<>();
	
	private boolean bRuleAnalysisFailed = false;


	public void setRulesForSignature(Map<String, ReactionRule> reactionRuleMap) {
		this.reactionRuleMap = reactionRuleMap;
		refreshRuleAnalysis();
//		repaint();
	}
	
	@Override
	public void repaint() {
//		if (isShowDifferencesOnly()){
//			refreshRuleAnalysis();
//		}
		super.repaint();
	}

	private void refreshRuleAnalysis() {
		reportMap.clear();
		modelRuleEntryMap.clear();
		bRuleAnalysisFailedMap.clear();
		if (reactionRuleMap == null || reactionRuleMap.isEmpty()) {
			return;
		}
		
		for(Map.Entry<String, ReactionRule> entry : reactionRuleMap.entrySet()) {
			String key = entry.getKey();
			ReactionRule rr = entry.getValue();
			
			ModelRuleFactory factory = new ModelRuleFactory();
			ModelRuleEntry modelRuleEntry = factory.createRuleEntry(rr, 0, ReactionRuleDirection.forward);
			try {
				RuleAnalysisReport report = RuleAnalysis.analyze(modelRuleEntry, false);

				reportMap.put(key, report);
				modelRuleEntryMap.put(key, modelRuleEntry);
				bRuleAnalysisFailedMap.put(key, false);
				
			}catch (Exception e){
				e.printStackTrace();
				System.err.println("exception computing RuleAnalysis report for " + key + ": "+e.getMessage());
				bRuleAnalysisFailedMap.put(key, true);
				bRuleAnalysisFailed = true;
			}
		}
	}
	
	public RuleAnalysisChanged hasBondChanged(String key, MolecularComponentPattern molecularComponentPattern){
		
		ReactionRule reactionRule = reactionRuleMap.get(key);
		if (reactionRule == null) {
			return RuleAnalysisChanged.ANALYSISFAILED;
		}
		
		ModelRuleEntry modelRuleEntry = modelRuleEntryMap.get(key);
		RuleAnalysisReport report = reportMap.get(key);
		
		if (modelRuleEntry == null || report == null) {
			System.out.println("modelRuleEntry == null || report == null,  NOT GOOD!");
//			refreshRuleAnalysis();
		}
		if (!bRuleAnalysisFailed && report != null) {
			MolecularComponentEntry molecularComponentEntry = modelRuleEntry.findMolecularComponentEntry(molecularComponentPattern);
			if (report.hasBondChanged(molecularComponentEntry)){
				return RuleAnalysisChanged.CHANGED;
			} else {
				return RuleAnalysisChanged.UNCHANGED;
			}
		} else {
			return RuleAnalysisChanged.ANALYSISFAILED;
		}
	}
	
	public RuleAnalysisChanged hasStateChanged(String key, MolecularComponentPattern molecularComponentPattern){
		ReactionRule reactionRule = reactionRuleMap.get(key);
		if (reactionRule == null){
			return RuleAnalysisChanged.ANALYSISFAILED;
		}
		
		ModelRuleEntry modelRuleEntry = modelRuleEntryMap.get(key);
		RuleAnalysisReport report = reportMap.get(key);
		
		if (modelRuleEntry == null || report == null) {
			System.out.println("modelRuleEntry == null || report == null,  NOT GOOD!");
//			refreshRuleAnalysis();
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
	
	public RuleAnalysisChanged hasNoMatch(String key, MolecularTypePattern molecularTypePattern){
		ReactionRule reactionRule = reactionRuleMap.get(key);
		if (reactionRule == null){
			return RuleAnalysisChanged.ANALYSISFAILED;
		}
		
		ModelRuleEntry modelRuleEntry = modelRuleEntryMap.get(key);
		RuleAnalysisReport report = reportMap.get(key);
		
		if (modelRuleEntry == null || report == null) {
			System.out.println("modelRuleEntry == null || report == null,  NOT GOOD!");
//			refreshRuleAnalysis();
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
	@Override
	public DisplayMode getDisplayMode() {
		return DisplayMode.participantSignatures;
	}
	@Override
	public RuleAnalysisChanged hasStateChanged(MolecularComponentPattern molecularComponentPattern) {
		throw new RuntimeException("hasStateChanged() must specify which reaction rule, invoke other hasStateChanged()");
	}
	
	@Override
	public RuleAnalysisChanged hasBondChanged(MolecularComponentPattern molecularComponentPattern) {
		throw new RuntimeException("hasBondChanged() must specify which reaction rule, invoke other hasBondChanged()");
	}
	
	@Override
	public RuleAnalysisChanged hasNoMatch(MolecularTypePattern molecularTypePattern) {
		throw new RuntimeException("hasNoMatch() must specify which reaction rule, invoke other hasNoMatch()");
	}
	@Override
	public boolean isViewSingleRow() {
		return true;
	}
}
