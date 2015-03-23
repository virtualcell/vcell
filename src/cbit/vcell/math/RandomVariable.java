/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;

public abstract class RandomVariable extends Variable {

	private Expression seed = null;
	private Distribution distribution = null;

	protected RandomVariable(String name, Expression seed, Distribution dist, Domain domain) {
		super(name, domain);
		this.seed = seed;
		distribution = dist;
	}
	
	protected RandomVariable(String name, MathDescription mathDesc, CommentStringTokenizer tokens, Domain domain) throws MathFormatException, ExpressionException {
		super(name, domain);
		read(tokens);
	}
	
	private void read(CommentStringTokenizer tokens) throws ExpressionException, MathFormatException {
		String token = tokens.nextToken();
		if (!token.equalsIgnoreCase(VCML.BeginBlock)){
			throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
		}
		seed = new Expression(0.0);
		distribution = new UniformDistribution(new Expression(0.0), new Expression(1.0));
		while (tokens.hasMoreTokens()){
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)){
				break;
			}			
			if(token.equalsIgnoreCase(VCML.RandomVariable_Seed))
			{
				seed = MathFunctionDefinitions.fixFunctionSyntax(tokens);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.UniformDistribution)) {
				distribution = new UniformDistribution(tokens);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.GaussianDistribution)) {
				distribution = new GaussianDistribution(tokens);
				continue;
			}
			else throw new MathFormatException("unexpected identifier "+token);

		}	
	}

	public boolean compareEqual(Matchable obj,boolean bIgnoreMissingDomain) {
		if (!(obj instanceof RandomVariable)){
			return false;
		}
		if (!compareEqual0(obj,bIgnoreMissingDomain)){
			return false;
		}
		RandomVariable v = (RandomVariable)obj;
		if (!Compare.isEqual(distribution,v.distribution)){
			return false;
		}
		return true;
	}
	
	protected abstract String getVCMLTag();

	@Override
	public String getVCML() throws MathException {
		StringBuffer sb = new StringBuffer();
		sb.append(getVCMLTag() + " " + getQualifiedName()  + " " + VCML.BeginBlock + "\n");
		if (seed != null) {
			sb.append("\t" + VCML.RandomVariable_Seed + " " + seed.infix() + ";\n");
		}
		sb.append(distribution.getVCML());
		sb.append(VCML.EndBlock + "\n");
		return sb.toString();
	}

	public final Expression getSeed() {
		return seed;
	}

	public final Distribution getDistribution() {
		return distribution;
	}

	@Override
	public void bind(SymbolTable symbolTable) throws ExpressionBindingException {		
		seed.bindExpression(symbolTable);
		distribution.bind(symbolTable);
	}

	public void flatten(MathSymbolTable simSymbolTable, boolean bRoundCoefficients) throws ExpressionException, MathException {

		seed = Equation.getFlattenedExpression(simSymbolTable,seed,bRoundCoefficients);
		distribution.flatten(simSymbolTable, bRoundCoefficients);
	}
}
