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

import java.awt.Dimension;
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
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.ClientTaskManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.constants.ApplicationActionCommand;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.solver.Simulation;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.xml.Xmlproducer;
import cbit.xml.merge.XmlTreeDiff;
import cbit.xml.merge.XmlTreeDiff.DiffConfiguration;
import cbit.xml.merge.gui.TMLPanel;

@SuppressWarnings("serial")
public class BioModelEditorApplicationsPanel extends BioModelEditorRightSidePanel<SimulationContext> {
	private JButton compareButton = null;
	private JButton moreActionsButton = null;
	// application popup menu items
	private JPopupMenu appsPopupMenu = null;
	private JPopupMenu ivjMoreActionsPopupMenu = null;
	
	private JMenu menuAppCopyAs = null;
	private JMenuItem menuItemAppNonSpatialCopyStochastic = null;
	private JMenuItem menuItemNonSpatialCopyDeterministic = null;
	private JMenuItem menuItemAppNonSpatialCopyRulebased = null;
	
	private JMenu menuAppSpatialCopyAsNonSpatial = null;
	private JMenuItem menuItemAppSpatialCopyAsNonSpatialStochastic = null;
	private JMenuItem menuItemAppSpatialCopyAsNonSpatialDeterministic = null;
	private JMenu menuAppSpatialCopyAsSpatial = null;
	private JMenuItem menuItemAppSpatialCopyAsSpatialStochastic = null;
	private JMenuItem menuItemAppSpatialCopyAsSpatialDeterministic = null;
	
	private JMenuItem appNewStochApp = null;
	private JMenuItem appNewDeterministicApp = null;
	private JMenuItem appNewRulebasedApp = null;
	private JMenuItem ivjJMenuItemAppCopy = null;	
	
	private EventHandler eventHandler = new EventHandler();
	
