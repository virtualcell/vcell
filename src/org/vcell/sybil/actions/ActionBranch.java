package org.vcell.sybil.actions;

/*   BaseAction  --- by Oliver Ruebenacker, UCHC --- May 2007 to January 2009
 *   Actions to be integrated with the ActionBase tree
 */

import org.vcell.sybil.util.enumerations.SmartEnum;

public interface ActionBranch {

	SmartEnum<BaseAction> actions();
	SmartEnum<ActionBranch> branches();

}
