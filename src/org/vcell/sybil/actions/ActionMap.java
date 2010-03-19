package org.vcell.sybil.actions;

/*   ActionMap  --- by Oliver Ruebenacker, UCHC --- July 2007 to January 2009
 *   Maps classes to instances of actions
 */

import java.util.Map;
import java.util.Set;

import org.vcell.sybil.util.enumerations.SmartEnum;

public interface ActionMap 
extends Map<Class<? extends ActionBranch>, ActionBranch> {

	public ActionBranch put(ActionBranch newBranch);
	public void putAll(Set<ActionBranch> newBranchSet);
	public SmartEnum<BaseAction> actions(Class<? extends ActionBranch> listClass);

	
}
