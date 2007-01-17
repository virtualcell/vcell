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
	D0CB838494G88G88GD4FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DA81F09457F9269DB943B08CA58DCE084B380ACDD255459EB2D5269E17CEF0C2E3D22B2D2CC0A2F770180C19C2B3F206B4F64338EA7DF412C0E730110D8CCACD92A5C1A060AC0BC3D8C296A01DCEC2A7A4F4C632ACE39934522ECE8BFB37475E8A1D30E4757F5F5B77EECF5A13E41AB273337B3E7FFF6F7D6FFF7F7F3F7F5D0A147DA63D3840D5C6C8C1B1317E559590D265A164595FFC7B5B9C2BBF16
	63A636FF3F85FA1DACABAE02E7BB7096DF49F11712EF44CB20BDGED6D7D9C77A1F8BFC07EAADEF482DED24C3365B8A1FD61060139677975ADBE4F9E5AFE50378F1E2F818D82064FB547F07E3F7515DBF81A05F3AAF081D605B2BF749D36301DA0D382D893D0EF9513576049943827215E129B78E286F9619F8C572BB80EEDCCCED9D65F1D5633023CD43072B84E75B255937BD4C1FB908897DFC872CF20C99A03B7C3670707D4337A763022E955E1A59613C2EAA4D4AD49E1B552506017CCA5A8C5948D4CF78247
	3B58D2C27B235DF25C69947B3102F37443FB94E871890EE70CA03E8D5E73014ACE72F51DBB56E8695D7F695F10CD5EF0517E946B721CE46B69BD19D8D71E760C6B17F48F2E99E86B65404B845A881487344F136376G3DC577DE39056BEC9EB2DB74F0D80FB4C723BECDB63AF4D951603D2181B896F6C599568D1990363F45AD9E87FD66A2566F40676E47EC12F11A6F751AAB0F10ADFF7DE5579E871BEC4978546577B0DB8CF892B6E1F3F7261CFB52136C0B49F3FFC86CF3FB1E5EDCD86CB0F77B77B697AE3746
	D9ED1BFB1387777A833A579B603D40728F06DF27F8795B891C7965F66AB3582E825E523739EF743F46F569ACFA00141E7A0A239DD6FEE729EB675BCC07126A193A9C8E61583555EC4EBDC217AA0AF75AF0264BA3D4C7EC4B405B88447CABE4E21CFA5E49F137838D824501D682E582EDFC077B58A3375ED9200F85A543740D291A2C9804758D9987612944CDC38AC4E2269499D67AA4C31506B4A5C6AC1928013A8DA5759B233EBF8CF3DCD4A20AA1192A9E01512530E2AAC60C49FCE63C0EB206C2393DAAC437A9
	0523040F7B8563GBCB5A9E6FE9015A15093920C3F4C40BD39AD754108B0G3CB33B2C98C3FB6D0075139A30DF8D3703BB0663DDD28C48978D8D1D3AFEF3AC5A0BABA103A8C706E96EB8826F0DB55C464BEFA3369A5A6B44B8CD31B7EC637855E1D4DFB2A6F80CFDBC0239B83B067B4CCF7B394F78DFFD18F8B65F6B68B36B40FFF754B0DFA127E6460EC247C43DD87BF9257FF3463193E557B8479D1BE377489FE20E4294B8336FFF0F63DED541FA7DC072A9EE63CFE3954CE7FDD2CC61DE5AED0003A9F24262CC
	4F5F05F82C62F9DAAC742F2678A40C399AA86B9437DD26D8D721A5BF5F9A8EFD0E7532B97EB1E90EBD8B683790BDB8B19B5DB271DCD2EEAB91532FC415088CAFB10B97B751772C389D51C51F4FA89E05B8EF01400A89BC4D44D814CC53D00746CC25C7B1CDB8E6937C25F42E5858D0B411A278DCDFB2D1F7FD9C62DC482FB4B1C60675C8C4211BA53AD87C7BE80CAA1A928625DB743108497074B174731B4A0415A3D60F6DA698635A98669BD69B2C1576629A8CB58CAE00A90B783D983FF7DA4AEDB6FA81769BEB
	1CF260F93CBC9647FF4FE371481D3FA46B2FC7DDCEE7494A2F7F09EB291745C63AB7910BCC27FBEFBB6B7EE7B4D7D8F80C697AD2A74EEBABD3AC3F2F1B40B15643581B00723C5C6F8F0DA31F67964B61DBF4CDE3E69476AD0A1762FEA8374694C8491844AE891B6D0D3FB10357E7590F718F4451FE1AAD7F192751FED20857FDC550BE8C54F81A59B0F81A5900497D4FA817DB8F6DDC1B5C2EA439FF92F207EB993F56FAF656B2B9E6231F9F47FD23355E8D6936942F0E0D036FB5B4F4D013313AEE9B4D4DA89F2D
	6575DB2C9DED18816D75C03935FC0F1FFA1557EFABEEE61012FF59D4182AAE5DE969D1F6A6314719DF9EAFE4FB74C7F7F04EDE6049E7787EAC0B5B4F1066D8CC9FD66971E559765C045D8798661BF8851EA32A91B3839024F13E8F6D9328AF1EEF96EC1D77CBE85E423546E15E35C0C75EC53CA6D0E653FD4799F6CE15822F100E77339076D1CDA5DCADECBBE0E26E02D35F9C0553FFC83F9EGF6AB260C2A63FDD6FD754001A6EB4C2CC79EA00D5357DCCE756E627D23AE6DDDE627956FA66C3575874F38DEGE2
	3A370CF25D3702CCF99D575DF554C977E09D2F77FF71B15699930AE4B828E9596597E2EC997A2D2A636B3E26A22EC6D4D315B4750ED2F51C0D3975902EDD6631F7BDE013683372743F4D2AC9B755A54E449DF5334FC7D62BED273E17DF47EA353D82FF1162076B93B853FD026258EE84DEFABD2FAFCAA751770B4E6638AB006A0102C0D12065E7394FE5CF1CA67354EF018814D591C96BC9CA503C0E3A03F6021CEA35175CC17B501CCA78F935BCB4538EEB4EA62FFD630CB66BF77F2CFE55B35AF9B37A313D89E9
	EFB947BC5DFE1F9E376EF321C0895599EC544063FB2A4D9E5F6B76CF397626086F5186B67762865B7D6CDB7FE14577E3D3181FB682EF9BD0BED079FB6017C07DC0136FF17B17FDFAEEBE7BD3D76D55DBGEB073C5AB2735C93768A0C5E1D1DBF1C55EF261DD73D175C0F596F6CD1349F46F366FB3C7E792F3B54E12FBA1F8E650D497DD8DDCD7C28C38D707CC08CDB520658A83497B50A1A369DF344EAE82F834A865A8634CB707F137271DCAABD17632E818A1EE3678F63CF375FCD3D3A4E2673EAEBCEFC5FE543
	33058EAB6FEA8EFBED073168365E774E30B17337DFCE262E6BAAA61D6B3A23134EF51DE772F5064398765B9D6757CE4EDD77B5CEBA55FD3F226B787E417DF46CD8CC0951G00B2C732F85B4DB709C37DF799AE00168D8E10A7C2EECB265554DB3C424F34G5AB8FAEEF66ECD83ECA5509A875EFA87EC93E0BB007201CA4FB3BE0B7D66E97425A300B5817583C501161F67B11FB3B9EF4EED0D4F11F3B7CC255E43A7271C777029A92CD3E27A18B12C4C5C275C29F241CBE497BCA55124CC6647343FA999A1E556BE
	6F207D99AFD57F1FCD25728536DF0BC66D7BF5CA6C57792338DFEB4FBB5FA5B72440F39D70BC4048059CF799D0558546E7F3572A7639CF8B7C109F6F871AB4210F19D08BDE405CD26D677700C6E87782CD82AD3F40EA8EF67EFCF39CE3EF44104230441E28B2CC521A306F575AF85F1DA0DF845891A0BF105C446B0D415DD84FF5091A0A6BF2EC926D5151A98DA95AA98B7B0A01F94EAFCDD0C906BDFFAAA16735642E7D9E5777912FFF50B7E95D74E2013DEE4AEAE2EB5859446B2AE7DDCEF5D5D1B3679F2EF46A
	DF5E4C7370B2FA3EFA206D97580B948BC2BBAA3067E44452201DAE30EA8531CCE8E7896C6F87915B8A6DFC01ED7D08677AD29F4748D544EA215DAE30G4586209D955817A816866DD5BE9E1B01695431951CF60E2D3EE9677CB8B0BDF7FE1427D3458453EF70A8375FDA217363C739DD5689CC1A62F649965885195BE51B402E4B5CAEA5AD9CFBD066F6B9AC309DB237CB8DE02C466C73735F527D00CD36F0DFBF6D451CA7EA2DEAE95CECE8E8537471CEABEC2CDA61FEFACE23EFE63670BB560FDAF14CBCE89779
	E1FEBF2F4162C7F98D36669B8F107C4D4EF72C2D3DD32EDABF0B63FEFF2286A361E7DCEC4EA7E89D007DD6GBF4BCF7D2F4D6E33989FA7892BB5987F58C5BB9F7767F892FFFF3B1D0F7ADEA830731B137845367EECEDCFD6723565BF3916E85FCACB79DD232C152D295EFAAE5F765502683F5EA9346E35A3A96E35B472597ABDAEF12F05B198F6301057EC99826B2964F27B5AF89E7AFAGF318296BDA2F9A6D851FA77EB67CB674F718922D3C322F8357BA2C0723FA84DC3C5BD061FFFAE15D5806639E6D40F197
	437B8AB16ECBB24E9D1662EAF8ACFCC59F27BFF190A6FF0046BC5AFC5FC5AE5F2A6CCD487740754A127D044A9E8639AAA13BE590ED99B5149145B09419CA93CB7EC51ABBFAC1F690E869C5FA677B281C4C363B5DB7608C20B9C211D593FF4CE0E3D5DF655F01B60939872F22EFAB9114122DB9472FF27F5BA6649E3ACA5783974A6475FC074A6E823922F648175456DBF1C80B7F939A77B540EB84E258929A772350B65B791A7A643956447AAD16397E69E22CAC196B3AD6E02FD39BEF00F6F6BB2FD1877CF777ED
	6846DD76FBB029DFFEFB7239DE9A0033A8100C313B7225B64CAD7581F6D7AE90F8AF457DB61C6537E7DAF9FE8B82AFB3406F505FADC1FB16F76438EB01BA01E420B420748EFEE63CB4DDC3662967FA75E8A79CAA1A6D98E06B4BEA3B3BEFE60F6F61F9A47331074823DF70273C27E6F6B07BEC321ED54FFD35A0D12B763DC2FFA33814632EG72838DGC50196DF626B73918FDBDFAB04C133E9CA43237497A83E38BED599CF40D87030351DF2B92DED55A526475ACB33EBB4566F2DC22C79B6B870E9ED17824FF3
	40C9A764B820AA2046CE46E7F374D7BA699634E4644E59E3B13B7F7347766F522577E9AE6EC7B365635698E91D09330945F5500FA331FC70C339921E7C682CEEF1C826559FDC3EF4397B434BC20774F13B8E752F7C31636F4CC80FDAF3E7D94F7CB76FABE0731ED0D09E47E1BABD511A50693141DF43B3220CD7883D7CAAECADFE882BBAD3632970569C677D9AAFA67A697D8B6947563071937BB9D8F1325B719C5C75499DD7D1176537DD89BB6E50AB8A2D7361AA7E26EF9AE38AEBAF3B0A3698113418A256363A
	BD21E3EE41425736A5106857B73570FEAFF7A47A554A8B6947E47FDCCEDE4BF3726C3554F7A6465E20ACE4ECD653BDC66B2F01AE167798F62695B1933034AEDE7BCDD1ECA534B33AF84EFF3F0D083839EAFBFF2E758F718DEF6E7C1F7F7FBA874BEFD9A529EB7E9F14B8577CCF17B8577C4F164C7D1B477612545F3AFEDAE27F564572F3C99756D86BE0AFF2017201CAAF4319F7196766C9776F884D4D8EF939D58E61FD9E3F2C1BFC7E6B2F24DE6B9E5F7C5F63CBFDF8BF9D52CDD38F63789CBF68437DB275A885
	9976168FEDE42821D11309DA78EFFD68E31AB2C2E166F76D34D6ED02F585AFF3DFFC58456FB08382BBD7486FC40B84F6FFA13F93AD95185A486FC469822B707271B285566465F722DC0125F9791DA8CFE05F7772BB91696618DE49F5AE93D8F6A51F23221B579015183A2D35FF5307793ECF3AE1D589F8246A865F318E08636E219B4A3059AB05048F6C6F2677FC1C231B6F7B3D7E13294681B0E868D145B0A798CA3F5071EF03C1BC6F9741B82B00568365826581958579580BAED64DB1B63B58D13FB2A4B5E232
	DC74149F77DA5254D084FDD064284ED64CE937C3D2145514C32D3C4EAC8F721AF885159995B28F8919AA21D3E9D7EA3FBD54B53F5FD6F431BABE2474CEC42D5F24AA3B70B2230F0C4494919FA73ACA8596088575180AC7B053733A0B66E7180865626C77F1DFD95D3D41B5D75D4D64FFFE8565599A1F20BEDB8F6B7387F93D7D2754E723501E8C72FA3E4EBBFFBD2FFAF9BD1FA146FA584BED7828407E1DE2D9504E8D72BB46415E449A36D74EB517753D227BB5FBFEE32E45633D9BF31578D6C60B179E900C042C
	3FED89265E430B417977B098F44EBDFD415939676320F36E1102765C4376F522956DD4A3FC76F151E6B6BA7B81913CEB0D8B2F83867A927D5E74AE3C8ED864CD2E83B6F8E757815F7DB0B1764BC796B2B67BFBD0F7512B3038B54F13E21394531F99CDD204BF8D468C722BB94B506F7D64AFDA3F36EAB84BC58A82E42F1EE044712F2A76C07FB7E6CF5F3C6C699F2D396EAFF391D7006C9ED5F0BF8224F8DC154DD15A8FBD469B3BC5BEA3C57F5716C98A5EB1C921DF1A40370577A33CA6BDBEED2F2BF75DB31C32
	2E4E3A42EA512DD792B5A94B2F776C263FCB7464386BFBF86E5ADC7CB6C91D73FB4212261912F633B9A2E37E77CD18CA22DED8329B470FF1990B49F8DF5C0DFA5F307579DFD0CB8788F584B3FC0390GGD0ACGGD0CB818294G94G88G88GD4FBB0B6F584B3FC0390GGD0ACGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3D90GGGG
**end of data**/
}
}
