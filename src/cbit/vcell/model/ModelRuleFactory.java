package cbit.vcell.model;

import java.util.ArrayList;
import java.util.List;

import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.RuleAnalysis;
import org.vcell.model.rbm.RuleAnalysis.MolecularComponentEntry;
import org.vcell.model.rbm.RuleAnalysis.MolecularTypeEntry;
import org.vcell.model.rbm.RuleAnalysis.ParticipantEntry;
import org.vcell.model.rbm.RuleAnalysis.ParticipantType;
import org.vcell.model.rbm.RuleAnalysis.ProductBondEntry;
import org.vcell.model.rbm.RuleAnalysis.ReactantBondEntry;
import org.vcell.model.rbm.RuleAnalysis.RuleEntry;
import org.vcell.model.rbm.SpeciesPattern.Bond;

import cbit.vcell.model.RbmKineticLaw.RateLawType;
import cbit.vcell.model.RbmKineticLaw.RbmKineticLawParameterType;

public class ModelRuleFactory {
	
	public class ModelRuleEntry implements RuleEntry {
		private final ReactionRule reactionRule;
		private final int ruleIndex;
		
		private final ArrayList<ParticipantEntry> reactantEntries = new ArrayList<ParticipantEntry>();
		private final ArrayList<ParticipantEntry> productEntries = new ArrayList<ParticipantEntry>();
		private final ArrayList<MolecularTypeEntry> reactantMolecularTypeEntries = new ArrayList<MolecularTypeEntry>();
		private final ArrayList<MolecularComponentEntry> reactantMolecularComponentEntries = new ArrayList<MolecularComponentEntry>();
		private final ArrayList<MolecularTypeEntry> productMolecularTypeEntries = new ArrayList<MolecularTypeEntry>();
		private final ArrayList<MolecularComponentEntry> productMolecularComponentEntries = new ArrayList<MolecularComponentEntry>();
		private final ArrayList<ReactantBondEntry> reactantBondEntries = new ArrayList<ReactantBondEntry>();
		private final ArrayList<ProductBondEntry> productBondEntries = new ArrayList<ProductBondEntry>();
		
		public ModelRuleEntry(ReactionRule reactionRule, int ruleIndex){
			this.reactionRule = reactionRule;
			this.ruleIndex = ruleIndex;
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
		public List<? extends ParticipantEntry> getReactantEntries() {
			return reactantEntries;
		}
		@Override
		public List<? extends MolecularTypeEntry> getReactantMolecularTypeEntries() {
			return reactantMolecularTypeEntries;
		}
		@Override
		public List<? extends MolecularComponentEntry> getReactantMolecularComponentEntries() {
			return reactantMolecularComponentEntries;
		}
		@Override
		public List<ReactantBondEntry> getReactantBondEntries() {
			return reactantBondEntries;
		}
		@Override
		public List<? extends ParticipantEntry> getProductEntries() {
			return productEntries;
		}
		@Override
		public List<? extends MolecularTypeEntry> getProductMolecularTypeEntries() {
			return productMolecularTypeEntries;
		}
		@Override
		public List<? extends MolecularComponentEntry> getProductMolecularComponentEntries() {
			return productMolecularComponentEntries;
		}
		@Override
		public List<ProductBondEntry> getProductBondEntries() {
			return productBondEntries;
		}

//		@Override
//		public String getForwardRateConstantName() {
//			if (reactionRule.getKineticLaw().getRateLawType() == RateLawType.MassAction){
//				return reactionRule.getKineticLaw().getLocalParameter(RbmKineticLawParameterType.MassActionForwardRate).getExpression().infix();
//			}else{
//				throw new RuntimeException("ModelRuleFactory.ModelRuleEntry.getForwardRateConstantName() is only supported for mass action kinetics");
//			}
//		}

	}
	
	public class ModelParticipantEntry implements ParticipantEntry {
		private final ArrayList<ModelMolecularTypeEntry> molecularTypeEntries = new ArrayList<ModelMolecularTypeEntry>();
		private final ReactionRuleParticipant participantPattern; 
		private final ModelRuleEntry modelRule;
		private final int participantIndex;
		
