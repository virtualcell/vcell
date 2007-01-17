package cbit.vcell.graph;
/**
 * Insert the type's description here.
 * Creation date: (6/19/2005 9:34:21 AM)
 * @author: Frank Morgan
 */
public class CartoonEditorPanelFixed extends javax.swing.JPanel {
	private cbit.gui.JToolBarToggleButton ivjFeatureButton = null;
	private javax.swing.JToolBar ivjJToolBar = null;
	private cbit.gui.JToolBarToggleButton ivjSelectButton = null;
	private cbit.gui.JToolBarToggleButton ivjSpeciesButton = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private StructureCartoon ivjStructureCartoon1 = null;
	private StructureCartoonTool ivjStructureCartoonTool1 = null;
	private cbit.gui.ButtonGroupCivilized ivjButtonGroupCivilized1 = null;
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.ButtonModel ivjselection1 = null;
	private cbit.gui.graph.GraphPane ivjGraphPane1 = null;
	private cbit.vcell.biomodel.BioModel fieldBioModel = null;
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
	private cbit.vcell.biomodel.BioModel ivjbioModel1 = null;
	private boolean ivjConnPtoP3Aligning = false;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == CartoonEditorPanelFixed.this.getButtonGroupCivilized1() && (evt.getPropertyName().equals("selection"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == CartoonEditorPanelFixed.this && (evt.getPropertyName().equals("documentManager"))) 
				connEtoM1(evt);
			if (evt.getSource() == CartoonEditorPanelFixed.this && (evt.getPropertyName().equals("bioModel"))) 
				connPtoP3SetTarget();
		};
	};

/**
 * CartoonEditorPanelFixed constructor comment.
 */
public CartoonEditorPanelFixed() {
	super();
	initialize();
}

/**
 * CartoonEditorPanelFixed constructor comment.
 * @param layout java.awt.LayoutManager
 */
