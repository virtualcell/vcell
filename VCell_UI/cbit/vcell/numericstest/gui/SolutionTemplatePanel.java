/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cbit.vcell.numericstest.gui;

/**
 * Insert the type's description here.
 * Creation date: (5/12/2003 3:39:18 PM)
 * @author: Anuradha Lakshminarayana
 */
public class SolutionTemplatePanel extends javax.swing.JPanel {
	private javax.swing.JScrollPane ivjSolutionTemplateScrollPane = null;
	private javax.swing.JTable ivjSolutionTemplateTable = null;
	private SolutionTemplateTableCellRenderer ivjsolutionTemplateTableCellRenderer = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.numericstest.ConstructedSolutionTemplate ivjconstructedSolutionTemplate1 = null;
	private cbit.vcell.numericstest.ConstructedSolutionTemplate fieldConstructedSolutionTemplate = null;
	private boolean ivjConnPtoP3Aligning = false;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SolutionTemplatePanel.this && (evt.getPropertyName().equals("constructedSolutionTemplate"))) 
				connPtoP3SetTarget();
		};
	};
	private SolutionTemplateTableModel ivjsolnTemplateTableModel = null;
/**
 * SolutionTemplatePanel constructor comment.
 */
public SolutionTemplatePanel() {
	super();
	initialize();
}
/**
 * SolutionTemplatePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public SolutionTemplatePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * SolutionTemplatePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public SolutionTemplatePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * SolutionTemplatePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public SolutionTemplatePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * connEtoM1:  (SolutionTemplatePanel.initialize() --> SolutionTemplateTable.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getSolutionTemplateTable().setDefaultRenderer(Object.class, getsolutionTemplateTableCellRenderer());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (constructedSolutionTemplate1.this --> solutionTemplateTableModel.constructedSolutionTemplate)
 * @param value cbit.vcell.numericstest.ConstructedSolutionTemplate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.numericstest.ConstructedSolutionTemplate value) {
	try {
		// user code begin {1}
		// user code end
		if ((getconstructedSolutionTemplate1() != null)) {
			getsolnTemplateTableModel().setConstructedSolutionTemplate(getconstructedSolutionTemplate1());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetTarget:  (SolutionTemplateTable.model <--> solutionTemplateTableModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getSolutionTemplateTable().setModel(getsolnTemplateTableModel());
		getSolutionTemplateTable().createDefaultColumnsFromModel();
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP3SetSource:  (SolutionTemplatePanel.constructedSolutionTemplate <--> constructedSolutionTemplate1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getconstructedSolutionTemplate1() != null)) {
				this.setConstructedSolutionTemplate(getconstructedSolutionTemplate1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP3SetTarget:  (SolutionTemplatePanel.constructedSolutionTemplate <--> constructedSolutionTemplate1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setconstructedSolutionTemplate1(this.getConstructedSolutionTemplate());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public cbit.vcell.numericstest.gui.SolutionTemplateTableModel createSolutionTemplateTableModel() {
	return new SolutionTemplateTableModel();
}
/**
 * Gets the constructedSolutionTemplate property (cbit.vcell.numericstest.ConstructedSolutionTemplate) value.
 * @return The constructedSolutionTemplate property value.
 * @see #setConstructedSolutionTemplate
 */
public cbit.vcell.numericstest.ConstructedSolutionTemplate getConstructedSolutionTemplate() {
	return fieldConstructedSolutionTemplate;
}
/**
 * Return the solutionTemplate1 property value.
 * @return cbit.vcell.numericstest.SolutionTemplate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.numericstest.ConstructedSolutionTemplate getconstructedSolutionTemplate1() {
	// user code begin {1}
	// user code end
	return ivjconstructedSolutionTemplate1;
}
/**
 * Comment
 */
