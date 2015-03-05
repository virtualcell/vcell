package org.vcell.model.rbm;

import cbit.vcell.bionetgen.BNGReaction;

public class GeneratedReactionTableRow {

	private BNGReaction reactionObject;
	private int index;
	private String expression;
	private int depiction;
	
	public GeneratedReactionTableRow(BNGReaction reactionObject) {
		this.reactionObject = reactionObject;
	}
	
	public BNGReaction getReactionObject() {
		return reactionObject;
	}

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public int getDepiction() {
		return depiction;
	}
	public void setDepiction(int depiction) {
		this.depiction = depiction;
	}
}

