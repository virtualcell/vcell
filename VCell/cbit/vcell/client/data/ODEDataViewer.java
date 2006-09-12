package cbit.vcell.client.data;
import java.awt.event.*;
import java.awt.*;

import cbit.util.VCDataIdentifier;
import cbit.vcell.export.gui.ExportMonitorPanel;
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
	private ExportMonitorPanel ivjExportMonitorPanel1 = null;
	private javax.swing.JTabbedPane ivjJTabbedPane1 = null;
	private javax.swing.JPanel ivjViewData = null;
	private boolean ivjConnPtoP2Aligning = false;
	private NewODEExportPanel ivjNewODEExportPanel1 = null;
	private boolean ivjConnPtoP3Aligning = false;
	private VCDataIdentifier fieldVcDataIdentifier = null;
	private boolean ivjConnPtoP4Aligning = false;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ODEDataViewer.this && (evt.getPropertyName().equals("odeSolverResultSet"))) 
				connPtoP1SetTarget();
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
		};
	};

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
		// user code begin {1}
		// user code end
		getPlotPane1().setPlot2D(getODESolverPlotSpecificationPanel1().getSingleXPlot2D());
		// user code begin {2}
		// user code end
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
public ExportMonitorPanel getExportMonitorPanel() {
	return getExportMonitorPanel1();
}


/**
 * Return the ExportMonitorPanel1 property value.
 * @return cbit.vcell.export.ExportMonitorPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ExportMonitorPanel getExportMonitorPanel1() {
	if (ivjExportMonitorPanel1 == null) {
		try {
			ivjExportMonitorPanel1 = new ExportMonitorPanel();
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
 * Gets the vcDataIdentifier property (cbit.vcell.server.VCDataIdentifier) value.
 * @return The vcDataIdentifier property value.
 * @see #setVcDataIdentifier
 */
