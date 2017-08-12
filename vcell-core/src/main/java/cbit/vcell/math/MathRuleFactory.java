package cbit.vcell.math;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.RuleAnalysis;
import org.vcell.model.rbm.RuleAnalysis.MolecularComponentEntry;
import org.vcell.model.rbm.RuleAnalysis.MolecularTypeEntry;
import org.vcell.model.rbm.RuleAnalysis.ParticipantEntry;
import org.vcell.model.rbm.RuleAnalysis.ParticipantType;
import org.vcell.model.rbm.RuleAnalysis.ProductBondEntry;
import org.vcell.model.rbm.RuleAnalysis.ReactantBondEntry;
import org.vcell.model.rbm.RuleAnalysis.RuleEntry;

import cbit.vcell.math.ParticleJumpProcess.ProcessSymmetryFactor;
import cbit.vcell.math.ParticleMolecularComponentPattern.ParticleBondType;

public class MathRuleFactory {
	
	public class MathRuleEntry implements RuleEntry {
		private final ParticleJumpProcess particleJumpProcess;
		private final int ruleIndex;
		
		private final ArrayList<MathParticipantEntry> reactantEntries = new ArrayList<MathParticipantEntry>();
		private final ArrayList<MathParticipantEntry> productEntries = new ArrayList<MathParticipantEntry>();
		private final ArrayList<MathMolecularTypeEntry> reactantMolecularTypeEntries = new ArrayList<MathMolecularTypeEntry>();
		private final ArrayList<MathMolecularComponentEntry> reactantMolecularComponentEntries = new ArrayList<MathMolecularComponentEntry>();
		private final ArrayList<MathMolecularTypeEntry> productMolecularTypeEntries = new ArrayList<MathMolecularTypeEntry>();
		private final ArrayList<MathMolecularComponentEntry> productMolecularComponentEntries = new ArrayList<MathMolecularComponentEntry>();
		private final ArrayList<ReactantBondEntry> reactantBondEntries = new ArrayList<ReactantBondEntry>();
		private final ArrayList<ProductBondEntry> productBondEntries = new ArrayList<ProductBondEntry>();
		
		public MathRuleEntry(ParticleJumpProcess particleJumpProcess, int ruleIndex){
			this.particleJumpProcess = particleJumpProcess;
			this.ruleIndex = ruleIndex;
		}
		
		@Override
		public String getRuleName() {
			return particleJumpProcess.getName();
		}
		@Override
		public int getRuleIndex() {
			return ruleIndex;
		}
		@Override
		public List<MathParticipantEntry> getReactantEntries() {
			return reactantEntries;
		}
		@Override
		public List<MathMolecularTypeEntry> getReactantMolecularTypeEntries() {
			return reactantMolecularTypeEntries;
		}
		@Override
		public List<MathMolecularComponentEntry> getReactantMolecularComponentEntries() {
			return reactantMolecularComponentEntries;
		}
		@Override
		public List<ReactantBondEntry> getReactantBondEntries() {
			return reactantBondEntries;
		}
		@Override
		public List<MathParticipantEntry> getProductEntries() {
			return productEntries;
		}
		@Override
		public List<MathMolecularTypeEntry> getProductMolecularTypeEntries() {
			return productMolecularTypeEntries;
		}
		@Override
		public List<MathMolecularComponentEntry> getProductMolecularComponentEntries() {
			return productMolecularComponentEntries;
		}
		@Override
		public List<ProductBondEntry> getProductBondEntries() {
			return productBondEntries;
		}

		@Override
		public String getReactionBNGLShort() {
			ArrayList<ParticleSpeciesPattern> reactantPatterns = new ArrayList<ParticleSpeciesPattern>();
			for (MathParticipantEntry reactant : getReactantEntries()){
				reactantPatterns.add(reactant.particleSpeciesPattern);
			}
			ArrayList<ParticleSpeciesPattern> productPatterns = new ArrayList<ParticleSpeciesPattern>();
			for (MathParticipantEntry product : getProductEntries()){
				productPatterns.add(product.particleSpeciesPattern);
			}
			return RbmUtils.toBnglStringShort(particleJumpProcess, reactantPatterns, productPatterns);
		}

