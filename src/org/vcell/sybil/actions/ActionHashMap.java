package org.vcell.sybil.actions;

/*   ActionHashMap  --- by Oliver Ruebenacker, UCHC --- July 2007 to January 2009
 *   Maps action classes to objects
 */

import java.util.HashMap;
import java.util.Set;

import org.vcell.sybil.util.enumerations.SmartEnum;


public class ActionHashMap 
extends HashMap<Class<? extends ActionBranch>, ActionBranch> implements ActionMap {

	private static final long serialVersionUID = -989519266941865083L;

	public ActionBranch simplePut(ActionBranch newActionBranch) {
		Class<? extends ActionBranch> class1 = 
			newActionBranch.getClass();
		return put(class1, newActionBranch);
	}

	public ActionBranch put(ActionBranch newActionBranch) {
		SmartEnum<ActionBranch> branches = newActionBranch.branches();
		while(branches.hasMoreElements()) { simplePut(branches.nextElement()); }
		return newActionBranch;
	}

	public void putAll(Set<ActionBranch> newActionLists) {
		for(ActionBranch list : newActionLists) { put(list); }
	}
	
	public SmartEnum<BaseAction> actions(Class<? extends ActionBranch> listClass) { 
		return get(listClass).actions(); 
	}



}
