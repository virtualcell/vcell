package cbit.vcell.client.desktop.biomodel;

import java.awt.AWTEventMulticaster;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.ClientSimManager;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.desktop.biomodel.SPPRTreeModel.SPPRTreeFolderNode;
import cbit.vcell.client.desktop.geometry.GeometrySummaryViewer;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.FieldDataSymbol;
import static cbit.vcell.data.VFrapConstants.*;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.gui.DataSymbolsPanel;
import cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel;
import cbit.vcell.mapping.gui.InitialConditionsPanel;
import cbit.vcell.mapping.gui.ReactionSpecsPanel;
import cbit.vcell.mapping.gui.StructureMappingCartoonPanel;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.gui.ModelParameterPanel;

public class SPPRPanel extends JPanel {

	private static final long serialVersionUID = 4307362201594030746L;
	private JSplitPane outerSplitPane;
	private javax.swing.JScrollPane treePanel = null;
	private javax.swing.JTree spprTree = null;
	private GeometrySummaryViewer ivjGeometrySummaryViewer = null;
	private StructureMappingCartoonPanel ivjStructureMappingCartoonPanel = null;
	private InitialConditionsPanel initialConditionsPanel = null;
	private DataSymbolsPanel dataSymbolsPanel = null;
	private ModelParameterPanel modelParameterPanel = null;
	private ReactionSpecsPanel reactionSpecsPanel = null;
	private ElectricalMembraneMappingPanel ivjElectricalMembraneMappingPanel = null;
	private EventsDisplayPanel eventsDisplayPanel = null;
	private cbit.vcell.mapping.SimulationContext fieldSimulationContext = null;
	private SPPRTreeModel spprTreeModel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	protected transient ActionListener commandActionListener = null;
	
	// menu items to add events
	private JPopupMenu addEventPopupMenu = null;
	private JPopupMenu addDataPopupMenu = null;
	private JPopupMenu deleteEventPopupMenu = null;
	private JMenuItem menuItemAddEvent = null;
	private JMenuItem menuItemAddGenericData = null;
	private JMenuItem menuItemAddVFrapData = null;
	private JMenuItem menuItemDeleteEvent = null;
	private ClientSimManager clientSimManager = null;
	
