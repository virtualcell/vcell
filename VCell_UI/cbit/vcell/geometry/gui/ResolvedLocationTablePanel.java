package cbit.vcell.geometry.gui;
/**
 * Insert the type's description here.
 * Creation date: (5/26/2004 1:59:21 PM)
 * @author: Jim Schaff
 */
public class ResolvedLocationTablePanel extends javax.swing.JPanel {
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTable ivjScrollPaneTable = null;
	private cbit.vcell.geometry.surface.GeometrySurfaceDescription fieldGeometrySurfaceDescription = null;
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.geometry.surface.GeometrySurfaceDescription ivjgeometrySurfaceDescription1 = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ResolvedLocationTablePanel.this && (evt.getPropertyName().equals("geometrySurfaceDescription"))) 
				connPtoP1SetTarget();
		};
	};
	private ResolvedLocationTableModel ivjResolvedLocationTableModel1 = null;

/**
 * ResolvedLocationTableModel constructor comment.
 */
public ResolvedLocationTablePanel() {
	super();
	initialize();
}

/**
 * ResolvedLocationTableModel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ResolvedLocationTablePanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * ResolvedLocationTableModel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ResolvedLocationTablePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * ResolvedLocationTableModel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ResolvedLocationTablePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoM1:  (ResolvedLocationTablePanel.initialize() --> ScrollPaneTable.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getScrollPaneTable().setModel(getResolvedLocationTableModel1());
		getScrollPaneTable().createDefaultColumnsFromModel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM2:  (geometrySurfaceDescription1.this --> resolvedLocationTableModel.geometrySurfaceDescription)
 * @param value cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.geometry.surface.GeometrySurfaceDescription value) {
	try {
		// user code begin {1}
		// user code end
		getResolvedLocationTableModel1().setGeometrySurfaceDescription(getgeometrySurfaceDescription1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetSource:  (ResolvedLocationTablePanel.geometrySurfaceDescription <--> geometrySurfaceDescription1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getgeometrySurfaceDescription1() != null)) {
				this.setGeometrySurfaceDescription(getgeometrySurfaceDescription1());
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
 * connPtoP1SetTarget:  (ResolvedLocationTablePanel.geometrySurfaceDescription <--> geometrySurfaceDescription1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setgeometrySurfaceDescription1(this.getGeometrySurfaceDescription());
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
 * Gets the geometrySurfaceDescription property (cbit.vcell.geometry.surface.GeometrySurfaceDescription) value.
 * @return The geometrySurfaceDescription property value.
 * @see #setGeometrySurfaceDescription
 */
public cbit.vcell.geometry.surface.GeometrySurfaceDescription getGeometrySurfaceDescription() {
	return fieldGeometrySurfaceDescription;
}


/**
 * Return the geometrySurfaceDescription1 property value.
 * @return cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.surface.GeometrySurfaceDescription getgeometrySurfaceDescription1() {
	// user code begin {1}
	// user code end
	return ivjgeometrySurfaceDescription1;
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
 * Return the ResolvedLocationTableModel1 property value.
 * @return cbit.vcell.geometry.gui.ResolvedLocationTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ResolvedLocationTableModel getResolvedLocationTableModel1() {
	if (ivjResolvedLocationTableModel1 == null) {
		try {
			ivjResolvedLocationTableModel1 = new cbit.vcell.geometry.gui.ResolvedLocationTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResolvedLocationTableModel1;
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
		setName("ResolvedLocationTablePanel");
		setPreferredSize(new java.awt.Dimension(200, 300));
		setLayout(new java.awt.BorderLayout());
		setSize(420, 205);
		setMinimumSize(new java.awt.Dimension(100, 200));
		add(getJScrollPane1(), "Center");
		initConnections();
		connEtoM1();
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
		ResolvedLocationTablePanel aResolvedLocationTablePanel;
		aResolvedLocationTablePanel = new ResolvedLocationTablePanel();
		frame.setContentPane(aResolvedLocationTablePanel);
		frame.setSize(aResolvedLocationTablePanel.getSize());
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
 * Sets the geometrySurfaceDescription property (cbit.vcell.geometry.surface.GeometrySurfaceDescription) value.
 * @param geometrySurfaceDescription The new value for the property.
 * @see #getGeometrySurfaceDescription
 */
