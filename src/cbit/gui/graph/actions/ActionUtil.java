/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph.actions;

/*  Useful methods for actions
 *  September 2010
 */

import java.util.Collection;
import javax.swing.Action;


public class ActionUtil {

	public static String getActionCommand(Action action) {
		return (String) action.getValue(Action.ACTION_COMMAND_KEY);
	}
	
	public static <A extends Action> A getAction(Collection<A> actions, String actionCommand) {
		A selectedAction = null;
		for(A action : actions) {
			if(actionCommand.equals(getActionCommand(action))) {
				selectedAction = action;
			}
		}
		return selectedAction;
	}
	
}
