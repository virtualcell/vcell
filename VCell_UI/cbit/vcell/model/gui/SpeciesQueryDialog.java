package cbit.vcell.model.gui;

/**
 * Insert the type's description here.
 * Creation date: (2/22/2003 4:08:34 PM)
 * @author: Frank Morgan
 */
public class SpeciesQueryDialog extends javax.swing.JDialog {
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private cbit.vcell.dictionary.database.DictionaryQueryResults fieldDictionaryQueryResults = null;
	private javax.swing.JButton ivjCancelJButton = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JButton ivjOKJButton = null;
	private SpeciesQueryPanel ivjSpeciesQueryPanel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.client.database.DocumentManager fieldDocumentManager = null;
	private long fieldSearchableTypes = 0;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SpeciesQueryDialog.this.getCancelJButton()) 
				connEtoC2(e);
			if (e.getSource() == SpeciesQueryDialog.this.getOKJButton()) 
				connEtoC1(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SpeciesQueryDialog.this.getSpeciesQueryPanel1() && (evt.getPropertyName().equals("dictionaryQueryResults"))) 
				connEtoM3(evt);
			if (evt.getSource() == SpeciesQueryDialog.this && (evt.getPropertyName().equals("documentManager"))) 
				connEtoM1(evt);
			if (evt.getSource() == SpeciesQueryDialog.this && (evt.getPropertyName().equals("searchableTypes"))) 
				connEtoM2(evt);
			if (evt.getSource() == SpeciesQueryDialog.this.getSpeciesQueryPanel1() && (evt.getPropertyName().equals("searchableTypes"))) 
				connEtoC3(evt);
		};
	};
/**
 * SpeciesQueryDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param modal boolean
 */
public SpeciesQueryDialog(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
	initialize();
}
/**
 * SpeciesQueryDialog constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public SpeciesQueryDialog(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
	initialize();
}
/**
 * Comment
 */
private boolean bDQSNotNull() {
	return (getSpeciesQueryPanel1().getDictionaryQueryResults() != null);
}
/**
 * Comment
 */
private void cancel() {
	setDictionaryQueryResults(null);
	dispose();
}
/**
 * connEtoC1:  (OKJButton.action.actionPerformed(java.awt.event.ActionEvent) --> SpeciesQueryDialog.done(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.done(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (CancelJButton.action.actionPerformed(java.awt.event.ActionEvent) --> SpeciesQueryDialog.done()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.cancel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (SpeciesQueryPanel1.searchableTypes --> SpeciesQueryDialog.firePropertyChange(Ljava.lang.String;Ljava.lang.Object;Ljava.lang.Object;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.firePropertyChange("speciesQueryPanel1SearchableTypes", arg1.getOldValue(), arg1.getNewValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (SpeciesQueryDialog.documentManager --> SpeciesQueryPanel1.documentManager)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getSpeciesQueryPanel1().setDocumentManager(this.getDocumentManager());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (SpeciesQueryDialog.searchableTypes --> SpeciesQueryPanel1.searchableTypes)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getSpeciesQueryPanel1().setSearchableTypes(this.getSearchableTypes());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (SpeciesQueryPanel1.dictionaryQueryResults --> OKJButton.enabled)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getOKJButton().setEnabled(this.bDQSNotNull());
		// user code begin {2}
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
private void done(java.awt.event.ActionEvent actionEvent) {
	setDictionaryQueryResults(getSpeciesQueryPanel1().getDictionaryQueryResults());
	dispose();
}
/**
 * Return the CancelJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCancelJButton() {
	if (ivjCancelJButton == null) {
		try {
			ivjCancelJButton = new javax.swing.JButton();
			ivjCancelJButton.setName("CancelJButton");
			ivjCancelJButton.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCancelJButton;
}
/**
 * Gets the dictionaryQueryResults property (cbit.vcell.dictionary.DictionaryQueryResults) value.
 * @return The dictionaryQueryResults property value.
 * @see #setDictionaryQueryResults
 */
