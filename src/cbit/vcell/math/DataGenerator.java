package cbit.vcell.math;

import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

public abstract class DataGenerator extends Variable {

	protected DataGenerator(String argName, Domain argDomain) {
		super(argName, argDomain);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean compareEqual(Matchable object, boolean bIgnoreMissingDomains) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getVCML() throws MathException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public abstract void bind(SymbolTable symbolTable) throws ExpressionBindingException;


}
