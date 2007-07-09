package cbit.vcell.graph;

import cbit.vcell.model.render.StructureCartoon;

/**
 * Insert the type's description here.
 * Creation date: (6/19/2005 9:34:21 AM)
 * @author: Frank Morgan
 */
public class CartoonEditorPanelFixed extends javax.swing.JPanel {
	private org.vcell.util.gui.JToolBarToggleButton ivjFeatureButton = null;
	private javax.swing.JToolBar ivjJToolBar = null;
	private org.vcell.util.gui.JToolBarToggleButton ivjSelectButton = null;
	private org.vcell.util.gui.JToolBarToggleButton ivjSpeciesButton = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private StructureCartoon ivjStructureCartoon1 = null;
	private StructureCartoonTool ivjStructureCartoonTool1 = null;
	private org.vcell.util.gui.ButtonGroupCivilized ivjButtonGroupCivilized1 = null;
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.ButtonModel ivjselection1 = null;
	private cbit.gui.graph.GraphPane ivjGraphPane1 = null;
	private cbit.vcell.biomodel.BioModel fieldBioModel = null;
	private cbit.vcell.client.database.DocumentManager fieldDocumentManager = null;
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
private org.vcell.util.gui.ButtonGroupCivilized getButtonGroupCivilized1() {
	if (ivjButtonGroupCivilized1 == null) {
		try {
			ivjButtonGroupCivilized1 = new org.vcell.util.gui.ButtonGroupCivilized();
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
public cbit.vcell.client.database.DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}


/**
 * Return the FeatureButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JToolBarToggleButton getFeatureButton() {
	if (ivjFeatureButton == null) {
		try {
			ivjFeatureButton = new org.vcell.util.gui.JToolBarToggleButton();
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
private org.vcell.util.gui.JToolBarToggleButton getSelectButton() {
	if (ivjSelectButton == null) {
		try {
			ivjSelectButton = new org.vcell.util.gui.JToolBarToggleButton();
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
private org.vcell.util.gui.JToolBarToggleButton getSpeciesButton() {
	if (ivjSpeciesButton == null) {
		try {
			ivjSpeciesButton = new org.vcell.util.gui.JToolBarToggleButton();
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
			ivjStructureCartoon1 = new cbit.vcell.model.render.StructureCartoon();
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
public void setDocumentManager(cbit.vcell.client.database.DocumentManager documentManager) {
	cbit.vcell.client.database.DocumentManager oldValue = fieldDocumentManager;
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
	D0CB838494G88G88G560171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DF8D4551588C123C6850B9AB01570EB4094A346EDF6BF762BF6337BE14DD65A35EDEC59B66EE297AD2D5126AEF55376E512C0F4787F5158A2D231B5D89AFE24DF59C8B51090928C9248B08488C95E4C3C4C3C644D8FEF5EA41390E04F39BF6F3DCC5EE4A22EFC5F714DBD675EFBFE6E39771EF36F0D421637534BCBECDB842124DCE07F7EDAA288A9478561192A3FFB1063A6851CF641746F77G9B
	053B864B601B8E347A9C27FD0370E529824A59D0EEFE44E9FF83FEEF96E6CAA98D70A38E9F8F506C2F351E9E1DCF340E73D9CD4AF33A5740F79DC08E6070DB96C47E7F58DD4970CB991EC309CD908AC81D7C6EED8C578865AE00B4002CA02D3F983E2F924E93EB8E32FA6D13B30422EF283635580F29CF8ED5A15A3619FDF7882F14940510D709F78DEB138465C5G28FC2990D02B61FB2825577B115BA5EB55FDEEC9D12A3D2A9872555454092A968C860EFAE4AD28360881C9E9102312C790C217304FA6EF8569
	C3908EC23918607A5D48A79BFE6781AC5371EF90BB552ACEFB9BC0C8653A7D6D3DC3CC76364BDF9516F97F795575F1F4CBD629CED92A215B0E77DD25EF12F14829C7FB2FG5A0630537EAEC0A3008FA09960F7E47C3FD677BEFC8F3B343A205F9F8C9C8E050EA89E75C450A3A9703B2686A88CF7DAF287D550130E71EA6FEE8BF9C6835AEE6BE7EEC7ED1271819F6F4C538B04258F5FEDDBEDE113259917ED69E1EA0B1C30E1934A7B53383C7343437DF1B86F33021937705C7E5565963C37FDF77F6A952C9FC1B3
	F857EBB856AF909F58813F319D7AAA457F1A6013CDF86A1BD7889E4B19C02B0CF05FE8DB47E5E9AE1BA9EC58F70F259DD23F1EE2CB0AD09952A231325CCB7CEEF10472DC2F4B72G41971A70D41637889E4B15FDCE7B94G6ADF0FB560DC5D506F346F862887E8879882C885486C67BED6D13FE70CBE5682136BC8C4D6BC12AA50360F553F8EDFA92A29E2D3A02C0981376494D5D9F4A9D2D8E0F53ED10FB6F08DEB77A471FDB770B8A685A4D55464E0GFA977D12A629E1DAA73FFEA356D1A5C2F548FE899878C3
	826F3720FEB3FC95B12C1D89F9C4CDB2EAD07A72FA9C13BE5189BDC287701B5A65E74750DEF9207FB200A25D8E6E23585FA7128A0BC6CDCDF3B0589B89B9D09321BE0AF616485AD1813FF7C4390D1FEFC4DCB21453233C1FE9C7B7197A29175DA83E288E70B9768BB76A1E8D757381D682EC9BF05AEB815A86389F7D2C0D7BD17D5A2F883B3FF317259FCD821F8E8DD07FC99E081DCFE59E640332E681ED89C0A14006C127FD6FA05F8BA6383B624D39A86D8FE55A467A2E9DB066A14A1D9D351E1756EB4BFDF1F9
	15336FDAB6277137BD26FD6998782C5347386DB80EF18B6829G248C7231794E519D54570F08E109FB77C9959CD37298A3C5651DC1763A92463B1C4901F23D4DE425759EA275E6830FBC00A200ED17E13E81A8GD3AEF3DB6F6C8ED97447F5DDBB865D5E9E238DC65A7E8861ED66EB36737AB174B39679E2015A7C09866CDF6A1382DA3D9412829E7891E634EFB7209CEC7D688E6AED7289BE846BCD9DCC70300E7FE1832E8922262932AB22C92D122649812FC17FB161950E38C246D245F97D3481F58F7643FA23
	57FF2981672ABB9888C8E47075A60CDEC4568AC9117CA0F4DDB09250A87E15E398D375CA83EC2D7A7531D5822EB5CA8457BD9A27E409E85B10AA7B4125F06994568C61BAB2642DB459E891788746DB8570AD3A4C67FF7FCEBE7F5F9D1CA7BC5A9932D96DE929F36E30B906689C748D99731F4A74C223356C3FA0EB964307292CFDEFE33C99AFF8AAA56D96B851762D1A9AF1EB91D5E21577B13F5AAC1455ED3520BB02C145283333816B9C09E8DAB0F0CC8DC6C2F5F21F2C4803124728F36464EBB806A0A09912FD
	9CFFF79F5A46A5874956047569DC1F7C895923C17FB4006CA1BE57AFB5A11D2F43EC1257859556AF1A798A0EC733E70B4926F9C3F4BC56697468B0FA6315D87A6CE174F1A6FABE21AF5169D9D0DEBC022EC91C3E632A53FE702AC127FAF49C5FC06CF1A9A241760A9B52A73A5FC947B745501C4C87A95DFF9CFDD0B16DE519D71167F2A30EE77C3A58B7FA1572BEF88D6B4D7472FAAB2F327D639A5DC32A603BDD2F2778F83DA4C6CF3BC6771C9CD6EF47B82C77314C6BAD39C679ADE7754A461979EE6E617514F1
	34DE52B83AEFD58D6B6FE93DDE2A090E7BDD966B2F6C86B2C63D3CDE6EB83AAEAFE5758B61DBAD70B97B73DDB8B7C9EE51A37609516AF0BF2CAFB5B54749EC20B9440FC99C0075DBEE60394226DE6203D0CE874839014F63EF2D653942145E8CE169F37BD6474B03165CC065AD3A4118473B8B8F2F22BE7E03E664F9E0BC6CAB6339FFAFBFEE0ED78E0743C137CCC2A5B67677FCE21E67941776894E2DEED98DEBCD3090C7391F4C7C846545D80A2119BD26DFFBCF081BEB45CEF5960FDFE9B06B3CE87D951BBD0E
	4EED63292E5171064E79FE47EAB69615FF9ABBEFDCE6184CF3F76E9C564E9B11EBEA2C169C96BF7F37E78DDBFBF2C00EC2002A8908BFA0619A0F7ECE75F2FF40751AB4B8C7683AEBDADC3D1489D41F148926313C3A321C723BD7627CF201DEBC01734BA77C504FE93D0DA47FC51D8EA4014F8250F5B0CA70F2C056E49175285AC5756E9D7A83CAE93D06F7B6196ABB851EBB4D5CDC4B573D85B305C357BAECD639EC72FA1FEDF69255A5AF49502971CED7E9BBG157789AF572BF222535EB2114BFBDCFAD5B02D67
	82DF4F1DBE54D77349E1AA4BCB074D32642CCE311445F7FF2AADF9A24B2BA79A329C1861AAED1C41E5394B47E591EE04B575C6AECB318F671FC88441F4C78CF8F4F95E13F1EE78E1F3816702D94F73AB1F4C47A1847DE468FDBDA923FC1E20BB02936C0498903D90D9D05AAB16B42A67D7361B752C90A7DA6A19721C57D6F0A3552F62A6C34FAC30F72EEE73C3BDDC4F962813FC93176DE764AC096F15342E2D1757FD946A2D546B5E56DB4E74D6842E733F6AF577A6BB6D3EE4FE763265A37C12B8C737DB03E2EE
	CFF59CBA47F59C1717ADD4EE3E4DD2C70474E4F6F614EC68B8146EAA1D84C0E3571F0CB897283C5948BBF65FBCB2872179720DC4075A1BE93EDC2E63A7933C430427BAD77672F17681AD6FE61E633DFC9277043D37B86D4DG9E00A100B400C5377035787D668F0484B9F4D3GD25B0028348E8BCEF9AE5B0C3604F8121517B51B6DCC65701C42B8A19FF896812C3955E9FF97207ED6AEC36F0904B29C0DDA4AC06D7CAB8F5A59F36B58F2BAA33730CB58EE68B33423BE326B3C790C30EC63F1DBE99C9F49E4FDAF
	3A557091EA13352DE813E240D72640FE8E508860CB61B6F964446EC4B66167954C98AC77132E4F968FFEE6DB50F6EF0C7071641461FD4C0EA953F6FF98512EBA263CB8269D351B5B0171D081500A81365C863683F0G04G265C466DE77B74EF89FD8A93BBC7308EF00D90E7564566D23A2C5FCAF836E82D630F92364B3E4DCA476F1FC19D7300369CC0389D72F200DA00F600F137F39D4FFCFAA4110E07F94269886ABFAD9425725E6B3BBEBD67A5EC17FA3BD53B00FCFD3EB3B4225D7618F2566DD6F6BDF9966D
	3A98E84BG0A81AA6FF05A8F8134818C5D416DFA7F279F0E51F78E337DB856206C6C22677A6C591230DD5A9DD66DAAFA2F0F5F6B895BE5DB727B376B6477CC42F6F9F7D80D5F13EDB8FE4B00A6CC023183B8G5086208124CE6263774B4473C2BF329DFDDE5073D31B8FE312AC68BF970062FEA4A3EEB91405497C1CAC2597F16FC279200E3B0760DA202C69386D3DED897447BEC623A39C0CD3077278EBC29EE3B917AC0F0327F67E4D497867E32BDA2C4F475634D81F0FEDEA59C871500779341E53FF5BB27A79
	59BBAD714F4F14D371658C1F3216F36014351C57CE0DAE67CD27624979963179F7ED982B291100A67BA552B50DCF25686F294F1E0E84285BE26F61583DDE1F8BEB4FE38E12BA197B7C76537BADFC1EA6D33C9307A42AA73B3BF507EF8DC9EEAE77565678B65A56EAED23F7DA2DEDD455BA3A0D76360EEE2347DFDFCF5A0743D218AC347522A6B25AFFE9F88EB7625C73D3184D820F6BBF753E4A6A9C5502F57BF43EFEC7FC9DB507350E038E4C3B4241086A16E2753841D129530C4523C9137CA88E2DF38BE92F09
	2AD79AE1074924BD2545EBBF4D914FD6D4578A9F5735C58B36983AAEBB93DF571F1C31567545B371F5DDF9A6312EA54E783AD6B8936B3A11BC82F0499ED9E599B755715B43F45C2F6BBEC7B60D7389C377F3E7636B5EF556DA776E3371F5771FCD2CFB5F59783ADFB91BD8779B4F0EBE4E83BDE69BFC2063BF6EB55B40304DEB0A1D576FD603FE708C4A6769367836A9E833364D0F5B625B6627ED09ED73F2DBFC5B7C2AAD31EDD61D33760BB7FDF8F721088341088639551D38AF6F7571BB0AECD89B73G0AGAA
	6E24F71FF42DFD289F7B6B0688GCCCC9641E25276E5193775C1F988A08DE0914052BBF95BFF2F447D7CB81E5A9D91D5AAC730815774632DEEB528A8F8F2350F5E17BC5EEB3EAF297F9215C1016FB8GDA6705AEF31DD4D6A7973E79DF62EB7B4673EB4D3E4156BA1A7B377978DB13C228DFB5054B392F8BE3A57DBC38DA6C57EAEA0E2932670868EDA69623E74269A4364176DDD3781970B7C91F29D04E82D8BC051FA3C67F484F91B3674E9416BCEBFDA6DC6038E2AB18424E306FB21DF33D6FAA25BC3775F01E
	35C00F5E053863BD663B8812CF17511C1052070651F12E60A108C1DFB00C0E723EB82CFD41B0BA5A684F2CBD556D09BFF05D0A1F489214C712623E7BF91469340C6B764D29A54929AD2B285D3B2E7DD630BC07E79E435F7314A8950C77363B395D07AE11739DA8ABGC9F77331749EFD4B3ACF4363845E6FFBA2FFA7142677FB358B7BFD8A4AABG4A6E61F1E8C7423C4DBA36D42E335D1C4F5C0E1EFB2513F34CED7750F32F929DBF0F602BCCF8BAA71E5267448120255E434F437E65824E35E2A8D726BA6DFB81
	9A81FCGC929FC2ECDBD3FD7C810BBB80221E6D8C095D3E8C3757BFD423CAD96286FBD361A7BDE76BF4C97F2EE2C0FFBBE1F1A4A5EA071EF5E5492A3BFBAF79175DB8E3432293096G5482B8G1429DC3FE74E33F3A58C8DA192945DBEF24B22871A3254EF20F1F1243A2DD2305D3829146F142943F30F0C1872A2A853B1AAA4BE3DE4AA9D23557A5895937CF2931E0E5DF8024732B04DE977CC633E3ADB9C4D360B2745BBCBFD09787E52E914CF29411F60D7187014FFD297BFBFAE86DA573D1C7FDCD9507D78C1
	536F6F7B9265D91F87A80F1FFF21BC583B268B7163026FDE300E8B16DE300E7F7F634268395A73976267EAAFDEB03FF520FEBD3D83576EDC980B820032B4183FG3B5338CFDFEBFF077A3405BF9F75F8B1AE4197DE9A676F3A98DFD779E262F7E16AC53CEFF185B5AD68477EB9BEFA9147CB8B06881245FE9751C62A6C75E934AA43CF38086B34A2F593B4BDD3F80B6C890D20D7D79ABFE7586EC71C8665E49D7765GBFFB4854F1151790D78C65ADDF66BE7BB219CBD5D02E85E85771B69571D1A8278264687864
	CBD6FE06ED2D7C265D82DF95272E614BB3ACF944BBE7D8ED019B6D5C025A22CE64764957F5DB590579F255FD3A8DC4AC576A65DB3ABE4F9C43FE934F63D91F0BC722739AF6F659915F3797F4A4766D2FF6103BF770C947C008455F0BBAB04E8EF6F707A5FD8EBD5631C147B505DB02E19983F39A8F9FD050673B15A0246D744CA2FA9FF92F2270384FF39F3F437B0B9F7BF685D5129450983E594FE362C5D06FA9000A69CE7BB6GBCC79B1A0EF53283752C4E0169A3ED4F6301D2860DD0279DE0AB9B0B50F4A3E6
	20F26C3904FBEED01541DE74AD82157958A59ECF65CE67B2FB4E607CE9A5774274FA1E5DA71237E68D1208577B1420777F51912461FEAACBE11DC65F617CC5B54726C5CC4FF753A19DA6747D1A957D20CE7F7BCEABFAF3BA3755F9115B2AABFD242DCA0561365220CEF2BA359509A353E3ED95F893573FE3781E01E4BB82FB9F7601E84EE5B252691967D370DD1E4EED77269F67FA37280451AFE87C135EEF3ECFBB785AF84BB7E388428CAA7F9676CD794DD47666DE7373FD3B99E8C9B3F86CDC3E835F9644464E
	555461EAEA0EEAEE1F64B9425C0F45500B7C5CCF974D60B17473247FA2A82F1989E383C06BBE9B60EFEA8E816EED2223A0D488463F1138420F3519F854D159AD68B6F8E68D0F8137BDB0CB287FDE3DEDE79C9B644CE4F76B330C5C6A512E832FD27BF6DC42F6E6D5DB713FFC8B8CD7BB0F1EF6B4559DEEE6EFCEFED9E9716604A7AB0E20572BF0CF267AAAEA855327F69637012CA18E777C59BA2E301363AAE6F35C2DA23715B25B4AD6059F473755F82FA07037BD33AA36474A4CDF5768CE494732D74F07E64F66
	E3196BA2B1A1140BG564C6179419E128B3F8B655AB9BC36F0F462DB9E2758B372C49510ADEAB0A4295AG451297ECEC2D9F74A74A853A66D85D0DA4A7EC97FB57C55BBD16305D083D1244419D813445501C585C4ACB7049F3E2F32BC2BF4F2D26GED659C3EFFBE9FB073EF1F8BE3BEF774BD117A7D13974CF3FF43B44546794556CFE27DCD1A9BFB171EA5637A168B788200327BE12FG588B5088508E9082C882C8834886D88C30942090C04800188920AA030F79456EDD230CB9BBC4A1B3CA95E58CAB5998DC
	D2F1FFBF14B1DC6F96D67630EF54C4CF015F69G59B1ED96C3F9E90615FF84B464D198C3AB1BC74B156C3BFB1EC12F055F0EF9A34742873821F9D6BCFE5109BCA64550E633F28E7B66196885703B4802C745FC980B79D6BCA68D4B6776C21D7A79E30921C2236601ED96FDD0FFBEC87E6EC5199F6B670789BEBABF564F3F61527F068BE84B67F35FB86043BE755C06CEF99693FD6843FDD8FF5FC642887AA20B5EDDBC3AF9AD21ABF2C02A23FFC1226B65411F6C375F747BE57F971FDF2E8FFC11BCE8CCDE924435
	357981EAE70ADBC5F09E0623F37975DE541D4518BE5C3BAD67A1E4F647E10FBB696A01984CA1FA75B639F27C787789B9F17C7B14EC1D5BFDDF9E195B7DC8364E6D1E154D399D4BF9E55C3B3C30F6346975DE50F167F4DC916993F1E7E50F6653712F9039905FA8A1C30E97FAD02EFE02EBE338F2936E9C433D5603F1B6286C8B2A3C6D7A9E4C013AE5C521654D240E9C0010DE37C7E58F4AA807C4135CEF6938F37AD8FE09448563E0ACD39F60B969AB84B79B4A59BA2EB30C38C7211C2F6352A208DB81650ACC0E
	AB6BC35C96A8D769389384F7884A4DBAEE500938AEA88F693805AA4F7FA669382D04EFB61473F45CA40D4BDC2063DE54384CABF45C698817F9250E6B77F21957FC0563723DDC3ECA9D772A1B4B37DB471D6E6072556A38C7BA38FC9E9D77C69717AF2A6322DDDC3EA49D77F49717EF120EBB5549651B2D6396F6F27932F45C4D9EAEDF2E0E6B6E5473E79D77F8A7172FD8475DAF0E1C531922F9CED35CCDFABE3EE1810F97BEF28B7A7AF34C74FB096B0BDF6F1E7BC2F95098D57C23692DB3194A943FAD3A564073
	294C722A792A1D2CB1C145A1079C30CE89B4766D4367292C4E46B0F9FB8CC4CA1B6BB7E8FDE1EC6F972332BF62EF1587A516435ECEFC7FG583EF6814F439E0ED83DA726FD2E8B9AFD7E0E4CB97A86DA72507E76777118DA517B737421DC32BB9820F586C91DA120A76BF5A6137B6110AAF5CB2AAAF908047CECE0BE1947CB216EF2002A87094F29BC1F5C7220F55E3AD958CDF586038E57391E689C02F6C3GD42FFD7827E4CCC747A5C167FD194C6F8C28172D57CDD18DFA89498B8A0036820059004C6B5AACD8
	0B32383CF28717B71AB5D2DE5A6E07E41E27GBDB50B73FAB0EC4835DCB3785A483C4905FACB743A76BE037E83B2A70B01D6719017EBA1194F6DD076BD44653A5249651A72D0BC39DEA2FDE5GBD9B005E075E2767738500AB82583250E9BFG60D84865296EB7FCE5F284F31300543F03DFB9D6574BFEA900FF44DA357F606E9D7B8F6E62F51B083FE8EAC4227D4C9BB07A111C47CC7D70BC3649AFFA25A6F030AA3CAA4683672ADD55707334C4EF1F2B5E59A3E315709E6A2D55DE397B1DDD3B18DD73860D7E6F
	D747A2A74D9FDFD0F94E2AAD6477200BAF1A6F8AF77C692465DD614A0B03364C05545ECB969AB9CBA6646AD4267F3DE24834D59B0BCC34DD7BD5231D629DFB3B7F39E634ABF2B5FEAE9BF753D3AEE6E45A7F7692237FE39DE337719D9D5C46E58FF33FBAECB37ACA6B9C3B0E21D223DD43E7E8B72159E827F9BE1FED427414EF18EDFEF242687F42186422361955291FAF6936D9F752686B5BDDE36FABD7BFFF519E66776E45E26C3DBB3F972774EAD76C3DBB8E32C16FF2455E3B8FEFFF27AB765E1D3727FBF111
	EA4E67B2C12EC79F9EBD8F24F942F272378DCFC15DC200AD5990DF81548234E573DCED4365AAA1FE9ECFDFC198E9BC15E7CA07D99E857A9A17BD163CB423535CEE8A344998D33B470765330B204D12846DE8CCE58F73586B140E5B906631976258FD5AA8EFB4D71ECC1C8F1534D867C32BDBC666C374ED6748FCE8D34B483B2E87BAC64A1F55E1161F623E56B932DEEE6748FA85FAACDA2A631E55E3D144517FAF4D5468733032E47ED4A88F2B10G71B953C4121E023E8FEACA7FF3D629CDA8EB92D6D1251B04B2
	143CC9A8A70AB5892520F414C49266FF61CB82D91505B2B3E8021DDE936C4093D5585AB5E1955D1DB4E18D3BA8B0C82B59228AB4F6D1E02231C585245EB4F2269F3E7D696FE5F656EF3189B6905CA7D1C94B7BB16EA43262E320ED50F627BB682F16DD7E6AC81F3FDAF4E955924C09EAFCD164515737737F541563FBFC6BBBB6213CC930F74B1262E9A5ABDC333001146A2530DB15C398BAEA4226E694E70FA2863CFB68E147074D42C6D20F2F0C58BD1AE7FD33305954DD032CC88103344EC4BAF33AD99356511E
	831236874A9F92BDCAB4613755556D64AFAA5B69F25BEEEC77A04D4898G3B5F94A26333C637C42252BA4A6BF7313CCC5B1EA6EC36588C31F1657F97B753C6E262E642E2636D23F2537FE6691A791CF565C4F75CBB16649C767FF74909BDE7CD390C73A99F70C5B9FC0DDDBA7867F8EB6CE117566A9795C593155E43818F1E579C9950A4639D40644B58FF1857E1C4CACB3D0CF277185A7C9FD0CB87880D36F283EF99GG34CAGGD0CB818294G94G88G88G560171B40D36F283EF99GG34CAGG8CGG
	GGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2999GGGG
**end of data**/
}
}