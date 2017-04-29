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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
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
import javax.swing.JTree;
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
import org.vcell.pathway.Entity;
import org.vcell.pathway.GroupObject;
import org.vcell.pathway.Interaction;
import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.PathwayEvent;
import org.vcell.pathway.PathwayListener;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.pathway.Protein;
import org.vcell.pathway.SmallMolecule;
import org.vcell.pathway.Transport;
import org.vcell.pathway.group.PathwayGrouping;
import org.vcell.pathway.tree.BioPAXTreeMaker;
import org.vcell.relationship.PathwayMapping;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.graphlayout.ExpandCanvasLayouter;
import org.vcell.util.graphlayout.GenericLogicGraphLayouter;
import org.vcell.util.graphlayout.RandomLayouter;
import org.vcell.util.graphlayout.ShrinkCanvasLayouter;
import org.vcell.util.graphlayout.SimpleElipticalLayouter;
import org.vcell.util.gui.ActionBuilder;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;
import org.vcell.util.gui.JTabbedPaneEnhanced;
import org.vcell.util.gui.UtilCancelException;
import org.vcell.util.gui.VCellIcons;
import org.vcell.util.gui.ViewPortStabilizer;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.graph.GraphLayoutManager;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.GraphResizeManager.ZoomRangeException;
import cbit.gui.graph.actions.GraphLayoutTasks;
import cbit.gui.graph.gui.GraphPane;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEvent;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEventListener;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayEditor;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphTool;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.BioModelEntityObject;