	class IvjEventHandler implements javax.swing.event.TreeSelectionListener, MouseListener, PropertyChangeListener, ActionListener {
		
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == SPPRPanel.this.getSpprTree())
				treeValueChanged(e);
		}
		public void mouseClicked(MouseEvent e) {
			boolean bRightClick = SwingUtilities.isRightMouseButton(e);		
			if (bRightClick) {
				if (e.getSource() == SPPRPanel.this.getSpprTree()) {			
					DefaultMutableTreeNode currentTreeSelection = (DefaultMutableTreeNode)getSpprTree().getLastSelectedPathComponent();
					if(currentTreeSelection == null){
						return;
					}
					Object selectedObject = currentTreeSelection.getUserObject();
					Point mousePoint = e.getPoint();
					// offer "New" popup menu only for DataSymbols folder
					if (selectedObject instanceof SPPRTreeFolderNode) {
						SPPRTreeFolderNode stfn = (SPPRTreeFolderNode)selectedObject;
						if (stfn.getName().equals(SPPRTreeModel.FOLDER_NODE_NAMES[SPPRTreeModel.DATA_SYMBOLS_NODE])) {	// Data Symbols
							getAddDataPopupMenu().show(getSpprTree(), mousePoint.x, mousePoint.y);
						}
					}
					// deactivate pop-up menu if simulationContext is spatial or stochastic
					int dimension = fieldSimulationContext.getGeometry().getDimension();
					if (dimension == 0 && !fieldSimulationContext.isStoch()) {
						if (selectedObject instanceof SPPRTreeFolderNode) {
							SPPRTreeFolderNode stfn = (SPPRTreeFolderNode)selectedObject;
							if (stfn.getName().equals(SPPRTreeModel.FOLDER_NODE_NAMES[SPPRTreeModel.EVENTS_NODE])) { // "Events"
								getAddEventPopupMenu().show(getSpprTree(), mousePoint.x, mousePoint.y);
							}
	
						} else if (selectedObject instanceof BioEvent) {
							getDeleteEventPopupMenu().show(getSpprTree(), mousePoint.x, mousePoint.y);
						}
					}
				}					
			} 
		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == getEventsDisplayPanel() && evt.getPropertyName().equals("selectedBioEvent")) {
				getSpprTreeModel().setSelectedValue(evt.getNewValue());
			}
			if (evt.getSource() == fieldSimulationContext && evt.getPropertyName().equals("geometry")) {
				getGeometrySummaryViewer().setGeometry(fieldSimulationContext.getGeometry());
			}
			if ((evt.getPropertyName().equals("simulationWorkspace"))) 
			{
				setClientSimManager(((ApplicationEditor)evt.getSource()).getSimulationWorkspace().getClientSimManager());
			}
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(getMenuItemAddEvent())) {
				addEvent();
			}
			if (e.getSource().equals(getMenuItemDeleteEvent())) {
				deleteEvent();
			}
			if (e.getSource().equals(getMenuItemAddGenericData())) {
				addGenericDataSymbol();
			}
			if (e.getSource().equals(getMenuItemAddVFrapData())) {
				addVFrapDataSymbol();
			}
			if (e.getSource().equals(getGeometrySummaryViewer())) {
				refireCommandActionPerformed(e);
			}
			
		}	
	};
	
	private void refireCommandActionPerformed(ActionEvent e) {
		// relays an action event with this as the source
		fireCommandActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
	}
	
	public synchronized void addCommandActionListener(ActionListener l) {
		commandActionListener = AWTEventMulticaster.add(commandActionListener, l);
	}
	
	public synchronized void removeCommandActionListener(ActionListener l) {
		commandActionListener = AWTEventMulticaster.remove(commandActionListener, l);
	}

	protected void fireCommandActionPerformed(ActionEvent e) {
		if (commandActionListener != null) {
			commandActionListener.actionPerformed(e);
		}         
	}

	public static void main(String[] args) {
	}

	public SPPRPanel() {
		super();
		setLayout(new GridBagLayout());
		initialize();
	}
	
	public void setSimulationContext(SimulationContext simulationContext) {
		if (fieldSimulationContext!=null){ // unlisten to the old simulation context.
			fieldSimulationContext.removePropertyChangeListener(ivjEventHandler);
		}
		fieldSimulationContext = simulationContext;
		if (fieldSimulationContext!=null){
			fieldSimulationContext.addPropertyChangeListener(ivjEventHandler); // listen to the new simlation context.
			getGeometrySummaryViewer().setGeometry(fieldSimulationContext.getGeometry());
		}else{
			getGeometrySummaryViewer().setGeometry(null);
		}
		getStructureMappingCartoonPanel().setSimulationContext(fieldSimulationContext);
		getInitialConditionsPanel().setSimulationContext(fieldSimulationContext);
		getDataSymbolsPanel().setSimulationContext(fieldSimulationContext);
		getModelParameterPanel().setModel(fieldSimulationContext.getModel());
		getReactionSpecsPanel().setSimulationContext(fieldSimulationContext);
		getEventsDisplayPanel().setSimulationContext(fieldSimulationContext);
		getSpprTreeModel().setSimulationContext(fieldSimulationContext);
		getElectricalMembraneMappingPanel().setSimulationContext(fieldSimulationContext);
		getSpprTree().setSelectionRow(SPPRTreeModel.STRUCTURE_MAPPING_NODE+1); // +1 is to go from index (0 based)to row number (1 based).
	}
	
	private void handleException(java.lang.Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}	

	private void initConnections() throws java.lang.Exception {
		getGeometrySummaryViewer().addActionListener(ivjEventHandler);
		spprTree.addTreeSelectionListener(ivjEventHandler);
		spprTree.addMouseListener(ivjEventHandler);
		spprTree.addTreeExpansionListener(getSpprTreeModel());
		getEventsDisplayPanel().addPropertyChangeListener(ivjEventHandler);
		getMenuItemAddEvent().addActionListener(ivjEventHandler);
		getMenuItemDeleteEvent().addActionListener(ivjEventHandler);
		getMenuItemAddGenericData().addActionListener(ivjEventHandler);
		getMenuItemAddVFrapData().addActionListener(ivjEventHandler);
	}	

	private void initialize() {
		try {
//			System.out.println("SPPRPanel:  initialize()");
			setName("SPPRPanel");
			setSize(750, 560);
			
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.weighty = 1;
			gridBagConstraints.weightx = 1;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			add(getOuterSplitPane(), gridBagConstraints);

			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	private JSplitPane getOuterSplitPane() {
		if (outerSplitPane == null) {
			outerSplitPane = new JSplitPane();
			outerSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			
			outerSplitPane.setLeftComponent(getTreePanel());
			outerSplitPane.setRightComponent(getStructureMappingCartoonPanel());
		}
		return outerSplitPane;
	}

// ------------ Left Panel -----------------------
	
	private javax.swing.JScrollPane getTreePanel() {
		if (treePanel == null) {
			try {
				treePanel = new javax.swing.JScrollPane();
				treePanel.setName("LeftTreePanel");
				Dimension dim = new Dimension(200, 20);
				treePanel.setPreferredSize(dim);
				treePanel.setViewportView(getSpprTree());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return treePanel;
	}
	private javax.swing.JTree getSpprTree() {
		if (spprTree == null) {
			try {
				spprTree = new javax.swing.JTree();
				spprTree.setName("JParameterTree");
				ToolTipManager.sharedInstance().registerComponent(spprTree);
			    spprTree.setCellRenderer(new SPPRTreeCellRenderer());				
				spprTree.setBounds(0, 0, 78, 72);
				spprTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
				spprTree.setModel(getSpprTreeModel());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return spprTree;
	}
	
	private SPPRTreeModel getSpprTreeModel() {
		if (spprTreeModel == null) {
			try {
				spprTreeModel = new SPPRTreeModel(getSpprTree());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return spprTreeModel;
	}
	
	public void treeValueChanged(TreeSelectionEvent e) {
		try {
			Object node = spprTree.getLastSelectedPathComponent();
			if (!(node instanceof BioModelNode)) {
				return;
			}
			BioModelNode selectedNode = (BioModelNode)node;
		    Object userObject = selectedNode.getUserObject();
		    if (userObject instanceof SPPRTreeFolderNode) { // it's a folder
		        setupRightComponent((SPPRTreeFolderNode)userObject, null);
		    } else if (userObject instanceof SimulationContext){
		    	outerSplitPane.setRightComponent(new JPanel());
		    	outerSplitPane.setDividerLocation(200);
		    } else {
		        Object leaf = userObject;
				BioModelNode parentNode = (BioModelNode) selectedNode.getParent();
				userObject =  parentNode.getUserObject();
				SPPRTreeFolderNode parent = (SPPRTreeFolderNode)userObject;
		        setupRightComponent(parent, leaf);
		    }
		}catch (Exception ex){
			ex.printStackTrace(System.out);
		}
	}
	
	private void setupRightComponent(SPPRTreeFolderNode folderNode, Object leaf) {
		int folderId = folderNode.getId();
		if (SPPRTreeModel.FOLDER_NODE_IMPLEMENTED[folderId] && folderNode.isSupported()) {
			if(folderId == SPPRTreeModel.GEOMETRY_NODE) {
				//  replace right-side panel only if the correct one is not there already
				if(outerSplitPane.getRightComponent() != getGeometrySummaryViewer()) {
					outerSplitPane.setRightComponent(getGeometrySummaryViewer());
				}
			} else if(folderId == SPPRTreeModel.STRUCTURE_MAPPING_NODE) {
				//  replace right-side panel only if the correct one is not there already
				if(outerSplitPane.getRightComponent() != getStructureMappingCartoonPanel()) {
					outerSplitPane.setRightComponent(getStructureMappingCartoonPanel());
				}
			} else if(folderId == SPPRTreeModel.INITIAL_CONDITIONS_NODE) {
				//  replace right-side panel only if the correct one is not there already
				if(outerSplitPane.getRightComponent() != getInitialConditionsPanel()) {
					outerSplitPane.setRightComponent(getInitialConditionsPanel());
				}
				getInitialConditionsPanel().setScrollPaneTableCurrentRow((SpeciesContext)leaf);	// notify right panel about selection change
			} else if(folderId == SPPRTreeModel.DATA_SYMBOLS_NODE) {
				if(outerSplitPane.getRightComponent() != getDataSymbolsPanel()) {
					outerSplitPane.setRightComponent(getDataSymbolsPanel());
				}
				getDataSymbolsPanel().setScrollPaneTableCurrentRow((DataSymbol)leaf);	// notify right panel about selection change
			} else if(folderId == SPPRTreeModel.GLOBAL_PARAMETER_NODE) {
				if(outerSplitPane.getRightComponent() != getModelParameterPanel()) {
					outerSplitPane.setRightComponent(getModelParameterPanel());
				}
				getModelParameterPanel().setScrollPaneTableCurrentRow((ModelParameter)leaf);	// notify right panel about selection change
			} else if(folderId == SPPRTreeModel.REACTIONS_NODE) {
				if(outerSplitPane.getRightComponent() != getReactionSpecsPanel()) {
					outerSplitPane.setRightComponent(getReactionSpecsPanel());
				}
				getReactionSpecsPanel().setScrollPaneTableCurrentRow((ReactionStep)leaf);	// notify right panel about selection change
			} else if(folderId == SPPRTreeModel.EVENTS_NODE) {
				if(outerSplitPane.getRightComponent() != getEventsDisplayPanel()) {
					outerSplitPane.setRightComponent(getEventsDisplayPanel());
				}
//				getEventsDisplayPanel().setScrollPaneTableCurrentRow((BioEvent)leaf);	// notify right panel about selection change
			} else if(folderId == SPPRTreeModel.ELECTRICAL_MAPPING_NODE) {
				if(outerSplitPane.getRightComponent() != getElectricalMembraneMappingPanel()) {
					outerSplitPane.setRightComponent(getElectricalMembraneMappingPanel());
				}
			}
		} else {
			JPanel emptyPanel = new JPanel();
			outerSplitPane.setRightComponent(emptyPanel);
		}
		outerSplitPane.setDividerLocation(200);
	}
	
	public void setScrollPaneTreeCurrentRow(Object selection) {
		if (selection == null) {
			return;
		}
		BioModelNode node = (BioModelNode)spprTree.getLastSelectedPathComponent();
		Object leaf = null;
		if (node != null) {
			leaf = node.getUserObject();
	    }
		if(selection instanceof SpeciesContext) {
			selectNode(leaf, selection, SPPRTreeModel.INITIAL_CONDITIONS_NODE);
		} else if(selection instanceof ModelParameter) {
			selectNode(leaf, selection, SPPRTreeModel.GLOBAL_PARAMETER_NODE);
		} else if(selection instanceof ReactionStep) {
			selectNode(leaf, selection, SPPRTreeModel.REACTIONS_NODE);
		} else if(selection instanceof FieldDataSymbol) {
			selectNode(leaf, selection, SPPRTreeModel.DATA_SYMBOLS_NODE);
		} else {
			System.out.println(selection.getClass() + " table selection changed");
		}
	}
	private void selectNode(Object oldSelection, Object newSelection, int whatNode) {
		if(oldSelection.equals(newSelection)) {	// already selected
		} else {
			BioModelNode folderRoot = getSpprTreeModel().folderNodes[whatNode];
			BioModelNode foundNode = folderRoot.findNodeByUserObject(newSelection);
			if(foundNode == null) {
				return;
			}
			TreePath treePath = new TreePath(foundNode.getPath());
			spprTree.setSelectionPath(treePath);
			spprTree.scrollPathToVisible(treePath);
		}
	}
		
//------------- Right Panel	-----------------------
	private GeometrySummaryViewer getGeometrySummaryViewer() {
		if (ivjGeometrySummaryViewer == null) {
			try {
				ivjGeometrySummaryViewer = new GeometrySummaryViewer();
				ivjGeometrySummaryViewer.setName("GeometrySummaryViewer");
				ivjGeometrySummaryViewer.setPreferredSize(new Dimension(500,500));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjGeometrySummaryViewer;
	}
	
	private StructureMappingCartoonPanel getStructureMappingCartoonPanel() {
		if (ivjStructureMappingCartoonPanel == null) {
			try {
				ivjStructureMappingCartoonPanel = new StructureMappingCartoonPanel();
				ivjStructureMappingCartoonPanel.setName("StructureMappingCartoonPanel");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjStructureMappingCartoonPanel;
	}
	
	private DataSymbolsPanel getDataSymbolsPanel() {
		if (dataSymbolsPanel == null) {
			try {
				dataSymbolsPanel = new DataSymbolsPanel();
				dataSymbolsPanel.setName("DataSymbolsPanel");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return dataSymbolsPanel;
	}
	
	private InitialConditionsPanel getInitialConditionsPanel() {
		if (initialConditionsPanel == null) {
			try {
				initialConditionsPanel = new InitialConditionsPanel();
				initialConditionsPanel.setName("InitialConditionsPanel");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return initialConditionsPanel;
	}
	
	private ModelParameterPanel getModelParameterPanel() {
		if (modelParameterPanel == null) {
			try {
				modelParameterPanel = new ModelParameterPanel();
				modelParameterPanel.setName("ModelParameterPanel");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return modelParameterPanel;
	}
	
	private ReactionSpecsPanel getReactionSpecsPanel() {
		if (reactionSpecsPanel == null) {
			try {
				reactionSpecsPanel = new ReactionSpecsPanel();
				reactionSpecsPanel.setName("ReactionSpecsPanel");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return reactionSpecsPanel;
	}
	
	private EventsDisplayPanel getEventsDisplayPanel() {
		if (eventsDisplayPanel == null) {
			try {
				eventsDisplayPanel = new EventsDisplayPanel();
				eventsDisplayPanel.setName("EventsDisplayPanel");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return eventsDisplayPanel;
	}	
	
	private ElectricalMembraneMappingPanel getElectricalMembraneMappingPanel() {
		if (ivjElectricalMembraneMappingPanel == null) {
			try {
				ivjElectricalMembraneMappingPanel = new ElectricalMembraneMappingPanel();
				ivjElectricalMembraneMappingPanel.setName("ElectricalMembraneMappingPanel");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjElectricalMembraneMappingPanel;
	}
	
	private JMenuItem getMenuItemAddGenericData() {
		if (menuItemAddGenericData == null) {
			try {
				menuItemAddGenericData = new javax.swing.JMenuItem();
				menuItemAddGenericData.setName("JMenuItemAddData");
				menuItemAddGenericData.setMnemonic('d');
				menuItemAddGenericData.setText(ADD_VFRAP_DATASET_MENU);
				menuItemAddGenericData.setEnabled(true);
				menuItemAddGenericData.setActionCommand(GuiConstants.ACTIONCMD_ADD_GENERIC_DATA);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return menuItemAddGenericData;
	}
	private JMenuItem getMenuItemAddVFrapData() {
		if (menuItemAddVFrapData == null) {
			try {
				menuItemAddVFrapData = new javax.swing.JMenuItem();
				menuItemAddVFrapData.setName("JMenuItemAddVFrapData");
				menuItemAddVFrapData.setMnemonic('v');
				menuItemAddVFrapData.setText(ADD_VFRAP_SPECIALS_MENU);
				menuItemAddVFrapData.setEnabled(true);
				menuItemAddVFrapData.setActionCommand(GuiConstants.ACTIONCMD_ADD_VFRAP_DATA);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return menuItemAddVFrapData;
	}
	
	private JPopupMenu getAddDataPopupMenu() {
		if (addDataPopupMenu == null) {
			try {
				addDataPopupMenu = new javax.swing.JPopupMenu();
				addDataPopupMenu.setName("DataSymbolPopupMenu");
				addDataPopupMenu.add(getMenuItemAddGenericData());
				addDataPopupMenu.add(getMenuItemAddVFrapData());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return addDataPopupMenu;
	}

	private void addGenericDataSymbol() {
		getDataSymbolsPanel().addVFrapOriginalImages();
	}
	private void addVFrapDataSymbol() {
		getDataSymbolsPanel().addVFrapDerivedImages();
	}

/*	private void addDataSymbol() {
		String name = null;
		try {
		// vcField('eeeD','fluor_0.32_eeeD',0.32373046875,'Volume');
			getDataSymbolsPanel().getNewDataSymbolPanel().setSymbolName("");
			getDataSymbolsPanel().getNewDataSymbolPanel().setSymbolExpression("vcField(dataset1,var1,0.0,Volume)");
			int newSettings = org.vcell.util.gui.DialogUtils.showComponentOKCancelDialog(this, getDataSymbolsPanel().getNewDataSymbolPanel(), "New DataSymbol");
			if (newSettings == JOptionPane.OK_OPTION) {
				name = getDataSymbolsPanel().getNewDataSymbolPanel().getSymbolName();
				String expression = getDataSymbolsPanel().getNewDataSymbolPanel().getSymbolExpression();
				Expression exp = new Expression(expression);
				FunctionInvocation[] functionInvocations = exp.getFunctionInvocations(null);
				DataSymbol ds = new FieldDataSymbol(name, getDataSymbolsPanel().getSimulationContext().getDataContext(), VCUnitDefinition.UNIT_TBD, new FieldFunctionArguments(functionInvocations[0]));
				getDataSymbolsPanel().getSimulationContext().getDataContext().addDataSymbol(ds);
			}
		} catch (java.lang.Throwable ivjExc) {
			DialogUtils.showErrorDialog(this, "Data symbol " + name + " already exists");
		}
	}*/
	
	private JMenuItem getMenuItemAddEvent() {
		if (menuItemAddEvent == null) {
			try {
				menuItemAddEvent = new javax.swing.JMenuItem();
				menuItemAddEvent.setName("JMenuItemAddEvent");
				menuItemAddEvent.setMnemonic('a');
				menuItemAddEvent.setText("Add Event");
				menuItemAddEvent.setActionCommand(GuiConstants.ACTIONCMD_ADD_EVENT);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return menuItemAddEvent;
	}
	
	private JPopupMenu getAddEventPopupMenu() {
		if (addEventPopupMenu == null) {
			try {
				addEventPopupMenu = new javax.swing.JPopupMenu();
				addEventPopupMenu.setName("EventPopupMenu");
				addEventPopupMenu.add(getMenuItemAddEvent());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return addEventPopupMenu;
	}
	
	private JPopupMenu getDeleteEventPopupMenu() {
		if (deleteEventPopupMenu == null) {
			try {
				deleteEventPopupMenu = new javax.swing.JPopupMenu();
				deleteEventPopupMenu.add(getMenuItemDeleteEvent());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return deleteEventPopupMenu;
	}

	private JMenuItem getMenuItemDeleteEvent() {
		if (menuItemDeleteEvent == null) {
			try {
				menuItemDeleteEvent = new javax.swing.JMenuItem();
				menuItemDeleteEvent.setName("JMenuItemAddEvent");
				menuItemDeleteEvent.setMnemonic('a');
				menuItemDeleteEvent.setText("Delete");
				menuItemDeleteEvent.setActionCommand(GuiConstants.ACTIONCMD_ADD_EVENT);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return menuItemDeleteEvent;
	}

	private void addEvent() {
		String eventName = fieldSimulationContext.getFreeEventName();
		try {
			BioEvent bioEvent = new BioEvent(eventName, fieldSimulationContext);
			fieldSimulationContext.addBioEvent(bioEvent);
//			getEventsDisplayPanel().selectEvent(bioEvent);
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, "Error adding Event : " + e.getMessage());
		}
	}
	
	public void deleteEvent() {
		DefaultMutableTreeNode currentTreeSelection = (DefaultMutableTreeNode)getSpprTree().getLastSelectedPathComponent();
		if(currentTreeSelection == null){
			return;
		}
		Object selectedObject = currentTreeSelection.getUserObject();;
		if (selectedObject instanceof BioEvent) {
			BioEvent bioEvent = (BioEvent)selectedObject;
			try {
				fieldSimulationContext.removeBioEvent(bioEvent);
			} catch (PropertyVetoException ex) {
				ex.printStackTrace(System.out);
				DialogUtils.showErrorDialog(this, ex.getMessage());
			}
		}		
	}
	
	public ClientSimManager getClientSimManager() {
		return clientSimManager;
	}

	public void setClientSimManager(ClientSimManager clientSimManager)
	{
		this.clientSimManager = clientSimManager;
	}
}
