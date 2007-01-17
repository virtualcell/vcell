package cbit.vcell.bionetgen;
/**
 * Insert the type's description here.
 * Creation date: (9/1/2006 2:18:54 PM)
 * @author: Anuradha Lakshminarayana
 */
public class BNGSpeciesComponent {
	private String[] stateNames = null;
	private String currentState = null;
	private String componentName = null;

/**
 * BNGSpeciesComponent constructor comment.
 */
public BNGSpeciesComponent(String argComponentName, String argCurrentState, String[] argStateNames) {
	super();
	componentName = argComponentName;
	if (argCurrentState != null) {
		currentState = argCurrentState;
	}
	if (argStateNames != null && argStateNames.length > 0) {
		stateNames = argStateNames;
	}
}


/**
 * BNGSpeciesComponent constructor comment.
 */
public String getComponentName() {
	return componentName;
}


/**
 * BNGSpeciesComponent constructor comment.
 */
public String getCurrentState() {
	return currentState;
}


/**
 * BNGSpeciesComponent constructor comment.
 */
public int getNumStates() {
	return stateNames.length;
}


/**
 * BNGSpeciesComponent constructor comment.
 */
public String[] getStateNames() {
	return stateNames;
}
}