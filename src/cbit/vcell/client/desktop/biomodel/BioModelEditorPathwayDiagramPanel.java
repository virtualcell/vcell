package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.PathwayEvent;
import org.vcell.pathway.PathwayListener;
import org.vcell.pathway.PathwayModel;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;
import org.vcell.util.gui.VCellIcons;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.graph.GraphModel;
import cbit.gui.graph.GraphPane;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEvent;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEventListener;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayEditor;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphTool;
import cbit.vcell.model.BioModelEntityObject;

@SuppressWarnings("serial")
public class BioModelEditorPathwayDiagramPanel extends DocumentEditorSubPanel implements PathwayEditor {
	
	private static final Dimension TOOLBAR_BUTTON_SIZE = new Dimension(28, 28);
	private EventHandler eventHandler = new EventHandler();
	private BioModel bioModel = null;
	private GraphPane graphPane;
	private PathwayGraphTool graphCartoonTool;
	private JTextArea sourceTextArea = null;
	private JMenuItem importIntoModelMenuItem = null; // wei's code
	private JSortTable pathwayModelTable = null;
	private PathwayModelTableModel pathwayModelTableModel = null;
	private JTextField searchTextField;
	private JTabbedPane tabbedPane;
	private JPanel graphTabPanel;
	private JPanel sourceTabPanel;
	private PathwayGraphModel pathwayGraphModel;
	
	private JButton deleteButton, physiologyLinkButton;
	private JMenuItem showPhysiologyLinksMenuItem, editPhysiologyLinksMenuItem;
	private JPopupMenu physiologyLinkPopupMenu;
	private BioPaxRelationshipPanel bioPaxRelationshipPanel;
	private ConversionPanel conversionPanel;
	
