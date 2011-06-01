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
