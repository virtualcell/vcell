package cbit.vcell.model.gui;
import cbit.vcell.model.SpeciesContext;
import javax.swing.JOptionPane;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2003 2:07:01 PM)
 * @author: Frank Morgan
 */
public class EditSpeciesDialog extends org.vcell.util.gui.JInternalFrameEnhanced {

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
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
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
public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
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
			ivjJPanel1.setBorder(new org.vcell.util.gui.LineBorderBean());
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
				cbit.vcell.clientdb.DocumentManager argDocumentManager) {
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
public void initEditSpecies(cbit.vcell.model.SpeciesContext argSpeciesContext, cbit.vcell.clientdb.DocumentManager argDocumentManager) {
	
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
		setContentPane(getJInternalFrameEnhancedContentPane());
		initConnections();
		pack();
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
public void setDocumentManager(cbit.vcell.clientdb.DocumentManager documentManager) {
	cbit.vcell.clientdb.DocumentManager oldValue = fieldDocumentManager;
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
	org.vcell.util.BeanUtils.centerOnScreen(aSpeciesQueryDialog);
	org.vcell.util.gui.ZEnforcer.showModalDialogOnTop(aSpeciesQueryDialog,this);
	//aSpeciesQueryDialog.setVisible(true);

	cbit.vcell.dictionary.DBFormalSpecies dbfs = null;
	cbit.vcell.dictionary.DictionaryQueryResults dqr = aSpeciesQueryDialog.getDictionaryQueryResults();
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
			
			contextName = org.vcell.util.TokenMangler.fixTokenStrict(
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
					!org.vcell.util.Compare.isEqualOrNull(getNameValueJTextField().getText(),getSpeciesContext().getSpecies().getCommonName()) ||
					!org.vcell.util.Compare.isEqualOrNull(getSpeciesContext().getSpecies().getDBSpecies(),getDBFormalSpecies()) ||
					!org.vcell.util.Compare.isEqualOrNull(getSpeciesContext().getSpecies().getAnnotation(),getAnnotationString()) ||
					getSpeciesContext().getHasOverride() != getJCheckBoxHasOverride().isSelected() ||
					!org.vcell.util.Compare.isEqualOrNull(getSpeciesContext().getName(),getContextNameValueTextField().getText())
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
	D0CB838494G88G88GA7FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BD8DD8D45715B4CB927AADCDEC169A1B3AD9571A14241A5206F45DDD5AF53BECEB93D269AE31F6C3DA5B380D5DF593EAE8E2B6A4F98CA0207C99D131C1C3920C2008202808287C8C8A88CA1068E000B0B00F1901E1E61C998495F44FFD771E7B5E4CFB43A07B657BF67D3E73BD5E39675C73FB7F6F9B057C779724A56B7285A1B9CDE07F0EA58BC204DB909E287B559E44892F2C4894947FBEG58A25C
	B3148A4FBCA8DBE50C4D4C933EF4B6835E0B61BD7673584C9D7077D6611E338B1C70079FB993D0F64FAF8A9F1BDE4E3777211CF4695DB112854F9C0092401127871EF9423DA3858C5F46708849BA40A5901A05A3058C371468891081784284CADF85CF2A405DB5550C2E672B61C26253AEDDB629C7D1A7C29863DD4A1EC542FFA7777F0148FAC6521370A4C0D989600871A9C2F1CB99BCEBBBC64C2786872C1E72EB03224DD6BE6AB00936F2731835262649E475DCF40A03D6512D379AED8EF33CD45FD6DB0644AF
	880B605D321E609E359099C570F7B5C047FA442F34917CE278BB9AA0FEBD5AF5F2EFAD535BF073EF0438C3232939FE6CCADCCF6DA9FCD5364BE9FFC137D30A41FF6EA73EB6C059A4409C00C800B800C4007724582F5F378F1E75831EC647682843DE6FF4B658CC2EF6E2A77CDDD383A58C57A98EBADCA6C120717D79C81906BE5381657B759D73D11F049F46D8AF6EFCC208FFF22EAEDD43A77161B7F5D9095497450932CF286C821BBF597AC46FDC7416FDD9D04A0E1C3BB2A5CDC3F66C1F2DCCB130FAEC8A5911
	09A4567B2CA4562170B761A37124789A893FC10127F939D84A9972DE74479519737F0839E148C1DD5AD2978AF9158FEA7AE15E0FC2F5852FD19DAADE7355E540CE6A6EF90D4ABC41F5891170718ABC55A5DE4AD3725E73BA64709BABB2E9FEA59EA06DB47F4D951995G4DG7DGC1G7381A25E449CBB317F608CF32C436872B40CD9EDA651A5D05E447D5B61A9CEF8DC46963B5BE3348F0A170CAE2BF140A6C27FC7E95E5ACFFCB0604527ABA339BF88B24E0AF651E574D89DF6285DB8AAFAC4171B52ECAE5BC2
	E8DC22D45AE59D95C140A8698AE97956322DA416C63767D327496891E58ADA7EDE990949B563A52891AA003F29DFBEB8C87C958B762F8558407DB0D72A6F1C68320B361A1AB607E3E44C59C5AC911A13081F8FCAFDC7967CDD1C04BE5ED5C1F021703EA8896BF9366CDDC5BDFA6BA0D15F683A0EED2C42CA62329468D7812C83C8FDEBC5E691C0AD0061AD4C25178D18CB7A6CA5C259F3DF534CA5FD704A9467DBB407C25E72EDD3F512AC22EF84144582A4G643D8DB9815071B60E875F33B88334F343DB5AED10
	G5A01B690DC2A8FBD31D73B4F79F9C0593953142531FAB683E4AB7A85A5CD0A869F0D5F1FD712DCF0029FC281963F0DB14CB8D0C45BC40351ADE2AB3860028496CDF2C4297E963B0749A679338D60A440C500CF819A8552C35176F98460B840DE00D2007DG27C97B81A093052FG1036F89360GC0A5C08B40B112CFG6DG97GBE87B0F27F5DA7490F865D5781643F83B1GB0810482C4824483248A3119E5GDDG13G0BGE2G92818A13E3B375GB600B000A800B5GD93A584CAA009E00E00045GF1G
	C9G45A931ACF7FEEB0BCD769F1FEC455F681F69E23D4747BF8718CFAAD97991660F634C27A7198FF3182F311E969F1F65A8E2F259C7A67AB30745E226E35F4D9950FC658E6A7B3F84B4365D235345D6592E309DCED767C782EDF7240D6DE2715D4BE2CC4A3731B853F9AFED476F8B6ABE006443E1C14E850243763F45C7A66689797B03BB303FF486B4FB7E8F44E9B6C07B38378F901F0A57C43BC7AFBAC53B897EF0333234F262FBB62E8FB9B84FE6896F04F9C0A38C3CEE0E5FD2CE7CEE74F8DC5601B10FF8D1
	74F82CF633DC3E331C48F20F8DB865A9844AAAAAA739639807F9G275FD3CE4650C1075DAECA1DADE7E165FB25B1DC34092320F423E34C6E2178838749BAE7C43C4E66901587B78AE48EE09BA373913AF608B012DCF13A2C23500513A90D30B8052C3D4E588A94BEEA839CD9838541B38C000E49639F6118DCFC63B1E15967CE1D56DCF35EA3776B9201077C1D119A4B47E42A5307955A3A17C8F38906F7D3DD8725B910EAB1A3503637D51898A4BCEEDAD6C9F1E1BDC4D713E3F00CD4D449E9235CA4867A06B38E
	572851466AE3E5EBBF4D03E73D5D6E70C8332D0B90C83B191695FE10AB4BE8F458BD1096150CEFD51FD256A181475568C3444692303D96208B005A38280214635C088D280D8E1B0D06994AA2D2C9AC3EEC4DE73E3441FBF0AA0DC58EAFFF0717EFA06FE93E658B07313C884A2AD56585A3D86E043290D579FD3CFCB114C52B4A8F5B30BC8E4A56A94A291DDF2DA47EF40BD747C4189213C964B91E1373AA5F75A93B44721316AF28A479E9D34CBF8D9B094C676D386ECE82F979002B82A0F33AE6F86E86FCC7BA21
	FB4401F40BG9F85903F116AD718CEF31152D91DC8B79F5EA353E53AB8AF3ADDD711AEB583668A99B2DDED86255B10C168AE0FA31D935EC3G428148FCF32D97DD65845269B3E325F9E9CFA625A3CF494719046ECDCEE703F7B21789E6747360D98EF84BA6C27754F56E97401B989D51AFFF13520E4BB710AEF293159733095245FB51655E6475117A5FD653D1BB7E095365EF26FA91BA9217324DCABB3EB209F4351B697C8B6D685ACC6D20751D1D6276AA6AC379F2FD593710AEF8B31D47E1FD73D9FD540E1FF0
	3A484DB4CFE2B6EB597B255BC837864A13G8A33A8DDCF9625237957A4A0DDB8ABDF86CF320E084FD266DFEEB2522DE3F42959DA7EFB1A5395E553FA2A61598CE049D652FDDD07F4130CAEAC1B5A9B162D744BE08A4F7BECBA07A5FEA174D15954AF142EB09569E23369FC976956E6AB6331125389B9D42F82F89AGA6F214F23F1B06F421B9F43ECC6AC3BB64F8089B39FE2C3E289CEF3FD0BF1726737CE37272F3E325359A710F6C67D7B8DD89AB2F4B557273E399C8E748157DCC6AF566AA6DF0E7A2DDD0AE1D
	6323DF6665D2BB6818F441CC66BD725E1B345F565A8263024BEE341DA10B78A63B05EC8518000E6E3BC570FD37F0280B6E2F75CA7B992B61FD83C0DE9E0E31BF4F467D353011F0E14E034F25785BB7AC49237A376549E32C651763A9F48C4A29125666D0B6BF8F471F778EA9577875EE37E350AA8DF82C6FFD7A10F28C26389507B641F3486AF2FBDAE012B4017D74F307083EE47F012159FECC0434E7C4EC0DG39EB81285FDC453B85BFFB16B5B5B89C33BD2D41436803B9B79E917A3F7610EE339F9F94ED2136
	57ED11FDD0FD79C88A556563E154E5824AA3G56EE115A4AB019A7117103527DF49877D94B5E05BE74DD54B97C62FE8D1DCDFC85DCD363BB1F20751D667B3673212EB8DE5FF35D5B313EE163B5E3394DE8B7037152EC837D7754886A1C35955625DBD16775A3C4E75267D03AABB6241B841AC8CE77200D5011B6C369D29DC8378EE80A73215D65933ABD66FC059FFBG978C908EC067B65707C9AE605C06626EF7103ABD962B1B46E9FDFD9D630FF9E22130B4BDD4273597EC7956BCDD6AB69A1F32EDF21CBA563C
	20B381D0BDFFA669C97CB489B4115BD04F3F6AD56A398E70795BE1CE339D75BC62A473AE239412F27A6890DDC3909551D4A2F959B86EA91766796575D2F193793B945BF3D8A6E94F0324415ACA5A7CB31CA83DA4E0FEAE5C2A343B76F61F265DA13996DD5BF6EA2FE73BEC37693997F4934F215D8FB8516EE5C033E1BB5A5DEF56323BE28766529F4A49FA4EF1AE304ED4CE4CB5142307BAA6F7201C39D33EF2A221EC951753D7C67CB3E637D96DA3B31595B44EF38E6AA9AEC0D97FE1501255D30031FCE9FC3631
	247C6FCDF86767125D4AB8E5986F560CD3686F4DBAE7810D4F3C82B9CEB1902355BCCED7A6502655C0D33093F57EC7AE135295DFC73A8E20895909FE4CBFC51E52DACCC0FD7F72BA51D35F403BBF16DF135D28F7D0CC24D03075AB1AFA93D836136A3BF6272C773C97DF50ADFE91757E55F50CFB751FE04C7B936A9371F193BC4D2247278BBB63F20C52E5D6C9E91BE62FD76E43F1026B7D66AE543BD83FC828CE9C712BF7781F283E31FF1275DE7A3BA3A9B13F433EBB68866ADD749E0C576F719C39017E26F6F4
	E7137544183487AE0D42C346C13104A4A32D277AA656B3876AD87E9E760B6BEEE21C04C268CF8A317E71C929FED865D13A6FCFA21D93E842B95D41A9B2AE8ECA078667E150C7F94EA91EA7C0DB308B659D3D457382F0A13B301E6F5CA27598795A936BC938457B85205D4069AF5FD62EB94BF91BFB7CB652D76C06B946EEECF393965FB6C77A79853B313EAD427F2E7FFC2C7D142C3F585AB06671B86C1F577BD494286320B6588574D1A468370E6B18E1A33AFB2F41318D3DA1F8373F6AE474C3717B50373C0FF5
	7CD5F22E42EF6DC63B51AC3A185CD713D1EE985047F01EABBA2C1B7A72F92FBEB935087A322C88696F52C97B91A2D94F0BA6DA770FF4D8F79D50CDF05A5294EF3DC3D3783C89E856F23A3454F459F626EFD3AA5296FDGED6383247DE1AA19A7D90C6E8B57C4174BEA92997DBB293C8DG6DF2CEBF14C6E2BF8833D533D86EA671EB147E26011FA8F70F434404C50C16E06C29BF4ED93466904207D87FC3E9C4F751CE1671A6264B3F25216E79C05776A1360F7BB6220E41000B62F57CF163EC7313565B190E7292
	214E320F305EA5B6657C065296E4208EA6201B4FE9EFE7E83739FF49403A6300B62B9869FBB33D7B107919C827871AC9CE37ED932133C36FC1771B1F72FB86AC2DE30BE3D5E7C874EC746752DAE2B914A7E37B11704FCB78D58ABC55E7500E7A2C053236BD316CBC2F729859830C00779800B5GC9G059F47E656FE0CEB085E43E46FF95A73D2DC8CDD745A7044F34BAAA2F7C43C4E5EF7D4A947C56A076FDB099F8C9F2B6D758714AFDA62F35E819F35FBEEB54963854097859087308EA0EBAF5ABC3F2AAC104D
	F86E4B0C257A148D4F4E0E1A61BB35030E3F25D714F7CED237346A082F35465F323D346E5A3D7278CBE52F981E5D5D0997E64957B44B3B9A97E7497777A3330B49CFC76EB4A6142F5FB6BBF9F65B6C64EDF284724B64DE6FBA597D1300FC8BFC7868783F27046C1B70B5AD9BB4EAEADA3B207BBBE395EDA6FA3E702EC3F93E30946AA267727170CCG207BA4BD668CD6EFC6896E07CC1DD16E07C465CE795D8B28AB217A751748B99D62FE1D6D07845790BD97C0D994C09CC082C0FEA92C29G3ACA312D7FC7D503
	9068BE889E74F4B9781F0D3E07B56857B815DFED255EFE540E47EFD5FC3BFD5EC3CA356211FBF127F6BC684C8C4AE8BC163BFC63A14D8B214E48D20C474ADE0CC7CCA90E91EF1CA0FECC5D971BD984D08D5085B081908630F49F7A71641113017CA88D54DD0EC640B5BB9CA39AAEE43E18671C1D8F9FD571797AF055BEEFBEEA7703D29E12BA927661B97F3FDC1B1D8E2BD5FC3EBA146C77662371F929CDABBEF5C05B359F63736C946AE95B0FFA5EB3BE3B769F9610EF51FEADDFFD500DBAC46D479CF11EA4B912
	886FF9E53119A5GFAG8BC0B0C0F8996648FDC7CE4CB0C7684CCE1DA12C0F1D181D4D7600FC4B4B347876DD1F1D3C6A00FC2BFD64D1FFA61CA27E5CGE57987E08E8B508CE0838881D8FCG7D397AC8403E0BDFB31BC95F35C6256F4CF2FE3D0A2F5C67BD7A00161D76BAE2E7BC14EDG48AB87DB81BAGECGF34A514E2A2375F7E0E7607E651E9B3333B3CC4567EBE7C439965F291B3313572662731537DCD35EFA955F4C72D5188C182F1A72228272E94BFBBAA05F86CDF95726E6A7AFB8A0DF5EC1AD3E5ADB
	3313579A102FC4D35EEBAA3E1965CB6A6DC079D227A9AFC245B7B3F9BFD471790EF50603DAE348F7ACB806B88F62B86691E667633B827205EB5AFCAAF916EDD245676B63E51A725ED0714D4C3EF4DDA03ED5BE72683C7054A552F7EA4C8B9BAD6260C803E3024562DD8B4E7992F8AC168714292D6B4A00FCA595DAFC8F244CCE5E0200FCF51A724E254ECEDEF7C0BE03263C343459655AD6951FEF2EB9AB345A57DD3CFD05D4E0CCD72B6A1A194DAF8764DB26E9F3584659593CD04567EB73AACDF906745959B794
	10AFC1D35E6E0C5959D726627335AF23D20BEF150AEFE6767DAEA0DF11263C051933139791102F5AC79E7D5EA0C65A1F6D00B2CBA5666CAF6CE43FB4143F7FC6FAA76BA27AEE359B82487205D09F5953819BDF6ED806567DFBFFC06737B5477C5FCD2DBF26FDB7756CB16D3B296D4736D0BC54217C02854BBFBDB67D5D555EE3DAF7D7F7C9F62D144EB6DCE3F60FF5D414445231C61CA0E753B08B6F9C3353A9BA116C765DAB65B29E2E76EF6FA3555A76FE3BDA5B5E6FD6CFEF6F5FD67B33172EC11E28A7B995D5
	09EB0D0E63078475DA035CC9F5385020AE51683AB0B44497D464E0D4E0BEFA49C36EF52B6E7252A3C3F606D56F48E4B49D9EC78759DF27FEFC35146C81E80C599DC63BE823FDFB2B83476BD515F837E847D592173CC330768628BB046D43E81E2EAD9A004EF6C8EBDD76D283599F8F023279G11GB1G6B8E717D7163814F841AA626B993382F46FF7C3FD6239D7FEF5410FBD1EE4718EBD0740D71421A82DEA62F7ADABC62A8F1BB4B9F095FE3F419C5D50EFCCF6227E57E787F3E066CC38ED8CDD6973B3DCB63
	7D536D39D27E395D22DB5A3C509BBDC696674DCE0CF3134751C86EA653988AEE9C1F338EE38C9F9BA2B15443FB8F0067B0467079DE2D98069E26F1DBF0D85D1FD1FDD3EA7D7BF8D32D360F73EA7D7BF8C7EDE09F97557A77715E5A40BE2E287557D629BFD7F879731000FD74FC4DBC0FE43ED98E4FF89FBF517AB2471575D571783C5F4E6744BCBE078D529D6FAAE8DFG06AA3ECF283926F4D6D1F941701CD725A57B87CA596DD55C160F3B356ADB4C6AD8864FB81F7A286FEE1C749F6FAF1D540E773DA77D477B
	3E1301637D60C97F71FE78E460F8FF6B647471FEF0C26923A39CEF3C3E1961BB06DC0E510ECAA6733FCF7977C172E9ED9F241F766F031C5301FD306334FF9F6CBE9D5887FBCF7B76AB6CEE538DAF9BA15BA97DC41DFF9BEF57E95BF8F71DFF9BC36B825B7835BA7FB64E2F8BEC63A3F553477987B7146D7098470F5DD4628FF3FC6924B2AF8EF17C2B135EF9F1086117CCA96965FA5C373C698FB37C015BCA39C7B97DCBDE78633C4DC6DA345A6CDA56CE136019F7C42B8F7836201C87485F2E0CA5AB65D4F3FC05
	CEE947B1AEFF07267CE2A6337A08FA0C20753D63D51FEC67CFD33C7DF204616FCA75469FE5785AB4E5BD32DF6E55542B0D69538FCF0F26DFF6EC7416F30C611F5938C961174A8A243FBFDD69C7B99FBE4D70222FC4FCEE26D2DF596F3F4C7416DB4566E55F9DA778A117F19452D61A6C959EA5E7B6AF72EF28C2400E79GD1G71GC9C7704E471A8CB2B7563EB0AEDDEDA253D63BBCB77EDEFD36322D30F9A375678555992169286C3F09236AB853FBA1E3AEB2F6059E2577C236F1FC0804DF2640537BA23B2479
	A4F9CF04328547E2B3699AED420CEB3436EA44FDA06DB54C077708EA1C9F3E52603F0FFA2DC13B0FCAEA501E5F6B9A271F5FEFEA745737303D973752E75120632AEA59CF6B2A7D79EC171978C0B8CEFD134671A592BEDF0127BE7B4E905EA5AA01325063681F755219EA823C8BB5083B5807FE4C6238A74DC85743F185963EA6663897BEC3FFAF2D41B9C8F13BEFDE445448F62D2E71E7633724B9CDE20D2F0D5F137099A7FCEDBCEEC09B8B21AC68846A747BA954FD8D47355EC25D93B8AEE58CF52F2BC55C49EE
	646D6238073B11371F63CECF71BC6338E7A47FC443FB9C47D5D811F7B54765C83E2DB8991B29BF0938F20957G6F960E7B8B6EEF8F47BDECC17D22B96E9DCE974BF1BF9BC27D0ACE61F9ED66A841919FD6814EF60A749947C615F768C89F71E749F48F0816DB3D4AC9EEFF6ED57E8D2FF252E612D314657B6D4AF2922BDF8B341C5E2FF8E68F5E2FC8FAA6C230BD956C77F7EAA2CF513CD9451EF356FCBDF9717E1BA9545EA1E99D1AF7BAB63378B47A605F1D68176653181BB7DDA5C4B7F88F828883D88C908310
	8810DF971BD981508CE02AC31E7BA49EF27F1D5C978F867C22BA76ED50C67A4DD6B43C2F29235F95D11E0F2EAE503001G11CBE478DB87939D08DE4D8A9A937B1B68486760CCEFFF75A84166070E6AFA5E2F2EE9027A1BEEA56432BA36AAF0789B834A5F9EA0315EBC83BDB1873570ED75A41F6EB76B59FBEA3D76BCA21969C47EAEG1AB200ED4C163AFA5A0760FD442DC262E74ABB2261120CD68B361330FA4C25C2E90D3C8E5E058644FD4343F30E6336DD63B95740F3C1EA6F7360BD02634AA59C796E13FCA3
	4A6EDF8E4DF4EF0F54C51E918DF73AFF7833994B40B8244D109EF9368ACF1B717EE98C6887A3E32B4063B75C07714BEBD46B1E466AC0A8D3440F442338D10E9F2D7B54B9396E0396FDG5D262B3BC2D1B74D032B3D1887350D985F936D98738E0EFB6ABC6F279BB116CB8C52D8G6F2BB9EE29048BE15F71D25CE903EFBC22B57C0230FA1AB27FB1B728E4CC37A7FCA7714E61F1B03649F1B05E42B8A4E9680BED54DF9C72743EF1A89B43B894C2991577376D323C273AD1DE05DEADAFB900BC3DCA1EE58A65F568
	F15E7CDB0BF7CE7F7E3CAC7F2C95651BB464277B6475845090C07921FA5FBEE90FD95DA7551AD127F9DC27A69F1DAABBE41D9E67EDE1319F1D14EDE19950ACD368942777EDE7DD1F48F5EF67762E56289B6BC41B93755EFDF0C6132F3FDF9FC25B721B30DD640C5E697DDABA1F7847F41CCFC47E5D634252FB74FE6FEB16B4D15D1B593364452FA713A73BC356C956D293D096863094A096E08DC0D2932EAB9EEEE4773249F6FC3D47E39C34C8DF2E7203862BB8AE2349C20D46ED4AC1FE9743E63C6E9873884264
	996203B0FEBFB58F5ECBGBAGCCE7683D3BE809A649D9B00D7D346EC7F6907F519DFA7C1D083B33E59C6EC1941FA3EBD1321CEBEDB38E08B6646FE9A263BD7B48C5D94C4ADF285D69DDAE7DFC823FEC4B698A4737327AA58279BAAEAB7F49D5B2F728777EEE87575B1709EEEDD67B0837EE1B255F7AA3F8DAA7AD24E55F126E50735B844D72C7A874DB11ABE34AF9E856D96A570A33781D526DD12DF2BDAF9FB6EB159BF879FA0BD6396DAC4E3D17713B45C100FBDF20F3268C817FA51CD663306D8DF689BC3FFF
	130579DD786DEF8A7ADF68F59F79496F25E7E9DEE7B44B771133BCC5A9EC4F52C2F499B25A5CA22FFB4138727EFE75263BB4FFDF05C09D2B330BBD1336BD14CC6BBD6CA639BD60F0C90BE21A5B2DEE4C5BD020DF84908B302619FED3CAE922BDC81354CC73BF3F851EAD140676BBE557305F29EB516E67B39990DD3A004652C27B9D72BE59625B4FF6DFA234974E49F71359B73D25E46E5AA8FD13AB17D19F2D78CC69233C07ECBA2C53D71F9026E3D80B6CFF1A8B5F6843DCD85E0263D977A5B95E8F101F48E23A
	0474AA63C4FDE5EAC7DF892D54D78570ACEB256B936A2B187368ABFD2B362F70B723089C835058DA657EBF285557D7319344A77A8629D57A786B315B7437EDBE153E5245D2EA6B8337505650D63475A903ECDF631852BE4A733BEE640966BC2B3AE51E5BD3EA1E89AB72A435EA35AF74ED50E7F2BD974D6AFA1E67770F2A4ECDD74FB7FB844EF3164B36E972A41C26BCBF6E15E567F849E65F817511BEE49154318CE08D4086002CB69CEF66FE7221A00DB79AE3CD1349CC764B492FC0FAF0AF6AFA277FFD2F204E
	403F9D7965CE528F8FB8A01C2324FE445F5FC9724663F0CAC80A8B6BA46D43E5B5DBBC1414619F6AA4FE3009C3921A6E5BC4C95F5A135CAA815B7A5B701B604AFE2FEFB94817F5B5B5E7218FEFB01A5B2451127ABB554967C2EDF89EF6C87A0EBD965E5782886D787D46444758DFAEFEF421907C975A3FE7313AEBCAD750CE5BC9ED3B5CDE036DAF68E8BD43A34AFE1714795B2718E07C739575603CBAC5F8F5484F193AB418D17B3EBA066731716DB8FEFCFF94F179679177577CBB01B60EAB35A2AE78BC4E257E
	596BBCB28A70316727DFC750FC7CF2BF69C356G2DF0815638G35G9DG9600C900B9G0BG22G62G5682A48164F544E69681D481B4F7E07E06F7115FF53BE49CD65FE2GA48B341444AE23D57ADDC52A47ABC6E2D38F54B3416BFA25EB7FB4F5F5389CCE516539CE3152EC016D993A5C786D61BC28AB3683F3EFEE67AEC1753BAA5059098D52D056A09A6D3C3D7F1D9B73AF63934C3F35D23DF570EEGF0FE423F7376E02E07GEE59A79827AA3E2659406B9898A334951D60B3007ECE246D3D067D4BC4671D
	4E9D968D8A5C0F1F106999EB17C7BE5D634FFF501B34C2EADE98989687BDDDC633406FB776A973A882F4096E1CBE0F5837456D6803D5C01FF7916D0AEBC79FB4DFC49F684E935AA0F81F879041E95FBA4F474C0B5A7D32FF9FFC17F4C74C06873D6EB28BDDB03F681A5E864A771FDE6760A540D3BBA33EA92B122F83F8CCB362DB6DF5D6B581BC21B362EB731237GF8A283703135CC5FB4F7207A820F95CFC8F39A917A782E6BCEF64E7AE49F198BBA06065CA29F13167665F1DC0B3B4361361273D92A7B0F82FE
	8328EDF36CAC79B2D55FE78582FA9E7128B456AC6F1267C8947F9D899F2740335F84F0E27E2F06B27D27781DD218B4960443FBB4C0BCC0A2C07EA5D817DC42FE2E7D738AA1405D26AE07338D82E7D39CC7517ADFB6137A75D0D7BFC0B040FC008800653C7E87BFA7674F7E7A51CE07C3BA8EA46BBD6812C51722F726BE1CBB26654378CBB467926019FAD96BDE6617B4FDDF40E84B60D927497728665D1AAEC6EB016724A6DF39D50BEF8E23DD844F25BEFCF4EFAFDE9AE3E3AE5318D25CEFA45C9A0623FE7C4BFE
	7F6DE7D1FF60763324DFFB2E75E43FFA2E3534DFFB2E7543FE65DC0B627E291F7419E698DB8D1CEEB947F5F35C33D21D84F759EA72D8B87EDFA53D883ED9A482917FCBC92FF189E7E038DFA9F05D8C77E2BF698340E40B43053CEB7B493E551055E6236F6BA49A2B9D3A846E0FBFC8BADA1DC6055E2FF11C2CF72211440A1F3752211E153DE94C176A05AE46E8335ED07C36A64D0989F72076AF9834F2691E00FC3E5F1DD03EFF8848275D5F74C8F308C203EFFFA3CA789203EFFF73329B7B1BAAA81BE740F960FA
	4F6C7AC9C1330D8766BBFE47FC542E1552BCA8C9E56F2FA5FCC6372F3D16B134B79F4AA63A515E812FEFDF63811F50BD7DD8C85B775F74295BFCD41F324D336FC4D4675CB3734B02D9727D720E79285F2EC9F32A54AB3E7E3CAD61732E787AB363BC7A33904A022E20BFF597147257GFE431519787309DBEAFFBEF9CB69CF0A53DDD15325DFD15305D955F4DF372A69DE37286912ACEA3AC10D7AACD6F53C97131F4AE7FFBF25787B1473CE6F645FF96CAFFF21B25898B6B44D98B624FDCF6671A16DFBB291C353
	5F6BDFBA647FB7297FE1C87D1B542F1B7D6B7636B970781AEE519EDF33AD6A7175DD0B7678CA6F3F6078CA73E449985E47487A0C5F6F60E740859CD7B20A77B1AC9CB74F093C139C77863F339042F109E664DD45F1BF10F06B60BD0963BE6477C0B2FA90B7175EAB01F79B47B5D911B708639E6577D4C2B9AE580C3C71BD38CEB6FECC7A86B626CAD3850AAF147687999E278A6CCE7A322D344CE6A5B3C969FF9D61F9BD60115B5108626FC477975F2632BDDF3C0C78B15926FFD77CED9F7B62E55F7B054A20B9F2
	7D1A3A6F3CF5CD5DF7CE1E17ED3FDB71F74E79AF5E8FFB3FD09954661D9754FE58FDC169873A97F12F1558CE77687770730C041E191FDB3F4770293D3E67281F707BDDF93D38EF7E7830ACAF6FAA4AAB6E5516B75D7D08EA153CDD7C1E0C3E974F2463FC6EFEBCBBA24BBF4E4F248DFE64AB4F25EDC0C3G6D8D6A75BD13D8E2D11F13C7F31DC2398F92EC328EBF60E7578BB4F4C8913C4F6BA3198E9FB15CF2158F1670F37938DE5C2B3A44776B56F29D365A852E37E5D4FD6ED0FF95F90A7A106728C366293228
	F90679D90521EF3A330AEE27DC4F739A753C4C4FAF66E85603679761D765FA223D4EAFE8BB7B4CEB3E3D9C6A0A6F1B491E55192BCA3E8460C97DFCA6FC8E2FBD328260A91B915F2BDEFB81F5405395000F36E717C6DFB7FECDBD1F6C3D2A269B382A263BC7E3FE7A679A7353CD43EA3A5CE1A59D7DBFDBFEECFA83DE967F17106691B65263CB8FCC5A4F419BBD1A7388B975A6D3113E21089C63151717FB044DF884AF3DE56021B994A84E785DAE10073FF558A22D53D7DFBBDC137262B7D3F4C2EA0B30114E2BDA
	04D4B2B9EA91522439D30B108273AA72DFA4CA3F114941FDD538A5C5627FB556A324915754F804EC6629A2FCE77EADA293E5F84F75393170C5E9A4705F613B2DBC235EF077205FF37C42811A77958372D9955DBB1C127AB59B60C386F02F7059A172FF8579BBB338B8EA3459BCC65BC83D5DC44E8F9A2EFBC4F93EFF1719546FC69AD6484E994DC46FE1854F7FGD0CB878885C84D7644A2GG58F3GGD0CB818294G94G88G88GA7FBB0B685C84D7644A2GG58F3GG8CGGGGGGGGGGGGG
	GGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7EA2GGGG
**end of data**/
}
}