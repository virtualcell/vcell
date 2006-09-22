package cbit.vcell.client.desktop;
import cbit.util.VCDocument;
import cbit.util.VCDocumentInfo;
import cbit.vcell.geometry.*;
import cbit.vcell.mathmodel.*;
import cbit.vcell.biomodel.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (5/24/2004 1:13:39 PM)
 * @author: Ion Moraru
 */
public class DatabaseWindowPanel extends JPanel {
	private cbit.vcell.desktop.BioModelDbTreePanel ivjBioModelDbTreePanel1 = null;
	private cbit.vcell.desktop.GeometryTreePanel ivjGeometryTreePanel1 = null;
	private JTabbedPane ivjJTabbedPane1 = null;
	private cbit.vcell.desktop.MathModelDbTreePanel ivjMathModelDbTreePanel1 = null;
	private cbit.vcell.client.database.DocumentManager fieldDocumentManager = null;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.client.DatabaseWindowManager fieldDatabaseWindowManager = null;
	private cbit.util.VCDocumentInfo fieldSelectedDocumentInfo = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DatabaseWindowPanel.this.getBioModelDbTreePanel1()) 
				connEtoC1(e);
			if (e.getSource() == DatabaseWindowPanel.this.getMathModelDbTreePanel1()) 
				connEtoC2(e);
			if (e.getSource() == DatabaseWindowPanel.this.getGeometryTreePanel1()) 
				connEtoC3(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == DatabaseWindowPanel.this && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == DatabaseWindowPanel.this.getBioModelDbTreePanel1() && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP1SetSource();
			if (evt.getSource() == DatabaseWindowPanel.this && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == DatabaseWindowPanel.this.getMathModelDbTreePanel1() && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP2SetSource();
			if (evt.getSource() == DatabaseWindowPanel.this && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == DatabaseWindowPanel.this.getGeometryTreePanel1() && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP3SetSource();
			if (evt.getSource() == DatabaseWindowPanel.this.getGeometryTreePanel1() && (evt.getPropertyName().equals("selectedVersionInfo"))) 
				connEtoC5(evt);
			if (evt.getSource() == DatabaseWindowPanel.this.getMathModelDbTreePanel1() && (evt.getPropertyName().equals("selectedVersionInfo"))) 
				connEtoC6(evt);
			if (evt.getSource() == DatabaseWindowPanel.this.getBioModelDbTreePanel1() && (evt.getPropertyName().equals("selectedVersionInfo"))) 
				connEtoC7(evt);
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == DatabaseWindowPanel.this.getJTabbedPane1()) 
				connEtoC4(e);
		};
	};

/**
 * DatabaseWindowPanel constructor comment.
 */
public DatabaseWindowPanel() {
	super();
	initialize();
}

/**
 * connEtoC1:  (BioModelDbTreePanel1.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseWindowPanel.bioModelDbTreePanel1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dbTreePanelActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC2:  (MathModelDbTreePanel1.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseWindowPanel.mathModelDbTreePanel1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dbTreePanelActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC3:  (GeometryTreePanel1.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseWindowPanel.geometryTreePanel1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dbTreePanelActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC4:  (JTabbedPane1.change.stateChanged(javax.swing.event.ChangeEvent) --> DatabaseWindowPanel.tabbedPaneStateChanged(Ljavax.swing.event.ChangeEvent;)V)
 * @param arg1 javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.ChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.currentDocumentInfo();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC5:  (GeometryTreePanel1.selectedVersionInfo --> DatabaseWindowPanel.currentDocumentInfo()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.currentDocumentInfo();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (MathModelDbTreePanel1.selectedVersionInfo --> DatabaseWindowPanel.currentDocumentInfo()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.currentDocumentInfo();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (BioModelDbTreePanel1.selectedVersionInfo --> DatabaseWindowPanel.currentDocumentInfo()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.currentDocumentInfo();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (DatabaseWindowPanel.documentManager <--> BioModelDbTreePanel1.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			this.setDocumentManager(getBioModelDbTreePanel1().getDocumentManager());
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
 * connPtoP1SetTarget:  (DatabaseWindowPanel.documentManager <--> BioModelDbTreePanel1.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			getBioModelDbTreePanel1().setDocumentManager(this.getDocumentManager());
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
 * connPtoP2SetSource:  (DatabaseWindowPanel.documentManager <--> MathModelDbTreePanel1.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			this.setDocumentManager(getMathModelDbTreePanel1().getDocumentManager());
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
 * connPtoP2SetTarget:  (DatabaseWindowPanel.documentManager <--> MathModelDbTreePanel1.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			getMathModelDbTreePanel1().setDocumentManager(this.getDocumentManager());
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
 * connPtoP3SetSource:  (DatabaseWindowPanel.documentManager <--> GeometryTreePanel1.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			this.setDocumentManager(getGeometryTreePanel1().getDocumentManager());
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
 * connPtoP3SetTarget:  (DatabaseWindowPanel.documentManager <--> GeometryTreePanel1.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			getGeometryTreePanel1().setDocumentManager(this.getDocumentManager());
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
 * Comment
 */
