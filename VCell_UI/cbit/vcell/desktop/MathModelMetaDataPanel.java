package cbit.vcell.desktop;

import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.simulation.SimulationInfo;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;

import org.vcell.util.document.MathModelInfo;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (3/29/01 10:39:36 AM)
 * @author: Jim Schaff
 */
public class MathModelMetaDataPanel extends JPanel {
	private JPanel ivjJPanel1 = null;
	private org.vcell.util.gui.JTreeFancy ivjJTree1 = null;
	private MathModelMetaDataCellRenderer ivjmathModelMetaDataCellRenderer = null;
	private MathModelMetaDataTreeModel ivjmathModelMetaDataTreeModel = null;
	private cbit.vcell.client.database.DocumentManager fieldDocumentManager = null;
	private boolean fieldPopupMenuDisabled = false;
	private org.vcell.util.document.MathModelInfo fieldMathModelInfo = null;
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private org.vcell.util.document.MathModelInfo ivjmathModelInfo1 = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == MathModelMetaDataPanel.this && (evt.getPropertyName().equals("mathModelInfo"))) 
				connPtoP1SetTarget();
		};
	};
/**
 * BioModelMetaDataPanel constructor comment.
 */
public MathModelMetaDataPanel() {
	super();
	initialize();
}
/**
 * BioModelMetaDataPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public MathModelMetaDataPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * BioModelMetaDataPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public MathModelMetaDataPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * BioModelMetaDataPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public MathModelMetaDataPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * connEtoC1:  ( (mathModelInfo1,this --> mathModelMetaDataTreeModel,mathModelInfo).normalResult --> MathModelMetaDataPanel.expandAllRows()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.expandAllRows();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (MathModelMetaDataPanel.initialize() --> MathModelMetaDataPanel.enableToolTips(Ljavax.swing.JTree;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.enableToolTips(getJTree1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (MathModelMetaDataPanel.initialize() --> MathModelMetaDataPanel.mathModelMetaDataPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8() {
	try {
		// user code begin {1}
		// user code end
		this.mathModelMetaDataPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (mathModelInfo1.this --> mathModelMetaDataTreeModel.mathModelInfo)
 * @param value cbit.vcell.mathmodel.MathModelInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(org.vcell.util.document.MathModelInfo value) {
	try {
		// user code begin {1}
		// user code end
		getmathModelMetaDataTreeModel().setMathModelInfo(getmathModelInfo1());
		connEtoC1();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetSource:  (MathModelMetaDataPanel.mathModelInfo <--> mathModelInfo1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getmathModelInfo1() != null)) {
				this.setMathModelInfo(getmathModelInfo1());
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
 * connPtoP1SetTarget:  (MathModelMetaDataPanel.mathModelInfo <--> mathModelInfo1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setmathModelInfo1(this.getMathModelInfo());
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
 * connPtoP2SetTarget:  (bioModelMetaDataTreeModel.this <--> JTree1.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getJTree1().setModel(getmathModelMetaDataTreeModel());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP3SetTarget:  (bioModelMetaDataCellRenderer.this <--> JTree1.cellRenderer)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		getJTree1().setCellRenderer(getmathModelMetaDataCellRenderer());
		// user code begin {1}
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
private void copyURLJMenuItem_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	javax.swing.tree.DefaultMutableTreeNode currentTreeSelection =
		(javax.swing.tree.DefaultMutableTreeNode)getJTree1().getLastSelectedPathComponent();
	if(currentTreeSelection != null){
		if(currentTreeSelection.getUserObject() != null && currentTreeSelection.getUserObject() instanceof cbit.vcell.export.ExportLogEntry){
			cbit.vcell.export.ExportLogEntry ele = (cbit.vcell.export.ExportLogEntry)currentTreeSelection.getUserObject();
			VCellTransferable.sendToClipboard(ele.getLocation());
		}
	}
}
/**
 * Comment
 */