		public ModelParticipantEntry(ModelRuleEntry modelRule, int participantIndex, ReactionRuleParticipant participantPattern) {
			this.modelRule = modelRule;
			this.participantIndex = participantIndex;
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
		public RuleEntry getRule() {
			return modelRule;
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
			if (modelParticipantEntry.getParticipantType() != ParticipantType.Reactant || ((ModelMolecularTypeEntry)productMTE).modelParticipantEntry.getParticipantType() != ParticipantType.Product){
				throw new RuntimeException("expecting reactantPattern.matches(productPattern)");
			}
			if (molecularTypePattern.getMolecularType() != ((ModelMolecularTypeEntry)productMTE).molecularTypePattern.getMolecularType()){
				return false;
			}
			if (!molecularTypePattern.getParticipantMatchLabel().equals(((ModelMolecularTypeEntry)productMTE).molecularTypePattern.getParticipantMatchLabel())){
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

		@Override
		public String toBngl() {
			return RbmUtils.toBnglString(this.molecularTypePattern);
		}

		@Override
		public String getMatchLabel() {
			if (molecularTypePattern.hasExplicitParticipantMatch()){
				return molecularTypePattern.getParticipantMatchLabel();
			}else{
				return null;
			}
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
			System.out.print("MODEL checking if "+RuleAnalysis.getID(this)+" is bound to "+RuleAnalysis.getID(productComponent)+" ");
			if (this == productComponent){
				System.out.println("FALSE");
				return false;
			}
			if (molecularComponentPattern.getBond() == null){
				System.out.println("FALSE");
				return false;
			}
			Bond bond = molecularComponentPattern.getBond();
			if (bond.molecularComponentPattern != ((ModelMolecularComponentEntry)productComponent).molecularComponentPattern){
				System.out.println("FALSE");
				return false;
			}
			System.out.println("TRUE");
			return true;
		}

		@Override
		public boolean hasBond() {
			Bond bond = molecularComponentPattern.getBond();
			return bond != null;
		}
		
	}
		
	public ModelRuleEntry createRuleEntry(ReactionRule reactionRule, int reactionRuleIndex) {

		ModelRuleEntry rule = new ModelRuleEntry(reactionRule, reactionRuleIndex);
		
		//
		// for each molecular type in reactant, find all possible matches in products (without regard for consistency).
		//
		int participantIndex = 0;
		for (ReactantPattern rp : reactionRule.getReactantPatterns()){
			ModelParticipantEntry reactantEntry = new ModelParticipantEntry(rule,participantIndex,rp);
			rule.reactantEntries.add(reactantEntry);
			int moleculeIndex = 0;
			for (MolecularTypePattern mtp : rp.getSpeciesPattern().getMolecularTypePatterns()){
				ModelMolecularTypeEntry molecularTypeEntry = new ModelMolecularTypeEntry(reactantEntry, moleculeIndex, mtp);
				rule.reactantMolecularTypeEntries.add(molecularTypeEntry);
				int componentIndex = 0;
				for (MolecularComponentPattern mcp : mtp.getComponentPatternList()){
					ModelMolecularComponentEntry mce = new ModelMolecularComponentEntry(molecularTypeEntry, componentIndex, mcp);
					rule.reactantMolecularComponentEntries.add(mce);
					
					//
					// if this reactant component has a bond, find partner already in list (will always find second binding site where first one is in the list already).
					//
					Bond bond = mcp.getBond();
					if (bond != null){
						ReactantBondEntry bondEntry = null;
						for (MolecularComponentEntry otherMCE : rule.reactantMolecularComponentEntries){
							if (((ModelMolecularComponentEntry)otherMCE).molecularComponentPattern == bond.molecularComponentPattern){
								bondEntry = new ReactantBondEntry(otherMCE, mce);
								break;
							}
						}
						if (bondEntry!=null){
							rule.reactantBondEntries.add(bondEntry);
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
			ModelParticipantEntry productEntry = new ModelParticipantEntry(rule,participantIndex,pp);
			rule.productEntries.add(productEntry);
			int moleculeIndex = 0;
			for (MolecularTypePattern mtp : pp.getSpeciesPattern().getMolecularTypePatterns()){
				ModelMolecularTypeEntry molecularTypeEntry = new ModelMolecularTypeEntry(productEntry, moleculeIndex, mtp);
				rule.productMolecularTypeEntries.add(molecularTypeEntry);
				int componentIndex = 0;
				for (MolecularComponentPattern mcp : mtp.getComponentPatternList()){
					ModelMolecularComponentEntry mce = new ModelMolecularComponentEntry(molecularTypeEntry, componentIndex, mcp);
					rule.productMolecularComponentEntries.add(mce);

					//
					// if this product component has a bond, find partner already in list (will always find second binding site where first one is in the list already).
					//
					Bond bond = mcp.getBond();
					if (bond != null){
						ProductBondEntry bondEntry = null;
						for (MolecularComponentEntry otherMCE : rule.productMolecularComponentEntries){
							if (((ModelMolecularComponentEntry)otherMCE).molecularComponentPattern == bond.molecularComponentPattern){
								bondEntry = new ProductBondEntry(otherMCE, mce);
								break;
							}
						}
						if (bondEntry!=null){
							rule.productBondEntries.add(bondEntry);
						}
					}
					componentIndex++;
				}
				moleculeIndex++;
			}
			participantIndex++;
		}
		
		return rule;
	}

}
