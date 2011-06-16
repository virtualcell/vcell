/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.actions;

/*   BaseAction  --- by Oliver Ruebenacker, UCHC --- May 2007 to January 2009
 *   Actions to be integrated with the ActionBase tree
 */

import org.vcell.sybil.util.enumerations.SmartEnum;

public interface ActionBranch {

	SmartEnum<BaseAction> actions();
	SmartEnum<ActionBranch> branches();

}
