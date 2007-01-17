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
	D0CB838494G88G88GEFFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD4D46715F6CB3A64D4D7F3C2DA12B5BDB621A96952D437762C6D31B96C1E1C54CD6D09E555323B581244531A1EEC42695A2E59F5CF9FB3834C7023284450ADD15A188495A988D19C5170B3AAA88A4AC8B092B30F19878C3E19B74EBCFE9DE46F775D773DF98C6F4D84CE42B977FC6F5D6FFE776F3B77FEBFEF604A8E2E484BB515F1DCEE9E277C1D4C6538358D9C77C053E606629A5F4B30F11A3F
	BF816C61AE8FD820ED073E0A1B9936D26ED52B955E7B61BD67238C5B1B703C0F7BC2CFE2BDBC9848F1C1DF768F3FF3AF361C2503CCCE81FDCF1DAA06F697008BF024CDFB0F48CF1FAAD770898A1EC12E096316D5911AFF1F2AD0F0B9C0E385DCA7002B8A69A720C5856E33B7AAF47D8F24F25B1F8B1ACA889F8DCF86194A581DCADB497DF7EE4AE1A24BF237D89953G6FA2GB15E4CB51DBD81EDD3776D21336E812FDCB36696C43146AD79C3F210777A65D04D502857EEEFD391BD12B8A6843BF93FA0827FA304
	F767DDAB6545F15B00EF9A45BD14C764F960B98C10F404613BEC841F834F562399364A234C46B36FB5A9B6745D7D87EE63F11FE537010D0DC751B65751080D6BC736187EC0676379A162776550178E3091E087C0F9F5062D91600FB48EB68F9E0136E5C0EE13FCBE495F9288340A1E60E549A30870EC37C30F023BA63825200763F02E8B6FD66B68938BF0DC793C4721CFD26B593C27DDFB0A4B7A6ED7CC85BABE49CA3DEB6A2EC6DF8CD7C7FC02322F9A4ACE2C1E9D1733E5FF48E9E52F3B33491C27A3FB6B2DCD
	66658A1F159A592FD41339366513395EG4F398ADF446F27785AE391BC46685F5218A16F1D503772980B0D3EDDCC17CE4BE3DCE95DD7F57D107CCF0BCDE5B528C3F5CD34AEBF22F157D703B2CBD4DDB6D3FCD803C7DD2ED3BCF9DF81FD45FF4930E1FCE5FAC94ECE40FB92C09A40B3GDB81385A8CDBD9AD0B3103C3FF7914B1564D876556D12F6891029C0E4D9C5A8F2DB081895781E9477B5DC2AF9F747283229062941A9706088F86E60D7BA50DFDB74838A07805A0AFFBA5BFF067FD02AC84C3C8735A509EC2
	9394E82F536B93C000AF40B13E7FB5348FDA118F4957839EDE96A294587F7BA1B2A7E3FCAFF08486700CFE493BCD7C55G76F782742BFE189AA67CAE89C12897F6FB27A45D9E8DB809A5DCDAAD7173B02D9D81F8CE2CE5BE4E9589AE875E2DF50C4F2A613D9ABE8E2F1B284F87A7D90E6D4EAF053682681B81BCG09F5ACFEDE6DE37163A879B6D73D69A15D78697BE413B9258E63E6EDDDF49ED5D07EC4472D50E7B91EE1BBF41C2D81DF4F6B5349A30F4223C2E9936B74734D881A95FA147183DD9985CAEB5664
	6EFCE464A9AD7ADD7211B9F400DDAE00C547196FDF992E44D8EE65C3820B5E2EA0841E60094C846A7993DB3962DB33423FC0512BC835A50B52A4837FF400EDG2575D01F81DC754C27B7ED2E98FC66E3E37C7ABCC6E56949A9798CE521AFDF7412F5CD98937C32C38888FE8FBC04143E173DC42E12770312BA66B50A8FC01DE803448C29785FFAC9AE73329C748E0C4AC20FA04BDE7FD0247FFF292C5068C0A0D2E218AC4B8831D59A07BA21525BC6C80E415A6F9768242AC3147EA21A6302A878C069B6E954AFA3
	FE4FED32A73AADCCAAB5660D5B799C299162A829D7384FD84993FF8602DE9F048AA9F9DCE6BD497F193B659A9FA5820E6C175220CD2FE779BB7EB64B5FC3D3CFF2691F84CCFAEBD172B7161AAA9BB0E79A9AA2790BBA650B7A3A9753DA2360C3286B5E2AB720B5587250B85947094DD8EB998DD04B508A418EDFC02CC31ADFCF3C294367BC6F16A5A8DCC813B9D1BA0B8F52E0BE267A094ECE30E598E0D1834B47C4117433DA28A4E21BA40AB8ED50175EC0FCBB10DF46FC836FAB9B5037F49DFD1F2E4F85E59A7F
	D95F477EC6E8F1FCC86DCF82DC5A7B11712847280F689E926E0C8A30C41122FEC90D0130EFEFD4DF2F928F581F60A371A0EA56030693C4665EC236A75E264863CEE04DBD846D814027D23AAC15EE11521FFC82EBB2E989DEBCC9681EA8E2F45BCFE03DA9BE09753B72A452E5D33A76E2C63766A456248DA7A3FCEBD439FBCB98DDF5E3C41E831E079B119F527DC225CB5050AD0767F4058EE36DBF8F13B8227B6F91FE0C1F28890DC38E5B6D97E938617E7AE53AC6927A2CC6360FFEE30644C659298CDBB5006394
	4B151F1630FDF4526DD4AEA53851ECF4D668BF05FAACBA954915EA573899E36FB79211198EFD9BCE31387B3DCF3B163704C2125BCB37914A1CBE65576692623E6BAF04F6509B8C499DD06CA6587CFF5FCF74A57B8C852D6C3B6E27FBC3129BDBC1EE05BD436634937C3379244610D8C0BB5D754C4EE5D35F60DC8FBDE2AAB230B3590E762D31C76CAC7DF903997DDFF1644F4C7FEE49D7639D70550C0E79EA788139C670F8E53B1D252BF256E9AEE0E71D9CBB53E57A3C5667EBF7CF9B6AD2F59AF5E8BF2D51654A
	6F4CE8BBE7E3E734C468CFBF4D4EE8B78A0FF1B14FE8F34FE72ABFCF97B2FF6EGFE35CD99B62CBBD979EF9A57AF25B6BDDBC8FCAE8FFBC3E867EFDA1A95BB56BF75983726E031C96F0CB67C4DE453F0935A372CA9E2E7DA7696D3BAG6A15D744744A861A72B3CCAF31E8364CEF9F504A347277694ADC7C7210297B0CF2EEBE9311D9153145541441E4BE2F4ACC871A9DE718FF4B4E12162ED7EADEF6F4903951EE2DB2B8A337697B71B05AA0B7B29B52B23E47750D7C0D2E8D84EA4F226EFDE7A3B6B43E762302
	CEG3461CEB133A185E8324EB23FE596933F714121BA14993E41E4AEDA3F1AAB5F3744D0E65987A82B6903084C6D3F58E2322AF3F53A24D8714BE220F986G576E4D395A35FBA7609B1BE16F8F90E8E6FABDDB22373EA1DF6BAE46B79168531B995FB5E6AD5F9D4D48373985EAC18B637B60AEBD3E381FFBF54E19D7EC096C3D97354C5D0763D970FEDAD313DA70ACE8D6718FD2FC0A860F7A7FF1064D45EA68ABEFE56717129051BF8C6F49GAB815681EC83303631DA5AA2112B281867438EBF9C5B7C3C58B3EB
	83474EE992118BFBAE657DFFA4ED8C239F166693BF9CEA7B74678E9C77A89D57B40FF198F307BF525ED3D976DCB4198DE2CED4F8075BA2B1073EFBDAA6FB0E35004F84488130BAB2ECD58E6637C5016AF8FEE3E7E545E1E853398223FB10F6073E1D284F21B122CFBF50CCGAC83C885D8276A73DCA06EBC1E1B501DC754AB5F3630BB2532850EEBA90CB7EEDB14BFF0DCD75CF14567E60FC37FCD0D13FD50A168EB86688310811281D683E41EE3FE7C3FBB2D717C58428EBACEC9FDEC0BBE2CB0FD0B0A96E6679F
	620E4B5135733589E22775BC6C75G1AG7AG42G49GEB4FB3BB45BB2763468BB99C020D542C3986221E1B96E85FCBF147E51D5733CF22F7555B212F748264A30083E098A081A07582336F07F7BE08E71FFAA5954F4671620559F8DF5CF16BAE684E61DDE2E3B6746D84A8EB077D920093A0G10544EECAC8D460DD52D0DF1E33525A4DA5F3476597A9964641CF187226D0C6203FB1B76B7EA39F95D795333882E4D4F4DE857666CF63C37AB6E007394813AC75EE5E26715468EF6DE49B31335B2G6F09GABBA
	58BD4F2EDD8B333DE24E38E85B57F468596E18BCBDBF5BC39702FCE018CEAE5EG200DB9E6E66386554697AD44C66BC57083C0A3G5EF1660589EDB73CFBD45C0B538417886FABAEB2DFAC1DA1FB38E754770769BB597B607B1FE6226FG89ED2CB81F8F208C7B9C99664F08BF814C1F7DA163BB3037C27AF7E0EF07746F408E0776A0DEA0E19AD9DDD9FFDDA8769D5929105E9D59DB546E4DA6B21F41D13F6C7589D4AC4610B0C13E85C216DF9B75E389A012C3517B8F0E5515E353E49E33AE327A71FC68B8B737
	FE1C03632694E4CC1C829F6C9A9CD40BC7CFC0F0B31D1F150D7D7793D95FFF7FA26B7B6FDF65587EFBC1B6729F46F1BFBD8F6F380877C3B826FA4CD83F7AB1FD7DCE0D696B7741D8EC7DAE0C996907F378AB195C654D39533C825519E3F7F99305B60526DB163A6BD45E896346F6FCF9DC5F0E874649FDCBC89A8D3A05E8DD13474B553E48E250A18BBE228E52FC1D0E1761A0A74C31B5150E47BE23712B46495D430057638DAA3701E8E3EB9133711CACDD0E58787A04310D5C243E0D791346B616CC4637F17F24
	310DEFCD4637715DC923B9C7DB0B0B0B58FC8E86A59FD80B63A6270CEDDDB425EF6B17260CEDDDB2955F562FCC995B7A68D4FCDB1F180AED6BD333ED6DE6363EFEB7463C068D66B59CE3DE431FE2DE43B166B57CA966B59C9D3B18437F3CFFB72D81211090221BE387AF73ACF7CB34F1CD6AAA566823D6B2D667DE55498F8C889E3214F36CCE4F4431BB3D62CBECADBF18CB3F0541FBBF00FC092D417BCC3176160BAF4DDE0FD740BB5EA1CC527D4F6ACBF807C0G71FFC571699ABC5EAD941959DD60FA682B6DE4
	779E67F6E96DA5E7CF546B3766D8FAA5F54E56AB35136955CB3F392F6D0C566B930ADF57992D57B68B53EBA374B5DEE6FAD50449B79F111F14C6E55883DDA13475E136D7DC8EF42B8132GF2AE631E9167896B55A06C16E1FA68A237150EFDFD1A0D6D06F78FC082C08A40B3D7184CA55709AE1A992DD3567C6992ED4EA8359E65AD9BA67E9A63C71C1381C171DD81974BF755DDC677C58F5298E96EC29F6529782FD13CD303C75F3D134B6E3B0621EFFD975B8B9EB1313DE0EDB743AD21B8A73C8FF733581BA1E6
	ABBAA7EA1E7718753E8DFF367B42A31F2B8C343958427CA02BFE10AC4C8F492A9F7A73A2368B1A67D7AD1F3F9F8AF43F43FFD600F1F2B34CF2AC85EC4E6AE639724B9A925FEACD2B6147E53B3DD5A25F7FBAB171301EBDCE4F9BE4FC59D5D64FFECAF9F6423B87A0FC15FD3B18F817FD3BC8FB62B1AE592F7F3DA85BB9EDCA3A2AFCC7399A39334B9E59E2C219876FB1195B213F7A9A41DD3E277D3EC8827685588857C00B7DB3337A49BAF71853762F1A55CF74754D9A7F522C7EBC4DF8346D476FB05BF67EF8A5
	A7FEAF4170F7F372B534A9D9E917EDFDB8F75BF7264C980387B896179B2E29E733DC025B896F65BD8C17C2F164DBA0794E07382339ACDE885DFCE2E167357874A843214A481B87FFB6A771E81CBD4487ADA6D60F1BF56C40FA1CC2EB164C8FB83D32A8AC6FA13F1F7CEB7A598EF93CEDE1BC32F5F8A07D37B4794C0797F647A9ADF0DC6A3C47E13DFFFC1A6456761E68B5B40D62B9E7749A7AB3B53F0B21AF60E45F8D1664937FEC04779CG6B758CDBA5C0B3C07FF5F6367A7EB4393B09F9776414821D307193B5
	47A5346F503D057D8E719F8BD89E2D7E413738B5DFF298FE5F8CDCD772C7E92BDEFAB81734E8DF251558178EFDD9GDCAF648EC0B50023175917FBCF395FA747E1B87D726EE17AB5D9BDDCFB0571881AEC9844DE625377219E995B51570B7AE01C969EA57BC9776C6BAD256FE97A5B0AC8DF746D8F5BB38E29F7DC2BFBD9ADDD7731365ED49E6D522DB7BBBE1EB23D2268D37AE124162E71D51A114F73B7355FD1CA9F91CD39BAFC88B814715D9ABE28DF0705691770A12BC5CFCEF3EA2C7F3D66F9CB7873DFB7FF
	77394AD04EDA7946E71EC773754FBC0F676B5FBBBC199F7B5EE8D53E71EF2B5664EBFFDB05313F341FFE57023948845889D05A17E1BB5447627EFC411FB16EF5E27E1CE7C82097123CDFD66F3D2C463656DA637FFE7414157CAEE3C012E549C7782B77ADD6B2DF329420C844B92C44C7C16F50300C248A7EA27D0D3DA88CD2B4664FB50E1C835A84726D12550FF10165C5455FBD4ED59CECB66DB1086766BE6537BBFD11F80E4410B86B6C3582E8D67745DEA3517787EE113A338EE83782D8EFC0DD85E8G683C41
	7C7FEF56AAE8FB79113937F100446DD564D2987936FC42293EF87A66BBC663E186AF42F62DEBE084CE35CEFEC835670AC5EB0F8BF411EF443687E5FD355FF86E3F569FFF6E3F59CFFF2783F3C68EC108CB6BA773A68D8E0684B546D675172A380ED037947212C3B95A6EF6D5C21B887A26G2C834886588ED07C9178972071A3665B360FE37996B730915F621E6B0BF87E819E3BAD6CFC10C8F1A260FEE5E1BA3EF74B5897B537627B6264AD7DBCB8F3EBEE9E345E524F0376DB5ABCC0DD858B53FF151969BFE2E1
	7ADFB63331FE17317E232E787A5FF3696B1F601A2B7F7DAEFD7D173A347AA3AEC9619BD4AFCB917F080B2CE9F84E3F4C7B79A1DA6370FC423FCB76B5DEBF0489AFFE687548434AEF2CA6CAA278F6014807F77C1F9C8BCF62A86DBF38BC99F6C37C40284887AB49A2AFF305F46D25AF4A6F8BE6346B620E7B5C3A3F832539F49373C83E997D7B82071B583A92706BEF3278CC9AB896A3F7FBFC3CA84A3CF83B456FA1F95CBAA98B11F52043CD7807980D5209FD5DEE227708E64C7F83D0CB87880B45018A4291GG
	64B4GGD0CB818294G94G88G88GEFFBB0B60B45018A4291GG64B4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7C91GGGG
**end of data**/
}
}