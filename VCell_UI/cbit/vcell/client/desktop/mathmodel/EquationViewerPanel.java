package cbit.vcell.client.desktop.mathmodel;
/**
 * Insert the type's description here.
 * Creation date: (5/20/2004 3:41:12 PM)
 * @author: Anuradha Lakshminarayana
 */
public class EquationViewerPanel extends javax.swing.JPanel {
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.math.gui.MathDescPanel ivjmathDescPanel = null;
	private cbit.vcell.mathmodel.MathModel fieldMathModel = new cbit.vcell.mathmodel.MathModel(null);

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == EquationViewerPanel.this && (evt.getPropertyName().equals("mathModel"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == EquationViewerPanel.this.getmathModel1() && (evt.getPropertyName().equals("mathDescription"))) 
				connEtoM2(evt);
		};
	};
	private cbit.vcell.mathmodel.MathModel ivjmathModel1 = null;

/**
 * EquationViewerPanel constructor comment.
 */
public EquationViewerPanel() {
	super();
	initialize();
}

/**
 * EquationViewerPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public EquationViewerPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * EquationViewerPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public EquationViewerPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * EquationViewerPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public EquationViewerPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoM1:  (mathModel1.this --> mathDescPanel.mathDescription)
 * @param value cbit.vcell.mathmodel.MathModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.mathmodel.MathModel value) {
	try {
		// user code begin {1}
		// user code end
		if ((getmathModel1() != null)) {
			getmathDescPanel().setMathDescription(getmathModel1().getMathDescription());
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
 * connEtoM2:  (mathModel1.mathDescription --> mathDescPanel.mathDescription)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getmathDescPanel().setMathDescription(getmathModel1().getMathDescription());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (EquationViewerPanel.mathDescription <--> mathDescription1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getmathModel1() != null)) {
				this.setMathModel(getmathModel1());
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
 * connPtoP1SetTarget:  (EquationViewerPanel.mathDescription <--> mathDescription1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setmathModel1(this.getMathModel());
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
 * Return the mathDescPanel property value.
 * @return cbit.vcell.math.gui.MathDescPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.math.gui.MathDescPanel getmathDescPanel() {
	if (ivjmathDescPanel == null) {
		try {
			ivjmathDescPanel = new cbit.vcell.math.gui.MathDescPanel();
			ivjmathDescPanel.setName("mathDescPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmathDescPanel;
}


/**
 * Gets the mathModel property (cbit.vcell.mathmodel.MathModel) value.
 * @return The mathModel property value.
 * @see #setMathModel
 */
public cbit.vcell.mathmodel.MathModel getMathModel() {
	return fieldMathModel;
}


/**
 * Return the mathModel1 property value.
 * @return cbit.vcell.mathmodel.MathModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mathmodel.MathModel getmathModel1() {
	// user code begin {1}
	// user code end
	return ivjmathModel1;
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
		setName("EquationViewerPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(303, 285);
		add(getmathDescPanel(), "Center");
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
		EquationViewerPanel aEquationViewerPanel;
		aEquationViewerPanel = new EquationViewerPanel();
		frame.setContentPane(aEquationViewerPanel);
		frame.setSize(aEquationViewerPanel.getSize());
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
 * Sets the mathModel property (cbit.vcell.mathmodel.MathModel) value.
 * @param mathModel The new value for the property.
 * @see #getMathModel
 */
public void setMathModel(cbit.vcell.mathmodel.MathModel mathModel) {
	cbit.vcell.mathmodel.MathModel oldValue = fieldMathModel;
	fieldMathModel = mathModel;
	firePropertyChange("mathModel", oldValue, mathModel);
}


