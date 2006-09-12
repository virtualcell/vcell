package cbit.vcell.client.desktop.geometry;

/**
 * Insert the type's description here.
 * Creation date: (6/2/2004 4:20:21 PM)
 * @author: Anuradha Lakshminarayana
 */
public class GeometryDisplayPanel extends javax.swing.JPanel {
	private cbit.vcell.geometry.Geometry fieldGeometry = null;
	private cbit.vcell.geometry.gui.GeometryViewer ivjgeometryViewer = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == GeometryDisplayPanel.this && (evt.getPropertyName().equals("geometry"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == GeometryDisplayPanel.this.getgeometryViewer() && (evt.getPropertyName().equals("geometry"))) 
				connPtoP2SetSource();
		};
	};
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.geometry.Geometry ivjgeometry1 = null;
/**
 * GeometryDisplayPanel constructor comment.
 */
public GeometryDisplayPanel() {
	super();
	initialize();
}
/**
 * GeometryDisplayPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public GeometryDisplayPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * GeometryDisplayPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public GeometryDisplayPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * GeometryDisplayPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public GeometryDisplayPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * connPtoP1SetSource:  (GeometryDisplayPanel.geometry <--> geometry1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getgeometry1() != null)) {
				this.setGeometry(getgeometry1());
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
 * connPtoP1SetTarget:  (GeometryDisplayPanel.geometry <--> geometry1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setgeometry1(this.getGeometry());
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
 * connPtoP2SetSource:  (geometry1.this <--> geometryViewer.geometry)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setgeometry1(getgeometryViewer().getGeometry());
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
 * connPtoP2SetTarget:  (geometry1.this <--> geometryViewer.geometry)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getgeometry1() != null)) {
				getgeometryViewer().setGeometry(getgeometry1());
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
 * Gets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @return The geometry property value.
 * @see #setGeometry
 */
