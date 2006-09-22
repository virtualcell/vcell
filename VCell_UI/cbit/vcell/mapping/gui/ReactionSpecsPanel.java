package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:31:14 PM)
 * @author: 
 */
public class ReactionSpecsPanel extends javax.swing.JPanel {
	private javax.swing.table.TableColumn ivjEnabledColumn = null;
	private javax.swing.table.TableColumn ivjFastColumn = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.table.TableColumn ivjNameColumn = null;
	private javax.swing.JTable ivjScrollPaneTable = null;
	private javax.swing.table.TableColumn ivjTypeColumn = null;
	private ReactionSpecsTableModel ivjReactionSpecsTableModel = null;
	private cbit.vcell.modelapp.SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP2Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.modelapp.SimulationContext ivjsimulationContext1 = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ReactionSpecsPanel.this && (evt.getPropertyName().equals("simulationContext"))) 
				connPtoP2SetTarget();
		};
	};

/**
 * ReactionSpecsPanel constructor comment.
 */
public ReactionSpecsPanel() {
	super();
	initialize();
}

/**
 * ReactionSpecsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ReactionSpecsPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * ReactionSpecsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ReactionSpecsPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * ReactionSpecsPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ReactionSpecsPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoM4:  (simulationContext1.this --> ReactionSpecsTableModel.simulationContext)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(cbit.vcell.modelapp.SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		getReactionSpecsTableModel().setSimulationContext(getsimulationContext1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (ScrollPaneTable.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getScrollPaneTable().setModel(getReactionSpecsTableModel());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetSource:  (ReactionSpecsPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getsimulationContext1() != null)) {
				this.setSimulationContext(getsimulationContext1());
			}
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
 * connPtoP2SetTarget:  (ReactionSpecsPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setsimulationContext1(this.getSimulationContext());
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
 * Return the EnabledColumn property value.
 * @return javax.swing.table.TableColumn
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.table.TableColumn getEnabledColumn() {
	if (ivjEnabledColumn == null) {
		try {
			ivjEnabledColumn = new javax.swing.table.TableColumn();
			ivjEnabledColumn.setIdentifier("Enabled");
			ivjEnabledColumn.setWidth(50);
			ivjEnabledColumn.setModelIndex(2);
			ivjEnabledColumn.setHeaderValue("Enabled");
			ivjEnabledColumn.setMinWidth(15);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEnabledColumn;
}

/**
 * Return the FastColumn property value.
 * @return javax.swing.table.TableColumn
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.table.TableColumn getFastColumn() {
	if (ivjFastColumn == null) {
		try {
			ivjFastColumn = new javax.swing.table.TableColumn();
			ivjFastColumn.setIdentifier("Fast");
			ivjFastColumn.setWidth(50);
			ivjFastColumn.setModelIndex(3);
			ivjFastColumn.setHeaderValue("Fast");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFastColumn;
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
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPane1().setViewportView(getScrollPaneTable());
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
 * Return the NameColumn property value.
 * @return javax.swing.table.TableColumn
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.table.TableColumn getNameColumn() {
	if (ivjNameColumn == null) {
		try {
			ivjNameColumn = new javax.swing.table.TableColumn();
			ivjNameColumn.setIdentifier("Name");
			ivjNameColumn.setWidth(120);
			ivjNameColumn.setHeaderValue("Name");
			ivjNameColumn.setMinWidth(30);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameColumn;
}

/**
 * Return the ReactionSpecsTableModel property value.
 * @return cbit.vcell.mapping.gui.ReactionSpecsTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReactionSpecsTableModel getReactionSpecsTableModel() {
	if (ivjReactionSpecsTableModel == null) {
		try {
			ivjReactionSpecsTableModel = new cbit.vcell.mapping.gui.ReactionSpecsTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReactionSpecsTableModel;
}


/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new javax.swing.JTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			ivjScrollPaneTable.setAutoCreateColumnsFromModel(false);
			ivjScrollPaneTable.addColumn(getNameColumn());
			ivjScrollPaneTable.addColumn(getTypeColumn());
			ivjScrollPaneTable.addColumn(getEnabledColumn());
			ivjScrollPaneTable.addColumn(getFastColumn());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}

/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public cbit.vcell.modelapp.SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}


/**
 * Return the simulationContext1 property value.
 * @return cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.modelapp.SimulationContext getsimulationContext1() {
	// user code begin {1}
	// user code end
	return ivjsimulationContext1;
}


/**
 * Return the TypeColumn property value.
 * @return javax.swing.table.TableColumn
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.table.TableColumn getTypeColumn() {
	if (ivjTypeColumn == null) {
		try {
			ivjTypeColumn = new javax.swing.table.TableColumn();
			ivjTypeColumn.setIdentifier("Type");
			ivjTypeColumn.setWidth(75);
			ivjTypeColumn.setModelIndex(1);
			ivjTypeColumn.setHeaderValue("Type");
			ivjTypeColumn.setMinWidth(30);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTypeColumn;
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
	connPtoP1SetTarget();
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
		setName("ReactionSpecsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(429, 367);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 0;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		constraintsJScrollPane1.insets = new java.awt.Insets(20, 20, 20, 20);
		add(getJScrollPane1(), constraintsJScrollPane1);
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
		ReactionSpecsPanel aReactionSpecsPanel;
		aReactionSpecsPanel = new ReactionSpecsPanel();
		frame.setContentPane(aReactionSpecsPanel);
		frame.setSize(aReactionSpecsPanel.getSize());
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
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(cbit.vcell.modelapp.SimulationContext simulationContext) {
	cbit.vcell.modelapp.SimulationContext oldValue = fieldSimulationContext;
	fieldSimulationContext = simulationContext;
	firePropertyChange("simulationContext", oldValue, simulationContext);
}


/**
 * Set the simulationContext1 to a new value.
 * @param newValue cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimulationContext1(cbit.vcell.modelapp.SimulationContext newValue) {
	if (ivjsimulationContext1 != newValue) {
		try {
			cbit.vcell.modelapp.SimulationContext oldValue = getsimulationContext1();
			ivjsimulationContext1 = newValue;
			connPtoP2SetSource();
			connEtoM4(ivjsimulationContext1);
			firePropertyChange("simulationContext", oldValue, newValue);
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
	D0CB838494G88G88G650171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD8D45715EA51E0C2A29A54D109E2EA32E425AD5918AD495A2D3BF577F3D3F657EDE9B63BE59B33315FEA5734264BA6266B56BBB37C99D0B11A04A45620310D2606001A460F10D6F4CC8491C799C7CC2870989E70E47E7266418CBF83FB4E3D6F5EF98CB390ED176FBB5FFB771CFB6FB9775C73F76F9B485EB7538A4C163D04188B087E77011910766304BC7BA3659B9C176CF51690435F81005D64
	199B7970CC83DA79D2E7C999395EDA8C6D9550B625B9CBDE0577BD44557AC4B53C4461138934451EBBDE1C184FFB031CCF91EDCF9BAA0567CE00AC4061B35F077C978E156B78DC9D4F41ECA1E49D6D737050BE9DF78E5A6DG29G19BE56FF8DBC37D34E53EBCE6A7DDAE727133C47D44B8E1C47B0A70743FEB636D1FFD6101F19737C486B87D4CE9C13G6D15G38F8AB99B97E9EBCEB6D3D5D3FF5F5A8DAE53FCBF63BABBD125F2FF83BAB3B7B141A1A4B326452941F3759AF3B82F649AB3B097FF31C73D74345
	F48EC2CEC2FBAB45EDB3A31F95701E8D30DE60BB8A91FFCAF51634G78D53E360F5E2E55E5EF997ABAD95F7DB75BF745D9DB224A561429C656D671CE0775B53A8FBE2A6FCDC0AB8BB8CB8E811C836881C884F8036EFF7F60BB702C6B504E78BC9E1F374E6FBF6D6ED4AF78BAE5B73C5754GC547DD16DDBE35139036476B070E4610E7A2E063FEFA536318CE520F737D4E387CD5127B50BCCBD18C1D64268FD95282CC97D90108CE986FDF8F47637DC4E02CBD0E65FD1598F9130D470A8AE2705E77783122CD7ABC
	C40B70BE2961DE87AC38579570EE56F541702394BFEC40B35BAC20F8EC27GED0B46ED23E5A71725B17FDED2D6BDBF269ECCFF1DE4E96DE3B2E8FD5132BCCE6DAE2DAFDA16FF27782C3EE8D9FA8A39ACEB01F6225FD9426CAB94C6DFB5C1FB85C0B640FA007C2033248260C4105B58B7875F7B02B6E617D46DF41F626E14D542460606DE01279C52D4295E9B50A42FCBF6CA2AA2F53865G517BCC9DC259BA460C1BC1ED5F85BCBE113D32AA2133426C12C756E4B5407A4C9A5A0DFDD419D29D0AC786869EBF6173
	4E9F5A83CF3794502E78BBA5CD0E74E074A5C338A77D1293E60489601D69653E91541783566F869896FAF8FD9867BBAF2B90AEEAEA9AFD3E5EBE3F83D7C23203286753B4F6A443FBFA106BF869A8624AC24E1243A1BE4F2561178C7358949A09A4F5007B58G351DDA685F8AB0A544ED6747AD5CF6ECBB3EC20EFEEFEECC5BC986BBCE8B69BE940A762169F4EE14EF9D5048G0F79A5161628F9DAC331FD0A43941DDEBBC93FC86CB8B40EC741979807CF6BA47DF60A3D5BC5E3F5852C2B9620F5006BBCE9380259
	70E9A9A0F32B3D280241491D119DE0F26696723C02FC8F8879B7D03C86F326822C963A739636987A5FCCECB4420189F5F79B65E17D82BA7BA27A0C862623E9E14CD3F23F6C55EC32DF76F642CBC0274D8C238D6AFE5C6593E3E6D13C9F7C7E8CB8DAC060670651B7A5CDD3150EBECDEE16B58D12EF04FE8F6595686B70C7C2866735AC0CEB7485416FC57F874268B3AE1F57AB534D92C3F47AF26A3332DB760050E7FCFDDE0D619F9C415A26D79E50E34643A30584FD5E5D077107558B199296C5FED5710089E088
	A33503684F57064B8DBA5A847B0DF5CF99508E8DF21F8C3E45FD72506083E4D51B5F92AB37186E1BE5C998E4FEE29A0C78A413697E515832E75058216383CCD673A554C30C72055A469E727459D72369B4C451E82D77794409F38494CF1F1BC64EB3BE2F86BBDB4DFD689C0D91D9A0E7B6407AC16EC307A91D47AD5DF94E785CEE36A5C02B9DC23DBD060528D87B56C126B71AFFA97DB9C1CF00F66AD0044E783CECAEC379644F7BE4C897A87DF931FF7B4CAFC5511C7ADEB27AA1B36E255B901B9D4348730DA2DE
	17668D3158D78A78FDG27G76831E757B116817B64C644A9AE6F132D46FD79146FE19453C5F93BAFD73309ED343ECADEC3F7E6B885A902DC5EFC87DD228B29084BF2829E9203B46EA4EEF513C017D5DE1DEDB7E045A44B2E82F844889F3FB7B279D3C36CC69CDA7399B2B0B62554D1BF4B94AC6A2763662A7F505EC0F67D811279DE86DA3FCFF17180D792DAE90703994EAA03AEE1BACC6FBE438E68B9EAE3A94B52055C3408871FD38E6C1F9B1776AE8DD9F97ADA7484D54669163E56B8FFFECDC7F4ADDE1CBC9
	1C75EB7A3AD38C6BBF64F194311A689756D242FCA6876825234E925BA862AB8AB1DE601EB3FE2E631CDF7260FD24FD6E42387C92C7991F7451883F23A3DB8A98BFDB9167378E6865D31CA54C971797A1BF2DC7893018D39FB4469CBB7453GCCG99E24CF7E97D6A219E4DEB5C1F561D5247E4FF75DE12D514E409F56668799313E57594A65F26A991B9CFAC6D301E83E0326EA9663256CE857ED3B95F5F946E0E95B7882FB37F050EC359B2E04CFAB16E54F63EC6566E599EEF9E36162F6CB72E25D81A9EF3ADC9
	9B3BAD7B2631B55826C55612B927433A9A00654DB5634E8CC953A275CA5A3471358B2B25DD041E792631DA5AAA709E0ADFED4033357F772830A7201D7C922F7F8EB760FE26C2BB8BE0AD4086006284288793385FBDE5B9CEA6292F6BC1C32AD7F2B70FC9183C4E35A2DF48F1FAFB1195770066B842E37A3C7398F3ED40339DA084108C10A9E4B818BF298CE7C3B1E5E0FA5ED800FADE1BB0F9ADB8362EF91E0E5BF09363180D9C7954F8FF103F3B41E20D63176553595C0727C7EC046964F797D0A7BD00CFGC885
	D88110B31D6B441DFFF4B21D70330C2E8C36260622494E48F953472E170DEB19F4DC790C3163D8CC3DB194A747506049624AB2E20CABD5B3589925951EFE841AF32686F99ECA1C41735033A1346BD550FE82E0738CEE57F31BB07F955F863590C0ADC08B40B00089E065EDDC070F661F1ECC07F53CC8F478446B1968C20F6B683562DB536D5B130E4B39AD5638A7470D5B9455AFB63FE7460D5B9F55AECE9CBB0E6975C5BB6A3582E835G8EGBFC08AC0A6C0CEA2576B3B759FCE6A2FD84803CEDDFA0C0DD2A8BB
	33BEC9EB0FBC1837F8A654FB9437F284F1C721ED1B494FB88A41985EA35A815A465C445A7FB39AFD363B95407963470A3D640F41A35E990D69353AA17E9968E4C36CB350EF9BE21F01EC8D3B999E66B05E0CF0FAE34344E7A4C7C37CB352627371653C6FFCECB9171F0FAD670367A71673117371647C85556763743C204239C57148F4EA9617645081E623177B3C4CCCF176C0F46C94365FF089EDEA4ACCEE637646E3B1EC7CEC272279D4BE09C316540BDDDDA2F0E05148657E64C2FC9DB5DD082DA3470558BA7A
	744244BA6A38B0310E7EC3FB1944B83FB6C99E1970335B54226DFA9F3B66336335BE53E377D91CA3C6FCEFF629F07A4213844AC663F5AEA9567DA5F5A60F61A97D38BFB9505E8410FFBB7759994109E2623E5B477AE29534D98DA48F60FC365BD98D04B2B33CCA716D86BC2B0D1E27B9845B43C0DBF73BB8EFB4455F1F7B1BE26FCFC6931EF7823EBE55A5C76F41C3CD65029609F7751A6CC1B533BEAB69F8CDD23B65F1FB78ADBA1E51620D4FEE427A3FC369D4D47D544B764EDEEC5C3BE3E25F7F6E15DD54EE82
	81B9C08332CD52A4FD4CAB5B7918331A6F4289A147DEFBFC3D3CE10F2D17837678FAB9EC1FDCAF557678FAB9E91FDCAFF576F8764F567A5CB05E3B382581DF9F947A19F720DD6C88731A44866DD60084G539D2CB6E1367FE090755D25CA9E180EBAF7D2920E7D6E889F3B857A17826EA8C0A300BB091FBBDC1F215D993C24DA777DCBBB0C326ABE48FC62A942FDA287665910B4568736A645BBAF948711D7591D51670517A83E624E68734242FEFEDE2882DA4A1DBC775E936439F70B407DE588F17B6E0A5C6B6F
	A63D83F8C7E283DCAB008610FC97776B902525C3FB8D40860062D91C763FA11EC3ED7A181B4D1F1AE1CC6BDD31EA1FD786B88F2B6E77B74B43F8CF99FD5F497477FE185F519E0635354F6276D25D0E7513081515D2D02B2979C4D5BACFCB5D0D547AD81C8C070B757126D9BCCEAE27F32E057686G124C6FGC23F62F7G99775FCB567EB0761D4BBAC758D21E2C5F09A79B4E566FF4D8994F97C6B84F9050B31391777628712E8C7575240510CAF8B27A35B1F47431A344C83FE38C9D657D4CEA24FFE38C3D40B0
	1E2D6D5B3F64EB5B7A6DCC62FEB8A16EF758AD7A1A764D5657768F8B4CE5A6FBA13351ADB46FB800961A4D6D36085E97A5C1FB0540C996EE5FEBE6F31B5C99443CDFBE07374BE99B6F66D8BB2D5FD877E13FBF34869CEF330F0C6171075C6347829633D6083B1D933006F3GBDG09G69F3F84C7ADE68F5F8CAFD1A6F8CFE5702F86D6E73F8839F2BBE8F3B42556369A3846F5DB0F4319E9C7F970D08F78C7823705F59037BFA560B85D7A7A76934F59F62180F250046896C9EB7E340E8BBEB66307B52AD70E474
	C707E351CB6F6674F9A3316895026EA13168A76E667B7EE178563ED1575F6238E9B7BD0E4578B07D76F06EEE961F4D82BF03FDC7B460D96CFAD0442EF6202D3E1B1F292D4DE89F07D3605C85508A208124GE426701A6971CBD5E412F31F4367EF046241EDA8D3597AFEBEF2EB5F6E7F2A084708950F7EA9491AE10BFB873ABA057966FA7D19153D400CCF7D8E4A016B5BBB57D9D28560GF083CC81B04D656B5BFFD93FF341B29F2AFA4955C3EF1B4521C1110391B496844C06F719713BC73F2CEA0ACBF23314FF
	DAD26DBE37629AA01B66225E5FB1E36CF997A0E3AE77377D96142D4727AA03BE2FB6FEA45147565038D587D0B54FD932EC9E62C2965B843A63FBEE55418FE392GDE565B26F9913BE032289E2C7BA2658A7BFE41F2473F86F96E58B40F670E62D0F46E18EDB6467E1AD0F46E4071913ABB941DBB460EDF9C0A4E9DFCBC4B350141374878F3C92506E25754D069750042567074C0314E6750FCFE6774AC3DFFC0FBEA95383F9F61389401DBC678586C79BCBEFCE904885FCDB23CFFBD7C4738A718B8D62C7DFF6541
	FC2134B97EB9E0CFF36CF340EB4D314F2FEFB6CFFC5770F6F37C3B064AE66377D8664B39D750B672CD904F81ECGADGEE937763154D8789756398BEFC36339B4FA178EB900D73DF621860AC68187CB7A4DFF3E08E6B70E91A4F0373F37C438E5CAF4D6727C87DCE5201BAD2156E9E0DF555712B9CE8E3EE390B2219CFBEEF46F338513C8D6709CA67594B0E7AB3F50DED4DE7AEDFECECBCDDF7392D2E71EA5D35E642E3E7DA50F8CFFD7245E9B13F8FA3A41AD84CCC551FC95B9618198C17687701F8B21C3BF839
	7E530BCD0ED8D23CC947936ABB6CEF736FE261F2CC783B40AB3FA7C26E27CAF94C5F377C4B44764FB64BDBF164AEDF40646DD99029F993DACFEC576FCFA73CE3086FDF9B867838B418F7450231B55F9A3D1DEB4033F14F096FBD1B01163F50D9D23EB05267287EFEEAE174386F085C68GDA8F403021CF327E3EEC5C388744F9B18BE859G6B8CFD72960E2FD5192D2D3F925F5E7F734A64761EF705FE0B85BB45DA0F61DE3802E7ECDFD7D7C096FE7573ABE582D79F307B828A1E5D198F57DF47B90A97B9CB8EAD
	62FEBB5B79EBF8BA259B63EF5BGE9D7FDFEC831838CCB3F3C6B76720F1F996D3CEC215B42F3DB747AED0B18CE6C0B223F47BC560679B48170E9G2B81F281B6839427C2ED88D085E083E88550G9281D281B281D68164GECC865EBD9787BA3932CC5BF9452C024CA8A06A27DCEFC9B4DD3DBE11E3D7770F34B570690F7885AA785EE8B45B5C23BD3605AE8BE73C3BBC160F60479FCA6013B946673258B5CDC0ADB896D9C01FBB24CF9EC9038D3A31CC71E40ED9C61BC0A97F3DC65A867D1AEF06AA867F1D4607E
	FC1473B8A5F02F924E23D160D6914E23DD60724D1C07DF604E19B90F8401CBB2F39E2645FCFF1A2E637E0B1C444CC9375BAB57B13FA8DEB0D7492D8C526F444D50515B4DEC25CAA242173EFF7D1771769932CD83444D0B9DB7E41766103A45189F3445773F9F36CD6EFFBFEE0B1DEF725A46671B975AE2671BEDED46FC43F0796D189F3A41B6DBC43F6D82F7CD60F636EF57F1D715CE2DC76077363F28634F49481063DFEFC73902945742EB82836E9A7F7DCCBB4690D8B21444FC6C11F6BC0FF7A9EE377E6D0976
	D13C90D204BE0ED199953FE4107B8301C33975BB4FC114CFC1F5ABDD8A9694D90B292D525F20AFDB4C6B723F9D4231C1FDED2C867FB76AEF4F228D0AFE3B0631CE6C11A528D7188131DC165CC1844FFE6A139E45CBD545663A1362BE82A8DD62ACE1B27CA6CC6F9D204D7A8CD25F1B8FD03504734B88237C7437977A8FDBD85F55E17E3B71D350174970100B885915914E4FAE783DB40A7C5A853F2B6C77DEGA101DBB4AA3E490AF91F6E0C4CFB0A7AA466F41318F7A37DB617AE66F8574C67DDA9F01F1B79F702
	ECB1EFE1D7C4E736C15C6303F841F01057849F494698DFB55498771BFB6E927DB7A6CB22EB193CA1FCD69F2B5279AC9B364073F895675B9826FCB1299D643859E16C73417B3C7D7DB0722C9632CE9999BB4761D1147720FE59F1D074730EE23F77C53FBF9B45F90E897AD50274EA41E79B21326045081045E69EBBC7229967407A0C4525673A288D257198D75F057C636520B3E0C7AC2AF11F79D997728C68B876BF9E33144F6119718CA98828E4973F562C2C278E382E7FF80D7569AFDBADA43F1E943200D4CF72
	B12A5413829AF46A099582D288FF983D57889A49477D05872ADBA3C53A46G01EBA66237962346FD5FB25D953706EDDD4A765B3FB4BA372F6FC5FB4BG7C1A25DCB7A7FA5F0D279B085B4D9E496D0693F9EF1D3793E3786981CD0E1CD7B676627C815EC7A7B25A66DE147B06E14C7F81D0CB878899F92EAE8693GG08B3GGD0CB818294G94G88G88G650171B499F92EAE8693GG08B3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAG
	GGC093GGGG
**end of data**/
}
}