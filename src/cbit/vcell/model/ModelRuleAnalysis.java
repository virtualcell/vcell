package cbit.vcell.model;

import java.util.ArrayList;
import java.util.List;

import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.RuleAnalysis;
import org.vcell.model.rbm.SpeciesPattern.Bond;

public class ModelRuleAnalysis extends RuleAnalysis {
	
	public class ModelParticipantEntry implements ParticipantEntry {
		private final ReactionRule reactionRule;
		private final ArrayList<ModelMolecularTypeEntry> molecularTypeEntries = new ArrayList<ModelMolecularTypeEntry>();
		private final ReactionRuleParticipant participantPattern; 
		private final int ruleIndex;
		private final int participantIndex;
		
		public ModelParticipantEntry(int ruleIndex, int participantIndex, ReactionRule reactionRule, ReactionRuleParticipant participantPattern) {
			this.ruleIndex = ruleIndex;
			this.participantIndex = participantIndex;
			this.reactionRule = reactionRule;
			this.participantPattern = participantPattern;
		}

		public void addMolecularTypeEntry(ModelMolecularTypeEntry molecularTypeEntry){
			this.molecularTypeEntries.add(molecularTypeEntry);
		}

		@Override
		public List<? extends MolecularTypeEntry> getMolecularTypeEntries() {
			return molecularTypeEntries;
		}

		@Override
		public String getRuleName() {
			return reactionRule.getName();
		}

		@Override
		public int getRuleIndex() {
			return ruleIndex;
		}

		@Override
		public int getParticipantIndex() {
			return participantIndex;
		}

		@Override
		public ParticipantType getParticipantType() {
			if (participantPattern instanceof ReactantPattern){
				return ParticipantType.Reactant;
			}else{
				return ParticipantType.Product;
			}
		}
		
	}
	

	public class ModelMolecularTypeEntry implements MolecularTypeEntry {
		private final ModelParticipantEntry modelParticipantEntry;
		private final MolecularTypePattern molecularTypePattern;
		private final int moleculeIndex;
		private final ArrayList<ModelMolecularComponentEntry> molecularComponentEntryList = new ArrayList<ModelMolecularComponentEntry>();
		
		public ModelMolecularTypeEntry(ModelParticipantEntry modelParticipantEntry, int moleculeIndex, MolecularTypePattern molecularTypePattern){
			this.modelParticipantEntry = modelParticipantEntry;
			this.moleculeIndex = moleculeIndex;
			this.molecularTypePattern = molecularTypePattern;
			modelParticipantEntry.addMolecularTypeEntry(this);
		}
			
		@Override
		public String getMolecularTypeName(){
			return molecularTypePattern.getMolecularType().getName();
		}
		
		@Override
		public boolean matches(MolecularTypeEntry productMTE) {
			if (modelParticipantEntry.getParticipantType() != ParticipantType.Product){
				throw new RuntimeException("expecting reactantPatter.matches(productPattern)");
			}
			if (molecularTypePattern.getMolecularType() != ((ModelMolecularTypeEntry)productMTE).molecularTypePattern.getMolecularType()){
				return false;
			}
			if (!molecularTypePattern.getParticipantMatchLabel().equals(productMTE)){
				return false;
			}
			return true;
		}
		
		public void add(ModelMolecularComponentEntry molecularComponentEntry){
			if (molecularComponentEntryList.contains(molecularComponentEntry)){
				throw new RuntimeException("molecular component already in list");
			}
			molecularComponentEntryList.add(molecularComponentEntry);
		}

		@Override
		public List<? extends MolecularComponentEntry> getMolecularComponentEntries() {
			return molecularComponentEntryList;
		}

		@Override
		public ParticipantEntry getParticipantEntry() {
			return modelParticipantEntry;
		}

		@Override
		public int getMoleculeIndex() {
			return moleculeIndex;
		}
	}
	
	public class ModelMolecularComponentEntry implements MolecularComponentEntry {
		private final ModelMolecularTypeEntry molecularTypeEntry;
		private final MolecularComponentPattern molecularComponentPattern;
		private final int componentIndex;
		
		
		private ModelMolecularComponentEntry(ModelMolecularTypeEntry molecularTypeEntry, int componentIndex, MolecularComponentPattern molecularComponentPattern) {
			this.molecularTypeEntry = molecularTypeEntry;
			this.componentIndex = componentIndex;
			this.molecularComponentPattern = molecularComponentPattern;
			molecularTypeEntry.add(this);
		}
		
		@Override
		public int getComponentIndex(){
			return this.componentIndex;
		}
		
		@Override
		public MolecularTypeEntry getMolecularTypeEntry() {
			return molecularTypeEntry;
		}

		@Override
		public String getMolecularComponentName() {
			return molecularComponentPattern.getMolecularComponent().getName();
		}

