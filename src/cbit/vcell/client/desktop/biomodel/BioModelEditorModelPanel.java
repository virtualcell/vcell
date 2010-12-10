package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.JDesktopPaneEnhanced;
import org.vcell.util.gui.JInternalFrameEnhanced;

import cbit.gui.graph.GraphModel;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.desktop.biomodel.BioModelEditor.BioModelEditorSelection;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.graph.CartoonEditorPanelFixed;
import cbit.vcell.graph.ReactionCartoonEditorPanel;
import cbit.vcell.graph.structures.AllStructureSuite;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.NameScope;

@SuppressWarnings("serial")
public class BioModelEditorModelPanel extends JPanel implements Model.Owner {
	protected static final String PROPERTY_NAME_BIO_MODEL = "bioModel";
	public enum ModelPanelTab {
		reaction_diagram("Reaction Diagram"),
		structure_diagram("Structure Diagram"),
		reaction_table("Reactions"),
		structure_table("Structures"),
		species_table("Species"),
		global_parameter_table("Parameters");
		
		private String name = null;
		private JComponent component = null;
		ModelPanelTab(String name) {
			this.name = name;
		}
		public void setComponent(JComponent c) {
			component = c;
		}
		public final String getName() {
			return name;
		}
		public final JComponent getComponent() {
			return component;
		}
	}
	private JButton newButton = null;
	private JButton deleteButton = null;
	private EditorScrollTable structuresTable = null;
	private EditorScrollTable reactionsTable = null;
	private EditorScrollTable speciesTable = null;
	private EditorScrollTable parametersTable = null;
	private BioModelEditorStructureTableModel structureTableModel = null;
	private BioModelEditorReactionTableModel reactionTableModel = null;
	private BioModelEditorSpeciesTableModel speciesTableModel = null;
	private BioModelEditorGlobalParameterTableModel parametersTableModel = null;
	private BioModel bioModel;
	private JTextField textFieldSearch = null;
	private BioModelEditorSelection bioModelEditorSelection = null;
	private JTabbedPane tabbedPane = null;
	private JPanel emptyPanel = null;
	
