package cbit.vcell.client.desktop.mathmodel;

import cbit.gui.*;
import java.awt.Container;
import javax.swing.*;

import org.vcell.util.gui.JInternalFrameEnhanced;
import org.vcell.util.gui.JToolBarToggleButton;

import cbit.vcell.client.*;
/**
 * Insert the type's description here.
 * Creation date: (5/11/2004 4:48:35 PM)
 * @author: Ion Moraru
 */
public class MathModelEditor extends JPanel {
	private MathModelWindowManager mathModelWindowManager = null;
	private cbit.vcell.math.MathDescription fieldMathDescription = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JToolBar ivjMathModelToolBar = null;
	private JToolBarToggleButton ivjEquationsViewerToggleButton = null;
	private JToolBarToggleButton ivjGeometryToggleButton = null;
	private JToolBarToggleButton ivjSimulationsToggleButton = null;
	private JToolBarToggleButton ivjvcmlToggleButton = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == MathModelEditor.this.getEquationsViewerToggleButton()) 
				connEtoC2(e);
			if (e.getSource() == MathModelEditor.this.getGeometryToggleButton()) 
				connEtoC3(e);
			if (e.getSource() == MathModelEditor.this.getSimulationsToggleButton()) 
				connEtoC4(e);
			if (e.getSource() == MathModelEditor.this.getvcmlToggleButton()) 
				connEtoC1(e);
		};
	};
public MathModelEditor() {
	super();
	initialize();
}
/**
 * connEtoC1:  (vcmlToggleButton.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelEditor.showVCMLEditor(Z)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showVCMLEditor(this.getVCMLButtonSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (EqunToggleButton.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelEditor.showEquationsViewer(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showEquationsViewer(this.getEqunButtonSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (GeometryToggleButton.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelEditor.showGeometryViewer(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showGeometryViewer(this.getGeoButtonSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (SimsToggleButton.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelEditor.showSimultionsList(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showSimulations(this.getSimsButtonSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (MathModelEditor.initialize() --> vcmlToggleButton.selected)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getvcmlToggleButton().setSelected(this.setVCMLButtonOnStartup());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the EquationsViewerToggleButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JToolBarToggleButton getEquationsViewerToggleButton() {
	if (ivjEquationsViewerToggleButton == null) {
		try {
			ivjEquationsViewerToggleButton = new org.vcell.util.gui.JToolBarToggleButton();
			ivjEquationsViewerToggleButton.setName("EquationsViewerToggleButton");
			ivjEquationsViewerToggleButton.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjEquationsViewerToggleButton.setText("Equations Viewer");
			ivjEquationsViewerToggleButton.setMaximumSize(new java.awt.Dimension(120, 25));
			ivjEquationsViewerToggleButton.setMinimumSize(new java.awt.Dimension(120, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEquationsViewerToggleButton;
}
/**
 * Comment
 */
private boolean getEqunButtonSelected() {
	return getEquationsViewerToggleButton().isSelected();
}
/**
 * Comment
 */
private boolean getGeoButtonSelected() {
	return getGeometryToggleButton().isSelected();
}
/**
 * Return the GeometryToggleButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JToolBarToggleButton getGeometryToggleButton() {
	if (ivjGeometryToggleButton == null) {
		try {
			ivjGeometryToggleButton = new org.vcell.util.gui.JToolBarToggleButton();
			ivjGeometryToggleButton.setName("GeometryToggleButton");
			ivjGeometryToggleButton.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjGeometryToggleButton.setText("Geometry Viewer");
			ivjGeometryToggleButton.setMaximumSize(new java.awt.Dimension(120, 25));
			ivjGeometryToggleButton.setMinimumSize(new java.awt.Dimension(120, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometryToggleButton;
}
/**
 * Gets the mathDescription property (cbit.vcell.math.MathDescription) value.
 * @return The mathDescription property value.
 * @see #setMathDescription
 */
