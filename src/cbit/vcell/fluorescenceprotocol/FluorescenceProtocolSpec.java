package cbit.vcell.fluorescenceprotocol;

import cbit.vcell.parser.Expression;
import java.util.Vector;
import cbit.vcell.model.SpeciesContext;


/**
 * Insert the type's description here.
 * Creation date: (3/20/2006 1:37:11 PM)
 * @author: Anuradha Lakshminarayana
 */
public class FluorescenceProtocolSpec {
	private cbit.vcell.model.Model fieldInputModel = null;
	private SpeciesContext[] selectedSpeciesContexts = null;
	private int fluorescenceType = -1;

	public static final int FLUORESCENT_INDICATOR_TYPE = 1;
	public static final int FLUORESCENT_LABEL_TYPE = 2;
/**
 * VCellBNGProtocolSpec constructor comment.
 */
public FluorescenceProtocolSpec(cbit.vcell.model.Model argModel, SpeciesContext[] argSpContexts, int argType) {
	super();
	fieldInputModel = argModel;
	selectedSpeciesContexts = argSpContexts;
	fluorescenceType = argType;
}
/**
 * Gets the inputModel property (cbit.vcell.model.Model) value.
 * @return The inputModel property value.
 * @see #setInputModel
 */
public int getFluorescenceType() {
	if (fluorescenceType != FLUORESCENT_INDICATOR_TYPE || fluorescenceType != FLUORESCENT_LABEL_TYPE) {
		throw new RuntimeException("Type doesn't match pre-defined options. You might want to re-set the type of fluorescence reaction.");
	}
	return fluorescenceType;
}
/**
 * Gets the inputModel property (cbit.vcell.model.Model) value.
 * @return The inputModel property value.
 * @see #setInputModel
 */
public cbit.vcell.model.Model getInputModel() {
	return fieldInputModel;
}
/**
 * Gets the inputModel property (cbit.vcell.model.Model) value.
 * @return The inputModel property value.
 * @see #setInputModel
 */
public SpeciesContext[] getSelectedSpeciesContexts() {
	return selectedSpeciesContexts;
}
/**
 * Sets the inputModel property (cbit.vcell.model.Model) value.
 * @param inputModel The new value for the property.
 * @see #getInputModel
 */
public void setInputModel(cbit.vcell.model.Model inputModel) {
	fieldInputModel = inputModel;
}
/**
 * Sets the inputModel property (cbit.vcell.model.Model) value.
 * @param inputModel The new value for the property.
 * @see #getInputModel
 */
public void setSelectedSpeciesContexts(SpeciesContext[] selectedSCs) {
	selectedSpeciesContexts = selectedSCs;
}
}
