package cbit.vcell.numericstest.gui;

import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import javax.swing.JTable;

/**
 * Insert the type's description here.
 * Creation date: (6/21/2002 1:47:40 PM)
 * @author: John Wagner
 */
public class SolutionTemplateTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
	private cbit.vcell.numericstest.SolutionTemplate fieldSolutionTemplate = null;
/**
 * SpeciesWizardTableCellRenderer constructor comment.
 */
public SolutionTemplateTableCellRenderer() {
	super();
}
/**
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
public cbit.vcell.numericstest.SolutionTemplate getSolutionTemplate() {
	return fieldSolutionTemplate;
}

// Comment

public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
	super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

	return this;
}
/**
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setSolutionTemplate(cbit.vcell.numericstest.SolutionTemplate solnTemplate) {
	cbit.vcell.numericstest.SolutionTemplate oldValue = fieldSolutionTemplate;
	fieldSolutionTemplate = solnTemplate;
	firePropertyChange("solutionTemplate", oldValue, solnTemplate);
}
}
