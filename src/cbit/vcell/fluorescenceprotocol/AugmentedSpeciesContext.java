package cbit.vcell.fluorescenceprotocol;

/**
 * Insert the type's description here.
 * Creation date: (9/7/2006 3:53:54 PM)
 * @author: Anuradha Lakshminarayana
 */

import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Species;
 
public class AugmentedSpeciesContext {
	private SpeciesContext fieldSpeciesContext = null;
	private SpeciesContext fieldIndicatorSpeciesContext = null;	// optional - will have a value if protocol is fluorescent indicator protocol.
	private boolean bFluorescence = false;	// true => fluoresced species; false => bleached => no fluorescence
	private boolean bSelected = false; 		// indicates whether speciesContext is selected for fluorescence or not.
/**
 * AugmentedSpeciesContext constructor comment.
 */
public AugmentedSpeciesContext(SpeciesContext argSpContext, SpeciesContext argIndicator, boolean argBleached, boolean argSelected) {
	super();
	if (argSpContext == null) {
		throw new RuntimeException("SpeciesContext cannot be null.");
	}
	fieldSpeciesContext = argSpContext;
	fieldIndicatorSpeciesContext = argIndicator;
	bFluorescence = argBleached;
	bSelected = argSelected;
}
/**
 * AugmentedSpeciesContext constructor comment.
 */
public SpeciesContext getAugmentedSpeciesContext() {
	SpeciesContext newSpeciesContext = null;
	if (!isSelected()) {
		throw new RuntimeException("SpeciesContext : " + fieldSpeciesContext.getName() + "is not selected; hence it doesn't have multiple states ");
	} else {
		String newName = null;
		if (isFluorescenced()) {
			newName = getSpeciesContext().getSpecies().getCommonName() + "_Fl";
		} else {
			newName = getSpeciesContext().getSpecies().getCommonName() + "_Bl";
		}
		newSpeciesContext = new SpeciesContext(new Species(newName, newName), getSpeciesContext().getStructure());
	}
	return newSpeciesContext;
}
/**
 * AugmentedSpeciesContext constructor comment.
 */
public SpeciesContext getIndicatorSpeciesContext() {
	return fieldIndicatorSpeciesContext;
}
/**
 * AugmentedSpeciesContext constructor comment.
 */
public SpeciesContext getSpeciesContext() {
	return fieldSpeciesContext;
}
/**
 * AugmentedSpeciesContext constructor comment.
 */
public boolean isFluorescenced() {
	return bFluorescence;
}
/**
 * AugmentedSpeciesContext constructor comment.
 */
public boolean isSelected() {
	return bSelected;
}
}
