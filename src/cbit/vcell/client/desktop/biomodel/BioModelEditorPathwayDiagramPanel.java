package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Complex;
import org.vcell.pathway.Control;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.PathwayEvent;
import org.vcell.pathway.PathwayListener;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.util.graphlayout.RandomLayouter;
import org.vcell.util.graphlayout.SimpleElipticalLayouter;
import org.vcell.util.gui.ActionBuilder;
import org.vcell.relationship.PathwayMapping;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;
import org.vcell.util.gui.VCellIcons;
import org.vcell.util.gui.ViewPortStabilizer;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.graph.GraphLayoutManager;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.GraphPane;
import cbit.gui.graph.GraphResizeManager.ZoomRangeException;
import cbit.gui.graph.actions.GraphLayoutTasks;
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
public class BioModelEditorPathwayDiagramPanel extends DocumentEditorSubPanel 
implements PathwayEditor, ActionBuilder.Generator {
	
	public static enum ActionID implements ActionBuilder.ID {
		select, zoomIn, zoomOut, randomLayout, circularLayout, annealedLayout, levelledLayout, 
		relaxedLayout, glgLayout, reactionsOnlyShown, reactionNetworkShown, componentsShown;
	}
	
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
	protected JScrollPane graphScrollPane;
	protected ViewPortStabilizer viewPortStabilizer;
	
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
		if (bioModel == null) {
			return;
		}
		String warningMessage = "";
		int warningCount = 0;
		String infoMessage = "";
		ArrayList <BioPaxObject> importedBPObjects = new ArrayList <BioPaxObject>();
		
		warningMessage = "The following pathway object(s) have been associated with object(s) in the physiology model:\n";
		warningCount = 0;
		infoMessage = "The following pathway object(s) have been coverted in the physiology model:\n\n";
		
		for(BioPaxObject bpo : getSelectedBioPaxObjects()){
			  if(bpo instanceof Conversion){
				  if(bioModel.getRelationshipModel().getRelationshipObjects(bpo).size() == 0){
					  importedBPObjects.add(bpo);
				  }else{
					  warningCount ++;
					  warningMessage += "\nReaction: \'" + ((Conversion)bpo).getName().get(0) + "\' =>\n";
					  for(RelationshipObject  r : bioModel.getRelationshipModel().getRelationshipObjects(bpo)){
						  warningMessage += "\t=> \'" + r.getBioModelEntityObject().getName()+"\'\n";
					  }
				  }
			  }else if(bpo instanceof PhysicalEntity){
				  if(bioModel.getRelationshipModel().getRelationshipObjects(bpo).size() == 0){
					  importedBPObjects.add(bpo);
				  }else{
					  warningCount ++;
					  warningMessage += "\nSpecies: \'" + ((PhysicalEntity)bpo).getName().get(0) + "\' =>\n";
					  for(RelationshipObject  r : bioModel.getRelationshipModel().getRelationshipObjects(bpo)){
						  warningMessage += "\t=> \'" + r.getBioModelEntityObject().getName()+"\'\n";
					  }
				  }
			  }	  
		}
		
		// show warning message 
		warningMessage += "\nThey will NOT be converted to the physiology model.\n";
		  if(warningCount > 0){
			  DialogUtils.showWarningDialog(conversionPanel, warningMessage);
		  }
		  
		if(importedBPObjects.size() == 0){
			return;
		}
		// create import panel
		if (conversionPanel == null) {
			conversionPanel = new ConversionPanel();
			conversionPanel.setBioModel(bioModel);
			conversionPanel.setSelectionManager(getSelectionManager());
		}		
		conversionPanel.setBioPaxObjects(getSelectedBioPaxObjects());
		int returnCode = DialogUtils.showComponentOKCancelDialog(this, conversionPanel, "Import into Physiology");
		if (returnCode == JOptionPane.OK_OPTION) {
			PathwayMapping pathwayMapping = new PathwayMapping();
			try{
				
				// function I:
				// pass the table rows that contains user edited values to create Vcell object
				pathwayMapping.createBioModelEntitiesFromBioPaxObjects(bioModel, conversionPanel.getTableRows());
				// function II:
				// pass the bioPax objects to generate Vcell objects
				// pathwayMapping.createBioModelEntitiesFromBioPaxObjects(bioModel, tableModel.getBioPaxObjects().toArray());
				// show import info
				for(BioPaxObject bpo : importedBPObjects){
					  if(bpo instanceof Conversion){
							  infoMessage += "Reaction: \t\'";
							  for(RelationshipObject  r : bioModel.getRelationshipModel().getRelationshipObjects(bpo)){
								  infoMessage += r.getBioModelEntityObject().getName()+"\'\n";
							  }
							  infoMessage += "\n";
					  }else if(bpo instanceof PhysicalEntity){
							  infoMessage += "SpeciesContext: \t\'";
							  for(RelationshipObject  r : bioModel.getRelationshipModel().getRelationshipObjects(bpo)){
								  infoMessage += r.getBioModelEntityObject().getName()+"\'\n";
							  }
							  infoMessage += "\n";
					  }	  
				}
				DialogUtils.showInfoDialog(this, infoMessage);
				
				if (selectionManager != null){
					selectionManager.setActiveView(new ActiveView(null,DocumentEditorTreeFolderClass.REACTION_DIAGRAM_NODE, ActiveViewID.reaction_diagram));
			//							selectionManager.setSelectedObjects(new Object[]{selectedBioPaxObjects});
				}
			}catch(Exception e)
			{
				e.printStackTrace(System.out);
				DialogUtils.showErrorDialog(this, "Errors occur when converting pathway objects to VCell bioModel objects.\n" + e.getMessage());
			}
		}
	}

	public void deleteButtonPressed() {
		List<BioPaxObject> allSelectedBioPaxObjects = getSelectedBioPaxObjects(); // all objects required by user
		List<BioPaxObject> selectedBioPaxObjects = new ArrayList<BioPaxObject> (); // the user required objects that can be deleted
		List<BioPaxObject> completeSelectedBioPaxObjects = new ArrayList<BioPaxObject> (); // the all objects that will be deleted from the pathway model
		StringBuilder warning = new StringBuilder("You can NOT delete the following pathway objects:\n\n");
		for (BioPaxObject bpObject : allSelectedBioPaxObjects) {
			if(canDelete(bpObject)){
				selectedBioPaxObjects.add(bpObject);
				completeSelectedBioPaxObjects.add(bpObject);
				if(bpObject instanceof Conversion){// all its participants and its catalysts will be deleted if deleting a conversion
					// check each participant
					for(InteractionParticipant ip : ((Conversion)bpObject).getParticipants()){
						if(canDelete(ip.getPhysicalEntity())) {
							completeSelectedBioPaxObjects.add(ip.getPhysicalEntity());
							// the complex of the pysicalEntity will be removed
							for(Complex complex : getComplex(ip.getPhysicalEntity())){
								if(canDelete(complex))
									completeSelectedBioPaxObjects.add(complex);
							}
						}
					}
					// check catalysts
					for(BioPaxObject bp : bioModel.getPathwayModel().getBiopaxObjects()){
						if(bp instanceof Control){
							if(((Control)bp).getControlledInteraction() == bpObject){
								completeSelectedBioPaxObjects.add(bp);
								for(PhysicalEntity pe : ((Control)bp).getPhysicalControllers()){
									if(canDelete(pe)) completeSelectedBioPaxObjects.add(pe);
								}
							}
						}
					}
				}
			}else{
				warning.append("    " + bpObject.getTypeLabel() + ": \'" + PhysiologyRelationshipTableModel.getLabel(bpObject) + "\'\n");
			}
		}
		warning.append("\nThey are required by other reactions.\n\n");
		if(allSelectedBioPaxObjects.size() > selectedBioPaxObjects.size()){
			PopupGenerator.showWarningDialog(this, warning.toString());
		}
		if(selectedBioPaxObjects.size() > 0){
			StringBuilder text = new StringBuilder("You are going to DELETE the following pathway objects:\n\n");
			for (BioPaxObject bpObject : selectedBioPaxObjects) {
				text.append("    " + bpObject.getTypeLabel() + ": \'" + PhysiologyRelationshipTableModel.getLabel(bpObject) + "\'\n");
			}
			text.append("\nContinue?");
			String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Deleting pathway objects", text.toString());
			if (confirm.equals(UserMessage.OPTION_CANCEL)) {
				return;
			}
			bioModel.getPathwayModel().remove(completeSelectedBioPaxObjects);
			bioModel.getRelationshipModel().removeRelationshipObjects(completeSelectedBioPaxObjects);
		}
	}
	
	private ArrayList<Complex> getComplex(PhysicalEntity physicalEntity){
		ArrayList<Complex> complexList = new ArrayList<Complex>();
		for(BioPaxObject bpObject: bioModel.getPathwayModel().getBiopaxObjects()){
			if(bpObject instanceof Complex){
				if(((Complex)bpObject).getComponents().contains(physicalEntity))
					complexList.add((Complex)bpObject);
			}
		}
		return complexList;
	}
	
	private boolean canDelete(BioPaxObject bpObject){
		List<BioPaxObject> selectedBioPaxObjects = getSelectedBioPaxObjects();
		for(BioPaxObject bp : bioModel.getPathwayModel().getBiopaxObjects()){
			if(bp instanceof Conversion){ // CANNOT delete any participants before deleting the reaction
				if(!selectedBioPaxObjects.contains(bp)) {
					if (((Conversion)bp).getLeft().contains(bpObject)) return false;
					if (((Conversion)bp).getRight().contains(bpObject)) return false;
				}
			}else if(bp instanceof Control){  // CANNOT delete any catalysts before deleting the reaction
				if(((Control)bp).getPhysicalControllers().contains(bpObject) 
						&& !selectedBioPaxObjects.contains(((Control)bp).getControlledInteraction())) return false;

			}
		}
		return true;
	}

	private JButton createToolBarButton(Action action) {
		JButton button = new JButton(action);
		button.setMaximumSize(TOOLBAR_BUTTON_SIZE);
		button.setPreferredSize(TOOLBAR_BUTTON_SIZE);
		button.setMinimumSize(TOOLBAR_BUTTON_SIZE);
		button.setMargin(new Insets(2, 2, 2, 2));
		return button;
	}

	private JToolBar createToolBar(int orientation) {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setBorder(new javax.swing.border.EtchedBorder());
		toolBar.setOrientation(orientation);
		for (ActionID id : ActionID.values()) {
			ActionBuilder actionBuilder = actionBuilderMap.get(id);
			if(actionBuilder != null) {
				Action action = actionBuilder.buildAction(this);
				if(action != null) {
					toolBar.add(createToolBarButton(action));					
				}
			}
		}
		return toolBar;
	}
	
	protected static Map<ActionBuilder.ID, ActionBuilder> 
	createActionBuilderMap(ActionBuilder ... builders) {
		Map<ActionBuilder.ID, ActionBuilder> builderMap = new HashMap<ActionBuilder.ID, ActionBuilder>();
		for(ActionBuilder builder : builders) {
			builderMap.put(builder.id, builder);
		}
		return builderMap;
	}
	
	protected static Map<ActionBuilder.ID, ActionBuilder> actionBuilderMap = createActionBuilderMap(
		new ActionBuilder(ActionID.select, "", "Select", "Select parts of the graph", VCellIcons.pathwaySelectIcon),
		new ActionBuilder(ActionID.zoomIn, "", "Zoom In", "Make graph look bigger", VCellIcons.pathwayZoomInIcon),
		new ActionBuilder(ActionID.zoomOut, "", "Zoom Out", "Make graph look smaller", VCellIcons.pathwayZoomOutIcon),
		new ActionBuilder(ActionID.randomLayout, "", "Random Layout", "Reconfigure graph randomly", VCellIcons.pathwayRandomIcon),
		new ActionBuilder(ActionID.circularLayout, "", "Circular Layout", "Reconfigure graph circular", VCellIcons.pathwayCircularIcon),
		new ActionBuilder(ActionID.annealedLayout, "", "Annealed Layout", "Reconfigure graph by annealing", VCellIcons.pathwayAnnealedIcon),
		new ActionBuilder(ActionID.levelledLayout, "", "Levelled Layout", "Reconfigure graph in levels", VCellIcons.pathwayLevelledIcon),
		new ActionBuilder(ActionID.relaxedLayout, "", "Relaxed Layout", "Reconfigure graph by relaxing", VCellIcons.pathwayRelaxedIcon),
		new ActionBuilder(ActionID.glgLayout, "", "GLG Layout", "Reconfigure graph by Generic Logic GraphLayout", VCellIcons.pathwayRandomIcon),
		new ActionBuilder(ActionID.reactionsOnlyShown, "", "Reactions Only", "Show only Reactions", VCellIcons.pathwayReactionsOnlyIcon),
		new ActionBuilder(ActionID.reactionNetworkShown, "", "Reaction Network", "Reaction Network", VCellIcons.pathwayReactionNetworkIcon),
		new ActionBuilder(ActionID.componentsShown, "", "Components", "Reactions, entities and components", VCellIcons.pathwayComponentsIcon));
		
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
		JToolBar layoutToolBar = createToolBar(SwingConstants.HORIZONTAL);
		sourceTextArea = new JTextArea();
		
		graphPane =  new GraphPane();
		pathwayGraphModel = new PathwayGraphModel();
		pathwayGraphModel.addPropertyChangeListener(eventHandler);
		graphPane.setGraphModel(pathwayGraphModel);
		
		graphCartoonTool = new PathwayGraphTool();
		graphCartoonTool.setGraphPane(graphPane);		
		graphTabPanel = new JPanel(new BorderLayout());
		graphScrollPane = new JScrollPane(graphPane);
		graphScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		graphScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		graphTabPanel.add(graphScrollPane, BorderLayout.CENTER);
		viewPortStabilizer = new ViewPortStabilizer(graphScrollPane);

		graphTabPanel.add(layoutToolBar, BorderLayout.NORTH);
		
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
	
	public PathwayGraphTool getCartoonTool() { return graphCartoonTool; }

	protected class LayoutAction extends AbstractAction {

		protected final String layoutName;
		
		public LayoutAction(String layoutName) { this.layoutName = layoutName; }
		
		public void actionPerformed(ActionEvent arg0) {
			System.out.println(layoutName);
			try { 
				GraphLayoutTasks.dispatchTasks(BioModelEditorPathwayDiagramPanel.this, 
						getCartoonTool().getGraphLayoutManager(), getCartoonTool(), layoutName);
			} 
			catch (Exception e) { e.printStackTrace(); }
		}
		
	}
	
	public Action generateAction(ActionBuilder.ID id) {
		if(id instanceof ActionID) {
			switch((ActionID)id) {
			case zoomIn: {
				return new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						try {
							viewPortStabilizer.saveViewPortPosition();
							getCartoonTool().getGraphModel().getResizeManager().zoomIn();
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									viewPortStabilizer.restoreViewPortPosition();						
								}
							});
						} catch (ZoomRangeException e) {
							e.printStackTrace();
						}
					}
				};
			}
			case zoomOut: {
				return new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						try {
							viewPortStabilizer.saveViewPortPosition();
							getCartoonTool().getGraphModel().getResizeManager().zoomOut();
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									viewPortStabilizer.restoreViewPortPosition();						
								}
							});
						} catch (ZoomRangeException e) {
							e.printStackTrace();
						}
					}
				};
			}
			case randomLayout: {
				return new LayoutAction(RandomLayouter.LAYOUT_NAME);
			}
			case circularLayout: {
				return new LayoutAction(SimpleElipticalLayouter.LAYOUT_NAME);
			}
			case annealedLayout: {
				return new LayoutAction(GraphLayoutManager.OldLayouts.ANNEALER);
			}
			case levelledLayout: {
				return new LayoutAction(GraphLayoutManager.OldLayouts.LEVELLER);
			}
			case relaxedLayout: {
				return new LayoutAction(GraphLayoutManager.OldLayouts.RELAXER);
			}
			case glgLayout : {
				return new LayoutAction(GraphLayoutManager.OldLayouts.GLG);
			}
			}
		}
		return null;
	}
	
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
