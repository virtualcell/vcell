package cbit.vcell.client.desktop.biomodel;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.mapping.*;
import java.awt.event.*;
/**
 * This type was created in VisualAge.
 */
public class MappingEditorPanel extends javax.swing.JPanel implements java.beans.PropertyChangeListener {
	private cbit.vcell.mapping.gui.InitialConditionsPanel ivjInitialConditionsPanel = null;
	private cbit.vcell.mapping.gui.StructureMappingCartoonPanel ivjStaticCartoonPanel = null;
	private javax.swing.JTabbedPane ivjJTabbedPane1 = null;
	private cbit.vcell.mapping.gui.ReactionSpecsPanel ivjReactionSpecsPanel = null;
	private cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel ivjElectricalMembraneMappingPanel = null;
	private cbit.vcell.mapping.SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	private boolean ivjConnPtoP4Aligning = false;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public MappingEditorPanel() {
	super();
	initialize();
}


/**
 * connPtoP1SetSource:  (MappingEditorPanel.simulationContext <--> StaticCartoonPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			this.setSimulationContext(getStaticCartoonPanel().getSimulationContext());
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
 * connPtoP1SetTarget:  (MappingEditorPanel.simulationContext <--> StaticCartoonPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			getStaticCartoonPanel().setSimulationContext(this.getSimulationContext());
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
 * connPtoP2SetSource:  (MappingEditorPanel.simulationContext <--> InitialConditionsPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			this.setSimulationContext(getInitialConditionsPanel().getSimulationContext());
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
 * connPtoP2SetTarget:  (MappingEditorPanel.simulationContext <--> InitialConditionsPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			getInitialConditionsPanel().setSimulationContext(this.getSimulationContext());
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
 * connPtoP3SetSource:  (MappingEditorPanel.simulationContext <--> ReactionSpecsPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			this.setSimulationContext(getReactionSpecsPanel().getSimulationContext());
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
 * connPtoP3SetTarget:  (MappingEditorPanel.simulationContext <--> ReactionSpecsPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			getReactionSpecsPanel().setSimulationContext(this.getSimulationContext());
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
 * connPtoP4SetSource:  (MappingEditorPanel.simulationContext <--> ElectricalMembraneMappingPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			this.setSimulationContext(getElectricalMembraneMappingPanel().getSimulationContext());
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
 * connPtoP4SetTarget:  (MappingEditorPanel.simulationContext <--> ElectricalMembraneMappingPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			getElectricalMembraneMappingPanel().setSimulationContext(this.getSimulationContext());
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
 * Return the ElectricalMembraneMappingPanel property value.
 * @return cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel getElectricalMembraneMappingPanel() {
	if (ivjElectricalMembraneMappingPanel == null) {
		try {
			ivjElectricalMembraneMappingPanel = new cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel();
			ivjElectricalMembraneMappingPanel.setName("ElectricalMembraneMappingPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjElectricalMembraneMappingPanel;
}

/**
 * Return the InitialConditionsPanel property value.
 * @return cbit.vcell.mapping.InitialConditionsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.gui.InitialConditionsPanel getInitialConditionsPanel() {
	if (ivjInitialConditionsPanel == null) {
		try {
			ivjInitialConditionsPanel = new cbit.vcell.mapping.gui.InitialConditionsPanel();
			ivjInitialConditionsPanel.setName("InitialConditionsPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInitialConditionsPanel;
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
			ivjJTabbedPane1.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJTabbedPane1.insertTab("Structure Mapping", null, getStaticCartoonPanel(), null, 0);
			ivjJTabbedPane1.insertTab("Initial Conditions", null, getInitialConditionsPanel(), null, 1);
			ivjJTabbedPane1.insertTab("Reaction Mapping", null, getReactionSpecsPanel(), null, 2);
			ivjJTabbedPane1.insertTab("Electrical Mapping", null, getElectricalMembraneMappingPanel(), null, 3);
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
 * Return the ReactionSpecsPanel property value.
 * @return cbit.vcell.mapping.gui.ReactionSpecsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.gui.ReactionSpecsPanel getReactionSpecsPanel() {
	if (ivjReactionSpecsPanel == null) {
		try {
			ivjReactionSpecsPanel = new cbit.vcell.mapping.gui.ReactionSpecsPanel();
			ivjReactionSpecsPanel.setName("ReactionSpecsPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReactionSpecsPanel;
}

/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public cbit.vcell.mapping.SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}


/**
 * Return the StaticCartoonPanel property value.
 * @return cbit.vcell.mapping.StructureMappingCartoonPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.gui.StructureMappingCartoonPanel getStaticCartoonPanel() {
	if (ivjStaticCartoonPanel == null) {
		try {
			ivjStaticCartoonPanel = new cbit.vcell.mapping.gui.StructureMappingCartoonPanel();
			ivjStaticCartoonPanel.setName("StaticCartoonPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStaticCartoonPanel;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

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
	this.addPropertyChangeListener(this);
	getStaticCartoonPanel().addPropertyChangeListener(this);
	getInitialConditionsPanel().addPropertyChangeListener(this);
	getReactionSpecsPanel().addPropertyChangeListener(this);
	getElectricalMembraneMappingPanel().addPropertyChangeListener(this);
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
		setName("SimulationContextPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(596, 515);

		java.awt.GridBagConstraints constraintsJTabbedPane1 = new java.awt.GridBagConstraints();
		constraintsJTabbedPane1.gridx = 0; constraintsJTabbedPane1.gridy = 0;
		constraintsJTabbedPane1.gridwidth = 2;
		constraintsJTabbedPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJTabbedPane1.weightx = 1.0;
		constraintsJTabbedPane1.weighty = 1.0;
		constraintsJTabbedPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJTabbedPane1(), constraintsJTabbedPane1);
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
		MappingEditorPanel aMappingEditorPanel;
		aMappingEditorPanel = new MappingEditorPanel();
		frame.setContentPane(aMappingEditorPanel);
		frame.setSize(aMappingEditorPanel.getSize());
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
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == this && (evt.getPropertyName().equals("simulationContext"))) 
		connPtoP1SetTarget();
	if (evt.getSource() == getStaticCartoonPanel() && (evt.getPropertyName().equals("simulationContext"))) 
		connPtoP1SetSource();
	if (evt.getSource() == this && (evt.getPropertyName().equals("simulationContext"))) 
		connPtoP2SetTarget();
	if (evt.getSource() == getInitialConditionsPanel() && (evt.getPropertyName().equals("simulationContext"))) 
		connPtoP2SetSource();
	if (evt.getSource() == this && (evt.getPropertyName().equals("simulationContext"))) 
		connPtoP3SetTarget();
	if (evt.getSource() == getReactionSpecsPanel() && (evt.getPropertyName().equals("simulationContext"))) 
		connPtoP3SetSource();
	if (evt.getSource() == this && (evt.getPropertyName().equals("simulationContext"))) 
		connPtoP4SetTarget();
	if (evt.getSource() == getElectricalMembraneMappingPanel() && (evt.getPropertyName().equals("simulationContext"))) 
		connPtoP4SetSource();
	// user code begin {2}
	// user code end
}


/**
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(cbit.vcell.mapping.SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	fieldSimulationContext = simulationContext;
	firePropertyChange("simulationContext", oldValue, simulationContext);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE5FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BF4D34715EE93DA1AFA8FCE6BB4262509C320F5DA17528DBDE55B34077650AEE95816FA6104EDCC16ECFDDAF6C33B5EB65D65A45E6DC87217E06200A926C062A490CCEC64AF45887331A40CED8C7EC8B636E54BFE361EAD41131E7A740485F1E06F4C3CF9FA12A5BB1060F32E5F1BBBF76E5CFB675EBBF76689153E191A275317A2244BC34A5F899DC259758865CE347D9D43B59E49A8C21A3F37G
	DEC74F8D664233957A3A86B30ACA504E5CC2E88FC1FBF5DFC651DEF85F0D2EDE9C2A0617B873B4C05F31B776B74DBE4F1EDBEC1E8252DE6058814F1DG2300434F74A3F87E2FBA4A947C7D8A1E01CE0FD0F2852679116300024B861AC240F58138AAA87DB4BC29G73CC0D8A5D5003E968251FCA7AE24CC74313C126B2969D254FF27432AE6DA81E6BC5A2A79E5388EDBFGD65E00567BCE4133296B5A641971B10FEC3CBE4E8B02F1DC7070BE5968628357E451EF9C7308DE51458BA653A54E6F7778A64F3BBC32
	A8F5F1BEDEC0075E437C6B078A89BF0436826F658437F1884F69067784001477987E51E10CFF895EF7D4E6949D2AE4FA1EBE522468517F7EF7507ABAEF6EEBF174EC2A247A39AA43FAE6DE5B245FC7562477B636FDAA742D8658881083F020AA232889E0BF710521DB6F4133F9CC3608DE2F68EB767B4D02CB3A043504F71389FA94DC8FBFAECAAE0468FA7F1723AA06BC33819D7727BB9EC7ED12D64F56BE3DE7994AFC728B7A0298B649CCFBDFEF2F2236902A42B621F3B78E451BBB31AA52B7A3673E0234F307
	3E39411097E36E64C59B8C298A1F951A39332A70DAF71035DE8F6FBA45BFA83E0F60AB0E057154CF1FA7BE035B0D50376898730D7E1DCC160E5C452824766118F6C879D1229E99298CE546E8D9F6BBB1EF2B114ED9244AD2CE70239ABC15E5A91191372721EFB3G75AF278E472D35BA23E894A0841084309CE0AD4066EA66E3CBEFD5FFC89F6B62A4599C7488AEDEC2F42C73761F614907E409EB7385E44EB74E77F112079B9378GD2E8A6EFE39B0CC50C93086F0F439C97F89FAFF132C77481F74E4B4B3C9420
	B4415B2FE39A09A73DF60F1707893CFE4478FEF0FBB776A3AEA0777ADD1C4C07A9E87F3C5B58A65739BE60888C601D5A6533FAECAFD413D1D486D0D1436CC061A2AF79F08A699045EBC13F9DEB02C2B5584E1DA4F7B4413B3D06597853860CFB8A5A6BD5BEA750AE8D9F2BE79C0B4FC9B7D80C758C635C3C2506794C6F7A194FD80B3F012AB6BC9453E78A3F3C413023167A4A215A685899A2BC31DC341DB554BFCB4C62396FA446E3670A7F1DF58E43C7603FD3DD2BBF6662356AGFD5DG73EB190DA5501D782C
	198B704CCBBBA5F0B05E9536B815F31F33C431A716A9CF111F763DCB7AD2016F9A00EC0032BAE6432F8F77479877D1F41AE94355E48EAD7F220FF18EEAAB378E6FCF7CF558AA2D3C1F773960A5207409BABC1F923F9322BAA6C870FE08F78B84D8C0453F2F43B149493264998B4A3C0D17E558E7437D7711398241B1FFB8D530399EA0050CB885712E52AF5061D89997FDBE1EAC1ABAC469FF1044AAAF70DE905AA286FDB245BF2C477EFE0D3F21640AAF6973910EF5A108738E2D9916F32FE29DA40F97DC8127AE
	14D40763F8E228CCE323F30043350F8B1E21BA9613D36F3018BCF4738934EA442F0F352724ACDD205FD8C7E3E9EBDDB8A629CC09065832FF0E648C859F2032A65463BCEA73F8038249F69651A7432A5052C50985E3BE663723BE23289C20311E45424EDC5C4F720D928496D19028C9D5DB3F120BFDBD407FAD48C31A46096D22EABFFD5E2E283EBE451634FFFB9E3625204909EB6B31AD871D2CB66C2A27F6F443F3BEC0BA409B0027F4B5A30C2E2CA1CCD7836F7686AD5D7F29F43206AE915E17C7502D9DE5F46B
	B4F4DB60FD47F1CAC76D7A0723F82DC95DF8153B4E050C01A970D713291DD81656046BC8DE4774D547D96D17B302ED6D0276B4C072F166973FA8E635DF7235B4E4DF3E5E902F46CDBFCE7DE15D7130DFD4A4CE9968DA068A701CA5FF05357CABDB47F9F95A7D27B9909047BD449914B5E848577A8D451D4C5F8E4F890F94105BA030C3EC3D4E66E3F9715E282015DA61BEA72BDB9AE0DE8140C0687F430963BA906DFF0023B1731FB6ED4CAC01F1DB81CACEC0DDFC02717873C8BC9E54F63FEFBE270CDF3BECB1DA
	D910280FD5B73B3F1A22EFBA21544DA74236FB6A05CD7AF5GD40F019126478A20598AC0657CC52FD64EC30DD4CE97BCC30DCC4E2C38F2D25EC7C6996FA49833261171DEF6C54B9B1D243C1B6059F512717E7B5159F97FD1656D06B129A7996F7F9E54725E2870AEB7E594B518986F84AE9EEF3A17EC18D1BFF718427B7C28E9661ECF6BCABF11C9B6C5573EB7897EFED3F46D2BD7F5C8063EEDA6D68B7DCFB356E16894589AA089A08DE08D4016D34C477FB19F0E4933571AED2015646384DB44A642EA3E82BCAF
	64FD257D09829C37A46F2BF6780CF32EB3CCEED324AD683807679CD791B50E66185F571EC4B34F26DEFA84B5CE86BDA613CD160263F2D062150329859493F145CB523B92E35F9A0D58171AE81DA940B38100662AA3F6162BD21AD82EFA23972F47BAE8EF81C02759FA74DAF00EAC03F683C087009BA091A08DE06DE936A6BB8A4DF32DC9B35B706D227AEA095E34199DC7C66E4E7E5EB947E51D0EE57FC53575F3593F4D6711BD1CGA23BBCD8620076CE20280F59B467B43369ABFD58268DE740EEG2EB32CA6BC
	B6FAF73A1D1AF35C7419D83A5D2A2F1ECB379E1EA32BE073736391FA7D7A8A53AB798C534BB20875DA8B6DAC009CD52FD76ED22FA2EE2EF125E7E369958C453A4F09506BBC2EDFA44FB8A7DC623DE392A826840FD6C723C35D3B4FB29D8F1333398B5A53G49G747C36156C736950DE25621EF6E2DC9634F34EB2DBDC9F4535E579B956CEA0ED1C0FE93BF0B47A5C03E9E733411DG1D632D99F3FC5473149614732EB97E3920409C7BDCD0EC0EFDAE58EDFE1D620107761600756FB74FFEEEF8479C6B5CF01068
	7D9BF98F0AF1C6384CF9F945AE3B51AAE711C253A50BDD352A1FD5969EC4B16BBFA8E247F897F6A568273E746F234C179A4FB1DFEA30E1DF72C3FBBEC02A6A93D576597CFEE514BF2C05B65DD3BFC7F27566F3F4CF45C071DFA2782D9ABC5DEB8F75327B50ED50B7544CF27608A57E5ACDD8E22F1DE04135EAC08CCA63FC747A84ACE5EADFB83137493C971B1952CC1371B2A7CD72B35677BEB21E76459B1FE841E708B10F4BA3A9A7933AF6DF98512EDD1D9A83BB7BE633712A66C89B2FEFE6B6962FE01BEDE90E
	36F10860DFEA0E36316E8A33B1B2C32EB733FA4BB52A152BDE156B45AB334935469CA9D7261949759D12FB334D51F22DA2789CF334DCED03CC2EC28B548196A6D79D27152BC1152BFE563B5BF516C83932ACCC2E05A4DFEE33C44B351860F32D51F2D58EB139CA20CF36B2398C43788CAEF0B744A0942556731876AF432C265988F459GE5002FBACFEB9B9A235F1A42FEB1A1F1DE88CD3C0B217296BCF633130DCD847AD400D5G1B81D08B1B7360CEAC0BA61AEB29ADFE97311F39DBA2F51FEE09D78B2711F8CF
	EA093EFB7DB6412737C45F3D1AEC6C6EF58D74D5DDE07B0A454E7615A495779502DB816DB597D89EC969C56A7ABD26F9FF2FF7363B350FE70F315C53B9284E077B189D562BF6185F476C30CD35C370CAD877DB1A7702AB775E8E6577F48E2A333E1F59A13715596119810C2B00F6D3AB33437787433A7FC4735EB9F86F6D603E27F3D01D5B9D4C8E2D2A9D3EC1F0823493D4BBBCBA9456BDCD73DEBB6B1D7347E30776FBBA874D9B66E116C393C167552DAC975612FBAD35DEB1F2D33249F4C172384C5CE487492C
	34D6F9E038D0991F534AEA151504E7C39B1CD9GDCED6C4EAA74AE3B33CA7F4AE2541A9E7B4EEA337D837DF49B4D0B29ED61FB176CEB1B74F44E7C91B6E7A6741736137AE824D44383676482A8160D70247D93917D3886BC0A347DA9917DD85EA7A2467FB422BFCFB31E6A764CE126DB4EB34B11706D7B63FEF39EEA27BAA5A84F242C053A2C67EE9A288F66F1D837155037261D7965713D4CD7B35B19DFAE3361BB542A0BE0E3GBFC052C55677BD52B7DF395744F71678BE12761D33B11F4263BE0C1FD4A9F47E
	A87A249863699C836A9CF99F52978D1A71DAFC2CEF906BAE127BDEBB5B131F0AA1875D133FCF76B9199B33FBE401AF6B403FCF50DFC4AA0F26BE46E328E3A68FCAEF6A88536F6BE7747E9874F44EFF9B18B9677025B08FAE7CBB0738BCD6BBE672D8FA992976ED9D3E3BEF6AFDF7B9EE419D0F23354A83640EB725A33A1E7BBC41AF69082E67FE2066226550D7FA095D9F4D6F44B68BC1BB99A09DE0B5C096G3A4C4EA1472CB5E80E3BA33B686F00030A20B996D27DDE1D733EA79AE84C7F3000457C0A6FFE8D2D
	7C14D57FAA0A9D73251769DAB7A84F0ADFAD5461A755EFFFB7564F85FD89GCBG1E82D88730C555AFE82D227A611F12B44BB2B76EA6376FCC39BE8FBF95C66362157A52818E5F99CC08BE3931935B380E64179C60DB886D55845731572A48B95DB953A7599A65A97E1984B4CBG76A86D151D61F524F38E1F445FF0ECB2A7FB46A3AF98E97F265D789BD31C2BB005E6DB81669163CAC969977661EFABF3DC4DA8676984752EF2C7975B0B568CEB73F5F9E5E74CFC3DED78265E5AC5ED626E8A6FC5893EF2836573
	F3077637C6A5DF94743A98FCB0ACD24627E978D0794654FB3F4CAEF63F3AF40061CA3B996E536A9DD3C7B74B67C9CE244669C24DBB7D1EFBA7BEFD67F9E153BD1DC3796E5A997FFEA125B3767D42A31D316F0616F44EFE77135E997F1B71131D5AEF46B4EEDF6C41B5417D309629G2B81B682EC6DE6B15B5C75B6A2B19BA3DE4F3BA6717D867E250A4C66BF549DDF574A6E39FF5F52500D3F410D0932ACFAB1FF06B7F5637512C5BFC1AA5F653A310DA44F24DB26248A7EFCB7290179890226FBCFB2DEE6A56E0E
	6E45BA797829F2D6969ADBA039D43C6D02D896A75F2EAA2F29AA2FEDB84AF2E46A14B6BE9A37FFB26677EB8C05BDB4AE8EA84F1CBDDFD46263033D4C6F6D5077A6223F0FA9C46CAF7BECAC9C15FDBBB93BD0D9DE5841E27E40579FC756E72D7AF762481228481059930E55E408F96A7773A26E5CCAED99C5953659EBA96AAF15E46FEF825AFE00E900E400B400D5B666B39B3AAB605947DDD5F6B07A4BC162B5002415B7719B097378E44A77B2B9175071FF69B99CEFBCF8DDBB2CD96758D5C81AF6EE9231B81E0C
	385FDA8F32EC19C39FBA6EEB835AF1B9B026447EE146A5C65C8F9D02B10DF30C233A7D21A7FEFC3C5CB3F7FC98EC643BB578357D06DF398BAC34613BA2F1E2A2402BF1D8CC6AE70AEB8BF4098132BFD1DBB71273CE87486B3633B57B177E4AD9562CCB927D3CA45F20D872EB86458E1F5C2B3543FC6037E88EBBD099E4A75E2F17836DDA00EC00425E0C2283G8DG2DGC3GFE0079G29GABG56GE482EC85C0FDD0DF75B179BB870F4EA23FF2688BBB9DE2E768C5E4DF2DGBEFBF2996EB102BB8738C78DEA
	DEF244DF37D30E39574D6A081D575A9DB373DA27A3F6DE6BF5E8739A1575D69F137FC183137F3EBEA6FF312A538F87981DC3257B49G237BD7BD43BD6EE0F4F96A58B487237B879D136574B0DECBF59F22AE41F49D46FB0A0756C61E1B647B3A8D88FD13145F423DEC8E1D3A8E0F6CE5F368D4FD67BB635BFC01F3EE1B27B8E35B7C9167CC1BBF6E0CED73A71CDA1BD35CB2A74E631360D37DAA5D13AAEEC0452DA4BCB16E0A47A53BD57C8F08DC985F4A638999FEB511EB0A607A955C539A5C00027B199347BB28
	6C96A5B6761F1D24C6768882EDBFCBE8BCBE88FF559E19C4C60F1F53487D020A43F2537A18FBF7C7F87D086894FFA0D49C46B351917D1D7DF77BBFG3FCA7FAD4A13516B6A977AF225141551AE25D4AE8F574AB2AAE1353106AEDC8EB3E4C0C245919B750F9FBB7E6B354F77858CA83F8D6D4367829BAF91D763DDD6CEE63FD1EDC1C5FE91E25D8678B6149BA00F3C00FCC360BBD069C48F7EFDD6C066DD6AC7F810E8CF871E29BAA8FB046AD6AE601631C31D6AC039BE7CEBDA340BF68B1CEF321AFA7429907E8D
	2D2EF994A1A53FA110F7B71197F3B12ADCF6FF6F3543EF35E72DB4A0FD8B5AAE081C2B85149725B6D4A43BA5BE609685D748FF769CE1D4B8055D8E9F458C40EE3F2ABED656E88737D7053E967AF2E2711662969C41922AA6147817ACC61542E5F1F392CC45CBEDB27EF530A89DD27BBE71067C6EBFDD58BB24C7BAC5493F21DBA8BF6417D17E84677588B76045CA4A22G6E59AE0334F0C689CC081297A4AB0F120CF5299398BF7FF44DC3CF3CBCC8A6888F15511B5414B034FA12973D554D93939E9F2FB2G3B82
	7B03AAFBCCE30C225995FAF0CF7B06BF0D019FD81141DBD3C37E57127FF578FFAD415492CCED9D98B79B4D7A975D9FE2BA936781F9DEA3726065B736408A4A3F7C6D530B1FEB4A5E8A5A3520BC724B99D80412B0556520F70C17BE157E5041D713B7FF962F119F3BCA88735D99D2B9E28D5BC82CEE3EDEEFB27C6AF1039E653621FC1A3B40F9F1828267A579298D99A0F7913F4B0D82DAA39E3D2D2DD7374D9B0FDB2FA68FAA673941F00DC877678CFA378CF864E0FB6C5B0EE3A8FEDDE673F202A0F34235E61F8B
	57E8669BB29FBE17BC6B407C830CC66924FD4F137D6E2AE64C7F83D0CB878807895BD83192GG98B4GGD0CB818294G94G88G88GE5FBB0B607895BD83192GG98B4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6B92GGGG
**end of data**/
}
}