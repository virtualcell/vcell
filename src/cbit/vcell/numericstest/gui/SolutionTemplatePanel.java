/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
	D0CB838494G88G88GD9FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BF0D457F5A445B1CC6984CFF0CAA819E89C3B410EECABB12434A5AD71388C2E5506A83803B350B0B60EE96B5AEA2D3844A153271530A4A330B08A48368C42DFE15632FE0010D620552EE0A52D24C592E2850B51536E1334626D473BEF2585895AF36E676D5B55DB2D51581AB9735EBD77F3BE777C6E5DA72174F05A2EDCC329A0646E92585F49DCC198BBAE88CD5F4D74F0DC43C7D9C502666F8840
	BE217455FCF85A20EF7D652C22926140E4A134DD50DE826D03703EDFF8FB52D88FAFC96864C35F6421872F4DCEE7391353A9A06D879A0B613997E088F078CC7F8869BF5AD8467073991EC32EC19096D76218AD0D658C178DE38A81E787982A24632370248C5C51584046396EDAA1647CE348308757512C49E1931B3B13BDAB04D7F26FBD0A34DEEDAAE6F36A21AD832070F9C2D8B989CF13635A68E97730D7291AF0CB32DC650F782410579DD62430D2B59A71B6B676856408628D787BA5DFD0969549A17AA5D928
	7898576EEEAAA4EB8942F6D8B71D602A4FA2BD8F3CCF83AC7918635F6DC4FCB63C979E4BAA2AB846E5EC7E4844E4F0CE7D15303156177FFA92999B0ED159060E45E4FCDE5EE2F8136C4766C1547BF268DB8B3091E087C019B12B288160EDE2874FB8BF0627F9D83184FC3E005F9C8C364A1ED0D740A349705E5888BD8C57AB3983A130BC3A5745CDC69DFEE683BA6F2D5B1EC7F532220E6FFBFA6FC34266C73EEDA85051496695D38607116AE24C985389255D17147682E33CDD46533EA8E8E99B1FFAB2EF178EED
	5B3AA7731633F5B2B4341FB762DE3FE6433D5EG6F39ECDD0ABFC870551F447054C6CF901B41369D7AB2BE6136615C4BF931675FA314547C392E9E16BE3A48D0DAC5F9B0D6A572320C581D330A525C2372F2BF41CFEB701417CA0247F69A7495FF1AD5C46D6B594B6833D1E8AF81C887F88CE09B00D01DD5D4DA4DED4CB878699734B10798D2DAA3DE59A30584BA77594183701422CAC8EC770795516F16864410D79C16253040463CB008BA980E1B779231FDB750B8A7792510087E8A2B0BBEC911C2E1BAE66760
	BE9C9312C8EF3F57879EAE7A0282DFB77F52FEF84AE2D8399074001F47C6507E62CB38A79362G2C888B40BB554B9BD7D0DF75A03F9D40256A61CE32DE279402F05158E88F842EC5027DA80910DE0DFA9EA731A3886F8B2A390E77FE0638ECE8975670F57E6E529B1AF52CDEB732AF062EF39FAB311540339C4637G84811656F07BF941496D473A67C14178645D3A763386ECF9CD8D351B0DB509FE748ED99FF95C89FD6535D9C526DA1E83CCE71DBAFE54D2236FDFDAA8E73CA66BC799A80D8B3AB4F625D89F81
	63F241AC7DC56A9ECEDF45BDF401DCD1002535DC773F3BD4C1ED39D58CCB5CFAFBC2E0F812A73693144F344EB2C68F57451CF9187951EE016F5543E4CC862C3F89C0284BAA2A84F0GC46B383CD51DC389FC72B5F73375D249FD1851CF9D1F5BE7504AE33A65FCFF11F8BD1B0E9128AEDF3C0CF9CD1A107C0AD58ACAFE8F3C04D95F4B17F19D6677A381F54ECE028FC21C3000E306D53C41053EACAACA48BB9CD124BEC9D13C7E51D87FEBAE24950E8C87E3A106535A67C279821390A7547187DC68E36E005FAF11
	CDD527307EB2626312AC7900E9CBA062D7A87E50952C092EC957D90CF9774A2B824688B902710A569999A26AB198727A40D4B0648959F5687FDFEBAA5368A88DF0D8AFBD864FCDF55CFFA75F637E7B7E0D7B0535D703863DDC34743EEF9A9A6A29AF596BE37ECBF95A7F19BE6FEF12D84370E14A2B397B0F70542FF828ED6C97FEE2C05B8AA70CA1114D8295D4AF6EEC0868160E7FC3C1B955EDD889C55C0A64C9DC3E060E7B2E69GDDB7FECD12B8381F2E9AC2D9E410F19E4052FA6E27F7FF067DBCC6B2873584
	E419EEA774159EC71D3BED259A3DE554D31D8B50B71F74F7F770FE9734156334FF2FBA7F658E6D7CCCCDBF65636BD7B1D606254FA39224AEE43FD3350D05D75FC8689BE0F6C27B6F3A0AF6A2EB72444693C87359CEDEAB171E20394288CFAB409840DAG67C99CF7240B0F2B00B646180613B4E65B60D985789CB26EC5F55C3DA7A97FEBCE52383E8D1E07GCF697EF5B79F5752906B47586FEC20B2D33A8BFB7838F98DB41EAD550C0F51EDF670F1698DF49D6CC7B99EEB20F4298D3EF4947D025465636204982D
	8ACF02EFB7B6F690B324F577FF105C0963B7B5707A7A6099340D12D34083C04BA96EC33F58436B6BA557D688053F5A18176C8C61BCC5651ABE957321155F1E4C2336773FA252DC83FD1927385D958FE9F33CB99C8E383D243CE0FB7A37C3DA9F233835C32F41F3449B8AAB6D60AAD13E7F6B0610DF2CBF981A696367A7AC42DC4EA9B3BD126AEE190D6BEE2B2A231BE735BADA737ACD436EA4BAAAEB243AE9E85454192BDE4E2335E324231879FD947AB3G36B7A27E479D98A7514EA8BDF79D27377846FD42505D
	4B1252B3B63138569423B73F74F89E55CDC744B4F75D1048C2795EEBAFE6BA198FF456G501894A1F82F5F2BF8C559FBC32ABCCAE37742D6ED6C4E0171E5A630E9G07091FAFEB6D1D445FA15B0D0891D9615BD0E9C7332E12C57FE89549EACDF60D1DD7A948DA550C4DEBBABA40766F81BBBFF30E1938BE9FBC5442E352C3778805629D863DB36422FF9BB52419289E571BE27A34ED5CE2F081D0DDF4F471BDACE90658514CF57123AE5CC3B1B4DACB5BCF1136B2668DD39E2687B98F73B2D78AE57B7FCC178752
	5FCCE92FE80E71D07A7496C365531C0732EE4E438E98D3FD1A73303E83FDE516DC42F24E773B63F97BEF33D6BF2B8B966972B676032506315314274527E33CE582DFDBD55E1ED6F95B8AE34A4EF05EFCBD29F82373FF5C4367BBE06E02B35CDECACF6313548E82BF0B0DB8785835B0EE07CAAB2795477AE8EAE435DC760C73EEF5CB2C3E36364C2C3569B970CFC95CF43650F3E01E0A3F1B6047B4F84A53BBE7D49F073E4DAD7C6C325E03BE51E206B39000823088A09DA0534C63E537D8A724B89B367B61486697
	653E38620D1F510611AE54DB2CDDB82C5599554377ECFA677E6DE6F6BFE41E29874A7F09914C6DA52D10DF812CGAE0069D64E7BB707D3725E9655651D72B55501FCADEFCDFDB60A2F519712F9ABEFE39E3579231734F7E7797BBA8CF9C97CF19BDBBB27B5E673D4A73F9EC51D54DBA02E810C814C83C833F01DBCB5ECCC2593FEFEE74A20B2155A9377E82DA5DE3E4D89EDBA6F5D99732A925A02B5FE9E752D7129A4B5850933B426E71E514674B2AB2D352C70F48250587B33D61EBB03D6DEF75CECC3FFD8816D
	35G1B2C7C9C77B061B55B7A45770D6A7C38976B247CB6208950G508F9085D882303A0D6B7E83F7D3CAFB4443D6FF40CC0AE0CB626906695451156A0ECEFFAFBECBB9EFD3DB7CBCD6430F23FC5950D7FCB62B68FD0096G8F40BC00FB4FF279A65D2D2964D32FF8C0C6FE961CA9A66377D0775C64B4261C37662C1E1C9F4BA867C6684B86A8BC87328258GBCG8B4EF1B91FC8AD27AA5C9711F37B9C65FCB9653C34F3FA73D6774C0D5E3A1473D66B52139DF323F7A3653C8D897468BDDD0189FDFEBB746D8420
	38ED4D082B3001FF5A383F5FF586EB8E175A7E8B52463A10365FBB23F70FB597C09A09B8F69F772551485341D1FB7D05A779BD4EA60F7EBD4E3FFA746FF136F976D1BC2C213D8D667D4FFBE63F674971685D7350BD7A89B9674C87DDAD3171FDFB3B1D6FDB1A0D7B5F69116472340D684B53B922AF4F79115965999CC9A64FBBC4677FE240DC920A78952FCFA2CB537CA5C5714E85A2C1EF44CF43842E9ECE4C31820FBDE3BE343D47D49933C7EA0519B1264D63D582A13EC83FA406FAC6C654G5397145C1C6F67
	C6136B6805D1FD9D650C6A6B68155159F5E4981BDDC70F9FF81D4C8F0725B0C9F0D6D191D95FF38A5EDF4D38476B06ACA1707B28CEFB919B5326843AEA84DEA30C36619ABAB5C21FBB9410E53CFDC05BA1B9BF3F2510795CD69B2F83B61AF16F4A5A613C82E0EB67FE3F2E95634ED03BFE9D206860E97D7DDF242ED850CE6B6FDC957F8A41AF5160E9DD7EA7ED7CF74EB4684B6F60B63F0E1C9747209D6D6076F0461BFCBF4FFA7577536E45FB13F0A092F2CB09FB5667ADD37BE20929DD11FC28FABA46C566ABF0
	0014E66C391B4C27FD49664B5EE4F6C177754E2E5DECDF9DA321004FD143567E7B7164322E9B5717B5F3BC392C3F9CCFAD6B66716432BEB31EDA566747D116E12F479BE2B718D4468B5D5CF69DCA40D1272ED9AFA71731C9561751ACA71751A62716713C1CDCC6271CDAC6D1561771F00F56BFEB557DFD31A7FEFFEB79D95F2155C90DBA7E10896F62E571FAA02240592683FD61380957788B201D86309EE0F3872D75E99C7851A45299014A924C0B84418D1DB877774DFC2E895A8E00A040E2000CCEFEAE7FED69
	DB424C7BF3EDG21637E49C66EA7E0D34A6C782D05849C8A0CCEEDBB2763023EA040E2008C008DF66E3F32EF0F5697D8FC237126280557B959B0BE3FABBE3E14F5A50BB736D60C9F462E44FB00DE02B7F5A55E837C4A4C6F816C50175145634D96F2C642BB5D52EE0E6B302A75D2B70F0B7F53A628BC8BE7E36F8B5B3E2C9AC68FA80DFB3ED29AF40F375859FDD90D520DBAF80934DD70CE75F745446375EA40E5F7F37B7BCD956E311A072A44C9253131B5003FE95829B1539CF49F29CBF1FEE58F4FC13FA4EB8E
	C1BB8A3038075F8FC6BF64772D69FFF90F3073C97DBB702D7DB78DABFA28FDE476446E82845F968325F964B427D96800F31B83F1BD27CB93E4376452F3B96D7FDADCBF46705F8A5A7EC7627A115FC772347D4F4675230E3E4566D3591E78004B3673098CC1DEB5BF69B7C28B9C6CBBA576DC3C6DBB39415F5C48236B7C7C32763E24E419EC48CD324EB6B6BF5B915391550F331D6B677D73DCBF5FE8CF544F11B879D737A76A679903367F5F5B9375733DB87D16F7A46A679F587A5477AAC82D6282FE661D677E78
	2999F1CB203DC645ADB4F39F5DF01E7BE8F7AB5A6CCE356DA4ED7C4D0636371A3FACBFC29A7A3E7A3BAF0D065E190666055D969EF3ABFA330A70F73D86F85AFBA9CFB4AE0BD6BE468378A840D20055G1BFAF97CAFA2F767BD436310512A5CD87632EF3CA42DEDD5CF59135E456DE0B48B7B58FD659F17316FB05C2639FD0375799C67BDF05B73E83E382F99F5505017D837BEC470563E443A75A9B58EBA20EFE59F3FCF96A7D05FE57DD9C575GF6G8F40FC0034FE1E875315EAA1455DC9FFA0E80742C8569CB5
	28FC9F1E1E5B37E9BFAB6071E6655FBCA02C7E06B569EFD7AB7B59B7B56CD9794CF7F271C9654B19C47972AFE495D5GD881DCGC100859738FCB98A3BDF45239A1C4CC4779879A5D1BD78F92549989A0B953A76416B48A9DD23B17281BC8744F15DD3DF872498EA2F7D62283A77D75AEFD7B7547ECF3660772693D2C8713AC51956BD2DE24891103D6E6B42668B241EA0716893006597385F1CB5A35FE3011077C6402F4C1CA9303943A4C61951CF87320A1481442519ABE661EBE8006A1E52297468FD5F504F4E
	4E549E6BAC3C2ED983F3F386785D6F140547F663C51EEF6FA7E3BD501E86D8F2110F3D534A4726DF64F9E015B5B18F5C9597471F33A666811C9F6BFF33ADB18F444F6FECCB4C83FCBE35095C9B983B37DD6436F5AB72EEB23BG2485FC288F4ACB7FA837099FB68B2AFD2C543C7F5A745557EBBB3ED29AD49F6393494FD01FCF681F21A6A7746FB8EECE4CFEEFF547C4726F1396CDE83FCF22FBD7BA05FB3B83762E54893989408EB06464FB7956A46EA53DA9CE0C91ED1ED1BC436157148A277F0749643266C7D3
	FF0339A70A5FB08C8794A5604375B9FEFF9477CB89848912FDBB95C59D053C23E38A9D4A70072268A332B4C2506C6CE746731B7667F04D6DCB55C0DBEFFF3B45EC3F5AE7696D315BDB4D3DD74D760B6641BE01476634C9ED1EEDF86D6B3A5FCBA1CCBBD98D680C65D94A43F5F35DACBC58FAFA5BAF75F4776BF1C1677F540C67CE0B043FE3F23EA6A51EB34AFF78FD217CF00BE1DF923E56B3FEF21C315AB2668727AC2962F265205E5D773914738E697A5D3C14739201667AF72CA4578FA656G9F923CE3B03186
	F85842EBG97742D9D647565A26B6D522736356B866EC11845AF0AAB1FCAEE736FCD2536F96394791687EC357FFA105DE154CCA11D4048C8D8D2FD6B44D4090AEB8FBB82E1AF669A6A47BF1D46B55E3F84B96192775D47270E89496330A39488C2BA3DCE31640BB1969332EFD640F388561986C883D88D1089308DE087C049E5CE6375694AD9E85043A50991A1516BD768DD6A5A7DF80E727AE52FDF3250AF2BD53BF84FAA286FC64D7BAA4BD71F8B7E79AB25C1757568B43E2B711B2A1D75BDBEDDAAE83E89A21F
	FB51CCC97BFFFFB3391D99EE2536335D3774E36B3EDBB3E36B01DB7A31357C16B636D25C9136EEC83D9027780FEE61BE53FB32AE51AF0E123CC37FEF6871FC341B747F94F6A94211141FFC326F8E7ECF9B03F75C61CE9A030BDDAC4639E2310E5A766E82F2E685FC5445ED78722B1FA433E148F2FDBED11621F23CE676FB3026ED3D2EC831FCDBD2006B07799856C97B8E96A05F631AB97F8FD0CB87884161FAB0E792GG10B5GGD0CB818294G94G88G88GD9FBB0B64161FAB0E792GG10B5GG8CGG
	GGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2192GGGG
**end of data**/
}
}
