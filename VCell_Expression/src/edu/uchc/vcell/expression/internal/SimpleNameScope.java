package edu.uchc.vcell.expression.internal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import org.vcell.expression.AbstractNameScope;
import org.vcell.expression.NameScope;
import org.vcell.expression.ScopedSymbolTable;

import cbit.util.TokenMangler;

/**
 * Insert the type's description here.
 * Creation date: (7/31/2003 4:18:02 PM)
 * @author schaff
 * @version $Revision: 1.0 $
 */
public class SimpleNameScope extends AbstractNameScope {
	private NameScope parent = null;
	private ArrayList<NameScope> children = new ArrayList<NameScope>();
	private String name = null;
	private ScopedSymbolTable scopedSymbolTable = null;
/**
 * SimpleNameScope constructor comment.
 * @param argName java.lang.String
 * @throws IllegalArgumentException if argName==null or argName.length()<=1.
 */
public SimpleNameScope(String argName) {
	super();
	if (argName==null || argName.length()<=0){
		throw new IllegalArgumentException("NameScope name must be specified");
	}
	this.name = argName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 11:04:49 AM)
 * @param childNameScope NameScope
 */
public void addChild(NameScope childNameScope) {
	if (childNameScope == null){
		throw new IllegalArgumentException("AbstractNameScope.addChild(): nameScope cannot be null");
	}
	for (int i = 0; i < children.size(); i++){
		if (children.get(i).equals(childNameScope)){
			return;
		}
	}
	
	children.add(childNameScope);
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 11:04:49 AM)
 * @return cbit.vcell.parser.SymbolTable[]
 * @see org.vcell.expression.NameScope#getChildren()
 */
public org.vcell.expression.NameScope[] getChildren() {
	return (NameScope[])children.toArray();
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 9:04:26 AM)
 * @return java.lang.String
 * @see org.vcell.expression.NameScope#getName()
 */
public String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 3:19:19 PM)
 * @return cbit.vcell.parser.AbstractNameScope
 * @see org.vcell.expression.NameScope#getParent()
 */
public NameScope getParent() {
	return parent;
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 9:11:20 AM)
 * @return cbit.vcell.parser.ScopedSymbolTable
 * @see org.vcell.expression.NameScope#getScopedSymbolTable()
 */
public ScopedSymbolTable getScopedSymbolTable() {
	return scopedSymbolTable;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 11:04:49 AM)
 * @param childNameScope NameScope
 */
public void removeChild(NameScope childNameScope) {
	if (childNameScope == null){
		throw new IllegalArgumentException("AbstractNameScope.removeChild(): nameScope cannot be null");
	}
	if (!children.contains(childNameScope)){
		children.remove(childNameScope);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 11:04:49 AM)
 * @param newChildren cbit.vcell.parser.SymbolTable[]
 */
public void setChildren(org.vcell.expression.NameScope[] newChildren) {
	children.clear();
	children.addAll(Arrays.asList(newChildren));
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
