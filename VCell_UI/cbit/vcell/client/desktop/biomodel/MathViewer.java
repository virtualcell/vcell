package cbit.vcell.client.desktop.biomodel;
import cbit.vcell.client.*;
import cbit.vcell.mapping.*;
import cbit.vcell.modelapp.SimulationContext;

import javax.swing.*;

/**
 * Insert the type's description here.
 * Creation date: (6/3/2004 10:10:46 PM)
 * @author: Ion Moraru
 */
public class MathViewer extends JPanel {
	private JButton ivjJButton1 = null;
	private cbit.vcell.math.gui.MathViewerPanel ivjMathViewerPanel1 = null;
	private cbit.vcell.modelapp.SimulationContext fieldSimContext = null;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimulationContext ivjsimContext1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == MathViewer.this.getJButton1()) 
				connEtoC1(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == MathViewer.this && (evt.getPropertyName().equals("simContext"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == MathViewer.this.getsimContext1() && (evt.getPropertyName().equals("mathDescription"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == MathViewer.this.getMathViewerPanel1() && (evt.getPropertyName().equals("mathDescription"))) 
				connPtoP2SetSource();
		};
	};

public MathViewer() {
	super();
	initialize();
}

/**
 * connEtoC1:  (JButton1.action.actionPerformed(java.awt.event.ActionEvent) --> MathViewer.updateMath()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateMath();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (simContext1.this --> MathViewerPanel1.mathDescription)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.modelapp.SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getsimContext1() != null)) {
			getMathViewerPanel1().setMathDescription(getsimContext1().getMathDescription());
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
 * connPtoP1SetSource:  (MathViewer.simContext <--> simContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getsimContext1() != null)) {
				this.setSimContext(getsimContext1());
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
 * connPtoP1SetTarget:  (MathViewer.simContext <--> simContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setsimContext1(this.getSimContext());
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
 * connPtoP2SetSource:  (simContext1.mathDescription <--> MathViewerPanel1.mathDescription)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getsimContext1() != null)) {
				getsimContext1().setMathDescription(getMathViewerPanel1().getMathDescription());
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
 * connPtoP2SetTarget:  (simContext1.mathDescription <--> MathViewerPanel1.mathDescription)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getsimContext1() != null)) {
				getMathViewerPanel1().setMathDescription(getsimContext1().getMathDescription());
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
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton1() {
	if (ivjJButton1 == null) {
		try {
			ivjJButton1 = new javax.swing.JButton();
			ivjJButton1.setName("JButton1");
			ivjJButton1.setText("Update Math");
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
 * Return the MathViewerPanel1 property value.
 * @return cbit.vcell.math.gui.MathViewerPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.math.gui.MathViewerPanel getMathViewerPanel1() {
	if (ivjMathViewerPanel1 == null) {
		try {
			ivjMathViewerPanel1 = new cbit.vcell.math.gui.MathViewerPanel();
			ivjMathViewerPanel1.setName("MathViewerPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathViewerPanel1;
}


/**
 * Gets the simContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simContext property value.
 * @see #setSimContext
 */
public cbit.vcell.modelapp.SimulationContext getSimContext() {
	return fieldSimContext;
}


/**
 * Return the simContext1 property value.
 * @return cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.modelapp.SimulationContext getsimContext1() {
	// user code begin {1}
	// user code end
	return ivjsimContext1;
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
	getMathViewerPanel1().addPropertyChangeListener(ivjEventHandler);
	getJButton1().addActionListener(ivjEventHandler);
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
		setName("MathViewer");
		setLayout(new java.awt.GridBagLayout());
		setSize(622, 485);

		java.awt.GridBagConstraints constraintsMathViewerPanel1 = new java.awt.GridBagConstraints();
		constraintsMathViewerPanel1.gridx = 0; constraintsMathViewerPanel1.gridy = 0;
		constraintsMathViewerPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsMathViewerPanel1.weightx = 1.0;
		constraintsMathViewerPanel1.weighty = 1.0;
		constraintsMathViewerPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getMathViewerPanel1(), constraintsMathViewerPanel1);

		java.awt.GridBagConstraints constraintsJButton1 = new java.awt.GridBagConstraints();
		constraintsJButton1.gridx = 0; constraintsJButton1.gridy = 1;
		constraintsJButton1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJButton1(), constraintsJButton1);
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
		JFrame frame = new javax.swing.JFrame();
		MathViewer aMathViewer;
		aMathViewer = new MathViewer();
		frame.setContentPane(aMathViewer);
		frame.setSize(aMathViewer.getSize());
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
 * Sets the simContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simContext The new value for the property.
 * @see #getSimContext
 */
public void setSimContext(cbit.vcell.modelapp.SimulationContext simContext) {
	cbit.vcell.modelapp.SimulationContext oldValue = fieldSimContext;
	fieldSimContext = simContext;
	firePropertyChange("simContext", oldValue, simContext);
}


/**
 * Set the simContext1 to a new value.
 * @param newValue cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimContext1(cbit.vcell.modelapp.SimulationContext newValue) {
	if (ivjsimContext1 != newValue) {
		try {
			cbit.vcell.modelapp.SimulationContext oldValue = getsimContext1();
			/* Stop listening for events from the current object */
			if (ivjsimContext1 != null) {
				ivjsimContext1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjsimContext1 = newValue;

			/* Listen for events from the new object */
			if (ivjsimContext1 != null) {
				ivjsimContext1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connPtoP2SetTarget();
			connEtoM1(ivjsimContext1);
			firePropertyChange("simContext", oldValue, newValue);
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
 * Comment
 */
public void updateMath() {
	try {
		SimulationContext simContext = getSimContext();
		MathMapping mathMapping = new MathMapping(simContext);
		cbit.vcell.math.MathDescription mathDesc = mathMapping.getMathDescription();
		simContext.setMathDescription(mathDesc);
		//
		// inform user if any issues
		//
		org.vcell.util.Issue issues[] = mathMapping.getIssues();
		if (issues!=null && issues.length>0){
			StringBuffer messageBuffer = new StringBuffer("Issues encountered during Math Generation:\n");
			for (int i = 0; i < issues.length; i++){
				messageBuffer.append(issues[i].toString()+"\n");
			}
			cbit.vcell.client.PopupGenerator.showWarningDialog(this,messageBuffer.toString(),new String[] { "Ok" }, "Ok");
		}
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Failed to generate new Math:\n"+exc.getMessage());
	}
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G4C0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DA8FF09457F917ED89848890A00140F22C58B8A8096C1231BCD5D25C21AECDC82CA4CE86A71A8C1D5286CF6DD6CD541626CC4653BE5D9D88FCE0E4235832AD47E093823622A0CC460AE2EC7DB9C0C288896BB84B3510583BDBC987275B735E5EDF89516FFBEF5F5E6A342B33E9FDB33F595B6F7D797E3F773DF7C76ADEAB5AD5ED29A324FA97D1BF6FD493B254C4487C550B3F4BE93981171D68BE0781
	4F91F148864FA2E82BABF459EB498BFDFB603D945EF36FF459DF046F8749ABFD0FB5429793BE45503638267D705CFC5EF91F7329216FF961FD70FC96D086B4FC5AA464DF922ED769DBD5BAC73505106D344F37428DAA2D8B5E2F820AG25926BDF8E4F3D14F3D6EB0B5AEFE8D9B159719D59329F67514D495198E4E37B5567A1724B6A1DC164D5C56544B159703E91004ADB4907EF3D8D4FF77B2F0F3D67F17B142608C7747B1BBCFE1F98D01A3CE2683AA2851B5CBEE9C2720A7E5656DEC199F7794428A8936593
	1C37A532074E83F340BB111136CEC1DEE5707E9860C90DDE97C13A833E8F8194196BF77A376F2A728FCE7EB959BE76B7FB8F186817A3B33DCA6524FE19EF3A2DAFD1DFC45B5066D550D697F2590F81AEG7C009C40ABB486265FFF931E6DEE25D31A1810826D41E0075FAB7722F6703D3595DAD45A8722C7123D04B0BF7FA25CE8A04FDCE063EC114FBA0E592478ED6E73128F9EA0DB9FDCE929B130495662C94B5A90334506D052A60C77E9D35EDBC3B3E3F2A66F8F091EB7F96AE44DAE835E8DBFBAD9D3254EE3
	D3123CDB947475ABB45E0E40F79C0763997DA825CF6968ACBE6F27B1036F395036C36131B178AC1765026D9ED25B3C4A508E850F64D88643CC06E0B8D516EC9AF705E146532E49321252B76868CC1617A99D5F37C2DBDB44E5E771155101793A965EB782AAGD5005A284B5E88E80B72987B567B3F7F14B156AF48CAC7586777C2BE313199EDAF40D30CA932509D88A9C240A33A8459A7387DE20828FD9634218D5CB346AD21316F819E674400A88B0ACF8A406C42042808F20875D955769C7611C55A6A74CD0840
	E0A2C8783CF7379D0427DF88A91703DEC19113BDD87B3AB674C9C4F0410CB081FCE7F6393F936DF595740F8132B5BB3C5E0E737508F2G170E8B12F4BD9CF422A624A20AF6F6523523903E17C6390D3F6CC0DAC34CE5BF956373FC547E3CEE9E074F03628BF21C675861B00E69027ED7E3BCEEFEB6486346317FFE5278F83EE15C64C28C47E2ACDEF2E3297953C867C5594AE2FC3DFFD099CC133FD7E34639752CAAE37AB523BCAD0FDDF334D9530CE5F6FE71AC7A2C82645981280BF3DB67359FE2315BA104C49E
	2DFDB2841A68CDDA1E4939A4D2275A542A4A04743D1A9E05343D9166F68226GEB8165F1EE4BC311C113711FE53D1C5B16FF24BCD267377FBF71E0B6DB58017B159801AD53A1864500973E045436E59D48CF4D67D1C99B330A5203107F1D10F0A10DFED787662820A8324F9DD64481D1D1FC0131E47BFD14D7A86C8EA6178E4E2B2483F51422107FDA7F2FF5E06EF824C0C0244E5306286D8F525C957D6284885DA905838A23FF3D936B1C6BE2DCDDBB9E6E5CCDB0777DE1DC07D86D30DEC0BB86E55F840484AEE5
	4411403C3E9E295759E887789BEB20FAE8EBCC707C0C3E417373C8624BE463C850E23447945C37541213E0B9D314C866A713698107316C25F48DD169A1A6EB06728CBCF5658B0D090364117328C348B751A98594F0D3331A9F7D67F06E0D40F38B20B24173634499EC67EB111A981D125F4F4C0B71BE09B688066BD49DEC705EB049EC100C19D74F56A2DF71133088CBBCAE0ABD1A2D0F1CFDBE254D255A1D35B71EC5197D3A753414727CC10457130A4AAFE7122DDDEB6179EB2033FEF7C6F9BFB2455A6B61F98A
	B0B8457A65CCE13F47635AFCD349F9F8BF2A5B8DBADF02772B18E2F9DE251B3789685BE93F3EC95E2F9F5E478159B7181C1BEF3079182F7E7DB8468F2DC92F8991A156940AC28E34361E6F97406A2C6EFC1C6E9D587F44B42FAF2B936833F1F84FG94CC73D87B41FEDEDF66DDAFA6DB1FEA2EB12B1F57CFB3B936CCA7E3AD372AFDB70B09B5DD48336E264BFE64A60F07F5E775FBDCFBA8A4F9FCF4FBD5FD65BA270FC5C67B789C466228CF8EA95D30D84438DF05F3A8AF6E3FAAD92DC7D6511A89633585788681
	45B7113EA50CB90ABEE6FAFA5E66FA66A66EA3D77357D86CA6FAD65EE47A6D4BC86A59B83DF39763774B8867670076A9G4B15FF8ABFB3A3D7F88EE5C5D18EE55C97E2F27CDBFB1B4A277C01FBC8D9CD0E45281E9FDFD7E0A9D579EF5549D170C537358440E459946532B45E6632FBEF6332AC0B631C93104F8EB16411FDC1EAF3367E14A5746BCF61ED2C3E58824FA735719B8C47B31E7F9567BC496DAEFB4B6D5C3E777FDA2F579EA14BD02F1C2746AC415B19BE8537A7752ADB61369EDB21691560BC36C11F7A
	BB78F92C6EBDFC52F51670FAFC6AA34EB7233C14549FDCEC481766559D0CDF6C0EA45FEDC5EE6B0EA24EB7B6496B6C4D5007E442B9AC936971C81DEE1E16CCE6332B195CDEFFB7F9921E82DD641A5823DF14C7A5F9C2749E43ACED9222CA935D831B5AE973997CFE1C677448E14C39B02DABF195BEF65C2569F74FC13DDD5F3DB9EC28DF7601F14B06CC2657764C24FEA54B5D560DG365F7FE4561967C4D63266F2E44D2E3F58D9A0CC7DB11815FADE4920F4FFD66AF925A6416D1881ED5B33F87D6A6B41586B1A
	87318A18826481CA81DB6671B56329F3F8661E73FC508D89A687847F400C0D1E5769DD6893581B5577FB3B74B1436CF097BD2FD64D1B3B5E1CDDAFFD150E5BB37F530FE3BEBC7EBF7ABB8A5BF367ADD61398EDD1676E1A1F74A1335D4B3D980359C0DF8B58G588258B11F5B6DD8F7E3BA3B713312EAB026930926BB0357E54F54170DFBB66D381614F1CC0F60C55C677A214D8F48849481B6813681EC8B38BE116E0EF47A3473C250A9E9DFBBD30BB9AE6FF7EED14F0A34639A964C9C47F2BAF29652FB565D912E
	29CDE3E11F7E3208EE5C50176D5DD589FEE7F4EA815F3B3FC977B8BF3CE7828A9770B302ADF1EBBABD1FF6DC6982A35FBD51073EAB0736A7D13605D03B815AGC3000C055CF71B3B7F144EF7F481843FD1D74DF61A6A33DB542FA26D382205C67A0917D03FB2E85B8228846CD984F5B9208B60DF4475FB79FC5A58542E97BECDECCECF264A1B39E826FC46FA664C9A57142A67A22358BCB07092B12CA1BB420AC26BF4960B6B35BA32EC915F67EF1C55570B9B8E5CB02D232AD45E35B949352CCDF256B0DB9F9D20
	6BG3479811900A240A640B600EDB137754479531FB216BA015685F53F011955BB15FF257A9C0139DBG0C763D98520660DDD94C73EA1266E0813C17EA34F9140675BD56660C36BF116EAE82479B518BCC6833FDFCB4AD8F337B0EF4F79D88E6E339477C0C9E6FB1BE235F6CB1BE23E775BC4768B0077E860F37AF6E197B8C1F5FE3F406FF156A73A38B2E03B21CE5FD93A2E54B62D90C9DE6B172C1B8404284B907D277BDAD4FC71DD813EF56E26C2D8BA78DE26C0C5727C8B21F44A98AF25F6828164C83C15143
	E5FE3D575CFE47FB0D6D777BDEE37B7D21F7EE7B1D6EB5339F3351BFAA3FA286F79A97E1B5A27CCC72CEF42F5A27DF117A1B353993974D75386D22319E73AFE26D9A124232C7CC15F54945FA2DAD19155D0AB8016228F7BDF43CA248E362ACDD3FC04733B633716BAE627A6376F9FD32FA4B40F42C0E69F5BC296553FF4C39B7ECB748CBD66B3652F35F4E452C562D56681D14EEDB1224339A38DC7BCD29965A14A57C2C702784176B0CA275A6ED7F73BEF35B7F224F5876CF77195B5E5A1F5E767B7A4DEDFF30BF
	3D6D9B7A4DE2D17541241D7BE0D416A6A05258B86912392E11CB463ACEDDB257750ECB69F54D39E42E6B0ACB69F5AD3C149AE7AC5F3E7D42811A2F211098229B11C3D084D57F07A6F53E6EEC56E270A1435F1EAA1630582BDCB2BB86593AF577E5BC831025D0F781CE81AEG7CCB79BA765E87E94FCEE7E2F31C1DCE8C187B651D81E33F1C9EB0770BE3A03DDFFA874C7D629CC86F17AB83F347601EA85E4D7A053894D6A0B717E2EE3E9665F7A0B9703E96D08E5836141D6B591E7235A87AFB942AB8180EAE76DB
	F2F16C93B1BE368D5E1DG851087A84D65B97E3BC63C3FCC29701BD95BB3340ED0AB2DE62643B567FE3D8E6ADA4D6A13FD133CBEE9D8466B0E368851E229DB777D0761F435427F850C47BFFF2EBC184E6534CE73033ED34BF81D569C65F648DE265DAFDEB60F5787AE9B47EB69E563FD779B17675EF79F39EC96E7EC3DBF97656BFD8548D73B1C47D173D52CF335DA9BEF02DADB4F49BEEF07B0F601C6A52B3517D27DF0FC57F2FE6EFB184E19816F8500754BF98D9EBB4AEB7012AF5DC39EFB42784EF61B7306E5
	73F2F5CDD92E3BC3FC53EDE5BC6DF14E73588A177D6A8A247DB12E3F6B4218FD544A4E952C7D7A0CF634891EF3136DF74DE8C7F97F5626EF7F7E0C76DD3A71CC37C7FF43F5FB7A5175447FD026697FB90AD7287F85D11F073E373A3A3220FF7705BC64734624C36DE74B1B3D1E723D5A2A229E7AB482FE253E3765A577F31637BF0D50FBDC88283CBC7C6F4B11ABA8AD635595653CAACCF8D9F52804BE15AAAFECA379C9DEAC47FA535EB99867664737B8AE6FB30FE3354F529846673E7C541A2800526B73D3EB22
	3F56F2648834E56773FB411AE1DCD3AB61FD4FCA177D902085B088D0D672DC9FF41DA0E976B627943CG0B01DF173ECC3F5A782D7D4F642FEAF8AC16FE63AB24EC1E43742C1B3D12793DD8FD16152F2E46277A5B48886AD781EDBBG752BDC7693G87E0E895572F48255E5F613183CE9902E71C7EAA229DE2E0A7C912F1C3E2B96C1C64E774A04C759A1D0B10BD04FF2A5EB72231980E89C453756F77F1DD9B3EFAAFF17C58E1F943C45762D5CC471D2B12EB4A6068293DEC1E6FFF2C3F6B2CDD6337D41B4CF322
	000DFF37A0B98F53A99E55F4AAB0520990A67FEFBDC40B617CD8727B97F45F3F9D7D7C7744FF78DCF93078289DB65F6BDE9CB65E6BDE9DB65E6B5E981E7B8C7E5630796F68A707753F2333787ECF8F2E6945602BCD00A781CF83EAD773585EB87CBA2131ED9057E73CE3D89B623FF9944E7F5E91F3DD3FB2127EBFC08F0E60EFF1EE89CA2C891C1F533FBE027ED224A0A5B25ADF0E200DE45F5838423A2A74CDA3348691C7A91979794014FE9DE9847D5AD64FFD0F436CF3D9447D4189FD0300BCC089E0A32082D0
	89A0EBDC76BAC0A34081988478819900DC40DAC0599AEE4F07855CB3DD423559B795C0D40B8BEAD4D97021D9D5B97EE214E87AD95D3FB11B833CF29ECA15BE77B5D8E41C4218966753B37609F210E75B1A3975E73C7E5BE36E4F5D5E747EBC60253F93039F1C7120DA7757F9312E17C6C7C32296B7AFF9EBB5DAF728DF8A79703C406A4723D3B8478E1057E365F566DB14366FCE177DF18B1F43A71A4B9A94534B9A970DE36F26B8BB76B2C56358DBA86AE30F51160A18F7E3605BC12DDF1EC67BC8232D26F3A26D
	C31FD7995768F7D339105EA5A2C36DBFB3D42EA8258D729C5351BED2E9FFA6223DC165F1C956EAE991EB22D11F5F4F5E4BE89FDFG4C2F5963E1AA23AFA86864FEC423215C4C9779D16E1FEA0D36A64A7D43FFDF34926168BE4A83DCAB78290A0C5E905B1F24F395897BEF736E0F7110D472AFE417C26C6C6FFB0770F4961249816D6759EEAA7636485B2D561F5EEB3590DBB7594D5C58CDEC680BEE320B3A2A1BD8410DB17CEB20CD0F50A7E41AEEF43619D0FF833C295F97F7E6F98C7F530398ACE4B965AFCC7D
	8DF0688A5A2398681B8A79DAD0B2743BB972F8E0C2707B9541FF3DBD60451C6E08ABE2F21D972E607CA15EC7EDE4ED63D7D06EEB3AB17F8BD0CB8788E6F629A0F690GG04AEGGD0CB818294G94G88G88G4C0171B4E6F629A0F690GG04AEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3090GGGG
**end of data**/
}
}