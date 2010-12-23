package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.JDesktopPaneEnhanced;
import org.vcell.util.gui.JInternalFrameEnhanced;

import cbit.gui.graph.GraphModel;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.graph.CartoonEditorPanelFixed;
import cbit.vcell.graph.ReactionCartoonEditorPanel;
import cbit.vcell.graph.structures.AllStructureSuite;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.NameScope;

@SuppressWarnings("serial")
public class BioModelEditorModelPanel extends DocumentEditorSubPanel implements Model.Owner {
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
	private JButton searchButton = null;
	private JButton showAllButton = null;
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
	private JTabbedPane tabbedPane = null;
	
	private CartoonEditorPanelFixed cartoonEditorPanel = null;
	private ReactionCartoonEditorPanel reactionCartoonEditorPanel = null;
	private JDesktopPaneEnhanced desktopPane = null;
	private JInternalFrameEnhanced diagramViewInternalFrame = null;	

	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private class InternalEventHandler implements ActionListener, PropertyChangeListener, ListSelectionListener, ChangeListener, MouseListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditorModelPanel.this && evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL)) {
				bioModelChange();
			} else if (evt.getPropertyName().equals(GraphModel.PROPERTY_NAME_SELECTED)) {
				Object[] selectedObjects = (Object[]) evt.getNewValue();
				setSelectedObjects(selectedObjects);
			} else if (evt.getSource() == reactionCartoonEditorPanel && evt.getPropertyName().equals(ReactionCartoonEditorPanel.PROPERTY_NAME_FLOATING)) {
				floatDiagramView((Boolean) evt.getNewValue());
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == newButton) {
				newButtonPressed();
			} else if (e.getSource() == deleteButton) {
				deleteButtonPressed();
			} else if (e.getSource() == showAllButton) {
				showAllButtonPressed();
			} else if (e.getSource() == textFieldSearch || e.getSource() == searchButton) {
				searchTable();
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
			}			
		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public BioModelEditorModelPanel() {
		super();
		addPropertyChangeListener(eventHandler);
		initialize();
	}

	public void tabbedPaneSelectionChanged() {
		Component component = tabbedPane.getSelectedComponent();
		if (tabbedPane.getSelectedIndex() == ModelPanelTab.reaction_diagram.ordinal()
				|| component == ModelPanelTab.structure_diagram.getComponent()) {
			newButton.setEnabled(false);
			deleteButton.setEnabled(false);
			searchButton.setEnabled(false);
			showAllButton.setEnabled(false);
			textFieldSearch.setEditable(false);
		} else {
			newButton.setEnabled(true);
			showAllButton.setEnabled(true);
			searchButton.setEnabled(true);
			textFieldSearch.setEditable(true);
			computeCurrentSelectedTable();
			if (currentSelectedTableModel != null) {
				int[] rows = currentSelectedTable.getSelectedRows();
				if (currentSelectedTable == parametersTable) {
					deleteButton.setEnabled(false);
					for (int i = 0; i < rows.length; i ++) {
						if (currentSelectedTableModel.getValueAt(rows[i]) instanceof ModelParameter) {
							deleteButton.setEnabled(true);
							break;
						}
					}
				} else {
					deleteButton.setEnabled(rows != null && rows.length > 0 && (rows.length > 1 || rows[0] < currentSelectedTableModel.getDataSize()));
				}			
			}
		}
	}

	public void onSelectedObjectsChange(Object[] selectedObjects) {
		reactionCartoonEditorPanel.getReactionCartoon().setSelectedObjects(selectedObjects);
		cartoonEditorPanel.getStructureCartoon().setSelectedObjects(selectedObjects);
		setTableSelections(selectedObjects, structuresTable, structureTableModel);
		setTableSelections(selectedObjects, reactionsTable, reactionTableModel);
		setTableSelections(selectedObjects, speciesTable, speciesTableModel);
		setTableSelections(selectedObjects, parametersTable, parametersTableModel);		
//		structuresTable.clearSelection();
//		reactionsTable.clearSelection();
//		speciesTable.clearSelection();
//		parametersTable.clearSelection();
//		for (Object object : selectedObjects) {
//			if (object instanceof SpeciesContext) {
//				for (int i = 0; i < speciesTableModel.getDataSize(); i ++) {
//					if (speciesTableModel.getValueAt(i) == object) {
//						speciesTable.addRowSelectionInterval(i, i);
//						break;
//					}
//				}
//			} else 	if (object instanceof Structure) {
//				for (int i = 0; i < structureTableModel.getDataSize(); i ++) {
//					if (structureTableModel.getValueAt(i) == object) {
//						structuresTable.addRowSelectionInterval(i, i);
//						break;
//					}
//				}
//			} else 	if (object instanceof ReactionStep) {
//				for (int i = 0; i < reactionTableModel.getDataSize(); i ++) {
//					if (reactionTableModel.getValueAt(i) == object) {
//						reactionsTable.addRowSelectionInterval(i, i);
//						break;
//					}
//				}
//			} else 	if (object instanceof Parameter) {
//				for (int i = 0; i < parametersTableModel.getDataSize(); i ++) {
//					if (parametersTableModel.getValueAt(i) == object) {
//						parametersTable.addRowSelectionInterval(i, i);
//						break;
//					}
//				}
//			}
//		} 
	}

	private void initialize(){
		newButton = new JButton("New");
		searchButton = new JButton("Search");
		showAllButton = new JButton("Show All");
		deleteButton = new JButton("Delete");
		textFieldSearch = new JTextField(15);
		
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
		cartoonEditorPanel  = new CartoonEditorPanelFixed();
		cartoonEditorPanel.getStructureCartoon().addPropertyChangeListener(eventHandler);
		
		setLayout(new GridBagLayout());
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		add(textFieldSearch, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		add(searchButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		add(showAllButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,20,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		newButton.setPreferredSize(deleteButton.getPreferredSize());
		add(newButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.insets = new Insets(4,4,4,10);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(deleteButton, gbc);
		
		tabbedPane = new JTabbedPane();
		ModelPanelTab.reaction_diagram.setComponent(reactionCartoonEditorPanel);
		ModelPanelTab.structure_diagram.setComponent(cartoonEditorPanel);
		ModelPanelTab.reaction_table.setComponent(reactionsTable.getEnclosingScrollPane());
		ModelPanelTab.structure_table.setComponent(structuresTable.getEnclosingScrollPane());
		ModelPanelTab.species_table.setComponent(speciesTable.getEnclosingScrollPane());
		ModelPanelTab.global_parameter_table.setComponent(parametersTable.getEnclosingScrollPane());
		tabbedPane.addChangeListener(eventHandler);
		tabbedPane.addMouseListener(eventHandler);
		
		for (ModelPanelTab tab : ModelPanelTab.values()) {
			tabbedPane.addTab(tab.getName(), tab.getComponent());
		}
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 6;
		gbc.fill = GridBagConstraints.BOTH;
		add(tabbedPane, gbc);		
						
		newButton.addActionListener(eventHandler);
		newButton.setEnabled(false);
		deleteButton.addActionListener(eventHandler);
		deleteButton.setEnabled(false);
		textFieldSearch.addActionListener(eventHandler);
		searchButton.addActionListener(eventHandler);
		showAllButton.addActionListener(eventHandler);
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
		String searchText = textFieldSearch.getText();
		computeCurrentSelectedTable();
		currentSelectedTableModel.setSearchText(searchText);
	}
	
	private void showAllButtonPressed() {
		if (textFieldSearch.getText() == null || textFieldSearch.getText().length() == 0) {
			return;
		}
		textFieldSearch.setText(null);
		computeCurrentSelectedTable();
		currentSelectedTableModel.setSearchText(null);
	}
	
	private void newButtonPressed() {
		computeCurrentSelectedTable();
		Object newObject = null;
		if (currentSelectedTable == speciesTable) {
			newObject = bioModel.getModel().createSpeciesContext(bioModel.getModel().getStructures()[0]);
		} else if (currentSelectedTable == structuresTable) {
			Feature parentFeature = null;
			for (int i = bioModel.getModel().getNumStructures() - 1; i >= 0; i --) {
				if (bioModel.getModel().getStructures()[i] instanceof Feature) {
					parentFeature = (Feature)bioModel.getModel().getStructures()[i];
					break;
				}
			}
			try {
				Feature feature = bioModel.getModel().createFeature(parentFeature);
				newObject = feature;
			} catch (Exception e) {
				e.printStackTrace();
				DialogUtils.showErrorDialog(this, e.getMessage(), e);
			}
		} else if (currentSelectedTable == reactionsTable) {
			SimpleReaction reactionStep = bioModel.getModel().createSimpleReaction(bioModel.getModel().getStructures()[0]);
			newObject = reactionStep;
		} else if (currentSelectedTable == parametersTable) {
			ModelParameter modelParameter = bioModel.getModel().createModelParameter();
			newObject = modelParameter;
		}
		if (newObject != null) {
			for (int i = 0; i < currentSelectedTableModel.getDataSize(); i ++) {
				if (currentSelectedTableModel.getValueAt(i) == newObject) {
					currentSelectedTable.setRowSelectionInterval(i, i);
					break;
				}
			}
		}
	}
	private void deleteButtonPressed() {
		computeCurrentSelectedTable();
		try {
			int[] rows = currentSelectedTable.getSelectedRows();
			if (rows == null || rows.length == 0) {
				return;
			}
			String deleteListText = "";
			if (currentSelectedTable == speciesTable) {
				ArrayList<SpeciesContext> deleteList = new ArrayList<SpeciesContext>();
				for (int r : rows) {
					if (r < speciesTableModel.getDataSize()) {
						SpeciesContext speciesContext = speciesTableModel.getValueAt(r);
						deleteList.add(speciesContext);
						deleteListText += speciesContext.getName() + "\n"; 
					}
				}
				String confirm = PopupGenerator.showOKCancelWarningDialog(this, "You are going to delete the following species:\n\n " + deleteListText + "\n Continue?");
				if (confirm.equals(UserMessage.OPTION_CANCEL)) {
					return;
				}
				for (SpeciesContext sc : deleteList) {
					bioModel.getModel().removeSpeciesContext(sc);
				}
			} else if (currentSelectedTable == structuresTable) {
				ArrayList<Feature> deleteList = new ArrayList<Feature>();
				for (int r : rows) {
					if (r < structureTableModel.getDataSize()) {
						Structure rowValue = structureTableModel.getValueAt(r);
						if (rowValue instanceof Feature) {
							deleteList.add((Feature) rowValue);
							deleteListText += ((Feature)rowValue).getName() + "\n"; 
						}
					}
				}
				String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Are you sure you want to delete the following structure(s):\n\n " + deleteListText + "\n Continue?");
				if (confirm.equals(UserMessage.OPTION_CANCEL)) {
					return;
				}
				for (Feature f : deleteList) {
					bioModel.getModel().removeFeature(f);
				}
			} else if (currentSelectedTable == reactionsTable) {
				ArrayList<ReactionStep> deleteList = new ArrayList<ReactionStep>();
				for (int r : rows) {
					if (r < reactionTableModel.getDataSize()) {
						ReactionStep reaction = reactionTableModel.getValueAt(r);
						deleteList.add(reaction);
						deleteListText += reaction.getName() + "\n"; 
					}
				}
				String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Are you sure you want to delete the following reaction(s):\n\n " + deleteListText + "\n Continue?");
				if (confirm.equals(UserMessage.OPTION_CANCEL)) {
					return;
				}
				for (ReactionStep sc : deleteList) {
					bioModel.getModel().removeReactionStep(sc);
				}
			} else if (currentSelectedTable == parametersTable) {
				ArrayList<ModelParameter> deleteList = new ArrayList<ModelParameter>();
				for (int r : rows) {
					if (r < parametersTableModel.getDataSize()) {
						Parameter parameter = parametersTableModel.getValueAt(r);
						if (parameter instanceof ModelParameter) {
							deleteList.add((ModelParameter)parameter);
							deleteListText += ((ModelParameter)parameter).getName() + "\n"; 
						}
					}
				}	
				String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Are you sure you want to delete the following global parameter(s):\n\n " + deleteListText + "\n Continue?");
				if (confirm.equals(UserMessage.OPTION_CANCEL)) {
					return;
				}
				for (ModelParameter param : deleteList) {
					bioModel.getModel().removeModelParameter(param);
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
		cartoonEditorPanel.setBioModel(bioModel);
		reactionTableModel.setBioModel(bioModel);
		structureTableModel.setBioModel(bioModel);
		speciesTableModel.setBioModel(bioModel);
		parametersTableModel.setBioModel(bioModel);	
	}

	JTable currentSelectedTable = null;
	BioModelEditorRightSideTableModel<?> currentSelectedTableModel = null;
	private void computeCurrentSelectedTable() {
		currentSelectedTable = null;
		currentSelectedTableModel = null;
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
	}
	
	private void tableSelectionChanged() {
		computeCurrentSelectedTable();
		if (currentSelectedTableModel != null) {
			int[] rows = currentSelectedTable.getSelectedRows();
			if (rows != null) {
				ArrayList<Object> selectedObjects = new ArrayList<Object>();
				for (int row : rows) {
					if (row < currentSelectedTableModel.getDataSize()) {
						selectedObjects.add(currentSelectedTableModel.getValueAt(row));
					}
				}
				setSelectedObjects(selectedObjects.toArray());
			}
			if (currentSelectedTable == parametersTable) {
				deleteButton.setEnabled(false);
				for (int row : rows) {
					if (row < currentSelectedTableModel.getDataSize()) {
						if (currentSelectedTableModel.getValueAt(row) instanceof ModelParameter) {
							deleteButton.setEnabled(true);
							break;
						}
					}
				}
			} else {
				deleteButton.setEnabled(rows != null && rows.length > 0 && (rows.length > 1 || rows[0] < currentSelectedTableModel.getDataSize()));
			}
		}
	}
	
	public void setDocumentManager(DocumentManager documentManager) {
		cartoonEditorPanel.setDocumentManager(documentManager);		
	}
	
	private void showDiagramView() {
		if (tabbedPane.getSelectedIndex() == ModelPanelTab.reaction_diagram.ordinal()) {
			if (tabbedPane.getComponentAt(ModelPanelTab.reaction_diagram.ordinal()) != ModelPanelTab.reaction_diagram.getComponent()) {
				try {
					if (diagramViewInternalFrame != null) {
						diagramViewInternalFrame.setSelected(true);
					}
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
			}
		}
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
			tabbedPane.setComponentAt(0, new JPanel());
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(p, BorderLayout.NORTH);
			panel.add(reactionCartoonEditorPanel, BorderLayout.CENTER);
			diagramViewInternalFrame.setResizable(true);
			diagramViewInternalFrame.setClosable(true);
			diagramViewInternalFrame.addInternalFrameListener(new InternalFrameAdapter() {

				@Override
				public void internalFrameClosing(InternalFrameEvent e) {					
					reactionCartoonEditorPanel.setFloatingRequested(false);
				}
				
			});
			diagramViewInternalFrame.setMaximizable(true);
			diagramViewInternalFrame.setIconifiable(true);
			diagramViewInternalFrame.add(panel);
			diagramViewInternalFrame.setFrameIcon(new ImageIcon(getClass().getResource("/images/step.gif")));
			diagramViewInternalFrame.pack();
			BeanUtils.centerOnComponent(diagramViewInternalFrame, this);
			DocumentWindowManager.showFrame(diagramViewInternalFrame, desktopPane);
		} else {
			DocumentWindowManager.close(diagramViewInternalFrame, desktopPane);			
			tabbedPane.setComponentAt(0, ModelPanelTab.reaction_diagram.getComponent());
			tabbedPane.setSelectedComponent(ModelPanelTab.reaction_diagram.getComponent());
		}
	}
}
