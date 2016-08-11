package cbit.vcell.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;

import sun.awt.im.CompositionArea;

public class RuleParticipantSignature {

	//private List<ReactionRuleParticipant> rrpList = new ArrayList<>();
	private Structure structure = null;
	private HashMap<MolecularType, Integer> composition = new HashMap<MolecularType, Integer>();
	
	private RuleParticipantSignature() {
		super();
	}
	
	public static RuleParticipantSignature fromReactionRuleParticipant(ReactionRuleParticipant rrParticipant){
		RuleParticipantSignature ruleParticipantSignature = new RuleParticipantSignature();
		ruleParticipantSignature.structure = rrParticipant.getStructure();
		for (MolecularTypePattern mtp : rrParticipant.getSpeciesPattern().getMolecularTypePatterns()){
			Integer count = ruleParticipantSignature.composition.get(mtp.getMolecularType());
			if (count == null){
				ruleParticipantSignature.composition.put(mtp.getMolecularType(), 1);
			}else{
				ruleParticipantSignature.composition.put(mtp.getMolecularType(), count + 1);
			}
		}
		return ruleParticipantSignature;
	}
	
//	public List<ReactionRuleParticipant> getReactionRuleParticipantList() {
//		return Collections.unmodifiableList(rrpList);
//	}
//	
//	public void addReactionRuleParticipant(ReactionRuleParticipant participant){
//		if (rrpList.contains(participant)){
//			throw new RuntimeException("already in list");
//		}
//		rrpList.add(participant);
//	}
//	
//	public void removeReactionRuleParticipant(ReactionRuleParticipant participant){
//		if (!rrpList.contains(participant)){
//			throw new RuntimeException("not in list");
//		}
//		rrpList.remove(participant);
//	}
//	
//	public boolean contains(ReactionRuleParticipant participant){
//		return rrpList.contains(participant);
//	}
//	
//	public Structure getStructure() {
//		if(rrpList.isEmpty()) {
//			return null;
//		}
//		return rrpList.get(0).getStructure();
//	}
	public Structure getStructure(){
		return structure;
	}
	
//	public String getLabel(){
//	if(rrpList.isEmpty()) {
//		return null;
//	}
//	ReactionRuleParticipant rrp = rrpList.get(0);
//	String label = getLabel(rrp);
//	return label;
//}

	public String getLabel(){
		return getSignature();
	}
	
	private String getSignature(){
		ArrayList<String> molecularTypeNames = new ArrayList<String>();
		for (Entry<MolecularType, Integer> entry : composition.entrySet()){
			for (int i=0;i<entry.getValue();i++){
				molecularTypeNames.add(entry.getKey().getName());
			}
		}
		String molecularSignature = getMolecularSignature(molecularTypeNames);
		return molecularSignature;
	}
	
	private String getUniqueSignature(){
		return getStructure().getName()+":"+getSignature();
	}
	
	public SpeciesPattern getSpeciesPattern() {
		SpeciesPattern sp = new SpeciesPattern();
		for (Entry<MolecularType, Integer> entry : composition.entrySet()){
			for (int i=0;i<entry.getValue();i++){
				MolecularType mt = entry.getKey();
				MolecularTypePattern mtp = new MolecularTypePattern(mt);
				sp.addMolecularTypePattern(mtp);
			}
		}
		return sp;
	}

	public static String getSignature(ReactionRuleParticipant reactionRuleParticipant){
		ArrayList<String> molecularTypeNames = new ArrayList<String>();
		for (MolecularTypePattern mt : reactionRuleParticipant.getSpeciesPattern().getMolecularTypePatterns()){
			molecularTypeNames.add(mt.getMolecularType().getName());
		}
		String molecularSignature = getMolecularSignature(molecularTypeNames);
		return molecularSignature;
	}
	
	public static String getUniqueSignature(ReactionRuleParticipant reactionRuleParticipant){
		String molecularSignature = getSignature(reactionRuleParticipant);
		return reactionRuleParticipant.getStructure().getName()+":"+molecularSignature;
	}
	
	private static String getMolecularSignature(List<String> molecularTypeNames){
		Collections.sort(molecularTypeNames);
		StringBuffer buffer = new StringBuffer();
		for (int i=0; i<molecularTypeNames.size(); i++){
			if (i>0){
				buffer.append(".");
			}
			buffer.append(molecularTypeNames.get(i));
		}
		return buffer.toString();
	}

	public List<MolecularType> getMolecularTypes() {
		ArrayList<MolecularType> molecularTypes = new ArrayList<MolecularType>();
		for (Entry<MolecularType, Integer> entry : composition.entrySet()){
			for (int i=0;i<entry.getValue();i++){
				molecularTypes.add(entry.getKey());
			}
		}
		return molecularTypes;
	}
}
