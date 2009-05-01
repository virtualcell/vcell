package cbit.vcell.client.desktop;
import cbit.vcell.document.*;
import cbit.vcell.geometry.*;
import cbit.vcell.mathmodel.*;
import cbit.vcell.biomodel.*;
import javax.swing.*;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocumentInfo;
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
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.client.DatabaseWindowManager fieldDatabaseWindowManager = null;
	private org.vcell.util.document.VCDocumentInfo fieldSelectedDocumentInfo = null;

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
public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
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
public void setDocumentManager(cbit.vcell.clientdb.DocumentManager documentManager) {
	cbit.vcell.clientdb.DocumentManager oldValue = fieldDocumentManager;
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
private void setSelectedDocumentInfo(org.vcell.util.document.VCDocumentInfo selectedDocumentInfo) {
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
	D0CB838494G88G88GB0FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BB8DD09C57F91A14B44A0C62489E4541296AD195254209124893E5ACBBEACBDACFAB4744D50864B89DB423A6A4F1D25C5A3512500CA6DD8E901C0DADA463C40E3103A345C28291C0D8C2A7E4F1BF405DF1C8879CBF31D14A42ADF0E2EF7734377C1CCC6DFE5FFB7B76760E3DBB890F264CFCF37B3E77FD6F7BF95F7B5E775EAEDC4DEFB71417D8EAB82E241C537E4E15F01C7B8C47FDF2447BA906EBBF
	11EF658CFFEFG9C66DA55B278F5C3DF4E9F732D555C5704CAE80FC1BB826DDF4373CBDC2ED0598A8FA9646CEB61380A67E77FBB3D1C1ED9A6270234B7F7D6416F0BG6300435FADA7D07E5FF59E5570D99A1EC10905635654A34D3FF456EA3827012692F0EE0031FACA2F42AFD5602336F60DEE6C4E9CEE7FB7944B8BB80EE1CC863BB55EE26D370E7BC5490613A82B54DE2571B4C3FB86G0DAF659A96403DDC07FFF67A6D4909105AB4BFA908E253249892A435A9A8C4E7D5B9E23339F8151F602342C8C88A4A
	8BFEDE92C42E76940E5BEB2FA463F0DCA10C19C3F051BE14B58E4F0BGEBCFB13C6DB2620B60392CA15FDA5B406C3BF822C353FF743D87385D2D6132C3A96CEBEB20F60DB5446DF3C9FBAD2F1039781808BE4F063E9DG3BG768354B466DB5BGDEA5B1F0476CA97835CF28CEB99C16A5FBA4629003CA1F9C94C4F8365920C7438D881332920418A0E3DB6D0DA67A24834A776BDB6623BE49B94366FC4B401738022F5CED29B071C9C14EFB96DFA375C530B16E93AA5B1FD2F6D6E3E2CCA64A9E610C32972547CB
	4BCDE42FFB777152ECED1CAD8659850DB857BFA2B110874FA5DAFCD07CB384DFF7BA0E277199A3F8ECB7C35F3A53ACB6C6DFE43A784AB6F255AD1FB675C3765F2F31941F26BAD4B7A56B627663589DCDD4664B3AAE6F92FC4400273AF412B84576A668737FA15FCA63EB2D046B75G340FB667DB1B815CGB3GD9G594DAC463EB37B071B0CB1BF2F280E39109894940E722E95FF853F42222A709EA92A725224B04CABA1FEC294221CC6F30F08BE18C8603B17447EA4486891A4C1615510AC4168FCD8D085A5CA
	E9B60B0711C691C8EFA09496C0C0B84231F13FAC3E843FA29FD507A2C1DE956294347F819167E41E9F0691E1GF826FE799B997D359D6C5F8530CF77C3EB98476B9594C897B61BCF16E767A2813404EBEEC1BFCF135C319F682BDA180F9F0CA0CE05761A96B6CEA8FC44B00EABB4096A73CA0C2D31277AB0A7EFE8E1B1736F23ACE6DCAFFC11EB5C73A95318299CFB3CB43705464A93AD49EB6767E4CC540B36BF689B3D45F5EC048DAD666BADB1D7E4FFA89925997A29FFCFA8B8D7DCAB64DF000ED66663CD61BA
	9A338EC873ACCA2FA890E0C2B06EF12A67E5FF3D66CF14D92E690F3EFF9EG7726C3048E47E5367104A7G7216GB68164816CC7BD4E64DBDDGA240BA009DE7184F3BAFEF3885B9AB755B07852A6BF5226B4D6879727F232EF42EBFA321BE42BCEC71AEA1A2C8C1F808EAFDFFA5215EDA7E1912F51E4D849F01FC650484915571DF1450AEDED51550441CAA8C8A2A9A1226637DDFA53222F39311F82AE332F6C868ABF9817215CE7FF589577A24ACC98289BA1DC56B7F8712EB84D1880352CEF9CED2A97E919957
	6B2C9053F25DB765039C66AAF18E73A62DF53672B89F91A59406D046544B3535E19EFA5FFE546023C208BF2C59AA212F360D651405E3AC279C3FF19F177BBF910B591E187D39CFDA165A68DADF5B964FA9D4273F0D186B7EB049F99ABECAF5ED0BE02C1855DDF49D0FC7F12C2DA063E10002B6360EBB2FE3BF4B155A82F64A22C85D297B7934D20D73A2DC1F93E00B41245C2B7B2ECDB91254B72C791176F7A868C751104FE75EC2BF7E051F55337B5B288F2BGDF8F6083F89D7014CE6EE7F45B5E0A536581BC1D
	C057B140682A4E46690E43337BAC25ABBB07F456C1C697BCCB65ADE974BB4E52B920F45F54697C67283C19F3E6F4F78698DD4EB9AAAF5714EEE80851F16DD093GF434D33A25F6A35DAB430CEEC3BB9DEF3BA95D9E1D6E09F64D9F26F4F70EB03A6A73545E7A7309F4B4061EBD09F1CDEA7BEB7CBC3F5894DD0035E933F9C9A0517A7DC93287A33D7BBC2B53EB3AB036D6C1BB9BE06BF936863E75822B53574D66F081F9CFE92AF3484E73D40F227371B5D01637D0CAE37773AA4AEC34013FECACEE9FD00C3502BD
	9A15A7C324CC51E2EEAAEADCA394B79BFD8EFE27C2CAD475C092DBE471F9BD0A7AE29D2321353A6EE1BF2BB17DA0F7B5C0A921EF68479C9514A767F070BEDE6227A1A3515C701D9EE3EE483551F9AE065F2A8B0CFF58141F7A6CBF6D1D9AEF5E17B6F25BAB56D84C4EB6B31B33AD47AFD0DF79AE44FDF63CF02F259D006A1F5D4F74DF8FB43B81280E8FFA0CBAD6F5D09D5D70BB5641F4BCB6E026A39DD798E0632AC03F25030DFBF7EFC2DE54462D3D88356FC5B66E53E946ED9D2CE23185742BF41EFB8671984E
	130454C4FF7C02B2A5ABE1A1D80F115A44AF28CDE4FFEA32136EAEFCBE49627AD855F914B991D084010432BDE98CDF6AC1AE1C9C6658B9E763CB4639697860CF26F3337A500CE57BC5BAA77BAE466726FD4FDE0BEF8F1B1B1F6936B63E1DEF8D3E4DEC3D9AD8292DF45C0782EC5C75B066EEFD5CB706BE5C386F8F31F12BAEC17DF5094D79A3DD46B9DF3AC4EB739C785DF109497E8111BDB943CB53C2939C90D441C91E29E0AD4DE832E9CF627C1DFA994FB813F30A02950896BAD8ED546364D07D9C43CC3F5D28
	C3A713FD5FB0660928007B199054CEA224D221FC3F5479DC4033247389A369799E9AE1FC5B0027C867FBE3B4951FF676D8F646BEEE0F577A6D7665F5BFBDFB2EA6B956E7CFBE9F5FC570E376647371EF3A589A9521EF171D1D17BEB10F73556600F3AA408C40AA008DG398E16DB7FAB0AF7BCE94F239E890E0992AF8EA694EA6CDC2822DC282D3476D7D47493292DB8D67FFE431F691E234011688B4A77C4C6BE4E1948C7E36831F964CB3E3752AF2C9CA11958670AA7893A5F9DF2337D6E28136DF787BD68DBBF
	34E7GD6B919EF7FED817759ECE8EF834883F892204A857BAD004F457C7B761CA313FF6D2CC08E487A23B33948E5BE3937FFE53E5C1211AF68B273654E5F1CCE674BBEDE1DC9F6A650D3FFBE63E17EDCF2B1FFFE5F0B7E5C866DBC00C297BB4F4D8E2C4C2ED8C63E8326F6ADAE1CCAE7D70FA00785D509990C22B6BD564BEC2A69E2B6657A48FA05F690E0290B5954BE182C5B3A2EC4DD4CED6ADE4677FAD2FBEB52B8DA5D9F43185C89FD05G87G0EF6C39D85B08A30544DE2724E390B19E212A465006C841C9B
	EAF913F054F4BD30C29B1F8BE432F1FD3799DF6EB23E1B1377D8C6F95BCC65E58D2DCC5EDD4B781A125AF95DE6F159D0799AE7DA1743E1EAC288E2ACF2AC96CB3A757B75EE960B2F77E0AC5676401E8D606BE131689EDE199D434B78126D88761871150D2CCC5E11E5FC497216CC65652ED05EE399652DCB12C76FA74B9C687BAD50178BC0F17F6AC4DCA1348F68FE3F238B6F9E6A5C2C1DCD5AD8AB53F6DDD7723D9E523E1804DBA9D09967174948F49FF8ABC0F34A5D73296F0D7EF25E7C5E6833736677C6774D
	9F26F89843F88B4E7A6F1FCFFF2F74603C593D52EB446E9F29F847366C8E69B29F96B43F3C441574DBB59A3FAA7BDBF4BF5638536DB56DC9736603B62D5BEE109AE25CCD6BB6840A7FB302978DF8DA4FD5F9587B38C59C434D7276CF97D27B784002390FAD0BF88E0F4AF34A24106CC76B62D13DAF1E61BD2A90C6F7D01AA304DF6515E9E159BC3CC278E9DFAA7E5FAD62FD50C4A898D234DBA66A63BD89BEEE55FD7CB3CFBA9FF7FA92FD9C70B09F0F74224FE6BC49BE9EA7F855136C639FFB190FD7C1DF210755
	749B870CFA1D51757AF6EFBA3D5C5EC43D463CCC2FB6726EAA62CD566BA241AFF913757AC79F53EBB57495815038FD64D707481A08C605A8591C717ACF7325BC48F46ED2E5E70BBEBF57E2296365FA4CBCDE96E22963657DD866F879D8ACF53C5C914B9CAF1F0E25DA5B5456B381232D2DBA7E678978B3BA7EEBC3C6FC1BBE1F873B534D273FB755596CAE323F86FB693CD56878FB89FE49002773F91C60313D8E7A0AFBD91CFDB0EC546BADDD5F8BA3C67C5938FD8978F3BA7ED5875EE50BFCCC1ED3217673113B
	9587BB270FC1DB85D88F3055C74F6B344E38FF815765144207413D0391E1125B54073C45CE46DB8D6D63GAE00A0C0D69F53DD3A04B699AA12963A7FB538597ED754477644BB882E7AB20CF5196DF92BBC1CBE8F9FB7BC9F71FC18F7DCB73797B65CD6995466973C4C8F4D17199FA2DE6607006E072B3DF15B278C4F7F613D7DFE286C3D7DFE78C9AF7343386E07BE9F73C3166E87DBDF5CF62761B94FF77B7D70035BAA035A7CDAB77343DA5D66EC1297F6C03BG003B42701B883EDEFB67016F83A87ED48F5393
	E95978481BCA760EB4FD663EF06AB2AA8CFD65E978EE250E2479709D875B5F6A400E51ABAC9F7D3089733FFE6E419BCA1B4DA163BBAA9FCDEE744C3311545D483F768ABB736CA2E36E0476BE0062AB6C1D44621B6C1D44163F5E4839E74D5FC96C8B7C2F255ACFFD55660F5F610ECAFBADD4666BAEA6B3827DB9FE4479DCB5C9FEB8D6CE4F8E347F7D04FE5C53CEF2467E2D897D286F69847E6FA574179B7829ED0F7E1E59F660512D1C78552C145FFDBD2D59D4532F3DFBAC3C27A4775577CA477B49DD2A1B65FB
	DF7F72F821797EF3A4072B7CC4A0240AC2CEBFFE7BD60A22B8BA46F1AF9B2348E48CCA7F26A5CEDF62E3745CC0AA7ABF21E82EFE0054D05DF5E9622AC69B036A7A68D5741B4995DF8B6D5F7AA6567FE657D6AD7A98477A719D6732AB209636CE469DAB7B5ECBDAA15F66DB6623B549C6A729BD8712EB4FAF907C52C0F26D79383EB6D7C3DF51G3B6B341761FC7987732DA2C09640FA009DGCF8C3233096DC6B31761CEA9A0C7FCD00C0A06A39D356F18EBE55F41FD3D022D01ED8FFD1E5B7E67AE4B73A956C051
	A00D112AG7D2D7F7EBDA578CB6D5BDDCE5E05C25FA8409240BA009C005CG336FA3B79A29FDDD41905E587213B364EDA3B3EEB8A4AC4451D8D0515CB1235FA96F8E303C34732AF16D56B5DCB1DD3B45D7EFD82A06283E4DC3713C34ABDAD7CA477967F70D5FFED6FFC634142430BF2871C78C63D07D3CDE265FA668233893BE06AB98E2B5DFFFB75619CE815F85B07FAF884C0E5AAFFC162B7DED2765F08A79B543D4EE67F0DCFEBC3EF31C1C7EFC3F613940F17B777F1F5ED699B4F63CE5294FE17DE566673011
	B2733B112BE5696FBE0432545F5408E546EFEAE86C7F388273258A733296E09B40CE0002E196773FA87B9D0DFB13186F8ACE63B9903FC4D4197C3D6529EDADAC4F7C7D62D365785EFEC2D6D5B90C63B37C3365B8DF2A9CA1C80AABAEC79FA92169991512EA78DF16E37C0A4294C153F98EA75C5D9403FDD5A3696BA36A1FB3D64C8DF5C05B81B08E30883086E053087E5D70C17CFEED183F26E5BDCC9132C23C84C8DA3E90BFA9FCC8D2693843E826266F378F7EBE95BFF8598B65501509EBF0D88E7053FA5CDE71
	9A6D598E3A64E5300772B57A0CFC7B00E77FCD79611F24BA782D9C051A94408DB08E20822C8158G30FD147963E8C5BAFF5022AE6E8F4A73CCC56A38A92E489CB7A515649B90186FC0AC225DA59C2C441CA5CFCDC585BDBE1F2F2C56F11E28DF0E06708E025669D9A4D76F86BB9A4AD96D7EF102AB825C61F27D9BA7EBEADD1D564C3A7A2C66B1BEE0DD9E63A3D673983FEAB546B855F54A3BDC7FD92FD1FF0AF37A165339FD4B6D4CCF73AD60F7254CF6FECFB23773C9E9391DCFC966F6BEAB996D5462C19BD751
	AFEE35F5AF61BECA6FC25857A66C3D8A7FA656B6A189261B97C7C2C1F50662EB497D2606F78BA89F5A74FFC2FE2860BB42ADCFF165AAE7E5A5E5D4651E534BC7ECE8F5E2886D267B62498F0C7BF271C7A7CD3F87A4F5499F695A635E096F0BF43D1D1CC33BBBGBF7A8EDBD7B175F41ABCB19866C5D16545D93B94441C610829C2FCDFE81A4371230CC66B24FDEF4D215E578CBC7F87D0CB8788CE9AC93F5490GGF0B3GGD0CB818294G94G88G88GB0FBB0B6CE9AC93F5490GGF0B3GG8CGGGGG
	GGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8E91GGGG
**end of data**/
}
}