package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;

import cbit.image.ImageException;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;

@SuppressWarnings("serial")
public class BioModelEditorApplicationsPanel extends BioModelEditorRightSidePanel<SimulationContext> {
	private static final String MENU_TEXT_SPATIAL_APPLICATION = "Spatial";
	private static final String MENU_TEXT_NON_SPATIAL_APPLICATION = "Non-Spatial";
	private static final String MENU_TEXT_DETERMINISTIC_APPLICATION = "Deterministic";
	private static final String MENU_TEXT_STOCHASTIC_APPLICATION = "Stochastic";

	private ApplicationPropertiesPanel applicationPropertiesPanel = null;
	private JButton moreActionsButton = null;
	// application popup menu items
	private JPopupMenu ivjAppsPopupMenu = null;
	private JPopupMenu ivjMoreActionsPopupMenu = null;
	
	private JMenu ivjJMenuAppCopyAs = null;
	private JMenuItem menuItemAppNonSpatialCopyStochastic = null;
	private JMenuItem menuItemNonSpatialCopyDeterministic = null;
	
	private JMenu menuAppSpatialCopyAsNonSpatial = null;
	private JMenuItem menuItemAppSpatialCopyAsNonSpatialStochastic = null;
	private JMenuItem menuItemAppSpatialCopyAsNonSpatialDeterministic = null;
	private JMenu menuAppSpatialCopyAsSpatial = null;
	private JMenuItem menuItemAppSpatialCopyAsSpatialStochastic = null;
	private JMenuItem menuItemAppSpatialCopyAsSpatialDeterministic = null;
	
	private JMenuItem ivjJMenuItemAppDelete = null;
	private JMenuItem appNewStochApp = null;
	private JMenuItem appNewDeterministicApp = null;
	private JMenuItem ivjJMenuItemAppCopy = null;	
	private BioModelWindowManager bioModelWindowManager = null;
	
	private EventHandler eventHandler = new EventHandler();
	