public java.lang.Class getObjectClass() {
	return(Object.class);
}
/**
 * Return the solnTemplateTableModel property value.
 * @return cbit.vcell.numericstest.gui.SolutionTemplateTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SolutionTemplateTableModel getsolnTemplateTableModel() {
	if (ivjsolnTemplateTableModel == null) {
		try {
			ivjsolnTemplateTableModel = new cbit.vcell.numericstest.gui.SolutionTemplateTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjsolnTemplateTableModel;
}
/**
 * Return the SolutionTemplateScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getSolutionTemplateScrollPane() {
	if (ivjSolutionTemplateScrollPane == null) {
		try {
			ivjSolutionTemplateScrollPane = new javax.swing.JScrollPane();
			ivjSolutionTemplateScrollPane.setName("SolutionTemplateScrollPane");
			ivjSolutionTemplateScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjSolutionTemplateScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getSolutionTemplateScrollPane().setViewportView(getSolutionTemplateTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSolutionTemplateScrollPane;
}
/**
 * Return the SolutionTemplateTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getSolutionTemplateTable() {
	if (ivjSolutionTemplateTable == null) {
		try {
			ivjSolutionTemplateTable = new javax.swing.JTable();
			ivjSolutionTemplateTable.setName("SolutionTemplateTable");
			getSolutionTemplateScrollPane().setColumnHeaderView(ivjSolutionTemplateTable.getTableHeader());
			getSolutionTemplateScrollPane().getViewport().setBackingStoreEnabled(true);
			ivjSolutionTemplateTable.setModel(new cbit.vcell.numericstest.gui.SolutionTemplateTableModel());
			ivjSolutionTemplateTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSolutionTemplateTable;
}
/**
 * Return the solutionTemplateTableCellRenderer property value.
 * @return cbit.vcell.numericstest.gui.SolutionTemplateTableCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SolutionTemplateTableCellRenderer getsolutionTemplateTableCellRenderer() {
	if (ivjsolutionTemplateTableCellRenderer == null) {
		try {
			ivjsolutionTemplateTableCellRenderer = new cbit.vcell.numericstest.gui.SolutionTemplateTableCellRenderer();
			ivjsolutionTemplateTableCellRenderer.setName("solutionTemplateTableCellRenderer");
			ivjsolutionTemplateTableCellRenderer.setText("solutionTemplateTableCellRenderer");
			ivjsolutionTemplateTableCellRenderer.setBounds(98, 369, 207, 16);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjsolutionTemplateTableCellRenderer;
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
		setName("SolutionTemplatePanel");
		setLayout(new java.awt.BorderLayout());
		setSize(473, 161);
		add(getSolutionTemplateScrollPane(), "Center");
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
		SolutionTemplatePanel aSolutionTemplatePanel;
		aSolutionTemplatePanel = new SolutionTemplatePanel();
		frame.setContentPane(aSolutionTemplatePanel);
		frame.setSize(aSolutionTemplatePanel.getSize());
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
 * Sets the constructedSolutionTemplate property (cbit.vcell.numericstest.ConstructedSolutionTemplate) value.
 * @param constructedSolutionTemplate The new value for the property.
 * @see #getConstructedSolutionTemplate
 */
