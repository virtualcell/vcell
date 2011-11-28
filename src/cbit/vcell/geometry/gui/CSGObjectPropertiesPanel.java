package cbit.vcell.geometry.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.util.gui.ColorIcon;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.VCellIcons;

import cbit.image.DisplayAdapterService;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.geometry.CSGHomogeneousTransformation;
import cbit.vcell.geometry.CSGNode;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.CSGPrimitive;
import cbit.vcell.geometry.CSGPrimitive.PrimitiveType;
import cbit.vcell.geometry.CSGRotation;
import cbit.vcell.geometry.CSGScale;
import cbit.vcell.geometry.CSGSetOperator;
import cbit.vcell.geometry.CSGSetOperator.OperatorType;
import cbit.vcell.geometry.CSGTransformation;
import cbit.vcell.geometry.CSGTransformation.TransformationType;
import cbit.vcell.geometry.CSGTranslation;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.render.Vect3d;

import com.ibm.icu.util.StringTokenizer;

@SuppressWarnings("serial")
public class CSGObjectPropertiesPanel extends DocumentEditorSubPanel {
	private class InternalEventHandler implements PropertyChangeListener, ActionListener, MouseListener, TreeSelectionListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == csgObject) {
				if (evt.getPropertyName().equals(GuiConstants.PROPERTY_NAME_NAME)) {
					titleLabel.setText("Properties for " + csgObject.getName());
				} else if (evt.getPropertyName().equals(CSGObject.PROPERTY_NAME_ROOT)) {
					csgObjectTreeModel.populateTree();
				}
			}
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == getDeleteMenuItem()) {
				deleteNode();
			} else if (source == getEditMenuItem()) {
				csgObjectTree.startEditingAtPath(csgObjectTree.getSelectionPath());
			} else if (source instanceof JMenuItem) {
				menuItemClicked(source);
			}			
		}

		public void mouseClicked(MouseEvent e) {			
		}

		public void mousePressed(MouseEvent e) {
			if (!e.isConsumed() && e.getSource() == csgObjectTree) {
				showPopupMenu(e);
			}
		}
		public void mouseReleased(MouseEvent e) {
			if (!e.isConsumed() && e.getSource() == csgObjectTree) {
				showPopupMenu(e);
			}			
		}
		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void valueChanged(TreeSelectionEvent e) {
		}
		
	}
	private class CSGObjectTreeCellEditor extends DefaultTreeCellEditor {

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
				Vect3d axis = stringToVect3d(axisTextField.getText());
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
						axisTextField.setText(vect3dToString(csgRotation.getAxis()));
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
		private DefaultCellEditor defaultCellEditor;
		public CSGObjectTreeCellEditor(JTree tree) {
			super(tree, null, new DefaultCellEditor(new JTextField()));
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
		@Override
		public Component getTreeCellEditorComponent(JTree tree, Object value,
				boolean isSelected, boolean expanded, boolean leaf, int row) {
			if (!(value instanceof BioModelNode)) {
				return null;
			}
			Object userObject = ((BioModelNode) value).getUserObject();
			if (!(userObject instanceof CSGTransformation)) {
				return null;
			}
			Component component = null;
			if (userObject instanceof CSGScale || userObject instanceof CSGTranslation) {
				realEditor = defaultCellEditor;
				component = super.getTreeCellEditorComponent(tree, value, isSelected, expanded,	leaf, row);
				if (editingComponent instanceof JTextField) {
					String text = null;
					JTextField textField = (JTextField)editingComponent;	
					if (userObject instanceof CSGScale) {
						text = vect3dToString(((CSGScale) userObject).getScale());
					} else if (userObject instanceof CSGTranslation) {
						text = vect3dToString(((CSGTranslation) userObject).getTranslation());
					}
					textField.setText(text);
					return component;
				}
			} else if (userObject instanceof CSGRotation) {
				realEditor = getRotationCellEditor();
				component = super.getTreeCellEditorComponent(tree, value, isSelected, expanded,	leaf, row);
			}
			return component;
		}
	}
	
	public class CSGObjectTreeCellRenderer extends DefaultTreeCellRenderer {
		private class CSGNodeLabel {
			String text;
			Icon icon;
		}
		
		private CSGNodeLabel getCSGNodeDescription(CSGNode csgNode, CSGNodeLabel csgNodeLabel) {
			csgNodeLabel.text = csgNode.getName();
			if (csgNode instanceof CSGPrimitive) {
				CSGPrimitive csgPrimitive = (CSGPrimitive)csgNode;
				switch (csgPrimitive.getType()) {
				case CONE:
					csgNodeLabel.icon = VCellIcons.csgConeIcon;
					break;
				case CUBE:
					csgNodeLabel.icon = VCellIcons.csgCubeIcon;
					break;
				case CYLINDER:
					csgNodeLabel.icon = VCellIcons.csgCylinderIcon;
					break;
				case SPHERE:
					csgNodeLabel.icon = VCellIcons.csgSphereIcon;
					break;
				}
				return csgNodeLabel;
			}
			if (csgNode instanceof CSGSetOperator) {
				CSGSetOperator csgSetOperator = (CSGSetOperator)csgNode;
				switch (csgSetOperator.getOpType()) {
				case DIFFERENCE:
					csgNodeLabel.icon = VCellIcons.csgSetDifferenceIcon;				
					break;
				case INTERSECTION:
					csgNodeLabel.icon = VCellIcons.csgSetIntersectionIcon;				
					break;
				case UNION:
					csgNodeLabel.icon = VCellIcons.csgSetUnionIcon;
					break;
				}
			}
			if (csgNode instanceof CSGTransformation) {
				if (csgNode instanceof CSGRotation) {
					CSGRotation csgRotation = (CSGRotation)csgNode;
					Vect3d axis = csgRotation.getAxis();
					double radius = csgRotation.getRotationRadians();
					csgNodeLabel.text += ", radian=" + radius + ", axis=" + getVect3dDescription(axis);
					csgNodeLabel.icon = VCellIcons.csgRotationIcon;
				} else if (csgNode instanceof CSGTranslation) {
					CSGTranslation csgTranslation = (CSGTranslation)csgNode;
					Vect3d translation = csgTranslation.getTranslation();
					csgNodeLabel.text += ", Translation=" + getVect3dDescription(translation);
					csgNodeLabel.icon = VCellIcons.csgTranslationIcon;				
				} else if (csgNode instanceof CSGScale) {
					CSGScale csgScale = (CSGScale) csgNode;
					Vect3d scale = csgScale.getScale();
					csgNodeLabel.text += ", Scale=" + getVect3dDescription(scale);				
					csgNodeLabel.icon = VCellIcons.csgScaleIcon;
				} else if (csgNode instanceof CSGHomogeneousTransformation){
					csgNodeLabel.icon = null;
				}
			}
			return null;
		}
		
		private int[] colormap = DisplayAdapterService.createContrastColorModel();
		public CSGObjectTreeCellRenderer() {
			super();
			setBorder(new EmptyBorder(0, 2, 0, 0));
		}
		public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			if (value instanceof BioModelNode) {
				BioModelNode node = (BioModelNode)value;
				Object userObj = node.getUserObject();
				CSGNodeLabel csgNodeLabel = new CSGNodeLabel();
				if (userObj instanceof CSGObject) {
					csgNodeLabel.text = ((CSGObject) userObj).getName();		
					java.awt.Color handleColor = new java.awt.Color(colormap[((CSGObject) userObj).getHandle()]);
					csgNodeLabel.icon = new ColorIcon(15,15,handleColor);
				} else if (userObj instanceof CSGNode) {
					getCSGNodeDescription((CSGNode) userObj, csgNodeLabel);
				}
				setText(csgNodeLabel.text);
				setIcon(csgNodeLabel.icon);
			}
			return this;
		}
	}
	
	private class CSGObjectTreeModel extends DefaultTreeModel implements PropertyChangeListener {
		BioModelNode rootNode;
		
		CSGObjectTreeModel() {
			super(new BioModelNode("Constructed Solid Geometry",true),true);
			rootNode = (BioModelNode)root;
		}
		
		private BioModelNode populateNode(CSGNode csgNode) {
			BioModelNode returnNode = null;
			if (csgNode instanceof CSGPrimitive) {
				returnNode = new BioModelNode(csgNode,false);
			} else if (csgNode instanceof CSGSetOperator) {
				returnNode = new BioModelNode(csgNode, true);
				for (CSGNode child : ((CSGSetOperator) csgNode).getChildren()) {
					BioModelNode childNode = populateNode(child);
					returnNode.add(childNode);					
				}
			} else if (csgNode instanceof CSGTransformation) {
				returnNode = new BioModelNode(csgNode, true);
				CSGNode child = ((CSGTransformation) csgNode).getChild();
				BioModelNode childNode = populateNode(child);
				if (child != null) {
					returnNode.add(childNode);
				}
			}
			return returnNode;
		}
		
		void populateTree() {
			rootNode.setUserObject(csgObject);
			rootNode.removeAllChildren();
			if (csgObject != null) {
				CSGNode rootCSGNode = csgObject.getRoot();
				BioModelNode csgTreeNode = populateNode(rootCSGNode);
				if (csgTreeNode != null) {
					rootNode.add(csgTreeNode);			
				}
			}
			nodeStructureChanged(rootNode);
			GuiUtils.treeExpandAll(csgObjectTree, rootNode, true);
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == csgObject && evt.getPropertyName().equals(GuiConstants.PROPERTY_NAME_NAME)) {
				nodeChanged(rootNode);
			}
		}
		
		private void select(Object objectToBeSelected) {			
			select(rootNode, objectToBeSelected);
		}
		
		private boolean select(BioModelNode node, Object objectToBeSelected) {
			if (objectToBeSelected instanceof CSGObject) {
				csgObjectTree.setSelectionPath(new TreePath(new Object[]{rootNode}));
				return true;
			}
			
			if (!(objectToBeSelected instanceof CSGNode)) {
				return false;
			}
			
			Object object = node.getUserObject();
			if (object instanceof CSGNode) {
				if (((CSGNode) object).getName().equals(((CSGNode)objectToBeSelected).getName())) {
					csgObjectTree.setSelectionPath(new TreePath(node.getPath()));
					return true;
				}
			}
			for (int i = 0; i < node.getChildCount(); i ++) {
				TreeNode child = node.getChildAt(i);
				if (child instanceof BioModelNode) {
					if (select((BioModelNode) child, objectToBeSelected)) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
			Object obj = path.getLastPathComponent();
			if (obj == null || !(obj instanceof BioModelNode)) {
				return;
			}
			BioModelNode selectedNode = (BioModelNode) obj;
			Object userObject = selectedNode.getUserObject();
			try {
				if (newValue instanceof String) {
					String inputString = (String)newValue;
					if (inputString == null || inputString.length() == 0) {
						return;
					}
					if (userObject instanceof CSGScale || userObject instanceof CSGTranslation) {
						Vect3d vect3d = stringToVect3d(inputString);
						if (userObject instanceof CSGScale) {
							CSGScale csgScale = (CSGScale)userObject;
							csgScale.setScale(vect3d);
						} else if (userObject instanceof CSGTranslation) {
							CSGTranslation csgTranslation = (CSGTranslation)userObject;
							csgTranslation.setTranslation(vect3d);						
						}
						updateCSGObject((CSGNode)userObject);
					}
				} else if (newValue instanceof CSGRotation && userObject instanceof CSGRotation) {
					CSGRotation currCSGRotation = (CSGRotation)userObject;
					CSGRotation newCSGRotation = (CSGRotation)newValue;
					currCSGRotation.setAxis(newCSGRotation.getAxis());
					currCSGRotation.setRotationRadians(newCSGRotation.getRotationRadians());
					updateCSGObject((CSGNode)userObject);
				}
			} catch (Exception ex) {
				DialogUtils.showErrorDialog(CSGObjectPropertiesPanel.this, ex.getMessage());			
			}
		}
	}
	private JTree csgObjectTree = null;
	private CSGObjectTreeModel csgObjectTreeModel = null;
	private CSGObject csgObject;
	private JLabel titleLabel = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private JMenu addSetOperatorMenu = null;
	private JMenuItem[] addSetOperatorMenuItems;
	
	private JMenu addPrimitiveMenu = null;
	private JMenuItem[] addPrimitiveMenuItems;
	
	private JMenu addTransformationMenu = null; 
	private JMenuItem[] addTranformationMenuItems;
	
	private JPopupMenu popupMenu;
	private JMenu addMenu;
	private JMenu transformMenu;
	private JMenuItem[] tranformMenuItems;
	
	private JMenu applySetOperatorMenu;
	private JMenuItem[] applySetOperatorMenuItems;
	
	private JMenuItem deleteMenuItem;	
	private JMenuItem editMenuItem;
	
	private SimulationContext simulationContext;
	
	public CSGObjectPropertiesPanel() {
		super();
		initialize();
	}
	
	private void createCsgObjectTree() {
		csgObjectTreeModel = new CSGObjectTreeModel();
		csgObjectTree = new JTree(csgObjectTreeModel) {

			@Override
			public boolean isPathEditable(TreePath path) {
				Object object = path.getLastPathComponent();
				if (object instanceof BioModelNode) {
					return (((BioModelNode)object).getUserObject() instanceof CSGTransformation);
				}
				return false;
			}			
		};
		csgObjectTree.setEditable(true);
		csgObjectTree.setCellRenderer(new CSGObjectTreeCellRenderer());
		csgObjectTree.setCellEditor(new CSGObjectTreeCellEditor(csgObjectTree));
		csgObjectTree.addMouseListener(eventHandler);
		int rowHeight = csgObjectTree.getRowHeight();
		if(rowHeight < 10) { 
			rowHeight = 20; 
		}
		csgObjectTree.setRowHeight(rowHeight + 2);
		csgObjectTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		ToolTipManager.sharedInstance().registerComponent(csgObjectTree);
		csgObjectTree.addTreeSelectionListener(eventHandler);
		csgObjectTree.addMouseListener(eventHandler);
	}
	
	private void initialize() {
		createCsgObjectTree();
		
		setLayout(new GridBagLayout());
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4,4,4,4);
		titleLabel = new JLabel("Construct Solid Geometry");
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		add(titleLabel, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.fill = GridBagConstraints.BOTH;
		add(new JScrollPane(csgObjectTree), gbc);
	}
	
	private JMenu getAddMenu() {
		if (addMenu == null) {
			addMenu = new JMenu("Add");				
			getAddMenu().add(getAddPrimitiveMenu());
			getAddMenu().add(getAddTransformationMenu());
			getAddMenu().add(getAddSetOperatorMenu());
		}
		return addMenu;
	}
	
	private JMenuItem getEditMenuItem() {
		if (editMenuItem == null) {
			editMenuItem = new JMenuItem("Edit");
			editMenuItem.addActionListener(eventHandler);
		}
		return editMenuItem;
	}
	
	private JMenuItem getDeleteMenuItem() {
		if (deleteMenuItem == null) {
			deleteMenuItem = new JMenuItem("Delete");
			deleteMenuItem.addActionListener(eventHandler);
		}
		return deleteMenuItem;
	}
	
	private JMenu getAddTransformationMenu() {
		if (addTransformationMenu == null) {
			addTransformationMenu = new JMenu("Transformation");
			TransformationType[] values = CSGTransformation.TransformationType.values();
			int numTypes = values.length;
			addTranformationMenuItems = new JMenuItem[numTypes];
			for (int i = 0; i < numTypes; i ++) {
				addTranformationMenuItems[i] = new JMenuItem(values[i].name().toLowerCase());
				addTranformationMenuItems[i].addActionListener(eventHandler);
				addTransformationMenu.add(addTranformationMenuItems[i]);
			}
		}
		return addTransformationMenu;
	}
	
	private JMenu getTransformMenu() {
		if (transformMenu == null) {
			transformMenu = new JMenu("Transform");
			TransformationType[] values = CSGTransformation.TransformationType.values();
			int numTypes = values.length;
			tranformMenuItems = new JMenuItem[numTypes];
			for (int i = 0; i < numTypes; i ++) {
				tranformMenuItems[i] = new JMenuItem(values[i].name().toLowerCase());
				tranformMenuItems[i].addActionListener(eventHandler);
				transformMenu.add(tranformMenuItems[i]);
			}
		}
		return transformMenu;
	}
	
	private JMenu getApplySetOperatorMenu() {
		if (applySetOperatorMenu == null) {
			applySetOperatorMenu = new JMenu("Apply Set Operator");
			CSGSetOperator.OperatorType[] values = CSGSetOperator.OperatorType.values();
			int numTypes = values.length;
			applySetOperatorMenuItems = new JMenuItem[numTypes];
			for (int i = 0; i < numTypes; i ++) {
				applySetOperatorMenuItems[i] = new JMenuItem(values[i].name().toLowerCase());
				applySetOperatorMenuItems[i].addActionListener(eventHandler);
				applySetOperatorMenu.add(applySetOperatorMenuItems[i]);
			}
		}
		return applySetOperatorMenu;
	}
	
	private JMenu getAddPrimitiveMenu() {
		if (addPrimitiveMenu == null) {
			addPrimitiveMenu = new JMenu("Primitive Geometry");
			PrimitiveType[] values = CSGPrimitive.PrimitiveType.values();
			int numTypes = values.length;
			addPrimitiveMenuItems = new JMenuItem[numTypes];
			for (int i = 0; i < numTypes; i ++) {
				addPrimitiveMenuItems[i] = new JMenuItem(values[i].name().toLowerCase());
				addPrimitiveMenuItems[i].addActionListener(eventHandler);
				addPrimitiveMenu.add(addPrimitiveMenuItems[i]);
			}
		}
		return addPrimitiveMenu;
	}
	private JMenu getAddSetOperatorMenu() {
		if (addSetOperatorMenu == null) { 
			addSetOperatorMenu = new JMenu("Set Operator");
			OperatorType[] values = CSGSetOperator.OperatorType.values();
			int numTypes = values.length;
			addSetOperatorMenuItems = new JMenuItem[numTypes];
			for (int i = 0; i < numTypes; i ++) {
				addSetOperatorMenuItems[i] = new JMenuItem(values[i].name().toLowerCase());
				addSetOperatorMenuItems[i].addActionListener(eventHandler);
				addSetOperatorMenu.add(addSetOperatorMenuItems[i]);
			}
		}
		return addSetOperatorMenu;
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		CSGObject csgObject = null;
		if (selectedObjects.length == 1 && selectedObjects[0] instanceof CSGObject) {
			csgObject = (CSGObject) selectedObjects[0];
		}
		setCSGObject(csgObject);	
	}
	
	private void setCSGObject(CSGObject newValue) {
		if (csgObject == newValue) {
			return;
		}
		CSGObject oldValue = csgObject;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(eventHandler);
			oldValue.removePropertyChangeListener(csgObjectTreeModel);
		}
		if (newValue != null) {
			newValue.addPropertyChangeListener(eventHandler);
			newValue.addPropertyChangeListener(csgObjectTreeModel);
		}
		csgObject = newValue;
		csgObjectTreeModel.populateTree();
		if (csgObject != null) {
			titleLabel.setText("Properties for " + csgObject.getName());
		}
	}
	
	private void selectClickPath(MouseEvent e) {
		Point mousePoint = e.getPoint();
		TreePath clickPath = csgObjectTree.getPathForLocation(mousePoint.x, mousePoint.y);
	    if (clickPath == null) {
	    	return; 
	    }
		Object rightClickNode = clickPath.getLastPathComponent();
		if (rightClickNode == null || !(rightClickNode instanceof BioModelNode)) {
			return;
		}
		TreePath[] selectedPaths = csgObjectTree.getSelectionPaths();
		if (selectedPaths == null || selectedPaths.length == 0) {
			return;
		} 
		boolean bFound = false;
		for (TreePath tp : selectedPaths) {
			if (tp.equals(clickPath)) {
				bFound = true;
				break;
			}
		}
		if (!bFound) {
			csgObjectTree.setSelectionPath(clickPath);
		}
	}
	
	private void showPopupMenu(MouseEvent e){ 
		if (!e.isPopupTrigger()) {
			return;
		}
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();			
		}		
		if (popupMenu.isShowing()) {
			return;
		}
		selectClickPath(e);		
		
		TreePath[] selectedPaths = csgObjectTree.getSelectionPaths();
		boolean bShowPopup = true;
		boolean bTransform = false;
		boolean bApplySetOperator = false;
		boolean bAddPrimitive = false;
		boolean bAddTransformation = false;
		boolean bAddOperator = false;
		boolean bEdit = false;
		boolean bDelete = true;
		if (selectedPaths == null) {
			return;
		}
		for (TreePath tp : selectedPaths) {
			Object obj = tp.getLastPathComponent();
			if (obj == null || !(obj instanceof BioModelNode)) {
				continue;
			}
			
			BioModelNode selectedNode = (BioModelNode) obj;
			Object userObject = selectedNode.getUserObject();
			if (userObject == csgObject) {
				bDelete = false;
				if (csgObject.getRoot() == null) {
					bAddPrimitive = true;
					bAddOperator = true;
					bAddTransformation = true;
				}
			} else if (userObject instanceof CSGPrimitive) {
				bApplySetOperator = true;
				bTransform = true;
				bAddPrimitive = false;
				bAddOperator = false;
				bAddTransformation = false;
			} else if (userObject instanceof CSGSetOperator) {
				bApplySetOperator = true;
				bTransform = true;
				bAddPrimitive = true;
				bAddOperator = true;
				bAddTransformation = true;
			} else if (userObject instanceof CSGTransformation) {
				bApplySetOperator = true;
				bTransform = true;
				bEdit = true;
				if (((CSGTransformation) userObject).getChild() == null) {
					bAddPrimitive = true;
					bAddOperator = true;
					bAddTransformation = true;
				}
			} 
		}
		if (bShowPopup) {
			popupMenu.removeAll();
			if (bAddPrimitive || bAddTransformation || bAddOperator) {
				getAddPrimitiveMenu().setEnabled(bAddPrimitive);
				getAddTransformationMenu().setEnabled(bAddTransformation);
				getAddSetOperatorMenu().setEnabled(bAddOperator);
				popupMenu.add(getAddMenu());
			}
			if (bEdit) {
				if (popupMenu.getComponents().length > 0) {
					popupMenu.add(new JSeparator());
				}
				popupMenu.add(getEditMenuItem());
			}
			if (bDelete) {
				if (popupMenu.getComponents().length > 0 && !bEdit) {
					popupMenu.add(new JSeparator());
				}
				popupMenu.add(getDeleteMenuItem());
			}
			
			if (bTransform) {
				if (popupMenu.getComponents().length > 0) {
					popupMenu.add(new JSeparator());
				}
				popupMenu.add(getTransformMenu());
			}
			if (bApplySetOperator) {
				if (popupMenu.getComponents().length > 0 && !bTransform) {
					popupMenu.add(new JSeparator());
				}
				popupMenu.add(getApplySetOperatorMenu());
			}

			Point mousePoint = e.getPoint();
			popupMenu.show(csgObjectTree, mousePoint.x, mousePoint.y);
		}
	}
	
	public void setSimulationContext(SimulationContext newValue) {
		simulationContext = newValue;
	}
	
	private boolean addNode(CSGNode newCsgNode) {
		if (newCsgNode == null) {
			return false;
		}
		Object obj = csgObjectTree.getLastSelectedPathComponent();
		if (obj == null || !(obj instanceof BioModelNode)) {
			return false;
		}
		BioModelNode selectedNode = (BioModelNode) obj;
		Object selectedUserObject = selectedNode.getUserObject();

		if (selectedUserObject == csgObject) {
			csgObject.setRoot(newCsgNode);
			return true;
		} 
		if (selectedUserObject instanceof CSGSetOperator) {
			CSGSetOperator csgSetOperator = (CSGSetOperator) selectedUserObject;
			csgSetOperator.addChild(newCsgNode);
			return true;
		} 
		if (selectedUserObject instanceof CSGTransformation) {
			CSGTransformation csgTransformation = (CSGTransformation) selectedUserObject;
			csgTransformation.setChild(newCsgNode);
			return true;
		}
		
		return false;
	}
	
	private boolean transformOrApplySetOperator(CSGNode newCsgNode) {
		if (newCsgNode == null) {
			return false;
		}
		Object obj = csgObjectTree.getLastSelectedPathComponent();
		if (obj == null || !(obj instanceof BioModelNode)) {
			return false;
		}
		BioModelNode selectedNode = (BioModelNode) obj;
		Object selectedUserObject = selectedNode.getUserObject();
		
		if (!(selectedUserObject instanceof CSGNode)) {
			return false;
		}

		CSGNode selectedCSGNode = (CSGNode) selectedUserObject;
		TreeNode parentNode = selectedNode.getParent();
		if (parentNode == null || !(parentNode instanceof BioModelNode)) {
			return false;
		}
		Object parentObject = ((BioModelNode)parentNode).getUserObject();
		if (newCsgNode instanceof CSGTransformation) {
			CSGTransformation csgTransformation = (CSGTransformation)newCsgNode;
			csgTransformation.setChild(selectedCSGNode);
		} else if (newCsgNode instanceof CSGSetOperator) {
			CSGSetOperator csgSetOperator = (CSGSetOperator)newCsgNode;
			csgSetOperator.addChild(selectedCSGNode);
		}
		if (parentObject == csgObject) {
			csgObject.setRoot(newCsgNode);
		} else if (parentObject instanceof CSGSetOperator) {
			CSGSetOperator parentCSGSetOperator = (CSGSetOperator) parentObject;
			int index = parentCSGSetOperator.indexOf(selectedCSGNode);
			if (index >= 0) {
				parentCSGSetOperator.setChild(index, newCsgNode);
				return true;
			}
		} else if (parentObject instanceof CSGTransformation) {
			CSGTransformation parentCSGTransformation = (CSGTransformation) parentObject;
			parentCSGTransformation.setChild(newCsgNode);
			return true;
		}				
		
		return false;
	}
	
	private void deleteNode() {
		Object obj = csgObjectTree.getLastSelectedPathComponent();
		if (obj == null || !(obj instanceof BioModelNode)) {
			return;
		}
		BioModelNode selectedNode = (BioModelNode) obj;
		Object selectedUserObject = selectedNode.getUserObject();
		if (!(selectedUserObject instanceof CSGNode)){
			return;
		}
		
		CSGNode selectedCSGNode = (CSGNode) selectedUserObject;
		TreeNode parentNode = selectedNode.getParent();
		if (parentNode == null || !(parentNode instanceof BioModelNode)) {
			return;
		}
		Object parentObject = ((BioModelNode)parentNode).getUserObject();
		if (parentObject == csgObject) {
			csgObject.setRoot(null);
		} else if (parentObject instanceof CSGSetOperator) {
			CSGSetOperator csgSetOperator = (CSGSetOperator) parentObject;
			csgSetOperator.removeChild(selectedCSGNode);
		} else if (parentObject instanceof CSGTransformation) {
			CSGTransformation csgTransformation = (CSGTransformation)parentObject;
			csgTransformation.setChild(null);
		}
		updateCSGObject(parentObject);
	}

	private void menuItemClicked(Object source) {
		boolean bGeometryChanged = false;
		boolean bFoundClickedMenuItem = false;
		Object objectToBeSelected = null;
		if (!bFoundClickedMenuItem) {
			PrimitiveType[] values = CSGPrimitive.PrimitiveType.values();
			int numTypes = values.length;
			for (int i = 0; i < numTypes; i ++) {
				if (source == addPrimitiveMenuItems[i]) {
					bFoundClickedMenuItem = true;
					CSGPrimitive csgPrimitive = new CSGPrimitive(csgObject.getFreeName(values[i]), values[i]);
					objectToBeSelected = csgPrimitive;
					bGeometryChanged = addNode(csgPrimitive);
					break;
				}
			}
		}
		if (!bFoundClickedMenuItem) {
			OperatorType[] values = CSGSetOperator.OperatorType.values();
			int numTypes = values.length;
			for (int i = 0; i < numTypes; i ++) {
				if (source == addSetOperatorMenuItems[i]) {
					bFoundClickedMenuItem = true;
					CSGSetOperator csgSetOperator = new CSGSetOperator(csgObject.getFreeName(values[i]), values[i]);
					objectToBeSelected = csgSetOperator;
					bGeometryChanged = addNode(csgSetOperator);
					break;
				}
			}
		}
		if (!bFoundClickedMenuItem) {
			TransformationType[] values = CSGTransformation.TransformationType.values();
			int numTypes = values.length;
			for (int i = 0; i < numTypes; i ++) {
				if (source == addTranformationMenuItems[i]) {
					bFoundClickedMenuItem = true;
					CSGTransformation csgTransformation = createNewCSGTransformation(values[i]);
					objectToBeSelected = csgTransformation;
					bGeometryChanged = addNode(csgTransformation);
					break;
				}
			}
		}
		if (!bFoundClickedMenuItem) {
			TransformationType[] values = CSGTransformation.TransformationType.values();
			int numTypes = values.length;
			for (int i = 0; i < numTypes; i ++) {
				if (source == tranformMenuItems[i]) {
					bFoundClickedMenuItem = true;
					CSGTransformation csgTransformation = createNewCSGTransformation(values[i]);
					objectToBeSelected = csgTransformation;
					bGeometryChanged = transformOrApplySetOperator(csgTransformation);
					break;
				}
			}
		}
		if (!bFoundClickedMenuItem) {
			OperatorType[] values = CSGSetOperator.OperatorType.values();
			int numTypes = values.length;
			for (int i = 0; i < numTypes; i ++) {
				if (source == applySetOperatorMenuItems[i]) {
					bFoundClickedMenuItem = true;
					CSGSetOperator csgSetOperator = new CSGSetOperator(csgObject.getFreeName(values[i]), values[i]);
					objectToBeSelected = csgSetOperator;
					bGeometryChanged = transformOrApplySetOperator(csgSetOperator);
					break;
				}
			}
		}
		
		if (!bGeometryChanged) {
			return;
		}
		updateCSGObject(objectToBeSelected);
	}
	
	private CSGTransformation createNewCSGTransformation(TransformationType type) {
		switch (type) {
		case Homogeneous:
			throw new RuntimeException("Homogeneous is not supported yet");
		case Rotation:
			return new CSGRotation(csgObject.getFreeName(TransformationType.Rotation), new Vect3d(1, 0, 0), 0);
		case Scale:
			return new CSGScale(csgObject.getFreeName(TransformationType.Scale), new Vect3d(1,1,1));
		case Translation:
			return new CSGTranslation(csgObject.getFreeName(TransformationType.Translation), new Vect3d(0,0,0));
		}
		return null;
	}
	
	private String getVect3dDescription(Vect3d point) {
		return "[" +  vect3dToString(point) + "]";
	}
	
	private String vect3dToString(Vect3d point) {
		return point.getX() + ", " + point.getY() + ", " + point.getZ();
	}
	
	private Vect3d stringToVect3d(String str) {
		StringTokenizer st = new StringTokenizer(str, ", ");
		double x = Double.parseDouble(st.nextToken());
		double y = Double.parseDouble(st.nextToken());
		double z = Double.parseDouble(st.nextToken());
		Vect3d vect3d = new Vect3d(x, y, z);
		return vect3d;
	}

	private void updateCSGObject(final Object objectToBeSelected) {
		if (csgObject.getRoot() != null) {
			csgObject.setRoot(csgObject.getRoot().cloneTree());
		}
		AsynchClientTask task1 = new AsynchClientTask("regenerating geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				simulationContext.getGeometry().precomputeAll(true, true);
			}
		};
		AsynchClientTask task2 = new AsynchClientTask("refreshing", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				csgObjectTreeModel.select(objectToBeSelected);
			}
		};
		ClientTaskDispatcher.dispatch(CSGObjectPropertiesPanel.this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, false);
	}
}
