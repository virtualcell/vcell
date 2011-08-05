/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
	D0CB838494G88G88GDBFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13599EDD0D4575599E9638FB2252DE9C8EABA0E511626A4B50DEDE8A76D58469F267107E134CDDB1C1A54FE4CB42DCC358D31B63D6CAE68A6FE20A199B321919B0C286B323BAC30ECD0790C28C4995D986AB0939F3B8FD8D976BD5EFB8888121EF36F3BEF5F62DBD64D484C613EFB4E3D77FC5CF34EBD77AEA95F3F302458D6CEC8F189517F9A0A89193F1F104C5D728B9C97B8146FA4263F83GFB08ED5C
	81EDA75036517E5C1A9D506E823882B8ECF321ADA3DF2F592763B3F4BC07E29BA15955B86687B595BA2E9046EC87DC88E0301A0D1F83AD93608B4100BE2E774BB9E4734F955BCEDC4734A607757ADC47E156D612AD4539071157EF291CB827897A9AGAAEFA7FF3EF69CF15DC383A742FDD14DFDADAC46E26EF0ACAA46B5F7C4D407B4C9F68F08523028A9934160A97D2BBD2A4AB1E122DB080BB118DFFD84F99C72EC27EB02BE307EB20AFB349E794660BB83E041910EFFBB0078A27CBE1A6F2CBE4AF5BDFE28C9
	5725E772C93246B76C581DC2576623CC47412389DD8B0656595E267B52BA0176DF8C3415G85G44156F2C84E886F887E8EF105363C720ED69535A2461E1A95EA24B2D310872119491E3709D8C82C547DD90431292A1046D796FEBDC96724C86EC5E265B1E47EC12D34B77BF7742E32460092F5936D95824A0E7525663E23610DD891BB05EAECFAA5ED92EE47FCC667D89B1739E7F6EDAFB09856F798B565A976B6B6419F83F64423DBE5000FB7DBCFC63BC1C4F70AE0A779CCB60192FBEC1FD867B6DC04BB946FD
	23E79717254B310814F96F3734C3764FB2EDBB5CCC06AA77CCD96E25FE576D1EA9CBB645C75CB3E5599F60320C832DG00795759C90C5D661AFCE7AF00861089108B30922020067B58D746EBEE51473A85C5EB9D0D46A222C2585C3393EFC1AB0EEB0AF0BA2EEAC2BCACDE921428509793D5220F89CD208D7A12667D0F7AFE98F81C926322A2E8D1A98E2B8B9039222232B1FD93FBF00CA2D2EAA8BAAC8203E11970F52FCE3C01F1AB285A47F2C450444488C6D7A6F0CF2E8917E0C5D8G3E19DD462750DEDBC17F
	F2CF3E334A436D70973A5ED9D10104918CF6C95250289CC2CD0866C1BB37515C91006FEE8F3731F683F1F950DFED2CD3F5FD2FE91D76E898459714899EE3079AF04E86986F70F23F79EB8F771B761D0F92575A7BAC7DE67BC3EB6D95DE66AFFE6F4C787151F5D136DEAF4F7DBF2C6F19A5F6D106D4B407B7B5ADB9E7644F4A43BE8B2DA44D7A3B0CBDFBF19A77EC8AE44A864873F2DBFFFA3D1279EE2B200A5CDB4FAB60E8E2A4E1F9A667D7835BF51B5A8D593FC1F12BE13DA243E68783BD26F137138BEFBD4F1E
	20BC6C37E8035B85E61B0FA771DC922F41B159AE4AE2BC829F2ACE6B1DC43F536336DFB26674D13C8CF15E8601259A7841C90CC5C15314E85F28A6DE94B5AD9A9FC850E54ACB9D6D1393A902739A1BC49D25B108F3E37C54A446C8D80A47C53AC946941DBECDE3D40C0943A0F41BB49A5798BEE38AEB1BA1F1C24F917326CA8946F8EC94738D2B97168A2F238ECAF498369ED39611FD983F17BD7BCCB62A849C56BD6D507678F89C0E3D4763306A7A374972CFE51B55D9123D64CB36BC9F0B0FD53EC49CB219A6EE
	D84B7E994D95BADEE53212772048A3D6A50B1D4E79159D6562450D570807F78872F889F8135AFC673EDA9E8F7F3C01F41EFB74C0E813E2B1E6E620755422AD2EB5146B3AF8205FD94BEC11701DBFCD1761FE0AA323A224F4CC02E78D1BEF1C5EBB03F6C937BF233FBC0D760F1972E7816539BA40EB4905BA3F3CDA162BD6C17BAE605938879B7938EAFF025E8C5F91BF9B47EC3769B06EA72D8B2F8A5704F137BA86BE998C1E2176E3755E8A1A3369596867F5DDE1AD5AEEA974D7822C77733DFFEEA72F6B668F65
	105052B576D4B5EC111F592A22AE31775519E3F636B7E2B1726C85DA2C0E6F0B721979ECE9D1D5A99C25471AEE3383446C9B8C77BEF98D5A7E2822EA27A1F84739FDDD346E41F3CFC76BF540C334D6C13F1983FC73G8A6B907F6B860C9934B553B3DC4B754C3A3E04DC396FC15B6BA9747457B37DC27589BD0B1CF5F6466F15G6717897495G4CE78B69B944FD1661B29AD186EDB02AB2997E56522C73D87558A21237AD53E6D5C38FFEAB5B36D567DD5510106129975659D683B0B91EEE64F2E8B0A69700457B
	923FB95EB7B430F33BA2106F7484382C6702E6D9D9EEFE6626BA34B310B88BFB83B71F0B2CBE0B78D18EB9406AB33B010FD37C9C931E497D0FDAAEF79650B687F8CDF1420EF277B4C24D8610893098E0854006C66ECF4BC8ADC9D3331D0EC3A99597E2971312B22F1D0A11AF64D13D9F2797CD1AC789BF239EEEC0BB10E07ABAA079ECFB1C4E5BF79B7318CF9C3EEC3EC3BA761C3159D378E5273EF6CFB061934CF69B9D185732813F8CE0954086G47875CEE1B0ADD696C466BD75DE0CC27FF87525D0B2ABEC856
	174D5B5918EEDE670CF9CC0F0ED24C01D700B685B09FE0A9409A00C20072A62ECF0B2DB51DBEAD7C508EC946E75B4C03174B7BCCDAF92D757CE55AF92E2664F9AC077B951CB773EE4F6351BDB09ACDDC68AFC545B1D1E1F9FD1D1F5F635B1BF8DE5FD2C7EBC4684F874885E0F57BFAFA862C007E73866E47BE44ED05FE79F1DE77DD75E30E8A98FD0D76715CE17D2D3533553969G57CD9FBBEF7ABF8F0FF475AA7319D37654F561B93BF5DD9832DB5705176DFB989E56B05F8E0DFA52BEFB5DF855EED5B77E076A
	728B9B6E27827593DC6CA8DB66B3627881667397C6634C6D1133BAB3FE0DD81A3B8D77B1F41C478CF178ACE226A39255A405AF92928565FCFF3F91B097E5B1EC54B70E5476FB55E1EDBFC709357DDEAB195DFEFBCBD2590F59680F5A1B44220EBE8797E95D9F5EA0750DCEFDCC37A6F5FB0D354F17265663D229359E3D25D8932952289296E74A9AAE5DE75092896634A68E23B8ECCC0C4E5784E5C03CC9D7154EE734D47327CA31D6690BC6220ADE51B29DC9502C234F0827977D3365A559A2A675B31B3E6FE49C
	E7E7F631019F2378AC931E1D65241E3FDDAEG9AB9416B0B17BDF8BF011ADE9A55601EF982476E72723AA4827DA90045GF9A7D8FD42FC7C71B15433DF9106E13B2973651E4439EBFCFC6EBE683BG3AGE2G19A7B94FF75DC89FC84A1A5C4677EC5BE976A75DCFD8CEFC454FF3626613C68E2CA706ADEF183ECBE73D73FFDEE0BC765FD59EEC5F6A3DFC5FAA1B619E584C6D7F87B75A4B38DB3805B1AD98EC1570BE5545B6131DC353DE7E2610554C4F21DC3A66CA682F87A8EA66F577787B3C6E4E7D66A25219
	EBFD3FD89F3AE1ABEB515FF8DA9275CD6150BA9B63394547F94AC04FE9C1DCF5ED39E98D740327611F9BDAC63F1CC4471CF218186919C9F41477E0527C1FA451CBCC7319EE4F9E643AEDFDF6A909FDBFA3657BFF212ED3F9ABEB33B6BCD03C61B7576DD7DA11CFD9DD65ACFB56254FE17E51643D33B76EB3F7B8EF6EED4FE3B9E35A077E1053BAB3175CC3714BDAE766126F99BE399CE8D5ED7CDE506444581F877D45GCF812C81588CD0564E4F365D5BBDA4CDBD981264AEC87AB153F12357383EBBFB7B7F69B6
	3E774BFE7488497BE2FB4A7BE4D5BB5B3FF63D2D7E5D834558B27D7EA5517C8F34AC00E5G2BG568394997AFD32DD2F5F71380653D9888F529B33F178C336CB20B1F1321CF82C8E73F01B08F7A2AE771848652E784E4324E2FF33EDCF8A39F7F4B0F9839D09F8CC78C5869EB77A773D266FBC6F5D4F114F5DD59E4C669D4E54F5C217533ACE3860342EF7FA1C335703D71C295F91871C66F7C466B373C6B0AF45E0DF66816482AC87D853417D656F4E7FB2FF3170150E48G56A9782B054679174A29F55DA9277F
	2D63AD995FEC7AA4CD1306F1FD0E2F10F13FB4C926C87D4DC7C69BA95101C10D8D557107E44C6FB1311F22191E6B14CAE88BC12F9D9F66BBBDG1DG3DG9A40BC00059FF21D1F142B213DA4DC3D393285A4BB44285E0A9065EF13D9A329753EFFA43D5E8BC7687B95489B1A1075DAED4988461C545F2F0A06FD9F99A9B3F02755EEC90DE291273FB9AA29E55828241721C831367D2B4A4D36372B5636F72AE65BB35CAE152D2B9805B543172BF846315A6EA3A1AE8CD03F7CBFD0CB878804B04A7C008BGG3C9F
	GGD0CB818294G94G88G88GDBFBB0B604B04A7C008BGG3C9FGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3A8BGGGG
**end of data**/
}
}
