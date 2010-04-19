package cbit.vcell.model.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import org.vcell.sybil.gui.pcsearch.test.PCKeywordQueryPanel;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.models.miriam.MIRIAMRef.URNParseFailureException;
import org.vcell.sybil.util.http.pathwaycommons.search.XRef;
import org.vcell.sybil.util.http.uniprot.UniProtConstants;
import org.vcell.sybil.util.miriam.XRefToURN;
import org.vcell.util.Compare;
import org.vcell.util.TokenMangler;
import org.vcell.util.gui.DialogUtils;

import uk.ac.ebi.miriam.lib.MiriamLink;
import cbit.vcell.biomodel.meta.MiriamManager;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.model.Model;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
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
	//getMiriam
	private SpeciesContext fieldSpeciesContext = null;
	private javax.swing.JPanel ivjJContentPane = null;
	private javax.swing.JButton ivjCancelJButton = null;
	private javax.swing.JLabel ivjContextNameJLabel = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJButtonsPanel2 = null;
	private javax.swing.JLabel ivjNameJLabel = null;
	private javax.swing.JTextField ivjNameValueJTextField = null;
	private javax.swing.JButton ivjOKJButton = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private Species ivjspecies1 = null;
	private boolean ivjConnPtoP3Aligning = false;
	private javax.swing.text.Document ivjdocument1 = null;
	private String ivjAnnotationString = null;
	private boolean ivjConnPtoP1Aligning = false;
	private SpeciesContext ivjspeciesContext1 = null;
	private javax.swing.JCheckBox ivjJCheckBoxHasOverride = null;
	private javax.swing.JTextField ivjContextNameValueTextField = null;
	private boolean ivjConnPtoP2Aligning = false;
	private javax.swing.text.Document ivjdocument2 = null;
	private Model fieldModel = null;
	private JTextArea annotationTextArea;
	private JButton pathwayDBJButton = null;
	private JLabel PCLinkJlabel = null;
	private JEditorPane PCLinkValueEditorPane = null;
	
	public void saveSelectedXRef(XRef selectedXRef, MIRIAMQualifier miriamQualifier) {
		String urn = XRefToURN.createURN(selectedXRef.db(), selectedXRef.id());
		try {
			MiriamManager miriamManager = getModel().getVcMetaData().getMiriamManager();
			MiriamResource resource = miriamManager.createMiriamResource(urn);
			Set<MiriamResource> miriamResources = new HashSet<MiriamResource>();
			miriamResources.add(resource);
			miriamManager.addMiriamRefGroup(getSpeciesContext().getSpecies(), miriamQualifier, miriamResources);
			miriamManager.invalidateCache(getSpeciesContext().getSpecies());
		} catch (URNParseFailureException e) {
			e.printStackTrace();
			DialogUtils.showErrorDialog(this, e.getMessage());
		}
		updatePCLink();
	}

	private void updatePCLink() {
		AsynchClientTask task1 = new AsynchClientTask("retrieving metadata", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				String htmlText = null;
				MiriamLink link = new MiriamLink();
				if (!link.isLibraryUpdated()) {
					System.err.println("MirianLink library is not up to date!");
				}
				ArrayList<String> pcLinkStr = getPCLinks();
				if (pcLinkStr != null && pcLinkStr.size() > 0) {
					StringBuffer buffer = new StringBuffer("<html>");
					for(String pcLink : pcLinkStr){
						String preferredName = " "; 
						if (pcLink.toLowerCase().contains("uniprot")) {
							preferredName = "[" + UniProtConstants.getNameFromID(pcLink) + "]";	
						}
						String prettyResourceName = pcLink.replaceFirst("urn:miriam:", "");
						if (pcLink != null && pcLink.length() > 0) {
							buffer.append("&#x95;&nbsp;" + prettyResourceName + "  <b>" + preferredName + "</b><br>");
							String[] locations = link.getLocations(pcLink);
							if (locations!=null){
								for(String url : link.getLocations(pcLink)) {
									buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;-&nbsp;<a href=\"" + url + "\">" + url + "</a><br>");
								}
							}
						}
					}
					buffer.append("</html>");
					htmlText = buffer.toString();
					hashTable.put("htmlText", htmlText);
				}
			}
		};

		AsynchClientTask task2 = new AsynchClientTask("displaying metadata", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				String htmlText = (String)hashTable.get("htmlText");			
				getPCLinkValueEditorPane().setText(htmlText);
				getPCLinkValueEditorPane().setCaretPosition(0);
			};

		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2 });
	}

	private ArrayList<String> getPCLinks() {
		ArrayList<String> pcLinkStrings = new ArrayList<String>();
		MiriamManager miriamManager = getModel().getVcMetaData().getMiriamManager();
		MIRIAMQualifier qualifier = MIRIAMQualifier.BioQualifier.isVersionOf;
		Set<MiriamRefGroup> refGroups = miriamManager.getMiriamRefGroups(getSpeciesContext().getSpecies(),qualifier);
		if (refGroups.size()>0) {
			for (MiriamRefGroup refGroup : refGroups){
				Set<MiriamResource> miriamResources = refGroup.getMiriamRefs();
				for (MiriamResource resource : miriamResources){
					pcLinkStrings.add(resource.getMiriamURN());
				}
			}
		}
		return pcLinkStrings;
	}

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener, javax.swing.event.DocumentListener, HyperlinkListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == EditSpeciesDialog.this.getCancelJButton()) 
				connEtoC1(e);
			if (e.getSource() == EditSpeciesDialog.this.getOKJButton()) 
				connEtoC2(e);
			if (e.getSource() == EditSpeciesDialog.this.getPathwayDBbutton()) 
				showPCKeywordQueryPanel();
			if (e.getSource() == EditSpeciesDialog.this.getJCheckBoxHasOverride()) 
				connEtoC10(e);
		};
		private void onDocumentChanged(DocumentEvent e) {
			if (e.getDocument() == EditSpeciesDialog.this.getdocument1()) 
				updateInterface();
			if (e.getDocument() == EditSpeciesDialog.this.getdocument2()) 
				updateOKButton();
			if (e.getDocument() == EditSpeciesDialog.this.annotationTextArea.getDocument()) 
				updateOKButton();
		}
		public void changedUpdate(javax.swing.event.DocumentEvent e) {
			onDocumentChanged(e);
		};
		public void insertUpdate(javax.swing.event.DocumentEvent e) {
			onDocumentChanged(e);
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
			onDocumentChanged(e);
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == EditSpeciesDialog.this.getJCheckBoxHasOverride()) 
				connEtoM9(e);
		}
		// @Override
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == EventType.ACTIVATED) {
				URL link = e.getURL();
				if (link != null) {
					DialogUtils.browserLauncher(EditSpeciesDialog.this, link.toExternalForm(), "failed to launch", false);
				}
			}
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
private void cancel(java.awt.event.ActionEvent actionEvent) {
	dispose();
}