private void currentDocumentInfo() {
	VCDocumentInfo selectedDocInfo = null;
	switch (getJTabbedPane1().getSelectedIndex()) {
		case VCDocument.BIOMODEL_DOC: {
			selectedDocInfo = (BioModelInfo)getBioModelDbTreePanel1().getSelectedVersionInfo();
			break;
		}
		case VCDocument.MATHMODEL_DOC: {
			selectedDocInfo = (MathModelInfo)getMathModelDbTreePanel1().getSelectedVersionInfo();
			break;
		}
		case VCDocument.GEOMETRY_DOC: {
			selectedDocInfo = (GeometryInfo)getGeometryTreePanel1().getSelectedVersionInfo();
			break;
		}
	}
	setSelectedDocumentInfo(selectedDocInfo);
}


/**
 * Comment
 */
private void dbTreePanelActionPerformed(java.awt.event.ActionEvent e) {
	if (e.getActionCommand().equals("Open")) {
		getDatabaseWindowManager().openSelected();
	} else if (e.getActionCommand().equals("Delete")) {
		getDatabaseWindowManager().deleteSelected();
	} else if (e.getActionCommand().equals("Permission")) {
		getDatabaseWindowManager().accessPermissions();
	} else if (e.getActionCommand().equals("Export")) {
		getDatabaseWindowManager().exportDocument();
	} else if (e.getActionCommand().equals("Latest Edition")) {
		getDatabaseWindowManager().compareLatestEdition();
	} else if (e.getActionCommand().equals("Previous Edition")) {
		getDatabaseWindowManager().comparePreviousEdition();
	} else if (e.getActionCommand().equals("Another Edition...")) {
		getDatabaseWindowManager().compareAnotherEdition();
	} else if (e.getActionCommand().equals("Another Model...")) {
		getDatabaseWindowManager().compareAnotherModel();
	} else if (e.getActionCommand().equals("Models Using Geometry")) {
		getDatabaseWindowManager().findModelsUsingSelectedGeometry();
	} else if (e.getActionCommand().equals("Archive")) {
		getDatabaseWindowManager().archive();
	} else if (e.getActionCommand().equals("Publish")) {
		getDatabaseWindowManager().publish();
	}
}


/**
 * Return the BioModelDbTreePanel1 property value.
 * @return cbit.vcell.desktop.BioModelDbTreePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.desktop.BioModelDbTreePanel getBioModelDbTreePanel1() {
	if (ivjBioModelDbTreePanel1 == null) {
		try {
			ivjBioModelDbTreePanel1 = new cbit.vcell.desktop.BioModelDbTreePanel();
			ivjBioModelDbTreePanel1.setName("BioModelDbTreePanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBioModelDbTreePanel1;
}


/**
 * Gets the databaseWindowManager property (cbit.vcell.client.DatabaseWindowManager) value.
 * @return The databaseWindowManager property value.
 * @see #setDatabaseWindowManager
 */
