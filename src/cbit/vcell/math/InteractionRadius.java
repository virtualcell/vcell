package cbit.vcell.math;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;

public class InteractionRadius extends JumpProcessRateDefinition{
private Expression expression = null;
	
	public InteractionRadius(Expression exp){
		this.expression = exp;
	}

	public Expression getExpression(){
		return expression;
	}

	@Override
	public Expression[] getExpressions() {
		return new Expression[] { expression };
	}

	@Override
	public String getVCML() {
		return VCML.InteractionRadius+"\t"+getExpression().infix();
	}

	public boolean compareEqual(Matchable obj) {
		if (obj instanceof InteractionRadius){
			InteractionRadius mrc = (InteractionRadius)obj;
			if (!Compare.isEqual(getExpression(), mrc.getExpression())){
				return false;
			}
			return true;
		}
		return false;
	}
}