	private class EventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == moreActionsButton) {
				moreActionsButtonPressed();
			} else if (e.getSource() == compareButton) {
				compareButtonPressed();
			} else if (e.getSource() == appNewStochApp
					|| e.getSource() == appNewDeterministicApp
					|| e.getSource() == appNewRulebasedApp
					|| e.getSource() == getJMenuItemAppCopy()
					|| e.getSource() == menuItemAppNonSpatialCopyStochastic
					|| e.getSource() == menuItemNonSpatialCopyDeterministic
					|| e.getSource() == menuItemAppNonSpatialCopyRulebased
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
		addNewButton.setName("New Application");
		addNewButton.setIcon(new DownArrowIcon());
		addNewButton.setHorizontalTextPosition(SwingConstants.LEFT);
		moreActionsButton = new JButton("More Copy Actions");
		moreActionsButton.setIcon(new DownArrowIcon());
		moreActionsButton.setHorizontalTextPosition(SwingConstants.LEFT);		
		compareButton = new JButton("Compare...");
		
		setLayout(new GridBagLayout());
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 7;
		gbc.fill = GridBagConstraints.BOTH;
		add(table.getEnclosingScrollPane(), gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(addNewButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(deleteButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.insets = new Insets(4,4,4,20);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(moreActionsButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.insets = new Insets(4,4,4,20);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(compareButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		add(new JLabel("Search "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		add(textFieldSearch, gbc);
						
		compareButton.setEnabled(false);
		compareButton.addActionListener(eventHandler);		
		moreActionsButton.setEnabled(false);
		moreActionsButton.addActionListener(eventHandler);		
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
		getNewAppPopupMenu().show(addNewButton, 0, addNewButton.getHeight());
	}

	private void moreActionsButtonPressed() {
		int[] rows = table.getSelectedRows();
		if (rows != null && rows.length == 1) {					
			getMoreActionsPopupMenu().show(moreActionsButton, 0, addNewButton.getHeight());
		}
	}
	
	private void compareButtonPressed() {
		int[] rows = table.getSelectedRows();
		if (rows == null || rows.length != 2) {
			return;
		}
		try {
			SimulationContext simContext1 = tableModel.getValueAt(rows[0]);
			SimulationContext simContext2 = tableModel.getValueAt(rows[1]);
			BioModel bioModel = simContext1.getBioModel();
			MathMappingCallback callback = new MathMappingCallback() {
				@Override
				public void setProgressFraction(float fractionDone) {
					Thread.dumpStack();
					System.out.println("---> stdout mathMapping: progress = "+(fractionDone*100.0)+"% done");
				}
				
				@Override
				public void setMessage(String message) {
					Thread.dumpStack();
					System.out.println("---> stdout mathMapping: message = "+message);
				}
				
				@Override
				public boolean isInterrupted() {
					return false;
				}
			};
			simContext1.refreshMathDescription(callback, NetworkGenerationRequirements.ComputeFullStandardTimeout);
			simContext2.refreshMathDescription(callback, NetworkGenerationRequirements.ComputeFullStandardTimeout);

			Xmlproducer xmlProducer = new Xmlproducer(false);
			String simContext1XML = XmlUtil.xmlToString(xmlProducer.getXML(simContext1,bioModel));
			String simContext2XML = XmlUtil.xmlToString(xmlProducer.getXML(simContext2,bioModel));

			DiffConfiguration comparisonSetting = DiffConfiguration.COMPARE_DOCS_OTHER;
			XmlTreeDiff diffTree = XmlHelper.compareMerge(simContext1XML, simContext2XML, comparisonSetting , true);
			String baselineDesc = "application "+simContext1.getName();
			String modifiedDesc = "application "+simContext2.getName();
			TMLPanel comparePanel = new TMLPanel();
			comparePanel.setXmlTreeDiff(diffTree);
			comparePanel.setBaselineVersionDescription(baselineDesc);
			comparePanel.setModifiedVersionDescription(modifiedDesc);

			ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(this);
			String title = "comparing application "+simContext1.getName()+" and "+simContext2.getName();
			ChildWindow childWindow = childWindowManager.addChildWindow(comparePanel, diffTree, title, true);
			childWindow.setSize(new Dimension(600,600));
			childWindow.show();
		} catch (XmlParseException e) {
			e.printStackTrace();
			DialogUtils.showErrorDialog(this, "failed to compare applications: \n\n"+e.getMessage());
		}
		
	}
	
	protected void deleteButtonPressed() {
		int[] rows = table.getSelectedRows();
		if (rows == null || rows.length == 0) {
			return;
		}
		String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Deleting application(s)", "Are you sure you want to delete selected application(s)?");
		if (confirm.equals(UserMessage.OPTION_CANCEL)) {
			return;
		}
		ArrayList<SimulationContext> deleteList = new ArrayList<SimulationContext>();
		for (int r : rows) {
			SimulationContext simContext = tableModel.getValueAt(r);
			if (simContext != null) {
				deleteList.add(simContext);
			}
		}
		try {
			for (SimulationContext sc : deleteList) {
				deleteApplication(sc);
			}
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
		compareButton.setEnabled(rows != null && rows.length == 2);
		moreActionsButton.setEnabled(rows != null && rows.length == 1);
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
		if (appsPopupMenu == null) {
			try {
				appsPopupMenu = new javax.swing.JPopupMenu();
				appsPopupMenu.setName("AppPopupMenu");
				
				//Menu items in Menu-New
				appNewStochApp=new JMenuItem(GuiConstants.MENU_TEXT_STOCHASTIC_APPLICATION);
				appNewStochApp.setActionCommand(GuiConstants.ACTIONCMD_CREATE_STOCHASTIC_APPLICATION);
				appNewStochApp.addActionListener(eventHandler);
				
				appNewDeterministicApp = new javax.swing.JMenuItem(GuiConstants.MENU_TEXT_DETERMINISTIC_APPLICATION);
				appNewDeterministicApp.setActionCommand(GuiConstants.ACTIONCMD_CREATE_DETERMINISTIC_APPLICATION);
				appNewDeterministicApp.addActionListener(eventHandler);
				
				appNewRulebasedApp = new javax.swing.JMenuItem(GuiConstants.MENU_TEXT_RULEBASED_APPLICATION);
				appNewRulebasedApp.setActionCommand(GuiConstants.ACTIONCMD_CREATE_RULEBASED_APPLICATION);
				appNewRulebasedApp.addActionListener(eventHandler);

				//add menu items to menu
				appsPopupMenu.add(appNewDeterministicApp);
				appsPopupMenu.add(appNewStochApp);
				appsPopupMenu.add(appNewRulebasedApp);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return appsPopupMenu;
	}

	/**
	 * Return the JMenuItemCopy property value.
	 * @return javax.swing.JMenuItem
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JMenu getJMenuAppCopyAs() {
		if (menuAppCopyAs == null) {
			try {
				menuAppCopyAs = new javax.swing.JMenu(GuiConstants.MENU_TEXT_APP_COPYAS);

				menuItemAppNonSpatialCopyStochastic=new JMenuItem(GuiConstants.MENU_TEXT_STOCHASTIC_APPLICATION);
				menuItemAppNonSpatialCopyStochastic.setActionCommand(GuiConstants.ACTIONCMD_NON_SPATIAL_COPY_TO_STOCHASTIC_APPLICATION);
				menuItemAppNonSpatialCopyStochastic.addActionListener(eventHandler);
				
				menuItemNonSpatialCopyDeterministic = new javax.swing.JMenuItem(GuiConstants.MENU_TEXT_DETERMINISTIC_APPLICATION);
				menuItemNonSpatialCopyDeterministic.setActionCommand(GuiConstants.ACTIONCMD_NON_SPATIAL_COPY_TO_DETERMINISTIC_APPLICATION);
				menuItemNonSpatialCopyDeterministic.addActionListener(eventHandler);				
				
				menuItemAppNonSpatialCopyRulebased = new javax.swing.JMenuItem(GuiConstants.MENU_TEXT_RULEBASED_APPLICATION);
				menuItemAppNonSpatialCopyRulebased.setActionCommand(GuiConstants.ACTIONCMD_NON_SPATIAL_COPY_TO_RULEBASED_APPLICATION);
				menuItemAppNonSpatialCopyRulebased.addActionListener(eventHandler);				

				menuAppSpatialCopyAsNonSpatial = new JMenu(GuiConstants.MENU_TEXT_NON_SPATIAL_APPLICATION);
				menuItemAppSpatialCopyAsNonSpatialDeterministic = new JMenuItem(GuiConstants.MENU_TEXT_DETERMINISTIC_APPLICATION);
				menuItemAppSpatialCopyAsNonSpatialDeterministic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_DETERMINISTIC_APPLICATION);
				menuItemAppSpatialCopyAsNonSpatialDeterministic.addActionListener(eventHandler);
				menuItemAppSpatialCopyAsNonSpatialStochastic = new JMenuItem(GuiConstants.MENU_TEXT_STOCHASTIC_APPLICATION);
				menuItemAppSpatialCopyAsNonSpatialStochastic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_STOCHASTIC_APPLICATION);
				menuItemAppSpatialCopyAsNonSpatialStochastic.addActionListener(eventHandler);
				menuAppSpatialCopyAsNonSpatial.add(menuItemAppSpatialCopyAsNonSpatialDeterministic);
				menuAppSpatialCopyAsNonSpatial.add(menuItemAppSpatialCopyAsNonSpatialStochastic);
				
				menuAppSpatialCopyAsSpatial = new JMenu(GuiConstants.MENU_TEXT_SPATIAL_APPLICATION);
				menuItemAppSpatialCopyAsSpatialDeterministic = new JMenuItem(GuiConstants.MENU_TEXT_DETERMINISTIC_APPLICATION);
				menuItemAppSpatialCopyAsSpatialDeterministic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_DETERMINISTIC_APPLICATION);
				menuItemAppSpatialCopyAsSpatialDeterministic.addActionListener(eventHandler);
				menuItemAppSpatialCopyAsSpatialStochastic = new JMenuItem(GuiConstants.MENU_TEXT_STOCHASTIC_APPLICATION);
				menuItemAppSpatialCopyAsSpatialStochastic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_STOCHASTIC_APPLICATION);
				menuItemAppSpatialCopyAsSpatialStochastic.addActionListener(eventHandler);
				menuAppSpatialCopyAsSpatial.add(menuItemAppSpatialCopyAsSpatialDeterministic);
				menuAppSpatialCopyAsSpatial.add(menuItemAppSpatialCopyAsSpatialStochastic);
	
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		menuAppCopyAs.removeAll();
		SimulationContext selectedSimContext = getSelectedSimulationContext();
		if (selectedSimContext != null) {
			if (selectedSimContext.getGeometry().getDimension() == 0) {
				menuAppCopyAs.add(menuItemNonSpatialCopyDeterministic);
				menuAppCopyAs.add(menuItemAppNonSpatialCopyStochastic);
				menuAppCopyAs.add(menuItemAppNonSpatialCopyRulebased);
			} else {
				menuAppCopyAs.add(menuAppSpatialCopyAsNonSpatial);
				menuAppCopyAs.add(menuAppSpatialCopyAsSpatial);
			}
		}
		return menuAppCopyAs;
	}

	private javax.swing.JMenuItem getJMenuItemAppCopy() {
		if (ivjJMenuItemAppCopy == null) {
			try {
				ivjJMenuItemAppCopy = new javax.swing.JMenuItem(GuiConstants.MENU_TEXT_APP_COPY);
				ivjJMenuItemAppCopy.setName("JMenuItemCopy");
				ivjJMenuItemAppCopy.setMnemonic('c');
				ivjJMenuItemAppCopy.setActionCommand(GuiConstants.ACTIONCMD_COPY_APPLICATION);
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJMenuItemAppCopy;
	}

	private void handleException(java.lang.Throwable exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}
	
	public void applicationMenuItem_ActionPerformed(java.awt.event.ActionEvent e) {
		String actionCommand = e.getActionCommand();
		ApplicationActionCommand acc = ApplicationActionCommand.lookup(actionCommand);
		switch (acc.actionType()) {
		case CREATE:
			newApplication(e, acc.getAppType());
			break;
		case COPY_AS_IS:
			copyApplication();
			break;
		case COPY_CHANGE:
			boolean bothSpatial = acc.isSourceSpatial() && acc.isDestSpatial();
			copyApplication(bothSpatial,acc.getAppType());
			break;
		}
	}
	
	private void copyApplication() {
		SimulationContext simulationContext = getSelectedSimulationContext();
		if (simulationContext == null) {
			PopupGenerator.showErrorDialog(this, "Please select an application.");
			return;
		}
		copyApplication(simulationContext.getGeometry().getDimension() > 0, simulationContext.getApplicationType());
	}
	
	private SimulationContext getSelectedSimulationContext() {
		int[] rows = table.getSelectedRows();
		if (rows != null && rows.length == 1) {					
			return tableModel.getValueAt(rows[0]);
		}
		return null;
	}

	private void copyApplication(final boolean bSpatial, SimulationContext.Application appType) { 
		final SimulationContext simulationContext = getSelectedSimulationContext();
		if (simulationContext == null) {
			PopupGenerator.showErrorDialog(this, "Please select an application.");
			return;
		}
		if (appType == Application.NETWORK_STOCHASTIC) {
			//check validity if copy to stochastic application
			String message = bioModel.getModel().isValidForStochApp();
			if (!message.equals("")) {
				PopupGenerator.showErrorDialog(this, message);
				return;
			}
		}
		AsynchClientTask[] copyTasks = ClientTaskManager.copyApplication(this, bioModel, simulationContext, bSpatial, appType);
		AsynchClientTask[] allTasks = new AsynchClientTask[copyTasks.length + 1];
		System.arraycopy(copyTasks, 0, allTasks, 0, copyTasks.length);
		allTasks[allTasks.length - 1] = new AsynchClientTask("showing", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
				setSelectedObjects(new Object[]{newSimulationContext});
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), allTasks,  false);
	}
	private void newApplication(java.awt.event.ActionEvent event,SimulationContext.Application appType) {
		if (appType == Application.NETWORK_STOCHASTIC) {
			String message = bioModel.getModel().isValidForStochApp();
			if (!message.equals("")) {
				PopupGenerator.showErrorDialog(this, message);
				return;
			}
		}
		AsynchClientTask[] newApplicationTasks = ClientTaskManager.newApplication(this,bioModel,appType); 
		AsynchClientTask[] tasks = new AsynchClientTask[newApplicationTasks.length + 1];
		System.arraycopy(newApplicationTasks, 0, tasks, 0, newApplicationTasks.length);
		tasks[newApplicationTasks.length] = new AsynchClientTask("show application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
				setSelectedObjects(new Object[]{newSimulationContext});
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), tasks);
	}

	private void deleteApplication(SimulationContext simulationContext) throws PropertyVetoException {
		//
		// BioModel enforces that there be no orphaned Simulations in BioModel.vetoableChange(...)
		// Check for no Simulations in SimualtionContext that is to be removed
		// otherwise a nonsense error message will be generated by BioModel
		//
		Simulation[] simulations = simulationContext.getSimulations();
		if(simulations != null && simulations.length != 0){
			for (Simulation simulation : simulations) {
				bioModel.removeSimulation(simulation);
			}
		}
		bioModel.removeSimulationContext(simulationContext);
	}
	
	public void onSelectedObjectsChange(Object[] selectedObjects) {
		setTableSelections(selectedObjects, table, tableModel);
	}
}