public void setConstructedSolutionTemplate(cbit.vcell.numericstest.ConstructedSolutionTemplate constructedSolutionTemplate) {
	cbit.vcell.numericstest.ConstructedSolutionTemplate oldValue = fieldConstructedSolutionTemplate;
	fieldConstructedSolutionTemplate = constructedSolutionTemplate;
	firePropertyChange("constructedSolutionTemplate", oldValue, constructedSolutionTemplate);
}
/**
 * Set the solutionTemplate1 to a new value.
 * @param newValue cbit.vcell.numericstest.SolutionTemplate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setconstructedSolutionTemplate1(cbit.vcell.numericstest.ConstructedSolutionTemplate newValue) {
	if (ivjconstructedSolutionTemplate1 != newValue) {
		try {
			cbit.vcell.numericstest.ConstructedSolutionTemplate oldValue = getconstructedSolutionTemplate1();
			ivjconstructedSolutionTemplate1 = newValue;
			connPtoP3SetSource();
			connEtoM2(ivjconstructedSolutionTemplate1);
			firePropertyChange("constructedSolutionTemplate", oldValue, newValue);
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
	D0CB838494G88G88G5C0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BF49457F5824447A01B858488A20C8C38D6DAE36393A555F1292B44F68E0EB9095C6204B6749CF21AB4B8A58DED71291A92FBF6A5ACE101ADC045EA028D0EF1A230AC7483A431E02DD68281823620EEE4079A0DF6C75A15C6BB4B6C2C34C200FA6F7B4C0ED633DA4CB1BA671E19F76FFB6FFE5E3D775D77F6A4149E4858EE35158A02F53B407ECED88541F7CC903656BF7D8B0E33845C4502616FFD
	005DC264FA81BCB300F6F0393B38C4F86ACE913433203DF9193B789DF85FAB0C3AAA6A60A58E1F82201DEFFA68490979EC92B91FC2521ED533931EEF81E4838E1F858A72DFDED346709B981E0355A6881BC81FE7EA76B3DC9B343BGD6GE4AB34FFAEBCF7904ED39B6BD93FEBF3B2052D5FD1ED3BF09E431C9C2A02F4EC87FB9E94FEE95D96C4DEBFA5F2621894E8BF87004A678BA16D84BC6D2E7E5E531EEE3FD6B96811E439B2909E10D43FA724C9A12D32B76CEFEC3C2A48E14D2F84BA240120ACEA12CB8CC8
	32903901F39F2DADA2F389C293348BD4446DBE0D7CD6C3FB9D40E69D1F6FC4FC9B3CF781C4D42E63295F5B198E1DA3FFAE7C3877799DEF47515122D25D3255280E870FF4677FB7D90FF28F5AFDAB504AC26E628AG97C0904082709B6287C73B0F4053512DB9150181A56088861BE52FFAD171CAB23CB7B68205612EC89EC5758A82DD6B1F57D41848B39150F17F790547D11BE45672F5FF7C4A9342062FAD30951A58E4C36608ADB3C4ED11930A5A04722E2B0D47FBE3E82CDF0E657D874148DBF82526F03B896F
	7D5F2FA95C4A66A952223CDBB4DC6BFF7A84573A9C5E2DECDE0A4FA3789986BC7551F7084FE0FB85500A425CB7BA5F62323497AC93CA2A971A5AA16DDBA9B6B9CCE5C88E474AB2C27CEED518725C254BB21360579970D4169D840F6DBC20F9875D45543F2AA50C59DCE8EF86A898F2976F873083F48248C35C47A25DC76F5247DC222AB5077D32D7D285BA365A338F1ED2C4D345F30110A686BC12DBD47DE237AC058456275103B6689EB36E93627B9E60F1D68AC82A08718A330B8312A629A15A274D339B7B2892
	21F67887A0424501204067F5F97642D396C35A7F863D9067519E145E6941B599945DB0A3CCG6F54AE1F77203DD2C07F95GD93A9D7E064CF7C1D2A1DDB4B636ABCAFFB858011A88859134738512BB56C07F0DC35C461F75A22E8D68DD91BE4F1B5EBD06F9DA7C9E94DFD407F90C7D4BA7A5705420FF9A409A00AD916EBF7F5C497D27E557CA21626579267EE381DFAE9D26FED3B19C9BC72F1179D1468E20E9G2943FC8FF877F427C99C219C0962DCE3324623238E1447F1D39E5B934C0F00F939F0827A8EFD8D
	5778F08D33C02FDC00AD435C762D5E0354171B4510443D77328A0EA7F923ABC1651C64ACE37CF0DE5CB38F30B8AA9678DAD9C81F221B6E62AA00CE00A400CC005C1BDC5F371DDDB1F272B90B59FC0974BE4078A74E4FC746714AE73665F25FCD3E1E4846885416A7A55C5724C1A92035C8C1A96005179023B5CBB88F0B7B9EC59F53C670C14893CE884C100E3FA4E1AC0B1A267A3B431AF4D552B4FF20B7CAF793DE21F0F7B01AE2B82F7F13D0BFE58872045E3FDB429873A80100C496D59F4268DE926312AC8D00
	50CEA59C50A83E2F87EB22FEE9186518C04F9B826688B90C790A56992BC434E3D0758F002BE04A935AC6B07E3BEB4B8CB64A037547FAE9BF502AC6F87C8EFD406377504D2F8AB95703B6333DA86D5159B64B880D2595A3517825B2DD6FB517DDA2390661C3D4D647257F0227F945C3FDE32F708D9B7AD6A8268F49ECCE28202E604A2A4436347F2F8A77D35B06B4B56C51A4EF6C7455345F927BBEBA6F58B949464163545A073ABC87BA2E8758B2426374C73DC867B9128528D311E53A1CC013EF214DDB1ACA8DF6
	AB3AC5ED5E09CFC2BF5A4C69D950DE4B68EF6963FF50EC9CFF68F614CE65782E8FF3E5C83A9116E06BC271AF683E7132EFCF8C4D4D7C04527F5E07FEA29B76090ABB48735BCEDEAB4B3769DE118C4FF4009C00B268330A74333572FE91E8E30E315C21B9BB831E1500EF9F45FE39FA3FEDF7287C2523B42F5B61799E60A95F45E7F83FB4839DF37F2AD12AB3655BF71677DBBFCA735996C37FA85F775AF83F02A4BA8F52D10F7DC914AF7541FFBB0CF1C16A72BEF1D00CD40606A0369B9B4F93B724F577DA32F7E2
	7F2AA4DEDF679DC75F70C1BB89A0AD094750CB3BF8FD1D5A1FA9ECF8253AB05E99E2D59255EBDDD2B406AA36B85E203EF7219FF916CEF2979F1A447D2E53EF5C639D211062711372022DE9E11FB146A8EEE75F1B706C712BA16D9C04CA042F7F1EBE14976B8F06E676785EF127F0AF67147191C9EDB75244EDD7BF095B687699230DD63FFD5BD69C47C641C954B616C9D19B2D9F68A82435233BF9A70B7BDC2097CDF697B7CDC67C3CE64C1368E7141F271673335CFCD4681A3FB8AE3F6449AC2FCDB62C491D3C6D
	54B6674376FB378D49ACD46EDF101A97675CGFCCA27380BE9CEF237A05E9F70EBFED1765F144A8F535C5D57E84C5D6D50BF88108A10B9051FAFEB5AAF10F8075D2EC78C4B9ADF0672F6F46BCAD98C74D612DD6DE43B414FABB5942DF25C621DBC6CE66BB72B051FB9F326F0FB2EFC2F0949327609E5C211B855E6F606CCF9255716B70559B1B9EA4FD5F33B731F8320367828152F218F7AE4A4F3DB4CED45B594555E9A5AFE1A34B51FBFC4E53875A917A1E9ED16D03677C1D399C81EE03C0529D199EEE5F467DB
	B2388C7F214B6002BEB326F2993E5A0C31B241DE42761C64B3E3E57BD70751BE5905A92632799ECB3365B019B699E42BFB24BB3F6D91AE5B33E738EC7553E05D27F159AE1DCDA49B9DBF6FAC9F1F89E3B7CE637ED2FA9A1F24F69078D96CEC9B6FDBB69D4E7353B92F179B316FG5D9AD9AD775238736E0C69517ABAFD7A78DA1B1E83659364FCB81D1E8373F57CA041679870D426570E6BB18E343A997C6CD29A4018C803F69640DA009F8394BCG7972811EAF9777558A894E066782F0E48B087255B1459BBF23
	75A3DF2837D83B235FE8B3EA0724A633F3FF53836CFE680171F62072CFD6F0EF7781BD99A09DA08BE01DAE7B8F7B934A5E9AB1151D4AF52D99655AEA42FF629A3D0F0C5BB9736E47D11FBF7C19716E2CE077F9DBFE1CF83433395BE7C6FD1E5A64239BE8139440E78264G2C87481B49ED7221DC1148A67C7C4E0CC1F5FA55993BC6E533466AD79753266372DBE247D54634BBE34651586A9B09D3D310BCCBF37A4A93461C9E1CC5EB2DF4F82E822039F7D9A35FBB574C62F5476727B09E04942845G2AD278B94E
	C2E4EDCB397BF523B61F24E21DF48D46C5GACG0F836482EC86A8F9105B7E06FCB221BF62E12BC3F110A25899FB3AE1B62DEECDF4C7E73E961FA49CD7756058F12C0657D03FB620F981EE8124812C86D88F30CD576FD939B911FE7A958F68484F026355E4723EFE0FFA6EBA13E8DC69C3E6FA8E06D14F8A203581F4815C8248GD88D3071A12E67BB83897554153B9BBDDFC8A82F391EBFC8B8AE6FA133F10B4E5E9B3F47920EAB19EDB66EE25B3D717BE342F165B17C68BD5DA6F256E882DA87G45BDFF8CF191
	E8DBE673F8D7CFE04D112537EF11B6560534DDF0426C9E6BDEA0EB76781C426E633EB49E79A6B86A2F6F86625F639C8A185F639C89185F63D485F6D3BC4CE13C8D66747A404477BCCD8133FB9E3AC68B49B9E78358EA333EEE5B1A783A654D6671B7D3092F4F9C45DC1F050A39BECB140975F9D409274F3B44665F3761DE2206831AFFC0A2D3537DCB0A601D8BE402AB61GCD93B8FBA8F60F95F86E499AC45F5BEF61BA5A03B542789C536A75EB0A4AA76910C475F2CF0F1EE02E86A58F173B76C6FC9BB55EB037
	D1538DF39B1D39B1310DAE5D1858C6AF6CFB1B0C8F0524905960DAC4CDE4341FEAF8FFB56E9E6F926C92823F0F2AE859417A34EA4A45EA0157883D2DB807C90DF05523AA320C378F68BB248E784909A296F37596DE87FC3D8157AE886D99G99969E770FB5E25E4936185781EBCD70347E7EEBD2D7EC34507A5B2A63FFC8701B8DF8DA17FBCF715FB97300F6ED8E77794748F9B1875A39F338BF4CD2632F67DC55FCBD53D43CB789A9E155A3452E59D235CC27C5B726F31AB4002627FDB249F88D8E105238B5FF12
	0C2734F8633355F8FEC1575557DA4C56555523AA832EEAB6F73196DF57BD1A392EE5DAFCDD8FE809F5AD57626BFAD4CB2CEB3D063AF47B3DFE155DE0D29D0F1F613E6B5294D72DBEE7D2B83E0E534366BABE980E2FE3EAB8310ECB4271F5DC91CE2C634A30390E42D9E3FC56686B1BFBF66C7A567033FE1B51A655FA7F57EB71AEDE9607153086E71B39988B7B6AF80D3F91E2A18F60A0606B6652DA1F6601270610CF8FD41660DEA489164F43319B0E71312950BF93E08D40A60022F97CDC7EEA692F0571776746
	84C27BBD51C46EA7E0D1027370DB0B27EEC31371596B60FC32203D86E093C0D12A3B38BC1547EF7660AEE3AC307CC6734D4FCE70BA278D7ADFCB9D1BDF022971724D61C64C9F49293177G35841F1A9AFB8F70978D7C9EE085500A6673FC738CB9A3611D2E2C63BE326B7552FC1E97571F92F419FFE4F86FBB75E555B0E6C0F9CC392FBC689ABF53426E4B2A574EC79B2CA36DACF82776BBDD4773F54982B8A3AC607E770F1538467ABED4A98EE90D0D4D8A7E2651CE1D196EC1536A0A58782485FC8F4AA2F3E6C3
	BB97E05382FEBF98799D3FEFFD7CCF168939BFB13F835F58F15BA6AC247EF1E821617E6BC8F7BE65E9BB4EF9F681BDEDA1622A0E1746686E3452F3B925F70F21E38EFFD5B052D34750D15E2F659B695F9EC3C79B4DE36329EEAFFE48F55B7662AAC17EFAF25CEF04B6B21D8A5218EE5FDDE41D1166FA034E733DBF9A6FCBCA964BB6EB1CF96CECFCDBDA54C654BE8D8EEE1FDBE95CBE3D0ED87B3CBFC67FC50ED87B7C034DC8FF5191EB1F25E36C7BABC72CFD1EE77353583356136F41C01E75E9BC9ECB9A903799
	5A250BB82E2F0147E879A29E23C79B51E7BB74F683E963EFB6347D4D06AFAB0E1007F92C7E5D1746436CCCC377054D7AFDFE8464405F75AC70DC41E422F939594E7B2C86FCAE40960012456E622A45BC7F7F0C7CDEF0393B8FF634CA8F163D6C9BAF4968DBD5A36DF16F62DC0BA94FAE76CCF9FD917B8E23256E5E3E413AF20F63E6FD61F1F43F18F68CEDE0D99CDB374EA678744531F56B53FA9E4C845A4E2F707B641523E86FA034D3GD6G2C8658G1077953E8F665D2E92925C1DF4A841F6A80CE443D1036A
	D7F87C5E3ECD7BE6A14FB7D9CF7F2910BD2DA56EEFD7BB5359B7B56C193DF611951FD43FA6AB6AF78DE89100F400AC00B5G9B5339FECDF7587DAA9E5560E4A6FAFC6417C47D6067170622E8ACD6685C9E9BCACA6718F667C3F83A45BE53D35FF958E2283FF6083D7A5A1FF6FCD15BD07FAF2B476FCD87A5D573FBC41956BD4D226AD2E43FE7D828DBC26A89120FBE86581A4E6366778DA837CFD17DB71500B6FE244046BAC90EF2E01CC2FBAD41CDEAB8B801DC59CB286DA91FF22F59778D9D6C6CCC7D3174A42F
	EBCA9FF69737BF4C6FFE2F1D6439BD79E13E5F4EA4FDD7C3FB9D40E63D2F4F4E7B96AC657B4082FB6CBEB0E7CC9EDFE30F5D87F0FC147ECBFB6CBEB0F6FC452958FD000F27BEF1B19FF337FDA9772D6F5C79EDBC3FG2493E268AA141701DE6E93A947845DBF669B5EF36A6EFF3D76377715873547B7C6631F217EF2547C8C756C28799D478B23935FDB3DB49A7F7B247523466F13685A7D4607EB6B02351381ACGABG3275356C91F0AD69CDF1EC0EE8757662998E3F2654B87FF356783A3A2C093F417C540A5F
	B0F4AB1A268C607C9CFF1D6CD51A92A4C8766D1495ED247AFBFD9A6D4A70FDD60C91D96AA1E8F676EB40731B7167F0436DCB253B75CA47B927237D7AD567154B6D6D4D0EAB579D6DFFF0FCFAD56039B9E348384F563FB95974FBA904F5CB596F3CCB237BAC156133065A89E4E83BFC655CE717AFF518C9C147BF5C0067CE27043FE3F23906A43EE76C7F3365427E83CD365DF164BA1841FE0B4B08561651B8787549C4F9B9A9436C6E3BBC613877CC63CECCB8AE9668DE7F9A490B699931B5C0219D71199931B500
	65A42F813200D676882FAF876CDF14BF752D7F4947B588317CC5F19F78627B7C91DFE21F2F7511EFF940D7BB06037C1E4607FC141E1E10244756A9728D98451D8B3914109F779A9A474F79F10EDB20DF5AA3BCF6F3FD9F89717330CBD502301D8ED3AC79E20C6504FF6FBB884FEC18E79DC09EC049B228DD816CGAEG5FB24EE31F3FFC829E74F0C9F204AA7A839A3DCB4D590B67A8FFC07687A4A77D32DA770B82F4E77E7B1861FD41497B3F97BCF9DFF9307C6D47F7BDFFD333B3DA2E3FD4B0FC93C4BE7722BB
	A5255B7A637B590E7E44FE36275FBC3716750F4F2D6F761B67568F7A0D39155DC932F9D57DC21C62AB7BF11D69BD59C5B1A07612FD077E5F50471F235FBC7EF3E13BA63C1F7013CF765D4128B18767CD75444D415E65B4D7C51647FEF720F411B3AB60F317F39F7E2B6B9F4773E158652E8E08328C15E33FA360451A36F9D813227B2D56057307F89FC6243411AE143B4FB0667F81D0CB87889ACF6C60E392GG10B5GGD0CB818294G94G88G88G5C0171B49ACF6C60E392GG10B5GG8CGGGGGG
	GGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1D92GGGG
**end of data**/
}
}
