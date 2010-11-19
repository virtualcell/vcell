package cbit.vcell.model.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import org.vcell.sybil.gui.pcsearch.test.PCKeywordQueryPanel;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.models.miriam.MIRIAMRef.URNParseFailureException;
import org.vcell.sybil.util.http.pathwaycommons.search.XRef;
import org.vcell.sybil.util.http.uniprot.UniProtConstants;
import org.vcell.sybil.util.miriam.XRefToURN;
import org.vcell.util.gui.DialogUtils;

import uk.ac.ebi.miriam.lib.MiriamLink;
import cbit.vcell.biomodel.meta.MiriamManager;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.PopupGenerator;
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
@SuppressWarnings("serial")
public class SpeciesEditorPanel extends JPanel {
	private SpeciesContext fieldSpeciesContext = null;
	private javax.swing.JButton ivjRevertButton = null;
	private javax.swing.JLabel ivjNameJLabel = null;
	private javax.swing.JTextField ivjNameValueJTextField = null;
	private javax.swing.JButton ivjApplyJButton = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private boolean ivjConnPtoP1Aligning = false;
	private SpeciesContext ivjspeciesContext1 = null;
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
		Map<MiriamRefGroup,MIRIAMQualifier> refGroups = miriamManager.getAllMiriamRefGroups(getSpeciesContext().getSpecies());
		if (refGroups != null && refGroups.size()>0) {
			for (MiriamRefGroup refGroup : refGroups.keySet()){
				Set<MiriamResource> miriamResources = refGroup.getMiriamRefs();
				for (MiriamResource resource : miriamResources){
					pcLinkStrings.add(resource.getMiriamURN());
				}
			}
		}
		return pcLinkStrings;
	}

	private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, HyperlinkListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getRevertJButton()) 
				updateInterface();
			if (e.getSource() == getApplyJButton()) 
				connEtoC2(e);
			if (e.getSource() == getPathwayDBbutton()) 
				showPCKeywordQueryPanel();
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SpeciesEditorPanel.this && (evt.getPropertyName().equals("speciesContext"))) 
				connPtoP1SetTarget();
		};
		// @Override
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == EventType.ACTIVATED) {
				URL link = e.getURL();
				if (link != null) {
					DialogUtils.browserLauncher(SpeciesEditorPanel.this, link.toExternalForm(), "failed to launch", false);
				}
			}
		};
	};

/**
 * EditSpeciesDialog constructor comment.
 */
public SpeciesEditorPanel() {
	super();
	initialize();
}

/**
 * connEtoC2:  (OKJButton.action.actionPerformed(java.awt.event.ActionEvent) --> EditSpeciesDialog.oK(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		this.apply(arg1);
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
 * Return the CancelJButton property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getRevertJButton() {
	if (ivjRevertButton == null) {
		try {
			ivjRevertButton = new javax.swing.JButton();
			ivjRevertButton.setName("CancelJButton");
			ivjRevertButton.setText("Revert");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRevertButton;
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
private javax.swing.JButton getApplyJButton() {
	if (ivjApplyJButton == null) {
		try {
			ivjApplyJButton = new javax.swing.JButton();
			ivjApplyJButton.setName("OKJButton");
			ivjApplyJButton.setText("Apply");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjApplyJButton;
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
	getRevertJButton().addActionListener(ivjEventHandler);
	getApplyJButton().addActionListener(ivjEventHandler);
	getPathwayDBbutton().addActionListener(ivjEventHandler);
	getPCLinkValueEditorPane().addHyperlinkListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
}

/**
 * Insert the method's description here.
 * Creation date: (5/20/2003 7:55:25 AM)
 * @param argSpeciesContext cbit.vcell.model.SpeciesContext
 * @param argDocumentManager cbit.vcell.clientdb.DocumentManager
 */
public void initEditSpecies(SpeciesContext argSpeciesContext, Model argModel) {
	setModel(argModel);
	setSpeciesContext(argSpeciesContext);
}


/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("SpeciesEditorPanel");
		setLayout(new GridBagLayout());
		
		int gridy = 0;
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.anchor = java.awt.GridBagConstraints.EAST;
		gbc.insets = new Insets(20, 20, 4, 4);
		add(getNameJLabel(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(20, 4, 4, 20);
		add(getNameValueJTextField(), gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 20, 4, 4);
		gbc.anchor = GridBagConstraints.NORTHEAST;
		add(new JLabel("Annotation"), gbc);

		annotationTextArea = new javax.swing.JTextArea("", 3, 30);
		annotationTextArea.setLineWrap(true);
		annotationTextArea.setWrapStyleWord(true);
		javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 20);
		gbc.ipady = 10;
		add(jsp, gbc);

		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(4, 4, 4, 4);
		add(getPathwayDBbutton(), gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(4, 20, 4, 4);
		gbc.gridx = 0;
		gbc.gridy = gridy;
		add(getPCLinkJlabel(), gbc);
		
		gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.ipady = 100;
		gbc.insets = new Insets(4, 4, 4, 20);
		JScrollPane scollPane = new JScrollPane(getPCLinkValueEditorPane());
		add(scollPane, gbc);

		gridy ++;
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
		panel.add(getApplyJButton());
		panel.add(getRevertJButton());
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.insets = new java.awt.Insets(4, 4, 4, 20);
		gbc.anchor = GridBagConstraints.PAGE_START;
		add(panel, gbc);
		
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
		SpeciesEditorPanel aEditSpeciesPanel = new SpeciesEditorPanel();
		frame.add(aEditSpeciesPanel);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.pack();
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of cbit.gui.JInternalFrameEnhanced");
		exception.printStackTrace(System.out);
	}
}

/**
 * Comment
 */
private void apply(java.awt.event.ActionEvent actionEvent) {
	try{
		getSpeciesContext().getSpecies().setCommonName(getNameValueJTextField().getText());				

		// set text from annotationTextField in free text annotation for species in vcMetaData (from model)
		VCMetaData vcMetaData = getModel().getVcMetaData();
		vcMetaData.setFreeTextAnnotation(getSpeciesContext().getSpecies(), annotationTextArea.getText());
		
		getSpeciesContext().setHasOverride(true);
		getSpeciesContext().setName(getNameValueJTextField().getText());
				
	} catch(Exception e){
		e.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this,"Edit Species Error\n"+e.getMessage(), e);
	}
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
			updateInterface();
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
	
	getNameValueJTextField().setText(getSpeciesContext().getName());	
	annotationTextArea.setText(getModel().getVcMetaData().getFreeTextAnnotation(getSpeciesContext().getSpecies()));
	
//	updatePCLink();	
}

private JButton getPathwayDBbutton() {
	if (pathwayDBJButton == null) {
		try {
			pathwayDBJButton = new javax.swing.JButton();
			pathwayDBJButton.setName("pathwayDBJButton");
			pathwayDBJButton.setText("Add Links to Databases");
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
			PCLinkJlabel.setText("Database Links");
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