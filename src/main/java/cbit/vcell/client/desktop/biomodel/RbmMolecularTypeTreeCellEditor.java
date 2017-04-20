package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionRule.ReactionRuleParticipantType;
import cbit.vcell.model.SpeciesContext;
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
