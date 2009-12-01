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
	private javax.swing.JTabbedPane ivjJTabbedPane1 = null;
	private javax.swing.JPanel ivjViewData = null;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	private org.vcell.util.document.VCDataIdentifier fieldVcDataIdentifier = null;
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
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		//amended March 29,2007. to get singleXPlot2D or plot2D from SepcificationPanel to display in plotPanel
		//if the data are time series, we use singleXPlot2D. if the data are histograms, we use plot2D.
		if (arg1.getPropertyName().equals("singleXPlot2D")) 
			getPlotPane1().setPlot2D(getODESolverPlotSpecificationPanel1().getSingleXPlot2D());
		if (arg1.getPropertyName().equals("Plot2D")) 
			getPlotPane1().setPlot2D(getODESolverPlotSpecificationPanel1().getPlot2D());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (ODEDataViewer.odeSolverResultSet <--> ODESolverPlotSpecificationPanel1.odeSolverResultSet)
 */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			ivjConnPtoP1Aligning = true;
			this.setOdeSolverResultSet(getODESolverPlotSpecificationPanel1().getOdeSolverResultSet());
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (ODEDataViewer.odeSolverResultSet <--> ODESolverPlotSpecificationPanel1.odeSolverResultSet)
 */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			ivjConnPtoP1Aligning = true;
			getODESolverPlotSpecificationPanel1().setOdeSolverResultSet(this.getOdeSolverResultSet());
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		handleException(ivjExc);
	}
}


protected void fireActionPerformed(ActionEvent e) {
	if (actionListener != null) {
		actionListener.actionPerformed(e);
	}         
}


/**
 * Method generated to support the promotion of the exportMonitorPanel attribute.
 * @return cbit.vcell.export.ExportMonitorPanel
 */
public cbit.vcell.export.ExportMonitorPanel getExportMonitorPanel() {
	return null;
}


/**
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
private javax.swing.JTabbedPane getJTabbedPane1() {
	if (ivjJTabbedPane1 == null) {
		try {
			ivjJTabbedPane1 = new javax.swing.JTabbedPane();
			ivjJTabbedPane1.setName("JTabbedPane1");
			ivjJTabbedPane1.insertTab("View Data", null, getViewData(), null, 0);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane1;
}


/**
 * Return the ODESolverPlotSpecificationPanel1 property value.
 * @return cbit.vcell.solver.ode.gui.ODESolverPlotSpecificationPanel
 */
private cbit.vcell.solver.ode.gui.ODESolverPlotSpecificationPanel getODESolverPlotSpecificationPanel1() {
	if (ivjODESolverPlotSpecificationPanel1 == null) {
		try {
			ivjODESolverPlotSpecificationPanel1 = new cbit.vcell.solver.ode.gui.ODESolverPlotSpecificationPanel();
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
public cbit.vcell.solver.ode.ODESolverResultSet getOdeSolverResultSet() {
	return fieldOdeSolverResultSet;
}


/**
 * Return the PlotPane1 property value.
 * @return cbit.plot.PlotPane
 */
private cbit.plot.PlotPane getPlotPane1() {
	if (ivjPlotPane1 == null) {
		try {
			ivjPlotPane1 = new cbit.plot.PlotPane();
			ivjPlotPane1.setName("PlotPane1");
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
public cbit.vcell.solver.Simulation getSimulation() {
	return fieldSimulation;
}


/**
 * Gets the vcDataIdentifier property (cbit.vcell.server.VCDataIdentifier) value.
 * @return The vcDataIdentifier property value.
 * @see #setVcDataIdentifier
 */
public org.vcell.util.document.VCDataIdentifier getVcDataIdentifier() {
	return fieldVcDataIdentifier;
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
	connPtoP1SetTarget();
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ODEDataViewer");
		setLayout(new java.awt.BorderLayout());
		setSize(720, 548);
		add(getJTabbedPane1(), "Center");
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
public void setVcDataIdentifier(org.vcell.util.document.VCDataIdentifier vcDataIdentifier) {
	org.vcell.util.document.VCDataIdentifier oldValue = fieldVcDataIdentifier;
	fieldVcDataIdentifier = vcDataIdentifier;
	firePropertyChange("vcDataIdentifier", oldValue, vcDataIdentifier);
}

public void iniHistogramDisplay()
{
	// protecting for null simulation - needed esp for mergedData (where there is no simulation per se).
	if (getSimulation() != null) {
		getPlotPane1().setStepViewVisible(getSimulation().getSolverTaskDescription().getSolverDescription().isSTOCHSolver(), getOdeSolverResultSet().isMultiTrialData());	
	} else {
		// if simulation is null (usually is for merged data), just disable the step view in the plot panel
		getPlotPane1().setStepViewVisible(false, getOdeSolverResultSet().isMultiTrialData());
	}
	
	if(getOdeSolverResultSet().isMultiTrialData())
	{
		getODESolverPlotSpecificationPanel1().getXAxisComboBox().setEnabled(false);
	}
	else //single trial
	{
		getODESolverPlotSpecificationPanel1().getXAxisComboBox().setEnabled(true);
	}
}
}