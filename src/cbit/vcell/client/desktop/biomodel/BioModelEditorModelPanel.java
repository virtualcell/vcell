package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
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

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
import org.vcell.util.gui.VCellIcons;

import cbit.gui.graph.GraphModel;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.graph.CartoonEditorPanelFixed;
import cbit.vcell.graph.ReactionCartoonEditorPanel;
import cbit.vcell.graph.structures.AllStructureSuite;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class BioModelEditorModelPanel extends DocumentEditorSubPanel implements Model.Owner {
	protected static final String PROPERTY_NAME_BIO_MODEL = "bioModel";
	public enum ModelPanelTabID {
		reaction_table("Reactions"),
		structure_table("Structures"),
		species_table("Species"),
		reaction_diagram("Reaction Diagram"),
		structure_diagram("Structure Diagram");
		
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
	private JButton deleteButton = null;
	private JButton showAllButton = null;
	private EditorScrollTable structuresTable = null;
	private EditorScrollTable reactionsTable = null;
	private EditorScrollTable speciesTable = null;
	private BioModelEditorStructureTableModel structureTableModel = null;
	private BioModelEditorReactionTableModel reactionTableModel = null;
	private BioModelEditorSpeciesTableModel speciesTableModel = null;
	private BioModel bioModel;
	private JTextField textFieldSearch = null;
	private JTabbedPane tabbedPane = null;
	
	private CartoonEditorPanelFixed cartoonEditorPanel = null;
	private ReactionCartoonEditorPanel reactionCartoonEditorPanel = null;
	private JDesktopPaneEnhanced desktopPane = null;
	private JInternalFrameEnhanced diagramViewInternalFrame = null;	

	private InternalEventHandler eventHandler = new InternalEventHandler();

	private JPanel buttonPanel;
	
	private class InternalEventHandler implements ActionListener, PropertyChangeListener, ListSelectionListener, ChangeListener, MouseListener, DocumentListener {

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
		}
		public void mouseReleased(MouseEvent e) {
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
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
	}
	
	public BioModelEditorModelPanel() {
		super();
		addPropertyChangeListener(eventHandler);
		initialize();
	}

	public void tabbedPaneSelectionChanged() {
		textFieldSearch.setText(null);
		reactionTableModel.setSearchText(null);
		structureTableModel.setSearchText(null);
		speciesTableModel.setSearchText(null);
		
		int selectedIndex = tabbedPane.getSelectedIndex();
		ModelPanelTabID[] tabIdValues = ModelPanelTabID.values();
		tabbedPane.setTitleAt(selectedIndex, "<html><b>"+ tabIdValues[selectedIndex].name + "</b></html>");
		for (int i = 0; i < tabbedPane.getTabCount(); i ++) {
			if (i != selectedIndex) {
				tabbedPane.setTitleAt(i, tabIdValues[i].name);
			}
		}
		ActiveView activeView = null;
		if (selectedIndex == ModelPanelTabID.reaction_table.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.REACTIONS_NODE, ActiveViewID.reactions);
		} else if (selectedIndex == ModelPanelTabID.structure_table.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.STRUCTURES_NODE, ActiveViewID.structures);
		} else if (selectedIndex == ModelPanelTabID.species_table.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.SPECIES_NODE, ActiveViewID.species);
		} else if (selectedIndex == ModelPanelTabID.reaction_diagram.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.REACTION_DIAGRAM_NODE, ActiveViewID.reaction_diagram);
		} else if (selectedIndex == ModelPanelTabID.structure_diagram.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.STRUCTURE_DIAGRAM_NODE, ActiveViewID.structure_diagram);
		}
		if (activeView != null) {
			setActiveView(activeView);
		}
		if (selectedIndex == ModelPanelTabID.reaction_diagram.ordinal()
				|| selectedIndex == ModelPanelTabID.structure_diagram.ordinal()) {
			newButton.setVisible(false);
			deleteButton.setEnabled(false);
		} else {
			newButton.setVisible(true);
			computeCurrentSelectedTable();
			if (currentSelectedTableModel != null) {
				int[] rows = currentSelectedTable.getSelectedRows();
				deleteButton.setEnabled(rows != null && rows.length > 0 && (rows.length > 1 || rows[0] < currentSelectedTableModel.getDataSize()));			
			}
		}
	}

	public void onSelectedObjectsChange(Object[] selectedObjects) {
		reactionCartoonEditorPanel.getReactionCartoon().setSelectedObjects(selectedObjects);
		cartoonEditorPanel.getStructureCartoon().setSelectedObjects(selectedObjects);
		setTableSelections(selectedObjects, structuresTable, structureTableModel);
		setTableSelections(selectedObjects, reactionsTable, reactionTableModel);
		setTableSelections(selectedObjects, speciesTable, speciesTableModel);
	}

	private void initialize(){
		newButton = new JButton("Add New");
		deleteButton = new JButton("Delete Selected");
		textFieldSearch = new JTextField();
		
		structuresTable = new EditorScrollTable();
		reactionsTable = new EditorScrollTable();
		speciesTable = new EditorScrollTable();
		structureTableModel = new BioModelEditorStructureTableModel(structuresTable);
		reactionTableModel = new BioModelEditorReactionTableModel(reactionsTable);
		speciesTableModel = new BioModelEditorSpeciesTableModel(speciesTable);
		structuresTable.setModel(structureTableModel);
		reactionsTable.setModel(reactionTableModel);
		speciesTable.setModel(speciesTableModel);
		
		reactionCartoonEditorPanel = new ReactionCartoonEditorPanel();
		reactionCartoonEditorPanel.addPropertyChangeListener(eventHandler);
		reactionCartoonEditorPanel.getReactionCartoon().addPropertyChangeListener(eventHandler);
		cartoonEditorPanel  = new CartoonEditorPanelFixed();
		cartoonEditorPanel.getStructureCartoon().addPropertyChangeListener(eventHandler);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		buttonPanel.add(new JLabel("Search "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.5;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		buttonPanel.add(textFieldSearch, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,5)), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.insets = new Insets(4,50,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(newButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(deleteButton, gbc);
				
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		modelPanelTabs[ModelPanelTabID.reaction_diagram.ordinal()] = new ModelPanelTab(ModelPanelTabID.reaction_diagram, reactionCartoonEditorPanel, VCellIcons.diagramIcon);
		modelPanelTabs[ModelPanelTabID.structure_diagram.ordinal()] = new ModelPanelTab(ModelPanelTabID.structure_diagram, cartoonEditorPanel, VCellIcons.diagramIcon);
		modelPanelTabs[ModelPanelTabID.reaction_table.ordinal()] = new ModelPanelTab(ModelPanelTabID.reaction_table, reactionsTable.getEnclosingScrollPane(), VCellIcons.tableIcon);
		modelPanelTabs[ModelPanelTabID.structure_table.ordinal()] = new ModelPanelTab(ModelPanelTabID.structure_table, structuresTable.getEnclosingScrollPane(), VCellIcons.tableIcon);
		modelPanelTabs[ModelPanelTabID.species_table.ordinal()] = new ModelPanelTab(ModelPanelTabID.species_table, speciesTable.getEnclosingScrollPane(), VCellIcons.tableIcon);
		tabbedPane.addChangeListener(eventHandler);
		tabbedPane.addMouseListener(eventHandler);
		
		for (ModelPanelTab tab : modelPanelTabs) {
			tab.getComponent().setBorder(GuiConstants.TAB_PANEL_BORDER);
			tabbedPane.addTab(tab.getName(), tab.getIcon(), tab.getComponent());
		}
		
		
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
						
		newButton.addActionListener(eventHandler);
		deleteButton.addActionListener(eventHandler);
		deleteButton.setEnabled(false);
		textFieldSearch.addActionListener(eventHandler);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		structuresTable.getSelectionModel().addListSelectionListener(eventHandler);
		reactionsTable.getSelectionModel().addListSelectionListener(eventHandler);
		speciesTable.getSelectionModel().addListSelectionListener(eventHandler);
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
				}
				return this;
			}			
		};
		structuresTable.setDefaultRenderer(Structure.class, tableRenderer);
		speciesTable.setDefaultRenderer(Structure.class, tableRenderer);
		reactionsTable.setDefaultRenderer(Structure.class, tableRenderer);
		reactionsTable.setDefaultRenderer(Kinetics.class, tableRenderer);
		
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
		if (currentSelectedTableModel != null) {
			currentSelectedTableModel.setSearchText(searchText);
		}
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
						deleteListText += "\t" + speciesContext.getName() + "\n"; 
					}
				}
				String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Deleting species", "You are going to delete the following species:\n\n " + deleteListText + "\n Continue?");
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
							deleteListText += "\t" + ((Feature)rowValue).getName() + "\n"; 
						}
					}
				}
				String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Deleting structure(s)", "You are going to delete the following structure(s):\n\n " + deleteListText + "\n Continue?");
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
						deleteListText += "\t" + reaction.getName() + "\n"; 
					}
				}
				String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Deleting reaction(s)", "You are going to delete the following reaction(s):\n\n " + deleteListText + "\n Continue?");
				if (confirm.equals(UserMessage.OPTION_CANCEL)) {
					return;
				}
				for (ReactionStep sc : deleteList) {
					bioModel.getModel().removeReactionStep(sc);
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
			deleteButton.setEnabled(rows != null && rows.length > 0 && (rows.length > 1 || rows[0] < currentSelectedTableModel.getDataSize()));			
		}
	}
	
	public void setDocumentManager(DocumentManager documentManager) {
		cartoonEditorPanel.setDocumentManager(documentManager);	
		reactionCartoonEditorPanel.setDocumentManager(documentManager);
	}
	
	private void showDiagramView() {
		if (tabbedPane.getSelectedIndex() == ModelPanelTabID.reaction_diagram.ordinal()) {
			if (tabbedPane.getComponentAt(ModelPanelTabID.reaction_diagram.ordinal()) != modelPanelTabs[ModelPanelTabID.reaction_diagram.ordinal()].getComponent()) {
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
			tabbedPane.putClientProperty("__index_to_remove__", ModelPanelTabID.reaction_diagram.ordinal());
			tabbedPane.setComponentAt(ModelPanelTabID.reaction_diagram.ordinal(), new JPanel());
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
			tabbedPane.putClientProperty("__index_to_remove__", ModelPanelTabID.reaction_diagram.ordinal());
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
		case REACTION_DIAGRAM_NODE:
			selectTab(ModelPanelTabID.reaction_diagram);
			break;
		case STRUCTURE_DIAGRAM_NODE:
			selectTab(ModelPanelTabID.structure_diagram);
			break;
		}
	}
	
	@Override
	public void setIssueManager(IssueManager issueManager) {
		super.setIssueManager(issueManager);
		reactionTableModel.setIssueManager(issueManager);
		speciesTableModel.setIssueManager(issueManager);
		structureTableModel.setIssueManager(issueManager);
	}
}
