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
	D0CB838494G88G88G650171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BF494471142C858008882449F47C0F0A2471CE33F10C417535DF1F9649DC970C17214044464A2BF13C4CE94CEE7F34FBA9FFE695D15C0A081921F8B8E42169D3041487AA0A587B2676833880908D8C14B32096122595DD96D20591DE1E6962D506F2A3B27E7E7D73BAB0C035EAB4DF4F5F5F5D5F5D5F5F56F20C32FAFAF31D88FA1E4A9C17A5F7FD890AAECC4286A003F0A61B2024EB2E47AFB8320
	92D5724570DC8EFDD9AB1CE595E86955D2E82F01F655A71DE53F047703E879556BF57012E01EA6687B69124E4FA51F6784E2736CA26D22BB65705C87108DB87CAC16707C9543C7F47C969D4F40E2C52800501C9E2E52F11D506E8348824811A8FDAEBCF71319531A4F6AF45767E4216DDFD72CFBB19F93CF86CDB29D6B521F5568E58B3A0567F28C176BE3B220EFBDGD65E06F2036F4173FC4FC07F7B9E3720555E767022D86B9185BE2855FAF9F5C01364DA37A085A4AFAFB6B7DF66E4D98876DF708A1A2474F0
	C1DEC4A3047F3261D2428FA1BB34CB950CFBDB42F32E0576E600C2833F0D603B615D8BB022B0BD7F776D733A9E2E61AF212D7DDF5D3DBF011E198A55AFC70968D97D0E5B76AAD9935E02EDDF84FDD52A332C9E40812081E4829CA53EA022F760596A565A25C0C08A364AF21B68D5AEE3AD613D3999FAF45C955EA3A9DE0468FA7FF938A60EBC49000E73FF64F154A6D90DEC6DD7DFF982ED797C826B2EB8B65912B5ECDD2DD2DB2CD3A3B621F32FC8B877D6B55AB723673E064CF323674F6CAA09B3F755F74E6CAA
	527914EB11394FEBA48E49DA57403BC5778F0AFF1460D3CCF86A276F92BCEEE7C05FF60D7906EB9F13253BF8A52AE8D898578E0B3E12EEF505288CF2A8D696370CF9AF8B51B9F79B32C8841FED42D3D93693D9F0BB977A4E5EF616D17F8A5AF05CAE03F6B640A600FC0052C1E759F10033034C473E006A6E5247FAB8C5EB8B892217D7909D9B347CB7BC793026F0DDC1D563029E5E49A9826796F99569B403E4FD5DD163C6086FFBE00E0BFC10D7B8CD1002401D8B709A2F2814A64DD209E9941E74BA04G8F9384
	E444784E329C042748295AD5594BE9FC0402764F37E01B5C661C40919840BB354B52E2ECAF9768AF832498F62830E2FE3F6715A0CEA15D12B490129DD89334E9905BB91B640ECCF8DFBD48EC6CEC4538C3E1E7D9CD187139E8BDE062E397BCD8FCCE99E2B136C2423939A54CFCE61B0B790CFD6F5F201AEF4F0F6BB399603F5EB0751511F0EC6CBCCEF8E239E83BD1F2A5095916F07CF87AE83922BB699C360F41FF1F31D66B5A70DA2D82F957826499B6FE40DACDFD360DD3F966259FA860E03CB7E2F1BD274995
	3ABD31CCA53A7C34AFC47A0A00EF7510332C93C09EE2B6FC196897BB6E6368B4510687E5D78C7F5DFF45B9282D42B63CBF7137E12B3473B29F74420B2A770D5B70FCFA7C7AA4E3CC9A414B906F6D90E02A011FE943B149E91AA238C39A5F4BEB9A6C33117EF9E4AEB56416A329024D354406F51486A15E8D7AC7ECB8D6BCD2B04813C5B306687DABC82C72A29FG215B25D0D023784F94E3FF9F600774DC71F871CE04E3DD8C613CC3EB06A7B9DC98490A90GD740298B658F61B85EE854BCA497838E57BEB9705C
	B444E2F27057ACA60F5FF98C2D7D0BEC0D3727ACFAF436357E8E0DA57B1DC8CCD219BEEC0DAFBBC7F2060ED7292CA90DB80F768A0110C832DD3B9454E0D5E869224742AB6D181F97668983E45CE1317003D65C4F720D9E846D12A8D2139A367EFA9B76F5153F9562A1CD634476FB43FE5FECBB90536754EDC97B3F5706EDA91AF26271E1EC4B47E4D69BE65D21F6DC8B4FBC00E268FF8D5B0F500D5FE2F472F0046EA1F8DF9DC5F7D9E1F49BCCF4DB613DF444CCF7D4E5F4A7C7A2F4AD706E556928DDDFBC05571A
	540DB7395BDC38D69D84FFEDEE3EC4ACCBEB42F3A325BAFD6A882B7D78F1EC6B9CE86782940CB03F7866DED67BE58EE421AD4FB76CCAD463960FD2FFB8B99A710BB57F563A132E65458BF8CEBF7405C759BABA5A4D7BCF2B2ACA9E01B8033E86B63B59EFA82E543E871EBEC1D135AE886CB0DB2FFDF6ACAF5E9BF534DEABFCCBE6F5CBBA4C3B8E40C6684F5D42F12D467A9F60E84C3C6FB3474C8E98E79FF3167981C646988FBEA18FEA3B97DADB585ABE3192E56FCA37462B1B7D1FD9E44D9C536B6631086D1AD6
	38ED1DGD40F2CDBCC0F72F11085004AD96DB74BB9B2CE654C016726F1A6E72312C8CE4A7B3642F86743186A94467B95414C5B15C2F9E742B3AB0571362849F95BD546FBAD0CA9B2F8DF8818F957CF213C4370CC1F42F8EFCC481B6EA56F0F44564F2B26C476796CA99377FCDAD7FE0F787A06A931356FF3843FE5CAEC6DBBB04EF4A8003E1E87D8AD3439836B30865A1BG728150D4309FC04BD466638B6CF0CCCEDEEBF601D6CA1093FB23B6915D2F3BAF60F9A16F6B6D1E8BB8EEC95EB76C30D91E6C8CF3FDEA
	34AD68389FCFBAAEA5E69C4DB1AFB41CC3934F2681FA842D6D8F894D4D3D1A9272E8A105578F266D201804ABDE125E75987B950A5A172652BAF39DBCB78350DCF55B47F2D561D4162B827DF8BDCE26021D81DC29ECBDFE5B09F3248C6DF400D5GEB81368220B4B8832431B5F966C25BE4EB524AB6FC07E43C3647EE5A4C0ECF5C3AB77B67CCBA6EFCDABC7BAFEBE81C4C7EDDC1C193B891C4768AD8E255FCA76072B31BBA5218CDAF8958266950DE85101346EA4291655EF41BB169385C383A0DB556CD265B951E
	A32B50AB731EA83DFEF31369D5E068B5D744FA9D1F66ACBB8F601846743A245E1BDE2EC94709536269958A473B4F09526B822EDF94414309177900DB81457460B16BB89AB0766EE9CCC7ADC876EEE8678294G50735BD5324F97BF68ACBB72A0431D9943387350F6BC48EC7143F1DCDB060D7673240D73B1ED5F9A0FBD77E05AE4B678A8207B4B04B9BE6EF94A8CB46E1F6DC8FCAE486F08FFAE5856917FDC303D2312620107791600756F68C8FEEEA83E986F5CF00C687D936DB00AF3C678839760F53B9CC47BEF
	6D56E9FAB42927417033532547D05C7A8F0AD8B76F452E847D54172EA066CB998FB2DF1A4AE3DFDA8F6DBC00A243A706FD497C3E6221E8FFB88EED3A27BECBF2755907681E0A0162FFCA70F6931E6E35EABF3B8F6D013EB58F311C5D5219F86DAEF646DF3B4B1D38D6D525106261E3574751F944680BA476AE0D8FE0B3D31A9BE43C46A97D7C047575127134AF5178C0A7BEC338852F2068A7933AF6BF0ADA3BB3C68C5C70A73371116951B62E194EEC7C8FA4F737CC0F3571BF93FC7774D89B77884C46AE684B19
	4E6A2D4FABE6399A8D391AEFA6132BFAC634DCCDB318DCBB83F81E4E9931F26DA7F8470CD8399609CC2EBE68DBB70349B5C5B54B55E44875C0A019DCA7E7C64BF5FEA613AB1F644B1E1931F27DBB41DF1F99AB571DA0134B8FFD9BE6B2394491FC869739A1A984C56932F492BFA32C262907F6A7008C101ACEEB9B9A23CF8DE23F70A9DCGC2936FE2A8BC8B0F6D9AE3E33782FD914091401F85F04DE2F39E5B07E5B1C5F3833545B5E436455AD9513A674ECAD48B3FC462BDFFD66C5DEB318F630BBF91FB77BA1D
	E7F72F5550177A89362F4C7531FDA55F407D8741154F061AF8B64BA3BF69C7467A951A5E077A135D2D7DF576183977F58E2A3366E7F628194D6C70DDBF33C30FE107F5C2C47727CD6FCE617E5BC13A2FF3D01D7FF81359613AE107DF099817826DCC438E6501086E07CD6F1F946F3F9D3EF4DF6720BAAF8EB0BBACB76C30036056C1FB33E107ED41086EAF195ED302775F8E0B6F6B9CB4EFBCBE42F268D65039AA03654206BEFCE6B36A15DAEED0EBEE3E28885EB62E3F1BE4D6DA2B3CC8EAD9BC5E1141EA15B7C6
	4919855A2BGF2B2581DD578ADF6E7357A53AB51269F453F334AF30CDAF3B3E8DEAC4AB05D3B3C633651B925B1B6E7539CE719B887639E9EBBE46201F372AEA816EB61C97BB7C675639A7094B277DB464D7DD85E472246B7C675171846D35D3EF1026936639BCFA2718B2989FFF3DEB3076A34D9FFD67F4BE2CB45221E1D54870324762B188B39F9AE734B028A662BCDF319DF3E4263BB54D4E82F82D88F10EF746D71613EBC7DDE935FD9627BC85AB71BE7BE05475D0D1F246AF46BE36873630C27F3E499F3145C
	25AF5ACC634D78F83FC11C1CC76EFB0DFB40C373A64AC1776472FEECB70DF3BB84CD6465F9787B0467BEC0860F19469D5D1AB8BCA87D51BFC6687DB7997D7A04F3B6089367FC7FCA044713467D5A1604BC8E86A77218560B8C7BBEB561FE6D6E7249BF5E6338220FBC0E56AAAF12BCD0B8AF361E5BC170BB664556F3EF0F32DCD41A89F11D496E0F84873659A6E897G944F07BC85F09E40B51F1DC376F55523C96E0E9C125C8D879551F4AC247AE9E377762D41BF6DE2B13F664B1FC559536C56B294BF664579F4
	2D5375E7760645967C247A6D380A754B013E4DGBBG8EADF0161D84E8D94074F3F655D07D7027A42D1A46F97C64761DA967947841889A972F5417524877B7BEA928E5ACA05F97107C620077BE002A0598E72B346BF266AE186813EC0DCAF47F4C871A9DG0775F6454248BA52B9EF1C433F6174EA1CA6F822AF98E97F8F8E625F1892DC0569B4C53BB00FB8D7CAFA3F78AA7EEDE5122B997DBC3D51382B74AEE4FB517A9B66FCDDFD7A0338793A68469D6B320554A6EB97C676A279FA53EE4AE7630766EF0DAA16
	08D6CB9CBE98366B635122889FAA5FD3463DDF53A2F63F3AFD0061C483779D630EE955A2164F7FF39499F1EA9D0B3C3FBBFA7F6B130E7BBA8775FB451178FEE148917FFEE15C917FEEA84D117C6EE716A371EF46198E73EF46B4EEB7DC43B54196D80BA2002A454E32FAG7BE29633EFDEFD9311180D932F973C7D78FE83FF29223179CB1C09F5ADF7CE7EFD4BE1A77E8D4EADE91A94407C997E28932F17A64984C9F155CEECA3C56877EB14D4473F6544BEA672BE0226FBCF688676AB9AF7F995D827A0BFD84D4A
	425A8EC8AEA75F74C2ACCB7DEF56D45757D4B7B41DE2B9F27920B9BE4E6EF9A06E6F57987A9653385074E77AAF966B7151D4497CFEB5743DCE64C0A40F513F425F4543D159FF76FF38C5E5F9361C45FC55631FC2766F5A2D3FCEA04BD6DD0626A511D8CD051827FE1F9BF567A682CD4A12643594751772BE1C67B30176C900DC0082G3454D9F6E4A97319AFBACF4253495D54F7B07A65A0719AC0524A1B780D4289C10D722D631031FE7F65BC11E8BCF85DA5D8338F5CB7A1E9BAB8FC48277AFCB16AFE2B86E4E9
	D91ADC9FBD778C1847B9E00C7F2E46BD93F5BFB482E3B2A699C7F54B3D16B8BE1E39B6F9FCEC3DC6FE37863FF68C497ADD60735770DD116473293C91075B2ED59838AE35C7D2497EC4ED7D2A9B73D885722EB5566C4BB7CEA7D9339EC512F9C59B22D872B503EE07F315E6BB64813F6D1358014AF05503776B52E54E3263G1DGFDG9AC0BA40F200B5G6B8172G0AG4A9F067384C093009D4085A0BE4C642F7F4B29A4726B073E0853A1F6067E9959D7D3004F61E206ABA438C500FB44E664253E446BD655B7
	793A1D680B1F57CE77CD4CEB75FD71735AEF7B4CF90D4A7A341F49BF4746647F161F493F575029C2E4F49F9AF407C5C677C3AB433D90E0F4A5465817820C6EEFADCC16575CF8AD0DFD083A8453550D77940156C6429D727BFAAF90867BA93F1ECAB60745D8072B15EC8E0B212F60C9ECF359B3394D07BC71EDBE6E19E873D4CFFC1B4F7018EDCEF133BDB80F7703CF398C3ACC8377A7833718704438EB02D7739B78C708DC985F496389997ED1A257A04139F45CE7CD38BF69384FF9F03C034AFEC9E1E357F8C80D
	AC08A2EDE7939AA18861EF58636F090C024C19647E0A0143F2537A18FB2BBC32FEC4F40A2F8A6F0D60196808FEE7DF689E853FDA7DF3D42221CA63977AEA3D145550813DD42E0E544A9A2AE0353109AED28EB3242A203DD19B75D7D77C66479B7E55295A504EAE74AABE9774728AF1B55EEB67B4760DEA875AADCB906B3D006FC245AAF914285A1048F723C33EAB787BACD5633D460F70A05161EEBCD3DDC893443ACECE75EB58215E6BC645C17CB5ADBAC03BC5AE58DFC7BD7A3DB07E0656525A07101E5F9048FB
	10480B39549ADC8E7E5D7E93EFDC5A1AEDC3568E34C714B8EF87A8AFA9DDE83766D7F855AF095E307C3B964228F4903B9DBE0A59005DD1C3FD2CEC2D835C5E90FAA07CF07A5E7C3D9D9041E1855591CA7CA5CB2DC161ED77F38ACC45ABDD9A7EBAD8D20E9BFDD3DE535EFA7A62AF2FDB11C5D772969AC3BB4332C68D86136FA7AC314A359DA035767D1FFFED65B3678B8BE0C887AAA1DF0B404495912ABF0482EEDE1936FA7E31324C3C19D8AE991BC7451C77E8E8270F8B8862903C58C93945703BA114032ACFD2
	82B0E9F50C462C4B903CF65E576A67BF76721F096011219AFA1DAE918C2D6B6725C0DD2B4FA784F903812C9730BFE63047B435B1B48742F38EDF7A76AF5C60DFF6E48B5457137F8D647F997C3F01E09A8826618CACDAA1CA7A975B9F8EE31D8F068D6BE2C93AC82C665DEEEC36BD77A91B9595F7211DB4F701736284844ECB72D39732C16EA2FED79C8334C6BCB5EE2ED70B52BC896B5502957AF9EEE524C6247BF3A97D4D85702E15EC0FF5716F2244F5D9EF0093C50D9387DA03DEDC23358DE9FC64DC32570779
	2B0CC66F24FD07FCD86E1B26B17F8FD0CB87881ABA5C1C2592GG98B4GGD0CB818294G94G88G88G650171B41ABA5C1C2592GG98B4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5F92GGGG
**end of data**/
}
}