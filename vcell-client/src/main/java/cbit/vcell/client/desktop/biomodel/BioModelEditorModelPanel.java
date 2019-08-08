/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.EntityImpl;
import org.vcell.relationship.RelationshipObject;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.util.Displayable;
import org.vcell.util.Pair;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.Identifiable;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.JTabbedPaneEnhanced;
import org.vcell.util.gui.VCellIcons;
import org.vcell.util.gui.sorttable.SortPreference;

import cbit.gui.ModelProcessEquation;
import cbit.gui.TextFieldAutoCompletion;
import cbit.gui.graph.GraphModel;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.client.ChildWindowListener;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.MolecularTypeTableModel.Column;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.graph.MolecularTypeSmallShape;
import cbit.vcell.graph.SpeciesPatternSmallShape;
import cbit.vcell.graph.gui.ReactionCartoonEditorPanel;
import cbit.vcell.graph.gui.ReactionCartoonTool;
import cbit.vcell.graph.gui.StructureToolShapeIcon;
import cbit.vcell.graph.structures.AllStructureSuite;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.BioModelEntityObject;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelProcess;
import cbit.vcell.model.ModelProcessDynamics;
import cbit.vcell.model.Product;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.RbmKineticLaw;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SymbolTableEntry;

@SuppressWarnings("serial")
public class BioModelEditorModelPanel extends DocumentEditorSubPanel implements Model.Owner {
	protected static final String PROPERTY_NAME_BIO_MODEL = "bioModel";
	public enum ModelPanelTabID {
//		structure_diagram("Structure Diagram"),
		reaction_diagram("Reaction Diagram"),
		reaction_table("Reactions"),
		structure_table("Structures"),
		species_table("Species"),
		species_definitions_table(MolecularType.typeName + "s"),
		observables_table("Observables");
		
		private String name = null;
		ModelPanelTabID(String name) {
			this.name = name;
		}
		final String getName() {
			return name;
		}
	}
	
	private class ModelPanelTab {
		ModelPanelTabID id;
		JComponent component = null;
		Icon icon = null;
		ModelPanelTab(ModelPanelTabID id, JComponent component, Icon icon) {
			this.id = id;
			this.component = component;
			this.icon = icon;
		}
		final String getName() {
			return id.getName();
		}
		final JComponent getComponent() {
			return component;
		}
		final Icon getIcon() {
			return icon;
		}
	}
	
	private ModelPanelTab modelPanelTabs[] = new ModelPanelTab[ModelPanelTabID.values().length]; 

	private JButton newButton = null;
	private JButton newButton2 = null;
	private JButton newMemButton = null;
	private JButton deleteButton = null;
	private JButton pathwayButton = null;
	private JButton duplicateButton = null;
	private JPopupMenu pathwayPopupMenu = null;
	private JMenuItem showPathwayMenuItem = null;
	private JMenuItem editPathwayMenuItem = null;
	private EditorScrollTable structuresTable = null;
	private EditorScrollTable reactionsTable = null;
	private EditorScrollTable speciesTable = null;
	private EditorScrollTable molecularTypeTable = null;
	private EditorScrollTable observablesTable = null;
	private BioModelEditorStructureTableModel structureTableModel = null;
	private BioModelEditorReactionTableModel reactionTableModel = null;
	private BioModelEditorSpeciesTableModel speciesTableModel = null;
	private MolecularTypeTableModel molecularTypeTableModel = null;
	private ObservableTableModel observableTableModel = null;
	private BioModel bioModel;
	private JTextField textFieldSearch = null;
	private JTabbedPane tabbedPane = null;
	private JDialog viewReactionRulesDialog = null;
	
//	private CartoonEditorPanelFixed cartoonEditorPanel = null;
	private ReactionCartoonEditorPanel reactionCartoonEditorPanel = null;

	private InternalEventHandler eventHandler = new InternalEventHandler();
//	private ChangeListener changeListener = new ChangeListener() {
//		@Override
//		public void stateChanged(ChangeEvent e) {
//			final String prologue = "<html><b>";
//			final String epilogue = "</html></b>";
//			JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();		// sourceTabbedPane == this.tabbedPane
//			int numTabs = sourceTabbedPane.getTabCount();
//			for(int i = 0; i < numTabs; i++) {
//				String curTitle = sourceTabbedPane.getTitleAt(i);
//				if(curTitle.startsWith(prologue)) {
//					curTitle = curTitle.substring(prologue.length());
//					curTitle = curTitle.substring(0, curTitle.indexOf(epilogue));
//					tabbedPane.setTitleAt(i, curTitle);
//				}
//			}
//			int index = sourceTabbedPane.getSelectedIndex();
//			System.out.println("Tab changed to: " + sourceTabbedPane.getTitleAt(index));
//			String selectedTitle = sourceTabbedPane.getTitleAt(index);	// we know it's clean of prologue or epilogue
//			selectedTitle = prologue + selectedTitle + epilogue;
//			tabbedPane.setTitleAt(index, selectedTitle);
//		}
//	};

	private JPanel buttonPanel;
	private PhysiologyRelationshipPanel relationshipPanel;
	
	private class InternalEventHandler implements ActionListener, PropertyChangeListener, ListSelectionListener, ChangeListener, MouseListener, DocumentListener, KeyListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditorModelPanel.this && evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL)) {
				bioModelChange();
			} else if (evt.getPropertyName().equals(GraphModel.PROPERTY_NAME_SELECTED)) {
				Object[] selectedObjects = (Object[]) evt.getNewValue();
				setSelectedObjects(selectedObjects);
			} else if (evt.getSource() == reactionCartoonEditorPanel && evt.getPropertyName().equals(ReactionCartoonEditorPanel.PROPERTY_NAME_FLOATING)) {
				floatDiagramView((Boolean) evt.getNewValue());
			} else if(evt.getSource() instanceof Model && evt.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST)) {
//				ArrayList<MolecularType> mtList = (ArrayList<MolecularType>)evt.getNewValue();
//				if(mtList.isEmpty() || mtList.size() == 0) {
//					if(tabbedPane.getComponents().length == ModelPanelTabID.observables_table.ordinal()+1) {
//						tabbedPane.removeTabAt(ModelPanelTabID.observables_table.ordinal());
//					}
//				} else {
//					ModelPanelTab tab = modelPanelTabs[ModelPanelTabID.observables_table.ordinal()];
//					tab.getComponent().setBorder(GuiConstants.TAB_PANEL_BORDER);
//					tabbedPane.addTab(tab.getName(), tab.getIcon(), tab.getComponent());
//				}
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == newButton) {
				try {
					newButtonPressed();
				} catch (ModelException | PropertyVetoException e1) {
					e1.printStackTrace();
					throw new RuntimeException(e1.getMessage(), e1);
				}
			} else if (e.getSource() == newMemButton) {
				newMemButtonPressed();
			} else if (e.getSource() == deleteButton) {
				deleteButtonPressed();
			} else if (e.getSource() == pathwayButton) {
				getPathwayPopupMenu().show(pathwayButton, 0, pathwayButton.getHeight());
			} else if (e.getSource() == duplicateButton) {
				duplicateButtonPressed();
			} else if (e.getSource() == showPathwayMenuItem) {
				showPathwayLinks();
			} else if (e.getSource() == editPathwayMenuItem) {
				editPathwayLinks();
			}

			if (e.getSource() == newButton2) {
				try {
					newButton2Pressed();
				} catch (ModelException | PropertyVetoException e1) {
					e1.printStackTrace();
					throw new RuntimeException(e1.getMessage(), e1);
				}
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			if (bioModel == null || e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() instanceof ListSelectionModel) {
				tableSelectionChanged();
			}
		}

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == tabbedPane) {
				tabbedPaneSelectionChanged();
			}
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == tabbedPane) {
				showDiagramView();
			} else if (e.getClickCount() == 2) {
				if(e.getSource() == reactionsTable) {
					EditorScrollTable table = reactionsTable;
					int row = table.getSelectedRow();
					int col = table.getSelectedColumn();
					Object tableSelection = table.getValueAt(row, col);
					if(col == BioModelEditorReactionTableModel.COLUMN_LINK && tableSelection instanceof BioPaxObject) {
						selectionManager.followHyperlink(new ActiveView(null,DocumentEditorTreeFolderClass.PATHWAY_DIAGRAM_NODE, ActiveViewID.pathway_diagram),new Object[]{tableSelection});
					} else if(col == BioModelEditorReactionTableModel.COLUMN_NOTES) {
						selectionManager.firePropertyChange(SelectionManager.PROPERTY_NAME_SELECTED_PANEL, null, selectionManager.getAnnotationNavigator());
					}
				} else if(e.getSource() == speciesTable) {
					EditorScrollTable table = speciesTable;
					int row = table.getSelectedRow();
					int col = table.getSelectedColumn();
					Object tableSelection = table.getValueAt(row, col);
					if(col == BioModelEditorSpeciesTableModel.COLUMN_LINK && tableSelection instanceof BioPaxObject) {
						selectionManager.followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.PATHWAY_DIAGRAM_NODE, ActiveViewID.pathway_diagram), new Object[]{tableSelection});
					} else if(col == BioModelEditorSpeciesTableModel.COLUMN_NOTES) {
						selectionManager.firePropertyChange(SelectionManager.PROPERTY_NAME_SELECTED_PANEL, null, selectionManager.getAnnotationNavigator());
					}
				} else if(e.getSource() == molecularTypeTable) {
					EditorScrollTable table = molecularTypeTable;
					int row = table.getSelectedRow();
					int col = table.getSelectedColumn();
					Object tableSelection = table.getValueAt(row, col);
					if(col == MolecularTypeTableModel.Column.link.ordinal() && tableSelection instanceof BioPaxObject) {
						selectionManager.followHyperlink(new ActiveView(null,DocumentEditorTreeFolderClass.PATHWAY_DIAGRAM_NODE, ActiveViewID.pathway_diagram),new Object[]{tableSelection});
					} else if(col == MolecularTypeTableModel.Column.notes.ordinal()) {
						selectionManager.firePropertyChange(SelectionManager.PROPERTY_NAME_SELECTED_PANEL, null, selectionManager.getAnnotationNavigator());
					}
				} else if(e.getSource() == observablesTable) {
					EditorScrollTable table = observablesTable;
					int col = table.getSelectedColumn();
					if(col == ObservableTableModel.Column.notes.ordinal()) {
						selectionManager.firePropertyChange(SelectionManager.PROPERTY_NAME_SELECTED_PANEL, null, selectionManager.getAnnotationNavigator());
					}
					
				} else if(e.getSource() == structuresTable) {
					EditorScrollTable table = structuresTable;
					int col = table.getSelectedColumn();
					if(col == BioModelEditorStructureTableModel.COLUMN_NOTES) {
						selectionManager.firePropertyChange(SelectionManager.PROPERTY_NAME_SELECTED_PANEL, null, selectionManager.getAnnotationNavigator());
					}
				}
/*				
				BioModelEditorReactionTableModel.COLUMN_NOTES
				BioModelEditorSpeciesTableModel.COLUMN_NOTES
				MolecularTypeTableModel.Column.notes.ordinal()
				ObservableTableModel.Column.notes.ordinal()
				BioModelEditorStructureTableModel.COLUMN_NOTES
*/
				
					// TODO: different behavior depending on whether we clicked on icon vs. the rest of the cell's body
