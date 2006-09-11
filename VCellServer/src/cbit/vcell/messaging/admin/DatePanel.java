package cbit.vcell.messaging.admin;

/**
 * Insert the type's description here.
 * Creation date: (8/29/2003 2:51:26 PM)
 * @author: Fei Gao
 */
public class DatePanel extends javax.swing.JPanel {
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JComboBox ivjDayCombo = null;
	private javax.swing.JComboBox ivjMonthCombo = null;
	private javax.swing.JComboBox ivjYearCombo = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private java.awt.FlowLayout ivjDatePanelFlowLayout = null;
	java.util.Calendar currcal = null;

class IvjEventHandler implements java.awt.event.ItemListener {
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == DatePanel.this.getMonthCombo()) 
				connEtoC2();
			if (e.getSource() == DatePanel.this.getYearCombo()) 
				connEtoC1();
		};
	};
/**
 * DatePanel constructor comment.
 */
public DatePanel() {
	super();
	initialize();
}
/**
 * DatePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public DatePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * DatePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public DatePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * DatePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public DatePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/2003 3:04:18 PM)
 */
public void changeMonth() {
	int month = Integer.parseInt((String)getMonthCombo().getSelectedItem());
	int year = Integer.parseInt((String)getYearCombo().getSelectedItem());
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar(year, month - 1, 1);
	getDayCombo().removeAllItems();

	int maxday = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
	for (int i = 1; i <= maxday; i++){
		getDayCombo().addItem(i + "");	
	}
}
/**
 * connEtoC1:  (YearCombo.item. --> DatePanel.yearCombo_ItemEvent()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.yearCombo_ItemEvent();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (MonthCombo.item. --> DatePanel.monthCombo_ItemEvent()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.monthCombo_ItemEvent();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (DatePanel.initialize() --> DatePanel.datePanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3() {
	try {
		// user code begin {1}
		// user code end
		this.datePanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void datePanel_Initialize() {
	currcal = new java.util.GregorianCalendar();
	for (int i = 0; i <= 10; i ++) {
		getYearCombo().addItem((i + currcal.get(java.util.Calendar.YEAR)) + "");
	}
	for (int i = 1; i <= 12; i ++) {
		getMonthCombo().addItem(i + "");
	}
	reset();	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/2003 3:17:06 PM)
 * @return java.util.Date
 */
public String getDate() {
	int month = Integer.parseInt((String)getMonthCombo().getSelectedItem());
	int day  = Integer.parseInt((String)getDayCombo().getSelectedItem());
	int year = Integer.parseInt((String)getYearCombo().getSelectedItem());

	java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.US);
	return df.format(new java.util.GregorianCalendar(year, month - 1, day).getTime());
}
/**
 * Return the DatePanelFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getDatePanelFlowLayout() {
	java.awt.FlowLayout ivjDatePanelFlowLayout = null;
	try {
		/* Create part */
		ivjDatePanelFlowLayout = new java.awt.FlowLayout();
		ivjDatePanelFlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
		ivjDatePanelFlowLayout.setVgap(0);
		ivjDatePanelFlowLayout.setHgap(0);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjDatePanelFlowLayout;
}
/**
 * Return the JComboBox2 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getDayCombo() {
	if (ivjDayCombo == null) {
		try {
			ivjDayCombo = new javax.swing.JComboBox();
			ivjDayCombo.setName("DayCombo");
			ivjDayCombo.setToolTipText("Day");
			ivjDayCombo.setMaximumSize(new java.awt.Dimension(50, 20));
			ivjDayCombo.setPreferredSize(new java.awt.Dimension(50, 20));
			ivjDayCombo.setEditable(true);
			ivjDayCombo.setEnabled(false);
			ivjDayCombo.setMinimumSize(new java.awt.Dimension(50, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDayCombo;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("/");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("/");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getMonthCombo() {
	if (ivjMonthCombo == null) {
		try {
			ivjMonthCombo = new javax.swing.JComboBox();
			ivjMonthCombo.setName("MonthCombo");
			ivjMonthCombo.setToolTipText("Month");
			ivjMonthCombo.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);
			ivjMonthCombo.setMaximumRowCount(8);
			ivjMonthCombo.setMaximumSize(new java.awt.Dimension(50, 20));
			ivjMonthCombo.setPreferredSize(new java.awt.Dimension(50, 20));
			ivjMonthCombo.setEditable(true);
			ivjMonthCombo.setEnabled(false);
			ivjMonthCombo.setMinimumSize(new java.awt.Dimension(50, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMonthCombo;
}
/**
 * Return the JComboBox3 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getYearCombo() {
	if (ivjYearCombo == null) {
		try {
			ivjYearCombo = new javax.swing.JComboBox();
			ivjYearCombo.setName("YearCombo");
			ivjYearCombo.setToolTipText("Year");
			ivjYearCombo.setMaximumSize(new java.awt.Dimension(50, 20));
			ivjYearCombo.setPreferredSize(new java.awt.Dimension(50, 20));
			ivjYearCombo.setEditable(true);
			ivjYearCombo.setEnabled(false);
			ivjYearCombo.setMinimumSize(new java.awt.Dimension(50, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYearCombo;
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
	getMonthCombo().addItemListener(ivjEventHandler);
	getYearCombo().addItemListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DatePanel");
		setLayout(getDatePanelFlowLayout());
		setSize(159, 21);
		add(getMonthCombo(), getMonthCombo().getName());
		add(getJLabel1(), getJLabel1().getName());
		add(getDayCombo(), getDayCombo().getName());
		add(getJLabel2(), getJLabel2().getName());
		add(getYearCombo(), getYearCombo().getName());
		initConnections();
		connEtoC3();
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
		DatePanel aDatePanel;
		aDatePanel = new DatePanel();
		frame.setContentPane(aDatePanel);
		frame.setSize(aDatePanel.getSize());
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
 * Comment
 */
