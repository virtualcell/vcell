package cbit.vcell.desktop;

import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.vcell.solver.SimulationInfo;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;
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
	private cbit.gui.JTreeFancy ivjJTree1 = null;
	private MathModelMetaDataCellRenderer ivjmathModelMetaDataCellRenderer = null;
	private MathModelMetaDataTreeModel ivjmathModelMetaDataTreeModel = null;
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
	private boolean fieldPopupMenuDisabled = false;
	private cbit.vcell.mathmodel.MathModelInfo fieldMathModelInfo = null;
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.mathmodel.MathModelInfo ivjmathModelInfo1 = null;

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
private void connEtoM1(cbit.vcell.mathmodel.MathModelInfo value) {
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
		if(currentTreeSelection.getUserObject() != null && currentTreeSelection.getUserObject() instanceof cbit.vcell.export.server.ExportLogEntry){
			cbit.vcell.export.server.ExportLogEntry ele = (cbit.vcell.export.server.ExportLogEntry)currentTreeSelection.getUserObject();
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
public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
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
private cbit.gui.JTreeFancy getJTree1() {
	if (ivjJTree1 == null) {
		try {
			ivjJTree1 = new cbit.gui.JTreeFancy();
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
public cbit.vcell.mathmodel.MathModelInfo getMathModelInfo() {
	return fieldMathModelInfo;
}
/**
 * Return the mathModelInfo1 property value.
 * @return cbit.vcell.mathmodel.MathModelInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mathmodel.MathModelInfo getmathModelInfo1() {
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
public void setDocumentManager(cbit.vcell.clientdb.DocumentManager documentManager) {
	cbit.vcell.clientdb.DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange("documentManager", oldValue, documentManager);
}
/**
 * Sets the mathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @param mathModelInfo The new value for the property.
 * @see #getMathModelInfo
 */
public void setMathModelInfo(cbit.vcell.mathmodel.MathModelInfo mathModelInfo) {
	cbit.vcell.mathmodel.MathModelInfo oldValue = fieldMathModelInfo;
	fieldMathModelInfo = mathModelInfo;
	firePropertyChange("mathModelInfo", oldValue, mathModelInfo);
}
/**
 * Set the mathModelInfo1 to a new value.
 * @param newValue cbit.vcell.mathmodel.MathModelInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmathModelInfo1(cbit.vcell.mathmodel.MathModelInfo newValue) {
	if (ivjmathModelInfo1 != newValue) {
		try {
			cbit.vcell.mathmodel.MathModelInfo oldValue = getmathModelInfo1();
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
	D0CB838494G88G88GD8FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DB8FF4D4651527AB6C21EBBC5ABD6822075D63DA3451E2CBEBECF3DC5C45BF27E24596D27C5B30464ADA5AE20BB5EBB9AEE77BB2891211E0C20996F7D1E3259AB446043FA186A4FF008492B2A413BFE004BCB2AFB3133C19B7F96FA599C2A0FB6FF75F7766E5725E8C30471C73BBEF3E7B7D3B77FE773B5F7D6EFB910A5F3DF9DD0E2BD890F25689465F5E9CC1A85EA788DF5FF3625FB82D6A438C37E0
	79FB8F30C9B85C17874FC628534FE4380B84A9B79F4ABDD04EBE1D617EB37C5EAC3436276C059F8E7378206EFBABF697A61EC79C60732CE76547038570FC9D508BB4FC4E7B90677FEDF00BC11FEE50B9F2DC02B033945B3887369A345550A69FE88D006ED2EA9F01A7B1B0233A4AE857730DD4A17B472AEBA30EE39913E30951B75BF816882F644C590EF36D98A8B07A14C15987C061F3054A7027702C718C7A8F741D8D6A65A3FD12AC177BA4EDD0D7A25555AD229EE8D1FC125CA269E203280B9EB1AC4942160F
	F04C3101FCB606A0E441F8F3996DB3260F9E782D83D2BE62740F42C8DF893F050FB35CDBBE66326D7F3046603D7B7C8F05E5BBC2F9058E32D5FCCCB2F5FF9C13ED153C5C7596DB07FF9EC4FD4F023AF440E240EAC0D1D9063B82703FEC7DD387BE02E76DD93DDE890514F0EDA4D2A77BD4A69E7C2E2E069A0356A675A92ACF90E80D2B864AEC78C9846AF778327B11CED2F772751E57769DA1732E9BDC6BEDF41219FA5E55D8C63A68AD0B6904661EF01CFBDA59E4FB1CBCF717E01D3B6C2947F257594C5D782347
	F2E7996324DA66DED106EB7DDDE6830B60F70EB1AE5197B0FA69A7B1BA59E6AD23E339816AD2BF6136517DBA6725B96F962128729FEC75B07B7E94D7C1B971302DBC1E17FB185DF94AE94ECDA6AF0FB07A3805CE3CD4B0BA16D321EE572799EE322FBBC238D797C2B993108DA82848F017819A81BD955C46DE9C787492ED4CA32AFA5DF0D076C92AC0FD6F98FC931ED2D4D7452630260B61BE29D3D40362D9D95284234DDD03280333137A25B35B6F03B90EC8E1C9957520920651451024CB2AC6ED6E9BC4197AD4
	0955FA03A189A688C584BE6E43031B61A90B1A5E9171093A94EBC175CB87F1CDC644CE989186005F2417A7C80FA07FAC40DCD38F4DB20EF7CCD241DDD4D7B7AB4A60F0440B1288599528679B186FC8075FCBAA380EFF26A02D2AB2436D296463DCA57F49B2CEC3308F599755F3FC0F5D9DAC02278F5A4F844C87E4D6F27B79EDB7371F060DF78AE50FDDEFEBBF8B401657D4125D946D085FC77733711147AA286B86C4F7F05FFFA4581DE0AF4F2F345FE7094065C099E80E5E04F32C3F4C7191682F57997B0D74DE
	BB04EBB89B64CA83AC5B41F57F2FF28959F21D28C95CFADBD5B0BC4997DB8962F3E1985B6EB446F49B7360B935C1203333D060EB361435DD8573946F4CF057GFC0094C09AA09310370B4B3FA7BC4D423755D7EDB046AF3411712D3872FB1720171DC9743CC9B8E972620E2B63F2EE32693BAE2EEC472F5D9AB955515A2405F09CE9C48A6B8DD2C48A7B6007E6547DCB8879B37CC83FE276390F51A360F76AE123EBA67D219072AD6A3A9ABCBB2CCB6D122E87437ED87DCF59DC5A7059C84CE571399E8F21FC4AA8
	789D337D4FC338E77B14F0D8E2C6E2F6B16A17B31FA149D288182ED70643BA511F8BE3ECB5A81DB3FC56AF432F8A68F364E174FF942F4C97D14F91B5988253C397AA0C6FC2FFF26740960B0E4A0006F117871E3EDD5C9F0C3E4F7D4136315B0505E7A2AE3B33ED762D573A9E5CC5FB7749DDB1FFC0BCBD2958737E6F4CF799F40DF8FDAA7C86BC6B020ACD84C5EB3CD918D178BF708C4507D9DED512980137AB6FF95B2EDDBDC4EBED387AAA53BF356DEFFA5D5A96ED20126F75B19579DF83F2956C4EF0EF5B4D77
	7A298856F3BFEBEC72FAC516E98921EEFCB76AB9FF2058222B1A5D2467574D7A1B862D758F9A7579FBA0365D0375B3C23CFEAB14F76D0975A7BE0E8C213F552421E1890EBFBCB00E1976F0FC68CFF1F51D06EDD0FD67905A06ECB9EBD631B95D8A0F33D30C79669A4F85FBE8EEEA7700592EEA2FE1BF46B330175A2D590B6DAEC4F83B59FB69CC188F4F0500AC40BBE63B16A15E2E210A4E8F5E2E270A5A25D6E13BADAAEF9701B27A4519D554EEB6BC4B4D711EB75BE5D791DF857B68BCAA5DC763497B305DFC4D
	1443D20F73FB76D9652D9F656D5277115FDD320F46DDE934A3BBFEE9BB6EADF6C79890C744E839B68A7E213A7AA8B3E53A8B7CF9B05FE81FD74DE37EFA364FEB206C854855FC9FAE5D48E37ED90329C27E534BF21D6EB3532B0D9833BA368F53EE984DA5DBFED043B9D7C25D1AEAEE47CB072CF1C72D26A9FDC1967298B6B2CD356ED3224DD0DF03E7FFD05574A658CAD1EECFD72B48AF46C486595047197A1D42655C19E21B19643FF058AA7F02428B2E8D8E7297FCC6F217FDE639735C7DF2AE71F135FF4FE571
	B159D9503AADB557CD06B9A8861E97AAB0FC49DC20E5820AEB107E2B907AE734B91223EFA717633A31DB055E6BEFF214233B06780F56446418DE3CBB57381BB21F0ABC4C077A15G72E3B7B2FAB09C5403229C9C13CA37135C193B37619A06F10BFB95C57686A3DAE93345B44B0F224ABF5B5EC9FC5E3279204147626F5CA254CC1CF6595DB1E79686DCA57B093F467DB1BE33FE365C158DA0DED7193C4E02B60B77F3DE5BA3763C7E7CCB64F50A5FE6BB67CC1325F9A70DFFDB046BBE6FG44C68778789B07D077
	FAA028111FDD60377AD99D5A4D81ACB45B6FE6B1DD1FE545C9973F2F356AA2FDFD0A2DAE825F1A6DDAF600F43076C0CC970BFF315C15758B2E0B11A14EEB736760B7BE67F36728A8FB080EAFFE47D3F95B8550AE5BEC1B2A22DC226A2F247292154BC97C0E1F647CCEDB1CA6EC59FC0DAD3FEC0F9CA4BE3D87E37C96832F25A63F051AE95750A673A067610F210DC25CF9A9703374A60D73C3E3BC257175AF2E4DF0B75772B1BE9A359B03E438739D2B4E73459936B2247C4A6F1ADEEB6410EAE3B2B4AEDB6E6AD9
	46E510C7797C5950262C0E67F18E1CC0FAD8D1C3A22C38B6AC6BC4778F60F9AAC5A3E258D78B46200CEAE0EB944FEE9D0C4F91786AE2F716E85D547B8B5D1DFF487CFACA9D5D1DF3CD7AFD0CBE47C2A71E1B42DC6F73202E281E5F771E9941F3C50772F500D440C2C096C0E8607EFC33063EB661FD3AA98C575C30A837CF8AD0793DD643F9A126B44A0F32580C4514820F09DA22381736421CD5G8FA0G18E97260551372F0A8EA4B0311A31C22E7F626B510FE97B4CC55B3757BFE887BAD3129F702F197E37DD6
	DEC6BF32536D272CF9463CCDC7DD398EFBED4BA19A3B74D04CCEC917AF1FC3DD8600BE9DB0871086D8F6086B721F7432E43A64B98FC309C68ECE0957E176214972959D1EDCB6F2F0D37A154715F74575A3B91A4FE3EC548CF58140B4409C40C2C0A6C0B84265790D3EBF29ED60A54D2B54832D91E2537A78FB11496B6F2FD0C6A112CC4612A3F67D66CD69978F7B796EC95A2FAAEEBE72C591366FE3710AFFB888C1A49E550745F05FB928233B5C1AF07CDD8E73AF9E985387203865A5FF3EE13763C738BFF9E09C
	57AC8D4ACBGAB81F90D907B829AG01C63EE687066B122DD9AD3F8CFB9573274D4211BE46072ECC0FFF17345F34C63BFEC754AB1B2FA3E93F590DF6FB2176A26AF5BE54AD862CG88CDF0F7849C84781A38DE7FBE39DE4D34642568758D6D4A64FCBFE93F71A63BFE4FCC69F7E97B6F05A97D5E09AB4FEA32536B8FA6D02F7320EE91A08B3096D0F29462BEC06FD12E5795431FA5532B61E3EA199A27AA1478D4C72FCC3EE9D37A457B17E9C7A777239C6FCEF637188DF573GC4AB8AA1ED8914D79E65B93EFBD9
	5E276098AFBF444A984FD239B51CA8870A7D922DF5B2509C0104F3A44A47DD8AE89DD70EB8676CFEB7E21F337B4F917B1C5DFF0DECA2BA0CE1FD13406BF3C7936774B60CBA6774BA224EFC76C46D79BC9B3567B390CD4CE7A46A44A7594B8B4C06CAE00DAB0EF19BEA13398D55G6DED36BE0F3BB046D40743FAB0A4B196684C11226F515EE99B8E5376C1AE3478D8435C13AF8BE8FB3D47785EDBFFEE074D5EBB648B6A0A4A8771CA225A5A5FEFBA3476085447657B6FF34E3ADCB7E62F4B8DE3763A5CB416D817
	EF0DB96912F474701B052C3F26C99AF398F893B56AFE29E32EF2CA4E76B8F8EB015F059445ED34B924AB75BB4CF9BB4FBB4B7865F9FB997B4E63FDC9D30655BEA9DE0E0173DB4C3A18AFEB52259032C3EDB456DF070B23B4C58FE32CBF55B9753F6A3C132EC8566D912B2CD5A67DB9136E51958F5E8BA89E29D0F89EE57C984FFF2D7723CD26C1F9B1E005695F5678931DCFEB21DDD1B31C2F002AE6EBEE7451F1E7BDAF9B37577353634EFAFEF6BC391EFFBD6E2C679747136B7915F14C891D8D7A022A11C9A6BD
	5EB5E455638ED33F97558D1C5E2FAAA1CF25B157310B4E3237DD34173D7B22336CE7AEA617BDF051D9F665E2F25923976DE5EF562C32D71A323F29DBEC2E25521CEB6104336C8FCD584B7E6804336C4BA6124B3EFC42D976959349E5FFE1A2717E3AFD543A3FE2BAB8BA086FFCE4711CB22C8BC2FAB36EA3F1105F871A216C8324GEEEE26FB8179796F0D222E7BA13A04E918831E5502FDBF8C713E82140B813BGDE005E42F3B757FF007B12325915869FF9B24B7B00E2B3DB70BB20973009B1E6D066E3169C07
	71GDE008E18F51C1F9B2F30F702267C069F257D3FFB4ABDAF6DF8AC06D9F45CA96FF21E79134C6371F917990C3E6AF8FC5EE5231F67DD56C2DDEFABBFBF8B837C7CFC5024B5B25AEAA867F9B86D9B0C36854A95A6AD13519A206C7370FC7157877937C5D1204D77185F9675E2FCEB3EB7A897C7756A6AA3EA50D7A77A1B598ADBF35058FF25077B4EBF32B1CBCE008F84F8CE701CF374831EF31EF75BAD425A476C5F9DE4F9AF3882A7C80FA9A7E2B98AA1345CC5F33604781C0B20DEE8E37A8DD9F32A28379BF3
	A956257ABBA75523FDEF972C754FCC2AC7FE7F603256EF8ADB6B57D97A13EC0F7C154B3676117902FC77F4476F3C9A5BC8A659F8DE3762461C4873E339B44ECF3F3066F10AEE12DDB98E634CB57A4FEF0B690874F32D4C7533220D6B67DEB9DEBF6FCD127FF7F23CFEAECC127F3DA97A3910E32DEF8F456BE73F21FF323DE6F646EEED87FBEC67767835A1EE4B5EF6FE3EFE4D0FFBF72ED93E1A1571FD9A15B77B93457A7F9F4466AC7B4A66C03DAFEEC759BF8814A4E8175ECEEBCA7E6CCCG7922F7A6BEE11B17
	656F837C7B3DE22F190F8F209FD595C56F8CEAC10C0C296D42G370700597625G3EEBD371FD2C269FD67A0635C313465F9864FD463DDCAF575A66C293017A7D639576334F3DA682795425B27A1F6B3C64D3F3CC7A530CBE47C2A79F38CE66BEB0956A8ABAF8CE6AB6B71E9591A827G668296GB281EBBA78D931AB3FC2C8F23F77AA11E688GE4CB58CF7235052E6C9B437B56F3DF13F64F9DC27A5FB6B83F036CB45EC19A4F5267EE4C41A749F7E3AB4A5783F5D1409CC09AE091A02B134BB71EEF64FC715A84
	37A431AF405E3E1A173020B49AA36361CDE3AF70A027B4467D79FF05E727B8E0FB83BB8A47CB6B598188C43C225FDC7BD972656A066C3ACF199A030CA97C4C4F86398A3A605C87B4F651FB3754AEECB3FB2841E8535BB5758C4F0D038EEDE6825EB04AF33AE2B6C5FCBC798672419224151CD69FC0DA3D046FBC78DA0ECAFCAD37FE7B1B4256F78F3AB6B92C65EA032F2D5DB1FF9F137B1AG0BF954E109463DF954FA0ED43D76B7365F4BA0BC46F8BE6339760D1BF2E84CA7825827DF143558BDF2DB50CA2335FA
	82DF959A3F33AC3FB748DF159F0D7908EDDF699CE43F5FF5BB4770BFF05B47708B5C76F9007B5D09F3BBBFF6BB67F6163A2D5FEB51BE6BE843F3F78E2CDDBAA093309A10F7126F5FB32F7DC5E07B57E66F9E7279718E01DF2B6AFC7EDBDA1DE53D3DB579B72E5FEF456FB14EAA3A2E04F0FCCECFEF4575521588A3926D5ED654119A7487F4EAEA509FE84533C7167A9919FCF4F7047B6892102D7BA40FD31FAF47BEF1F1EA1D025FC0D118EA5C71EF0E70736C3A13BCCE7D899BF3911433GEBCE7277938F8F71FC
	C051298C773B0CA7C14897785F6A4F6DE8F457586845926D41E78B2CFB3B610986577B02C3CCF80A765E6CD3313D3DD6A9B13E155957D1824FA5D0378AD074C5063B94D08370828200E9DF70755EFF22D4F07035C0A44530B5D745E0D8277187BBD10EB9B0C63AB94ED5A7BECEB00EC7D5A2122A1FA3AA7B120B670C5A1C6D4757165CFEC2ED6C9B9FD8F76F3908F19FD55B50EFAA7D7D1AE45A69E8DB11C9EB52BC0A9644FBAC790453C95FF1597B92798A7B5DFA597DA89E29E35FE0BC79C5FC1CF20C51D7FC91
	9F27ACB37740EADC7B9E9E177DE4523B28F420AF6EC91CD727F57FD6971EF9D950F6AD2064CB382F811A81BEC09410823899108ED882D881D883A8B81D615E8A28GB41C66B653665D1E40E60C4BE34C784C7D5E5061ECB347BA125B0C37435E671C6A186AF34EF4587B9C29436AF30CDC26B12EEAA6D30D3CF7875E6DA846E89143225F1A936FC47D1A7E1D36863759CE745566F7C66C5336F6E8987653BAB68E8926CD1D307CFEBA72551F23AFFD25F350FD6FBF18DF6DBECDF6CD34DFB3DA4420995FC0F7B95B
	437DDD496D6111AEFBFBD85AB5559E9E6F3237074C2E297670EC971E89FE3063EE335D73A66D24C97B8D9B93E9DDC11F9EB069AFB23E105EA861041C7E874657A823F59B3435965AC90316530DFE90C48EA8AA6F7BEAB746FD7DC1D92672EB2CCDB08CEE5154C7E1B772980C08963E0BCD9A72CDE729780165BBB846BA5137C6B746681CF5017EAFEF250836BF6F85E11DAE3C1D7093E7CD958A7991DD5E44984C9A59D91D7B5CB7F3DDC2DE1370AAADD8131007DAEF925631C5E992F2E14122784FAEF913E1FC77
	B2E10DF3574C68F30CF3534F10CFDCFCA67E3B177DBEDC6B7C5E8C7736DE6E4B2E697BC4F03E333407C4D956C5F930B66C437BCB5DB9DD0A45F0F5BE9CDF63ED0CCA2AEB72A15F8316BE7F87D0CB878830A916672A93GGBCB9GGD0CB818294G94G88G88GD8FBB0B630A916672A93GGBCB9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6493GGGG
**end of data**/
}
}
