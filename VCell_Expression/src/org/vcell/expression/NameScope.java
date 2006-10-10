package org.vcell.expression;


/**
 * Insert the type's description here.
 * Creation date: (7/31/2003 9:43:42 AM)
 * @author: Jim Schaff
 */
public interface NameScope {
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:55:02 PM)
 * @return java.lang.String
 */
String getAbsoluteScopePrefix();
/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 11:49:10 AM)
 * @return cbit.vcell.parser.NameScope[]
 */
NameScope[] getChildren();
/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 11:56:19 AM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
SymbolTableEntry getExternalEntry(String identifier) throws ExpressionBindingException;
/**
 * Insert the method's description here.
 * Creation date: (8/26/2003 10:46:30 PM)
 * @return java.lang.String
 */
String getName();
/**
 * Insert the method's description here.
 * Creation date: (9/2/2003 11:57:21 AM)
 * @return cbit.vcell.parser.NameScope
 * @param prefix java.lang.String
 */
NameScope getNameScopeFromPrefix(String prefix);
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:55:50 PM)
 * @return cbit.vcell.parser.NameScope
 */
NameScope getParent();
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:55:02 PM)
 * @return java.lang.String
 * @param referenceNameScope cbit.vcell.parser.NameScope
 */
String getRelativeScopePrefix(NameScope referenceNameScope);
/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 11:50:50 AM)
 * @return cbit.vcell.parser.ScopedSymbolTable
 */
ScopedSymbolTable getScopedSymbolTable();
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 12:20:39 PM)
 * @return java.lang.String
 * @param symbolTableEntry cbit.vcell.parser.SymbolTableEntry
 */
String getSymbolName(SymbolTableEntry symbolTableEntry);
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 12:21:14 PM)
 * @return java.lang.String
 * @param unboundName java.lang.String
 */
String getUnboundSymbolName(String unboundName);
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:56:09 PM)
 * @return boolean
 * @param nameScope cbit.vcell.parser.NameScope
 */
boolean isAncestor(NameScope nameScope);
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:56:09 PM)
 * @return boolean
 * @param nameScope cbit.vcell.parser.NameScope
 */
boolean isPeer(NameScope nameScope);
}