public VCDataIdentifier getVcDataIdentifier() {
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
 * Sets the vcDataIdentifier property (cbit.vcell.server.VCDataIdentifier) value.
 * @param vcDataIdentifier The new value for the property.
 * @see #getVcDataIdentifier
 */
public void setVcDataIdentifier(VCDataIdentifier vcDataIdentifier) {
	VCDataIdentifier oldValue = fieldVcDataIdentifier;
	fieldVcDataIdentifier = vcDataIdentifier;
	firePropertyChange("vcDataIdentifier", oldValue, vcDataIdentifier);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G380171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8DD4D45719C671B7E344788791A3A2AACDC9CA326CA94D26E7CD57E4C975B424B52DE95956F3C2DB5338B9EECB93375276B2002203899A1225D91A3246D40CACC125DBC235C141869485E59CF0CB9B5F4C3CE1865E4C9B5F3C7981B46CF76FFD774DE3F8B394BCAC67FC673D7BFD775E6FFE3F773BF79E2862B749A505468A048ACB1072777BC20472BEC2E85B17DE69E038F80F4504B4FF7F85F088
	BDBBDA8C4FE420A5ED30184A51BA31945A1950EE0076FF407B1B68A1D1EE0417A8FCBA003660637FB147667358A70C4FBE523E66A803676BGD900434FE291739FF0D4AA78DC854F205088B852E73623CA4135C17BA6C082C03AC87BEF0267FE42F9F6D323522FEFF19A4A7F2AE4BC0867514C496028170EEDD31E5568E7053B3C1857A3E41DF84C8CE8E781E0610B500F8EFCG4FB3DD037DFF34D9DDF2DD40468BC21DCDF0719E394E4E49DCD3D3E76B05D6F8313878A0AFA1A45C42733D6AA8A56391EA0076EE
	02BB9B42BCB260BD9BE03B0AAF9E4278B31245E48690A4A65759E3E714F5770EFC91D57A1E5A7FC6943962A4AACF3A9416CBB8E1AD7A9531410F5B302EF7G2D4CE7B1D5833482588162G7E13587E5FBF41F21E334AAD225BADFA4EF93D67853BF4D93473823CB7B581C541DD63ED22E4C7085AB753D12B331ED8C047FDE5526328CE52CEB3DB27DFFB84657E5DF263BE9D1D64260D9813FCD497193E30CEA86FBDD1F9EF750D7545313CFB1016B7FA69543E929D5ED55FBC35EF07B2CF0106F72D0CED7DA87101
	AAF8AFD47C03627F01603D9ABC754B9602476D99C04B1319EF743E4E5652D11C024ACF2E5055C342D78C46B6BFDD43CDFF64DAC2446F8CFE4A53242EE58E4127EB70F4AD87889E3737G2DBEE0B1D17F5AF3914769D2E8E7GE483EC87C0C10B298A20BE48FC6CFC5B07FF230FF5F112FC5E6F926C90D0F46C1E3637614907E409EB7778E44EE363AD1C64622C826FC3CA1F62F62C836B18F17B5B316F5B0047A73C0717B859A5FAE0F64E4D4B3C6423FD8E379F42FDA41ED04DAEB78F8C5CDE4466FD277DCDF88A
	1CCF3E6E05106743BDA87DC8BB36C90033400CB0813CD33D54F4E0FDF5017CCE00102A07D5E43ECB3C64610526268ED19C74FB4DD892149D44FABE5C0FF307815ED303CC47C7AFE3DCD948E2BA9AE273ECEDBF2C192755E5434B6724A196E3DF8C61DC5C98E2BE73CAAF731956038F235A6717697ACCBC782FB9C4FDC588C5464E13E4CE3CAE5AEE8F75CEA2069BC37A7195BBD7D8A74523E892FDDFD7EDF57F95EC2B25303E8C009CD547EFDC2C26BEFB1E73714CCBBBA5F0B05E9E56B8DD67E7C330E92292E55D
	7B15D8B9284AE1A0749DB0F77910455488F093E05E9053E57910D6CE3DF9A6A3173EAE0F0F61D1244367EEF8D01D15DC427B949F00AD321577729EBB3C7894DA59A54CC709E307280EB9CC70DE087B9688B41F0A2F3C04E31313E549E5754BFCB7AF4BAECFFF18DECDF8797CD6EFB8E5B0DE47AEE159442087EF4B8A2E76920E991B68717044F86A9005DEC7E21697F8B7AC3AC574FBE40AEF6840FEBF488FA9B92331E3AF42B1AF78F17E2135C28607756715DCEEF0891C42D079B00E67637D159A9DE5003DF14D
	13874F5DC3ACB6036F3158BCBA7CB93471D32FD1EFEFC9D8374858BB2C4466F0B8B6691A0EDF56DF7B07A4F7A8F89FDDEB68ED6CCF91250B928B1BBB493EG73A783E48D33D8787CE5CCE779C6890296D19028AAD59DA7DCA947B661EF79F9C853B831DDD275367A4A61881AC551A125EF3802F5A8E8F2E24788566113C32C964C99267A5B814FD22055823C8BF85AEF45B06B17B0C27BE540B387A0FFCCBF77886BD7F11B762B3DCD7BF55C56765BBF4A7A89CA3FB825DF32522F668E6E77AFEA3FA7EE53F5ED05
	67AE000ABB54CED46F71C70F21896AC90DC5E8AD39046C87F83CFC07550C7FB8C076E8E8EF82483D437C6839032CE6DCBA1806F2DFBA39AFDAAD1C2F2C2B7233309F656FBA37177A40DBD7B14FDE20B9BFE376BF52295D374E79FC224DC536CC45F64FF5EA7D0C623E59F9G1E8E1764135BA19104181DF3BB71FA711E2A20159A63C7C32C5E398DFCB3810AC87F2B4358EFE09B6F9605G8FC5284FAF48108D1092E3A9DEED0C6D04F10D2396D39F00F7144DF1A76A9CD4F7BFBE572C0C5F72C88A4A5AE7B06A55
	5B4E8D8946F923D4E79923E15D4DC83196AD8520F2EC9CE6F22099301699EC8D47C6E24B1196504A6105F169GB9GF96A9C5DD167203C170FB05E85B026619EB66E67A3B8175AD5773A4CF938FE927714FE7CF6B4BA1D7754A81BD700B9D355F993C7F13C87EC58F15BF132F7B9DC3CC4E91BF4E9F43E3D28CC716BDC182BE2A61B4FCD76BA9F64F9016F6D92C479D4ABE27A7949AD2DFEBAE6D2BBFB6199270ECFC0B8B7FB150154368F3F2B35EDA9B7DB573606177A0DC9B31573654C30ED8B16D80B4A17DA0B
	689E1A509FF9FEA81B952EEB2AE70D2FF1E8DDFDFA904B5BB0AB3276BFCB702D33A2EB7F2786183E4DC04B1A45EA41D72FE19DD445016CGDDGCE00F9GE9F1ACD66DDD27518435F63B87CAE08FA7F40F59BCD94DFB9573057DCEE93FFB955B096CF70855923B06A6BA43ED0A9B2B8BBA6E97930E5B91B10E664E3477BEC663F3270F84C19DC8D6576FF71163B80D8B6CB95DDE5E869EE7D36AFEC8DC0A9F4DF0EB7D884D063A9A208D208F00665DEFFB5999DE1E4D72EE73AD12F7213D89A0F7B63309590C73FF
	BE344B67D8CCB5G2DGCE00B80034B94CAE1F3BFAFEA23B1CE3450ED9D4DFDBA28B96264B270727E6032D930E5BB8C74F86F9D5F5BAB69833FFDD6103E0038BA12FA849D4650A4E1BFDFABA5F86FCF64EE1BA5F9EE0BAAF1E4BF43EA0C86EC720ED86906632FAB9E9E4EA32BFB461383839FA639AC727466FCF930ECB5265D706A247E5C6745367774E38F16FC634F3666A5976454E4AB13645993429890791B1A33145890959E707EA1F059236CF75BC48C7GDD73D8CCBCF39D4704935AF1GC9GD9G39G3B
	812A66331858F3758F93454485FC1800F8A0A1B0BE986839798B24CEBA8373F681D01C4105F1B2348D73195FFCB408EB798C355DC25A5897E9FB73C064F993770DE53F49G6511B70E47E44F2D31006A751757221F4376F66B1F434A3A754FE1073B8FD1BC4C213D1DD16FC83AE31F530EF46B1D53FECD647E217C9652B913DD615CBCE2354931617DCA1FAED96CBA297A6CCF0E2BBEBB408538D01DAF88FC1B1AAE921725F9C79E54661D6C7974DEA59F1E258BD85E49F217AA3629DE407C7AE5917BF5AF343DG
	7396B07F781EB7D6BCA7AF08367FE7123DA3F3815D678BD57C1789FE2386CF777FBF5DE2F752DB00D6FFAF0B173F1A235B15B76B5BF550CCEAAA51AF5978C85BC966CA15960E2DF619F7E39350BEA3E43C4CC97D7CB85B4FA463A9AD5A78FB4D78DCE7F559DD12F2DAD4EA52B1F6BD05587D79F7E5EC8FCCDBFAAF3307056C83DB201D87D0F0AF33477783316CD1E108E60FDF1079EA8C117658C76EDC9B8C1176088FB2FBB483AD55406A4DEF0CE8E5B82D7A2CB82A45B740BB7577E75EFE0344124F477BC8E243
	4531B2268531B197E47172A9B5DF54CB31E4ACDD98CD46C29F39EFDE98A96381022FD998A96392094958G3404054C673EF1BD3A4FFD7B3A3E4FED3F9E5D67FEF8FDE21F5BF5BD3A4F3DF6FDE21FC316E8B9096A7A69FEFCCE9038A1518FE7A767FD649C2F5E5594006C9500BB8350F59F4D99B467BC9A443E6C10B8B7CC07ABCC543A880FDD6BE2E3B3207FA600BC00E2209DDD44FC65DB9FE27DDEB4F3D6ABEF47B96AA41D73C4A9768D1DBC966E2946C67AA05B038D0BD8ECBCA360F9B7C1BB97A0FF910B0DAF
	3B471DD562B5E7157831FE53GED6A9F1FFA71FCED7154BFCAD43F99A078BE8D1E7A4D3F096CAC67855A56F8367F3E6AE57BEF63E2362647EFA1552F7FC9735EF1EBB2773CD35BF7797F879EAF4A11BA37AF8E6BB9846F86CDBBF9B15379D7C9FE49DC9C29736789BEFBF1244E7B026A7999E84D77B31D9784184ED36FE7BAAF8E0575FCD0733EBCB87D7A7842347220B2FFA2B119F3D419777942F216EB5E932569177931E965C16DDF62E4393AGE4EED84272538F6AF07ED3F3C99D9704B27E3C086FEBBBE832
	23F9643233D499AFACE1F9E4044C198A6DA7G36AEE1770A215F32FB457475A9A8677B7A770A5B4DF70CBB17D05FAEDF2A399BBBE1AD22BC1FF1B11E9DC00FDB0AF12F39AAB4F3603C568E85F99DBCA97D7800160EF37AF124257FF98C9D2FF7CD09163EF08C3DC4B31E4A3679FDA6DB41668CA47CFDDC546F8736A8B26DE232FDAD3130AB31EBAF357923033176681AE551766833EEF23EDD969977ED845F35ACB26E1F95D85C5B01163D0C457DD6D24356AE07DC301C613AC59697F69567A338F950CEDA4EE205
	A7F9BA8D5ADBD51C176070DDB63E47263857443B71EBBCF7B4DA1A4A375CFBB7BCCAE250481D68F22C0303372AE3745B292C136A6BE72496EDDDE1B1752EE0BA9CD2F36CED9517C8F049504ED4F1AF86182E33D7B079F2029837955A79AA6EBB84D711C0FFDBD0FE779C13B7F1DFDD1DC6417FEDF9EAE48C0FE83A1B6A59146A7A4DA053F5E3427835F6A8B85A772FD2AC3B5CD47A52351B1CD37B7664D7D39CF7ED5263E89CDFF661F81593A2EBF08B414FC8082C416FD1733A81E83B9258FDCACE8F2EAF4D09F0
	9E851887108C3091A0B7115569EBAD75E8023B9433686D00C2DE509C6329FCDFF3CD6D1B1CA777317C197978C3A8EBCE2B51047473672EC4E5EFF800BE3336A49662A7154FF6834B578C34BE0099G89G69G1B9EE0723DEC292572DD303B70CDA9E7F312DF1B18F078170BB09A9767B48E3387D93CEEFBC03D77A17932F82545D43912612E909C7ECD817F6EC2F14F8BF73B176379A3F145AB353E7C4ADD73500B5DA24D93F4679EFF5EB1FA581925FEA5BD6F344133EFA57DDDC5DEC9EAFE31B286DFBA77823A
	4FF0D633CB9678A7D662EF83FBEFA215DECF62FA023B7913346FD99B76ABF66FF8D21D6303DB781E2E0777494AEF66BC733B2A4F2FC5D53FE9B69E0A62F7A8096A3AB6A9DC130457FE48BFDEAFB5FEA6734DA42A1790BC8DC954AF28DE6283936BA5A3006D4BFB70CF59F6141504477DF30055C02950263A5A4805F5653B0DFFE725976817C50FCB96A52A9E056EBC04FF7F9BF74DFE125D339F8CE957D5202C39E19558F6953B6F7AC3107D26FCF395BB1B7A02D88FADBC7EDD082D25E0D00B233E5A6DC22A5F76
	E95E67BA273F2ECE1ED69EB4473C50937D2EE3FB0F7EDD478FFA746FCDF776443E974D6F097E7D4A1E9E6D77ABB4C746DBF1AD1BGB64B8458863093GBD4872637C5EA30864C71D5CF8415E0F6FDA7057F3B263AF5C08AE2B6F46445F5C5D3E01FF5F370A32AC3A717C6A1D608DECAFD97492A4454D3D01F5A4397A1DB26D2A60975E403EA8708E0226F54077862BE35809558154C7DFA3E78A1C7971C59A45BDB7F2C00DA1F23D266C2D9FF8585EDA85BAB3BF4862C1723018B4BC484EC249246FA6E8678264BF
	484EC259FF511E972ACFF46A1E97F67FE558D8361A2E33FEF5B86F742DEA58CF67F9764F5A6F164B9F900C05BA73E030AB633D1AF928BF08F62C27D440EF844883A8GA8CF3618EAG4EG181319EFA45D2C41FA6286142AGEF2122C43C83107400C87CC362DC9E196A2B4B4F746505F95212193E927CCCDF5B13193E1EA6FDAB57D8CC75GEDEBD8DE79EEG7B310393FC5866D4766420D6764683B3F53FF7A236DEC3E516152761170905741C7BF81F5E3759BC31F9D353787C496E4FAE04D86ECBDF43567ED3
	3276BCE897GD424303D3FB9282D09EA815F1C923B5ED63E0132C60F2B55560963AA4DCA3E2501F8B08FF9157B4C072DB8970B8E070FD76377B1EB390AEB77F509BE975ECD15BBE34F546A5CBFCEF1DCF05263E89DDCC16E8E4DA91175712F897EE6CAE4FD3CDE0DE181E8B9A92CBEFE0B7CCFC143DA0B298BC086B0GA483E42DE5B150EA0B958374FAA49C83F45E46FE245AF53E6D7DE863A143DE6482DC27F5003749E62EDF9DB3EC0F6E8B7758A7760585F67D9C9BEF9F1FE3175975F36CCA3BB647D2DC32B2
	2F247E58C6716B6DB81F51FAA87C8D90353145BFB55F30CFF15C2AC90F233E5394A03F952D0D742996025F36B652273628F9AE8FE8ED294C270E10BB32F4E8E727B29F7A08BF9143073AA4514BCB729045122F9B95D9AE0618ACBBD2476710829D9CDD330B2C39FCDD24AC9241D72D0B14E50FAACB8D508C6B58DDC48F2979F6C17B67456CEE230F60CA57DBCC77942A5F8F6032C6F9FFCF731E6E197E7A6E29E96521FC4B64089E0F6FBBA60E47BA07FEBCB6B8464763C78E7DF87C58210DC7E52F8904F55D2EF9
	7F3AFF7A757E723472D03E47F662F73596262141F46A44F52D8BB6EB97A738064919309BBAFA7A291FF2E44FBE8AFE5A3C1E7973C3FEF626B72FE77B352851DBC873DE9818FE9D3EB32DBCE84D437D36AC2C279E17DDF6D2FCD568E0985F46E3FFC32CBE8E911AD7629D3CA47176EE57B076C3FA96DDCC74BA8374E7G203577137E6A986B48DACF7394BD373EC04EFF144FEAB24E43872B594F8EF52DAEB80160FF0A29E9785D695A6A133F2BBF2E5404FE3C6EEE58E11C70247FFF987C6CFF61197E2F28C4C67B
	F1B9DF0DABF79919E8A9C7DABE899D9EBF7B397BDE78D67A272D95C6E4ECC7FB1DC47CF6D492447A8961FF64D98E7970A9D6FB9E9F55561E3BE75B22561E5BB7D0D973B7C4565DCFA32C377AB40B29A30D6D8BEF237FC651EB03EEB7A788B2A78C1E7358F11DF0FEC8664367404D041F0F75D1081476F5326E814D187F83D0CB878818233AA59493GG8CBAGGD0CB818294G94G88G88G380171B418233AA59493GG8CBAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1
	D0CB8586GGGG81G81GBAGGGCE93GGGG
**end of data**/
}
}