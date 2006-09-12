package cbit.vcell.client.desktop.geometry;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (6/3/2004 11:48:54 PM)
 * @author: Ion Moraru
 */
public class GeometrySummaryViewer extends JPanel {

	//
	public static class GeometrySummaryViewerEvent extends ActionEvent{

		private cbit.vcell.geometry.Geometry geometry;
		
		public GeometrySummaryViewerEvent(cbit.vcell.geometry.Geometry argGeom,Object source, int id, String command,int modifiers) {
			super(source, id, command, modifiers);
			geometry = argGeom;
		}
		public cbit.vcell.geometry.Geometry getGeometry(){
			return geometry;
		}
	}
	private cbit.vcell.geometry.gui.GeometrySummaryPanel ivjGeometrySummaryPanel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JButton ivjJButton1 = null;
    protected transient ActionListener actionListener = null;
	private JButton ivjJButtonViewSurfaces = null;
	private cbit.vcell.geometry.Geometry fieldGeometry = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GeometrySummaryViewer.this.getJButton1()) 
				connEtoC1(e);
			if (e.getSource() == GeometrySummaryViewer.this.getJButtonViewSurfaces()) 
				connEtoC2(e);
			if (e.getSource() == GeometrySummaryViewer.this.getJButtonOpenGeometry()) 
				connEtoC4(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == GeometrySummaryViewer.this && (evt.getPropertyName().equals("geometry"))) 
				connEtoM1(evt);
			if (evt.getSource() == GeometrySummaryViewer.this && (evt.getPropertyName().equals("geometry"))) 
				connEtoC3(evt);
		};
	};
	private JButton ivjJButtonOpenGeometry = null;

public GeometrySummaryViewer() {
	super();
	initialize();
}

public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
}


/**
 * connEtoC1:  (JButton1.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySummaryViewer.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JButtonViewSurfaces.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySummaryViewer.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (GeometrySummaryViewer.geometry --> GeometrySummaryViewer.initSurfaceButton()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.initSurfaceButton();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JButton2.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySummaryViewer.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (GeometrySummaryViewer.geometry --> GeometrySummaryPanel1.geometry)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getGeometrySummaryPanel1().setGeometry(this.getGeometry());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


protected void fireActionPerformed(ActionEvent e) {
	if (actionListener != null) {
		actionListener.actionPerformed(e);
	}         
}


/**
 * Gets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @return The geometry property value.
 * @see #setGeometry
 */
public cbit.vcell.geometry.Geometry getGeometry() {
	return fieldGeometry;
}


