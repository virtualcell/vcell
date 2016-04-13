package cbit.vcell.model;

import java.util.ArrayList;
import java.util.List;

import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;
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

public class ModelRuleFactory {
	
	public class ModelRuleEntry implements RuleEntry {
		private final ReactionRule reactionRule;
		private final int ruleIndex;
		
		private final ArrayList<ModelParticipantEntry> reactantEntries = new ArrayList<ModelParticipantEntry>();
		private final ArrayList<ModelParticipantEntry> productEntries = new ArrayList<ModelParticipantEntry>();
		private final ArrayList<ModelMolecularTypeEntry> reactantMolecularTypeEntries = new ArrayList<ModelMolecularTypeEntry>();
		private final ArrayList<ModelMolecularComponentEntry> reactantMolecularComponentEntries = new ArrayList<ModelMolecularComponentEntry>();
		private final ArrayList<ModelMolecularTypeEntry> productMolecularTypeEntries = new ArrayList<ModelMolecularTypeEntry>();
		private final ArrayList<ModelMolecularComponentEntry> productMolecularComponentEntries = new ArrayList<ModelMolecularComponentEntry>();
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

		@Override
		public String getReactionBNGLShort() {
			return RbmUtils.toBnglStringShort(reactionRule, CompartmentMode.hide);
		}

		@Override
		public Double getSymmetryFactor() {
			System.err.println("symmetry factor is wrong");
			return new Double(1); // this is not yet correct, go ask BNG?
		}

		public MolecularComponentEntry findMolecularComponentEntry(MolecularComponentPattern mcp) {
			for (ModelMolecularComponentEntry entry : reactantMolecularComponentEntries){
				if (entry.molecularComponentPattern == mcp){
					return entry;
				}
			}
			for (ModelMolecularComponentEntry entry : productMolecularComponentEntries){
				if (entry.molecularComponentPattern == mcp){
					return entry;
				}
			}
			return null;
		}

		public MolecularTypeEntry findMolecularTypeEntry(MolecularTypePattern mcp) {
			for (ModelMolecularTypeEntry entry : reactantMolecularTypeEntries){
				if (entry.molecularTypePattern == mcp){
					return entry;
				}
			}
			for (ModelMolecularTypeEntry entry : productMolecularTypeEntries){
				if (entry.molecularTypePattern == mcp){
					return entry;
				}
			}
			return null;
		}

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
			return RbmUtils.toBnglString(this.molecularTypePattern, null, CompartmentMode.hide, false);	// in natural order, NOT sorted alphabetically
			// TODO: perhaps we should sort alphabetically here too??
		}

		@Override
		public String getMatchLabel() {
			if (molecularTypePattern.hasExplicitParticipantMatch()){
				return molecularTypePattern.getParticipantMatchLabel();
			}else{
				return null;
			}
		}

		@Override
		public String getMolecularTypeBNGL() {
			return RbmUtils.toBnglString(molecularTypePattern.getMolecularType(), null, CompartmentMode.hide);
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
		
		public MolecularComponentPattern getMolecularComponentPattern(){
			return this.molecularComponentPattern;
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

		@Override
		public boolean isBondPossible() {
			if(molecularComponentPattern.getBondType() == BondType.Possible) {
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		public boolean isBondExists() {
			if(molecularComponentPattern.getBondType() == BondType.Exists) {
				return true;
			} else {
				return false;
			}
		}
		
	}
	
	public enum ReactionRuleDirection {
		forward,
		reverse
	};
		
	public ModelRuleEntry createRuleEntry(ReactionRule reactionRule, int reactionRuleIndex, ReactionRuleDirection direction) {

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
					// TODO: divergence point vs. MathRuleFactory 
					// we need to look at all components when we compare reactants to products looking for changes
//					if(mcp.getBondType() == BondType.Possible && (mcp.getComponentStatePattern() == null || mcp.getComponentStatePattern().isAny())) {
//						continue;
//					}
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
					// TODO: divergence point vs. MathRuleFactory 
					// we need to look at all components when we compare reactants to products looking for changes
//					if(mcp.getBondType() == BondType.Possible && (mcp.getComponentStatePattern() == null || mcp.getComponentStatePattern().isAny())) {
//						continue;
//					}
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
