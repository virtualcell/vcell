package org.vcell.sybil.rdf.reason;

/*   SYBREAM  --- by Oliver Ruebenacker, UCHC --- June 2008
 *   Factory for various versions of the
 *   System Biological Reasoning Engine for Analysis and Modeling
 */

import java.util.List;

import org.vcell.sybil.rdf.reason.builtin.MakeAndKeep;

import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

public class SYBREAMFactory {

	static {
		BuiltinRegistry.theRegistry.register(new MakeAndKeep());		
	}
	
	static GenericRuleReasoner defaultSYBREAM;
	
	public static GenericRuleReasoner defaultGenericRuleReasoner(List<Rule> rules) {
		GenericRuleReasoner reasoner = new GenericRuleReasoner(rules);
		reasoner.setMode(GenericRuleReasoner.FORWARD_RETE);
		return reasoner;
	}
	
	public static GenericRuleReasoner defaultSYBREAM() {
		if(defaultSYBREAM == null) { defaultSYBREAM = defaultGenericRuleReasoner(SYBREAMRules.RULES); }
		return defaultSYBREAM;
	}
	
}
