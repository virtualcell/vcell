package cbit.vcell.client.data;
import java.awt.event.*;
import java.awt.*;
/**
 * Insert the type's description here.
 * Creation date: (6/11/2004 6:01:46 AM)
 * @author: Ion Moraru
 */
public class ODEDataViewer extends DataViewer {
    protected transient ActionListener actionListener = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.solver.ode.gui.ODESolverPlotSpecificationPanel ivjODESolverPlotSpecificationPanel1 = null;
	private cbit.plot.PlotPane ivjPlotPane1 = null;
	private cbit.vcell.solver.ode.ODESolverResultSet fieldOdeSolverResultSet = null;
	private boolean ivjConnPtoP1Aligning = false;
	private javax.swing.JPanel ivjExportData = null;
	private cbit.vcell.export.ExportMonitorPanel ivjExportMonitorPanel1 = null;
	private javax.swing.JTabbedPane ivjJTabbedPane1 = null;
	private javax.swing.JPanel ivjViewData = null;
	private boolean ivjConnPtoP2Aligning = false;
	private NewODEExportPanel ivjNewODEExportPanel1 = null;
	private boolean ivjConnPtoP3Aligning = false;
	private cbit.vcell.server.VCDataIdentifier fieldVcDataIdentifier = null;
	private boolean ivjConnPtoP4Aligning = false;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ODEDataViewer.this && (evt.getPropertyName().equals("odeSolverResultSet"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == ODEDataViewer.this && (evt.getPropertyName().equals("odeSolverResultSet")))
			{
				if(getOdeSolverResultSet()!=null)
				{
					iniHistogramDisplay();
				}
			}
			if (evt.getSource() == ODEDataViewer.this.getODESolverPlotSpecificationPanel1() && (evt.getPropertyName().equals("odeSolverResultSet"))) 
				connPtoP1SetSource();
			if (evt.getSource() == ODEDataViewer.this && (evt.getPropertyName().equals("odeSolverResultSet"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == ODEDataViewer.this.getNewODEExportPanel1() && (evt.getPropertyName().equals("odeSolverResultSet"))) 
				connPtoP2SetSource();
			if (evt.getSource() == ODEDataViewer.this && (evt.getPropertyName().equals("dataViewerManager"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == ODEDataViewer.this.getNewODEExportPanel1() && (evt.getPropertyName().equals("dataViewerManager"))) 
				connPtoP3SetSource();
			if (evt.getSource() == ODEDataViewer.this && (evt.getPropertyName().equals("vcDataIdentifier"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == ODEDataViewer.this.getNewODEExportPanel1() && (evt.getPropertyName().equals("vcDataIdentifier"))) 
				connPtoP4SetSource();
			if (evt.getSource() == ODEDataViewer.this.getODESolverPlotSpecificationPanel1() && (evt.getPropertyName().equals("singleXPlot2D"))) 
				connEtoM2(evt);
			//add March 29, 2007. to display histogram,which is not singleXPlot2D.
			if (evt.getSource() == ODEDataViewer.this.getODESolverPlotSpecificationPanel1() && (evt.getPropertyName().equals("Plot2D"))) 
				connEtoM2(evt);
		};
	};
	private cbit.vcell.solver.Simulation fieldSimulation =null;

public ODEDataViewer() {
	super();
	initialize();
}

public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
}


/**
 * connEtoM2:  (ODESolverPlotSpecificationPanel1.singleXPlot2D --> PlotPane1.plot2D)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		//amended March 29,2007. to get singleXPlot2D or plot2D from SepcificationPanel to display in plotPanel
		//if the data are time series, we use singleXPlot2D. if the data are histograms, we use plot2D.
		if (arg1.getPropertyName().equals("singleXPlot2D")) 
			getPlotPane1().setPlot2D(getODESolverPlotSpecificationPanel1().getSingleXPlot2D());
		if (arg1.getPropertyName().equals("Plot2D")) 
			getPlotPane1().setPlot2D(getODESolverPlotSpecificationPanel1().getPlot2D());
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (ODEDataViewer.odeSolverResultSet <--> ODESolverPlotSpecificationPanel1.odeSolverResultSet)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			this.setOdeSolverResultSet(getODESolverPlotSpecificationPanel1().getOdeSolverResultSet());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (ODEDataViewer.odeSolverResultSet <--> ODESolverPlotSpecificationPanel1.odeSolverResultSet)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			getODESolverPlotSpecificationPanel1().setOdeSolverResultSet(this.getOdeSolverResultSet());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetSource:  (ODEDataViewer.odeSolverResultSet <--> NewODEExportPanel1.odeSolverResultSet)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			this.setOdeSolverResultSet(getNewODEExportPanel1().getOdeSolverResultSet());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (ODEDataViewer.odeSolverResultSet <--> NewODEExportPanel1.odeSolverResultSet)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			getNewODEExportPanel1().setOdeSolverResultSet(this.getOdeSolverResultSet());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (ODEDataViewer.documentWindowManager <--> NewODEExportPanel1.documentWindowManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			this.setDataViewerManager(getNewODEExportPanel1().getDataViewerManager());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP3SetTarget:  (ODEDataViewer.documentWindowManager <--> NewODEExportPanel1.documentWindowManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			getNewODEExportPanel1().setDataViewerManager(this.getDataViewerManager());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP4SetSource:  (ODEDataViewer.vcDataIdentifier <--> NewODEExportPanel1.vcDataIdentifier)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			this.setVcDataIdentifier(getNewODEExportPanel1().getVcDataIdentifier());
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetTarget:  (ODEDataViewer.vcDataIdentifier <--> NewODEExportPanel1.vcDataIdentifier)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			getNewODEExportPanel1().setVcDataIdentifier(this.getVcDataIdentifier());
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


protected void fireActionPerformed(ActionEvent e) {
	if (actionListener != null) {
		actionListener.actionPerformed(e);
	}         
}


/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getExportData() {
	if (ivjExportData == null) {
		try {
			ivjExportData = new javax.swing.JPanel();
			ivjExportData.setName("ExportData");
			ivjExportData.setLayout(new java.awt.BorderLayout());
			ivjExportData.setEnabled(false);
			getExportData().add(getExportMonitorPanel1(), "South");
			getExportData().add(getNewODEExportPanel1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExportData;
}

/**
 * Method generated to support the promotion of the exportMonitorPanel attribute.
 * @return cbit.vcell.export.ExportMonitorPanel
 */
public cbit.vcell.export.ExportMonitorPanel getExportMonitorPanel() {
	return getExportMonitorPanel1();
}


/**
 * Return the ExportMonitorPanel1 property value.
 * @return cbit.vcell.export.ExportMonitorPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.export.ExportMonitorPanel getExportMonitorPanel1() {
	if (ivjExportMonitorPanel1 == null) {
		try {
			ivjExportMonitorPanel1 = new cbit.vcell.export.ExportMonitorPanel();
			ivjExportMonitorPanel1.setName("ExportMonitorPanel1");
			ivjExportMonitorPanel1.setPreferredSize(new java.awt.Dimension(453, 150));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExportMonitorPanel1;
}


/**
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getJTabbedPane1() {
	if (ivjJTabbedPane1 == null) {
		try {
			ivjJTabbedPane1 = new javax.swing.JTabbedPane();
			ivjJTabbedPane1.setName("JTabbedPane1");
			ivjJTabbedPane1.insertTab("View Data", null, getViewData(), null, 0);
			ivjJTabbedPane1.insertTab("Export Data", null, getExportData(), null, 1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane1;
}


/**
 * Return the NewODEExportPanel1 property value.
 * @return cbit.vcell.client.data.NewODEExportPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private NewODEExportPanel getNewODEExportPanel1() {
	if (ivjNewODEExportPanel1 == null) {
		try {
			ivjNewODEExportPanel1 = new cbit.vcell.client.data.NewODEExportPanel();
			ivjNewODEExportPanel1.setName("NewODEExportPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNewODEExportPanel1;
}


/**
 * Return the ODESolverPlotSpecificationPanel1 property value.
 * @return cbit.vcell.solver.ode.gui.ODESolverPlotSpecificationPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.ode.gui.ODESolverPlotSpecificationPanel getODESolverPlotSpecificationPanel1() {
	if (ivjODESolverPlotSpecificationPanel1 == null) {
		try {
			ivjODESolverPlotSpecificationPanel1 = new cbit.vcell.solver.ode.gui.ODESolverPlotSpecificationPanel();
			ivjODESolverPlotSpecificationPanel1.setName("ODESolverPlotSpecificationPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
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
public cbit.vcell.solver.ode.ODESolverResultSet getOdeSolverResultSet() {
	return fieldOdeSolverResultSet;
}


/**
 * Return the PlotPane1 property value.
 * @return cbit.plot.PlotPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.plot.PlotPane getPlotPane1() {
	if (ivjPlotPane1 == null) {
		try {
			ivjPlotPane1 = new cbit.plot.PlotPane();
			ivjPlotPane1.setName("PlotPane1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
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
public cbit.vcell.solver.Simulation getSimulation() {
	return fieldSimulation;
}


/**
 * Gets the vcDataIdentifier property (cbit.vcell.server.VCDataIdentifier) value.
 * @return The vcDataIdentifier property value.
 * @see #setVcDataIdentifier
 */
public cbit.vcell.server.VCDataIdentifier getVcDataIdentifier() {
	return fieldVcDataIdentifier;
}


/**
 * Return the ViewData property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getViewData() {
	if (ivjViewData == null) {
		try {
			ivjViewData = new javax.swing.JPanel();
			ivjViewData.setName("ViewData");
			ivjViewData.setLayout(new java.awt.BorderLayout());
			getViewData().add(getODESolverPlotSpecificationPanel1(), "West");
			getViewData().add(getPlotPane1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	getODESolverPlotSpecificationPanel1().addPropertyChangeListener(ivjEventHandler);
	getNewODEExportPanel1().addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
	connPtoP4SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ODEDataViewer");
		setLayout(new java.awt.BorderLayout());
		setSize(720, 548);
		add(getJTabbedPane1(), "Center");
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
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
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


private void refireActionPerformed(ActionEvent e) {
	// relays an action event with this as the source
	fireActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
}


public synchronized void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
}


/**
 * Sets the odeSolverResultSet property (cbit.vcell.solver.ode.ODESolverResultSet) value.
 * @param odeSolverResultSet The new value for the property.
 * @see #getOdeSolverResultSet
 */
public void setOdeSolverResultSet(cbit.vcell.solver.ode.ODESolverResultSet odeSolverResultSet) {
	cbit.vcell.solver.ode.ODESolverResultSet oldValue = fieldOdeSolverResultSet;
	fieldOdeSolverResultSet = odeSolverResultSet;
	firePropertyChange("odeSolverResultSet", oldValue, odeSolverResultSet);
}


/**
 * Sets the simulation property (cbit.vcell.solver.Simulation) value.
 * @param simulation The new value for the property.
 * @see #getSimulation
 */
public void setSimulation(cbit.vcell.solver.Simulation simulation) {
	cbit.vcell.solver.Simulation oldValue = fieldSimulation;
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
public void setVcDataIdentifier(cbit.vcell.server.VCDataIdentifier vcDataIdentifier) {
	cbit.vcell.server.VCDataIdentifier oldValue = fieldVcDataIdentifier;
	fieldVcDataIdentifier = vcDataIdentifier;
	firePropertyChange("vcDataIdentifier", oldValue, vcDataIdentifier);
}

public void iniHistogramDisplay()
{
	if(getOdeSolverResultSet().isMultiTrialData())
	{
		getPlotPane1().setIsHistogram(true);
		getPlotPane1().getJCheckBox_stepLike().setEnabled(false);
		getODESolverPlotSpecificationPanel1().getXAxisComboBox().setEnabled(false);
	}
	else //single trial
	{
		getPlotPane1().setIsHistogram(false);
		getPlotPane1().getJCheckBox_stepLike().setEnabled(true);
		if(getSimulation().getSolverTaskDescription().getSolverDescription().isSTOCHSolver())
		{
			getPlotPane1().getJCheckBox_stepLike().doClick();
		}
		
		getODESolverPlotSpecificationPanel1().getXAxisComboBox().setEnabled(true);
	}
}
}