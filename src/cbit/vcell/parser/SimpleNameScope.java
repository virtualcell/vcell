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

/**
 * Insert the type's description here.
 * Creation date: (7/31/2003 4:18:02 PM)
 * @author: Jim Schaff
 */
public class SimpleNameScope extends AbstractNameScope {
	private NameScope parent = null;
	private NameScope children[] = new NameScope[0];
	private String name = null;
	private ScopedSymbolTable scopedSymbolTable = null;
/**
 * SimpleNameScope constructor comment.
 * @param argName java.lang.String
 */
public SimpleNameScope(String argName) {
	super();
	this.name = argName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 11:04:49 AM)
 * @param newChild cbit.vcell.parser.SymbolTable
 */
public void addChild(NameScope childNameScope) {
	if (childNameScope == null){
		throw new IllegalArgumentException("AbstractNameScope.addChild(): nameScope cannot be null");
	}
	for (int i = 0; i < children.length; i++){
		if (children.equals(childNameScope)){
			return;
		}
	}
	
	children = (NameScope[])org.vcell.util.BeanUtils.addElement(children,childNameScope);
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 11:04:49 AM)
 * @return cbit.vcell.parser.SymbolTable[]
 */
public cbit.vcell.parser.NameScope[] getChildren() {
	return children;
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 9:04:26 AM)
 * @return java.lang.String
 */
public String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:19:19 PM)
 * @return cbit.vcell.parser.AbstractNameScope
 */
public NameScope getParent() {
	return parent;
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 9:11:20 AM)
 * @return cbit.vcell.parser.ScopedSymbolTable
 */
public ScopedSymbolTable getScopedSymbolTable() {
	return scopedSymbolTable;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 11:04:49 AM)
 * @param newChild cbit.vcell.parser.SymbolTable
 */
public void removeChild(NameScope childNameScope) {
	if (childNameScope == null){
		throw new IllegalArgumentException("AbstractNameScope.removeChild(): nameScope cannot be null");
	}
	for (int i = 0; i < children.length; i++){
		if (children.equals(childNameScope)){
			children = (NameScope[])org.vcell.util.BeanUtils.removeElement(children,childNameScope);
		}
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 11:04:49 AM)
 * @param newChildren cbit.vcell.parser.SymbolTable[]
 */
public void setChildren(cbit.vcell.parser.NameScope[] newChildren) {
	if (newChildren == null){
		throw new IllegalArgumentException("AbstractNameScope.setChildren(): nameScope[] cannot be null");
	}
	children = newChildren;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:19:19 PM)
 * @param newParent cbit.vcell.parser.AbstractNameScope
 */
public void setParent(AbstractNameScope newParent) {
	parent = newParent;
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 11:44:46 AM)
 * @param newScopedSymbolTable cbit.vcell.parser.ScopedSymbolTable
 */
public void setScopedSymbolTable(ScopedSymbolTable newScopedSymbolTable) {
	scopedSymbolTable = newScopedSymbolTable;
}
}
