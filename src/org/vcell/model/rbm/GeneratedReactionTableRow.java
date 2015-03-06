package org.vcell.model.rbm;

import cbit.vcell.bionetgen.BNGReaction;

public class GeneratedReactionTableRow {

	private BNGReaction reactionObject;
	private String index;
	private String expression;
	
	public GeneratedReactionTableRow(BNGReaction reactionObject) {
		this.reactionObject = reactionObject;
	}
	
	public BNGReaction getReactionObject() {
		return reactionObject;
	}

	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
}

