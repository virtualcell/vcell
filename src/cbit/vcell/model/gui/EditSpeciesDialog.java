package cbit.vcell.model.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.TokenMangler;
import org.vcell.util.gui.LineBorderBean;
import org.vcell.util.gui.ZEnforcer;

import cbit.vcell.model.Model;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.dictionary.DBFormalSpecies;
import cbit.vcell.dictionary.DictionaryQueryResults;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2003 2:07:01 PM)
 * @author: Frank Morgan
 */
public class EditSpeciesDialog extends JDialog {

	//
	//
	private int mode = ADD_SPECIES_MODE;
	private static final int ADD_SPECIES_MODE = 0;
	private static final int EDIT_SPECIES_MODE = 1;
	//
	//
	private SpeciesContext fieldSpeciesContext = null;
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
	private Species ivjspecies1 = null;
	private boolean ivjConnPtoP3Aligning = false;
	private javax.swing.text.Document ivjdocument1 = null;
	private DBFormalSpecies ivjDBFormalSpecies = null;
	private javax.swing.JButton ivjAnnotateJButton = null;
	private String ivjAnnotationString = null;
	private javax.swing.JLabel ivjLinkJLabel = null;
	private javax.swing.JLabel ivjLinkValueJLabel = null;
	private boolean ivjConnPtoP1Aligning = false;
	private SpeciesContext ivjspeciesContext1 = null;
	private DocumentManager fieldDocumentManager = null;
	private javax.swing.JCheckBox ivjJCheckBoxHasOverride = null;
	private javax.swing.JTextField ivjContextNameValueTextField = null;
	private boolean ivjConnPtoP2Aligning = false;
	private javax.swing.text.Document ivjdocument2 = null;
	private Model fieldModel = null;

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
public EditSpeciesDialog(JFrame parent) {
	super(parent);
	setModal(true);
	initialize();
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
private void connEtoC7(DBFormalSpecies value) {
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
private void connEtoM1(SpeciesContext value) {
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
private void connEtoM2(Species value) {
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
private void connEtoM3(Species value) {
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
private void connEtoM5(Species value) {
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
private void connEtoM6(SpeciesContext value) {
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
private void connEtoM7(SpeciesContext value) {
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
private DBFormalSpecies getDBFormalSpecies() {
	// user code begin {1}
	// user code end
	return ivjDBFormalSpecies;
}


/**
 * Comment
 */
private DBFormalSpecies getDBFormalSpeciesFromDialog() {
	DBFormalSpecies dbfs = showDatabaseBindingDialog();
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
public DocumentManager getDocumentManager() {
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
			ivjJPanel1.setBorder(new LineBorderBean());
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
public Model getModel() {
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
private Species getspecies1() {
	// user code begin {1}
	// user code end
	return ivjspecies1;
}

/**
 * Gets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @return The speciesContext property value.
 * @see #setSpeciesContext
 */
public SpeciesContext getSpeciesContext() {
	return fieldSpeciesContext;
}


/**
 * Return the speciesContext1 property value.
 * @return cbit.vcell.model.SpeciesContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SpeciesContext getspeciesContext1() {
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
				Model argModel,
				Structure argStructure,
				DocumentManager argDocumentManager) {
	//
	//
	//
	if(argStructure != null && argModel != null){
		setTitle("Add New Species to Structure "+argStructure.getName());
		getOKJButton().setText("Add");
	}
	mode = ADD_SPECIES_MODE;
	Species newSpecies = new Species(argModel.getFreeSpeciesName(),null);
	SpeciesContext newSpeciesContext = new SpeciesContext(newSpecies,argStructure);
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
public void initEditSpecies(SpeciesContext argSpeciesContext, DocumentManager argDocumentManager) {
	
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
		aEditSpeciesDialog = new EditSpeciesDialog(frame);
		frame.setContentPane(aEditSpeciesDialog);
		frame.setSize(aEditSpeciesDialog.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
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
			Species existingSpecies = getModel().getSpecies(getSpeciesContext().getSpecies().getCommonName());
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
		PopupGenerator.showErrorDialog(this,"Edit Species Error\n"+e.getMessage());
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
private void setDBFormalSpecies(DBFormalSpecies newValue) {
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
public void setDocumentManager(DocumentManager documentManager) {
	DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange("documentManager", oldValue, documentManager);
}


/**
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(Model model) {
	Model oldValue = fieldModel;
	fieldModel = model;
	firePropertyChange("model", oldValue, model);
}


/**
 * Set the species1 to a new value.
 * @param newValue cbit.vcell.model.Species
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setspecies1(Species newValue) {
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
public void setSpeciesContext(SpeciesContext speciesContext) {
	SpeciesContext oldValue = fieldSpeciesContext;
	fieldSpeciesContext = speciesContext;
	firePropertyChange("speciesContext", oldValue, speciesContext);
}


/**
 * Set the speciesContext1 to a new value.
 * @param newValue cbit.vcell.model.SpeciesContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setspeciesContext1(SpeciesContext newValue) {
	if (ivjspeciesContext1 != newValue) {
		try {
			SpeciesContext oldValue = getspeciesContext1();
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
private DBFormalSpecies showDatabaseBindingDialog() {

	SpeciesQueryDialog aSpeciesQueryDialog = new SpeciesQueryDialog((java.awt.Frame)null,true);
	aSpeciesQueryDialog.setDocumentManager(getDocumentManager());
	aSpeciesQueryDialog.setSize(500,500);
	BeanUtils.centerOnScreen(aSpeciesQueryDialog);
	ZEnforcer.showModalDialogOnTop(aSpeciesQueryDialog,this);
	//aSpeciesQueryDialog.setVisible(true);

	DBFormalSpecies dbfs = null;
	DictionaryQueryResults dqr = aSpeciesQueryDialog.getDictionaryQueryResults();
	if(dqr != null && dqr.getSelection() != null){
		dbfs = dqr.getDBFormalSpecies()[dqr.getSelection()[0]];
	}
	
	return dbfs;
	
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
			
			contextName = TokenMangler.fixTokenStrict(
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
					!Compare.isEqualOrNull(getNameValueJTextField().getText(),getSpeciesContext().getSpecies().getCommonName()) ||
					!Compare.isEqualOrNull(getSpeciesContext().getSpecies().getDBSpecies(),getDBFormalSpecies()) ||
					!Compare.isEqualOrNull(getSpeciesContext().getSpecies().getAnnotation(),getAnnotationString()) ||
					getSpeciesContext().getHasOverride() != getJCheckBoxHasOverride().isSelected() ||
					!Compare.isEqualOrNull(getSpeciesContext().getName(),getContextNameValueTextField().getText())
				)
			);
}

}