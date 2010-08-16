package cbit.vcell.math;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;

public class MacroscopicRateConstant extends ParticleProbabilityRate {
	private Expression expression = null;
	
	public MacroscopicRateConstant(Expression exp){
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
		return VCML.MacroscopicRateConstant+"\t"+getExpression().infix();
	}

	public boolean compareEqual(Matchable obj) {
		if (obj instanceof MacroscopicRateConstant){
			MacroscopicRateConstant mrc = (MacroscopicRateConstant)obj;
			if (!Compare.isEqual(getExpression(), mrc.getExpression())){
				return false;
			}
			return true;
		}
		return false;
	}
	
}