//					if(col == MolecularTypeTableModel.Column.link.ordinal()) {
//						Point whereClicked = e.getPoint();
//						Rectangle cellRectangle = table.getCellRect(row, col, true);
//						Icon icon = VCellIcons.noteIcon;
//						Rectangle iconRectangle = new Rectangle(cellRectangle.x, cellRectangle.y, icon.getIconWidth(), icon.getIconHeight());
//						if(iconRectangle.contains(whereClicked)) {
//							// if we clicked in the annotation icon general area we navigate to that panel.
//							selectionManager.firePropertyChange(SelectionManager.PROPERTY_NAME_SELECTED_PANEL, null, selectionManager.getAnnotationNavigator());
//						} else {
//							Object tableSelection = table.getValueAt(row, col);
//							if(tableSelection instanceof BioPaxObject) {
//								// if we have a link to a BioPaxObject, navigate to it
//								selectionManager.followHyperlink(new ActiveView(null,DocumentEditorTreeFolderClass.PATHWAY_DIAGRAM_NODE, ActiveViewID.pathway_diagram),new Object[]{tableSelection});
//							}
//						}
//					}
			}
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}

		public void insertUpdate(DocumentEvent e) {
			search();
		}

		public void removeUpdate(DocumentEvent e) {
			search();
		}

		public void changedUpdate(DocumentEvent e) {
			search();
		}
		
		public void keyTyped(KeyEvent e) {
		}
		
		public void keyReleased(KeyEvent e) {
		}
		
		public void keyPressed(KeyEvent e) {
			if (e.getModifiersEx() == 0) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					deleteButtonPressed();
				}
			}
		}
	}
	
	public BioModelEditorModelPanel() {
		super();
		addPropertyChangeListener(eventHandler);
		initialize();
	}
	
	public void specialLayout(){
		reactionCartoonEditorPanel.specialLayout();
	}
	private BioModelEntityObject getSelectedBioModelEntityObject() {
		BioModelEntityObject selectedBioModelEntityObject = null;
		ArrayList<Object> selectedObjects = selectionManager.getSelectedObjects(BioModelEntityObject.class);
		if (selectedObjects.size() == 1) {				
			if (selectedObjects.size() == 1 && selectedObjects.get(0) instanceof BioModelEntityObject) {
				selectedBioModelEntityObject = (BioModelEntityObject) selectedObjects.get(0);
			}
		}
		return selectedBioModelEntityObject;
	}
	private void refreshButtons() {
		int selectedIndex = tabbedPane.getSelectedIndex();		
		newMemButton.setVisible(false);
		newButton2.setVisible(false);
		duplicateButton.setVisible(false);
		
		if (selectedIndex == ModelPanelTabID.reaction_diagram.ordinal() 
//			|| selectedIndex == ModelPanelTabID.structure_diagram.ordinal()
			|| selectedIndex == ModelPanelTabID.species_table.ordinal() && (bioModel == null || 
			bioModel.getModel().getNumStructures() < 1))
		{
			newButton.setVisible(false);
		} else {
			Icon downArrow = null;
			if( bioModel.getModel().getNumStructures() > 1) {
				downArrow =  new DownArrowIcon();
			}
			newButton.setVisible(true);
			if (selectedIndex == ModelPanelTabID.structure_table.ordinal()) {
				newButton.setText("New Compartment");
				newButton.setIcon(null);
				newMemButton.setVisible(true);
				newMemButton.setIcon(null);
			} else if(selectedIndex == ModelPanelTabID.reaction_table.ordinal()) {
				newButton.setText("New Reaction");
				if(bioModel.getModel().getRbmModelContainer().getMolecularTypeList().isEmpty()) {
					newButton2.setVisible(false);		// show rule-related buttons only if rules are possible
					duplicateButton.setVisible(false);
				} else {
					newButton2.setVisible(true);
					duplicateButton.setVisible(true);
				}
				newButton2.setText("New Rule");
				newButton2.setIcon(downArrow);
				newButton2.setHorizontalTextPosition(SwingConstants.LEFT);
				duplicateButton.setIcon(downArrow);
				duplicateButton.setHorizontalTextPosition(SwingConstants.LEFT);
			} else if(selectedIndex == ModelPanelTabID.species_table.ordinal()) {
				newButton.setVisible(true);
				newButton.setIcon(downArrow);
				newButton.setHorizontalTextPosition(SwingConstants.LEFT);
				if(bioModel.getModel().getRbmModelContainer().getMolecularTypeList().isEmpty()) {
					duplicateButton.setVisible(false);
				} else {
					duplicateButton.setVisible(true);
				}
				duplicateButton.setIcon(downArrow);
				duplicateButton.setHorizontalTextPosition(SwingConstants.LEFT);
				newButton.setText("New Species");
			} else if(selectedIndex == ModelPanelTabID.observables_table.ordinal()) {
				newButton.setVisible(true);
				newButton.setIcon(downArrow);
				newButton.setText("New Observable");		
				if(bioModel.getModel().getRbmModelContainer().getMolecularTypeList().isEmpty()) {
					duplicateButton.setVisible(false);
				} else {
					duplicateButton.setVisible(true);
				}
				duplicateButton.setIcon(downArrow);
				duplicateButton.setHorizontalTextPosition(SwingConstants.LEFT);
			} else if(selectedIndex == ModelPanelTabID.species_definitions_table.ordinal()) {
				newButton.setText("New Molecule");		
			}
		}
		
		deleteButton.setEnabled(false);
		pathwayButton.setEnabled(false);
		duplicateButton.setEnabled(false);
		getShowPathwayMenuItem().setEnabled(false);
		Object[] selectedObjects = null;
		if (selectedIndex == ModelPanelTabID.reaction_diagram.ordinal()) {
			selectedObjects = reactionCartoonEditorPanel.getReactionCartoon().getSelectedObjects();
//		} else if (selectedIndex == ModelPanelTabID.structure_diagram.ordinal()) {
//			selectedObjects = cartoonEditorPanel.getStructureCartoon().getSelectedObjects();
		} else {
			computeCurrentSelectedTable();
			if (currentSelectedTableModel != null) {
				int[] rows = currentSelectedTable.getSelectedRows();
				ArrayList<Object> objectList = new ArrayList<Object>();
				for (int r = 0; r < rows.length; r ++) {
					Object object = currentSelectedTableModel.getValueAt(rows[r]);
					if (object != null) {
						objectList.add(object);
					}
				}
				selectedObjects = objectList.toArray(new Object[0]);
			}
		}
		if (selectedObjects != null) {				
			deleteButton.setEnabled(selectedObjects.length > 0);
			if (selectedObjects.length == 1 && 
					(selectedObjects[0] instanceof ReactionRule || selectedObjects[0] instanceof RbmObservable)) {
				duplicateButton.setEnabled(true);
			} else if(selectedObjects.length == 1 && selectedObjects[0] instanceof SpeciesContext) {
				SpeciesContext sp = (SpeciesContext)selectedObjects[0];
				if(sp.hasSpeciesPattern()) {
					duplicateButton.setEnabled(true);
				} else {
					duplicateButton.setEnabled(false);
				}
			}
			if (selectedObjects.length == 1 && selectedObjects[0] instanceof BioModelEntityObject) {
				pathwayButton.setEnabled(true);
				if (bioModel.getRelationshipModel().getRelationshipObjects((BioModelEntityObject) selectedObjects[0]).size() > 0) {
					getShowPathwayMenuItem().setEnabled(true);
				}
			}
		}
	}

	public void tabbedPaneSelectionChanged() {
		textFieldSearch.setText(null);
		reactionTableModel.setSearchText(null);
		structureTableModel.setSearchText(null);
		speciesTableModel.setSearchText(null);
		molecularTypeTableModel.setSearchText(null);
		observableTableModel.setSearchText(null);
		
		int selectedIndex = tabbedPane.getSelectedIndex();
		ActiveView activeView = null;
		if (selectedIndex == ModelPanelTabID.reaction_table.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.REACTIONS_NODE, ActiveViewID.reactions);
		} else if (selectedIndex == ModelPanelTabID.structure_table.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.STRUCTURES_NODE, ActiveViewID.structures);
		} else if (selectedIndex == ModelPanelTabID.species_table.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.SPECIES_NODE, ActiveViewID.species);
		} else if (selectedIndex == ModelPanelTabID.species_definitions_table.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.MOLECULAR_TYPES_NODE, ActiveViewID.species_definitions);
		} else if (selectedIndex == ModelPanelTabID.observables_table.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.OBSERVABLES_NODE, ActiveViewID.observables);
		} else if (selectedIndex == ModelPanelTabID.reaction_diagram.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.REACTION_DIAGRAM_NODE, ActiveViewID.reaction_diagram);
