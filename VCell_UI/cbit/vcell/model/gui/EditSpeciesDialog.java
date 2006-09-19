package cbit.vcell.model.gui;
import cbit.vcell.model.SpeciesContext;
import javax.swing.JOptionPane;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.server.UserMessage;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2003 2:07:01 PM)
 * @author: Frank Morgan
 */
public class EditSpeciesDialog extends cbit.gui.JInternalFrameEnhanced {

	//
	//
	private int mode = ADD_SPECIES_MODE;
	private static final int ADD_SPECIES_MODE = 0;
	private static final int EDIT_SPECIES_MODE = 1;
	//
	//
	private cbit.vcell.model.SpeciesContext fieldSpeciesContext = null;
	private javax.swing.JPanel ivjJInternalFrameEnhancedContentPane = null;
	private javax.swing.JButton ivjCancelJButton = null;
	private javax.swing.JLabel ivjContextNameJLabel = null;
	private javax.swing.JButton ivjDBLinkJButton = null;
	private javax.swing.JButton ivjDBUnlinkJButton = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JLabel ivjNameJLabel = null;
	private javax.swing.JTextField ivjNameValueJTextField = null;
	private javax.swing.JButton ivjOKJButton = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.model.Species ivjspecies1 = null;
	private boolean ivjConnPtoP3Aligning = false;
	private javax.swing.text.Document ivjdocument1 = null;
	private cbit.vcell.dictionary.DBFormalSpecies ivjDBFormalSpecies = null;
	private javax.swing.JButton ivjAnnotateJButton = null;
	private String ivjAnnotationString = null;
	private javax.swing.JLabel ivjLinkJLabel = null;
	private javax.swing.JLabel ivjLinkValueJLabel = null;
	private boolean ivjConnPtoP1Aligning = false;
	private cbit.vcell.model.SpeciesContext ivjspeciesContext1 = null;
	private cbit.vcell.client.database.DocumentManager fieldDocumentManager = null;
	private javax.swing.JCheckBox ivjJCheckBoxHasOverride = null;
	private javax.swing.JTextField ivjContextNameValueTextField = null;
	private boolean ivjConnPtoP2Aligning = false;
	private javax.swing.text.Document ivjdocument2 = null;
	private cbit.vcell.model.Model fieldModel = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener, javax.swing.event.DocumentListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == EditSpeciesDialog.this.getCancelJButton()) 
				connEtoC1(e);
			if (e.getSource() == EditSpeciesDialog.this.getOKJButton()) 
				connEtoC2(e);
			if (e.getSource() == EditSpeciesDialog.this.getDBUnlinkJButton()) 
				connEtoC4(e);
			if (e.getSource() == EditSpeciesDialog.this.getDBLinkJButton()) 
				connEtoM4(e);
			if (e.getSource() == EditSpeciesDialog.this.getAnnotateJButton()) 
				connEtoC9(e);
			if (e.getSource() == EditSpeciesDialog.this.getJCheckBoxHasOverride()) 
				connEtoC10(e);
		};
		public void changedUpdate(javax.swing.event.DocumentEvent e) {
			if (e.getDocument() == EditSpeciesDialog.this.getdocument1()) 
				connEtoC6(e);
			if (e.getDocument() == EditSpeciesDialog.this.getdocument2()) 
				connEtoC11(e);
		};
		public void insertUpdate(javax.swing.event.DocumentEvent e) {
			if (e.getDocument() == EditSpeciesDialog.this.getdocument1()) 
				connEtoC6(e);
			if (e.getDocument() == EditSpeciesDialog.this.getdocument2()) 
				connEtoC11(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == EditSpeciesDialog.this.getNameValueJTextField() && (evt.getPropertyName().equals("document"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == EditSpeciesDialog.this && (evt.getPropertyName().equals("speciesContext"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == EditSpeciesDialog.this && (evt.getPropertyName().equals("documentManager"))) 
				connEtoC8(evt);
			if (evt.getSource() == EditSpeciesDialog.this.getContextNameValueTextField() && (evt.getPropertyName().equals("document"))) 
				connPtoP2SetTarget();
		};
		public void removeUpdate(javax.swing.event.DocumentEvent e) {
			if (e.getDocument() == EditSpeciesDialog.this.getdocument1()) 
				connEtoC6(e);
			if (e.getDocument() == EditSpeciesDialog.this.getdocument2()) 
				connEtoC11(e);
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == EditSpeciesDialog.this.getJCheckBoxHasOverride()) 
				connEtoM9(e);
		};
	};

/**
 * EditSpeciesDialog constructor comment.
 */
public EditSpeciesDialog() {
	super();
	initialize();
}

/**
 * EditSpeciesDialog constructor comment.
 * @param title java.lang.String
 */
public EditSpeciesDialog(String title) {
	super(title);
}


/**
 * EditSpeciesDialog constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 */
public EditSpeciesDialog(String title, boolean resizable) {
	super(title, resizable);
}


/**
 * EditSpeciesDialog constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 * @param closable boolean
 */
public EditSpeciesDialog(String title, boolean resizable, boolean closable) {
	super(title, resizable, closable);
}


/**
 * EditSpeciesDialog constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 * @param closable boolean
 * @param maximizable boolean
 */
public EditSpeciesDialog(String title, boolean resizable, boolean closable, boolean maximizable) {
	super(title, resizable, closable, maximizable);
}


/**
 * EditSpeciesDialog constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 * @param closable boolean
 * @param maximizable boolean
 * @param iconifiable boolean
 */
public EditSpeciesDialog(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
	super(title, resizable, closable, maximizable, iconifiable);
}


/**
 * EditSpeciesDialog constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 * @param closable boolean
 * @param maximizable boolean
 * @param iconifiable boolean
 * @param stripped boolean
 */
public EditSpeciesDialog(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable, boolean stripped) {
	super(title, resizable, closable, maximizable, iconifiable, stripped);
}


/**
 * Comment
 */
private void annotateJButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	javax.swing.JTextArea jta = new javax.swing.JTextArea(getAnnotationString(),10,40);
	javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(jta);
	javax.swing.JOptionPane.showMessageDialog(this,jsp,"Edit Species Annotation",javax.swing.JOptionPane.PLAIN_MESSAGE);
	String result = jta.getText();
	if((result != null) && (result.length() == 0)){
		result = null;
	}
	setAnnotationString(result);
}


/**
 * Comment
 */
private void cancel(java.awt.event.ActionEvent actionEvent) {
	dispose();
}


/**
 * connEtoC1:  (CancelJButton.action.actionPerformed(java.awt.event.ActionEvent) --> EditSpeciesDialog.cancel(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.cancel(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC10:  (JCheckBoxHasOverride.action.actionPerformed(java.awt.event.ActionEvent) --> EditSpeciesDialog.updateInterface()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateInterface();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (document2.document. --> EditSpeciesDialog.updateInterface()V)
 * @param evt javax.swing.event.DocumentEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(javax.swing.event.DocumentEvent evt) {
	try {
		// user code begin {1}
		// user code end
		this.updateInterface();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (OKJButton.action.actionPerformed(java.awt.event.ActionEvent) --> EditSpeciesDialog.oK(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.oK(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (AnnotationString.this --> EditSpeciesDialog.updateInterface()V)
 * @param value java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.lang.String value) {
	try {
		// user code begin {1}
		// user code end
		this.updateInterface();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (DBUnlinkJButton.action.actionPerformed(java.awt.event.ActionEvent) --> EditSpeciesDialog.unlink(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.unlink(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (document1.document. --> EditSpeciesDialog.updateInterface()V)
 * @param evt javax.swing.event.DocumentEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(javax.swing.event.DocumentEvent evt) {
	try {
		// user code begin {1}
		// user code end
		this.updateInterface();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (DBFormalSpecies.this --> EditSpeciesDialog.updateInterface()V)
 * @param value cbit.vcell.dictionary.DBFormalSpecies
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(cbit.vcell.dictionary.DBFormalSpecies value) {
	try {
		// user code begin {1}
		// user code end
		this.updateInterface();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (EditSpeciesDialog.userDictionaryDbServer --> EditSpeciesDialog.updateInterface()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateInterface();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (AnnotateJButton.action.actionPerformed(java.awt.event.ActionEvent) --> EditSpeciesDialog.annotateJButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.annotateJButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (speciesContext1.this --> ContextNameValueTextField.text)
 * @param value cbit.vcell.model.SpeciesContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.model.SpeciesContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getspeciesContext1() != null)) {
			getContextNameValueTextField().setText(getspeciesContext1().getName());
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
 * connEtoM2:  (species1.this --> NameValueJTextField.text)
 * @param value cbit.vcell.model.Species
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.model.Species value) {
	try {
		// user code begin {1}
		// user code end
		if ((getspecies1() != null)) {
			getNameValueJTextField().setText(getspecies1().getCommonName());
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
 * connEtoM3:  (species1.this --> AnnotationString.this)
 * @param value cbit.vcell.model.Species
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(cbit.vcell.model.Species value) {
	try {
		// user code begin {1}
		// user code end
		if ((getspecies1() != null)) {
			setAnnotationString(getspecies1().getAnnotation());
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
 * connEtoM4:  (DBLinkJButton.action.actionPerformed(java.awt.event.ActionEvent) --> DBFormalSpecies.this)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setDBFormalSpecies(this.getDBFormalSpeciesFromDialog());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM5:  (species1.this --> DBFormalSpecies.this)
 * @param value cbit.vcell.model.Species
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(cbit.vcell.model.Species value) {
	try {
		// user code begin {1}
		// user code end
		if ((getspecies1() != null)) {
			setDBFormalSpecies(getspecies1().getDBSpecies());
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
 * connEtoM6:  (speciesContext1.this --> species1.this)
 * @param value cbit.vcell.model.SpeciesContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(cbit.vcell.model.SpeciesContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getspeciesContext1() != null)) {
			setspecies1(getspeciesContext1().getSpecies());
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
 * connEtoM7:  (speciesContext1.this --> JCheckBoxHasOverride.selected)
 * @param value cbit.vcell.model.SpeciesContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(cbit.vcell.model.SpeciesContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getspeciesContext1() != null)) {
			getJCheckBoxHasOverride().setSelected(getspeciesContext1().getHasOverride());
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
 * connEtoM9:  (JCheckBoxHasOverride.change.stateChanged(javax.swing.event.ChangeEvent) --> ContextNameValueJLabel.enabled)
 * @param arg1 javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(javax.swing.event.ChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getContextNameValueTextField().setEnabled(getJCheckBoxHasOverride().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetSource:  (EditSpeciesDialog.speciesContext <--> speciesContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getspeciesContext1() != null)) {
				this.setSpeciesContext(getspeciesContext1());
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
 * connPtoP1SetTarget:  (EditSpeciesDialog.speciesContext <--> speciesContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setspeciesContext1(this.getSpeciesContext());
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
 * connPtoP2SetSource:  (ContextNameValueTextField.document <--> document2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getdocument2() != null)) {
				getContextNameValueTextField().setDocument(getdocument2());
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
 * connPtoP2SetTarget:  (ContextNameValueTextField.document <--> document2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setdocument2(getContextNameValueTextField().getDocument());
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
 * connPtoP3SetSource:  (NameValueJTextField.document <--> document1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getdocument1() != null)) {
				getNameValueJTextField().setDocument(getdocument1());
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
 * connPtoP3SetTarget:  (NameValueJTextField.document <--> document1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setdocument1(getNameValueJTextField().getDocument());
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
 * Return the AnnotateJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAnnotateJButton() {
	if (ivjAnnotateJButton == null) {
		try {
			ivjAnnotateJButton = new javax.swing.JButton();
			ivjAnnotateJButton.setName("AnnotateJButton");
			ivjAnnotateJButton.setText("Annotate...");
			ivjAnnotateJButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnnotateJButton;
}

/**
 * Return the AnnotationString property value.
 * @return java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.lang.String getAnnotationString() {
	// user code begin {1}
	// user code end
	return ivjAnnotationString;
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
 * Return the ContextNameJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getContextNameJLabel() {
	if (ivjContextNameJLabel == null) {
		try {
			ivjContextNameJLabel = new javax.swing.JLabel();
			ivjContextNameJLabel.setName("ContextNameJLabel");
			ivjContextNameJLabel.setText("Context Name:");
			ivjContextNameJLabel.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContextNameJLabel;
}

/**
 * Return the ContextNameValueJLabel property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getContextNameValueTextField() {
	if (ivjContextNameValueTextField == null) {
		try {
			ivjContextNameValueTextField = new javax.swing.JTextField();
			ivjContextNameValueTextField.setName("ContextNameValueTextField");
			ivjContextNameValueTextField.setText("ContextName");
			ivjContextNameValueTextField.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContextNameValueTextField;
}

/**
 * Return the DBFormalSpecies property value.
 * @return cbit.vcell.dictionary.DBFormalSpecies
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.dictionary.DBFormalSpecies getDBFormalSpecies() {
	// user code begin {1}
	// user code end
	return ivjDBFormalSpecies;
}


/**
 * Comment
 */
private cbit.vcell.dictionary.DBFormalSpecies getDBFormalSpeciesFromDialog() {
	cbit.vcell.dictionary.DBFormalSpecies dbfs = showDatabaseBindingDialog();
	if(dbfs != null){
		return dbfs;
	}
	return getDBFormalSpecies();
}


/**
 * Return the DBLinkJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDBLinkJButton() {
	if (ivjDBLinkJButton == null) {
		try {
			ivjDBLinkJButton = new javax.swing.JButton();
			ivjDBLinkJButton.setName("DBLinkJButton");
			ivjDBLinkJButton.setText("Add DB Link...");
			ivjDBLinkJButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDBLinkJButton;
}

/**
 * Return the DBUnlinkJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDBUnlinkJButton() {
	if (ivjDBUnlinkJButton == null) {
		try {
			ivjDBUnlinkJButton = new javax.swing.JButton();
			ivjDBUnlinkJButton.setName("DBUnlinkJButton");
			ivjDBUnlinkJButton.setText("Unlink");
			ivjDBUnlinkJButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDBUnlinkJButton;
}

/**
 * Return the document1 property value.
 * @return javax.swing.text.Document
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.text.Document getdocument1() {
	// user code begin {1}
	// user code end
	return ivjdocument1;
}


/**
 * Return the document2 property value.
 * @return javax.swing.text.Document
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.text.Document getdocument2() {
	// user code begin {1}
	// user code end
	return ivjdocument2;
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
 * Return the JCheckBoxHasOverride property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxHasOverride() {
	if (ivjJCheckBoxHasOverride == null) {
		try {
			ivjJCheckBoxHasOverride = new javax.swing.JCheckBox();
			ivjJCheckBoxHasOverride.setName("JCheckBoxHasOverride");
			ivjJCheckBoxHasOverride.setText("Override");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxHasOverride;
}


/**
 * Return the JInternalFrameEnhancedContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJInternalFrameEnhancedContentPane() {
	if (ivjJInternalFrameEnhancedContentPane == null) {
		try {
			ivjJInternalFrameEnhancedContentPane = new javax.swing.JPanel();
			ivjJInternalFrameEnhancedContentPane.setName("JInternalFrameEnhancedContentPane");
			ivjJInternalFrameEnhancedContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 4;
			constraintsJPanel1.gridwidth = 4;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getJPanel1(), constraintsJPanel1);

			java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
			constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 2;
			constraintsJPanel2.gridwidth = 3;
			constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getJPanel2(), constraintsJPanel2);

			java.awt.GridBagConstraints constraintsNameJLabel = new java.awt.GridBagConstraints();
			constraintsNameJLabel.gridx = 0; constraintsNameJLabel.gridy = 0;
			constraintsNameJLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsNameJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getNameJLabel(), constraintsNameJLabel);

			java.awt.GridBagConstraints constraintsContextNameJLabel = new java.awt.GridBagConstraints();
			constraintsContextNameJLabel.gridx = 0; constraintsContextNameJLabel.gridy = 1;
			constraintsContextNameJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getContextNameJLabel(), constraintsContextNameJLabel);

			java.awt.GridBagConstraints constraintsContextNameValueTextField = new java.awt.GridBagConstraints();
			constraintsContextNameValueTextField.gridx = 1; constraintsContextNameValueTextField.gridy = 1;
			constraintsContextNameValueTextField.gridwidth = 2;
			constraintsContextNameValueTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsContextNameValueTextField.weightx = 1.0;
			constraintsContextNameValueTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getContextNameValueTextField(), constraintsContextNameValueTextField);

			java.awt.GridBagConstraints constraintsNameValueJTextField = new java.awt.GridBagConstraints();
			constraintsNameValueJTextField.gridx = 1; constraintsNameValueJTextField.gridy = 0;
			constraintsNameValueJTextField.gridwidth = 3;
			constraintsNameValueJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameValueJTextField.weightx = 1.0;
			constraintsNameValueJTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getNameValueJTextField(), constraintsNameValueJTextField);

			java.awt.GridBagConstraints constraintsAnnotateJButton = new java.awt.GridBagConstraints();
			constraintsAnnotateJButton.gridx = 3; constraintsAnnotateJButton.gridy = 2;
			constraintsAnnotateJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getAnnotateJButton(), constraintsAnnotateJButton);

			java.awt.GridBagConstraints constraintsLinkJLabel = new java.awt.GridBagConstraints();
			constraintsLinkJLabel.gridx = 0; constraintsLinkJLabel.gridy = 3;
			constraintsLinkJLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsLinkJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getLinkJLabel(), constraintsLinkJLabel);

			java.awt.GridBagConstraints constraintsLinkValueJLabel = new java.awt.GridBagConstraints();
			constraintsLinkValueJLabel.gridx = 1; constraintsLinkValueJLabel.gridy = 3;
			constraintsLinkValueJLabel.gridwidth = 2;
			constraintsLinkValueJLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLinkValueJLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLinkValueJLabel.weightx = 1.0;
			constraintsLinkValueJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getLinkValueJLabel(), constraintsLinkValueJLabel);

			java.awt.GridBagConstraints constraintsJCheckBoxHasOverride = new java.awt.GridBagConstraints();
			constraintsJCheckBoxHasOverride.gridx = 3; constraintsJCheckBoxHasOverride.gridy = 1;
			constraintsJCheckBoxHasOverride.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getJCheckBoxHasOverride(), constraintsJCheckBoxHasOverride);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJInternalFrameEnhancedContentPane;
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
			ivjJPanel1.setBorder(new cbit.gui.LineBorderBean());
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOKJButton = new java.awt.GridBagConstraints();
			constraintsOKJButton.gridx = 1; constraintsOKJButton.gridy = 1;
			constraintsOKJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getOKJButton(), constraintsOKJButton);

			java.awt.GridBagConstraints constraintsCancelJButton = new java.awt.GridBagConstraints();
			constraintsCancelJButton.gridx = 2; constraintsCancelJButton.gridy = 1;
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDBLinkJButton = new java.awt.GridBagConstraints();
			constraintsDBLinkJButton.gridx = 0; constraintsDBLinkJButton.gridy = 0;
			constraintsDBLinkJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getDBLinkJButton(), constraintsDBLinkJButton);

			java.awt.GridBagConstraints constraintsDBUnlinkJButton = new java.awt.GridBagConstraints();
			constraintsDBUnlinkJButton.gridx = 1; constraintsDBUnlinkJButton.gridy = 0;
			constraintsDBUnlinkJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getDBUnlinkJButton(), constraintsDBUnlinkJButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}

/**
 * Return the LinkJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLinkJLabel() {
	if (ivjLinkJLabel == null) {
		try {
			ivjLinkJLabel = new javax.swing.JLabel();
			ivjLinkJLabel.setName("LinkJLabel");
			ivjLinkJLabel.setText("DB Link Info:");
			ivjLinkJLabel.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLinkJLabel;
}


/**
 * Return the LinkValueJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLinkValueJLabel() {
	if (ivjLinkValueJLabel == null) {
		try {
			ivjLinkValueJLabel = new javax.swing.JLabel();
			ivjLinkValueJLabel.setName("LinkValueJLabel");
			ivjLinkValueJLabel.setText("LinkValueJLabel");
			ivjLinkValueJLabel.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLinkValueJLabel;
}


/**
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
public cbit.vcell.model.Model getModel() {
	return fieldModel;
}


/**
 * Return the NameJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameJLabel() {
	if (ivjNameJLabel == null) {
		try {
			ivjNameJLabel = new javax.swing.JLabel();
			ivjNameJLabel.setName("NameJLabel");
			ivjNameJLabel.setText("Name:");
			ivjNameJLabel.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameJLabel;
}

/**
 * Return the NameValueJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameValueJTextField() {
	if (ivjNameValueJTextField == null) {
		try {
			ivjNameValueJTextField = new javax.swing.JTextField();
			ivjNameValueJTextField.setName("NameValueJTextField");
			ivjNameValueJTextField.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameValueJTextField;
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
 * Return the species1 property value.
 * @return cbit.vcell.model.Species
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.model.Species getspecies1() {
	// user code begin {1}
	// user code end
	return ivjspecies1;
}

/**
 * Gets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @return The speciesContext property value.
 * @see #setSpeciesContext
 */
public cbit.vcell.model.SpeciesContext getSpeciesContext() {
	return fieldSpeciesContext;
}


/**
 * Return the speciesContext1 property value.
 * @return cbit.vcell.model.SpeciesContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.model.SpeciesContext getspeciesContext1() {
	// user code begin {1}
	// user code end
	return ivjspeciesContext1;
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
 * Insert the method's description here.
 * Creation date: (5/20/2003 7:55:25 AM)
 * @param argSpeciesContext cbit.vcell.model.SpeciesContext
 * @param argDocumentManager cbit.vcell.clientdb.DocumentManager
 */
public void initAddSpecies(
				cbit.vcell.model.Model argModel,
				cbit.vcell.model.Structure argStructure,
				cbit.vcell.client.database.DocumentManager argDocumentManager) {
	//
	//
	//
	if(argStructure != null && argModel != null){
		setTitle("Add New Species to Structure "+argStructure.getName());
		getOKJButton().setText("Add");
	}
	mode = ADD_SPECIES_MODE;
	cbit.vcell.model.Species newSpecies = new cbit.vcell.model.Species(argModel.getFreeSpeciesName(),null);
	cbit.vcell.model.SpeciesContext newSpeciesContext = new cbit.vcell.model.SpeciesContext(newSpecies,argStructure);
	setSpeciesContext(newSpeciesContext);
	setModel(argModel);
	setDocumentManager(argDocumentManager);
	
	
	
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getNameValueJTextField().addPropertyChangeListener(ivjEventHandler);
	getCancelJButton().addActionListener(ivjEventHandler);
	getOKJButton().addActionListener(ivjEventHandler);
	getDBUnlinkJButton().addActionListener(ivjEventHandler);
	getDBLinkJButton().addActionListener(ivjEventHandler);
	getAnnotateJButton().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getJCheckBoxHasOverride().addChangeListener(ivjEventHandler);
	getJCheckBoxHasOverride().addActionListener(ivjEventHandler);
	getContextNameValueTextField().addPropertyChangeListener(ivjEventHandler);
	connPtoP3SetTarget();
	connPtoP1SetTarget();
	connPtoP2SetTarget();
}

/**
 * Insert the method's description here.
 * Creation date: (5/20/2003 7:55:25 AM)
 * @param argSpeciesContext cbit.vcell.model.SpeciesContext
 * @param argDocumentManager cbit.vcell.clientdb.DocumentManager
 */
public void initEditSpecies(cbit.vcell.model.SpeciesContext argSpeciesContext, cbit.vcell.client.database.DocumentManager argDocumentManager) {
	
	//
	if(argSpeciesContext != null){
		setTitle("Edit Species "+argSpeciesContext.getSpecies().getCommonName()+" in Structure "+argSpeciesContext.getStructure().getName());
		getOKJButton().setText("OK");
	}
	mode = EDIT_SPECIES_MODE;
	setSpeciesContext(argSpeciesContext);
	setDocumentManager(argDocumentManager);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("EditSpeciesDialog");
		setSize(384, 193);
		setContentPane(getJInternalFrameEnhancedContentPane());
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		EditSpeciesDialog aEditSpeciesDialog;
		aEditSpeciesDialog = new EditSpeciesDialog();
		frame.setContentPane(aEditSpeciesDialog);
		frame.setSize(aEditSpeciesDialog.getSize());
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
		System.err.println("Exception occurred in main() of cbit.gui.JInternalFrameEnhanced");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
private void oK(java.awt.event.ActionEvent actionEvent) {
	try{
		getSpeciesContext().getSpecies().setCommonName(getNameValueJTextField().getText());
		getSpeciesContext().getSpecies().setDBSpecies(
				(getDBFormalSpecies() != null?getDocumentManager().getBoundSpecies(getDBFormalSpecies()):null)
			);
		getSpeciesContext().getSpecies().setAnnotation(getAnnotationString());
		getSpeciesContext().setHasOverride(getJCheckBoxHasOverride().isSelected());
		if (getJCheckBoxHasOverride().isSelected()){
			getSpeciesContext().setName(getContextNameValueTextField().getText());
		}
		//
		if(mode == ADD_SPECIES_MODE && getModel() != null){
			cbit.vcell.model.Species existingSpecies = getModel().getSpecies(getSpeciesContext().getSpecies().getCommonName());
			if (existingSpecies==null){
				//
				// unique species name, use new species
				//
				getModel().addSpecies(getSpeciesContext().getSpecies());
				getModel().addSpeciesContext(getSpeciesContext());
			}else if (getModel().getSpeciesContext(existingSpecies,getSpeciesContext().getStructure()) != null){
				//
				// existing species name found in same compartment
				//
				throw new RuntimeException("species named '"+existingSpecies.getCommonName()+"' already exists in structure '"+getSpeciesContext().getStructure().getName()+"'");
			}else{
				//
				// existing species name found in other compartment, either use existing species or abort
				//
				String name = getSpeciesContext().getSpecies().getCommonName();
				String msg = "Warning: species name same as existing species\n\n"+
							"species '"+name+"' found in structure(s): ";
				SpeciesContext speciesContexts[] = getModel().getSpeciesContexts();
				for (int i = 0; i < speciesContexts.length; i++){
					if (speciesContexts[i].getSpecies() == existingSpecies){
						msg += "'"+speciesContexts[i].getStructure().getName()+"' ";
					}
				}
				msg += "\n\n          Did you want to reuse that species?\n\n";
				msg += "(Note: species may be copied and pasted using using popup menus or keyboard <CTRL-C><CTRL-V>)";
				String selection = PopupGenerator.showWarningDialog(this,msg,new String[]{UserMessage.OPTION_USE_EXISTING_SPECIES, UserMessage.OPTION_CANCEL},UserMessage.OPTION_USE_EXISTING_SPECIES);
				if (selection.equals(UserMessage.OPTION_CANCEL)){
					return;
				}else if (selection.equals(UserMessage.OPTION_USE_EXISTING_SPECIES)){
					//
					// user requested to use existing species
					//
					SpeciesContext newSC = new SpeciesContext(	null,
																getSpeciesContext().getName(),
																getModel().getSpecies(getSpeciesContext().getSpecies().getCommonName()),
																getSpeciesContext().getStructure(),
																getSpeciesContext().getHasOverride());
					getModel().addSpeciesContext(newSC);
					setSpeciesContext(newSC);
				}
			}
		}
		//
		dispose();
	}catch(Exception e){
		e.printStackTrace(System.out);
		cbit.vcell.client.PopupGenerator.showErrorDialog(this,"Edit Species Error\n"+e.getMessage());
	}
}


/**
 * Set the AnnotationString to a new value.
 * @param newValue java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setAnnotationString(java.lang.String newValue) {
	if (ivjAnnotationString != newValue) {
		try {
			ivjAnnotationString = newValue;
			connEtoC3(ivjAnnotationString);
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
 * Set the DBFormalSpecies to a new value.
 * @param newValue cbit.vcell.dictionary.DBFormalSpecies
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setDBFormalSpecies(cbit.vcell.dictionary.DBFormalSpecies newValue) {
	if (ivjDBFormalSpecies != newValue) {
		try {
			ivjDBFormalSpecies = newValue;
			connEtoC7(ivjDBFormalSpecies);
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
 * Set the document1 to a new value.
 * @param newValue javax.swing.text.Document
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdocument1(javax.swing.text.Document newValue) {
	if (ivjdocument1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjdocument1 != null) {
				ivjdocument1.removeDocumentListener(ivjEventHandler);
			}
			ivjdocument1 = newValue;

			/* Listen for events from the new object */
			if (ivjdocument1 != null) {
				ivjdocument1.addDocumentListener(ivjEventHandler);
			}
			connPtoP3SetSource();
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
 * Set the document2 to a new value.
 * @param newValue javax.swing.text.Document
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdocument2(javax.swing.text.Document newValue) {
	if (ivjdocument2 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjdocument2 != null) {
				ivjdocument2.removeDocumentListener(ivjEventHandler);
			}
			ivjdocument2 = newValue;

			/* Listen for events from the new object */
			if (ivjdocument2 != null) {
				ivjdocument2.addDocumentListener(ivjEventHandler);
			}
			connPtoP2SetSource();
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
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(cbit.vcell.model.Model model) {
	cbit.vcell.model.Model oldValue = fieldModel;
	fieldModel = model;
	firePropertyChange("model", oldValue, model);
}


/**
 * Set the species1 to a new value.
 * @param newValue cbit.vcell.model.Species
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setspecies1(cbit.vcell.model.Species newValue) {
	if (ivjspecies1 != newValue) {
		try {
			ivjspecies1 = newValue;
			connEtoM5(ivjspecies1);
			connEtoM2(ivjspecies1);
			connEtoM3(ivjspecies1);
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
 * Sets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @param speciesContext The new value for the property.
 * @see #getSpeciesContext
 */
public void setSpeciesContext(cbit.vcell.model.SpeciesContext speciesContext) {
	cbit.vcell.model.SpeciesContext oldValue = fieldSpeciesContext;
	fieldSpeciesContext = speciesContext;
	firePropertyChange("speciesContext", oldValue, speciesContext);
}


/**
 * Set the speciesContext1 to a new value.
 * @param newValue cbit.vcell.model.SpeciesContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setspeciesContext1(cbit.vcell.model.SpeciesContext newValue) {
	if (ivjspeciesContext1 != newValue) {
		try {
			cbit.vcell.model.SpeciesContext oldValue = getspeciesContext1();
			ivjspeciesContext1 = newValue;
			connPtoP1SetSource();
			connEtoM7(ivjspeciesContext1);
			connEtoM6(ivjspeciesContext1);
			connEtoM1(ivjspeciesContext1);
			firePropertyChange("speciesContext", oldValue, newValue);
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
private cbit.vcell.dictionary.DBFormalSpecies showDatabaseBindingDialog() {

	SpeciesQueryDialog aSpeciesQueryDialog = new SpeciesQueryDialog((java.awt.Frame)null,true);
	aSpeciesQueryDialog.setDocumentManager(getDocumentManager());
	aSpeciesQueryDialog.setSize(500,500);
	cbit.util.BeanUtils.centerOnScreen(aSpeciesQueryDialog);
	cbit.gui.ZEnforcer.showModalDialogOnTop(aSpeciesQueryDialog,this);
	//aSpeciesQueryDialog.setVisible(true);

	cbit.vcell.dictionary.DBFormalSpecies dbfs = null;
	cbit.vcell.dictionary.database.DictionaryQueryResults dqr = aSpeciesQueryDialog.getDictionaryQueryResults();
	if(dqr != null && dqr.getSelection() != null){
		dbfs = dqr.getDBFormalSpecies()[dqr.getSelection()[0]];
	}
	
	return dbfs;
	
}


/**
 * Comment
 */
private java.lang.Object speciesFromSpeciesContext() {
	return null;
}


/**
 * Comment
 */
private java.lang.Object structureFromSpeciesContext() {
	return null;
}


/**
 * Comment
 */
private void unlink(java.awt.event.ActionEvent actionEvent) {
	String ret = PopupGenerator.showWarningDialog(this,"OK to Unlink DB Reference?",new String[] { UserMessage.OPTION_YES, UserMessage.OPTION_NO },UserMessage.OPTION_YES);
	if (ret.equals(UserMessage.OPTION_YES)){
		setDBFormalSpecies(null);
	}
}


/**
 * Comment
 */
private void updateInterface() {

	getNameJLabel().setEnabled(getSpeciesContext() != null);
	
	getNameValueJTextField().setEnabled(true);
	if(getSpeciesContext() == null){
		getNameValueJTextField().setText(null);
	}
	getNameValueJTextField().setEnabled(getSpeciesContext() != null);

	getContextNameJLabel().setEnabled(
				(getSpeciesContext() != null) &&
				(getSpeciesContext().getSpecies() != null) &&
				(getNameValueJTextField().getText() != null) &&
				(getNameValueJTextField().getText().length() >0));

	if (!getJCheckBoxHasOverride().isSelected()){
		String contextName = null;
		if(	(getSpeciesContext() != null) &&
			(getSpeciesContext().getStructure() != null) &&
			(getNameValueJTextField().getText() != null) && 
			(getNameValueJTextField().getText().length() > 0)){
			
			contextName = cbit.util.TokenMangler.fixTokenStrict(
				getNameValueJTextField().getText()+"_"+
				getSpeciesContext().getStructure().getName()
			);
		}

		getContextNameValueTextField().setEnabled(true);
		getContextNameValueTextField().setText(contextName);
	}
	getContextNameValueTextField().setEnabled(
		(getSpeciesContext() != null) &&
		(getSpeciesContext().getStructure() != null) &&
		getJCheckBoxHasOverride().isSelected()
		);

	getLinkJLabel().setEnabled(getDBFormalSpecies() != null);
	
	getLinkValueJLabel().setEnabled(true);
	getLinkValueJLabel().setText((getDBFormalSpecies() != null?getDBFormalSpecies().getFormalSpeciesInfo().getFormalID()+" - "+getDBFormalSpecies().getFormalSpeciesInfo().getPreferredName():null));
	getLinkValueJLabel().setEnabled(getDBFormalSpecies() != null);

	
	getDBLinkJButton().setEnabled(true);
	getDBLinkJButton().setText((getDBFormalSpecies() != null?"Change DB Link...":"Add DB Link..."));
	getDBLinkJButton().setEnabled(getDocumentManager() != null);
	
	getDBUnlinkJButton().setEnabled(getDBFormalSpecies() != null);
	getAnnotateJButton().setEnabled(getSpeciesContext() != null);
	getOKJButton().setEnabled(
			(getSpeciesContext() != null) && (mode == EDIT_SPECIES_MODE || (mode == ADD_SPECIES_MODE && getModel() != null)) &&
			(getNameValueJTextField().getText() != null) && (getNameValueJTextField().getText().length() > 0) && 
			(getContextNameValueTextField().getText() != null) && (getContextNameValueTextField().getText().length() > 0) && 
				(
					mode == ADD_SPECIES_MODE ||
					!cbit.util.Compare.isEqualOrNull(getNameValueJTextField().getText(),getSpeciesContext().getSpecies().getCommonName()) ||
					!cbit.util.Compare.isEqualOrNull(getSpeciesContext().getSpecies().getDBSpecies(),getDBFormalSpecies()) ||
					!cbit.util.Compare.isEqualOrNull(getSpeciesContext().getSpecies().getAnnotation(),getAnnotationString()) ||
					getSpeciesContext().getHasOverride() != getJCheckBoxHasOverride().isSelected() ||
					!cbit.util.Compare.isEqualOrNull(getSpeciesContext().getName(),getContextNameValueTextField().getText())
				)
			);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G370171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BD8DD8D4E53AECEA112252AE969A3BCB0505C5ADED344B6DDACB069B969595DB5A45223B6E0DEEF62FDB5E2E95FBBB8C183820E2E2319BEE342921920922A2E289C26B4F34F295A7701779B94040B803B387C1C1396FF73E6F7D4E19B3E7981C7B74BC777ABC6FF3B86F773E5F7B7B7DFFE7940ABF09DE1AE3A896041C25827B37ABC79062BD02F0FFED7F6D088BF71A8A84553FCF81D68B3FEB4F03E7
	B414F53DECAAA892B26B7361BD815E5BDFB295FC84FF2F91FED79F5D8BFF7811B3GE58DBF33EC9BD94E2A8DA8E7197C5EFF22901EAB819281C71EF9AEA2FF720992064FE0F8049C8360E41ABBCF14B25C81F8EF87088408F3D17AE4F8AE17A50F2B2DE1F427EE089596BF66B62CA075286AC4D85FCBF98F30E71970CE4E22DEA26BD9D9CF4293826F09G4478DCE17D418AF856B577F4FEE53558254A8BD6516128BC6732090E4A4EBEFBEDED134DAE9D6B952DF65153E8B7BBDC1D0E73243E0FCF664B7C02D08D
	6FCBE45CEAAB11918FFF27GE4F2FCFDA7415739CD85ADG8EB75A357773BA26F76B60AF05924F6C652B7C58B556CD6D09F3ABF6B936D8F27FA4476048C6626BACA8AB74188A4AG9A81ECGE381BE16E3EF5A30991E7596E93F6B5CB917333E373741E1F3FFCB6C043FEBEB2104610E0AD6175BA688B43E73CED46868B392D03ED72E1A0F7AA4F6BB46BA6E68BDC246FDB79A166978A4A3F6509065213EC870A8BE21324BCE7A131D6E714EC5EF595F89EA59422B5B16AD5511DD7A5C36E5D92C1EEC156CC60944
	FA0A1C832570B761A371247869B2BED40527F979110CA76F71D0D6510739513A92F5B1665DAA94D55D246B0748D9E10658BE2AC3D21FD617A73AC85D8B7B284CBDDC17B7E53C630202273A7CCD4ED3723E904AD286CC85B43FDA4BC9BB0D39E4AAC882188B308820E890748558B708B976538DDF0EB2471A4DEE29214F6E3009EE017236EE7C901E620064B69FF4FAA43353AA1A4CEE3B596290217F23B4660D448796AF3E8EB9772DA063EF22D3F41BA53B4B89351B4F09126876D09A5746550446AD4A25AD76F3
	A288B8C73AC2DA5E3FF18D710759A39D6F3519A5D1212065439BC9CCAE18CDD0A3D4GFFD33F0C5BC47C558E768FG04F2BF3CA357F7D8F4BBC5C7ED2D51656A696BEDA11688F387091FF749FDC7947C1DB008BE7E41E602AB9DB295D48FE1BDD51FFF202A2751EEA56A1B5D97310D5DECA5F1B9G74DDGC3G91G71G29GD9C318CB7F560A39543862EE2162B7D3F4F3A99C72DA38CCF328643236CD5DA54BA27A6E0332F6009000E800A4004C4BB89E2C337686E867C4AFFFE5E8875AC0F0919A7A83BA7CD4
	F6DDC0592BC6A8DB4A6AA1035E8AD53F202649556123717B77ADA49704AB2602D2003AAB98C34767E534CDB418BDA23602A3EEC8E05126C4146A7FEC1744E4137CD98B3097609840F1007D826921E87B5C81309B60F300CDGDBG3EA66D87E08340C4G529687813EG2882B888300B64934037GC7G4E8218397F5E166537006EC3GB1G29G0B09CD43B026GB8GA2GE682ACGA88C01FC85B885B096A08EA09DA09BE07D8FCC85C6G89A08AA099E0A1C071B5609FG9BC098C042B518BB5FF62665780F
	4F8A555F681F11E23DC1631FAF18CF2AD8798E660F5D4C27FB198FD7B2DFE3BD87B5BEDB290A49F79A19684F15AC96239D7B86C7C1B371AA6A7B3F84B43649A346D65D2E309D0ED467E782EDF7240D2DE3717D1C4518142FE5F12673DE5A0EBF91FC7BG128F5B85A597888E5B7FEA0DCC4C93727727D7E17F26D150EC78BF9027E00076F116F262D3710268149A45DE51E903BFBC2C4CDECEFC4F46758E9767F14978DE18876C0701574371FD65446FE6C9F25BADFD12F8CC14A43B33D3A93FAC4B7274D9FA15A9
	844A9ADBCEF247558F73GCEFFFDB999C32DAE27D314BBDB4E424AA748E33868904F01527BDDFDCE0962EF58C456B9BD62C5B607183269FD014C819CFDE4BEC2578E71E612AB3DEE7BB968424914C6289BC346773F1FACD179A8837AB73286AA023275E3F0CC6E7F8C47647517E688C9E7FB8DFAF34D4869138DE3475071AEF20CB2A6D31D2E5D2C2FFB18BC17E0F88F5555DAC17C6A331891E85BDBA38CD8890F0716D5D1DC44A9222B4DE56DA395D5F15A199EF9DE5B700D4BFD4E6CE07531322C63C5702CF7BA
	DD12BC5BBA8601F4F6523252CFD7A9B2763B1C9224C595631BFBC6AD0BACC26938DA7E8531B191ECCF85D8B08647557CAD249C67C6ECC05D6FF2B8E81821EC5FD8920B4556E2664BEC60AD9ECBE3311217F75B30BC945E23FD4AD708D89E8F6FA9BE65E3F839B04ED4D0B2CEDB7EB6AF2F0332961F721FF4E0398D4A06D465544E7F58C27C69914F7709B0A5A6134843BCA73337FC20A9B3317C2465F995A4BF9D2A79E756B8A2739BBB2E3B4360BD86A0F99C1D534D0567DFG1FF9AD21AB6AC63AEAF8B782B82E
	257A45DDCBF524F4691C2E62BA18A7DC2750592ED3530576A0DD846063D4F4290CAEB414504DBA0FF4823C17G5404527966C0281AAE4A0DF46921F4DE3A1051ADE4F47B2EA7F49D9E244B8E25F319626BA9FDB9BCAB813FC426AB1338DF2E27739EC2C7740B39DEED47ACCE57B81E4ABBB51E52B9462B69AE75717A46533932160E5AD1F98169E246D33D889D094B4C71EABB16F03A5471F47E05F6AC98CF6D20757D3C1F5B2B2A8F65AB758D8CA0DD7184BA0F437AAAA6507A289D5BAEF2FBA750BCB9B5C14F5E
	051CCE0272B000B8C637F08225237997FF8969EA42E8F9F398DDC7B8425479F7E990690698DDC4181E7FF68CA1DDBC2BA7851EF3811604296972AEA3DD61C4CA37FEA23517BC953FBC49692AA752B9AC718B21B7CE24FE21F457DDC13A7609F43E0BF483935571289FC63A7009D42FD8F8E68164CFD24BAD95102EF4921DAF137A508EA59EE91C2EFA92ADB7CE72768B75F3C48E4F3FC9D4DE4CA43AD6CB717273898352A5327234C9FAFEAE4EC53A2CC90A1FC93D42E4359DAFF23A2249F40E0FFEA91FCC6D20E3
	5211CEB26FD1765E647D36C387E1DCF0BB4D0EEF48A23E4959C53682EC249F13775292793EDB4DE45CDF93CF13B122975EC38122A763987B748A5CDF0B680995B2DE2DDA66EF5FB011691FB1D999E397AF2AFF1F0EC16ECAF9ED9E8E6DA29C471F618A759A3F5E63F1D96D7200477A5E4AAF54E3B045ED7F620F706C303BBD52C118A48DE0BF3D7B8B22AF59FFE0E8369F73312D10593A8F648E04631E25FB7DDF84BFFB163535B89C33BDAD6BF674C1782569C27B14E90682BFBE088F27362705ABBE283832E4A9
	5565862ECB7E8D50E6G86EE105B0A0D4C134878C1692AC55CE71D89650BEFC01DE30FED51515946D74035355A79842D6FCEDEDF458FE1BC79A156771B368F313EEE7385F325436C6C84636559867A2F2C83F50E823E749F224E27BB084E244F21F433B9DD610FE07C7A915299BA899DE9B3144EE5C73AA120C9GB0C6487D0021D865470500AF86FC8DG1D5BAC3011DC40398D453DE3A7F5CBDDF68F0D539B757B98FF6ABD378A094B428CFAFB41DDF7C49AA2A2E8FCE6C6A8F18A3955129B81C0755C5DDD485A
	D8619430E78A6A99FFD82D679060E3GD226201E515DE45EE516C3D2C99F4D223B8322A25A4AE5AF1B7B25CAF91EDFD9AF97B7113FB7E1FB0ED8CE5A3315B4D8C73951BF431ECDA681737356B5EA3B6B064F685A9D3A2A4B10B1051DAF5C28589D71A3CBEE6C0F506EBFF03B1B01A674C6343B7D041E5DC9B7E2AE7D3E122C67DC07836BCC656CBA0FF252210E421BD04E0B0DDAB90DD056F5934AB9D3C17C5367F4581DBD2315755AF91EF3D0CF8217F5690A1E2C05B7E1AC0F3A030DA5653F566D1D1FF77FC59D
	27FC73B85DB8053D5AE990A2E9FC4AA315B8E5C30C0AA2B0CE335DE8D3AF5044C6224E959E14C969A6FB10AE93E8CA26229F0B3FA2CFF9ADA6203E799251333101F7FFAC3F065AD06F10548421E44DC4DD3D89B4CF257A8ECCD574CE3945121BF18B6AFDD0423827G0DB08D75095F58844FCED152F4E15F38DD6768B22BFC13D137572B54F06C61FA3F3B8E75DE5F98A354AC6E712BF74DB42AEF7BB4C56F3895EFAFCD82E0B944750E871A2CE9BCC77A505F540E3695E4BD51A76F014B23F00759AA1613E42475
	5C560775145DECAAE83D997B456FAEE01C428117F2B3567F4F97647AE115C769BE6DC7BAA18A7A47A8241B56CF46C52BFCE870F79874D15EBFF17ADE200D0DC2F9378C703CGDC490F311EBFDFA47518795A936BB9F911778BC09B4A69E7DDD42FB9ABF91BDBFB89691300F6490F314D05D935ED0E7473DBFF02758DDD7A5F751FB33E7DCA51DFBC5450A7C9AE6759FACDC501BA8EEA433CCB6823C650EF086B6869A03AFB2F41318D59863D5B5FED0368078460DF486B786350AA155F3EB5BB4D1D221B49BDB304
	F2577F945689BFC51E59C3D8B77565E39635AFA3FECAFDB913533FBEA46FC708E4BDAF5AE85DDFDC463A5300AEBF9AE9A3AEFB6B7D1F1CEE9F508CF03A5EAB4B945B193E71D710B69E6832B86D26E1B2CF6AB2FB0EDC905DEE3BCDE4745D433C8D5C82ED609624FFFA18445E8A3355CE3152C36237DF7E1B86FE2052538F939396B1DA0231277EB838D9EF8E914E6BDFAA6F090ACE320C37B1DD1A8454BD8668B2EE4176713600BA965F8A39FBAB56511E93ECFE52FA9F4CC1F9E3214E193C5E12CE757C06520EB1
	208E0B012E62B6247DD703FE1B5B1B0BF55B00B60A532764FA77A14B7210AE9DE88AE310AEA40F50B92177207B4D8F7ABD83165731B13EE7C874ECF41E3C16E80D21E723399C7FDB995F2542D3FDFACE23BE83D016910367F9D32A489E603E695086GA400B000B80054693806F8788B3277BC62F9A9AE060EF9EDF86239E5A5115BA3DEE46F03156AF1117A61D39B71C356F47FE7EEDA20FCDF4AFC426D236723F63F330D6471D660B38258G06G22EEC71B05AFAB82590C673E4CD82A4FCDC15AB17D2A6D2063
	6F26936ABBA7F92B8F99082F75465F192C6E545B35E72635E2F0F7A78E8449979FA45FC3C172ED6C88AEA6559DD79B934A77F810726685A92F4F9E48AF05F7F857C9794684645B2A6123637F86791E85DF5332C1233676D08BF4FF5F58C5070D1EAF7C790C7AFC61409D749E01831EA1GF41F64A1C3BE2BB7728E5C8F397C0DFABFE4662A4BFE77825218FE0B6ED0F2BA5355320C36712CEA226756D8E863GB60090009800A400853158567F7EE5039068BE889E743438781F7B3507B568572F3B35FE4D0E7576
	23FEBC3E75617B0B663DE406DEBCD69D7B13FEBC684C0C144971C8B3EB63A14F8B214E4699980FD78EE3BCCE4D40B16274CE624788F80F87C881D8G10FF2729E0BD4001BB510F37FC39B7109F65013A45359FF087DC2E9E9D97B2DF647684674355BEFCDA9FF65D694DC76D3E69C4BE2BA364CEBC675FFBBEB89D8E7B70E9F5C85468C06313F9CCAFBEE9C03B60CE0C4F5AA654B31B6B79FB9FF923EB7F6F85642B3ECB4FD777DEC19D0CF7E10E246FA2B9B2965E23819281528116G9447419AAA8EF3640DAF77
	0CB2C7684C4EB7C3D89F6B8E4E66F98179DA63747826F80213F7DBC03EDE0DBC6A4F165D441F21D09683B093E0AEC0B6C0495D26023A3B511F0DDB8376DD7C1A59E87A2EA3D2F0B9FF5A072FD2735EF2371E1D4F5492BB9DD0968A908DB093A093A09B206C9E34B3F2EB7DD5589938FF79FD10F63E97504EFD7768714D688BCE5EAFFD783472DAF5651D3E90DC3E7684646B551577F9FFF072F684648B551577F210727EBDA0DF342E3CBB860213F7FFC03EC4DDF9660B41650B4B07CF1BAFE93A72BE3E941C3CCD
	BEFC5A31AE6B9E3DB16491AB0EA1424FF09CFB49272E5179787500FCB5BF53ED1303C136C99FBE2D0F1BF565590602334F9D102FCBA30F4E8B3FB2113ED3E7DE383FCB3476B43886D8ACF22CB8678B613158FEB9B8DD6B8372A56A7A668FC14AAB389208AFCDD75EAF8272694BCB8E481725AB2FF7B838DC3B6243274DB5A1DE2FFD05EC4676D5920FB1BDAC84E7F3DBC03E66F8BD3E77FC78C6E7730A1CC0B6F7694AFB4407EFF476BD9D10AFC4D75E7506606C3B4907CFEBDF242E3C7D394159775F817962F565
	2D488BCE5E478179D2B47268778669721D09CCA8DB42F3762553E43F3474DEFCFFD5FEA76BA27AFE7EF4EBGD9DAA0F50D16164A98FF55B2746E5F7B83BA3F7DC9157F3B29532B746F265ED525FFB7755E2A55948FF5283FE04172FF289A796E6A43D5FAF7D75749F6BDA76F173B7B1C127D1CA80B25E30DB8C04E26E196FE344FC92768C432C73BD74AE598367B37F779F6FDFBD7ED5737F76D76116DDD375D1F3DF48D72C19D49A9633D3856C8583ECD70DDEB10BB29AEB79A54A21A5DC7BABA78020A9C0C8A4C
	C72FC864DE374FDDDEFAE4484E30626C4B99CD3364EAA67B6B540FEFEEA2FBGBAE3F63359A9BAE85FFE748C0E57DD77625D22CF5AC9DC2261BD91A00D3717C7FC7A9AB5E481DD3626BDD01F9C5ECB76470BFEEEAA28GE884B885B074F33EBFDE9D70CC20E9E004B301B72A7D477F6DEA7D783FD7CD6EC5F9DCFDEE2B280D717BBBCAF819326ABBA80967085BD97E6CA07C12595DA97A644807B2BFAD73477F498E328FE9315B6CEEF6FB1746FB4E072B647C73F8C40F3CF951E8164CAC4E973AB14ECD12EBBF39
	1BCCE318EF4171B96A67984399EDA40669703E90A06FBE0C614243FAB1AC3D0F46ED6BFD3E7D995537F327FF9FBBF76A7BD85A695F4703BB837BF86CCE7FBE1E30B3300FA3F67AEB2B541F35BDEAFFEE93300F0E527D0E33117926951E8E0D1FE8FD52F9F5FD55BCBEA5ADFCCEFC9F466791793CB6915E53G32F8FC7E5C24A7DBC820720A61D91E20A73B5C2D127DED35B0725C340E55518CCF1B26BE6A3B97F7790F776FF669477B35DD7E637D462E4071FEF7177FF89BEA82473B20E664F89BBCEA9F6D6078D4
	CF8143B7F738DD671A2B184CB33B7D7BC05C2D6F031E5D7EFD605E9D5887033B7D7B601A5D01FDB0E13736DFE1F71BA4AF9BA15BA9FDC62DFF9BFFDB2BEF632B357EEDDCD49B584637EB7D5BA86C89EC6372BDA347393CCF5D8EF7F1FC2697FEBB47C7DCD06745B60EBF536F1D975B983EE4C0CD2F54736200B77DF6060F3C28163B13539F3D24466F66EDB6452A57E687D8BB8D03E734EE9F706120FA9E20FC3B12B92816D34371B78F296D5845653F2FABBF0149CCC9709DA3E8FD5D175575A9F6D6DF71764B8E
	06FFDD035F4970F78C2B6BD17CB2EE331EDE99CC1FC5704C3BDF4FAF57885EF2F6B17C4E1CBF2A7CD23595695F49D17BD14907A40397FD9562AF652A75D57C5E1867AD371A4D4B6E15732A43EDBE87E9ABCF76626E176F2C71EF28CA603D82408860G883B1F5F2B4DA7F3E37D8B637255A6B2EDF5AAF3638F6BD6285B8A1BB7D2FFFEF7C66B4F45AA7F657F425FFD114BE6B2F6157E025E8BD94B7161B23ED9052777C5B64873C972BE964A367E52D4C057E8AF98F00D1641F1BB647D230AC4D3413EC41C9F9E5B
	6B3F0FBA31D73F0FEA5F2BBF3F6F58BB727C5E3957DF5F42563196354FDAC0472EC445CFC3097EFC36CE7EAD0E70C46A1B259CDFAE63E3D4F86A331F3561DD22C4A8AB7D8774CF2E1178A7845E43B94EE6C4BFC6F15CFDA710EEA14715A8EBE20E7B3703686F838F706F59DB34F9F16A81452E5E877C5938CB3ED7B676812D0D8DB2BE72812D0DF3AE230DF1D0D6740F28D3D5936AAEF15C8F1BD077900E0B2EC55D53B86E09AB483B0063AA0711F79147DD574873ECA662DE12F32F855EED9C57E8C35EDE0E7B03
	19601260BD1D6356493CD9703E04632E31A2EF5E03080B5A0C7A35F05C9FB6A3DDBB47EDB520FE718F62F96D6AD384C7FC188C386C87896E6BD36ABBF4240F9813C37700E879F92FF2125BE73D4AEF73AAA7EDA6A7D7DD3E7D343A1C446AA50116537B950FEF407B95590F478B0E7B477A7D1D1A4607E85EF431E75913D3F31A27B63FCF6DF54A6B50E8A8CBF888FD707AD9744B5C07B0B747184B09ACF8AF7A1529E0BDC09D40A90031GB1GC9GF381967F8AF9EE14F9E258FD7162A4D3C1F5927BB6E89C7DE6
	2B855E25A47ADD916559529E2DE38301A2A6435FBAB80669B5D7C533187DBDD605C33D7D5523066C077469282EA63F3AAE95FC3F69D643AAD6479A958EFFE3C07D5B83A45685235093F3D08F1F11C4E27AFBC3A3FB0FC8521FC764B01D485F31C0B393E0AD33A5AD0976A1F89FF10D3074107A0EE84D4372FDDCAB3613758FE3AE2D175748C3709E4EF137D9F94EF15CBA11675C43188B1F49FDE979AC68ABE6A12ED246116FBE49B7226CFC2EED34FBFB24AE725CB76BEA778F1F9C358C0C4352D152A34F9A6131
	93F87F7454AC72FBD843A4E4ACFE03C60CDF340E6ECBD99D88B3D571A371C81825440F56FDEB13D27717A79B8368B6D25DC92A3AE99EDCF39873A0F59646373A85E31E49F1779C677D74A37C3E0EBC0E3440FBAF479D10F1645BDC729DAF45BDF5D99B8F42634F065E914A7C47BC4BC746C8FB42D7936F15BC8E333FD162B0238963902623AF36D1FFF108FEC49B07B9359807B8A82372569DD064B58D233CA49DF9B98164257B480BEFC4F9190F603C79AD2BF7CEFFFBD011FF520672976B48DF26496B7CE4D381
	8114DF1A2C6D130AAD3EFD52968B6AD41E0CBAD5EBF41AF2C851E92A95F52ACB5657C95D961A0126D92513ADD95B4E928FABF597EF413AFBF56A46BA516631495EFDF0E432565FED8634ADA6995B4507272E76FEAD1DCFBC3C8C6793898F5CA9A4DE5B68773EE6A253FDAEFBA626CE4DA1CFF6074CC856D27933E1AC86B8G508EA081044D46F50561ABF6AF1BEC4757CB12595AA5FF394A8F9A6CE23F02A68BB59A37F1E7486FE2B84C97DDFD12A0947EDA1E57717B2951D0FFA2C0A6406259745EDD0BCC1357DE
	B2027D346E699F917F519DFA7C1D08F1AB949C6EC12CBFCC5622E4B9F748E836088E64BF55C446FB76110B3A18154F2F7B13F7397C73897C32AD27AB6DDF436A1789146B382C7C5173E46ED06F7D5D8EAB7B68BB221B516E6C71562DA0EF954353BAE9A1DB735710F9A63FCDF0C0798805FEAB721F356AF9E8542F29DF13FE0D5FA9BDD32DD71E4E4B5BACFA65D93C7CA92BDEF9762FF16E7D57DC5C43ADFA54D47009C067CC79827EDB7435AF8E5B5E23FF97F8FE3FDC0879DDFA57EDC263730D064F7C647701C7
	E9DEC7BE2A5CC76E7DF175F26A2FBEAB5125436C70083C6E68FE757D7D1ABFDE237B7BAA8452D81D8B58B36C3D29B9345EBD96125B9617DBDE94535CBEEA413CADCD0139B3C0BB0014C23FA925B429D62489CB21799FC31E0C0676BB55A276BBE9A97A7D7CF286C41785C033A40576BB643DF00E361FEDB3915AA307153B49943FE393193B6E173F49D54A280F1EBA2976D151B407816B546AD3B2076A38FE0E62FF1A8BF1C64C0556B9B81EBD582C447B2D7F9638AED38F2B63C4FDF53A85FD95BE077AAA961EB3
	6750758975556C63682B74B97A3E425F0CA2F23200A6FB0E527F97BD2675D55AG71C9E3035CAAB57E1AB1CCFF5B66387C15AE16D2DBDFE8C2DBCB9FC3DB7FF4CC31AF33D6ED9F6539B00CBCAD1C272EC561F9345117A74C06BCE10F6935AF746D0263CABD8D965FFA9E63770F12C72CE7997999B346533485F932F5F996FDCDF9B637AA3269EF9721EC769DD0AB69C32A9F073198C082888508FA1C7FC662613F8A72F823B356B459BA49FEB97995C8897722669B7D6FFBADB0867E6D48570D249F3638A01C67C8
	7D08FF53C872C6F2754AC80ADBE2A46D43ED6F6C12A8A9437F171C6B8E31C3C653FD1B64B305AC37924136C50F63B741D56DDE5FF210AF6BEAEB7F86FDF8033953A80F16545F05E779DCA8954F43EA4F12BA5B61FDGA0BC953F5F9858087DE55C6D378AE93F537FBD0B4C164B0658D45ACED2D315769A324512CB6B696ED1773B244C5FBEC57E93143F6289259E1CD7678AEFF678B9D31787B3EADF659EBC0FF5BC016347729D080B6138F0FEEE1B41F16F5B90D77CA44E25BE773AF3E384FC7B13A32FA3E8BE3E
	FE0274A192500683A4G2482E482AC81A8FC4AD4D086D08DE08430818CG0481C4834483A4834CFD8A7337708879DDB713395B779683A0D92065A4F61B6DA40D199E4764FD2B05D0CFFE9A5F53BD32E5043A1A5D2EDE51ADDD24D8F93640768CAFD9705B43F2282BBD8DF36F46236B841F5FD5014ECEEC10073286516C646DFD961F0BC726E17EAD126BCD0377ACG61E97C2EF43195F33D84F04DCFE31C6AECD8C76853D8C7377CFBA1C970BE1770F3DAD1447EA57F192B1DBB641F91389F6F177F267E1BFAE403
	BF7FC1EFF288D2730825DB34CAAD66CE815711779A55F9348FF4E9F9E664BC22BE985702BE68827A68E7502EA7DA5087F31FC19F3CAB5396BDEBAAA8875877AC523EF51C0F194F6A774B7EFD3036DD60B63C64F5BE9E8EF545BCBB328D14EF506B6EE3A270240E0A6F861B1AAF93F8960F0A6FA92FB3347CF4D3C1E97AE8788EEDD173ED851E46GFCECAD53B6429D283640E34587ED72EF77C09F5FF231171D33FE54C666022E0E8E0F4847245236A20EBB68E9F6F96C64FC166A7E184FF9D2A020FCF30364DBFD
	76EA7968F944BD72D8531A2E4C11A87E17B25E2642537C5FF796733F974A525371BB25A572FA2E66B7500EG9CGE381E2G12FE037D5C3DEDDB05GF71BDADC3DC6881CC3F59CC56B27736EF428EB91C071F326028A00FDG2D4FE17D6F361173E7FF7D68D117CBBE8EA46BBD6812C5372AF7E6F73377687950719C4D3990F8C6BC27F7AFB3FD179EDFAC231D894FB4DD3EA996BD3E850CF689BC8B1F573D1BED5363ABE3345570BC206123FBFBDF496B36D34F5318D25CFE99A7B19C75E34EC97F6DA77FE46076
	337A247EDC6B23133EF32D52137AF32D3F1ED44F35A86E7313244F6C0431351553D5F0DC9B47D549F5925CF7F61B5445713BE43D887E00C884A2FE2F2CD73F0CEBE53886952E0D617EF6127481E0F2174B0D3C4613E45F2A436EF05077E619466E04AE017B63382C233D572C527B8447A9FA371EA23162672DF428E7E5E76533A93B933A983343FEC9755B1AB4A7C2F4F3C98D613A39B4A9A01F763B934A77E8C0BE7D7E46A64FA1621E5776B767E4FC62735A7E66AD8B76B749D0D6BE9767016F685E4B885CCF66
	6B361D40FC75D74DC76DFAD11E87054D555A7B0A0C0F1C2B353757067646C0D97EBC34375B6B9BFD8760C3660DBC9652761DE274ED73CF9855ED1E56BF41671CFBF4FE199AA45FB3D74DC77D66BACE7C93B1CF6B4F81999FBDCF6B4F1C63684FB8A8ABFA817D796EF135FC897021AF0C461FEFB5797A735DA635BF59997D30AF5DCD433EF431B6DF3A3BED3EF47FE27525FB4D6ACBE75E62CB573145B75E8964274A595F8F2A7E7E666C555E493F7A581F7DDEE530B14CBC4298E6563FA733522CFFCF2658BC723D
	7ED2337F5F247E4C6C7B1B54A7AD7EF533D8820F2FBD967D71753C45F7FC3DE0519FDF07AC6A7115664921BDF89FA36A85BC8F3CEB8F1E8746F25CE0B55E47D842F12F6DC25E42FFC21C3186F9CBB86E0DCD485B45F1F174BC9A5E43B82E145F970964B88F3DD7826F599C77D79B7296E5A0EE8C3F27D24AF19E8B72BAB2F01DEC5EC87A86B6264AD3850AAF157787999E278A6CCEFA529ADA66301319247C3F0E703C6E36AA6D48237ABB5B727D372935566FDF46821BE2536B2A3FAF583EFF59133FD799B4C7FE
	A07A761D570A3EFDE75FF14576AB2A3F179D7F7E7DD0763D4A20B6FFE0727543C7A6359F68DE44EBF20E53BD7AD9FBF00FBEA4E37467569FB3FCC406769CB5269A77D322B3F05FFC2528483356203C84BF72C63A9F1162A36F634DA8AFBD834F249F31F81FC9D7F5A8723773B369ACBF7255675259C0C3G6DAD1A2FBD1398EF75BDA73F055F5DA91D0FBEE869D4F418464F6A374E57BF3BD61F57B74E27BAFC46F02D7335BE0862FB01367938D775669E5C2B9A60BA7413A595537B9F2BFD4F9DA2EA10A71E73CC
	33ABBC5BB67B725878D9C5567C114EAA9E6CD66AC9347A5673B8BFBBA9FBF124730BC207D24FFDDEE7A7341DAD715A8BE8053A9CAF0EE64FEAEA0D1AAF84F8A2C645E777DA4F44824F4CD17125F92DCB5200E7C1GBE5A1ED75579367162BA5F79E4FC0DAF5D7DB53EF4A1BA7353F1BA7353D7F4663B8B3D663B747FEC59F076EDF809FBDDD8AA896F5363CBC9B09CB98CEF74E8CE92D6565BECE50D8DE564982F3232D2928A7088DEFE4B47C3F3A8D01D71FB5C42AA7EDB078765F5FA66056D35393F3DAD57A064
	9D945E27732A03C29E199C9D94164AF32703C2AE4C2B48FF11A8642941F3DE38A2C7627FB5D69216925754CA428A6629B2FCE77EADA393E5F8DF25393170FDE9A4705F619BD61FD1AF99E775FB0E9F77B24D7B24171533AA3AF718A15FD14E86FC49BF63DEE105197CDFC17E4E8C0E1DB3BB9C125951D36F34117303060B12284C77DFB6137ABDC8438AE9D9163C0668D6717C8FD0CB878892CA2C614AA2GG58F3GGD0CB818294G94G88G88G370171B492CA2C614AA2GG58F3GG8CGGGGGGG
	GGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG84A3GGGG
**end of data**/
}
}