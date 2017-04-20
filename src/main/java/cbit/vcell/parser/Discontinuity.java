/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.parser;

import org.vcell.util.Compare;

public class Discontinuity {
	Expression discontinuityExp;
	Expression rootFindingExp;
	int astRelationOperator;
	
	public Discontinuity(Expression argOriginalExp, Expression argRootFindingExp, int argAstRelationOperator) {
		super();
		this.discontinuityExp = argOriginalExp;
		this.rootFindingExp = argRootFindingExp;
		if(argAstRelationOperator < ASTRelationalNode.GT  || argAstRelationOperator > ASTRelationalNode.NE)
		{
			throw new IllegalArgumentException("AST Relational node out of range.");
		}
		this.astRelationOperator = argAstRelationOperator;
	}
	public Expression getDiscontinuityExp() {
		return discontinuityExp;
	}
	public Expression getRootFindingExp() {
		return rootFindingExp;
	}
	public int getASTRelationOperator()
	{
		return astRelationOperator;
	}
	public Expression getSignedRootFindingExp() {
		if(astRelationOperator == ASTRelationalNode.LT || astRelationOperator == ASTRelationalNode.LE)
		{
			return Expression.negate(rootFindingExp);
		}
		else
		{
			return rootFindingExp;
		}
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
	 
}
