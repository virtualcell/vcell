package cbit.vcell.constraints.gui;
/**
 * Insert the type's description here.
 * Creation date: (9/19/2003 10:54:27 AM)
 * @author: Jim Schaff
 */
public class ConstraintSolverPanel extends javax.swing.JPanel {
	private cbit.vcell.constraints.ConstraintContainerImpl fieldConstraintContainerImpl = new cbit.vcell.constraints.ConstraintContainerImpl();
	private boolean ivjConnPtoP1Aligning = false;
	private cbit.vcell.constraints.ConstraintContainerImpl ivjconstraintContainerImpl1 = null;
	private ConstraintsGraphModelPanel ivjconstraintGraphModelPanel = null;
	private ConstraintPanel ivjconstraintPanel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTabbedPane ivjJTabbedPane1 = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ConstraintSolverPanel.this && (evt.getPropertyName().equals("constraintContainerImpl"))) 
				connPtoP1SetTarget();
		};
	};
	private cbit.vcell.constraints.ConstraintSolver ivjconstraintSolver = null;
	private cbit.vcell.constraints.ConstraintSolver ivjConstraintSolverFactory = null;

/**
 * ConstraintSolverPanel constructor comment.
 */
public ConstraintSolverPanel() {
	super();
	initialize();
}

/**
 * ConstraintSolverPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ConstraintSolverPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * ConstraintSolverPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ConstraintSolverPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * ConstraintSolverPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ConstraintSolverPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoM1:  (constraintContainerImpl1.this --> constraintPanel.constraintContainerImpl)
 * @param value cbit.vcell.constraints.ConstraintContainerImpl
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.constraints.ConstraintContainerImpl value) {
	cbit.vcell.constraints.ConstraintSolver localValue = null;
	try {
		// user code begin {1}
		// user code end
		setconstraintSolver(localValue = new cbit.vcell.constraints.ConstraintSolver(getconstraintContainerImpl1()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	setConstraintSolverFactory(localValue);
}

/**
 * connEtoM2:  (constraintSolver.this --> constraintPanel.constraintSolver)
 * @param value cbit.vcell.constraints.ConstraintSolver
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.constraints.ConstraintSolver value) {
	try {
		// user code begin {1}
		// user code end
		getconstraintPanel().setConstraintSolver(getconstraintSolver());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (constraintSolver.this --> constraintGraphModelPanel.constraintSolver)
 * @param value cbit.vcell.constraints.ConstraintSolver
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(cbit.vcell.constraints.ConstraintSolver value) {
	try {
		// user code begin {1}
		// user code end
		getconstraintGraphModelPanel().setConstraintSolver(getconstraintSolver());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (ConstraintSolverPanel.constraintContainerImpl <--> constraintContainerImpl1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getconstraintContainerImpl1() != null)) {
				this.setConstraintContainerImpl(getconstraintContainerImpl1());
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
 * connPtoP1SetTarget:  (ConstraintSolverPanel.constraintContainerImpl <--> constraintContainerImpl1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setconstraintContainerImpl1(this.getConstraintContainerImpl());
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
 * Gets the constraintContainerImpl property (cbit.vcell.constraints.ConstraintContainerImpl) value.
 * @return The constraintContainerImpl property value.
 * @see #setConstraintContainerImpl
 */
public cbit.vcell.constraints.ConstraintContainerImpl getConstraintContainerImpl() {
	return fieldConstraintContainerImpl;
}


