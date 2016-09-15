package cbit.vcell.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.vcell.model.bngl.ParseException;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;

import cbit.vcell.graph.ModelCartoon;
import cbit.vcell.graph.ReactionCartoon;

public class RuleParticipantSignature {

	public enum Criteria {
		full,
		moleculeNumber
	}
	
	private Structure structure = null;
	private SpeciesPattern speciesPattern = null;
	private ModelCartoon modelCartoon = null;		// it's always a ReactionCartoon, actually
	
	private RuleParticipantSignature() {
		super();
	}
	
	public static RuleParticipantSignature fromReactionRuleParticipant(ReactionRuleParticipant rrParticipant, ModelCartoon modelCartoon){
		RuleParticipantSignature ruleParticipantSignature = new RuleParticipantSignature();
		ruleParticipantSignature.structure = rrParticipant.getStructure();
		ruleParticipantSignature.speciesPattern = rrParticipant.getSpeciesPattern();
		ruleParticipantSignature.modelCartoon = modelCartoon;
		return ruleParticipantSignature;
	}
	
	public boolean compareByCriteria(SpeciesPattern speciesPattern, Criteria criteria) {

		ArrayList<String> theirMolecularTypeNames = getListOfMolecularTypePatternSignatures(speciesPattern, criteria);
		String theirMolecularSignature = getMolecularSignature(theirMolecularTypeNames);
		
		ArrayList<String> ourMolecularTypeNames = getListOfMolecularTypePatternSignatures(this.speciesPattern, criteria);
		String ourMolecularSignature = getMolecularSignature(ourMolecularTypeNames);
			
		return ourMolecularSignature.equals(theirMolecularSignature);
	}
	public boolean compareByCriteria(String theirMolecularSignature, Criteria criteria) {

		try {
			SpeciesPattern theirSpeciesPattern = RbmUtils.parseSpeciesPattern(theirMolecularSignature, modelCartoon.getModel());
			return compareByCriteria(theirSpeciesPattern, criteria);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to parse species pattern signature: " + e.getMessage());
		}
	}
	
	public ModelCartoon getModelCartoon() {
		return modelCartoon;
	}

	
	public Structure getStructure(){
		return structure;
	}
	public void setStructure(Structure structure) {
		this.structure = structure;
	}
	
	private static ArrayList<String> getListOfMolecularTypePatternSignatures(SpeciesPattern sp, Criteria crit) {
		
		ArrayList<String> mtpSignatures = new ArrayList<String>();
		for (MolecularTypePattern mtp : sp.getMolecularTypePatterns()){
			if(crit == Criteria.moleculeNumber) {
				mtpSignatures.add(mtp.getMolecularType().getName());
			} else if(crit == Criteria.full) {
				String signature = RbmUtils.toBnglString(mtp, null, CompartmentMode.hide, -1, true);
				mtpSignatures.add(signature);
			}
		}
		return mtpSignatures;
	}
	
	public String getLabel() {
		ArrayList<String> ourMolecularTypeNames = getListOfMolecularTypePatternSignatures(this.speciesPattern, Criteria.moleculeNumber);
		String ourMolecularSignature = getMolecularSignature(ourMolecularTypeNames);
		return ourMolecularSignature;
	}
	
	private static String getMolecularSignature(List<String> molecularTypePatternSignature){
		Collections.sort(molecularTypePatternSignature);
		StringBuffer buffer = new StringBuffer();
		for (int i=0; i<molecularTypePatternSignature.size(); i++){
			if (i>0){
				buffer.append(".");
			}
			buffer.append(molecularTypePatternSignature.get(i));
		}
		return buffer.toString();
	}
	
// ---------------------------------------------------------------------------------------
	
	public SpeciesPattern getSpeciesPattern() {
		if(modelCartoon instanceof ReactionCartoon) {
			ReactionCartoon reactionCartoon = (ReactionCartoon)modelCartoon;
			if(reactionCartoon.getRuleParticipantGroupingCriteria() == Criteria.full) {
				return speciesPattern;
			} else {
				SpeciesPattern spGroup = new SpeciesPattern();
				for(MolecularTypePattern mtp : speciesPattern.getMolecularTypePatterns()) {
					MolecularTypePattern mtpGroup = new MolecularTypePattern(mtp.getMolecularType());
					spGroup.addMolecularTypePattern(mtpGroup);
				}
				return spGroup;
			}
		}
		return speciesPattern;
	}
	public String getSpeciesPatternAsString() {
		return RbmUtils.toBnglString(speciesPattern, null, CompartmentMode.hide, -1);
	}

	public List<MolecularType> getMolecularTypes() {
		ArrayList<MolecularType> molecularTypes = new ArrayList<MolecularType>();
		for(MolecularTypePattern mtp : speciesPattern.getMolecularTypePatterns()) {
			molecularTypes.add(mtp.getMolecularType());
		}
		return molecularTypes;
	}
	
}