	private class EventHandler implements ListSelectionListener, AnnotationEventListener, PropertyChangeListener, DocumentListener, ChangeListener, PathwayListener, ActionListener {

		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == pathwayModelTable.getSelectionModel()) {
				setSelectedObjectsFromTable(pathwayModelTable, pathwayModelTableModel);
			}
		}
		public void annotationChanged(AnnotationEvent annotationEvent) {
			refreshInterface();
		}
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(GraphModel.PROPERTY_NAME_SELECTED)) {
				Object[] selectedObjects = (Object[]) evt.getNewValue();
				setSelectedObjects(selectedObjects);
			}			
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
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == tabbedPane) {
				searchTextField.setText(null);
				searchTextField.setEditable(tabbedPane.getSelectedComponent() != sourceTabPanel);
				refreshButtons();
			}			
		}
		public void pathwayChanged(PathwayEvent event) {
			refreshInterface();
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == physiologyLinkButton) {
				getPhysiologyLinksPopupMenu().show(physiologyLinkButton, 0, physiologyLinkButton.getHeight());
			} else if (e.getSource() == showPhysiologyLinksMenuItem) {
				showPhysiologyLinks();
			} else if (e.getSource() == editPhysiologyLinksMenuItem) {
				editPhysiologyLinks();				
			} else if (e.getSource() == deleteButton) {
				deleteButtonPressed();
			} else if (e.getSource() == importIntoModelMenuItem) {
				importIntoModel();
			}
		}
	}
	
	public BioModelEditorPathwayDiagramPanel() {
		super();
		initialize();
	}
	
	public void importIntoModel() {
		if (conversionPanel == null) {
			conversionPanel = new ConversionPanel();
			conversionPanel.setBioModel(bioModel);
			conversionPanel.setSelectionManager(getSelectionManager());
		}		
		conversionPanel.setBioPaxObjects(getSelectedBioPaxObjects());
		conversionPanel.showSelectionDialog(); 
	}

	public void deleteButtonPressed() {
		List<BioPaxObject> selectedBioPaxObjects = getSelectedBioPaxObjects();
		StringBuilder text = new StringBuilder("You are going to delete the following pathway objects:\n\n");
		for (BioPaxObject bpObject : selectedBioPaxObjects) {
			text.append("    " + bpObject.getTypeLabel() + ":" + PhysiologyRelationshipTableModel.getLabel(bpObject) + "\n");
		}
		text.append("\nContinue?");
		String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Deleting pathway objects", text.toString());
		if (confirm.equals(UserMessage.OPTION_CANCEL)) {
			return;
		}
		bioModel.getPathwayModel().remove(selectedBioPaxObjects);
	}

	private JButton createToolBarButton(ToolBarButton toolButton) {
		JButton button = new JButton();
		button.setMaximumSize(TOOLBAR_BUTTON_SIZE);
		button.setPreferredSize(TOOLBAR_BUTTON_SIZE);
		button.setMinimumSize(TOOLBAR_BUTTON_SIZE);
		button.setMargin(new Insets(2, 2, 2, 2));
		button.setToolTipText(toolButton.shortDescrString);
		button.setIcon(toolButton.icon);
		return button;
	}

	private JToolBar createToolBar(ToolBarButton[] layouts, int orientation) {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setBorder(new javax.swing.border.EtchedBorder());
		toolBar.setOrientation(orientation);
		for (int i = 0; i < layouts.length; i ++) {
			toolBar.add(createToolBarButton(layouts[i]));
		}
		return toolBar;
	}
	
	private static enum ToolBarButton {
		select("Select", "Select", "Select parts of the graph", VCellIcons.pathwaySelectIcon),
		zoomin("Zoom In", "Zoom In", "Make graph look bigger", VCellIcons.pathwayZoomInIcon),
		zoomout("Zoom Out", "Zoom Out", "Make graph look smaller", VCellIcons.pathwayZoomOutIcon),
		random("Random", "Random Layout", "Reconfigure graph randomly", VCellIcons.pathwayRandomIcon),
		circular("Circular", "Circular Layout", "Reconfigure graph circular", VCellIcons.pathwayCircularIcon),
		annealed("Annealed", "Annealed Layout", "Reconfigure graph by annealing", VCellIcons.pathwayAnnealedIcon),
		levelled("Levelled", "Levelled Layout", "Reconfigure graph in levels", VCellIcons.pathwayLevelledIcon),
		relaxed("Relaxed", "Relaxed Layout", "Reconfigure graph by relaxing", VCellIcons.pathwayRelaxedIcon),
		reactions_only("Reactions Only", "Reactions Only", "Show only Reactions", VCellIcons.pathwayReactionsOnlyIcon),
		reaction_network("Reaction Network", "Reaction Network", "Reaction Network", VCellIcons.pathwayReactionNetworkIcon),
		components("Components", "Components", "Reactions, entities and components", VCellIcons.pathwayComponentsIcon),
		
		reaction("Reaction", "Biochemical Reaction", "Biochemical Reaction", VCellIcons.pathwayReactionIcon),
		transport("Transport", "Transport", "Transport", VCellIcons.pathwayTransportIcon),
		reaction_wt("Reaction WT", "Biochemical Reaction with Transport", "Biochemical Reaction with Transport", VCellIcons.pathwayReactionWtIcon),
		entity("Entity", "Physical Entity", "Physical Entity", VCellIcons.pathwayEntityIcon),
		small_molecule("Small Molecule", "Small Molecule", "Small Molecule", VCellIcons.pathwaySmallMoleculeIcon),
		protein("Protein", "Protein", "Protein", VCellIcons.pathwayProteinIcon),
		complex("Complex", "Complex", "Complex", VCellIcons.pathwayComplexIcon),
		participant("Participant", "Participant", "Physical Entity Participant", VCellIcons.pathwayParticipantsIcon);


		String name, shortDescrString, longDescription;
		Icon icon;
		private ToolBarButton(String name, String shortDescrString,
				String longDescription, Icon icon) {
			this.name = name;
			this.shortDescrString = shortDescrString;
			this.longDescription = longDescription;
			this.icon = icon;
		}		
	}

	private JPopupMenu getPhysiologyLinksPopupMenu() {
		if (physiologyLinkPopupMenu == null) {
			physiologyLinkPopupMenu = new JPopupMenu();
			showPhysiologyLinksMenuItem = new JMenuItem("Show Linked Physiology Objects");
			showPhysiologyLinksMenuItem.addActionListener(eventHandler);			
			editPhysiologyLinksMenuItem = new JMenuItem("Edit Physiology Links...");
			editPhysiologyLinksMenuItem.addActionListener(eventHandler);
			importIntoModelMenuItem = new JMenuItem("Import into Physiology...");// new ConversionPanel()));
			importIntoModelMenuItem.addActionListener(eventHandler);
			physiologyLinkPopupMenu.add(showPhysiologyLinksMenuItem);
			physiologyLinkPopupMenu.add(editPhysiologyLinksMenuItem);
			physiologyLinkPopupMenu.add(importIntoModelMenuItem);	
		}
		refreshButtons();
		return physiologyLinkPopupMenu;
	}
	
	private void initialize() {
		ToolBarButton[] layouts = new ToolBarButton[] {
				ToolBarButton.select,
				ToolBarButton.zoomin,
				ToolBarButton.zoomout,
				ToolBarButton.random,
				ToolBarButton.circular,
				ToolBarButton.annealed,
				ToolBarButton.levelled,
				ToolBarButton.relaxed,
				ToolBarButton.reactions_only,
				ToolBarButton.reaction_network,
				ToolBarButton.components,				
		};

		ToolBarButton[] interactions = new ToolBarButton[] {
				ToolBarButton.reaction,
				ToolBarButton.transport,
				ToolBarButton.reaction_wt,
				ToolBarButton.entity,
				ToolBarButton.small_molecule,
				ToolBarButton.protein,
				ToolBarButton.complex,
				ToolBarButton.participant,
		};
		
		JToolBar layoutToolBar = createToolBar(layouts, javax.swing.SwingConstants.VERTICAL);
		JToolBar nodesToolBar = createToolBar(interactions, javax.swing.SwingConstants.HORIZONTAL);
		sourceTextArea = new JTextArea();
		
		graphPane =  new GraphPane();
		pathwayGraphModel = new PathwayGraphModel();
		pathwayGraphModel.addPropertyChangeListener(eventHandler);
		graphPane.setGraphModel(pathwayGraphModel);
		
		graphCartoonTool = new PathwayGraphTool();
		graphCartoonTool.setGraphPane(graphPane);		
				
		graphTabPanel = new JPanel(new BorderLayout());
		JScrollPane graphScrollPane = new JScrollPane(graphPane);
		graphScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		graphScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		graphTabPanel.add(graphScrollPane, BorderLayout.CENTER);
		graphTabPanel.add(layoutToolBar, BorderLayout.WEST);
		graphTabPanel.add(nodesToolBar, BorderLayout.NORTH);
		
		sourceTabPanel = new JPanel(new BorderLayout());
		sourceTabPanel.add(new JScrollPane(sourceTextArea), BorderLayout.CENTER);
		
		pathwayModelTable = new JSortTable();
		pathwayModelTable.getSelectionModel().addListSelectionListener(eventHandler);
		pathwayModelTableModel = new PathwayModelTableModel(pathwayModelTable);
		pathwayModelTable.setModel(pathwayModelTableModel);
		
		searchTextField = new JTextField();
		searchTextField.getDocument().addDocumentListener(eventHandler);
		deleteButton = new JButton("Delete Selected");
		deleteButton.addActionListener(eventHandler);
		physiologyLinkButton = new JButton("Physiology Links", new DownArrowIcon());
		physiologyLinkButton.setHorizontalTextPosition(SwingConstants.LEFT);
		physiologyLinkButton.addActionListener(eventHandler);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(4, 4, 4, 4);
		bottomPanel.add(new JLabel("Search "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		bottomPanel.add(searchTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		bottomPanel.add(deleteButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		bottomPanel.add(physiologyLinkButton, gbc);
		
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(eventHandler);
		tabbedPane.addTab("Pathway Diagram", VCellIcons.diagramIcon, graphTabPanel);
		tabbedPane.addTab("Pathway Objects", VCellIcons.tableIcon, pathwayModelTable.getEnclosingScrollPane());
		tabbedPane.addTab("BioPAX Source", null, sourceTabPanel);

		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
		
		pathwayModelTable.getColumnModel().getColumn(PathwayModelTableModel.COLUMN_ENTITY).setCellRenderer(new DefaultScrollTableCellRenderer(){

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,	row, column);
//				if (column == PathwayModelTableModel.COLUMN_ENTITY) {
//					BioPaxObject bioPaxObject = pathwayModelTableModel.getValueAt(row);
//					Set<RelationshipObject> relationshipObjects = bioModel.getRelationshipModel().getRelationshipObjects(bioPaxObject);
//					
//					if (relationshipObjects.size() > 0) {
//						StringBuilder text = new StringBuilder("<html>Links to Physiology objects:<br>");
//						for (RelationshipObject ro : relationshipObjects) {
//							text.append("<li>" + ro.getBioModelEntityObject().getTypeLabel() + ":" + ro.getBioModelEntityObject().getName() + "</li>");
//						}
//						text.append("</html>");
//						setToolTipText(text.toString());
//						if (!isSelected) {
//							setForeground(Color.blue);
//						}
//						setText("<html><u>" + value + "</u></html>");
//					}					
//				}
				return this;
			}
			
		});
	}

	private void search() {
		Component selectedComponent = tabbedPane.getSelectedComponent();
		if (selectedComponent == pathwayModelTable.getEnclosingScrollPane()) {
			pathwayModelTableModel.setSearchText(searchTextField.getText());
		} else if (selectedComponent == graphTabPanel) {
			graphPane.getGraphModel().searchText(searchTextField.getText());
		}
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		graphPane.getGraphModel().setSelectedObjects(selectedObjects);
		setTableSelections(selectedObjects, pathwayModelTable, pathwayModelTableModel);
		refreshButtons();
	}

	private void refreshButtons() {
		deleteButton.setEnabled(false);
		physiologyLinkButton.setEnabled(false);
		if (showPhysiologyLinksMenuItem != null) {
			showPhysiologyLinksMenuItem.setEnabled(false);
		}
		if (editPhysiologyLinksMenuItem != null) {
			editPhysiologyLinksMenuItem.setEnabled(false);
		}
		if (importIntoModelMenuItem != null) {
			importIntoModelMenuItem.setEnabled(false);
		}
		if (selectionManager != null && tabbedPane.getSelectedComponent() != sourceTabPanel) {
			ArrayList<Object> selectedObjects = selectionManager.getSelectedObjects(BioPaxObject.class);
			if (selectedObjects.size() > 0) {				
				deleteButton.setEnabled(true);
				physiologyLinkButton.setEnabled(true);
				if (importIntoModelMenuItem != null) {
					importIntoModelMenuItem.setEnabled(true);
				}
				if (selectedObjects.size() == 1) {
					if (showPhysiologyLinksMenuItem != null && bioModel.getRelationshipModel().getRelationshipObjects((BioPaxObject)selectedObjects.get(0)).size() > 0) {
						showPhysiologyLinksMenuItem.setEnabled(true);
					}
					if (editPhysiologyLinksMenuItem != null) {
						editPhysiologyLinksMenuItem.setEnabled(true);
					}
				}
			}
		}
	}
	
	private void refreshInterface() {
		if (bioModel == null) {
			sourceTextArea.setText("");
		} else {
			try {
				PathwayModel pathwayModel = bioModel.getPathwayModel();			
				sourceTextArea.setText("======Summary View========\n\n"+pathwayModel.show(false)
						+"\n"+"======Detailed View========\n\n"+pathwayModel.show(true)+"\n");
			} catch (Exception ex) {
				ex.printStackTrace();
				DialogUtils.showErrorDialog(this, ex.getMessage());
			}
		}
	}

	public void setBioModel(BioModel bioModel) {
		if (this.bioModel == bioModel) {
			return;
		}
		if (bioModel!=null){
			bioModel.getPathwayModel().removePathwayListener(eventHandler);
		}
		this.bioModel = bioModel;
		if (this.bioModel!=null){
			this.bioModel.getPathwayModel().addPathwayListener(eventHandler);
		}
		pathwayModelTableModel.setBioModel(bioModel);
		pathwayGraphModel.setPathwayModel(bioModel.getPathwayModel());		
		refreshInterface();
	}
	
	// wei's code
	public List<BioPaxObject> getSelectedBioPaxObjects(){
		ArrayList<BioPaxObject> bpObjects = new ArrayList<BioPaxObject>();
		for(Object selected : graphCartoonTool.getGraphModel().getSelectedObjects()) {
			if(selected instanceof BioPaxObject) {
				bpObjects.add((BioPaxObject) selected);
			}
		}
		return bpObjects;
	}
	
	public SelectionManager getSelectionManager() { return selectionManager; }

	public BioModel getBioModel() { return bioModel; }
	
	private BioPaxObject getSelectedBioPaxObject() {
		ArrayList<Object> selectedObjects = selectionManager.getSelectedObjects(BioPaxObject.class);
		BioPaxObject selectedBioPaxObject = null;
		if (selectedObjects.size() == 1 && selectedObjects.get(0) instanceof BioPaxObject) {
			selectedBioPaxObject = (BioPaxObject)selectedObjects.get(0);
		}
		return selectedBioPaxObject;
	}
	private void showPhysiologyLinks() {
		BioPaxObject selectedBioPaxObject = getSelectedBioPaxObject();
		if (selectedBioPaxObject != null) {
			Set<RelationshipObject> relationshipSet = bioModel.getRelationshipModel().getRelationshipObjects(selectedBioPaxObject);
			if (relationshipSet.size() > 0) {
				ArrayList<BioModelEntityObject> selectedBioModelEntityObjects = new ArrayList<BioModelEntityObject>();
				for(RelationshipObject re: relationshipSet){
					BioModelEntityObject bioModelEntityObject = re.getBioModelEntityObject();
					selectedBioModelEntityObjects.add(bioModelEntityObject);
				}
//				if (selectedBioPaxObjects.get(0) instanceof ReactionStep) {
//					selectionManager.setActiveView(new ActiveView(null,DocumentEditorTreeFolderClass.REACTIONS_NODE, ActiveViewID.reactions));
//				} else if (selectedBioPaxObjects.get(0) instanceof SpeciesContext) {
//					selectionManager.setActiveView(new ActiveView(null,DocumentEditorTreeFolderClass.SPECIES_NODE, ActiveViewID.species));
//				}
				selectionManager.setActiveView(new ActiveView(null,DocumentEditorTreeFolderClass.REACTION_DIAGRAM_NODE, ActiveViewID.reaction_diagram));
				selectionManager.setSelectedObjects(selectedBioModelEntityObjects.toArray(new BioModelEntityObject[0]));
			}
		}
	}

	private void editPhysiologyLinks() {
		BioPaxObject selectedBioPaxObject = getSelectedBioPaxObject();
		if (selectedBioPaxObject != null) {
			if (bioPaxRelationshipPanel == null) {
				bioPaxRelationshipPanel = new BioPaxRelationshipPanel();
				bioPaxRelationshipPanel.setBioModel(bioModel);
			}
			bioPaxRelationshipPanel.setBioPaxObject(selectedBioPaxObject);
			DialogUtils.showComponentCloseDialog(this, bioPaxRelationshipPanel, "Edit Physiology Links");
		}
		refreshButtons();
	}
	
}