/**
 * Return the constraintContainerImpl1 property value.
 * @return cbit.vcell.constraints.ConstraintContainerImpl
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.constraints.ConstraintContainerImpl getconstraintContainerImpl1() {
	// user code begin {1}
	// user code end
	return ivjconstraintContainerImpl1;
}


/**
 * Return the constraintGraphModelPanel property value.
 * @return cbit.vcell.constraints.gui.ConstraintsGraphModelPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ConstraintsGraphModelPanel getconstraintGraphModelPanel() {
	if (ivjconstraintGraphModelPanel == null) {
		try {
			ivjconstraintGraphModelPanel = new cbit.vcell.constraints.gui.ConstraintsGraphModelPanel();
			ivjconstraintGraphModelPanel.setName("constraintGraphModelPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjconstraintGraphModelPanel;
}


/**
 * Return the constraintPanel property value.
 * @return cbit.vcell.constraints.gui.ConstraintPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ConstraintPanel getconstraintPanel() {
	if (ivjconstraintPanel == null) {
		try {
			ivjconstraintPanel = new cbit.vcell.constraints.gui.ConstraintPanel();
			ivjconstraintPanel.setName("constraintPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjconstraintPanel;
}


/**
 * Return the constraintSolver property value.
 * @return cbit.vcell.constraints.ConstraintSolver
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.constraints.ConstraintSolver getconstraintSolver() {
	// user code begin {1}
	// user code end
	return ivjconstraintSolver;
}


/**
 * Return the ConstraintSolverFactory property value.
 * @return cbit.vcell.constraints.ConstraintSolver
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.constraints.ConstraintSolver getConstraintSolverFactory() {
	// user code begin {1}
	// user code end
	return ivjConstraintSolverFactory;
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
			ivjJTabbedPane1.insertTab("Tabular View", null, getconstraintPanel(), null, 0);
			ivjJTabbedPane1.insertTab("Graph View", null, getconstraintGraphModelPanel(), null, 1);
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
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ConstraintSolverPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(566, 624);
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ConstraintSolverPanel aConstraintSolverPanel;
		aConstraintSolverPanel = new ConstraintSolverPanel();
		frame.setContentPane(aConstraintSolverPanel);
		frame.setSize(aConstraintSolverPanel.getSize());
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
 * Sets the constraintContainerImpl property (cbit.vcell.constraints.ConstraintContainerImpl) value.
 * @param constraintContainerImpl The new value for the property.
 * @see #getConstraintContainerImpl
 */
public void setConstraintContainerImpl(cbit.vcell.constraints.ConstraintContainerImpl constraintContainerImpl) {
	cbit.vcell.constraints.ConstraintContainerImpl oldValue = fieldConstraintContainerImpl;
	fieldConstraintContainerImpl = constraintContainerImpl;
	firePropertyChange("constraintContainerImpl", oldValue, constraintContainerImpl);
}