public cbit.vcell.geometry.Geometry getGeometry() {
	return fieldGeometry;
}
/**
 * Return the geometry1 property value.
 * @return cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.Geometry getgeometry1() {
	// user code begin {1}
	// user code end
	return ivjgeometry1;
}
/**
 * Return the geometryViewer property value.
 * @return cbit.vcell.geometry.gui.GeometryViewer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.gui.GeometryViewer getgeometryViewer() {
	if (ivjgeometryViewer == null) {
		try {
			ivjgeometryViewer = new cbit.vcell.geometry.gui.GeometryViewer();
			ivjgeometryViewer.setName("geometryViewer");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjgeometryViewer;
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
	getgeometryViewer().addPropertyChangeListener(ivjEventHandler);
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
		setName("GeometryDisplayPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(402, 481);
		add(getgeometryViewer(), "Center");
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
		GeometryDisplayPanel aGeometryDisplayPanel;
		aGeometryDisplayPanel = new GeometryDisplayPanel();
		frame.setContentPane(aGeometryDisplayPanel);
		frame.setSize(aGeometryDisplayPanel.getSize());
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
 * Set the geometry1 to a new value.
 * @param newValue cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setgeometry1(cbit.vcell.geometry.Geometry newValue) {
	if (ivjgeometry1 != newValue) {
		try {
			cbit.vcell.geometry.Geometry oldValue = getgeometry1();
			ivjgeometry1 = newValue;
			connPtoP1SetSource();
			connPtoP2SetTarget();
			firePropertyChange("geometry", oldValue, newValue);
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
	D0CB838494G88G88G5D0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135998DF094D7191F948204CAC7DAD2871DC84126CAEB54A80C459924B0E6A4F6E80B8C56CC070E290022B295E798A6BA6FFEA0C00006020A1A16A0200019C00096BC0390925AGC1CE8857CCE5066E5DED124BDDEEAF3BFB393B5C6562773D37EFEFAF6C5D819DB273656DFE5FFB6F7BF95F5F5BA3FB7797ED36D87792E259CC343FB396C2266FA7E44906F913B8EEDA50D5C38CFF87GF613B7A4BB0CC5
	C02B266FD3E3BBE05C85B09FF0B85AA5D7CDAD19935B2761ABB4BC870B151075F44E42D81D06EB0777CFG6682AC14587C15B0EE231C9FF11C55665DFE2C18EC783EEC5D097B987664F0B644565E5646FA3249E29FC0DEEB291C38E6BA504A81D0F99B797960B9981B1D7E1EFFFB5CBE35F150A386820D1E00CF8C2A0DDED1712BD22831C7147AC5D50EB99C9FEACFEDBEA59490E2CEA1A886E07D984A23E9E8AB5D937401779DB2626691643B985EABGB6687803D644BB613997E00C4CF5BD7777E6CD172E78BC
	D25933E45BDB99F4AD14190E8B65142E75475C36BF51F3B9BC00762F82DA3D622AB98D50859085A884788B5076100650B198DB5CEA2B545FAF85DBC2210B812FFCCD720A81F8F6B8002261EE089EC97692424E7CD7318693F93281DB775B7BDE47ECD2FC0A1F7F5C9B4F110AEFBCE12DB631C9C5F15CDA22B0DB14A9A91BB05E6F8FE5623DC6C9774FF45E9F91A3EF3276E475E6935EF5ABCED6D7E97B542AA95EEDAA1E75BFACF856A760995761FA06FF1F62738DF8662B5F21BE036FB301C6425CB73AF6F1D9BA
	6C33C8ED538CD3BB94AEAA30FA43CC06C4F834ACB3285F9507C74BB20B629704C74BF2544AE5D9863453032E9A66DF0D8A46EEA13C17821483D48258A360EFG27A35C474221637768E3CEC1D6AF06FD812FA8933636F1608FB00AD1D596AE8795D588FAC417A07B84F7C0D408B6674CG5A401D362E197A3E87F8FCA886C5D9D0FDD290F697A0F2C5D9E1F3AE8D6C46B932C8291D3EFE91987407885F775A409E988302225E8AF985D5CC4DE074DB83F8A603028BF6048D60195965FFAA5A2B93748FGA4F4BBAC
	237BDD95E5C8988EC707A4794321CE540414C7504E57E86E18864F4591EE630F42082B0D3AEA0EC479BE55725B06FD5AFC9E94DF10E3BC461AAC382699665F0EF23F79E597771B361D4F12069FBEEE6AB753400755A8731702686878E9267B22EC25D11E7BDFA0DDD9E237B9EA9ED7B4BFE72125670C0A2CBCECD9E81BF36C3FCBBF33A503F8E64BC12675G35B1EE6B0BF2BD735D0B02A2F2EF3DAE0323095E1465191C1F37EE55ECEA53E5AF2638A3301FB346EDF65C5AE518F7BF39705E73ECBB65E13BC79B5C
	AFB05B1CD530AE0903D0A65B44109874420322514EAB68F7DA5CF6CB7A1ACB949F02B8EF0540D2F47CD585E3D1D0D559678E2B62CDD1D5FD411E947DA66525045D21D40A603CBED6D0C7A982F12E4F3F23E00CF824E0D0240724AF5168820DD1B1A07603502DD2B828B2FC0F0A3D0DDF0CE9B9422FEEA1986301B0669B56AF1488589405E4DFBF9CBD26ACD2964378758E6DB358A88AB86CFBE642D89263F198B94863705050B3E4611D10552C16944E790C35F60845472121D49CB219EE074DE5F753DC216195A6
	ABB988CD9EB1EBD9ECF44D0FECA897EFEE1A74F878DD84F934816FAE0050900F075742C86739C78B04D6A990E0E686DA49905AE20CE52F26CBBE3C978DB1DB24FCE769E0AD1E27B89096A125E3923C2A5B7C654137C751DC1A7D997D55C134FF4010BFCF4711672BD65ECBEE54785546D92EBA84633B00E773E65B783CB186FAA14082ED9E335D2F0F62F9523E30CF9894220DCA84FC526138C26D477A3D97E94E2635B14E7B3ACD43E83B2D892803GE7937C6CDF5A497B3A697EE2D2313629BAD38F6BCCB0DB29
	095459B72CEF59424E26BD06BCCB0136B8414F65D644D8DBDA94C57278E8D953ECE60F9AFD036136C53743586D139575B284EF145B772DA84A0BF5CFC3EBFD405334D7C13FD989FCEB07DDB56D43082F34E04C202D191E1ED3DC4FE9C3F348A70FFF4EDA13C14F02E1265F5CE1031E490D1B99BF1B0D732B84FADD52D543FCF6932DC35CE7996ED19B4A20767A94A6431BAD97B49E654F4DA2732B8B2CE6BDF46F5305564E2456A7A5D3B21C7E225B568E40647801AEC7B94C310F38EAD83CFFB5E90C776691D637
	D59873C7382CDDF6232CACB73FF2D79FDAB4122A0525A3F757C5561F8513A8C759886B4FECBABECA71AB8DF8A6B7C9F23957G2DE38C6FA98E44D16E92F8AF8728842882289B6B2AE99E4B7DE9C67494495133DD8EC2AB959482B75312B26F1D6839C09E555E7F9B439821F914709A752C856D50B5B6F79F10DE5B1E276BC277310E7944510F0DF7C87B6EABD6DB863FAC5276AE991B7289E63B179318575683FE47B8489BG4DG3747F13B2D20771B2CF6637D2BE6B02653B6DB2EFBD1E2DC3A3EEC5D9FF32EAB
	9A350E69F1A409B9F0BE5016832C8358BA5ED553G508E90984F75B9B4F4B117BEAD3CE8F7CA7AE36B68424B65FD658175FCBD673A3C71696BD88EBFAD633A51F7FB9E0F0DBDE1DF6AC26F720991D1E6F9FDF5125F63E70E67F95DCE6FE44B61FD9D00BD8F6207766DBF23B5208E5ECF68386FA59057896F01BC5E7729C94CD1537477A4FD473A435E37A433753939G774D9DBB7BBF950FDC7DAA7319E3714CFDE1D35C3CAFBC93B76F8B4F45F7B3BC6CE13C9D6A7DE4BCFB5FF8ADEE56B73EC3F5D9C16F35B274
	CFF03123EC194F0851834C67EF04034C6D1133B2BAFE75D86A9E41F31C1B47E366A77113A6B1F3496BD3A519EF52A98A72756EEEBDE0EE06C40F5E5FA4B25B6F7504397DD6A74C6D770BC4F67B7DA61149FE4CC62B555F9313BE7ABFF011567CE18F39C0FBC81C63D4A5E7133E7749E4E6BD7E19B45763FC92FBA2C58A4B9EF1342C9F50FA4BE82984F3D9957BD19CB667BADD2F8AF20FF8172EAE3A1E51B22D3F1344DE456D737AE42D23E5BAEE379BF5BC2947533AE4363CD4E69213DA4D263924A20F55EC0B0E
	9FCB71EB8CF8D64B37937E6DF283503A9E617D05A50E7793686925308A774C89B8F7DF1C77A58BE05EF200AA005A892CBFE1BE7E35886A59AD8B7DF05C54796C93F16DEB893EB6847373GE683AC86281C40F93E5B08741E342C49ED5CB7325368CF1A1F301CE89B61B931E3221E838951EDB95170DC1B754E7FE90171B87AD0F930F33B90676796851DCBA6F27B7F34916D255FAD9A05086AF0DC1470BE55418E1355217CB87F2630E6A22FC35F22FB5667435D8240194F7B6E685FF85FBD77CB334832B74C6F97
	2BBA072D3D795AB71EFCC34FFB4CEDE3BC6D431CE7995049A4441D9C5EEB58837D6085785708ECA95D1BC6471CF2149869CF2451D15E43E96B4B52681B8D6B19EECB8FF35D2A16161040B773B2FE7FEF1F44F48AE8E37D0BCFDA728B1DDB66D3BEFF9E294FF2E63335B54CBFBE08BF58B76E4E87DCB7753E57311C111FC0FFA013C7671247A8FE47645139E4016E137B0016184C6F857BAD987B2B603D8AE06F94688781BAGFA27705A7666488912239F6C14C29D10748306F2237538438F766D7F3B557C6CCB3F
	7DE5B2FFC2DB467BE4E28ABB3F195AB83F7CC98B0ECC3F2FDBD13FF2202D8158D1G7DBB40D9GE781574FC1347E9D4BB5D4E74153CBEF4CFA7107EC17C2E362E4B9714C886661D6916FC4DC6E0848652E7B4AD324EE7F856B6E8CF2FB8B183C538AD27118720BC793C4FF1EE1F8DE94FF78B972478F1587337991CB66BE6138453CCFF84FE25E6F7C4B123D9FEC35E47E0EF845E27C0E48FC661DBE4CCB0B61DCD68158273AEA7681B4CC657E325272D766AFA63EF2495B03FD8A7EEA21F27E4FDBB36B3A481A7B
	370E25D67CE66316D4D56A477DB97EA57ABDDF15C294295DC52DE8A35957532B32299A3E02FE138E085D944D74147D75B03603DEDE00FC00A200D200F200D5G9BF51D8336A3B03A043E3BBBDBC032A2C67516859F7FB6F938AF335E8DFD3975FE2F0FFE3F82F9BBE3A12DD7F374E14CC95D5D0A285B77FCDF2D0E3B2CB8A54507CD9C238D7BB34B10674FAD43D43F396DBF6B3F5B7605FEF35BFF41EF34BD43BD256DAB6B0DB543BF63479A47FA3BEBC2D06821FE79FFD0CB87880E9747FDFD8BGG3C9FGGD0
	CB818294G94G88G88G5D0171B40E9747FDFD8BGG3C9FGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG378BGGGG
**end of data**/
}
}