public cbit.vcell.client.DatabaseWindowManager getDatabaseWindowManager() {
	return fieldDatabaseWindowManager;
}


/**
 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @return The documentManager property value.
 * @see #setDocumentManager
 */
public cbit.vcell.client.database.DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}


/**
 * Return the GeometryTreePanel1 property value.
 * @return cbit.vcell.desktop.GeometryTreePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.desktop.GeometryTreePanel getGeometryTreePanel1() {
	if (ivjGeometryTreePanel1 == null) {
		try {
			ivjGeometryTreePanel1 = new cbit.vcell.desktop.GeometryTreePanel();
			ivjGeometryTreePanel1.setName("GeometryTreePanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometryTreePanel1;
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
			ivjJTabbedPane1.insertTab("BioModels", null, getBioModelDbTreePanel1(), null, 0);
			ivjJTabbedPane1.insertTab("MathModels", null, getMathModelDbTreePanel1(), null, 1);
			ivjJTabbedPane1.insertTab("Geometries", null, getGeometryTreePanel1(), null, 2);
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
 * Return the MathModelDbTreePanel1 property value.
 * @return cbit.vcell.desktop.MathModelDbTreePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.desktop.MathModelDbTreePanel getMathModelDbTreePanel1() {
	if (ivjMathModelDbTreePanel1 == null) {
		try {
			ivjMathModelDbTreePanel1 = new cbit.vcell.desktop.MathModelDbTreePanel();
			ivjMathModelDbTreePanel1.setName("MathModelDbTreePanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathModelDbTreePanel1;
}


/**
 * Comment
 */
