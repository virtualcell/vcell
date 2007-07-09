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
	private cbit.vcell.client.database.DocumentManager fieldDocumentManager = null;
	private boolean ivjConnPtoP4Aligning = false;
	private cbit.vcell.client.database.DocumentManager ivjdocumentManager1 = null;
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
private void connEtoM2(cbit.vcell.client.database.DocumentManager value) {
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
public cbit.vcell.client.database.DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}


/**
 * Return the documentManager1 property value.
 * @return cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.client.database.DocumentManager getdocumentManager1() {
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
public void setDocumentManager(cbit.vcell.client.database.DocumentManager documentManager) {
	cbit.vcell.client.database.DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange("documentManager", oldValue, documentManager);
}


/**
 * Set the documentManager1 to a new value.
 * @param newValue cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdocumentManager1(cbit.vcell.client.database.DocumentManager newValue) {
	if (ivjdocumentManager1 != newValue) {
		try {
			cbit.vcell.client.database.DocumentManager oldValue = getdocumentManager1();
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
	D0CB838494G88G88G720171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DDB8BD894D7167601069892B122C2C2941FA189E6488EB3636CB8B3E6C2F64C464D3A5F67E64DFE6C0C59B899B24BE64DE332E65759948D86B22D9123A6A6C141C79C489862AB9B16C1659981C5F934ADA0A285DD8D2DCDF7273AD0B098771CFB6AD697CDF537B26B72FD7FD7FD4FBD771E47BD775CC79542563C387569262D02103EDED07F3ECC9704706302B0E9E6F4BA27C53A2CE6C177378B30D978
	4305CCF846C1DD59C22BB9C7182AE4C3B9894ACB922C668F607796A1DA910E410FGF2D69E9504B39FFE3FA6381CD9C7391CACD67E5D479B6079AEE09150701969C6795BF2372B7494154E11EE9204D50C67C0EE2ECA2B01F2A7A0961068A67E2570FC07C91ED0D22C7235CF0997DE7FEB5934917B517549D16021362754E73E709F692FFBD0D6EB6E8635CD9814978350788CE14706A2F816B6DC6EB9565D65D00A2EF4CBCEE711CD72DED65C1E121293123BCFD26441FAC9912BC4C5EC90DD12D390BCDFE31F33
	F333D99F02D08C65F50C76A3AB4AC9025F4BG2BB57AEF5A10DEA6DB4DADG0F4CEDBB7A07D2D57716EFFEA02C6EF9721DCD81EC8B174926C459E7DBF4E1D74607EC9CFEF98C7D1D86F5B9DE2BF9AF2086508B8887FC4C467F45231F42333CCB29F4777539DD659ECF0553A6573BED1293FE1714C00DCAEB123A5D32CD90E80CAF666EB750A79828DD5FAD37A31F449F66631D50742810725D6926AC831F2444FFE30A7312AF96F9FDBEA1598B824ADE699D9E0F43E51F93743205178EE52DB7101D7B6C212CB435
	9FC17149AED3F02CBFBF07E31D8F3F319DCEA2229FE574B09D1DE273F196B3D81E87F5597DBCB6DA5E653A1C4A1CA3649C1CE16807681FC5181C7D24C3D83F3FAEB34FE35F097DA47383CD173FE074D49D1DF4D94362944B79D72C6694G45D7EA994E55682BD6F392E099E0B5A0F340EA4E879C9960B17679314FEFB2469AC4D92968F7B8ED12ACD05B54E36F43D39AD0E4315665D5C4D737E495E5075865143C024A734AB174C15730F6EF30586F8699A7A417A40B0A436D025EC51839126CA51E370EEDC69ED9
	E23596C71F84827ABC826FB76B7896F8BAC52FF256E393954947C1751B0E6318DC912D50A3F4G3F49AF5BAA50DF96305F8998527CB00975F7D212A1DF14141CF23BAF77FBACE80930ECG7D7CE316BBA261F77CG777166CA24658CDA4D85033C1F470F3F276B274A510D6A0B72A01FE3DF1C03C4AE1482FFA7E088B08F30EC1047504BADBC062AB6AE94767F7DB443980A04F8CE9DD463791A7FDC2AE4B2D04F82282B817482422F71B560675696DDDFDC07C0F33CD355AFD89EA85555130CD543E4E42973B5D0
	7B756AFC8AA6A3C3771B7CFF238A47B29E6CCA862C3E4647E05D71FC0A698A51AB71A8BEA3C3GCAB65F08101E734E6F5749BF8C488360FA350955E3FF24BB4A5F2659370835DB87B277FEE3B51F82A800D8C0B2A08D30F548EAAE8558G91C35C77EF34A56A6CD9AF78F21959C06EB683BF6405709347611B643B7975611373DC77ED3A715822731FDE7F8F8C7A8996837E727E2FF42718F8358C75122EC8AE25CA72C8AE9B7C702AF5EF1621DEEA9E333B35B6EFB13A8772DEA5A49A2FC6DFDF0E76090AA2BB3A
	7A9529D9D294072B47D73F319CE5F97B3BBC3E1449E5EDADC7DF392FC25E5378BFAC471C516DF639A4961CDA93357EE31633A4275487CAD73A7BDD8A51F7D5605E6E32B428664CCFAA5E96B067B97BB17F52FEA9D144F171480EBE88F9CC61C266F54CE7AB347D9EA289629377FDAB6119B644F35155BDBC976D3D761030642247E43436C64F1FECBAF51D66FF67F5DFAEA21D5E2FB456BD1765CE156EA5DD1FF3E1DCD5B85CEC79715B4231F85BA25CF596E32CC75D6661F09D546A7E6A5A073ABA5E5EA2CB926B
	D063FB710F3986FC153011EC424010A51978968F644053666E6EC7A56BC51758A34987A977AC2AC63B46003DD100046BBC77C4D7E2BD4F7FEA5229F4BB1DB434D017467CBF0965E874D5B214D32E137F312CFC0B75C5ADDBF5BEBE722DFFFD5B307A45EA3DF083460F75DF5D466B3782ED7F8DDFFB52F3C2955A66153E6E17E05946056E2496C791D56F7955D95518227A28AA0CA927EE0D9C3A01B2DFBB4F4F8725B7E86D315C207547F603E4EF9D03FCCF34F33E25C0479C3ACA55EF2D4A974778248B1C2FF38C
	558F0C21FEA36139D3637B3C03732529FC19E3E93D4B9FCBFC245F1B9A5F81DDBD6AD7B31664C60EC53E08AE4EE79BCBB9FDC847DF2459D1564D79D2D4BEFFBB086F1FB53E5747D27D06F1745CBBCEEFEF2C0D73D946511A0D7EF3426F08F1FAFBFBA54EB7EF9C694F79962BFCE447E1BB67DBBE0E562354F14437F69C59C1736E35C24C856CCCF5C93CA28E94F92FC2BEABA929E3D30F4ECE47F6E42B7C5963799969D2BEBBA3C1398560994F73465FED64E724284B71C24ACB8733821D7F4247137D8963FDF9C3
	78377237E90E19CE204CB414B71E4F2FCD557AFDDA39576B6EF630AD229A1BBF2956679522BDD17DFBF85A9D32D7290579BD40637829EA54977710AAD95DD3BF46767D1883F242403F00B1E1C8FF799C66DB0CC132337BB037B3725AFC21F35AFDA6F3GBB1343483E55E1BEBB77FF3BF6BD797F72870704DBB8536A929A69F17DAB3D3F97EF3A9ED00F5C89A43FF402CF0FA5CECB966951D47F452D6821CF1882BFE3CFE961F1B284B2481F4EB66E4FA42025CD607EFC288D7D09F31578F25BB728ED77CE341A5B
	8114074737A31F526B70D279914627DF83488F8BF71629F6AEFBF40E10ADCEB0991D4DA3DE6AB1C5CDA47B97CF747921605E2E0CD2G6912266912F9876C636E60778F479A116EF24BFDA2986F6DF7AAC46F390479D49A7008AEDBB97845FD55DBD0E8A53D66EC516BD5FA2343D02F70CD3DA6658E52A7768E5D78CC694AD8B1056BF54F856E4BD460498F67BE4AEC71778951BF6C603EA3DDDEAD576B32A8AB42D0175E8723CDAD61244318F01FAE89F1DD99CB6238AEAD9DDC17654013FDA7576527ED58275F3A
	292E3DAFE87AD09F3BAF72BE4A207D005647420BC6FDD01B6FF571B609401FFAA79F1B2D47704976BF821F9BC32D5C5EB14B12046DDB6EB6341759B7096C2C19243B0B1A5515112D596B6866B1918DBC4BA77138B55BB8BD7BAE2B396CAEEE034C688E17C3F108CE47B529201074CC79E2AF460A8B17CE0B5B6D34B8BC5E02D33A54DBD407F37CCF05D66A7FC99B37F7G7ACE84509E60EC13FE8F108A741C88B85BC7F0798D92366B56CDD36AEF0A447B2B815EB19AFF2E9D756BD377CA445BEE673C0B00EF0D46
	7B049D47D114FB8ED279DF6CFCDC55B3688E7F7B1C023BFDE744523BC71EA7691E6397ECCDE9381B6EB9B2B4FAAA23F76A68241FA71F6B67013A95F77333790EBADC530A6F013591608404816681126F61EBC963D7F8C68AFA77D16BD2E029911D4D43B6737C8E62844A057D37DAB61F50479F79618767508F2B6E89FEFE76BF7BEC961EE46D565EC2BB0A7742B67D5DEB6666BAD3C600F8AF18CCFD97CF7645BB796E191398D783C00F82A4G12812BA7F33FC51E589F4AEF7C9EC8F59875FB249E7B4D0C043C85
	A886B4GBC113C5FFFBC91F2BC2A878C4703FCB63D45688EB1B2126C1B97B9521754CEEE9B5D980DE93B55B122F63B5B7D75DC64275B8A3FB235BBB0225DCE3F72DA3FF66477C927F19F18B3854E8100AAC0A7A08C9087C81E427DDFD07327107E47833445DD4E8EB3157EA7D6D54FD7C7E91FF0413FDD11DFF97594A37B529B503EF5D0377DDE2B79G6094408308GA45E4B6D9B2CB9FA1376D5822D864EB7A3AD643AC60D50559F46B64E8959EE793DC6B68EB4220D29D017B955EA4E8594835A81830058295C
	46E5B59521ECAC679797B00E7C27012124EFC747686CF406EC17B45528DD414551497B6F106D16994A7BD1576864BD95325D6A29C66378C6930BD5285B9E85318AB88570GA2G09D1FC9C73EBC30E23F6E5FEB363686E9E5D1C3CBE221D7F1CDC9AE554EE33EDF47272C6347317374AD05E53A35A5D5CB8BE97325DDABFF93407733C036369BBAB7574BB60400C5B362FC4D777A05451BDDB5EAE7FFBB63C174F1986E7FFG1D19A21A704C04E73652E97C1EF806B4BAFB6607EC27CCB3EA57E59F1DBCCF48F691
	FE7268FDCA9B3BCF1887F50BGC47BB28FE9A9D0FEDD73436AFC5CD7EE1F4E4BAF33B26EF329FCA53FA508ECEC97CC37D0A09913034A4878B37AC750FC5FD79778FE7A33BA637B6943F54677537FD337196850077E2D9D2FAF2F8BFEFFDDDBE7F4FF3D03597A2C89775BF23FCBF174C9CCAC4D87E9G3FB7002C54546F221405123D7E7BC1016741A3969C477D53F93E7BE55DA1E1E43E2B36B994374CBB31C822FC46EE5712DD33C76A66BABFFBB2307F7E6924317FDEB8E96C3F17CE86775FABA7037B2F34BE30
	AE1575463A54569B6BF226BE38AE676A8369C2F3EAA13B0F2981BF37CC67736C3DD6BE4FECC023F1FD6A7DCDCC0E57ABF959A688EFF854B17F35027772A35ECF1C0655C760F76A2D6D6F28BC550A3B7E2026DF5469403E387F3431AFE61F467BC22F3BDF6E167C6D0DBF3DDD2B73ADF6350A54076A904FE32C3D8207D3E9043F963176D497287D6327837914ECAD3C20333552E72B39A1302D6FB5985B3A3DA1302DF98D21EDADE888EC6B678D21EDADEE88EE6B531A2D8D0A3B41E76B384640365E59E8EC6B6446
	4036CEEF8CED6B2C464036AEE88CED6BF79AF10D6DF2589C327AB606EC1C5621377108E67B450B7A18BE2451DF6DB2F3FE3B6C6E832FD07FC2F3E01FE4B59B7BE4E3F3E01FECED8E6D139D4D01FD3237B934CFBEEBB6764902EEFD0C7BEC2F31696907B53AC9527B90693437B9370B5F8707CF676F8D62589D6A92A82F842C515676A9CD41769259B3605C83B8G2882344C60F7D14F8C136D53B546BEFC1C8E29742FF760FBD227B8686ED784E1658C54A9FC875F27B9219C86C8GAC19C17BB5DA639E3B0A7D59
	E1578DD304ADBEC9515876F49EEF1B8F65A3G8BC081C4C5F39D276DC39B690D8A0F17E24BC67D5C9B16CF13DB58FD9A844C3AE87C2E6F5373D0D4659536F2F9E5B1A08B20G22GC9B15C176D3B7CFD39A2463797CA0D9939AF22FB362DCDB8B66BE27C6F5FF6B27A067B7C6F5F96B4727B37DC289BF39FDFE3621B789A33DA23BD4FE8B977435E77FECE7B3499E945D0EE50E87D0CE603F218C67B9923C5C3B969FEFE6F1A3803FFA33892E8B931FC1C8EF66299CBFB1FD5A4DED5CACACE488EDB0558F30A0DBA
	4566DA36874576B531BCB6776DE46FF5219C8DC80C656FFA867671F7AC898B66884BFFE57CCEEB15653AE9E9AC79B6ADD6F7EFDE5815C1B215BCAE3360812B397381244D49535F6322AFE3B2685CC375AB0655E33EAE947475EF676B6BD15FFFB7696B0B07552F5735A75B1E7E045B366E69C441793D30005FEB46BFC0B6ADD31F794F4424374FE8F81B7A795B737A3B471C7B1C2674G7DE44E2476B9B3FDBEA27F1CE9657E291A497DF33D555FBF3B06597F1DD6FF7F7CAADDDF7FFC2B3FFF9C436A3734797B67
	FE557F94FBDE164306C01F4819DAFC6F61711DB4134F390F1A587B2CD93C3C171571FDAB15939A031DA37E9C78E4BEF95BE4205F774FC25BFF5A149F04EF7BAC9AD34AE3EB1BD0AFFA57E19342D87BCD4D7CBBDCE5964F0F65EC9D125DEE456A70BA7024C03C2D4DBC9E16EA3C914D788ED8466F933C4AD76E6EFEEF75307E97E8EDD234B64FB523CEDEC97D8EC3FDFFCA397F55E61ECF57GFFF69C2CAF0016B8FAD7CEFE7D7E08779921C06D12C7596EF7375C0E7274DA163FBCF1142753B5FAFA9E5207F4F44A
	217BF7729C9A8EF5E9F17C7D498B6D681306596083C098A096309830F2B6BF33FDFD7E0090620E4A62761C028D11D3F7F4A17B8673C6772D7163D9BCD7A57D6543422209D5815FF5274D26B85CB0C77D66F8D9CCBABE493EAB9D6C1B8728EB818C8122G7100A5F338FDBFBC2F3E6740E3A81CBA456EDE76D501F628F5C8D7FDE45C90D05FB197D1D3759B32731F4053AADEB2BC51564172F4266B924424C56C51465E52FA2B3E21981F34EB643EE636F67F34826CCA83EC1DEBB5974C25F70561F359FC5ED345E3
	E86E48FDC1069FAC40538B605FG8E4D7545946931F29B6A41AE438EF2DACA9352AAA5FC1F4647722A4447B2771139C2EEDE19E9F30031DC2E6A35EE2EEF3D70593D2F196D1564FE097A0D3B2ADF070AFFBF4E703BB54401F954DF157A0CF8ABA61D7A74B4E39B3B68747A76E4BF9EC62379B366AC5F7F8CC19FC973789EE4E5854E8D7FEFEA0A9470C9C9C915E4977B1D8ABA28F9D8C622B5F069D91E3F526671FD499BCC4E5E79D6F3A940B21F67257F42D7C0EA5CE46579FE9F5AF93B5681DF0EBAF9DBE5507C
	C9EB8FFC26FA2D5D78CC75E63B71DD4ADB6D416FED32AE847EEE735D8B7A6FB6E91E9F92F15F6004318887A4G16GD64C6779A37642EE01658F035CD1ED6B41B39DFEB52FF079B7BA825BBA31A374377613BB70BB27AE3722387B30FFCE1F5601632538BD0CC8347B5819D7F67474AA442A5263BAB0761C121D11A946EF6C66EB449A302DE0810F71970BBE96C66C33AB5C784DA3ED3355B3604FF773F86EDC406379372C4FE8A8A78216AE60F761EB76707362AA2065B11D606CA7703FB5470DE8F4C67ACD9716
	A887BC3FC11FDB2A7E214A34C7B04E815B9F24395F7B20AF3754D80E3CC3E37C3EAD9F1EF1D03798308A309630A19E4EB000E2C0C3BC9F6F0FBA8B04G399E086498B6663268F0A954FF0D447C8DFD04EB7DF4F67EB1C8BF8D325BA3494AA0D15917196AFC1CF8B6543A919FEFF4079F95321D7FFBE6EA77B7A15B790376A217587F4FA4477B6FD13C0C3EBC5EFF0F326EAC0F3F9428ABFB087B29C48CBCDF4A45507325CEE45F8AC21CDB86BD6AFD48999157A9375D6E1534F9E991F3B4DA2D37416DF5E042A69F
	CC58BD3A7D5D54D136DBF64B6D48370EBD6843F98F797BDCE67444077CFD7E1F5A1CDF8CF5798F73756637FB747243009E7DF070F7AC9467E1F65CE3A4G6FD2C09AA073912BF9BB60G208A5082F08242G3100C4C0B2E0A5A095308E1013207D5FD2F7E110B9229E76FD13CD60E3394B96B8E68AEC21E366204DB847FEE99B19E3CBED46B93642264F31C4BB2176ABEB976BC42F3761D91C76F47CABB82DCEC27FEA6B9925829E33922ECD5A37E76C9359E6E0F475501D7E12AD9BD93D536112AA69BF31349873
	D8856D77206E3749FA7B77949F7FBF48F06F7159B4207B1D3E67764B7E68364A203B098DFBB13E76A6501CA65AE6C6ABD5E96A7FAA58834F05D97650F3A15EEEBC979E350F1C8B5F359B4F059F5AC74E051F306F90FBE08E37E8FCCFE834D60D3614750934F38E1B522B511FE1FAA13DC6C2011C3E0269F51551DAD45AF3BADA2BCA7B059D57GB03957ADEB6F8A59770DF60753C965DFB39E078B16844D9FE9CCC707C754697D0AC6C33DE95FA46E53FD974AD4A7FA6E40C69F1D2BAE507F02BFF57EEBF8A67C2B
	30DE91F2037D6B0AD796B6715DD8D1AD53EF551543A599AF4C4DB0891935425BB4DE35C2A6BA3DD6D84F4624D64800719A407F2F4C74037ADD618D7D19EA6D046E00E72A3005349EC4AF747F2EF0A75BDB2EG7A1A05BC0F3BAEFCA684BE9FB77709CE27A2BAAF173BECF8D62E98D4A45FFEFDCF87766F65BCEAA555FD4A763717F4ED7E97D0CB8788B0047A9AF395GG30BFGGD0CB818294G94G88G88G720171B4B0047A9AF395GG30BFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4
	E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2D95GGGG
**end of data**/
}
}