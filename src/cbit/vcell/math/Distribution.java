package cbit.vcell.math;

import java.io.Serializable;

import org.vcell.util.Matchable;

import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;

public abstract class Distribution implements Matchable, Serializable {
	public abstract void bind(SymbolTable symbolTable) throws ExpressionBindingException;	
	public abstract String getVCML(); 	
	
	public abstract double[] getRandomNumbers(int numRandomNumbers) throws ExpressionException; 
}