	private class EventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == moreActionsButton) {
				moreActionsButtonPressed();
			} else if (e.getSource() == getJMenuItemAppDelete()
					|| e.getSource() == appNewStochApp
					|| e.getSource() == appNewDeterministicApp
					|| e.getSource() == getJMenuItemAppCopy()
					|| e.getSource() == menuItemAppSpatialCopyAsNonSpatialDeterministic
					|| e.getSource() == menuItemAppSpatialCopyAsNonSpatialStochastic
					|| e.getSource() == menuItemAppSpatialCopyAsSpatialDeterministic
					|| e.getSource() == menuItemAppSpatialCopyAsSpatialStochastic) {
				applicationMenuItem_ActionPerformed(e)	;	
			} 
		}
	}

	public BioModelEditorApplicationsPanel() {
		super();
		initialize();
	}
        
	private void initialize() {
		applicationPropertiesPanel = new ApplicationPropertiesPanel();
		newButton.setIcon(new DownArrowIcon());
		newButton.setHorizontalTextPosition(SwingConstants.LEFT);
		moreActionsButton = new JButton("More Actions");
		moreActionsButton.setIcon(new DownArrowIcon());
		moreActionsButton.setHorizontalTextPosition(SwingConstants.LEFT);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);		
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
		gbc.insets = new Insets(4,50,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		topPanel.add(newButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		topPanel.add(deleteButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.insets = new Insets(4,4,4,20);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		topPanel.add(moreActionsButton, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 6;
		gbc.fill = GridBagConstraints.BOTH;
		topPanel.add(table.getEnclosingScrollPane(), gbc);
		
		splitPane.setDividerLocation(350);
		splitPane.setTopComponent(topPanel);
		splitPane.setBottomComponent(applicationPropertiesPanel);
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
		
		moreActionsButton.addActionListener(eventHandler);		
		getJMenuItemAppDelete().addActionListener(eventHandler);
		getJMenuAppCopyAs().addActionListener(eventHandler);
		getJMenuItemAppCopy().addActionListener(eventHandler);
	}
	
	public static void main(java.lang.String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			JFrame frame = new javax.swing.JFrame();
			BioModelEditorApplicationsPanel panel = new BioModelEditorApplicationsPanel();
			frame.add(panel);
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.pack();
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}
	
	protected void newButtonPressed() {
		getNewAppPopupMenu().show(newButton, 0, newButton.getHeight());
	}

	private void moreActionsButtonPressed() {
		getMoreActionsPopupMenu().show(moreActionsButton, 0, newButton.getHeight());
	}
	
	protected void deleteButtonPressed() {
		int[] rows = table.getSelectedRows();
		ArrayList<SimulationContext> deleteList = new ArrayList<SimulationContext>();
		for (int r : rows) {
			if (r < tableModel.getDataSize()) {
				deleteList.add(tableModel.getValueAt(r));
			}
		}
		try {
			for (SimulationContext sc : deleteList) {
				bioModel.removeSimulationContext(sc);
			}
			applicationPropertiesPanel.setSimulationContext(null);
		} catch (PropertyVetoException ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(BioModelEditorApplicationsPanel.this, ex.getMessage());
		}
	}

	@Override
	protected BioModelEditorRightSideTableModel<SimulationContext> createTableModel() {
		return new BioModelEditorApplicationsTableModel(table);
	}

	@Override
	protected void tableSelectionChanged() {
		super.tableSelectionChanged();
		int[] rows = table.getSelectedRows();
		if (rows != null && rows.length == 1 && rows[0] < tableModel.getDataSize()) {					
			applicationPropertiesPanel.setSimulationContext(tableModel.getValueAt(rows[0]));
		} else {
			applicationPropertiesPanel.setSimulationContext(null);
		}
	}

	private javax.swing.JPopupMenu getMoreActionsPopupMenu() {
		if (ivjMoreActionsPopupMenu == null) {
			try {
				ivjMoreActionsPopupMenu = new javax.swing.JPopupMenu();
				ivjMoreActionsPopupMenu.setName("AppPopupMenu");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		ivjMoreActionsPopupMenu.removeAll();
		ivjMoreActionsPopupMenu.add(getJMenuItemAppCopy());
		ivjMoreActionsPopupMenu.add(getJMenuAppCopyAs());
		return ivjMoreActionsPopupMenu;
	}

	private javax.swing.JPopupMenu getNewAppPopupMenu() {
		if (ivjAppsPopupMenu == null) {
			try {
				ivjAppsPopupMenu = new javax.swing.JPopupMenu();
				ivjAppsPopupMenu.setName("AppPopupMenu");
				
				//Menu items in Menu-New
				appNewStochApp=new JMenuItem();
				appNewStochApp.setName("JMenuItemStochApp");
				appNewStochApp.setText(MENU_TEXT_STOCHASTIC_APPLICATION);
				appNewStochApp.setActionCommand(GuiConstants.ACTIONCMD_CREATE_STOCHASTIC_APPLICATION);
				appNewStochApp.addActionListener(eventHandler);
				
				appNewDeterministicApp = new javax.swing.JMenuItem();
				appNewDeterministicApp.setName("JMenuItemNonStochApp");
				appNewDeterministicApp.setText(MENU_TEXT_DETERMINISTIC_APPLICATION);
				appNewDeterministicApp.setActionCommand(GuiConstants.ACTIONCMD_CREATE_NON_STOCHASTIC_APPLICATION);
				appNewDeterministicApp.addActionListener(eventHandler);
				
				//add menu items to menu
				ivjAppsPopupMenu.add(appNewDeterministicApp);
				ivjAppsPopupMenu.add(appNewStochApp);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjAppsPopupMenu;
	}

	/**
	 * Return the JMenuItemCopy property value.
	 * @return javax.swing.JMenuItem
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JMenu getJMenuAppCopyAs() {
		if (ivjJMenuAppCopyAs == null) {
			try {
				ivjJMenuAppCopyAs = new javax.swing.JMenu();
				ivjJMenuAppCopyAs.setName("JMenuCopy");
				ivjJMenuAppCopyAs.setText("Copy As");
				//Menu items in Menu-Copy
				menuItemAppNonSpatialCopyStochastic=new JMenuItem();
				menuItemAppNonSpatialCopyStochastic.setName("JMenuItemToStochApp");
				menuItemAppNonSpatialCopyStochastic.setText(MENU_TEXT_STOCHASTIC_APPLICATION);
				menuItemAppNonSpatialCopyStochastic.setActionCommand(GuiConstants.ACTIONCMD_NON_SPATIAL_COPY_TO_STOCHASTIC_APPLICATION);
				menuItemAppNonSpatialCopyStochastic.addActionListener(eventHandler);
				
				menuItemNonSpatialCopyDeterministic = new javax.swing.JMenuItem();
				menuItemNonSpatialCopyDeterministic.setName("JMenuItemToNonStochApp");
				menuItemNonSpatialCopyDeterministic.setText(MENU_TEXT_DETERMINISTIC_APPLICATION);
				menuItemNonSpatialCopyDeterministic.setActionCommand(GuiConstants.ACTIONCMD_NON_SPATIAL_COPY_TO_DETERMINISTIC_APPLICATION);
				menuItemNonSpatialCopyDeterministic.addActionListener(eventHandler);				
				
				menuAppSpatialCopyAsNonSpatial = new JMenu(MENU_TEXT_NON_SPATIAL_APPLICATION);
				menuItemAppSpatialCopyAsNonSpatialDeterministic = new JMenuItem(MENU_TEXT_DETERMINISTIC_APPLICATION);
				menuItemAppSpatialCopyAsNonSpatialDeterministic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_DETERMINISTIC_APPLICATION);
				menuItemAppSpatialCopyAsNonSpatialDeterministic.addActionListener(eventHandler);
				menuItemAppSpatialCopyAsNonSpatialStochastic = new JMenuItem(MENU_TEXT_STOCHASTIC_APPLICATION);
				menuItemAppSpatialCopyAsNonSpatialStochastic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_STOCHASTIC_APPLICATION);
				menuItemAppSpatialCopyAsNonSpatialStochastic.addActionListener(eventHandler);
				menuAppSpatialCopyAsNonSpatial.add(menuItemAppSpatialCopyAsNonSpatialDeterministic);
				menuAppSpatialCopyAsNonSpatial.add(menuItemAppSpatialCopyAsNonSpatialStochastic);
				
				menuAppSpatialCopyAsSpatial = new JMenu(MENU_TEXT_SPATIAL_APPLICATION);
				menuItemAppSpatialCopyAsSpatialDeterministic = new JMenuItem(MENU_TEXT_DETERMINISTIC_APPLICATION);
				menuItemAppSpatialCopyAsSpatialDeterministic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_DETERMINISTIC_APPLICATION);
				menuItemAppSpatialCopyAsSpatialDeterministic.addActionListener(eventHandler);
				menuItemAppSpatialCopyAsSpatialStochastic = new JMenuItem(MENU_TEXT_STOCHASTIC_APPLICATION);
				menuItemAppSpatialCopyAsSpatialStochastic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_STOCHASTIC_APPLICATION);
				menuItemAppSpatialCopyAsSpatialStochastic.addActionListener(eventHandler);
				menuAppSpatialCopyAsSpatial.add(menuItemAppSpatialCopyAsSpatialDeterministic);
				menuAppSpatialCopyAsSpatial.add(menuItemAppSpatialCopyAsSpatialStochastic);
	
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		ivjJMenuAppCopyAs.removeAll();
		SimulationContext selectedSimContext = getSelectedSimulationContext();
		if (selectedSimContext != null && selectedSimContext.getGeometry().getDimension() == 0) {
			//add menu items to menu
			ivjJMenuAppCopyAs.add(menuItemNonSpatialCopyDeterministic);
			ivjJMenuAppCopyAs.add(menuItemAppNonSpatialCopyStochastic);
		} else {
			ivjJMenuAppCopyAs.add(menuAppSpatialCopyAsNonSpatial);
			ivjJMenuAppCopyAs.add(menuAppSpatialCopyAsSpatial);
		}
		return ivjJMenuAppCopyAs;
	}

	private javax.swing.JMenuItem getJMenuItemAppCopy() {
		if (ivjJMenuItemAppCopy == null) {
			try {
				ivjJMenuItemAppCopy = new javax.swing.JMenuItem();
				ivjJMenuItemAppCopy.setName("JMenuItemCopy");
				ivjJMenuItemAppCopy.setMnemonic('c');
				ivjJMenuItemAppCopy.setText("Copy");
				ivjJMenuItemAppCopy.setActionCommand(GuiConstants.ACTIONCMD_COPY_APPLICATION);
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJMenuItemAppCopy;
	}

	/**
	 * Return the JMenuItemDelete property value.
	 * @return javax.swing.JMenuItem
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JMenuItem getJMenuItemAppDelete() {
		if (ivjJMenuItemAppDelete == null) {
			try {
				ivjJMenuItemAppDelete = new javax.swing.JMenuItem();
				ivjJMenuItemAppDelete.setName("JMenuItemDelete");
				ivjJMenuItemAppDelete.setMnemonic('d');
				ivjJMenuItemAppDelete.setText("Delete");
				ivjJMenuItemAppDelete.setActionCommand(GuiConstants.ACTIONCMD_DELETE_APPLICATION);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJMenuItemAppDelete;
	}

	private void handleException(java.lang.Throwable exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}
	
	public void applicationMenuItem_ActionPerformed(java.awt.event.ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_NON_STOCHASTIC_APPLICATION)) {
			newApplication(e);
		}  else if (actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_STOCHASTIC_APPLICATION)) {
			newApplication(e);
		} else if (actionCommand.equals(GuiConstants.ACTIONCMD_DELETE_APPLICATION)) {
			deleteApplication();
		} else if (actionCommand.equals(GuiConstants.ACTIONCMD_COPY_APPLICATION)) {
			copyApplication();
		} else if (actionCommand.equals(GuiConstants.ACTIONCMD_NON_SPATIAL_COPY_TO_STOCHASTIC_APPLICATION)) {
			copyApplication(false, true);
		} else if (actionCommand.equals(GuiConstants.ACTIONCMD_NON_SPATIAL_COPY_TO_DETERMINISTIC_APPLICATION)) {
			copyApplication(false, false);
		} else if (actionCommand.equals(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_DETERMINISTIC_APPLICATION)) {
			copyApplication(false, false);
		} else if (actionCommand.equals(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_STOCHASTIC_APPLICATION)) {
			copyApplication(false, true);
		} else if (actionCommand.equals(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_DETERMINISTIC_APPLICATION)) {
			copyApplication(true, false);
		} else if (actionCommand.equals(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_STOCHASTIC_APPLICATION)) {
			copyApplication(true, true);
		} else if (actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_NEW_APPLICATION)) {
			newApplication(e);
		} 
	}
	
	private void copyApplication() {
		SimulationContext simulationContext = getSelectedSimulationContext();
		if (simulationContext == null) {
			PopupGenerator.showErrorDialog(this, "Please select an application.");
			return;
		}
		copyApplication(simulationContext.getGeometry().getDimension() > 0, simulationContext.isStoch());
	}
	
	private SimulationContext getSelectedSimulationContext() {
		int[] rows = table.getSelectedRows();
		if (rows != null && rows.length == 1 && rows[0] < tableModel.getDataSize()) {					
			return tableModel.getValueAt(rows[0]);
		}
		return null;
	}

	private void copyApplication(final boolean bSpatial, final boolean bStochastic) {
		final SimulationContext simulationContext = getSelectedSimulationContext();
		if (simulationContext == null) {
			PopupGenerator.showErrorDialog(this, "Please select an application.");
			return;
		}

		try {			
			if (bStochastic) {
				//check validity if copy to stochastic application
				String message = bioModel.getModel().isValidForStochApp();
				if(!message.equals(""))
				{
					throw new Exception(message);
				}
			}
			
			//get valid application name
			String newApplicationName = null;
			newApplicationName = "Copy of " + simulationContext.getName();
			int count = 0;
			while (true) {
				if (bioModel.getSimulationContext(newApplicationName) == null) {
					break;
				}
				count ++;
				newApplicationName += " " + count;
			}
			
			final String newName = newApplicationName;
			AsynchClientTask task1 = new AsynchClientTask("copying", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					SimulationContext newSimulationContext = copySimulationContext(simulationContext, newName, bSpatial, bStochastic);
					hashTable.put("newSimulationContext", newSimulationContext);
				}
			};
			AsynchClientTask task2 = new AsynchClientTask("showing", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
				
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
					bioModel.addSimulationContext(newSimulationContext);
					bioModelWindowManager.showApplicationFrame(newSimulationContext);
					select(newSimulationContext);
				}
			};
			ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2} );
		} catch (Throwable exc) {
			exc.printStackTrace(System.out);
			PopupGenerator.showErrorDialog(this, "Failed to Copy Application!\n"+exc.getMessage(), exc);
		}
	}

	private SimulationContext copySimulationContext(SimulationContext srcSimContext, String newSimulationContextName, boolean bSpatial, boolean bStoch) throws java.beans.PropertyVetoException, ExpressionException, MappingException, GeometryException, ImageException {
		Geometry newClonedGeometry = new Geometry(srcSimContext.getGeometry());
		newClonedGeometry.precomputeAll();
		//if stoch copy to ode, we need to check is stoch is using particles. If yes, should convert particles to concentraton.
		//the other 3 cases are fine. ode->ode, ode->stoch, stoch-> stoch 
		SimulationContext destSimContext = new SimulationContext(srcSimContext,newClonedGeometry, bStoch);
		if(srcSimContext.isStoch() && !srcSimContext.isUsingConcentration() && !bStoch)
		{
			try {
				destSimContext.convertSpeciesIniCondition(true);
			} catch (MappingException e) {
				e.printStackTrace();
				throw new java.beans.PropertyVetoException(e.getMessage(), null);
			}
		}
		if (srcSimContext.getGeometry().getDimension() > 0 && !bSpatial) { // copy the size over
			destSimContext.setGeometry(new Geometry("nonspatial", 0));
			StructureMapping srcStructureMappings[] = srcSimContext.getGeometryContext().getStructureMappings();
			StructureMapping destStructureMappings[] = destSimContext.getGeometryContext().getStructureMappings();
			for (StructureMapping destStructureMapping : destStructureMappings) {
				for (StructureMapping srcStructureMapping : srcStructureMappings) {
					if (destStructureMapping.getStructure() == srcStructureMapping.getStructure()) {
						if (srcStructureMapping.getUnitSizeParameter() != null) {
							Expression sizeRatio = srcStructureMapping.getUnitSizeParameter().getExpression();
							GeometryClass srcGeometryClass = srcStructureMapping.getGeometryClass();
							GeometricRegion[] srcGeometricRegions = srcSimContext.getGeometry().getGeometrySurfaceDescription().getGeometricRegions(srcGeometryClass);
							if (srcGeometricRegions != null) {
								double size = 0;
								for (GeometricRegion srcGeometricRegion : srcGeometricRegions) {
									size += srcGeometricRegion.getSize();
								}
								destStructureMapping.getSizeParameter().setExpression(Expression.mult(sizeRatio, new Expression(size)));
							}
						}
						break;
					}
				}
			}
		}
		destSimContext.setName(newSimulationContextName);	
		return destSimContext;
	}
	
	private void newApplication(java.awt.event.ActionEvent event) {
		boolean isStoch = false;
		if (event.getActionCommand().equals(GuiConstants.ACTIONCMD_CREATE_STOCHASTIC_APPLICATION))
		{
			isStoch = true;
			String message = bioModel.getModel().isValidForStochApp();
			if(!message.equals(""))
			{
				PopupGenerator.showErrorDialog(this, "Error creating stochastic application:\n" + message);
				return;
			}
		}
		
		try {
			String newApplicationName = bioModel.getFreeSimulationContextName();
			if (newApplicationName != null) {			
				final String finalNewAppName = newApplicationName; 
				final boolean finalIsStoch = isStoch;
				AsynchClientTask task0 = new AsynchClientTask("create application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						SimulationContext newSimulationContext = bioModel.addNewSimulationContext(finalNewAppName, finalIsStoch);
						hashTable.put("newSimulationContext", newSimulationContext);
					}
				};
				AsynchClientTask task1 = new AsynchClientTask("process geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
						newSimulationContext.getGeometry().precomputeAll();
					}
				};
				AsynchClientTask task2 = new AsynchClientTask("show application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
						bioModelWindowManager.showApplicationFrame(newSimulationContext);
						select(newSimulationContext);
					}
				};
				ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task0, task1, task2});				
			}
		} catch (Throwable exc) {
			exc.printStackTrace(System.out);
			PopupGenerator.showErrorDialog(this, "Failed to create new Application!\n"+exc.getMessage(), exc);
		}
	}

	private void deleteApplication() {
		SimulationContext simulationContext = getSelectedSimulationContext();
		if (simulationContext == null) {
			PopupGenerator.showErrorDialog(this, "Please select an application.");
			return;
		}
		try {
			//
			// BioModel enforces that there be no orphaned Simulations in BioModel.vetoableChange(...)
			// Check for no Simulations in SimualtionContext that is to be removed
			// otherwise a nonsense error message will be generated by BioModel
			//
			boolean bHasSims = false;
			Simulation[] simulations = simulationContext.getSimulations();
			if(simulations != null && simulations.length != 0){
				bHasSims = true;
			}
	
			if (bHasSims) {
				String confirm = PopupGenerator.showWarningDialog(this, bioModelWindowManager.getUserPreferences(), UserMessage.warn_DeleteSelectedAppWithSims, simulationContext.getName());
				if (!confirm.equals(UserMessage.OPTION_CANCEL)) {
					for (Simulation simulation : simulations) {
						bioModel.removeSimulation(simulation);
					}
					bioModel.removeSimulationContext(simulationContext);
				}
			} else {
				String confirm = PopupGenerator.showWarningDialog(this, bioModelWindowManager.getUserPreferences(), UserMessage.warn_DeleteSelectedApp, simulationContext.getName());		
				if (!confirm.equals(UserMessage.OPTION_CANCEL)) {
					bioModel.removeSimulationContext(simulationContext);
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace(System.out);
			PopupGenerator.showErrorDialog(this, "Failed to Delete!\n"+exc.getMessage(), exc);
		}
	}
	
	public void setBioModelWindowManager(BioModelWindowManager bioModelWindowManager) {
		this.bioModelWindowManager = bioModelWindowManager;
	}
}