		@Override
		public Double getSymmetryFactor() {
			ProcessSymmetryFactor processSymmetryFactor = particleJumpProcess.getProcessSymmetryFactor();
			return processSymmetryFactor.getFactor();
		}

//		@Override
//		public String getForwardRateConstantName() {
//			JumpProcessRateDefinition rateDefinition = particleJumpProcess.getParticleRateDefinition();
//			if (rateDefinition instanceof MacroscopicRateConstant){
//				return ((MacroscopicRateConstant)rateDefinition).getExpression().infix();
//			}else{
//				throw new RuntimeException("MathRuleFactory.MathRuleEntry.getForwardRateConstantName() only supported for MacroscopicRateConstant kinetic rate");
//			}
//		}

	}
	
	public class MathParticipantEntry implements ParticipantEntry {
		private final ArrayList<MathMolecularTypeEntry> molecularTypeEntries = new ArrayList<MathMolecularTypeEntry>();
		private final ParticleSpeciesPattern particleSpeciesPattern; 
		private final ParticipantType participantType;
		private final MathRuleEntry rule;
		private final int participantIndex;
		
		public MathParticipantEntry(MathRuleEntry rule, int participantIndex, ParticleSpeciesPattern particleSpeciesPattern, ParticipantType participantType) {
			this.rule = rule;
			this.participantIndex = participantIndex;
			this.particleSpeciesPattern = particleSpeciesPattern;
			this.participantType = participantType;
		}

		public void addMolecularTypeEntry(MathMolecularTypeEntry molecularTypeEntry){
			this.molecularTypeEntries.add(molecularTypeEntry);
		}

		@Override
		public List<MathMolecularTypeEntry> getMolecularTypeEntries() {
			return molecularTypeEntries;
		}

		@Override
		public MathRuleEntry getRule() {
			return rule;
		}

		@Override
		public int getParticipantIndex() {
			return participantIndex;
		}

		@Override
		public ParticipantType getParticipantType() {
			return participantType;
		}
		
		public ParticleSpeciesPattern getParticleSpeciesPattern() {
			return particleSpeciesPattern;
		}
	}
	

	public class MathMolecularTypeEntry implements MolecularTypeEntry {
		private final MathParticipantEntry participantEntry;
		private final ParticleMolecularTypePattern particleMolecularTypePattern;
		private final int moleculeIndex;
		private final ArrayList<MathMolecularComponentEntry> molecularComponentEntryList = new ArrayList<MathMolecularComponentEntry>();
		
		public MathMolecularTypeEntry(MathParticipantEntry participantEntry, int moleculeIndex, ParticleMolecularTypePattern particleMolecularTypePattern){
			this.participantEntry = participantEntry;
			this.moleculeIndex = moleculeIndex;
			this.particleMolecularTypePattern = particleMolecularTypePattern;
			participantEntry.addMolecularTypeEntry(this);
		}
			
		@Override
		public String getMolecularTypeName(){
			return particleMolecularTypePattern.getMolecularType().getName();
		}
		
		@Override
		public boolean matches(MolecularTypeEntry productMTE) {
			if (participantEntry.getParticipantType() != ParticipantType.Reactant || ((MathMolecularTypeEntry)productMTE).participantEntry.getParticipantType() != ParticipantType.Product){
				throw new RuntimeException("expecting reactantPatter.matches(productPattern)");
			}
			if (particleMolecularTypePattern.getMolecularType() != ((MathMolecularTypeEntry)productMTE).particleMolecularTypePattern.getMolecularType()){
				return false;
			}
			if (!particleMolecularTypePattern.getMatchLabel().equals(((MathMolecularTypeEntry)productMTE).particleMolecularTypePattern.getMatchLabel())){
				return false;
			}
			return true;
		}
		
		public void add(MathMolecularComponentEntry molecularComponentEntry){
			if (molecularComponentEntryList.contains(molecularComponentEntry)){
				throw new RuntimeException("molecular component already in list");
			}
			molecularComponentEntryList.add(molecularComponentEntry);
		}

		@Override
		public List<MathMolecularComponentEntry> getMolecularComponentEntries() {
			return molecularComponentEntryList;
		}

		@Override
		public ParticipantEntry getParticipantEntry() {
			return participantEntry;
		}

		@Override
		public int getMoleculeIndex() {
			return moleculeIndex;
		}
		
		public ParticleMolecularTypePattern getParticleMolecularTypePattern(){
			return this.particleMolecularTypePattern;
		}

		@Override
		public String toBngl() {
			return "nobngl";
		}