//		} else if (selectedIndex == ModelPanelTabID.structure_diagram.ordinal()) {
//			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.STRUCTURE_DIAGRAM_NODE, ActiveViewID.structure_diagram);
		}
		if (activeView != null) {
			setActiveView(activeView);
		}
		refreshButtons();
	}

	@Override
	public void onSelectedObjectsChange(Object[] selectedObjects) {
		reactionCartoonEditorPanel.getReactionCartoon().setSelectedObjects(selectedObjects);
		reactionCartoonEditorPanel.selectedObjectsChanged();
//		cartoonEditorPanel.getStructureCartoon().setSelectedObjects(selectedObjects);
		setTableSelections(selectedObjects, structuresTable, structureTableModel);
		setTableSelections(selectedObjects, reactionsTable, reactionTableModel);
		setTableSelections(selectedObjects, speciesTable, speciesTableModel);
		setTableSelections(selectedObjects, molecularTypeTable, molecularTypeTableModel);
		setTableSelections(selectedObjects, observablesTable, observableTableModel);
		refreshButtons();
	}

	private JMenuItem getShowPathwayMenuItem() {
		if (showPathwayMenuItem == null) {
			showPathwayMenuItem = new JMenuItem("Show Linked Pathway Objects");
			showPathwayMenuItem.addActionListener(eventHandler);			
		}
		return showPathwayMenuItem;
	}
	private JPopupMenu getPathwayPopupMenu() {
		if (pathwayPopupMenu == null) {
			pathwayPopupMenu = new JPopupMenu();
			editPathwayMenuItem = new JMenuItem("Edit Pathway Links...");
			editPathwayMenuItem.addActionListener(eventHandler);
			pathwayPopupMenu.add(getShowPathwayMenuItem());
			pathwayPopupMenu.add(editPathwayMenuItem);
		}
		return pathwayPopupMenu;
	}
	
	private void initialize(){
		newButton = new JButton("New");
		newButton2 = new JButton("New Rule");
		newMemButton = new JButton("New Membrane");
		deleteButton = new JButton("Delete");
		duplicateButton = new JButton("Duplicate");
		pathwayButton = new JButton("Pathway Links", new DownArrowIcon());
		pathwayButton.setHorizontalTextPosition(SwingConstants.LEFT);
		textFieldSearch = new JTextField();
		textFieldSearch.putClientProperty("JTextField.variant", "search");
		
		structuresTable = new EditorScrollTable();
		reactionsTable = new EditorScrollTable();

		speciesTable = new EditorScrollTable();
		molecularTypeTable = new EditorScrollTable();
		observablesTable = new EditorScrollTable();
		structureTableModel = new BioModelEditorStructureTableModel(structuresTable);
		reactionTableModel = new BioModelEditorReactionTableModel(reactionsTable);
		speciesTableModel = new BioModelEditorSpeciesTableModel(speciesTable);
		molecularTypeTableModel = new MolecularTypeTableModel(molecularTypeTable);
		observableTableModel = new ObservableTableModel(observablesTable);
		structuresTable.setModel(structureTableModel);
		reactionsTable.setModel(reactionTableModel);
		speciesTable.setModel(speciesTableModel);
		molecularTypeTable.setModel(molecularTypeTableModel);
		observablesTable.setModel(observableTableModel);
		
		reactionCartoonEditorPanel = new ReactionCartoonEditorPanel();
		reactionCartoonEditorPanel.addPropertyChangeListener(eventHandler);
		reactionCartoonEditorPanel.getReactionCartoonFull().addPropertyChangeListener(eventHandler);
		reactionCartoonEditorPanel.getReactionCartoonMolecule().addPropertyChangeListener(eventHandler);
		reactionCartoonEditorPanel.getReactionCartoonRule().addPropertyChangeListener(eventHandler);
//		cartoonEditorPanel  = new CartoonEditorPanelFixed();
//		cartoonEditorPanel.getStructureCartoon().addPropertyChangeListener(eventHandler);
		
		/* button panel */
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(newButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(newButton2, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(newMemButton, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(duplicateButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(deleteButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(pathwayButton, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 6;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,5)), gbc);		

		gbc = new GridBagConstraints();
		gbc.gridx = 7;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		buttonPanel.add(new JLabel("Search "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 8;
		gbc.gridy = 0;
		gbc.weightx = 1.5;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		buttonPanel.add(textFieldSearch, gbc);
		/* button panel */
		
		tabbedPane = new JTabbedPaneEnhanced();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		modelPanelTabs[ModelPanelTabID.reaction_diagram.ordinal()] = new ModelPanelTab(ModelPanelTabID.reaction_diagram, reactionCartoonEditorPanel, VCellIcons.diagramIcon);
//		modelPanelTabs[ModelPanelTabID.structure_diagram.ordinal()] = new ModelPanelTab(ModelPanelTabID.structure_diagram, cartoonEditorPanel, VCellIcons.structureIcon);
		modelPanelTabs[ModelPanelTabID.reaction_table.ordinal()] = new ModelPanelTab(ModelPanelTabID.reaction_table, reactionsTable.getEnclosingScrollPane(), VCellIcons.tableIcon);
		modelPanelTabs[ModelPanelTabID.structure_table.ordinal()] = new ModelPanelTab(ModelPanelTabID.structure_table, structuresTable.getEnclosingScrollPane(), VCellIcons.tableIcon);
		modelPanelTabs[ModelPanelTabID.species_table.ordinal()] = new ModelPanelTab(ModelPanelTabID.species_table, speciesTable.getEnclosingScrollPane(), VCellIcons.tableIcon);
		modelPanelTabs[ModelPanelTabID.species_definitions_table.ordinal()] = new ModelPanelTab(ModelPanelTabID.species_definitions_table, molecularTypeTable.getEnclosingScrollPane(), VCellIcons.tableIcon);
		modelPanelTabs[ModelPanelTabID.observables_table.ordinal()] = new ModelPanelTab(ModelPanelTabID.observables_table, observablesTable.getEnclosingScrollPane(), VCellIcons.tableIcon);
		tabbedPane.addChangeListener(eventHandler);
		tabbedPane.addMouseListener(eventHandler);
		
		for (ModelPanelTab tab : modelPanelTabs) {
			tab.getComponent().setBorder(GuiConstants.TAB_PANEL_BORDER);
			tabbedPane.addTab(tab.getName(), tab.getIcon(), tab.getComponent());
		}
//		tabbedPane.addChangeListener(changeListener);
		
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
						
		newButton.addActionListener(eventHandler);
		newButton2.addActionListener(eventHandler);
		newMemButton.addActionListener(eventHandler);
		duplicateButton.addActionListener(eventHandler);
		duplicateButton.setEnabled(false);
		deleteButton.addActionListener(eventHandler);
		deleteButton.setEnabled(false);
		pathwayButton.addActionListener(eventHandler);
		pathwayButton.setEnabled(false);
		textFieldSearch.addActionListener(eventHandler);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		structuresTable.getSelectionModel().addListSelectionListener(eventHandler);
		reactionsTable.getSelectionModel().addListSelectionListener(eventHandler);
		speciesTable.getSelectionModel().addListSelectionListener(eventHandler);
		molecularTypeTable.getSelectionModel().addListSelectionListener(eventHandler);
		observablesTable.getSelectionModel().addListSelectionListener(eventHandler);
		
		DefaultScrollTableCellRenderer tableRenderer = new DefaultScrollTableCellRenderer(){
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				if (value instanceof Structure) {
					setText(((Structure)value).getName());
				} else if (value instanceof Kinetics) {
					setText(((Kinetics) value).getKineticsDescription().getDescription());
				} else if (value instanceof RbmKineticLaw) {
					setText(((RbmKineticLaw) value).getRateLawType().name());
				}
				return this;
			}			
		};
		
		RbmTableRenderer rbmTableRenderer = new RbmTableRenderer();
		structuresTable.setDefaultRenderer(Structure.class, tableRenderer);
		speciesTable.setDefaultRenderer(Structure.class, tableRenderer);
		reactionsTable.setDefaultRenderer(Structure.class, tableRenderer);
		reactionsTable.setDefaultRenderer(Kinetics.class, tableRenderer);
		reactionsTable.setDefaultRenderer(RbmKineticLaw.class, tableRenderer);
		reactionsTable.setDefaultRenderer(ModelProcessDynamics.class, tableRenderer);
		
		// Link to biopax object Table Cell Renderer
		DefaultScrollTableCellRenderer linkTableCellRenderer = new DefaultScrollTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				BioModelEntityObject bioModelEntityObject = null;
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					if (table.getModel() == reactionTableModel && reactionTableModel.getValueAt(row) instanceof BioModelEntityObject) {
						bioModelEntityObject = (BioModelEntityObject)reactionTableModel.getValueAt(row);
					} else if (table.getModel() == speciesTableModel){
						bioModelEntityObject = speciesTableModel.getValueAt(row);
					} else if(table.getModel() == molecularTypeTableModel) {
						bioModelEntityObject = molecularTypeTableModel.getValueAt(row);
					}
					if (bioModelEntityObject != null) {
						Set<RelationshipObject> relationshipSet = bioModel.getRelationshipModel().getRelationshipObjects(bioModelEntityObject);
						if (relationshipSet.size() > 0) {
							StringBuilder tooltip = new StringBuilder("<html>Links to Pathway objects:<br>");
							for (RelationshipObject ro : relationshipSet) {
								tooltip.append("<li>" + ro.getBioPaxObject() + "</li>");
							}
							if (!isSelected) {
								setForeground(Color.blue);
							}
							String finalName = null;
							BioPaxObject bioPaxObject = relationshipSet.iterator().next().getBioPaxObject();
							if(bioPaxObject instanceof EntityImpl && ((EntityImpl)bioPaxObject).getName() != null && ((EntityImpl)bioPaxObject).getName().size() > 0) {
								finalName = ((EntityImpl)bioPaxObject).getName().get(0);
							} else if(bioPaxObject instanceof Conversion) {
								Conversion mp = (Conversion)bioPaxObject;
								finalName = "[" + bioPaxObject.getIDShort() + "]";
							} else {
								finalName = bioModelEntityObject.getName();
							}
							final int LIMIT = 40;
							final String DOTS = "...";
							if(finalName != null && finalName.length() > LIMIT){
								finalName = finalName.substring(0, LIMIT-DOTS.length()-1)+DOTS;
							}
							setText("<html><u>" + finalName + "</u></html>");
							setToolTipText(tooltip.toString());
						}
					}
				}
				return this;
			}
		};
		// Annotations icon column renderer
		DefaultScrollTableCellRenderer annotationTableCellRenderer = new DefaultScrollTableCellRenderer() {
			final Color lightBlueBackground = new Color(214, 234, 248);
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				Identifiable entity = null;
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					if (table.getModel() == reactionTableModel) {
						entity = (BioModelEntityObject)reactionTableModel.getValueAt(row);
					} else if (table.getModel() == speciesTableModel) {
						entity = speciesTableModel.getValueAt(row);
					} else if(table.getModel() == molecularTypeTableModel) {
						entity = molecularTypeTableModel.getValueAt(row);
					} else if(table.getModel() == observableTableModel) {
						entity = observableTableModel.getValueAt(row);
					} else if(table.getModel() == structureTableModel) {
						entity = structureTableModel.getValueAt(row);
					}
					if (entity != null) {
						if(isSelected) {
							setBackground(lightBlueBackground);
						}
						Identifiable identifiable = AnnotationsPanel.getIdentifiable(entity);
						String freeText = bioModel.getVCMetaData().getFreeTextAnnotation(identifiable);
						MiriamManager miriamManager = bioModel.getVCMetaData().getMiriamManager();
						TreeMap<Identifiable, Map<MiriamRefGroup, MIRIAMQualifier>> miriamDescrHeir = miriamManager.getMiriamTreeMap();
						Map<MiriamRefGroup, MIRIAMQualifier> refGroupMap = miriamDescrHeir.get(identifiable);

						Icon icon1 = VCellIcons.issueGoodIcon;
						Icon icon2 = VCellIcons.issueGoodIcon;
						if(freeText != null && !freeText.isEmpty()) {
							icon2 = VCellIcons.noteIcon;
//							icon = VCellIcons.bookmarkIcon;
//							icon = VCellIcons.addIcon(icon, VCellIcons.linkIcon);
//							icon = VCellIcons.addIcon(icon, VCellIcons.certificateIcon);
//							icon = VCellIcons.addIcon(icon, VCellIcons.noteIcon);
						} 
						if(refGroupMap != null && !refGroupMap.isEmpty()) {
							icon1 = VCellIcons.linkIcon;
						}
						Icon icon = VCellIcons.addIcon(icon1, icon2);
						setIcon(icon);
					}
				}
				return this;
			}
		};

		DefaultScrollTableCellRenderer rbmReactionExpressionCellRenderer = new DefaultScrollTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					Object selectedObject = null;
					if (table.getModel() == reactionTableModel) {
						selectedObject = reactionTableModel.getValueAt(row);
					}
					if (selectedObject != null) {
						if(selectedObject instanceof ReactionRule && value instanceof ModelProcessEquation) {
							String text = "<html>";
							text += "Reaction Rule";
							text += "</html>";
							setText(text);
						} else {					// plain reaction, check if reactants have species pattern
							ReactionStep rs = (ReactionStep)selectedObject;
							String text = "<html>";
							for(int i = 0; i<rs.getNumReactants(); i++) {
								Reactant p = rs.getReactant(i);
								if(p.getSpeciesContext().hasSpeciesPattern()) {
									text += p.getStoichiometry()>1 ? (p.getStoichiometry()+"") : "";
									text += p.getName();			//		text += "<b>" + p.getName() + "</b>";
								} else {
									text += p.getStoichiometry()>1 ? (p.getStoichiometry()+"") : "";
									text += p.getName();
								}
								if(i < rs.getNumReactants()-1) {
									text += " + ";
								}
							}
							text += " -&gt; ";
							for(int i = 0; i<rs.getNumProducts(); i++) {
								Product p = rs.getProduct(i);
								if(p.getSpeciesContext().hasSpeciesPattern()) {
									text += p.getStoichiometry()>1 ? (p.getStoichiometry()+"") : "";
									text += p.getName();			//			text += "<b>" + p.getName() + "</b>";
								} else {
									text += p.getStoichiometry()>1 ? (p.getStoichiometry()+"") : "";
									text += p.getName();
								}
								if(i < rs.getNumProducts()-1) {
									text += " + ";
								}
							}								
							text += "</html>";
							setText(text);
						}
					}
				}
				return this;
			}
		};
		DefaultScrollTableCellRenderer rbmReactionDefinitionCellRenderer = new DefaultScrollTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					Object selectedObject = null;
					if (table.getModel() == reactionTableModel) {
						selectedObject = reactionTableModel.getValueAt(row);
					}
					if (selectedObject != null) {
						if(selectedObject instanceof ReactionRule && value instanceof ModelProcessEquation) {
							ReactionRule rr = (ReactionRule)selectedObject;
							String text = "<html>";
							for(int i=0; i< rr.getReactantPatterns().size(); i++) {
								ReactantPattern rp = rr.getReactantPattern(i);
								if(rp.getStructure() != null && !rp.getSpeciesPattern().getMolecularTypePatterns().isEmpty()) {
									text += "@" + rp.getStructure().getName() + ":";
								}
								text += RbmUtils.toBnglString(rp.getSpeciesPattern(), null, CompartmentMode.hide, 0);
								//text += RbmTableRenderer.toHtml(rp.getSpeciesPattern(), isSelected);
								if(i<rr.getReactantPatterns().size()-1) {
									text += "+";
								}
							}
							if(rr.isReversible()) {
								text += " &lt;-&gt; ";		// &lt;-&gt;  <->
							} else {
								text += " -&gt; ";
							}
							for(int i=0; i< rr.getProductPatterns().size(); i++) {
								ProductPattern pp = rr.getProductPattern(i);
								if(pp.getStructure() != null && !pp.getSpeciesPattern().getMolecularTypePatterns().isEmpty()) {
									text += "@" + pp.getStructure().getName() + ":";
								}
								text += RbmUtils.toBnglString(pp.getSpeciesPattern(), null, CompartmentMode.hide, 0);
								if(i<rr.getProductPatterns().size()-1) {
									text += "+";
								}
							}
							text += "</html>";
							setText(text);
						} else {					// plain reaction, check if reactants have species pattern
							ReactionStep rs = (ReactionStep)selectedObject;
							String text = "<html>";
							for(int i = 0; i<rs.getNumReactants(); i++) {
								Reactant p = rs.getReactant(i);
								if(p.getSpeciesContext().hasSpeciesPattern()) {
									text += p.getStoichiometry()>1 ? (p.getStoichiometry()+"") : "";
									text += p.getName();
								} else {
									text += p.getStoichiometry()>1 ? (p.getStoichiometry()+"") : "";
									text += p.getName();
								}
								if(i < rs.getNumReactants()-1) {
									text += " + ";
								}
							}
							if(rs.isReversible()) {
								text += " &lt;-&gt; ";		// &lt;-&gt;  <->
							} else {
								text += " -&gt; ";
							}
							for(int i = 0; i<rs.getNumProducts(); i++) {
								Product p = rs.getProduct(i);
								if(p.getSpeciesContext().hasSpeciesPattern()) {
									text += p.getStoichiometry()>1 ? (p.getStoichiometry()+"") : "";
									text += p.getName();
								} else {
									text += p.getStoichiometry()>1 ? (p.getStoichiometry()+"") : "";
									text += p.getName();
								}
								if(i < rs.getNumProducts()-1) {
									text += " + ";
								}
							}								
							text += "</html>";
							setText(text);
						}
					}
				}
				return this;
			}
		};
		DefaultScrollTableCellRenderer rbmObservablePatternCellRenderer = new DefaultScrollTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					Object selectedObject = null;
					if (table.getModel() == observableTableModel) {
						selectedObject = observableTableModel.getValueAt(row);
					}
					if (selectedObject != null) {
						if(selectedObject instanceof RbmObservable && value instanceof String) {
							RbmObservable o = (RbmObservable)selectedObject;
							String text = "<html>";
							for(int i=0; i<o.getSpeciesPatternList().size(); i++) {
								SpeciesPattern sp = o.getSpeciesPattern(i);
								text += RbmTableRenderer.toHtml(sp, isSelected);
								if(i<o.getSpeciesPatternList().size()-1) {
									text += " ";
								}
							}
							text = RbmUtils.appendSequence(text, o);
							text += "</html>";
							setText(text);
						}
					}
				}
				return this;
			}
		};
		DefaultScrollTableCellRenderer rbmSpeciesNameCellRenderer = new DefaultScrollTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					Object selectedObject = null;
					if (table.getModel() == speciesTableModel) {
						selectedObject = speciesTableModel.getValueAt(row);
					}
					if (selectedObject != null) {
						if(selectedObject instanceof SpeciesContext) {
							SpeciesContext sc = (SpeciesContext)selectedObject;
							String text = "<html>";
							if(sc.hasSpeciesPattern()) {
								text += "<b>" + sc.getName() + "</b>";
							} else {
								text += sc.getName();
							}
							text += "</html>";
							setText(text);
						}
					}
				}
				return this;
			}
		};
		
		//
		// this renderer only paints the molecular type small shape in the MolecularType Table
		//
		DefaultScrollTableCellRenderer rbmMolecularTypeShapeDepictionCellRenderer = new DefaultScrollTableCellRenderer() {
			MolecularTypeSmallShape stls = null;
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, 
					boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					Object selectedObject = null;
					if (table.getModel() == molecularTypeTableModel) {
						selectedObject = molecularTypeTableModel.getValueAt(row);
					}
					if (selectedObject != null) {
						if(selectedObject instanceof MolecularType) {
							MolecularType mt = (MolecularType)selectedObject;
							Graphics cellContext = table.getGraphics();
							if(mt != null) {
								stls = new MolecularTypeSmallShape(4, 3, mt, null, cellContext, mt, null, issueManager);
							}
						}
					} else {
						stls = null;
					}
				}
				return this;
			}
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if(stls != null) {
					stls.paintSelf(g);
				}
			}
		};
		// painting of species patterns small shapes inside the species context table
		DefaultScrollTableCellRenderer rbmSpeciesShapeDepictionCellRenderer = new DefaultScrollTableCellRenderer() {
			SpeciesPatternSmallShape spss = null;
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, 
					boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					Object selectedObject = null;
					if (table.getModel() == speciesTableModel) {
						selectedObject = speciesTableModel.getValueAt(row);
					}
					if (selectedObject != null) {
						if(selectedObject instanceof SpeciesContext) {
							SpeciesContext sc = (SpeciesContext)selectedObject;
							SpeciesPattern sp = sc.getSpeciesPattern();		// sp may be null for "plain" species contexts
							Graphics panelContext = table.getGraphics();
							spss = new SpeciesPatternSmallShape(4, 2, sp, panelContext, sc, isSelected, issueManager);
						}
					} else {
						spss = null;
					}
				}
				setText("");
				return this;
			}
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if(spss != null) {
					spss.paintSelf(g);
				}
			}
		};
