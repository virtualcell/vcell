package cbit.vcell.client.desktop.biomodel;
import cbit.vcell.client.*;
import cbit.vcell.mapping.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (6/3/2004 10:10:46 PM)
 * @author: Ion Moraru
 */
public class MathViewer extends JPanel {
	private JButton ivjJButton1 = null;
	private cbit.vcell.math.gui.MathViewerPanel ivjMathViewerPanel1 = null;
	private cbit.vcell.mapping.SimulationContext fieldSimContext = null;
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
private void connEtoM1(cbit.vcell.mapping.SimulationContext value) {
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
public cbit.vcell.mapping.SimulationContext getSimContext() {
	return fieldSimContext;
}


/**
 * Return the simContext1 property value.
 * @return cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.SimulationContext getsimContext1() {
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
public void setSimContext(cbit.vcell.mapping.SimulationContext simContext) {
	cbit.vcell.mapping.SimulationContext oldValue = fieldSimContext;
	fieldSimContext = simContext;
	firePropertyChange("simContext", oldValue, simContext);
}


/**
 * Set the simContext1 to a new value.
 * @param newValue cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimContext1(cbit.vcell.mapping.SimulationContext newValue) {
	if (ivjsimContext1 != newValue) {
		try {
			cbit.vcell.mapping.SimulationContext oldValue = getsimContext1();
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
		cbit.util.Issue issues[] = mathMapping.getIssues();
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
	D0CB838494G88G88GC3FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BAFFD0D45739344FE4EC473E581676110E3E49186405171254B74FCE1DB7CCEBB37E51B4CE12B2A4A5B3501244E9CD1F6950B66DCBB3F92F075D9558A8D1A3363425293E580891B77CD291C1E1D99090E415F585D98D175D8BFB716E8FF7AF0A82526FBB671E3B1765DEB6713D6C4CB7675E6F7C78FEFF67BB67AE29786B835B0AAD950494EFA36A6FF8B1A1BE87A1122D78B10EEBBA18E3A73A5FFE00
	5D24294C86EDB774E5FBF26C3B48AB374B605D876FCD707E87F85EC3FEFE7B599AF8B0211389FD79AF0DCFADCD67BFEFF1BA25743DE0E88734EF830C828E5B2C03C87F57C315AAFE190A67D0ECA1E4C5B50E59B5DC25620AE0CC99603A81FC55EC3C82ADE3601E66A6F51C6F0B1964356F47AC65380EEECD8E396A5C5755F69FF9237801C3C86B44708EF5CEAD3C8781D0F8AB39F438815A96773509D37EB1C9F15C700B326C704B1298D69C81B1FECD09C49DE3D2A4948908F2F3F33F20843D12F8D30C112A8FF0
	5D4F8C1751F588598CEBE6D21CB80134C661F99AE065879C1F93C47CD6F8369D4E31D79D6672359EECD1799F1A7996492D8F59F61A4857F8184965BB1C10AFADD4E079A33545619954F98674E583BC8B709AC0C5CD0E3D91604F54876AEFFFGED7B1852918905A26176E854A987E27DA89DBCB7B7C30F0A3BA07AA331GA14C4E2707EB8C78D98A583C7ECFBC0F69A433015BBC6B4263A47F5F3FEAA9B550C9FE660C252F0669A2D013508923FD2FA96DE5B58BFDF2A16DCBC4CF5BD67E3CF51B816DFDEFBCE74D
	D057495251AE2CC1DB3FC0FDE093BC63BC1C4F701BA97E4011841E7967B50A4777A668DBF5047B46505B1C17BE5B9A322B6E1F8C751071448A8BF1B09EAA9D493CF4C938364B41E8EE57F871D0FCC007E73C64D1BF4577E55037850079D7639C46EB5F0790BFGE9G99G6B81F28136FC48FDEC6676079F53475CC2CCF1CEC9F2G62094DED1C79BD3462349293FA42F1C5887BC52F901304B1D90C93F5CC6B8C6AE0EC41BCA775FDBF50B8A306451820C811B02CAE04C4C50C45591833B33BF1CCCC243D9EA9A4
	8201D01470F587E776C0AB8BF165E2B4A0A8E2E2846B9F19C11B5C903C30A2ACG4FCCAF429D54D7D9ED0EFD9FC0EDAD574373F43D33E2AC0C2923AF9239B6957520A4A42D96753C1A668E97BC7BEA390E2F4CA3EEA33C67EB6B144F3E23DB47A57911FDA1F60B47580FA7F0CE910CAF2B63FE7373A16EB72E7247C84DF3DFB1741BF47061FDF54CDF1A6A126367E7F4DD64ED340E677B6B93C3A962372C4EB8365ED6F9CC1DB3BE1F124636A57A2CA966B2BDBFC550E6E9406BAA006CBA2E6B6E59FD4CF71DC2DC
	645EBA9083C79383894DB3BE7B248AD527D615A7445BB5B9AE537EDCD87BCD008375B976BEG251E6B72C741A113791FA4DFAE2D4B7F22B412575F7E7FC403696C549C6ED7628D58B2DDE2D48C8760A12E76F54EA1BDB51E47A35A1C33949F05786F00000BEB7873F398230222442431A9C59C9495C58ACFA47A07A92D7854D8B411BAB82D51B914B1F29362DF9BAF4EE16C78A36130C80D27CDD17BA7E86C0A32988226BBA2D3E10561C3F7304E39A65ED2F34775BBA584E3DF1E42BC44EA0735826AB19A13C260
	92184A4856FA0C6BF54315BA9D2D829C56C09B204D2D6771F973BD9E1F87EE7FAB5970D154E23447E4BCFC1F25310145CCF7C3A2BE99CFE373463C87E98ED171F146EB1A7296343A72057A449E72C4AF4A9017C29D113082E62AD36343E6413503C0F396A03D01474766F96C6739C88D0C0E08ACB375223FB7208E9C418AD506ACF85F5840F41070199F10DDC8D73CBEA5C20A4724F8D653F59EF9A7294F2B6A1D7597D27F15F579545708B46F13F8BDD9554868B5B6325C55876D3B00E76306A77938CC35FF8334
	79G64A89B57F89447FDD95656BB1AD8070F233251F143A1BEAE6DA80B730C2309F59D00DFF18C471507793857F19E60EA0FB1BE234758FA4CD63FBA047EC3EB52C96106B06D085F0498E8EE6EF58B20F5D6F73EFB39CC9D1FF70C57173DD750E63B0E67582B815A0EF3DF7BC1B92FAF532FE5124272672CE6757350F146476C71042F65FF6326157944B9AB524C063ECD4739BF0C946B773876F8BC621768762A5A6AAD0B5E9799EE2785FDF1DC0A45159EC896535C2EFBAD48AF6E3FAADA2DC7AE52FA847D3590
	68D6B665585DCD087FD290E394ED4C6474B7F0B9D75EFE180CFE65EB166DA6F226B7B17956B5A5644CBAF2544A682D17B83D2D50FF60C40E1D45CAE6702D853142E328E39279D002D21C71714B76B6154E264757107525ABACC675FC70118C0B6F84233F6CC4020F51178BAC8AG636533131C17DC9833231973B2A0631AA108E71798774724A855B94BBF45D7757947554C6A0BD9E8D7EA73EF9B4EE7B433E5CEB3934697B6F37DBE762EDE2EB2619EC339D63CB2E129BC4964E9BB2973235C82CBD12EA6D70853
	D88EE3B61C6467310AD35852BCCBF8BDBEBB4C6926EDDAC7AA77FC4110AE0DABCE2FA5C1B7732582CB76CB1C6E31B02F3323B0A693202A95711766ABF46B9436B01D1535F2FD2D8E57C3AB50A467E00DDB0C0DC7E2A1B1D00DD16A90EEAA8E3A87BA5AE9F7A7BE9F62B17D51FE0C39A9DAD7E2962EBE645564DB33C72F5716792B0672AD5F993484DA19DCABDA9372A9205365GEC3FFF6FF27219A72FB5D1F3EDEDDDDCFF31334037E96EA027124FABCFD2FC452964730A7B8A57E3B5742DB8456B573F14206F95
	417B0E5390BBGADGBE005953BCE72C366019FB4973C18F84D8ACAC48038BB6FADE27DB51A630B72B6F172CFA1FE1FA9889229EB2CEAFDDEFAE2E17A4BAEF6DA71847ECF868327E0E42363B57E2B57151C2F56D2253899BB25DBDDD0ABED85B867B8CC0GE096E0D59B57DB3E35A6155E78D9C9D59813695BD22AB3F8F65BC2F9593CCDA96795A64DE3F29C3063BE77BA74D5346758EBG3A81E40065GD96DDC1E8FEC4ED4723473C25093519EBB120BB94E6F72493B13732BA967EDECDFB80F45740DAE886FC5
	F7C718D39D93D3127E3208EE5CB0166D5DA72E72BB237CF63EF7BFC46B508AA7688B406564E704A7643B1369591473FCCEA35BBD530636D320EFA5C0A640C600ADG368E28ABBA386DE6EDA7D3590EA6C0309BB555E22329B68B5D254DD24E6B6EB012EF4009720DC25FACC0BA40DA00DC00A2008A97176F69543E29DDAFFC9C5FEC89A773DB63DA481F311C3D0B66B91265F49979664E41BF92439A52B925A834CEE73E780F733C0E9CF571FDFE2ECBDFAFE66F1CB32D23B2D45A6BDD09DC3671155F287562AE97
	4D8350D75189FE8D508DA083AC87486A643A5E3F2D75E37AD287603A216EB7D033FA2752CC6B62CD30F6A1G437D4107B8DB976C75DDBC2EB6DEC1DC9B3C7BB45CB3940775BD56668C673D126AAE82679B615BCC700BED9CC9C9436C3EA355DD878253717BA566E774A3A546E774069263B37A09125D8C8FEB68EF70F8FFFB4952E7781E9223B37CDF28BC4F53F3E78C4E32D2C824E419BF0B537B190FDC188AB3B7C14A7164FDCF0BF3F7A7566451AE6EE3BFAE29B770314E0024C4E2FC910FA84486464735E09E
	0C0AFE4E737325667AABA8B5565F4B25467AFB25F4E97D7D2254CCFFCCC7BFD5769203BB0DF3100D88BF137CDA322BE35CCA44DD272D6D301B4BF154EEAC47C9BB562E7148D44CAFA6736A34D7EAFD09286CD1449032235E7550790A901B909749FA014EE7FDE673C76C18FF46240094D3EF99180C5F19544BD82F4553292BCB6D8DAB8C6212553A1574BE6501AED66B96EB787D941F2543339A7851BB7C1B52FA4CADE778D96137B2672BD30974A7F47FC21B396E8B5B0CF57F13B6F35D3F5A16DA773FE9B3577D
	FF372556FDC93B19AF2AB688ED67B6980FC5C260E96ABD1D53DC56D127312C63CEF3D9C34E54325EF01A4BBA67CCAD6B3D4EE4BFE37176646FF752F80D4745B85D08DC02A228724743BADBF754E9BE7865F9A39FCCBB43FCAF7D4CE29FE4F96BF3DDF88648047E0DG79G2F81D4F473BCD65D11726C54B93D445969CFAEF33B6CF7995B65204B5CAE8ED7EA3B9CF7195B6514AB35DD4E38167641731771EED696EEC52694084DEE0C4D60E5FE8752886FFDG8A4072EEF62EE7FB4A3F5DC4FB0FC395874B51E4BF
	5BC36FAEFDFC6E96987FA6C09560DBGFCBDBC460F54607DE5D205DF477A5E225F5E55CA2B0E496069AC574B2066EAD61FDC3C4A6B130DBD3C6EF8F71EE83EF4D0773C76CE2ADA617F820C46131FAA8DA67323346E2C684D319F6865F55ACE4275D05B4B7D7D0BDD667E1A51E56C2F7F5CE53C6FBE5435743E1B55E566E7AC5F2F3C4C73FD9A70373E177BD15DA85639DA2D0DB7C14D4DE7E2D240A9CC74D12FE435F69E2DCDF1FED1AFBF77150C601A55E7F36CEDGC3E7F98DBE7DBE2F41337EE58D49359B5F59
	96FA66AC5133EACEB91B284153C38596C6536363B41F057E32BEFA7766535FF5214FBEE5E5678A563FEEC1BF6A844F39097E1FAC68C7FE8BEC7A7E4AAB7A7EED3A79CC36277E46E5FB732935C47E66B2537FF3387B547F02286D4A4D7797E77C6E3675B5CAC73E6A62BA6ADB1CCF79DEEDD5E1830C4985582B3EEF69CB6C67ACEE3FBBCF6FF14121DA7A713FAFEF1FC3EE992D7789271556EFCC4B2A03F4980330D76D4B6CCF50D24F2F0B6E69BEDEECBEF417738ABE71BCD67B64D17FDC571FDC933DC8719B7A13
	EB229DA3BCC6B6C1DF6DB9FEAF780BF318D35361FDAD4077G8A8148G6456819E6B4EB335A4455E6609C47BA099483A70E5728D7B6E6EFFA65FAD653E386EBF9EA56B6FF5191EF5EB87185D5DEADB7D727D4558B2791639D13EB468DB853081A08FE0AB004D4D657B7C99757E8E0F9DF04A907CC17AD5C4BB4440CE12C0630644E2F875D5FEC62F04357ECA57A2B4D732DF51E9A39C7361318BC41375459DDC562A2FBFC8DCBFF4D95EB311556DE6B2BE60CE64143C6B7B2CEC1DE7C674F71D3B3EA6DB0ACD5649
	D367EF562D43E43AC4B4194E9B49847914723F5BC3B49F7E51C86279273A67F7C6BE7DBD717027CA0379479B674C773A6281633D2EF440F82FAB9FD87A8C3EF7407CBBFA55007EBBBA736F97BD18535DE0AB99E0A540C3G6B4FF35FFEF460FF8975ED833F6E8CCCE0ED087F66D1B87DAF384DE5CDF7277E8F50EAB7FE0B9B0BC009954275B97EC1B75ACB09C4A91261B25D282318B491D458D0957F389B7DCD9647A91A59F968A3FD9E498579361CDF7A9E07696734977707B7E1EC6585289581BCGC100B400F4
	00CC00EC00DC002DGE4906AFA00EA00A600BE0051C12E4FC7AE601E6995A6975FD4GD2ADAE28D2E30204EAD579F878925164FB75425F4C56G2B74C229B2B0B6898537C718507C5CC77472AB404F72412565D76FB4BC6676AC72243667EFBD74BBB15841F3AB2A567D7F63413ABEB2BE9E97B53F31DE5C2561FA626EC8DC4273822B9FA39F619A2B00DF3F1557198A452D835CF3966DFFDEDEF3DEAFFBD3736A779A7B1E64DD6CFBE12F316FA9DE3D6FB15CADAF465D8458F6C89BF7C7438DEB38FF20EBA26E12
	94D0029A7EF314AF44F70BC810636F23FC5D2438A19EE3BA5C300A3B5F0B7A8611031118D6CBFB31A69A17E4193D2F21E324B028DF5347A314C7A9AA68784E52F048B733C51CF07B94EB38E9426D433FAFDA09707E8ECA83CCAB48D4C4062F1AAECF603908047D377905129B50E63DCA36A9E4BB7B7B5EBEBC1D45E3E42776F9360732DDF82321597A7203D68B31751092E6469EE2C3DB7410ED54D4BD448AE61C463F865A74903FCE6E500D4E369054EF00737AFD7175FB7C067F69C1A097D9CCD5DCCC7E867875
	C154079B7072C51E8B664B0EAC914703A1C11695C13E569E8EE0CCBBEFA9E2A24F2F9D447563FC0C5A497A3EB508FCCF6A667C9DD0CB878869E92DEDF290GG04AEGGD0CB818294G94G88G88GC3FBB0B669E92DEDF290GG04AEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2C90GGGG
**end of data**/
}
}