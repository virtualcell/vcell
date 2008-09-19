package cbit.vcell.parser;

import cbit.util.Compare;
import cbit.vcell.math.MathUtilities;

public class Discontinuity {
	Expression discontinuityExp;
	Expression rootFindingExp;
	
	public Discontinuity(Expression argOriginalExp, Expression argRootFindingExp) {
		super();
		this.discontinuityExp = argOriginalExp;
		this.rootFindingExp = argRootFindingExp;
	}
	public Expression getDiscontinuityExp() {
		return discontinuityExp;
	}
	public Expression getRootFindingExp() {
		return rootFindingExp;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Discontinuity) {
			return Compare.isEqual(discontinuityExp, ((Discontinuity)obj).discontinuityExp);			
		}
		return false;
	}
	@Override
	public int hashCode() {
		return discontinuityExp.infix().hashCode();
	}
	public void subsituteAndFlatten(SymbolTable st) throws ExpressionException {		
		discontinuityExp = MathUtilities.substituteFunctions(discontinuityExp, st).flatten();
		rootFindingExp = MathUtilities.substituteFunctions(rootFindingExp, st).flatten();
	}
	 
}