/**
 * connEtoC1:  (CancelJButton.action.actionPerformed(java.awt.event.ActionEvent) --> EditSpeciesDialog.cancel(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		this.cancel(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC10:  (JCheckBoxHasOverride.action.actionPerformed(java.awt.event.ActionEvent) --> EditSpeciesDialog.updateInterface()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		this.updateOKButton();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (OKJButton.action.actionPerformed(java.awt.event.ActionEvent) --> EditSpeciesDialog.oK(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		this.oK(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (AnnotationString.this --> EditSpeciesDialog.updateInterface()V)
 * @param value java.lang.String
 */
private void connEtoC3(java.lang.String value) {
	try {
		this.updateInterface();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC8:  (EditSpeciesDialog.userDictionaryDbServer --> EditSpeciesDialog.updateInterface()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
private void connEtoC8(java.beans.PropertyChangeEvent arg1) {
	try {
		this.updateInterface();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (speciesContext1.this --> ContextNameValueTextField.text)
 * @param value cbit.vcell.model.SpeciesContext
 */
private void connEtoM1(SpeciesContext value) {
	try {
		if ((getspeciesContext1() != null)) {
			getContextNameValueTextField().setText(getspeciesContext1().getName());
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (species1.this --> NameValueJTextField.text)
 * @param value cbit.vcell.model.Species
 */
private void connEtoM2(Species value) {
	try {
		if ((getspecies1() != null)) {
			getNameValueJTextField().setText(getspecies1().getCommonName());
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoM3:  (species1.this --> AnnotationString.this)
 * @param value cbit.vcell.model.Species
 */
private void connEtoM3(Species value) {
	try {
		if ((getspecies1() != null)) {
			// setAnnotationString(getspecies1().getAnnotation());
			setAnnotationString(getModel().getVcMetaData().getFreeTextAnnotation(getspecies1()));
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoM6:  (speciesContext1.this --> species1.this)
 * @param value cbit.vcell.model.SpeciesContext
 */
private void connEtoM6(SpeciesContext value) {
	try {
		if ((getspeciesContext1() != null)) {
			setspecies1(getspeciesContext1().getSpecies());
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoM7:  (speciesContext1.this --> JCheckBoxHasOverride.selected)
 * @param value cbit.vcell.model.SpeciesContext
 */
private void connEtoM7(SpeciesContext value) {
	try {
		if ((getspeciesContext1() != null)) {
			getJCheckBoxHasOverride().setSelected(getspeciesContext1().getHasOverride());
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoM9:  (JCheckBoxHasOverride.change.stateChanged(javax.swing.event.ChangeEvent) --> ContextNameValueJLabel.enabled)
 * @param arg1 javax.swing.event.ChangeEvent
 */
private void connEtoM9(javax.swing.event.ChangeEvent arg1) {
	try {
		getContextNameValueTextField().setEditable(getJCheckBoxHasOverride().isSelected());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetSource:  (EditSpeciesDialog.speciesContext <--> speciesContext1.this)
 */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			ivjConnPtoP1Aligning = true;
			if ((getspeciesContext1() != null)) {
				this.setSpeciesContext(getspeciesContext1());
			}
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (EditSpeciesDialog.speciesContext <--> speciesContext1.this)
 */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			ivjConnPtoP1Aligning = true;
			setspeciesContext1(this.getSpeciesContext());
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetSource:  (ContextNameValueTextField.document <--> document2.this)
 */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			ivjConnPtoP2Aligning = true;
			if ((getdocument2() != null)) {
				getContextNameValueTextField().setDocument(getdocument2());
			}
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (ContextNameValueTextField.document <--> document2.this)
 */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			ivjConnPtoP2Aligning = true;
			setdocument2(getContextNameValueTextField().getDocument());
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (NameValueJTextField.document <--> document1.this)
 */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			ivjConnPtoP3Aligning = true;
			if ((getdocument1() != null)) {
				getNameValueJTextField().setDocument(getdocument1());
			}
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetTarget:  (NameValueJTextField.document <--> document1.this)
 */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			ivjConnPtoP3Aligning = true;
			setdocument1(getNameValueJTextField().getDocument());
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		handleException(ivjExc);
	}
}

/**
 * Return the AnnotationString property value.
 * @return java.lang.String
 */
private java.lang.String getAnnotationString() {
	return ivjAnnotationString;
}


/**
 * Return the CancelJButton property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getCancelJButton() {
	if (ivjCancelJButton == null) {
		try {
			ivjCancelJButton = new javax.swing.JButton();
			ivjCancelJButton.setName("CancelJButton");
			ivjCancelJButton.setText("Cancel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCancelJButton;
}


/**
 * Return the ContextNameJLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getContextNameJLabel() {
	if (ivjContextNameJLabel == null) {
		try {
			ivjContextNameJLabel = new javax.swing.JLabel();
			ivjContextNameJLabel.setName("ContextNameJLabel");
			ivjContextNameJLabel.setText("Context Name");
			ivjContextNameJLabel.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjContextNameJLabel;
}

/**
 * Return the ContextNameValueJLabel property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getContextNameValueTextField() {
	if (ivjContextNameValueTextField == null) {
		try {
			ivjContextNameValueTextField = new javax.swing.JTextField();
			ivjContextNameValueTextField.setName("ContextNameValueTextField");
			ivjContextNameValueTextField.setText("ContextName");
			ivjContextNameValueTextField.setEditable(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjContextNameValueTextField;
}


/**
 * Return the document1 property value.
 * @return javax.swing.text.Document
 */
private javax.swing.text.Document getdocument1() {
	return ivjdocument1;
}


/**
 * Return the document2 property value.
 * @return javax.swing.text.Document
 */
private javax.swing.text.Document getdocument2() {
	return ivjdocument2;
}


/**
 * Return the JCheckBoxHasOverride property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getJCheckBoxHasOverride() {
	if (ivjJCheckBoxHasOverride == null) {
		try {
			ivjJCheckBoxHasOverride = new javax.swing.JCheckBox();
			ivjJCheckBoxHasOverride.setName("JCheckBoxHasOverride");
			ivjJCheckBoxHasOverride.setText("Override");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxHasOverride;
}


/**
 * Return the JInternalFrameEnhancedContentPane property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJContentPane == null) {
		try {
			ivjJContentPane = new javax.swing.JPanel();
			ivjJContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNameJLabel = new java.awt.GridBagConstraints();
			constraintsNameJLabel.gridx = 0; constraintsNameJLabel.gridy = 0;
			constraintsNameJLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsNameJLabel.insets = new Insets(4, 4, 5, 5);
			getJDialogContentPane().add(getNameJLabel(), constraintsNameJLabel);

			java.awt.GridBagConstraints constraintsNameValueJTextField = new java.awt.GridBagConstraints();
			constraintsNameValueJTextField.gridx = 1; constraintsNameValueJTextField.gridy = 0;
			constraintsNameValueJTextField.gridwidth = 2;
			constraintsNameValueJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
//			constraintsNameValueJTextField.weightx = 1.0;
			constraintsNameValueJTextField.insets = new Insets(4, 4, 5, 5);
			getJDialogContentPane().add(getNameValueJTextField(), constraintsNameValueJTextField);
			
			GridBagConstraints gbc_1 = new GridBagConstraints();
			gbc_1.weighty = 1.0;
			gbc_1.weightx = 1.0;
			gbc_1.gridwidth = 2;
//			gbc_1.weightx = 1.0;
			gbc_1.fill = GridBagConstraints.BOTH;
			gbc_1.anchor = GridBagConstraints.WEST;
			gbc_1.gridx = 1;
			gbc_1.gridy = 4;
			gbc_1.insets = new Insets(0, 4, 0, 5);
			JScrollPane scollPane = new JScrollPane(getPCLinkValueEditorPane());
			ivjJContentPane.add(scollPane, gbc_1);

			java.awt.GridBagConstraints constraintsContextNameJLabel = new java.awt.GridBagConstraints();
			constraintsContextNameJLabel.gridx = 0; constraintsContextNameJLabel.gridy = 1;
			constraintsContextNameJLabel.insets = new Insets(4, 4, 5, 5);
			constraintsContextNameJLabel.anchor = GridBagConstraints.EAST;
			getJDialogContentPane().add(getContextNameJLabel(), constraintsContextNameJLabel);
			
			java.awt.GridBagConstraints constraintsContextNameValueTextField = new java.awt.GridBagConstraints();
			constraintsContextNameValueTextField.gridx = 1; constraintsContextNameValueTextField.gridy = 1;
			constraintsContextNameValueTextField.gridwidth = 2;
			constraintsContextNameValueTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
//			constraintsContextNameValueTextField.weightx = 1.0;
			constraintsContextNameValueTextField.insets = new Insets(4, 4, 5, 5);
			getJDialogContentPane().add(getContextNameValueTextField(), constraintsContextNameValueTextField);

			java.awt.GridBagConstraints constraintsJCheckBoxHasOverride = new java.awt.GridBagConstraints();
			constraintsJCheckBoxHasOverride.gridx = 3; constraintsJCheckBoxHasOverride.gridy = 1;
			constraintsJCheckBoxHasOverride.insets = new Insets(4, 4, 5, 4);
			getJDialogContentPane().add(getJCheckBoxHasOverride(), constraintsJCheckBoxHasOverride);

			java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; gbc.gridy = 2;
			gbc.insets = new Insets(4, 4, 5, 5);
			gbc.anchor = GridBagConstraints.NORTHEAST;
			getJDialogContentPane().add(new JLabel("Annotatation"), gbc);

			annotationTextArea = new javax.swing.JTextArea("", 2, 30);
			annotationTextArea.setLineWrap(true);
			annotationTextArea.setWrapStyleWord(true);
			javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);
			
			gbc = new java.awt.GridBagConstraints();
//			gbc.weighty = 0.8;
			gbc.weightx = 1.0;
			gbc.gridx = 1; gbc.gridy = 2;
			gbc.gridwidth = 2;
			gbc.fill = java.awt.GridBagConstraints.BOTH;
//			gbc.weightx = 1.0;
			gbc.insets = new Insets(4, 4, 5, 5);
			getJDialogContentPane().add(jsp, gbc);
			
			GridBagConstraints gbc_2 = new GridBagConstraints();
			gbc_2.anchor = GridBagConstraints.EAST;
			gbc_2.insets = new Insets(0, 0, 5, 5);
			gbc_2.gridx = 0;
			gbc_2.gridy = 4;
			ivjJContentPane.add(getPCLinkJlabel(), gbc_2);
			
			java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
			constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 5;
			constraintsJPanel2.gridwidth = 4;
			constraintsJPanel2.insets = new Insets(4, 4, 5, 5);
			getJDialogContentPane().add(getJButtonsPanel2(), constraintsJPanel2);

			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; gbc.gridy = 6;
			gbc.gridwidth = 4;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4, 4, 5, 4);
			ivjJContentPane.add(new JSeparator(), gbc);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 7;
			constraintsJPanel1.gridwidth = 4;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			getJDialogContentPane().add(getJPanel1(), constraintsJPanel1);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJContentPane;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
//			ivjJPanel1.setBorder(new LineBorderBean());
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOKJButton = new java.awt.GridBagConstraints();
			constraintsOKJButton.gridx = 0; constraintsOKJButton.gridy = 1;
			constraintsOKJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getOKJButton(), constraintsOKJButton);

			java.awt.GridBagConstraints constraintsCancelJButton = new java.awt.GridBagConstraints();
			constraintsCancelJButton.gridx = 1; constraintsCancelJButton.gridy = 1;
			constraintsCancelJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getCancelJButton(), constraintsCancelJButton);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}

/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJButtonsPanel2() {
	if (ivjJButtonsPanel2 == null) {
		try {
			ivjJButtonsPanel2 = new javax.swing.JPanel();
			ivjJButtonsPanel2.setName("JPanel2");
			ivjJButtonsPanel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			ivjJButtonsPanel2.add(getPathwayDBbutton());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonsPanel2;
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
private javax.swing.JLabel getNameJLabel() {
	if (ivjNameJLabel == null) {
		try {
			ivjNameJLabel = new javax.swing.JLabel();
			ivjNameJLabel.setName("NameJLabel");
			ivjNameJLabel.setText("Name");
			ivjNameJLabel.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNameJLabel;
}

/**
 * Return the NameValueJTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getNameValueJTextField() {
	if (ivjNameValueJTextField == null) {
		try {
			ivjNameValueJTextField = new javax.swing.JTextField();
			ivjNameValueJTextField.setName("NameValueJTextField");
			ivjNameValueJTextField.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNameValueJTextField;
}

/**
 * Return the OKJButton property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getOKJButton() {
	if (ivjOKJButton == null) {
		try {
			ivjOKJButton = new javax.swing.JButton();
			ivjOKJButton.setName("OKJButton");
			ivjOKJButton.setText("OK");
			ivjOKJButton.setPreferredSize(getCancelJButton().getPreferredSize());
			ivjOKJButton.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOKJButton;
}

/**
 * Return the species1 property value.
 * @return cbit.vcell.model.Species
 */
private Species getspecies1() {
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
private SpeciesContext getspeciesContext1() {
	return ivjspeciesContext1;
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
 * Insert the method's description here.
 * Creation date: (5/20/2003 7:55:25 AM)
 * @param argSpeciesContext cbit.vcell.model.SpeciesContext
 * @param argDocumentManager cbit.vcell.clientdb.DocumentManager
 */
public void initAddSpecies(Model argModel, Structure argStructure) {
	if(argStructure != null && argModel != null){
		setTitle("Add New Species to Structure "+argStructure.getName());
		getOKJButton().setText("Add");
	}
	mode = ADD_SPECIES_MODE;
	setModel(argModel);
	Species newSpecies = new Species(argModel.getFreeSpeciesName(),null);
	SpeciesContext newSpeciesContext = new SpeciesContext(newSpecies,argStructure);
	setSpeciesContext(newSpeciesContext);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
	getNameValueJTextField().addPropertyChangeListener(ivjEventHandler);
	getCancelJButton().addActionListener(ivjEventHandler);
	getOKJButton().addActionListener(ivjEventHandler);
	getPathwayDBbutton().addActionListener(ivjEventHandler);
	getPCLinkValueEditorPane().addHyperlinkListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getJCheckBoxHasOverride().addChangeListener(ivjEventHandler);
	getJCheckBoxHasOverride().addActionListener(ivjEventHandler);
	getContextNameValueTextField().addPropertyChangeListener(ivjEventHandler);
	annotationTextArea.getDocument().addDocumentListener(ivjEventHandler);
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
public void initEditSpecies(SpeciesContext argSpeciesContext, Model argModel) {
	if(argSpeciesContext != null){
		setTitle("Edit Species "+argSpeciesContext.getSpecies().getCommonName()+" in Structure "+argSpeciesContext.getStructure().getName());
		getOKJButton().setText("OK");
	}
	mode = EDIT_SPECIES_MODE;
	setModel(argModel);
	setSpeciesContext(argSpeciesContext);
}


/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("EditSpeciesDialog");
		add(getJDialogContentPane());
		initConnections();
		setSize(580,404);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
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
		AsynchClientTask task1 = new AsynchClientTask("step2", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				getSpeciesContext().getSpecies().setCommonName(getNameValueJTextField().getText());				

				// set text from annotationTextField in free text annotation for species in vcMetaData (from model)
				setAnnotationString(annotationTextArea.getText());
				// old ---- getSpeciesContext().getSpecies().setAnnotation(getAnnotationString());
				VCMetaData vcMetaData = getModel().getVcMetaData();
				vcMetaData.setFreeTextAnnotation(getSpeciesContext().getSpecies(), getAnnotationString());
				
				getSpeciesContext().setHasOverride(getJCheckBoxHasOverride().isSelected());
				if (getJCheckBoxHasOverride().isSelected()){
					getSpeciesContext().setName(getContextNameValueTextField().getText());
				}
				// if there is a Pathway commons reference to this species, add it as an RDF statement to VCMetadata
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
						msg += "\n\nDid you want to reuse that species?\n\n";
						msg += "(Note: species may be copied and pasted using using popup menus or keyboard <CTRL-C><CTRL-V>)";
						String selection = PopupGenerator.showWarningDialog(getParent(),msg,new String[]{UserMessage.OPTION_USE_EXISTING_SPECIES, UserMessage.OPTION_CANCEL},UserMessage.OPTION_USE_EXISTING_SPECIES);
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
			}
		};
		ClientTaskDispatcher.dispatch(getParent(), new Hashtable<String, Object>(), new AsynchClientTask[] {task1});
		
	}catch(Exception e){
		e.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this,"Edit Species Error\n"+e.getMessage());
	}
}


/**
 * Set the AnnotationString to a new value.
 * @param newValue java.lang.String
 */
private void setAnnotationString(java.lang.String newValue) {
	if (ivjAnnotationString != newValue) {
		try {
			ivjAnnotationString = newValue;
			connEtoC3(ivjAnnotationString);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}

/**
 * Set the document1 to a new value.
 * @param newValue javax.swing.text.Document
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}

/**
 * Set the document2 to a new value.
 * @param newValue javax.swing.text.Document
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
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
private void setspecies1(Species newValue) {
	if (ivjspecies1 != newValue) {
		try {
			ivjspecies1 = newValue;
			connEtoM2(ivjspecies1);
			connEtoM3(ivjspecies1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
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
private void setspeciesContext1(SpeciesContext newValue) {
	if (ivjspeciesContext1 != newValue) {
		try {
			SpeciesContext oldValue = getspeciesContext1();
			ivjspeciesContext1 = newValue;
			connPtoP1SetSource();
			connEtoM7(ivjspeciesContext1);
			connEtoM6(ivjspeciesContext1);
			connEtoM1(ivjspeciesContext1);
			annotationTextArea.setText(getAnnotationString());
			firePropertyChange("speciesContext", oldValue, newValue);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}

private void showPCKeywordQueryPanel() {
	 PCKeywordQueryPanel aPCKeywordQueryPanel = new PCKeywordQueryPanel();
	 int returnVal = DialogUtils.showComponentOKCancelDialog(this, aPCKeywordQueryPanel, "Search External Database");
	 
	 if (returnVal == JOptionPane.OK_OPTION) {
		 saveSelectedXRef(aPCKeywordQueryPanel.getSelectedXRef(),aPCKeywordQueryPanel.getMiriamQualifier());
	 }
}


/**
 * Comment
 */
private void updateInterface() {

	if (fieldSpeciesContext == null || fieldModel == null) {
		return;
	}
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

		getContextNameValueTextField().setEditable(true);
		getContextNameValueTextField().setText(contextName);
	}
	getContextNameValueTextField().setEditable(
		(getSpeciesContext() != null) &&
		(getSpeciesContext().getStructure() != null) &&
		getJCheckBoxHasOverride().isSelected()
		);

	
	updatePCLink();
	
	updateOKButton();
}

private void updateOKButton() {
	boolean bEnabled = (getSpeciesContext() != null) && (mode == EDIT_SPECIES_MODE || (mode == ADD_SPECIES_MODE && getModel() != null)) &&
	(getNameValueJTextField().getText() != null) && (getNameValueJTextField().getText().length() > 0) && 
	(getContextNameValueTextField().getText() != null) && (getContextNameValueTextField().getText().length() > 0) && 
		(
			mode == ADD_SPECIES_MODE ||
			!Compare.isEqualOrNull(getNameValueJTextField().getText(),getSpeciesContext().getSpecies().getCommonName()) ||
//			!Compare.isEqualOrNull(getSpeciesContext().getSpecies().getAnnotation(),getAnnotationString()) ||
			getSpeciesContext().getHasOverride() != getJCheckBoxHasOverride().isSelected() ||
			!Compare.isEqualOrNull(getSpeciesContext().getName(),getContextNameValueTextField().getText()) ||
			!Compare.isEqualOrNull(getAnnotationString(),annotationTextArea.getText()) 
//			!Compare.isEqualOrNull(getPCLink(),getPCLinkValueEditorPane().getText())
		);
	getOKJButton().setEnabled(bEnabled);
}

private JButton getPathwayDBbutton() {
	if (pathwayDBJButton == null) {
		try {
			pathwayDBJButton = new javax.swing.JButton();
			pathwayDBJButton.setName("pathwayDBJButton");
			pathwayDBJButton.setText("Link to databases");
			// pathwayDBJButton.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return pathwayDBJButton;
}
	private JLabel getPCLinkJlabel() {
		if (PCLinkJlabel == null) {
			PCLinkJlabel = new JLabel();
			PCLinkJlabel.setText("Database Link");
			PCLinkJlabel.setName("LinkJLabel");
		}
		return PCLinkJlabel;
	}
	private JEditorPane getPCLinkValueEditorPane() {
		if (PCLinkValueEditorPane == null) {
			PCLinkValueEditorPane = new JEditorPane();
			PCLinkValueEditorPane.setContentType("text/html");
			PCLinkValueEditorPane.setEditable(false);
			PCLinkValueEditorPane.setBackground(getBackground());
			PCLinkValueEditorPane.setText(null);
		}
		return PCLinkValueEditorPane;
	}

}