public void enableToolTips(JTree tree) {
	ToolTipManager.sharedInstance().registerComponent(tree);
}
/**
 * Comment
 */
public void expandAllRows() {
	for (int i=0;i<3;i++){	
		int numRows = getJTree1().getRowCount();
		for (int row=0;row<numRows;row++){
			getJTree1().expandRow(row);
		}
	}
}
/**
 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @return The documentManager property value.
 * @see #setDocumentManager
 */
public cbit.vcell.client.database.DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.BorderLayout());
			getJPanel1().add(getJTree1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JTree1 property value.
 * @return cbit.gui.JTreeFancy
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JTreeFancy getJTree1() {
	if (ivjJTree1 == null) {
		try {
			ivjJTree1 = new org.vcell.util.gui.JTreeFancy();
			ivjJTree1.setName("JTree1");
			ivjJTree1.setEnabled(true);
			ivjJTree1.setRootVisible(false);
			ivjJTree1.setRequestFocusEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTree1;
}
/**
 * Gets the mathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @return The mathModelInfo property value.
 * @see #setMathModelInfo
 */
public org.vcell.util.document.MathModelInfo getMathModelInfo() {
	return fieldMathModelInfo;
}
/**
 * Return the mathModelInfo1 property value.
 * @return cbit.vcell.mathmodel.MathModelInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.document.MathModelInfo getmathModelInfo1() {
	// user code begin {1}
	// user code end
	return ivjmathModelInfo1;
}
/**
 * Return the mathModelMetaDataCellRenderer property value.
 * @return cbit.vcell.desktop.MathModelMetaDataCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathModelMetaDataCellRenderer getmathModelMetaDataCellRenderer() {
	if (ivjmathModelMetaDataCellRenderer == null) {
		try {
			ivjmathModelMetaDataCellRenderer = new cbit.vcell.desktop.MathModelMetaDataCellRenderer();
			ivjmathModelMetaDataCellRenderer.setName("mathModelMetaDataCellRenderer");
			ivjmathModelMetaDataCellRenderer.setText("mathModelMetaDataCellRenderer");
			ivjmathModelMetaDataCellRenderer.setBounds(414, 318, 190, 16);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmathModelMetaDataCellRenderer;
}
/**
 * Return the mathModelMetaDataTreeModel property value.
 * @return cbit.vcell.desktop.MathModelMetaDataTreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathModelMetaDataTreeModel getmathModelMetaDataTreeModel() {
	if (ivjmathModelMetaDataTreeModel == null) {
		try {
			ivjmathModelMetaDataTreeModel = new cbit.vcell.desktop.MathModelMetaDataTreeModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmathModelMetaDataTreeModel;
}
/**
 * Gets the popupMenuDisabled property (boolean) value.
 * @return The popupMenuDisabled property value.
 * @see #setPopupMenuDisabled
 */
public boolean getPopupMenuDisabled() {
	return fieldPopupMenuDisabled;
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
	connPtoP3SetTarget();
	connPtoP2SetTarget();
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
		setName("BioModelMetaDataPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(379, 460);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 1;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);
		initConnections();
		connEtoC2();
		connEtoC8();
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
		BioModelMetaDataPanel aBioModelMetaDataPanel;
		aBioModelMetaDataPanel = new BioModelMetaDataPanel();
		frame.setContentPane(aBioModelMetaDataPanel);
		frame.setSize(aBioModelMetaDataPanel.getSize());
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
private void mathModelMetaDataPanel_Initialize() {
	getJTree1().getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
}
/**
 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @param documentManager The new value for the property.
 * @see #getDocumentManager
 */
public void setDocumentManager(cbit.vcell.client.database.DocumentManager documentManager) {
	cbit.vcell.client.database.DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange("documentManager", oldValue, documentManager);
}
/**
 * Sets the mathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @param mathModelInfo The new value for the property.
 * @see #getMathModelInfo
 */
public void setMathModelInfo(org.vcell.util.document.MathModelInfo mathModelInfo) {
	org.vcell.util.document.MathModelInfo oldValue = fieldMathModelInfo;
	fieldMathModelInfo = mathModelInfo;
	firePropertyChange("mathModelInfo", oldValue, mathModelInfo);
}
/**
 * Set the mathModelInfo1 to a new value.
 * @param newValue cbit.vcell.mathmodel.MathModelInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmathModelInfo1(org.vcell.util.document.MathModelInfo newValue) {
	if (ivjmathModelInfo1 != newValue) {
		try {
			org.vcell.util.document.MathModelInfo oldValue = getmathModelInfo1();
			ivjmathModelInfo1 = newValue;
			connPtoP1SetSource();
			connEtoM1(ivjmathModelInfo1);
			firePropertyChange("mathModelInfo", oldValue, newValue);
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
 * Sets the popupMenuDisabled property (boolean) value.
 * @param popupMenuDisabled The new value for the property.
 * @see #getPopupMenuDisabled
 */
public void setPopupMenuDisabled(boolean popupMenuDisabled) {
	boolean oldValue = fieldPopupMenuDisabled;
	fieldPopupMenuDisabled = popupMenuDisabled;
	firePropertyChange("popupMenuDisabled", new Boolean(oldValue), new Boolean(popupMenuDisabled));
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G5B0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DB8DF49457F596C6584296A0E3710FED719754982764185634C7EDC9CA0FC92C247898BB3451E915D6F1CC0AF3B80EF26CA6249E2D8488CB0030A1A5B6366506E0E1E385019AE39908048416C082C9ACAB71E31B591D516EE8E7E71659D93482813D6F5DF93323554CAE50034E794E6C3B6F3E776E3D6F3E7B6EFBB362363D17332E44350D63CA56F1465FA7A59C376DD30E7B6918015FB0DA16622960
	ACFF6F8336F219574A601983F579DF73D4D4F1D941F2A8AF02F2BB147F9BFE3F49CD8AE6FE82BF9C469120AE764149BFCFBE4EFF3430F1B650F2FEFFA5BCB78396830DBC4BD4B2FED17F8E03DEE05099CADC9CD7C8F9DE6B5FE950DA21FC89B09330D0C57EE5705CC8C79E53F050603B78E0AED77CDD4D350974E3691321A60CED5B0DE7B5774B1262B0992BA6D8E934C907F29E00A8DF4A55A9FF0067214E207F084FAB6935D7FC22AC578AE2A4282B6106068EDE8FF42802A8F708BA5F426BFCA72F08B2973ECC
	7A8CD39B17828EC2F9AD256589E41CC570BB9FD0E4529F13883DD173D47482429A536D7087078C59FB2FBD4995797FE16396875DB2B454E92196572D7AC3EF69EF69BCFCF41C58FBB554D5C5BC953BG2D00GA08370B61D7F7D47BE04E713D7EFD6C3A1D5E98A070F4A02C655035F8D8DD0E3503AC51F2A899C07F3DC9B5CEBA3CFB2E03BC3375D8EED12FB004D77026EEFF085CFCCF6ED3031C9C16EB5D7CE84ED31B89237890E9DF59CFBE5E438BF8E9F3B07330E4D2D5A3FE11D4D58BB1F5F3FE135518F2747
	47BE2413395EA712392E065FA5C63FC8BFCC69E996BA7A6637294F1072CC2863224CB7FAB7B3D95A4BE6F3D5F5D3EC6DB0755B19AEA10AB28CC593E5790C2E4F5CA80E395514251D52D7D868A84BB7291F12B2F745D31189C07F2AEFA5EBF5470027229E508E90G69009C40228166E3570E7D6196FD2C13577423D1C996C40D43367547C91093E33A4637A9911DD7FC220757A45EAB0B914E60B9FC1C5840BB2C5DD16A7BBE9863F3D191B5DE17D485FA67C322AEEA9164B9F91C686453C4DA6B16C2A28C908AF3
	2C5FB3475F04274CC77433E1015745B88756DFB8CE66648A6F019E2183780DF6615B289DC17F550035269D1E277D1D94B588978D8D6D2A9A0C065DC4932EBDC66C1CC9E3C7957C2E09B19BFFF1025032201C9BE37DEC7A7C8DCBBFAD120F084FEB03EC0D7DD12C02E79E7097824A87E1FE86197F7C2C1779CF4B2647393D4FCD32751FAC70654EC1741B40E062BAEA267D93993320EEA1E079A00B7D4B045EA4EB39FC50FE1DA58353235E9C63C7C94758F01B7D9310F83D4ED8EFE87727CF12B9DC83E3D6DE75D4
	6C3D4AEC5F7AF9B57A72D1BEA2B26FBD2D016309C2FCA6D04E26FE42FB4068F323B18E592748A6CA764EAD9C1B33EE4A5B82634800ECC09E2088D0F98DF487DC3C4674FFD2CA3348ED0DD595C67FDBECF47CEDC2797DDB304B0194F65E4A7D33A94B4604BA2667D61B366B924AF6725A4D11D39D4E4D11D6520FF8C5D47496B1ACAA827C0898F547DA09FCC69C69D34DB6A7A9BD8CF1279996FA4424F7359239F9DD57A4EFD4974F083AAEA97EF87DB9BAD6A46A8D47C3969B6BD2AB51CF9D0038E3720B2DE44D7A
	D4C591291318CD0CFABF0D9922AC06C068E6B52A68C88F35115CAAA88E9AB16BF25BFA0E449CB9CA629F66AB8BF9E26730A6054075C88865D6DCA371E4CEF00745C669C0A3F9D7AEBC732E31F8B070BB968FF6DD7DB3EE49D7E1175D5EB6F55E84574EA1DC3B7B066271GE5721E3017BDC0E317C10F202C7F24EC0767D1C9354920F00E5F6446ECF98B1E2144B44B2D09A2A5B03E5A0B6F5871B5C33656CDE6DF2376C75E3E364DD6DE6283F5EC2D17F59079BBC1AF81B0B44456FA5189D24F622C31481BD5D946
	A9043A95C3444EE345ED96DBE58F211DB71B75EF7B2D75BB2FE37DA5F84E3CCE6A57CB2CDE07F26675F8FB14631913A45EC6444BD19136BF32E11CB47DE16549B7926ABC06EFE0FD61C9629B32E52FE939C1463C5F4C330B0C71569A4FEDB7F0EC64EB89B23E2C9B067F984FA586DF67CD4277DFA65F1A9B38A71483FD8760A060DD136F7BB2631BF993778F463748606352885F349063DB86F492978B8D3EB500DA33BF3F42785A53D0AEA18D772334B46CEFA96D2FCEB57530541371F3532C7AAE5598DF55A80C
	3BB523305F46D14807FE7C73BDE4ED51B3C2BFFF050F55C686A0BEB4B41C202E0CE701CDF23941FFF1944B79BFD5092FE5C3F981E069A8368E1F5944F27E6CE0AED7302AEE0353F9264010033BA73E8E772EE9DA0F3EFC6A949933916ABA6FE1FEFC215D1AF7B4C5A22ACF22A90F61A32FF7D857A95236F43C8E4FBEC90B68ED3014E24C1F36F790F9C9CEE4108DFBFC55FC003B1DB3D3FCB1237E570FDB754F5BF25DD561203FE0681DEE51FFE5483D8165F8407F475B12E3F83040F96B7633F9DB8AE3E08E7CB1
	4D01C9ACD98B347A5130CF0FA674FE0944E762F328076FG53A36B6ABC6E5224690EFAAC9C0D72AF9FED19479B2F2E43711E2039B81121BC9DFC8F00F16CADFAE61594C917F8D93AAA566CC13D8B7EF79719C305ACF1372A4AEEA99C29E93738E66D89E2724F76F8D04E59EFB69AF264FFE3B6F7686617AE3BB3E6461600AB160E7265244765EC1D68AD3D88C0D9C3C1A66BEA6059B506493A42C1569FFEC1E49D9137694A792A4D426E417EBF10196DAFC25F59E67F63E5E2FBBDA0C5B04E9E76D963ECBE709583
	F65C4B787F127663334CB85A6265A62BAD96EF483435C5E07ED4575EFB51866EFB6336281F65ADED1D45EC710AA96BDC60A9B0473EA7C4F48F6176454EF8A1463B6DBEB8BF5D47F877A8C4AFDE7357E139CBE1FA223CC367183CE9790B389DEF0E3315172E117BD04E857745659D4A7116E665B0F93394532F0127BE034910A6ED629276CB0E6D25EF2BCC9E6C633C4A66DF0676F34DBE9E536C7AC09D9EFF57EA73F2FE0C2D8E192B7C2E028C638EE9ECDC070560FBCBCD7FFBC9E3633783CF7AD8F60FF3240B50
	95D58B71B06311282CA35D5FCF76D3B196669521891CC19D08002FE1BE7B061CF8C710B7B6FEE6D9BEF66479854F4E0FD39B940D4533F329C9FF12520BADF41479CFAA33FB597D1E0A407D6C3CA7F511FDA59F4AAF82388740FF8187813D8F30F87EF0870935C94F53ED8A9CF395DEBEB3ACC1E5675AD3E4DC48A90DF24FA9E2971AD3F2ACA77AC1B7D9CBBA0C19854885AC81941AB27C4B2914B29C0B594A00F66E715B5D45F019E85FED19A36D0C6D8EC824DD0DCD3D930C3398ED57F89B6D50CF771C375EB316
	EDBD61AAF5D8EBE1236F344C381F22AD535C4416CB00DEGA886D40E03B365B8E64B1A53FBD359125DF998C6C41D7E2ABF51066D63066B97C8A8E33B27C6342BCDDCF7896DD00F674F125CE8AE54AD812C849483F60C073881689D4F74091EBE1C52B748A14D2DB6832D95F2536644F311A9EBE4441D5D2D69B8EAC43BC49DE3636D5AFDBCE27DA742FE3C4FD2364BCA988FE3D1F8A331EDBCDF71C7A5C8A249D6FD1CD7FC03D007E7391F2909E7B9F27F128BFD6681B0EF51FD65065F2E984F62C9C78F19334A89
	408FE884DC842481E68216CCE0F3366C7451D4F356448E43EE557CE9B3F1E80FDF5F219DABC2295A2D1CE057EED94AF67663BD1F325D1A89F6EB6169DEE25772AC48F7G07G3D00A140D4C0DE96336B4E4E14F6B52FA5EF452E1315BB53F3EE4AF6AB326C5ADDD26FEC7DA9A35A3D1BD0DE1DE5E7574673442EE58FFAAA2A818781EEC08C108DD87CA0332B5279D9AA3B9AB126091AF124C1D14E1FEBF726DFC9A4D5FCD9796070F6F8473B071E8957C0DD59C44879A8ED3DC2E8B5D0EE1C486E781E20B916E016
	7F0616C9BE0B65E3EA32BBD052AE59DC27820E71E552B1125D475D8AF09E35AE67BB3B41AE7BBB3B1BDD76F7F6E33A36A29D7A303EC9E075633A125F69CD6AF23E537B57EEE7B9DF683617F3F5373D1CAFF7A71773175DCEF2223FA874FC9A03B94EB0FD68D9BF7321EC40BBF4FE1EF7119CD30BAA3A949229883867083177F16DF4C795DCBEC40AC8E22EE12E49348B447796CFE4EB6F41B37BED565EB1C152D50DF56296F96DF4DF1F99504E04C59F53AF436DEC4B89EEFBDBCEF25B5BF206BB39AD67381DEC09
	B67A4EEF3650761108982181031CC40D3A1F68642EF2441D6DA908569CBB8B2D695FE8709C5355667D663805E71DF57C49D9FB9DDFBACB4ECB91B52A7944C4BD0A4F6EB06B62312CCD97C3C49C6479B5ED2F4341D19CE107B24F8E334E297DA60F132DD0572F872D3A9EB469024C681D3A5AC94E85180F6C8E33FB149593597DD7061778E465C330EF828EBD44625920AF597E64863EGE08810751075EE342747594EDF76585BD968F136F330A7351D759EE7BBDF6BC9ED67749EF2A7641584C9B3EE12510E75A1
	2B9D771B76FDADD44168FD1A9A6A2CB346FA2E57D97702DEFB5D7F2D57D977D53D29F5FF3957D9777F6CCD2D3B6B1C3D6E5FD32C3A57193ACFD1ADBE57D1E70EF5623C336EDD676DF56FB96F2C7B1767D36B6EBF6F2C3BF2BE356E8367132F2F8FA356759537C123CC5E79487C209A55B92EAA1B2C239E191D876642FA48839481DEFD884F8598673FB9C0EC5D8759A58CC38370EA5A769D05356D05328C4884ACG64E7333B1BC93B493A445B6CBA76DE46CF6FFD4030751348F7C083DEA89AFD7E544F7A0CC1DD
	A6E081A09F30FA925BB752AFEC326AEF44D1DC7F9F059357FF6564F88ED3BD59695EE5AD0DA77513936FDD567B883DE5F2623D4BF8AF3BF7F1C35D6249EC7F4C72317DF36794C67B8E2535C27922C9FBC3A034B014B3CC5A9714B6934AF9D358FD71CD19FDDB349CE865D34DEF0BAE117C56FCEFD04B8F688D8D1FEB12F01477375399465879944D93C97B4629ACF67E02769903F296A0F7AA3BF30E6DE6F74E8B3EB61BDB7EE37BF787056E6B2EA5D3510EC5D3AD77179FFACBF14CE6050DD9BD8D4EB353886D32
	E23DD3A5F61BD60A39AE564F99D6CF7CFB8FE72DFFEED8BD1177951735FE03EA2DDFE7E90F3ABD7DFB265B5A2797F272DF24BBFE6715B38DF5DAEABC2B3FBF2DA4E3EA67FA6C679FAFD86FF12A264B2E9207FE569A6D4B2747ED0476298AB07B9C1A4E6CF3A410E81F7707699F8EA45A677AB07DE787926DF33D44DA3FA210E81F43067D51770E52BDD687F9B226B3FF9C5018AFAF184E76D7170F2C5D35E6391216497BB4ACE7FB13657A7F9F4447FC7C2E0DC16C3EEB8651FD3E2FBA89DF558C1CD30CE705BEA2
	973EB39138A15A7E959F7BFECF1EE15E478BA40EEA2A2AFB2408C4B2E364ED9218BFACB1F9F5013CEB53487B5808FEDC75C5A3470675BF5EEC33E286334B1814F7A50940F68F5DE13B7C5BEE07B175A97A3E62459998D3CBCC7AF2CAAF3650B1867E4A0C015CCCCF05B0135DC96D9348DE318C4AC5003500ED8FFBAA6A811D8F333D62EF3D7B389467FB379AEE0784C036247D28DFEB4A3B0EC4E00C78568696EB967D755739457736B83F03FC58F887E9BC9767CFABA1CF546F6FC5225FA228DB8EA886D4BE6229
	28869CFC0469375EE75C7912E3931C12F8DF003EFDB58FE112B890A7134D9B7BFE210FC80AFD347BFE8FCF8F5FEFFB82BB815B4BE9EFBFA4A2EE5EEF4E7D76406D5A867D7ADCF8E48E928E33BD3F9D7492G19001CC7703D9B77A875F72D4560D97C4848BD3CB4817940D38858EE140B9F097B944A31F2BB1103DE1056B15AD29F21B50B641D871B4B81114D654E4766F0BB5FEBF4EDF5184B56C7D1AE7D51F83C0F6B3DD920B90F9695315F1C816BBEF27075FBEC3F17A148B57A4BB31E192FCDAB41BEFB855226
	0F17A371F3643CE1B41C2B6F05B8AEFECF997FDD923ADBF1B49EA35E3A2BE3207FFEAAB867700D02FD8EFFDC303F87E897125F6D3885673B1D8B0275FBADDCE7B7693EDBCCFCB887E2932095F0B1072D5FD5427FF0F47D5A2C5DE3021F1CA148572ABA9BBFDFF45675D9B175B72E85A2799E432B6A3A9AA27D1B67B9114C172E06A95138DF203E2AC97E000E2C6CFEC1A4FB0FAC76D1B24668D317D90C0E01EE8BF3D81E7AC2ADE91310279ED549B7D018269AE77C491759FE76E28E4BD31752BE2BE7C13C83F44E
	E26FA77ECEE3778181203DC7E502BC0BE3FFEB7EE4C7433346A77D2404EB70C71556355D720396576FB8071CF0962E3DB53362EBFB49257A0DB8473EE0B5BCEBE6FBAADAG81C09AA09B30G308430F2B61BEF3F3F06F30835C0C44350B957F8C951317FC9B407AF06BE2A6630FE9E77FF14241FCECD8D0B1ABE08D47AA517B1F777851C7DE7FCA0357FCC21392B88736E9E8C9B6751198192B7553E3E08E87A692CC015C9EB0BF42A91091CE3B1A638AF5FD93E613D43F6B3EE3B9D66A3876977G7B66A466A9CD
	14FEE8CEE21E72DDF38D34C25D02B9ACAFDBBA6C5DC055DCCF452E39496F55F15E1F8911BD6FA0703A81B1C096E0AEA08F309CD084F895D0B58F4E6B00C300CE00G508199001973184F8C756FC962B34661B16EFC66FAD7024EBE938D2676199BC17B18139E9C99F346866DE34E0420B566A0AD5B68D7B3AFD311BEBDC84EF618E3F4708A6F375C2FCC1709FD4D780ECB03792CCCE235791D917D346D8CB0AAFE1C47A3249B4327BE377CDEFE796E6F232B6E6A98F85E7BA10D2B8B6721DFA32D10521699B43453
	0FC34E7E30BA145A9FFE91327707DF05C67A8327587B833E6B9A6E8F958A59937C60473DA6DF15C9BBE7523653BE892DC7927400C9FFDBF95D20370AE4C0C6FF1F462D81CA6BB5E83BAD34F3866DA30544C1D0B920EA2C6DBE7A4DD51FA44BD82E27BC1282E151344727D4C6A94CDB64BEE252085C3817723BAD5F41D151113EB336A9CEE722F378FFF935D70923AEF809DB27F36FA47D44B922F1DB5896DD5BC685AC3CF22021745F67143A3832B6EEBDCED89BD7C62C5E462D231352461542044548BF3B148D07
	715D4BCDEB1E7B6A981FE31EDBB59FE3622E79095F3D0C9EA2F3FD8968C373D9ACFB765A471C731965CC0817E51D1703CD0AC04EAFC787F5B11E43E58C117EA30C4728B4624C90113B5F52667FGD0CB8788D720AA202C93GGBCB9GGD0CB818294G94G88G88G5B0171B4D720AA202C93GGBCB9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6693GGGG
**end of data**/
}
}