public CartoonEditorPanelFixed(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * CartoonEditorPanelFixed constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public CartoonEditorPanelFixed(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * CartoonEditorPanelFixed constructor comment.
 * @param isDoubleBuffered boolean
 */
public CartoonEditorPanelFixed(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * Comment
 */
private void cartoonEditorPanelFixed_Initialize() {
	getButtonGroupCivilized1().add(getSelectButton());
	getButtonGroupCivilized1().add(getFeatureButton());
	getButtonGroupCivilized1().add(getSpeciesButton());
	getSelectButton().setSelected(true);

	getStructureCartoonTool1().setGraphModel(getStructureCartoon1());
	getStructureCartoonTool1().setButtonGroup(getButtonGroupCivilized1());
	getStructureCartoonTool1().setGraphPane(getGraphPane1());
	getGraphPane1().setGraphModel(getStructureCartoon1());

}


/**
 * connEtoC1:  (CartoonEditorPanelFixed.initialize() --> CartoonEditorPanelFixed.cartoonEditorPanelFixed_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.cartoonEditorPanelFixed_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (CartoonEditorPanelFixed.documentManager --> StructureCartoonTool1.documentManager)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getStructureCartoonTool1().setDocumentManager(this.getDocumentManager());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (bioModel1.this --> StructureCartoon1.model)
 * @param value cbit.vcell.biomodel.BioModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.biomodel.BioModel value) {
	try {
		// user code begin {1}
		// user code end
		if ((getbioModel1() != null)) {
			getStructureCartoon1().setModel(getbioModel1().getModel());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (ButtonGroupCivilized1.selection <--> selection1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getselection1() != null)) {
				getButtonGroupCivilized1().setSelection(getselection1());
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
 * connPtoP1SetTarget:  (ButtonGroupCivilized1.selection <--> selection1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setselection1(getButtonGroupCivilized1().getSelection());
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
 * connPtoP2SetTarget:  (selection1.actionCommand <--> StructureCartoonTool1.modeString)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if ((getselection1() != null)) {
			getStructureCartoonTool1().setModeString(getselection1().getActionCommand());
		}
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (CartoonEditorPanelFixed.bioModel <--> bioModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getbioModel1() != null)) {
				this.setBioModel(getbioModel1());
			}
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
 * connPtoP3SetTarget:  (CartoonEditorPanelFixed.bioModel <--> bioModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setbioModel1(this.getBioModel());
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
 * Gets the bioModel property (cbit.vcell.biomodel.BioModel) value.
 * @return The bioModel property value.
 * @see #setBioModel
 */
public cbit.vcell.biomodel.BioModel getBioModel() {
	return fieldBioModel;
}


/**
 * Return the bioModel1 property value.
 * @return cbit.vcell.biomodel.BioModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.biomodel.BioModel getbioModel1() {
	// user code begin {1}
	// user code end
	return ivjbioModel1;
}


/**
 * Return the ButtonGroupCivilized1 property value.
 * @return cbit.gui.ButtonGroupCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.ButtonGroupCivilized getButtonGroupCivilized1() {
	if (ivjButtonGroupCivilized1 == null) {
		try {
			ivjButtonGroupCivilized1 = new cbit.gui.ButtonGroupCivilized();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroupCivilized1;
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
 * Return the FeatureButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.JToolBarToggleButton getFeatureButton() {
	if (ivjFeatureButton == null) {
		try {
			ivjFeatureButton = new cbit.gui.JToolBarToggleButton();
			ivjFeatureButton.setName("FeatureButton");
			ivjFeatureButton.setToolTipText("Feature Tool");
			ivjFeatureButton.setText("");
			ivjFeatureButton.setMaximumSize(new java.awt.Dimension(28, 28));
			ivjFeatureButton.setActionCommand("feature");
			ivjFeatureButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/feature.gif")));
			ivjFeatureButton.setPreferredSize(new java.awt.Dimension(28, 28));
			ivjFeatureButton.setMinimumSize(new java.awt.Dimension(28, 28));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFeatureButton;
}


/**
 * Return the GraphPane1 property value.
 * @return cbit.gui.graph.GraphPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.graph.GraphPane getGraphPane1() {
	if (ivjGraphPane1 == null) {
		try {
			ivjGraphPane1 = new cbit.gui.graph.GraphPane();
			ivjGraphPane1.setName("GraphPane1");
			ivjGraphPane1.setBounds(0, 0, 150, 150);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGraphPane1;
}


/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			getJScrollPane1().setViewportView(getGraphPane1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}

/**
 * Return the JToolBar property value.
 * @return javax.swing.JToolBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToolBar getJToolBar() {
	if (ivjJToolBar == null) {
		try {
			ivjJToolBar = new javax.swing.JToolBar();
			ivjJToolBar.setName("JToolBar");
			ivjJToolBar.setFloatable(false);
			ivjJToolBar.setBorder(new javax.swing.border.EtchedBorder());
			ivjJToolBar.setOrientation(javax.swing.SwingConstants.VERTICAL);
			getJToolBar().add(getSelectButton(), getSelectButton().getName());
			getJToolBar().add(getFeatureButton(), getFeatureButton().getName());
			getJToolBar().add(getSpeciesButton(), getSpeciesButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToolBar;
}


/**
 * Return the SelectButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.JToolBarToggleButton getSelectButton() {
	if (ivjSelectButton == null) {
		try {
			ivjSelectButton = new cbit.gui.JToolBarToggleButton();
			ivjSelectButton.setName("SelectButton");
			ivjSelectButton.setToolTipText("Select Tool");
			ivjSelectButton.setText("");
			ivjSelectButton.setMaximumSize(new java.awt.Dimension(28, 28));
			ivjSelectButton.setActionCommand("select");
			ivjSelectButton.setSelected(true);
			ivjSelectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/select.gif")));
			ivjSelectButton.setPreferredSize(new java.awt.Dimension(28, 28));
			ivjSelectButton.setMinimumSize(new java.awt.Dimension(28, 28));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectButton;
}


/**
 * Return the selection1 property value.
 * @return javax.swing.ButtonModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonModel getselection1() {
	// user code begin {1}
	// user code end
	return ivjselection1;
}


/**
 * Return the SpeciesButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.JToolBarToggleButton getSpeciesButton() {
	if (ivjSpeciesButton == null) {
		try {
			ivjSpeciesButton = new cbit.gui.JToolBarToggleButton();
			ivjSpeciesButton.setName("SpeciesButton");
			ivjSpeciesButton.setToolTipText("Species Tool");
			ivjSpeciesButton.setText("");
			ivjSpeciesButton.setMaximumSize(new java.awt.Dimension(28, 28));
			ivjSpeciesButton.setActionCommand("species");
			ivjSpeciesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/species.gif")));
			ivjSpeciesButton.setPreferredSize(new java.awt.Dimension(28, 28));
			ivjSpeciesButton.setMinimumSize(new java.awt.Dimension(28, 28));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSpeciesButton;
}


/**
 * Return the StructureCartoon1 property value.
 * @return cbit.vcell.graph.StructureCartoon
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private StructureCartoon getStructureCartoon1() {
	if (ivjStructureCartoon1 == null) {
		try {
			ivjStructureCartoon1 = new cbit.vcell.graph.StructureCartoon();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStructureCartoon1;
}


/**
 * Return the StructureCartoonTool1 property value.
 * @return cbit.vcell.graph.StructureCartoonTool
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private StructureCartoonTool getStructureCartoonTool1() {
	if (ivjStructureCartoonTool1 == null) {
		try {
			ivjStructureCartoonTool1 = new cbit.vcell.graph.StructureCartoonTool();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStructureCartoonTool1;
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
	getButtonGroupCivilized1().addPropertyChangeListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
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
		setName("CartoonEditorPanelFixed");
		setLayout(new java.awt.GridBagLayout());
		setSize(632, 512);

		java.awt.GridBagConstraints constraintsJToolBar = new java.awt.GridBagConstraints();
		constraintsJToolBar.gridx = 0; constraintsJToolBar.gridy = 0;
		constraintsJToolBar.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsJToolBar.weighty = 1.0;
		add(getJToolBar(), constraintsJToolBar);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 1; constraintsJScrollPane1.gridy = 0;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJScrollPane1(), constraintsJScrollPane1);
		initConnections();
		connEtoC1();
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
		CartoonEditorPanelFixed aCartoonEditorPanelFixed;
		aCartoonEditorPanelFixed = new CartoonEditorPanelFixed();
		frame.setContentPane(aCartoonEditorPanelFixed);
		frame.setSize(aCartoonEditorPanelFixed.getSize());
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
 * Sets the bioModel property (cbit.vcell.biomodel.BioModel) value.
 * @param bioModel The new value for the property.
 * @see #getBioModel
 */
public void setBioModel(cbit.vcell.biomodel.BioModel bioModel) {
	cbit.vcell.biomodel.BioModel oldValue = fieldBioModel;
	fieldBioModel = bioModel;
	firePropertyChange("bioModel", oldValue, bioModel);
}


/**
 * Set the bioModel1 to a new value.
 * @param newValue cbit.vcell.biomodel.BioModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setbioModel1(cbit.vcell.biomodel.BioModel newValue) {
	if (ivjbioModel1 != newValue) {
		try {
			cbit.vcell.biomodel.BioModel oldValue = getbioModel1();
			ivjbioModel1 = newValue;
			connPtoP3SetSource();
			connEtoM2(ivjbioModel1);
			firePropertyChange("bioModel", oldValue, newValue);
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
 * Set the selection1 to a new value.
 * @param newValue javax.swing.ButtonModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselection1(javax.swing.ButtonModel newValue) {
	if (ivjselection1 != newValue) {
		try {
			ivjselection1 = newValue;
			connPtoP1SetSource();
			connPtoP2SetTarget();
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GCFFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8BF4D4D735AC25DDF109AD2DD823C64596EB2A506212AA764537228F7B68AB6F99A3A8AAACF9ED17C6C56535BC25BED65F4DA4A9895FGA92616C2AC540612067013B4C0498F48E782A199428C19105C4C5C495C644E5CE166CEFE057276BE1FFBEFE66EE4D27A4ADA3BF74E5E7B1C3377BE7B1C3377B9A795CA764EAE48351588C2EE01407E1D48950499AD0250FA3D68B247D53F11DDA4187E7D8D
	E03BF0B4B79F3ECDC0DB9D4AAEAA96CEAA05D0F6C3F9EDA03B68AF70FB27D02B4CB887BF927463855A1627DEF8E472FEF63570FEB610327B72E678EE8568839CFE673E017DEB17CB99FEBA43F3483589C2CAB9725CFDF9B7432D811EC240B582B84BA9FF883ED400BBEA2A991F7B1EF4E16D3F05EDDB309DD31B9C32D85D356CDBA67CB9B76DG7635104809F5AA206C85C0657304E038923E275A07865E71744BDA6530C7D2144A013098725754B408E1CDD503E73D3226065B4520241C13C7A52FA0143E09ED8A
	DD05248DC1D8856D4DA138E7E56C478D3FB50099EFF27C3D8A62F36037F0B03B2874A0576D749B27186C4EF73FA5ACBD92485F16C0372A03D4A767C1C337554ACA5B4BE49C3ADA515E33003690E089409A00620A6C22AA00D7487877343C895F3AFE2DC18D8454E0DDA8D42FF843ED2AD7D260F7CD8DD0982EC37228E150130E71BFDF2E3010E7B22075163C6FFA54A669C779F84F6DF8D8D8710D7BEC9BACEC32A27DDDDBD38535C5DF05E1935AF7C1D7223E27D5CC744709FDDF914CFDD77CF4D9DE01C55FCD0F
	AF4B1B455AC9B7753D3A8247FAF09047FAB17C46FA682B94BFC670D5EF99F86A1B6B084FE05981344C3738EFB837F2D96C79778B4507BFEDE907546F4D30D5D4D2999AABE3E559954036C315344FED3AACFFA778D4931E4A12CA7C944B19C0BBF5A83B087AD7EF9B4E5505D0DE8A309AA03FAA3BE8BFC0AD00230A7B5803AD072668E36DB0396A2332621542822D5B5B3A8B3E522896961B0391CD8CFA24AEB1AC0B7D0A9491980F3F95ED50BF21DE00783E877AB8AF8525302849EA90DA978312A605A314E738F5
	BB7204A5C2F5488189BA8804845E6EFB2DBB612B08916DF248ABEA1241C169F73662188C0BDD50A2B4G3F29DDE6DCC0FBE9207F8C00B45D8E39F6EC2FD58A4322D1D3E3D75521E848011A882B2B504E3B495AB19FFEAF2E62B6CE3908382A4359C5CD07F9BB55769D26F69AE58F0AAF0647789C8B8D226EEE601F86B09BE091C08E00F0047B516F1D5C0F9A374C93AA16FD4A520FB24027CB0FD07F29BA92BB1FEE11FED0D6A750C681D2819682ACBF42770297643EC4F36EB0ED8FE5DA44FE679CB666A14A6DBE
	ECBDAF2D571617879375D5403EDB581C465F1BE26A67CD211F2D7A987F319D47F89D48DEF2B43B6858D1BEB691FB997575FAB1A2F16F3E988647143C46C8D1F9F787F6B31B67B199B7B03976B2D9A9DF856133C39F9AC09A40A20055GA547E0BE9E63367E029232E80F6B3AE58A3A6D1D228D62ED7F9D523739DF331D37CD211D2948978B5466F29B36AF8DCBC12DD18AC9C1AF7C08B0DA288D65E06B07CF556B8C93FC88561B861860919DFF238D5784D15342F2FFD413BAA5CD13038386FDBA69AB926D8F99CB
	956F6B63ED283BBA826B0D4EBF338D672AC78D86A5B278FA95C63F172C9512A285C06886B59A54A8BE758246D4C35298DB2B52AE3CA860DA23C4F15D23F14AFC91ED9B8A4B81F0A9DCBA058547F09DB95AD5EA321197F098EF2540B76D981F7FA37B787C5FBF7E1010599B32D96DE929DF3E5B367E981D0345470D79CFE57A44C5EB59BFC956AC060FD0D90777E23C19A8F84AA3759E6EC25BF7EA6128C70B06A546FC187955CEA13F61A58B3AC3D59503E75FB964290FEA1A9ABC9FD6232186F9D8D664F149EB70
	54DF5C08E3888212A1B94C71778D23ED7AE515ECCD48CF677A218E32C7037E0DG6E63FC2EEFEBC7BADF0759A4EFD09556AE50520F63F87C6BE00949265AF1BA9EDBF57AF39368EB6268FB074C7472FF9874E97FC0FA2BCEF7C2B994C77F03CEDF8C65D5A6BA55E3FDFBB131477528845BABEEC82D3A5F65DD5A91C36BE2BEC8691BAE218FAA263D4CF1827B748474B81E75D7F802FE4BCE503ED7913ED7031CEF768936FF1C20FBC896FC7768FC3F5279AA2AA93D311A6EB9FD5514EFF1B572BD26F23E51EA5A5F
	CC46B7375A5C6FDDA14ED772B6652BF81B6EDB3CDF5A1E63BA672BB551F13FF33E4DF21B37499805B91F72B6DD1727B37ED478D68AFC4EBEF7G67A649AD8645E1F134B2B2826BCBCDCD8B198DB40778BC098310FF5D5BBCD7F842C5FC308662G003E9ABE0FFF3805678A33065205421FAD4DCB14870D56D0F953EA0CF93C603E11BC6A63E31D58678A202D2E617EFD67A5F33CD29709289E1904CAEC6C0FF71867B94555F4605C72496108568C8B71A877135A8E1497E3A906E67678622DB7040439D66CD4E771
	784DF3E61DB3365D34EDCA2033F01269FA529483BF7A47BCB6962587275EB7AEB3CC6687776D1BD0EFA0AA5754D8ADB9ACFE8E8FEDE6EBCF9F48118A10F592710F8C619A0F7ECE75729C65FA4D9C7F325077294FA6542B6494556758A9C32F69A54773E8FF3B945E1F8274B927F8FF1D8A7607FECE79E610F89FF5DA81BCA527330B68BA789C414BC1D913C554237CG55FB68466BA825759A5E5BEC626F92F86EF47F4EDAA67312076F97CE5D3AE6334AE5D33679ED7653D4976D3421532A9F2D342D8520721E8B
	F23DB201E71DAE6F77070A8453FAAE70757C07C154D7734B91AA4B33F5E6D996EE18E1A90B7FAB29362AF7D8DE7D0EA14B1227D65AD6BD45E5A9D639AC6940336A9DAE4B080A73CFA402E03AA3863D3ABC772BB8B782303900F3412C67795553AA9F0752B359C56DE7F8DBADA1144F2BFA22B8415A4420B8G118525C5ADE9D44FF9FB4CFA960AF7D86AB96357833614B3D43F79E78CBD1B162E343917F2BD3F9562FA2E831E2ADAAE1BCC4E1278DEC9F9EFDE673CFE601B2D7396068B185E0A40F5EE8AF35E6540
	D7DC474FDECA5E41AF09F3F43BFDF7C2FD2A638D9757F15A1285C2694ECFD86A0850D4474E0E6A8C9DCBE09C4B81E86CFAFF5C39C0E61D11F7AC2D0B4FC1E83EFC7AAAB93F292379F2010EEFA27875A6BC557987AEBE4E4575904B57739CEF96095716C3F9AD4066864821819A813C8DFCADDE5CF1D4C812C3B787A1358D0ACA6704601467321DE8CB08A7D979A31DE6BBD3B9ECCE0C9326C11FE9G8BG16G2C56E5A8F2A415616C28258C544E23C3E8674D0DD34B690C5C62CED2EF7F7B28C7FD6440D57399E1
	7E7696DBDE829FF130363D0D060FD01BFC4D05B6198378CC0065GEBG0A4FF21BF4BAAA1259041FD7B0E3305CCF39BDDB3C293CDFDB50FA7786E2FD3C6A6C44B66CB1E5DAEFF6DC3D4A18F2A8269E35DBEEB746C3A9C01B831081309CE0BDC069B948D94FF17B4DF61CCE6AD31858B9548640B5C11C59901BCB69325A03494E962DF53C1A341E7B1C150E23EE54D1835ACC00F400C5GB9G7967330B4A4FF39D1FF354A753310EA71C8ED57FE921A815F717FAFBFA6ECFDA2F763CD53DDF47551B1A6F7CA92E5E
	1E1832733C15DDFF5703F68D81ED86409C00CC00E7G04A6D89B1B38DD4FDEBEB9C55F29E37BF12CC15959C568766C79E452FA0DCDD675BEFA7D767A3BA7E9BD37E5FF974237571FBBE9BD2D49EA7C823DB8FEB300168E10893082C0E84EAE5A8DD05B4C476F5317134E8B7D48F672F9C14FCF3FCD72A6A7342FGD05CFDCE444D04F2FAB3BFA7EB380A38A5D0DE2563AE905CBA1431056316391CC97447B6A623A32CCA40C37B78FD52BE26F2AED910GCF6D7C792E4467E3F33A2C4F473E5AE5FDBEB61F1C51G
	9E5AB01F56F37A422E494F4FB23B921F1FEDF4A616F33B53DA4E3FB82D65FC55B9391C2FBB934979AA31790FED982B0523C1CD8EC824E99A1FCA23FF23BE5B918DD2374556A331FB3DBE973E5607B9C8ED8B77798C67918B1F274994EF44A109610BBE1F6E701DA14943657E06AB310D9EF3D95BA853E5ED2347DD135B68BFDC135B6809DD5BC87DC8C40A100536D154C4C67B150667F0F1671E97E0B68BBC2EFFA2D044F84EEAEA43E13D5FCD5D09F55C51ED2DE3E9B766DD91B59A76C831FA6C6DAE55E9466251
	2CC98194077214137A1A989E10626CF008542734C4752B3B93590A6A7A51A05735DDD35B8DDDBF62CE2C6B475C563A5E6DCE2C6BFD6E643AFE511DD857875D49F5FD440D67927D32D78E330C1B6A58261AF5BC226BDE96B20DF31B217B1FFA926B7EC20F356E7957926B3E69DAF25DCB2EA5567D15EB49F55FFFED72F1DE9BB25B60280EFF6C3A598606ED6E88EF627C3E309AG4F207D0C74A6364DFB3D5636195E1B58B69F6FCDEE1BD93D09ED73395E64361953EB6D97D9DDF8F72108E3EAD4930445AD64BCD7
	3F23F0C3D983C883185FC26FBE69DA7B7591EC4F8791G1818AC02F3DA316EBCA72FDB8C657DG0DGDE00692D3C6E4AD25C4FDB70542EDE8CD3B954F3382637F4FA422A22604955E1FADFB26D2A793EE4F5AB1521448EB1BCG65791C574CD3EB27BC8AFC2759795A7EC85F9633EF30350E667E5B2E7037A629401FE567F29E6E43D8C9BF8F2E94C7341A1A73E159DBAF8E58094568197015AB052CFEE19BBF933E0334D98BE5A7C0280D1FA30E7E1D1FA34EFD70FEE17DB26BB361D50E1B3614B6F6065DE6643D
	C2E0250D7679A3A76FB38768E59790176BB25FC510FCBA1F660414FEF4829D674A26DCB3DD1AC0C7F97FEBC27DCFCF20230D5EE27529EECF3E4EF5DB7F64FCC1F9F4FA42F7BF7E8BD427996CBB73194F6406FEB94E4EFE7BEE7DD530BC07E79E435F73ACA4F1A1763D6882377B21B1F23E8365120B59C5959779D88E1CFD553ACD4363845E6E0F3C7C1DD063C55E6E73BEEC778614E7814C3D486350172E5C5EDB1BFD37D94F7D3E6B51F32FABCE14FF51C5FA6E152B637B88BE4B0427F362637A1CD881345AF6FE
	9E666D4739B6874A19G4B8156G94DF82DBDF62F36D04D8A5A4499D9CEA488E8B28E28AED28FE4B1D377786693B9B386FADF86C2B4242BB9B931E4F57DEE2EF1058377C694F649A79511FBC285FCC204D8548824881D88FD05241753BA532F3A58C8DA19294BDFEF24B22871A32B4E220F1F1E4396CD52CD75EC17BBD55B1B177E80FA9FB21CC4768816253239DF40CB668E37788414FB46169589DA7F8AC2783EDF3A77755655E49EC9B6ACCF4169A98407626F752FE72F47C88414FB261E97F7F6B636747F300
	D668607D77E0BA457C58E77AFD4F15E4F95687815A47D7BF54BE583B267E44F10156EF9D970C77DB477F537AA74F55D27A9367EAF7771B5FBAD03FBEAD615A2D40D824G4C854884D86A60BE7D2A67B56A53967EFC56BB00F1893E7052F87F02B7312E9B3C495F0595FB713E25DF55B4B5006DF37CAEAF0E172606881245FEDE34D1D89E70EB1415617FC656E9C57291B4BDD3D8CA7604B520D761E5FE4E302C9BF13B21DC25639CBD7C6C4121631E9CC55C9CA8E7DC66BE7B9019CBD9D04E81486F6278C7893E8C
	4A35GFDBA7E6418151FE1DDAB3F4176E2F1D989F88DDFF6D87611681CE10385EE32F38BEA0BBFF839FD26693AC586B0DF4E524BB7C9B9C7AF978CFC10B916E5E10BF85D77FC20BE121D573033CBA931EF5B25643EFDD9A2F76F60130E31900B3F2FCA98E72BBEDFC45267D02FD42C631AA36DEAC446401C46433F3D0ABE6FD3D4C85B6919C559959C173CEE9E77ED3E426F70D6F7E35B7DEA18A485B4064FF37318588BFCB7G6683AC8240F3347DCE64996AE9E4BCAB2E445B1E4783F98C56GCFBE54FB090DC5
	29530899289CCF0D621E2B06E53097FD0BC0E57E73A80F2794A71759CB5EF7F712FBE1FABDCF716748DB33F31208577B1420377FCEBD52F0BF152508CE236FF0F2464C31E91A136A39843E14FEA6EAC5DF255317F92C68EB1D5CD6AFCA5CD6052EF8DB658993ED359BF82ADC54D6A40EF4455AAA78B22EFF6771BD8349F68476BE6C6181F3AE536E22E71EB760BB5345ED17550DF35DA7AA91748B9A7F4C9EB15F27D5EF7C08651BB104F4A6FF867B2EFF69336C4D7DC1B75F3757822D62AA0F1D8B4A70EDC1EC6C
	DCC99D2E26662C6671CB5EFA66FEAC060E38391FFA2F72987ACB247DB4A8AFGD882C0F953FA781B1AE7G37176888F6960CFF7138B5E7F6F7635FBBFC932F1D198D7EF3B30F81F7FF6D812171A70D36FD89EC50574D6E563B0D5CEA3DDA16C76D3BFD8C6B19D56D447F72ADB0D259F53643515CD0E7E7EFCE1EAF35F8F342139507BAB020F0CF267A6E9DAFE47A64F4F39B8CC79107FB3E5D4DF11B3D9CB7DF471D10382DCAFA2CEC3566CCE2DB1561F4E1B2BFD038A7D6E67E3AC6F7CABE163F9C6073575E4347
	728CF983BF9D4AE9G8BFAF8FE706FC1F2FE8F651C9E9EDBBCAB61DB1EAEF1B07EC495106DE1B5A40535B10AA5AF5858DAFF581DAC97A83CE6F5B7F2A6E93D583BAEF6871E34DE5CDEC9626017FA50967B2F4566D6FB883E6ADAECEE7595B74F2DCE81ED76B53EFFFE29475CFFFEAF0CF96F64FBA2757B9FF41B67FE71E7959B6FAF163F0235D75D9BFB17FEA1046B1B8278940039G0BG16832C81486F83D9GAAG9A815CGA10069G29G69G99GD9FDFC4C7FC7B9B0491833C394B22342220CE1A53F879D
	477D7D193E09FA2FE3654DA213DBB4684760F79300DB1CD8A78465692215FF3C9B45BED2E3E8F3D9B913FD171A68B970FB3D98BF96457D30BF74DB75315D0BFDD447506C2C5C473E1A091E823F537A637B188F38C516FD1C124C7E329CF8D6DB540F2F775B01497CDC70A44A833FC166FC09A7564FB3883E4C936B67E7E57DEF3800B65343FDA3BB08ED6A398D1D72ACA67AE99077E17DFD9B89A3680BACFAF71139F38B21ABF2D0EA20FFC1226B75CA0FE078116977BC77071FDFBE7E2176C1E37205E41FDC4B6C
	CCF15FA1384DDE0A23F3F941F5541D4518AA6E5D167390B23B965863AE768FC28C66908774BA7D2144712F9CCA9E7F06C356395DE8A8BE373B99324E6DBE9AB267F6AC678D615EB5GEB07D3677B040EF36938FBC91B083BA2FBB53F0ECFA5F2A13EC9428EB97E8BC42E9102F3B25C83A61C0B6152C398E7034AFEB54C6B4E8BE18E64139505163FCEF864A004743ABD3EC9E414C322C96EFF52F1AEFDAC4F1138209C4632564BF352F984E70732DB476DF897F1FEA8CF53F177107B75D9D01E2F631EA7388CA8E7
	6938FAF2D67D8C14576A383FECC4DC2184EB1944F123AE1E7FD56B381F931C9B4A1A0E7BFC144B1C2263DE08F219E769385EE1AE73EC9D773BB1AE73829DF7E11C4B172963660538FCCBF55C2F747CB4C74735C8DC3E4DBE0EBB6A6572156938D9DEAEDF050E7BEFAF172FDA477DC6627259F5DC2B0F4B6754F1F77939FC0A0E2B726979330E7B240F4BB7C747F5CA71F35AA91967B445597C3C3D05BE9EAFFCDB96747567FB265F2D72073F5EF9BF54BEE80CAA7E5D745619CCE50A5FBD3A454073294C722A1E71
	CDE40DD195079CF240BAA5505837840F4E984F22F1727698081456289934836FE27D00B8AA872201CEF9DCE2B9EC9D19F3AB4076B9BE1E0725F7D93DA726ED4E0D9AED9613B9C75FC0CBDE5A5E9BD7F8CCDDB2405BF3DDC139E40F9A24BCAA61598F74AA1D27F2A376998ACBBEA99C163CC4C2FEB6F01E4C636940BB93A0EBGF1F73BF8BE19B1E01D37C2FC6EA2BA03C1A76A3C111C25157AE1DDG20FA7DE0D850F5FCF6C4507BCEA1733B9D785CBA6F1BE386FD044C6B94204D8220389F13F91D836535FEAE6F
	8B7ADDDE199CAFAF2D97A173639850EBE55ED75B38A1571D6F9A7D6EA13C8A700D6A3C8F5DB0689A1913F300B6DF66F2FD0B4C357CC178BD4865BA6F63F21D9ACCA4D786E92B9D68EEGFA9F7A321F57CB81DC9AC086408A007503DC1EA5B78DDF39338BF31320B4D2462F9CAB9B65009444BFE2ADBFD2DDD1F6247A00FE9EC27CC58BC7A55A4EF941E84736713C299D1E47B687448129999C2C9C2F0A7140397CC0A57C6C10686DF379EB07E4E4099C225EDAB9A07BDEBBF00059F5B057E8BF601A0A1CB4FFFC54
	45F3565DC37C9EF4F10F792E306C60C54B3B42F5BD63B6479035776810113364C02ECEE57AC91EA153EB512948C46B7DB95F28775458546BFD66C523DEEA2869835958C7CF391811E97B999B0C76D7F827EE637D7A39405CA16ED73FA8B45ADA68193A0EDBB79A75FEA1CD3DDE345D28B7667FE0360950D33E8936D9F949E87FF55F54ED93706967CB3AED04CE232D775E071D4F78F5DFD6783D7B08BF765E1D5F0BD37AC37E58FBF774F3037EF3FF6C3D7B447ADB6458FBF7DE1F6E450F394C791C836472AB1367
	01B4CF78D9A72ECDB700B795A083E0A9C08E009060395A433965C262BC1E3E02B052F8AA4F0189F9E98934D59E18CADE9A76196B1D02BA6DD32A5768B75773C21D51A475E8CC75C3970F3DAE69389F3BF86C05B8EA0327BB9367C33F69CC1E8FBD57E91D8FBD5F991F8F3D50E91D8F65BB626F3AEA25F8794FCAE679A96EE33EF83E3BFC71FC5DFE4E1727637A7C1C8FF1747FCBF36256EFEC304E7CCEA8080405ED7A1CE9A6C94F2A6123B5F9CFBF10E793721B059729524DC2BECA5EAC9490451A05BCD0FA947F
	A8FD423F48F561BD72A35F8C1A3009DE93146109AA04EA1A70A25D1DB4E1B33BA8B0C89B58228AB4F6D1E02231C585245E91BF536B6E7A69CF6676B61658849B886E17282485A398F792D971B150EE283B4F2386AA657EC0E5F4B8D0A976EB15924C09CAFCD1645557372B7F5237507F44CB2F590402E6E113CF1694EFA7D9616CC2B1A9B5CA91CFD88EE1682889BB6CA84EA1C58C8E9C22079DA76D42F64247D7C6EC9E4D334DAE6CB4B5F7CED62420C15AEAA2DD6E30EB42D65AF2D0528EC179A451A3D7937EDA
	D969A6FFD169264B2D5B586EC11A7898G1B5F91A26333D937C43252D65A57AB31FD1936BDCD58E93199E265527F2F5ECC9B0929B79396AB6F1934B77DEF16EE194FD9575D61C9F847D2F51D7D7FDD2E471E333EE94379B48D70E957799ABB1E7BD622B5362EDF6B8C080A22094AD0DD500B67B575E31AE43C83B8E44376A31C0791A96D9FB614FB50D4677FGD0CB87889D096FE8E399GG34CAGGD0CB818294G94G88G88GCFFBB0B69D096FE8E399GG34CAGG8CGGGGGGGGGGGGGG
	GGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1D99GGGG
**end of data**/
}
}