		@Override
		public String getMatchLabel() {
			if (particleMolecularTypePattern.hasExplicitParticipantMatch()){
				return particleMolecularTypePattern.getMatchLabel();
			}else{
				return null;
			}
		}

		@Override
		public String getMolecularTypeBNGL() {
			return RbmUtils.toBnglString(particleMolecularTypePattern.getMolecularType());
		}
	}
	
	public class MathMolecularComponentEntry implements MolecularComponentEntry {
		private final MathMolecularTypeEntry molecularTypeEntry;
		private final ParticleMolecularComponentPattern particleMolecularComponentPattern;
		private final int componentIndex;
		
		
		private MathMolecularComponentEntry(MathMolecularTypeEntry molecularTypeEntry, int componentIndex, ParticleMolecularComponentPattern particleMolecularComponentPattern) {
			this.molecularTypeEntry = molecularTypeEntry;
			this.componentIndex = componentIndex;
			this.particleMolecularComponentPattern = particleMolecularComponentPattern;
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
			return particleMolecularComponentPattern.getMolecularComponent().getName();
		}

		@Override
		public String getExplicitState() {
			if (particleMolecularComponentPattern.getComponentStatePattern() == null || particleMolecularComponentPattern.getComponentStatePattern().getParticleComponentStateDefinition() == null){
				return null;
			}
			return particleMolecularComponentPattern.getComponentStatePattern().getParticleComponentStateDefinition().getName();
		}

		@Override
		public boolean isBoundTo(MolecularComponentEntry productComponent) {
			System.out.print("MATH checking if "+RuleAnalysis.getID(this)+" is bound to "+RuleAnalysis.getID(productComponent)+" ");
			if (this == productComponent){
				System.out.println("FALSE");
				return false;
			}
			if (particleMolecularComponentPattern.getBondType() != ParticleBondType.Specified){
				System.out.println("FALSE");
				return false; // this one not bound
			}
			if (((MathMolecularComponentEntry)productComponent).particleMolecularComponentPattern.getBondType() != ParticleBondType.Specified){
				System.out.println("FALSE");
				return false; // other one not bound
			}
			int bondId = particleMolecularComponentPattern.getBondId();
			int otherBondId = ((MathMolecularComponentEntry)productComponent).particleMolecularComponentPattern.getBondId();
			if (bondId != otherBondId){
				System.out.println("FALSE");
				return false; // has to have same BondID
			}
			if (getMolecularTypeEntry().getParticipantEntry() != productComponent.getMolecularTypeEntry().getParticipantEntry()){
				System.out.println("FALSE");
				return false; // has to be from the same participant (reactant or product)
			}
			System.out.println("TRUE");
			return true;
		}

		public ParticleMolecularComponentPattern getParticleMolecularComponentPattern() {
			return this.particleMolecularComponentPattern;
		}

		@Override
		public boolean hasBond() {
			return particleMolecularComponentPattern.getBondType() == ParticleBondType.Specified;
		}
		@Override
		public boolean isBondPossible() {
			if(particleMolecularComponentPattern.getBondType() == ParticleBondType.Possible) {
				return true;
			} else {
				return false;
			}
		}		
		@Override
		public boolean isBondExists() {
			if(particleMolecularComponentPattern.getBondType() == ParticleBondType.Exists) {
				return true;
			} else {
				return false;
			}
		}		
	}
		
