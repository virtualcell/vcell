package cbit.vcell.parser;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * A <code>SymbolTableEntry</code> describes an interface for any object that
 * can represent the value corresponding to a symbol in an expression.  
 * A <code>SymbolTableEntry</code> is returned by a <code>SymbolTable</code>
 * to resolve the reference of a symbol defined in an <code>Expression</code> 
 * or a <code>ReactionParticipant</code>
 * <p>
 */
public interface SymbolTableEntry {

/**
 * This method was created in VisualAge.
 * @return double
 */
public double getConstantValue() throws ExpressionException;
/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @exception java.lang.Exception The exception description.
 */
public Expression getExpression() throws ExpressionException;
/**
 * This method was created in VisualAge.
 * @return int
 */
int getIndex();
/**
 *
 *
 */
public String getName();    
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 10:22:46 AM)
 * @return cbit.vcell.parser.NameScope
 */
NameScope getNameScope();
/**
 * Insert the method's description here.
 * Creation date: (3/31/2004 10:33:52 AM)
 * @return cbit.vcell.units.VCUnitDefinition
 */
cbit.vcell.units.VCUnitDefinition getUnitDefinition();
/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @exception java.lang.Exception The exception description.
 */
public boolean isConstant() throws ExpressionException;
}