public cbit.vcell.math.MathDescription getMathDescription() {
	return fieldMathDescription;
}
/**
 * Return the MathModelToolBar property value.
 * @return javax.swing.JToolBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToolBar getMathModelToolBar() {
	if (ivjMathModelToolBar == null) {
		try {
			ivjMathModelToolBar = new javax.swing.JToolBar();
			ivjMathModelToolBar.setName("MathModelToolBar");
			ivjMathModelToolBar.setFloatable(false);
			getMathModelToolBar().add(getvcmlToggleButton(), getvcmlToggleButton().getName());
			ivjMathModelToolBar.addSeparator();
			getMathModelToolBar().add(getEquationsViewerToggleButton(), getEquationsViewerToggleButton().getName());
			ivjMathModelToolBar.addSeparator();
			getMathModelToolBar().add(getGeometryToggleButton(), getGeometryToggleButton().getName());
			ivjMathModelToolBar.addSeparator();
			getMathModelToolBar().add(getSimulationsToggleButton(), getSimulationsToggleButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathModelToolBar;
}
/**
 * Gets the mathModelWindowManager property (cbit.vcell.client.desktop.MathModelWindowManager) value.
 * @return The mathModelWindowManager property value.
 * @see #setMathModelWindowManager
 */
public MathModelWindowManager getMathModelWindowManager() {
	return mathModelWindowManager;
}
/**
 * Comment
 */
private boolean getSimsButtonSelected() {
	return getSimulationsToggleButton().isSelected();
}
/**
 * Return the SimulationsToggleButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JToolBarToggleButton getSimulationsToggleButton() {
	if (ivjSimulationsToggleButton == null) {
		try {
			ivjSimulationsToggleButton = new org.vcell.util.gui.JToolBarToggleButton();
			ivjSimulationsToggleButton.setName("SimulationsToggleButton");
			ivjSimulationsToggleButton.setPreferredSize(new java.awt.Dimension(100, 25));
			ivjSimulationsToggleButton.setText("Simulations");
			ivjSimulationsToggleButton.setMaximumSize(new java.awt.Dimension(100, 25));
			ivjSimulationsToggleButton.setMinimumSize(new java.awt.Dimension(100, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSimulationsToggleButton;
}
/**
 * Comment
 */
private boolean getVCMLButtonSelected() {
	return getvcmlToggleButton().isSelected();
}
/**
 * Return the vcmlToggleButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JToolBarToggleButton getvcmlToggleButton() {
	if (ivjvcmlToggleButton == null) {
		try {
			ivjvcmlToggleButton = new org.vcell.util.gui.JToolBarToggleButton();
			ivjvcmlToggleButton.setName("vcmlToggleButton");
			ivjvcmlToggleButton.setPreferredSize(new java.awt.Dimension(100, 25));
			ivjvcmlToggleButton.setText("VCML Editor");
			ivjvcmlToggleButton.setMinimumSize(new java.awt.Dimension(100, 25));
			ivjvcmlToggleButton.setMaximumSize(new java.awt.Dimension(100, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjvcmlToggleButton;
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
	getEquationsViewerToggleButton().addActionListener(ivjEventHandler);
	if((getMathDescription() != null) && (getMathDescription().isStoch()))
		getGeometryToggleButton().setEnabled(false);
	else
		getGeometryToggleButton().addActionListener(ivjEventHandler);
	getSimulationsToggleButton().addActionListener(ivjEventHandler);
	getvcmlToggleButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("MathModelEditor");
		setPreferredSize(new java.awt.Dimension(208, 25));
		setLayout(new java.awt.BorderLayout());
		setSize(504, 49);
		setMaximumSize(new java.awt.Dimension(150, 150));
		setMinimumSize(new java.awt.Dimension(208, 25));
		add(getMathModelToolBar(), "Center");
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
		JFrame frame = new javax.swing.JFrame();
		MathModelEditor aMathModelEditor;
		aMathModelEditor = new MathModelEditor();
		frame.setContentPane(aMathModelEditor);
		frame.setSize(aMathModelEditor.getSize());
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
 * Sets the mathDescription property (cbit.vcell.math.MathDescription) value.
 * @param mathDescription The new value for the property.
 * @see #getMathDescription
 */
public void setMathDescription(cbit.vcell.math.MathDescription mathDescription) {
	cbit.vcell.math.MathDescription oldValue = fieldMathDescription;
	fieldMathDescription = mathDescription;
	firePropertyChange("mathDescription", oldValue, mathDescription);
}
/**
 * Sets the mathModelWindowManager property (cbit.vcell.client.desktop.MathModelWindowManager) value.
 * @param mathModelWindowManager The new value for the property.
 * @see #getMathModelWindowManager
 */
