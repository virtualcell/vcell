package cbit.vcell.solver.test;

/**
 * Insert the type's description here.
 * Creation date: (1/16/2003 2:14:07 PM)
 * @author: Jim Schaff
 */
public interface ODESolution {
/**
 * Insert the method's description here.
 * Creation date: (1/16/2003 2:15:26 PM)
 * @return double
 * @param name java.lang.String
 * @param x double
 * @param y double
 * @param z double
 * @param t double
 */
TimeSeriesSample getSamples(String name) throws cbit.vcell.parser.ExpressionException;
}
