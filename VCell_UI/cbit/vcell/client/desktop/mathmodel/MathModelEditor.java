package cbit.vcell.client.desktop.mathmodel;

import java.awt.Container;
import javax.swing.*;

import org.vcell.util.gui.*;

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
	D0CB838494G88G88G580171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BF494D516C102C60890B1E000C442C70C9AB50EF1A73A1C33B8434CE4472C036730B34CECF6B7FB9695574CEE9CD9B76EF04ED4BA81824DA7E2D6239346284C8CE89B9344A50B2079E248C73350362C06F4D73AABC91B7E253A7213GFB5F67D6D5F7D7F7A2AA67DC2A6B3EF7BF6F5E7B6E3B6F3D0AD079EA7A7692D325A014EC97783F63A502507801A0DC2EFE3A90F1C9BE1BD9507DFB8D60F941
	F3218C1E6950D6FC274DDCA16CD54A61BD9B5ED75FE1B33F843FDF908EA81B5F079FD1642C05B63B7D736258F2FEAB201C1D74FDF8EC8FBC778164G0EBC4B7CC47E6C31AA0E4F67F8049213A09452BE370CD5F3DCBB3CBBGB2G567AD97F0D705CC5A54FB951407B755E18A1EC7D29EC5ACB7868F8A29C8BB05A737CD9A37CE709B0C2E47D046AC9E81220AD97008C3ED4383761FF60F932E7F87083E73FDB299DF3CA9ECF2D536316FCCA2DCB8A8EAB7EC02DD7D4063CFE1764B9F1229BFEF6131FEDAE376217
	01F60A725FBFDECE7989C2AB3C174B8477081D48DC876F1BG0AD47CA28741F741EF9740140C63BC756713FC9C67AFFEDF58BC7823DD7B230CB3D9E663DBABEB632CF9333F74E56A1377CD44764550D69D34196BG2CG8AC0B2406FE9ACB4173C894F26FE25456F757AFDCD01C03347A55311416F93A72005634ECACE3F6C9284666F3F99339868938B985D5FFDEDBAE6130CF7517719E76F96726FDBEC5AE9E0137C0C0B264CA033457A20E693A63BF2BC1A6C4D4150588C157D192017ADBCF9F467F6835955BF
	BF3A3318735923E832CFAA44574D54579678DD426303613BA8BECE07E7F13A11468CF9CF02362D8A464679FD28CBD759CA21227EE6C3BB247CA051F4FE146990988D576514C8F8270EB2197F256A52C3719BF4F8264BB22AA3F9AF9B331933GD8FC351611F95BBBEEB3CFGA481E4G6482EC86A89E47981BD7724E8CE32CC7141566D13747A5498223EDB53D88CFE9C21145B33E20A27A1C12CD145DE23FC78A8A3CCFB735C1FF885DD99A7BCE1071116413E4D1F17BFD40DD74CA0AA487D91FAFCC4F13BE32C4
	DB2DEE2F84823C818179BACDAF4053A38615CF83AED111349E2CFD58C4FCB2A65A00A3B0005F4CAE6BBEA4762A1A00B98750B801F6F81C726B14E49F49A0DDFE7F70E840CAC6A2A4CD90BB3FC5F30795FEFBA65046EB9B89EEA33C972AFC8E169E5071E9F5BB097A22BC09F32C5DC1F233B009B1732F67B1E6DA775EA5D87EF611E14CA4C17CD6CF32D8B9B699BEF74ED21EC4AF767EAB7B792FB90F43C118B41EF3DA2EF8669B4BA80D51466C7BE6B371D5AF68B28110B209B6BEDFDA43E236D98CCA98251F4890
	E012CB33B85333C5BC4CE5ED673A1385132CCDA4EF3F8170C74A134DA1C6E323B4D9A0EF930070154DFC982087E082A09DA0972088206AA25AFC05A3DDB7AE1435AFCC5EBE1D2CE8E3C71D3E09EDE37BAEAF2A2E3BF47A4EC4577D145FF723AB737547E5C406B486CBFD2B9410FCAE789164ED1F1691FDF97E99702BB4DFD0FCG72D58BA408200A974B082E2222486E7ED1C5BAA7A90A5BB728353B292C60E8FFC0CBF5A8ABD0C66C649F07FC25769FADA3F35D697779A49AF4AA89EF7FB14DB512C7720252AD7E
	D11F4270F99F1279BAACCD72DC3761439D8249D51ED112B7D94D13A592BF84E43797C21924DEA169A2DD2B462BF4B6EA849C295DDC701C3808B9E57C8D4CA9073E3ADDD8E78F180C564414DB9718B6DDE4733C702216D318CE8FB59A6B7E931A73B8BE48F47D675D44EE61D5971B434E9642E74F144DDC8350B005F338250934E31E641337456F71B0D32AB62EEB2EA0BE11C6C6A5D8DEC8C26ED46DF63C79C0D81B0D5B1035FF58CCEC6851657242A9E243A491EB5A9EF89F82C818E2F64C1D6276207D2C7D582F
	61924C7FCBDA3F40A5FD3FF2A776CB86FC262EDF9E6F57F01974DB2B76AB84BC49AFE517D93F4A4B7AFE0A8B7B45DFE6B9A815774B8C69E711305F064BACCFED617D0AAF6B755B2C762B3EE2B39F8330DEE17D122F6879E58CE03F6CABAC4F6571FE793CDFEB9C69679844FEC29CEB2F0AE3FC6B62747C6A0730DFD29C4B17993C7FDA5E0F454FB3C7C8CC537A7ECBF1CC1C288D0E433CBCF12223C70468E035FBA6DDFF09BFB747E10DFECD9F092D6AD9A09B20EB96467F23FB31C6CF9E4E90721F2C5F99EDAF62
	1A4574C81825453F2528E9870B5D3BDA094CDCE85BB48B637641E6FD1D50948C7A1DEEDA22E08EE9514F8F0673356C066700DB8EAAE7A001CDE0FCAAADC4DFD243F0B42F699E20359E31CDA148BDF40D4D3C7E9A024F32D46AF46F85DCA2C096C0A965D753FF1464B7BA01EA5923C712877C32D7F29DA6162D954715DA1ACBEB1BE8F39B79FD847D201CA2FB506010FF3CEDE4148EAAE8F3CB6312FC7873A3B6DEEF2FFC21114B4F3BFB25F072CA1F49E87F1330FF4834E7B62FD7E6EB36EDDF58DF5A8B4046F929
	9F479987FDF2E663B8CFB90C46D9B49B7703174497391E9FC9FEA89A65C91D1A5C0675CE64DD96EFB31F0CC75E59CEA35ECABC725EFA6C854E7B1C5BBB6AE1D6E00CA95F47D43E89C033AE9EED5F65A2B9DD14876B991F27CF11189E1494B0A5AC8C0A62771D13C8F283D79C791C59723787511607DAD78B8DDB078DEDC920B81E5BF00EE64B4C3D4FEDDF874074CAF321DE816813B18775AA13C275BAF370A5269718AEC2ADBE46CFA564D5GFC2A2EC5DEEB43F81D7D1C0F91CC950CE4C6F96D9DC0DEBD40A761
	DA7443EF8CFD3CCE15258CFC33D8DEFE78G77232D253B0BADC97A78F8D455EBA3485CF39D4AED988C9DE35181BEC642A64A982F9F447DE0AB7019D2F995D13C5B67D65C22477D15F47888433FB2C4F2C41033E131F0579F74732ADC1CE3988B09CF8E1A322FE3B110FF1D968BD9B027562B736A73A19C5B21841B39B781E33B72837224F50600F65A6EFC196A229B5FA73EF38A949C23819A913CE63CD75D1FA7A7B03999895A9EE1AD7C5EG30D907E37B56E7E90EAECE885FDB8BF48FDDFEFD785EBA218FEDD9
	85EDD35763DE2B60B471C3813CEF83281CEBB3DBG3AGBCF3B1B7EFEBF9D718E6AFFB4687DBCC1F68B997D26471FC7C3DD6A2976AB27E7E972DFAFBB1BB7C1598FECE11B0B7F42F34BA6C1D1F49C45055063D670651B17FBCD7C576835A595760289B96C62B5F6FE996E52BFFF050A3B12F717A7038DDDF9F964CE57B5572F9B6F3B5GDBCB1F3060DAFAEC9E2E1917CE6B574C357BAF194CD1F211E79E53B3F11E96FF1B3C561D4CCFF35B491A1F87EDDBG04C4108B5088508B9017083EEAECBBB51D2F680436
	7ADBG578EB5DCCBF81D2D5AF6F67F55796446883AF01F24A70652B17BA98E34DFCEA26E7F0EBB2FCE0736883AF09D0A8CF5F84A09BA145F00BABCF515BABCB72D8E0DB70452B1DF6F6EA03EBE8FED8AC0A2C086C09E409600BD735157B735B5CD67EB7578E7A67EDE653A3AF15E9BC197BECE4BFCA33AAF242B13670E208B1757EAA86F05012B1377DA84DD383C5E79C631F4C444D8D266E3AC7D62AAF5F8ECDA9DB28D473CE0F03A33E7E3F9E953522D1FEF943B33BAC96C66C3DBB1C04582D8CFGFAGBCGC9
	8BB0F69FEBFBFF3A5865F90AD5A111D14B74F48CDD1DBDFD91F461764CDA90CA474EABF369FEA9975AB61371D0DCCABF4195437B1EA4440D3A882E8E5E5BD55CE20960BC709E2F622E25B9AF855E3312B0C6367511DAE93D7A3E43CE5EC99D475E6777FD53B34288DFA6E9E3E5B2EE7B56E5684FA5D98CBCF9BA7A99D451E963B328E7CF9B1FC1EDBB7DBC43838F7D09BA3617FD947B0CEA4FC7C6E7D487295EFF7D62FE4ABB981402B4A8DBC5C564EDCFA8649CAF622C6AE3483AFC4C2033380B77E9D37CAD247E
	E6B508634AAB02617E1C17A118C77C76F2CED31004FB7457687DC4550DB6F39DC07B0D989B05960CEB470D1ACF957EBB811E29GD93AB6B6EF4B3B493EE1BD608BG0A81AA96826F05B8DFD7FEB4ED8D58B6912386CCEE0F6E6BE56D463EDE51CE4E97027ED159A9057BB3233DCAED53F24399C572925333BE77D2FA2839E11B91CE1FC369D9DBB47A075A23458C736B037DFA3F3E2B4E1DC9877A20E721E6E7977FBD814FC400F4DD9B6367F36A799DD3799529F5C4360EA6177F4E07E791C059CD617CDEF719B9
	3F1E81596F0548E3632A6C086E0B5FF7987B62350E683EB85CB13DAF6ABB227B2221E3FADFB4F54476452F42467AAE47AF14C27147B83EE3C0EF6BFA5556356AFE22CAE7CF8B7F5D88CFAB40D0042D778D064A2967789F8F6A64F4572BE31E57995D878BBB0DFD1052995D874BBB2777C1C6E7F49F5C53B93D8FF2BAE37B606621D09B3C47717FB8CE6486E4E9C012E549F58E366CA416EFA2F92C182E27D370BB61A65C5F5F53CF4E6DBD6224FFD401F913CC7A9DF2627E2B1850812E8120AB196D4378778C63C4
	7EGD43C2056390064949A97915AC49752E6C27FF5G85G823455ACC219F751B8710A93EE6F2817EA48743BB8C074CB00FE49EA5FB4876D6B7605753D1F2E656B20DF1E5A7706DAF24E2E1E61734C4EE2CD3113D8ABDEE4FCA738E7F1A41E6D71EF25393FE6B15B636FD77177D3FC3B8E4F767EFF26F83A06C1DB61E22CBDCA5F46DAE668E6445DD3CA7D816F09AAEEECB756B2A9AA6E179437965EB72A38B9A256B2DBEE46F52322BBFA1C3F54ED9C67873B0D5778B73AE32F71EFF5C70BCF26DF1283473BCD55
	79F5870E37A285F1339DB85EEA95773883475B2862ECF69C2FD5456DF429B6D5F187C414B1256202A24AC8D0F1BF97D1C6260A3BDF2D8F572938B691E5ECD4F11ECE1431C5459D6FC699DBD55C52EE64D73584F1E98DA857823846A5645B27EA2B204E6B9B7ADF0527CF9A2FD18F88DB5DDE49C7BEGB8FC342E41D2F3743DA37C9CBBFDDC7F3DD2436EEB8C6F488844AFE1711D421F093FDB423FD76AF26277CA9B202DFCA94E6F47EB091F55FA09A8F262C4331F5CF1F531E4416A25E43A07A7740DCB31DE7A81
	65B9816FC9G99CB71FCE662CFF8BE1339E6253061B163BB0D826BA5533A25CC4F4DCB357319E3EF7617B2195FF3214C1AE5B67379E584D760521F4592DBBA85364FE06D87A4FDBB095DA3A16D2DA16DC45F27CB756DE3A16D5BF574ECEC8F7F9147366D61AC41F3FFFC54EF475217716F0778336611A5A589A9BDBBD8AC1C34E931F051D193BD966A5F2B335440FFC7981D7DB30DCE6A1BC98C31F81CC76755B65025B2D51D9BF60C654329D86B5E69A0FBCDAB3C87C8BF009C00CDG45GD5E9586FAFE9BFF20F
	F6B20D5DCB762631FB4729B4F62F487A3D6D88687891BBECCAB54E4DB9D170F41F98230D68C4573054707DD697150D5F8D903CB98A0FDDD17048ABDA9BFEA731D707E36B16052E832920DBC69A2EDB33BB491ED301644CFC3900EC8705ADE9247F8D3BF1BD5D1896B9D6C61F5DE03C1E1A4C540F3700AFG986EEE1A4F0660DDD1F18F503C178E6F1937E0CE7A0F7F53F4D86AC09DB65D92298363311F72AD0476E2156F959AC70716435EE6B9626A685C6D05772165A86B15AFB4D9F3C514153CBC1A2C5FD1D999
	04274A779F68988A603DD04579E83F2AF41B79D0BA4ABA552B497A389BE57524C71375C31A43DD509EC8C73EF9B4072742FBC6BA66361FFD214F4595CBBDA6B2770D667FC6AE2BA0DD4BEDECDF784E854237F28558EB852E692275ADF85A44AF79AE105D8251EDA0A0FBE4FFC01215C90625DF8E7098BF9BF19EBA9D70B31E2B24DB723569D89D755F744E2FE7852B23CAD47C07945F2B433375AAD7DD2F0620ADEF855E2DB87A08AF6BD6C21D8A60821882C88548D909363C43DAA7CC332F367A83DDD0C0F9F4A5
	8F9B5F77A34EDD278396978F6D44F5A17B41BB041CEBDB235EC9642D645FBB70E7CE5E129272E463FB065E09D42F02DA9C2097C081C8GC8DF05637B0D554246C74221C9D1C467903D414741119BE68DCD0AF616BFD65A092E839E3F2850AFCCB0769F3463BA1B8DB272D7915C2F6DB5B1461C3B0A694E78BE77922947471CDE0F7E1207351DBCCE6EB67174651C84152688A1FC94E9175410B1055D61075C95716F855EB46473AE72F92A062C5BF8479E294743BD067475C87F190D5CB56B6E51C3F4E07740EF05
	FC53F0E8B53B876EDD0D7744B9F62376005A7E0E5328BDFEB5662C9522205AF4F5E90C353D7EE89DDB5BD91DF45FEEBC4BCAD90DF9AA77023ECE29F96B9343BA257842D7268D2B19AF372E565D57BB0E6DE2BA6DF2EABAFDA54EC4A7C6576C52681659E7CEA7C99A5DCB0E195BE01D83EDF078D634E74200462B73EB68F06720C657B5A39D985DA3C39A5D4E18362A8B51BDCBC45D9D2A6EE3DFEA3C6E0829C3DD088EFFBF2C515D9BF34C21F402D72393BBE72E3B522D461E2A7B9BBE0D574206196BF05A2F516D
	334C040E67CCCA47F2632CDE72BD2A61BA8A193183F8FD527F2564D42C6220CAF36C8209FBF50F4B16D75E767E85B2E7556FA2E8EEB8879DFD03EC3DB8C9631C5F0F6AFEBF627C364F7AA35762A73ED3997C9B313E68E78D01BE63330631BE633306293E58778571FD513FE91D57277F261579DD98A13E59GF1D788D031866ABF0006B538CE3EECFFDD206B24419A59669AA4E7FA64AF8194753E439EFD2C250E697FFEE00F03FCBB576F07A543CB78A37E000378CB7187A8121F53B8080DE4776010423A62B98D
	4DE39EE9002259DCF854CE46BEA0FA025A7D47EE87B94FEB1148B7A2D8030CCB18732B6FDCA5D43F5AC87FAE412886312EE139BEEE0D16737789F80732230354976CBB8E3F37565D6F2D9D9D73560A7DCA2D8453C93BCCD117D1168B7AAD78B7CB59EB707B0DFF33A32ED0458DB891F768B65CA7666C5657DD3D008F5C96FD4F2755CB8BE4D2AF45C35FF40075G79GC5G65996A3DA87DC6BEDAED4D0EB9E8AC48225B27B0FDFE64506BD38D3C6AB2E26B43686E8F79F62695E84E4F08EE32DBCF67811A38E968
	58584AC722476C7E1169E336EA04FE838A31E61D8C70B36903A3A4367C8383C1C91D9B2F0FD42838B3419EFF50CDB2A173413F84EB60198C7AE681EC84A88490EE37192BGACG2D3723AF32C7E279029DBC057BE250922BA6F75C9EE9A7C637559E0BCE09CAB79CF30F13E0C0474ED3FEC66B30D4E87FDF9537116232B90E5FB94851FD36DC1E5EE79932F11E39DB0E4CB3774946F96681D91FE7182E790EC87D7F4921571F61724448FE0F0A117D123AA37BAD6A0E34473F87235BE3DBF0FAFBECD70C6DE1D6A2
	6DD121985B63C5C5EF8FFEEF47794A6A65B33F37D3C8AD4D6ED23AC51FB8C857A4B63E4B96BCD35BF0BB1E331955336F2DAA6E9AA7622A6E4033D871CF3AEFB0BFF33B14A1062F1E582B615BA5223B00E79B0BAC7AEF68420175F95DB1FD1FA54ED8FD585F7BBEDB5A860BC9662F056DC1D9B890D917B54D7F65AFB26D2D15A641F4C658B1C495BDA3EC9FA7A319A0C7F1A4DDEC880E88178572EFB181750D36A9C2053A952CE1FBC1C5580F3BBA95E3566D53600DFC8EDA4392C9883B096F604D756D3C8938E69F
	3922BFCFAA1E630CFA36FFB213651D1E4C7073A4E79F0925C440E7E4E21E3D6F425BC274BA781CD774F89451B35C64F3111A38F9D21134BAEC280F708FE29F5E485A7C34367BD2C7737FD0CB8788C1F2C9A8B895GGE8BEGGD0CB818294G94G88G88G580171B4C1F2C9A8B895GGE8BEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF295GGGG
**end of data**/
}
}
