package org.vcell.sybil.actions;

/*   ActionTree  --- by Oliver Ruebenacker, UCHC --- July 2007 to January 2009
 *   A hierarchical structure of actions
 */

import java.util.Vector;

import org.vcell.sybil.util.enumerations.NestedEnum;
import org.vcell.sybil.util.enumerations.OneElementEnum;
import org.vcell.sybil.util.enumerations.SmartEnum;


public class ActionTree 
extends Vector<ActionBranch> implements ActionBranch {

	private static final long serialVersionUID = 1406937808961945103L;

	public SmartEnum<BaseAction> actions() {
		NestedEnum<BaseAction> list = new NestedEnum<BaseAction>();
		for(ActionBranch subList : this) { list.add(subList.actions()); }
		return list;
	}

	public SmartEnum<ActionBranch> branches() {
		NestedEnum<ActionBranch> list = new NestedEnum<ActionBranch>();
		list.add(new OneElementEnum<ActionBranch>(this));
		for(ActionBranch subList : this) { list.add(subList.branches()); }
		return list;
	}

}