public cbit.vcell.dictionary.database.DictionaryQueryResults getDictionaryQueryResults() {
	return fieldDictionaryQueryResults;
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
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSpeciesQueryPanel1 = new java.awt.GridBagConstraints();
			constraintsSpeciesQueryPanel1.gridx = 0; constraintsSpeciesQueryPanel1.gridy = 0;
			constraintsSpeciesQueryPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsSpeciesQueryPanel1.weightx = 1.0;
			constraintsSpeciesQueryPanel1.weighty = 1.0;
			constraintsSpeciesQueryPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getSpeciesQueryPanel1(), constraintsSpeciesQueryPanel1);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 1;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJPanel1(), constraintsJPanel1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
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
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOKJButton = new java.awt.GridBagConstraints();
			constraintsOKJButton.gridx = 0; constraintsOKJButton.gridy = 0;
			constraintsOKJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getOKJButton(), constraintsOKJButton);

			java.awt.GridBagConstraints constraintsCancelJButton = new java.awt.GridBagConstraints();
			constraintsCancelJButton.gridx = 1; constraintsCancelJButton.gridy = 0;
			constraintsCancelJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getCancelJButton(), constraintsCancelJButton);
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
 * Return the OKJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getOKJButton() {
	if (ivjOKJButton == null) {
		try {
			ivjOKJButton = new javax.swing.JButton();
			ivjOKJButton.setName("OKJButton");
			ivjOKJButton.setText("OK");
			ivjOKJButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOKJButton;
}
/**
 * Gets the searchableTypes property (long) value.
 * @return The searchableTypes property value.
 * @see #setSearchableTypes
 */
public long getSearchableTypes() {
	return fieldSearchableTypes;
}
/**
 * Return the SpeciesQueryPanel1 property value.
 * @return cbit.vcell.model.gui.SpeciesQueryPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SpeciesQueryPanel getSpeciesQueryPanel1() {
	if (ivjSpeciesQueryPanel1 == null) {
		try {
			ivjSpeciesQueryPanel1 = new cbit.vcell.model.gui.SpeciesQueryPanel();
			ivjSpeciesQueryPanel1.setName("SpeciesQueryPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSpeciesQueryPanel1;
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
	getCancelJButton().addActionListener(ivjEventHandler);
	getSpeciesQueryPanel1().addPropertyChangeListener(ivjEventHandler);
	getOKJButton().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SpeciesQueryDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(587, 369);
		setTitle("Search For Formal Species Definitions");
		setContentPane(getJDialogContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Sets the dictionaryQueryResults property (cbit.vcell.dictionary.DictionaryQueryResults) value.
 * @param dictionaryQueryResults The new value for the property.
 * @see #getDictionaryQueryResults
 */
private void setDictionaryQueryResults(cbit.vcell.dictionary.database.DictionaryQueryResults dictionaryQueryResults) {
	cbit.vcell.dictionary.database.DictionaryQueryResults oldValue = fieldDictionaryQueryResults;
	fieldDictionaryQueryResults = dictionaryQueryResults;
	firePropertyChange("dictionaryQueryResults", oldValue, dictionaryQueryResults);
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
 * Sets the searchableTypes property (long) value.
 * @param searchableTypes The new value for the property.
 * @see #getSearchableTypes
 */
public void setSearchableTypes(long searchableTypes) {
	long oldValue = fieldSearchableTypes;
	fieldSearchableTypes = searchableTypes;
	firePropertyChange("searchableTypes", new Long(oldValue), new Long(searchableTypes));
}
/**
 * Comment
 */
private void speciesQueryPanel1_DictionaryQueryResults(cbit.vcell.dictionary.database.DictionaryQueryResults arg1) {
	setDictionaryQueryResults(arg1);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G6B0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD8D45715EA50D0B5D1A3B1E8B1E1950D26A41FDD5DAF34EBB6A6B5DB3735A95D5A2D6D7A6D3A29E9F44B266EAE7B15EE5D4DE54000459F22D4E95484FF12C5E308A8C9DC9485E64061A7B28A8E83E21CB7B38F9899E6461907607F1EF3FF5EBC06B78C3A8D5FF73EB7775CFB4FB9777C5DF36FFB10027793B7E69A8A88495CC8785F671904941EA4E445C3E326895CE42FB50FE87E76813CC3BA2F
	E643B3917AD267D9737249775AF2213D885AAB1E36666D025FBB488F5B8A7E97FEC46013F60210E5BDBFB9B6BC1F1F5F95FCF2E8FB2BF1B3BC378224G8E1F59BE647F21311063D7F13C00CC83A12B6918AA639E0EB3C35B8E10G30404746AF0367A64AF9EC7989BE2EE34ABC127E3D00E18B52515094D06CE7F39B7933087CE7E63A9FF9DD25F2621C98E8AF864045E711377E78B1BCCFD9FA3ACEBB9DEE25643AD376F8CAFAFDAE59D3525567AEAFEF764BCE379CBC5FA787EE185C12475785637D5710661F9B
	F3A98DC2CEC0FB83453DF49179AC025F4B81562878D8AB62AB8356BC9B00BFA056D671BF27386C36DB4F13B5DD2FEC5A96E1ED318136268501505A0A8EBB327EC86D70AEC1FD27C1DFFE501AF790408C508D908B301B5AFF5F3D43702CF2A8C6DFEF2F4FDB6577D7FBDC01AFF02D703B3C9CFAB86E026C7485DC04B09B7B0DC7F464998E583C3B77BD0F69E45EF1E16F648B4F11D58B2799F2F4F432EA5EADC3E21069A2A5985289637DFCC45EAB03037DF1B06FD62265CD569D4B592843FB4F5F9D4BC963F40892
	62FDD4C1DBFFCAEDDD843FB339FFB07C990AD7B4F8661B5F25BE036D9868DB2B885F30ED9532B4E64FA27925CF686AA17E65090646BEA6032BAFDC164D2DC8FBF29F6339CD15E58F4527E870CC16C7290C58DE8DFD9D572DF94C3F8E98B0D6D7C2BB9DE0F3BF78980089408E20748B9F3BFA6F13917A18C58AA855FDEE0FCB8E90B677G5989CFF9C089C8F55E20A2F91D32D58A38A507C78E92BE668875AD4720F92554771D40632C6C1583126276F9013A54ABABF2A0484614439AE0CCC0263DAD6EDE9998747A
	0920DB8DC008C78AAA977DAEC911C3A3D8FFBD750F6B1295A88281784D74E249C2FD45417A1381962BFAF80552EB1083DE59D3DE5E6873757479DBF0A5E473G6A79D51ABBD2E17C5AFE216346EC44D9203FFBC05059C036EB68185CCE94DF8A5C90B1F6520A797876007019B7ED42E7CCDB1EA5C7FE72382E4FCC867FCD98E03E32E8A0BCF6EAA8CD140B35FFF051B642783DBD209FDB51F345AFC74CA36BBE69EFD5ED75E9855AEA95481881D0F8C36878734CA2663355D2D096DE5A94G8713DDA10DB3B93737
	E29C6C85A885F81F303CD183001B6596F5BD7B693823404386B08AE09EC0AAC086C051CD215BB82BDFC75ECD1CDE8D2799E9DDFB398C5156DF2A2B730591F9EF617C37C531C34E88F8EF8CEBB31DFFECC05A72F5592B18E43F6CF5410FA06FABB3208CBC8FF47A54B965946F073CE104C08D2A78CA83463624A8813723CF111BE5C5F1FB3BC27DE64AAB58677007D20E6055E4C079FC7D10B75471AD860CB9274F6B15297155A93C5FCAE3DE76483DA034515767D5983EA38B6326C73E41F30EBD6BED02B9435307
	790B559B8BA4542BBF606E85174294C892EEE2BEF8DD2D17904C00433A498F4F181BA2367B8F0858BEF8F3BED9F245EF505B1B6267CCB22C394962B063E6A83619CC673375E5EF2539076303CC569FB7239E748AA096CF5E53C82B7016B56F880069160827F32730DF64AC9EC8C61F474354296A79E4C5BE5AC53E56A7C32A476458206A2E3AE2FBD81F156B1167638A5423C713D753EE219E5BDAC5CD5981ED85A06E9653650ADBCC974BEF63383DB6B16E8434ADG8AC0BCC04AEDB64EFE87477D2BBA2E708E0B
	6523F798BD539DB60E711D5AA646A55FE1F41672F159F735FCDB5B4538323B2C3F71AE635FBDE85CC717443898BEAE010FCB392B657B16BAEE05269F47274153F4CF5439972E21CDE81DFBD53AAE8D14847BA1C64A4B6B43EA5AA6D3AE0FEB5BBDD13BFE2B165690501E8730641E70479FED9135EBDC4FBC32EADDE9CE243AFC65BDA67306FBA1FFDC746B2A37199F251CC11E27C601AEC6297BE705F67F2C8A86FDCEB75D3A397D1553DAFFE5389B27FF8F4FCEF7A02854C1C299903EF26FB44A0BFBBBC773DAE7
	8C2D01D0BFF6609B8FF0F4B4625FF395E8E4CF85FC86409E684B2274563562A1C9224EDC429E96B950698B744A2EE254EE09542F14505CD6D2C53BEB7077A11183F65DC0F91CD85FF80A8FD90928BDE76D2864BC17BFB70B1C3A7725C16FAC903B2D5BB0B01A69B1F1F4C81F66479CD99DGECED35B6D1D326411803E340FFE81ED9DD2F4DB39D000F83C881D8BEC62C6FD99B56E1AEF5672321FFC18E76F9A0C59326237DCEBD9D2DD1E97C5C06BEA4FBB1B4DDFCFD4F3E27DDDF2EB4D6F7FD9357F599B24670F5
	BD94DA5F82D85BD2F5FD6F36895BF543184407041F971C46A74D39C4683B7FB852F0184EB71F73A9677ABC2074F621735F6D95B29DB4A5119369BD3AB2D19F6632101810CC49DBFE3BF189G13E931AA138546444688DD9CE87F7F79CB754E83E88F1FD7AEEE0CBC7CA4F89213E3D43B10E3894810214A71C67BB6CA475907C1730564153AE057E3FD5BAF6975B1FAAD17843D23E3A1CF0D95747E124E894AD240590DF6EE396107844D7AFE295B47683D792520178834D60E95364BEEECE071ABDB82BE3F9CD0EE
	983BA5EF17D83587FE17B443A66F6DBA2941B4B92E423E220381FD301C96F3C564347CF1226EEAB1617950BCCE3FDE6D88437B214D4ECD9F1FC559E346051F6DBE277838F161E73B97EBC59CA6C1DF6E4322567FD5954621875A31G09GA9GAB81B29E96B9B0674CF19265ACD56705A30ED772B48FAAEEF85EFB618C7205FA0437DFBA230D8D2607585670F3F2C16C60359F8DEB33F953064CAB89EB37044DE3B65E569CE19F2A6ED394DA9B30BC355B254DD31ED81627A643B38900597522AB1753DD9CAB7624
	BB355ABDE97136BB063C88711C4E65AB78DAA81E8F763664B07BBCCE6F26DA204F8F908B1084308CE08DC06EF8E123064A0AE8B622515962B3824E8C750AB13C2ED4F55AE80BF6DF22EF0B7628730A476B59222E7E8899715D17C5021D185946FBB65CB6F8D62C849EAD6305ED8A9B445D18E73C28A33A1D988BC950DE8C30C29D7B22CB44E75AF8919F1BEBD07F87A7GDDG9B0082908F3088E06584217F044A13A354BFCB15C3354FF4D85476E0F1D0B2E4DEF89CE4CC98BC0F2DEFE2AD2EAFFFA2544EGA6G
	BBC08CC0A2405209E2FDEFD6D6C5DB1FFA55B1929FFB79815719BAE45EFBE16DB59375660DEBFFB0FEF1C36605735B204B2F7D5203716B9EB2AF1CDF41A3FA73523FFCB0FE06AB51789D516577B5BA4F447BEC0F686FDFEAECC2BF3BEF6A21F57DA8E8A7293817A9AE855A29AA2E2A96F169504EFDD46C15474F623936C0EDD750B666E356FE25F624F7B7A38564B5F85D7F70A767213DC7E23116DE9579CE62F7D57AF79219557AF792B9556FB0BC50505EF40A7EED55435FD96C2A563B3358CB657E1B1D5BA86D
	E0D08E5264E6129409773D217C01685CDD1C038C41573C033C583A090F29D1FCC62CCD59FE706F07B02F6B6C4DB47733DC5D2C6E39E50F0A3C5E63403C6E01F68CC0026AA76DAE3DFDE991376D32C7077AAE4B0DBB4CF836D8857D99G79132CF945G66C9A2A7BEDB9D35AE2A999826AEFA4A98594EF30D7AF6CEB66259B6686B8BB865F0DBAE24F7B72CAF14F96B943997554E46AC267395A950A58F71056F50792CAF527C6546C87E42EC7A10CDE353AFD0BF4CDEF34F8AFBF5CF9276BAD40F76CA04F68AC06A
	A4E12F3D8D435591EBA78D35972BFDD751FDFD43A4D67BE62A7857A9BEF7F2884FEAE22FD33C0BAB003E0149027FD2DDFFD9B51971DD8F4F6CA903E5E03A59D993591EFBEB746DF920A632BD8F57C43767711A4876AC2F09EECFE34D7076ECED4B637634F486FC3D46D2CEFBDAED64354E2C55DF6B6C5A48EBFD26B67ADA9756C6DE6B37EB232F75BB35432F7537ED5AFCD42A62DFE8577AF488BF651296FFCC45FFF4C5CB87E3006DE909747CD7883ED3BCC56CF39B6D08AB0336C945D9A82E855A2EA9620CBB46
	047954A5F7CAFD9EC8713E205C64672F18G1FBC85674DB40998D32608336CB77B51FE1DD01941D2310EA6B10F61581F19C46D3C9E46E6836EA0008940751860E92B25345C0A87F376724770BD3C3109286BBC6B453BD1FEF387CAD7689E631545DEE99B92C779D3C3F1F3F0EA24B87EC71A47CFCCE5713AD1457FB345B7EA70AC0E2FB9547BB368DBBAD568719A5DA30A632CF9A762846EEFDD2256B04509BC3E5B9C5917771B75FD39582C3F5F1618075FEFBFB3C772C12633EF1C8D57D9DCDCC8CF7362A269EC
	A64D250B6342F5B60F62D36342F576C93D50599A68B3BFAE74D356A074137C3848072FB909AA5F2FB43F95671F3A969ADAE7BE721572E0EB7EFB55CFB628FA2814059EF22729F52A93F1B6E8FB260998F97E8A562010F6CAD9FCCC300462E30F1D779DCF1C06730F5176A8F5EE1768BF467A6F50769A35FF913D9B5214C0A52DEE2F4B578F2106EF2515E009292E595254DCF325695C95E3A37CE0F78F097D5A6FD4CE7CFE0C6E7B99046CA718BF957267443726E7327D7AAD1ACBF00E857AE21FC05C049A6DDDAB
	4A788B83BBE7327E978675231F5FA15A7EDF8F6AC7D91F4856766F2B5576234C921F4F56757D8F441D4A066FAFA01E3F0809787DCDAADFCFBAFF963DBABDB3BF5E72B6237343CBDA7D644F7098B2A350B9924F661F088F5D4DB0DF70D11F71GFED43C700FF52A4FA4450B58F95DC14FB1B8E63A402D27B8FCB7036F05982E5FB1D2BFCF6732A03D7B0B2549A366A14EA4616FCC234D59C17E4D05BE2CC042E471B07A82EA3CE3BA550F4B4447966B481E45ED104561940CC17803704369217A0D4763D9E1032469
	42863F2993B6D8266AFBF6BD6252F04C8C01FB0E6270BD953EAFE038CF6B07538F4E3D1F5C12B6DD6F5CFCEED89E7723EF2D1D84B0BDB7B488BD976948ACF4A874DC86E34CB3181E315DB1A324E7E6432466108DDBE5C13BFB843443EDF8DBC31B794B4F1C5A77B32C0ED96F9439A7F6064805E6BB569A18E7CB2042BBA7775357304507C4EE281035B1FD74D623C178D3380C89DC6E6499211C47F8CC9D44234963D2F93073E0DC8B4A358A66E54C9035C32A65A3F8DA252B7C74D763F2AB3EGBD7E81D23CBFE0
	D8CA0E7B012366413EC9FBA9C76440C55FFE06240CB3C53CD7AE7CBA4FDD7C1932FCFAA6BE59DA7E7BA256FF5D509783B08FE089408A00342F0B356DBA0FB262A996442FD2944959CD5FF20A432D55AD770750D8DCB25A7BDAD1D2C66372798FA26987A854435E465E1234C8DDA11FB80B5FABF8249B3EBE851E896893716ABDBC01F6A1C0A5C0CB820BDF661B24D378662284FD5F4C5240B29833AA0179A6465D7A04F05F7C260CBAEFEA883D93E078CD747D32113EB78D7531FD73DDD954609B005E7BF4BC1CBD
	C9680F45540FB18DF01B7E53E6E153BD5F18CDCCBFB5998EC430E959CC362E1819217DA82623EC935351FA75BEFC71CC26239570DCBB133D33A0CF62182D035E3B6A6770ADFB423F372097AF25227FB58F7AF0BDC732F55FEE906BB6BF29376E75E774F0CC97956DC455DF2C2A3FD1C007755BAF91D5369C9776F5CA1E202C7A72168E5CF3D21E94FE7BC6737EC8BE87C83C1BE6EF6F040FCEEF41352BB5B98BD35E175802B5095B6BD66014633EC98FDCECB663DDF4997B17836F35G1B1F3266958114BDA5E4B9
	F8F1BF0BA11D7829F1F5492216D36F6B2C114FA2DB2D513FCB5CE545EF8F9CBEF04ADE24AF70FB2DE8B3456727C8FE3631227F855CDD5D8A9BAA4EB6D63A2F4A1D944D4E8BCBDC622E4482EB0BFDCA1CAFCB6D787EC03D2B43D7426565E783EED7355455C8A31859B5D796B9FD49D3627E67B399E9E6C0BBBF914E4409625D594007A29E12674EA2292F6BFF4F313A650E21B211FFEF1A980A0732430EAC463334D378D2E262C8FDD2446776AB781B656A77E5ED2EDE89345292072F8518EF90BB66A372E7563CBD
	G15GB6G8FC08CC0BCC0B240920015G6B8132E7C15C829C81B885E081F04D923ED458F6E8983F66CA278E95105C68D27C2C772AD958EEGE8AD18A55EF76D2C93B6D9BBCB5844D4C73F991E8DE7E6G4BECE1132BBD5A9C05FA0ED4FBF74F667B6A6C10CDC2754F68C1F7FB4BE14C6A59436B121F838759A09D6664A68DBF0F696C1D8E5CCB0203623D78F264987B64F27498BBF119FE73833101DF93B0DC45E564636B6C8C4AEAACD7DF4ED7F1F5C10BAF68466C415673573AF70C517530628167ED116FF79E
	3BBFF80646681E2470BB550594DF1C94FE27FACA963ED686FD71C962BD6A9B926EBB9B20DDB8471AF794408C508D90B3C77877CFAF9FA5D16E53DBFC7EC6B01CC7F335425677D927F6FD71C0B3F94E707E414E8B1F513C34E48EDB87431D2438159C47E4AB30C776195D7668BEF3502E1F178F5B076665CF6C7AF979D33BB6AF73BBDEBB6637AE08FD1BBA6E0C0AEBD3F1A6CA93F12DEE17522D626B29DC08B74B48D060ADD42EFE0A33F15CC58D2E0D635A6D68773064EEDFC04C3DC26FB1BA5D9E8FEBBB699837
	9742C055C7B715516D17B4F2772A3836103F1B9F4C5FDFFB40F93B6B9EACCED2E85ECC1F939EA7AFD1BC199B9EA775F5A2CEB6C31FFF2E0813AAA746498AE82F87A8F89A768200CAG5B53A2CE0804F74A1176018BBE9F3D05458BD958866400E6C7E07E9D5FA0FC7E23EC6173B39B044F7FC72640A56944467CC13141F436GDD147F7E1666F7357C555F8FDE7ACAF9B0FD2F17A2477EEFA47D7B617F12746F07339C433FEF4DF3C47EC6FC3BC37B0DB83347971D42C68F2B363C50A9ECB41608F72A520768EF3C
	0E25A90061778CEC8961C58A603A7D2B3A10AD3F2B79ED297B6A6D6A7ECAF9705C670CEC57734E68B93D5D291F536D4E21B9DDF66A67749E27B627337F0FDC577DA6BC137F05ECD448874D74534522DADFG21D772947103D911C96E24F64370A67B02C30EEB4D03BFFAD4485B6C482A90C3D3C3B0C036295FF05651543C7A7A71722CDF4C4EB2106CBA72B6DBD69D49C6596A48C6AAFA9D4902E58D603FEBE4EBA1F80D5C9D0C39DF2CB88B9F3A273D4749986B0CF8F7DDB01F6D6F4573C3F5A64B2117FBD027AE
	400F1A2F3E2B3F0A7FDF99699E2299746AD1A4CFCF155705F79255B794B994130E9E249F94E3F8A76BF377205CD7B5F37E8FD0CB8788941C4000D894GGC0BBGGD0CB818294G94G88G88G6B0171B4941C4000D894GGC0BBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1294GGGG
**end of data**/
}
}
