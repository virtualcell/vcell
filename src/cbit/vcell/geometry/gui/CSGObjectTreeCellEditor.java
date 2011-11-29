package cbit.vcell.geometry.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;

import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.geometry.CSGNode;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.CSGRotation;
import cbit.vcell.geometry.CSGScale;
import cbit.vcell.geometry.CSGTranslation;
import cbit.vcell.render.Vect3d;

@SuppressWarnings("serial")
public class CSGObjectTreeCellEditor extends DefaultTreeCellEditor {
	
	private class Vect3dCellEditor extends DefaultCellEditor {

		private JTextField textField;
		public Vect3dCellEditor(JTextField textField) {
			super(textField);
			this.textField = (JTextField) editorComponent;
		}

		@Override
		public Component getTreeCellEditorComponent(JTree tree,
				Object value, boolean isSelected, boolean expanded,
				boolean leaf, int row) {
			if (value instanceof Vect3d) {
				String text = CSGObjectPropertiesPanel.vect3dToString((Vect3d) value);
				textField.setText(text);
			}
			return textField;
		}

		@Override
		public Object getCellEditorValue() {
			return CSGObjectPropertiesPanel.stringToVect3d(textField.getText());
		}			
	}

	private class RotationCellEditor extends AbstractCellEditor implements TreeCellEditor {
		private JPanel rotationPanel = null;
		private JTextField radianTextField;
		private JTextField axisTextField;
		private JButton okButton; 
		private CSGRotation csgRotation;
		
		public RotationCellEditor() {
		}

		public Object getCellEditorValue() {
			double radian = Double.parseDouble(radianTextField.getText());
			Vect3d axis = CSGObjectPropertiesPanel.stringToVect3d(axisTextField.getText());
			csgRotation.setRotationRadians(radian);
			csgRotation.setAxis(axis);
			return csgRotation;
		}

		public Component getTreeCellEditorComponent(JTree tree,
				Object value, boolean isSelected, boolean expanded,
				boolean leaf, int row) {
			if (value instanceof BioModelNode) {
				Object userObject = ((BioModelNode) value).getUserObject();
				if (userObject instanceof CSGRotation) {
					createRotationPanel();
					csgRotation = (CSGRotation)userObject;
					radianTextField.setText(csgRotation.getRotationRadians() + "");
					radianTextField.setCaretPosition(0);
					axisTextField.setText(CSGObjectPropertiesPanel.vect3dToString(csgRotation.getAxis()));
					axisTextField.setCaretPosition(0);
				}
			}
			return rotationPanel;
		}
		
		private void createRotationPanel() {
			if (rotationPanel == null) {
				rotationPanel = new JPanel();
				rotationPanel.setPreferredSize(new Dimension(300, 50));
				rotationPanel.setBackground(Color.white);
				rotationPanel.setLayout(new GridBagLayout());
				
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = 0; 
				gbc.gridy = 0;
				JLabel label = new JLabel("Radian");
				rotationPanel.add(label, gbc);
				
				gbc = new GridBagConstraints();
				gbc.gridx = 1; 
				gbc.gridy = 0;
				gbc.weightx = 1.0;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.insets = new Insets(0, 2, 2, 4);
				radianTextField = new JTextField(10);
				rotationPanel.add(radianTextField, gbc);
				
				gbc = new GridBagConstraints();
				gbc.gridx = 2; 
				gbc.gridy = 0;
				label = new JLabel("Axis");
				rotationPanel.add(label, gbc);
				
				gbc = new GridBagConstraints();
				gbc.gridx = 3; 
				gbc.gridy = 0;
				gbc.weightx = 1.0;
				gbc.insets = new Insets(0, 2, 2, 0);
				gbc.fill = GridBagConstraints.HORIZONTAL;
				axisTextField = new JTextField(10);
				rotationPanel.add(axisTextField, gbc);
				
				gbc = new GridBagConstraints();
				gbc.gridx = 4; 
				gbc.gridy = 0;
				gbc.insets = new Insets(0, 2, 4, 0);
				okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						if (e.getSource() == okButton) {
							fireEditingStopped();
						}
					}
					
				});
				Border border = BorderFactory.createEmptyBorder(4, 4, 4, 4);
				okButton.setBorder(border);
				rotationPanel.add(okButton, gbc);
			}
		}
	}
	
	private RotationCellEditor rotationCellEditor;
	private Vect3dCellEditor vect3dCellEditor;
	private DefaultCellEditor defaultCellEditor;
	private boolean bRenaming = false;
	public CSGObjectTreeCellEditor(JTree tree) {
		super(tree, new DefaultTreeCellRenderer(), new DefaultCellEditor(new JTextField()));
		defaultCellEditor = (DefaultCellEditor) realEditor;
	}		

	private RotationCellEditor getRotationCellEditor() {
		if (rotationCellEditor == null) {
			rotationCellEditor = new RotationCellEditor();
			for (CellEditorListener listener : defaultCellEditor.getCellEditorListeners()) {
				rotationCellEditor.addCellEditorListener(listener);
			}
		}
		return rotationCellEditor;
	}
	
	private Vect3dCellEditor getVect3dCellEditor() {
		if (vect3dCellEditor == null) {
			vect3dCellEditor = new Vect3dCellEditor(new JTextField());
			for (CellEditorListener listener : defaultCellEditor.getCellEditorListeners()) {
				vect3dCellEditor.addCellEditorListener(listener);
			}
		}
		return vect3dCellEditor;
	}
	
	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
		if (!(value instanceof BioModelNode)) {
			return null;
		}
		Object userObject = ((BioModelNode) value).getUserObject();
		CSGObjectTreeCellRenderer.CSGNodeLabel csgNodeLabel = new CSGObjectTreeCellRenderer.CSGNodeLabel();
		CSGObjectTreeCellRenderer.getCSGNodeLabel(userObject, csgNodeLabel);
		renderer.setOpenIcon(csgNodeLabel.icon);
		renderer.setClosedIcon(csgNodeLabel.icon);
		renderer.setLeafIcon(csgNodeLabel.icon);
		Component component = null;			
		if (bRenaming) {
			realEditor = defaultCellEditor;
			component = super.getTreeCellEditorComponent(tree, value, isSelected, expanded,	leaf, row);				
			if (editingComponent instanceof JTextField) {
				String text = null;
				JTextField textField = (JTextField)editingComponent;
				if (userObject instanceof CSGObject) {
					text = ((CSGObject) userObject).getName();
				} else if (userObject instanceof CSGNode) {
					text = ((CSGNode) userObject).getName();
				}
				textField.setText(text);
			}
		} else {
			if (userObject instanceof CSGScale || userObject instanceof CSGTranslation) {
				realEditor = getVect3dCellEditor();					
				Vect3d vect3d = null;
				if (userObject instanceof CSGScale) {
					vect3d = ((CSGScale) userObject).getScale();
				} else if (userObject instanceof CSGTranslation) {
					vect3d = ((CSGTranslation) userObject).getTranslation();
				}
				component = super.getTreeCellEditorComponent(tree, vect3d, isSelected, expanded,	leaf, row);						
			} else if (userObject instanceof CSGRotation) {
				realEditor = getRotationCellEditor();
				component = super.getTreeCellEditorComponent(tree, value, isSelected, expanded,	leaf, row);					
			}
		}
		return component;
	}

	public final void setRenaming(boolean bRenaming) {
		this.bRenaming = bRenaming;
	}
}