public void setMathModelWindowManager(MathModelWindowManager newMathModelWindowManager) {
	MathModelWindowManager oldValue = mathModelWindowManager;
	mathModelWindowManager = newMathModelWindowManager;
	firePropertyChange("mathModelWindowManager", oldValue, newMathModelWindowManager);
}
/**
 * Comment
 */
public void setToggleButtonSelected(String whichButton, boolean bSelected) {
	if (whichButton.equals("VCML Editor")) {
		getvcmlToggleButton().setSelected(bSelected);
	} else if (whichButton.equals("Equations Viewer")) {
		getEquationsViewerToggleButton().setSelected(bSelected);
	} else if (whichButton.equals("Geometry Viewer")) {
		getGeometryToggleButton().setSelected(bSelected);
	} else if (whichButton.equals("Simulations")) {
		getSimulationsToggleButton().setSelected(bSelected);
	} 
}
/**
 * Comment
 */
public boolean setVCMLButtonOnStartup() {
	if (System.getProperty("java.version").compareTo("1.3") >= 0) {
		return true;
	} else {
		return false;
	}
}
/**
 * Comment
 */
public void setVCMLEditorButtonSelected() {
	if (System.getProperty("java.version").compareTo("1.3") >= 0) {
		getvcmlToggleButton().setSelected(true);
	}	
}
/**
 * Comment
 */
public void setVCMLEditorButtonSelectedOnStartup() {
	if (System.getProperty("java.version").compareTo("1.3") >= 0) {
		getvcmlToggleButton().setSelected(true);
	}	
}
/**
 * Comment
 */
private void showEquationsViewer(boolean bSelected)
{
	getMathModelWindowManager().equationsViewerButtonPressed(bSelected);
}
/**
 * Comment
 */
private void showGeometryViewer(boolean bSelected) {
	getMathModelWindowManager().geometryViewerButtonPressed(bSelected);
}
/**
 * Comment
 */
private void showSimulations(boolean bSelected) {
	getMathModelWindowManager().simulationsButtonPressed(bSelected);
}
/**
 * Comment
 */
private void showTestingFramework(java.awt.event.ActionEvent actionEvent) {
//	getMathModelWindowManager().testingFrameworkButtonPressed(actionEvent);

}
/**
 * Comment
 */
