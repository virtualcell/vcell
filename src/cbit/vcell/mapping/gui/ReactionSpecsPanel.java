package cbit.vcell.mapping.gui;

import cbit.vcell.mapping.SimulationContext;

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
	private cbit.vcell.mapping.SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP2Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.mapping.SimulationContext ivjsimulationContext1 = null;

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
private void connEtoM4(cbit.vcell.mapping.SimulationContext value) {
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
			//amended on 14th June, 2007. fast column in reactionSpecTable is not needed for stochastic applications.
			if(getsimulationContext1() != null && getsimulationContext1().isStoch())
			{
				getFastColumn().setMaxWidth(0);
				getFastColumn().setMinWidth(0);
				getFastColumn().setPreferredWidth(0);
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
public cbit.vcell.mapping.SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}


/**
 * Return the simulationContext1 property value.
 * @return cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.SimulationContext getsimulationContext1() {
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

public void setScrollPaneTableCurrentRow(String selection) {
	
	int numRows = getScrollPaneTable().getRowCount();
	for(int i=0; i<numRows; i++) {
		Object valueAt = getScrollPaneTable().getValueAt(i, ReactionSpecsTableModel.COLUMN_NAME);
		if(valueAt.toString().equals(selection)) {
			getScrollPaneTable().changeSelection(i, 0, false, false);
		}
	}
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
public void setSimulationContext(cbit.vcell.mapping.SimulationContext simulationContext) {
	cbit.vcell.mapping.SimulationContext oldValue = fieldSimulationContext;
	fieldSimulationContext = simulationContext;
	firePropertyChange("simulationContext", oldValue, simulationContext);
}


/**
 * Set the simulationContext1 to a new value.
 * @param newValue cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimulationContext1(cbit.vcell.mapping.SimulationContext newValue) {
	if (ivjsimulationContext1 != newValue) {
		try {
			cbit.vcell.mapping.SimulationContext oldValue = getsimulationContext1();
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
	D0CB838494G88G88GE4FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BF49457F5EE02936223E363C6360112C635F1CAD26C2AADBD218EB9D6D35262061E12B5A41C83EEC52DB410233858A589CDD51F516E922DEC40420620364481CCEC81C26843C788817A2CC082010434C88B1211C63B235522590FE6E6D7ABE9413D77FDE6C72BD9891CD4675CB3736EFD6F5D7B6E3B3F77E6A56C7EF54636FC6BEEC1485FA6303F53790250DFA5882F6725F5F0DC75C716C24170F7
	GE017F0B95F8E4FE620E55D349496895FD09C506E05F69D3477427BFBC216323682DED27049845A3AAD5FEA1C1E0F2D0073A9A06DE3116D705C89508FB8FCAE7E8879B7C50A99FE9643F348378A426C926C539F5947F01B210F83F0CEGF7896D9F06A79560611AEA562F7771C542163FD72CBBF09E431C9CD63131DB58F33F70737C8507115797081CB826945ADEGDC3CCD38D7F59C1E356D433E731E813FD69675C832DC969043E1FF50D7660B78EBEABAA4512379C3414E3064D15B4520A48B45C7F04E6FC6
	9CE48EC148067996925C26A8726905F78DA06D884737CEA0FEA33C8BC7AD0545C7795A4EFDD44BE4F7CFFCDDD8DD99303F13E2ED65C7691A5CC793EBDBBF7C0A753F49BEB4BAD05F69C0DB8A3092E0B3C0D12925309C60FF497EDFAEB8824F7A812DB1948804027561F0036CD52E063C128C6FB5B5C0E1388E4993D23C02C0777831C82909BC53819D7725879EC7F532280A6F77620E670575FF71243540C4A76B97CDD81BCB29AE7ACB93BA213C3F171277C325136DF1B26FEE4148BB7627EBEC5BCCF827AFD8E3
	CBE773ACB2704EAE453D7EF89477FA853C67B3DDD03C1F608F9DCB6029ED7E9B3199EC5782AD6398378D77CEAECB2B7DE9212862A9D3BD4C7DD61A55DEC6E558D716ACCB96313B6632E4D9D692FCFFD932AC9D93DC1698505683D07B4A69C0DF2DBBEEA96C8550G52G96832CGD8FF1C5B58978B0E5F270D350B0A5690714BDEC9916858D647AF61A945B4C5EC892A1A9874C8DD22629787E4C995D81F9B8E14EDE05238DB4476BD406312941494911D95E6978312A6A9AA6D63F16C42BE0AC428AEFFC0828601
	3040679DF63C87CFD9D4359BE12F28C9099E142EBAF0CF22E2974C889340BB554BAE976AAB8F563F3B5CD2F8281C6B61F357F13EAB128261222626B5949A0E04DD3892C1ABC7BD7F18440EEAF8EFAF67BA5EF183F1CB213DCA1F671B575FB54C637413C8A4AAE35C47BC4426F2203F3D42D2D8D241ED67B5B7379D670E670452B5CF185A0E63CBEBEC4D954C07AA12FDA8C466C679E683EDD1850F79BF093A136631D7187B940792C6C7B96FAFF66C9CCD66316DBE466153B6C33F1D7A5ECDF491BF8559F2G6C15
	DC67DB2F6F27B65CA02A92375AB6858CCE72A6F6004A793793BC2FA05F833A7C96025F87F3BA8164CA2EBB77045B507FC1E223918ECC2B3B914243F69FBA3B9FFDA6835551EB9D1827242894541CD2D88AFA61C5E5341FF4208DB2BF9E8C69E37E13604360770D60E82A0E4F6FC45F94B5CD718FC4B429D352B4C83E89FAE1A772D2A38361C448603C0ABAF10D21D170FB3D7F1ECE7499CFA8981448E669C398FDAF71D9C91682A0F4E3A89254A87ED7AE2CED0625B196B38E38FEA1204F4B910CBF34DE48945F46
	B5A87EG18G06B0A127927D599EADB668A89DF0D877AC05674ACA6E13239FF01FBCB47ED5A12BAFECB54BADF31FFD4CDADAC57D242EAA6113D426F7EF184BDECCE287432BD456FC92BBCC4A97E29B6F892FB66DCD261390C522B56BF3298B67D07D0108CCA2E7E3A828414ED6F09FDA52CD728848298124D5F19FCA3F01F49E371873B406E419EE8950F22AD0EFCBC6F79B563E300A6A8D5FDDA7101E2D53CB216DBC1120D3BE3F57D50472C9A391895285CAFFC55F3F59D4F6832D0B6DA5254F69423D148D31B9
	0F705C9667F5690293B476AD0167F2000DG6F831E76DB2D77EBBEC9656ABFC963649256EF45C96CB77FAE6F7790234FBD49E26AC93A963ADF7FFE98ED085422F7442898ABD3C7418FEAEAAE13DD23B5E7B06AE07DF71F6235657CB15C03D6E8FB816227383DFDF7872FAD530797892E4C5536D4F5F37AA9AA4752D389FBABC99B3551BDFE530DBC37824DF11A6F6F5B5D467CD62F2AA10F1F9888536D8B5DC6FB2438973B5F026720DFD135968898B13E8F5F6EC6F9B177B2B453C71B750470A035F942F8697A6F
	DEB42EFF59BBF72DEF27D87F3E53F45D3527936B2FCE7B290D56C485316D827519B85017G641EC67CDB9398AFF04FA9BFCF9567B7E77CD9217F0979A97915D7D3BE6D5589FEDB8BCFB2FEE5F14EEFB65033G28AFBE91C7FE5A10DF25B127CAB6461C2D50EF5F99B86F81384E70B15F0C23AE834423F90D7BC6FD9D9B33727927052585E9D633B347509F4F354AE728FC69E792F2AE5B700AF595G15753FF4D9F3204F3E9A4EF77F44AE3338A170BA73EF6EEEE77B6C02B1E97A38A377789AE93B63DE2AF968DA
	1EFB5F389607783069DA52B6792C4BEB689AF2EB8CFB7C72AB56D6G1AB755E87219614459C43D52FCF6EA6DC2EB691D63644CF7165652B69DDFCC703281CF571EB1265B9350324F727ACF6D417DF456C29C8108814C814884D8D54B7D6E2A3BCA18213EEE818DA9C1D16E1C14B0192F59EEA2DF48F12CDDF093770064B80147748D3D98F3F30127631C25F0BFC0B5007B9C1761511BB34A5094B315016A7965F154F36C5C4C3560643AE68399B7678146D19BB9FC4BF8FFE05FF559EACB6117D9EC6ED56792B6C2
	F57248ED54C951F9CBE1A900932097A0FE1E6BE46D4D5219F4424FB2CC99F4CDA762B31D11971C1F3CDEBA2EE146F1D9C963E8CC3DB311A2471060C963CA5B38B12E2CBDCF4FA8768B1642E28412F306E51E074AAF70BC34AE00F6BD846D07G66DE60F61D5D0779AF935AAB81F2G04BAB8AB815481F86B388EBF3C55B0138E6BF911688A692F0D49059E57116D6E27536D2E9947456B4C463DB4E5DCFAD2BFF3FE5F1FB26E7D24F6E652BCD42FB7C4546B8A2065G64819457DB8AEB815CG71FA2E57582D33B3
	7AAB967220D38F0B31C98A25E756C7C86D31G664D8420B8DF8CF12B211DDB4F4FB83B47B10697B570767B240D3909363FB216FC367BB40073270E95AF7ECEF824BA23D13D46FBD21F01BE5BE3FE867AC20F799968311EDD948FF398EFC6B87D291E694FC8DF6EC9FDC65A541BDA4E4D3D66F27E3457DC4E3C5E696534FF1CCA4EDF91FDFE4F0AB1C201F30BBFA01129E9DC12E287280DF6C40254CCF1F6B5B9F66A367F44G5AD4C9833771BF78385244461B3CFEAD2470C9DC1228348D8E6A01830BC6AE776F5F
	CE2D23F9374DF574655B66BAFA767674BAFA6E7674BA7A2136C7B0B93FDE938312404FEE4371C25627DD8B356335BE55232F8967B0096F1D9E85CEDFF812GBA0D611F9FF7B0FFF1B67098FEEF9877A78E6DF400453A8F076569E26272A4DFDC8BEDDA833585F13E5C86DA83214C945FCB700E46841E56C69BC88E4176A1204DEE647B7B233E547B73FA1F797E7C2C8F4FBBEAA822F82464BDF8332FD827A562DD0BA685D04D2CB66A47711A287824A9FB38333FD827259A3F379F6B7F813F572F30D3AF5D3B1A3B
	463D2B54777D25DF3EC36CC6D5A51584E4272809EC0CF00F0FE952C2D7CF68F274092975B2A81A6BC596D36BC595E756CBDCCC2D174F0AB36BA5CDCCE57FF42DFF9445FB97D99C8BC52050F7BB51AE166A77AB39E093F640158254B9E9EDC2ED7F4FC6D15F030A98006908F31FE8A2B69D63E3B3207F920055GDBGF6B771F30727876D4E60A5954C7787F698E5E5BEC8FDA2ED1C7BC49C661953B4598796B625BAAF3CC1FCECE9D372F9A10F60D7B4A51F97E68D737342DA2055DE64397773B24F3D99BAEED1G
	F14BAFA66E7521CE88629DC9AE606C17600C86D0FD097B75G2135C3BB8CB087A0D3277DB040F3A80E45B18F1ABF77994658AF1955BEC2107330B13FFFD09E46FB4A647BCE2A3F6CA83F23DD85B2B81A393DD474E37D24474AB2F1D42B293924783D8D222F15D89F0D13A5238EB63E2E194749AEF2B79E03769C00C54D7C8EA076A13F83D87C152705664566F7AE592E3B562CE6F6A75E1CB8EBEC9EFE45CAF9AE0CF11E7BDB201EEBC11CA5E63CABC3FD7D33D5904A60C96976B1A39DFD6C30E0241F1AC4C7F9FB
	ECC67A50A47AB643F83A366F7C062FAD6FBB19027C1733D2FE074DE821EBDA4E1EF3F26665672C9B37D19BFD06641DBC206D3F4C6D764FA790F7825A3DBAEEFB1C5BF778B237492D431877337436D546B65E4D5176A343463A8F7B7D36B560D41BFDE6920F5F669E5F8CE84C7AC37DEEE7BD2CE1B3C05195CBE1B9C07B959E33DE8C7C8FBC450896EA446FDA902F65C8A028DED4C281FA054B6269DF89F86F06210B766078EF34A25EB596CE42DB5E43FDED8AE2416565A4C64BBE0BE3AE0A2A4689749E77492051
	F642D768FDE986BCA9FD610899FD09CEFFC8B5232F50695D91B37A7AABFC5F7FF54A5D4D7D6D6516CFB96E5868030E23B13E045CA5EE3EC263F33E0E2FA078BC831E462E5ED19E3B9C2D16C239151F292FF951BED6C1BB9740FE9562BAC0AD007BAA2F198EF84A05994EFD2ED038950A8759D0265275FDB576693E5D7FF5810F91CBDE781A3074F34E14F72072D56A1BE96CD9726A3CFCFC32BBA88957378CE8EB8172GF6DF339416G54DD636B73FB581D8B1679D0550B1EA1F25B2C9F9A7C52E8820D85813561
	9746713BC7D4D2B43FC714E94AEF901576106C770C896957D06FEBC80CBA067EF10D7B5BCB93A85BD0C8710F07025A5411829B7B83924B6A8156C23B358DF19F441D53680E6F390DC1B10CA98558435AF5ED893B2032A8812C7B92658A7DFEC1F347F219670E74B61EBB5E1013F34763794658FFA8101CBBF0FC025E95C84E9D13474F8EA4678EBE1E66DAF57C83E16A3924CCC331EBEA0874AC2050B57CC35041782CEC63F7CE6BC6900776E4EF67382F2B9CD72B639E0D7031DABB0F8FE578D901796EE9437B40
	146F4C3F7BFCB1717F4A037AC237B775B96036577C9C60751A1FDF6FF8273FEBD03C296F9A463C466F315417BF7221EDAC063DD881108BA0DC87FF3E4E7D38C0BAA890BFB67161A62F8F4FA178EB900D737F11B44DD9D01A79B7A4BF17B0078D04B4AD944079B9DE98447D52C2E1022438ED0328A3456F9B52E8D706FFFB90EDCC1686891A7A645393F88EB71A3761BCD156555461EAE92CEF6D6BEC6CE8EBEDED286F682BEF6D2E3F59A9705819B1EA3C272EFE6BB3265F07914A2F531869E44F3CBD7373298CF9
	93D55348505C5651F22B6D1A4BCC0ADFD39D888E017FED3EE0060BDF475F85E68485DD6E8D5BF94C5F77A74F884E6FBB2D9F2410BB0B49DB541128F953037BED6C7EF45ABB06547EE58972F14DB0EFEF47641AAF4C5A338CF8BAAEFB040F1B8B3445GD906BE2B597B46A9631629FCDC9E500ABAAD0507BA93FD2A59FBEBE77238AF6A3FC96B871A86B05B50E7C16754DA155A5AF1DFEAFBBF651B595EAF784837D830D32C75A84E6943B3F6E8F0D015F43FEA719569389635BD247A716CCEFDB8BD04F3E4020CAB
	BB395F2E779D05E717F8E76AED9BA05B15D098D26C984512AF6F4CDE7E3147E867C573E5AB4FED496B4FE5BA595A197CBDE64F886653D217253099C0861885B097A093E0B940DA00DCG7B8D280DG4A811CGEE00A100B8401C9BFCADBF889E1EE6AD6CD0C88211A27AB19431BB71CDA4CFAD04F9165D606716F9E1442D04F6360E7B97025B826D6DDD9C57C272D9B134CBF55C4FC678FCF5BA6E2C4267EB57F11F901C975AF19D771242F94C51F1C7D44EE3010EFBD965BCB2F5DC3146F9E46938DE0D73D82D63
	B2B44EE3230E4B0FF09EDBF45C57A21C07231B63DE0BF09E45BA2EB24AF9146A38E81473286B667BB3AF047B2F67A4EACE4CEEB3C218DF7CC1B0D7D1760F136F441D50B168E33F7342D4447AFE987ECD2AFD06ECF39962E65B409D492339C41FBE6670C8EA7FAB9F19597F4E0C18671B8BA3D3730DF344BC5FDC9EB1669B0AEB9B417C60835BF46B7DDCBA6E260E3BC966C4DC375F2B8D6978BEA297621BA5E448719211EB14605C3CA6B060EEB2DCE884E388AC99CAE2BED69B41737820DF165937A752479F0410
	226B63AE11519F968DF2FFC647215C6C4EB3047279D15D7EC1BF96947D5D44D6C34823351B576519E19CBB4A56C6EB703FA37E76BA5A205E6FBF42D8A78EC9A254AB5400E8AEDB07258D6359CF749C7087092A68DC2384F78EE0894CC5E5582F10FB87F827FD3C44771E82D82B73FBD2C17949EFAF588FDBE85F67957E3B710D50174A70A60694C66FD4B93F2DBA3FBC9579B95C1CDFBD71C3340F7DBA6EE1CD7FA66B667316C593739EA17E07B93D4E4D67FDB90273366BF36C0D72F93DBA2EA74A3F93E87A3C57
	C793BAAB8B619E9F448B06033CA6B8A799E3FC79C4EB4AEF6E335C6CB7A66E64DA661F42782C28ACE7FC9E9D3142332A1C73BD25903E18548EF25C0402FDCE1F646DEFAB4833C217750EBAF90EF7B51477A03B6CB82877F3EB586F245EEF010673D46A747A8852ABF4BE1BA2C4963C9851E5A90BCE1EC309629CD81F5138D49FA3B66456FFB3B70A7CD36520C630A39A55384FB44510274AF074FFBCF2BF990167629F8B5BD4C5F807DFEB163590874C0ED65558DEFD46E6956CAD42AFE8C0EA916C98D5DA04EDA4
	683488B688C8B17CE174EEA3E8029D77979E28EECDA8E09A83842ED950FFEB7109F15F37BE6CC9D9435AEF517DAE3E151C5B6B2DE8EFAE4007EFF15DBC1EFFAC15EEA0EEF786C4D9069379F0FD500BB13CE1CC139267150BD61CDF65FD98117D1E460AF25FB10C79BFD0CB878871001B027F92GG08B3GGD0CB818294G94G88G88GE4FBB0B671001B027F92GG08B3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB993GGG
	G
**end of data**/
}
}