@SuppressWarnings("serial")
public class BioModelEditorPathwayDiagramPanel extends DocumentEditorSubPanel 
implements PathwayEditor, ActionBuilder.Generator {
	
	public static enum ActionID implements ActionBuilder.ID {
		SELECT, ZOOM_IN, ZOOM_OUT, EXPAND_CANVAS, SHRINK_CANVAS, RANDOM_LAYOUT, CIRCULAR_LAYOUT, ANNEALED_LAYOUT, LEVELLED_LAYOUT, 
		RELAXED_LAYOUT, GLG_LAYOUT, SHOOT_AND_CUT_LAYOUT, WEREWOLF_LAYOUT, REACTIONS_ONLY_SHOWN, 
		REACTION_NETWORK_SHOWN, COMPONENTS;
	}
	
	public enum PathwayPanelTabID {
		pathway_diagram("Pathway Diagram"),
		pathway_objects("Pathway Objects"),
		biopax_summary("BioPAX Summary"),
		biopax_tree("BioPAX Tree");
		
		private String name = null;
		PathwayPanelTabID(String name) {
			this.name = name;
		}
		final String getName() {
			return name;
		}
	}
	
	private class PathwayPanelTab {
		PathwayPanelTabID id;
		JComponent component = null;
		Icon icon = null;
		PathwayPanelTab(PathwayPanelTabID id, JComponent component, Icon icon) {
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
	private PathwayPanelTab pathwayPanelTabs[] = new PathwayPanelTab[PathwayPanelTabID.values().length]; 
	
	private static final Dimension TOOLBAR_BUTTON_SIZE = new Dimension(28, 28);
	private EventHandler eventHandler = new EventHandler();
	private BioModel bioModel = null;
	private GraphPane graphPane;
	private PathwayGraphTool graphCartoonTool;
	private JTextArea sourceTextArea = null;
	private JTree biopaxTree = new JTree(BioPAXTreeMaker.makeEmptyTree());
	private JSortTable pathwayModelTable = null;
	private PathwayModelTableModel pathwayModelTableModel = null;
	private JTextField searchTextField;
	private JTabbedPane tabbedPane;
	private JPanel graphTabPanel;
	private JPanel sourceTabPanel;
	private JPanel treeTabPanel = new JPanel(new BorderLayout());
	private PathwayGraphModel pathwayGraphModel;
	protected JScrollPane graphScrollPane;
	protected ViewPortStabilizer viewPortStabilizer;
	
	private JButton deleteButton, physiologyLinkButton, groupButton;
	private JMenuItem importIntoModelMenuItem = null; 
	private JMenuItem showPhysiologyLinksMenuItem, editPhysiologyLinksMenuItem;
	private JMenuItem showPhysiologyLinksMenuItem1, editPhysiologyLinksMenuItem1, importIntoModelMenuItem1;
	private JPopupMenu physiologyLinkPopupMenu;
	private JPopupMenu groupPopupMenu;
	private JPopupMenu popupMenu;
	private JMenuItem groupMenuItem, ungroupMenuItem, expandMenuItem, collapseMenuItem;
	private JMenuItem groupMenuItem1, ungroupMenuItem1, expandMenuItem1, collapseMenuItem1;
	private JMenuItem deleteMenuItem, selectAllMenuItem;
	private BioPaxRelationshipPanel bioPaxRelationshipPanel;
	private ConversionPanel conversionPanel;
	
	private class EventHandler implements ListSelectionListener, AnnotationEventListener, 
			PropertyChangeListener, DocumentListener, ChangeListener, PathwayListener, ActionListener, MouseListener {

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
				tabbedPaneSelectionChanged();
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
			} else if (e.getSource() == importIntoModelMenuItem) {
				importIntoModel();
			} else if (e.getSource() == showPhysiologyLinksMenuItem1) {
				showPhysiologyLinks();
			} else if (e.getSource() == editPhysiologyLinksMenuItem1) {
				editPhysiologyLinks();	
			} else if (e.getSource() == importIntoModelMenuItem1) {
				importIntoModel();
			} else if (e.getSource() == deleteButton) {
				deleteButtonPressed();
			} else if (e.getSource() == deleteMenuItem) {
				deleteButtonPressed();
			} else if (e.getSource() == selectAllMenuItem) {
				selectAll();
			} else if(e.getSource() == groupButton) {
				getGroupPopupMenu().show(groupButton, 0, groupButton.getHeight());
			} else if (e.getSource() == groupMenuItem) {
				groupBioPaxObjects();
			} else if (e.getSource() == ungroupMenuItem) {
				ungroupBioPaxObjects();				
			} else if (e.getSource() == expandMenuItem) {
				expandBioPaxObject();				
			} else if (e.getSource() == collapseMenuItem) {
				collapseBioPaxObject();				
			} else if (e.getSource() == groupMenuItem1) {
				groupBioPaxObjects();
			} else if (e.getSource() == ungroupMenuItem1) {
				ungroupBioPaxObjects();				
			} else if (e.getSource() == expandMenuItem1) {
				expandBioPaxObject();				
			} else if (e.getSource() == collapseMenuItem1) {
				collapseBioPaxObject();		
			}
		}
		public void mouseClicked(MouseEvent e) {
			if ((e.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0) {
				getPopupMenu().show(graphScrollPane, e.getX(), e.getY());
			}
			if (e.getClickCount() == 2 ) {
				// double click to collapse/expand complex object
				if(getSelectedBioPaxObjects().size() == 1){
					BioPaxObject bpObject = getSelectedBioPaxObjects().get(0);
//					if(((bpObject instanceof Complex) ||
//					 (bpObject instanceof Protein) ||
//					 (bpObject instanceof SmallMolecule))){
//						collapseComplex();
//					}else 
					if(bpObject instanceof GroupObject){
						GroupObject groupObject = (GroupObject)bpObject;
						if(groupObject.getType().equals(GroupObject.Type.GROUPEDCOMPLEX) 
								|| groupObject.getType().equals(GroupObject.Type.GROUPEDINTERACTION)
								){
							expandBioPaxObject();
						}
					}else if(bpObject instanceof Interaction){
						collapseBioPaxObject();
					}
				}
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
	
	public BioModelEditorPathwayDiagramPanel() {
		super();
		initialize();
	}
	
	public void importIntoModel() {
		if (bioModel == null) {
			return;
		}
		String warningMessage = "";
		String warningMessage2 = "";
		int warningCount = 0;
		int warningCount2 = 0;
		String infoMessage = "";
		ArrayList <BioPaxObject> importedBPObjects = new ArrayList <BioPaxObject>();
		
		warningMessage = "The following pathway object(s) have been associated with object(s) in the physiology model:\n";
		warningMessage2 = "The following transport reaction(s) will NOT be converted to the physiology model:\n";
		warningCount = 0;
		warningCount2 = 0;
		
		infoMessage = "The following pathway object(s) have been coverted in the physiology model:\n\n";
		
		boolean hasMembrane = false;
		if(bioModel.getModel().getMembranes().size() > 0)
			hasMembrane = true;
		
		HashSet<BioPaxObject> selected = new HashSet<BioPaxObject>();
		// convert group Object to regular biopaxObjects
		for(BioPaxObject bpo : getSelectedBioPaxObjects()){
			if(bpo instanceof GroupObject){
				selected.addAll(((GroupObject)bpo).computeGroupedBioPaxObjects());
			}else{
				selected.add(bpo);
			}
		}
		
		for(BioPaxObject bpo : selected){
			  if(bpo instanceof Conversion){
				  if(bpo instanceof Transport && !hasMembrane){
					  warningCount2 ++;
					  if(((Conversion)bpo).getName().size() > 0)
						  warningMessage2 += "\nTransport: \'" + ((Conversion)bpo).getName().get(0) + "\'\n";
					  else 
						  warningMessage2 += "\nTransport: \'" + ((Conversion)bpo).getIDShort() + "\'\n";
				  }else if(bioModel.getRelationshipModel().getRelationshipObjects(bpo).size() == 0){
					  importedBPObjects.add(bpo);
				  }else{
					  warningCount ++;
					  if(((Conversion)bpo).getName().size() > 0)
						  warningMessage += "\nReaction: \'" + ((Conversion)bpo).getName().get(0) + "\' =>\n";
					  else 
						  warningMessage += "\nReaction: \'" + ((Conversion)bpo).getIDShort() + "\' =>\n";
					  for(RelationshipObject  r : bioModel.getRelationshipModel().getRelationshipObjects(bpo)){
						  warningMessage += "\t=> \'" + r.getBioModelEntityObject().getName()+"\'\n";
					  }
				  }
			  }else if(bpo instanceof PhysicalEntity){
				  if(bioModel.getRelationshipModel().getRelationshipObjects(bpo).size() == 0){
					  importedBPObjects.add(bpo);
				  }else{
					  warningCount ++;
					  if(((PhysicalEntity)bpo).getName().size() > 0)
						  warningMessage += "\nSpecies: \'" + ((PhysicalEntity)bpo).getName().get(0) + "\' =>\n";
					  else
						  warningMessage += "\nSpecies: \'" + ((PhysicalEntity)bpo).getIDShort() + "\' =>\n";
					  for(RelationshipObject  r : bioModel.getRelationshipModel().getRelationshipObjects(bpo)){
						  warningMessage += "\t=> \'" + r.getBioModelEntityObject().getName()+"\'\n";
					  }
				  }
			  }	  
		}
		
		// create import panel
		conversionPanel = new ConversionPanel();
		conversionPanel.setBioModel(bioModel);
		conversionPanel.setSelectionManager(getSelectionManager());
		
		// show warning message 
		warningMessage2 += "\nNO membrane structures available in the physiology model.\n";
		if(warningCount2 > 0){
			  DialogUtils.showWarningDialog(conversionPanel, warningMessage2);
		  }
		warningMessage += "\nThey will NOT be converted to the physiology model.\n";
		  if(warningCount > 0){
			  DialogUtils.showWarningDialog(conversionPanel, warningMessage);
		  }
		  
		if(importedBPObjects.size() == 0){
			return;
		}
		
		// set the selected objects to be the validly imported biopaxObject set
		conversionPanel.setBioPaxObjects(importedBPObjects);

		int returnCode = DialogUtils.showComponentOKCancelDialog(this, conversionPanel, "Import into Physiology");
		if (returnCode == JOptionPane.OK_OPTION) {
			PathwayMapping pathwayMapping = new PathwayMapping();
			try {
				
				// function I:
				// pass the table rows that contains user edited values to create Vcell object
				pathwayMapping.createBioModelEntitiesFromBioPaxObjects(bioModel, conversionPanel.getTableRows(), conversionPanel.isAddSubunits());
				for(BioPaxObject bpo : importedBPObjects){
					  if(bpo instanceof Conversion){
							  infoMessage += "Reaction: \t\'";
							  for(RelationshipObject  r : bioModel.getRelationshipModel().getRelationshipObjects(bpo)){
								  infoMessage += r.getBioModelEntityObject().getName()+"\'\n";
							  }
							  infoMessage += "\n";
					  }else if(bpo instanceof PhysicalEntity){
							  infoMessage += "Species: \t\'";
							  for(RelationshipObject  r : bioModel.getRelationshipModel().getRelationshipObjects(bpo)){
								  infoMessage += r.getBioModelEntityObject().getName()+"\'\n";
							  }
							  infoMessage += "\n";
					  }	  
				}
				DialogUtils.showInfoDialog(this, infoMessage);
				
				// jump the view to reaction diagram panel
				if (selectionManager != null){
					selectionManager.setActiveView(new ActiveView(null,DocumentEditorTreeFolderClass.REACTION_DIAGRAM_NODE, ActiveViewID.reaction_diagram));
				}
			}catch(Exception e)
			{
				e.printStackTrace(System.out);
				DialogUtils.showErrorDialog(this, "Errors occur when converting pathway objects to VCell bioModel objects.\n" + e.getMessage());
			}
		}
	}

	public void deleteButtonPressed() {
		deleteSelectedBioPaxObjects(this, bioModel, graphCartoonTool.getGraphModel());
	}
	public static void deleteSelectedBioPaxObjects(Component guiRequester, BioModel bioModel, GraphModel graphModel) {
		StringBuilder warning = new StringBuilder("You can NOT delete the following pathway objects:\n\n");
		StringBuilder text = new StringBuilder("You are going to DELETE the following pathway objects:\n\n");
		List<BioPaxObject> selected = getSelectedBioPaxObjects(graphModel); // all objects required by user
		List<BioPaxObject> selectedBioPaxObjects = new ArrayList<BioPaxObject> (); // the user required objects that can be deleted
		List<BioPaxObject> completeSelectedBioPaxObjects = new ArrayList<BioPaxObject> (); // all objects that will be deleted from the pathway model
		HashSet<GroupObject> undeletedGroupObjects = new HashSet<GroupObject>(); // used for recover group objects
		List<BioPaxObject> allSelectedBioPaxObjects = new ArrayList<BioPaxObject> (); // all objects required by user + grouped elements contained in selected group objects
		
		// build a list of selected objects with selected grouped elements
		for (BioPaxObject bpObject : selected){
			if(bpObject instanceof GroupObject){ // all elements of the groupObject will be deleted
				allSelectedBioPaxObjects.add(bpObject);
				// check all elements
				allSelectedBioPaxObjects.addAll(getAllGroupedObjects((GroupObject)bpObject));
			}else{
				allSelectedBioPaxObjects.add(bpObject);
			}
		}
		
		// check whether each selected object can be deleted
		// if so, whether participants and catalysts of a selected reaction can be deleted
		for (BioPaxObject bpObject : allSelectedBioPaxObjects) {
			if(canDelete(bioModel, allSelectedBioPaxObjects, bpObject)){
				selectedBioPaxObjects.add(bpObject);
				text.append("    " + bpObject.getTypeLabel() + ": \'" + PhysiologyRelationshipTableModel.getLabel(bpObject) + "\'\n");
				completeSelectedBioPaxObjects.add(bpObject);
				if(bpObject instanceof Conversion){// all its participants and its catalysts will be deleted if deleting a conversion
					// check each participant
					for(InteractionParticipant ip : ((Conversion)bpObject).getParticipants()){
						if(canDelete(bioModel, allSelectedBioPaxObjects, ip.getPhysicalEntity())) {
							completeSelectedBioPaxObjects.add(ip.getPhysicalEntity());
							// the complex of the pysicalEntity will be removed
							for(Complex complex : getComplex(bioModel, ip.getPhysicalEntity())){
								if(canDelete(bioModel, allSelectedBioPaxObjects, complex))
									completeSelectedBioPaxObjects.add(complex);
							}
						}
					}
					// check catalysts
					for(BioPaxObject bp : bioModel.getPathwayModel().getBiopaxObjects()){
						if(bp instanceof Control){
							Control control = (Control)bp;
							if(control.getControlledInteraction() == bpObject){
								completeSelectedBioPaxObjects.add(bp);
								for(PhysicalEntity pe : control.getPhysicalControllers()){
									if(canDelete(bioModel, allSelectedBioPaxObjects, pe)) 
										completeSelectedBioPaxObjects.add(pe);
								}
							}
						}
					}
				}else if(bpObject instanceof GroupObject){ // all elements of the groupObject will be deleted
					completeSelectedBioPaxObjects.add(bpObject);
					// check all elements
					completeSelectedBioPaxObjects.addAll(getAllGroupedObjects((GroupObject)bpObject));
				}
			}else{
				warning.append("    " + bpObject.getTypeLabel() + ": \'" + PhysiologyRelationshipTableModel.getLabel(bpObject) + "\'\n");
				if(bpObject instanceof GroupObject){
					undeletedGroupObjects.add((GroupObject)bpObject);
				}
			}
		}
		
		warning.append("\nThey are either required by other reactions or linked with other physiological objects.\n\n");
		
//		for(GroupObject gObject : undeletedGroupObjects){
//			for (BioPaxObject bpo : gObject.getGroupedObjects()){
//				completeSelectedBioPaxObjects.remove(bpo);
//			}
//		}
		
		StringBuilder finalWarningMessage = new StringBuilder();
		if(allSelectedBioPaxObjects.size() > selectedBioPaxObjects.size()){
			finalWarningMessage.append( warning.toString() + "\n\n");
		}
		if(selectedBioPaxObjects.size() > 0){
			text.append("\nContinue?");
			finalWarningMessage.append(text.toString());			
		}
		if (finalWarningMessage.length() == 0) {
			return;
		}
		String confirm = DialogUtils.showOKCancelWarningDialog(guiRequester, "Deleting pathway objects", finalWarningMessage.toString());
		if (confirm.equals(UserMessage.OPTION_CANCEL)) {
			return;
		}
		if (completeSelectedBioPaxObjects.size() > 0) {
			bioModel.getPathwayModel().remove(completeSelectedBioPaxObjects);
			bioModel.getPathwayModel().cleanGroupObjects();
			bioModel.getRelationshipModel().removeRelationshipObjects(completeSelectedBioPaxObjects);
		}
	}
	
	private static ArrayList<BioPaxObject> getAllGroupedObjects(GroupObject groupObject){
		ArrayList<BioPaxObject> elements = new ArrayList<BioPaxObject>();
		for(BioPaxObject bpo : groupObject.getGroupedObjects()){
			elements.add(bpo);
			if(bpo instanceof GroupObject){
				elements.addAll(getAllGroupedObjects((GroupObject)bpo));
			}
		}
		return elements;
	}
	
	private static ArrayList<Complex> getComplex(BioModel bioModel, PhysicalEntity physicalEntity){
		ArrayList<Complex> complexList = new ArrayList<Complex>();
		for(BioPaxObject bpObject: bioModel.getPathwayModel().getBiopaxObjects()){
			if(bpObject instanceof Complex){
				if(((Complex)bpObject).getComponents().contains(physicalEntity))
					complexList.add((Complex)bpObject);
			}
		}
		return complexList;
	}
	
	private static boolean canDelete(BioModel bioModel, List<BioPaxObject> selectedBioPaxObjects, BioPaxObject bpObject){
		// CANNOT delete an pathway object if it has been linked to a bioModel object
		if(bioModel.getRelationshipModel().getRelationshipObjects(bpObject).size() > 0){
			return false;
		}
		// CANNOT delete an GroupObject if its elements are required by other objects
		if(bpObject instanceof GroupObject){
			for(BioPaxObject bpo : ((GroupObject)bpObject).getGroupedObjects()){
				return canDelete(bioModel, selectedBioPaxObjects, bpo);
			}
		}
		// CANNOT delete a Object if it is in a un-deleting groupObject
		BioPaxObject ancestor = bioModel.getPathwayModel().findTopLevelGroupAncestor(bpObject);
		if(ancestor != bpObject ){
			if(selectedBioPaxObjects.contains(ancestor)){
//				return canDelete(bioModel, selectedBioPaxObjects, ancestor);
			}else{
				return false;
			}
		}
		
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
		new ActionBuilder(ActionID.SELECT, "", "Select", "Select parts of the graph", VCellIcons.pathwaySelectIcon),
		new ActionBuilder(ActionID.ZOOM_IN, "", "Zoom In", "Make graph look bigger", VCellIcons.pathwayZoomInIcon),
		new ActionBuilder(ActionID.ZOOM_OUT, "", "Zoom Out", "Make graph look smaller", VCellIcons.pathwayZoomOutIcon),
		new ActionBuilder(ActionID.RANDOM_LAYOUT, "", "Random Layout", "Reconfigure graph randomly", VCellIcons.pathwayRandomIcon),
		new ActionBuilder(ActionID.CIRCULAR_LAYOUT, "", "Circular Layout", "Reconfigure graph circular", VCellIcons.pathwayCircularIcon),
		new ActionBuilder(ActionID.ANNEALED_LAYOUT, "", "Annealed Layout", "Reconfigure graph by annealing", VCellIcons.pathwayAnnealedIcon),
		new ActionBuilder(ActionID.LEVELLED_LAYOUT, "", "Levelled Layout", "Reconfigure graph in levels", VCellIcons.pathwayLevelledIcon),
		new ActionBuilder(ActionID.RELAXED_LAYOUT, "", "Relaxed Layout", "Reconfigure graph by relaxing", VCellIcons.pathwayRelaxedIcon),
		new ActionBuilder(ActionID.GLG_LAYOUT, "", "", "Reconfigure graph by Generic Logic GraphLayout", VCellIcons.glgLayoutIcon),
		new ActionBuilder(ActionID.EXPAND_CANVAS, "", "Expand Canvas", "Expand canvas by about 10 percent", VCellIcons.expandLayoutIcon),
		new ActionBuilder(ActionID.SHRINK_CANVAS, "", "Shrink Canvas", "Shrink canvas by about 10 percent", VCellIcons.shrinkLayoutIcon),
		new ActionBuilder(ActionID.REACTIONS_ONLY_SHOWN, "", "Reactions Only", "Show only Reactions", VCellIcons.pathwayReactionsOnlyIcon),
		new ActionBuilder(ActionID.REACTION_NETWORK_SHOWN, "", "Reaction Network", "Reaction Network", VCellIcons.pathwayReactionNetworkIcon),
		new ActionBuilder(ActionID.COMPONENTS, "", "Components", "Reactions, entities and components", VCellIcons.pathwayComponentsIcon));
		
	private JPopupMenu getPhysiologyLinksPopupMenu() {
		if (physiologyLinkPopupMenu == null) {
			physiologyLinkPopupMenu = new JPopupMenu();
			showPhysiologyLinksMenuItem = new JMenuItem("Show Linked Physiology Objects");
			showPhysiologyLinksMenuItem.addActionListener(eventHandler);			
			editPhysiologyLinksMenuItem = new JMenuItem("Edit Physiology Links...");
			editPhysiologyLinksMenuItem.addActionListener(eventHandler);
			importIntoModelMenuItem = new JMenuItem("Import into Physiology...");
			importIntoModelMenuItem.addActionListener(eventHandler);
			physiologyLinkPopupMenu.add(showPhysiologyLinksMenuItem);
			physiologyLinkPopupMenu.add(editPhysiologyLinksMenuItem);
			physiologyLinkPopupMenu.add(importIntoModelMenuItem);	
		}
		refreshButtons();
		return physiologyLinkPopupMenu;
	}
	
	private JPopupMenu getPopupMenu(){
		if(popupMenu == null){
			popupMenu = new JPopupMenu();
			
			deleteMenuItem = new JMenuItem("Delete");
			deleteMenuItem.addActionListener(eventHandler);
			popupMenu.add(deleteMenuItem);
			selectAllMenuItem = new JMenuItem("Select All");
			selectAllMenuItem.addActionListener(eventHandler);
			popupMenu.add(selectAllMenuItem);
			popupMenu.addSeparator();
			
			showPhysiologyLinksMenuItem1 = new JMenuItem("Show Linked Physiology Objects");
			showPhysiologyLinksMenuItem1.addActionListener(eventHandler);			
			editPhysiologyLinksMenuItem1 = new JMenuItem("Edit Physiology Links...");
			editPhysiologyLinksMenuItem1.addActionListener(eventHandler);
			importIntoModelMenuItem1 = new JMenuItem("Import into Physiology...");
			importIntoModelMenuItem1.addActionListener(eventHandler);
			popupMenu.add(showPhysiologyLinksMenuItem1);
			popupMenu.add(editPhysiologyLinksMenuItem1);
			popupMenu.add(importIntoModelMenuItem1);	
			popupMenu.addSeparator();
			
			collapseMenuItem1 = new JMenuItem("Collapse");
			collapseMenuItem1.addActionListener(eventHandler);
			expandMenuItem1 = new JMenuItem("Expand");
			expandMenuItem1.addActionListener(eventHandler);
			groupMenuItem1 = new JMenuItem("Group");
			groupMenuItem1.addActionListener(eventHandler);			
			ungroupMenuItem1 = new JMenuItem("Ungroup");
			ungroupMenuItem1.addActionListener(eventHandler);
			popupMenu.add(collapseMenuItem1);
			popupMenu.add(expandMenuItem1);
			popupMenu.add(groupMenuItem1);
			popupMenu.add(ungroupMenuItem1);
		}
		refreshButtons();
		return popupMenu;
	}
	
	private JPopupMenu getGroupPopupMenu() {
		if (groupPopupMenu == null) {
			groupPopupMenu = new JPopupMenu();
			collapseMenuItem = new JMenuItem("Collapse");
			collapseMenuItem.addActionListener(eventHandler);
			expandMenuItem = new JMenuItem("Expand");
			expandMenuItem.addActionListener(eventHandler);
			groupMenuItem = new JMenuItem("Group");
			groupMenuItem.addActionListener(eventHandler);			
			ungroupMenuItem = new JMenuItem("Ungroup");
			ungroupMenuItem.addActionListener(eventHandler);
			groupPopupMenu.add(collapseMenuItem);
			groupPopupMenu.add(expandMenuItem);
			groupPopupMenu.add(groupMenuItem);
			groupPopupMenu.add(ungroupMenuItem);	
		}
		refreshButtons();
		return groupPopupMenu;
	}
	
	private void initialize() {
		JToolBar layoutToolBar = createToolBar(SwingConstants.HORIZONTAL);
		sourceTextArea = new JTextArea();
		
		graphPane =  new GraphPane();
		pathwayGraphModel = new PathwayGraphModel();
		pathwayGraphModel.addPropertyChangeListener(eventHandler);
		graphPane.setGraphModel(pathwayGraphModel);
		graphPane.addMouseListener(eventHandler);
		
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
		
		treeTabPanel.add(new JScrollPane(biopaxTree), BorderLayout.CENTER);
		
		pathwayModelTable = new JSortTable();
		pathwayModelTable.getSelectionModel().addListSelectionListener(eventHandler);
		pathwayModelTableModel = new PathwayModelTableModel(pathwayModelTable);
		pathwayModelTable.setModel(pathwayModelTableModel);
		
		searchTextField = new JTextField();
		searchTextField.putClientProperty("JTextField.variant", "search");
		searchTextField.getDocument().addDocumentListener(eventHandler);
		groupButton = new JButton("Group", new DownArrowIcon());
		groupButton.setHorizontalTextPosition(SwingConstants.LEFT);
		groupButton.addActionListener(eventHandler);
		deleteButton = new JButton("Delete");
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
		gbc.fill = GridBagConstraints.HORIZONTAL;
		bottomPanel.add(groupButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		bottomPanel.add(deleteButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		bottomPanel.add(physiologyLinkButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.insets = new Insets(4, 20, 4, 4);
		bottomPanel.add(new JLabel("Search "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		bottomPanel.add(searchTextField, gbc);
		
		tabbedPane = new JTabbedPaneEnhanced();
		pathwayPanelTabs[PathwayPanelTabID.pathway_diagram.ordinal()] = new PathwayPanelTab(PathwayPanelTabID.pathway_diagram, graphTabPanel, VCellIcons.diagramIcon);
		pathwayPanelTabs[PathwayPanelTabID.pathway_objects.ordinal()] = new PathwayPanelTab(PathwayPanelTabID.pathway_objects, pathwayModelTable.getEnclosingScrollPane(), VCellIcons.tableIcon);
		pathwayPanelTabs[PathwayPanelTabID.biopax_summary.ordinal()] = new PathwayPanelTab(PathwayPanelTabID.biopax_summary, sourceTabPanel, VCellIcons.textNotesIcon);
		pathwayPanelTabs[PathwayPanelTabID.biopax_tree.ordinal()] = new PathwayPanelTab(PathwayPanelTabID.biopax_tree, treeTabPanel, VCellIcons.tableIcon);
		tabbedPane.addChangeListener(eventHandler);
		tabbedPane.addChangeListener(eventHandler);
		
		for (PathwayPanelTab tab : pathwayPanelTabs) {
			tab.getComponent().setBorder(GuiConstants.TAB_PANEL_BORDER);
			tabbedPane.addTab(tab.getName(), tab.getIcon(), tab.getComponent());
		}
		
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
		groupButton.setEnabled(false);
		
		if (showPhysiologyLinksMenuItem != null) {
			showPhysiologyLinksMenuItem.setEnabled(false);
		}
		if (editPhysiologyLinksMenuItem != null) {
			editPhysiologyLinksMenuItem.setEnabled(false);
		}
		if (importIntoModelMenuItem != null) {
			importIntoModelMenuItem.setEnabled(false);
		}
		if (groupMenuItem != null) {
			groupMenuItem.setEnabled(false);
		}
		if (ungroupMenuItem != null) {
			ungroupMenuItem.setEnabled(false);
		}
		if (expandMenuItem != null) {
			expandMenuItem.setEnabled(false);
		}
		if (collapseMenuItem != null) {
			collapseMenuItem.setEnabled(false);
		}
		if (showPhysiologyLinksMenuItem1 != null) {
			showPhysiologyLinksMenuItem1.setEnabled(false);
		}
		if (editPhysiologyLinksMenuItem1 != null) {
			editPhysiologyLinksMenuItem1.setEnabled(false);
		}
		if (importIntoModelMenuItem1 != null) {
			importIntoModelMenuItem1.setEnabled(false);
		}
		if (groupMenuItem1 != null) {
			groupMenuItem1.setEnabled(false);
		}
		if (ungroupMenuItem1 != null) {
			ungroupMenuItem1.setEnabled(false);
		}
		if (expandMenuItem1 != null) {
			expandMenuItem1.setEnabled(false);
		}
		if (collapseMenuItem1 != null) {
			collapseMenuItem1.setEnabled(false);
		}
		if (deleteMenuItem != null) {
			deleteMenuItem.setEnabled(false);
		}
		if (selectAllMenuItem != null) {
			selectAllMenuItem.setEnabled(false);
		}
		if (selectionManager != null && tabbedPane.getSelectedComponent() != sourceTabPanel) {
			ArrayList<Object> selectedObjects = selectionManager.getSelectedObjects(BioPaxObject.class);
			if (bioModel.getPathwayModel().getBiopaxObjects().size() > 0 && selectAllMenuItem != null) {
				selectAllMenuItem.setEnabled(true);
			}
			if (selectedObjects.size() > 0) {	
				deleteButton.setEnabled(true);
				if (deleteMenuItem != null) {
					deleteMenuItem.setEnabled(true);
				}
				physiologyLinkButton.setEnabled(true);
				if (importIntoModelMenuItem != null) {
					importIntoModelMenuItem.setEnabled(true);
				}
				if (importIntoModelMenuItem1 != null) {
					importIntoModelMenuItem1.setEnabled(true);
				}
				if (selectedObjects.size() == 1) {
					if(bioModel.getRelationshipModel().getRelationshipObjects((BioPaxObject)selectedObjects.get(0)).size() > 0){
						if (showPhysiologyLinksMenuItem != null) {
							showPhysiologyLinksMenuItem.setEnabled(true);
						}
						if (showPhysiologyLinksMenuItem1 != null) {
							showPhysiologyLinksMenuItem1.setEnabled(true);
						}
					}
					if (editPhysiologyLinksMenuItem != null ) {
						editPhysiologyLinksMenuItem.setEnabled(true);
					}
					if (editPhysiologyLinksMenuItem1 != null ) {
						editPhysiologyLinksMenuItem1.setEnabled(true);
					}
					if((selectedObjects.get(0) instanceof GroupObject)){
						GroupObject selectedGroup = (GroupObject)selectedObjects.get(0);
						if(	//(selectedGroup.getType().equals(GroupObject.Type.GROUPEDCOMPLEX)) || // expand function for complex 
							(selectedGroup.getType().equals(GroupObject.Type.GROUPEDINTERACTION))){
							groupButton.setEnabled(true);
							if(expandMenuItem != null ){ // expand function only available when one grouped complex or interaction is selected
								expandMenuItem.setEnabled(true);
							}
							if(expandMenuItem1 != null ){ // expand function only available when one grouped complex or interaction is selected
								expandMenuItem1.setEnabled(true);
							}
						}
					}
					if( //(selectedObjects.get(0) instanceof Complex) || // collapse function for complex
						//	(selectedObjects.get(0) instanceof  SmallMolecule) ||  // collapse function for SmallMolecular
						//	(selectedObjects.get(0) instanceof Protein) ||  // collapse function for Protein
							(selectedObjects.get(0) instanceof Interaction)){
						groupButton.setEnabled(true);
						if(collapseMenuItem != null ){
							collapseMenuItem.setEnabled(true);
						}
						if(collapseMenuItem1 != null ){
							collapseMenuItem1.setEnabled(true);
						}
					}
				}
				if(selectedObjects.size() > 1){ // only provide the "group" function when users select more than one object
					groupButton.setEnabled(true);
					if (groupMenuItem != null) {
						groupMenuItem.setEnabled(true);
					}
					if (groupMenuItem1 != null) {
						groupMenuItem1.setEnabled(true);
					}
				}
				boolean includingGroup = false;
				for(Object object : selectedObjects){
					if(object instanceof GroupObject){
						includingGroup = true;
						break;
					}
				}
				if(includingGroup){ // only provide the "ungroup" function when selected objects contain at least one GroupObject
					groupButton.setEnabled(true);
					if (ungroupMenuItem != null) {
						ungroupMenuItem.setEnabled(true);
					}
					if (ungroupMenuItem1 != null) {
						ungroupMenuItem1.setEnabled(true);
					}
				}
			}
		}
	}
	
	private void refreshInterface() {
		if (bioModel == null) {
			sourceTextArea.setText("");
			biopaxTree.setModel(BioPAXTreeMaker.makeEmptyTree());
		} else {
			try {
				PathwayModel pathwayModel = bioModel.getPathwayModel();			
				sourceTextArea.setText("======Summary View========\n\n"+pathwayModel.show(false)
						+"\n"+"======Detailed View========\n\n"+pathwayModel.show(true)+"\n");
				biopaxTree.setModel(BioPAXTreeMaker.makeTree(pathwayModel));
				pathwayModel.populateDiagramObjects();
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
		pathwayGraphModel.setBioModel(bioModel);
		pathwayGraphModel.setPathwayModel(bioModel.getPathwayModel());
		refreshInterface();
	}
	
	public static List<BioPaxObject> getSelectedBioPaxObjects(GraphModel graphModel){
		ArrayList<BioPaxObject> bpObjects = new ArrayList<BioPaxObject>();
		for(Object selected : graphModel.getSelectedObjects()) {
			if(selected instanceof BioPaxObject) {
				bpObjects.add((BioPaxObject) selected);
			}
		}
		return bpObjects;
	}
	
	public List<BioPaxObject> getSelectedBioPaxObjects(){
		return getSelectedBioPaxObjects(graphCartoonTool.getGraphModel()); 
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
			case ZOOM_IN: {
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
						} 
						catch (ZoomRangeException e) { e.printStackTrace(); }
						catch (GraphModel.NotReadyException e) { e.printStackTrace(); }
					}
				};
			}
			case ZOOM_OUT: {
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
						} 
						catch (ZoomRangeException e) { e.printStackTrace(); }
						catch (GraphModel.NotReadyException e) { e.printStackTrace(); }
					}
				};
			}
			case RANDOM_LAYOUT: {
				return new LayoutAction(RandomLayouter.LAYOUT_NAME);
			}
			case CIRCULAR_LAYOUT: {
				return new LayoutAction(SimpleElipticalLayouter.LAYOUT_NAME);
			}
			case ANNEALED_LAYOUT: {
				return new LayoutAction(GraphLayoutManager.OldLayouts.ANNEALER);
			}
			case LEVELLED_LAYOUT: {
				return new LayoutAction(GraphLayoutManager.OldLayouts.LEVELLER);
			}
			case RELAXED_LAYOUT: {
				return new LayoutAction(GraphLayoutManager.OldLayouts.RELAXER);
			}
			case GLG_LAYOUT : {
				return new LayoutAction(GenericLogicGraphLayouter.LAYOUT_NAME);
			}
			case SHRINK_CANVAS : {
				return new LayoutAction(ShrinkCanvasLayouter.LAYOUT_NAME);
			}
			case EXPAND_CANVAS : {
				return new LayoutAction(ExpandCanvasLayouter.LAYOUT_NAME);
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
				selectionManager.followHyperlink(new ActiveView(null,DocumentEditorTreeFolderClass.REACTION_DIAGRAM_NODE, ActiveViewID.reaction_diagram),selectedBioModelEntityObjects.toArray(new BioModelEntityObject[0]));
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
	
	public void selectAll(){
		selectionManager.setSelectedObjects(bioModel.getPathwayModel().getBiopaxObjects().toArray());
	}
	
	private void groupBioPaxObjects(){
		HashSet<BioPaxObject> selected = new HashSet<BioPaxObject>();
		selected.addAll(getSelectedBioPaxObjects());
		if (selected.size() == 0) return;
		if(bioModel == null || bioModel.getPathwayModel() == null) return;
		PathwayGrouping pathwayGrouping = new PathwayGrouping();
		ArrayList<String> names = new ArrayList<String>();
		String id = pathwayGrouping.groupIdGenerator(bioModel.getPathwayModel());
		String newName = null;
		try{
			newName = DialogUtils.showInputDialog0(this, "Name of the GroupObject", id);
		} catch (UtilCancelException ex) {
			// user canceled; it's ok
		}
		if(newName != null ){
			if(newName.length() == 0){
				PopupGenerator.showErrorDialog(this, "The Name of the GroupObject should be provided.");
			}else{
				names.add(newName);
				GroupObject groupObject = pathwayGrouping.createGroupObject(bioModel.getPathwayModel(), 
						names, id, selected, GroupObject.Type.GROUPEDBIOPAXOBJECTS);
				if(groupObject == null){
					// error message
					DialogUtils.showErrorDialog(this, "The set of selected objects is a subset of one group object in the model. They will not be grouped together again.");
				}else{
					bioModel.getPathwayModel().add(groupObject);
					bioModel.getPathwayModel().refreshGroupMap();
					graphCartoonTool.getGraphModel().setSelectedObjects(new GroupObject[] {groupObject}); // set the grouped object to be selected
				}
			}
		}
		pathwayGraphModel.refreshAll();
	}
	
	private void ungroupBioPaxObjects(){
		HashSet<BioPaxObject> selected = new HashSet<BioPaxObject>();
		selected.addAll(getSelectedBioPaxObjects());
		if (selected.size() == 0) return;
		if(bioModel == null || bioModel.getPathwayModel() == null) return;
		for(BioPaxObject bpObject : selected){
			if(bpObject instanceof GroupObject){ // remove the GroupObject from pathway model
				GroupObject group = (GroupObject) bpObject;
				ArrayList<BioPaxObject> groupElements = new ArrayList<BioPaxObject>();
				for(BioPaxObject bpo : group.getGroupedObjects()){
					groupElements.add(bpo); 
				}
				bioModel.getPathwayModel().remove(bpObject);
				graphCartoonTool.getGraphModel().setSelectedObjects(groupElements.toArray()); // set the group elements to be selected
			}
		}
		pathwayGraphModel.refreshAll();
	}
	
	private void expandBioPaxObject(){
		ungroupBioPaxObjects();
	}
	
	private GroupObject collapse2Complex(BioPaxObject bpObject){
		PathwayGrouping pathwayGrouping = new PathwayGrouping();
		GroupObject groupObject = null;
		HashSet<BioPaxObject> hiddenobjects = new HashSet<BioPaxObject>();
		if(bpObject instanceof Complex){ // collapse components
			Complex complex = (Complex)bpObject;
			Set<BioPaxObject> bioPaxObjects = new HashSet<BioPaxObject>(bioModel.getPathwayModel().getBiopaxObjects());
			for(PhysicalEntity pe : complex.getComponents()){
				if(bioPaxObjects.contains(pe)){
					hiddenobjects.add(pe);
				}
			}
			if(hiddenobjects.size() > 0){
				hiddenobjects.add(bpObject);
				String id = pathwayGrouping.groupIdGenerator(bioModel.getPathwayModel());
				groupObject = pathwayGrouping.createGroupObject(bioModel.getPathwayModel(), 
						((Entity)bpObject).getName(), id, hiddenobjects, GroupObject.Type.GROUPEDCOMPLEX);
			}else{
				// error message
				DialogUtils.showErrorDialog(this, 
						"No Collapse action happened because the components of the complex, " 
						+ getEntityName(complex) + ", haven't been imported to pathway model.");
			}
		}else if(bpObject instanceof Protein || bpObject instanceof SmallMolecule){
			Set<BioPaxObject> bioPaxObjects = new HashSet<BioPaxObject>(bioModel.getPathwayModel().getBiopaxObjects());
			ArrayList<String> name = new ArrayList<String>();
			List<BioPaxObject> parents = bioModel.getPathwayModel().getParents(bpObject);
			if(parents.isEmpty()){
				DialogUtils.showErrorDialog(this, 
						"No Collapse action happened because protein, " 
						+ getEntityName((Entity)bpObject)+ ", doesn't involve in any complexes in the pathway data.");
				return null;
			}
			for(BioPaxObject bpo : bioModel.getPathwayModel().getParents(bpObject)){
				if(bpo instanceof Complex && bioPaxObjects.contains(bpo)){
					hiddenobjects.add(bpo);
					name.addAll(((Complex)bpo).getName());
					for(PhysicalEntity pe : ((Complex)bpo).getComponents()){
						if(bioPaxObjects.contains(pe)){
							hiddenobjects.add(pe);
						}
					}
				}
			}
			if(hiddenobjects.size() > 0){
				hiddenobjects.add(bpObject);
				String id = pathwayGrouping.groupIdGenerator(bioModel.getPathwayModel());
				if(name.size() == 0){
					name.add(id);
				}
				groupObject = pathwayGrouping.createGroupObject(bioModel.getPathwayModel(), 
						name, id, hiddenobjects, GroupObject.Type.GROUPEDCOMPLEX);
			}else{
				// error message
				DialogUtils.showErrorDialog(this, 
						"No Collapse action happened because complexes that relate to protein, " 
						+ getEntityName((Entity)bpObject)+ ", haven't been imported to pathway model.");
			}
		}
		return groupObject;
	}
	
	private void collapseComplex(){
		HashSet<BioPaxObject> selected = new HashSet<BioPaxObject>(getSelectedBioPaxObjects());
		if (selected.size() != 1) return;
		if(bioModel == null || bioModel.getPathwayModel() == null) return;
		GroupObject groupObject = null;
		for(BioPaxObject bpObject : selected){
			groupObject = collapse2Complex(bpObject);
		}
		if(groupObject != null){
			bioModel.getPathwayModel().add(groupObject);
			bioModel.getPathwayModel().refreshGroupMap();
			graphCartoonTool.getGraphModel().setSelectedObjects(new GroupObject[] {groupObject}); // set the grouped object to be selected
			pathwayGraphModel.refreshAll();
		}
	}
	
	private void collapseBioPaxObject(){
		HashSet<BioPaxObject> selected = new HashSet<BioPaxObject>(getSelectedBioPaxObjects());
		if (selected.size() == 0) return;
		if(bioModel == null || bioModel.getPathwayModel() == null) return;

		PathwayGrouping pathwayGrouping = new PathwayGrouping();
		GroupObject groupObject = null;
		HashSet<BioPaxObject> hiddenobjects = new HashSet<BioPaxObject>();
		for(BioPaxObject bpObject : selected){
			hiddenobjects.clear();
			if(bpObject instanceof Complex || bpObject instanceof Protein || bpObject instanceof SmallMolecule){
				groupObject = collapse2Complex(bpObject); // collapse complex with physicalEntities
			}else if(bpObject instanceof Interaction){
				for(InteractionParticipant itp : ((Interaction) bpObject).getParticipants()){
					hiddenobjects.add(itp.getPhysicalEntity());
				}
				hiddenobjects.add(bpObject);
				String id = pathwayGrouping.groupIdGenerator(bioModel.getPathwayModel());
				groupObject = pathwayGrouping.createGroupObject(bioModel.getPathwayModel(), 
						((Entity)bpObject).getName(), id, hiddenobjects, GroupObject.Type.GROUPEDINTERACTION);
			}
		}
		if(groupObject != null){
			bioModel.getPathwayModel().add(groupObject);
			bioModel.getPathwayModel().refreshGroupMap();
			graphCartoonTool.getGraphModel().setSelectedObjects(new GroupObject[] {groupObject}); // set the grouped object to be selected
			pathwayGraphModel.refreshAll();
		}
	}
	
	private String getEntityName(Entity bpObject){
		if(bpObject.getName().size() == 0){
			return bpObject.getIDShort();
		}else{
			return bpObject.getName().get(0);
		}
	}
	
	public void tabbedPaneSelectionChanged() {
		searchTextField.setText(null);
		searchTextField.setEditable(tabbedPane.getSelectedComponent() != sourceTabPanel);
		refreshButtons();

		int selectedIndex = tabbedPane.getSelectedIndex();
		ActiveView activeView = null;
		if (selectedIndex == PathwayPanelTabID.pathway_diagram.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.PATHWAY_DIAGRAM_NODE, ActiveViewID.pathway_diagram);
		} else if (selectedIndex == PathwayPanelTabID.pathway_objects.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.PATHWAY_OBJECTS_NODE, ActiveViewID.pathway_objects);
		} else if (selectedIndex == PathwayPanelTabID.biopax_summary.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.BIOPAX_SUMMARY_NODE, ActiveViewID.biopax_summary);
		} else if (selectedIndex == PathwayPanelTabID.biopax_tree.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.BIOPAX_TREE_NODE, ActiveViewID.biopax_tree);
		}
		if (activeView != null) {
			setActiveView(activeView);
		}
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
		case PATHWAY_DIAGRAM_NODE:
			tabbedPane.setSelectedIndex(PathwayPanelTabID.pathway_diagram.ordinal());
			break;
		case PATHWAY_OBJECTS_NODE:
			tabbedPane.setSelectedIndex(PathwayPanelTabID.pathway_objects.ordinal());
			break;
		case BIOPAX_SUMMARY_NODE:
			tabbedPane.setSelectedIndex(PathwayPanelTabID.biopax_summary.ordinal());
			break;
		case BIOPAX_TREE_NODE:
			tabbedPane.setSelectedIndex(PathwayPanelTabID.biopax_tree.ordinal());
			break;
		}
	}
}
