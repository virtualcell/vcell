package org.vcell.sybil.rdf.reason.rules;

/*   RuleSpec  --- by Oliver Ruebenacker, UCHC --- May 2008
 *   Class for collecting information about a rule, for example for creating one
 */

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.reasoner.rulesys.Node_RuleVariable;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

public class RuleSpec {
	
	protected String name;
	protected ClauseList body;
	protected ClauseList head;
	protected Map<String, Node_RuleVariable> vars;
	
	public RuleSpec() { initRuleSpec(); }
	
	public RuleSpec(String nameNew) { 
		name = nameNew; 
		initRuleSpec();
	}

	private void initRuleSpec() {
		body = new ClauseList();
		head = new ClauseList();
		vars = new HashMap<String, Node_RuleVariable>();		
	}
	
	public void setName(String nameNew) { name = nameNew; }
	public String name() { return name; }
	public ClauseList body() { return body; }
	public ClauseList head() { return head; }
	
	public Node_RuleVariable var(String name) {
		Node_RuleVariable var = vars.get(name);
		if(var == null) {
			var = new Node_RuleVariable(name, vars.size());
			vars.put(name, var);
		}
		return var;
	}
	
	public Rule createRule() { 
		if(name != null) { return new Rule(name, head, body); }
		else { return new Rule(head, body); }
	}
	
}
