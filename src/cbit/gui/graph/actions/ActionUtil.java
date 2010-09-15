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