	public MathRuleEntry createRuleEntry(ParticleJumpProcess particleJumpProcess, int particleJumpProcessIndex) {

		MathRuleEntry rule = new MathRuleEntry(particleJumpProcess, particleJumpProcessIndex);
				
		ArrayList<ParticleSpeciesPattern> selectedPatterns = new ArrayList<ParticleSpeciesPattern>();
		for (ParticleVariable particleVariable : particleJumpProcess.getParticleVariables()){
			if (!(particleVariable instanceof ParticleSpeciesPattern)){
				throw new RuntimeException("expecting only "+ParticleSpeciesPattern.class.getSimpleName()+"s for "+ParticleJumpProcess.class.getSimpleName()+" "+particleJumpProcess.getName());
			}
			selectedPatterns.add((ParticleSpeciesPattern) particleVariable);
		}
		ArrayList<ParticleSpeciesPattern> createdPatterns = new ArrayList<ParticleSpeciesPattern>();
		HashSet<ParticleSpeciesPattern> destroyedPatterns = new HashSet<ParticleSpeciesPattern>();
		for (Action action : particleJumpProcess.getActions()){
			if (!(action.getVar() instanceof ParticleSpeciesPattern)){
				throw new RuntimeException("expecting only "+ParticleSpeciesPattern.class.getSimpleName()+"s for "+ParticleJumpProcess.class.getSimpleName()+" "+particleJumpProcess.getName());
			}
			if (action.getOperation().equals(Action.ACTION_CREATE)){
				createdPatterns.add((VolumeParticleSpeciesPattern) action.getVar());
			}else if (action.getOperation().equals(Action.ACTION_DESTROY)){
				destroyedPatterns.add((VolumeParticleSpeciesPattern) action.getVar());
			}else{
				throw new RuntimeException("unexpected action operation "+action.getOperation()+" for jump process "+particleJumpProcess.getName());
			}
		}
			
		ArrayList<ParticleSpeciesPattern> productSpeciesPatterns = new ArrayList<ParticleSpeciesPattern>(selectedPatterns);
		productSpeciesPatterns.removeAll(destroyedPatterns);
		productSpeciesPatterns.addAll(createdPatterns);

		//
		// for each molecular type in reactant, find all possible matches in products (without regard for consistency).
		//
		int participantIndex = 0;
		for (ParticleSpeciesPattern particleSpeciesPattern : selectedPatterns){
			MathParticipantEntry reactantEntry = new MathParticipantEntry(rule,participantIndex,particleSpeciesPattern,ParticipantType.Reactant);
			rule.reactantEntries.add(reactantEntry);
			int moleculeIndex = 0;
			for (ParticleMolecularTypePattern mtp : particleSpeciesPattern.getParticleMolecularTypePatterns()){
				MathMolecularTypeEntry molecularTypeEntry = new MathMolecularTypeEntry(reactantEntry, moleculeIndex, mtp);
				rule.reactantMolecularTypeEntries.add(molecularTypeEntry);
				int componentIndex = 0;
				for (ParticleMolecularComponentPattern mcp : mtp.getMolecularComponentPatternList()){
					// TODO: divergence point vs. ModelRuleFactory
					// here we skip the trivial components
					if(mcp.getBondType() == ParticleBondType.Possible && (mcp.getComponentStatePattern() == null || mcp.getComponentStatePattern().isAny())) {
						continue;
					}
					MathMolecularComponentEntry mce = new MathMolecularComponentEntry(molecularTypeEntry, componentIndex, mcp);
					rule.reactantMolecularComponentEntries.add(mce);
					
					//
					// if this reactant component has a bond, find partner already in list (will always find second binding site where first one is in the list already).
					//
					if (mcp.getBondType() == ParticleBondType.Specified){
						ReactantBondEntry bondEntry = null;
						for (MolecularComponentEntry otherMCE : rule.reactantMolecularComponentEntries){
							if (mce.isBoundTo(otherMCE)){
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
		for (ParticleSpeciesPattern particleSpeciesPattern : productSpeciesPatterns){
			MathParticipantEntry productEntry = new MathParticipantEntry(rule,participantIndex,particleSpeciesPattern,ParticipantType.Product);
			rule.productEntries.add(productEntry);
			int moleculeIndex = 0;
			for (ParticleMolecularTypePattern mtp : particleSpeciesPattern.getParticleMolecularTypePatterns()){
				MathMolecularTypeEntry molecularTypeEntry = new MathMolecularTypeEntry(productEntry, moleculeIndex, mtp);
				rule.productMolecularTypeEntries.add(molecularTypeEntry);
				int componentIndex = 0;
				for (ParticleMolecularComponentPattern mcp : mtp.getMolecularComponentPatternList()){
					// TODO: divergence point vs. ModelRuleFactory
					// here we skip the trivial components
					if(mcp.getBondType() == ParticleBondType.Possible && (mcp.getComponentStatePattern() == null || mcp.getComponentStatePattern().isAny())) {
						continue;
					}
					MathMolecularComponentEntry mce = new MathMolecularComponentEntry(molecularTypeEntry, componentIndex, mcp);
					rule.productMolecularComponentEntries.add(mce);

					//
					// if this product component has a bond, find partner already in list (will always find second binding site where first one is in the list already).
					//
					if (mcp.getBondType() == ParticleBondType.Specified){
						ProductBondEntry bondEntry = null;
						for (MolecularComponentEntry otherMCE : rule.productMolecularComponentEntries){
							if (mce.isBoundTo(otherMCE)){
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
