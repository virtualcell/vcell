/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.data;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.vcell.solver.nfsim.NFSimMolecularConfigurations;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.RenderDataViewerDoubleWithTooltip;
import org.vcell.util.gui.SpecialtyTableRenderer;

import cbit.plot.gui.PlotPane;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.ClientTaskDispatcher.BlockingTimer;
import cbit.vcell.export.gui.ExportMonitorPanel;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.DataManager;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.gui.ODESolverPlotSpecificationPanel;
import cbit.vcell.solver.ode.gui.OutputSpeciesResultsPanel;
/**
 * Insert the type's description here.
 * Creation date: (6/11/2004 6:01:46 AM)
 * @author: Ion Moraru
 */
public class ODEDataViewer extends DataViewer {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private ODESolverPlotSpecificationPanel ivjODESolverPlotSpecificationPanel1 = null;
	private PlotPane ivjPlotPane1 = null;
	private JTabbedPane ivjJTabbedPane = null;
	private ODESolverResultSet fieldOdeSolverResultSet = null;
	private NFSimMolecularConfigurations nFSimMolecularConfigurations = null;
	private javax.swing.JPanel ivjViewData = null;
	private OutputSpeciesResultsPanel outputSpeciesResultsPanel = null;
	private VCDataIdentifier fieldVcDataIdentifier = null;

	private static final String OUTPUT_SPECIES_TABNAME = "Output Species";

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ODEDataViewer.this && (evt.getPropertyName().equals("odeSolverResultSet"))
				||
				(evt.getSource() == ODEDataViewer.this && evt.getPropertyName().equals(DataViewer.PROP_SIM_MODEL_INFO)))
			{
				updateMetadata();
				if(getOdeSolverResultSet()!=null)
				{
					iniHistogramDisplay();
				}
			}
			if (evt.getSource() == ODEDataViewer.this.getODESolverPlotSpecificationPanel1() && (evt.getPropertyName().equals("Plot2D"))) 
				connEtoM2(evt);
		};
	};
	private Simulation fieldSimulation =null;

public ODEDataViewer() {
	super();
	initialize();
}

/**
 * connEtoM2:  (ODESolverPlotSpecificationPanel1.singleXPlot2D --> PlotPane1.plot2D)
 * @param arg1 java.beans.PropertyChangeEvent
 */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		//amended March 29,2007. to get singleXPlot2D or plot2D from SepcificationPanel to display in plotPanel
		//if the data are time series, we use singleXPlot2D. if the data are histograms, we use plot2D.
		if (arg1.getPropertyName().equals("Plot2D")) 
			getPlotPane1().setPlot2D(getODESolverPlotSpecificationPanel1().getPlot2D());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (ODEDataViewer.odeSolverResultSet <--> ODESolverPlotSpecificationPanel1.odeSolverResultSet)
 */
BlockingTimer odeDataViewersetupTimer = null;
private void updateMetadata() {
	/* Set the target from the source */
	if(getOdeSolverResultSet() == null){
		return;
	}
	//check if clienttaskdispatcher is busy, if so schedule this method to run later (workaround spurious threading problem)
	if((odeDataViewersetupTimer = ClientTaskDispatcher.getBlockingTimer(ODEDataViewer.this, null, null, odeDataViewersetupTimer, new ActionListener() {@Override public void actionPerformed(ActionEvent e2){updateMetadata();}},"ODEDataViewer Setup...")) != null){
		return;
	}

	try {
		AsynchClientTask filterCategoriesTask = new AsynchClientTask("Calculating Filter...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				if(ODEDataViewer.this.getSimulationModelInfo() != null){
					SimulationModelInfo simulationModelInfo = ODEDataViewer.this.getSimulationModelInfo();
					simulationModelInfo.getDataSymbolMetadataResolver().populateDataSymbolMetadata();
				}
			}
		};
		AsynchClientTask firePropertyChangeTask = new AsynchClientTask("Fire Property Change...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				SimulationModelInfo simulationModelInfo = ODEDataViewer.this.getSimulationModelInfo();
				ODEDataInterfaceImpl oDEDataInterfaceImpl = new ODEDataInterfaceImpl(getOdeSolverResultSet(),simulationModelInfo);
				getODESolverPlotSpecificationPanel1().setMyDataInterface(oDEDataInterfaceImpl);
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						while(ClientTaskDispatcher.isBusy()){
//							try{Thread.sleep(200);}catch(Exception e){e.printStackTrace();}
//						}
//						SwingUtilities.invokeLater(new Runnable() {
//							@Override
//							public void run() {
//								((Window)BeanUtils.findTypeParentOfComponent(ODEDataViewer.this, Window.class)).toFront();						
//							}
//						});
//					}
//				}).start();
			}
		};
		ClientTaskDispatcher.dispatch(ODEDataViewer.this, new Hashtable<String, Object>(),
				new AsynchClientTask[] {filterCategoriesTask,firePropertyChangeTask},
				false, false, false, null, true);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Method generated to support the promotion of the exportMonitorPanel attribute.
 * @return cbit.vcell.export.ExportMonitorPanel
 */
