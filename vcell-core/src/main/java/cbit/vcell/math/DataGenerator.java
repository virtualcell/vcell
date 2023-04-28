package cbit.vcell.math;

import cbit.vcell.parser.ExpressionException;
import org.vcell.util.Issue.IssueSource;


public abstract class DataGenerator extends Variable implements IssueSource {

	protected DataGenerator(String argName, Domain argDomain) {
		super(argName, argDomain);
	}

    public abstract void flatten(MathSymbolTable mathSymbolTable, boolean bRoundCoefficients) throws MathException, ExpressionException;
}
