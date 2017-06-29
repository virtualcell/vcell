package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularType;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.desktop.BioModelNode;

public class RbmMolecularTypeTreeCellEditor extends DefaultTreeCellEditor {

	private DefaultCellEditor defaultCellEditor;
	
	public RbmMolecularTypeTreeCellEditor(JTree tree) {
		super(tree, new DefaultTreeCellRenderer(), new DefaultCellEditor(new JTextField()));
		defaultCellEditor = (DefaultCellEditor) realEditor;
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {	
		Component component = null;
		realEditor = defaultCellEditor;
		if (value instanceof BioModelNode) {
			BioModelNode node = (BioModelNode) value;
			Object userObject = node.getUserObject();
			String text = null;
			Icon icon = null;
			if (userObject instanceof MolecularType) {
				text = ((MolecularType) userObject).getName();
				icon = VCellIcons.rbmMolecularTypeIcon;
			} else if (userObject instanceof MolecularComponent) {
				BioModelNode parentNode = (BioModelNode) node.getParent();
				Object parentObject = parentNode == null ? null : parentNode.getUserObject();
				icon = VCellIcons.rbmComponentErrorIcon;
				text = ((MolecularComponent) userObject).getName();
			} else if (userObject instanceof ComponentStateDefinition) {
				text = ((ComponentStateDefinition) userObject).getName();
				icon = VCellIcons.rbmComponentStateIcon;
			} else {
				System.out.println("unexpected thing here " + userObject);
			}
			renderer.setOpenIcon(icon);
			renderer.setClosedIcon(icon);
			renderer.setLeafIcon(icon);
			component = super.getTreeCellEditorComponent(tree, value,
						isSelected, expanded, leaf, row);
			if (editingComponent instanceof JTextField) {
				JTextField textField = (JTextField) editingComponent;
				textField.setText(text);
			}
		}
		return component;
	}
}
