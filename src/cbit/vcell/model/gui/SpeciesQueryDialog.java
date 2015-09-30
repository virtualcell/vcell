/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model.gui;

import cbit.vcell.client.task.CommonTask;

/**
 * Insert the type's description here.
 * Creation date: (2/22/2003 4:08:34 PM)
 * @author: Frank Morgan
 */
public class SpeciesQueryDialog extends javax.swing.JDialog {
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private cbit.vcell.dictionary.DictionaryQueryResults fieldDictionaryQueryResults = null;
	private javax.swing.JButton ivjCancelJButton = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JButton ivjOKJButton = null;
	private SpeciesQueryPanel ivjSpeciesQueryPanel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
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
			if (evt.getSource() == SpeciesQueryDialog.this && (evt.getPropertyName().equals(CommonTask.DOCUMENT_MANAGER.name))) 
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
public cbit.vcell.dictionary.DictionaryQueryResults getDictionaryQueryResults() {
	return fieldDictionaryQueryResults;
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
		add(getJDialogContentPane());
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
private void setDictionaryQueryResults(cbit.vcell.dictionary.DictionaryQueryResults dictionaryQueryResults) {
	cbit.vcell.dictionary.DictionaryQueryResults oldValue = fieldDictionaryQueryResults;
	fieldDictionaryQueryResults = dictionaryQueryResults;
	firePropertyChange("dictionaryQueryResults", oldValue, dictionaryQueryResults);
}
/**
 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @param documentManager The new value for the property.
 * @see #getDocumentManager
 */
public void setDocumentManager(cbit.vcell.clientdb.DocumentManager documentManager) {
	cbit.vcell.clientdb.DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange(CommonTask.DOCUMENT_MANAGER.name, oldValue, documentManager);
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
private void speciesQueryPanel1_DictionaryQueryResults(cbit.vcell.dictionary.DictionaryQueryResults arg1) {
	setDictionaryQueryResults(arg1);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GEEFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BD0DC57F5A4A3371E0E12A8999CAB0EBA43B80A233832AA0F1531522A99D22BD359D69B1962315A4A9D922B2D1CE20754EAC39D0DFDF797A43011C096F648D832E5CB0E1104B0884B4200A5E0174F8A848896D8BE06FD6CBE58877BE3772DC0369C771C7BF97BD85E32C80919B973761EFB6FB9671E5FBD773E87A9FDB3AD5FE4AEA54414CF785F8593A1E57504BC551B52ABF0356FE694925D5FDB
	G0709B914874FF6681B17B28ACB4864F88134C721FD4315D1789B78FD044C0C17FE88BF9270492EA5645A179F3D31B41F1D1F8BBE87E83B4EDF844FC3GAE4061F37D3B483F57DF4671ABB8DE0049CC486D65B8466FBF4AF1B9B026GF0B6GE7B99B9F02A79360363ADABEEE746B6B485EC7426603C8C7C7D3408EBEF7AFFF9EA74F1B561CC2DE5F24F2621C8AE8FBGF071967262EF4F423321E7E66A22FBC2D1AB2F39E51F2F529F74483E4A2928D2D75797125D0A9C399A154357ED0A648BCE41783253C873
	5F7C851486A1D9C0EFAD45F987104FA87CD681D61E96787786913F87FE13B39905E5E7445ABEFA37014B6E7C74C112F94E1FD71CE0EDD5E7581A1CE7E2EB5BBE7D247937548EE5A654F7AA74ED86588E1083D0D211D1D88570BA357F1B64B4BC1BA6D4EB506F8F861AC221E61FA7FC85578A3F6B6A200763FAE5F7B06CA10459F80D3F42C01E25004D3B772667B11D2C2B9176DE5F3B016CFA601B668386BA59356ED3F3FB8553052BA22693467B29043CD3AA967A63C25E03C44FFB5B6C6316FC835E3B479E3724
	F2BA6BF43CF7D6202DF7D19F58863FCD5CBF987EBFA87E6859981E79E6006231DD81FD2B4E8A5FF09E9232F4665DCDCA2A6FB454436A3FDDE95EFB1649D0D499AF4B83546FEAAB994FE2CD1607A85E2543B3D94ED3BC36EF073E3C77B28A19FF3DE541D8CD01769A00CDG5B8132818A2AB28A0FD6899F7312771669E3BDD2D8ED0EAABE0F9CA6EC6EDB265760A94F2BE1499E0828D240AD8FC8E1C51A70499142471C26761DD8B06FBD6A7BEE6051A687643024AA41GD0177C32AA07A3ECCC2D69B00E894B3457
	2178E5E0608F91C13751F4841EBEA9227607BC12AA47C6307EF6935A641AB4G9401G7CE6FA694AC3FDB5407A9DG9ECD8F8FD1FA9DF2B8A07B6A6ABA034119E84801ABA11B2AD04F4F535CB18F3FD7D5899DDF4EC7DCAE76D78BBA4F1BDE515131A9EE94DF8ADF97B17633814C47A72B054FBC6B94BEE3BBF89F29F87C8EC31FF10EBFEE31D5B3DF992D0E0F1DECCA9365E2EDF540394C78BDD9ED9CDB49F345D7164D43F213748FE9365A3C9FED3582E4CC83C857F4FC5EFC1C79EC3394110517F6074141E4CF
	CC63CC4E9F8EE29C9C832886F813303C518270B240C1EDBD8F53F1BB0187B917D1D88E5083B08F1086306D1C50ED7DE048C05EC2CE2F0553CC342EE3DC06E46B2FB65479F0C25E87B97F62A4F6B8308C5E79F1ED2673B39624ADDF13832ACD8E49818F7C08703EF396140167014920B6271662C310B72C9028918DFF510231AD29EAD819082AF21F2C2ACAE0AA565FCAF9C5229321D84A913C2E507C9E1C033C210D6F35E04C39030100CC0D2FCD617D8E9A7332CF76035056E0B420B27CF09E464D0CFC1D671C31
	3C7D84F306AF0A790B559B9BA554EBA82C784125B08592DB8D66034301B21D0EF240BF30EEAA033E0A9A915BF3A7C4EC1F7C647BA4FDBCE4B65A1BD6FF77EB6615B5AC8E53EAE2314DE46A4EB7163D1F669E0E0FB0D97F298F75E0D4G31F87A658124158EBCB28132EBC4BCBD160F7DA2E771C03286FDBE26CECD4FBF5ADF02F611E723B224FACC0E9D1A6E7EEE7FABF1FD83DC0F2C7F9F76239EFD3A3C1A7ABE6A11B8C5CD19876D23G8D6FB3DD5EF81F69C2BD0F6336EA6332G1F0BE3815F8860BA4F4695FC
	0063D68F09F16967D9AC6FBC4F68E573F10CEF5D30986778005171FD40462D7FC04F7785ED5CAE5E3F7783463F6402FEDC460898D7F1010D33DDE063DC9774FC6F999563EE687AF1FCAABC33AF08BA37F796EDC26B5CE9691AB4DF99190398292B3B9CD753FE9AA860F1CDEAC56D7A6FA1DAC3C03B8740DBAB7C71310322F6CD1DD9C75606B3AD096A7214DAA6735A5A18BFD63866AC4C0F6A8B1167EE685BDBABFC287D00FE7FEC0AC402EE05EE5D5C7E3FBA2077D7065BF760A5F8CEAA6108EA0704B2AFFC45DC
	0072625E4E513C56B965AC62792960C33099404E8F91BF27146AE41F87DC9A40D6G8B25374AF98E738DF566CA76681143134130DF761423F6AB25B9351266364AA65A5D023FCF099C701B6BA80F9B6B9BDF792981A2EA4F3B0FB4F21E5BB75CCD9ABE9FB39B1D85EEAF761A0F57B1BD36574574197E581366CCG3636E71C2226CC05B15B81D81EA90A6A73CCDEBD4487008BC02D976BF38CE19D6651F6BE9A7A3DF2A46A0394CD180E3EED2823159A0DAF8D218F49818CCD8FDF5FFDEF6857D7A05DE6383E15CF
	CF19536A593A36554756D71E712439A1C32C6F47C342F6A58D99056D8D424FCBAF621366DCA274BDD703B4A6ECD77B3A02EAD75487CA9F96BA7F75B1A153C95BDAD23BF746D0A66A438D3C5EEF0849344AFF5E32960049649E96B26542182A0F04AE9E997E437C25793593E80FE0C0AE6FCCBC3C9EBC0949716E0810438BB224E9F2FCF5241852F1C7B1E82EC881E98AF6BD5677C343BEC66F579A3D1DC02B642220B7B1CA77D5D98A3B3DE8E7477590A4E856779543BEC66F0EF1C12F9DE825DC94364B6B6CE071
	AB770403A1B92CDE37FA254014D8358FFED77641A69F182A57E13AA726E1DFB14000BED8CEBB6392B9ED73C5D1F7258470FC18F35138DE4D3B3490DF86EDF6EE9A1AC559ABAE451F6D5C945FF0A97EEC77DFA191071D50377112287577B5E39C16B6021FG348338GD2G529AC58E7C33029A1264ACE58F4091A7A0797A9694B762CCC36D847588EF5B8A75314174701EB37E1C3C25F1615AF74635593C0F964D2B0CEB6F0B1B47ECDC5C17E09FEA0E2AAA2D8DD81E9A766A73D4E9934BD335706C84E0F65D2094
	F03A1EA631A7FD562A5F133694FFE6FEB9C1BC2FE9E272EDE90A45F3C9577F72BD699E9B6E2B7B10D7B358882093A0843092E0E3333051BF96FE144CC6B4BA9DC1AB605A21DE314657151ACE1FDB2453F8B03605E9A8593C9D4DC63630DF2EA04B3E7B6A11E0A7E63639EBB65EB6F8D65C83BC76B58B5B582262EE2C54AA6A086FCDE1ACF4C25B83F043AA46DAE7C4FC26DAC5FC9CEBC57DEF07769EGE2037A8C2091E094A045265DE93DD43FCC7D33D439D87BCC07FF3FC8074B0B0347974D0B0F03B45B42F9EC
	FDFF5E066B5B8CFD19G59G85ADD06B83348378DA447ADEECEACA36BE6D2AE3B9BE268C5F5ABA67964DFBA32E3D3245E85E5911DB63D73FE8DEBC3F35067CCC2337462FF8513CF8FEDB8C79FDFB7C5678FDFF513CF8FE19067C2ADCB8AF1B771156256FDBCA211F5DB75DC96B7AF2E8F7EA38E7A94E856DF98D678FA1EE8D34B7360A3DF2FC964F35DB3436C25B180FD97B1721655E5DAC9736685645F83CB47B4766213FC7E23116EFCDFCA7D1E4B53E13F845EAFCA7D1E6BD4C70C0C3FF53A97A0FDB173E33A8
	379A5DD99C23F2BF7CDAB1259D0948911A5CEC12AA713E7FD4DFA586F797DD10A1781A0F10C9E7A19F5322862DD81B327D6039D318578D76E61A7BD92E7E06D76C393BDAC5DE5F6D453CDE5A86798B4056A67C628145E8DF9AED636FF75A967BAE4B0DA76DF836D8817DE9G1B81F6G6434091C78A3EB523A28E5FE093AE823AD311D9F34995B79EFECF8360D8423E1379CEF4B2D36B22DAF16F96D2A6CC735737B8ABADF1542D372A2DF482473D9DF2279CF5A92798B336969A11DCD2F20FE183DB2E6053DCA5A
	053D7AD534D7BB34DDG736D42DE7668D2F5442A764576E2356F6B547EEB5BD96DEB5270A7A9FE238E4FEA627B26443B38AD50F75CAE78374F9871DFE1E7FCD743F33DFD218CCCB76F36A636E7E52B31BD5FEFCDEC4F7A5664763435A636E7C7EBF2FBBADA1736678BC3AFF3FB76CC0603FEEBB5277D3D3644EB5D50E63C56CDED09577A57ED49573A35AD71DA9FEDCB3E564C3625571AB62C4FC7559A5EB3227769983EF6D40FBF2761B34675F4B086581E56C64FFF6960BBBB6CE21FDBCFF13B201D2D61FEC5F1
	7B20DDF4D91CF1B784B01FFA64C9A96A03949F0C485DA17E0A89700E4BB86F61000831231745D9767EB9345FA4D4E630D42C23C9C5870E4D8F0A5AF9B50CDD8F309DA09B2028C370DC91C29A2A227AB0E72B9D789E5E51C334F5368570EE145F5C0152D53A4784E431D75E690D0F234D9D3138595E11A80E1D8A46E5D6870B57FC8D6F26783DBABC0B631FFB357B33CE28D9BB059E9F2167329D504E52F0E726C52D115DA972F805BD31AF57580DFD79C33B71FEFB493E74FE5BE6CF6403CCE7CF4C466B2C61CACC
	CFBDD792696CE11ACBBDD762F516C97173D762F5B6228A1D2D043E1CABC2BF2E28500F23CB6443373CC4136F346E7783D3FF6CDAE8F11D397D8B654156BCBEA37CE4ED175043569921070D9A6EF56ACF24BB233034DB4448188BEBD0C8BB55ACBE56DC0D4547C3222F263D9B677FB3ED17EBF38FC93C7F9C6BBFC95BAB357EACFAB724AB01AA871500A7B8872106EF2555C8254D5E57535D57B25E5DB5EE6D049F6C6EA1EDCE7F1DCA6DCBDFB6FCBF03303E1B79D3BAFF6EFB75AE933F2B8A9670B93950D7550338
	87037A3BD61471A9B3BBE7327E279774231FFFC6747D2F07747DA86B1DF97A7EAB8B7AD1E6094FE76BFA749DF127326F510D44770395893F3F196FE16BD9431F2BF6FF4B3467AFBF31B0BABF99516B2764AE1F591400CEA61F1F55933B1BE13E3001561F25D7B38A4B2F8A7FA857F2CB67D591BBE354E75660988D27D09C3E1B4177C28C1763DD2E1FA3AD6A3BD7177F0E1B71A8D4EE765DF67CBB53E4F30E10EBB36843AAA4CC968F5F68C30D677522FE1A27EDFC6C8E8359AD5C86968E3BE18C422B428F2F466A
	B7FEEE1C95B6686C95B6B8218A9B04FA05EEFFC2F129B8C6437D8B4561FBAAFCDF40F0123A14FEF06E4D641654DE23F373A7CB723899FD6B6DA40069F9AEAA743C4DC0E621C32167DDB0A6271769995BF9FDB1BDB39B7E409133613F4E885AA5FD49E9475B70240EB67317F30A7E7D8C2BE36C0A48BDD5FDA2977E420535866659CA28703A64B97A9A367C14488D9F497A182E7A3453AC7CA9DEC69B175B51974BF90CC71D2467516D73E8BC58F9703B7DA8578A181756A7EA071FDEBD834F81E91A1F7EDABC0A9A
	8C53639FA0457B83062564389FFCB5F8EB5F247D780048011B7E6ADE3279CFEC896F1553797A3278337C29EF19704956D2BC0875DF09834E4AGBDGDE009BG298E313672DE1491CF31A0FE132ACAEEAFFD4BA98E37830ABC97C3E3F149E81FC3976775CE20771DC47A818A17E1EFE3EFC99C5214669379E17CDE41A7DD8FC6D5086FFE7409A734FB78F5A0DFBA409E00FD8E963F4CB7AFE979E5345F58B7ADBA884118957D4CB7B16ED677477B667DB26A3C3BA376CE00618B8F603EE3256FCDE3FDEC5F0CCC0B
	9AFCAD50FB130E073A14447ED8CC6D9EA21ACDFFDAA4ECFA74AF3EC3ECCF584CA79258F497DFD745006E5BCB7FF18B535151305011E700696886BCD78D30F7966B86687781EA19815D05B97C6051786FAD6845CB35687F198FFD78B2C732F52F0B0AF5678C982DBB6712910E6922EB04E87AAB7289BA650399052CFFF614E83239946C1B14FC91D97365B7BE46BD47B5A87CB65771F6A21F83A45ECD3337F742C76F6D4735EBB5B98BD35EF7FFBF56A4CAC0D1611423FCC28FDCECB663DDBD067DAA70DE853089E0
	9B40AECD167383EF3398B2081F964F14ACEAB96D3EEEB071D9644D41645FA5168F62378793C1F0CABF5297784AC134199A8CD1A4BF5B8C227F0515A92F4A060A334DA05A40A7CFD2B4BBAF3C6895F7A5393036AA27B8DFD63B707D01F6D7072F046B6A5A420A27D91A6A24914C6C5A2665F42FD35C7FC469F9AE8D5A1B81F6B8453B33795F09F8D8FF4F5DE4F54078FB0EAC47E766BDCE7E3D69D0AC9E32261FB4B31EA1AD965B07166B13A2BECF3870B74B5573537ADC1D8234D207162E8518EF9C16B09F2D0331
	DB8176G10E1B8BBGD4GB482B8G3CGA9G2B8156832483E4826C864885A89A913EF4EA785492FE4D15CE9DAAACA968D27C2C77FCD8586EB850F20E0877DD4D91E113D5A342A621887DE6985AD9G39A342A653B37A9C05FACED4FB170CB25D3CBF9A33C92C7E39EF415D1E8AE3EE9FDDDA17EC5EE11FFE5E9A1833B149BC263337BF463DA432A05EBF9CCB9CE30DE349E34CBEC63F790158402F89982EEB8C7984A7A7A332964BFDE3A59A4E9E6989C6944C9EECBD7906F7524975F0649667F55E74BCF6FF70
	B40D512D2371F72A7FCD71BBC6636FD4BFD5042F6D023E460F45FB545C895CF756C2BB9DE0A7C08EC04998444218706FBDE3D5A449FD3AA3986A844379F4D7ABECFD5185B15A88B49DE3CB7B87BBAFFC0D66886F98DB874325D2DC8A47B1595E11927BCC05145CE74ECB46F939DEDA1C179BA5633C5CAA6973B23F6315B03FCDC16CBB35F1BD9AEEC84375D31A089BD4BC2AD7430FD039905FAEA3C3011728DCF394676438C99DEE0863E6A474FBD832B79896F3C3749EE3D27179D8DB25E314G0401260F6BD4C6
	A5A469647E3D069B527C16C4EE4D5F8F5D623C361B1E4762E1B74D1BEB46626364990ADFB7969FA757A2A2CEB6C1DF59380813DE8F46498DE82F865882108930870038C41C9C1E40BB65C47BC0EFB0C8EFE171C2963681B92C5B91785DD6D4787C19BC61738FC7054F7F0FC9604AFD0BE3632CCF9F9BCCE73FD00876BBD7777BCB4B3EEF3895E0BC6E7AC2F9B0FD1B5C09E33F40EDFCBFFC48EDFCBFDC6ADE7AFD6BD1F762EF44CF38755F08B3FB2C5472571FEA367C3A163FEEA3621D2A74BB74B7DE4752944070
	C7678F4670A285F05D7EBC923365F33A5FB7A2DF3CDD6F78C2F9705C67C9EC5789CF721CBE63B14E69331E45B97D1A47B8275F7068F3BA7B7F485B824F42F37DB3A4DFA56F7451CF970F37864388FE49F71C9F4C0E5B64C9EAB78CEF72D6E451F12DEF61C70FAA594F0E2CAAB1F7F7C44224D87B0653CED3F356351ABA4BD35F3118C91E1D6CE74B3213BC144D8EE7F2945DCEAC302CF97CF70DBCBDC4E6496F97E2EE96AB4E4227BE575F63645E66CEF8F73DC5E27B7B8EA9D6E7329C7AFF745B1122090C427289
	11B36DA17C7F4AC47790FD20D71FAA79E61A829E3C13E83E2E4A3118FCA10474A3E28C6F645FEA4C225C533AB97F8FD0CB8788F2984413D594GGC0BBGGD0CB818294G94G88G88GEEFBB0B6F2984413D594GGC0BBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0F94GGGG
**end of data**/
}
}
