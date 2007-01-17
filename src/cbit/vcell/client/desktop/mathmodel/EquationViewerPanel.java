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
	D0CB838494G88G88GCBFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359A8BD0DC5515314446B13658510AEDBA0D918D5528F19AC76AE4BAE9271D21CA5B14A6CE5A12E9C6B3D5BBD8B38DF62C62746E07AC2B449003965B98310D1198C27865376443B76133AC8B2CC85B3472D89E704C6E3E4DFBEFE1A1896DB96F3E7B76313CE5CD2C4C1C396F1EF36FBD1FFB4E39675E05143D332C48E2ADA344D2C4343FE396C23C6F91F2C7412107986E787B39CEE278FB97E09739C932
	C35B81B4075AFF2333845A57G0681076DCAE8CB49214EF28D1F2A6199D82C0424D762180E4EBD9AEEAB0CA9861C8FE022120ECF01168A3048F5DC9BF77E66CCD27028E45D016B9856E430D91BEBBFC05B8A7222E5658164F5D11593671C043E82004A5B4872FD2790673DB0FE5ABFA2A85513FEBE9028768784BE24D40F727285C58CD787B9E5A2A80E7281172B7DE204D384B1B4A870D33C6465C2FCG66D7FE00BC3E53D92C2E897A407A2BD55CF5DE649B006FD400259FB07CF3FD08FF813F8F66BAAB8FB2DD
	CF3DFFD253E5685243E4FDDD503EB3012ECD87290E9387E33AD684B7D95FD2776547B25ABF83E859GF9G24AA57D981508470D7206DA66B258F20ED9ED1DA45E0D08CB50743AD01D129879505EF178BA89A2E1F770B52A8A1F44F27BA2BCC64D988683CC5D7BD0F5AA4331E6D7F4A7E7BC95E373FEAF51858A4AF7312F5280A5AA2DC953389657D5D043C532A667A67DC5E9F91A36F3467B6580ACCF82F4C5BE04B505649B2704E2F423D7E036A839B619B6761FC0A3728BED0F3A806273E3AD89D0F7DB620E59E
	E23EB1749A1345EDDFCECAEBEFB335C37A7717D8CB2A298C7B2A63E57177635A5EEA4A53294B92D67181831E4A723CAAA3769783AD9F007A5763910CDD6F61DCE7GA095E0A9409A000DG7907190F3DAD9F7E14BE6665A425A5A284C6F90950390F4BEFC24BC795096B8C498A977273031CA4F0A381DEA65A18DF4BE80311B97336283E6F879E677890AF29718A2BF3C1DE61A5190EF9D65E05E3A4DE257A04A08F8C02E14256FDDE5E0D7660E4654370A827703191147E320CB6196486E1C5D8G3E29DDECD3E8
	2F121ADC67BE00069AE60728026BF573D2883308DB94AFC442BE54042456201DB7283923833E07EB180D7FB40538EC68EF5657F9C0F95D30CE1B60C771B9E91A45D821976794407832DA66B73F9BE2FE533663BED23561D6D33F29583441D6D5CB7D252DB6BEFE9CFD38AE4A9628E5397FCB5E212B0C6732DA73D8C3F94DF3460A2B66D114GEFB341D1BB8727F14F1600EC19G59354C563FD5AA286F36F0B24F3C35D782C763C7E31627F28A7D251ACDED1A8C8EDD07C915369956AD2E4BF556G8C55B19B3E54
	B7E4B26FEA74CD1EF74B7A076256F77E9FF9D0DBED0E60B945CF42315946077950A8FC489A6D2988725362F8CC5467BC2B624390772D90E8320E5F9641586494C592C6A28ABF40AB0A909A0F51DFD2F9491111F0ACE5B0DE56C954D11C02385747BBA6B1E67CE2A8442B1B26CF5168CEB5E679G9F8421DB45C8C821781DD3D86BDC6027351C31FBEABB41188FC4B07F507AE19577AA6AA089C1F085CCE1E4DD9D46F33EDE7FA024G8E6B208CE8336AD8DCCE7D1D45653E19EF11359F072DE6E7CB7ADDDF361657
	5378282A0F45A515499EB5177D15E8F18CAFD3D97B9AB08719D5B0B6F5CE6A74AB50E231236667DA969FF74FA08FB7709E860856337838BE0AF4160B3440E8958381EAE620E55523AD3A7B4AB4DD52201FD9CFED91739DF99A63C366AFC6F8C87118943BF51B4FCC3F9EC79B546CCF6957CD237D8306FC5A5400BC6F69E735A5E9207C4A9BE86E2A01F6AF203728631481B6EE09011E81105DC0E50C9E41F14DBEB62E66880D630EA3F47CF9E82B75758A75F1BFB0500D6B513D58F6G7DC32DBBBF61A639E835BC
	85BE6EF2F5299BC26B49412EE2EDFC5E913D1E6C413DA8BB1A6B2C82E8BB4AFC69E7BBD85DF84B05CC92583A5E1628C6BEFFD42B2F0F46FCA9BBF34AC677FA6BA5643996E86B0E32FDFEF95AF8F6B54B326897D4975276E0650C5157A86E41997425B1C11215CEC886D136DF8F4F203CF82EEAE82D4E1054BA837DF08B705DF38C6E8247907FC09F46A06E9D5553DF4F74CC1B390B8C5F7AB56B2B8974CCBF2655E347E2FA266FBFE2237CFE5E4F78ED85FA6571DCA70D019F76B90DB1200D59CD7EB300F2A89302
	CC6578FDF31346A7677E65A44B3144EAD627CF2CC8378E9F277C979F0F49C11E5CE4AD832032FCE30049329146141EG5B2BB964184718C3BACE505A60B234E9A7183C153E1D1A3CED3C6C1704303AAFD4467B769AE5AC6696194A386419F1EB6689AA5B3A93B199AF2F5FE4CDDB4FE47C4447E4ACF765BA3DAEF6FFAABB0D2D1A93F5DBBD3900CC177F41E4CA49D9CD4AF75FE4AA13EAAF9715E515ABA6D347931B2C671FE0B23D77A12B13C9E32E73F8A3BBD37F5595DF6B079BE375C5EA637CDAC32B535558CA
	EF24B530CD47938F62B38DF82DCE6FE1B649825A1E132CEE8B4E623E25C0FFA940EA009C00FC0012D3AC264EDC2AA7C96A624E90142BA1AEB0B06720E37569A5648BE713567F63A563BED0BBC47B508E15274CEB2E447542756A3C262B18C7777640BF0D77F47B2EAE2BAD415E063535D3CE4576165AEEAF4133E2AD6073G8AGCACEC32EBB4D6CB6FB39AA195D589DC1B3985569977D496E1E6E53F37525731ECABAAF9CB70F6A21D8709CD88C348C00B5G9B818A814A4F64BACF1EE17A64DCEEC926CFB3AB04
	FC227E599ADF4CB0F96F9C38B6BD9FCCBA4FF7E66EBC9A7FFF29FE1F4CFFBF41582F9E0F88AED70F1685E81DC14FB551435EC982E758397653DE0C258C682F81D8FF064552B3D634E3BE740B1BF21D95GA7810681AE83ACEBE2F65CFB25B1199D5B31B8859B2AE61BEFC0EA07306F5A6CB71BF45E5AA6B3BF916C285FC62095G14B443D984608618GD8524C748BDFC96AA77A95775378494D037172E6B44F154FDC4FEF4E1B37B7DE4F38F5687DD6D2EB190DC04B872038DEB762CADBC06796163B8BFB701CF5
	6B7D6D9E6CE37D245DB9FB2E76BEB8EF7FDA9267B56D4E7919F9185DB599507D1C1CCDFCDF3AB2EBFEDF7A422C79FD690659DD948FEB98DFD1987D6659056FD3374F1A5D275ED675F85C0AF1A9413DC28872AADB9A77FC74DD6A0F7D1190F5C964AC4767E05D4FD79461BECE34B0FFBE7C5FBA93FFEE9F9594D1E20B78F8CE6A9D9B531DF9A04C7B194C7BC9E27B9DA266762BA76676BBC19636DFB3C9E4BFEA232714B708497D528351C8D8CDD4BA605446F89551DB2B2FBDEBC92C47A20B399EB7DA3036174508
	646763E57D0A25DC2745023FD36103A88E9D73F5F53E42C9637CBCDD172B73A9AD517CFBADD8F30F882302245D74280E3F54F5ECD7441EDABD1E944FC239B155A49EE95D552A3E032637503A4B22635DAABE430027755806DE76463F8AE8E52D4C5736D9935B78C52B390D09AD310D9D3664B6AE35A536711FED49EDDCE1CB64F35456ABFC2FB2FF9A1344A0F814F61736A756F546EE2EEB0ABD312EDF34A757B54D1ED8575B6C49F5DDE60F77A79AD7BFFAF3279A17324C4B6A615D46A91C26FF762051576AF43B
	5C5005EFB481EEDA0CA8F017ECC53FD856456ED1D1682781E4816434527B944DE78FCE218CE3F002021955C433368D670E39595CAA68B7818C8324GACEBE3F730410B28FFF0CE2543743F37E807F19F351CC04F3F9BBD6C7CABEEE767DB271768F153EF787ED56FE7BD1396824A2360F365C1E3B43B0B45E883689CE8E7767FCDB55ACBFF8F29662694172BC544B7A5B75DCCDABB16F697EB73B35AD96DF83A9B574C03FE81C049D976D6905D4F5E8AD65E3D1C8C671B3F09EC76DD316EBBCBF3D0475958FD24A1
	3849CAF9AEF1B31E29C0DFF396F10F384B8CEB209FB4DA8929061652736750B1168F90A3FDEF0F110E72FEBCE73EF78E3D48B01F6A7658FBCC374247D61140C32989FF93351F23BAD5EAED5A165BAD29AF4C58961FC3BE4BBD955A3861F3737330226128FF7CE45E3D7A5379D55EB54EFB237BEA67517321540D7E306EDC7C3971160A4FBB97FFEE34F5B31FFC9AE86E8EF67798F6E06C2F02FE8E409600C200BD1D394E631D6CEC295FDEC3125465BEB16C06049CB01496DA8D6E3E365FC33F67E0FB3F7A11FBC8
	5675ED895F405C1DF47F82DADB79646D96EC29FE5B0AD13F25C04B824883A8GA86E02FBD697536F52F66D3E0D2599D4E21CFFC2FD65538BBD011F0A21B1F15218F954435EAE9B3AD81E4C6A455C5C4A633B8653E50AE73A6C3977CE32671DA66B2E843A843A288E69DD31980D794A1BEE22FF3FE3786E6A7E7C73267FF365C177C1F0A4BE57438E73F3FD52E1DE6FDEF6ACFC9FC8F5A47EFD65C6077177956AC74F2A39AE877665E90012EE709F0006EE66C3EF957F0D7A10097F340F0EE3DD013F6EAA0CFFE1F1
	E2DDED4E643F8917B8716DF9C4D494B1086BB37C6B6A992B08E1952955E26AD9A18963938A9D2A615FF6227F8678B1952D7D7FC22FB1DE3D205FE87742EFE454BEADEA0E0D4258DBG32G5681EC81A884A8F5437D574DECF51F33925AC16E1379B7A2C05283D1351744896C371E675D096DD568CEEE2F229E757D9E7474CD07351A6C159E0CD5F1ECCC6675FD5959D32A63BAE52FA88BD82C51DAE6E9AF2E5186FA88F6D65F5C21624E836EE19BDB234713D8D61FA7392C7F76186F2D5FB3FFEFA7BC66FB9B7298
	77D62B1B35F5A57D62C671539EBCD7E9BD59430538F1556FE94D426D47F3C38801993840C74228B2C171FB22BBE2788E9E79C37FFFD0CB878815900ED2D38DGGF8A5GGD0CB818294G94G88G88GCBFBB0B615900ED2D38DGGF8A5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0D8DGGGG
**end of data**/
}
}