// ---------------------------------------------------------------------------------------------------------------------------------
		DefaultScrollTableCellRenderer rbmReactionShapeDepictionCellRenderer = new DefaultScrollTableCellRenderer() {
			List<SpeciesPatternSmallShape> spssList = new ArrayList<SpeciesPatternSmallShape>();
			SpeciesPatternSmallShape spss = null;
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, 
					boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					Object selectedObject = null;
					if (table.getModel() == reactionTableModel) {
						selectedObject = reactionTableModel.getValueAt(row);
					}
					if (selectedObject != null) {
						if(selectedObject instanceof ReactionRule) {
							ReactionRule rr = (ReactionRule)selectedObject;
							Graphics panelContext = table.getGraphics();

							spssList.clear();
							List<ReactantPattern> rpList = rr.getReactantPatterns();
							int xPos = 4;
							for(int i = 0; i<rpList.size(); i++) {
								SpeciesPattern sp = rr.getReactantPattern(i).getSpeciesPattern();
								spss = new SpeciesPatternSmallShape(xPos, 2, sp, null, panelContext, rr, isSelected, issueManager);
								if(i < rpList.size()-1) {
									spss.addEndText("+");
								} else {
									if(rr.isReversible()) {
										spss.addEndText("<->");
										xPos += 7;
									} else {
										spss.addEndText("->");
									}
								}
								xPos += spss.getWidth() + 15;
								spssList.add(spss);
							}
							List<ProductPattern> ppList = rr.getProductPatterns();
							xPos+= 7;
							for(int i = 0; i<ppList.size(); i++) {
								SpeciesPattern sp = rr.getProductPattern(i).getSpeciesPattern();
								spss = new SpeciesPatternSmallShape(xPos, 2, sp, null, panelContext, rr, isSelected, issueManager);
								if(i < ppList.size()-1) {
									spss.addEndText("+");
								}
								xPos += spss.getWidth() + 15;
								spssList.add(spss);
							}
						} else {
							ReactionStep rs = (ReactionStep)selectedObject;
							Graphics panelContext = table.getGraphics();
							spssList.clear();
							int xPos = 4;
							int extraSpace = 0;
							for(int i = 0; i<rs.getNumReactants(); i++) {
								SpeciesPattern sp = rs.getReactant(i).getSpeciesContext().getSpeciesPattern();
								spss = new SpeciesPatternSmallShape(xPos, 2, sp, panelContext, rs, isSelected, issueManager);
								if(i < rs.getNumReactants()-1) {
									spss.addEndText("+");
								} else {
									if(rs.isReversible()) {
										spss.addEndText("<->");
										extraSpace += 7;
									} else {
										spss.addEndText("->");
									}
								}
								int offset = sp == null ? 17 : 15;
								offset += extraSpace;
								int w = spss.getWidth();
								xPos += w + offset;
								spssList.add(spss);
							}
							xPos+= 8;
							for(int i = 0; i<rs.getNumProducts(); i++) {
								SpeciesPattern sp = rs.getProduct(i).getSpeciesContext().getSpeciesPattern();
								if(i==0 && rs.getNumReactants() == 0) {
									xPos += 14;
								}
								spss = new SpeciesPatternSmallShape(xPos, 2, sp, panelContext, rs, isSelected, issueManager);
								if(i==0 && rs.getNumReactants() == 0) {
									spss.addStartText("->");
								}
								if(i < rs.getNumProducts()-1) {
									spss.addEndText("+");
								}
								int offset = sp == null ? 17 : 15;
								int w = spss.getWidth();
								xPos += w + offset;
								spssList.add(spss);
							}
						}
					} else {
						spssList.clear();
					}
				}
				setText("");
				return this;
			}
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				for(SpeciesPatternSmallShape spss : spssList) {
					if(spss == null) {
						continue;
					}
					spss.paintSelf(g);
				}
			}
		};
