package cbit.vcell.math;

import java.io.Serializable;

import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;

public abstract class ParticleProbabilityRate implements Matchable, Serializable{

	public abstract String getVCML();

	public abstract Expression[] getExpressions();
}