	private CartoonEditorPanelFixed cartoonEditorPanel = null;
	private ReactionCartoonEditorPanel reactionCartoonEditorPanel = null;
	private ReactionPropertiesPanel reactionStepPropertiesPanel = null;
	private SpeciesPropertiesPanel speciesPropertiesPanel = null;
	private StructurePropertiesPanel structurePropertiesPanel = null;
	private ModelParameterPropertiesPanel modelParameterPropertiesPanel = null;
	private JSplitPane splitPane = null;
	private JDesktopPaneEnhanced desktopPane = null;
	private JInternalFrameEnhanced diagramViewInternalFrame = null;	
	private SelectionManager selectionManager = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private class InternalEventHandler implements ActionListener, PropertyChangeListener, DocumentListener, ListSelectionListener, ChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditorModelPanel.this && evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL)) {
				bioModelChange();
			} else if (evt.getSource() == selectionManager && evt.getPropertyName().equals(SelectionManager.PROPERTY_NAME_SELECTED_OBJECTS)) {
				propogateSelections();
			} else if (evt.getPropertyName().equals(GraphModel.PROPERTY_NAME_SELECTED)) {
				Object[] selectedObjects = (Object[]) evt.getNewValue();
				selectionManager.setSelectedObjects(selectedObjects);
			} else if (evt.getSource() == reactionCartoonEditorPanel && evt.getPropertyName().equals(ReactionCartoonEditorPanel.PROPERTY_NAME_FLOATING)) {
				floatDiagramView((Boolean) evt.getNewValue());
			}
		}
		
		public void insertUpdate(DocumentEvent e) {
			searchTable();
		}

		public void removeUpdate(DocumentEvent e) {
			searchTable();
		}

		public void changedUpdate(DocumentEvent e) {
			searchTable();
		}
		
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == newButton) {
				newButtonPressed();
			} else if (e.getSource() == deleteButton) {
				deleteButtonPressed();
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
	}
	
	public BioModelEditorModelPanel() {
		super();
		addPropertyChangeListener(eventHandler);
		initialize();
	}

	public void tabbedPaneSelectionChanged() {
		Component component = tabbedPane.getSelectedComponent();
		newButton.setEnabled(component != ModelPanelTab.reaction_diagram.getComponent()
				&& component != ModelPanelTab.structure_diagram.getComponent());
		
	}

	public void propogateSelections() {
		if (selectionManager == null) {
			return;
		}
		Object[] selectedObjects = selectionManager.getSelectedObjects();
		deleteButton.setEnabled(selectedObjects != null && selectedObjects.length > 0);
		setBottomPanelOnSelection(selectedObjects);
		reactionCartoonEditorPanel.getReactionCartoon().setSelectedObjects(selectedObjects);
		cartoonEditorPanel.getStructureCartoon().setSelectedObjects(selectedObjects);
		structuresTable.clearSelection();
		reactionsTable.clearSelection();
		speciesTable.clearSelection();
		parametersTable.clearSelection();
		for (Object object : selectedObjects) {
			if (object instanceof SpeciesContext) {
				for (int i = 0; i < speciesTableModel.getDataSize(); i ++) {
					if (speciesTableModel.getValueAt(i) == object) {
						speciesTable.addRowSelectionInterval(i, i);
						break;
					}
				}
			} else 	if (object instanceof Structure) {
				for (int i = 0; i < structureTableModel.getDataSize(); i ++) {
					if (structureTableModel.getValueAt(i) == object) {
						structuresTable.addRowSelectionInterval(i, i);
						break;
					}
				}
			} else 	if (object instanceof ReactionStep) {
				for (int i = 0; i < reactionTableModel.getDataSize(); i ++) {
					if (reactionTableModel.getValueAt(i) == object) {
						reactionsTable.addRowSelectionInterval(i, i);
						break;
					}
				}
			} else 	if (object instanceof Parameter) {
				for (int i = 0; i < parametersTableModel.getDataSize(); i ++) {
					if (parametersTableModel.getValueAt(i) == object) {
						parametersTable.addRowSelectionInterval(i, i);
						break;
					}
				}
			}
		} 
	}

	private void initialize(){
		newButton = new JButton("New");
		deleteButton = new JButton("Delete");
		textFieldSearch = new JTextField(10);
		
		structuresTable = new EditorScrollTable();
		reactionsTable = new EditorScrollTable();
		speciesTable = new EditorScrollTable();
		parametersTable = new EditorScrollTable();		
		structureTableModel = new BioModelEditorStructureTableModel(structuresTable);
		reactionTableModel = new BioModelEditorReactionTableModel(reactionsTable);
		speciesTableModel = new BioModelEditorSpeciesTableModel(speciesTable);
		parametersTableModel = new BioModelEditorGlobalParameterTableModel(parametersTable, false);
		structuresTable.setModel(structureTableModel);
		reactionsTable.setModel(reactionTableModel);
		speciesTable.setModel(speciesTableModel);
		parametersTable.setModel(parametersTableModel);
		
		reactionCartoonEditorPanel = new ReactionCartoonEditorPanel();
		reactionCartoonEditorPanel.addPropertyChangeListener(eventHandler);
		reactionCartoonEditorPanel.getReactionCartoon().addPropertyChangeListener(eventHandler);
		reactionStepPropertiesPanel = new ReactionPropertiesPanel();
		cartoonEditorPanel  = new CartoonEditorPanelFixed();
		cartoonEditorPanel.getStructureCartoon().addPropertyChangeListener(eventHandler);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		topPanel.add(new JLabel("Search "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		topPanel.add(textFieldSearch, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,100,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		newButton.setPreferredSize(deleteButton.getPreferredSize());
		topPanel.add(newButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.insets = new Insets(4,4,4,20);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		topPanel.add(deleteButton, gbc);
		
		tabbedPane = new JTabbedPane();
		ModelPanelTab.reaction_diagram.setComponent(reactionCartoonEditorPanel);
		ModelPanelTab.structure_diagram.setComponent(cartoonEditorPanel);
		ModelPanelTab.reaction_table.setComponent(reactionsTable.getEnclosingScrollPane());
		ModelPanelTab.structure_table.setComponent(structuresTable.getEnclosingScrollPane());
		ModelPanelTab.species_table.setComponent(speciesTable.getEnclosingScrollPane());
		ModelPanelTab.global_parameter_table.setComponent(parametersTable.getEnclosingScrollPane());
		tabbedPane.addChangeListener(eventHandler);
		
		for (ModelPanelTab tab : ModelPanelTab.values()) {
			tabbedPane.addTab(tab.getName(), tab.getComponent());
		}
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.BOTH;
		topPanel.add(tabbedPane, gbc);		
		
		emptyPanel = new JPanel(new GridBagLayout());
		emptyPanel.setBackground(Color.white);
		JLabel label = new JLabel("Select one reaction, structure or species to show properties.");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(10,10,4,4);
		gbc.gridy = 0;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.PAGE_START;
		emptyPanel.add(label, gbc);
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(topPanel);
		splitPane.setBottomComponent(emptyPanel);

		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerLocation(450);
		splitPane.setResizeWeight(0.7);
		
		newButton.addActionListener(eventHandler);
		deleteButton.addActionListener(eventHandler);
		deleteButton.setEnabled(false);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		structuresTable.getSelectionModel().addListSelectionListener(eventHandler);
		reactionsTable.getSelectionModel().addListSelectionListener(eventHandler);
		speciesTable.getSelectionModel().addListSelectionListener(eventHandler);
		parametersTable.getSelectionModel().addListSelectionListener(eventHandler);
		DefaultScrollTableCellRenderer structureRenderer = new DefaultScrollTableCellRenderer(){

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				if (value instanceof Structure) {
					setText(((Structure)value).getName());
				}
				return this;
			}			
		};
		structuresTable.setDefaultRenderer(Structure.class, structureRenderer);
		speciesTable.setDefaultRenderer(Structure.class, structureRenderer);
		reactionsTable.setDefaultRenderer(Structure.class, structureRenderer);
		
		parametersTable.setDefaultRenderer(NameScope.class, new DefaultScrollTableCellRenderer(){

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row,
					int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				if (value instanceof NameScope) {
					NameScope nameScope = (NameScope)value;
					String text = nameScope.getName();
					if (nameScope instanceof Model.ModelNameScope) {
						text = "Global";
					}
					setText(text);
				}
				return this;
			}
			
		});
		
		reactionCartoonEditorPanel.getReactionCartoon().addPropertyChangeListener(eventHandler);
	}
	
	public void setBioModel(BioModel newValue) {
		BioModel oldValue = bioModel;
		bioModel = newValue;		
		firePropertyChange(PROPERTY_NAME_BIO_MODEL, oldValue, newValue);
	}
	
	public Model getModel() {
		return bioModel.getModel();
	}
	
	private void searchTable() {
		String text = textFieldSearch.getText();
//		tableModel.setSearchText(text);
	}
	
	private void newButtonPressed() {
		
	}
	private void deleteButtonPressed() {
		
	}
	
	private void bioModelChange() {
		if (bioModel == null) {
			return;
		}
		reactionCartoonEditorPanel.setModel(bioModel.getModel());
		reactionCartoonEditorPanel.setStructureSuite(new AllStructureSuite(this));
		cartoonEditorPanel.setBioModel(bioModel);
		reactionTableModel.setBioModel(bioModel);
		structureTableModel.setBioModel(bioModel);
		speciesTableModel.setBioModel(bioModel);
		parametersTableModel.setBioModel(bioModel);	
	}

	private void tableSelectionChanged() {
		JTable currentSelectedTable = null;
		BioModelEditorRightSideTableModel<?> currentSelectedTableModel = null;
		Component selectedTabComponent = tabbedPane.getSelectedComponent();
		if (selectedTabComponent == reactionsTable.getEnclosingScrollPane()) {
			currentSelectedTable = reactionsTable;
			currentSelectedTableModel = reactionTableModel;
		} else if (selectedTabComponent == structuresTable.getEnclosingScrollPane()) {
			currentSelectedTable = structuresTable;
			currentSelectedTableModel = structureTableModel;
		} else if (selectedTabComponent == speciesTable.getEnclosingScrollPane()) {
			currentSelectedTable = speciesTable;
			currentSelectedTableModel = speciesTableModel;
		} else if (selectedTabComponent == parametersTable.getEnclosingScrollPane()) {
			currentSelectedTable = parametersTable;
			currentSelectedTableModel = parametersTableModel;
		}	
	
		if (currentSelectedTableModel != null) {
			int[] rows = currentSelectedTable.getSelectedRows();
			if (rows != null) {
				Object[] selectedObjects = new Object[rows.length];
				for (int i = 0; i < rows.length; i ++) {
					selectedObjects[i] = currentSelectedTableModel.getValueAt(rows[i]);
				}
				selectionManager.setSelectedObjects(selectedObjects);
			}
		}
	}
	
	private ReactionPropertiesPanel getReactionPropertiesPanel() {
		if (reactionStepPropertiesPanel == null) {
			reactionStepPropertiesPanel = new ReactionPropertiesPanel();
		}
		return reactionStepPropertiesPanel;
	}
	private SpeciesPropertiesPanel getSpeciesPropertiesPanel() {
		if (speciesPropertiesPanel == null) {
			speciesPropertiesPanel = new SpeciesPropertiesPanel();
		}
		return speciesPropertiesPanel;
	}
	private StructurePropertiesPanel getStructurePropertiesPanel() {
		if (structurePropertiesPanel == null) {
			structurePropertiesPanel = new StructurePropertiesPanel();
		}
		return structurePropertiesPanel;
	}
	private ModelParameterPropertiesPanel getModelParameterPropertiesPanel() {
		if (modelParameterPropertiesPanel == null) {
			modelParameterPropertiesPanel = new ModelParameterPropertiesPanel();
		}
		return modelParameterPropertiesPanel;
	}
	
	public void setBottomPanelOnSelection(Object[] selections) {
		JComponent bottomComponent = emptyPanel;
		if (selections != null && selections.length == 1) {
			Object singleSelection = selections[0];
			if (singleSelection instanceof ReactionStep) {
				bottomComponent = getReactionPropertiesPanel();
				getReactionPropertiesPanel().setReactionStep((ReactionStep)singleSelection);
			} else if (singleSelection instanceof SpeciesContext) {
				bottomComponent = getSpeciesPropertiesPanel();
				getSpeciesPropertiesPanel().setModel(bioModel.getModel());
				getSpeciesPropertiesPanel().setSpeciesContext((SpeciesContext) singleSelection);
			} else if (singleSelection instanceof Structure) {
				bottomComponent = getStructurePropertiesPanel();
				getStructurePropertiesPanel().setModel(bioModel.getModel());
				getStructurePropertiesPanel().setStructure((Structure) singleSelection);
			} else 	if (singleSelection instanceof ModelParameter) {
				bottomComponent = getModelParameterPropertiesPanel();
				getModelParameterPropertiesPanel().setModelParameter((ModelParameter) singleSelection);
			}

		}
		if (splitPane.getBottomComponent() != bottomComponent) {
			splitPane.setBottomComponent(bottomComponent);
		}
		splitPane.setDividerLocation(0.7);
	}

	private final void setBioModelEditorSelection(BioModelEditorSelection newValue) {
		BioModelEditorSelection oldValue = this.bioModelEditorSelection;
		this.bioModelEditorSelection = newValue;
		firePropertyChange(BioModelEditor.PROPERTY_NAME_BIOMODEL_EDITOR_SELECTION, oldValue, newValue);
	}
	
	public void setDocumentManager(DocumentManager documentManager) {
		cartoonEditorPanel.setDocumentManager(documentManager);		
	}
	
	public void selectTab(ModelPanelTab tab) {
		if (tab == null) {
			return;
		}
		tabbedPane.setSelectedComponent(tab.getComponent());
	}
	
	private void floatDiagramView(boolean bFloating) {
		if (desktopPane == null) {
			desktopPane = (JDesktopPaneEnhanced)JOptionPane.getDesktopPaneForComponent(this);
		}
		if (desktopPane == null) {
			return;
		}
		if (bFloating) {
			diagramViewInternalFrame = new JInternalFrameEnhanced("Reaction Diagram View");
			tabbedPane.remove(ModelPanelTab.reaction_diagram.getComponent());
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(p, BorderLayout.NORTH);
			panel.add(reactionCartoonEditorPanel, BorderLayout.CENTER);
			diagramViewInternalFrame.setResizable(true);
			diagramViewInternalFrame.setMaximizable(true);
			diagramViewInternalFrame.setIconifiable(true);
			diagramViewInternalFrame.add(panel);
			diagramViewInternalFrame.pack();
			BeanUtils.centerOnComponent(diagramViewInternalFrame, this);
			DocumentWindowManager.showFrame(diagramViewInternalFrame, desktopPane);
		} else {	
			DocumentWindowManager.close(diagramViewInternalFrame, desktopPane);			
			tabbedPane.insertTab(ModelPanelTab.reaction_diagram.getName(), null, ModelPanelTab.reaction_diagram.getComponent(), null, 0);
			tabbedPane.setSelectedComponent(ModelPanelTab.reaction_diagram.getComponent());
		}
	}

	public final void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
		if (selectionManager != null) {
			selectionManager.removePropertyChangeListener(eventHandler);
			selectionManager.addPropertyChangeListener(eventHandler);
		}
	}
}