// -------------------------------------------------------------------------------------------------------------------------------
		DefaultScrollTableCellRenderer rbmObservableShapeDepictionCellRenderer = new DefaultScrollTableCellRenderer() {
			List<SpeciesPatternSmallShape> spssList = new ArrayList<SpeciesPatternSmallShape>();
			SpeciesPatternSmallShape spss = null;
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, 
					boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					Object selectedObject = null;
					if (table.getModel() == observableTableModel) {
						selectedObject = observableTableModel.getValueAt(row);
					}
					if (selectedObject != null) {
						if(selectedObject instanceof RbmObservable) {
							RbmObservable observable = (RbmObservable)selectedObject;
							Graphics panelContext = table.getGraphics();

							int xPos = 4;
							spssList.clear();
							for(int i = 0; i<observable.getSpeciesPatternList().size(); i++) {
								SpeciesPattern sp = observable.getSpeciesPatternList().get(i);
								spss = new SpeciesPatternSmallShape(xPos, 2, sp, panelContext, observable, isSelected, issueManager);
								xPos += spss.getWidth() + 6;
								spssList.add(spss);
							}
						}
					} else {
						spssList.clear();
					}
				}
				setText("");
				return this;
			}
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				for(SpeciesPatternSmallShape spss : spssList) {
					if(spss == null) {
						continue;
					}
					spss.paintSelf(g);
				}
			}
		};
		
		
		// TODO: here are the renderers associated with the columns
		reactionsTable.getColumnModel().getColumn(BioModelEditorReactionTableModel.COLUMN_LINK).setCellRenderer(linkTableCellRenderer);
		reactionsTable.getColumnModel().getColumn(BioModelEditorReactionTableModel.COLUMN_EQUATION).setCellRenderer(rbmReactionExpressionCellRenderer);
		reactionsTable.getColumnModel().getColumn(BioModelEditorReactionTableModel.COLUMN_DEFINITION).setCellRenderer(rbmReactionDefinitionCellRenderer);
		reactionsTable.getColumnModel().getColumn(BioModelEditorReactionTableModel.COLUMN_NOTES).setCellRenderer(annotationTableCellRenderer);
		speciesTable.getColumnModel().getColumn(BioModelEditorSpeciesTableModel.COLUMN_NAME).setCellRenderer(rbmSpeciesNameCellRenderer);
		speciesTable.getColumnModel().getColumn(BioModelEditorSpeciesTableModel.COLUMN_LINK).setCellRenderer(linkTableCellRenderer);
		speciesTable.getColumnModel().getColumn(BioModelEditorSpeciesTableModel.COLUMN_NOTES).setCellRenderer(annotationTableCellRenderer);
		molecularTypeTable.getColumnModel().getColumn(MolecularTypeTableModel.Column.link.ordinal()).setCellRenderer(linkTableCellRenderer);
		molecularTypeTable.getColumnModel().getColumn(MolecularTypeTableModel.Column.notes.ordinal()).setCellRenderer(annotationTableCellRenderer);
		observablesTable.getColumnModel().getColumn(ObservableTableModel.Column.species_pattern.ordinal()).setCellRenderer(rbmObservablePatternCellRenderer);
		observablesTable.getColumnModel().getColumn(ObservableTableModel.Column.structure.ordinal()).setCellRenderer(tableRenderer);
		observablesTable.getColumnModel().getColumn(ObservableTableModel.Column.notes.ordinal()).setCellRenderer(annotationTableCellRenderer);
		structuresTable.getColumnModel().getColumn(BioModelEditorStructureTableModel.COLUMN_NOTES).setCellRenderer(annotationTableCellRenderer);
		
		// fixed width columns
		final int notesWidth = 65;
		molecularTypeTable.getColumnModel().getColumn(MolecularTypeTableModel.Column.depiction.ordinal()).setMaxWidth(180);
		molecularTypeTable.getColumnModel().getColumn(MolecularTypeTableModel.Column.notes.ordinal()).setPreferredWidth(notesWidth);
		molecularTypeTable.getColumnModel().getColumn(MolecularTypeTableModel.Column.notes.ordinal()).setMaxWidth(notesWidth);
		speciesTable.getColumnModel().getColumn(BioModelEditorSpeciesTableModel.COLUMN_NOTES).setPreferredWidth(notesWidth);
		speciesTable.getColumnModel().getColumn(BioModelEditorSpeciesTableModel.COLUMN_NOTES).setMaxWidth(notesWidth);
		observablesTable.getColumnModel().getColumn(ObservableTableModel.Column.notes.ordinal()).setPreferredWidth(notesWidth);
		observablesTable.getColumnModel().getColumn(ObservableTableModel.Column.notes.ordinal()).setMaxWidth(notesWidth);
		reactionsTable.getColumnModel().getColumn(BioModelEditorReactionTableModel.COLUMN_DEPICTION).setPreferredWidth(180);
		reactionsTable.getColumnModel().getColumn(BioModelEditorReactionTableModel.COLUMN_NOTES).setPreferredWidth(notesWidth);
		reactionsTable.getColumnModel().getColumn(BioModelEditorReactionTableModel.COLUMN_NOTES).setMaxWidth(notesWidth);
		structuresTable.getColumnModel().getColumn(BioModelEditorStructureTableModel.COLUMN_NOTES).setPreferredWidth(notesWidth);
		structuresTable.getColumnModel().getColumn(BioModelEditorStructureTableModel.COLUMN_NOTES).setMaxWidth(notesWidth);

		// all "depictions" have their own renderer
		molecularTypeTable.getColumnModel().getColumn(MolecularTypeTableModel.Column.depiction.ordinal()).setCellRenderer(rbmMolecularTypeShapeDepictionCellRenderer);
		speciesTable.getColumnModel().getColumn(BioModelEditorSpeciesTableModel.COLUMN_DEPICTION).setCellRenderer(rbmSpeciesShapeDepictionCellRenderer);
		speciesTable.getColumnModel().getColumn(BioModelEditorSpeciesTableModel.COLUMN_DEFINITION).setCellRenderer(rbmTableRenderer);
		observablesTable.getColumnModel().getColumn(ObservableTableModel.Column.depiction.ordinal()).setCellRenderer(rbmObservableShapeDepictionCellRenderer);
		reactionsTable.getColumnModel().getColumn(BioModelEditorReactionTableModel.COLUMN_DEPICTION).setCellRenderer(rbmReactionShapeDepictionCellRenderer);
		
		observablesTable.getColumnModel().getColumn(ObservableTableModel.Column.type.ordinal()).setCellEditor(observableTableModel.getObservableTypeComboBoxEditor());
		observableTableModel.updateObservableTypeComboBox();
		
		reactionsTable.addMouseListener(eventHandler);
		reactionsTable.addKeyListener(eventHandler);
		speciesTable.addMouseListener(eventHandler);
		speciesTable.addKeyListener(eventHandler);
		molecularTypeTable.addMouseListener(eventHandler);
		molecularTypeTable.addKeyListener(eventHandler);
		observablesTable.addMouseListener(eventHandler);
		observablesTable.addKeyListener(eventHandler);
		structuresTable.addMouseListener(eventHandler);
		structuresTable.addKeyListener(eventHandler);
	}
	
	public void setBioModel(BioModel newValue) {
		BioModel oldValue = bioModel;
		bioModel = newValue;		
		firePropertyChange(PROPERTY_NAME_BIO_MODEL, oldValue, newValue);
		if(oldValue != null && oldValue.getModel() != null) {
			oldValue.getModel().removePropertyChangeListener(eventHandler);
		}
		if(bioModel != null && bioModel.getModel() != null) {
			bioModel.getModel().addPropertyChangeListener(eventHandler);
		}
		SortPreference sp = new SortPreference(true, BioModelEditorReactionTableModel.COLUMN_NAME);
		reactionTableModel.setSortPreference(sp);
	}
	
	public Model getModel() {
		return bioModel.getModel();
	}
	
	private void search() {
		String searchText = textFieldSearch.getText();
		int selectedIndex = tabbedPane.getSelectedIndex();
		if (selectedIndex == ModelPanelTabID.reaction_diagram.ordinal()) {
			reactionCartoonEditorPanel.getReactionCartoon().searchText(searchText);
//		} else if (selectedIndex == ModelPanelTabID.structure_diagram.ordinal()) {
//			cartoonEditorPanel.getStructureCartoon().searchText(searchText);
		} else {
			computeCurrentSelectedTable();
			if (currentSelectedTableModel != null) {
				currentSelectedTableModel.setSearchText(searchText);
			}
		}
	}
	
	Object newObject;
	private void newButtonPressed() throws ModelException, PropertyVetoException {
		newObject = null;
		computeCurrentSelectedTable();
		if (currentSelectedTable == speciesTable) {
			if( bioModel.getModel().getNumStructures() == 1) {
				newObject = bioModel.getModel().createSpeciesContext(bioModel.getModel().getStructures()[0]);
			} else if( bioModel.getModel().getNumStructures() > 1) {
				final JPopupMenu menu = new JPopupMenu("Choose compartment");
				for(int i=0; i<bioModel.getModel().getNumStructures(); i++) {
					Structure s = bioModel.getModel().getStructure(i);
					String sName = s.getName();
					JMenuItem menuItem = new JMenuItem("In " + s.getTypeName() + " " + sName);
					menuItem.setIcon(new StructureToolShapeIcon(17));
					menu.add(menuItem);
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							newObject = bioModel.getModel().createSpeciesContext(s);
						}
					});
				}
				menu.show(newButton, 0, newButton.getHeight());
			}
		} else if (currentSelectedTable == molecularTypeTable) {
			if(bioModel.getModel().getRbmModelContainer() != null) {
				MolecularType mt = bioModel.getModel().getRbmModelContainer().createMolecularType();
				bioModel.getModel().getRbmModelContainer().addMolecularType(mt, true);
				newObject = mt;
			}
		} else if (currentSelectedTable == observablesTable) {
			if(bioModel.getModel().getRbmModelContainer() != null) {
				if(bioModel.getModel().getRbmModelContainer().getMolecularTypeList().isEmpty()) {
					PopupGenerator.showInfoDialog(this, VCellErrorMessages.MustBeRuleBased);
					return;
				}
				if( bioModel.getModel().getNumStructures() == 1) {
					RbmObservable o = bioModel.getModel().getRbmModelContainer().createObservable(RbmObservable.ObservableType.Molecules);
					bioModel.getModel().getRbmModelContainer().addObservable(o);
					SpeciesPattern sp = new SpeciesPattern();
					o.addSpeciesPattern(sp);
					newObject = o;
				} else if( bioModel.getModel().getNumStructures() > 1) {
					final JPopupMenu menu = new JPopupMenu("Choose compartment");
					for(int i=0; i<bioModel.getModel().getNumStructures(); i++) {
						Structure s = bioModel.getModel().getStructure(i);
						String sName = s.getName();
						JMenuItem menuItem = new JMenuItem("In " + s.getTypeName() + " " + sName);
						menuItem.setIcon(new StructureToolShapeIcon(17));
						menu.add(menuItem);
						menuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								RbmObservable o = bioModel.getModel().getRbmModelContainer().createObservable(RbmObservable.ObservableType.Molecules, null, s);
								o.setStructure(s);
								try {
									bioModel.getModel().getRbmModelContainer().addObservable(o);
								} catch (ModelException | PropertyVetoException e1) {
									e1.printStackTrace();
									throw new RuntimeException(e1.getMessage(), e1);
								}
								SpeciesPattern sp = new SpeciesPattern();
								o.addSpeciesPattern(sp);
								newObject = o;
								if (newObject != null) {
									for (int i = 0; i < currentSelectedTableModel.getRowCount(); i ++) {
										if (currentSelectedTableModel.getValueAt(i) == newObject) {
											currentSelectedTable.setRowSelectionInterval(i, i);
											break;
										}
									}
								}
							}
						});
					}
					menu.show(newButton, 0, newButton.getHeight());
				}
			}
		} else if (currentSelectedTable == structuresTable) {
			try {
				Feature feature = bioModel.getModel().createFeature();
				newObject = feature;
			} catch (Exception e) {
				e.printStackTrace();
				DialogUtils.showErrorDialog(this, e.getMessage(), e);
			}
		} else if (currentSelectedTable == reactionsTable) {
			if (bioModel.getModel().getNumStructures() == 1) {
				SimpleReaction reactionStep = bioModel.getModel().createSimpleReaction(bioModel.getModel().getStructures()[0]);
				newObject = reactionStep;
			} else {
				addNewReaction();
			}
		}
		if (newObject != null) {
			for (int i = 0; i < currentSelectedTableModel.getRowCount(); i ++) {
				if (currentSelectedTableModel.getValueAt(i) == newObject) {
					currentSelectedTable.setRowSelectionInterval(i, i);
					break;
				}
			}
		}
	}
	private void newButton2Pressed() throws ModelException, PropertyVetoException {
		computeCurrentSelectedTable();
		if (currentSelectedTable == reactionsTable) {
			if(bioModel.getModel().getRbmModelContainer().getMolecularTypeList().isEmpty()) {
				PopupGenerator.showInfoDialog(this, VCellErrorMessages.MustBeRuleBased);
				return;
			}
			if( bioModel.getModel().getNumStructures() == 1) {
				Structure s  = bioModel.getModel().getStructure(0);
				newObject = makeReactionRule(s);
				if (newObject != null) {
					for (int i = 0; i < currentSelectedTableModel.getRowCount(); i ++) {
						if (currentSelectedTableModel.getValueAt(i) == newObject) {
							currentSelectedTable.setRowSelectionInterval(i, i);
							break;
						}
					}
				}
			} else if( bioModel.getModel().getNumStructures() > 1) {
				final JPopupMenu menu = new JPopupMenu("Choose compartment");
				for(int i=0; i<bioModel.getModel().getNumStructures(); i++) {
					Structure s = bioModel.getModel().getStructure(i);
					String sName = s.getName();
					JMenuItem menuItem = new JMenuItem("In " + s.getTypeName() + " " + sName);
					menuItem.setIcon(new StructureToolShapeIcon(17));
					menu.add(menuItem);
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							newObject = makeReactionRule(s);
							if (newObject != null) {
								for (int i = 0; i < currentSelectedTableModel.getRowCount(); i ++) {
									if (currentSelectedTableModel.getValueAt(i) == newObject) {
										currentSelectedTable.setRowSelectionInterval(i, i);
										break;
									}
								}
							}
						}
					});
				}
				menu.show(newButton2, 0, newButton2.getHeight());
			}
		}
	}
	private Object makeReactionRule(Structure structure) {
		ReactionRule rr = bioModel.getModel().getRbmModelContainer().createReactionRule(structure);
		if(rr != null) {
			try {
			rr.setReversible(false);
			bioModel.getModel().getRbmModelContainer().addReactionRule(rr);
			SpeciesPattern sp = new SpeciesPattern();
			ReactantPattern rp = new ReactantPattern(sp, rr.getStructure());
			rr.addReactant(rp);
			sp = new SpeciesPattern();
			ProductPattern pp = new ProductPattern(sp, rr.getStructure());
			rr.addProduct(pp);
			return rr;
			} catch (Exception e){
				e.printStackTrace();
				DialogUtils.showErrorDialog(this, e.getMessage(), e);
			}
		} else {
			throw new RuntimeException("Reaction Rule is null");
		}
		return null;
	}

	private void newMemButtonPressed() {
		computeCurrentSelectedTable();
		Object newObject = null;
		if (currentSelectedTable == structuresTable) {
			try {
				Membrane membrane = bioModel.getModel().createMembrane();
				newObject = membrane;
			} catch (Exception e) {
				e.printStackTrace();
				DialogUtils.showErrorDialog(this, e.getMessage(), e);
			}
		}
		if (newObject != null) {
			for (int i = 0; i < currentSelectedTableModel.getRowCount(); i ++) {
				if (currentSelectedTableModel.getValueAt(i) == newObject) {
					currentSelectedTable.setRowSelectionInterval(i, i);
					break;
				}
			}
		}
	}
	
	private void duplicateButtonPressed() {

		computeCurrentSelectedTable();
		int row = currentSelectedTable.getSelectedRow();
		System.out.println("Duplicate Button Pressed for row " + row);
		if (currentSelectedTable == reactionsTable) {
			ModelProcess mp = reactionTableModel.getValueAt(row);
			if(!(mp instanceof ReactionRule)) {
				return;
			}
			ReactionRule oldRule = (ReactionRule)mp;
				
			if( bioModel.getModel().getNumStructures() == 1) {
				duplicateReactionRule(oldRule, oldRule.getStructure());
			} else if( bioModel.getModel().getNumStructures() > 1) {
				final JPopupMenu menu = new JPopupMenu("Choose compartment");
				for(int i=0; i<bioModel.getModel().getNumStructures(); i++) {
					String ourType = oldRule.getStructure().getTypeName();
					Structure s = bioModel.getModel().getStructure(i);
					String theirType = s.getTypeName();
					if(!ourType.equals(theirType)) {
						continue;	// offer to duplicate only within structures of the same type as the original, 
									// otherwise units will be meaningless (surface vs volume)
					}
					String sName = s.getName();
					JMenuItem menuItem = new JMenuItem("In " + s.getTypeName() + " " + sName);
					menuItem.setIcon(new StructureToolShapeIcon(17));
					menu.add(menuItem);
					menuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							newObject = duplicateReactionRule(oldRule, s);
							// moves selection on the newly created object
//							if (newObject != null) {
//								for (int i = 0; i < currentSelectedTableModel.getRowCount(); i ++) {
//									if (currentSelectedTableModel.getValueAt(i) == newObject) {
//										currentSelectedTable.setRowSelectionInterval(i, i);
//										break;
//									}
//								}
//							}
						}
					});
				}
				menu.show(duplicateButton, 0, duplicateButton.getHeight());
			}
		} else if(currentSelectedTable == speciesTable) {
			SpeciesContext sc = speciesTableModel.getValueAt(row);
			if(!sc.hasSpeciesPattern()) {
				return;
			}
			if( bioModel.getModel().getNumStructures() == 1) {
				duplicateSpecies(sc, sc.getStructure(), bioModel.getModel());
			} else if( bioModel.getModel().getNumStructures() > 1) {
				final JPopupMenu menu = new JPopupMenu("Choose compartment");
				for(int i=0; i<bioModel.getModel().getNumStructures(); i++) {
					String ourType = sc.getStructure().getTypeName();
					Structure s = bioModel.getModel().getStructure(i);
					String theirType = s.getTypeName();
					if(!ourType.equals(theirType)) {
						continue;
					}
					String sName = s.getName();
					JMenuItem menuItem = new JMenuItem("In " + s.getTypeName() + " " + sName);
					menuItem.setIcon(new StructureToolShapeIcon(17));
					menu.add(menuItem);
					menuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							newObject = duplicateSpecies(sc, s, bioModel.getModel());
						}
					});
				}
				menu.show(duplicateButton, 0, duplicateButton.getHeight());
			}
		} else if(currentSelectedTable == observablesTable) {
			RbmObservable o = observableTableModel.getValueAt(row);
			if(o.getSpeciesPatternList().isEmpty()) {
				return;
			}
			if( bioModel.getModel().getNumStructures() == 1) {
				duplicateObservable(o, o.getStructure(), bioModel.getModel());
			} else if( bioModel.getModel().getNumStructures() > 1) {
				final JPopupMenu menu = new JPopupMenu("Choose compartment");
				for(int i=0; i<bioModel.getModel().getNumStructures(); i++) {
					Structure s = bioModel.getModel().getStructure(i);
					String sName = s.getName();
					JMenuItem menuItem = new JMenuItem("In " + s.getTypeName() + " " + sName);
					menuItem.setIcon(new StructureToolShapeIcon(17));
					menu.add(menuItem);
					menuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							newObject = duplicateObservable(o, s, bioModel.getModel());
						}
					});
				}
				menu.show(duplicateButton, 0, duplicateButton.getHeight());
			}
		}
	}
	private ReactionRule duplicateReactionRule(ReactionRule oldRule, Structure structure) {
		try {
			ReactionRule newRule = ReactionRule.duplicate(oldRule, structure);
			Model m = oldRule.getModel();
			m.getRbmModelContainer().addReactionRule(newRule);
			return newRule;
		} catch (PropertyVetoException | ExpressionBindingException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem duplicating " + ReactionRule.typeName + " " + oldRule.getDisplayName());
		}
	}
	private SpeciesContext duplicateSpecies(SpeciesContext oldSpecies, Structure structure, Model model) {
		try {
			SpeciesContext newSpecies = SpeciesContext.duplicate(oldSpecies, structure, model);
			return newSpecies;
		} catch (PropertyVetoException | ExpressionBindingException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem duplicating " + SpeciesContext.typeName + " " + oldSpecies.getDisplayName());
		}
	}
	private RbmObservable duplicateObservable(RbmObservable oldObservable, Structure structure, Model model) {
		try {
			RbmObservable newObservable = RbmObservable.duplicate(oldObservable, structure, model);
			return newObservable;
		} catch (PropertyVetoException | ExpressionBindingException | ModelException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem duplicating " + RbmObservable.typeName + " " + oldObservable.getDisplayName());
		}
	}
	
	private void deleteButtonPressed() {
		try {
			ArrayList<Object> deleteList = new ArrayList<Object>();
			int selectedIndex = tabbedPane.getSelectedIndex();
			if (selectedIndex == ModelPanelTabID.reaction_diagram.ordinal()) {
				deleteList.addAll(Arrays.asList(reactionCartoonEditorPanel.getReactionCartoon().getSelectedObjects()));			
//			} else if (selectedIndex == ModelPanelTabID.structure_diagram.ordinal()) {
//				deleteList.addAll(Arrays.asList(cartoonEditorPanel.getStructureCartoon().getSelectedObjects()));
			} else {
				computeCurrentSelectedTable();
				int[] rows = currentSelectedTable.getSelectedRows();
				if (rows == null || rows.length == 0) {
					return;
				}
				if (currentSelectedTable == speciesTable) {
					for (int r : rows) {
						if (r < speciesTableModel.getRowCount()) {
							SpeciesContext speciesContext = speciesTableModel.getValueAt(r);
							if (speciesContext != null) {
								deleteList.add(speciesContext);
							}
						}
					}
				} else if (currentSelectedTable == molecularTypeTable) {
					// TODO: delete stuff
					for (int r : rows) {
						if (r < molecularTypeTableModel.getRowCount()) {
							MolecularType mt = molecularTypeTableModel.getValueAt(r);
							if (mt != null) {
								deleteList.add(mt);
							}
						}
					}
				} else if (currentSelectedTable == observablesTable) {
					for (int r : rows) {
						if (r < observableTableModel.getRowCount()) {
							RbmObservable o = observableTableModel.getValueAt(r);
							if (o != null) {
								deleteList.add(o);
							}
						}
					}
				} else if (currentSelectedTable == structuresTable) {
					for (int r : rows) {
						if (r < structureTableModel.getRowCount()) {
							Structure rowValue = structureTableModel.getValueAt(r);
							if (rowValue instanceof Feature || rowValue instanceof Membrane) {
								deleteList.add(rowValue);
							}
						}
					}
				} else if (currentSelectedTable == reactionsTable) {
					for (int r : rows) {
						if (r < reactionTableModel.getRowCount()) {
							ModelProcess reaction = reactionTableModel.getValueAt(r);
							if (reaction != null) {
								deleteList.add(reaction);
							}
						}
					}
				}
			}
			if (deleteList.size() == 0) {
				return;
			}
			StringBuilder deleteListText = new StringBuilder();
			for (Object object : deleteList) {
				if (object instanceof SpeciesContext) {
					deleteListText.append("Species\t'" + ((SpeciesContext)object).getName() + "'\n");
				} else if (object instanceof MolecularType) {
					deleteListText.append(((MolecularType)object).getDisplayType() + "\t'" + ((MolecularType)object).getDisplayName() + "'\n");
				} else if (object instanceof RbmObservable) {
					deleteListText.append("Observable\t'" + ((RbmObservable)object).getName() + "'\n");
				} else if (object instanceof ReactionStep) {
					deleteListText.append("Reaction\t'" + ((ReactionStep)object).getName() + "'\n");
				} else if (object instanceof ReactionRule) {
					deleteListText.append("Reaction rule\t'" + ((ReactionRule)object).getName() + "'\n");
				} else if (object instanceof Structure) {
					deleteListText.append("Structure\t'" + ((Structure)object).getName() + "'\n");
				}
			}
			// TODO: once we display reaction rules in the carton editor panel we'll have to change the way we delete reaction rules
			if(deleteList.get(0) instanceof SpeciesContext || deleteList.get(0) instanceof ReactionStep){
				try{
					ArrayList<SpeciesContext> speciesContextArrList = new ArrayList<SpeciesContext>();
					ArrayList<ReactionStep> reactionStepArrList = new ArrayList<ReactionStep>();
					for(Object obj:deleteList){
						if(obj instanceof SpeciesContext){
							speciesContextArrList.add((SpeciesContext)obj);
						}else if(obj instanceof ReactionStep){
							reactionStepArrList.add((ReactionStep)obj);
						}else{
							throw new Exception("Unexpected delete object "+obj.getClass().getName());
						}
					}
					ReactionCartoonTool.deleteReactionsAndSpecies(reactionCartoonEditorPanel,reactionStepArrList.toArray(new ReactionStep[0]),speciesContextArrList.toArray(new SpeciesContext[0]));
				}catch(UserCancelException uce){
					return;
				}
				return;
			}else{
				String confirm = DialogUtils.showOKCancelWarningDialog(this, "Deleting", "You are going to delete the following:\n\n" + deleteListText + "\n Continue?");
				if (confirm.equals(UserMessage.OPTION_CANCEL)) {
					return;
				}
				for (Object object : deleteList) {
					if(object instanceof ReactionRule) {
						ReactionRule rr = (ReactionRule)object;
						bioModel.getModel().getRbmModelContainer().removeReactionRule(rr);
					} else if(object instanceof MolecularType) {
						Map<String, Pair<Displayable, SpeciesPattern>> usedHere = new LinkedHashMap<String, Pair<Displayable, SpeciesPattern>>();
						MolecularType mt = (MolecularType)object;
						if(!bioModel.getModel().getRbmModelContainer().isDeleteAllowed(mt, usedHere)) {
							String errMsg = mt.getDisplayType() + " <b>'" + mt + "'</b> cannot be deleted because it's already being used by:<br>";
							final int MaxListSize = 7;
							int count = 0;
							for(String key : usedHere.keySet()) {
								System.out.println(key);
								if(count >= MaxListSize) {
									errMsg += "<br> ... and more.";
									break;
								}
								Pair<Displayable, SpeciesPattern> o = usedHere.get(key);
								Displayable e = o.one;
								SpeciesPattern sp = o.two;
								errMsg += "<br> - " + e.getDisplayType().toLowerCase() + " <b>" + e.getDisplayName() + "</b>";
								errMsg += ", " + sp.getDisplayType().toLowerCase() + " " + " <b>" + sp.getDisplayName() + "</b>";
								count++;
							}
							errMsg = "<html>" + errMsg + "</html>";
							throw new RuntimeException(errMsg);
						}
						bioModel.getModel().getRbmModelContainer().removeMolecularType(mt);
					} else if(object instanceof RbmObservable) {
						RbmObservable o = (RbmObservable)object;
						bioModel.getModel().getRbmModelContainer().removeObservable(o);
					} else {
						bioModel.getModel().removeObject(object);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}
	}
	
	private void bioModelChange() {
		if (bioModel == null) {
			return;
		}
		reactionCartoonEditorPanel.setModel(bioModel.getModel());
		reactionCartoonEditorPanel.setStructureSuite(new AllStructureSuite(this));
		bioModel.getRelationshipModel().removeRelationShipListener(reactionCartoonEditorPanel.getReactionCartoonFull());
		bioModel.getRelationshipModel().removeRelationShipListener(reactionCartoonEditorPanel.getReactionCartoonMolecule());
		bioModel.getRelationshipModel().removeRelationShipListener(reactionCartoonEditorPanel.getReactionCartoonRule());
		bioModel.getRelationshipModel().addRelationShipListener(reactionCartoonEditorPanel.getReactionCartoonFull());
		bioModel.getRelationshipModel().addRelationShipListener(reactionCartoonEditorPanel.getReactionCartoonMolecule());
		bioModel.getRelationshipModel().addRelationShipListener(reactionCartoonEditorPanel.getReactionCartoonRule());
		reactionCartoonEditorPanel.getReactionCartoonFull().refreshRelationshipInfo(bioModel.getRelationshipModel());
		reactionCartoonEditorPanel.getReactionCartoonMolecule().refreshRelationshipInfo(bioModel.getRelationshipModel());
		reactionCartoonEditorPanel.getReactionCartoonRule().refreshRelationshipInfo(bioModel.getRelationshipModel());
//		cartoonEditorPanel.setBioModel(bioModel);
		reactionTableModel.setBioModel(bioModel);
		structureTableModel.setBioModel(bioModel);
		speciesTableModel.setBioModel(bioModel);
		molecularTypeTableModel.setBioModel(bioModel);
		observableTableModel.setBioModel(bioModel);
//		reactionCartoonEditorPanel.specialLayout();
	}
	
	private JTable currentSelectedTable = null;
	private BioModelEditorRightSideTableModel<?> currentSelectedTableModel = null;
	private void computeCurrentSelectedTable() {
		currentSelectedTable = null;
		currentSelectedTableModel = null;
		int selectedIndex = tabbedPane.getSelectedIndex();
		if (selectedIndex == ModelPanelTabID.reaction_table.ordinal()) {
			currentSelectedTable = reactionsTable;
			currentSelectedTableModel = reactionTableModel;
		} else if (selectedIndex == ModelPanelTabID.structure_table.ordinal()) {
			currentSelectedTable = structuresTable;
			currentSelectedTableModel = structureTableModel;
		} else if (selectedIndex == ModelPanelTabID.species_table.ordinal()) {
			currentSelectedTable = speciesTable;
			currentSelectedTableModel = speciesTableModel;
		} else if (selectedIndex == ModelPanelTabID.species_definitions_table.ordinal()) {
			currentSelectedTable = molecularTypeTable;
			currentSelectedTableModel = molecularTypeTableModel;
		} else if (selectedIndex == ModelPanelTabID.observables_table.ordinal()) {
			currentSelectedTable = observablesTable;
			currentSelectedTableModel = observableTableModel;
		}
	}
	
	private void tableSelectionChanged() {
		computeCurrentSelectedTable();
		if (currentSelectedTableModel != null) {
			setSelectedObjectsFromTable(currentSelectedTable, currentSelectedTableModel);
			int[] rows = currentSelectedTable.getSelectedRows();
			deleteButton.setEnabled(rows != null && rows.length > 0 && (rows.length > 1 || currentSelectedTableModel.getValueAt(rows[0]) != null));			
		}
	}
	
	public void setDocumentManager(DocumentManager documentManager) {
//		cartoonEditorPanel.setDocumentManager(documentManager);	
		reactionCartoonEditorPanel.setDocumentManager(documentManager);
	}
	
	private void showDiagramView() {
		if (tabbedPane.getSelectedIndex() == ModelPanelTabID.reaction_diagram.ordinal()) {
			if (tabbedPane.getComponentAt(ModelPanelTabID.reaction_diagram.ordinal()) != modelPanelTabs[ModelPanelTabID.reaction_diagram.ordinal()].getComponent()) {
				ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(this);
				if (childWindowManager!=null){
					ChildWindow childWindow = childWindowManager.getChildWindowFromContext(this.reactionCartoonEditorPanel);
					if (childWindow!=null){
						childWindow.setIsCenteredOnParent();
						childWindow.show();
					}
				}
			}
		}
	}
	
	private void floatDiagramView(boolean bFloating) {
	
		ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(this);
		
		if (bFloating) {
			//
			// insert dummy panel into tabbed pane, real one is floating now.
			//
			tabbedPane.setComponentAt(ModelPanelTabID.reaction_diagram.ordinal(), new JPanel());  
			
			//
			// create new panel to add to client window (and hold the reactionCartoonEditorPanel)
			//
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(p, BorderLayout.NORTH);
			panel.add(reactionCartoonEditorPanel, BorderLayout.CENTER);
			ChildWindow childWindow = childWindowManager.addChildWindow(panel, reactionCartoonEditorPanel, "model diagram");
			childWindow.setSize(500, 400);
			childWindow.setIsCenteredOnParent();
			childWindow.addChildWindowListener(new ChildWindowListener() {				
				public void closing(ChildWindow childWindow) {
					reactionCartoonEditorPanel.setFloatingRequested(false);
				}				
			});

			childWindow.show();
//			diagramViewInternalFrame.setFrameIcon(new ImageIcon(getClass().getResource("/images/step.gif")));
		} else {
			if (childWindowManager!=null){
				ChildWindow childWindow = childWindowManager.getChildWindowFromContext(reactionCartoonEditorPanel);
				if (childWindow!=null){
					childWindowManager.closeChildWindow(childWindow);
				}
			}
			tabbedPane.setComponentAt(ModelPanelTabID.reaction_diagram.ordinal(), modelPanelTabs[ModelPanelTabID.reaction_diagram.ordinal()].getComponent());
			tabbedPane.setSelectedIndex(ModelPanelTabID.reaction_diagram.ordinal());
		}
	}
	
	private void selectTab(ModelPanelTabID tabid) {		
		tabbedPane.setSelectedIndex(tabid.ordinal());
	}
	
	@Override
	protected void onActiveViewChange(ActiveView activeView) {
		super.onActiveViewChange(activeView);
		SimulationContext selectedSimContext = activeView.getSimulationContext();
		DocumentEditorTreeFolderClass folderClass = activeView.getDocumentEditorTreeFolderClass();
		if (selectedSimContext != null || folderClass == null) {
			return;
		}		
		switch (folderClass) {
		case REACTIONS_NODE:
			selectTab(ModelPanelTabID.reaction_table);
			break;
		case STRUCTURES_NODE:
			selectTab(ModelPanelTabID.structure_table);
			break;
		case SPECIES_NODE:
			selectTab(ModelPanelTabID.species_table);
			break;
		case MOLECULAR_TYPES_NODE:
			selectTab(ModelPanelTabID.species_definitions_table);
			break;
		case OBSERVABLES_NODE:
			selectTab(ModelPanelTabID.observables_table);
			break;
		case REACTION_DIAGRAM_NODE:
			selectTab(ModelPanelTabID.reaction_diagram);
			break;
//		case STRUCTURE_DIAGRAM_NODE:
//			selectTab(ModelPanelTabID.structure_diagram);
//			break;
		}
	}
	
	@Override
	public void setIssueManager(IssueManager issueManager) {
		super.setIssueManager(issueManager);
		reactionTableModel.setIssueManager(issueManager);
		speciesTableModel.setIssueManager(issueManager);
		molecularTypeTableModel.setIssueManager(issueManager);
		observableTableModel.setIssueManager(issueManager);
		structureTableModel.setIssueManager(issueManager);
//		AbstractComponentShape.setIssueListProvider( () -> issueManager.getIssueList() );
	}

	private void showPathwayLinks() {
		BioModelEntityObject selectedBioModelEntityObject = getSelectedBioModelEntityObject();
		if (selectedBioModelEntityObject != null) {
			Set<RelationshipObject> relationshipSet = bioModel.getRelationshipModel().getRelationshipObjects(selectedBioModelEntityObject);
			if (relationshipSet.size() > 0) {
				ArrayList<BioPaxObject> selectedBioPaxObjects = new ArrayList<BioPaxObject>();
				for(RelationshipObject re: relationshipSet){
					selectedBioPaxObjects.add(re.getBioPaxObject());
				}
				selectionManager.followHyperlink(new ActiveView(null,DocumentEditorTreeFolderClass.PATHWAY_NODE, ActiveViewID.pathway),selectedBioPaxObjects.toArray(new BioPaxObject[0]));
			}
		}
	}

	private void editPathwayLinks() {
		BioModelEntityObject selectedBioModelEntityObject = getSelectedBioModelEntityObject();
		if (relationshipPanel == null) {
			relationshipPanel = new PhysiologyRelationshipPanel();
			relationshipPanel.setBioModel(bioModel);
		}
		relationshipPanel.setBioModelEntityObject(selectedBioModelEntityObject);
		DialogUtils.showComponentCloseDialog(BioModelEditorModelPanel.this, relationshipPanel, "Edit Pathway Links");
		refreshButtons();
	}
	
	ReactionEditorPanel reactionEditorPanel = null;
	private static class ReactionEditorPanel extends JPanel {
		JComboBox structureComboBox = new JComboBox();
		JTextField nameTextField = new JTextField(); 
		TextFieldAutoCompletion equationTextField = new TextFieldAutoCompletion();
		
		public ReactionEditorPanel() {
			super();
			equationTextField.setColumns(30);
			equationTextField.setAutoCompleteSymbolFilter(new AutoCompleteSymbolFilter() {
				
				public boolean acceptFunction(String funcName) {
					return false;
				}
				
				public boolean accept(SymbolTableEntry ste) {
					Structure structure = (Structure) structureComboBox.getSelectedItem();
					
					if (ste instanceof SpeciesContext) {
//						SpeciesContext sc = (SpeciesContext) ste;
//						if (sc.getStructure() == structure) {
//							return true;
//						}
//						if (structure instanceof Membrane) {
//							if (((Membrane)structure).getInsideFeature() == sc.getStructure() 
//									|| ((Membrane)structure).getOutsideFeature() == sc.getStructure()) {
//								return true;
//							}
//						}
							return true;
						}
					return false;					
				}
			});
			
			setLayout(new GridBagLayout());
			int gridy = 0;
			GridBagConstraints gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = gridy;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.LINE_END;		
			JLabel label = new JLabel("Structure");
			add(label, gbc);
			
			gbc.gridx = 1; 
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.fill = java.awt.GridBagConstraints.BOTH;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.LINE_START;		
			add(structureComboBox, gbc);

			gbc.gridx = 2; 
			gbc.gridy = gridy;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.LINE_END;		
			label = new JLabel("(where reaction occurs)");
			add(label, gbc);
			
			gridy ++;
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = gridy;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.LINE_END;		
			label = new JLabel("Reaction Name");
			add(label, gbc);
			
			gbc.gridx = 1; 
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.fill = java.awt.GridBagConstraints.BOTH;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.LINE_START;		
			add(nameTextField, gbc);			
			
			gridy ++;
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = gridy;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.LINE_END;		
			label = new JLabel("Equation");
			add(label, gbc);
			
			gbc.gridx = 1; 
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.fill = java.awt.GridBagConstraints.BOTH;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.LINE_START;		
			add(equationTextField, gbc);
			
			gbc.gridx = 2; 
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.fill = java.awt.GridBagConstraints.BOTH;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.LINE_START;		
			label = new JLabel("(e.g. a+b->c)");
			add(label, gbc);
			
			structureComboBox.setRenderer(new DefaultListCellRenderer() {

				@Override
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					super.getListCellRendererComponent(list, value, index, isSelected,
							cellHasFocus);
					if (value instanceof Structure) {
						setText(((Structure) value).getName());
					}
					return this;
				}
				
			});
			setBackground(Color.white);
		}

		void setModel(Model model) {			
			DefaultComboBoxModel dataModel = new DefaultComboBoxModel();
			for (Structure s : model.getStructures()) {
				dataModel.addElement(s);
			}
			structureComboBox.setModel(dataModel);
			nameTextField.setText(model.getReactionName());
			equationTextField.setSymbolTable(model);
			equationTextField.setText(null);
		}
		
		Structure getStructure() {
			return (Structure) structureComboBox.getSelectedItem();
		}
		String getEquationString() {
			return equationTextField.getText();
		}

		public String getReactionName() {
			return nameTextField.getText();
		}
	}
	
	private void addNewReaction() {
		if (reactionEditorPanel == null) {
			reactionEditorPanel = new ReactionEditorPanel();
		}
		Model model = getModel();
		reactionEditorPanel.setModel(model);
		while (true) {
			int confirm = DialogUtils.showComponentOKCancelDialog(this, reactionEditorPanel, "New Reaction");
			if (confirm == javax.swing.JOptionPane.OK_OPTION) {
				Structure reactionStructure = reactionEditorPanel.getStructure();
				String reactionName = reactionEditorPanel.getReactionName();
				String equationString = reactionEditorPanel.getEquationString();
				try {
					if (reactionName == null || reactionName.trim().length() == 0 || equationString == null || equationString.trim().length() == 0) {
						throw new RuntimeException("Reaction name or equation cannot be empty.");
					}
					if (getModel().getReactionStep(reactionName) != null) {
						throw new RuntimeException("Reaction '" + reactionName + "' already exists.");
					}
					ReactionStep simpleReaction = new SimpleReaction(model, reactionStructure, "dummy", true);
					ReactionParticipant[] rpArray = ModelProcessEquation.parseReaction(simpleReaction, getModel(), equationString);
//					StructureTopology structTopology = getModel().getStructureTopology();
//					for (ReactionParticipant reactionParticipant : rpArray) {
//						if (reactionParticipant.getStructure() == reactionStructure) {
//							continue;
//						}
//						if (reactionStructure instanceof Feature) {
//							throw new RuntimeException("Species '" + reactionParticipant.getSpeciesContext().getName() + "' must be in the same volume compartment as reaction.");
//						} else if (reactionStructure instanceof Membrane) {
//							if (structTopology.getInsideFeature((Membrane)reactionStructure) != reactionParticipant.getStructure()
//									&& (structTopology.getOutsideFeature((Membrane)reactionStructure)) != reactionParticipant.getStructure()) {
//								throw new RuntimeException("Species '" + reactionParticipant.getSpeciesContext().getName() + "' must be adjacent to " +
//										"or within reaction's structure '" + reactionStructure.getName() + "'.");
//							}
//						}
//					}
					simpleReaction = getModel().createSimpleReaction(reactionStructure);
					for (ReactionParticipant rp : rpArray) {
						SpeciesContext speciesContext = rp.getSpeciesContext();
						if (bioModel.getModel().getSpeciesContext(speciesContext.getName()) == null) {
							bioModel.getModel().addSpecies(speciesContext.getSpecies());
							bioModel.getModel().addSpeciesContext(speciesContext);
						}
					}
					simpleReaction.setReactionParticipants(rpArray);
					simpleReaction.setName(reactionName);
					setSelectedObjects(new Object[]{simpleReaction});
					break;
				} catch (Exception e) {
					e.printStackTrace();
					DialogUtils.showErrorDialog(this, e.getMessage());
				}
			} else {
				break;
			}
		}
	}
	private boolean hasAnnotation(Identifiable identifiable) {
		Identifiable entity = AnnotationsPanel.getIdentifiable(identifiable);
		String freeText = bioModel.getVCMetaData().getFreeTextAnnotation(entity);
		MiriamManager miriamManager = bioModel.getVCMetaData().getMiriamManager();
		TreeMap<Identifiable, Map<MiriamRefGroup, MIRIAMQualifier>> miriamDescrHeir = miriamManager.getMiriamTreeMap();
		Map<MiriamRefGroup, MIRIAMQualifier> refGroupMap = miriamDescrHeir.get(entity);

		Icon icon = VCellIcons.issueGoodIcon;
		if(freeText != null && !freeText.isEmpty()) {
			return true;
		} else if(refGroupMap != null && !refGroupMap.isEmpty()) {
			return true;
		}
		return false;
	}
}
