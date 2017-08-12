package cbit.vcell.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;

public class ReactionRuleShortSignature {

	Set<ReactionRule> reactionRuleList = new HashSet<> ();
	
	public ReactionRuleShortSignature(ReactionRule reactionRule) {
		super();
		if(reactionRule != null) {
			this.reactionRuleList.add(reactionRule);
		}
	}
	private ReactionRuleShortSignature() {	// do not use default constructor
		super();
	}
	
	public boolean add(ReactionRule reactionRule) {
		for(ReactionRule rr : reactionRuleList) {
			if(rr.getName().equals(reactionRule.getName())) {
				// sanity check, the instances must be the same
				if(rr != reactionRule) {
					throw new RuntimeException("ReactionRuleShortSignature, found multiple instances of the same rule " + reactionRule.getDisplayName());
				}
				return false;	// already there, not added
			}
		}
		boolean ret = reactionRuleList.add(reactionRule);
		return ret;
	}
	public boolean remove(ReactionRule reactionRule) {
		boolean ret = reactionRuleList.remove(reactionRule);
		return ret;
	}
	
	public String getDisplayName() {
		return "( " + reactionRuleList.size() + " )";
	}
	
	public Set<ReactionRule> getReactionRules() {
		return reactionRuleList;
	}

	// ------------------------------------------
	public boolean isEmpty() {
		return reactionRuleList.isEmpty();
	}
	public boolean isConsistent(Map <String, Integer> rulesSignatureMap) {
		if(rulesSignatureMap.size() == 1) {
			return true;	// all rules have the same signature, we're consistent
		}
		return false;
	}
	public boolean compareBySignature(ReactionRule theirRr) {
		
		if(reactionRuleList.isEmpty()) {
			return false;
		}
		// verify that all the other rules still have the same signature
		Map <String, Integer> ruleSignaturesMap = getRulesSignatureMap();
		if(!isConsistent(ruleSignaturesMap)) {
			throw new RuntimeException("ReactionRuleShortSignature, contains multiple signatures");
		}
		
		// recover our signature (at this point we know that all the rules present have the same signature)
		Map.Entry<String, Integer> entry=ruleSignaturesMap.entrySet().iterator().next();
		String ourRuleSignature= entry.getKey();
		
		// compute the signature of rr and compare it with the signature of our rules.
		String theirRuleSignature = computeRuleSignature(theirRr);

		if(ourRuleSignature.equals(theirRuleSignature)) {
			return true;
		}
		return false;
	}
	
	// --- utility static methods ------------------------------------------------------
	private Map <String, Integer> getRulesSignatureMap() {
		
		Map <String, Integer> ruleSignaturesMap = new HashMap<>();
		for(ReactionRule rr : reactionRuleList) {
			
			String signature = computeRuleSignature(rr);
			if(ruleSignaturesMap.containsKey(signature)) {
				Integer value = ruleSignaturesMap.get(signature);
				value++;
				ruleSignaturesMap.put(signature, value);
			} else {
				ruleSignaturesMap.put(signature, 1);
			}
		}
		return ruleSignaturesMap;
	}
	
	private static String computeRuleSignature(ReactionRule rr) {
		List <String> signatures = new ArrayList<> ();
		List<String> reactantSignatures = getListOfReactantSignatures(rr);
		List<String> productSignatures = getListOfProductSignatures(rr);
		String reactantSignature = computeLongSignature(reactantSignatures);
		String productSignature = computeLongSignature(productSignatures);
		signatures.add(reactantSignature);
		signatures.add(productSignature);
		Collections.sort(signatures);
		// format:   comp1@mt1.mt2,comp2@mt3.mt4:comp3@mt5.mt6,comp4@mt7.mt8
		String signature = signatures.get(0) + ":" + signatures.get(1);
		return signature;
	}
	
	// sort a list of participant signatures (either reactants or products) 
	// and produce a unique string of comma separated participant signatures
	// format:   comp1@mt1.mt2,comp2@mt3.mt4,etc
	private static String computeLongSignature(List<String> signatures) {
		Collections.sort(signatures);
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<signatures.size(); i++) {
			if (i>0){
				buffer.append(",");
			}
			buffer.append(signatures.get(i));
		}
		String signature = buffer.toString();
		return signature;
	}
	
	private static List<String> getListOfReactantSignatures(ReactionRule rr) {
		
		List<String> reactantSignatures = new ArrayList<> ();
		for(ReactantPattern rp : rr.getReactantPatterns()) {
			String signature = getParticipantSignature(rp.getStructure(), rp.getSpeciesPattern());
			reactantSignatures.add(signature);
		}
		Collections.sort(reactantSignatures);
		return reactantSignatures;
	}
	private static List<String> getListOfProductSignatures(ReactionRule rr) {
		
		List<String> productSignatures = new ArrayList<> ();
		for(ProductPattern pp : rr.getProductPatterns()) {
			String signature = getParticipantSignature(pp.getStructure(), pp.getSpeciesPattern());
			productSignatures.add(signature);
		}
		Collections.sort(productSignatures);
		return productSignatures;
	}
	
	// format:   comp@mt1.mt2.etc
	private static String getParticipantSignature(Structure structure, SpeciesPattern speciesPattern) {
		
		List<String> spSignatures = getListOfMolecularTypePatternSignatures(speciesPattern);
		Collections.sort(spSignatures);
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i < spSignatures.size(); i++) {
			if (i > 0){
				buffer.append(".");
			}
			buffer.append(spSignatures.get(i));
		}
		String signature = structure.getName() + "@" + buffer.toString();
		return signature;	// even if there's no participant yet, the signature will contain at least the compartment name
	}
	
	
	private static ArrayList<String> getListOfMolecularTypePatternSignatures(SpeciesPattern sp) {
		
		ArrayList<String> mtpSignatures = new ArrayList<String>();
		for (MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
			mtpSignatures.add(mtp.getMolecularType().getDisplayName());
		}
		return mtpSignatures;
	}

	

	
}