public void monthCombo_ItemEvent() {
	changeMonth();
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (9/3/2003 8:02:44 AM)
 */
public void reset() {
	for (int i = 1; i <= currcal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH); i ++) {
		getDayCombo().addItem(i + "");
	}
	getYearCombo().setSelectedItem(currcal.get(java.util.Calendar.YEAR) + "");
	getMonthCombo().setSelectedItem((currcal.get(java.util.Calendar.MONTH) + 1) + "");
	getDayCombo().setSelectedItem(currcal.get(java.util.Calendar.DATE) + "");	
}
/**
 * Insert the method's description here.
 * Creation date: (9/3/2003 10:20:35 AM)
 * @param enabled boolean
 */
public void setEnabled(boolean enabled) {
	getYearCombo().setEnabled(enabled);
	getMonthCombo().setEnabled(enabled);
	getDayCombo().setEnabled(enabled);
}
/**
 * Comment
 */
public void yearCombo_ItemEvent() {
	changeMonth();
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G590171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DA8FD0D45739A70AA909C4C90A0A8C53DADFDECADA1A5156CC188C6D1854263C971BE7B32631AD6DF39A53E75FA46F5909AFE1721C1743A202DD7C93A509C94C8BEDC80306D244F46AD01A222C9194F5054D8A0AE56F6EDE60425D3D4B5D3B6C2E02747B4E39676E856E827175B973F96F79FD67FC67BB5F793E6FFC67AE246A5D95E5A536AAC2CA4A087E6F0FA50454569172756C521D9C4B883A6D44
	746F57C02F1273A558E18570B6AFF75B7793D12B00769AE82F4BF25B5F007703E4C85BF994DE124CD3F40410E72527164DBC0FE7084FD3CE5B3F7C34921EFB0172814367CE85675F6DBE2463C5BA4E294486986DD363BE2CE3ED5076GE58165A92C7FBAF86E26B3AFE8BA21776B3DBB17EC7FE75536876518E4F22A893131ED7A331A3CDC32AD04F31DF7D76AE3D2201D8F040BAFA5BB0E22C91A1D43839F7B3C12D6BF6A93E539BEA006434200949C28977C81A958546490B451A984C51990F98465ADF1D75071
	04FC846DE20AB97AF00ED570DE883459406D9E441BD5375D85A42BFCDDFF7E20D9573B7B46C3E44B40F7F66FCB322ED4152DA7CFCD2C2B7A283774CD3A87AB065156DB01D799F65B2B019CC0FE20D42037695EFFF98857F9522B35AA010092BC998A1D127D6AF945AF4A705E54849C9D6B94FD0A6AA7046DEF173B4EC21F19080D93BE73B8E6135C63FC2F73BA9FA0C55FD8EAAB3730C9D16E8DDBCE1859E2CDB8E193B6F7F65239B706A77B626439AF93735C64970D65E596F39F7EC1E379D6DD4E8E535CF59A6E
	F5BE75014370DE227B87439F25F84804B33FBCCBF1EC27GEF0B46FD23FBAF5725E367CA327F58B2CBBBE4BD1AEEEB0BB09DBC11293ACCD03FCB0F30B94B8DDD96D27CDE934EF4F9156258AEGDE75285B4E7C6B7EG46E9AA34D7G6583EDG5A86D49905FED16EE35B06FEBFC79FF38A2AF6AAA249FED1A5EC6C7D432F43D30CE92A509E8CEBC250A73A85D5923C3298A6FA1F2F8F23EE5EC9639E223E6F03B94E08C1D195B4C98902F4A1A0EA229AE6FD568E3F0AFDD411F2DD92C437A684C2044BAD9CBE88CF
	D988EB1F067C9068099E0C3FE19877E4D4F003C490G6F4CAEBF88223DDAE07D5DC003069D4E49A86F1C28C23EE8EA6AD01461C84805ABA179D13473EA1ABB4660BDB34AED7C2402588EE8D745381CF97281139C0764C37585B54EE36CDBFD180BEBE35CE77E231B7B0CE34F57C85DD3CBACFDA6837C37A546FC25BBB6B5F68A29CC540B351DFD5D1FB10E2D28B6E69DF7EC8E696FB2C7E9921C5937E58477AA85744881AAB0EC7CCD391A796CA9A1ACF2AF3D280203097E0445191E8BBCBCD671BCADB3743F1B62
	9BC1668E201AB8375D9B1EEE13EEE5F3D8439B1FE13DEC0E06C9F3144FE15CDC746044EC740DG1ECB622898549CE2C88C7A61A52C739E8E206F69F15B2F98E356D2BC84F15E8A0195B6704782980B02262912B72209DD222641B11B60BFC1678AC73C21C40A60F3BD95C05D15A84439517FC7810C911F928C0AF4330CA1BA3F084628A80B81D03AD50984B5067FAC08FEBEAC46759C717360AE02B1AEC7B05F305AE0150085D0C8158260821832C8CE9C63375028EDE8FE868CEB1C90BCD363BC96236F71D82C39
	FEBFD95B9732D91DA5D97F385836A54EE223B81E08C52653530A356EBF2139C24743CC5717BBF0DED319227BFDDCC599072E3B6DF5C00E6B5C6FFBC2486739C5F778D6C51619998D7BFEB2329F77C39C09081012B1091DB3ECF6FE6440941EDB379F63FFBA027613CD79EF7DF5345FBA8F2F7BFA212D81E5DEE7B64C3D4EEC407A0D8B3C5F219BEEFB430DC4BF678DF33FE3DE5ECF3B4178697A73DE3D9F33518BC7F05FE82DB7A40C8A317AF094FC2F2969ACB5992B6B9AAFD7687D8BEF707A6D112BE8C3B26636
	9F82EA9863FB7C7DBD3CFE4B9C4EA5C53FB8D61E2CAEED9BE3FA48E326B3FA5B49DDEC0F3E9D46B97300D7B04677E74308798CB9998EABBE099EDF3AEDA7D4338FB0ACD57D95BC7BA5B52C35C3104678BE5C29223EF83E6930FE5EBFCF73962EF5834CDBB16E360F0DA3FE33374A24FB4BB8BB278661D9CA6539849CA3E9E2205E30ED2F06398BCEFDED90CEFF2F52578E6C5318B2EA0F38757AEA654196DD667A87D61266093F5A2C6A5D34FD0336F5634CCE4F0DA76C55F60F3734970869FE1F40F5BFF193E26F
	A6577DC51F156E39B7F93D7F52D52CB3622220DA28295B650A174BAE00F15BEF72F5BFC2F1A9A8E912A0CB57455AA3CC6666B7F06DFE9EFBFD6D269EEEBDCF7F7172541A34EEA2F1A636CCCCBF9FD92D7607DE1C53B541EAB53B0137D2DCB361CC776FD2DF45F6A6700AA7F8FD5199C15F6FCDF15BE3C099C039C005C0DBD3384F9D889FA73354EF6DC1A82B02025CB5A9C173BAAA0CF6021C2A3757535CC4F3AA6167558B1E29F658F95B6435D7CFE933F1A54254F175D35A0EA963585E8C48EFDB47BC5D7ED3CA
	CC3F4FBD58DBC1F5869B5D4663FB7C93F3FC976C9B3759134477DAFD6E4D37A57CF5D340D54EEC3FE39473D375BC373D99488594824A84DA8534F19E373FDB7B73EC76272E6AD2DA81EB033C5ABA755CB36C15B74DDEF3337343335AF97B3C4963187DFE67C77BE1BCD74E67750F47FBEBBA84270D1B2AC3E87E64F12C2E3EC0F3F79A70F200983629873135505EE46875A71A5FF6C03BAA55ED2F85EA86F226F2FEBB656339B488D89AD0EEAABBFF987FFBD7EF25DE453978BB4E5D2C371DA63C5650616737B407
	393643D85C2D3F5B275818795B55C8723A4E9B312E6B86A356F5DDB072AA43C10679F647792311196B3E1B912B3A6F9D3A0EFFFAFD9F159D8E0BE19AGD06688BA6F5F345708C57DF781AE003A8D8E122342EE3D4FE9CDE9EDCC78FAAF5A38B0F5FA6EAD82EC9B504E8553F907AC30BA40DA00DCC003BA1F457E7C987A5298E0D9C02B008A0136AE60B1DFB5BAEB4EBD9D1BA1676E0AA65F43BDD16BBDBC9845BAA52CC4D41FB8F51F5E0A9EB2F8096C0227A41A1475F90F0E5784F5C01C364FC769F846CBB67EF8
	B419AF307DFAE652FE9DB3762B510F7BD5F13B75DD72F0923C4182F7G568DA4832568FCB677B22FF96E6386AEDE467B01AC44150886AF1F435CB2F6195F83B2C1C69E50C6202D37331A031DBF2B2398FB7D2A9000A5F605C49FA922E37F33070FED03F6AF5098D08ED04167F83D615905755CF92326623AFCB00676B85BA1F8C57998571B56038EA1CEFBB26C79E309FE0DFA3F3F3E076B6B61758F7AA62D1B4EDCB257CDB5E9EC8DEDE93C2EBA56E9D5D7759A7C7DBE2B7121B41E070B68791A8A6D9C83EB3D06
	D8AE348B8D6CD91A2F0B20DDECE08DD7902B3C836E56F7F0AC13FEC3B981ED170135B9F92E9FB430C10A25C3FB05012D26589AE8979A58FFD32C885A5B6F603139B016BC366E09D947D6D64CBABFFEA1B6F3FE3CAF16ACA618FEAEBF37DF451DDC67877D5CAED586367C9A37CB2D0155FA38DD1A8D2C4D436D62B130F48F370BE6E05F77F03B248146EA4CFBAF73EF69B900ED3C137B7A71C64CF9C62DD5AFC4352626CFE4A55A414306558A4F51F39AFD33F2A13FE33D5F03B29D506E855296729AAC76DBDE0365
	5D3712ECF8567A0E3559B5EECBDF4862F85542C48D7651D1EFA91B73B56AC3B86EB9605724D37F3BE276D90C0F8F892BB598BFB2090F7BF3E4927F5AD5B39F757DCB09193FF8923F4CB41E2D6D7177795AF6BC3E0A488F26A67DDDC3CEE7EB4A501F55CFACAFC94BF26E526F357DC96E35B472597AEFDEB26E35A003E1031D3CE6A3F7F16C0C0F77735F4573508B3D1843B4C511DDD24885BECFF26E425F06DE19A0046724AB57F02DBEA590D202606297D5897E2797566AC5A8D723FE399946BDE7485D5103F387
	0418940884BA15A87D44C1D8FF36DFE8F37FA25E7F4C95FB22FF97DC2F743EFD342F866FA9C65F2F3A5116A1D56C97D5D574535EC46F3F0E660EBCF84F875A42E4B84D5FF4269213B9C1F3046817B47C1841E4ADF5725F016A9673398B1D685BE290FB7975B97F55497D2F5968F76092DD8FDCA8A72F67A85DABA7746B85E2367E8D8E5579BF25F11F863CCCA006BDCA6D3B965A6B9773B57D45B3531A5838918F573F58107520076BDA11413117A9F6985A35993CC67D4A34BB6DEC4446655F6238DFCE9A674A18
	FC2E8FC2FBEC8A466E4A4BE9EE4948E0F7659283DFC9719C934E725B07BDBC3F65822F72EEFE07AE3321BDC350CE873A9728G28882878EEFEE6B4479B48AC751CCB89F54021A21B0E81363E372E5C5AEFE60F1473BC3266612F107C5B9DC96F291577B07B54694F7C754BCB9235EAD6A97DC6G3C98D08E509A20C2204D77707525466B587ACEC3981C54B441B7C83FC0714539A5B11A003160E1EB1BFF49EAED5BF5BDAABEBF3DC6E363821DD8739D3660535AAE896E30403B811301D200B2F5BE1B2343E725DB
	2E5E2740C296337B3FDCB57FAE3DBFDB36F1BF1A5AFF03AE23687309330945755E9614447241261E7761490F4E7AD60BE4DA7F6905F397AEDE3EE06830A2EA5661442F66D9FEE7C65A1F49662E511F692FACAFE17356DD417EA807693460E4C227BB5C7F8B4F20982DB674F2C8303578C3D86D9F9A6A2A9B9B0E70F1AF1FCA0CFB7A42DC4631B594DEB31F03559FDE34BC870B2FDD37756A3A27E4A66C186779E8B71B73EBCE7C262F29911135DFF422AD7A85B9AC9AEB73BA92BA9E3DB47735E51FCE0CDBF4ED6E
	633E79C9E25CBB1E390CE3FD53BC1357325EB3FDADAF1EC9487E20EFAE32D9CDD751CD6B3CCC167798B61FE26B81AB4A64355F63945B86ED3204677CAF7690A3EEF2CD6F47FA7E9E3F614D1C7F4F7E3F4E41725BDEDB721A7FB51BF54D7F364D3A667F35ED66EF9EC7EC49FF6BEA3019FF6BE2797931B62C312AE0AF9A00DCC003C0E3CBF8EE7E166DB70466E60B3CFC5ABF0077F97CE5DD6373FF29B479DAF3CBE77FBDFEF5A95ECF3D0A26A981144F7107CAF13FB4A5C4C1069514220DD4E9E0D0E3DDF57C51D2
	74B1D96C27B0733BECDA7FE4413AF217F0DFFC65923F432CB130451D7CCE34494036F572BB5196837BC90C5F090A8D6C81094B2BDC4A31A7A4FEA7EAB03083C37CCE64B0304BC37CCE54EDE0BE9F57D9B630F5BEBEC7ECA92FA11EE9A7465A150FB15F3B05A13DCA40A3D5D1790E1D05B83E689D92FD1ACB98B0FC60FBE7D1E68E4E31146FFBD74B074964G68D4151028EAF10652DF68F4D97FF58E4F7BCDA0E7BB5021E530EEA087D06FB2AE7B63565A99E4330B9D75ABD510029A4BC56FF663DE8B32B490C49F
	B4F2D4F6B766345181A144EA4AD55D3C4E8CAD63B5714FE81FC123CF3151A7455069093664FE7BD45B6CFE7B53B6D6478F082EF8C87FA675CC9BFE5FD17A7B4322919FDB5B769BD8FB582904A5BC02191E2BAF517C8C9331DC3C68BC6EAB2B3BDF6F1C296EE67DD763912E5760EFF621BE99303E1CE53C5E5E95C72C905A9B1771FA7EBB526C757CCB922F67C996177527A1EE437D8616C2319AE8B7E471BB46460B09B57C58B753DC4CBECF1E5DEB4EEF4C35F45E8F4FE22EB2FEAB2345CB97F48C8EB07E7F1CCB
	3E07E59D336F615E8E6B5CD355B1BD773C59E11DFB5E6DB0679E362F5F6DC6BBB598BE3B2F0737190DDEF2920377E4EC6EF540DB1D09F17FA04D3D8E58BBB4398E689C1ADE875C5615107D02F7AE32595F0396CE6C014565BDCF4AB4D2C6BFB3EAE4A7FE9A8C2B641D994B50477E7D4B273F1463DBEFA3A56D442EA498B17C2B2AF298FFE07A74A7977D70693CBEC7150D585A492EC19177231D14C5A53FB6C8472147B406C748CD32737F5A52C8496FB5D26A906278B677F1045724C7A64CF5F57182DF523A3AA6
	1B5522A73293B5A94B2F8E9B7DAE81F8C6B64FDD19A53FA349F3FED7C010E5CD1007CF867D187FCF45B5B1D1AF345BD0FE187751190C6724B54810E94C5FGD0CB878851936217FD90GGD0ACGGD0CB818294G94G88G88G590171B451936217FD90GGD0ACGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3790GGGG
**end of data**/
}
}
