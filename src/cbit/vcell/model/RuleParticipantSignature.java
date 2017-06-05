package cbit.vcell.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vcell.model.bngl.ParseException;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;

import cbit.vcell.graph.ModelCartoon;

public abstract class RuleParticipantSignature {

	protected Structure structure = null;
	protected SpeciesPattern firstSpeciesPattern = null;
	protected ModelCartoon modelCartoon = null;		// it's always a ReactionCartoon, actually
	
	protected RuleParticipantSignature() {
		super();
	}
	
	public abstract GroupingCriteria getGroupingCriteria();

	public boolean compareByCriteria(SpeciesPattern speciesPattern, GroupingCriteria criteria) {

		ArrayList<String> theirMolecularTypeNames = getListOfMolecularTypePatternSignatures(speciesPattern, criteria);
		String theirMolecularSignature = getMolecularSignature(theirMolecularTypeNames);
		
		ArrayList<String> ourMolecularTypeNames = getListOfMolecularTypePatternSignatures(this.firstSpeciesPattern, criteria);
		String ourMolecularSignature = getMolecularSignature(ourMolecularTypeNames);
			
		return ourMolecularSignature.equals(theirMolecularSignature);
	}
	public boolean compareByCriteria(String theirMolecularSignature, GroupingCriteria criteria) {

		try {
			SpeciesPattern theirSpeciesPattern = RbmUtils.parseSpeciesPattern(theirMolecularSignature, modelCartoon.getModel());
			return compareByCriteria(theirSpeciesPattern, criteria);
		} catch (ParseException e) {
			e.printStackTrace();
//			throw new RuntimeException("Unable to parse species pattern signature: " + e.getMessage());
			return false;
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
	
	private static ArrayList<String> getListOfMolecularTypePatternSignatures(SpeciesPattern sp, GroupingCriteria crit) {
		
		ArrayList<String> mtpSignatures = new ArrayList<String>();
		for (MolecularTypePattern mtp : sp.getMolecularTypePatterns()){
			if(crit == GroupingCriteria.molecule) {
				mtpSignatures.add(mtp.getMolecularType().getName());
			} else if(crit == GroupingCriteria.rule) {
				mtpSignatures.add(mtp.getMolecularType().getName());
			} else if(crit == GroupingCriteria.full) {
				String signature = RbmUtils.toBnglString(mtp, null, CompartmentMode.hide, -1, true);
				mtpSignatures.add(signature);
			}
		}
		return mtpSignatures;
	}
	
	public String getLabel() {
		ArrayList<String> ourMolecularTypeNames = getListOfMolecularTypePatternSignatures(this.firstSpeciesPattern, GroupingCriteria.molecule);
		String ourMolecularSignature = getMolecularSignature(ourMolecularTypeNames);
		return ourMolecularSignature;
	}
	
	private static String getMolecularSignature(List<String> molecularTypePatternSignature) {		// sorted!
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
	
	public SpeciesPattern getSpeciesPattern() {			// only used in paint for RuleParticipantSignatureDiagramShape
//		if(modelCartoon instanceof ReactionCartoon) {
//			ReactionCartoon reactionCartoon = (ReactionCartoon)modelCartoon;
//			if(reactionCartoon.getRuleParticipantGroupingCriteria() == Criteria.full) {
//				return speciesPattern;
//			} else {
//				SpeciesPattern spGroup = new SpeciesPattern();
//				for(MolecularTypePattern mtp : speciesPattern.getMolecularTypePatterns()) {
//					MolecularTypePattern mtpGroup = new MolecularTypePattern(mtp.getMolecularType());
//					spGroup.addMolecularTypePattern(mtpGroup);
//				}
//				return spGroup;
//			}
//		}
		return firstSpeciesPattern;
	}
	public String getFirstSpeciesPatternAsString() {			// sorted!
//		return RbmUtils.toBnglString(speciesPattern, null, CompartmentMode.hide, -1);
		ArrayList<String> molecularTypeNames = getListOfMolecularTypePatternSignatures(firstSpeciesPattern, GroupingCriteria.full);
		String molecularSignature = getMolecularSignature(molecularTypeNames);
		return molecularSignature;
	}
	public static String getSpeciesPatternAsString(SpeciesPattern sp) {
		ArrayList<String> molecularTypeNames = getListOfMolecularTypePatternSignatures(sp, GroupingCriteria.full);
		String molecularSignature = getMolecularSignature(molecularTypeNames);
		return molecularSignature;
	}

	public List<MolecularType> getMolecularTypes() {	// only used in paint for RuleParticipantSignatureDiagramShape
		ArrayList<MolecularType> molecularTypes = new ArrayList<MolecularType>();
		for(MolecularTypePattern mtp : firstSpeciesPattern.getMolecularTypePatterns()) {
			molecularTypes.add(mtp.getMolecularType());
		}
		return molecularTypes;
	}
	
}
