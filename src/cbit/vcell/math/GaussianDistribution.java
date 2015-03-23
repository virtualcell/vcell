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

public class GaussianDistribution extends Distribution {
	private Expression mean, standardDeviation;

	public GaussianDistribution(Expression mu, Expression sigma) {
		this.mean = new Expression(mu);
		this.standardDeviation = new Expression(sigma);
	}
	
	public GaussianDistribution(CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {
		read(tokens);
	}

	private void read(CommentStringTokenizer tokens)
			throws MathFormatException, ExpressionException {
		String token = tokens.nextToken();
		if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
			throw new MathFormatException("unexpected token " + token
					+ " expecting " + VCML.BeginBlock);
		}
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				break;
			}
			if (token.equalsIgnoreCase(VCML.GaussianDistribution_Mean)) {
				mean = MathFunctionDefinitions.fixFunctionSyntax(tokens);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.GaussianDistribution_StandardDeviation)) {
				standardDeviation = MathFunctionDefinitions.fixFunctionSyntax(tokens);
				continue;
			} else
				throw new MathFormatException("unexpected identifier " + token);
		}

	}
	
	@Override
	public void bind(SymbolTable symbolTable) throws ExpressionBindingException {
		mean.bindExpression(symbolTable);
		standardDeviation.bindExpression(symbolTable);
	}

	public boolean compareEqual(Matchable obj) {
		if (!(obj instanceof GaussianDistribution)) {
			return false;
		}

		GaussianDistribution gd = (GaussianDistribution) obj;
		if (!Compare.isEqual(mean, gd.mean)) {
			return false;
		}
		if (!Compare.isEqual(standardDeviation, gd.standardDeviation)) {
			return false;
		}

		return true;
	}

	@Override
	public String getVCML() {
		StringBuffer sb = new StringBuffer();
		sb.append("\t" + VCML.GaussianDistribution + " " + VCML.BeginBlock
				+ "\n");
		sb.append("\t\t" + VCML.GaussianDistribution_Mean + " " + mean.infix()
				+ ";\n");
		sb.append("\t\t" + VCML.GaussianDistribution_StandardDeviation + " "
				+ standardDeviation.infix() + ";\n");
		sb.append("\t" + VCML.EndBlock + "\n");

		return sb.toString();
	}

	public final Expression getMean() {
		return mean;
	}

	public final Expression getStandardDeviation() {
		return standardDeviation;
	}

	@Override
	public void flatten(MathSymbolTable simSymbolTable,	boolean bRoundCoefficients) throws ExpressionException, MathException {
		mean = Equation.getFlattenedExpression(simSymbolTable, mean, bRoundCoefficients);
		standardDeviation = Equation.getFlattenedExpression(simSymbolTable, standardDeviation, bRoundCoefficients);
	}
}