/**
 * Return the GeometrySummaryPanel1 property value.
 * @return cbit.vcell.geometry.gui.GeometrySummaryPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.gui.GeometrySummaryPanel getGeometrySummaryPanel1() {
	if (ivjGeometrySummaryPanel1 == null) {
		try {
			ivjGeometrySummaryPanel1 = new cbit.vcell.geometry.gui.GeometrySummaryPanel();
			ivjGeometrySummaryPanel1.setName("GeometrySummaryPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometrySummaryPanel1;
}


/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton1() {
	if (ivjJButton1 == null) {
		try {
			ivjJButton1 = new javax.swing.JButton();
			ivjJButton1.setName("JButton1");
			ivjJButton1.setText("Change Geometry...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton1;
}


/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOpenGeometry() {
	if (ivjJButtonOpenGeometry == null) {
		try {
			ivjJButtonOpenGeometry = new javax.swing.JButton();
			ivjJButtonOpenGeometry.setName("JButtonOpenGeometry");
			ivjJButtonOpenGeometry.setText("Open Geometry");
			ivjJButtonOpenGeometry.setActionCommand("Open Geometry");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOpenGeometry;
}

/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonViewSurfaces() {
	if (ivjJButtonViewSurfaces == null) {
		try {
			ivjJButtonViewSurfaces = new javax.swing.JButton();
			ivjJButtonViewSurfaces.setName("JButtonViewSurfaces");
			ivjJButtonViewSurfaces.setText("View Surfaces");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonViewSurfaces;
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
	getJButton1().addActionListener(ivjEventHandler);
	getJButtonViewSurfaces().addActionListener(ivjEventHandler);
	getJButtonOpenGeometry().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GeometrySummaryViewer");
		setLayout(new java.awt.GridBagLayout());
		setSize(877, 471);

		java.awt.GridBagConstraints constraintsGeometrySummaryPanel1 = new java.awt.GridBagConstraints();
		constraintsGeometrySummaryPanel1.gridx = 0; constraintsGeometrySummaryPanel1.gridy = 0;
		constraintsGeometrySummaryPanel1.gridwidth = 3;
		constraintsGeometrySummaryPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsGeometrySummaryPanel1.weightx = 1.0;
		constraintsGeometrySummaryPanel1.weighty = 1.0;
		constraintsGeometrySummaryPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getGeometrySummaryPanel1(), constraintsGeometrySummaryPanel1);

		java.awt.GridBagConstraints constraintsJButton1 = new java.awt.GridBagConstraints();
		constraintsJButton1.gridx = 0; constraintsJButton1.gridy = 1;
		constraintsJButton1.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJButton1.weightx = 1.0;
		constraintsJButton1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJButton1(), constraintsJButton1);

		java.awt.GridBagConstraints constraintsJButtonViewSurfaces = new java.awt.GridBagConstraints();
		constraintsJButtonViewSurfaces.gridx = 1; constraintsJButtonViewSurfaces.gridy = 1;
		constraintsJButtonViewSurfaces.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJButtonViewSurfaces(), constraintsJButtonViewSurfaces);

		java.awt.GridBagConstraints constraintsJButtonOpenGeometry = new java.awt.GridBagConstraints();
		constraintsJButtonOpenGeometry.gridx = 2; constraintsJButtonOpenGeometry.gridy = 1;
		constraintsJButtonOpenGeometry.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJButtonOpenGeometry.weightx = 1.0;
		constraintsJButtonOpenGeometry.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJButtonOpenGeometry(), constraintsJButtonOpenGeometry);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private void initSurfaceButton() {
	boolean bSpatial =
		getGeometry() != null &&
		getGeometry().getDimension() > 0;

	getJButtonViewSurfaces().setEnabled(bSpatial);
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new javax.swing.JFrame();
		GeometrySummaryViewer aGeometrySummaryViewer;
		aGeometrySummaryViewer = new GeometrySummaryViewer();
		frame.setContentPane(aGeometrySummaryViewer);
		frame.setSize(aGeometrySummaryViewer.getSize());
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


private void refireActionPerformed(ActionEvent e) {
	// relays an action event with this as the source
	//fireActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
	fireActionPerformed(new GeometrySummaryViewerEvent(getGeometry(),this, e.getID(), e.getActionCommand(), e.getModifiers()));
}


public synchronized void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
}


/**
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(cbit.vcell.geometry.Geometry geometry) {
	cbit.vcell.geometry.Geometry oldValue = fieldGeometry;
	fieldGeometry = geometry;
	firePropertyChange("geometry", oldValue, geometry);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G6C0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BF0D457F58A88AC87E184ACA08CF894CC9339119DA546B563A19325C3EBEA1394E788A593CD07A6B4011634CAAA375451CC2F3482C1A50C4C4745B1B6A216E34B234882ABE322E02C4FD2A431369523EEC423C9F952BE6D2E74F65F6A6DDB6D2E3412FA4E7D3CFD5AFDABE1A6564C197B6E39771E5FBD675CF36F0A1CBE1FF7383C62B4A16507897FFB371C906F5B04AC79666E2784AEA7602AA626
	3F8BGAF103169CAE873E02CEE032B3A167CE570A874B7C37F72FAD775FF4177A972F4B07ACB78C843279746FEE08F164E4D67DB2302CF956D4746EB20FD9EE08B60302DD4117F7271339CDF4C71824AAB88A921F38A464FF1DC9774EF8394G9429EC7E9EE80FD14E0BDAAF73F9834B73C969D3DA45F124E322A960F2102D9D60ED9DF92E3CF28CF9FD1B4A09EBECB03683G153713AB3F3C8A6D155ED14F5521C11F5EB4BEA4ABCA5310621383FA13DB8E0D6AEA3049A32BFED957E22D2D5779574D305FAFE9B1
	17CF0E489AA1991447AB112314A668837D9A0D623C48F79B740B81CA8D7CBE0A6F05EFAFC006A6F4FD6F0DABDC177E494749BE4FDF9CBB11C657DC0D69D824A5F42DFBEB507E925D1713FE34FF990C5505DC55ADG7DGD100DC001729BF3C243C85ED7B205E297A7DEA20BD986CD05C5A872ADBD6603B3595C6B86EE3F9C8555C0430BD7F6EF82305BCF381DBF760932FE3B649FFC76CFF41470F1062C7D7D7D4D95824B8FF3222B044EC31BD143089637D73C8BA5E7BC3337DF3B66F5F91B3EFF260D255E18B5E
	67F6DF2AAA63F4EA75846FB61D46825D6BE678AE677E41706BA8BE5304E73E7A9A45E35F86E307F4619B7D4F8BD91C159BC96D45B516F6487D736C0A01B013C18FA74BB260C35AF9E1467338A14BA845EFB36119AC7FC0E5417EFE986B9BF7D5B37FFAD94558AD02FEB1C0A9C0CD44D55D885085B09091BEB6283CFD07BE56ABE9FAC7582738A120585A177DAFC2ABC7F5CD6A8E04F4A9B0A43BA44DA78DAAF20870B975FE3441602CF58D5477070047F5B9A0EB126ED383C0DD02481535901BF3497F824E51E4BA
	5A67734B40409FA402EE2B7F94340A94527FA76816F4B9B1030DF778F1CF46A597D08482704D6C5295C4FB45C1FF9B40A6438EDF2574EE48DAC0D6DADB1D2ABA9A8E7621A624B402F67EB54D9DDB60FBE7C45858B106384BD1D7F5EFD450A9751FB451F17806D0FC48C4A24656F8B1BF3B2342E77E29DF780C63784324715B2BACFDA6877CB7AB4AFCA5AF1A9CBB1F23B4D1AE56EF70761B563A235671F33740F8F44C62E17FA352FF5E582B2FE838D75BC17EFDG15B1E1633783F54CE7BB2410AC3C74A38D9CCC
	F6A7AC4E649C74212F212D9B386F3C82008767F9C317B1BA679C50F7GA8GB6002DG7BE24226A55E5B96F29E63346652C570BCEFB156E0E95BEA1F95AF7B9D702A62B29D3798BBECA157A9723A0A6717BC8E4729C38E4A81B7FC0478D8230A34F8FC8F2B461ACB949F04FC50898198B27017D50CD9C957B55FE0D817EF4A3A6E8BF89263ED14D7A8BC98CC2492416B3A0A722911G9E579C67D4B1160654C0C0261BEAAC61639F52D81695598FC2F72A61004E70FDC10C07D1B946F3C9FF708841DC2004B1AF31
	3A22D0C2DB86B59F1689185A48408446F9EF642C49C6B500437A2899DA4704085948EBA2E66BA79EA2C57F972C30BAF3F27FE4D945268996FBDBA692B14BE46A9E33167DA31AD3B8BE44E42D6FBB83ED1A5206474ABF06105E9E60F39020F2D2444A53E3B8AE72918F12CED5D118C98DDBEF51EAF1EF6431308CE99C935F8D43FEDF53CEA60D3938AD5978139A5AD2B1654CDD13E84BAF78C47DF88E7AADGFD134C1E36C9E68FB6EFF4C44C2B0BC3BD8C509F371A77762818178B63059673DA26F05E770DF9BB79
	78FEE84BG6A26583C82BA6FD1C54C4B18E2B11FBB15185FE4707578453CC3FC3CF61A3575530C9E5B27FFE9C05F21356A08B4AEC51BC291707F56561EDE892C4FEA50A768B902739D5322567C4184BD1320EF83A81896FE764CF1D1EB5AC673C971010BD5696A6AEDDC0E7D5389BF5B7C4F6DC718EF7CAA0CBC5F1C815B4E883F6888194F3B76D0C89D725123166F690FC3E6BFE438FF8D7DA73443BEAD24F7C322080A7DAF8B213CF896F3B42FCDF6521A85F5F5825FCC00C3B3083FA11FB6493EF5061D297B20
	35D3FA6F0EDC42BCC29D3409B53D32B62CEAFE597DA6DA36C90A68CDB4E7B5355361EB785DA07661371196E8B599A4155B1316BB532F7FCD030B59FD6329B6AE5B0EC7B612ABB3FF28302A7133CEF8AB5AB2183D3D99893BF72D98348FGB09B58C6048DB6431C03994286E76CD6B6287F0C3041CF6F5A860CEF7F2860FB99E886BFA3782E0EDA714D07F1362EC691356DD640159A7204947487F151E23972F31366DC59380051D2204DD8A0562D1F350E597761D74D76BDAAAD3234EF7681CFC56E82FE07DA1030
	EFA158F61BE15FE2C35E238BDD55ED8B453D7174D5ECE94EA7622E903FA578E66C58CC4E1EDAEA4997A13A1057DA8B93FC63F903761CBC41B747AF6CDB82F35E4C947A7E1B5F2CAF13E5329DED6E8B78741BE1EDD89A12BB423A2E865EECF031737EEBA9F71F5B19091ACD4FCC2D5F581D60FFA7D1062CCCF6A7B8E660BD943F4904E7B29F24F9867BC5B0D63FC85431DEFA66E5C1FF9340D600DDG25G27970BFC11293FC36639A7F48720FC8FC84A4DD9873C285743E89FB813F97FEA583CBF4C8E3BFD49F6E8
	DEBCDBF7E7D21F2D5B17326E5524FEB0E99D5B1793B7D1FF0BFC4D760708F70385324859D90BC5EE1E7AEFF3EE5EFAE22A22BA0DAF95F15EFB97A7FC295E5FD7455F81E2F836545D632A3E8250879084308194826C3AC758FFFB783D796CCF93C01F5A8938AEB80BBB136BA643DE770F5C1D1D73D356A55B39741E596B187D1CF6E13F1AACD1CF7FE1746EE4D0D256A54B60483212619B51231C4600A143C9E53E778EEB996A66DDE74B32723717FEFF09243EE509DC5164897BD29E30E865A0F4784E24F83FAA48
	9235C32E9BE3FA9F744BGCE5FABE27A5E89742BC668F7815C8608836481EC8558FB2F7146B47E1BBB74ABF64A24FA953349776E5216A5732EA31F1D3D0E69F7AC0E7A1D053196GA70097A09BA09FE047E705FE23639D7369E7BCBD000E626EE291BCCC5E557E3B735BCDA96B1A127A7B1274E46F8E3BE93DD886E335CBA0A7D35C23B14435C05F39C478726DC92C5F035037816483ECB7467CF48C6BDF2C41AF735A190D7DE442FC0F47755866AFC948E2DB12FA7E4C875B799AFE1F1D34FAAB28CA0A0FCF4A63
	98EF59BE0FC5525FE323916BFB6CF4447A9E1B99F9016101067955CB0CE7C7663E675AA2D6775CD7286EFF756289CABB9412C3B4309C12AE71319F6AF84FCB396FFE885E496DF60A9C9FB94667DC53554E0B060D6F112D62E8BF5F07B20BBDE4F1F45F9456A635592E6AC6G8740GC0BCDB444FDB51F94F7DEB51B94E7DF331747B72DA4CFADF9AE2F84F8929E18DCB255936EF0E1DB54692C15A2D4BFEB4931B73EB3ADE17B40F1C32F7EDF4BD9BCB373EBB16EEFF599EBCB3EB8FAE99FB70DB3B559E2C4FE636
	AF4CCE5D83C6EF552819DE0BC16FDBD1ABFA5BB81DBD3CBDE8224B68F5ABA6FA9FA4FC64EFA7674A35E4E93ABA33C646B36674D2D6CF169B78B394DFE74233BA73813778AD279146F2168ADFDAB9115E976E1F3076053C09743E70050979FD21F0A23DAFBCBEB13FAF94CD4C6D8BAF792B392DFB07B5555FFB1153BE95CF2F6B4F62563A5645536B7AC6FCFEDD1B6369F5FDB7BE3F2EEDF154E5506706FBB3FB9DE2BAFE43EF764FF78C5DFF9441F7B5C50A29E19D6EAC77615ED7C6451D3094763D86F0ADGDD77
	313BA12B033E9CC1BE43F06A02A9EF8665A1F2E5992EFDAAA65666437C2DG45GE5G6716097BD5612FAC5E42E815F451104BFB864FC7DE45DFE4381B6F1ECA60706D0CDF45C2FC4D876526710FE043848BFE8EBFC06FG19A007ED19B8076D43629C4EDBA6FCFC75D47AFD5FB0E53D6F8FCED91FC38FCD4DFD8EFDF9AA1DEFB2793CF6A1F309A1F36B85A1F31901BBC0EBE3E78E647E9C017B5028B13C866EC18F7D8D847A3B8C5C3313E25E5E9C232E46630A671C17CD5F1B5C66BA600F8D0C47D7BFD59EAC3F
	B5C6C5FEEBDE8E76D9AE7C7A62ED2CC90D7BA43E50C0C12F795C9D1247C923045571BA35B72E4FDBAE6A780D14669E6817G9CDDA16E1951DF08FBE64167B7121DFFEF7D863837EF2A22EE854B5F0E9509FBE64BDB03F64633A4A6F8E64078169508FBF5427C9604F170A5BB2BCB59F86F2CF1743599E29EDFB0EB9C657D6E91737866D963E823B778FA265B532F8B5D4A1EAEA44AE319E97FEF002CE4BA5573366E1BEB4BEBF3FB0F3058FDCC4677FE9D82EBE0A57ECF0402D7E532FFA54A7033E187271137B275
	FC933F6B8A588CF3362FE4310CE345AB93E79B233D69EA02764085C13B59E6FD361B41E1E3344FF0FA7D3684ED2687094D56634977933C641860653660F5B809D79466E4833CA67CAC0557939E3435446FF410C583EEC347936D893EB5F1413740022F20D9456996411CDDB626A376771B7832783CE4D9534C9F57EDF739AE760957315AC60FE1BC165A12EB1EA90AA72B12EB1EFCA3A75440D8F01538077F249D4F34DD50AF81B83A5AD5DD875086B030DA1C9957271B49BCB5F61F9AF4422122180E81265F3331
	3B7B1F0D2FD709585B7C441F12AD0B9DE95F2F022B593E6770F64B0E356558B27D96F522FEC5B0D68CC056382A4F82B482B856887D16CDB7B27D2E39FDBADC3125A1AF7DD5C1A80747F1820D858113712FFFEF7E3F225A7B150A72B4B2762FE1323957A4F29F4BFBB706455BD3AE0C1D277290F214083F1277D3F1A28E9E3BC68CFB7DDD0D305739AFBEC89CFF6328903115AC4BCEA1C3EEC2164C0116E3CC1E259704BC7D39D672885E672F63B7DB33BAA65664D82E19CB070E2E841D7762024E76BB26D372BE23
	B3510DBD66F393314477C25377A351CF7FFCFF72D365417C75276D69EB380A8E6B9A6EE807F58D77FC475CEF85AFF6247FCD7C158E73EF62AC5EFAFA70CCA5EB21A686E8836883702E953176450E1F939AEB96F1F64D6D413B837E270EAE785F5719DE57D51D737FFF4F831D781B60208AE5339F698B7C26CE5CAFDD8DD2A42FE7BB51C61A4F635559D40E7FD2A77A1BA28FD3B45353F30398636FBBDE0756A50D704C08E9C45504D6BDD0EAFDB4B882B716BE49E378C96F30B9AFE602FDF2574E7D0E45782EF062
	F9D7GF337839C84283C9FF29AC0B30083209FC0814884D88FD088308DE08F40FE00B2005AF5E2BF1E3B5610CEF6C0726210EE0AA679F0DB387CABAE18652F87FA17574DADBFDB37EE56F96004B5376FE85D2138F91D8EEB3266D947747B38A73D7F5C6A195FFF86FB686F4C306FFD31A03F07FAFA70DE298E8F07E443CF151EDA83579D6AD5C3BE2CDC589D44C3F37C7AF56CCCE6B80562B6F39C2371F9E7FAD99FF64EAF6B1FB92DFDFD2BB355573F6E347675A71DE6DFE7389DCE0CF38F78C23FB16F9983F74B
	406D26B49177BB1FDB779A78E2AA97623BE4E4A8705F23F2C5A82E1F63FEE8425D623803CE34B7286CD5B5317647CE2CC507FD0A427A4F52B93EG185F3047F3D4C6DFD0B2497D9F866E1631978FDDC85D1FC7AE187707613E92CB1D77F8ACF55E09A9B1EF21DD60CECF09F9574AC5BDAC7D2206305FDDFD12C2CD467067224793F8E1B2427E877B5F635F41787F91B92C13131DDEA960116B44D3C0D3D313CE0EE335D2A7DE8F200F0F85461464FEC8A3A70CDF6B3B2975760E3F53EA7F7E03768AD259CD0EB0EF
	6AA61568925D64B07518EEE287EF0A62FFBBD61AA1B4C626E7E3BEA91608FA26E146DCDB9DDAB4E479BFCC34265A4072C0F4C3221E613F9B4FD01B83FE6786116F36CC633F30274B55B77D12226812B25A9EF0E35E6E0869F262AC744FA07D101843875918BE03F20F18567CBFD0CB8788F92BC0032B90GG10AFGGD0CB818294G94G88G88G6C0171B4F92BC0032B90GG10AFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG65
	90GGGG
**end of data**/
}
}