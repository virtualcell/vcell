package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;

import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.solver.Simulation;

public class DocumentEditorTreeCellEditor extends DefaultTreeCellEditor {

	public DocumentEditorTreeCellEditor(JTree tree) {
		super(tree, null, new DefaultCellEditor(new JTextField()));
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
		Component component = super.getTreeCellEditorComponent(tree, value, isSelected, expanded,
				leaf, row);
		if (editingComponent instanceof JTextField) {
			String text = null;
			JTextField textField = (JTextField)editingComponent;
			if (value instanceof BioModelNode) {
				Object userObject = ((BioModelNode) value).getUserObject();
				if (userObject instanceof ReactionStep) {
					text = ((ReactionStep) userObject).getName();
				} else if (userObject instanceof Structure) {
					text = ((Structure) userObject).getName();
				} else if (userObject instanceof SpeciesContext) {
					text = ((SpeciesContext) userObject).getName();
				} else if (userObject instanceof ModelParameter) {
					text = ((ModelParameter) userObject).getName();
				} else if (userObject instanceof SimulationContext) {
					text = ((SimulationContext) userObject).getName();
				} else if (userObject instanceof BioEvent) {
					text = ((BioEvent) userObject).getName();
				} else if (userObject instanceof Simulation) {
					text = ((Simulation) userObject).getName();
				}
				textField.setText(text);
			}
		}
		return component;
	}
}