public void setGeometrySurfaceDescription(cbit.vcell.geometry.surface.GeometrySurfaceDescription geometrySurfaceDescription) {
	cbit.vcell.geometry.surface.GeometrySurfaceDescription oldValue = fieldGeometrySurfaceDescription;
	fieldGeometrySurfaceDescription = geometrySurfaceDescription;
	firePropertyChange("geometrySurfaceDescription", oldValue, geometrySurfaceDescription);
}


/**
 * Set the geometrySurfaceDescription1 to a new value.
 * @param newValue cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setgeometrySurfaceDescription1(cbit.vcell.geometry.surface.GeometrySurfaceDescription newValue) {
	if (ivjgeometrySurfaceDescription1 != newValue) {
		try {
			cbit.vcell.geometry.surface.GeometrySurfaceDescription oldValue = getgeometrySurfaceDescription1();
			ivjgeometrySurfaceDescription1 = newValue;
			connPtoP1SetSource();
			connEtoM2(ivjgeometrySurfaceDescription1);
			firePropertyChange("geometrySurfaceDescription", oldValue, newValue);
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
	D0CB838494G88G88G580171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8DD0D4573944E0C4450891958DEDCD228DED5131236FBD5267FBA59D525A04B634CD935BE126F6CA9313E9D327493C47F4F86DE1C1C15146985A7ADEEDC2DA7302F90400B20DC353D4845F933B428A6B8A0AE5AF3B9758303B77F277AE3B0B006F7B4E39676EE53917CD0C5D19EF6EBD5FB9677BBB5F771D6F1C3B24763575FB4BED3504146FA55A6F5DF2C2529A89794B7ACDE39C1791F45590436F
	F50017492EF0A5BC57C35F19ECD74DE112B1DA8D6DED50AE3957D5739BF8FF052C9ABD7EB63CD870A9EBA0E4F378516F4F4F675D0B1CCF95EDDF089E0467AF8172G074FCA89798FC50FEA78E28D4F215CC648EEBAE6BAFACC431D07369BE083C01E44469741F3BF653C307534B6EEE0C58EF971D145F6886998E8F2E81259DC27762CA3BFABAF13115727291CB8A79D5A85G28FC85112EA06D779CE3A36FFB06FCEA6304C7747B9BC7C4A9A02ACA2CF1A46CEBED6D91C312FFC2745AA50F207A2420D398720B8E
	A1A87A2363C87B79E8B525C54899E8D7AA085B94C6FE5B217D84400FF43CBD027873706E8608AADC47BF3F791E26C35F0DFFA2A5A30F6CFF49C2478C0569162744F52CFBEB2862BF69FAC43A506EAFC25F51102B26814081A083E4G7C167A414C45376059B62436CB010094EC136533FE2FF2D1720AFEF8EFED059E8D57A3FAA445CB88DB6B5CE80309BC73811B77050FBC0F59A46794DF775C1E4D24F86BEADB1509CD0AF3EE58F2C24C96792138CD986F97ACF96F8A4D764B593C2F90A3EF726C492A3DA63C0F
	BDF9326AC50DCE359A67FDC64535FEFD82573A9E5E4BB5DBB07C5B941FEA40B39F5DCAFD865B5950C7425CB77AFE49E531D75ECF8EB72FB135C3561752ED5EB013E1AA1CA8CBBE753B1CB06339DF1765EB94DFE040B3D9FAA89E5B3B202FE342D5437CAB4501B11B836D8200E2009781EAA32E1AA6000E8877312FDCFC67C37A18C3D05433E11F5FABAA044DCD697AB5BC45282A881D4110AA84BD22CBD0FC98F5A1220DD95C05B6981AB56FEE6A7B9E6071FFE2D0D4E82C82F581C2D9D4C2EC4C1A2E17F10CA232
	C87685C4E0901089277B292ED7606997C26AE559AB28E2FC846BFF308BEDB2A13800A29000F7E617CD1750DEDE50FF8AA0DD37436F3B115EDFC58532C5EB2BDD124642B24D9F24B802F6BE90435C319E5E73A25C461F69C1DCDD54D5F3BA4A6974F79FB1506970F9D0FCC1097198FB137A149D462BG59D16EBB4F77F15F69B83409B4FCFB15296FE4009F97C4194F94C793E36894250D72D546DCB547E3BC77BF946E03279AB50F230F82F60DC63601966371CD4AE36F47240FD0E1204BF14C5E3FF122FE9D2017
	9BA0A546ED1E59DD47FC782C90923957F6AB60F022B73E82CC4EAFC78EEBF62D50642D52F578966D4B823A79G3B818ECFF29B76C57ACC66FDDCDDE75BF01C72B0525FFF9BF9B0DBADF160FEA5CE08C135C314452097DEC2DA5F8A87725362F9D852672C21F899623F9D82AE2463BF61409895D4D5718D05D531D7D4D5DFF0A45E3F11728A050764F86A603CF29D28239401785747FF5E0131631102C111AE1ABEC56B5FCAE3D7740B81903ADD8A87D506FF689256BAE3E2CC4B9D7FF2E99F41587707B18F317AE1
	0B00C5122C7882608A184AC85AA44D7D3123869B1D869C56C1837014A7F9ECC67E40E3737864E7C87E20ECB35BE332B6AE3795CD3238A8190C47A613E9F30F396C5BE88E5170A1A6EB6440FF41533A1AE1BE728AD1CFA38DDE83750615E141A3F608A10F6213F1C633B6EE67115FDA5123093ED90BAF47E51431728654A4GCDB7F8FCB575E2BF4FE9DAE035CBFEBFDBA6346B8D34E5F422D63345F9E83BEFB0DBE23B0C7657C5EA8D764AB374B3BE3FF3E27C0544713088DB890A77D7FDCD0FBB0FA474393475E5
	7D8DCEDCDF3FA1EF67CEA14FAA3DF66D185256961EAAC05694635DB20D63F66A63CA318FE3FF1A65506370EC84FCBE9D17916363D226993D2CE9AD5F4273D540B33E2EC9BE6E60CC3C1F521BE1FC59DA3FF0827D0F56359F8893C234B1940198EAED3DE028DB17523D87FD51BE436B53CB922DCD201D8D30ED067B6AB78E717AB4F3AC0794BF5BDCE5D50397EAF26C19097BEA431E36FD4C97563910E753CDB8275C64FE70205338C73605C21247C7DDC9DB8357E523AFB35C354B8760B96CD3C2EAA7A41BA8DFAF
	61B24A0B7B3706566A19CF51BD8C7D5689FC53818AEEA23EF882E39CED4D74741C62FAE6CCEEA46ED56BECB596FAD627B07D6AD38CFA4E146EE5763FEF60964F95B4F218AC537FEB3479761726ADE5F1EBB22498E45999F0D6B15D1B22DC77FC68AF85E0713B0662FDC11F6A937C3EC9317E844BDD0794E36EEA3A43D563840882245F4167BE9CC5BD83A8AC61B57326D74FF09F58FCBF2996965A4C4EB0694F0E58B65C4164AC3CA3AE6F16FB06AA8AG184C65B1AEF36D82D70DFD81BF2F553E0FCF1A03894FD7
	13B1DCC3F55497E2F2CC75F3B9D28A3711232FACB31583A1F581631F3BA0AE47547A210A0C75DC0E07F4B94AE0CC4B1DDC7F1FCEE0AE354E115CA6BFEDB35AA42FAA5DD416518733EC7EBB198C19F746E5E939EF28627CFDDC161B13873578AE01B147D239AC87EEA41305558199B1FE96F226B29E232971FAEB8A5EB3D2E757F26C0C70884D899BD2599921C247BFCE715B8CF8A66795095B2CG7A6A97723A56530FFE158E6D9C00FC001DG25GC76F623960594B27C812F3C3E7904A7920606F1DD5G707A5D
	05FCE14F565A8F380C7E426C30F1826D50F257072FDB593C2DF41E7DA34CE3BEF0622A717E2272658B368A8BFF1C52E8275F957781E63B5AEB38479582FEB7GD984B683B83D085B2D7ECAC3B23B71B314E6B0C6F7F2G6976819D95A09DE083C001CED73912F4BD4EC5CD5703596C0FC94F7EA50BE65B134D6BC8BA2FBAED76BC169FBE38E1316F51644A725BBE59185F6A5258594B8ECFB7G5B9B4FA97CCEA71A4677461F296857DB203D83E0D79A776B226B38A717C23BF6312B268160BC009FA08DA0F7B137
	E57E15BFA535A59650CE290D96C06D09952BE61B2A582D5974D7C96795AE1EBDCF2B5F86D13F9268A3CB406FGDAG7AGD4002CA5DC3FA3FDE71369279F47C1C7DE5F4FD5D313770B37286757124E5B3244CC4FC75C28678E68AB81A8C3DD173AEA5E83688318DA4A759CCE2E272E5C0751F361E4223C19CBE74BE72E674AB973DECD54B3018E3BF3F81056FDBB202F8400610A8208AB057641F40E5BA451DA8D5A67F55C57E54479211D1A4E4F300FC93406525B5F26ED2C99D87B3A5417E4BDBE8CA0FD6B5CBC
	F1DBF8D4D860593A7B7B2D4F3821FE73B3EE2C5F7C0CFB337FE506879A469BB05E3F28FF7EB37072FE33B3706F288ECF5AB0CFA9F0967485C44A1665C6B17ABA735B1EF01039AEF28EA56E8FFABCD48C61BAEEC967FE7F465513A6FEFF4E6BD3A50593F10A0252BDBC2CBBFD2FACFA384C472ED95B6F8F574C6DF7621A397D5E39B63F7D5E3DB63F7D968CD84B32F840DC16658366322C9A18DF16CF8CD84942566B9975D74464FE228BB2A561B5EEA03ADF9BF3CE15AEB6933E5F0D1CC39AA67BDD2FC70113B41E
	76201F6DE96F535845F8A9CC67FBDA5FB8BD1BAF03FA9220E5197EDDC719AF075A174D0DC5D69386699D36FB992B894BF5FC04627D86BC2B95C918FF1709C2DF71B26EEB5F3DEE3DBE5F3FEE3EBE4FDC47B3E9C88AAB9EB1F18D1E3FFED46F0BA762CED58C20195918FF2773D5C19991672C614F69FC56E7B53F6AEFD66B4C56707DD80D36060EE1C58AB81AB55AD7872DF595864DF59D993456B5B818DC574820352EB30349F5DDB4083A8C793CBEC53B2DE1BA7EDB0C7B29C3159C4DBA4D475D56BABE69B65731
	58ED2D638F5C49F57C115BDA47975C49F5ACF3A76A4862752B3FFE0946FBA8A406E84156A12802267F76C9E31C1E54577EDFE3A80B2C08432222085EDEB8EE234F5F0DB170945D0F4F403B7DEEFE26FBA600F70DFEA1A605D5C26A17633801A02FDB374338A7G4AF07EF2D63F32FC7079884ABC8CD596389ECD3E1599B837C062F3E5981F8A308120G20E4B96779C3996742C4DFA09C2092B279360E23FCF62053174147BA86B0EE8DD926D90B0121C3469850F2984BA96DB24FA929C0A7ABE3F68E494D304AA9
	05B4C765E7A41EBF1F2478220C4473675F467979F39774F52C60350B2070FAA6E7854FF3EA186872CD9B5E7705EFC7BDE1850C47EBFFD79E05AB707B7DF323C47741C782F5708C0A11BA5CAA9A05085A588195C290BFE4551FECEAEA28BB5554F4021DD957C70CE756538796185ECDA39C3E072D5BF16D197E0B355AB77754A04F6D835017F98F7751279B51D774BD8B85E9EDBDAB617D2F1DB9BC5B2F8A03555A7C42FB74B39825D9399276AB0016157C5EAE7A5F7C2EAC77537713229F1A5FCF6EF2CE5B6CAB19
	1C72CA43BD4BDBC3950C67F39267198B7DA5AB91F7D4B65EAF23AD6F05810D70E47D97E675639EF002987B27E675233C1D95467E4D337A779A66B35D9EFB036BD6765896627F07D44B7FBFB4E4B21D9C5A336E6BEB4B53329C7B18AF9C980B7BC20BB40FAFB423AF00C31CE0F1F315568DE9C0B3B7134752D60A4B0776EE9DF7E81C47D7D9A60F2FEF0EE38DD93F0A370BE99B6F49D95BBBEE0C819CF77B63ECFA968F2B9A7FD601655728FEE75A81BA7481C48132G72D6F19FFD7C1AB1268E2F735BF8AD14C8F3
	27E60712D50977C0E5E3E863232B4177D773DA699D5F7F4053A5FCB037A28724C311E4D1D1E38CCB3FEEE8F6D9923C357FF3E45D623CEFFC64F9DACDC96F931D2B675414946FDEBD272614F85E71C3DF51EAFECF73F6G5728E50D2B4681B08A1082309EE07B9AEE4B282F09A4B95BBBA5598E050C5FD06AB37DFEA25D5A7FDC9E2E62713D6D8B1FA5F9F7F5D85E73972D513E2DE84F3C42356578E47A65537A20AE4BD5739E009BA08A108E30A1CB3F0372E97704688EF0CA93BC23742B0AFE88740911B89A8B88
	16AF46E57C8F5984B8124FA3785936FFD6D09C125F6709E9F5C2BA4D897897333CACDEA7BCAA23EC2312621B14026A5C193C86FA1A4EED83A80539763508BBBEDEB70F6DCE2FE5B6E0FC9E1F441AACDE0E30EFF5AC27FFD261B9BDF3AD4F6905CAE2CEDFB1AB277FDCC94C69B8BF5E5F94CA4C6933676FD093F3BA1F4F566A87D866EA35E857589B44A2FEE1C5AEC0FE6E9E7AGAAD027B0224FA977A3DD3D4EE2E12D75D57B11377EDD07FEDB6A050141912D66213C355CE8F85FF7DB6ED0664F812F7DDDF9B07D
	7F9830BEEB7CB9E0FE56E88F181F6DBB83735FA3DC8AD87F9721AFE07CAF82DB77BDF42FAF84BF5C8DF0F01D2B268E20E59D0F4F27833FE771E9921B673CA3F85641FFC4291C7FD703563A9685137F0F6ABBC17C0EBAA4292A94C07A9C7F3DA02E17AA499449F0A5C13411629B99D559D08D7F9C2D537C62B0C533F8FCC046331F61CA2251F0D6E8F41D6BF1F636375987FB5BFB3A6D7633EDBD03ED76ABED7D3DFABC7F07FCEA1E79673BFBBA2FF6F7B94DA83079ED63F8FEEA9771DB914F3391116759E31FFB00
	9CFB6D0C0D47F5229FB956E97F23DB975FFF63BE7C403CF7A356FED9AADB2F55AF6464EBD5CD6BAB91EC6C0C495A99759055D39A9E8E093ACF9C99BF2C63BAC38EA964438C407CEF09D2874FAD20D7A1409E006AFB211686E881B88F603E177B620671FAE2DDD730021D7A23A278787FCED6A956BAE6AB49F55C28187B636714397E38C5B17747FFD40C7E48F07F2C51D5740BB506FFD841DA1A1D33AF8AC1E10446AA7B3F74D2358B1E39BFA6FBC38AB9B237CEEE3B7BBBCF658EF6545A082D136C9B95D110CE32
	B762732A23D199BC8B5DE4C7E81C4CD0775B8160553F895EB45602258BBD16672B54EC6603D959093560D393286793005F134D576C4D09372D568CB2C8EFC0707BE1979EEB8BFAF1DFB99BD345F8AEAB1EC07AA1BEC66B546EDBA676EB7148677CBFD0CB8788910EDBDE2090GG30AEGGD0CB818294G94G88G88G580171B4910EDBDE2090GG30AEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5A90GGGG
**end of data**/
}
}