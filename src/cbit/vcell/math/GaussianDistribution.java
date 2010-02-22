package cbit.vcell.math;

import java.util.Random;

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
				mean = new Expression(tokens);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.GaussianDistribution_StandardDeviation)) {
				standardDeviation = new Expression(tokens);
				continue;
			} else
				throw new MathFormatException("unexpected identifier " + token);
		}

	}

	@Override
	public double[] getRandomNumbers(int numRandomNumbers) throws ExpressionException {
		double muVal = mean.evaluateConstant();
		double sigmaVal = standardDeviation.evaluateConstant();

		double[] randomNumbers = new double[numRandomNumbers];

		Random random = new Random();
		for (int i = 0; i < numRandomNumbers; i++) {
			double r = random.nextGaussian();
			randomNumbers[i] = sigmaVal * r + muVal;
		}
		return randomNumbers;
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
}