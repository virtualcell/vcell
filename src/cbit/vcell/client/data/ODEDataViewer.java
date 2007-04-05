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
	else
	{
		getPlotPane1().setIsHistogram(false);
		getPlotPane1().getJCheckBox_stepLike().setEnabled(true);
		getODESolverPlotSpecificationPanel1().getXAxisComboBox().setEnabled(true);
	}
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA8FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD4D45719F61764E4F7592E69DAC3FBCCCBADB134E2EA3774AC5B3A3B36A5BDEC622E132DC97504A4B4219B5B4678D30C28E89E8388237C8CB0409886C483CAD6CB7D21A704189695CFA40210B08A92B4BE1887BCFC4C4C4EBCFE7D5B6F3B775DB70F614DA0F8601C6F5CFB3F6F7EFD3F773B778D1C65C0F8C62A5142F129991C7277FEAA47A5FE40F1A355AB17B39C37F13509537C9D8448673E17
	158E2599E85955861319FBE8AC8B5A15500EB8E2B015C03D107B5BB1398EAA8156E9865A066AB75A022FF32F192D13C95A9B3233214C8528869C16B214E6AEA3472A601B95BC03D4A360B6E11F0A9C1B020B01BE8600AC405BB6517E35D0528DBCD4DF27746BFEB412CBFA46E34C41F9B4F3B2D8200C0DD14A8AEEC76A0D4D38D6D3CE36B2E693502A81074C27F13F5D7B3FD01EEE3B597F675E9ED12E996995A42926D7928527DC6360E53E3E3E3571FCA3D42E084228606138F8B25F23B9D9E4BC47C5C0FBE08B
	62DE3E05EBD4C03D8E20ED8B43BFF19B71D1D00F85085F42787A6848E9E55F1D377E19DBF3F2A8BDAFGDFC9DBA8BF365F7B78F2BBDFB26EA7BA78779614F5A750EE834C870886D8831084D0CAF47F73E66473CC0FFC4EB5B464F21EF13B4FCA8ECF0B4BA1C8D02F2F870A027BCC68F5F99C9CC7755B1AF3CCE7BF41000E3BB663F1D4A61127182E23BEFB0A0B7F41C2E3260ECC62A3EF99331328ACAA13FCB221EBAF8C38F6E352E4DB1C3CF68727DDBBFA618BE999BAEB9B7E7A0534CEE59EC9337622A45475E7
	C45721D0CFD56C03623B89BEC10327F619C8EC865B1B00E65F4AEC23B3176D25B9FDB1E7BE71182E9C42FE9AEA0C59CA77E0586A3F177DF7F06E142DF44DFD6ADE0E903C6DAD9F1E6EE5A959A336CF83AD9C005AD7DCAB7AE97A3655268A00BA00B6GB7C0A8C078B6E6E3F9ADFF3CCF9BEB63BD7259E1D1F200C35131F1AD45D08AE33207EFF2FAE55E59ABDC61BDA25FA389DECE6973DC8B4A20E7523897086D77429A9F8BCE41434B224B893373C302ACF83C344FABAD795847A3902ADD9C92E001A1B74766FD
	3D2590F549FB654BEEF0F941570352B73620CEC678ABB0A3CCGF5AA172DEDA82F15407FDA00752A9C86AF61FC97850FD3106A6B1BDD2E1B43EEBBF24255A5231C17115811827DAD49CC461BBFC55CEDE8AFC8E673FC6BD201E61EC63197374FFB46190FF55E46D89C194CEC664DCEE6B30DB94B38E34FFFD557E69AC3DEC80BCD2636921F6C6FBBBDE4CE5C97ED7F76D6678CFCB8B2D95F3F02470A345BB3D9A3ED86FDF3D5DDFD510E3ACA5F8EB68C50301D49B87ED2853559333CD7E0D65A6A8183939CBE0953
	FD9E388307A617216C5B2478CA0E4A479F883D93661E879089E0GC85E4EE4797DC9FC6A4DB3933E74E5794C24B552F456F910B5284C7E3B954FA9E1840E48C641ADB89DD071AA34F52D380E6247FDAEF54CAB846F863FBF870E66D5713FEEC55F64E559A3768C4BC23BA04B22335FC75FC056728E7738FDA1032D15540A3C39C61DF8ACAB386D2D68B33DAE27D3A04AD307A8741D44E785C99802CD1FF38DBBE50ACF7D946D7E26B02E440C0CCF77F0687352B0469F1AABAC67D1FEEE0FB884A601A10C1B37837D
	B9D64DF508AF818EF39EBB1483BB18EF0E3E4BFC33F262BB5C4AAF5CC63D33A56C09FFB0466DD0FCF3074FB7691E5E7AD4FF6FA9A4F6A8F8AF5D6BD8B15A13DF6A2278C2181D1C8B38771D108B6DE43EB0769952D93CD11C601CCB1228A8D5998B6DE6540970FF438204E98CEC97D539C96D85FE34AB0A8CA9DDEEC799CA1A1838E2A74A30678E4B859BF6D279F5C2B9863088209C7034DF5DDD56AFAB05762B0032814011226D17FB0F758BD17AADD27A2D18546F3BC656AFDE69172474B36F227D42F6E13F3BE9
	2C5F31DDF4DF0DD05EG8859C575C465BE3F7288B7CDBE2951884DA5EBF2331471093BD84E5815CF4E685D2BCD35G4D3B199DBD1743F24685B7A339790FBD1F96A897F66C267BFAE4374F0E86560D26D19BF875B22E998734353B197EB736EB4F2DB3DE2F2BD7A4C726223BEF5835F6C6F15F366F0532CF74F865A68884E3CC4F5F3163FE71CCD550CA0E71F7F7D83E73862CDB7976EAD39A693F658E5A8D9C636DAEE9C400A454BBAC49908DB84547BEAA517AD8B70C0B84D88510204ED1FEB7509CD4F6DB4EB4
	A863D7BD35180B498CB56A655B83CF0699135FE6B2B32832EBCBFC4968G20FC70F7999FEE689389C0779097F08F140F2689AD9F89B04E16EAB0B5G58D3599CC9816720EB3FFF0F2DAD411888F55CB76EE1ACF528665542BB79FE6277147ECCC0BA1D7797C6B6EFBC4CE9B132F96B53505FC7FA51F01BB0580BFD226021B4C117C667FBB2ADDB316BE618ABC41DAFB79DED568BF1DE92BA5BA417FC3211E3727932D5AB1F95B086751CGE512BA7E03F40C4DEEE5A0556D32F22DEE337807F4F59B7A1BFEE3F69A
	55E9ED1ACF372B1EFF4918GC04F50D3397E77075BCADF04791ABA831AD77F9771598820CF4E7D5792FC3486CF65F3A31F49BB96E8556986A5977CD9874AA0945A11GAB815682A483D8B38C0A2F1E38FC0A1BA657EEF2C28A6C642576C907A74BF9AF633AF05EA96D5FDDC6BD11730EE3394423F7263B4355E6CC16859D774DE947F57A0D2331B3725D3FF0D3E32717B8C18DF0D653BFAC126BB8758B341CF637508B96572B647D903894BBAA35E86D488D6BADG0881D885C0636EF0893B43A7820E465D97CBC8
	5C5DE3B05582B46FE1BAB9F895633F835A73G42G22815682A481D8B319DEC62E1C1DCEAFE7D832E3F72955F37E898B13E57F5D5969E0F85AF147B375F410E82B51514124736B12B08ABAB8BF66F6F9E4AAF2C566AF6E57137985D827BB13497CCB93CC66F2A61379D1F2870E02F6AC768320BCFCF4EFF63CDF1CF6DCD2265E38F546592D777A346332337446ADCB739FD76957CFFF3D9FCF99D76E57EE4852536DEB2D56C93A4588DADF0FEE04EA247A7939156927B30B6927FABF6AE7BE34A381D6E6B11FD858
	05BE31965AC9G59A608E1G4DG83G21A66693F1D7BE1C4EA74E63E5827C013840D4E7207766CFC81E9485732E842038A3E644A5C2BB45446CE6CDBE667295FBD97BE552C6DB246D3EFC7F7BA6768D263F19GDD635E14B5E6FAEF8D86D42E2BBA825F43FE5121FF8FDB5721FF8FFB25A31F62E18E6D6B0C7AC652917C1E3631C36F1ED6C678FEDDAE62F46EE41778A101E339C95C9D135227CDF6351DD0ED76776F29B6BB480F70E3B55ED1D8373E7E9392F9E85C49AB50461DBA50A53EDFB8209C5B4B62CE3B
	A5CB514D7C3D4C2E9FAEC63B0E03F682C032EAAF93A5417C593CAF50795FEA4579AA7751F3BED545F7927CF18D1E1E7F3FAAE1EF522701963E0F794B512B0175FA722A3EDE6B2E121C4AB56C69957CF577172BD61566732DA6D998C29550BE9748F81977748BD3F45FCA46D3DA20711DD771DE57A3BAC40FF2DBD4F2523BDA3D1E64587B79F8A96A83E96959CC9F1B4BD09E2721ED87103219BE9E1E88260F106CC07A78A6B9D742327D7591C97091597E7AB8F10B69E3B950ACB9AC5F146FE9F9B8255A2CB9CD0B
	2F05BA35777FA84EA33E64758ADE9258B0B9D646A42AE34E4B2E1613EA3CF8569A0C47311CC0BCAEA231B9B4471F47A788BEAC471F471A7D0C478820E565B21B8B6F8AECF3CFF6695B5C322E40B6975DB53D4D7DEBD7E01B0B6D1A5E667E33ABD0CC223266F3711EA071632EE138BB2D4D456FE2E339ACD6C8508E810882D819CBE3860DB95F9FC5DB6E7370C3B09DE618DCF49E0E7D33190D2D00F6AD009DC086D81047EC6517FFC479FEE267FBFA8487462893F44E23D9E89BBAF14C57D3750D0F8B58991C1247
	FC43D10873561A61FC85F01819EFF4DA265CD54C1A3B0AF9325DC4C01B5A478E927BE24C54BEB2D43B49A0B1E91586CF6DA634185D65920056184F4E5FC7CB58799B194F76F49945245875F5CD7D7512193C734E6E5C652CF33F465DD2FF192F497749F9BD54D3B4EDF3811339D0CE4E0782FF193B893E2E40DF665B4A547BB3501697B019BFBE41E4EEA9E4B2FF6216CF4ECFE96A6F4F687DFEF672E81B53B5A84F2FDA994F8DAA4F0B777B78DC2A29576E1FFB1EDB66F48D2A7F70DC962BA560B9820046127F29
	41782746129AFE94527833AEFC2FED26410E461175A4DF46717105AC0E9467611C960B41F48C2091003E1F0C9DE66F0AD1CB96F3A17F287F2E18E03FE36C36D05B1EE7713D1F04B8DFB252B59DE636668A20A7D9B0EEFE45EC514C01F12D8992729AA8A9BDF6929DE37AFB1C167E56A4BA6E77DB99DAFAF53E161E2199CFF9FB360A7116726CF2CE7AE1C8405F8F1CAE22BC5DD04A7909DFCB0DBAFAAB0D6A3C39A058999DD694680CDECBE2F7D4113F5F3FC670AB0B7C7DFE3010797D9A205595B33FF7119CF691
	34172B38B70B18DF2CD1F1394508CB06F6F68973950E44E9AB349BD55CBE024337ECFC4726382F94BF08DD635C01E8D6F55D258F34C6C6909AF993ADC11B08AC2988522FDB59A71557D7C9AE9A8D383892A6C3CB99136B9BAA2E16604CD60329524AF0F74B182C6B2C0C3F1B6508EB0436C3450D90DC8834715B82456DAA534A817B6A4AB4G7E7E62D4F1397FB747C0F21B4D9AD4565193CC5611BAFBDD2160E85F6D56E0FAB1A8FD695E17CCF9073DBF1E23E7B9EE430C47D1BFDECFE2EA2255BF875FCC701B2C
	7EB938AD0F457594205D584F5ED39E6F46E4A9965A89G49G66F78C2663G4D6F30BC5D7D79F1EE1A37943B4B5D8C093C243946D37EC4736CFE137313CC96BF23FF74DDAE6661C663BECEBFFE5EF8C7B99B14327A352F25E2C979BBCE124165C0DB853089A02B54E032815416B27EE2BEBFC679BB6F907125146F9DA0DF1B98F37865420746641C7AE1EF8173578B256A3BCFA162E4E8BF2262FEC3F078CD813F3BD01C3B70C14FF21C5F9FA717EAED79EF9EF88DBD5FCD5314055C431629771D4755F77070D2FA
	5F0901F2D5A97D2E12D80AFDB20A2DC15625F3D711B3C566FB6C22AC8947EC86B00DAA3C32A874631E8A0EFCEF8E76B6FF02767D28976D0A3DBB1ED067F83A845F69BA842F2CFCB397185D5916FE1B339DE8B0668730BB370D4AFA114D171378769ED5BAD5AEBFABE5BC9BECD4AE6B21CC31516FF1D4AEC74B26174B45B2542F6044CF598E2E3A8C477514319C4882EDAA2B3F6F73494AFB9B3F335287749617D314DD9EAA9E05BE100F5F7F26BC331FE06F6C11655AFDC9E5F44F91D0461431772E788976CD59D0
	466E2616DBA807F382FE97E2FBF92CC00B23367AA6BE85A875ED1A7A213C394F2BBF1C53B5E80CD9529D782DE3D9377EDB478F3A755FCD7F25BB783B68CF3A837FFEA52EDB7B7B959AA32F75E3AE1BD58EB986408500EEGF7B90B0FD72E9D62C8FC54090D679D7D7856023F1E13597A9FDC8F4CEB437569FFF357F49D3F6F7738E459B504732BEF0257D1DF324BCD1094E73F0EB272087D83B26D2A603B2E23ADCAC29FC153BCE02220A2081ED89EC0EDF41B0863B17263C39A45BDF7EB2F6AC364F9CDB9DB1F36
	3033B594E496DB4E7CA1270879E4CAB93B8BFDD8C45ED48E40798A60B8406EC2F1573477050A232D3A770564EB93465B8768BE438F7862CE3237A20D4E337AF36D6F164DDF170C29BA73A02CD146A7E8662176F0DAC4B9D9AAA08FG3083C8G73G42G22GE2AB18ED08C2B54A099FD43282BCC2DD9EE29D002497C4E29F9EDEF44AD4DE3FABE572CA00F92C87193CCE59183C5A8EB2F971B66C7B88344381E28E3238B2DE06F65C47CBDE54B965BDFCD44BFB5D5E3F527D3D935175C14AF322D22694FDBD155E
	F3FF542D775BEC01683C3EFEEA7CE46FE78965AC76598E313DAF9C473D5B21AD81049CE2E77F2FA63DADAF827C72C34173ED65B7D07D017DCA6A1F5E2F067B49EFE9409F6C63EE65BDF3229FE3312B2F4FAB287EFB2F5F2C621A3CEDAE2F08272972E6BC656D6F7EE26C3AD90EABA81ADDFE7CBD62E731077C73639802B79C724F0F4F94B11B0C87DA433BACBFFEF5904F3D88E82F84C884C881B0D79ACC55154C8772870279GFD9E7179G1DB7C362D43DDE99288AB49EA26CA77C885F5AB3A8744AF63EDF9D73
	31985896AE09535BC207289FE32F09D3E3EC0F289FE387C4ED0C2538A1E5DE0F7A310D623DA246B31A8F79FE83C4F53CF94A5B667D59465B339CD7E71B1DCDBDCFE25169CAFF1BFA1960AFD47A5BD43F0D59149DE8B1154C26B6CAA8B7DB955C5F2B188D6D99BC9A44065ABCAE37601147A9167C3AD1616557650C174E2A29B1C4524151BD6F1D403D4D2B7267251060C32B7CF9D9B44EF88983DACA95FB0BC8A6B95F0D4386532EF47636310B6046G7725D4A6739F6267D8257EEF1A7A47C5F31F5F5D1853B514
	5FB28D867647AD03537B634EC1FDFFCC3DB9559F775C5477476C1BDAFF243C3FE87349BAD1D39F324D3D5C9F1E53B5145FE3CBD8D7F3E16A9ACC26926635A29C56A2AF099364CE588E9D1D7D54CED348193DG6CF479E1E64F9747591DBE76B0BB2F4D93BE396D5754970F4F3D8CFFBC27EB501C07BF1C6D13D307681087A85EB6166343DF90505EB8169F979EC01A5BA374899E0F60E897A7508E69DD7458811461A610DF8AG4D3DFB46AB026C237A880DD3745E3A4449296BB410F1CEE13402FDF628E9946186
	027F94D3DD7B27D347AACE7C6978FB346F3DF15CF7BB1CB083D0527FBF6C486C02B2EA8317A1F3A6CC67ABB0F31739FDB415A3AD2F07AB18BA7B19AF2F7BE554970D96A3E7EC62768C90761B380CD1144F987EA34FC20807CF335C733DFB5A5CB379215E0039E7DBB56555D16D1FF7AFB1215C42813F221A1D8BEFE57D018B1C9B348F7112A473524DB3CE876689E747E541F78FDCCA667732BE8A11527E095CBB86B5E37E9FD0CB87888DDA731CBC93GG8CBAGGD0CB818294G94G88G88GA8FBB0B68DDA
	731CBC93GG8CBAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF693GGGG
**end of data**/
}
}