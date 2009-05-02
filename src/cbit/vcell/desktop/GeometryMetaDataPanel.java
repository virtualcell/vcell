package cbit.vcell.desktop;
import cbit.vcell.geometry.GeometryInfo;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (3/29/01 10:39:36 AM)
 * @author: Jim Schaff
 */
public class GeometryMetaDataPanel extends JPanel {
	private JPanel ivjJPanel1 = null;
	private org.vcell.util.gui.JTreeFancy ivjJTree1 = null;
	private cbit.vcell.geometry.GeometryInfo fieldGeometryInfo = null;
	private boolean ivjConnPtoP1Aligning = false;
	private cbit.vcell.geometry.GeometryInfo ivjgeometryInfo1 = null;
	private GeometryMetaDataCellRenderer ivjgeometryMetaDataCellRenderer = null;
	private GeometryMetaDataTreeModel ivjgeometryMetaDataTreeModel = null;
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
	private boolean ivjConnPtoP4Aligning = false;
	private cbit.vcell.clientdb.DocumentManager ivjdocumentManager1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private boolean fieldPopupMenuDisabled = false;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == GeometryMetaDataPanel.this && (evt.getPropertyName().equals("geometryInfo"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == GeometryMetaDataPanel.this && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP4SetTarget();
		};
	};

/**
 * BioModelMetaDataPanel constructor comment.
 */
public GeometryMetaDataPanel() {
	super();
	initialize();
}

/**
 * BioModelMetaDataPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public GeometryMetaDataPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * BioModelMetaDataPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public GeometryMetaDataPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * BioModelMetaDataPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public GeometryMetaDataPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoC1:  ( (bioModelMetaData1,this --> bioModelMetaDataTreeModel,bioModelMetaData).normalResult --> BioModelMetaDataPanel.expandAllRows()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.expandAllRows();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  ( (documentManager1,this --> geometryMetaDataTreeModel,documentManager).normalResult --> GeometryMetaDataPanel.expandAllRows()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.expandAllRows();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (GeometryMetaDataPanel.initialize() --> GeometryMetaDataPanel.enableToolTips(Ljavax.swing.JTree;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3() {
	try {
		// user code begin {1}
		// user code end
		this.enableToolTips(getJTree1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (geometryInfo1.this --> mathModelMetaDataTreeModel.mathModelMetaData)
 * @param value cbit.vcell.geometry.GeometryInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.geometry.GeometryInfo value) {
	try {
		// user code begin {1}
		// user code end
		getgeometryMetaDataTreeModel().setGeometryInfo(getgeometryInfo1());
		connEtoC1();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM2:  (documentManager1.this --> geometryMetaDataTreeModel.documentManager)
 * @param value cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.clientdb.DocumentManager value) {
	try {
		// user code begin {1}
		// user code end
		if ((getdocumentManager1() != null)) {
			getgeometryMetaDataTreeModel().setDocumentManager(getdocumentManager1());
		}
		connEtoC2();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (GeometryMetaDataPanel.geometryInfo <--> geometryInfo1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getgeometryInfo1() != null)) {
				this.setGeometryInfo(getgeometryInfo1());
			}
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
 * connPtoP1SetTarget:  (GeometryMetaDataPanel.geometryInfo <--> geometryInfo1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setgeometryInfo1(this.getGeometryInfo());
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
 * connPtoP2SetTarget:  (bioModelMetaDataTreeModel.this <--> JTree1.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getJTree1().setModel(getgeometryMetaDataTreeModel());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP3SetTarget:  (bioModelMetaDataCellRenderer.this <--> JTree1.cellRenderer)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		getJTree1().setCellRenderer(getgeometryMetaDataCellRenderer());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP4SetSource:  (GeometryMetaDataPanel.documentManager <--> documentManager1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getdocumentManager1() != null)) {
				this.setDocumentManager(getdocumentManager1());
			}
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
 * connPtoP4SetTarget:  (GeometryMetaDataPanel.documentManager <--> documentManager1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			setdocumentManager1(this.getDocumentManager());
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
 * Comment
 */
public void enableToolTips(JTree tree) {
	ToolTipManager.sharedInstance().registerComponent(tree);
}


/**
 * Comment
 */
public void expandAllRows() {
	for (int i=0;i<3;i++){	
		int numRows = getJTree1().getRowCount();
		for (int row=0;row<numRows;row++){
			getJTree1().expandRow(row);
		}
	}
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
 * Return the documentManager1 property value.
 * @return cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.clientdb.DocumentManager getdocumentManager1() {
	// user code begin {1}
	// user code end
	return ivjdocumentManager1;
}


/**
 * Gets the geometryInfo property (cbit.vcell.geometry.GeometryInfo) value.
 * @return The geometryInfo property value.
 * @see #setGeometryInfo
 */
public cbit.vcell.geometry.GeometryInfo getGeometryInfo() {
	return fieldGeometryInfo;
}


/**
 * Return the geometryInfo1 property value.
 * @return cbit.vcell.geometry.GeometryInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.GeometryInfo getgeometryInfo1() {
	// user code begin {1}
	// user code end
	return ivjgeometryInfo1;
}


/**
 * Return the geometryMetaDataCellRenderer property value.
 * @return cbit.vcell.desktop.GeometryMetaDataCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometryMetaDataCellRenderer getgeometryMetaDataCellRenderer() {
	if (ivjgeometryMetaDataCellRenderer == null) {
		try {
			ivjgeometryMetaDataCellRenderer = new cbit.vcell.desktop.GeometryMetaDataCellRenderer();
			ivjgeometryMetaDataCellRenderer.setName("geometryMetaDataCellRenderer");
			ivjgeometryMetaDataCellRenderer.setText("geometryMetaDataCellRenderer");
			ivjgeometryMetaDataCellRenderer.setBounds(461, 242, 180, 16);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjgeometryMetaDataCellRenderer;
}

/**
 * Return the geometryMetaDataTreeModel property value.
 * @return cbit.vcell.desktop.GeometryMetaDataTreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometryMetaDataTreeModel getgeometryMetaDataTreeModel() {
	if (ivjgeometryMetaDataTreeModel == null) {
		try {
			ivjgeometryMetaDataTreeModel = new cbit.vcell.desktop.GeometryMetaDataTreeModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjgeometryMetaDataTreeModel;
}


/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.BorderLayout());
			getJPanel1().add(getJTree1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}

/**
 * Return the JTree1 property value.
 * @return cbit.gui.JTreeFancy
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JTreeFancy getJTree1() {
	if (ivjJTree1 == null) {
		try {
			ivjJTree1 = new org.vcell.util.gui.JTreeFancy();
			ivjJTree1.setName("JTree1");
			ivjJTree1.setEnabled(true);
			ivjJTree1.setRootVisible(false);
			ivjJTree1.setRequestFocusEnabled(false);
			ivjJTree1.setSelectionModel(new javax.swing.tree.DefaultTreeSelectionModel());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTree1;
}

/**
 * Gets the popupMenuDisabled property (boolean) value.
 * @return The popupMenuDisabled property value.
 * @see #setPopupMenuDisabled
 */
public boolean getPopupMenuDisabled() {
	return fieldPopupMenuDisabled;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
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
	connPtoP1SetTarget();
	connPtoP4SetTarget();
	connPtoP3SetTarget();
	connPtoP2SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("BioModelMetaDataPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(379, 460);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 1;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);
		initConnections();
		connEtoC3();
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
		BioModelMetaDataPanel aBioModelMetaDataPanel;
		aBioModelMetaDataPanel = new BioModelMetaDataPanel();
		frame.setContentPane(aBioModelMetaDataPanel);
		frame.setSize(aBioModelMetaDataPanel.getSize());
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
 * Set the documentManager1 to a new value.
 * @param newValue cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdocumentManager1(cbit.vcell.clientdb.DocumentManager newValue) {
	if (ivjdocumentManager1 != newValue) {
		try {
			cbit.vcell.clientdb.DocumentManager oldValue = getdocumentManager1();
			ivjdocumentManager1 = newValue;
			connPtoP4SetSource();
			connEtoM2(ivjdocumentManager1);
			firePropertyChange("documentManager", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}


/**
 * Sets the geometryInfo property (cbit.vcell.geometry.GeometryInfo) value.
 * @param geometryInfo The new value for the property.
 * @see #getGeometryInfo
 */
public void setGeometryInfo(cbit.vcell.geometry.GeometryInfo geometryInfo) {
	cbit.vcell.geometry.GeometryInfo oldValue = fieldGeometryInfo;
	fieldGeometryInfo = geometryInfo;
	firePropertyChange("geometryInfo", oldValue, geometryInfo);
}


/**
 * Set the geometryInfo1 to a new value.
 * @param newValue cbit.vcell.geometry.GeometryInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setgeometryInfo1(cbit.vcell.geometry.GeometryInfo newValue) {
	if (ivjgeometryInfo1 != newValue) {
		try {
			cbit.vcell.geometry.GeometryInfo oldValue = getgeometryInfo1();
			ivjgeometryInfo1 = newValue;
			connEtoM1(ivjgeometryInfo1);
			connPtoP1SetSource();
			firePropertyChange("geometryInfo", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}


/**
 * Sets the popupMenuDisabled property (boolean) value.
 * @param popupMenuDisabled The new value for the property.
 * @see #getPopupMenuDisabled
 */
public void setPopupMenuDisabled(boolean popupMenuDisabled) {
	boolean oldValue = fieldPopupMenuDisabled;
	fieldPopupMenuDisabled = popupMenuDisabled;
	firePropertyChange("popupMenuDisabled", new Boolean(oldValue), new Boolean(popupMenuDisabled));
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF6FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DDB8DF0D4D51626E650255CB893EB239BFF2AE495C6E60CEE5CC14DDA682003B368D0A3E6C119AD2CE1B46B62AC4E32CA4D32D32CF5531DC0B20240A4C0000CC4858D92A27F044E8F1DBFA0A4A1A16904C6027012FEC99ABA5D1D572F73C782FB4EBD6F3EFE693CCEC3E65CD4FD757A1EFB6EBDBF775CF3FF5E8B4B5EB1B5BD55124DD8EABA537E0E26B2B665B8E3AF8E5F7F3E2095FD111C498CFF1FG
	B632EF0E2741739454AD1513B3B730C753B3205C8665EEA8EF015FD96C653D2C8CFEC410332814312B9FFD7A6678F2CE8D8BB9EBF839AFEBADBCBF82DC869ABE933EC0795F4B4A516813B53AC02A85EC4AC71E9FE665EA346540138134AA00B31F787D70A485EE3395E9FCEDF74DE0ABDED6AC6B309FC31F82733536AB34E79E7BC36A833BD1D6B257935B94C05985C0632D6C4FEB8B61D952F8352B2C235D2D9676F5489ECF21CB8EDCD5FDFE1B6D246C6B11D5E530DED625AAC9159AA52F6CE1AC67CB6CF3DBF6
	866F0331A5505FB4CEEB88221CB6782D82E23E94746B03C8DF8A3F591E644C1CBD423652AFCAB45D1D571EE68B8E74242D0FE05B3EBDE413F3CF48B65715B7ACDB79B8CC99C1FF47C1DD92E09EE0B9E0C3C1F266BE40F6BE7E5F9F7E921E656DEA252F274767AD777BABBCAE255E6712BD705BE6039A0D56A4F7789497E3B4460B328ACC74998F546E6DDBEEC7BE19F1D00CF7C25363EC710F6F312CB17149629957AC278A4897178BC2BEA159BB32A3491ED4B0BA9EC74BBE470C32A71FD9E8CDB711BD35F821B5
	CE6BE706C17652829C6B1F7298188B3F315D07B86E1C7EAA27976C8D51A9B6DBB89D4BF5D01738D7440673A321CBDD5AC3EC437E3FB775C37C8BB1166CC2522120B0DC1783C358373310E4EE51F5B941693186BA69B21947A91667C25D642F12B3A93E6E3A01F3553EAFB9338D2082E2G89003900457BC40CAD9A7E6AA6E32CD1D2540A205B631295C6ED6F9A598CCFF9C0D5249AEFC0153C9DF22B243825F60F9CE09A4FFDA3680376D16D9E64315F81B2CE4ADED911D4374F8B3DCBB0F3E5A5C0BCB3C6B6A20F
	A273DA073BC78681BDFEA67ACD9C490227C78A28ADFE17244AA18E2ACF9A41B16913DA21C768GFE13DF1EE3682FD5E0FF767E644C1D7B059F8EDE477EEAE58572054DD66773DD8D7A9DE889D377231F0F705CD1843F9B778B9FBF130A34A4A84F577B39F27DCF06FE2A5C9D283E248C0AB976D29092B9CB817E0C83A09FD087D08F08987A1DD344D0553A47D841423BCDE3A83BE421B57600964F874267526BDC866AB99F6A1683B69C043CF2D02C819702CEC3DFC207C8F3BCC353EF3CBC00760C4EA56AA899EB
	34799A29FD3AB61F4613E1B57CA67F67DBF1AC9B41AE8FA066209803695773A826AB2400AC2238C101G14DD2191A1BD0B068A8C728F82F6GF03DDA4F6B31BF529D65EF526D2B636D26014CF90095005CC3309E82BC00B840ACC08AE06D61644CC307056F6F98CCB458134EC2396CC393391BCC7C30A30A1F848E5EA45F4D2F8F7F2C6B3E49B09ED9867F99755FE2524FF8B190AE6F2F25BB45447DB7D0AF39CF762AD532DF763A60C7C02BFB68866A256531CE1F5EE686277BA16FD5C22289687447EF20FD122A
	AA6E7620AAB74B2A6A76F6056A1F62328241F6FFA8E58AD94F5EC0DF797AA16F697CAF5C401C516173FAE59E1CFA932DFE8E4FD932C76E8125ABFDC12FCA7417996E6D2E4A03DA4E7CA5DB4DB067F90218FFE93F14A861387895F78F04BC26F016F09873D9FDF60E41C7EDC043FD5FA4F8469D9639287FB3110BF68E7D104D3E6437182D2D71533FEFD9F11866FF4611D0AEA21DFE12EA2E7B0BBCF7EA74G697AABAF46D5055B47171F30AD9C0F37AC7637AD98E3DD5AB68F07EB3FDE7733212D06BA515E2148B2
	6FD067FBFBCF2E89DFA5ECA41BB0B094D9A13ED9839B606972F584D149FA49ABF5494AFE4ABDD29A5A158F7616G9CC7C46EA9677BBB117F3524D36973F8E8E821AE6E887A7F633EEC4DFF9EA8CFBEC27E47F26ED12CFFC12FC7AC9ED37F3BD1752E23D4BF831E547F2F86C47DAC20ADB89AEACFFA96DA5136005C9B14E15946052ED60F234356BF055535EAB1C57525D60CA90FE10D5CD904B27FE6C81C8FD20E525A336AA82DBFEB0BC876AC4EF76E1A6073C399F36814A252EF2A46F76A98727D0F4E17F10C6A
	730ED13FC5707CD8677B6930600B5378920E51FAB7D763A37D6E9C91FC0B8C75285F72E3A4374806FCF92902EF2D0DF27ACEDB083FD037E3014EB7D9638B3703787AAC026F819B554F541E73ECC6FB77D9855FAA9B2D59683F6CE2D8BF0A0D762E52796A0AC9FF41675278480E1F27893E01E2DA0FE20B09EFEAB159C1736E7D5D188B78196A0A54A78D94867AA11F59EC27795423335353FC7F037C0945620C745696FEC602B2AB01B3D209489B2F2E93E724382BB35865068556C8673FFDA5E43F23A414B7624F
	75DBE90E5119B88E6A2615087915E4B56E534A8381DF071BEF9135586CCAB3669522F9527E884FCE3792D0EBE0FE8F08B8D652D0DF5CC3EAE4EDCFDD5D37D64B95C9A0F7B9A03F94695F6943FC0BB1C8F6F69C94F6468ECDE7176F3E4F72E184BBBD25E4DFCCE9484E958B8FD9497FD7379CE037F026B5A4B452E36404515F33560FC454E30EA6BF4520C76C173F5774E82ABFFCABFA9893A693E76CDD7DA2CEF616A5E712BF592070E79B50624A04BFCB87511FB8D709AF111FF1306DBC60C9BB1E1CC9F9F84BB5
	64D33B5D81AA9F3F06FC46B5007C705847F64D4EF90FBF44B2245BACE6E77318F73AACA54749FE5771109FE63F7A06E58100F419F4CD6812GBCA947457DC359D924FBFDCA0F844687029E1568DDD7B01F4A83FE496BAA873F787A83793BDBC92F07320CFA155C7846D42FA96B3BAD39F652274A9E524B89BAF9F43DBE9D963E0C851E39F66123783EF01F907D31E161BB52653DF223AEC9EBE2CCF569FEA45E424AC9077C7210AED5CB5E30B81788DD16693A8CGCFE2395025F9907B8CDBB7353537FFC468C3FD
	BCB5A27AD88A6D73AAC49F65574D7A20B6B529220D9378E3AB4458E41761136FFF18189B4367053D13664DE4B9D9F71A5A0B18DBC1F6AE2F88599B736BB7ACD3FFAD6C7D5FD49193764A644C01CA91378FDA84BD91E8CBAB058D2B7979446DF52BEE49639E1273F7131E0B8F6F44D8716252697079BC8E37BF10DFE7C83D0527F10E976FEE257E4FDB053DF9D54919CEG6D815E719B77G31C0CF82AC2E9272D3F83B8E43B4257EF625097E1683EFFE3560CFCCC37DFA343D9271AE57F9AF83DF3C4EFBAE9D47D1
	D23A76D379C63A98D7BAAB4C9AF31FB33FBAF4C6CC299EFB1E24FB0EF3B9A8EFE5B55DF3D8F53A446999A7C2F4526F1DADC23F9C289BBEA14E66BF4B40B5ED8914D78132CFA6E796G6AG1E13E2AD79C39A1E11463D7B28712A3054C81E66D11BF9F187110EF2E17F2D15E7269B630F7CD05B07FE18F2F27C73F37859E7A3BB4F5BCD3D05F6946F3B3FB65E3D26EDBCED31C6087779DA5FCBCE06621DFCF74106F115F78A7636G87408308B9A57CF6ECF5C1B43F09FBA04DE1546F734558EF8274B39730843092
	10D3A37A7D6E6A2863D1BDE0BA9E6433AF7B4D6E900BEA483E3A1A313E24F62B87A7B6C6590337BAC6546E2921F0BDAF0769B69CD626F6F346347BB82CBCB52C9D79FDCBA96E831320EE81E099A063B44C8740A9006734707F2B2B0B237A9F8F508EDFB9BF4CD4061FD8B5BDEF3FB6B17B624634AB8CAB471CB6336FFBE5E85FB4281B8DD884D881482905FDB340D9AB6C7BF4CD69CD5AD78934D3F03E99EB2150B5FFB85ADD39390DC722369B28B533B15D0EB646C2DD82E08EE089A02D8E56CCC0D51D30B13826
	A21A0D6562628246D17CB4B11474FDEF02F63211E86D5A6A4C5A4D0A5A4EDC5E4BD15B2926729AD2A7A66FE254F6B1F5E663785DF29E2BD0B79B3088308210D38F318AF0560BF1FCA97AB86AD766B7B30ED64B44666446B16D4267243F5E2C5D74894A7BF1D4F9D3CC65DD32CEEC9CFB22361B9AA60F76F0FE3E4F881D153A02EEB8B063366D04646D9804BA3AE70B4F893FE743FB79A468F39E004ECCF93DF8E642B3DBCA3D38A75E13B6B1FB0EC5ED17FB462C5D6FA7A8AFADBDDA3BC3E17268FD4A3F707B04BA
	283B8CA0DA62E624CD067283E7049FAE66603EF236DE6E62E55C67D279BF37B84711BD7B4C4D2F4DE6A09999B96349307E857DA3E83E3F129179FE7A358C737B694599667753EFE6ECA4BA74E1FCEBA76AFF1BB17E7D75FB99E67757FF66363E464FBACA502B3AFBE4AE166603BC005F9BC0D6EA8AFAA9E52164C078FE1009BC78FC850E6302B3A25FCD49BC4046663BEA17DB75A9229307ACA98D1D1DFA32EB764B9DC2678EDBE47FDD31197B4FEFB377DF1FEDFC7FDD370D6F3F170AA36BB23F58DC17570A4DF5
	D9DCBC3EAEA94511F421B9D54E6FE316031FD90318E7BF689773ECAD50E8DCDF5A3C1E4B898464G5F84618D0FB6667F2E623D7C187793E7E075E162CE7D5D21CC0D27DA75556F57757B24B432AF72CB4DFDD1D80A770581DFD06910436DBDDC1A235705963B9AD56EC1F508270C37D761F0AA0F71D7B5EFCFF5115A1FA90D64D3327559EB86DBABC3364617C5367541B2F3DB2717C536B521AC3A2DCF16C536F5D6D9F4DBDFAC9B5F56CB434256C655579832F53DBD322D1B6D6636665AA35B7A19BD3A2D857648
	369E34C73735440EEBEC3B5B65D63437B1E4636E91230D07F45B5F9FB14674811DFEFB6A07023FD3717500D7287FBB4BA37BA42E5C5CA77715C77649BF14C777490F4AA37B64097268BEF9265C5CA7B60BB146C336AF9EC5BF28536F319AFD08F45A5B4C4F9177417B9A44FB03C3FEDCC33A21BC89905FA05672DD3D636DA59201EF8EE091E09900B50A3B2876B4236C102EFB53C70F53810D7E5BECFCCF6A1186FDC11531C90D28D36AA631CF4B06F281408168EE247D9A2DF1CF74E3FF1D306B06A942971F3633
	7CBD4BE651F6AE70AF862C8264C2DD49D921635D1F230D74C6C5444B0B956B0CF3EFD4BEF576737BB48818E9E7713B3E27EB202849FB36DF48DB8A65D5005CA61085E8EB923EDC18936E4B612650DEA836E96C3E086E59E67031195694FE7F76CF1CBE33A97C7E4D669777EFF320AE3FD92CB1A53DE20D0951E9C1CECB0272BC1D769C27AD0172CA1D1629A0ED2D834641A1E82D1CE607F21BC35C3B6E5EA43E911C8434A40798277D17710C253F4FAA147AD51B6D2462F6D5C8DDF5FC54A9B6FBB7E5E86D17BBC4
	ECBE487B4CEFC94E34831CAD62DD4F4067629DCB428F9EE20EDA73F7DACB9CA396FF8B79B62EA5F42FB6F0658D8B49FCF73310B99F6AB3DA1136FD0B719E97FDF92F154EBDD4DFBF2A9E7375EEE62C3FB12A9E757DEF0B317E09D175690676E45BAFF6895BD67DA211F91E1C9C71FB4D46D632C9551E31A97726BA9E9932D2BF2FDCB05EBDEE384FE3C90D50CF0256BE29B564A3724FDB7D42BF4BDA05FFB68E047B6713D176D78E047B675FD20D754101F07F38C755BFB2906E1F7BB57FD36CAD63B9EC6739644C
	22F3A29EBF59AE623B6D1C18F30F7B797BAC3D1C444B783E154AF67FF867083F84A119673FB599687785675076665E3CF178E61F23B125BC96530BFA513B8E97ABF0E27B07FB45F73839CE119F9776E2EED5FCBE35559DF063C901F85F6D95716057F97394FC872C6077898175042FA398289E553FCD91EDA66BEDFA9454A9A0EB5FE1E86FCFA9775F2E08FC9A8F7C0900E5GF61E5E1513DF3F5AB431EFEACBA7582E6F165BD11E6E5D0C79AB67BC6569D41DBE4469BB8DF44A2153759C3A8F6A624E0B77A77EDA
	7449CA744157100781D5G97E05257624C76C14DBE96650E4A617357410648E3B83AE8770A1BA7762D7173EBC42E1A794C0FD8526DD5915FF547FDCDF1B8D3FB663FF5EFAABE49BEEBBD5A1782F56CC2F266CEC089209150FDC158F76234761E830F21F06A14BA3A79D7857A2156AD77074838A1203EBFBF031AD29FB127F74133D53AE2FA22BD8D4BD3C37B9508C90754250F7DD25377985166E69633EE9D3B2F5936D54476B0589587188518FF015E956EEBC31E226DD5A206AE0C5D97D843308AF8B6C0BB718D
	604E36D0CC119E0BB6219E7CB2EC3F2079FB11D6A9637BB4B1167D32984B5CC79FE639BB6C160D9146F2200D741A5696DAAFC2F6BF534B77CACAD026FE27769B572122BFFE47743BB544A22D3FE55AF355267BD2294FB4855BF4CA1EC0E8CFF6F6940D66CF36AA76BFBBAF42DAF0D16CC196D5605C887F2626D0851F58ECD5F227947428682066D199095640B6D5642F380BE2DFF28717B38F4AA900D597C5DE6A47CF30353819E4787D6826EFEB9D886528B95F2A8C1ABF0335114FD413EA4D4FD437571A5F2544
	540EFFEF93D79B793B4D7BEB0D5FED52BC7FC9836E9B323F01F984F0G3A81435F087C313B6ED3467307C96E28F6F561198E3F1AD7057C357511ED4D2A0F7E2DFDEEBDFE67546ED3D5DF8F76AF681F5663F829BEBFA7926D0BFA7411626E6AD609D5236F2D475873481D1CCCB13EF22BD8A3624136795F0898FF3BF0BB9B334F2E7061B70F344D564E0076EDA21EB3AE09F876EE43BE6DD0F6827C1744DD38FB3BB8AFCE815A8E2E9334E362EF79F1B39A1D117A4FE209F240EF569AF3CB556BD5164F18F98E18FD
	09667E064B215C125D1327FD0376F0D39EBCCFC11D8BB085B095B093B08F3084307232986FC74E643388399E086498BE660A64762A547F42E634A3DB029812C4BF6F1E59B3CEBF0D0A4FAFAB6AA0D17917195AFC5C26C6DBB79AA533BB7C7C286D425FB3D3BBA9EA3BF050DE64831E4BBCD2789EE58D7F3F1A81A9FC0F922B0A781B8CF5CBF5BF7D3CA172FC79E5C3747972FA837FD690625CB168576EC396B760BA656B6C8C487A3C7CCD43861DD693E874855C183049879FEC1D587EEE6D3609352B3E65F6645B
	B779FC2BEB8F7779FFF03A33BD5C676AB661F397544DED976B0DF73BD1FEC1871C13BB46FF47C2F13E318577988E60758362G89005900C500E5G66023DA620GD085F082BC00C900D840B4C012CB0C7D2C333B4719A35AE1BFB459187E6DFED36418F932A9FA4CBC57E41EE367B40D4D31AFB5196758D71A0CB916E88B34FE957DE21D687F5A340E09BD1D788ACE2FEBC67F6A6B99258291334D38B6695F1E71CFE41B01515BC5F77A33335671FA0F5BABD752FFE269B11696E47A6FF5065F77285F7E1E629F7F
	9FE4AC5D9632693F8C3F0736FD7B327F6EDB15C1F7933771B5F41E0B66B4516E6034940DC6B1322BA572DCA8E809BE978E37184F05621631F3415EE2BE97CE340C1D8B35AD387EF7419CF66AFC0DBA6D3CCEEB61FDA26D1C5B25F66B748BDCAF241F12D12020CBDC2FFECEF3EA34CE836D3CC63B5A02EBG185C6DD374F705ADF84668F4FBBCD4D6B90F5B8BCB026E0FC12E235BAF99743E2E53D0EF5AB7C91F9B3E8B652A93BDF7E0DD08AED4E7743F602C2C971E896F32F415650E772FAB81052D973B31429A2E
	5F123E03B66BDB8FDBADAC2D062D26712AE1E968749A164E472406D9E13C86707FAB534220FDD7F843F826DAF9DBC744B3D5C1A72D87764E706F8ADB6CB85643C00F6F547FD76F78DE9679FC5C5CA3F9BC2A6439DA6EF561D939E2D015C37B752F6D58FFC070E8155AD873F7D0D78CED7E8FD0CB87883B12GA0E095GG30BFGGD0CB818294G94G88G88GF6FBB0B63B12GA0E095GG30BFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81G
	BAGGG1A95GGGG
**end of data**/
}
}