		@Override
		public String getExplicitState() {
			if (molecularComponentPattern.getComponentStatePattern() == null || molecularComponentPattern.getComponentStatePattern().getComponentStateDefinition() == null){
				return null;
			}
			return molecularComponentPattern.getComponentStatePattern().getComponentStateDefinition().getName();
		}

		@Override
		public boolean isBoundTo(MolecularComponentEntry productComponent) {
			if (molecularComponentPattern.getBond() == null){
				return false;
			}
			Bond bond = molecularComponentPattern.getBond();
			if (bond.molecularComponentPattern == ((ModelMolecularComponentEntry)productComponent).molecularComponentPattern){
				return true;
			}
			return false;
		}		
	}
		
	private final ReactionRule reactionRule;
	private final int reactionRuleIndex;

	public ModelRuleAnalysis(int reactionRuleIndex, ReactionRule reactionRule) {
		this.reactionRuleIndex = reactionRuleIndex;
		this.reactionRule = reactionRule;
	}

	public ReactionRule getReactionRule(){
		return reactionRule;
	}
	
	@Override
	protected void populateRuleInfo() {
		reactantEntries.clear();
		productEntries.clear();
		reactantMolecularTypeEntries.clear();
		reactantMolecularComponentEntries.clear();
		productMolecularTypeEntries.clear();
		productMolecularComponentEntries.clear();
		reactantBondEntries.clear();
		productBondEntries.clear();
		
		//
		// for each molecular type in reactant, find all possible matches in products (without regard for consistency).
		//

		int participantIndex = 0;
		for (ReactantPattern rp : reactionRule.getReactantPatterns()){
			ModelParticipantEntry reactantEntry = new ModelParticipantEntry(reactionRuleIndex,participantIndex,reactionRule,rp);
			reactantEntries.add(reactantEntry);
			int moleculeIndex = 0;
			for (MolecularTypePattern mtp : rp.getSpeciesPattern().getMolecularTypePatterns()){
				ModelMolecularTypeEntry molecularTypeEntry = new ModelMolecularTypeEntry(reactantEntry, moleculeIndex, mtp);
				reactantMolecularTypeEntries.add(molecularTypeEntry);
				int componentIndex = 0;
				for (MolecularComponentPattern mcp : mtp.getComponentPatternList()){
					ModelMolecularComponentEntry mce = new ModelMolecularComponentEntry(molecularTypeEntry, componentIndex, mcp);
					reactantMolecularComponentEntries.add(mce);
					
					//
					// if this reactant component has a bond, find partner already in list (will always find second binding site where first one is in the list already).
					//
					Bond bond = mcp.getBond();
					if (bond != null){
						BondEntry bondEntry = null;
						for (MolecularComponentEntry otherMCE : reactantMolecularComponentEntries){
							if (((ModelMolecularComponentEntry)otherMCE).molecularComponentPattern == bond.molecularComponentPattern){
								bondEntry = new BondEntry(otherMCE, mce);
								break;
							}
						}
						if (bondEntry!=null){
							reactantBondEntries.add(bondEntry);
						}
					}
					componentIndex++;
				}
				moleculeIndex++;
			}
			participantIndex++;
		}
		
		participantIndex = 0;
		for (ProductPattern pp : reactionRule.getProductPatterns()){
			ModelParticipantEntry productEntry = new ModelParticipantEntry(reactionRuleIndex,participantIndex,reactionRule,pp);
			productEntries.add(productEntry);
			int moleculeIndex = 0;
			for (MolecularTypePattern mtp : pp.getSpeciesPattern().getMolecularTypePatterns()){
				ModelMolecularTypeEntry molecularTypeEntry = new ModelMolecularTypeEntry(productEntry, moleculeIndex, mtp);
				productMolecularTypeEntries.add(molecularTypeEntry);
				int componentIndex = 0;
				for (MolecularComponentPattern mcp : mtp.getComponentPatternList()){
					ModelMolecularComponentEntry mce = new ModelMolecularComponentEntry(molecularTypeEntry, componentIndex, mcp);
					productMolecularComponentEntries.add(mce);

					//
					// if this reactant component has a bond, find partner already in list (will always find second binding site where first one is in the list already).
					//
					Bond bond = mcp.getBond();
					if (bond != null){
						BondEntry bondEntry = null;
						for (MolecularComponentEntry otherMCE : productMolecularComponentEntries){
							if (((ModelMolecularComponentEntry)otherMCE).molecularComponentPattern == bond.molecularComponentPattern){
								bondEntry = new BondEntry(otherMCE, mce);
								break;
							}
						}
						if (bondEntry!=null){
							productBondEntries.add(bondEntry);
						}
					}
					componentIndex++;
				}
				moleculeIndex++;
			}
			participantIndex++;
		}
	}

}
