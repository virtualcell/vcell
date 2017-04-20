/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Insert the type's description here.
 * Creation date: (7/31/2003 3:10:31 PM)
 * @author: Jim Schaff
 */
public abstract class AbstractNameScope implements NameScope, java.io.Serializable {
/**
 * AbstractNameScope constructor comment.
 */
public AbstractNameScope() {
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:36:04 PM)
 * @return java.lang.String
 * @param referenceNameScope cbit.vcell.parser.AbstractNameScope
 */
public String getAbsoluteScopePrefix() {
	String prefix = "";
	NameScope nameScope = this;
	while (nameScope!=null){
		prefix = nameScope.getName()+"."+prefix;
		nameScope = nameScope.getParent();
	}
	return prefix;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 11:04:49 AM)
 * @return cbit.vcell.parser.SymbolTable[]
 */
public abstract NameScope[] getChildren();
/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 11:57:04 AM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public SymbolTableEntry getExternalEntry(String identifier, SymbolTable localSymbolTable) {
	
	String prefix = getPrefix(identifier);

	if (prefix.length()==0){
		//
		// no prefix, look in current symbol table (if not the local one) and pass up the tree if not found
		//
		if (getScopedSymbolTable()!=null){
			if (getScopedSymbolTable()!=localSymbolTable){
				SymbolTableEntry ste = getScopedSymbolTable().getLocalEntry(identifier);
				if (ste!=null){
					return ste;
				}
			}
		}else{
			throw new RuntimeException("error binding '"+identifier+"', nameScope '"+getName()+"' has no symbolTable");
		}
		if (getParent()!=null){
			return getParent().getExternalEntry(identifier, localSymbolTable);
		}else{
			return null;
		}
	}else{
		//
		// has explicit scoping, get resolved scope from prefix and resolve bare identifier to that symbolTable.
		//
		NameScope nameScope = getNameScopeFromPrefix(prefix);
		if (nameScope != null){
			String strippedName = identifier;
			while (getSuffix(strippedName).length()>0){
				strippedName = getSuffix(strippedName);
			}
			if (nameScope.getScopedSymbolTable()!=null){
				SymbolTableEntry ste = nameScope.getScopedSymbolTable().getLocalEntry(strippedName);
				if (ste!=null){
					return ste;
				}else{
					return null;
				}
			}else{
				throw new RuntimeException("error binding '"+identifier+"', nameScope '"+getName()+"' has no symbolTable");
			}
		}else{
			// throw new RuntimeException("error binding '"+identifier+"', explicit scope '"+prefix+"' was unresolved");
			return null;
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:14:10 PM)
 * @return java.lang.String
 */
public abstract String getName();
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:36:04 PM)
 * @return java.lang.String
 * @param referenceNameScope cbit.vcell.parser.AbstractNameScope
 */
public NameScope getNameScopeFromPrefix(String prefix) {
	//
	//
	// A
	//  \
	//   B
	//    \
	//     C
	//
	//   A.getNameScopeFromPrefix(null)    should throw an exception
	//   A.getNameScopeFromPrefix("")      should throw an exception
	//
	//   A.getNameScopeFromPrefix("A")     should return A
	//   A.getNameScopeFromPrefix("B")     should return B
	//   A.getNameScopeFromPrefix("A.B")   should return B ???
	//   A.getNameScopeFromPrefix("C")     should return null
	//   A.getNameScopeFromPrefix("A.C)    should return null
	//   A.getNameScopeFromPrefix("C.A)    should return null
	//   A.getNameScopeFromPrefix("B.C")   should return C
	//   A.getNameScopeFromPrefix("A.B.C") should return C
	//
	//   B.getNameScopeFromPrefix("A")     should return A
	//   B.getNameScopeFromPrefix("B")     should return B
	//   B.getNameScopeFromPrefix("A.B")   should return B ???
	//   B.getNameScopeFromPrefix("C")     should return C
	//   B.getNameScopeFromPrefix("A.C)    should return null
	//   B.getNameScopeFromPrefix("C.A)    should return null
	//   B.getNameScopeFromPrefix("B.C")   should return C
	//   B.getNameScopeFromPrefix("A.B.C") should return null
	//
	//   C.getNameScopeFromPrefix("A")     should return null
	//   C.getNameScopeFromPrefix("B")     should return B
	//   C.getNameScopeFromPrefix("A.B")   should return B ???
	//   C.getNameScopeFromPrefix("C")     should return C
	//   C.getNameScopeFromPrefix("A.C)    should return null
	//   C.getNameScopeFromPrefix("C.A)    should return null
	//   C.getNameScopeFromPrefix("B.C")   should return C ???
	//   C.getNameScopeFromPrefix("A.B.C") should return C ???
	//
	if (prefix == null){
		throw new IllegalArgumentException("AbstractNameScope.getNameScopeFromPrefix(), prefix must not be null");
	}
	//
	// if prefix is empty or is this Name (e.g. A where this = A)
	//
	String name = getName();
	if (prefix.length()==0 || prefix.equals(name)){
		return this;
	}
	
	//
	// if scope starts with this Name (e.g.  A.B.C where this = A)
	// then pass down to children to continue resolving
	//
	NameScope children[] = getChildren();
	if (prefix.startsWith(name)){
		for (int i = 0; i < children.length; i++){
			NameScope nameScope = ((AbstractNameScope)children[i]).getNameScopeFromPrefix(getSuffix(prefix));
			if (nameScope!=null){
				return nameScope;
			}
		}
	//
	// if scope ends with this Name (e.g.  A.B.C where this = C)
	// then confirm that the prefix is consistent with the parents (check A->B is the parent).
	//
	}else if (prefix.endsWith(name)){
		String remainingPrefix = getPrefix(prefix);
		NameScope tempParent = getParent();
		while (remainingPrefix.length()>0){
			if (tempParent == null){
				return null;
			}
			if (!remainingPrefix.endsWith(tempParent.getName())){
				return null;
			}	
			tempParent = tempParent.getParent();
			remainingPrefix = getPrefix(remainingPrefix);
		}
		return this;
	}else{
		//
		// if scope starts with a child's name (e.g. B.C where this = A)
		// then pass down to appropriate child and continue resolving
		//
		for (int i = 0; i < children.length; i++){
			if (prefix.startsWith(children[i].getName())){
				NameScope nameScope = ((AbstractNameScope)children[i]).getNameScopeFromPrefix(prefix);
				if (nameScope!=null){
					return nameScope;
				}
			}
		}
		//
		// else, pass prefix up the tree to see if a parent owns it
		//
		if (getParent()!=null){
			return ((AbstractNameScope)getParent()).getNameScopeFromPrefix(prefix);
		}		
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:19:19 PM)
 * @return cbit.vcell.parser.AbstractNameScope
 */
public abstract NameScope getParent();
/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 2:45:47 PM)
 * @return java.lang.String
 * @param identifier java.lang.String
 */
public static String getPrefix(String identifier) {
	if (identifier.startsWith(".") || identifier.endsWith(".")){
		throw new RuntimeException("identifier '"+identifier+"' either starts with or ends with a period (.)");
	}
	int lastIndexOfPeriod = identifier.lastIndexOf(".");
	if (lastIndexOfPeriod<0){
		return "";
	}else{
		return identifier.substring(0,lastIndexOfPeriod);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:36:04 PM)
 * @return java.lang.String
 * @param referenceNameScope cbit.vcell.parser.AbstractNameScope
 */
public String getRelativeScopePrefix(NameScope referenceNameScope) {
	//
	// A.getRelativeScopePrefix(B) should return "A."
	// A.getRelativeScopePrefix(C) should return "A."
	// C.getRelativeScopePrefix(A) should return "B.C."
	// C.getRelativeScopePrefix(B) should return "C."
	// B.getRelativeScopePrefix(A) should return "B."
	// B.getRelativeScopePrefix(C) should return "B."
	//
	// A
	//  \
	//   B
	//    \
	//     C
	//
	String name = getName();
	if (referenceNameScope.isAncestor(this)){
		return name+".";
	}else if (isAncestor(referenceNameScope)){
		return getParent().getRelativeScopePrefix(referenceNameScope)+name+".";
	}else if (referenceNameScope == this || referenceNameScope.isPeer(this)){
		return "";
	}else{
		System.out.println("AbstractNameScope.getRelativeScopePrefix() scopes '"+name+"' and '"+referenceNameScope.getName()+"' are unrelated");
		return "UNRESOLVED.";
		//throw new RuntimeException("scopes are unrelated");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 11:44:46 AM)
 * @return cbit.vcell.parser.ScopedSymbolTable
 */
public abstract ScopedSymbolTable getScopedSymbolTable();
/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 2:45:47 PM)
 * @return java.lang.String
 * @param identifier java.lang.String
 */
public static String getStrippedIdentifier(String identifier) {
	String strippedName = identifier;
	while (getSuffix(strippedName).length()>0){
		strippedName = getSuffix(strippedName);
	}
	return strippedName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 2:45:47 PM)
 * @return java.lang.String
 * @param identifier java.lang.String
 */
public static String getSuffix(String identifier) {
	if (identifier.startsWith(".") || identifier.endsWith(".")){
		throw new RuntimeException("identifier '"+identifier+"' either starts with or ends with a period (.)");
	}
	int firstIndexOfPeriod = identifier.indexOf(".");
	if (firstIndexOfPeriod<0){
		return "";
	}else{
		return identifier.substring(firstIndexOfPeriod+1,identifier.length());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:10:31 PM)
 * @return java.lang.String
 * @param symbolTableEntry cbit.vcell.parser.SymbolTableEntry
 */
public String getSymbolName(SymbolTableEntry symbolTableEntry) {
	//
	// reconcile name according to relationship between bound nameScope and currentNameScope
	//
	if (symbolTableEntry==null){
		throw new IllegalArgumentException("symbolTableEntry was null");
	}
	if (symbolTableEntry instanceof SymbolProxy){
		return getSymbolName(((SymbolProxy)symbolTableEntry).getTarget());
	}
	if (symbolTableEntry.getNameScope()==null){
		//throw new RuntimeException("NameScope can't resolve bound symbol '"+symbolTableEntry.getName()+"', symbol has no scope");
		System.out.println("AbstractNameScope.getSymbolName() can't resolve bound symbol '"+symbolTableEntry.getName()+"', symbol has no scope");
		return symbolTableEntry.getName();
	}else{
		if (symbolTableEntry.getNameScope() == this){
			return symbolTableEntry.getName();
		}else{
			//
			// scopes are different
			//
			// if not ambiguous (bound to default binding), don't introduce relative mangling
			//
			SymbolTableEntry defaultBinding = null;
			defaultBinding = getExternalEntry(symbolTableEntry.getName(),getScopedSymbolTable());
			if (defaultBinding!=null && defaultBinding.getNameScope().equals(symbolTableEntry.getNameScope())){
				return symbolTableEntry.getName();
			}else{
				//
				// differs from default binding, need mangling
				//
				return symbolTableEntry.getNameScope().getRelativeScopePrefix(this)+symbolTableEntry.getName();
			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:10:31 PM)
 * @return java.lang.String
 * @param unboundName java.lang.String
 */
public String getUnboundSymbolName(String unboundName) {
	//System.out.println("AbstractNameScope.getUnboundSymbolName("+unboundName+"): within scope "+getName());
	return unboundName;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:21:40 PM)
 * @return boolean
 * @param nameScope cbit.vcell.parser.NameScope
 */
public boolean isAncestor(NameScope nameScope) {
	if (getParent() == nameScope || (nameScope!=null && nameScope.isPeer(getParent()))){
		return true;
	}else if (getParent() == null){
		return false;
	}else{
		return getParent().isAncestor(nameScope);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:21:40 PM)
 * @return boolean
 * @param nameScope cbit.vcell.parser.NameScope
 */
public boolean isPeer(NameScope nameScope) {
	return false; // default
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 4:47:26 PM)
 * @return java.lang.String
 */
public String toString() {
	return getClass().getName() + "@" + Integer.toHexString(hashCode())+": '"+getName()+"'";
}

public void getExternalEntries(Map<String, SymbolTableEntry> entryMap) {	
	if (getParent() != null) {
		getParent().getExternalEntries(entryMap);
	}
	getScopedSymbolTable().getLocalEntries(entryMap);
}

public String getPathDescription() {
	return getName();
}


@Override
public void findReferences(SymbolTableEntry symbolTableEntry, ArrayList<SymbolTableEntry> references, HashSet<NameScope> visited) {
	//
	// if already visited, don't process again
	//
	if (visited.contains(this)){
		return;
	}
	visited.add(this);
	
	//
	// find references in local symbol's expressions.
	//
	HashMap<String, SymbolTableEntry> entryMap = new HashMap<String, SymbolTableEntry>();
	getScopedSymbolTable().getLocalEntries(entryMap);
	for (SymbolTableEntry localSTE : entryMap.values()) {
		if (localSTE.getExpression()!=null){
			SymbolTableEntry boundSTE = localSTE.getExpression().getSymbolBinding(symbolTableEntry.getName());
			//
			// if bound to this symbol (or a proxy of this symbol), then add to the list
			//
			if (boundSTE == symbolTableEntry || ((boundSTE instanceof SymbolProxy) && ((SymbolProxy)boundSTE).getTarget() == symbolTableEntry)){
				if (references.contains(localSTE)){
					System.err.println("found duplicate referenced to "+localSTE.getClass().getName()+"("+localSTE.getName()+") when looking for references to ste "+symbolTableEntry.getName());
					Thread.dumpStack();
				}else{
					references.add(localSTE);
				}
			}
		}
	}
	
	//
	// propagate to children
	//
	NameScope[] children = getChildren();
	if (children!=null){
		for (NameScope child : children){
			child.findReferences(symbolTableEntry, references, visited);
		}
	}
}

}