public VCDocumentInfo getSelectedDocumentInfo() {
	return fieldSelectedDocumentInfo;
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
	getBioModelDbTreePanel1().addPropertyChangeListener(ivjEventHandler);
	getMathModelDbTreePanel1().addPropertyChangeListener(ivjEventHandler);
	getGeometryTreePanel1().addPropertyChangeListener(ivjEventHandler);
	getBioModelDbTreePanel1().addActionListener(ivjEventHandler);
	getMathModelDbTreePanel1().addActionListener(ivjEventHandler);
	getGeometryTreePanel1().addActionListener(ivjEventHandler);
	getJTabbedPane1().addChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DatabaseWindowPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(662, 657);
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
		JFrame frame = new javax.swing.JFrame();
		DatabaseWindowPanel aDatabaseWindowPanel;
		aDatabaseWindowPanel = new DatabaseWindowPanel();
		frame.setContentPane(aDatabaseWindowPanel);
		frame.setSize(aDatabaseWindowPanel.getSize());
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


/**
 * Sets the databaseWindowManager property (cbit.vcell.client.DatabaseWindowManager) value.
 * @param databaseWindowManager The new value for the property.
 * @see #getDatabaseWindowManager
 */
public void setDatabaseWindowManager(cbit.vcell.client.DatabaseWindowManager databaseWindowManager) {
	cbit.vcell.client.DatabaseWindowManager oldValue = fieldDatabaseWindowManager;
	fieldDatabaseWindowManager = databaseWindowManager;
	firePropertyChange("databaseWindowManager", oldValue, databaseWindowManager);
}


/**
 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @param documentManager The new value for the property.
 * @see #getDocumentManager
 */
public void setDocumentManager(cbit.vcell.client.database.DocumentManager documentManager) {
	cbit.vcell.client.database.DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange("documentManager", oldValue, documentManager);
}


/**
 */
public void setLatestOnly(boolean latestOnly) {
	getBioModelDbTreePanel1().setLatestVersionOnly(latestOnly);
	getMathModelDbTreePanel1().setLatestVersionOnly(latestOnly);
	getGeometryTreePanel1().setLatestVersionOnly(latestOnly);
}


/**
 * Sets the selectedDocumentInfo property (cbit.vcell.document.VCDocumentInfo) value.
 * @param selectedDocumentInfo The new value for the property.
 * @see #getSelectedDocumentInfo
 */
private void setSelectedDocumentInfo(cbit.util.VCDocumentInfo selectedDocumentInfo) {
	VCDocumentInfo oldValue = fieldSelectedDocumentInfo;
	fieldSelectedDocumentInfo = selectedDocumentInfo;
	firePropertyChange("selectedDocumentInfo", oldValue, selectedDocumentInfo);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G400171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BB8DD4D4E7F618E842AAD1B4C4F103AE7E34E137A6B5A736CB373435DB33CBD732EB33E40F3BCBCF6D293B51D33635095B7564581E47A0028195B7A4F1B7A6E12B317E90D614B6061ADD916C0AA08EB00C28203C19F9B083B37306B7EF8482CA6F7D6E7B5EBC06B7B3119CCFB9671E773E7B5D7B5D1F6FFE773B5F778621627D742282CB05A09494895A5FC78502F0690CA0A87F7A52D60ECB765BCB85
	435F2FG8E88CEF997BC5331EF193D34DC705CAB01769AE8E7275BCB5F0677B704FF69ADB98DAFD164EC2A9104CFDEF27AE24B79D59F17D34C5A331B4A60398FA093F0785CA5237C65CD87B5FC1E0667D0E091044D0C664F1A8EE938CB506E85C887D8A393FD8EBC77B049336A4EEAF45D73B3046D5FD2ACFBF19C43189C2A8244EB551E1542EB85BB82A86B47CCCF64C90476DAGB43ED0B8B98C6E954ED9877BFF63F4F8546ABBCE496B2DF6FABD12DF2DF6C941C1D58E5455B50A2A68900352B50F5FA58FDBC5
	3F649554A19C377AF2899B8746003620A04EEEC7D919505EG30C5473FFB8D710D705E8D20AA5C3ECF7E731C26FF5758D7054D7D4F6F599F453EA4056CDA23046D4BBC61A87C851B8B319FFD3E8D7AAA027652E3GADGDE00A400F7D88C387BCE40335E21B648BE1F6C2F8F84AEF8DD4A9559A5F9613D2E8EFAB4DC3B64149517A050BC67B7D51968938B086F357B66A31FE41C61F33E2A7D99A16F390516E2931F64E50CD9D6864997D94130CFC876194B51E46785A74764E459578423ECE16B6962A29359073E
	F73AF81BB64EAEB5AC3BD645397E960B01A370DE20458761F399FE440027786CE6F8ECA7C2DF3E4AE323EB9F5725E557F2213CE611299FD23F1EE43106C887F7A8D2171357F16C149049FCD35765B78C1FED4013AE07D91CE2FBA774E55C3117D2FC8DF8F03D5A066D258100C400F40035GF9G5B06F90CBD5B776B4F98E3D6D1D1AF04BCDE17248844BB507F96BC2591D5911B7CC1D574BBA53B28F8C407D78A8A9ACD309FFD601844B742E25F89B2FEA779A5C5D4BD329FC697FD12AAA9C1221951FFGE99409
	755ABCBE89847882829F778B7DEF4053AB86554E00CBD425B085754F6F47B939A35AE1C498G5E49AF0B8650DF65A33066G4E0EF0BF1439F13CE6C901F4D1D757A24B0321008DAD9192C7504F17D96EE801F7578877716303084B0176E6FD1C86774F8D63B4FA1C283E280C72B536410EB9F947880F19FF6A62B1533877E9216A25A7CCE3A699627760A8454A295148357383B6A66AC5ED073D6BBE573191D0377839E26CF349A80C53CF7EAD7061DCF5013DAAC04AA87771BDF7A5456C85C873BCCA2FAA90E012
	AB6CF15233667AB14D1FA833C8539FFD0F9BA66ECD7B999D0E4BEDEBE4BC2BC0DEAE40CE00A31FC2FE86D08152G56816483D40CF11F9F3F16FE9FF2266B374F8B242B0D697AD974FC737FD1D71AEB55037AC8F7E00BEF1482125F85AFC12DEF4C03FAEB7927CF56F9E6B0FCG72D583A408200EFF5403F6092A2AF89CA1D56A10D455636F8F774FE33202A1C7A01C6A382C059E7415BC8C79CA27FF5203EB5DA97B7D928BBA1DC56BDF42F20D6415FC20F4039C722B04DFB1006BF5D09A55F25DD3833B854CD55E
	9066CD2AF5D60BB89F81456303D04654ABA40DE19E92AF9FB478689C6030E66B0527BA46F34A70E11ED30EFC7AE5E16D6D0045ECCFCC7D3DF9165CB1DA6BDB4642B905F4CA9DB457FDA94BF99ABEC83A5686B0D64C6AAEDA47F5FE9C2BE45CDEFA8820F61C2F63DFFA311F67CAED81B748DEAF39D377F3312F9C67C59A8AC93045E0D2EE56FD374F777308BE3B66C76AFF4B07FE749A72795AF174633D6B3C1EED99A79F3A6019G309260BD4093DD7B8DCED7F6B7CCF7845EAF5DB5523DFF1353398CF463701E2E
	51F55FC33AFC1DAE6BAE494B5568AB6E519C905D6AEECE17F10F642D3DE7C62776F0BAE102649D1CB023BBFB0B53F5C1FFGA0C5234B1DB0526D5469F6E86315A71851E55E66F42792B4FF1852293D1C4E1DC076A6C450D18C3DFA9C631A557683629DF124BAB88CEB332E6EB28BA42A5F5733BD98695393F81D7E33AB98DB9B213D8D20E486DF83FF33175769A90399C25E561A62E867104A9924C7630C709A4859D63F1BE2F7260CB2E7C2DF4A8C9E3773FD46DA21BE98141D9ED626E8B1F75EEFDCA3043B68FF
	9D1EFD9EA528B6C1929B6171596CC7FD310E5150DADD7754F5DEE3E6005CCDG05946FB7B0C739E4E788873FA27A45FE48C8149B3EE6B56606038F51BCDB6169FA0873FFEC4ACFBEFB257E3C461B734CF2A133B849E2F636F1BF15EA99FF08FC3572A1034F16B98AB781107EE3B7387E5B01266AE1B857B09D535A0DBA3A9EA69D53613966E12E636E1BE6BA523817EE72F1F300FE57CCBE6E28EDD2DE1CC9632A70CC1C49477DEE0CF15F6CAE633185749BF51E71EEBC060BACA1D55343AAA9FD326213DC47B0D2
	2B45E1351A6DCF55752C7BA23E9F67F1FD386CE3146930A91244C239BEE20C1668037C4FF13B404FB94B5FB04E4D3909DB26F313385FEDA91FC5F3F2F6D6F8EEB6ADF094EEDF4067667B3A2DB301A6EB963775FC4FF4ED25F1177470F1374318D50F70F1F75D7AFC630A377838AE98B36D91BE672BDA0DF31E7B8855664223107B9E65323319EC27DB7477CB55F0C0D0258676CE0235B42349261E4973F762CDBC63B8C30A02950896BAD8ED9C4349A17DBE384D752B8239AEDDF652ED4C93C18977B349251DC4D8
	25C2FCFF2F732581CF2E4EF729B7B65F12DE4ED716E8AFEDCC547DACC663534E9ED34E58630961DABFB9F1EA5DCFE74F41D61437B2B172FC9CE278B509116763FF3B425768BA68BB7685FEDE6A9F4279CA02F68640DA000DGBBG8E4E6639753BFE3C6309F99EED7243B151AFFABBA695EA7CDCA823DC282D3476E3B27A0955D6822FFF1F3E9E6F1E23F676E4DF905F1F46656B0A6023987A769D640B3C3752AF2CAEF8E4E6DFE3B8C950FE770A156FF70159FC3F5B5A063E4D0076DA000D3339EF1FD7F01F5D86
	6D32B9309F831C87F081A4G2C1C437DFBB0F0A11EFF6BF901EC1375570648A2177BE4460D6979F2DEDC3E2CB9E63E4C7E650731FCF9C5D45D114E84FA72E7EEBB77E76E9C6E4FEFF420BF4B1240E7G671278F92E7566746C6A0E4BE7CBB233EBE478C4AC3BFEA749BEC9D5C68DC611CD4F5A38CD5EA4EE530ACE36DE211D85102B5B74CEF724EE79913A185BF4FC8A5FFB916D1247A673E9F53F0AB1D989FD67GECG8100940055G390F71189C8CFC92AFA6D9D236498D003B84353CC9B8EA3A7E5DB4ED7CC9
	DC9B37BFE6463722E7FA721E1D4297A92FEC2E99DF7F2D69498BCD612B0EE89F19EB9617A7CB5E95CC6BF2B8CCB9A497462240E3F1F32BFE3FBE1747E20195E3D105F692404A39BC96CF5E1E1E9D9FCF610B34A34B54EFAFCFD35ECF634A4BB5153722F71AF1B105AFD2DEFE04BC3A1FFC191D0DF64D033D9400F06BDA90F78E5A36F95C6FC32DF877B0223747D89BEBE5EA9736C65E6BA16D3E885CF401E49C1AA2A35EFD607DG651440D074FB2361A173FB233BC36677C68F8F9DA0BC0CE13C85677DF306E25F
	ABBDBEE4F62F74AE337BE5956F58265CA1350ABEC9734B9B42666BFBB49A2BAADBEBF4BF7E4C9AEB2FC90E18371550263A2D271DFD731AC7F59B82617B98FE1D81CF755CBF37716FF19BF00CE41E373F23C477717794F39F67A9F88E8F4AA145A9C57A719F1403FADFB843B729128F5DC1B4790CDF9515FEE94ABC3C4A7829AF9A7FCE856F039C9E17C751EE19484719B70CBEBE2D7B787B6D31FC1C1ABC59472B12390F6BEC6833354911BEEEE0781C64C89F67F4F09FEF043EF373F9CDBF6B26D12FB33ADEFFEC
	0B25D77A7C49FA2D194F75FA1BFD3B4A1E9F29D7A543674E0F546B4B1DDC2FCD50573840DECAF17B57EF6DE7EBA2981402ECF3466BBF4D176D5DDC670B2A5CD0234FCF239ABDDE1AD573F8E9D32347CB179ABFDEC4B5FA3C38557871B224C6DB5BE46BDBBDC6DBCF6B789FCC421F5171CBEF9971357AFCEEED0DB51F998B221D4DC2ECFF4DDAC073D62C63E7B0FC2E81CF73D94C70584E07BE6B63BC4E9C370DFA7D172E6FFB3DC67CFF076D1B047FC8477F7BE53C4B760A23F2C8055AAF055D2DB473F37A9A1017
	83309D20A4054E6BD4E7BCBB0C6B32CF91FD605E0E006494F6B25E9F36F0DEB734478152G32G72D2386E7E5F22CD060A2406763FFDEDFC7FEBFC026F09C38C6706767893FC4FF3368B7ABCF88D6FBFED7BBC5F38BE5BDE382F7DC14AA01B3735F3BFA46AFE686860FED8257B613CADECFB13617D450E876F07FFFC20B2486697EC5C8F193A9FCEF5F2BF6469FEF8579E367D28617D8FBB9F3C9F329F288C32D9384A7D30C537F90C65050A0530CFGF4AD6478C799BEC17B66015F83883F574A75C4DABEFE6D42
	6835E2C50CBEF3DF9C55E5949B7A0AE2705DCF9DC9797013E63E3F0D00FE2B9772FC7463EA4C7F7A3987EFA86B6AAE48780D2A05129B1DF9E6B517E87CDB9672B34FF3EC4C4AC5765233G56C57C1B4448D17ECDE2556FAF97B67C487C1B44A65BDD0BFB9179AAE91161FE7D042310E496DC61B23321DFC8C55C2F2FD4C47861F0911D9D28DF1C540FFB5AF141587F7824FE547743C97C4FCF6AAFB27013ED6BBF60366DDC3FDA707E514C283F7B3A14CAB6F935E7653797979CDBEC5D1D4964FC5046737D4A5429
	71C279BE1165F0D5F458BC2AD7929663EF5F7EF68C3520B10ABB78980D0B270EC174425DB07DE67D5BC1D7D47A337770B5E1B12B212ED64608AB2FB686693A3E877DE6F245D7C37D2B0FE27DEFF6EDD5230FF1388D3FF1CE39822A616B2421F9FA3F77EA1BA65F6C7B66231AE4D68B2BBD97C7561EF399BEF7F1E46D79A77A5A5C84FD0DDF64F71D6FCC60FCE5C0FB9DC09E40F6000AA76D25271E64E7130A50A9A14E1D12CD8E34C0B16AB59C69483E5DD72677BB383FAC66EBE04D573EA2E4BE52E8A90D32869A
	1F2498F1E94F4C1C458578A47B1E91503E9468DB8D108B108FA0244159BE0D5B579D2AA27BAE3ABCF8E3ABBA5D6CEBA3B74E6E110643E8AC28A8F7B437717B422AB41E1732FB0CEB377264D553353B2367D30BAB0D74CDCC8B67258ACF6D9E9A67BBB70D3F7DAC7F2257D290457EAC0DBF5BB08E6977E18757EFA774916E75CE0E2BDD42EB3E5A2BD8E7B6C878AD007BFFD862F69C7A03954221774FDB8EC4116FDDC2F2D31704650763FBCE0B203F27985E7F2279416F7F9B9F288C0A1DA393514FE1A7A74C4FE1
	B59366F7A39FCD443E7B786DC4745F547C6F0471B7B5947BFF05BFF696F2E0DE36G14AD3517D68254AE65F11FBB719F9477A6B1FF51550F67C07CA5224A65FFD508EE6B1F8B71FF3F78CD813F5BBBE4D515FDB8BE473F4066CB15838CC93897D99CAA1EFE37CA249AFE2300716B157A981A66595AE64CB1D6304F35B4F6FDC47ED961465CB08234A9G19G9BGB6836C54FD64AE405F2F594581AD6BE10A109566A5C0D279427C24089E3FCA63FE43A3687ABEDD70C1B4FE7072E5A8072EBA86A0215844FEBDEE
	CFF7986DA97F92540BDF7AAC3FA35C5FE964BB8BBCADF178C85FAD8315706C855AF100F400CC009C004DGBBG4A5339BFDE09698FAA6A427EA01E751668F17322A5FE5C7C5042FE838273ED9B8DE8F7891BAC1833643E3E202447670FAC65BA2EA9E815039E3C0320BA3D1F656AAA3063E4912F5D3D8C57883883C5FC0CD56E683ABE670E2FEB165BBC463F6E1E9A635FF41B47788BEEE30C132ECD9DD375EF6DB06ACF38231DD369CEF6CE3573550168F63EB6905F4E42C1F3BB778CCE35F35F20391D9503C6BB
	89778BEDDCC53F38A57C7B03380F52DD887F35897F2EA29E455A46630769963D57BCAE55CD78C3A3FB4378CB924A07B67DCF485BFE7CC6386AA7C211AA6C61A5E5D095DE574BC7ECE8F522876D26FD717804F1DF5EB14BE97AFBC0D617AC23355735AC3CAF52FA3B91C03BD3G3FFA99DFD7DF11BF0C11A7BAFC22572B0A5E41FA3F8BF34605D1D58A6F8B3783B8FE1053E81D545797C03D878CBC7F87D0CB8788E121FB4E5B90GGF0B3GGD0CB818294G94G88G88G400171B4E121FB4E5B90GGF0B3G
	G8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9591GGGG
**end of data**/
}
}