/**
 * Set the mathModel1 to a new value.
 * @param newValue cbit.vcell.mathmodel.MathModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmathModel1(cbit.vcell.mathmodel.MathModel newValue) {
	if (ivjmathModel1 != newValue) {
		try {
			cbit.vcell.mathmodel.MathModel oldValue = getmathModel1();
			/* Stop listening for events from the current object */
			if (ivjmathModel1 != null) {
				ivjmathModel1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjmathModel1 = newValue;

			/* Listen for events from the new object */
			if (ivjmathModel1 != null) {
				ivjmathModel1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connEtoM1(ivjmathModel1);
			firePropertyChange("mathModel", oldValue, newValue);
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
	D0CB838494G88G88G520171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359A8BEC945751A1B69830417C9AA72115CBC86234CECADBD25C96D5B4F2DB5A204A15C8C4AB2B2192E921EAAA54D2B52A2C6A794E0EED8E0281173A89A5CEA189A06498F09BB8EC634FC1781844C95C0B9BD145FBF76B3B45F737476E9EBEFF694CFB7B7656762EAFA60D25515B1D77664D674D4C1B59B329FDA52F224CD1CBC8D985517F5EAAA3247BB5C26421FD8FF0DCCE546BA2263FD78176129DC3
	65B06641DCA5FDFF6A4CAE98DF84A8849C0E653257D5C31EBBD32763CBF4BC07B287A15B691A2A3387F55CA5F86F83A8GD8A733751BE02C261CB35CE774F5579765139DBFD29C3BF19F531E9C4E44986DF5FD2CA7FFAAAB3F093CCE1E5D2553AC05398DG283C132CB85282E3F377C030556F133406DBFEB19CEE7007A5B12AB584C4F5C013E38D91C18BC5640098F63BAF5C0C8B1AA4C73D12B8A8AA5DC2D48C83FD9A65B1F4360A6E897A407BAE85F19DED4837885ECBGF698782F34A33E9B1EC3GE98A5735
	650DE6DD175E11B5E4CB70FB55FBECF44DD5180E6B14242E5D47FD4E3F51F3E99AC17B1742DC3D6AF5B5817482A4GF281DE06397D645C70F1985BFDDA279C09485176D82CA39CD02E2122706CF6430C0EFBDF744BCA0090F666EF1DBDE1A14FF44068AE4C180E59A47FB4BF7F02779FA3A5DF7F1C235242A6A579A30ED5AA3345FAB5E99346FB5C1677D6F522FFCE647DA1B173A65BCED5D6D870BE7874294AD2FD1F9AAD495B2361D9FF037AC0A3BCA39D52B37CF7A9BE5304E73EFA096271FDB94C11B8770D5E
	9739ACDD65ABC84D49FBAC6D107BC416A390E7B20C46A74BF23883774E0FB31E5506AC0D94DFE442B3D99E27FE0A6F1BE14EF34B6BE27E55B206311B8F6FC5GA5GBBGEA873D2EC6G4FA077317461B7BF210FF58B0A5691174281D1A10C36E564G0CE2C2D3044BD1D59322FE51ABA812608B0BAA5157DC9CC19B78A650DD253E6F879EEF0BD1D121718A3B8B91D19395152D6999590BEB94114E76C8919198C4E2046FFBFDE4BF0CE1C1557E9D8B881A18DC416683A3E813DB0297F6048D6019596546B85AAB
	G7A0F82E499F6F8066EF7D5D42218C53AE4F9A09E6BC1CDC849A05AF9994D9DF970DCB848ED5CFF9BF175892F6BCC026FF3E0F41FE99F0F64C77185E50847585AB6246902756184771B5F76F23F716CFE141CF8EA19255F64008F27A7183FACCFCC0E1F9F50FDD13622844F7D6F34754EB01E43896BD86B3240B39E419973283041BBADF04C4E9B891E599610018CC13E9C6236FEEF341E79EE07200A5CDB5FD5405144C05272CC4EE39DB53ACD1D3A8C15068E7F22F3E7E05FBE00CC00D5C35C065FEE6F35201B
	093E29736EC64A433CFF757F118733555BE3F8CF0937605A7408B1B19A0087D51FFBE78C7969F15CAF9BB4BD949F03386F04C0D38D7CC7E3981B0226A912AF2E099F081AA6C50349F99F652546FD31E44A603C24B154D19E0438B756C747B0E67CF2B4AA52C3B3C87479980DD9B1ACC6C068CEB99E55987E56B856BA8362101EB30647DFA0987361B8669FD6BF2C9230C80AA9D284DC81D398B9BA0C71DCF0364EE423CDF05ED8871542D8B34C63F270B00F4BA3430F10F59F479CD6F7CB6E038B9D31E1969F6943
	4938E4B28D5C36167DA64D9DBADEE5323E5704B9482A02F1D21A74211DB0E231C37373C99E9F3B9D48E3A570AE84A89E6671715CED1C6739C88F0CCEB99CE6E606391A9134052B3DD657E5AB501291E60B246F7C0CE0FC28624D3888A99E1362D543663FA07BA64DF9F57B3379ED846D9FB66553DC4A73C6872FADFBF5FE3191163BB2E1BC847866D1DC57544957EDB14D1782540FB2990B693AB28FDF17B94A62B8EF142DDF8DE303315FDAE35D4131643CF9BFF6963FBF067EC16B4E9B42ADA151208E020F3B5D
	D7680130FA72003BCADF5FB4E65413ADF896E1F8CF87D8BE46FD69A73BF95D38F4A01F14ECBBD9E9D7A32F9E536B6B3124AF0536373F404E7AC3A7722C9B773A0E0E73F38E14196F2EF6D5157D92F5A17D8C76BB4C3E46F0F58E7425FEC9D1354B108C927C3C8EB9D0DE3CD7F534DEE71C22F5867AE1B37055G8A4691AF37E38C6259B1BD7D27391EB9438F123EE577B9DCB6FAEE9F576B315BC9BDCF0CBFDF4178AD69607CAE41FC9AG0B010C76EAF38C68EBC06ECE14C38BC9AA1363F76DEDBA1F8D0F2DA005
	15D98E2BBABD74F02E23D0673F59A4C7600BBEE7820049725FCEAECBE39A7483GAC077C31451CC37252D8ED3091462DE9DC5E2D1DFBF4F9BD226AD72498BD97A66323074CB2D6899916B2E6ED8BBA48DDCC3623F7A5E55C3C58675C31184BF85C43E50C411A7C3BF87FD45B0AA34D09062D9E70584BB47A9FAED35A065524EEFF3625CC54DE3AACD53312B29564791C6B7238CC4FFAF81D5C8BEBF2E671BB75E57764DAFF7D2CE4FDD1B2EBEA2D215769B436364FE2B53053402F23F8F2F7922F5769AD5CA6B5B0
	275D4D6B36F49F1E5BA6F85F8130AB5D6BBA82608188247318FA48F11A24280BAFC721DC0D8A618FA6DCF43CBEF5A2DF381B7477BE27799C189D4E34239D52522DEBAE7BFA61A2254B1D819DBB5BE39F197B74723DD79CCE1B33DD2F6F3DA9BDF9364CF67F8C60DDD11781BDABC097C0GA0BD035B6DD96709D4F663BD02EEB026533D9D29FA4F15999375E5F48F2724DBBF090E69B1DF44FBE0B34C1582544E063E882087A08630F4B65767F4F9C7AAFD5AF9A154A39B0F1D130B99AE2F3F734E743C19122EE076
	C4BA967FAFB53CC126FEBF4158EF8846A5377B1A1E85D81D416E35B7DB79F71222597CDEDBF89EE329945EEB67F8DDA76670D8A2C13423875E7BG92GCB818A81B682BCBF075BB1237CDCAABBDE406294ECC84DB655004C8E0D1EBB33DFEBCA3A3ACCABBFC98B21FE0DB0578590GC883D889308EE0CBA657EF0E1052CF0C9677137849FB97A74BDBBAC9BEEBBDBF1EC2F7E8321EF3A752317E76942DE59AE14E8340F0FB1A919702773439BCF72FED41FBF625717EFD7A0E75135EF3364C349F1CF2FEF36D731A
	5EF3FEEA9ED63DA687F61E59BE7BFEE9294F3ADF3A4FE75DAF657976B2BC6CE17E0A426773FD5377D32BFCD67D545F299ECF535ADF013EC20A0814AD0BFBB171AA73477763D1661248D91D1C038DBF7F0E046738EEAE77670B7ED3967EFCA1A0E93242B7699185655D7EFE4319BF0809FEAEF3CB405EFE1E00357D2E842C6D579D185EFE3D81BB7BB19B7DCA7B8B3168AF5F01E8A43CA6AA6E285657F4EBF277C9E36FE5223D9E4BC5EBBDD608D85B2BF2DC710B13E5FDC42CB3661241FFD993A3A88EDB73B5CA2F
	89CAD01C226BB7A9BD1B3323FFC2441A5BA785A4C56F74180E77F5F29DAFE8723513C6BCB535CE179BCBAC621155DDCE7A9DF47BDCD6F7159978DD94DFEA4233FAEC59F97E0D3FEA1E57951E47FDEDE7505E46FB0356B67EEB505E46070229ED7CC6505E468D4154B6FEABE867F34C56C10F0B7BD33FA2C74023985D3C103D2E0BC2563A5E933257758B21543ABE9C32577531D0EADD5704A67B930B2B9F9E58C363D2D5C515DE5E9EC193F47DB3AF1AFD6D14E1174BEE7CC6939606643886F549FC740BBEB76F23
	0A41A7368254G7E48FC56CF31FC7655C114219FEED0B0B3CDB4F5D9C87BFAB327CD077539C88330896079793C87735EC47DA393AA9D2E7F9352EE73B96AB9015DFFD7DB797D5717456F376AB6E2444DBE53737D67BF6D1DB49DB09E2BBFD39EACC6B35DBCC633C06722ACEE7FDFB6203D0C6FA18D422066F6F74878CD290B9DA62B9D0BE99D017425D93CF67CB35D33A99BEAA800C0B67FD610F81DFFABA8F8E885A9FE567A1B48661EB147E8B64BC1F959269E7D384F49F8F6B5F31EA5B0DF3BGF1434D3526BD
	508F4EB988E900114D97CC1847D8BEC64C73BF1FB00F72FEBC013E2645BCDFE122E73ABD799A572D7449D5A47CF83A6DEF225797B01D5274317E4777169565F63F3019727135566B6B8A97CC4D439BF49C730F85D37A6ACF66D74B6F106E299953317B21789C7A435105136F0D1FD2FC53424977C679B96E13EDB037F2A16FBD8687B0762BF2A0D7GB48374GE8GB9B97CEE39F2231124284BFB64D897A46430293450EB7066BB7BBD743B157C6CD7FF6BCB24F03647769B584A9CF6FEC57AD838615EB29C19FE
	BBA3285F8E182BD9847DB6C097C09FC0E29157EF718D3D5F46528CAAB1419F22DF790CC2CF928713E8CC1CACE6663572EF17D90BF81ECC1C475C5CA962F78D2E4B2048F5B97865874841D75A9CFBEDF4A9DA44F4583EA89923C9DFF99A5BBF7D799953734EF31FFD5EBC7C1972E06710B6E0FF2F4F99303E5733872C6B5DA583537783778F587F3E32E2407C7B8A7323E5D14CD5C796FBDDEDG810084C056E26EC30D61FFB09F32701F8B01A05695786B2E46796F0F586B7AD2A475EF4207A3786D59A7EB1A9C41
	7DB97ED8844FCB13E3942957E2913411A285C39ADB2A631BA2683FE1311F22757FDFB8EF0E57FC50EF6D62693F11B17B7CDA2E073198567E8620E60957F59420992087A0841036045B2AA8F294C62FF0E3EAC784C8F6A1D2FBA90244FF6B79FC545EDEAB23296D75E814FE3F87BDFB06E23CC708E22C4A7D7D2AE81C4B1AE80D013B2CF64B2A04459A2BE52E1F47BD16039E5868B11CC0F12B81374649777803ECAFEB291CDA560A1875593AE2D34F36A6E6FD3687E26633E5381775FD9523F1E378D7E3F82F32FA
	721A909502546FD94DA23C0E77069485B388618F250096E27803095DC97CA59179437B7FGD0CB878883557A72C38DGGF8A5GGD0CB818294G94G88G88G520171B483557A72C38DGGF8A5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGFD8DGGGG
**end of data**/
}
}