public ExportMonitorPanel getExportMonitorPanel() {
	return null;
}


/**
 * Return the ODESolverPlotSpecificationPanel1 property value.
 * @return cbit.vcell.solver.ode.gui.ODESolverPlotSpecificationPanel
 */
private ODESolverPlotSpecificationPanel getODESolverPlotSpecificationPanel1() {
	if (ivjODESolverPlotSpecificationPanel1 == null) {
		try {
			ivjODESolverPlotSpecificationPanel1 = new ODESolverPlotSpecificationPanel();
			ivjODESolverPlotSpecificationPanel1.setName("ODESolverPlotSpecificationPanel1");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjODESolverPlotSpecificationPanel1;
}


/**
 * Gets the odeSolverResultSet property (cbit.vcell.solver.ode.ODESolverResultSet) value.
 * @return The odeSolverResultSet property value.
 * @see #setOdeSolverResultSet
 */
public ODESolverResultSet getOdeSolverResultSet() {
	return fieldOdeSolverResultSet;
}
public NFSimMolecularConfigurations getNFSimMolecularConfigurations() {
	return nFSimMolecularConfigurations;
}


/**
 * Return the PlotPane1 property value.
 * @return cbit.plot.PlotPane
 */
private PlotPane getPlotPane1() {
	if (ivjPlotPane1 == null) {
		try {
			ivjPlotPane1 = new PlotPane();
			ivjPlotPane1.setName("PlotPane1");
			SpecialtyTableRenderer str = new RenderDataViewerDoubleWithTooltip();
			ivjPlotPane1.setSpecialityRenderer(str);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPlotPane1;
}


/**
 * Gets the simulation property (cbit.vcell.solver.Simulation) value.
 * @return The simulation property value.
 * @see #setSimulation
 */
public Simulation getSimulation() {
	return fieldSimulation;
}


/**
 * Gets the vcDataIdentifier property (cbit.vcell.server.VCDataIdentifier) value.
 * @return The vcDataIdentifier property value.
 * @see #setVcDataIdentifier
 */
public VCDataIdentifier getVcDataIdentifier() {
	return fieldVcDataIdentifier;
}

private final ChangeListener mainTabChangeListener = 
new ChangeListener(){
	public void stateChanged(ChangeEvent e) {
		if(ivjJTabbedPane.getSelectedIndex() == ivjJTabbedPane.indexOfTab(OUTPUT_SPECIES_TABNAME)){
			// TODO: here
		}else {

		}
	}
};

private javax.swing.JTabbedPane getJTabbedPane() {
	if (ivjJTabbedPane == null) {
		try {
			ivjJTabbedPane = new javax.swing.JTabbedPane();
			ivjJTabbedPane.setName("JTabbedPane1");
			ivjJTabbedPane.insertTab("View Data", null, getViewData(), null, 0);
			outputSpeciesResultsPanel = new OutputSpeciesResultsPanel(this);
			outputSpeciesResultsPanel.addPropertyChangeListener(ivjEventHandler);
			ivjJTabbedPane.addTab(OUTPUT_SPECIES_TABNAME, outputSpeciesResultsPanel);
			ivjJTabbedPane.addChangeListener(mainTabChangeListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane;
}
/**
 * Return the ViewData property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getViewData() {
	if (ivjViewData == null) {
		try {
			ivjViewData = new javax.swing.JPanel();
			ivjViewData.setName("ViewData");
			ivjViewData.setLayout(new java.awt.BorderLayout());
			getViewData().add(getODESolverPlotSpecificationPanel1(), "West");
			getViewData().add(getPlotPane1(), "Center");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjViewData;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
	this.addPropertyChangeListener(ivjEventHandler);
	getODESolverPlotSpecificationPanel1().addPropertyChangeListener(ivjEventHandler);
	updateMetadata();
}

/**
 * Initialize the class.
 */
//private void initialize() {
//	try {
//		setName("ODEDataViewer");
//		setLayout(new java.awt.BorderLayout());
//		setSize(720, 548);
//		add(getViewData(), "Center");
//		initConnections();
//	} catch (java.lang.Throwable ivjExc) {
//		handleException(ivjExc);
//	}
//}
private void initialize() {
	try {
		setName("ODEDataViewer");
		setLayout(new java.awt.BorderLayout());
		setSize(720, 548);
		add(getJTabbedPane(), "Center");
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ODEDataViewer aODEDataViewer;
		aODEDataViewer = new ODEDataViewer();
		frame.setContentPane(aODEDataViewer);
		frame.setSize(aODEDataViewer.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}

/**
 * Sets the odeSolverResultSet property (cbit.vcell.solver.ode.ODESolverResultSet) value.
 * @param odeSolverResultSet The new value for the property.
 * @see #getOdeSolverResultSet
 */
public void setOdeSolverResultSet(ODESolverResultSet odeSolverResultSet) {
	ODESolverResultSet oldValue = fieldOdeSolverResultSet;
	fieldOdeSolverResultSet = odeSolverResultSet;
	firePropertyChange("odeSolverResultSet", oldValue, odeSolverResultSet);
}
public void setNFSimMolecularConfigurations(NFSimMolecularConfigurations nFSimMolecularConfigurations) {
	NFSimMolecularConfigurations oldValue = nFSimMolecularConfigurations;
	this.nFSimMolecularConfigurations = nFSimMolecularConfigurations;
	firePropertyChange("nfSimMolecularConfigurations", oldValue, nFSimMolecularConfigurations);
}

/**
 * Sets the simulation property (cbit.vcell.solver.Simulation) value.
 * @param simulation The new value for the property.
 * @see #getSimulation
 */
public void setSimulation(Simulation simulation) {
	Simulation oldValue = fieldSimulation;
	fieldSimulation = simulation;
	firePropertyChange("simulation", oldValue, simulation);

	getODESolverPlotSpecificationPanel1().setSymbolTable(
		(simulation != null?simulation.getMathDescription():null));
}


/**
 * Sets the vcDataIdentifier property (cbit.vcell.server.VCDataIdentifier) value.
 * @param vcDataIdentifier The new value for the property.
 * @see #getVcDataIdentifier
 */
public void setVcDataIdentifier(VCDataIdentifier vcDataIdentifier) {
	VCDataIdentifier oldValue = fieldVcDataIdentifier;
	fieldVcDataIdentifier = vcDataIdentifier;
	setOdeDataContext();
	firePropertyChange("vcDataIdentifier", oldValue, vcDataIdentifier);
	outputSpeciesResultsPanel.refreshData();
}

public void setOdeDataContext() {
	if(getSimulation() != null && getNFSimMolecularConfigurations() != null) {
		getJTabbedPane().setEnabledAt(getJTabbedPane().indexOfTab(OUTPUT_SPECIES_TABNAME), true);
	} else {
		getJTabbedPane().setEnabledAt(getJTabbedPane().indexOfTab(OUTPUT_SPECIES_TABNAME), false);
	}
}

public void iniHistogramDisplay()
{
	// protecting for null simulation - needed esp for mergedData (where there is no simulation per se).
	if (getSimulation() != null) {
		getPlotPane1().setStepViewVisible(getSimulation().getSolverTaskDescription().getSolverDescription().isNonSpatialStochasticSolver(), getOdeSolverResultSet().isMultiTrialData());	
	} else {
		// if simulation is null (usually is for merged data), just disable the step view in the plot panel
		getPlotPane1().setStepViewVisible(false, getOdeSolverResultSet().isMultiTrialData());
	}
	
	if(getOdeSolverResultSet().isMultiTrialData())
	{
		getODESolverPlotSpecificationPanel1().getXAxisComboBox_frm().setEnabled(false);
	}
	else //single trial
	{
		getODESolverPlotSpecificationPanel1().getXAxisComboBox_frm().setEnabled(true);
	}
}

@Override
public void showTimePlotMultipleScans(DataManager dataManager) {
	String[] selectedVariableNames = getODESolverPlotSpecificationPanel1().getSelectedVariableNames();
	if (selectedVariableNames.length == 0) {
		DialogUtils.showErrorDialog(this, "Please choose one or more variables!");
		return;
	}
	ODETimePlotMultipleScansPanel panel = new ODETimePlotMultipleScansPanel(selectedVariableNames, getSimulation(), dataManager);
	ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(this);
	//ChildWindow childWindow = childWindowManager.getChildWindowFromContentPane(panel);
	
	ChildWindow childWindow = childWindowManager.addChildWindow(panel, panel, "");
	String titleStr = childWindow.getParent().getTitle();
	titleStr = titleStr.substring(0, titleStr.indexOf("("));
	childWindow.setTitle("Parameter scan results time plot for " + titleStr);
	
	childWindow.setIsCenteredOnParent();
	childWindow.setSize(600,600);
	childWindow.show();
}


}