private void showVCMLEditor(boolean bSelected) {
	if (getMathModelWindowManager() == null) {
		return;
	} else {
		getMathModelWindowManager().vcmlEditorButtonPressed(bSelected);
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD4FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD4DCE5F6699657EC45B676D09BD3CF97E35632AB3AF145B6FAF2DC34314D2E59DDCBF1A5FB62EECECDAD2E290DEDF431621E4770BB668F930C2838E20D8A89CE0604A404C004GB2A490C69888901270E6664D4C8BEFFEB273E006182C3D5F5FFBEFE65EB098B5675C3C796E775D1F6F5E7B6EF73F6FFBF0D56F261796992AB82E2814237F0E94F1DC7BF10EB33B72D6B0DC73C7B9C6CE736FBD00
	57384F6DA57034C0DF3AB347D8497D6DEC8534A7205D6748B13E813FF7F1374FEEB886BF9248D989FD6F5CF8DB4D5CF26CE5CCCEB9EE5FB736951E5B81A681071E199FA179BF9B2B2678940AE7D0E4603834BAB4A6FF2C0662B641188A40D9811CF5E47C95F892852EEBE92663A6EE4A60B67FB4E8580678E8F8B2D8C7E939FA722C657E37A8239E49FAEDFCAB25E906F6GGCD3E183B2779A8BCDB2DD36E638E3BA81BE69C02A4199C12A878E413D388CD497E00494B4B9E2F5FA9C8ADAD7D70339F7D6CF60A32
	BF8834FB76A27EE36395189F47ED845E4BB16ED9A71269015F8BG966FE5789F89883F99FEEF5D17E35C330F4D336D23D6BA0F514F7F014BBD60AD5911E01E2D7B487C1C7B54F9EE141EB43C09FD32349C59BE9D7AD6816481948254B46498DB815E4631707D323D706C304B1DFE2F576F6B8884CEC84EA01E997CEEE9019E0A9B949C7E201363083F5F98EF5051E7AEA0F49FFCE9BAE2130C034C771903F7F36B6E3D59D02EE313F5991F9BEC8D4496D203EA93A27BFCC259298D513199AD7B8C27155D30767162
	D29D5916FFFA3CB81D724952485E5000FCFD817BBA97FE975178A0F891636BBED671A4CE1F47B1035A4D50F76B47ACB6C637B3DD7ACA16F2150DFF2DEB07458F279AB89351215A942B4B388B716EB291192FAB3AF8B1BE224193DDD6E09DD1BB937AC6776798C9FC5DE9C46FED09B947D88B508CE08588G2482241BD90C7DF7597EF94618158F4AA726C549A984B9C2FBE779EEF88A91B948777AC2B26FF388A3FCD0646D12906268182CF2E483FB945D7DB8769DA023C77089C1DE967DBE604EFB85D988064818
	074BDFC3E302826E3509DE8184F8839C637B5372DDA80E7810BC9CF07232200EA07DB96548A7B37C88F08486701B58657EAEE42FEC18FF9EC03EE2077E8A446F149074218C526777CFCD87ECE8A6DCF3A33273B44E9D05B03E2A1159783EEE043B826D34C646A725E22706CF1768C06A7341D9760E89824A4D990DACE61E9BE5B153356DAE2E61713F520D1915903F2B9AC92C2CEB0CFDF77C18A7520B34FF679C7D126FF1ACE4B46A3FF3EA2E28943E2A0C62B97A08FD5F6FC13EAAB9GB18C50FE005978479535
	A4E6CF70A101C569C09082CCF02A96A7FACE3A6A282CD22A7B96G34B6213C7DBE408718A7F90788CD88530C023C85G99G0F8194A0BD8E6698ADG810045G5987194D9F9452B573E2323647485B2E1115E86ECC272FE25B39FD77CCC2DD0D9AFD67236B8E4C6F1B5115787ABEA312A14C40D25FA584841F93FE04E85FCA7C8E517C6372ABB48FE3FCG72D5A7A4081002DFEDC43A72329C946D5332B0A4483268F32B7D0FE1D921E9FBC0CDF5CC569338967107A1DFA9635799513B6E707BFC828EBA0504767F
	98679AC1923C20F427FF5AA7937CA3DD68FD1D92E6E92E7BC9D79907F215B40D72A629F91673488F01206805D0C62917EBEEC2F9A8F53CDAE323FC08BFD43BED053E5AA616D3426F331C32677277396C8B81035E1A3878FBFFE1D850C45E7334A6B527901D9E685657BD9B67BC0A8F91DD1FFA9559AD366AA26F7004857149827E2B8156B731F73875D3544F72A4FDF9BB7D12C4CC2958383E279279C438B4AD4072029272A945EE661E1DB1FDA35406243F398759D052647234C34806F739D8CDDB00EC8AB873A1
	E24740A1EA8FBC6E121B0DDB8F6D824D386A435AF19FF85838D6405B8E2B63E4BAEE7DE1B4EE2DB2AE8D5AA83FE49EA663D6C60D3BC9E4639A0E109C54F5040C339D510E6B3D4846C50E10BC35080ECBBF22552FE80A0DDB8578F5G05F4DCEB33165FA34A3809E6126764E6B2AE050E5B580C46DD1558388C5A1F5DCC7866C571BB6DE5631A0F12FCE9BDCA46BB0F12F1A4FE7E279E45B42E6FAF72B3FC44948A43FB5952F2524AC3F4105A7DC53C7EA2FF269EE5B57AE7388EDB856DF5G1B0F32787F17ED2CC6
	CF1B4A60AAFE15DB1CE8AF32350568E1EED163BF6B66F0B1095DBBFA114CG74ADE8E1F1FBEF0F36CE6888057C8E9117A8B4666C966D7BC1F0EE4B2B70F40941105C8B89AC426253EBC17A229A0622E9CD77CBDC6BA15B24015CB5G52B1043FFEFF15C6779240B5810C8294E3FEED6E83A83F619748C49ED6A16872873D02338ED9564407E5934E25268E5C5D0DFE57B3BF48EDE88F9A7278435D17267124C2A322909602F56375A3345EDE3A2B1D4ADFF377D22E750B73863D7D4F429D9EC356B1DA2F9CD3ED3B
	7EDF1FB4EC86A073DC62E6732CEB4DB1CE3632F97A853DF9AEEEE57B412B7CEE2AE70F600722B1B82BD1135A703407714E843A8D8AEF0728473B260D715E5C340B729E923D53923182E6CC789AC446578CB41EB6E6FBD1C4B91D8F3A9B891F675BD0CC3B8599CC898B03AC7BFDC382CAEE600A7AF1E24B175FE1365C53350CEB5EBC25EBCB8469ED44062B5BD4DB16ACBBD4DC8DC074FAC0512B7AB83CE74719DE6FDD0C562B771DB708DEE03AB835689C0397992F0540A7DB6135B6065760B81DA318AA94470C70
	5ABB45F895G9F73A74C8F8F39747C607904493AE96A2B4572F76BF6D2BF0EF4767711A5C99B9F4FA9FADD8119D90A5CE1A9FA0E1BF652B9A2B68966F8074476039B014F1EF646EB1B9761C51FA80B3CA4DE966A6A897E0897650890E5C3E2612EBFE85F2B8A7EBA5DD8C8FD46ED18E8A7B110522E4602A577C943C4AEFB2F42DEB637B5B026240345F655F17444F586476CD46AF8936B22195F00EFC80602E3BA00A202550CAFAB7B73568EA2575A216E911C70BB82102A4111FD6BA3B8C727F7446E2DFF01714B
	BBE27756E7143DF5B6746DB94176DAEBCFA1BFAC0476B20015G39G1B812ABAD9EEFE41F210CB32176D7541965347CBC3D1C59E5B4F76A239D01751768FFB3576A2F6D8670ABD27B0F7C66F157AE25A046E37F1F42618F6A0060E786725EA349FD04F3E5C53A2AC0CB63FDFBA4187EDFE37DB920857E8FDD8A6E86B43051DE43F3A9C1E2BG48DA7A60FE36162E6BE4EB6655CF35EB664A9DD78DDB9264222AAE22E7D31766DC6B3E97691A79277DE84D1721EF91C08640AA00FC00126E9CE3DDB773555113ED49
	FC05DFE81B3F93F0962861BAE36BEC4536696EEB73495DF1F431BE31F4C753917B5DA1B07BCDF6337D1F4DF3EDBA586368E2F5D82C2B4392116930DC5121C23CB69DF64751456A109F238371F571GBE087E9456CF00A6GAB008C3088A06BD3666B54139D49FC2D9C7F4C475F5957B84F1FA71DE76E27FAF4110B57A66F5BF1F43172B66A4AB3CFDD1B3C63F1F43172CAFA74E2294045E2292607455266EB5441A0A553415623C71799C7978B7A729EC8CAA77568456EE5AB0A5D9468CB87D881108BD0GD0E549
	B1B6DBD86C7E7A6431E431CB739429C262231668F955FBED763CA10EAE561E2316E8BAF2DE3973AC5E3BC0DFAA1A8F4685F15ECA07F616029BF0A3DC9E34B7A9380087612AFAF30C8D3D8CB76BC438F6E80F763298F95409EAA5C9E9BF0E5B280EA36DF3DF799CB24E173D6ADC090C192FDD0676DC1244403F1DCAFC86754CA97DB3287FBA25FF8675422957889EF8E8CF54D97FAB2766BE23AA6D53BB23FA876B7D135DBBB06FD0C88861206C62E51E767D070C4E71624E2ACEC35625F35E456DF29B6918EE595F
	096AEFD203CCFE7196273BBF27E5884BA347F1FC61FDC3AF5B23EF42779359504E835824444ABF6EE7F1DDF1D275E98D7DED06E7974028260F3C37C503E85FA081FEA1C0BA408A003C136CFD7D1B3E24B5E0F7E40E9A70467E443ECE6B57777512FEF43E9072CF879DC22CBFEF6B2FD67A545C50AB8BDEE4FAB26687189EEAEE58E644525F0369C9DFA27A877A93458C716B93D1FEBD283CBBF78A4C87859ABBEFBDC56BBAF8B681D8CE29FD045F8D9EAD3FA6055FF71575F5C2C393203FD3603998A0B30E5FA762
	964A4F6A8A7A3D90F9E4DE5B86927B22EAC05F97EF8DA47645FB8349FD31F7A031AF9A87127B22E5E0EEDF7CAEE62E87A97E07972371CD946F1E525A3AD13175AB4AFEA2DBE34FDC7ABB9F1E05G15FD31365EA7C54BE9247827A40D1C7EC6E54EA95644BE38412A6F031B2C09FD30581A5C8737D9937BA0431A5C8777D86776C116B75A8607A83E7EAC129B888AAEA198941CC330E5C7315C077258893C1E6E015F66BE363F7F0E004E6DA5FE56BFAD43FB530F467DDE59FF25433895G6B81B67711FD987D1EA1
	0C643B20628535068202034B4734AEB72335C15B833090A083E0F5BF13F93D8751FA7908681D76E28D09FE43F8BFE0BE1DE3ECBD4D46AEF2623122AFE6EC86DE4BBDB0CED6465EE8C2676C4A99BE4D6CA456BAF11EC8BF2DFFA7182503A7FBFC991FE32EBECD7678BB94FC8A46EF5260495E7FC53CD620F675C00EB1ED00559E3F5A4FEA19CAAB43DDD7067D816DA68577F4992BE55A95DCEFB942B921FDC54539D8AD33484A568D63E062B82F9C540F735D037AEBFC4D605CEB7C1E41C471C974FBD8E073DD2668
	7C2040663BC2413D6BE273DD25603E65E2734D374657EE058A6E6FC545261FB1DC070B4958236016AAB24C8A2E544DE458941CCD3129C741D539190CAB8A6E58901331C841158DB39937AAB8F118714BD6F0F737B23939004B7F8CFD7B747A98273C57EE61DDF87A04F02DF2C058A5FA859F7AG206E00393921764021FAFA0E1D9E56FE2F547C6A37F46F4890B48C1278EE274F425717506F15BEF4316F15A25037FC103D5FCF1B101F15FA89A95252F2420F6E387AC832A075128F5B8C51678F32FA69D64C33F6
	88668CE09DE267B3118F5979CC669DCB394247756FB4565B2E9ABCC3C44F54A175FC0673BEE9A0B2D35DCC66EA6867EC786C582DBD0BC536F4F0E41FC17AB7C67523582D0F6A7F03C75B0F74FD3ED85BEF0D6AAF55501339BD7A811BDB61234BB9693E14045F0ED9EC747BA17ADC346116224053170BC9AC9CBA2B46024FD91BB8969A8F199BEA613FFAC2B7BA2152B5B96793C3A49E4771BB398CF4D9E9E3B17ABD014572E3B6D66BDE46770D05502E9E06F1G13G8BG528132075938BF436350BD5A06E1F2AF
	D9B2C26E9D770C10FBC5B22ED088E878A1BBAC98564F4D138970F81FB8C7DFB66DCB9F0E5DEF3D6D8AF06AF783883FA5818FE382BC6315280FFDA731CD03A36BD6319959368BECE19DE16B5660905AF34A101C09AF971EC33EDCB402463FD2465653ABA371F3A5740F346A2F27C13CEE64815D86G0A436FEB65199CE34D9906FB967B5B82ED5B991613B61DD7F5B86CE2BAACB8932F8361F1A7661B867D698A5F25B8E72F01F61E027B8D4E59A52370FB14495AB5294A3ADF1155BA1AC816893F7FD6681F98E5FC
	2FE2798B211D26605A7038ECE82FD1E49D66D5D96F8CB3D98589E57DA54E61DB47A0CE47985FA7F18E6F0236F50C6536DF1C5566624AA512813D7BFA6F7F15B1A2EB61181A5B483E304E0E782E84FC5E98DB5347477641F3043FC8F701649688EF8381E98D7A83C2D01EA5D87C65G0D710638337AE4C068DA2E11AE742569C89DD5096FCF8B46C89DD524605F447812F195CF562B254AFAD589FD72B83BDB99F3A0DF66C1FBB340563310EFG3AGA64FB2DB5EBEE666126C2BED7EC09F94D0122664A1733BA96E
	1CBD991038F8281C2D8BD98F7C00DB716D2E04F79272D97A3D83FD56BDF5CB91FA1279EDF422792D023E75GA593707E821881AC93ECFE4F0EB5107921F06810E55E6141B778ECF26806D9C523221D640FCB825255A579F999FFE142E27FBB82DBE7A7C0C64A84422D94EA6718F3E00268CE783E74862A47E79CDEC9FB49C37ADA0F203BCDF67AB2A4C02549C3889FE034375422B9455C61C75D9511F135FBF5799CE4FC1E2DC56BB63BE30F5763D12BAEFDA323BFB3026E1AB57768D1BA10FBE0C354B78DEBA648
	BDF049B9F6CF7CDB27DEFF35527FEB0FDEFF43B916331638B84526174B66D85B9B8F18495ACE6A24774B59D9D67BB91627D61F5356A9357B86F46B1402F3178D11F34417371ED373D43E3F1656A9860F2A53F157FCF4A2F447C4156EE7F355A9B1F467AF2AF4774ED5A34758E04245EC7018E24FBF19D2F9793E048E4BA4156E65F969C0687E592B529D1E53D666A85DFF2368DEF11E696E772B3CFA67450B507DB220527DFC4EB9C7533DF4C925B38F4DDF774EE1A576945DEBC2AA2F5BDB672F43A732CA776F26
	7950511C0969C8EE3C4C236FD1F557D1480CA70157007D226010ED3CDB2179580E62DE596312651576B55951BB2BFC9701F343908C743969FDA4E2C35746F7B53FB3DCDF77D9FF7CDA7C50B7AA03FEA366C8FC56A0BA744F9A7C8E7D3306E9475C7785D79D093FE9CDF1E83FE9A5FEFFE99A79A682F117863082E08D407A73EC1D5C697CBF8E2F13BAEBE437530D4E7450DF82484AFD07B371DC8B1D497FFE20D4C05F4E597D30E4F891FF065FA2A0FF497EGC652F39A3C9687C537C7A6C359B98DDE0BA5410551
	64DDC897505CDD3C94D26FBF5662733CCE81FDA342EA103040F2FE4D1D37F3B56F369B103D75EA10427334763820667C6D9C3BC3A9BB096A8B729D075FEB926DDE53740C57445BE513G2F13FA1922AC23A4979C5D4F7EE6E962827BFE63BD016152945C6BA2432D39407609F5655A3A2BE492564D4944FBBE35DEDA98C675D2830C35GC8GA9G0B8116CFAA7722789B79C435B5B96640319064C51FCC7461236A5CD540ABAF09BE04EEC3945DC62061787950D90635F4D5C0D317040E4C2DE8A6F14CD64CA40F
	5957E670B72090EB3659GBD135EBD03E24B6FF20584655DF8FB26D24175062C7E1008B2A17141D311DA94AF206FA8409500B4008C00EC00DC000DBC7345DDB3F379029CBC457A62787E39EA728AFB3C1D885D0742DCF4B589695E0A7B76C28BE69DBAF21E72892E433A207FB3855705F19394C76F9C4209FD36B81C5CE7370575734C5F056373CCE6D8BF4F7CA82C4DB3C45708ABDEFF325F1E08423D2CB30EF3470F3BB09CBF4EB99CEF0F67A2096D71C2A439BDFE9F5137C749EC3CBD0C337A7658B12B35873D
	371BA5FC034A65B33D371BC535B439CB6967FD3C9B2FC9E4FE437B5919DA444E4E595A14F336DB95DC2348F059F6F6964BFF287986730C6814BD84DF93592662AD82521DE3E79B52FE6DB7F431C046580464E3AC62DCE3485F7BDEEF6C0645A473BF3952D0105B99DF17F57C795AA7B2AFF4D599B8C3AFD766410A76F225E1B413882ACBD13AF8ACF4097BA3077E5D0CC0E961BE192BD4360235E4AFA8F3BB582ECE41EC516C532005BE872DA509A40ADD649BE8B93F1E9647566C7AAF3467C98557B9921E6DEFF0
	123CD3600CBDCF8ABAD0ACB589B9C62B40726C43760F3944F570101717A4191726BAFCCED4931F181585358E8BBB907F909BC3BBC95F9FF1EDF7D1C3737FD0CB8788A92D9986AC95GGE8BEGGD0CB818294G94G88G88GD4FBB0B6A92D9986AC95GGE8BEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE695GGGG
**end of data**/
}
}