/**
 * Set the constraintContainerImpl1 to a new value.
 * @param newValue cbit.vcell.constraints.ConstraintContainerImpl
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setconstraintContainerImpl1(cbit.vcell.constraints.ConstraintContainerImpl newValue) {
	if (ivjconstraintContainerImpl1 != newValue) {
		try {
			cbit.vcell.constraints.ConstraintContainerImpl oldValue = getconstraintContainerImpl1();
			ivjconstraintContainerImpl1 = newValue;
			connPtoP1SetSource();
			connEtoM1(ivjconstraintContainerImpl1);
			firePropertyChange("constraintContainerImpl", oldValue, newValue);
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
 * Set the constraintSolver to a new value.
 * @param newValue cbit.vcell.constraints.ConstraintSolver
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setconstraintSolver(cbit.vcell.constraints.ConstraintSolver newValue) {
	if (ivjconstraintSolver != newValue) {
		try {
			ivjconstraintSolver = newValue;
			connEtoM2(ivjconstraintSolver);
			connEtoM3(ivjconstraintSolver);
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
 * Set the ConstraintSolverFactory to a new value.
 * @param newValue cbit.vcell.constraints.ConstraintSolver
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setConstraintSolverFactory(cbit.vcell.constraints.ConstraintSolver newValue) {
	if (ivjConstraintSolverFactory != newValue) {
		try {
			ivjConstraintSolverFactory = newValue;
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
	D0CB838494G88G88G5DFC71B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD4D46715C85410948DD1B4E8B59245E8AAB13851AC1B30291BF5F3303ADBB6EBFBE8E33749299A372156EDBDDDFB4AB9FD7C8EC68C0AE8A002CEC0D8CD88A151EE8DA1469F8C90B0F094AD5D1833BE18870C8EB31319B74CA0BF6E3D5FFD5F1B47705E0CF8A2675C735EF73F1F7B735DFB3F6F5EB788C5C7E2F3B2B20B84A1A3C7D07E7E1CA188C9A785E15A13FFDC4FF1A92E5C82C173778E40BE
	611D0AECF83E82FDAB9F4FAD58AB4C7766C1FB8734A3207D963C6F972E94C51C009783BA5721EFE44F162E60F4BE5E476918D87B12AB9F1EEF826C849CBE9B5CC87F062BD841173A894FA1A3D3900E49B8E60A2BD441C54018D800CDGBB1571FBE14CAEC6F9CA5DA9E55C3507178A9BFE305A348757512C4941A9535CE865A988CD26FA8F52DA4E7844B96B21AF9BF0A8FC16D0FF7AFF60D95FFE3377F4F717D52E996816EC361AEE075DAD3BC42BDDF65774FA2CF5F567D544E507EDC0F2350BF649A68809EC6D
	5F397258DA0290866D068144FDB602745221DD846D6A810E7F3D007888F80F85C89C60B2FE7A5F750A8C1DC3FFABECBD685CD7E0A0E34AG4936F340AFE3F26D324C92369F03052877135E5C02CEG9BC094C082C08A40A1E687E17B5E056759AE793C233F5FE1BF6BF41E33D9DC979D9649866FF5F5502360AEC95D8E17C590E82FD73A2AF5788986B46F05894FA31DACBD4177BD7E52F2A16969CDA6130ECE12162EB5ED75AA3A7079F5C2347FE0C85B6C9BEB17E3E97FC55052CE6EEDE77692C8FBD3C7FB41C9
	E51DA68D6D49BE5C6B27198D7441FB0632AE61D7B3FC3286CFB6FA0161313D897A1A8639EDF43E49F9E94DDEA8EC2F5D2C2B875F8BBFB5458F928FEB8683F979A683575EBEC8B477283C04B1FC51ADBF1EF879A333D3EC1F023EB8G322F70A274593DC3E053G8DG57G0681E2G6207380D2D5D77419D5AD83B68124FF92CB60B6492E8EEF861C1F8CABEF038E6F0BB515EADDD95DDD6314BA63985E54CF78AD187DDE366C5B15B6F869A0D12DDF2093255E1075545FEC916DCEE9AB327F09F0EF1C92C37435A
	AF8101FE274057FD2CF0BFBCED22DB3E62340832649FC17DCF94621E8C08D7E1C5D8G5EC9AFCF9DC0FD2D8779B7816CD075F0F4BF2E57A239A0DE545535BA9CB7BD4E8E14C4E898C6BD973AB1F664413BF9186BF859C144C5C0BBF6182F73557EC24DBA8D56EEE4DFF48DF29F7B3B113D70DC8963D38152F13D91EEBF5BBA397DB46CF9D2487F51AFF46D2773CAFBC17D8859CD47C8209F3D4856C79E0721EF9EC05288BF83DAC6BAF57C88F9F8D36109795B083E3F99C12AB20FE8D8F4E91814E71646F7A7C2A3
	C7F9125ECB0AF18FB7821FBB810AC7396EE76FB713AD1F935D92375EB697981EE4716F8471694DA8D6F41B252CEFD2785A2D4AB2150D290575BB8142G168224G6C9C65BA4D49389EE41D094898BABE1B99ADBDBAFB3EC5DA244B48A2BC57248149AEB7C8CE49EE01973752B7338869AAFE5F63D0674CE1F8A74409736018EE953F2088FDD914E517354BA3CB17A5D9365AFB7D7DCB98AD37274B698FB11C5672A214556105B8210EFF3A88FD8C4EFE3B44B6D51D22742FE2BEAE5924FEE07A3C43E317897F5C81
	3C935D14861598737D83398246881B8763955DB392C4542753E56D87D34110A7B8EF237F9FF797EBF4E4869C5E179A6059F91B7B2F37027B6F51DBCF88317F773CC96FAC72AD7E19A971B6794C7A5BFE7FA51E928E6A737E8C0BB58A5ECD3C969EBBGCF03AB8F33137D026D8C45DABE86C64B7014DC4D7DCE5B07B466173E37F45679DC6C169D9038E8CC2AEF6F18F5E88C7963259264B98DE49142409F4338BFFE74967673D828B862F9074DC65B86FD1DE1285B7C51A2C5B73550EE8AA35D32F31B75BF2D76A3
	44AA7DA9702479E537F9FF75A45813C97E79444711E2645DADFD6311600842205E225AC0D5F1E1C05FD5459E283F26986D4126B98F56CFC21AC9997CCE9C26500B19C4B1B7891E0781FFA69C47CD4D64630A43293FB61CE2B2BE911FFD9F0EEB4F6263A243A95E4405D37CCED4467958385F28635A6F231854F71FFF5D9A15EED2B69F3766BEBF3D0D70AECC2675E85C83B9FCDC69E47F3813705E290CA3DB7B6DF134A3F67F3EA18E083E9A3797FC382E6E8BE6EEF43F1E494EC89CAFCF6677686F8F20ED44C3FB
	8D4046495CD77EF58F3FC7C75FDCAA4475359598658ABB94BE0A2778FD45721F1F6DA25B1B77B6526C043E3EA95C6E1EA8561E65E75DEEC73715DDA314BD6DAC517A92613EAEF9831EBDD617DBEE06E067637B5FDD027C62BDC3C1AB772E9323790A0F8D835D15GE9D3906F9C45980336C0F2F61F60F2C65DDAAC1C193555345BC04E5A29A4DF7BD43F1C55233F4BA37D173EDB4E755F6D682F31F67557F8867AEB44AE39C632D8653ABA6E2ECA2E734BD11E6BC4CC653C0CFC2E55793C02F5063C2CD6F8F8C543
	4B4EED5F94106C5F93F80EE63E9F6C64FE1E237D750D770520B95A787CCC5567668C2E4FA8D8AF9900624E5451370C6317921B1C1928F3394F6AA6B9FFF3760CA2477A658B059853CFCDFAB9DA5312ADA6A10264BB966117F36312E5195B81082F27B2B9DFBE9813904179BA18B51666130735B437DE7FB7DD1A367FD8E55A2A505A7B001FE6671CE51936B91C66E096275989E3229E607AADBA0DCFF6DE29FE595C0CF48355FA4CA0C7BE2F2F4763A403FC0A4B907F52AA61540D0D3AB2A0A4AB3CEF5748E0DEF4
	A273A4G49F0A81B4BD07FE0EE017CA057DBF8B66ACDF475FEC8B4077F4AE9062DDFA96CACFC5910E67C03C4EB4303FE1AC7E11FCE29FB351951C43D9C7D8E64BBGF4F6078DEA4F6EE84027G6CG480B64FCB9F3744EB7DA77EFF2783AE6985F9949576D9A522E9B95C96B2642B3CDDD7753DC3DF569BE97638E4CF93327796F5E4553465F43A997BC61C5FE2A27D1AE182562BFE578FA8D1E787F27813E972D5017B00D67AFAF14A17FC553A16683B481DC878883081D4EE3696BA5D80A8A1A9FB65BA1ED330B
	364BE3AEF0BCCFFB9B69421DCBE9AFFEDBEB4324070FC7D18FC9536FBC6F20F90DEC5E06894CA31BBB7E3F5ABA55263DB126AC831B4BFE0856AEFA48EFF324BB77913CF3F4G5E899081908B307AA12E37F78FD507529B4F15950511CC3F9EB52A033C72103E1C444FCF4C484F8E9833B7AA3760984099006BD11C9F79D048FD3C60535DC762ABC1383B1A5273F7B96F750CD0734222466A03667DBE643C3800F9243F8F4A719E1484FD29G5B81729F863FGE885F0BE4C7538665039D0FABC4B931D8E077AFABE
	B0D9617C2E4A3CBBB97FB1643C080775640C2BC0B9E321AF89E08340F6002299A0ABC0478CAE674152CFC25A8BA607A0A393EB3C0044676D2C3B13EFDA48F9728CBD790ED622FC11503794E0B540C6GE1A65C51814E4C64727516FE96CABE35A495CA46F7326FCE468FC24E3BBED3F78F2BD0C69F74C5834483A483248164C56796D4C7F3991FAD8DE92BDA99C35A6A6B6378ED089E4B1F01CF0E1BF7B8D04E00F568EE53F420D618D04D1F65A2F4B64BDE6D596C0B263ADD9CBC9391583918B848731514E81E2F
	BCF58B4F4A3CD9B09E6054ACDE67C94C39BB59DF9CB7AFD07676D9FA32B78CFEB2B1595D0DAE515947B617AAGA4635CA1AEE35FACAEE364B04A988B6DC4009444339A67D3ACFF5B8A6DF4959769C11CF9B668E2B6574547DE3C43DDD35B67D99B6FBE54DEB790D8834431416CFCA2C0B4B60D231175AD2D0FC07E332E4C388676D219FE8DAC35CC3F86766FE57B88AF21197ACFD75E3F25ACF80DECDB19DE0D2C0C497DE3162338BCF6595AAFB132E4C3128F3F850217DF7258A984A0E5F7607DC360F145F91A65
	B433F97C18FC78A3E1FC7C38G69264345976910C4D7DBCF0F9ABCAEBB25EE4E735091E37D059F5157DF4491FD7DCDBF92DCFF330F98690F6CB877B65AF154EC2A8F511CD74D467CEDB56B73374D2C4F5F8EF3F07E524DC67C519E3EA6E3ADEFDCCD73CB084E822F6515E76ED246344B0E768F5535BFAEB71663CC393E9C1F17E33D456D70383A25C0DEAF16972BFD7E4320D9167A119D9AF3154D17A1111346497AB51BCFFDC673FB4A317650E535D8DDCAB510E4CCD3E53CA0BBAE7AE5DCDEE1AC632A8AFD991F
	2BB01671050A50B22E2BB01671250A50B23EDCE13467A46BB3D93B79FE76389C7DA02DD2F32CB41675CF157A32FED2E9AC6B394A503236D49A4BFA29B2342CDFD5861735B3FB0C2C6DDC5665D5C176354AE0DF2B026CEB559D6CEBD510FD2D3A03FD2D8A34DD726135878BD88CF03BA5B73B9CB7083248FDB7C7EB5798D7A9C63F1707F3F56A2A9DE2D717E44123DC60B53DF8AF2F69454D66E779DA9F7B96866D9DG790F70B3786F87035DAD0FBEB276BCBE85ED2AA1D48C61FA2D0FD08D810170558C5F2941D3
	EDE155ADDE8B34C0DF72A33C6E716BB172E26EC9FCAD988A46D7F54CD83E4E44F03E84764D3DA3A6102FDD8CFFBDA6102F99431CAF9B742544F03EDEF463B79F1BB8687048F0871A03E3FF26FE5BB9896DD6G99A0E28E5D99E91FD6F8F14FFB60368C5B438E396139B8F711074F5D8A635381CA81DF8FF0EDAE27B96D8A722259518F151C7C749E2D4FA8311E68C575213E86449B9D03CEC9515D8AEFB05D2D19EBD4AF7AF49069274EA59D652878A606CF5360C9F7BF7471FA17705D5C824BF779DDF043A03F8B
	A62B38BF5FC2DC9A3405F95C760E8F896AFEFF24F9FF6E165E3761EF77DE38611E52A0191F9962FA481F4775D0B24C75D02B6AA1F744AFFB0166BDE6785E6BE145BD25C1F612A3F39F2B871965F95CD7365420FD2BB12DC6744AF5F5679C787D2F159C0F6259C5B9CF199FBF0F4733A13666A6E82783944D675FAEFCD57C5BC57C638B05980B7E7722559D6BCC5573156FA873B5B57D5AE519C4F32D07530C047EB5739177DF9E6D77C57459D761A2DC83CF6ABFBE206D47F36E38206D7FEACCBF725BBFE67E34B1
	7DB91A79A45B3ACAAEDB7A3A8421F555CB063F1B4BFF14E42AD51E09D969397366FD390BECB0594B6D326FD1B5B7E3BE9B8D6D849577A5436137C07C4EC7389F79383DA4BCBA31DCAD7A8E46930DEDAA0D1C892C4F77A4541834C7D1872F2B357ED49D3EA89EFF49E216ACF6F5D8E51BF4F2817EFE72879784F50DFFD66B7A3E85635720713F7ADCD0655AAD5FDD0D3364AE67DD1A70BC0A7797BD68DB118B824F502B8C9F33A0708C1D247AF79C746545726F86A90D289F9B34A3GE2819281D281F644725CAA7D
	B456EE02561EBA9C4ED6387859B469924977AF1E3B7B9D62BF18389F2DFC76FBC24C5499065FB7739ED33E7BA94F764C74DCFC12FC5D4DA8DFA77449GB1G89GEBGB6BE4665EB78CC296FE3BA8C592F585D473EA62B4935D5727A51F8E148DE08BA7D39579CC40E6D8ABFE427EF3C0777496E3165AD256FF9DB49183E406A8F3FB36E3B456BBF2D8BF9ACCD7EDA9BEF0477666B461B542F13CD139792BFCB977AE3296D40665D344E8F3F52FEC759BE67D9D3064EBA889B1579DBB56B90FF3FD56BD3250BF8AC
	3A6C91D4DB3F26F93FDF3E7767E66CBD25C1367313C6631C6715C67D1CE7CB23FE5DA12DB1F85DE8C72371EF2B7E50287DED95597E099676DD8B764289908D3094A0E991377B871A4A496EF5EC7E0225D7E285C951AEF37A36E6E3D95D4D21FFBFBA5C0C3F4B68F24832239F57677870E65CAF5961E4C8425D5F0CBAF2D9FB7BE49A2A6027B5230D59249E06A67FC977E29EF0DE42EF17BCFEF8A56E97254B96896907271B769958F36AA26537BBF1FEFB765B5041DB5A78F58A46344685BFA3156F312D98F72E43
	58E100D800C40075G1B62387E4B3EB8864F2B620D7155B8C052754ADF9426F5B7DF94D4DDF4FDD1E9B49FF6708B382E35F55D002C36C36CD56559B9A6E75B893C64AF8EAE0FD213E8B15E7BF3AD21773E25057DCE87768C13A0423537603EB9FAFA5C12EAE3D7DA762A38E6F73B43ED45241CE4B7351961E986FE6B812E8378GA28162G9281D296F35D3E5692CC37F401756B166EDC6FD37E83EB94E4737C60A3064BFEBC376035EC35165C9A24165C9ADA97AF376A7B412B2D637DE0CB2B3E9F2435EA7D00F8
	AD98667C2F48627C978DF37EAFE6713905ED467C173405663F3CCD1F7FE3ED6379FF3FCD1F7F93EDDA7E89F7CAD9572596CB89FF3A8D4FB44A73AF0AF6311745B84ACF44AA3C57D86DE0A6226DAFD60B5C277C464A37470FEF1210BE3469FFF2D25FC1BB0A7F95A4BDC2BEE4729E1B68B263A1AF8BEF303317B5145F975C561E0B29D3D6687E8E1479526364C779CB82FFDFD0FC94E5ED823CE589374F3E0A7703786E65FE51E613C55B4D33F68B7A7139C1D9721F83E5C7F1FDB79F23F4AA3FFDBF0AFC5F504C79
	FFD0CB87881A6562A14F91GG64B4GGD0CB818294G94G88G88G5DFC71B41A6562A14F91GG64B4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8992GGGG
**end of data**/
}
}